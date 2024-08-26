package primalcat.tempusessential.StrongerDragon;

import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import org.bukkit.Bukkit;
import primalcat.tempusessential.StrongerDragon.phases.DragonChargePlayerPhase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class CustomPhaseRegistry {


    public static void replaceDragonPhase() {
        try {
            // Получаем поле phases из класса EnderDragonPhase
            Field phasesField = EnderDragonPhase.class.getDeclaredField("phases");
            phasesField.setAccessible(true);

            // Получаем текущий массив фаз
            EnderDragonPhase<?>[] phases = (EnderDragonPhase<?>[]) phasesField.get(null);

            // Получаем приватный конструктор класса EnderDragonPhase
            Constructor<EnderDragonPhase> constructor = EnderDragonPhase.class.getDeclaredConstructor(
                    int.class, Class.class, String.class
            );

            // Делаем конструктор доступным
            constructor.setAccessible(true);

            // Создаем кастомную фазу для замены CHARGING_PLAYER с помощью рефлексии
            EnderDragonPhase<DragonChargePlayerPhase> customPhase = (EnderDragonPhase<DragonChargePlayerPhase>)
                    constructor.newInstance(
                            EnderDragonPhase.CHARGING_PLAYER.getId(),
                            DragonChargePlayerPhase.class,
                            "ChargingPlayer"
                    );

            // Заменяем существующую фазу на кастомную
            phases[EnderDragonPhase.CHARGING_PLAYER.getId()] = customPhase;

            // Устанавливаем обновленный массив фаз обратно в поле
            phasesField.set(null, phases);

            Bukkit.getLogger().info("Successfully replaced Dragon Charge Player phase with Custom phase.");

        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("Failed to replace Dragon phase.");
        }
    }
}
