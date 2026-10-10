package com.universalxp.lowhealthresistance;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Low Health Resistance
 *
 * - A hit that would kill a player (fall damage, mobs, players, ...) does NOT kill them.
 *   They are left at 1 heart and get infinite Resistance 99 (full damage immunity).
 * - While at 1 heart (2 HP or less) the effect is kept on the player.
 * - Once they heal up to 2 hearts (4 HP or more) the effect is removed.
 *
 * The void and /kill still kill, otherwise a player falling into the void would be saved forever.
 */
public class LowHealthResistance implements ModInitializer {

	/** 1 heart = 2 HP. */
	private static final float ONE_HEART = 2.0f;

	/** 2 hearts = 4 HP. Effect is removed at or above this. */
	private static final float REMOVE_AT_OR_ABOVE = 4.0f;

	/** Effect amplifier. 99 = "Resistance 99" (level 100). Anything from 4 up is full immunity. */
	private static final int AMPLIFIER = 99;

	@Override
	public void onInitialize() {
		// Death save: turn a lethal hit into "1 heart + Resistance 99"
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
			if (!(entity instanceof ServerPlayerEntity player)) {
				return true;
			}
			if (damageSource.isOf(DamageTypes.OUT_OF_WORLD) || damageSource.isOf(DamageTypes.GENERIC_KILL)) {
				return true; // let the void and /kill work normally
			}

			player.setHealth(ONE_HEART);
			applyEffect(player);
			return false; // cancel death
		});

		// Keep the effect while at 1 heart, remove it at 2 hearts
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				tick(player);
			}
		});
	}

	private static void tick(ServerPlayerEntity player) {
		if (!player.isAlive()) {
			return;
		}

		float health = player.getHealth();
		boolean hasOurEffect = hasOurEffect(player);

		if (health <= ONE_HEART) {
			if (!hasOurEffect) {
				applyEffect(player);
			}
		} else if (health >= REMOVE_AT_OR_ABOVE && hasOurEffect) {
			player.removeStatusEffect(StatusEffects.RESISTANCE);
		}
	}

	private static boolean hasOurEffect(ServerPlayerEntity player) {
		StatusEffectInstance current = player.getStatusEffect(StatusEffects.RESISTANCE);
		return current != null
				&& current.getDuration() == StatusEffectInstance.INFINITE
				&& current.getAmplifier() == AMPLIFIER;
	}

	private static void applyEffect(ServerPlayerEntity player) {
		// infinite duration, no particles, icon shown in the HUD
		player.addStatusEffect(new StatusEffectInstance(
				StatusEffects.RESISTANCE,
				StatusEffectInstance.INFINITE,
				AMPLIFIER,
				false,
				false,
				true
		));
	}
}
