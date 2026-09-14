package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;

// --- Import aggiornati per Minecraft 26.1.2 (Mojang mappings, niente più Yarn) ---
import net.minecraft.world.entity.animal.Sheep; // prima: net.minecraft.entity.passive.SheepEntity
import net.minecraft.world.phys.AABB;           // prima: net.minecraft.util.math.Box

/**
 * Modulo ESP per le pecore.
 * Evidenzia tutte le entità Sheep nel mondo, anche attraverso i muri.
 *
 * Aggiornato per Minecraft 26.1.2, che dal 26.1 in poi è distribuito
 * senza offuscamento e usa direttamente i nomi ufficiali Mojang
 * (Minecraft invece di MinecraftClient, "level" invece di "world",
 * Sheep invece di SheepEntity, AABB invece di Box, ecc.).
 */
public class SheepESP extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("Come vengono disegnate le forme.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> fillColor = sgGeneral.add(new ColorSetting.Builder()
        .name("colore-riempimento")
        .description("Colore di riempimento del box attorno alla pecora.")
        .defaultValue(new SettingColor(255, 255, 255, 60))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
        .name("colore-contorno")
        .description("Colore del contorno del box.")
        .defaultValue(new SettingColor(255, 255, 255, 255))
        .build()
    );

    private final Setting<Double> boxExpand = sgGeneral.add(new DoubleSetting.Builder()
        .name("espansione-box")
        .description("Espande leggermente il box di rendering.")
        .defaultValue(0.0)
        .min(0)
        .sliderMax(0.5)
        .build()
    );

    public SheepESP() {
        super(Categories.Render, "sheep-esp", "Evidenzia le pecore attraverso i muri.");
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (mc.level == null) return; // prima: mc.world

        // entitiesForRendering() scorre tutte le entità caricate lato client,
        // incluse quelle dietro ai blocchi: è il modo giusto per un ESP.
        for (var entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Sheep sheep)) continue;

            AABB box = sheep.getBoundingBox().inflate(boxExpand.get()); // prima: box.expand(...)

            event.renderer.box(
                box,
                fillColor.get(),
                lineColor.get(),
                shapeMode.get(),
                0
            );
        }
    }
}
