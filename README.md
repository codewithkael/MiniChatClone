# Mini Chat Application Using WebRTC & Firebase (Android-Jetpack Compose)

This project demonstrates how to build a **real-time mini chat application** using **WebRTC and Firebase** for signaling. The system consists of the following key components:

1. **Firebase Realtime Database** – Acts as the signaling server for peer discovery and connection setup.
2. **Android Client** – A mobile chat app built with **Kotlin** and **Jetpack Compose**.
3. **WebRTC** – Used for establishing a peer-to-peer connection for chat and media exchange.

---
## 🎥 Video Tutorial & Playlist

📺 **Watch the full tutorial here:** [YouTube Playlist](https://youtube.com/playlist?list=PLFelST8t9nqgqOFypRxdTQZ4xX9Ww6t8e&si=joSiiHfmLSuefaEu)

---
## 📡 Firebase Signaling Server
The Firebase Realtime Database serves as the signaling mechanism, allowing users to:

✅ **Find a match** – Search for an available user to connect with.

✅ **Exchange SDP offers and answers** – Initiate a WebRTC session.

✅ **Exchange ICE candidates** – Establish a peer-to-peer connection.

✅ **Handle connection status** – Manage user availability using real-time updates.

🔗 **Firebase Setup Guide:** [Firebase Documentation](https://firebase.google.com/docs/database/)

---
## 📱 Android Client
The Android client is developed using **Kotlin**, **Jetpack Compose**, and **WebRTC**. It includes:

✅ **User matchmaking** – Finds and connects with an available peer.

✅ **SDP Offer/Answer exchange** – Uses Firebase for signaling.

✅ **ICE Candidate exchange** – Establishes a direct WebRTC connection.

✅ **Real-time messaging** – Users can chat after establishing a connection.

✅ **Connection management** – Allows users to start/stop connections dynamically.

🔗 **Source Code:** [Android Repository](https://github.com/codewithkael/MiniChatClone)

---
## 🔄 Matchmaking & Connection Flow

1️⃣ **Client connects to Firebase** with a random ID.

2️⃣ Sets **initial state** to `LookingForMatch`.

3️⃣ **Finds a match** and updates the target's status to `OfferReceived`.

4️⃣ Target **accepts the match**, updates sender’s status to `ReceivedMatch`.

5️⃣ **Exchanges SDP and ICE candidates** to establish a WebRTC connection.

6️⃣ Users can now **send messages and transfer media streams**.

7️⃣ If a user presses **STOP**, their status is set to `Idle`, preventing new matches.

### **Connection Status Values:**
| Status Name         | Description |
|---------------------|-------------|
| `LookingForMatch`  | Client is searching for a match. |
| `OfferReceived`    | A match request has been received. |
| `ReceivedMatch`    | The client accepted the match and is ready for connection. |
| `Idle`            | The client is unavailable for new matches. |

---
## 📌 How It Works

1️⃣ **User enters the app and searches for a match**.

2️⃣ **Firebase Realtime Database** manages matchmaking and signaling.

3️⃣ **WebRTC establishes a direct peer-to-peer connection**.

4️⃣ Users can **exchange messages** in real-time.

5️⃣ If a user disconnects, the system resets their status to `Idle`.

---
## 🎬 About My YouTube Channel – @CodeWithKael

I create **programming tutorials**, **real-world projects**, and **tech-related content** to help developers learn and build amazing applications. From **WebRTC** to **Android Development**, my goal is to simplify complex topics and make learning fun and practical.

📢 If you found this project helpful, please **LIKE**, **SHARE**, and **SUBSCRIBE** to my channel **[@CodeWithKael](https://www.youtube.com/@codewithkael)**. It really helps support my work and allows me to keep creating more valuable content! 🚀

---
If you find this project useful, consider giving a ⭐ on GitHub and subscribing to my YouTube channel! 🚀
