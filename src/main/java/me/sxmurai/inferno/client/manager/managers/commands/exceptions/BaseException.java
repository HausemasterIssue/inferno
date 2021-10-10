package me.sxmurai.inferno.client.manager.managers.commands.exceptions;

public class BaseException extends Exception {
    private final String reason;

    public BaseException(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
