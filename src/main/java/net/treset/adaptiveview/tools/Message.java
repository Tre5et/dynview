package net.treset.adaptiveview.tools;

public record Message(String message, Object... args) {
    @Override
    public String toString() {
        return String.format(message, args);
    }
}
