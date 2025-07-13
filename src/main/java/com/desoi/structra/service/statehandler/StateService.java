package com.desoi.structra.service.statehandler;

import com.desoi.structra.service.blockstate.*;
import io.papermc.paper.block.MovingPiston;
import org.bukkit.block.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class StateService {

    /**
     * Returns the State Handler class of the BlockState.
     * Class(?) -> Class that extends BlockState
     * IStateHandler(?) -> State Handler of that class. It may be null if the version of the server is not supported.
     */
    private static final Map<Class<?>, @Nullable IStateHandler<?>> handlers = new HashMap<>();

    static {
        handlers.put(Banner.class, new BannerState().getIfSupported());
        handlers.put(Barrel.class, new BarrelState().getIfSupported());
        handlers.put(Beacon.class, new BeaconState().getIfSupported());
        handlers.put(Beehive.class, new BeehiveState().getIfSupported());
        handlers.put(Bell.class, new BellState().getIfSupported());
        handlers.put(BrewingStand.class, new BrewingStandState().getIfSupported());
        handlers.put(BrushableBlock.class, new BrushableBlockState().getIfSupported());
        handlers.put(Campfire.class, new CampfireState().getIfSupported());
        handlers.put(Chest.class, new ChestState().getIfSupported());
        handlers.put(ChiseledBookshelf.class, new ChiseledBookshelfState().getIfSupported());
        handlers.put(CommandBlock.class, new CommandBlockState().getIfSupported());
        handlers.put(Comparator.class, new ComparatorState().getIfSupported());
        handlers.put(Conduit.class, new ConduitState().getIfSupported());
        handlers.put(Crafter.class, new CrafterState().getIfSupported());
        handlers.put(CreakingHeart.class, new CreakingHeartState().getIfSupported());
        handlers.put(CreatureSpawner.class, new CreatureSpawnerState().getIfSupported());
        handlers.put(DaylightDetector.class, new DaylightDetectorState().getIfSupported());
        handlers.put(DecoratedPot.class, new DecoratedPotState().getIfSupported());
        handlers.put(Dispenser.class, new DispenserState().getIfSupported());
        handlers.put(Dropper.class, new DropperState().getIfSupported());
        handlers.put(EnchantingTable.class, new EnchantingTableState().getIfSupported());
        handlers.put(EnderChest.class, new EnderChestState().getIfSupported());
        handlers.put(EndGateway.class, new EndGatewayState().getIfSupported());
        handlers.put(Furnace.class, new FurnaceState().getIfSupported());
        handlers.put(Hopper.class, new HopperState().getIfSupported());
        handlers.put(Jigsaw.class, new JigsawState().getIfSupported());
        handlers.put(Jukebox.class, new JukeboxState().getIfSupported());
        handlers.put(Lectern.class, new LecternState().getIfSupported());
        handlers.put(MovingPiston.class, new MovingPistonState().getIfSupported());
        handlers.put(SculkCatalyst.class, new SculkCatalystState().getIfSupported());
        handlers.put(SculkSensor.class, new SculkSensorState().getIfSupported());
        handlers.put(SculkShrieker.class, new SculkShriekerState().getIfSupported());
        handlers.put(ShulkerBox.class, new ShulkerBoxState().getIfSupported());
        handlers.put(Sign.class, new SignState().getIfSupported());
        handlers.put(Skull.class, new SkullState().getIfSupported());
        handlers.put(Structure.class, new StructureState().getIfSupported());
        //noinspection removal
        handlers.put(SuspiciousSand.class, new SuspiciousSandState().getIfSupported());
        handlers.put(TestBlock.class, new TestBlockState().getIfSupported());
        handlers.put(TestInstanceBlock.class, new TestInstanceBlockState().getIfSupported());
        handlers.put(TrialSpawner.class, new TrialSpawnerState().getIfSupported());
        handlers.put(Vault.class, new VaultState().getIfSupported());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <B extends BlockState> IStateHandler<B> getHandler(BlockState blockState) {
        return (IStateHandler<B>) handlers.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getKey().isInstance(blockState))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }
}
