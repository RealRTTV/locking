package ca.rttv.locking;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Locking {
    public static final Thread nbtThread = new Thread(Locking::saveNbt, "(Locking) Lock NBT Writer");
    public static final File file = new File(MinecraftClient.getInstance().runDirectory, "locked_slots.nbt");
    public static final Set<Integer> LOCKS = new HashSet<>(40);
    public static final AtomicBoolean shouldSave = new AtomicBoolean(false);
    public static boolean loaded = false;

    public static boolean isLocked(Slot slot) {
        if (!loaded) {
            loadNbt();
            loaded = true;
        }
        return slot.inventory instanceof PlayerInventory && LOCKS.contains(slot.getIndex());
    }

    public static void toggleLock(Slot slot) {
        final MinecraftClient client = MinecraftClient.getInstance();


        if (!(slot.inventory instanceof PlayerInventory) || client.world == null || client.player == null) {
            return;
        }

        if (!LOCKS.remove(slot.getIndex())) {
            if (slot.hasStack()) {
                LOCKS.add(slot.getIndex());
                client.world.playSound(client.player.getX(), client.player.getY(), client.player.getZ(), SoundEvents.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, 1.0f, 1.5f, false);
            }
        } else {
            // disable
            client.world.playSound(client.player.getX(), client.player.getY(), client.player.getZ(), SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS, 1.0f, 0.9f, false);
        }

        shouldSave.set(true);
        if (!nbtThread.isAlive()) {
            nbtThread.start();
        }
    }

    private static String getWorldName() {
        final MinecraftClient client = MinecraftClient.getInstance();
        if (client.getServer() != null) {
            return client.getServer().getSaveProperties().getLevelName();
        } else if (client.getNetworkHandler() != null) {
            return client.getNetworkHandler().getConnection().getAddress().toString();
        } else {
            throw new NullPointerException("Tried to get world name in a situation where you are not on a server, nor on a single-player world, please note how the hell this happened");
        }
    }

    private static void loadNbt() {
        NbtCompound nbt;
        if (!file.exists()) {
            nbt = new NbtCompound();
            shouldSave.set(true);
        } else {
            nbt = readFromFile();
        }
        Arrays.stream(nbt.getIntArray(getWorldName())).forEach(LOCKS::add);
    }

    private static void saveNbt() {
        while (true) {
            if (shouldSave.getAndSet(false)) {
                NbtCompound nbt = readFromFile();
                nbt.putIntArray(getWorldName(), LOCKS.stream().mapToInt(i -> i).toArray());
                writeToFile(nbt);
            }
        }
    }

    private static NbtCompound readFromFile() {
        if (!file.exists()) {
            return new NbtCompound();
        }

        try {
            return NbtIo.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeToFile(NbtCompound nbt) {
        try {
            NbtIo.write(nbt, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
