package me.sxmurai.inferno.client.manager.managers.alts;

import me.sxmurai.inferno.client.manager.AbstractManager;

import java.util.ArrayList;

public class AltManager extends AbstractManager<Alt> {
    public AltManager() {
        // @todo alt config saver. maybe do a wurst and make it encrypted?? plus encrypting it would make it so RATs cant grab it
    }

    public void add(Alt alt) {
        this.items.add(alt);
    }

    public void remove(Alt alt) {
        this.items.remove(alt);
    }

    public ArrayList<Alt> getAlts() {
        return this.items;
    }
}
