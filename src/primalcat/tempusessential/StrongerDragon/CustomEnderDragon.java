package primalcat.tempusessential.StrongerDragon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import primalcat.tempusessential.StrongerDragon.phases.*;
import primalcat.tempusessential.TempusEssential;


import java.util.*;

public class CustomEnderDragon extends EnderDragon {
    private int ticksUntilNextCrystalRespawn;
    private int ticksUntilNextPhantomSpawn;
    private int ticksUntilNextShulkerSpawn;
    private int ticksUntilExplosionAttack;
    private int ticksUntilPillarAttack = 200;
    public Set<UUID> seenPlayers;
    public int timeSinceLastLanding;
    public boolean isWardenSpawned;
    public boolean ultraFireballLaunched;
    public int holdingTicks = 0;
    public int ticksUntilLightAttack;
    public int ticksUntilBallAttack;
    public int ticksHBeamAttack;
    public boolean showedStats = false;

    public World END_WORLD;
    public BlockPos CENTER_OF_END = new BlockPos(0, 64,0);
    public Location CENTER_LOCATION;
    private static final int CRYSTAL_RESPAWN_MIN = 200;
    private static final int CRYSTAL_RESPAWN_MAX = 600;
    private static final int PHANTOM_RESPAWN_MIN = 150;
    private static final int PHANTOM_RESPAWN_MAX = 500;
    private static final int SHULKER_RESPAWN_MIN = 400;
    private static final int SHULKER_RESPAWN_MAX = 500;
    private static final int EXPLOSION_ATTACK_MIN = 200;
    private static final int EXPLOSION_ATTACK_MAX = 500;
    private static final int LIGHT_ATTACK_MIN = 300;
    private static final int LIGHT_ATTACK_MAX = 800;
    public static Player chargeTargetLocation;

    private static final int H_BEAM_ATTACK_MIN = 200;
    private static final int H_BEAM_ATTACK_MAX = 650;
    private static final int PILLAR_ATTACK_MIN = 250;
    private static final int PILLAR_ATTACK_MAX = 550;

    private static final int BALL_ATTACK_MIN = 150;
    private static final int BALL_ATTACK_MAX = 300;

    private final HashMap<String, Double> playerDamageMap = new HashMap<>();
    private String lastHitPlayer = null;



    public CustomEnderDragon(EntityType<? extends EnderDragon> entitytypes, Level world) {
        super(entitytypes, world);
        this.ticksUntilNextCrystalRespawn = randomBetween(CRYSTAL_RESPAWN_MIN, CRYSTAL_RESPAWN_MAX);
        this.ticksUntilNextPhantomSpawn = randomBetween(PHANTOM_RESPAWN_MIN, PHANTOM_RESPAWN_MAX);
        this.ticksUntilNextShulkerSpawn = randomBetween(SHULKER_RESPAWN_MIN, SHULKER_RESPAWN_MAX);
        this.timeSinceLastLanding = 0;
        this.seenPlayers = new HashSet<>();
        this.isWardenSpawned = false;
        this.ultraFireballLaunched = false;


        if (world instanceof ServerLevel serverLevel) {
            this.END_WORLD = this.getBukkitLivingEntity().getWorld();
            this.CENTER_LOCATION = new Location(END_WORLD, CENTER_OF_END.getX(), CENTER_OF_END.getY(), CENTER_OF_END.getZ() );

            startEffectScheduler(END_WORLD);
            addToPurpleTeam(this.stringUUID);
            this.setGlowingTag(true);
        }

    }





    private void startEffectScheduler(World world) {
        new BukkitRunnable() {
            @Override
            public void run() {
                applyEffectToPlayers(world);
            }
        }.runTaskTimer(TempusEssential.getPlugin(), 0, 100); // Проверка каждые 100 тиков (5 секунд)
    }

    // Метод для применения дебаффов к игрокам в зависимости от их радиуса
    private void applyEffectToPlayers(World world) {
        if (world.getEnvironment() == World.Environment.THE_END) {
            Location centerLocation = world.getSpawnLocation(); // Используем центр для отсчета радиусов

            world.getPlayers().forEach(player -> {
                double distance = player.getLocation().distance(centerLocation);

                // Дебафф в радиусе 70 - 300 блоков
                if (distance <= 300 && distance >= 90) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 300, 1, true, true, true));
                }

                // Дебафф в радиусе 100 - 300 блоков
                if (distance <= 300 && distance >= 110) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 600, 3, true, true, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 600, 2, true, true, true));
                }
            });
            Collection<Entity> nearbyEntities = world.getNearbyEntities(centerLocation, 90, 90, 90);
            nearbyEntities.stream()
                    .filter(entity -> entity instanceof Enderman)
                    .forEach(entity -> {
                        addToPurpleTeam(entity.getUniqueId().toString());
                    });
        }
    }


    @Override
    public void tick() {
        super.tick();
        BlockPos portalLocation = this.getDragonFight().portalLocation;
        if(portalLocation != null){
            this.CENTER_OF_END = portalLocation.above(1);
//            System.out.println("CENTER_OF_END " + CENTER_OF_END);
        }

        if (++holdingTicks >= 2000 && this.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.HOLDING_PATTERN) {
            this.getPhaseManager().setPhase(EnderDragonPhase.LANDING);
            holdingTicks = 0;
        }

        handleDragonAttacks();
        // Логика фаз и атак дракона
//        handleDragonAttacks();

//        // Проверка на респавн кристаллов, фантомов и шалкеров
//        handleEntitySpawning();
    }

    public void handleDragonAttacks(){
        double healthPercentage = (this.getHealth() / this.getMaxHealth()) * 100;
        if (healthPercentage <= 100) {
            // Атаки при здоровье 80% и выше (например, фантомы)
            if (--this.ticksUntilNextPhantomSpawn <= 0) {
                List<Player> players = getNearbyPlayers(100);
                if (!players.isEmpty()) {
                    Player targetPlayer = players.get(random.nextInt(players.size()));

                    spawnRandomPhantoms(targetPlayer);
                }

                try {
                    // Здесь добавлен блок try-catch для предотвращения ClassCastException
                    if (randomBetween(1, 3) == 1 && this.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.HOLDING_PATTERN) {
                        if (!players.isEmpty()) {
                            Player targetPlayer = players.get(random.nextInt(players.size()));
                            chargeTargetLocation = targetPlayer;
                            DragonChargePlayerPhase customPhase = new DragonChargePlayerPhase(this);
                            customPhase.setTarget(targetPlayer.position());
                            this.getPhaseManager().setPhase(customPhase.getPhase());
                        }
                    }
                } catch (ClassCastException e) {
                    // Игнорируем ошибку
                    System.out.println("ClassCastException caught: " + e.getMessage());
                }catch (Exception e){
                    System.out.println("Cannot change ender dragon");
                }
                ticksUntilNextPhantomSpawn = randomBetween(PHANTOM_RESPAWN_MIN, PHANTOM_RESPAWN_MAX);

            }

//            if(--this.ticksUntilChargeAttack <= 0){
//                DragonPillarAttack.startMultipleDragonPillarAttacks(CENTER_LOCATION, END_WORLD, randomBetween(1, 5));
//                ticksUntilChargeAttack = randomBetween(CHARGE_ATTACK_MIN, CHARGE_ATTACK_MAX);
//            }
            if(--this.ticksUntilLightAttack <= 0){
                spawnLightningAtCircle(CENTER_LOCATION, randomBetween(3, 6), END_WORLD);
                ticksUntilLightAttack = randomBetween(LIGHT_ATTACK_MIN, LIGHT_ATTACK_MAX);
            }

            if(--this.ticksUntilPillarAttack <= 0){
                DragonPillarAttack.startMultipleDragonPillarAttacks(CENTER_LOCATION, END_WORLD, randomBetween(1, 5));
                ticksUntilPillarAttack = randomBetween(PILLAR_ATTACK_MIN, PILLAR_ATTACK_MAX);
            }
        }

        if (healthPercentage < 80) {
            // Атаки при здоровье ниже 80%
            // Допустим, здесь появляются новые атаки, которые выполняются одновременно с атаками выше 80%
            if (--this.ticksUntilExplosionAttack <= 0) {
                if (this.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.SITTING_ATTACKING || this.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.SITTING_FLAMING || this.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.SITTING_SCANNING) {
                    DragonWaveAttack.startDragonAttack(CENTER_LOCATION, END_WORLD);
                }
                if (this.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.LANDING) {
                    DragonCircleExplosion.explodeInCircleAround(CENTER_LOCATION, randomBetween(4, 8), END_WORLD);
                    ticksUntilExplosionAttack = randomBetween(EXPLOSION_ATTACK_MIN, EXPLOSION_ATTACK_MAX);
                }
            }

        }

        if (healthPercentage <= 60) {
            // Атаки при здоровье ниже 50%
            // Например, атака ультра-файерболами при респавне кристала
            if (--this.ticksUntilNextCrystalRespawn <= 0) {
                respawnRandomCrystal();
                ticksUntilNextCrystalRespawn = randomBetween(CRYSTAL_RESPAWN_MIN, CRYSTAL_RESPAWN_MAX);

            }

            if(--this.ticksUntilBallAttack <= 0 ){
                DragonFireballAttack.shootFireballsAtPlayers(getNearbyBukkitPlayers(CENTER_LOCATION, 50), this.getBukkitEntity().getLocation(), END_WORLD, 13);
                ultraFireballLaunched = true; // Устанавливаем флаг, что атака была выполнена
                ticksUntilBallAttack = randomBetween(BALL_ATTACK_MIN, BALL_ATTACK_MAX);
            }

        }

        if (healthPercentage < 45) {
            // Атаки при здоровье ниже 30%
            if (--this.ticksUntilNextShulkerSpawn <= 0) {
                spawnCustomShulkers();
                List<Player> players = getNearbyPlayers(80);

                if (!players.isEmpty()) {
                    Player targetPlayer = players.get(random.nextInt(players.size()));
                    org.bukkit.entity.Player bukkitTargetPlayer = ((org.bukkit.entity.Player) targetPlayer.getBukkitEntity()); // Приведение к Bukkit Player

                    DragonUltraFireballAttack.startUltraFireballAttack(
                            (org.bukkit.entity.EnderDragon) this.getBukkitEntity(),
                            bukkitTargetPlayer,
                            this.getBukkitEntity().getWorld()
                    );
                }

                ticksUntilNextShulkerSpawn = randomBetween(SHULKER_RESPAWN_MIN, SHULKER_RESPAWN_MAX);
            }
        }

        if (healthPercentage < 30) {
            // Атаки при здоровье ниже 10%
            // Спавним Варден только один раз
            if (--this.ticksHBeamAttack <= 0) {
                DragonHorizontalBeamAttack.startHorizontalBeamAttack(CENTER_LOCATION, END_WORLD);
                ticksHBeamAttack = randomBetween(H_BEAM_ATTACK_MIN, H_BEAM_ATTACK_MAX);
            }


        }
        if(healthPercentage < 10){
            if (this.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.SITTING_ATTACKING && !isWardenSpawned) {
                this.spawnWarden(CENTER_LOCATION);
                this.isWardenSpawned = true;
            } else if (this.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.SITTING_FLAMING && !isWardenSpawned) {
                this.spawnElderGuardian(CENTER_LOCATION);
                this.isWardenSpawned = true;
            }
        }

        // always execute
        // Обновление здоровья для новых игроков
        updateHealthForNewPlayers();

        // увеличивает время на портале
        if (this.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.LANDING_APPROACH ||
                this.getPhaseManager().getCurrentPhase().getPhase() == EnderDragonPhase.DYING) {
            timeSinceLastLanding = 0;
        }

        if (++timeSinceLastLanding > 2400) {
            timeSinceLastLanding = 0;
            this.getPhaseManager().setPhase(EnderDragonPhase.LANDING_APPROACH);
        }

    }

    public List<Player> getNearbyPlayers(double radius) {
        // Получаем список игроков в радиусе вокруг дракона
        return this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(radius));
    }

    /**
     * Возвращает список ближайших игроков на основе Bukkit API в заданном радиусе вокруг определенной точки.
     *
     * @param location Местоположение центра поиска игроков.
     * @param radius Радиус поиска.
     * @return Список игроков Bukkit в указанном радиусе.
     */
    public static List<org.bukkit.entity.Player> getNearbyBukkitPlayers(Location location, double radius) {
        List<org.bukkit.entity.Player> nearbyPlayers = new ArrayList<>();
        World world = location.getWorld();
        if (world == null) return nearbyPlayers;

        // Получаем всех игроков в мире
        for (org.bukkit.entity.Player player : world.getPlayers()) {
            // Проверяем расстояние до каждого игрока
            if (player.getLocation().distance(location) <= radius) {
                nearbyPlayers.add(player);
            }
        }
        return nearbyPlayers;
    }

    private void handleEntitySpawning() {
        if (--this.ticksUntilNextCrystalRespawn <= 0) {
            respawnRandomCrystal();
            ticksUntilNextCrystalRespawn = randomBetween(CRYSTAL_RESPAWN_MIN, CRYSTAL_RESPAWN_MAX);
        }

        if (--this.ticksUntilNextPhantomSpawn <= 0) {
//            spawnRandomPhantoms();
            ticksUntilNextPhantomSpawn = randomBetween(PHANTOM_RESPAWN_MIN, PHANTOM_RESPAWN_MAX);
        }

        if (--this.ticksUntilNextShulkerSpawn <= 0) {
            spawnCustomShulkers();
            ticksUntilNextShulkerSpawn = randomBetween(SHULKER_RESPAWN_MIN, SHULKER_RESPAWN_MAX);
        }
    }

    // Метод для генерации случайных значений в заданных пределах
    public static int randomBetween(int min, int max) {
        return min + new Random().nextInt(max - min + 1);
    }



    // Метод для спавна конкретного Варден
    public static void spawnWarden(Location spawnLocation) {
        World world = spawnLocation.getWorld();
        if (world != null) {
            // Спавн варден в заданной локации
            Warden warden = (Warden) world.spawnEntity(spawnLocation, org.bukkit.entity.EntityType.WARDEN);

            // Дополнительные настройки для варден (если нужны)
            warden.setCustomName("Ender Warden");
            warden.setCustomNameVisible(true);
            warden.setGlowing(true);
            addToPurpleTeam(warden.getUniqueId().toString());
        }
    }

    public static void spawnElderGuardian(Location spawnLocation) {
        World world = spawnLocation.getWorld();
        if (world != null) {
            // Спавн варден в заданной локации
            ElderGuardian warden = (ElderGuardian) world.spawnEntity(spawnLocation, org.bukkit.entity.EntityType.ELDER_GUARDIAN);

            // Дополнительные настройки для варден (если нужны)
            warden.setCustomName("Ender Guardian");
            warden.setCustomNameVisible(true);
            warden.setGlowing(true);
            addToPurpleTeam(warden.getUniqueId().toString());
        }
    }

    /**
     * Добавляет сущность или игрока в команду с заданным именем и фиолетовым цветом
     *
     * @param entityId Уникальный идентификатор сущности или игрока
     */
    public static void addToPurpleTeam(String entityId) {
        // Получаем основной Scoreboard сервера
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = manager.getMainScoreboard();

        // Создание или получение команды с фиолетовым цветом
        Team team = scoreboard.getTeam("dragon_team");
        if (team == null) {
            team = scoreboard.registerNewTeam("dragon_team");
            team.setColor(ChatColor.DARK_PURPLE);  // Устанавливаем фиолетовый цвет
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }

        // Добавляем сущность в команду
        team.addEntry(entityId);
    }



    private void spawnCustomShulkers() {
        // Количество шалкеров для спавна
        int shulkerCount = 1;

        for (int i = 0; i < shulkerCount; i++) {
            // Рандомная позиция вокруг центра острова (0, 0) с небольшим разбросом
            double offsetX = (this.getCommandSenderWorld().random.nextDouble() - 0.5) * 40.0;
            double offsetZ = (this.getCommandSenderWorld().random.nextDouble() - 0.5) * 40.0;

            BlockPos spawnPos = CENTER_OF_END.offset((int) offsetX, 0, (int) offsetZ);

            // Спавн шалкера
//            Shulker shulker = EntityType.SHULKER.create(this.getCommandSenderWorld());
            Shulker shulker =  (Shulker) END_WORLD.spawnEntity(CENTER_LOCATION, org.bukkit.entity.EntityType.SHULKER);

            shulker.setGlowing(true);
            shulker.clearLootTable();
            shulker.setLootTable(null);

            addToPurpleTeam(shulker.getUniqueId().toString());

        }
    }

    // Спавн молний в окружности
    public static void spawnLightningAtCircle(Location midPoint, int radius, World world) {
        Set<Location> lightningPositions = getCircularPositionsAround(midPoint, radius, 15 - radius / 10);

        // Асинхронное выполнение с задержкой
        new BukkitRunnable() {
            Iterator<Location> locationIterator = lightningPositions.iterator();

            @Override
            public void run() {
                if (!locationIterator.hasNext()) {
                    this.cancel(); // Завершаем выполнение, когда все молнии заспавнены
                    return;
                }

                Location lightningPos = locationIterator.next();
                Location actualPos = new Location(world, lightningPos.getX(), world.getHighestBlockYAt(lightningPos), lightningPos.getZ());

                int yLevel = world.getHighestBlockYAt(lightningPos);
                if (Math.abs(midPoint.getY() - yLevel) <= 20) {
                    // Устанавливаем молнию как тихую и спавним её
                    LightningStrike lightningBolt = world.spawn(new Location(world, lightningPos.getX(), yLevel, lightningPos.getZ()), LightningStrike.class);
                    lightningBolt.setSilent(true);
                }
            }
        }.runTaskTimer(TempusEssential.getPlugin(), 0, 5); // Запуск с интервалом в 5 тиков между каждым спавном молнии
    }

    // Взрывы по окружности
//    public static void explodeInCircleAround(Location midPoint, int radius, World world) {
//        Set<Location> explodePositions = getCircularPositionsAround(midPoint, radius, 15);
//
//        // Асинхронное выполнение с задержкой
//        new BukkitRunnable() {
//            Iterator<Location> locationIterator = explodePositions.iterator();
//
//            @Override
//            public void run() {
//                if (!locationIterator.hasNext()) {
//                    this.cancel(); // Завершаем выполнение, когда все взрывы произошли
//                    return;
//                }
//
//                Location explodePos = locationIterator.next();
//                Location actualPos = new Location(world, explodePos.getX(), world.getHighestBlockYAt(explodePos), explodePos.getZ());
//
//                int yLevel = world.getHighestBlockYAt(explodePos);
//                if (Math.abs(midPoint.getY() - yLevel) <= 20) {
//                    // Создаём взрыв без разрушения блоков
//                    world.createExplosion(explodePos.getX(), yLevel, explodePos.getZ(), (float) (1 + getDifficulty() / 4), false, false);
//                }
//            }
//        }.runTaskTimer(TempusEssential.getPlugin(), 0, 5); // Запуск с интервалом в 5 тиков между каждым взрывом
//    }




    private static Set<Location> getCircularPositionsAround(Location start, int radius, int precision) {
        Set<Location> positions = new HashSet<>();
        int randomOffset = (int) (Math.random() * 40);

        for (int i = randomOffset; i < 360 + randomOffset; i += precision) {
            double angle = Math.toRadians(i);
            int x = (int) Math.round(radius * Math.cos(angle));
            int z = (int) Math.round(radius * Math.sin(angle));
            positions.add(start.clone().add(x, 0, z));
        }

        return positions;
    }
    private static int getDifficulty() {
        // Пример возвращения сложности
        return Bukkit.getWorlds().get(0).getDifficulty().ordinal();
    }


    private void spawnRandomPhantoms(Player targetPlayer) {
        // Количество фантомов для спавна
        int phantomCount = randomBetween(1, 3);

        for (int i = 0; i < phantomCount; i++) {
            // Рандомная позиция вокруг центра острова (0, 0) с небольшим разбросом
            double offsetX = (this.getCommandSenderWorld().random.nextDouble() - 0.5) * 40.0;
            double offsetZ = (this.getCommandSenderWorld().random.nextDouble() - 0.5) * 40.0;

            BlockPos spawnPos = CENTER_OF_END.offset((int) offsetX, 0, (int) offsetZ);

            // Спавн фантома
            org.bukkit.entity.Phantom bukkitPhantom = (org.bukkit.entity.Phantom) END_WORLD.spawnEntity(
                    new Location(END_WORLD, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5),
                    org.bukkit.entity.EntityType.PHANTOM
            );
            if (bukkitPhantom != null) {
                // Установка цели через Bukkit API
                bukkitPhantom.setTarget(targetPlayer.getBukkitLivingEntity());

                // Дополнительные настройки для фантома (если нужны)
                bukkitPhantom.setGlowing(true); // Например, сделаем фантома светящимся

                addToPurpleTeam(bukkitPhantom.getUniqueId().toString());
                // Логирование
            }
        }
    }

    private void respawnRandomCrystal() {
        // Получаем список всех спайков в мире
        List<SpikeFeature.EndSpike> spikes = SpikeFeature.getSpikesForLevel(this.getCommandSenderWorld().getMinecraftWorld());
        if (spikes.isEmpty()) return;

        // Выбираем случайный спайк
        SpikeFeature.EndSpike randomSpike = spikes.get(this.getCommandSenderWorld().random.nextInt(spikes.size()));

        // Получаем координаты центра спайка и высоту спайка
        BlockPos spikeCenter = new BlockPos(randomSpike.getCenterX(), randomSpike.getHeight(), randomSpike.getCenterZ());

        // Позиция для спавна кристалла: на вершине спайка (на 1 блок выше высоты спайка)
        BlockPos crystalPos = spikeCenter.above();

        // Проверяем, есть ли уже кристалл на этой позиции
        List<EndCrystal> nearbyCrystals = this.getCommandSenderWorld().getEntitiesOfClass(EndCrystal.class, new AABB(crystalPos).inflate(1.0));

        // Если в радиусе одного блока от позиции нет кристаллов, то спавним новый
        if (nearbyCrystals.isEmpty()) {
            EndCrystal crystal = EntityType.END_CRYSTAL.create(this.getCommandSenderWorld());
            if (crystal != null) {
                crystal.setGlowingTag(true);
                addToPurpleTeam(crystal.getUUID().toString());
                crystal.moveTo(crystalPos.getX() + 0.5, crystalPos.getY(), crystalPos.getZ() + 0.5, this.getCommandSenderWorld().random.nextFloat() * 360.0F, 0.0F);
                this.getCommandSenderWorld().addFreshEntity(crystal);
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

    }

    private void updateHealthForNewPlayers() {
        double searchRadius = 100.0D;
        List<Player> playersNearby = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(searchRadius));

        for (Player player : playersNearby) {
            if (seenPlayers.add(player.getUUID())) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + 50.0);
            }
        }
    }


//    @Override
//    public void setDragonFight(EndDragonFight fight) {
//        super.setDragonFight(fight instanceof CustomDragonFight ? fight : new CustomDragonFight((ServerLevel) this.level(), this.level()., CustomDragonFight.Data.DEFAULT));
//    }

    @Override
    public void setFightOrigin(BlockPos fightOrigin) {
        super.setFightOrigin(fightOrigin);
    }

    @Override
    public BlockPos getFightOrigin() {
        return super.getFightOrigin();
    }


    @Override
    protected boolean reallyHurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) source.getEntity();
            String playerName = player.getName().getString();

            // Обновляем урон, нанесенный игроком
            playerDamageMap.put(playerName, playerDamageMap.getOrDefault(playerName, 0.0) + amount);

            // Запоминаем игрока, который сделал последний удар
            lastHitPlayer = playerName;
        }
        return super.reallyHurt(source, amount);
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();

        if(!showedStats){
            Bukkit.getScheduler().runTask(TempusEssential.getPlugin(), () -> {
                // Сортируем игроков по урону по убыванию
                List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(playerDamageMap.entrySet());
                sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

                int rank = 1;

                // Отправляем сообщение только тем игрокам, которые наносили урон
                for (Map.Entry<String, Double> entry : sortedEntries) {
                    String playerName = entry.getKey();
                    double damage = Math.round(entry.getValue() * 100.0) / 100.0;
                    ChatColor color = playerName.equals(lastHitPlayer) ? ChatColor.DARK_PURPLE : ChatColor.LIGHT_PURPLE;

                    // Находим игрока по имени
                    org.bukkit.entity.Player player = Bukkit.getPlayerExact(playerName);
                    if (player != null && player.isOnline()) {
                        if (rank == 1) {
                            player.sendMessage(ChatColor.GOLD + "You did the most damage!");
                        }
                        player.sendMessage(ChatColor.GREEN + "Damage Summary:");
                        player.sendMessage(color + "" +rank + ". " + playerName + ": " + damage + " damage");
                    }

                    rank++; // Увеличиваем номер для следующего игрока
                }
            });
            this.showedStats = true;
        }



    }
}

