package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.game.TrafficEngine
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun GameCanvasView(
    engine: TrafficEngine,
    onTapIntersection: () -> Unit,
    onTapVehicle: (Vehicle) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = engine.gameState
    val signal = engine.signal
    val drone = engine.drone
    val vehicles = engine.vehicles
    val pedestrians = engine.pedestrians
    val chatters = engine.memeChatters
    val ripples = engine.sonicRipples

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("game_canvas_container")
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val scaleX = 1000f / size.width
                    val scaleY = 1000f / size.height
                    val simX = tapOffset.x * scaleX
                    val simY = tapOffset.y * scaleY

                    // Check if tapped a vehicle
                    val tappedVehicle = vehicles.find { v ->
                        simX in (v.x - 35f)..(v.x + 35f) && simY in (v.y - 35f)..(v.y + 35f)
                    }

                    if (tappedVehicle != null) {
                        onTapVehicle(tappedVehicle)
                    } else if (simX in 380f..620f && simY in 380f..620f) {
                        // Tapped central intersection
                        onTapIntersection()
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize().testTag("dhaka_game_canvas")) {
            val scaleX = size.width / 1000f
            val scaleY = size.height / 1000f

            // Apply screen shake when honk meter is dangerously high
            val shakeOffsetX = state.screenShakeX * scaleX
            val shakeOffsetY = state.screenShakeY * scaleY

            withTransform({
                translate(left = shakeOffsetX, top = shakeOffsetY)
                scale(scaleX, scaleY, pivot = Offset.Zero)
            }) {
                // 1. Cyberpunk Dhaka Ground / Base
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF060911)),
                        center = Offset(500f, 500f),
                        radius = 800f
                    )
                )

                // 2. City Blocks / Buildings Grids (Cyber aesthetic background)
                drawCityBuildingBlocks()

                // 3. Roads & Intersections
                drawRoadsAndCrosswalks(signal, state.holoBarricadeActiveSec > 0f)

                // 4. Vehicles
                drawVehicles(vehicles)

                // 5. Jaywalking Pedestrians
                drawPedestrians(pedestrians)

                // 6. Traffic Signal Hologram Posts
                drawHolographicSignals(signal)

                // 7. Cyber Heavy-Lifter Drone & Tractor Beam
                drawCyberDrone(drone)

                // 8. Sonic Ripples (Honk Soundwaves)
                for (r in ripples) {
                    drawCircle(
                        color = NeonGold.copy(alpha = r.alpha),
                        radius = r.radius,
                        center = Offset(r.x, r.y),
                        style = Stroke(width = 3.5f)
                    )
                }

                // 9. EMP Blast Shockwave
                if (state.empBlastRadius > 0f) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonCyan.copy(alpha = 0.4f), Color.Transparent),
                            center = Offset(500f, 500f),
                            radius = state.empBlastRadius
                        ),
                        radius = state.empBlastRadius,
                        center = Offset(500f, 500f)
                    )
                    drawCircle(
                        color = NeonCyan,
                        radius = state.empBlastRadius,
                        center = Offset(500f, 500f),
                        style = Stroke(width = 5f)
                    )
                }
            }
        }

        // Overlay Crisp Bengali Meme Dialogues
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val maxW = maxWidth
            val maxH = maxHeight

            for (chatter in chatters) {
                val relX = (chatter.x / 1000f) * maxW.value
                val relY = (chatter.y / 1000f) * maxH.value

                Box(
                    modifier = Modifier
                        .offset(
                            x = (relX - 45f).coerceAtLeast(8f).dp,
                            y = (relY - 32f).coerceAtLeast(8f).dp
                        )
                        .background(
                            color = CyberSurface.copy(alpha = 0.95f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = NeonGold,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = chatter.text,
                        color = NeonGold,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawCityBuildingBlocks() {
    val blockColor = Color(0xFF0C1322)
    val neonGrid = Color(0xFF1E293B)

    // Corner city blocks
    val corners = listOf(
        Rect(0f, 0f, 400f, 400f),
        Rect(600f, 0f, 1000f, 400f),
        Rect(0f, 600f, 400f, 1000f),
        Rect(600f, 600f, 1000f, 1000f)
    )

    for (rect in corners) {
        drawRoundRect(
            color = blockColor,
            topLeft = rect.topLeft,
            size = rect.size,
            cornerRadius = CornerRadius(16f, 16f)
        )
        drawRoundRect(
            color = neonGrid,
            topLeft = rect.topLeft,
            size = rect.size,
            cornerRadius = CornerRadius(16f, 16f),
            style = Stroke(width = 2f)
        )

        // Futuristic rooftop solar/hologram pad
        val center = rect.center
        drawCircle(
            color = NeonCyan.copy(alpha = 0.08f),
            radius = 70f,
            center = center
        )
        drawCircle(
            color = NeonPink.copy(alpha = 0.15f),
            radius = 40f,
            center = center,
            style = Stroke(width = 2f)
        )
    }
}

private fun DrawScope.drawRoadsAndCrosswalks(signal: IntersectionSignal, holoBarricadeActive: Boolean) {
    val roadColor = Color(0xFF111827)
    val curbColor = NeonCyan.copy(alpha = 0.6f)
    val dashColor = Color(0xFFE2E8F0).copy(alpha = 0.4f)

    // Vertical Road (x: 400 to 600)
    drawRect(
        color = roadColor,
        topLeft = Offset(400f, 0f),
        size = Size(200f, 1000f)
    )
    // Curbs
    drawLine(curbColor, Offset(400f, 0f), Offset(400f, 1000f), strokeWidth = 3f)
    drawLine(curbColor, Offset(600f, 0f), Offset(600f, 1000f), strokeWidth = 3f)

    // Horizontal Road (y: 400 to 600)
    drawRect(
        color = roadColor,
        topLeft = Offset(0f, 400f),
        size = Size(1000f, 200f)
    )
    // Curbs
    drawLine(curbColor, Offset(0f, 400f), Offset(1000f, 400f), strokeWidth = 3f)
    drawLine(curbColor, Offset(0f, 600f), Offset(1000f, 600f), strokeWidth = 3f)

    // Central Intersection Glow
    val isVertGreen = signal.verticalState == SignalState.GREEN
    val junctionGlow = if (isVertGreen) SignalGreen.copy(alpha = 0.08f) else SignalYellow.copy(alpha = 0.08f)
    drawRect(
        color = junctionGlow,
        topLeft = Offset(400f, 400f),
        size = Size(200f, 200f)
    )

    // Center divider dash lines
    // Vertical dashes (skipping intersection 390..610)
    for (y in 10..370 step 40) {
        drawLine(dashColor, Offset(500f, y.toFloat()), Offset(500f, y + 20f), strokeWidth = 3f)
    }
    for (y in 620..980 step 40) {
        drawLine(dashColor, Offset(500f, y.toFloat()), Offset(500f, y + 20f), strokeWidth = 3f)
    }
    // Horizontal dashes
    for (x in 10..370 step 40) {
        drawLine(dashColor, Offset(x.toFloat(), 500f), Offset(x + 20f, 500f), strokeWidth = 3f)
    }
    for (x in 620..980 step 40) {
        drawLine(dashColor, Offset(x.toFloat(), 500f), Offset(x + 20f, 500f), strokeWidth = 3f)
    }

    // Holographic Crosswalks / Stop lines
    drawCrosswalk(Offset(405f, 375f), true, isVertGreen)
    drawCrosswalk(Offset(405f, 610f), true, isVertGreen)
    drawCrosswalk(Offset(375f, 405f), false, !isVertGreen)
    drawCrosswalk(Offset(610f, 405f), false, !isVertGreen)

    // Holo-Barricade laser barrier
    if (holoBarricadeActive) {
        if (!isVertGreen) {
            drawLine(
                brush = Brush.linearGradient(listOf(NeonCyan, NeonPink, NeonCyan)),
                start = Offset(400f, 390f),
                end = Offset(600f, 390f),
                strokeWidth = 6f
            )
            drawLine(
                brush = Brush.linearGradient(listOf(NeonCyan, NeonPink, NeonCyan)),
                start = Offset(400f, 610f),
                end = Offset(600f, 610f),
                strokeWidth = 6f
            )
        } else {
            drawLine(
                brush = Brush.linearGradient(listOf(NeonCyan, NeonPink, NeonCyan)),
                start = Offset(390f, 400f),
                end = Offset(390f, 600f),
                strokeWidth = 6f
            )
            drawLine(
                brush = Brush.linearGradient(listOf(NeonCyan, NeonPink, NeonCyan)),
                start = Offset(610f, 400f),
                end = Offset(610f, 600f),
                strokeWidth = 6f
            )
        }
    }
}

private fun DrawScope.drawCrosswalk(topLeft: Offset, isHorizontalStripe: Boolean, isGreen: Boolean) {
    val stripeColor = if (isGreen) SignalGreen.copy(alpha = 0.5f) else SignalRed.copy(alpha = 0.7f)
    if (isHorizontalStripe) {
        for (i in 0 until 9) {
            drawRect(
                color = stripeColor,
                topLeft = Offset(topLeft.x + i * 21f + 3f, topLeft.y),
                size = Size(14f, 15f)
            )
        }
    } else {
        for (i in 0 until 9) {
            drawRect(
                color = stripeColor,
                topLeft = Offset(topLeft.x, topLeft.y + i * 21f + 3f),
                size = Size(15f, 14f)
            )
        }
    }
}

private fun DrawScope.drawHolographicSignals(signal: IntersectionSignal) {
    val isVertGreen = signal.verticalState == SignalState.GREEN
    val vertColor = if (isVertGreen) SignalGreen else SignalRed
    val horizColor = if (!isVertGreen) SignalGreen else SignalRed

    val posts = listOf(
        Pair(Offset(380f, 380f), vertColor),
        Pair(Offset(620f, 380f), horizColor),
        Pair(Offset(380f, 620f), horizColor),
        Pair(Offset(620f, 620f), vertColor)
    )

    for ((pos, color) in posts) {
        drawCircle(color = CyberSurfaceVariant, radius = 14f, center = pos)
        drawCircle(color = NeonCyan, radius = 14f, center = pos, style = Stroke(width = 2f))
        drawCircle(color = color, radius = 9f, center = pos)
        drawCircle(color = color.copy(alpha = 0.35f), radius = 18f, center = pos)
    }
}

private fun DrawScope.drawVehicles(vehicles: List<Vehicle>) {
    for (v in vehicles) {
        withTransform({
            translate(v.x, v.y)
            val rot = when (v.direction) {
                Direction.NORTH_TO_SOUTH -> 90f
                Direction.SOUTH_TO_NORTH -> 270f
                Direction.WEST_TO_EAST -> 0f
                Direction.EAST_TO_WEST -> 180f
            }
            rotate(rot, pivot = Offset.Zero)

            if (v.isLiftedByDrone) {
                val scale = 1f - (v.droneLiftProgress * 0.35f)
                scale(scale, scale, pivot = Offset.Zero)
            }
        }) {
            val length = v.type.length
            val width = v.type.width
            val color = v.getVisualColor()

            drawRoundRect(
                color = color,
                topLeft = Offset(-length / 2f, -width / 2f),
                size = Size(length, width),
                cornerRadius = CornerRadius(6f, 6f)
            )

            drawRoundRect(
                color = CyberSurface,
                topLeft = Offset(-length / 4f, -width / 3f),
                size = Size(length / 2f, width * 2f / 3f),
                cornerRadius = CornerRadius(4f, 4f)
            )

            drawCircle(
                color = NeonCyan,
                radius = 3.5f,
                center = Offset(length / 2f, -width / 3f)
            )
            drawCircle(
                color = NeonCyan,
                radius = 3.5f,
                center = Offset(length / 2f, width / 3f)
            )

            drawCircle(
                color = NeonCyan.copy(alpha = 0.25f),
                radius = 12f,
                center = Offset(length / 2f + 8f, 0f)
            )

            drawCircle(
                color = DangerRed,
                radius = 3f,
                center = Offset(-length / 2f, -width / 3f)
            )
            drawCircle(
                color = DangerRed,
                radius = 3f,
                center = Offset(-length / 2f, width / 3f)
            )

            when (v.type) {
                VehicleType.ROCKET_LEGUNA -> {
                    drawRoundRect(
                        color = NeonPink,
                        topLeft = Offset(-length / 2f - 9f, -4f),
                        size = Size(9f, 8f),
                        cornerRadius = CornerRadius(3f, 3f)
                    )
                    drawLine(
                        color = NeonCyan,
                        start = Offset(-length / 3f, -width / 2f - 3f),
                        end = Offset(-length / 2f, -width / 2f - 3f),
                        strokeWidth = 2.5f
                    )
                    drawLine(
                        color = NeonCyan,
                        start = Offset(-length / 3f, width / 2f + 3f),
                        end = Offset(-length / 2f, width / 2f + 3f),
                        strokeWidth = 2.5f
                    )
                }
                VehicleType.HOVER_RICKSHAW -> {
                    drawCircle(
                        color = NeonPink,
                        radius = width / 2.2f,
                        center = Offset(-length / 4f, 0f),
                        style = Stroke(width = 2.5f)
                    )
                }
                VehicleType.MURI_TIN_BUS -> {
                    for (wx in -18..18 step 9) {
                        drawRect(
                            color = NeonGold.copy(alpha = 0.8f),
                            topLeft = Offset(wx.toFloat(), -width / 3f),
                            size = Size(5f, 4f)
                        )
                        drawRect(
                            color = NeonGold.copy(alpha = 0.8f),
                            topLeft = Offset(wx.toFloat(), width / 3f - 4f),
                            size = Size(5f, 4f)
                        )
                    }
                    if (v.isBrokenDown) {
                        drawCircle(
                            color = DangerRed.copy(alpha = 0.5f),
                            radius = 16f,
                            center = Offset(-length / 2f, 0f)
                        )
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.7f),
                            radius = 24f,
                            center = Offset(-length / 2f - 10f, 0f)
                        )
                    }
                }
                VehicleType.VIP_CONVOY -> {
                    drawCircle(
                        color = DangerRed,
                        radius = 4f,
                        center = Offset(0f, -width / 4f)
                    )
                    drawCircle(
                        color = NeonCyan,
                        radius = 4f,
                        center = Offset(0f, width / 4f)
                    )
                }
                else -> {}
            }
        }
    }
}

private fun DrawScope.drawPedestrians(pedestrians: List<Pedestrian>) {
    for (p in pedestrians) {
        val color = if (p.isScared) NeonPink else NeonGold
        drawCircle(color = color, radius = 5.5f, center = Offset(p.x, p.y))
        drawCircle(color = NeonCyan.copy(alpha = 0.6f), radius = 8.5f, center = Offset(p.x, p.y), style = Stroke(width = 1.5f))
    }
}

private fun DrawScope.drawCyberDrone(drone: CyberDrone) {
    val droneX = drone.x
    val droneY = drone.y

    if (drone.status == DroneStatus.LIFTING || drone.status == DroneStatus.DISPATCHED) {
        val path = Path().apply {
            moveTo(droneX, droneY)
            lineTo(drone.targetX - 30f, drone.targetY + 15f)
            lineTo(drone.targetX + 30f, drone.targetY + 15f)
            close()
        }
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(NeonCyan.copy(alpha = 0.6f), NeonCyan.copy(alpha = 0.05f)),
                startY = droneY,
                endY = drone.targetY + 20f
            )
        )
    }

    drawCircle(
        color = CyberSurface,
        radius = 16f,
        center = Offset(droneX, droneY)
    )
    drawCircle(
        color = if (drone.isReady) NeonCyan else NeonOrange,
        radius = 16f,
        center = Offset(droneX, droneY),
        style = Stroke(width = 3f)
    )

    val offsets = listOf(
        Offset(-20f, -20f),
        Offset(20f, -20f),
        Offset(-20f, 20f),
        Offset(20f, 20f)
    )
    for (off in offsets) {
        drawLine(
            color = Color.White.copy(alpha = 0.7f),
            start = Offset(droneX, droneY),
            end = Offset(droneX + off.x, droneY + off.y),
            strokeWidth = 2.5f
        )
        drawCircle(
            color = NeonCyan.copy(alpha = 0.8f),
            radius = 9f,
            center = Offset(droneX + off.x, droneY + off.y),
            style = Stroke(width = 2f)
        )
    }

    drawCircle(
        color = if (drone.isReady) NeonGreen else DangerRed,
        radius = 5f,
        center = Offset(droneX, droneY)
    )
}
