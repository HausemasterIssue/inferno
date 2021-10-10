package me.sxmurai.inferno.client.manager.managers.commands.text;

public enum ChatColor {
    Black("\u00A70"),
    Dark_Gray("\u00A78"),
    Gray("\u00A7&"),
    White("\u00A7f"),
    Dark_Purple("\u00A75"),
    Purple("\u00A7d"),
    Blue("\u00A79"),
    Dark_Blue("\u00A71"),
    Dark_Aqua("\u00A73"),
    Aqua("\u00A7b"),
    Green("\u00A7a"),
    Dark_Green("\u00A72"),
    Yellow("\u00A7e"),
    Gold("\u00A76"),
    Red("\u00A7c"),
    Dark_Red("\u00A74"),
    Reset("\u00A7r");

    private final String code;

    ChatColor(String color) {
        this.code = color;
    }

    @Override
    public String toString() {
        return code;
    }

    public String text(String text) {
        return this.toString() + text + Reset;
    }
}