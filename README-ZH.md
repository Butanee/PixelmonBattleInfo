# Pixelmon Battle Info Pixelmon 对战信息
## 描述
生活质量（QoL）MOD，为《Pixelmon》中的战斗画面添加了一些功能。
其中包括
- 显示敌人和自己的Pixelmon信息的工具提示、
- 移除昵称
- 并显示你的招式的类型效果（仅限于第一个敌人）。

本 MOD 仅用于客户端。可在单人游戏中使用。

- 版本
  - Minecraft 1.16.5
  - Forge 36.2.34
  - Pixelmon 9.1.7 - 可能适用于更早版本，未尝试过

## 当前功能
- **删除所有Pixelmon的昵称**
  - 如果你只是不知道你要对付的是什么，因为它有一个昵称，这很有用


- **提示框**
  - 你的首发Pixelmon (也许你忘了 )
    - 种族,血量,属性,特性,持有物
  - 任何敌方Pixelmon
    - 种族,属性
    - **_Optional - 请参阅 Configs_**
      - 确切的当前 HP
      - 特性
      - 持有物
      - 招式列表
        - 类型 + 有效性 添加到当前 Pixelmon
        - 战斗开始时的 PP (不更新)
        - 招式类型, 威力, 命中率


- **Type Effectiveness （类型有效性） 显示在 Choose Attack UI （选择攻击 UI） 中**
  - 显示您的招式对第一个（最左侧）敌人的输入效果

### Type Effectiveness 颜色表

| 有效性  | 倍率  |                       颜色                       |
|------|:---:|:-------------------------------------------------:|
| 无效   |  0  |  <span style="color: darkgray;">DARK GRAY</span>  |
| 四倍抵抗 | 1/4 |   <span style="color: darkred;">DARK RED</span>   |
| 二倍抵抗 | 1/2 |       <span style="color: red;">RED</span>        |
| 一般   |  1  |     <span style="color: white;">WHITE</span>      |
| 二倍克制 |  2  |     <span style="color: green;">GREEN</span>      |
| 四倍克制 |  4  | <span style="color: darkgreen;">DARK GREEN</span> |


## 计划的功能
- **战斗发现**
  - 当某些部分不再暴露给 Client 端时
  - 关于战斗中敌人的发现
    - 招式列表 - 重写 by `TooltipEnemyMoveset`
      - PP 跟踪
    - 特性 - 重写 by `TooltipEnemyAbility`
    - 持有物 - 重写 by `TooltipEnemyHeldItem`
  - 配置


- **追踪属性增加和减少**
  - 当统计数据增加 / 减少时
    - 在工具提示框中显示
    - 在 Pixelmon 战斗元素附近显示？
  - 配置


## 配置
- `RemoveNicknames` 移除昵称
  - _**Default** = true_
  - 将 Pixelmon 昵称替换为本地化物种名称
- `YourMovesetEffectiveness`
  - _**Default** = true_
  - 显示你的招式效果（仅限第 1 个敌人）
- `PixelmonTooltip`
  - _**Default** = true_
  - 在战斗中悬停 Pixelmon 时显示提示工具 (或者按住 'alt' 代表敌人，按住 'shift' 代表你的)
  - **Related Configs**
    - `TooltipEnemyHeldItem`
      - _**Default** = true_
      - 在 PixelmonTooltip 中，知晓敌人持有的项目
    - `TooltipEnemyMoveset`
      - _**Default** = true_
      - 在 PixelmonTooltip 中，知晓敌人的招式
    - `TooltipEnemyHP`
      - _**Default** = true_
      - 在 PixelmonTooltip 中，知晓敌人的确切 HP
    - `TooltipEnemyAbility`
      - _**Default** = true_
      - 在 PixelmonTooltip 中，知晓敌人的特性