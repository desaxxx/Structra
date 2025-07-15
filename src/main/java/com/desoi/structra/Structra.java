package com.desoi.structra;

import com.desoi.structra.command.MainCommand;
import com.desoi.structra.util.Wrapper;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Structra extends JavaPlugin {

    static public final String FILE_EXTENSION = ".structra";

    public Wrapper WRAPPER;

    @Getter
    static private Structra instance;
    @Override
    public void onEnable() {
        instance = this;

        Objects.requireNonNull(getCommand("structra")).setExecutor(new MainCommand());
        Objects.requireNonNull(getCommand("structra")).setTabCompleter(new MainCommand());

        if(!getDataFolder().exists()) //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();

        WRAPPER = new Wrapper(this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
