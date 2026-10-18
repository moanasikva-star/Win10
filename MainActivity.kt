package com.win10.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

private val Red = Color(0xFFE53935)
private val Ink = Color(0xFF17191D)
private val Muted = Color(0xFF737780)
private val Pale = Color(0xFFF5F6F8)
private val Green = Color(0xFF218653)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.WHITE
        window.navigationBarColor = android.graphics.Color.WHITE
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
            android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        Win10ContextHolder.context = applicationContext
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Red,
                onPrimary = Color.White,
                background = Color.White,
                surface = Color.White,
                onSurface = Ink
            )) {
                Win10App()
            }
        }
    }
}

@Composable
private fun Win10App() {
    var showIntro by remember { mutableStateOf(true) }
    var enabled by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Tudo pronto para começar") }
    var requestPending by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        enabled = allGranted
        status = if (allGranted) {
            "Ativação ligada neste aplicativo"
        } else {
            "Permissões não concedidas. A ativação ficou desligada."
        }
        requestPending = false
    }

    LaunchedEffect(Unit) {
        delay(2300)
        showIntro = false
    }

    Box(Modifier.fillMaxSize().background(Color.White)) {
        AnimatedVisibility(
            visible = showIntro,
            enter = fadeIn(tween(350)),
            exit = fadeOut(tween(900))
        ) {
            IntroScreen()
        }

        AnimatedVisibility(
            visible = !showIntro,
            enter = fadeIn(tween(900)),
            exit = fadeOut(tween(200))
        ) {
            MainMenu(
                enabled = enabled,
                status = status,
                onToggle = { turnOn ->
                    if (!turnOn) {
                        enabled = false
                        status = "Ativação desligada"
                    } else {
                        val permissions = requiredPermissions()
                        val missing = permissions.filter {
                            ContextCompat.checkSelfPermission(
                                Win10ContextHolder.context,
                                it
                            ) != PackageManager.PERMISSION_GRANTED
                        }
                        if (missing.isEmpty()) {
                            enabled = true
                            status = "Ativação ligada neste aplicativo"
                        } else {
                            requestPending = true
                            permissionLauncher.launch(missing.toTypedArray())
                        }
                    }
                }
            )
        }
    }
}

// Activity context is set only for this small app flow; no background service is created.
private object Win10ContextHolder {
    lateinit var context: android.content.Context
}

private fun requiredPermissions(): List<String> {
    val result = mutableListOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    if (Build.VERSION.SDK_INT >= 33) {
        result += Manifest.permission.READ_MEDIA_IMAGES
        result += Manifest.permission.READ_MEDIA_VIDEO
        result += Manifest.permission.READ_MEDIA_AUDIO
    } else {
        result += Manifest.permission.READ_EXTERNAL_STORAGE
    }
    return result
}

@Composable
private fun IntroScreen() {
    val transition = rememberInfiniteTransition(label = "intro")
    val pulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFAFAFB)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StickFigure(Modifier.size(150.dp).alpha(pulse), animated = true)
        Spacer(Modifier.height(18.dp))
        Text("WIN10", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Ink, letterSpacing = 5.sp)
        Spacer(Modifier.height(8.dp))
        Text("Iniciando experiência", fontSize = 13.sp, color = Muted)
    }
}

@Composable
private fun StickFigure(modifier: Modifier = Modifier, animated: Boolean = false) {
    Canvas(modifier) {
        val red = Red
        val cx = size.width / 2f
        val headR = size.minDimension * 0.115f
        val headY = size.height * 0.22f
        val stroke = size.minDimension * 0.055f
        drawCircle(red, radius = headR, center = Offset(cx, headY))
        drawLine(red, Offset(cx, headY + headR), Offset(cx, size.height * 0.61f), stroke, cap = StrokeCap.Round)
        drawLine(red, Offset(cx, size.height * 0.39f), Offset(size.width * 0.24f, size.height * 0.51f), stroke, cap = StrokeCap.Round)
        drawLine(red, Offset(cx, size.height * 0.39f), Offset(size.width * 0.76f, size.height * 0.51f), stroke, cap = StrokeCap.Round)
        drawLine(red, Offset(cx, size.height * 0.61f), Offset(size.width * 0.31f, size.height * 0.86f), stroke, cap = StrokeCap.Round)
        drawLine(red, Offset(cx, size.height * 0.61f), Offset(size.width * 0.69f, size.height * 0.86f), stroke, cap = StrokeCap.Round)
    }
}

@Composable
private fun MainMenu(
    enabled: Boolean,
    status: String,
    onToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Red),
                contentAlignment = Alignment.Center
            ) {
                StickFigure(Modifier.size(32.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Win10", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text("Painel de controle", fontSize = 12.sp, color = Muted)
            }
            Spacer(Modifier.weight(1f))
            Box(Modifier.size(9.dp).clip(CircleShape).background(if (enabled) Green else Color(0xFFB8BBC2)))
        }

        Spacer(Modifier.height(34.dp))
        Text("Olá.", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Ink)
        Spacer(Modifier.height(5.dp))
        Text("Gerencie a ativação local do aplicativo.", fontSize = 14.sp, color = Muted)

        Spacer(Modifier.height(28.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Pale
        ) {
            Column(Modifier.padding(22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("ATIVAÇÃO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Muted, letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(if (enabled) "ON" else "OFF", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = if (enabled) Green else Red)
                    }
                    Switch(
                        checked = enabled,
                        onCheckedChange = onToggle
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    if (enabled) "O recurso local está ligado." else "O recurso local está desligado.",
                    fontSize = 14.sp, color = Ink
                )
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = { onToggle(!enabled) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (enabled) Ink else Red)
                ) {
                    Text(if (enabled) "DESATIVAR" else "ATIVAR", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E9ED))
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.size(34.dp).clip(CircleShape).background(Color(0xFFFFEEEE)), contentAlignment = Alignment.Center) {
                    Text("i", color = Red, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Status", fontWeight = FontWeight.SemiBold, color = Ink, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(status, color = Muted, fontSize = 13.sp, lineHeight = 18.sp)
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Text(
            "Win10",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Muted
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "As permissões são solicitadas ao ativar. Nesta versão, o botão controla apenas o estado local do app.",
            fontSize = 11.sp,
            color = Color(0xFF9A9DA5),
            lineHeight = 15.sp
        )
    }
}
