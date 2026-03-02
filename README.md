# EvoAsell — Fabric Mod 1.21.4

**Developer:** sh1zaExE  
**Minecraft:** 1.21.4  
**Loader:** Fabric Loader 0.16.10  

---

## Features

### 1. Auto Chat Message
Automatically sends a configurable message to the server chat at a set interval.

- Set any message text (up to 256 characters)
- Set the interval in seconds (minimum: 1 second)
- Toggle ON / OFF at any time — the timer resets when you save settings

### 2. Nickname Highlight in Chat
When any player mentions your in-game nickname in chat:

- Your name is highlighted in **bold yellow**
- A soft ping sound plays (quiet Orb Pickup sound)
- Can be toggled ON / OFF

---

## Controls

| Action | Default Key |
|---|---|
| Open EvoAsell Settings Screen | **Right Shift** |

The keybinding can be changed in **Options → Controls → EvoAsell**.

---

## Settings Screen

Press **Right Shift** (or your configured key) in-game to open the settings panel.  
The screen is also accessible from **ModMenu** in the main menu.

Fields in the screen:
- **Message** — the text that will be sent to chat
- **Interval (sec)** — how many seconds between each send
- **Auto Send** — green ON / red OFF toggle
- **Nick Highlight** — green ON / red OFF toggle
- **Save & Close** — persists changes to `config/evoasell.json`

---

## Building from Source

### Requirements
- JDK 21+
- Internet connection (Gradle downloads dependencies automatically)

```bash
# Clone / download the project, then:
cd minecraft-mod

# Build the mod JAR
./gradlew build

# Output:  build/libs/evoasell-1.0.0.jar
```

### Installation
1. Install [Fabric Loader 0.16.10](https://fabricmc.net/use/installer/) for Minecraft 1.21.4
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Install [Cloth Config](https://modrinth.com/mod/cloth-config)
4. Install [ModMenu](https://modrinth.com/mod/modmenu) (optional, for the ModMenu button)
5. Drop `evoasell-1.0.0.jar` into your `.minecraft/mods/` folder
6. Launch Minecraft

---

## Config File

Stored at `.minecraft/config/evoasell.json`:

```json
{
  "autoMessageEnabled": false,
  "autoMessageText": "Hello! EvoAsell is active!",
  "autoMessageInterval": 60,
  "nickHighlightEnabled": true
}
```

---

## Project Structure

```
src/
  main/java/me/sh1zaexe/evoasell/
    EvoAsell.java               ← Common mod initializer
    ModMenuIntegration.java     ← ModMenu config screen factory
    config/EvoAsellConfig.java  ← JSON config (GSON)

  client/java/me/sh1zaexe/evoasell/
    EvoAsellClient.java         ← Client init, keybind, tick event
    feature/AutoMessageManager.java  ← Timed chat sender
    gui/EvoAsellScreen.java     ← In-game settings screen
    mixin/ChatHudMixin.java     ← Chat intercept for nick highlight
```
