package me.sxmurai.inferno.client.manager;

import java.util.ArrayList;

public abstract class AbstractManager<T> extends Manager {
    protected final ArrayList<T> items = new ArrayList<>();
}
