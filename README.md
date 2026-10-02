# Phchat (PHN Chat) for Android 🇵🇭

**Phchat** is a Philippine mobile chat and community application built natively with **Kotlin**, **Jetpack Compose**, and **Material 3**. Inspired by classic Philippine mobile chat networks (Uzzap), Phchat provides provincial chat rooms, real-time message threading, direct messaging, guestbook profile walls, interactive polls, daily check-in streaks, and Pinoy chat stickers.

## Key Features

- **Tambayan (Provincial Chat Rooms)**:
  - Filter and join regional chat rooms across the Philippines (Metro Manila / NCR, Cebu, Davao, Pampanga, Iloilo, and nationwide groups).
  - Philippine provincial coding and regional tags.
  - Room lock / moderation controls, pin message, announcements, and live online counters.
- **Chika (Direct Messaging)**:
  - 1-on-1 private messaging with active presence status and unread counters.
  - Pin favorite conversations.
- **Pinoy Chat Stickers & Reactions**:
  - Expressive sticker pack with authentic Tagalog/Pinoy phrases (*"Kumusta!"*, *"Salamat Lodi!"*, *"Ingat!"*, *"Tara G!"*, *"Kape Muna!"*, *"Chibog Na!"*, *"Sana All!"*, *"Walang Ganyanan!"*).
  - Quick emoji reactions (`👍`, `❤️`, `😂`, `🔥`, `🇵🇭`, `☕`, `🎉`).
  - Threaded message replies and `@username` mention detection.
- **Community Feed & Interactive Polls**:
  - Community posts by category (*General*, *Provincial Buzz*, *Food & Travel*, *Gaming*, *Chismis*).
  - Interactive polls with live percentage calculations and vote tracking.
  - Post reactions (Likes), bookmarks (Saves), and threaded discussion.
- **Tambay Hall & Gamification**:
  - Daily Check-in Streak system with flame indicators and bonus Tambay Points.
  - Top Tambay Leaderboard ranking active community members.
  - Featured Profiles spotlight carousel.
- **User Profiles & Guestbook Wall**:
  - Custom status messages (*"Online & Tambay ☕"*, *"Chika Time"*, etc.), bio, and province.
  - Guestbook Profile Wall allowing users to leave messages with upvote/downvote (+1/-1) voting.
  - Profile visitor logs showing recent visitors.
- **Notification Center**:
  - Alerts for mentions, replies, wall posts, and daily streak rewards.

## Tech Stack

- **Language**: Kotlin 2.2
- **UI Toolkit**: Jetpack Compose with Material 3 (Material Design 3)
- **Architecture**: MVVM with Kotlin Coroutines and StateFlow
- **Build System**: Gradle 9.3.1 with Android Gradle Plugin (AGP) 9.1.1
- **Icons**: Adaptive Material launcher icon and Material Symbols
