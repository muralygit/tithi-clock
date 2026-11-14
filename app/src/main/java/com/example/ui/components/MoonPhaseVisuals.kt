package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.ChandraPearl
import com.example.ui.theme.CosmicMidnight
import com.example.ui.theme.PakshaKrishnaAccent
import com.example.ui.theme.SuryaGold
import com.example.ui.theme.SuryaGoldBright
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Large Astronomical Moon Phase Visualizer combining:
 * 1. An outer 30-Tithi orbital dial ring (each sector = 12° of Sun-Moon elongation).
 * 2. High-detail lunar surface photography (`img_moon_surface_1794626433068`).
 * 3. Mathematically accurate spherical terminator shadow overlay for any elongation [0°, 360°),
 *    preserving subtle Earthshine on the unlit portion of the Moon so craters remain faintly visible.
 */
@Composable
fun AstronomicalMoonPhaseOrb(
    elongationDeg: Double,
    activeTithiNumber: Int, // 1..30
    modifier: Modifier = Modifier,
    orbSize: Dp = 250.dp
) {
    val isShukla = elongationDeg < 180.0
    val accentColor = if (isShukla) SuryaGold else PakshaKrishnaAccent

    Box(
        modifier = modifier.size(orbSize),
        contentAlignment = Alignment.Center
    ) {
        // Outer 30-Tithi Celestial Ring + Glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.minDimension / 2f - 6.dp.toPx()
            val innerDialRadius = outerRadius - 16.dp.toPx()

            // Ambient radial halo behind the Moon
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.25f),
                        accentColor.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = outerRadius * 1.05f
                ),
                radius = outerRadius * 1.05f,
                center = center
            )

            // Draw 30 Tithi sectors (12° each; 0° at top = -90° in screen coordinates)
            for (i in 0 until 30) {
                val startElong = i * 12f
                val startAngleScreen = startElong - 90f
                val isCurrentTithi = (i + 1) == activeTithiNumber
                val isShuklaSector = i < 15

                // Sector arc
                drawArc(
                    color = when {
                        isCurrentTithi -> if (isShuklaSector) SuryaGoldBright else PakshaKrishnaAccent
                        isShuklaSector -> SuryaGold.copy(alpha = 0.28f)
                        else -> PakshaKrishnaAccent.copy(alpha = 0.24f)
                    },
                    startAngle = startAngleScreen + 1f,
                    sweepAngle = 10f,
                    useCenter = false,
                    topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                    size = androidx.compose.ui.geometry.Size(outerRadius * 2f, outerRadius * 2f),
                    style = Stroke(
                        width = if (isCurrentTithi) 6.dp.toPx() else 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                // Tick marks every Tithi boundary
                val tickRad = (startElong - 90.0) * PI / 180.0
                val tickInner = if (i % 5 == 0) innerDialRadius - 3.dp.toPx() else innerDialRadius + 2.dp.toPx()
                val tickOuter = outerRadius - 5.dp.toPx()
                drawLine(
                    color = if (i == 0 || i == 15) SuryaGoldBright else Color.White.copy(alpha = 0.3f),
                    start = Offset(
                        x = center.x + (tickInner * cos(tickRad)).toFloat(),
                        y = center.y + (tickInner * sin(tickRad)).toFloat()
                    ),
                    end = Offset(
                        x = center.x + (tickOuter * cos(tickRad)).toFloat(),
                        y = center.y + (tickOuter * sin(tickRad)).toFloat()
                    ),
                    strokeWidth = if (i == 0 || i == 15) 2.dp.toPx() else 1.dp.toPx()
                )
            }

            // Current exact elongation orbital marker dot on the outer ring
            val markerAngleRad = (elongationDeg - 90.0) * PI / 180.0
            val markerPos = Offset(
                x = center.x + (outerRadius * cos(markerAngleRad)).toFloat(),
                y = center.y + (outerRadius * sin(markerAngleRad)).toFloat()
            )
            drawCircle(
                color = accentColor.copy(alpha = 0.45f),
                radius = 9.dp.toPx(),
                center = markerPos
            )
            drawCircle(
                color = Color.White,
                radius = 4.5.dp.toPx(),
                center = markerPos
            )
        }

        // Inner Lunar Disk (High-res Moon texture + accurate spherical shadow)
        val moonDiskSize = orbSize * 0.74f
        Box(
            modifier = Modifier
                .size(moonDiskSize)
                .clip(CircleShape)
                .background(CosmicMidnight)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.75f),
                            Color.White.copy(alpha = 0.2f),
                            accentColor.copy(alpha = 0.4f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Base high-detail lunar surface photography (scaled slightly to crop outer star margin)
            Image(
                painter = painterResource(id = R.drawable.img_moon_surface_1794626433068),
                contentDescription = "Corresponding Moon Phase Surface",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp)
            )

            // Spherical Terminator Shadow Overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLunarShadowOverlay(
                    elongationDeg = elongationDeg,
                    shadowColor = Color(0xDD050814), // Deep earthshine shadow so subtle craters remain visible
                    drawLitHighlight = false
                )
            }
        }
    }
}

/**
 * Compact Vector/Canvas Moon Phase icon for lists, 30-Tithi Atlas cards, and forecast rows.
 */
@Composable
fun MiniMoonPhaseCanvas(
    elongationDeg: Double,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    val isShukla = elongationDeg < 180.0
    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f - 2.dp.toPx()
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        // Base dark lunar disk (Earthshine dark side)
        drawCircle(
            color = Color(0xFF131B36),
            radius = radius,
            center = center
        )

        // Draw illuminated portion of the Moon
        val litPath = buildIlluminatedMoonPath(
            center = center,
            radius = radius,
            elongationDeg = elongationDeg
        )
        drawPath(
            path = litPath,
            brush = Brush.radialGradient(
                colors = listOf(
                    ChandraPearl,
                    if (isShukla) Color(0xFFF7E4B2) else Color(0xFFDCE6FA),
                    if (isShukla) Color(0xFFD8B25C) else Color(0xFF9EB4E0)
                ),
                center = Offset(center.x - radius * 0.2f, center.y - radius * 0.2f),
                radius = radius * 1.3f
            )
        )

        // Subtle rim border
        drawCircle(
            color = if (isShukla) SuryaGold.copy(alpha = 0.6f) else PakshaKrishnaAccent.copy(alpha = 0.6f),
            radius = radius,
            center = center,
            style = Stroke(width = 1.2.dp.toPx())
        )
    }
}

/**
 * Builds the exact 2D projected path of the **unlit (shadow)** portion of the Moon
 * and draws it over the textured moon sphere.
 */
private fun DrawScope.drawLunarShadowOverlay(
    elongationDeg: Double,
    shadowColor: Color,
    drawLitHighlight: Boolean
) {
    val radius = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    val shadowPath = buildShadowMoonPath(center, radius, elongationDeg)
    drawPath(path = shadowPath, color = shadowColor)

    // Soft spherical vignette around limb for 3D realism
    drawCircle(
        brush = Brush.radialGradient(
            0.78f to Color.Transparent,
            0.94f to Color(0x44040711),
            1.0f to Color(0xAA040711),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )

    if (drawLitHighlight) {
        drawCircle(
            color = Color.White.copy(alpha = 0.15f),
            radius = radius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

/**
 * Computes the exact illuminated region Path of a lunar disk of given [center] and [radius]
 * for Sun-Moon elongation [elongationDeg] in [0, 360):
 * - 0° = New Moon (Amavasya end / Shukla Pradhama start)
 * - 90° = First Quarter (Right half lit)
 * - 180° = Full Moon (Pournami end)
 * - 270° = Third Quarter (Left half lit)
 */
fun buildIlluminatedMoonPath(
    center: Offset,
    radius: Float,
    elongationDeg: Double
): Path {
    val norm = ((elongationDeg % 360.0) + 360.0) % 360.0
    val path = Path()

    // Near New Moon: very thin sliver or completely dark
    if (norm < 1.0 || norm > 359.0) {
        return path
    }
    // Near Full Moon: full circle
    if (norm in 179.0..181.0) {
        path.addOval(Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius))
        return path
    }

    val isWaxing = norm < 180.0
    val phaseRad = norm * PI / 180.0
    // Terminator x-semi-axis = radius * cos(elongation)
    val cosPhase = cos(phaseRad).toFloat()

    val steps = 72
    for (i in 0..steps) {
        val theta = -PI / 2.0 + (PI * i / steps) // -90° (top) to +90° (bottom)
        val y = center.y + (radius * sin(theta)).toFloat()
        val xLimb = (radius * cos(theta)).toFloat()
        // Outer lit limb: right side (+xLimb) for waxing, left side (-xLimb) for waning
        val x = if (isWaxing) center.x + xLimb else center.x - xLimb
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }

    // Return from bottom (+90°) to top (-90°) along the terminator ellipse
    for (i in steps downTo 0) {
        val theta = -PI / 2.0 + (PI * i / steps)
        val y = center.y + (radius * sin(theta)).toFloat()
        val xTermBase = (radius * cos(theta)).toFloat()
        val x = if (isWaxing) {
            center.x + xTermBase * cosPhase
        } else {
            center.x - xTermBase * cosPhase
        }
        path.lineTo(x, y)
    }

    path.close()
    return path
}

/**
 * Computes the unlit (shadowed) region Path of the lunar disk for overlaying on the real Moon image.
 */
fun buildShadowMoonPath(
    center: Offset,
    radius: Float,
    elongationDeg: Double
): Path {
    // Shadow is the complement of the illuminated region, which is identical to
    // the illuminated region rotated by 180° and flipped horizontally!
    val norm = ((elongationDeg % 360.0) + 360.0) % 360.0
    val path = Path()

    // Full Moon: virtually no shadow
    if (norm in 179.2..180.8) {
        return path
    }
    // New Moon: full shadow disk
    if (norm < 0.8 || norm > 359.2) {
        path.addOval(Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius))
        return path
    }

    val isWaxing = norm < 180.0
    val phaseRad = norm * PI / 180.0
    val cosPhase = cos(phaseRad).toFloat()

    val steps = 72
    // Shadow outer limb is on the LEFT for waxing moon, RIGHT for waning moon
    for (i in 0..steps) {
        val theta = -PI / 2.0 + (PI * i / steps)
        val y = center.y + (radius * sin(theta)).toFloat()
        val xLimb = (radius * cos(theta)).toFloat()
        val x = if (isWaxing) center.x - xLimb else center.x + xLimb
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }

    // Terminator curve from bottom to top
    for (i in steps downTo 0) {
        val theta = -PI / 2.0 + (PI * i / steps)
        val y = center.y + (radius * sin(theta)).toFloat()
        val xTermBase = (radius * cos(theta)).toFloat()
        val x = if (isWaxing) {
            center.x + xTermBase * cosPhase
        } else {
            center.x - xTermBase * cosPhase
        }
        path.lineTo(x, y)
    }

    path.close()
    return path
}

/**
 * Renders an Android [Bitmap] of the current Moon Phase for the Home Screen AppWidget (`RemoteViews`).
 */
fun createWidgetMoonPhaseBitmap(elongationDeg: Double, sizePx: Int = 192): Bitmap {
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bmp)
    val cx = sizePx / 2f
    val cy = sizePx / 2f
    val radius = sizePx * 0.42f

    val norm = ((elongationDeg % 360.0) + 360.0) % 360.0
    val isWaxing = norm < 180.0

    // Outer subtle glow
    val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = RadialGradient(
            cx,
            cy,
            sizePx * 0.49f,
            intArrayOf(
                if (isWaxing) AndroidColor.argb(90, 245, 196, 81) else AndroidColor.argb(90, 124, 166, 255),
                AndroidColor.TRANSPARENT
            ),
            floatArrayOf(0.6f, 1.0f),
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawCircle(cx, cy, sizePx * 0.49f, glowPaint)

    // Base dark lunar sphere
    val darkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.rgb(18, 25, 52)
        style = Paint.Style.FILL
    }
    canvas.drawCircle(cx, cy, radius, darkPaint)

    // Illuminated portion
    if (norm >= 1.0 && norm <= 359.0) {
        val litPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx - radius * 0.2f,
                cy - radius * 0.2f,
                radius * 1.3f,
                intArrayOf(
                    AndroidColor.rgb(255, 251, 240),
                    AndroidColor.rgb(245, 218, 150),
                    AndroidColor.rgb(212, 168, 78)
                ),
                floatArrayOf(0f, 0.65f, 1f),
                Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
        }

        if (norm in 178.5..181.5) {
            canvas.drawCircle(cx, cy, radius, litPaint)
        } else {
            val phaseRad = norm * PI / 180.0
            val cosPhase = cos(phaseRad).toFloat()
            val androidPath = AndroidPath()
            val steps = 60
            for (i in 0..steps) {
                val theta = -PI / 2.0 + (PI * i / steps)
                val y = cy + (radius * sin(theta)).toFloat()
                val xLimb = (radius * cos(theta)).toFloat()
                val x = if (isWaxing) cx + xLimb else cx - xLimb
                if (i == 0) androidPath.moveTo(x, y) else androidPath.lineTo(x, y)
            }
            for (i in steps downTo 0) {
                val theta = -PI / 2.0 + (PI * i / steps)
                val y = cy + (radius * sin(theta)).toFloat()
                val xTerm = (radius * cos(theta)).toFloat()
                val x = if (isWaxing) cx + xTerm * cosPhase else cx - xTerm * cosPhase
                androidPath.lineTo(x, y)
            }
            androidPath.close()
            canvas.drawPath(androidPath, litPaint)
        }
    }

    // Rim stroke
    val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = if (isWaxing) AndroidColor.argb(190, 245, 196, 81) else AndroidColor.argb(190, 124, 166, 255)
        style = Paint.Style.STROKE
        strokeWidth = sizePx * 0.022f
    }
    canvas.drawCircle(cx, cy, radius, rimPaint)

    return bmp
}
