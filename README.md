# StackOverflow Users App

<img src="/screenshot-light.png?raw=true" width=50% height=50%><img src="/screenshot.png?raw=true" width=50% height=50%>

## Assumptions

I took the task mostly at face value, assuming the user is hardcoded to 20 users at once (although
it's configurable on the backend). I followed the spec to show an error screen when no internet
connection.

## Approach

I approached this as a "launch off" for a larger scale project. Therefore, it lays all the
groundwork to be expanded upon in any needed direction - adding more data source implementations,
adjusting UI depending on states (network connection, offline data availability). I made use of a ui/domain/data approach for clear separation between layers, and easier abstractions around sources/repositories.

## Further Improvements

From the top of my head:
- Currently, some interfaces and their implementations are grouped together; separating them would be cleaner. Could even go for modularisation based on feature.
- Implement error handling in the network layer (e.g., API rate limiting, server errors).
- Pull-to-refresh to update user data without requiring a restart.

## Technical Details

**Note:** All libraries were chosen as they are Kotlin Multiplatform compatible, making the codebase ready for iOS use out of the box - just add a SwiftUI view.
- **Compose**: Jetpack Compose for UI.
- **Ktor**: Networking library for API requests.
- **Koin**: Dependency injection framework.
- **Room**: Local database for persistence of follow statuses.

## Prerequisites

- Android Studio 4.0 or later.
- Supports API >= 26 minimum.

## Testing

The application includes unit tests for the business logic layer. To run tests:

```sh
./gradlew test
```

or run them from within Android Studio.
## Contact

For any questions, feel free to contact me at [contact@callumdixon.com](mailto:contact@callumdixon.com).