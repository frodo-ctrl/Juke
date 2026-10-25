package com.thomaskioko.tvmaniac.app

import android.graphics.Paint as NativePaint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val SplashBackground = Color(0xFF373737)
private val SplashSurface = Color(0xFF43474C)
private val SplashDeep = Color(0xFF2B2B2B)
private val SplashText = Color(0xFFF8FDFF)
private val SplashCenterScrim = Color(0xFF141414)
private val SplashEntranceEasing = CubicBezierEasing(0.21f, 0.61f, 0.35f, 1f)

private val ChineseBrandFont = FontFamily(
    Font(R.font.noto_serif_sc_900, weight = FontWeight.Black),
)

private val SloganFont = FontFamily(
    Font(R.font.noto_sans_sc_200, weight = FontWeight.Thin),
)

@Composable
internal fun JukeSplashScreen(onFinished: () -> Unit) {
    val context = LocalContext.current
    val animationsEnabled = remember(context) {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) > 0f
    }
    val saturationFilter = remember {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(1.1f) })
    }

    LaunchedEffect(Unit) {
        delay(1_650L)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackground),
    ) {
        Image(
            painter = painterResource(R.drawable.juke_splash_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(3.dp)
                .graphicsLayer {
                    scaleX = 1.04f
                    scaleY = 1.04f
                },
            contentScale = ContentScale.Crop,
            colorFilter = saturationFilter,
        )

        Box(
            Modifier
                .fillMaxSize()
                .ellipticalRadialGradient(
                    centerFraction = Offset(0.50f, 0.52f),
                    radiusFraction = Size(0.70f, 0.46f),
                    colors = listOf(
                        SplashCenterScrim.copy(alpha = 0.42f),
                        SplashCenterScrim.copy(alpha = 0.20f),
                        Color.Transparent,
                        Color.Transparent,
                    ),
                    stops = floatArrayOf(0f, 0.55f, 0.78f, 1f),
                ),
        )
        Box(
            Modifier
                .fillMaxSize()
                .ellipticalRadialGradient(
                    centerFraction = Offset(0.50f, -0.12f),
                    radiusFraction = Size(1.40f, 0.62f),
                    colors = listOf(
                        SplashSurface.copy(alpha = 0.18f),
                        SplashBackground.copy(alpha = 0.26f),
                        SplashDeep.copy(alpha = 0.35f),
                        SplashDeep.copy(alpha = 0.35f),
                    ),
                    stops = floatArrayOf(0f, 0.52f, 0.78f, 1f),
                ),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            SplashDeep.copy(alpha = 0.05f),
                            SplashDeep.copy(alpha = 0.30f),
                        ),
                    ),
                ),
        )
        Box(
            Modifier
                .fillMaxSize()
                .ellipticalRadialGradient(
                    centerFraction = Offset(0.50f, 0.46f),
                    radiusFraction = Size(1.20f, 0.90f),
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.25f),
                    ),
                    stops = floatArrayOf(0f, 0.58f, 1f),
                ),
        )

        BrandLockup(animationsEnabled = animationsEnabled)
    }
}

@Composable
private fun BrandLockup(animationsEnabled: Boolean) {
    val wordmarkProgress = rememberEntranceProgress(0L, animationsEnabled)
    val chineseNameProgress = rememberEntranceProgress(140L, animationsEnabled)
    val sloganProgress = rememberEntranceProgress(300L, animationsEnabled)
    val glowRadius = with(LocalDensity.current) { 18.dp.toPx() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Juke",
            modifier = Modifier.entranceAnimation(wordmarkProgress),
            style = TextStyle(
                color = Color.White,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Normal,
                fontSize = 46.sp,
                letterSpacing = 3.sp,
                lineHeight = 50.6.sp,
                textAlign = TextAlign.Center,
                shadow = Shadow(
                    color = Color.White.copy(alpha = 0.28f),
                    offset = Offset.Zero,
                    blurRadius = glowRadius,
                ),
            ),
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "剧刻",
            modifier = Modifier.entranceAnimation(chineseNameProgress),
            style = TextStyle(
                color = SplashText,
                fontFamily = ChineseBrandFont,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                letterSpacing = 12.sp,
                textAlign = TextAlign.Center,
                textIndent = TextIndent(firstLine = 12.sp),
            ),
        )
        Spacer(Modifier.height(28.dp))
        Text(
            text = "好剧 从此刻开始",
            modifier = Modifier.entranceAnimation(sloganProgress),
            style = TextStyle(
                color = SplashText.copy(alpha = 0.62f),
                fontFamily = SloganFont,
                fontWeight = FontWeight.Thin,
                fontSize = 13.sp,
                letterSpacing = 8.sp,
                textAlign = TextAlign.Center,
                textIndent = TextIndent(firstLine = 8.sp),
            ),
        )
    }
}

@Composable
private fun rememberEntranceProgress(delayMillis: Long, enabled: Boolean): Float {
    val progress = remember { Animatable(if (enabled) 0f else 1f) }
    LaunchedEffect(enabled) {
        if (enabled) {
            delay(delayMillis)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 620, easing = SplashEntranceEasing),
            )
        } else {
            progress.snapTo(1f)
        }
    }
    return progress.value
}

private fun Modifier.entranceAnimation(progress: Float): Modifier = graphicsLayer {
    alpha = progress
    translationY = (1f - progress) * 14.dp.toPx()
}

private fun Modifier.ellipticalRadialGradient(
    centerFraction: Offset,
    radiusFraction: Size,
    colors: List<Color>,
    stops: FloatArray,
): Modifier = drawBehind {
    val centerX = centerFraction.x * size.width
    val centerY = centerFraction.y * size.height
    val radiusX = radiusFraction.width * size.width
    val radiusY = radiusFraction.height * size.height
    val paint = NativePaint().apply {
        isAntiAlias = true
        shader = RadialGradient(
            0f,
            0f,
            1f,
            colors.map(Color::toArgb).toIntArray(),
            stops,
            Shader.TileMode.CLAMP,
        )
    }

    // 将单位径向渐变缩放为与 CSS 规格一致的椭圆。
    drawIntoCanvas { canvas ->
        canvas.save()
        canvas.translate(centerX, centerY)
        canvas.scale(radiusX, radiusY)
        canvas.nativeCanvas.drawRect(
            -centerX / radiusX,
            -centerY / radiusY,
            (size.width - centerX) / radiusX,
            (size.height - centerY) / radiusY,
            paint,
        )
        canvas.restore()
    }
}
