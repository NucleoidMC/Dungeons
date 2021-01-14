package xyz.nucleoid.dungeons.dungeons.game;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgEnemyDropGenerator;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMap;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.Set;
import java.util.stream.Collectors;

public class DgActive {
    private final DgConfig config;

    public final GameSpace gameSpace;
    private final DgMap gameMap;

    // TODO replace with ServerPlayerEntity if players are removed upon leaving
    private final Object2ObjectMap<PlayerRef, DgPlayer> participants;
    private final DgSpawnLogic spawnLogic;
    private final DgIdle idle;
    private final boolean ignoreWinState;
    private final DgTimerBar timerBar;

    private DgActive(GameSpace gameSpace, DgMap map, DgConfig config, Set<PlayerRef> participants) {
        this.gameSpace = gameSpace;
        this.config = config;
        this.gameMap = map;
        this.spawnLogic = new DgSpawnLogic(gameSpace, map);
        this.participants = new Object2ObjectOpenHashMap<>();

        for (PlayerRef player : participants) {
            this.participants.put(player, new DgPlayer());
        }

        this.idle = new DgIdle();
        this.ignoreWinState = this.participants.size() <= 1;
        this.timerBar = new DgTimerBar();
    }

    public static void open(GameSpace gameWorld, DgMap map, DgConfig config) {
        Set<PlayerRef> participants = gameWorld.getPlayers().stream()
                .map(PlayerRef::of)
                .collect(Collectors.toSet());
        DgActive active = new DgActive(gameWorld, map, config, participants);

        gameWorld.openGame(builder -> {
            builder.setRule(GameRule.CRAFTING, RuleResult.DENY);
            builder.setRule(GameRule.PORTALS, RuleResult.DENY);
            builder.setRule(GameRule.PVP, RuleResult.DENY);
            builder.setRule(GameRule.HUNGER, RuleResult.DENY);
            builder.setRule(GameRule.FALL_DAMAGE, RuleResult.DENY);
            builder.setRule(GameRule.INTERACTION, RuleResult.DENY);
            builder.setRule(GameRule.BLOCK_DROPS, RuleResult.DENY);
            builder.setRule(GameRule.THROW_ITEMS, RuleResult.DENY);
            builder.setRule(GameRule.UNSTABLE_TNT, RuleResult.DENY);

            builder.on(GameOpenListener.EVENT, active::onOpen);
            builder.on(GameCloseListener.EVENT, active::onClose);

            builder.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
            builder.on(PlayerAddListener.EVENT, active::addPlayer);
            builder.on(PlayerRemoveListener.EVENT, active::removePlayer);

            builder.on(GameTickListener.EVENT, active::tick);

            builder.on(PlayerDamageListener.EVENT, active::onPlayerDamage);
            builder.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
        });
    }

    private void onOpen() {
        ServerWorld world = this.gameSpace.getWorld();
        for (PlayerRef ref : this.participants.keySet()) {
            ref.ifOnline(world, this::spawnParticipant);
        }
        this.idle.onOpen(world.getTime(), this.config);
    }

    private void onClose() {
        this.timerBar.close();
    }

    private void addPlayer(ServerPlayerEntity player) {
        if (!this.participants.containsKey(PlayerRef.of(player))) {
            this.spawnSpectator(player);
        }
        this.timerBar.addPlayer(player);
    }

    private void removePlayer(ServerPlayerEntity player) {
        this.participants.remove(PlayerRef.of(player));
        this.timerBar.removePlayer(player);
    }

    private ActionResult onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
        return ActionResult.PASS;
    }

    private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        // TODO handle death
        this.spawnParticipant(player);
        return ActionResult.FAIL;
    }

    private void spawnParticipant(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player);

        ServerWorld world = this.gameSpace.getWorld();
        for (int i = 0; i < 30; i++) {
            ItemStack stack = DgEnemyDropGenerator.generate(player.getRandom(), 1.0);

            if (stack != null) {
                player.inventory.offerOrDrop(world, stack);
            }
        }

        player.inventory.offerOrDrop(world, ItemStackBuilder.of(Items.ARROW).setCount(1).build());
    }

    private void spawnSpectator(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.SPECTATOR);
        this.spawnLogic.spawnPlayer(player);
    }

    private void tick() {
        ServerWorld world = this.gameSpace.getWorld();
        long time = world.getTime();

        DgIdle.IdleTickResult result = this.idle.tick(time, gameSpace);

        switch (result) {
            case CONTINUE_TICK:
                break;
            case TICK_FINISHED:
                return;
            case GAME_FINISHED:
                this.broadcastWin(this.checkWinResult());
                return;
            case GAME_CLOSED:
                this.gameSpace.close(GameCloseReason.FINISHED);
                return;
        }

        this.timerBar.update(this.idle.finishTime - time, this.config.timeLimitSecs * 20);
    }

    protected static void broadcastMessage(Text message, GameSpace world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            player.sendMessage(message, false);
        }
    }

    protected static void broadcastSound(SoundEvent sound, float pitch, GameSpace world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            player.playSound(sound, SoundCategory.PLAYERS, 1.0F, pitch);
        }
    }

    protected static void broadcastSound(SoundEvent sound,  GameSpace world) {
        broadcastSound(sound, 1.0f, world);
    }

    protected static void broadcastTitle(Text message, GameSpace world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            TitleS2CPacket packet = new TitleS2CPacket(TitleS2CPacket.Action.TITLE, message, 1, 5,  3);
            player.networkHandler.sendPacket(packet);
        }
    }

    private void broadcastWin(WinResult result) {
        ServerPlayerEntity winningPlayer = result.getWinningPlayer();

        Text message;
        if (winningPlayer != null) {
            message = winningPlayer.getDisplayName().shallowCopy().append(" has won the game!").formatted(Formatting.GOLD);
        } else {
            message = new LiteralText("The game ended, but nobody won!").formatted(Formatting.GOLD);
        }

        broadcastMessage(message, this.gameSpace);
        broadcastSound(SoundEvents.ENTITY_VILLAGER_YES, this.gameSpace);
    }

    private WinResult checkWinResult() {
        // for testing purposes: don't end the game if we only ever had one participant
        if (this.ignoreWinState) {
            return WinResult.no();
        }

        ServerWorld world = this.gameSpace.getWorld();
        ServerPlayerEntity winningPlayer = null;

        // TODO win result logic
        return WinResult.no();
    }

    static class WinResult {
        final ServerPlayerEntity winningPlayer;
        final boolean win;

        private WinResult(ServerPlayerEntity winningPlayer, boolean win) {
            this.winningPlayer = winningPlayer;
            this.win = win;
        }

        static WinResult no() {
            return new WinResult(null, false);
        }

        static WinResult win(ServerPlayerEntity player) {
            return new WinResult(player, true);
        }

        public boolean isWin() {
            return this.win;
        }

        public ServerPlayerEntity getWinningPlayer() {
            return this.winningPlayer;
        }
    }
}
