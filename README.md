# FluxVault

**A Robust Fluid & Logistics Capability Library for Hytale.**

> **Status:** 🚧 **Work in Progress**
>
> * **Flux API:** ✅ Finalized
> * **Liquid Standard:** 🛠️ In Development
> * **Reference Implementation:** 🛠️ In Development

FluxVault is a **Developer Tool / Library Mod** designed to simplify resource logistics in Hytale modding.

### 🎯 The Mission: Standardization & Interoperability
In a modding environment, the hardest part isn't creating a tank—it's making that tank compatible with everyone else's pipes.

Without a common standard, Mod A's pipes cannot talk to Mod B's machines. FluxVault solves this by providing a **Unified Interface Protocol** (`IFluxHandler`).

* **For Developers:** You simply implement the interface. You don't need to write custom compatibility code for every other mod.
* **For Players:** It ensures that a pipe from one mod can seamlessly connect to a machine from another.

**FluxVault handles the "Handshake"; you handle the "Storage".**

---

## 🌟 Core Features

### 🔌 The Flux Capability API
A flexible, high-performance interface (`IFluxHandler`) for handling resource I/O.
* **A Common Language:** Provides a unified way for blocks and items to exchange resources (Liquids, Energy, etc.) if they choose to adopt it.
* **Simplified Logic:** Developers don't need to write complex pipe logic; just implement the interface and let the API handle the transfer.

### 💧 Advanced Liquid System (Reference Implementation)
While Hytale natively handles fluids as blocks or bucket items, FluxVault introduces a more flexible **LiquidStack** system (conceptually similar to Minecraft Forge).
* **Granular Control:** Allows fluids to be stored directly in custom containers **independent of items**, enabling storage in much smaller, precise units (millibuckets).
* **Fluid Registry:** A standardized method for registering and managing custom fluids.

### 🛢️ Basic Storage (Demo)
Includes a simple **Wooden Storage Barrel** as a reference implementation.
* **Example Code:** Demonstrates how to implement `IFluxContainer` for your own custom blocks.
* **Early Utility:** Acts as a simple standalone liquid storage block for players.

---

## 👨‍💻 For Developers

FluxVault serves as a foundational library to speed up your development.

If you are building a machine, tank, or pipe system:
1.  **Implement `IFluxProvider`:** This exposes your block's inventory capabilities to external systems.
2.  **Use `FluxType.LIQUID`:** To leverage the pre-built fluid handling logic.
3.  **Define Rules:** Simply specify if your block allows insertion (`fill`) or extraction (`drain`).

FluxVault handles the backend transfer logic, allowing you to focus on your mod's unique gameplay features.

---
---

# FluxVault (中文說明)

**Hytale 模組開發的流體與物流解決方案。**

> **開發狀態:** 🚧 **開發中**
>
> * **Flux API:** ✅ 已定案
> * **液體標準:** 🛠️ 製作中
> * **參考實作:** 🛠️ 製作中

FluxVault 是一個 **開發者工具/核心庫模組**，旨在簡化 Hytale 模組中的資源物流開發流程。

### 🎯 設計初衷：標準化與互通性
在模組開發環境中，最困難的往往不是「如何寫出一個儲罐」，而是「如何讓這個儲罐能被其他模組的管線識別」。

如果沒有統一的標準，A 模組的管線將無法與 B 模組的機器溝通。FluxVault 透過提供一套 **統一的介面協定** (`IFluxHandler`) 來解決這個問題。

* **對開發者：** 您只需要實作這個介面，而不需要為了相容其他模組去撰寫成堆的轉接代碼。
* **對玩家：** 這確保了來自不同模組的管線與機器能夠無縫連接。

**FluxVault 負責處理「握手協定」，您只需要專注於「儲存邏輯」。**

---

## 🌟 核心特色

### 🔌 Flux Capability API
一套靈活、高效能的介面 (`IFluxHandler`)，用於處理資源的 I/O。
* **通用語言:** 為選擇採用此系統的方塊與物品提供了一種交換資源（如液體、能量）的統一方式。
* **簡化邏輯:** 開發者無需編寫複雜的管線傳輸代碼，只需實作介面，剩下的交給 API 處理。

### 💧 進階液體系統 (參考實作)
Hytale 原生的流體主要以方塊或桶裝物品的形式存在。FluxVault 引入了 **LiquidStack** 概念（類似 Minecraft Forge 的實作），提升了流體處理的靈活性。
* **更精細的單位:** 允許液體以 **非物品 (Non-item)** 的數據形式直接儲存在容器中，並支援被分割成更小的計量單位（毫桶）進行傳輸與使用。
* **液體註冊:** 提供標準化的液體註冊與管理方式。

### 🛢️ 基礎儲存 (範例容器)
包含一個簡單的 **儲液木桶** 作為參考實作。
* **範例代碼:** 展示如何為您的自定義方塊實作 `IFluxContainer`。
* **早期實用性:** 對玩家而言，它也是一個獨立且實用的簡易液體儲存方塊。

---

## 👨‍💻 給開發者

FluxVault 的定位是加速您開發流程的基礎庫。

如果您正在構建機器、儲罐或管線系統：
1.  **實作 `IFluxProvider`:** 讓您的方塊能夠向外部系統宣告其庫存能力。
2.  **使用 `FluxType.LIQUID`:** 直接利用內建的流體處理邏輯。
3.  **定義規則:** 簡單設定您的方塊是否允許注入 (`fill`) 或抽取 (`drain`)。

FluxVault 負責處理後端的傳輸邏輯，讓您可以專心於開發模組獨特的遊戲玩法。

---

**Author:** ben
**License:** MIT (Planned)