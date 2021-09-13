package me.sxmurai.inferno.managers.alts;

public class Alt {
    private final String email;
    private final String password;
    private final Type type;

    public Alt(String email, String password, Type type) {
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        CRACKED, THEALTENING, MOJANG, MICROSOFT
    }
}
