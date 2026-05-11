package com.pinyinmate.app

import android.icu.text.Transliterator
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import java.text.Normalizer
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    companion object {
        const val EXTRA_QUICK_CONVERT = "com.pinyinmate.app.extra.QUICK_CONVERT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = AndroidColor.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            var themeMode by remember { mutableStateOf(loadThemeMode(context)) }
            val isSystemDark = isSystemInDarkTheme()
            val dark = when (themeMode) {
                ThemeMode.System -> isSystemDark
                ThemeMode.Light -> false
                ThemeMode.Dark -> true
            }

            val view = androidx.compose.ui.platform.LocalView.current
            if (!view.isInEditMode) {
                androidx.compose.runtime.SideEffect {
                    WindowCompat.getInsetsController(window, view).apply {
                        isAppearanceLightStatusBars = !dark
                        isAppearanceLightNavigationBars = !dark
                    }
                }
            }

            PinyinMateTheme(dark = dark) {
                val initialInput = remember { quickConvertInput() }
                var showLaunch by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(1180)
                    showLaunch = false
                }

                AnimatedContent(
                    targetState = showLaunch,
                    transitionSpec = {
                        (fadeIn(tween(320, easing = FastOutSlowInEasing)) + scaleIn(initialScale = 0.985f))
                            .togetherWith(fadeOut(tween(260)) + scaleOut(targetScale = 1.015f))
                            .using(SizeTransform(clip = false))
                    },
                    label = "launch"
                ) { launching ->
                    if (launching) {
                        LaunchSplash(dark = dark)
                    } else {
                        PinyinMateApp(
                            initialInput = initialInput,
                            themeMode = themeMode,
                            onThemeModeChange = {
                                themeMode = it
                                saveThemeMode(context, it)
                            },
                            dark = dark
                        )
                    }
                }
            }
        }
    }
}

private enum class ToneStyle(val label: String) {
    Marks("声调"),
    Numbers("数字"),
    None("无调")
}

private enum class CaseStyle(val label: String) {
    Lower("小写"),
    Title("首字母"),
    Upper("大写")
}

private enum class CopyTemplate(val label: String) {
    Pinyin("拼音"),
    ChineseWithPinyin("中文(拼音)"),
    Lines("逐行"),
    Table("表格"),
    Initials("首字母")
}

private enum class ThemeMode(val label: String) {
    System("跟随系统"),
    Light("浅色"),
    Dark("深色")
}

private enum class AppPage(val label: String, val iconRes: Int) {
    Convert("转换", R.drawable.ic_nav_convert),
    History("记录", R.drawable.ic_nav_history),
    Settings("设置", R.drawable.ic_nav_settings),
    About("关于", R.drawable.ic_nav_about)
}

@Composable
private fun LaunchSplash(dark: Boolean) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        appeared = true
    }

    val logoScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.86f,
        animationSpec = tween(durationMillis = 620, easing = FastOutSlowInEasing),
        label = "launchLogoScale"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
        label = "launchContentAlpha"
    )
    val lineScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.18f,
        animationSpec = tween(durationMillis = 760, delayMillis = 120, easing = FastOutSlowInEasing),
        label = "launchLineScale"
    )
    val background = if (dark) {
        Brush.verticalGradient(listOf(Color(0xFF050507), Color(0xFF10141B), Color(0xFF07080A)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF9FBFF), Color(0xFFEFF5FF), Color(0xFFF7F8FB)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        AmbientGlass(dark = dark)
        Column(
            modifier = Modifier.graphicsLayer { alpha = contentAlpha },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                    }
                    .shadow(
                        elevation = if (dark) 12.dp else 26.dp,
                        shape = RoundedCornerShape(34.dp),
                        ambientColor = Color.Black.copy(alpha = if (dark) 0.20f else 0.08f),
                        spotColor = Color.Black.copy(alpha = if (dark) 0.24f else 0.10f)
                    )
                    .clip(RoundedCornerShape(34.dp))
                    .background(glassFill(dark))
                    .border(
                        1.dp,
                        if (dark) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.78f),
                        RoundedCornerShape(34.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "拼",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(22.dp))
            Text(
                text = "PinyinMate",
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 34.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "中文转拼音",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(lineScale)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.72f))
                )
            }
        }
    }
}

@Composable
private fun PinyinMateTheme(dark: Boolean, content: @Composable () -> Unit) {
    val colors = if (dark) {
        darkColorScheme(
            primary = Color(0xFF0A84FF),
            secondary = Color(0xFF64D2FF),
            tertiary = Color(0xFF30D158),
            background = Color(0xFF07080A),
            surface = Color(0xFF15171C),
            onSurface = Color(0xFFF5F5F7)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF007AFF),
            secondary = Color(0xFF5AC8FA),
            tertiary = Color(0xFF34C759),
            background = Color(0xFFF5F7FB),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1D1D1F)
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography.copy(
            displaySmall = MaterialTheme.typography.displaySmall.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp
            ),
            titleLarge = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp
            ),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.sp
            )
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun PinyinMateApp(
    initialInput: String,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    dark: Boolean
) {
    var input by remember { mutableStateOf(initialInput) }
    var page by remember { mutableStateOf(AppPage.Convert) }
    var toneStyle by remember { mutableStateOf(ToneStyle.Marks) }
    var caseStyle by remember { mutableStateOf(CaseStyle.Lower) }
    var compactSpacing by remember { mutableStateOf(false) }
    var copyTemplate by remember { mutableStateOf(CopyTemplate.Pinyin) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val history = remember { mutableStateListOf<String>().apply { addAll(loadList(context, "history")) } }
    val favorites = remember { mutableStateListOf<String>().apply { addAll(loadList(context, "favorites")) } }
    val clipboard = LocalClipboardManager.current

    val result = remember(input, toneStyle, caseStyle, compactSpacing, copyTemplate) {
        PinyinConverter.format(
            text = input,
            toneStyle = toneStyle,
            caseStyle = caseStyle,
            template = copyTemplate,
            separator = if (compactSpacing) "" else " "
        )
    }

    val background = if (dark) {
        Brush.verticalGradient(listOf(Color(0xFF050507), Color(0xFF10141B), Color(0xFF07080A)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF9FBFF), Color(0xFFEFF5FF), Color(0xFFF7F8FB)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        AmbientGlass(dark = dark)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                GlassBottomBar(
                    current = page,
                    onPageSelected = { page = it },
                    dark = dark
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(
                        PaddingValues(
                            start = 20.dp,
                            top = 14.dp,
                            end = 20.dp,
                            bottom = padding.calculateBottomPadding() + 10.dp
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Header(
                    title = when (page) {
                        AppPage.Convert -> "PinyinMate"
                        AppPage.History -> "转换记录"
                        AppPage.Settings -> "偏好设置"
                        AppPage.About -> "关于作者"
                    },
                    subtitle = when (page) {
                        AppPage.Convert -> "中文转拼音"
                        AppPage.History -> "最近复制的结果"
                        AppPage.Settings -> "输出格式"
                        AppPage.About -> "PinyinMate 与创作者"
                    },
                    onClear = {
                        if (page == AppPage.History) {
                            showClearHistoryDialog = true
                        } else {
                            input = ""
                        }
                    },
                    onSample = { input = "春风又绿江南岸，明月何时照我还" },
                    showSample = page == AppPage.Convert,
                    showClear = page != AppPage.About
                )

                if (showClearHistoryDialog) {
                    ConfirmDialog(
                        title = "清空所有记录？",
                        body = "历史记录和收藏都会被清空，此操作不能撤销。",
                        confirmText = "清空",
                        onConfirm = {
                            history.clear()
                            favorites.clear()
                            saveList(context, "history", history)
                            saveList(context, "favorites", favorites)
                            showClearHistoryDialog = false
                        },
                        onDismiss = { showClearHistoryDialog = false }
                    )
                }

                AnimatedContent(
                    targetState = page,
                    transitionSpec = {
                        (fadeIn(spring(stiffness = Spring.StiffnessLow)) + scaleIn(initialScale = 0.98f))
                            .togetherWith(fadeOut() + scaleOut(targetScale = 0.98f))
                            .using(SizeTransform(clip = false))
                    },
                    label = "page"
                ) { selectedPage ->
                    when (selectedPage) {
                        AppPage.Convert -> ConvertPage(
                            input = input,
                            onInputChange = { input = it },
                            result = result,
                            onCopy = {
                                copyResult(result, clipboard, history)
                                saveList(context, "history", history)
                            },
                            onShare = { shareText(context, result) },
                            isFavorite = favorites.contains(result) && result.isNotBlank(),
                            onToggleFavorite = {
                                toggleFavorite(result, favorites)
                                saveList(context, "favorites", favorites)
                            },
                            dark = dark
                        )
                        AppPage.History -> HistoryPage(
                            items = history,
                            favorites = favorites,
                            onCopy = { item ->
                                clipboard.setText(AnnotatedString(item))
                            },
                            onShare = { item -> shareText(context, item) },
                            onToggleFavorite = { item ->
                                toggleFavorite(item, favorites)
                                saveList(context, "favorites", favorites)
                            },
                            onDelete = { item ->
                                history.remove(item)
                                favorites.remove(item)
                                saveList(context, "history", history)
                                saveList(context, "favorites", favorites)
                            },
                            dark = dark
                        )
                        AppPage.Settings -> SettingsPage(
                            themeMode = themeMode,
                            onThemeModeChange = onThemeModeChange,
                            toneStyle = toneStyle,
                            onToneStyleChange = { toneStyle = it },
                            caseStyle = caseStyle,
                            onCaseStyleChange = { caseStyle = it },
                            compactSpacing = compactSpacing,
                            onCompactSpacingChange = { compactSpacing = it },
                            copyTemplate = copyTemplate,
                            onCopyTemplateChange = { copyTemplate = it },
                            input = input,
                            onClearInput = { input = "" },
                            dark = dark
                        )
                        AppPage.About -> AboutPage(dark = dark)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConvertPage(
    input: String,
    onInputChange: (String) -> Unit,
    result: String,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    dark: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassPanel(dark = dark, modifier = Modifier.weight(0.92f)) {
            Text(
                text = "中文",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 27.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = TextStyle(fontSize = 20.sp, lineHeight = 29.sp),
                placeholder = { Text("输入中文文本") },
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = glassFill(dark),
                    unfocusedContainerColor = glassFill(dark),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                )
            )
        }

        GlassPanel(dark = dark, modifier = Modifier.weight(1.08f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "拼音",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 27.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RoundActionButton(
                        onClick = onToggleFavorite,
                        enabled = result.isNotBlank(),
                        color = MaterialTheme.colorScheme.tertiary,
                        contentDescription = "收藏结果"
                    ) {
                        Icon(if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder, null)
                    }
                    RoundActionButton(
                        onClick = onShare,
                        enabled = result.isNotBlank(),
                        color = MaterialTheme.colorScheme.secondary,
                        contentDescription = "转发结果"
                    ) {
                        Icon(Icons.Rounded.Share, null)
                    }
                    RoundActionButton(
                        onClick = onCopy,
                        enabled = result.isNotBlank(),
                        color = MaterialTheme.colorScheme.primary,
                        contentDescription = "复制结果"
                    ) {
                        Icon(Icons.Rounded.ContentCopy, null)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            AnimatedContent(
                targetState = result,
                transitionSpec = {
                    (fadeIn(spring(stiffness = Spring.StiffnessLow)) + scaleIn(initialScale = 0.98f))
                        .togetherWith(fadeOut() + scaleOut(targetScale = 0.98f))
                        .using(SizeTransform(clip = false))
                },
                label = "result"
            ) { value ->
                SelectionContainer {
                    Text(
                        text = value.ifBlank { "转换结果会出现在这里" },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(28.dp))
                            .background(glassFill(dark))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f),
                                RoundedCornerShape(28.dp)
                            )
                            .padding(20.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 20.sp,
                            lineHeight = 30.sp,
                            color = if (value.isBlank()) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryPage(
    items: List<String>,
    favorites: List<String>,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDelete: (String) -> Unit,
    dark: Boolean
) {
    GlassPanel(dark = dark, modifier = Modifier.fillMaxSize()) {
        if (items.isEmpty() && favorites.isEmpty()) {
            EmptyState(
                title = "还没有内容",
                body = "复制或收藏结果后，这里会显示最近内容。"
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                favorites.forEach { item ->
                    androidx.compose.runtime.key(item) {
                        SwipeToDeleteWrapper(item = item, onDelete = onDelete) {
                            HistoryRow(
                                item = item,
                                pinned = true,
                                dark = dark,
                                onCopy = onCopy,
                                onShare = onShare,
                                onToggleFavorite = onToggleFavorite
                            )
                        }
                    }
                }
                items.filterNot { favorites.contains(it) }.forEach { item ->
                    androidx.compose.runtime.key(item) {
                        SwipeToDeleteWrapper(item = item, onDelete = onDelete) {
                            HistoryRow(
                                item = item,
                                pinned = false,
                                dark = dark,
                                onCopy = onCopy,
                                onShare = onShare,
                                onToggleFavorite = onToggleFavorite
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteWrapper(
    item: String,
    onDelete: (String) -> Unit,
    content: @Composable () -> Unit
) {
    var pendingDelete by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.StartToEnd) {
                pendingDelete = true
                false
            } else {
                false
            }
        },
        positionalThreshold = { distance -> distance * 0.82f }
    )

    if (pendingDelete) {
        ConfirmDialog(
            title = "删除这条记录？",
            body = item,
            confirmText = "删除",
            onConfirm = {
                onDelete(item)
                pendingDelete = false
            },
            onDismiss = { pendingDelete = false }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            val color = MaterialTheme.colorScheme.errorContainer
            val alignment = Alignment.CenterStart
            val icon = Icons.Rounded.Delete
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(icon, contentDescription = "删除", tint = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    ) {
        content()
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    body: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = body,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun HistoryRow(
    item: String,
    pinned: Boolean,
    dark: Boolean,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (dark) Color(0xFF1C1E24) else Color.White)
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
        )
        IconButton(onClick = { onToggleFavorite(item) }) {
            Icon(
                if (pinned) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                contentDescription = "收藏记录"
            )
        }
        IconButton(onClick = { onShare(item) }) {
            Icon(Icons.Rounded.Share, contentDescription = "转发记录")
        }
        IconButton(onClick = { onCopy(item) }) {
            Icon(Icons.Rounded.ContentCopy, contentDescription = "复制记录")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SettingsPage(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    toneStyle: ToneStyle,
    onToneStyleChange: (ToneStyle) -> Unit,
    caseStyle: CaseStyle,
    onCaseStyleChange: (CaseStyle) -> Unit,
    compactSpacing: Boolean,
    onCompactSpacingChange: (Boolean) -> Unit,
    copyTemplate: CopyTemplate,
    onCopyTemplateChange: (CopyTemplate) -> Unit,
    input: String,
    onClearInput: () -> Unit,
    dark: Boolean
) {
    GlassPanel(dark = dark, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "外观",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                ThemeMode.entries.forEachIndexed { index, item ->
                    SegmentedButton(
                        selected = themeMode == item,
                        onClick = { onThemeModeChange(item) },
                        shape = SegmentedButtonDefaults.itemShape(index, ThemeMode.entries.size),
                        label = { Text(item.label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "格式",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                ToneStyle.entries.forEachIndexed { index, item ->
                    SegmentedButton(
                        selected = toneStyle == item,
                        onClick = { onToneStyleChange(item) },
                        shape = SegmentedButtonDefaults.itemShape(index, ToneStyle.entries.size),
                        label = { Text(item.label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CopyTemplate.entries.forEach { item ->
                    FilterChip(
                        selected = copyTemplate == item,
                        onClick = { onCopyTemplateChange(item) },
                        label = { Text(item.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                CaseStyle.entries.forEachIndexed { index, item ->
                    SegmentedButton(
                        selected = caseStyle == item,
                        onClick = { onCaseStyleChange(item) },
                        shape = SegmentedButtonDefaults.itemShape(index, CaseStyle.entries.size),
                        label = { Text(item.label, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = compactSpacing,
                    onClick = { onCompactSpacingChange(!compactSpacing) },
                    label = { Text("紧凑间距") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                    )
                )
                FilterChip(
                    selected = input.isNotBlank(),
                    onClick = onClearInput,
                    label = { Text("清空输入") },
                    leadingIcon = {
                        Icon(Icons.Rounded.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )
            }
        }
    }
}

@Composable
private fun Header(
    title: String,
    subtitle: String,
    onClear: () -> Unit,
    onSample: () -> Unit,
    showSample: Boolean,
    showClear: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 34.sp, lineHeight = 40.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (showSample) {
                RoundActionButton(
                    onClick = onSample,
                    enabled = true,
                    color = MaterialTheme.colorScheme.primary,
                    contentDescription = "示例文本"
                ) {
                    Icon(Icons.Rounded.Refresh, null)
                }
            }
            if (showClear) {
                RoundActionButton(
                    onClick = onClear,
                    enabled = true,
                    color = MaterialTheme.colorScheme.onSurface,
                    contentDescription = "清空"
                ) {
                    Icon(Icons.Rounded.DeleteSweep, null)
                }
            }
        }
    }
}

@Composable
private fun RoundActionButton(
    onClick: () -> Unit,
    enabled: Boolean,
    color: Color,
    contentDescription: String,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = if (enabled) 0.13f else 0.06f))
    ) {
        Box(
            modifier = Modifier.size(25.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun GlassBottomBar(
    current: AppPage,
    onPageSelected: (AppPage) -> Unit,
    dark: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .navigationBarsPadding()
            .padding(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(86.dp)
                .shadow(
                    elevation = if (dark) 8.dp else 20.dp,
                    shape = RoundedCornerShape(36.dp),
                    ambientColor = Color.Black.copy(alpha = if (dark) 0.18f else 0.08f),
                    spotColor = Color.Black.copy(alpha = if (dark) 0.24f else 0.10f)
                )
                .clip(RoundedCornerShape(36.dp))
                .border(
                    1.dp,
                    if (dark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.72f),
                    RoundedCornerShape(36.dp)
                )
                .background(glassFill(dark))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppPage.entries.forEach { page ->
                PillNavItem(
                    page = page,
                    selected = current == page,
                    onClick = { onPageSelected(page) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PillNavItem(
    page: AppPage,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.13f)
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
    }
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(28.dp))
            .background(if (selected) selectedColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(page.iconRes),
            contentDescription = page.label,
            modifier = Modifier.size(26.dp),
            tint = contentColor
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = page.label,
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp
            ),
            color = contentColor
        )
    }
}

@Composable
private fun AboutPage(dark: Boolean) {
    GlassPanel(dark = dark, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "拼",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 32.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PinyinMate",
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 30.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "中文转拼音工具",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.56f)
                )
            }
        }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "一个专注中文转拼音的小工具，界面追求安静、轻量、顺手。",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp, lineHeight = 25.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
            )

            Spacer(Modifier.height(16.dp))
            CompactInfoRow("作者", "XIAO", dark)
            Spacer(Modifier.height(8.dp))
            CompactInfoRow("版本", "1.0.10", dark)
            Spacer(Modifier.height(8.dp))
            CompactInfoRow("隐私", "离线转换，不上传文本内容", dark)

            Spacer(Modifier.height(24.dp))
            Text(
                text = "更新日志",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))
            
            FeatureRow("1.0.10", "• 新增 PinyinMate 品牌启动动画\n• 启动时加入标识缩放、文字淡入与进度胶囊过渡\n• 优化打开应用时的第一眼体验", dark)
            Spacer(Modifier.height(8.dp))
            FeatureRow("1.0.9", "• 桌面小组件改为快捷转换入口\n• 点击小组件会读取剪贴板文本并直接进入转换页\n• 优化小组件文案，使入口用途更明确", dark)
            Spacer(Modifier.height(8.dp))
            FeatureRow("1.0.8", "• 修复历史记录只显示少量内容的问题\n• 删除记录改为高力度滑动后再确认，避免误删\n• 修复记录页右上角清空按钮无效的问题\n• 扩大历史记录与收藏保存数量", dark)
            Spacer(Modifier.height(8.dp))
            FeatureRow("1.0.7", "• 精修设置页与记录页的小屏滚动适配\n• 保持底部悬浮胶囊栏与手势提示线区域协调\n• 清理无用导航依赖，减少界面维护噪音", dark)
            Spacer(Modifier.height(8.dp))
            FeatureRow("1.0.6", "• 修复点击输入框界面被顶起的问题\n• 新增深色/浅色模式切换\n• 记录页面支持右滑删除\n• 更新了全新的应用 Logo\n• 记录列表项背景调整为不透明，更清晰", dark)
            Spacer(Modifier.height(8.dp))
            FeatureRow("1.0.4", "• 优化了类苹果风格的玻璃拟物化设计\n• 完善转换核心算法与复制功能", dark)
        }
    }
}

@Composable
private fun CompactInfoRow(title: String, body: String, dark: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(glassFill(dark))
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(0.42f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = body,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.56f)
        )
    }
}

@Composable
private fun FeatureRow(title: String, body: String, dark: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(glassFill(dark))
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 16.dp, vertical = 13.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.56f)
        )
    }
}

@Composable
private fun EmptyState(title: String, body: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.56f)
        )
    }
}

@Composable
private fun GlassPanel(
    dark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(34.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (dark) 8.dp else 22.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = if (dark) 0.18f else 0.08f),
                spotColor = Color.Black.copy(alpha = if (dark) 0.24f else 0.10f)
            )
            .clip(shape)
            .background(glassFill(dark))
            .border(
                1.dp,
                if (dark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.74f),
                shape
            )
            .padding(20.dp),
        content = content
    )
}

@Composable
private fun AmbientGlass(dark: Boolean) {
    val density = LocalDensity.current
    val blur = with(density) { 36.dp }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .blur(blur)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 18.dp)
                .size(190.dp)
                .clip(CircleShape)
                .background((if (dark) Color(0xFF0A84FF) else Color(0xFF74B9FF)).copy(alpha = 0.22f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = 80.dp)
                .size(210.dp)
                .clip(CircleShape)
                .background((if (dark) Color(0xFF30D158) else Color(0xFF80D7A8)).copy(alpha = 0.18f))
        )
    }
}

@Composable
private fun glassFill(dark: Boolean): Color {
    return if (dark) {
        Color(0xFF1C1E24).copy(alpha = 0.76f)
    } else {
        Color.White.copy(alpha = 0.78f)
    }
}

private fun copyResult(
    result: String,
    clipboard: androidx.compose.ui.platform.ClipboardManager,
    history: MutableList<String>
) {
    if (result.isBlank()) return
    clipboard.setText(AnnotatedString(result))
    history.remove(result)
    history.add(0, result)
    while (history.size > 50) {
        history.removeAt(history.lastIndex)
    }
}

private fun toggleFavorite(result: String, favorites: MutableList<String>) {
    if (result.isBlank()) return
    if (favorites.contains(result)) {
        favorites.remove(result)
    } else {
        favorites.add(0, result)
        while (favorites.size > 30) {
            favorites.removeAt(favorites.lastIndex)
        }
    }
}

private fun shareText(context: Context, result: String) {
    if (result.isBlank()) return
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, result)
    }
    context.startActivity(Intent.createChooser(intent, "转发拼音结果"))
}

private fun loadList(context: Context, key: String): List<String> {
    val raw = context.getSharedPreferences("pinyinmate", Context.MODE_PRIVATE)
        .getString(key, "")
        .orEmpty()
    if (raw.isBlank()) return emptyList()
    return raw.split('\u001E').filter { it.isNotBlank() }
}

private fun saveList(context: Context, key: String, values: List<String>) {
    context.getSharedPreferences("pinyinmate", Context.MODE_PRIVATE)
        .edit()
        .putString(key, values.joinToString("\u001E"))
        .apply()
}

private fun loadThemeMode(context: Context): ThemeMode {
    val name = context.getSharedPreferences("pinyinmate", Context.MODE_PRIVATE)
        .getString("theme_mode", ThemeMode.System.name) ?: ThemeMode.System.name
    return try { ThemeMode.valueOf(name) } catch (e: Exception) { ThemeMode.System }
}

private fun saveThemeMode(context: Context, mode: ThemeMode) {
    context.getSharedPreferences("pinyinmate", Context.MODE_PRIVATE)
        .edit()
        .putString("theme_mode", mode.name)
        .apply()
}

private fun MainActivity.quickConvertInput(): String {
    if (intent?.getBooleanExtra(MainActivity.EXTRA_QUICK_CONVERT, false) != true) {
        return "你好，欢迎使用 PinyinMate"
    }

    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val hasText = clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true ||
        clipboard.primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML) == true
    val text = if (hasText && clipboard.hasPrimaryClip()) {
        clipboard.primaryClip?.getItemAt(0)?.coerceToText(this)?.toString().orEmpty()
    } else {
        ""
    }

    return text.ifBlank { "请先复制中文文本，再点击桌面小组件快捷转换" }
}

private object PinyinConverter {
    private val hanToLatin by lazy {
        Transliterator.getInstance("Han-Latin")
    }

    fun format(
        text: String,
        toneStyle: ToneStyle,
        caseStyle: CaseStyle,
        template: CopyTemplate,
        separator: String
    ): String {
        if (text.isBlank()) return ""
        val lines = text.lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toList()
        if (lines.isEmpty()) return ""

        return when (template) {
            CopyTemplate.Pinyin -> lines.joinToString("\n") {
                convertLine(it, toneStyle, caseStyle, separator)
            }
            CopyTemplate.ChineseWithPinyin -> lines.joinToString("\n") {
                "$it(${convertLine(it, toneStyle, caseStyle, separator)})"
            }
            CopyTemplate.Lines -> lines.mapIndexed { index, line ->
                "${index + 1}. $line\n${convertLine(line, toneStyle, caseStyle, separator)}"
            }.joinToString("\n\n")
            CopyTemplate.Table -> buildString {
                appendLine("中文 | 拼音 | 首字母")
                appendLine("--- | --- | ---")
                lines.forEach { line ->
                    appendLine("${line.escapeTable()} | ${convertLine(line, toneStyle, caseStyle, separator).escapeTable()} | ${initials(line)}")
                }
            }.trim()
            CopyTemplate.Initials -> lines.joinToString("\n") { initials(it) }
        }
    }

    private fun convertLine(
        text: String,
        toneStyle: ToneStyle,
        caseStyle: CaseStyle,
        separator: String
    ): String {
        if (text.isBlank()) return ""
        val raw = hanToLatin.transliterate(text)
            .replace(Regex("\\s+"), " ")
            .trim()
        val normalized = when (toneStyle) {
            ToneStyle.Marks -> raw
            ToneStyle.Numbers -> raw.splitToSequence(" ").joinToString(separator) { it.toToneNumber() }
            ToneStyle.None -> removeToneMarks(raw)
        }
        val spaced = normalized.splitToSequence(" ")
            .filter { it.isNotBlank() }
            .joinToString(separator)
        return when (caseStyle) {
            CaseStyle.Lower -> spaced.lowercase(Locale.CHINA)
            CaseStyle.Upper -> spaced.uppercase(Locale.CHINA)
            CaseStyle.Title -> spaced.splitToSequence(separator.ifEmpty { " " })
                .joinToString(separator) { syllable ->
                    syllable.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase(Locale.CHINA) else char.toString()
                    }
                }
        }
    }

    private fun initials(text: String): String {
        return convertLine(text, ToneStyle.None, CaseStyle.Upper, " ")
            .splitToSequence(" ")
            .filter { it.isNotBlank() }
            .mapNotNull { it.firstOrNull()?.toString() }
            .joinToString("")
    }

    private fun String.escapeTable(): String {
        return replace("|", "\\|").replace("\n", " ")
    }

    private fun removeToneMarks(value: String): String {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
            .filterNot { Character.getType(it) == Character.NON_SPACING_MARK.toInt() }
    }

    private fun String.toToneNumber(): String {
        val decomposed = Normalizer.normalize(this, Normalizer.Form.NFD)
        var tone = '5'
        val builder = StringBuilder()
        decomposed.forEach { char ->
            when (char) {
                '\u0304' -> tone = '1'
                '\u0301' -> tone = '2'
                '\u030C' -> tone = '3'
                '\u0300' -> tone = '4'
                else -> if (Character.getType(char) != Character.NON_SPACING_MARK.toInt()) {
                    builder.append(char)
                }
            }
        }
        return if (tone == '5') builder.toString() else builder.append(tone).toString()
    }
}
