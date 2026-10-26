# Juke「J」App 图标 · 落地交接说明（定稿版）

> 本文件由设计侧（只读，未改动项目代码）产出，供具备代码写权限的 agent / 开发同学执行。
> 目标：**像素级一致**地实现已确认的图标，不做任何再创作。

---

## 0. 一句话需求

在白色圆角底上放一只**纯黑**的艺术字 **J**：顶部一条横杠、竖笔下行、底部 R20 半圆钩、钩尾左侧向上回挑；J 的底钩围出的白色空腔里放一枚**实心黑色播放三角**。禁止渐变、阴影、描边外发光、彩色。

---

## 1. 唯一真源（canonical SVG，120 画布）

所有尺寸都从这一份推导。**不要**从截图临摹。

```svg
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120" width="120" height="120">
  <rect width="120" height="120" fill="#FFFFFF"/>
  <!-- 顶部横杠 -->
  <rect x="58" y="27" width="32" height="8" fill="#000000"/>
  <!-- J 主笔画：竖笔 + 半圆底钩 + 左侧回挑 -->
  <path d="M76 31 V65 A20 20 0 0 1 36 65 V55"
        fill="none" stroke="#000000" stroke-width="12"
        stroke-linecap="butt" stroke-linejoin="round"/>
  <!-- 播放三角，锁在白腔内 -->
  <path d="M49 57 L65 65 L49 73 Z" fill="#000000"/>
</svg>
```

### 几何配方（可据此在任意画布重绘）

| 元素 | 定义 | 数值（120 画布，单位 u） |
|---|---|---|
| 顶横杠 | 实心矩形 | x 58–90，y 27–35（32 × 8） |
| 竖笔 | 直线，带描边 | x = 76，y 从 31 到 65 |
| 底钩 | 半圆弧，圆心 (56,65) | R = 20，顺时针 180°，从 (76,65) 到 (36,65) |
| 回挑 | 直线 | 从 (36,65) 垂直向上到 (36,55) |
| 主笔画描边 | 恒定宽度 | 12u = 画布 10%，端点 butt，转角 round |
| 白腔 | 底钩内圈空腔 | 圆心 (56,65)，半径 R20 − 6 = **14u** |
| 播放三角 | 实心等腰，尖朝右 | 顶点 (49,57) (65,65) (49,73)，16 × 16，质心 (54.3, 65) |
| 图形外接框 | 含描边 | x 30–90（宽 60）× y 27–91（高 64），中心 (60, 59) |

**关键约束（错了就不是这一版）**

1. 竖笔、底钩、回挑是**同一条连续笔画**，同一描边宽度 12u，不允许分段或粗细变化。
2. 底钩必须是**完整 180°**，且回挑段向上 10u——回挑是这一版的记忆点，去掉它就变成 U。
3. 顶横杠**只向左右各探出约 12u / 8u**，不居中于画布，而是**居中于竖笔**（横杠中心 x=74，竖笔 x=76）。
4. 三角必须**完全落在半径 14u 的白腔内**，且不得触碰任何黑色笔画（当前最小间隙 3.4u）。
5. 全图只有两个颜色值：`#FFFFFF` 与 `#000000`。

---

## 2. Android 落地清单

### 2.1 自适应图标前景层（108×108dp）

映射规则**不是等比缩放**：把 120 画布按 **1u = 1dp 平移重定心**，使图形中心 (60,59) 落到 108 画布中心 (54,54)，即 **x − 6、y − 5**。这样图形在 108 画布中占 60 × 64dp（55.6% × 59.3%），完整落在 **66dp 安全区**内，套任何系统蒙版（圆/方/水滴）都不会被切。

`app/src/main/res/drawable/ic_launcher_foreground.xml`

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">

    <!-- 顶部横杠 32×8 -->
    <path
        android:fillColor="#000000"
        android:pathData="M52,22 H84 V30 H52 Z" />

    <!-- J 主笔画：竖笔 + R20 半圆底钩 + 左侧回挑 -->
    <path
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

前景层**保持透明底**，白底由背景层提供。不要在前景里画白色方块或圆角矩形。

### 2.2 背景层（当前是 AS 默认绿，必须改）

`app/src/main/res/values/colors.xml`

```xml
<color name="ic_launcher_background">#FFFFFF</color>
```

> 现值为 Android Studio 模板绿 `#3DDC84`。**不改会出现图标四周泛绿**，深色主题下尤其明显。

### 2.3 单色层（API 33+ themed icons）

`app/src/main/res/drawable/ic_launcher_monochrome.xml`：与 2.1 **路径完全相同**，仅把三处 `#000000` 保留为单色即可（系统会自行着色）。J 与三角在单色下仍成立，无需简化。

### 2.4 adaptive-icon 声明

`app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` 与 `ic_launcher_round.xml`

```xml
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_monochrome" />
</adaptive-icon>
```

### 2.5 旧版位图（API 25 及以下）

按 108 前景层内容等比渲染到白底方形 PNG，**图形高度 = 画布的 59.3%**，居中：

| 目录 | 尺寸 | 图形高 | 主笔画宽 | 三角 |
|---|---|---|---|---|
| mipmap-mdpi | 48 | 28.5 | 5.3 | 7.1 |
| mipmap-hdpi | 72 | 42.7 | 8.0 | 10.7 |
| mipmap-xhdpi | 96 | 56.9 | 10.7 | 14.2 |
| mipmap-xxhdpi | 144 | 85.3 | 16.0 | 21.3 |
| mipmap-xxxhdpi | 192 | 113.8 | 21.3 | 28.4 |

### 2.6 应用商店 / 512 主标

白底 512×512，不透明，无圆角（商店自行裁切）：把 108 前景层内容 **×4.74** 铺入，图形高 **303px**、主笔画 **56.9px**、三角 **75.9px**。

### 2.7 ≤24px 的 UI 内联用法

**去掉播放三角**，其余几何不变（三角在 16px 下仅约 2.4px，双关失效且易脏）。J 本体识别不受影响。这一简化版用于 Compose 内联品牌位，不用于 launcher。

---

## 3. 执行顺序与红线

1. **先 grep 再改**：`ic_launcher_foreground`、`ic_app_launcher`、`ic_launcher_background`、`@mipmap/ic_launcher`。现网存在一个**场记板造型的 `drawable/ic_app_launcher.xml`**，它可能被 App 内界面（关于页 / 登录页等）引用。
   - 只替换 **launcher 相关**资源；
   - 若发现 `ic_app_launcher` 被界面引用，**停下来列出来征求确认**，不要顺手一起换。
2. 不修改 `JukeSplashScreen.kt`、启动页文案、`juke_splash_wordmark.png`、主题色板（`Colors.kt`）与字体资源。本次改动范围**仅限 launcher 图标资产**。
3. 不引入新依赖、不改 `build.gradle.kts`、不动 `applicationId`。
4. 不要「顺手优化」图形：不改圆角、不加阴影、不把三角改成圆角、不做渐变、不改笔画粗细。
5. 保留旧图标文件以便回滚（走版本控制或备份，不要直接物理删除）。

---

## 4. 验收标准（逐条可测，全过才算一致）

| # | 检查项 | 通过条件 |
|---|---|---|
| 1 | 颜色 | 全图仅 `#FFFFFF` / `#000000`（位图允许抗锯齿灰阶） |
| 2 | 外接框 | 108 前景层内图形为 60 × 64dp，中心 (54, 54) |
| 3 | 安全区 | 图形完整落在中央 66 × 66dp 内 |
| 4 | 笔画 | 主笔画 12dp 恒定；端点 butt；转角 round |
| 5 | 底钩 | 圆心 (50,60)、R20、扫过完整 180°、回挑段向上 10dp |
| 6 | 横杠 | 52–84 × 22–30，居中于竖笔而非画布 |
| 7 | 三角 | (43,52)(59,60)(43,68)，与任何黑色笔画最小间隙 ≥ 3dp |
| 8 | 背景层 | `ic_launcher_background` = `#FFFFFF`，无绿色残留 |
| 9 | 蒙版 | 圆形 / 方形 / 水滴三种蒙版下，J 与三角均不被切到 |
| 10 | 小尺寸 | 48 / 72 / 96 / 144 / 192 下 J 可辨识，三角不糊成黑点 |
| 11 | 单色层 | themed icons 开启后轮廓正常，无缺笔 |
| 12 | 无副作用 | 未改动启动页、界面图标引用、主题色板、构建脚本 |

**建议的自检方式**：把导出的 512 主标与设计侧真源 SVG 以 50% 透明度叠加比对，任何一条边的错位都应能归因到抗锯齿，而不是几何偏移。

---

## 5. 可直接粘贴给代码 Agent 的提示词

```text
任务：为 Juke（Android，包名 com.thomaskioko.tvmaniac）替换 App launcher 图标。
这是一次「精确复现」任务，不是再设计。请严格按下列规格实现，不要自行调整任何比例。

一、唯一真源（120 画布，白底黑字，只有 #FFFFFF 与 #000000）
  1. 顶横杠：实心矩形 x 58–90, y 27–35
  2. J 主笔画：单条连续路径 "M76 31 V65 A20 20 0 0 1 36 65 V55"，
     stroke-width 12，stroke-linecap butt，stroke-linejoin round，fill none
     （竖笔 x=76 从 y31 到 y65；底钩为圆心 (56,65) 半径 20 的完整 180° 顺时针弧；
      弧终点 (36,65) 后垂直向上回挑到 y=55）
  3. 播放三角：实心 "M49 57 L65 65 L49 73 Z"，必须完全落在底钩围出的半径 14 的白腔内

二、Android 资源
  - drawable/ic_launcher_foreground.xml：viewport 108，把上述坐标整体 x−6、y−5
    （即横杠 M52,22 H84 V30 H52 Z；主笔画 M70,26 V60 A20,20 0 0 1 30,60 V50，strokeWidth 12；
     三角 M43,52 L59,60 L43,68 Z），前景保持透明底，不要画白色底板。
  - values/colors.xml：ic_launcher_background 从 #3DDC84 改为 #FFFFFF
  - drawable/ic_launcher_monochrome.xml：路径同上，单色
  - mipmap-anydpi-v26/ic_launcher.xml 与 ic_launcher_round.xml：
    background=@color/ic_launcher_background，foreground/monochrome 指向上面两个 drawable
  - 各 dpi 的位图 launcher 图标：按 108 前景层内容渲染，图形高占画布 59.3%，白底方形

三、红线
  - 先 grep ic_launcher_foreground / ic_app_launcher / @mipmap/ic_launcher 的全部引用；
    现网存在一个场记板造型的 drawable/ic_app_launcher.xml，若它被 App 内界面引用，
    停下来把引用位置列给我确认，不要一并替换。
  - 本次只改 launcher 资产。禁止改动 JukeSplashScreen.kt、启动页文案、
    juke_splash_wordmark.png、designsystem 的 Colors.kt / Type.kt / Shape.kt、
    build.gradle.kts、applicationId。
  - 禁止给图标加渐变、阴影、描边、圆角底板或任何彩色。
  - 不要物理删除旧图标，保留可回滚。

四、验收（逐条自查并回报结果）
  1. 图形在 108 前景层内为 60×64dp，中心 (54,54)，完整落在中央 66dp 安全区内
  2. 主笔画 12dp 恒定、端点 butt、转角 round；底钩为完整 180° 且带 10dp 向上回挑
  3. 三角与任何黑色笔画的最小间隙 ≥3dp
  4. 圆形/方形/水滴三种蒙版下均不被切；48/72/96/144/192 位图下 J 可辨识
  5. 深色主题与 themed icons 开启后显示正常，无绿色残留
  6. 列出本次改动的全部文件清单，并确认未触碰上述红线文件
```

---

## 6. 交接边界

- 设计侧（本会话）对项目目录**只读**，未写入、未删除任何代码或资源；本页与提案板仅存在于 Canvas 工作区。
- 若执行侧在真实渲染中发现几何需要微调（例如某蒙版下回挑被切、或 48px 下三角发脏），请把渲染截图回传设计侧复核，**不要**由执行侧自行改形。
