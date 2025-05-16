# Mini Chat Application Using WebRTC & Firebase (Android-Jetpack Compose)

This project demonstrates how to build a **real-time mini chat application** using **WebRTC and Firebase** for signaling. The system consists of the following key components:

1. **Firebase Realtime Database** – Acts as the signaling server for peer discovery and connection setup.
2. **Android Client** – A mobile chat app built with **Kotlin** and **Jetpack Compose**.
3. **WebRTC** – Used for establishing a peer-to-peer connection for chat and media exchange.
4. **End-to-End Encryption** – Added cryptographic layer to secure signaling data using AES and RSA.

---
## 🎥 Video Tutorial & Playlist

📺 **Watch the full tutorial here:** [YouTube Playlist](https://youtube.com/playlist?list=PLFelST8t9nqgqOFypRxdTQZ4xX9Ww6t8e&si=joSiiHfmLSuefaEu)

---
## 📡 Firebase Signaling Server
The Firebase Realtime Database serves as the signaling mechanism, allowing users to:

✅ **Find a match** – Search for an available user to connect with.

✅ **Exchange encrypted signaling data** – Secure SDP offers, answers, and ICE candidates by encrypting them before sending.

✅ **Publish public RSA keys** – Each user uploads their public RSA key to Firebase, enabling others to encrypt symmetric AES keys securely.

✅ **Handle connection status** – Manage user availability using real-time updates.

🔗 **Firebase Setup Guide:** [Firebase Documentation](https://firebase.google.com/docs/database/)

---
## 🔐 Enhanced Security with Cryptography

This updated version adds an encryption layer to the signaling process to protect your connection setup data:

- When a user connects, they **generate and publish a public RSA key** to Firebase.
- The caller **generates a random AES symmetric key** to encrypt the signaling messages.
- The AES key is **encrypted using the target user’s public RSA key**.
- Both the **AES-encrypted signaling data** and the **RSA-encrypted AES key** are sent to the target via Firebase.
- The target decrypts the AES key with their private RSA key, then decrypts the signaling data with the AES key.
- This ensures **end-to-end encryption** of signaling messages, preventing eavesdropping and securing metadata.

---
## 📱 Android Client
The Android client is developed using **Kotlin**, **Jetpack Compose**, and **WebRTC**. It includes:

✅ **User matchmaking** – Finds and connects with an available peer.

✅ **Encrypted SDP Offer/Answer exchange** – Uses Firebase for signaling with AES/RSA encryption.

✅ **Encrypted ICE Candidate exchange** – ICE candidates are encrypted before transmission.

✅ **Real-time messaging** – Users can chat after establishing a secure connection.

✅ **Connection management** – Allows users to start/stop connections dynamically.

🔗 **Source Code:** [Android Repository](https://github.com/codewithkael/MiniChatClone)

---
## 🔄 Matchmaking & Connection Flow

1️⃣ **Client connects to Firebase** with a random ID and publishes their **RSA public key**.

2️⃣ Sets **initial state** to `LookingForMatch`.

3️⃣ **Finds a match** and updates the target's status to `OfferReceived`.

4️⃣ Caller **generates a random AES key** and encrypts signaling data (SDP/ICE) with AES.

5️⃣ The AES key is **encrypted using the target’s public RSA key**.

6️⃣ Both **encrypted signaling data** and **encrypted AES key** are sent to the target.

7️⃣ Target **decrypts the AES key** with their private RSA key and uses it to decrypt signaling data.

8️⃣ Target **accepts the match**, updates sender’s status to `ReceivedMatch`.

9️⃣ Users can now **send messages and transfer media streams securely**.

🔟 If a user presses **STOP**, their status is set to `Idle`, preventing new matches.

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

2️⃣ The client **publishes its RSA public key to Firebase**.

3️⃣ **Firebase manages matchmaking and encrypted signaling**.

4️⃣ WebRTC establishes a **peer-to-peer connection** after secure signaling.

5️⃣ Users can **exchange messages in real-time** over the established connection.

6️⃣ If a user disconnects, the system resets their status to `Idle`.

---
## 🎬 About My YouTube Channel – @CodeWithKael

I create **programming tutorials**, **real-world projects**, and **tech-related content** to help developers learn and build amazing applications. From **WebRTC** to **Android Development**, my goal is to simplify complex topics and make learning fun and practical.

📢 If you found this project helpful, please **LIKE**, **SHARE**, and **SUBSCRIBE** to my channel **[@CodeWithKael](https://www.youtube.com/@codewithkael)**. It really helps support my work and allows me to keep creating more valuable content! 🚀

---
If you find this project useful, consider giving a ⭐ on GitHub and subscribing to my YouTube channel! 🚀
