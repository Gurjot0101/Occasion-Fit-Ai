# OccasionFit AI

OccasionFit AI is a full-stack cross-platform mobile application that provides AI-powered personalized outfit recommendations through an intelligent conversational interface. Built end-to-end from architecture to deployment, the app leverages multiple specialized AI models for chat, image generation, image analysis, and context management.

Users can securely sign in with Google, upload outfit images, receive real-time streaming AI responses, and get personalized styling suggestions based on occasion, weather, and preferences.

---

## ✨ Features

### 🔐 Authentication
- Google Sign-In integration
- JWT Access + Refresh Token authentication
- Secure token storage using Keychain
- Persistent session management with Redux

### 🤖 AI-Powered Chat
- **Streaming AI responses** with real-time token-by-token display
- Conversational outfit recommendations using multiple AI models
- **Separate specialized models** for different capabilities:
  - Chat generation
  - Image generation
  - Image analysis
  - Context and memory management
- Persistent chat threads with intelligent memory
- Message history across sessions
- Animated typing indicators
- Pending response states
- Thread-based context awareness

### 🖼️ Media Support
- Image upload from gallery
- **AI-powered image generation** for outfit visualization
- **AI image analysis** for outfit feedback
- Outfit image preview before sending
- Multi-modal chat (text + image messages)

### 🎨 UI/UX
- Light / Dark theme support
- Animated RGB gradient borders
- Dynamic welcome screen
- Keyboard-aware chat input
- Smooth chat scrolling
- Real-time streaming response animations

### ⚙️ Backend
- Spring Boot REST API with **Java 21 Virtual Threads**
- High-performance concurrent request handling
- JWT authorization with role-based access
- Google token verification
- Thread and message persistence
- Docker containerization
- Deployed on Render

### 💾 Database
- MongoDB Atlas cloud database
- User profile storage
- Chat thread persistence
- Message history with metadata
- Optimized queries with indexing

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| **Mobile** | React Native, Expo Router |
| **State Management** | Redux Toolkit |
| **Authentication** | Google Sign-In, JWT |
| **Secure Storage** | react-native-keychain |
| **Backend** | Spring Boot, Java 21 (Virtual Threads) |
| **Database** | MongoDB Atlas |
| **AI/ML** | Gemini API, OpenAI API |
| **Containerization** | Docker |
| **Deployment** | Render (Backend), Expo EAS (Mobile) |

---

## 🤖 AI Models Integration

### Production Models
- **Google Gemini** - Chat generation, context management
- **OpenAI GPT** - Advanced conversational AI
- **DALL-E / Stable Diffusion** - Image generation
- **GPT-4 Vision / Gemini Vision** - Image analysis

### Architecture
- **Multi-model orchestration** for optimal performance
- **Cost-efficient model selection** (right model for each task)
- **Streaming response support** for real-time user experience
- **Intelligent context management** across models

### Planned
- Meta Llama support
- Ollama (Local Models)

---

## 📊 Data Models

### User

```json
{
  "id": "string",
  "name": "string",
  "email": "string",
  "profilePicture": "string",
  "provider": "google",
  "preferences": {
    "style": "string",
    "occasions": ["string"]
  },
  "createdAt": "date",
  "updatedAt": "date"
}
```

### ChatThread

```json
{
  "id": "string",
  "userId": "string",
  "title": "string",
  "context": {
    "preferences": {},
    "history": []
  },
  "createdAt": "date",
  "updatedAt": "date"
}
```

### Message

```json
{
  "id": "string",
  "threadId": "string",
  "sender": "user | assistant",
  "text": "string",
  "imageUrl": "string",
  "imageData": "base64",
  "aiModel": "gemini | openai | dalle",
  "status": "pending | streaming | sent | failed",
  "metadata": {
    "tokens": "number",
    "model": "string"
  },
  "createdAt": "date"
}
```

---

## 🔄 Authentication Flow

1. User taps **Sign in with Google**
2. Google SDK returns an `idToken`
3. Token is sent to Spring Boot backend
4. Backend verifies token with Google OAuth
5. User is created or updated in MongoDB
6. Backend issues:
   - JWT Access Token (short-lived)
   - Refresh Token (long-lived)
7. Tokens are securely stored in Keychain
8. User session is managed with Redux Toolkit
9. Automatic token refresh on expiry

---

## 🌐 API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/google` | Google authentication | ❌ |
| POST | `/api/auth/refresh` | Refresh access token | ❌ |
| GET | `/api/user/me` | Current user profile | ✅ |
| DELETE | `/api/user/delete` | Delete user account | ✅ |
| POST | `/api/chat/message` | Send message (streaming), Generate outfit image, Analyze outfit image | ✅ |
| GET | `/api/chat/threads` | Get chat threads | ✅ |
| GET | `/api/chat/messages/{threadId}` | Get thread messages | ✅ |
| DELETE | `/api/chat/thread/{id}` | Delete thread | ✅ |
| GET | `/api/chat/stream/{messageId}` | Stream AI response (SSE) | ✅ |

---

## 📁 Project Structure

```bash
OccasionFit-AI/
│
├── react-frontend/
│   ├── app/
│   │   ├── (auth)/
│   │   │   └── login.tsx
│   │   ├── (tabs)/
│   │   │   ├── chat.tsx
│   │   │   ├── profile.tsx
│   │   │   └── settings.tsx
│   │   └── _layout.tsx
│   ├── components/
│   │   ├── ChatMessage.tsx
│   │   ├── StreamingMessage.tsx
│   │   ├── ImagePreview.tsx
│   │   └── ThemeToggle.tsx
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   ├── useChat.ts
│   │   └── useTheme.ts
│   ├── store/
│   │   ├── authSlice.ts
│   │   ├── chatSlice.ts
│   │   └── store.ts
│   ├── theme/
│   │   └── colors.ts
│   ├── utils/
│   │   ├── api.ts
│   │   └── storage.ts
│   └── assets/
│
└── java-backend/
    ├── controller/
    │   ├── AuthController.java
    │   ├── ChatController.java
    │   └── UserController.java
    ├── service/
    │   ├── AuthService.java
    │   ├── ChatService.java
    │   ├── AIService.java
    │   └── ImageService.java
    ├── repository/
    │   ├── UserRepository.java
    │   ├── ThreadRepository.java
    │   └── MessageRepository.java
    ├── model/
    │   ├── User.java
    │   ├── ChatThread.java
    │   └── Message.java
    ├── dto/
    │   ├── ChatRequest.java
    │   ├── ChatResponse.java
    │   └── AuthResponse.java
    ├── config/
    │   ├── SecurityConfig.java
    │   ├── MongoConfig.java
    │   └── AIConfig.java
    ├── security/
    │   ├── JwtFilter.java
    │   └── JwtUtil.java
    └── util/
        └── VirtualThreadExecutor.java
```

---

## 🚀 Installation & Setup

### Prerequisites
- Node.js 18+
- Java 21+
- MongoDB Atlas account
- Google OAuth credentials
- Gemini API key
- OpenAI API key
- Docker (optional)

### Frontend Setup

```bash
# Clone the repository
git clone https://github.com/Gurjot0101/Occasion-Fit-AI.git
cd Occasion-Fit-AI/react-frontend

# Install dependencies
npm install

# Configure environment variables
cp .env.example .env
# Add your Google Web Client ID

# Start development server
npx expo start

# Build for production
eas build --platform android
eas build --platform ios
```

### Backend Setup

```bash
cd java-backend

# Configure application.properties
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Add MongoDB URI, JWT secret, API keys

# Run with Maven
./mvnw spring-boot:run

# Or build Docker image
docker build -t occasionfit-backend .
docker run -p 8080:8080 occasionfit-backend
```

---

## 🔐 Environment Variables

### Frontend (`.env`)

```env
EXPO_PUBLIC_API_URL=https://your-backend-url.com
EXPO_PUBLIC_GOOGLE_WEB_CLIENT_ID=your-google-client-id
```

### Backend (`application.properties`)

```properties
# MongoDB
spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/occasionfit

# JWT
jwt.secret=your-super-secret-jwt-key
jwt.expiration=3600000
jwt.refresh.expiration=604800000

# Google OAuth
google.web.client.id=your-google-web-client-id

# AI APIs
gemini.api.key=your-gemini-api-key
openai.api.key=your-openai-api-key

# Virtual Threads
spring.threads.virtual.enabled=true

# Server
server.port=8080
```

---

## 🔒 Security Features

- **JWT-based stateless authentication**
- **Google OAuth 2.0 verification**
- **Secure token storage** with react-native-keychain
- **Spring Security filters** with custom JWT validation
- **Protected API routes** with role-based authorization
- **Encrypted communication** (HTTPS/TLS)
- **Token refresh mechanism** for session continuity
- **Rate limiting** on sensitive endpoints (**Pending)

---

## 🎯 Key Technical Achievements

### Performance
- ✅ **Java 21 Virtual Threads** for high-concurrency handling
- ✅ **Streaming AI responses** for real-time user experience
- ✅ **Multi-model orchestration** for cost and performance optimization
- ✅ **MongoDB indexing** for fast query performance

### Architecture
- ✅ **End-to-end ownership** - architected and developed solo
- ✅ **Microservices-ready** design with clear separation of concerns
- ✅ **Docker containerization** for consistent deployments
- ✅ **Cloud-native deployment** on Render with auto-scaling

### AI Integration
- ✅ **Multiple specialized AI models** for different capabilities
- ✅ **Intelligent context management** across conversations
- ✅ **Image generation and analysis** integration
- ✅ **Cost-efficient model selection** strategy

---

## 📱 Mobile App Features

### Android & iOS Support
- Built with React Native and Expo for true cross-platform compatibility
- Production APK/IPA builds via Expo EAS cloud build pipeline
- Platform-specific optimizations

### User Experience
- Smooth animations and transitions
- Responsive design for all screen sizes
- Offline-first architecture (planned)
- Push notifications (planned)

---

## 🛣️ Roadmap

### In Progress
- [ ] Weather-based recommendations
- [ ] Social sharing features

### Planned
- [ ] Voice input for outfit queries
- [ ] Shopping links integration
- [ ] Outfit calendar planning
- [ ] Push notifications

---

## 🤝 Contributing

This is a personal project built end-to-end as a showcase of full-stack mobile development skills. However, feedback and suggestions are welcome!

---

## 📄 License

MIT License © 2026 Gurjot Singh

---

## 👨‍💻 Author

**Gurjot Singh**
- GitHub: [@Gurjot0101](https://github.com/Gurjot0101)
- LinkedIn: [linkedin.com/in/gurjot0101](https://linkedin.com/in/gurjot0101)
- Email: gurjot78885@gmail.com

---

## 🙏 Acknowledgments

- Google Gemini API
- OpenAI API
- React Native & Expo team
- Spring Boot community
- MongoDB Atlas

---

**Built with ❤️ using Spring Boot, React Native, and AI**
