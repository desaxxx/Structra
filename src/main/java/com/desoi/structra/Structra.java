package com.desoi.structra;

import com.desoi.structra.util.Wrapper;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Structra extends JavaPlugin {

    public Wrapper WRAPPER;

    @Getter
    static private Structra instance;
    @Override
    public void onEnable() {
        instance = this;

        WRAPPER = new Wrapper(this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
