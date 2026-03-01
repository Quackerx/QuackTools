package com.quack.tools.modules;

import com.quack.tools.QuackTools;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ShieldDestroyer extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private boolean sendingPackets = false;

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
        .name("amount")
        .description("The number of packets to send.")
        .defaultValue(16)
        .range(1, 100)
        .sliderRange(1, 20)
        .build()
    );

    public ShieldDestroyer() {
        super(QuackTools.CATEGORY, "Shield-Destroyer", "Destroys anyones shield instantly (Requires a mace).");
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (!(event.packet instanceof PlayerInteractEntityC2SPacket attackPacket)) return;
        if (!(mc.crosshairTarget instanceof EntityHitResult ehr)) return;
        if (!(ehr.getEntity() instanceof PlayerEntity target)) return;
        if (mc.player == null) return;

        if (mc.player.getMainHandStack().getItem() != Items.MACE) {
            info("No mace detected!");
            return;
        }

        if (!target.isBlocking()) return;

        if (sendingPackets) return;

        event.cancel();
        sendingPackets = true;

        Vec3d originalPos = mc.player.getEntityPos();
        double fallHeight = 2;

        Vec3d fakeUp = originalPos.add(0, fallHeight, 0);

        for (int i = 0; i < 4; i++) {
            mc.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.OnGroundOnly(false, mc.player.horizontalCollision)
            );
        }

        mc.player.networkHandler.sendPacket(
            new PlayerMoveC2SPacket.PositionAndOnGround(
                fakeUp.x,
                fakeUp.y,
                fakeUp.z,
                false,
                mc.player.horizontalCollision
            )
        );

        mc.player.networkHandler.sendPacket(
            new PlayerMoveC2SPacket.PositionAndOnGround(
                originalPos.x,
                originalPos.y,
                originalPos.z,
                false,
                mc.player.horizontalCollision
            )
        );

        for (int i = 0; i < amount.get(); i++) {
            mc.player.networkHandler.sendPacket(attackPacket);
        }

        sendingPackets = false;
    }
}
