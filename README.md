# FluxVault

**A Robust Fluid & Logistics Capability Library for Hytale.**

> **Status:** 🚀 **Released v0.1.0**
>
> * **Flux API:** ✅ Finalized (Intent-based routing, Atomic transactions)
> * **Liquid Ecosystem:** ✅ Fully Functional (Buckets, Barrels, Creative Blocks)
> * **Energy Ecosystem:** 🛠️ Framework Ready (Waiting for machine implementations)

FluxVault is a **Developer Tool / Library Mod** designed to simplify resource logistics in Hytale modding.

### 🎯 The Mission: Standardization & Interoperability
In a modding environment, the hardest part isn't creating a tank—it's making that tank compatible with everyone else's pipes.

Without a common standard, Mod A's pipes cannot talk to Mod B's machines. FluxVault solves this by providing a **Unified Interface Protocol** (`IFluxHandler`).

* **For Developers:** You simply implement the interface. You don't need to write custom compatibility code for every other specific mod out there.
* **For Players:** It ensures that pipes and machines from **any mod adopting this API** can seamlessly connect with each other out of the box.

**FluxVault handles the "Handshake"; you handle the "Storage".**

---

## 🌟 Core Features

### 🔌 The Flux Capability API (ECS Driven)
A flexible, high-performance interface (`IFluxHandler` & `IFluxProvider`) built on Hytale's Entity Component System.
* **Intent-Based Routing (`FluxAccess`):** Providers know *why* a connection is being made (`FILL` or `DRAIN`). This allows output-only faces to physically reject input pipes before a connection is even visually established.
* **Dynamic Type Introspection (`matchesFluxType`):** Strict generic capture is avoided. Handlers declare their own compatibility, allowing Mod A's `AdvancedLiquid` to flow into Mod B's tank as long as the underlying payload classes align.
* **Atomic Transactions (`isExact` Contract):** Moves the "All-or-Nothing" contract directly onto the `IFlux` carrier (the "Waybill"). This allows high-tier machines or fluid capsules to mandate exact transfers that persist through any pipe network, eliminating the need for "Simulate-then-Execute" boilerplate.

### ⚡ Flux Energy (FE)
A lightweight, standardized energy unit.
* **Pure Quantity:** Designed as a simple `long` value wrapper. No complex attributes like voltage or amperage—just raw power.
* **Stress-Testing Ready:** Includes `CreativeEnergyComponent` for infinite power generation or absolute voiding.

### 💧 Advanced Liquid System
Hytale natively handles fluids as blocks or bucket items. FluxVault introduces a granular **LiquidStack** system.
* **Millibucket Precision:** Allows fluids to be stored directly in custom containers independent of items.
* **Fluid Registry:** Standardized method for registering custom fluids and dynamic bucket/capsule types.
* **Built-in Content:** Fully functional `Wood Bucket`, `Ancient Bucket`, `Tavern Barrel`, and `Ancient Barrel` for immediate player use and developer reference.
---

## 🛠️ JSON Configuration (Data-Driven)

FluxVault allows you to register liquids and attach storage capabilities to blocks purely through JSON, without writing Java code.

### 1. Registering Custom Liquids
To register a new liquid, place a JSON file in your asset directory:
**Path:** `Asset.zip/Server/Item/Liquid/LiquidType/[liquid_name].json`

```json
{
  "Id": "Acid",
  "TranslationProperties": {
    "Name": "server.items.Fluid_Acid.name"
  },
  "Hazards": ["Corrosive", "Toxic"]
}
```

### 2. Adding Storage to Blocks
   You can add liquid or energy storage to any block by adding components to the BlockEntity section of your block's JSON definition.

#### 🛢️ Liquid Container Component
```JSON
"BlockEntity": {
  "Components": {
    "SingleLiquidContainerComponent": {
      "Capacity": 10000,
      "AcceptedHazards": ["Molten", "Corrosive"]
    }
  }
}
```
* **Capacity:** Maximum amount of liquid (in mB).

* **AcceptedHazards:** Whitelist of hazard tags this container can store. If empty, it accepts safe liquids only.

#### ⚡ Energy Container Component
```JSON
"BlockEntity": {
  "Components": {
    "SingleEnergyContainerComponent": {
      "Capacity": 50000
    }
  }
}
```
* **Capacity:** Maximum energy storage (FE).

#### ♾️ Creative Components (Infinite / Void)
Used for stress-testing or creative mode tools.
```JSON
"BlockEntity": {
  "Components": {
    "CreativeLiquidComponent": {
      "LiquidId": "Water" 
      // Set to "Empty" or leave null to act as a Liquid Void
    },
    "CreativeEnergyComponent": {
      "IsVoid": false 
      // false = Infinite Energy Source, true = Energy Void
    }
  }
}
```

---

## 👨‍💻 For Developers

FluxVault serves as a foundational library to speed up your development. If you are building a machine, tank, or pipe system:

1. **Implement `IFluxProvider`:** Expose your block's capabilities to the ECS network.

2. **Check `FluxAccess`:** Evaluate if the incoming request (`FILL` or `DRAIN`) is allowed on that specific `BlockFace`. Return `null` to physically reject incompatible connections.

3. **Handle `IFlux` Contracts:** Respect the `isExact()` flag on the incoming carrier to enforce atomic transactions.

4. **Return your `IFluxHandler`:** Let the protocol handle `SIMULATE` vs `EXECUTE` lifecycle contracts.

---
---

# FluxVault (中文說明)

**Hytale 模組開發的流體與物流解決方案。**

> **開發狀態:** 🚀 **v0.1.0 正式發布**
>
> * **Flux API:** ✅ 已定案 (支援意圖路由、原子交易)
> * **液體生態系:** ✅ 功能完善 (內建木桶、水桶、創造測試方塊)
> * **能量生態系:** 🛠️ 框架就緒 (等待機器模組實作)

FluxVault 是一個 **開發者工具/核心庫模組**，旨在簡化 Hytale 模組中的資源物流開發流程。

### 🎯 設計初衷：標準化與互通性
在模組開發環境中，最困難的往往不是「如何寫出一個儲罐」，而是「如何讓這個儲罐能被其他模組的管線識別」。

若缺乏統一標準，A 模組的管線注定無法與 B 模組的機器溝通。FluxVault 透過提供一套 **通用介面協定** (`IFluxHandler`) 來打破這個壁壘。

* **對開發者：** 只要實作此介面，就能直接融入物流生態系，免去為個別第三方模組撰寫專屬轉接代碼的災難。
* **對玩家：** 確保了**所有基於 FluxVault 協議開發的模組**，其管線與設備都能夠實現開箱即用的無縫連接。

**FluxVault 負責處理「模組間的握手協定」，您只需要專注於「自身的儲存邏輯」。**

---

## 🌟 核心特色

### 🔌 Flux Capability API (基於 ECS 架構)
一套靈活、高效能的介面 (`IFluxHandler`& `IFluxProvider`)
* **意圖路由(`FluxAccess`):** 供應端可以在建立連線前，提早得知管線的意圖 (`FILL`灌入或`DRAIN`抽出)。這允許「僅限輸出」的介面直接在物理層面拒絕輸入管線的連接。
* **動態型別相容(`matchesFluxType`):** 捨棄死板的泛型綁定，由Handler自身宣告相容性。最大化提升第三方模組自定義物流載體的相容度。
* **原子交易 (`isExact` 合約):** 將「全有或全無」交易要求直接封裝在 IFlux 載體上（即「運單化」）。當高階機器或單元罐（如水桶）發出精準裝填請求時，該合約會隨物流網傳遞，徹底免除繁瑣的「先模擬後執行」兩步驗證。

### ⚡ 通量能量 (Flux Energy / FE)
一套輕量級、標準化的能量單位。
* **純粹數值:** 設計為單純的 `long` 數值包裝。沒有電壓、安培等複雜屬性，專注於通用性。
* **測試工具:** 內建`CreativeEnergyComponent`，支援無限發電與無底虛空模式，供開發者進行管線測試。

### 💧 進階液體系統
FluxVault 引入了 **LiquidStack** 概念，提升了流體處理的靈活性。
* **毫桶精度:** 允許液體以 **非物品 (Non-item)** 的數據形式直接儲存在容器中，並支援被分割成更小的計量單位（毫桶）進行傳輸與使用。
* **液體註冊:** 提供標準化的液體與膠囊(水桶)註冊系統。
* **內建互動內容:** 模組內建完整的`木桶`、`古老木桶` 以及對應的水桶物件，供玩家直接使用，也作為開發者的最佳參考實作。

---

## 🛠️ JSON 配置指南 (數據驅動)
FluxVault 允許您完全透過 JSON 來註冊新液體或為方塊添加儲存功能，無需編寫 Java 代碼。

### 1. 註冊自定義液體
   若要註冊新液體，請將 JSON 檔案放置於資源包路徑：
   **路徑:** `Asset.zip/Server/Item/Liquid/LiquidType/[liquid_name].json`

```JSON
{
  "Id": "Acid",
  "TranslationProperties": {
    "Name": "server.items.Fluid_Acid.name"
  },
  "Hazards": ["Corrosive", "Toxic"]
}
```

### 2. 為方塊添加儲存功能
   您可以在方塊定義 JSON 的 `BlockEntity` 區塊中加入 `Components` 來啟用功能。

#### 🛢️ 基礎液體容器組件 (Simple Liquid Container)
```JSON
"BlockEntity": {
  "Components": {
    "SingleLiquidContainerComponent": {
      "Capacity": 10000,
      "AcceptedHazards": ["Molten", "Corrosive"]
    }
  }
}
```
* **Capacity:** 最大容量 (mB)。

* **AcceptedHazards:** 容許的危害標籤白名單。若為空陣列，則只能裝載無危害的液體。

#### ⚡ 基礎能量容器組件 (Simple Energy Container)
```JSON
"BlockEntity": {
  "Components": {
    "SingleEnergyContainerComponent": {
      "Capacity": 50000
    }
  }
}
```

#### ♾️ 創造模式組件 (無限源 / 虛空)
   用於管線壓力測試或創造模式專用方塊。
```json
"BlockEntity": {
  "Components": {
    "CreativeLiquidComponent": {
      "LiquidId": "Water" 
      // 填入 "Empty" 或留空 (null) 將作為液體虛空
    },
    "CreativeEnergyComponent": {
      "IsVoid": false 
      // false = 無限能源, true = 能量虛空
    }
  }
}
```

---

## 👨‍💻 給開發者

FluxVault 的定位是加速您開發流程的基礎庫。如果您正在構建機器、儲罐或管線系統：

1. **實作`IFluxProvider`:** 讓您的方塊元件向 ECS 網路宣告其庫存能力。
2. **檢查`FluxAccess`:** 判斷該`BlockFace`面向是否允許當前意圖 (`FILL`/`DRAIN`)，若不允許請直接回傳`null`以阻斷管線連接。
3. **遵循載體合約:** 讀取傳入載體的`isExact()`狀態以強制執行原子交易。
4. **回傳`IFluxHandler`:** 將實際的搬運動作與`SIMULATE`/`EXECUTE`生命週期交由協議處理。

詳細的 API 實作方式與防呆規範，請參閱本專案的 Java 原始碼與 Javadoc 註解。

---

**Author:** Ben (popo70023)
**License:** MIT