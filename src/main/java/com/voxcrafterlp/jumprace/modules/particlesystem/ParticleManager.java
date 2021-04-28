package com.voxcrafterlp.jumprace.modules.particlesystem;

import com.google.common.collect.Lists;
import com.voxcrafterlp.jumprace.modules.objects.ModuleData;
import com.voxcrafterlp.jumprace.modules.particlesystem.effects.ParticleEffect;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * This file was created by VoxCrafter_LP!
 * Date: 25.04.2021
 * Time: 16:42
 * Project: JumpRace
 */

@Getter
public class ParticleManager {

    private final List<ParticleEffect> particleEffects;
    private final Player player;

    public ParticleManager(ModuleData moduleData, Player player) {
        this.particleEffects = Lists.newCopyOnWriteArrayList();
        this.player = player;

        moduleData.getParticleEffectData().forEach(particleEffectData ->
                this.particleEffects.add(new ParticleEffectBuilder(particleEffectData.getEffectType(),
                particleEffectData.getLocation(), particleEffectData.getEnumParticle())
                .setRotation(particleEffectData.getYaw(), particleEffectData.getPitch(), particleEffectData.getRoll())
                .setSize(particleEffectData.getSize()).setVisibleTo(Collections.singletonList(player)).build()));
    }

    public void addParticleEffect(EffectType effectType, Location location) {
        this.particleEffects.add(new ParticleEffectBuilder(effectType, location, EnumParticle.VILLAGER_HAPPY)
        .setVisibleTo(Collections.singletonList(this.player)).setRotation(0, 0, 0)
        .setSize(1).build());
    }

    public void startEffects() {
        this.particleEffects.forEach(ParticleEffect::startDrawing);
    }

    public void stopEffects() {
        this.particleEffects.forEach(ParticleEffect::stopDrawing);
    }

}
