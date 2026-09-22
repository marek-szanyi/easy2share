# Contributing to easy2share

Thanks for your interest in contributing to easy2share.

This project is a Kotlin Android app for sharing clipboard contents and files over a local network without a cloud service or external app install. We welcome bug reports, fixes, documentation improvements, and small, well-scoped feature proposals.

## Project goals

Please keep contributions aligned with the project’s intent:

- local-first, privacy-focused sharing
- encrypted communication over the local network
- no cloud dependency
- simple browser-based client experience

The browser client for this project lives in a separate repository:
https://github.com/marek-szanyi/easy2share-web

## Code of conduct

Be respectful, constructive, and focused on the problem at hand. We want discussions to stay technical and helpful, especially around privacy, security, and local-network behavior.

## Development setup

### Prerequisites

- Android Studio
- Android SDK
- Git
- An Android device or emulator
- Wi‑Fi for local-network testing

### Clone the repository

```bash
git clone https://github.com/marek-szanyi/easy2share.git
cd easy2share
```

Open the project in Android Studio and let Gradle sync it.

## Build and test

Use Gradle commands like:

```bash
./gradlew assembleRelease
./gradlew testDebugUnitTest
```

Before submitting a pull request, make sure the project still builds and the relevant tests pass.

## Contribution workflow

1. Fork the repository if you are working independently.
2. Create a feature or fix branch.
3. Keep changes small and focused.
4. Update documentation when behavior changes.
5. Run the relevant tests before submitting.
6. Open a pull request with a clear summary.

Example branch names:

```bash
git checkout -b fix/clipboard-encoding
git checkout -b feat/qr-session-retry
```

## Coding guidelines

- Follow the existing Kotlin and Android project conventions.
- Prefer readable, maintainable code over clever abstractions.
- Keep scope narrow; avoid unrelated refactors in the same PR.
- Preserve the project’s security model and local-first design.
- Add or update tests when changing behavior.

### Security and protocol-sensitive changes

This project includes encrypted local-network communication and a protocol contract. If you change networking, encryption, session flow, or message formats, please:

- document the change
- add or update tests for the protocol behavior
- preserve backward compatibility unless a breaking change is intentional

## Testing expectations

Please add or update tests for behavior you change, especially for:

- protocol message handling
- session setup and teardown
- file transfer flows
- clipboard synchronization
- encryption and decryption logic

Run the existing tests before opening a PR:

```bash
./gradlew testDebugUnitTest
```

## Reporting bugs

When filing an issue, include:

- a clear description of the bug
- steps to reproduce
- expected behavior
- actual behavior
- Android version and device model if relevant
- screenshots, logs, or recordings when helpful

## Pull request guidelines

A good PR should:

- explain the problem and the fix
- include screenshots or short recordings for UI changes
- describe any behavior changes
- list the tests that were run
- keep the scope narrow and easy to review

Suggested PR template:

```md
## Summary

Describe the problem and the fix.

## Testing

- ./gradlew testDebugUnitTest
```

## Security reports

Please do not open a public issue for security vulnerabilities.

If you believe you have found a security issue, report it privately through the repository’s security reporting flow or contact the maintainer directly before disclosing details publicly.

## License

By contributing, you agree that your contributions will be licensed under the MIT License as described in the repository.

Thank you for helping improve easy2share.
