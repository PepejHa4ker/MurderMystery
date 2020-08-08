package pl.plajer.murdermystery.utils;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public final class PotionEffectBuilder {

    @NotNull
    private final PotionEffectType potionEffectType;

    private Integer duration, amplifier ;
    private Boolean visible, ambient;


    public PotionEffectBuilder(@NotNull PotionEffectType potionEffectType)  {
        this.potionEffectType = potionEffectType;
    }

    public PotionEffectBuilder setDuration(@NotNull Integer duration) {
        this.duration = duration;
        return this;
    }

    public PotionEffectBuilder setAmplifier(@NotNull Integer amplifier) {
        this.amplifier = amplifier;
        return this;
    }

    public PotionEffectBuilder setVisible(@NotNull Boolean visible) {
        this.visible = visible;
        return this;
    }

    public PotionEffectBuilder setAmbient(@NotNull Boolean ambient) {
        this.ambient = ambient;
        return this;
    }

    public PotionEffect build() {
        return new PotionEffect(potionEffectType, duration * 20, amplifier, ambient, visible);
    }
}
