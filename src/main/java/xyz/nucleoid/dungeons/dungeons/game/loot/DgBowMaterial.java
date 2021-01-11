package xyz.nucleoid.dungeons.dungeons.game.loot;

// FIXME(restioson): I don't know enough about bows so I just came up with these on the spot
// Can someone take a look?
public enum DgBowMaterial {
    ASH("Ash"),
    YEW("Yew"),
    OAK("Oak"),
    CEDAR("Cedar"),
    HORN("Horn");

    public final String name;

    DgBowMaterial(String name) {
        this.name = name;
    }
}
