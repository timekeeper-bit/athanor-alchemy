package io.github.timekeeperbit.athanor.registry;

import io.github.timekeeperbit.athanor.Athanor;
import io.github.timekeeperbit.athanor.world.AuraState;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public final class ModAttachments {
	/** Aether and miasma, stored per chunk. */
	public static final AttachmentType<AuraState> AURA = AttachmentRegistry.createPersistent(Athanor.id("aura"), AuraState.CODEC);

	private ModAttachments() {
	}

	public static void init() {
	}
}
