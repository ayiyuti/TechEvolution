# Tech Evolution · 科技进化怪潮

一个 **NeoForge 1.21.1** 模组，实现你的创意：

> 随着科技发展，怪物变强；过得越久，怪物越聪明；后期怪物的**第一主动目标就是你的产线**——它们会主动寻找并破坏你的机器、管道、线缆与发电机。

---

## 核心机制

全局**难度**由两部分驱动，二者取和并封顶 100：

- **时间项**：每游戏日（24000 tick）按 `difficulty.timePerDay` 增加
- **科技项**：自动统计你附近区块里的「物流/机器/能源方块」数量，按 `difficulty.techPerBlocks` 折算

怪物分四个进化阶段（按难度值）：

| 阶段 | 难度阈值 | 强度 | 解锁行为 |
|---|---|---|---|
| 幼体 | < 10 | 弱（×0.6 血/伤） | 仅追玩家 |
| 成熟体 | 10–30 | 标准 | — |
| 智能体 | 30–60 | 强（×1.5 血/伤/速） | 避开岩浆/仙人掌/火 |
| 破坏者 | ≥ 60 | 极强（×2.2+） | **第一目标=产线**：主动寻路并带进度破坏机器/管道/线缆/发电机 |

怪物在存活期间会**每分钟检查一次全局难度**，若已超过自身阶段就自动进化（提升属性、解锁更高 AI）。你造的工厂越多、玩得越久，来的怪就越狠、越聪明、越针对你的产线。

---

## 安装

1. 需要 **NeoForge 21.1.259 + Minecraft 1.21.1**
2. 把 `techevolution-1.0.0.jar` 放进整合包 `mods/` 文件夹
3. 启动游戏，配置文件会自动生成在 `config/techevolution.toml`

---

## 指令（需管理员权限 2 级）

- `/techevolution spawn [0-3]` — 在你面前召唤指定阶段的进化怪物（省略则按当前难度生成）
- `/techevolution info` — 显示当前难度 / 阶段 / 科技方块数

---

## 配置项（`config/techevolution.toml`）

| 项 | 默认 | 说明 |
|---|---|---|
| `difficulty.timePerDay` | 1.5 | 每游戏日增加的难度 |
| `difficulty.techPerBlocks` | 20 | 每多少科技方块 +1 难度 |
| `difficulty.scanChunkRadius` | 2 | 统计科技方块时扫描玩家周围区块半径 |
| `stage.larvaeMax / matureMax / smartMax` | 10 / 30 / 60 | 各阶段难度阈值 |
| `stage.healthMul` | 0.6,1.0,1.5,2.2 | 四阶段血量倍数 |
| `stage.damageMul` | 0.6,1.0,1.5,2.6 | 四阶段伤害倍数 |
| `stage.speedMul` | 0.9,1.0,1.1,1.28 | 四阶段速度倍数 |
| `sabotage.enabled` | true | 是否允许破坏产线 |
| `sabotage.breakTicks` | 60 | 破坏一个方块所需 tick（≈3 秒） |
| `sabotage.searchRadius` | 16 | 寻找产线方块的半径 |
| `sabotage.drops` | true | 破坏时是否掉落物品 |
| `spawn.naturalSpawn` | true | 夜晚自然刷怪开关 |
| `spawn.baseChance` | 0.18 | 基础刷怪概率（随难度倍增） |
| `target.extraKeywords` | [] | 额外判定为产线的关键词 |
| `target.ignoreBlocks` | [] | 强制排除的方块（格式 `modid:blockname`） |

---

## 调参速查

- **想立刻看"后期破坏产线"效果**：把 `stage.smartMax` 改成 `5`，进游戏后执行 `/techevolution spawn 3` 召唤破坏者，它会直奔你的机器拆。
- **觉得太难**：调低 `damageMul` / `healthMul`，或调高各阶段阈值。
- **误伤了某些方块**：在 `target.ignoreBlocks` 加入 `"modid:blockname"`。
- **想覆盖更多/更少方块**：在 `target.extraKeywords` 加词，或用更激进/保守的关键词。

---

## 兼容性与性能

- 纯**新增实体 + 事件监听**，**不修改任何原版实体/类、不注入 mixin** → 与大型整合包（如 Astral Anvil 的 196 模组）零冲突。
- 产线判定基于方块注册名关键词匹配，覆盖 Create / AE2 / Mekanism / Ender IO / Refined Storage / Industrial Foregoing / Immersive Engineering / Pipez 等主流科技模组。
- 科技方块扫描**每 5 分钟一次**且仅限玩家附近区块，破坏行为有 tick 冷却与半径限制，手机端（FCL）也能跑。

---

## 在手机上构建（无需电脑）

编译需要联网下载 NeoForgeGradle 插件与 NeoForge 本体（来自 `maven.neoforged.net`）。
本 AI 沙箱无法访问该域名，所以**无法在云端直接产出 jar**；但你的手机能访问（FCL 能正常下载 NeoForge 即为证明），因此可选下面两条路在手机端完成。

### 路线 A：Termux 在手机上直接编译（最推荐，全程不用电脑）

> 适合想"完全在手机上搞定"的情况。FCL 已证明手机网络能直连 NeoForge 仓库，Termux 同样可以。

1. 从 **F-Droid** 安装 Termux（不要装 Google Play 版，那个已停止维护、版本过旧）。
2. 打开 Termux，依次执行：

   ```bash
   pkg update && pkg upgrade -y
   pkg install -y openjdk-21 git unzip
   termux-setup-storage        # 弹窗授权，让 Termux 能访问 /sdcard
   ```

3. 把源码拿到手机上：
   - 若你手上有 `TechEvolution-1.0.0-src.zip`：用数据线/网盘传到手机 `Download` 目录，再在 Termux 里
     ```bash
     cp /sdcard/Download/TechEvolution-1.0.0-src.zip .
     unzip TechEvolution-1.0.0-src.zip
     ```
   - 若已上传到 GitHub：直接 `git clone <你的仓库地址>`。

4. 编译（首次会自动下载 Gradle 9.2.1 与 NeoForge 依赖，需联网且手机留 **3~4GB 空闲空间**，耗时几分钟到十几分钟）：

   ```bash
   cd TechEvolution
   chmod +x gradlew
   export GRADLE_OPTS="-Xmx2g"      # 手机内存紧张时限制一下，避免 OOM
   ./gradlew build --no-daemon
   ```

5. 产物在 `TechEvolution/build/libs/techevolution-1.0.0.jar`。拷进 FCL 实例的 `mods/`：

   ```bash
   cp build/libs/techevolution-1.0.0.jar /sdcard/FCL/.minecraft/versions/Astral\ Anvil/mods/
   ```

   也可用手机自带文件管理器把 jar 复制过去。
6. 启动 FCL 里的整合包即可。进游戏后 `/techevolution spawn 3` 召唤破坏者试试。

**排错：**
- 报 `Could not determine java version` / toolchain 失败 → 确认装的是 `openjdk-21` 而非 17。
- 卡在下载 / `000` → 检查手机网络代理，或换用路线 B。
- 编译中途 OOM 被杀 → 调小 `GRADLE_OPTS` 的 `-Xmx`，并确保剩余存储足够。

### 路线 B：GitHub Actions 云端编译（手机只负责下载 jar）

> 适合不想在手机上跑编译、省电省空间的情况。本仓库已附带 `.github/workflows/build.yml`，推上去即可自动出 jar。

1. 在 GitHub 新建一个仓库。
2. 把本目录（含 `.github/`）推上去（在电脑或 Termux 里 `git init && git add . && git commit && git push`）。
3. 进该仓库 **Actions** 标签页 → 选 `Build TechEvolution` → **Run workflow**。
4. 跑完后在 **Artifacts** 里下载 `techevolution-jar`（里面就是 `techevolution-1.0.0.jar`）。
5. 手机浏览器下载 jar → 丢进 FCL 实例 `mods/`。

> 云端 Runner（GitHub）能正常访问 `maven.neoforged.net`，所以这条路不挑网络。

---

## 构建（开发者）

本工程已按官方 NeoForge 1.21.1 MDK 的 **ModDevGradle（`net.neoforged.moddev` 2.0.148 / Gradle 9.2.1）** 结构配置好，`gradlew` 与 wrapper 均已就绪。

> 注：早期版本误用了旧的 `net.neoforged.gradle.userdev` 工具链，会导致 `Plugin [id: 'net.neoforged.gradle.userdev'] was not found` 报错；现已改用 ModDevGradle，插件从 Gradle 插件门户解析，更稳。

```bash
./gradlew build          # 产物：build/libs/techevolution-1.0.0.jar
# 或首次无 wrapper 时： gradle wrapper --gradle-version 9.2.1  再 ./gradlew build
```

**前置条件：**
- **JDK 21**（Mojang 在 1.21.1 强制 JDK 21）。
- **可访问 `maven.neoforged.net`**（首次构建需联网下载 NeoForgeGradle 插件与 NeoForge 本体；沙箱/内网环境可能无法构建，需在能联网的机器上执行）。
- 产物直接丢进整合包 `mods/` 即可，无需任何其他前置库。
