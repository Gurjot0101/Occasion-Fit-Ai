# Frontend (React Native / Expo)

The mobile frontend source is kept private.

This repo focuses on the backend architecture — the planner-based agentic orchestration, tool execution, and API design — which is the primary technical showcase of this project. Below is an overview of the frontend so reviewers can understand what it covers without the source being public.

---

## Stack

| Layer | Technology |
|---|---|
| Framework | React Native + Expo (Expo Router, file-based navigation) |
| State | Redux Toolkit |
| Auth | Google Sign-In, JWT (access + refresh) |
| Networking | Axios with interceptor-based token refresh |
| Styling/Theming | Custom theme context, light/dark support |
| Build | EAS (Expo Application Services) |

---

## Navigation Structure

Expo Router with route groups separating authenticated and unauthenticated flows:

```
app/
├── (auth)/
│   └── login.tsx          → Google Sign-In entry point
└── (app)/
    ├── index.tsx           → Main chat screen
    ├── about.tsx           → App/version info
    ├── profile.tsx         → User profile & settings
    └── _layout.tsx         → Drawer navigation shell
```

The `(auth)` / `(app)` split lets the root layout gate navigation based on auth state pulled from Redux — unauthenticated users never reach the `(app)` group.

---

## Core Components

| Component | Responsibility |
|---|---|
| `Chatbox.tsx` | Main conversational interface — message list, input bar, streaming response rendering |
| `MessageBubble.tsx` | Renders individual messages, including multi-image layout, text, and streaming partial content |
| `SearchBar.tsx` | Input bar with multi-image attachment support |
| `TypingIndicator.tsx` | Animated indicator shown while the backend is generating a response |
| `LoadingRing.tsx` | Reusable loading spinner used across async states |
| `CustomDrawer.tsx` | Custom navigation drawer (replaces default Expo Router drawer UI) |
| `GoogleLoginButton.tsx` | Wraps Google Sign-In SDK and handles the OAuth handoff to the backend |

---

## State Management (Redux Toolkit)

```
store/
├── store.ts            → Root store configuration
├── hooks.ts             → Typed useAppDispatch / useAppSelector
└── reducers/
    ├── authSlice.ts      → Session state: user, tokens, auth status
    └── chatSlice.ts       → Threads, messages, streaming state, image arrays
```

- **`authSlice`** persists session across app restarts (tokens read from Keychain on launch) and exposes auth status to gate navigation.
- **`chatSlice`** manages chat threads, message history, in-flight streaming message buffers, and the `images[]` array used for multi-image upload/analysis flows.

---

## Networking Layer

`config/axios.ts` centralizes the API client:
- Base URL configuration via environment variables
- Request interceptor to attach the JWT access token
- Response interceptor to handle 401s — triggers refresh-token flow and retries the original request transparently
- Avoids circular dependency between the client, auth slice, and store by initializing the store reference lazily

---

## Theming

```
theme/
├── ThemeContext.tsx     → Light/dark theme provider
├── themes.ts             → Color tokens, typography scale
└── FadeInAnimation.tsx   → Reusable entrance animation wrapper
```

Theme switching is context-based rather than relying on the OS color scheme alone, so users can override system preference inside the app.

---

## Custom Hooks

- **`useRainbow.ts`** — drives the animated RGB gradient border/text effect used as a visual signature element across the UI (login button, chat header).

---

## Notable Implementation Details

- **Multi-image support**: `SearchBar` and `chatSlice` were refactored from a singular `image` field to an `images[]` array to support outfit comparison flows (2+ images → `COMPARE_OUTFIT_IMAGES` on the backend).
- **Streaming UI**: messages render token-by-token via an `updateStreamingMessage` action, rather than waiting for the full response — backed by fetch-based SSE streaming (not Axios, which doesn't support streamed responses cleanly in RN).
- **Performance**: `FlatList` is inverted (chat-style, newest at bottom) with `React.memo` and custom equality comparators on `MessageBubble` to avoid re-rendering the entire list on each token update.
- **Feedback loop**: an animated `FeedbackToast` (star rating + Redux-tracked token count trigger) prompts users for feedback after a configurable number of interactions.

---

## Interested in more?

Reach out via LinkedIn: [linkedin.com/in/gurjot0101](https://linkedin.com/in/gurjot0101) — happy to walk through the implementation, share specific files, or do a live demo.

📲 Or just try the app directly: **[APK install link]**
