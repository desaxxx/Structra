package com.desoi.structra;

import com.desoi.structra.command.MainCommand;
import com.desoi.structra.listener.BukkitListener;
import com.desoi.structra.util.ItemCreator;
import com.desoi.structra.util.Util;
import com.desoi.structra.util.Wrapper;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class Structra extends JavaPlugin {

    public static final String FILE_EXTENSION = ".structra";
    public static final ItemStack SELECTOR_TOOL = ItemCreator.of(Material.STICK).name("<#e4e471>Structra Tool").get();

    @Getter
    private static File savesFolder;
    @Getter
    private static File historyFolder;
    @Getter
    private Wrapper wrapper;
    @Getter
    private static Structra instance;


    @Override
    public void onEnable() {
        instance = this;

        Objects.requireNonNull(getCommand("structra")).setExecutor(new MainCommand());
        Objects.requireNonNull(getCommand("structra")).setTabCompleter(new MainCommand());

        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);

        initFolders();

        wrapper = new Wrapper(this);
        initMetrics();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void initFolders() {
        if(!getDataFolder().exists()) //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();
        savesFolder = new File(Structra.instance.getDataFolder(), "saves");
        if(!savesFolder.exists()) //noinspection ResultOfMethodCallIgnored
            savesFolder.mkdirs();
        historyFolder = new File(Structra.instance.getDataFolder(), "history");
        if(!historyFolder.exists()) //noinspection ResultOfMethodCallIgnored
            historyFolder.mkdirs();
    }

    private void initMetrics() {
        Metrics metrics = new Metrics(this,26802);
        metrics.addCustomChart(new SingleLineChart("saves", () -> Util.savesFileNames().size()));
    }
}
