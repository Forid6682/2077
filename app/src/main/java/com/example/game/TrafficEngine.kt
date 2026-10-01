package com.example.game

import com.example.audio.SoundSystem
import com.example.model.*
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.random.Random

class TrafficEngine(
    private val soundSystem: SoundSystem
) {
    var gameState: GameState = GameState()
    val signal: IntersectionSignal = IntersectionSignal()
    val drone: CyberDrone = CyberDrone()

    val vehicles: MutableList<Vehicle> = mutableListOf()
    val pedestrians: MutableList<Pedestrian> = mutableListOf()
    val memeChatters: MutableList<MemeChatter> = mutableListOf()
    val sonicRipples: MutableList<SonicRipple> = mutableListOf()

    private var nextVehicleId = 1L
    private var nextPedestrianId = 1L
    private var nextChatterId = 1L
    private var nextRippleId = 1L

    private var spawnTimerSec = 0f
    private var pedestrianSpawnTimerSec = 0f
    private var vipProtocolTimerSec = 0f
    private var honkSoundCooldown = 0f

    // Iconic Dhaka traffic meme lines
    private val memeLines = listOf(
        "মামা একটু সাইড দেন!",
        "আইজকা জ্যাম ছুডামুই!",
        "সিগন্যাল ভাইঙ্গা কই যান?!",
        "হর্ন দিলে রাস্তা ফাঁকা অইব?!",
        "লেগুনা সাইড লও ভাই!",
        "অফিসে লেট হইলে চাকরি শেষ!",
        "ভিআইপি আইতাছে, পিছে সরেন!",
        "মামা একটু চাপায়া লন!",
        "রাস্তা কি আপনের বাপের?!",
        "সবাই হর্ন বাজান একসাথে!"
    )

    fun startLevel(level: GameLevel, mode: GameMode = GameMode.CAMPAIGN) {
        gameState = gameState.copy(
            mode = mode,
            currentLevel = level,
            status = GameStatus.PLAYING,
            score = 0,
            carsPassed = 0,
            honkMeter = 10f,
            timeRemainingSec = level.timeLimitSec.toFloat(),
            isVipProtocolActive = false,
            vipTimerSec = 0f,
            screenShakeX = 0f,
            screenShakeY = 0f,
            empCooldownSec = 0f,
            holoBarricadeActiveSec = 0f,
            isDroneModeSelected = false
        )
        vehicles.clear()
        pedestrians.clear()
        memeChatters.clear()
        sonicRipples.clear()
        signal.verticalState = SignalState.GREEN
        signal.horizontalState = SignalState.RED
        signal.isEmergencyVipLocked = false
        drone.status = DroneStatus.READY
        drone.targetVehicleId = null

        spawnTimerSec = 0f
        pedestrianSpawnTimerSec = 0f
        vipProtocolTimerSec = level.vipProtocolIntervalSec
    }

    fun toggleSignal() {
        signal.toggle()
        soundSystem.playSignalClick()
    }

    fun deployDrone(targetVehicle: Vehicle? = null) {
        if (!drone.isReady) return

        val target = targetVehicle ?: vehicles.firstOrNull { it.isBrokenDown }
        ?: vehicles.filter { it.waitingTimeSec > 3.0f }.maxByOrNull { it.waitingTimeSec }
        ?: vehicles.maxByOrNull { it.waitingTimeSec }

        if (target != null) {
            drone.status = DroneStatus.DISPATCHED
            drone.targetVehicleId = target.id
            drone.targetX = target.x
            drone.targetY = target.y
            soundSystem.playDroneAction()
        }
    }

    fun triggerEmpBlast() {
        if (gameState.empCooldownSec > 0f) return
        gameState.empCooldownSec = 15f
        gameState.empBlastRadius = 10f
        gameState.honkMeter = (gameState.honkMeter - 35f).coerceAtLeast(0f)
        soundSystem.playEmpBlast()

        // Calms reckless drivers & clears breakdown smoke
        for (v in vehicles) {
            v.isReckless = false
            v.waitingTimeSec = 0f
        }
    }

    fun triggerHoloBarricade() {
        gameState.holoBarricadeActiveSec = 6.0f
        soundSystem.playTone(600f, 900f, 200, 0.8f)
    }

    fun triggerWhistle() {
        soundSystem.playPoliceWhistle()
        // Scares jaywalking pedestrians off the road and stops reckless legunas
        for (p in pedestrians) {
            p.isScared = true
        }
        for (v in vehicles) {
            if (v.isReckless) {
                v.isReckless = false
                v.speed = v.type.baseSpeed * 0.5f
            }
        }
    }

    /**
     * Main simulation loop called per frame (~60 FPS, deltaSec ≈ 0.016s).
     */
    fun update(deltaSec: Float) {
        if (gameState.status != GameStatus.PLAYING) return

        val dt = deltaSec.coerceIn(0.001f, 0.05f)

        // 1. Level time remaining (in campaign mode)
        if (gameState.mode == GameMode.CAMPAIGN) {
            gameState.timeRemainingSec -= dt
            if (gameState.timeRemainingSec <= 0f) {
                if (gameState.carsPassed >= gameState.currentLevel.targetCarsToPass) {
                    gameState.status = GameStatus.LEVEL_COMPLETED
                } else {
                    gameState.status = GameStatus.GAME_OVER
                }
                return
            }
            if (gameState.carsPassed >= gameState.currentLevel.targetCarsToPass) {
                gameState.status = GameStatus.LEVEL_COMPLETED
                return
            }
        }

        // 2. Cooldowns
        if (gameState.empCooldownSec > 0f) {
            gameState.empCooldownSec = (gameState.empCooldownSec - dt).coerceAtLeast(0f)
        }
        if (gameState.holoBarricadeActiveSec > 0f) {
            gameState.holoBarricadeActiveSec = (gameState.holoBarricadeActiveSec - dt).coerceAtLeast(0f)
        }
        if (honkSoundCooldown > 0f) {
            honkSoundCooldown -= dt
        }

        // 3. Drone Update
        updateDrone(dt)

        // 4. VIP Protocol Spawning & State
        vipProtocolTimerSec -= dt
        if (vipProtocolTimerSec <= 0f && !gameState.isVipProtocolActive) {
            triggerVipProtocol()
        }
        if (gameState.isVipProtocolActive) {
            gameState.vipTimerSec -= dt
            if (gameState.vipTimerSec <= 0f) {
                gameState.isVipProtocolActive = false
                signal.isEmergencyVipLocked = false
            }
        }

        // 5. Vehicle Spawning
        spawnTimerSec -= dt
        if (spawnTimerSec <= 0f) {
            spawnVehicle()
            spawnTimerSec = (gameState.currentLevel.spawnRateSec * Random.nextDouble(0.7, 1.3)).toFloat()
        }

        // 6. Pedestrian Spawning
        pedestrianSpawnTimerSec -= dt
        if (pedestrianSpawnTimerSec <= 0f && pedestrians.size < 4) {
            spawnPedestrian()
            pedestrianSpawnTimerSec = Random.nextDouble(4.0, 8.0).toFloat()
        }

        // 7. Update Vehicles
        updateVehicles(dt)

        // 8. Update Pedestrians
        updatePedestrians(dt)

        // 9. Update Honk Meter and Screen Shake
        updateHonkMeter(dt)

        // 10. Update Effects (Chatters, EMP shockwave, Ripples)
        updateEffects(dt)

        // Game Over Check on Honk Meter reaching 100%
        if (gameState.honkMeter >= 100f) {
            gameState.status = GameStatus.GAME_OVER
            soundSystem.vibrate(300)
        }
    }

    private fun triggerVipProtocol() {
        gameState.isVipProtocolActive = true
        gameState.vipTimerSec = 8.0f
        vipProtocolTimerSec = gameState.currentLevel.vipProtocolIntervalSec
        gameState.vipDirection = if (Random.nextBoolean()) Direction.WEST_TO_EAST else Direction.NORTH_TO_SOUTH

        // Emergency lock signal in VIP favor
        signal.isEmergencyVipLocked = true
        if (gameState.vipDirection.isVertical()) {
            signal.setVerticalGreen()
        } else {
            signal.setHorizontalGreen()
        }

        soundSystem.playVipSiren()

        // Add VIP convoy
        val (startX, startY) = when (gameState.vipDirection) {
            Direction.WEST_TO_EAST -> Pair(-80f, 545f)
            Direction.EAST_TO_WEST -> Pair(1080f, 455f)
            Direction.NORTH_TO_SOUTH -> Pair(455f, -80f)
            Direction.SOUTH_TO_NORTH -> Pair(545f, 1080f)
        }

        vehicles.add(
            Vehicle(
                id = nextVehicleId++,
                type = VehicleType.VIP_CONVOY,
                x = startX,
                y = startY,
                direction = gameState.vipDirection,
                speed = VehicleType.VIP_CONVOY.baseSpeed,
                chatter = "ভিআইপি প্রোটোকল! সরুন!"
            )
        )
    }

    private fun spawnVehicle() {
        val level = gameState.currentLevel
        val dir = Direction.entries.random()

        // Choose lane coordinates
        val (startX, startY) = when (dir) {
            Direction.NORTH_TO_SOUTH -> Pair(455f, -60f)
            Direction.SOUTH_TO_NORTH -> Pair(545f, 1060f)
            Direction.WEST_TO_EAST -> Pair(-60f, 545f)
            Direction.EAST_TO_WEST -> Pair(1060f, 455f)
        }

        // Avoid overlap at spawn
        val hasOverlap = vehicles.any { v ->
            v.direction == dir && hypot(v.x - startX, v.y - startY) < 70f
        }
        if (hasOverlap) return

        // Pick type based on probabilities
        val roll = Random.nextFloat()
        val type = when {
            roll < level.legunaProbability -> VehicleType.ROCKET_LEGUNA
            roll < level.legunaProbability + level.rickshawProbability -> VehicleType.HOVER_RICKSHAW
            roll < level.legunaProbability + level.rickshawProbability + 0.18f -> VehicleType.HOVER_CNG
            roll < level.legunaProbability + level.rickshawProbability + 0.38f -> VehicleType.MURI_TIN_BUS
            else -> VehicleType.CYBER_CAR
        }

        vehicles.add(
            Vehicle(
                id = nextVehicleId++,
                type = type,
                x = startX,
                y = startY,
                direction = dir,
                speed = type.baseSpeed
            )
        )
    }

    private fun spawnPedestrian() {
        val isHorizontalCrossing = Random.nextBoolean()
        val p = if (isHorizontalCrossing) {
            // Crossing vertical road around y=360 or y=640
            val yPos = if (Random.nextBoolean()) 360f else 640f
            val startFromLeft = Random.nextBoolean()
            Pedestrian(
                id = nextPedestrianId++,
                x = if (startFromLeft) 340f else 660f,
                y = yPos,
                axis = PedestrianCrossingAxis.HORIZONTAL,
                direction = if (startFromLeft) 1f else -1f,
                speed = 1.3f
            )
        } else {
            // Crossing horizontal road around x=360 or x=640
            val xPos = if (Random.nextBoolean()) 360f else 640f
            val startFromTop = Random.nextBoolean()
            Pedestrian(
                id = nextPedestrianId++,
                x = xPos,
                y = if (startFromTop) 340f else 660f,
                axis = PedestrianCrossingAxis.VERTICAL,
                direction = if (startFromTop) 1f else -1f,
                speed = 1.3f
            )
        }
        pedestrians.add(p)
    }

    private fun updateVehicles(dt: Float) {
        val stopLineN = 390f
        val stopLineS = 610f
        val stopLineW = 390f
        val stopLineE = 610f

        val iterator = vehicles.iterator()
        while (iterator.hasNext()) {
            val v = iterator.next()

            // If being lifted by drone
            if (v.isLiftedByDrone) {
                v.droneLiftProgress = (v.droneLiftProgress + dt * 1.2f).coerceAtMost(1f)
                if (v.droneLiftProgress >= 1f) {
                    iterator.remove()
                    // Reward coins & score
                    val rewardCoins = (v.type.coinValue * gameState.upgrades.getCoinMultiplier()).toInt()
                    gameState.coins += rewardCoins
                    gameState.score += v.type.scoreValue + 30
                    gameState.honkMeter = (gameState.honkMeter - 10f).coerceAtLeast(0f)
                    soundSystem.playCoinChime()
                }
                continue
            }

            // Check if vehicle has broken down (only Muri-Tin bus after waiting)
            if (!v.isBrokenDown && v.type == VehicleType.MURI_TIN_BUS && v.waitingTimeSec > 3.0f) {
                if (Random.nextFloat() < gameState.currentLevel.busBreakdownChance * 0.05f) {
                    v.isBrokenDown = true
                    v.speed = 0f
                    addChatter(v.x, v.y - 20f, "ইঞ্জিন গরম হইয়া সিজ মারছে!")
                }
            }

            // Determine if vehicle must stop for signal
            var mustStopForSignal = false
            val isBeforeStopLine = when (v.direction) {
                Direction.NORTH_TO_SOUTH -> v.y < stopLineN && v.y > stopLineN - 60f
                Direction.SOUTH_TO_NORTH -> v.y > stopLineS && v.y < stopLineS + 60f
                Direction.WEST_TO_EAST -> v.x < stopLineW && v.x > stopLineW - 60f
                Direction.EAST_TO_WEST -> v.x > stopLineE && v.x < stopLineE + 60f
            }

            if (isBeforeStopLine) {
                val isRed = when (v.direction) {
                    Direction.NORTH_TO_SOUTH, Direction.SOUTH_TO_NORTH -> signal.verticalState == SignalState.RED
                    Direction.WEST_TO_EAST, Direction.EAST_TO_WEST -> signal.horizontalState == SignalState.RED
                }

                if (isRed) {
                    // Check if Rocket-Leguna becomes reckless!
                    if (v.type == VehicleType.ROCKET_LEGUNA && v.waitingTimeSec >= v.type.impatienceThresholdSec) {
                        if (!v.isReckless) {
                            v.isReckless = true
                            addChatter(v.x, v.y - 20f, "সিগন্যাল দেখার টাইম নাই!")
                        }
                    }

                    // VIP Convoy never stops for red!
                    if (v.type != VehicleType.VIP_CONVOY && !v.isReckless) {
                        mustStopForSignal = true
                    }
                }
            }

            // Check Holo Barricade blocking cross traffic
            if (gameState.holoBarricadeActiveSec > 0f && isBeforeStopLine) {
                if (!signal.verticalState.name.contains("GREEN") && v.direction.isVertical()) {
                    mustStopForSignal = true
                } else if (!signal.horizontalState.name.contains("GREEN") && v.direction.isHorizontal()) {
                    mustStopForSignal = true
                }
            }

            // Check vehicle in front
            var obstacleAhead = false
            for (other in vehicles) {
                if (other.id == v.id || other.direction != v.direction) continue

                val isAhead = when (v.direction) {
                    Direction.NORTH_TO_SOUTH -> other.y > v.y && (other.y - v.y) < (v.type.length + 25f)
                    Direction.SOUTH_TO_NORTH -> other.y < v.y && (v.y - other.y) < (v.type.length + 25f)
                    Direction.WEST_TO_EAST -> other.x > v.x && (other.x - v.x) < (v.type.length + 25f)
                    Direction.EAST_TO_WEST -> other.x < v.x && (v.x - other.x) < (v.type.length + 25f)
                }

                if (isAhead) {
                    obstacleAhead = true
                    break
                }
            }

            // Check pedestrian blocking
            for (p in pedestrians) {
                val isBlocking = when (v.direction) {
                    Direction.NORTH_TO_SOUTH -> p.y > v.y && (p.y - v.y) < 35f && abs(p.x - v.x) < 20f
                    Direction.SOUTH_TO_NORTH -> p.y < v.y && (v.y - p.y) < 35f && abs(p.x - v.x) < 20f
                    Direction.WEST_TO_EAST -> p.x > v.x && (p.x - v.x) < 35f && abs(p.y - v.y) < 20f
                    Direction.EAST_TO_WEST -> p.x < v.x && (v.x - p.x) < 35f && abs(p.y - v.y) < 20f
                }
                if (isBlocking) {
                    obstacleAhead = true
                    break
                }
            }

            // Acceleration & braking
            if (v.isBrokenDown) {
                v.speed = 0f
                v.waitingTimeSec += dt
            } else if (mustStopForSignal || obstacleAhead) {
                v.speed = (v.speed - dt * 10f).coerceAtLeast(0f)
                v.waitingTimeSec += dt

                // Impatience honking
                if (v.waitingTimeSec > 1.8f && Random.nextFloat() < 0.08f) {
                    triggerCarHonk(v)
                }
            } else {
                val targetSpeed = if (v.isReckless) v.type.baseSpeed * 1.5f else v.type.baseSpeed
                v.speed = (v.speed + dt * 6f).coerceAtMost(targetSpeed)
                v.waitingTimeSec = (v.waitingTimeSec - dt * 2f).coerceAtLeast(0f)
            }

            // Move vehicle
            when (v.direction) {
                Direction.NORTH_TO_SOUTH -> v.y += v.speed * 60f * dt
                Direction.SOUTH_TO_NORTH -> v.y -= v.speed * 60f * dt
                Direction.WEST_TO_EAST -> v.x += v.speed * 60f * dt
                Direction.EAST_TO_WEST -> v.x -= v.speed * 60f * dt
            }

            // Check if cleared the intersection
            val pastIntersection = when (v.direction) {
                Direction.NORTH_TO_SOUTH -> v.y > 620f
                Direction.SOUTH_TO_NORTH -> v.y < 380f
                Direction.WEST_TO_EAST -> v.x > 620f
                Direction.EAST_TO_WEST -> v.x < 380f
            }
            if (pastIntersection && !v.isPastIntersection) {
                v.isPastIntersection = true
                gameState.carsPassed++
                val reward = (v.type.coinValue * gameState.upgrades.getCoinMultiplier()).toInt()
                gameState.coins += reward
                gameState.score += v.type.scoreValue
                gameState.honkMeter = (gameState.honkMeter - 1.5f).coerceAtLeast(0f)
            }

            // Remove off-screen vehicles
            val isOffScreen = v.x < -120f || v.x > 1120f || v.y < -120f || v.y > 1120f
            if (isOffScreen) {
                iterator.remove()
            }
        }
    }

    private fun triggerCarHonk(v: Vehicle) {
        val multiplier = gameState.upgrades.getHonkMultiplier()
        gameState.honkMeter = (gameState.honkMeter + 1.8f * multiplier).coerceAtMost(100f)

        // Sonic wave ripple animation
        sonicRipples.add(SonicRipple(id = nextRippleId++, x = v.x, y = v.y))

        if (honkSoundCooldown <= 0f) {
            soundSystem.playDhakaHonk()
            honkSoundCooldown = 0.35f
        }

        if (Random.nextFloat() < 0.25f && memeChatters.size < 3) {
            addChatter(v.x, v.y - 25f, memeLines.random())
        }
    }

    private fun updatePedestrians(dt: Float) {
        val iter = pedestrians.iterator()
        while (iter.hasNext()) {
            val p = iter.next()
            val moveSpeed = if (p.isScared) p.speed * 2.2f else p.speed

            when (p.axis) {
                PedestrianCrossingAxis.HORIZONTAL -> {
                    p.x += p.direction * moveSpeed * 50f * dt
                    if (p.x < 310f || p.x > 690f) {
                        iter.remove()
                    }
                }
                PedestrianCrossingAxis.VERTICAL -> {
                    p.y += p.direction * moveSpeed * 50f * dt
                    if (p.y < 310f || p.y > 690f) {
                        iter.remove()
                    }
                }
            }
        }
    }

    private fun updateDrone(dt: Float) {
        when (drone.status) {
            DroneStatus.READY -> {
                // Drone hovers around top right
                drone.x = 880f
                drone.y = 120f
            }
            DroneStatus.DISPATCHED -> {
                val targetV = vehicles.find { it.id == drone.targetVehicleId }
                if (targetV == null) {
                    drone.status = DroneStatus.READY
                    return
                }
                drone.targetX = targetV.x
                drone.targetY = targetV.y

                val dx = drone.targetX - drone.x
                val dy = drone.targetY - drone.y
                val dist = hypot(dx, dy)
                val flySpeed = 450f + (gameState.upgrades.droneSpeedLevel * 100f)

                if (dist < 20f) {
                    drone.status = DroneStatus.LIFTING
                    drone.liftTimer = 0f
                    targetV.isLiftedByDrone = true
                    soundSystem.playDroneAction()
                    addChatter(targetV.x, targetV.y - 30f, "ড্রোন দিয়ে সাইড করা হইছে!")
                } else {
                    drone.x += (dx / dist) * flySpeed * dt
                    drone.y += (dy / dist) * flySpeed * dt
                }
            }
            DroneStatus.LIFTING -> {
                drone.liftTimer += dt
                // Fly off-screen with vehicle
                drone.y -= 300f * dt
                val targetV = vehicles.find { it.id == drone.targetVehicleId }
                targetV?.y = drone.y
                targetV?.x = drone.x

                if (drone.liftTimer >= 1.5f || drone.y < -50f) {
                    drone.status = DroneStatus.RECHARGING
                    drone.cooldownRemainingSec = gameState.upgrades.getDroneCooldown()
                }
            }
            DroneStatus.RECHARGING -> {
                drone.cooldownRemainingSec -= dt
                // Return to home base
                val homeX = 880f
                val homeY = 120f
                drone.x += (homeX - drone.x) * 3f * dt
                drone.y += (homeY - drone.y) * 3f * dt

                if (drone.cooldownRemainingSec <= 0f) {
                    drone.status = DroneStatus.READY
                }
            }
        }
    }

    private fun updateHonkMeter(dt: Float) {
        // Natural decay when traffic is flowing
        val stoppedVehiclesCount = vehicles.count { it.waitingTimeSec > 1.0f }
        if (stoppedVehiclesCount == 0) {
            gameState.honkMeter = (gameState.honkMeter - 2.5f * dt).coerceAtLeast(0f)
        } else {
            // Gradual rise if lots of cars stopped
            val rise = stoppedVehiclesCount * 0.4f * gameState.upgrades.getHonkMultiplier() * dt
            gameState.honkMeter = (gameState.honkMeter + rise).coerceAtMost(100f)
        }

        // Screen shake when honk meter is dangerously high
        if (gameState.honkMeter > 60f) {
            val shakeIntensity = ((gameState.honkMeter - 60f) / 40f) * 14f
            gameState.screenShakeX = (Random.nextFloat() - 0.5f) * shakeIntensity
            gameState.screenShakeY = (Random.nextFloat() - 0.5f) * shakeIntensity
        } else {
            gameState.screenShakeX = 0f
            gameState.screenShakeY = 0f
        }
    }

    private fun updateEffects(dt: Float) {
        // EMP wave expand
        if (gameState.empBlastRadius > 0f) {
            gameState.empBlastRadius += dt * 700f
            if (gameState.empBlastRadius > 800f) {
                gameState.empBlastRadius = 0f
            }
        }

        // Sonic ripples
        val rippleIter = sonicRipples.iterator()
        while (rippleIter.hasNext()) {
            val r = rippleIter.next()
            r.radius += dt * 140f
            r.alpha = (1f - (r.radius / r.maxRadius)).coerceIn(0f, 1f)
            if (r.radius >= r.maxRadius) {
                rippleIter.remove()
            }
        }

        // Meme chatters
        val chatterIter = memeChatters.iterator()
        while (chatterIter.hasNext()) {
            val c = chatterIter.next()
            c.remainingLifeSec -= dt
            if (c.remainingLifeSec <= 0f) {
                chatterIter.remove()
            }
        }
    }

    private fun addChatter(x: Float, y: Float, text: String) {
        memeChatters.add(MemeChatter(id = nextChatterId++, text = text, x = x, y = y))
    }
}
