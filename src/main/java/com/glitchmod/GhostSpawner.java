package com.glitchmod;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class GhostSpawner {

    // Основной метод, который запускает "Глюк"
    public static void triggerFearEvent(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.getWorld();
        BlockPos pos = player.getBlockPos();

        // 1. Проверка на небо (не спавним в пещерах)
        if (!world.isSkyVisible(pos)) return;

        // 2. Накладываем эффекты: Слепота (10 сек) и легкая Тошнота (15 сек)
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200, 0));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 300, 0));

        // 3. Включаем звук пластинки 13 (на игроке, чтобы он слышал)
        world.playSound(null, pos, SoundEvents.MUSIC_DISC_13, SoundCategory.AMBIENT, 1.0f, 1.0f);

        // 4. Спавним "Наблюдателей" (пусть будет сразу 2-3 для жути)
        for (int i = 0; i < 2; i++) {
            spawnScaryPhoto(player, world);
        }
    }

    private static void spawnScaryPhoto(ServerPlayerEntity player, ServerWorld world) {
        // Дистанция от 20 до 40 блоков
        double angle = Math.random() * Math.PI * 2;
        double distance = 20 + Math.random() * 20;
        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;
        
        // Высота над игроком (от 5 до 12 блоков вверх)
        double y = player.getY() + 5 + Math.random() * 7;

        // Создаем стойку
        ArmorStandEntity ghost = new ArmorStandEntity(world, x, y, z);
        ghost.setInvisible(true);        // Невидимая
        ghost.setNoGravity(true);        // Висит в воздухе
        ghost.setInvulnerable(true);     // Нельзя сломать
        ghost.setCustomNameVisible(false);

        // Выбираем одну из твоих 6 фоток через CustomModelData
        ItemStack photoStack = new ItemStack(GlitchMod.WATCHER_ITEM);
        int photoId = (int) (Math.random() * 6) + 1;
        photoStack.getOrCreateNbt().putInt("CustomModelData", photoId);

        // "Надеваем" фотку на голову
        ghost.equipStack(EquipmentSlot.HEAD, photoStack);

        // Спавним в мир
        world.spawnEntity(ghost);

        // Таймер на удаление (через 20 секунд призрак исчезнет сам)
        new Thread(() -> {
            try {
                Thread.sleep(20000); 
                ghost.discard(); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
