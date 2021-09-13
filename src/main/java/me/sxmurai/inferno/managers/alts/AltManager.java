package me.sxmurai.inferno.managers.alts;

import java.util.ArrayList;

public class AltManager {
    private final ArrayList<Alt> alts = new ArrayList<>();

    public AltManager() {
        // @todo alt config saver. maybe do a wurst and make it encrypted?? plus encrypting it would make it so RATs cant grab it
    }

    public void add(Alt alt) {
        this.alts.add(alt);
    }

    public void remove(Alt alt) {
        this.alts.remove(alt);
    }

    public ArrayList<Alt> getAlts() {
        return alts;
    }
}
