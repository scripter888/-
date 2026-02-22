package com.glitchmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlitchMod implements ModInitializer {
    public static final String MOD_ID = "glitch";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Тот самый предмет-пустышка для моделей
    public static final Item WATCHER_ITEM = new Item(new Item.Settings());

    @Override
    public void onInitialize() {
        LOGGER.info("The Glitch Mod has started. Watch your back...");

        // Регистрируем предмет
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "watcher"), WATCHER_ITEM);

        // Регистрируем команду /glitchtest
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("glitchtest")
                .requires(source -> source.hasPermissionLevel(2)) // Только для админов/опа
                .executes(context -> {
                    // Вызываем наш спавнер для того, кто ввел команду
                    GhostSpawner.triggerFearEvent(context.getSource().getPlayer());
                    return 1;
                })
            );
        });
    }
}
