package com.desoi.structra.service;

import com.desoi.structra.service.blockstate.*;
import io.papermc.paper.block.MovingPiston;
import org.bukkit.block.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BlockStateService {

    private static final Map<Class<? extends BlockState>, BlockStateHandler<?>> handlers = new HashMap<>();

    static {
        handlers.put(Banner.class, new BannerState());
        handlers.put(Barrel.class, new BarrelState());
        handlers.put(Beacon.class, new BeaconState());
        handlers.put(Beehive.class, new BeehiveState());
        handlers.put(Bell.class, new BellState());
        handlers.put(BrewingStand.class, new BrewingStandState());
        handlers.put(BrushableBlock.class, new BrushableBlockState());
        handlers.put(Campfire.class, new CampfireState());
        handlers.put(Chest.class, new ChestState());
        handlers.put(ChiseledBookshelf.class, new ChiseledBookshelfState());
        handlers.put(CommandBlock.class, new CommandBlockState());
        handlers.put(Comparator.class, new ComparatorState());
        handlers.put(Conduit.class, new ConduitState());
        handlers.put(Crafter.class, new CrafterState());
        handlers.put(CreakingHeart.class, new CreakingHeartState());
        handlers.put(CreatureSpawner.class, new CreatureSpawnerState());
        handlers.put(DaylightDetector.class, new DaylightDetectorState());
        handlers.put(DecoratedPot.class, new DecoratedPotState());
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
        handlers.put(MovingPiston.class, new MovingPistonState());
        handlers.put(SculkCatalyst.class, new SculkCatalystState());
        handlers.put(SculkSensor.class, new SculkSensorState());
        handlers.put(SculkShrieker.class, new SculkShriekerState());
        handlers.put(ShulkerBox.class, new ShulkerBoxState());
        handlers.put(Sign.class, new SignState());
        handlers.put(Skull.class, new SkullState());
        handlers.put(Structure.class, new StructureState());
        //noinspection removal
        handlers.put(SuspiciousSand.class, new SuspiciousSandState());
        handlers.put(TestBlock.class, new TestBlockState());
        handlers.put(TestInstanceBlock.class, new TestInstanceBlockState());
        handlers.put(TrialSpawner.class, new TrialSpawnerState());
        handlers.put(Vault.class, new VaultState());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <B extends BlockState> BlockStateHandler<B> getHandler(BlockState blockState) {
        return (BlockStateHandler<B>) handlers.entrySet().stream()
                .filter(e -> e.getKey().isInstance(blockState))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
