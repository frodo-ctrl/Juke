# Juke Logo 切换 · 实现交接文档（启动器 + 应用内全量替换）

> 设计侧产出，供代码执行侧使用。本文档是本次切换的**唯一执行依据**，与
> `qwenwork/canvas/mvnr5i586kvki7p8/juke-j-icon-handoff.md`（定稿几何真源）一致，
> 并在其基础上补充了**应用内图标替换的确认与现状审计**。
> 用户已确认：旧的场记板/胶片样式图标**全部替换**，包括应用内关于页。

---

## 0. 一句话需求

把所有仍为"场记板/胶片"样式的图标资源，替换为定稿的 Juke 新 Logo：
**白底 + 纯黑几何 J（顶横杠 + 连续笔画竖笔·半圆底钩·左侧回挑）+ 底钩白腔内的播放三角 ▶**。
只有两个颜色值：`#FFFFFF` 与 `#000000`。禁止渐变、阴影、描边、圆角底板、彩色。

---

## 1. 现状审计（2026-10-26，只读核查结果）

| 文件 | 现状 | 动作 |
|---|---|---|
| `app/src/main/res/drawable/ic_launcher_foreground.xml` | **旧场记板**（1024 viewport，0.8067 缩放） | 替换内容（见 §3.1） |
| `app/src/main/res/drawable/ic_launcher_background.xml` | **AS 模板绿 #3DDC84 + 网格线** | 替换为纯白（见 §3.2） |
| `app/src/main/res/drawable/ic_launcher_monochrome.xml` | 旧内容，且当前未被引用 | 替换内容（见 §3.3） |
| `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` | monochrome 指向 foreground | 一行修改（见 §3.4） |
| `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` | 同上 | 同上 |
| `app/src/main/res/mipmap-*/ic_launcher*.webp` | 旧位图（10 个文件） | 重新生成（见 §3.5） |
| `app/src/main/res/drawable/ic_app_launcher.xml` | **旧场记板** | 替换内容（见 §3.6） |
| `features/settings/ui/src/main/res/drawable/ic_app_launcher.xml` | **旧场记板**（关于页实际引用的就是这份） | 替换内容（同 §3.6） |
| `app/src/debug/res/drawable/ic_app_launcher.xml` | 旧场记板（debug 变体） | 替换内容（同 §3.6） |

**已就绪、禁止改动**：`juke_splash_bg.jpg`、`juke_splash_wordmark.png`、
`values/colors.xml` 的 `splash_background`、`themes.xml` 的两条 `windowSplashScreen*` —— 启动页已按
`design/juke-splash/HANDOFF.md` 实施完毕。

**应用内引用位置（无需改 Kotlin 代码）**：
`features/settings/ui/.../components/InfoPage.kt:59` 用 `painterResource(R.drawable.ic_app_launcher)`
显示 72dp 图标并 `clip(MaterialTheme.shapes.large)`——资源原地替换后自动生效，零代码改动。

---

## 2. 唯一真源（canonical SVG，120 画布）

文件：`design/juke-icon/logo-canonical.svg`（可直接在浏览器打开核对）。
**所有尺寸从它推导，不要从截图临摹。**

```svg
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120">
  <rect width="120" height="120" fill="#FFFFFF"/>
  <rect x="58" y="27" width="32" height="8" fill="#000000"/>
  <path d="M76 31 V65 A20 20 0 0 1 36 65 V55"
        fill="none" stroke="#000000" stroke-width="12"
        stroke-linecap="butt" stroke-linejoin="round"/>
  <path d="M49 57 L65 65 L49 73 Z" fill="#000000"/>
</svg>
```

几何要点（错了就不是这一版）：

1. 竖笔、底钩、回挑是**同一条连续笔画**，描边恒定 12u（画布 10%），端点 butt、转角 round。
2. 底钩为圆心 (56,65)、R20 的**完整 180°** 弧；弧终点 (36,65) 后**垂直向上回挑 10u 到 y=55**——回挑是记忆点，去掉就变成 U。
3. 顶横杠 x58–90 × y27–35，居中于竖笔（横杠中心 x=74，竖笔 x=76），不居中于画布。
4. 播放三角 (49,57)(65,65)(49,73) 完全落在半径 14u 的白腔内，与任何黑笔画最小间隙 3.4u。
5. 图形外接框：x30–90（宽 60）× y27–91（高 64），中心 (60,59)。

---

## 3. 逐文件替换清单（内容可直接粘贴）

### 3.1 `app/src/main/res/drawable/ic_launcher_foreground.xml`

把 120 画布坐标整体 **x−6、y−5** 平移到 108 画布（图形中心落到 (54,54)，
占 60×64dp，完整位于中央 66dp 安全区内，任意系统蒙版不裁切）：

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">

    <!-- 顶横杠 -->
    <path
        android:fillColor="#000000"
        android:pathData="M52,22 H84 V30 H52 Z" />

    <!-- J 主笔画：竖笔 + R20 半圆底钩 + 左侧回挑 -->
    <path
        android:fillColor="#00000000"
        android:strokeColor="#000000"
        android:strokeWidth="12"
        android:strokeLineCap="butt"
        android:strokeLineJoin="round"
        android:pathData="M70,26 V60 A20,20 0 0 1 30,60 V50" />

    <!-- 播放三角 -->
    <path
        android:fillColor="#000000"
        android:pathData="M43,52 L59,60 L43,68 Z" />
</vector>
```

前景层保持**透明底**，白底由背景层提供；不要在前景里画白色方块。

### 3.2 `app/src/main/res/drawable/ic_launcher_background.xml`

删除模板绿与全部网格线，整文件替换为：

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M0,0h108v108h-108z" />
</vector>
```

> 不改会出现图标四周泛绿，深色壁纸下尤其明显。

### 3.3 `app/src/main/res/drawable/ic_launcher_monochrome.xml`

与 §3.1 路径完全相同（系统会自行着色，J 与三角在单色下仍成立，无需简化）。
即将 §3.1 的整份内容原样写入本文件即可。

### 3.4 `mipmap-anydpi-v26/ic_launcher.xml` 与 `ic_launcher_round.xml`

只改 monochrome 一行（当前错误地指向 foreground）：

```xml
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
    <monochrome android:drawable="@drawable/ic_launcher_monochrome"/>
</adaptive-icon>
```

### 3.5 旧版位图 `mipmap-*/ic_launcher.webp` / `ic_launcher_round.webp`（API ≤25 兜底）

按 §3.1 前景层内容渲染到**白底方形**位图，图形高 = 画布 59.3%，居中：

| 目录 | 尺寸 px | 图形高 px | 主笔画宽 px | 三角底边 px |
|---|---|---|---|---|
| mipmap-mdpi | 48 | 28.5 | 5.3 | 7.1 |
| mipmap-hdpi | 72 | 42.7 | 8.0 | 10.7 |
| mipmap-xhdpi | 96 | 56.9 | 10.7 | 14.2 |
| mipmap-xxhdpi | 144 | 85.3 | 16.0 | 21.3 |
| mipmap-xxxhdpi | 192 | 113.8 | 21.3 | 28.4 |

生成方式二选一：
- Android Studio → New → Image Asset → Launcher Icons (Adaptive) → Foreground Layer 选
  `ic_launcher_foreground`、Background Color `#FFFFFF`，覆盖生成；
- 或用 CLI 把 `logo-canonical.svg` 按 120→108 的映射渲染（注意先做 −6,−5 平移）。

### 3.6 `ic_app_launcher.xml`（三处同名文件内容相同，逐个替换）

- `app/src/main/res/drawable/ic_app_launcher.xml`
- `features/settings/ui/src/main/res/drawable/ic_app_launcher.xml`
- `app/src/debug/res/drawable/ic_app_launcher.xml`

应用内版本：120 画布 + **自带白色底**（关于页直接绘制，配合已有的
`clip(shapes.large)` 呈现圆角白 tile 效果）。旧文件的 `android:autoMirrored="true"` 一并去掉：

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="120dp"
    android:height="120dp"
    android:viewportWidth="120"
    android:viewportHeight="120">

    <path
        android:fillColor="#FFFFFF"
        android:pathData="M0,0h120v120h-120z" />

    <path
        android:fillColor="#000000"
        android:pathData="M58,27 H90 V35 H58 Z" />

    <path
        android:fillColor="#00000000"
        android:strokeColor="#000000"
        android:strokeWidth="12"
        android:strokeLineCap="butt"
        android:strokeLineJoin="round"
        android:pathData="M76,31 V65 A20,20 0 0 1 36,65 V55" />

    <path
        android:fillColor="#000000"
        android:pathData="M49,57 L65,65 L49,73 Z" />
</vector>
```

### 3.7 ≤24px 的 UI 内联用法（如后续需要）

去掉播放三角，其余几何不变（三角在 16px 下仅约 2.4px，会糊）。
真源文件：`design/juke-icon/logo-inline.svg`。本次切换如无 ≤24px 使用点，可不做。

### 3.8 512 主标（商店 / GitHub 仓库头像）

`logo-canonical.svg` 直接导出 **512×512 PNG**（白底不透明、无圆角，商店/平台自行裁切）。
若用户在 IDE 里看到的胶片图标来自 GitHub 仓库头像，需在 GitHub 仓库 Settings → Upload picture 手动换上这张图。

---

## 4. 红线（执行侧不得越界）

1. **不触碰**：`JukeSplashScreen.kt`、启动页文案、`juke_splash_wordmark.png`、`juke_splash_bg.jpg`、
   `themes.xml` 的 splash 条目、`values/colors.xml` 的 `splash_background`、
   designsystem 的 `Colors.kt` / `Type.kt` / `Shape.kt`、`build.gradle.kts`、`applicationId`。
2. **原地替换内容，不重命名、不删除文件**——`InfoPage.kt` 等引用按资源名解析，改名会编译失败。
3. 不给图形加渐变、阴影、外发光、圆角底板或任何彩色；不改笔画粗细、圆弧半径、回挑长度。
4. 不改任何 Kotlin 代码（本次纯资源替换，InfoPage 零改动自动生效）。
5. 完成后 grep 校验：`#324E5B`、`#ECBC23`、`#FEBD0A`、`#3DDC84` 在 `**/res/**` 下应**零残留**
   （`ic_launcher_background` 旧绿与场记板三色全部消失）。

---

## 5. 验收清单（逐条自查）

| # | 检查项 | 通过条件 |
|---|---|---|
| 1 | 桌面/抽屉图标 | 白底圆角 tile + 黑 J + 播放三角；无绿色边缘、无场记板残留 |
| 2 | 圆形/水滴/超椭圆蒙版 | J 与三角均不被裁切（图形在 66dp 安全区内） |
| 3 | 深色壁纸 | 白底对比清晰，图形完整 |
| 4 | Themed icon（API 33+） | 开启主题图标后 J 轮廓正常，无缺笔 |
| 5 | 设置 → 关于 | 顶部 72dp 白色圆角 tile 显示新 Logo，无场记板 |
| 6 | 启动页 | 与改动前完全一致（splash 资源未被触碰） |
| 7 | 颜色审计 | `#324E5B` / `#ECBC23` / `#FEBD0A` / `#3DDC84` 全仓库 res 零命中 |
| 8 | 构建 | debug 与 release 变体均编译通过（debug 的 `ic_app_launcher` 同步替换） |

---

## 6. 可直接粘贴给代码 Agent 的提示词

```text
任务：为 Juke（Android，模块 app + features/settings/ui）执行 Logo 全量切换，用定稿的
"黑 J + 播放三角" Logo 替换所有旧场记板/胶片图标。这是精确复现任务，不是再设计。
唯一执行依据：design/juke-icon/LOGO-SWITCH-HANDOFF.md（先完整阅读，严格按 §3 逐文件替换）。

要点：
1. 替换 app 模块 ic_launcher_foreground / ic_launcher_background（旧模板绿→纯白）/
   ic_launcher_monochrome 三个 drawable；mipmap-anydpi-v26 两个 adaptive-icon 的
   monochrome 改指向 @drawable/ic_launcher_monochrome。
2. 三处同名 ic_app_launcher.xml（app/main、features/settings/ui、app/debug）整文件替换为
   HANDOFF §3.6 的内容（120 画布、自带白底、去掉 autoMirrored）。
3. 重新生成各 dpi 的 ic_launcher / ic_launcher_round webp（白底方形，图形高占 59.3%）。
4. 红线：不碰启动页任何资源（juke_splash_*、themes.xml splash 条目、splash_background）、
   不碰 Colors.kt/Type.kt/build 脚本/applicationId、不改任何 Kotlin 文件、
   原地替换不重命名不删除、不加渐变阴影彩色。
5. 完成后自查 HANDOFF §5 验收清单，并 grep #324E5B/#ECBC23/#FEBD0A/#3DDC84 确认 res 零残留，
   回报全部改动文件清单。
```
