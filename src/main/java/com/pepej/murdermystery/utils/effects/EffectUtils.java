package com.pepej.murdermystery.utils.effects;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

@UtilityClass
public class EffectUtils {

    public void addEffect(Player player, PotionEffect effect) {
       PotionEffect playerEffect = player.getActivePotionEffects()
              .stream()
              .filter(eff -> effect.getType() == eff.getType())
              .findFirst()
              .map(eff -> new PotionEffectBuilder(
                          effect.getType())
                          .setDuration(effect.getDuration() + eff.getDuration())
                          .setAmbient(eff.isAmbient())
                          .setVisible(eff.hasParticles())
                          .setAmplifier(eff.getAmplifier())
                          .build())
              .orElse(effect);
       player.addPotionEffect(playerEffect, true);

    }
}
