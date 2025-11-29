# PocketDM 🎲

**PocketDM** is a "Nothing" themed, AI-powered Dungeon Master in your pocket. Built for the **Mobile AI Hackathon: Cactus X Nothing X Hugging Face**, it combines the power of on-device LLMs with cloud intelligence to deliver an immersive, infinite RPG experience.

![PocketDM Icon](icon.jpg)

## Features

### Hybrid AI Architecture

- **Local First**: Runs **Gemma 3 1b(Best)**, **Qwen 3 0.6b**, or **TinyLlama** directly on your device using the **Cactus SDK**. No internet required for the core experience.
- **Cloud Boost**: Seamlessly switches to **Gemini Pro** (via Hybrid Mode) for complex storytelling or when you need that extra creative spark.
- **Two-Pass Logic**: Solves the "confused AI" problem by splitting generation into two distinct passes:
  1. **The Storyteller**: Focuses purely on narrative and immersion (Streamed in real-time).
  2. **The Accountant**: Runs in the background to analyze the story and update game stats (HP, Gold, Location) via structured JSON.

### Structured Memory & State

- **JSON State Machine**: The game maintains a robust state (Inventory, Quest Log, NPCs) that persists across turns.
- **Smart Context**: The AI receives a structured view of the world, ensuring it remembers where you are and what you're doing.
- **Thinking Tag Cleaner**: Automatically handles and cleans `<thinking>` tags from reasoning models like Qwen to keep the chat clean.

### "Nothing" Aesthetic

- **Simplist UI**: Custom typography and layout inspired by the Nothing OS design language.
- **Dot Matrix Typography**: Uses the iconic dot matrix font for a distinct, retro-futuristic look.
- **Portrait Lock**: Optimized for one-handed mobile play.

### On-Demand Model Management

- **Model Discovery**: Browse and download models directly from the app.
- **Smart Persistence**: Checks your `Downloads` folder to avoid re-downloading large files.
- **Verification**: Automatically verifies GGUF magic bytes to ensure file integrity.

## Installation

1. **Download APK**: Get the latest `PocketDM.apk` from the [Releases](https://github.com/anish-vempaty/PocketDM/releases) page.
2. **Install**:
   - Transfer the APK to your Android device.
   - Tap to install (Allow "Install from Unknown Sources" if prompted).
3. **Permissions**:
   - On first launch, grant **"All Files Access"** when redirected to settings. This is required to save and load AI models.

## How It Works

1. **Start the Adventure**: Launch the app and tap **START GAME**.
2. **Create Your Hero**: Choose your class (Warrior, Mage, Rogue, Cleric).
3. **Model Setup**:
   - The app will attempt to download **Gemma 3 1B** automatically.
   - Alternatively, go to **DISCOVERY MODE** to choose a specific model like Qwen or TinyLlama.
4. **Play**:
   - Type your actions (e.g., "I look around the tavern").
   - Watch the DM narrate the result in real-time.
   - Keep an eye on your **HP**, **Gold**, and **Location** at the top—they update automatically as the story unfolds.

## Configuration

You can tweak the experience in the **OPTIONS** menu:

- **Offline / Hybrid**: Switch between purely local or cloud-assisted AI.
- **Gemini API Key**: Input your key to enable cloud features (Hybrid Mode).
- **Companion**: Enable/Disable the helpful AI companion.
- **Hardware**: Toggle GPU acceleration (if supported).

## Hackathon TracksMain Track: Best Mobile Application with On-Device AI

**PocketDM** brings the power of Large Language Models directly to Android. By leveraging the **Cactus SDK**, it runs models like **Gemma 3** and **Qwen 2.5** entirely on-device, ensuring low latency, privacy, and offline capability. It demonstrates that complex, stateful RPGs can run smoothly on mobile hardware without relying on a server.

### Track 1: The Memory Master

*Best implementation of a shared memory/knowledge base for local LLMs.*

PocketDM implements a **Structured JSON State Machine** that acts as a persistent memory layer.

- **State Persistence**: The app maintains a structured record of the game state (Inventory, Quest Log, NPCs, Location) that persists across turns.
- **Context Injection**: This state is dynamically injected into the prompt, ensuring the local LLM always has perfect context of the world, solving the "amnesia" problem common in long chat sessions.
- **Two-Pass Architecture**: The "Accountant" pass specifically dedicates an inference cycle to maintaining and updating this knowledge base, ensuring accuracy.

### Track 2: The Hybrid Hero

*Best execution of a local <> cloud hybrid inference strategy (router pattern).*

PocketDM features a robust **Hybrid Mode** that acts as a smart router between on-device and cloud inference.

- **Local First**: For standard interactions, the app uses the efficient on-device model (Gemma 3/Qwen) for speed and privacy.
- **Cloud Boost**: When "Hybrid Mode" is enabled, the app can route complex storytelling or reasoning tasks to **Gemini Pro** via the API.
- **Role-Based Routing**: Users can configure *which* role (DM or Companion) uses the cloud, allowing for a mix-and-match strategy (e.g., Local DM for speed, Cloud Companion for rich lore).

---

*Made by 1 person for the Mobile AI Hackathon.*
