# Challenge Retrofit - Android App

This Android application demonstrates a sophisticated flow control mechanism using Retrofit CallAdapter.Factory, OkHttp interceptors, and Coroutines Flow.

## Architecture Overview

The app implements a unique pattern where:

1. **API calls are intercepted** and mocked to return a 423 (Locked) response
2. **CallAdapter.Factory handles 423 responses** by requesting verification through UnlockFlowManager
3. **MainActivity listens for navigation events** and launches the verification screen
4. **Flow-based communication** allows the verification screen to unlock the API call
5. **Seamless continuation** of the original API call after verification

## Key Components

### 1. MockInterceptor
- Intercepts all API calls to `test-endpoint`
- Returns a mocked 423 response with "Resource locked" message
- Located in: `network/MockInterceptor.kt`

### 2. UnlockCallAdapterFactory
- Custom Retrofit CallAdapter that handles 423 responses
- Requests verification through UnlockFlowManager when 423 is received
- Waits for unlock signal via Flow before continuing
- Located in: `network/UnlockCallAdapterFactory.kt`

### 3. UnlockFlowManager
- Singleton object managing unlock mechanism, navigation, and UI state
- Uses CompletableDeferred for reliable unlock coordination
- Uses StateFlow for UI state management and navigation events
- Encapsulates all flow logic away from CallAdapter and activities
- Located in: `flow/UnlockFlowManager.kt`

### 4. Activities

#### MainActivity
- Contains a button to trigger API calls
- Shows loading state and API response
- Observes UI state changes from UnlockFlowManager for reactive UI updates
- Listens for navigation events and launches SecondActivity when needed

#### SecondActivity
- Verification/unlock screen
- Contains a button to complete verification
- Publishes unlock signal and returns to MainActivity

## How It Works

1. User clicks "Call API" button in MainActivity
2. MockInterceptor returns 423 response
3. UnlockCallAdapterFactory detects 423 and requests verification via UnlockFlowManager
4. MainActivity receives navigation event and launches SecondActivity
5. User clicks "Complete Verification" in SecondActivity
6. UnlockFlowManager publishes unlock signal
7. CallAdapter receives signal and continues with successful response
8. MainActivity shows success message

## Testing

The app includes unit tests for:
- MockInterceptor returning 423 responses
- UnlockFlowManager unlock flow communication
- UnlockFlowManager navigation flow communication

Run tests with:
```bash
./gradlew test
```

## Building and Running

1. Open project in Android Studio
2. Build the project: `./gradlew build`
3. Install on device/emulator: `./gradlew installDebug`
4. Launch the app and test the flow

## Dependencies

- Retrofit 2.9.0 - HTTP client
- OkHttp 4.12.0 - HTTP interceptors
- Kotlinx Coroutines 1.7.3 - Flow and async operations
- AndroidX Activity 1.8.2 - Modern activity handling

## Technical Notes

- Uses `GlobalScope.launch` in CallAdapter (shows warning but necessary for this pattern)
- Channel-based unlock flow ensures proper event delivery without replay issues
- StateFlow for navigation events ensures they're not missed by observers
- CallAdapter works with `enqueue()` method (avoids ANR with async handling)
- Thread-safe flow communication between CallAdapter and activities
- Navigation logic is properly encapsulated in UnlockFlowManager
- MainActivity observes navigation events in lifecycle-aware manner
- Proper state management prevents UI inconsistencies
