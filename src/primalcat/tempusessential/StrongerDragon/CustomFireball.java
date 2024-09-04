package primalcat.tempusessential.StrongerDragon;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;


public class CustomFireball extends LargeFireball {
    private float yield;
    private boolean incendiary;
    private LivingEntity target;

    public CustomFireball(EntityType<? extends LargeFireball> type, Level world) {
        super(type, world);
    }

    public CustomFireball(Level world, LivingEntity owner, Vec3 velocity, int explosionPower) {
        super(world, owner, velocity, explosionPower);
    }

    @Override
    public void tick() {
        super.tick();

        // Устанавливаем невидимость огненного шара для коллизий
        this.noPhysics = true;
    }



    @Override
    protected void onHit(HitResult hitResult) {

    }
}