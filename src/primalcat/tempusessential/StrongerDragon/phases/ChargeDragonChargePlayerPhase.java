package primalcat.tempusessential.StrongerDragon.phases;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonChargePlayerPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import primalcat.tempusessential.StrongerDragon.CustomEnderDragon;

public class ChargeDragonChargePlayerPhase extends DragonChargePlayerPhase implements DragonPhaseInstance {
    private static final int CHARGE_RECOVERY_TIME = 10;

    private static final int MAX_PHASE_DURATION = 600;
    @Nullable
    private Vec3 targetLocation;
    private int timeSinceCharge;
    private int phaseDuration;

    public ChargeDragonChargePlayerPhase(EnderDragon dragon) {
        super(dragon);
    }

    @Override
    public void doServerTick() {
        if (CustomEnderDragon.chargeTargetLocation != null) {

            Vec3 playerPosition = CustomEnderDragon.chargeTargetLocation.position();
            this.targetLocation = new Vec3(playerPosition.x, playerPosition.y - 5, playerPosition.z);
            if (this.targetLocation == null) {
                super.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            }
            if (this.timeSinceCharge > 0 && this.timeSinceCharge++ >= 10) {
                super.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            } else {
                double d = this.targetLocation.distanceToSqr(super.dragon.getX(), super.dragon.getY(), super.dragon.getZ());
                if (d < 100.0 || d > 22500.0 || super.dragon.horizontalCollision || super.dragon.verticalCollision) {
                    ++this.timeSinceCharge;
                }

            }
            if (this.phaseDuration++ >= MAX_PHASE_DURATION) {
                super.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
            }
        }else {
            super.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
        }
    }

    @Override
    public void begin() {
        this.targetLocation = null;
        this.timeSinceCharge = 0;
        this.phaseDuration = 0;
    }

    public void setTarget(Vec3 pathTarget) {
        this.targetLocation = pathTarget;
    }

    @Override
    public float getFlySpeed() {
        return 3.0F;
    }

    @Nullable
    @Override
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }


//    @Override
//    public EnderDragonPhase<net.minecraft.world.entity.boss.enderdragon.phases.DragonChargePlayerPhase> getPhase() {
//        return EnderDragonPhase.CHARGING_PLAYER;
//    }
}
