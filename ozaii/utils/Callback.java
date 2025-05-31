package de.codingair.tradesystem.spigot.ozaii.utils;

@FunctionalInterface
public interface Callback<T> {
    void call(T result);
}
