# FluxVault

**The Ultimate ECS Resource Storage & Capability Framework for Hytale.**

Before any pipe can route a resource, a machine must be able to safely store it. Whether you are building a steampunk factory, a high-tech ME network, a magic-based RPG, or vehicular logistics, FluxVault provides the ultimate, battle-tested foundation for storing, simulating, and transacting *any* conceivable resource safely and efficiently.

---

## 🌌 The Universal Vault: The Core of Logistics

Before resources can be transported, they must be safely stored. FluxVault focuses entirely on being the definitive foundational layer for resource storage in the Hytale ecosystem.

We provide the complete infrastructure for container creation, state management, and secure external interactions—acting as the ultimate, standardized "endpoint" for any future pipe, cable, or automation mod.

### Limitless Quantifiable Resources
If it can be counted, FluxVault can store it. Our API is heavily optimized for quantifiable payloads (`long` based), with a generic architecture (`<D>`) that accommodates both pure numerical values and highly complex data objects.

**Currently Implemented & Ready to Use:**
* **Liquid:** A fully realized, millibucket-precision liquid system designed for exact, high-performance volume management.
* **Pure Numerical Resources:** A JSON-driven system allowing you to register any pure numerical resource with zero code. It comes pre-registered with `Flux_Energy` (FE) as the universal power standard, but you can easily register custom abstract resources like `Mana`, `Steam_Pressure`, or `Research_Points`.

**Architectural Potential (Java Custom Implementations):**
Because FluxVault uses a generic data layer, you are not restricted to our default implementations. You can utilize our container framework to build entirely custom resource ecosystems:
* **Complex Data Systems:** Need more than just a quantity? You can implement resources with deep metadata, such as an Electricity system that tracks precise *Voltage and Amperage*, or traditional *ItemStacks* that respect NBT data and durability.

---

## 🛠️ Developer Experience (DX): Data-Driven & ECS Native

FluxVault is deeply integrated with Hytale's data-driven architecture. We provide ready-to-use container components so you can attach robust, multiplayer-safe storage to blocks and entities with minimal to zero Java code.

* **JSON & ECS Native Integration:** Simply attach our pre-built components (like `SimpleLiquidContainerBlock` or `SimpleResourceContainerBlock`) directly in your block's JSON definition. Your block instantly gains a working, fully registered tank or energy buffer—no complex Java initialization required.
* **Dynamic Capsule Registry:** Moving past the rigid, hardcoded bucket systems of legacy voxel modding, FluxVault offers a highly flexible capsule registry. You can register any item to act as a fluid container (fully implemented examples include the `Wood Bucket` and `Ancient Bucket`). *(Note: Resource capsules are [Planned])*
* **Entity-Mounted Containers [Planned]:** Unlike older engines where moving containers is a nightmare, FluxVault natively embraces Entity capabilities. Soon, you will be able to create cargo carts or mechs with fuel tanks using the exact same component logic as static blocks.
* **ItemStack Capabilities [Planned]:** Bringing the power of Flux directly to the player's inventory—allowing items like batteries, fluid cells, or mana tablets to inherently hold and transact resources dynamically.
* **Deterministic Compatibility:** Interoperability isn't magic; it's protocol-based.
    * **For the Resources system:** Mod A and Mod B connect instantly by simply targeting the same registered String ID (e.g., sharing the `FluxVault.Flux_Energy` keyword).
    * **For Item, Liquid, and custom data systems:** Seamless interaction is guaranteed as long as both mods reference the same `FluxType` instance (e.g., `FluxType.LIQUID` , `FluxType.ITEM`). No complex integration patches or reflection hacks are required.

---

## 🚀 The Horizon: Roadmap & Ecosystem

To keep our scope strictly focused on being the ultimate storage and capability API, we clearly separate our internal development goals from the future mods we aim to empower.

### 📅 FluxVault Roadmap (Core Mod Features)
These are features planned for sequential implementation within the core FluxVault library:

* 🧬 **Entity Capability Expansion:** Polishing and expanding the `IFluxHandlerProvider` support for entities, ensuring rock-solid stability for data transactions on moving constructs.
* 🎒 **ItemStack Capabilities:** Bringing the power of Flux directly to the player's inventory—enabling items like batteries, fluid cells, and mana tablets to inherently hold and transact resources dynamically.
* 📦 **ItemVault & Native Item Adapters:** Implementing our own high-capacity, automation-driven `ItemVault` (accessed purely via routing, similar to Create's vaults), alongside seamless adapters for Hytale's native `ItemContainerBlock`.
* 🖥️ **Universal Container UI:** Developing a standardized, generic GUI for manual player interaction with Flux containers. Placed at the end of the roadmap to allow time for deep research into Hytale's UI and player inventory synchronization frameworks.

### 🌌 The Ecosystem Vision (What You Can Build)
By adopting the FluxVault API, modders are unlocking the potential to build the most advanced logistics systems in voxel gaming history. These are the types of dependent mods this framework is built to seamlessly support:
* 🧠 **Centralized Network Storage (AE2 / Refined Storage):** Our high-performance, compact payload delivery system (`IFlux`) acts as the perfect backbone for massive, chunk-spanning digital storage networks.
* 🔀 **Microblock & Multi-Part Routing (EnderIO Style):** The `accessPoint` (Face/Bone) routing architecture allows multiple distinct pipes (e.g., Water, Power, Items) from different mods to exist and route within the exact same block space seamlessly.
* ⚙️ **Data-Driven Machinery:** Future standalone addons (e.g., `FluxVault-Machinery`) can allow modders to create fully functional, UI-enabled processing machines simply by linking a recipe JSON to a generic FluxVault processing component.
* 🚂 **Dynamic Vehicular Logistics:** Utilizing our native Entity-based container support, modders can create trains that pump fluids from stations, or drones that transfer energy in mid-air.