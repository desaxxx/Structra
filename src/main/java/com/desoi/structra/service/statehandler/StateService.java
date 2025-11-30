package com.desoi.structra.service.statehandler;

import com.desoi.structra.service.blockstate.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StateService {

    private static final Map<Class<?>, IStateHandler<?>> handlers = new HashMap<>();

    static {
        handlers.put(Banner.class, new BannerState());
        handlers.put(Barrel.class, new BarrelState());
        handlers.put(Beacon.class, new BeaconState());
        handlers.put(Beehive.class, new BeehiveState());
        handlers.put(Bell.class, new BellState());
        handlers.put(BrewingStand.class, new BrewingStandState());
        tryPutting("org.bukkit.block.BrushableBlock", new BrushableBlockState());
        handlers.put(Campfire.class, new CampfireState());
        handlers.put(Chest.class, new ChestState());
        tryPutting("org.bukkit.block.ChiseledBookshelf", new ChiseledBookshelfState());
        handlers.put(CommandBlock.class, new CommandBlockState());
        handlers.put(Comparator.class, new ComparatorState());
        handlers.put(Conduit.class, new ConduitState());
        tryPutting("org.bukkit.block.CopperGolemStatue", new CopperGolemStatueState());
        tryPutting("org.bukkit.block.Crafter", new CrafterState());
        tryPutting("org.bukkit.block.CreakingHeart", new CreakingHeartState());
        handlers.put(CreatureSpawner.class, new CreatureSpawnerState());
        handlers.put(DaylightDetector.class, new DaylightDetectorState());
        tryPutting("org.bukkit.block.DecoratedPot", new DecoratedPotState());
        handlers.put(Dispenser.class, new DispenserState());
        handlers.put(Dropper.class, new DropperState());
        handlers.put(EnchantingTable.class, new EnchantingTableState());
        handlers.put(EnderChest.class, new EnderChestState());
        handlers.put(EndGateway.class, new EndGatewayState());
        handlers.put(Furnace.class, new FurnaceState());
        handlers.put(Hopper.class, new HopperState());
        handlers.put(Jigsaw.class, new JigsawState());
        handlers.put(Jukebox.class, new JukeboxState());
        handlers.put(Lectern.class, new LecternState());
        tryPutting("io.papermc.paper.block.MovingPiston", new MovingPistonState());
        tryPutting("org.bukkit.block.SculkCatalyst", new SculkCatalystState());
        handlers.put(SculkSensor.class, new SculkSensorState());
        tryPutting("org.bukkit.block.SculkShrieker", new SculkShriekerState());
        tryPutting("org.bukkit.block.Shelf", new ShelfState());
        handlers.put(ShulkerBox.class, new ShulkerBoxState());
        handlers.put(Sign.class, new SignState());
        handlers.put(Skull.class, new SkullState());
        handlers.put(Structure.class, new StructureState());
        tryPutting("org.bukkit.block.SuspiciousSand", new SuspiciousSandState());
        tryPutting("org.bukkit.block.TestBlock", new TestBlockState());
        tryPutting("org.bukkit.block.TestInstanceBlock", new TestInstanceBlockState());
        tryPutting("org.bukkit.block.TrialSpawner", new TrialSpawnerState());
        tryPutting("org.bukkit.block.Vault", new VaultState());
    }

    private static void tryPutting(@NotNull String className, @NotNull IStateHandler<?> handler) {
        try {
            Class<?> clazz = Class.forName(className);
            handlers.put(clazz, handler);
        } catch (ClassNotFoundException ignored) {}
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <B extends BlockState> IStateHandler<B> getHandler(BlockState blockState) {
        return (IStateHandler<B>) handlers.entrySet().stream()
                .filter(e -> e.getKey().isInstance(blockState))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }
}
