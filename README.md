# Mastermind CLI Game

A Java-based implementation of the classic Mastermind guessing game with a command-line interface. Players attempt to crack a secret 4-number combination within 10 attempts, receiving strategic feedback after each guess. The game integrates with Random.org's [API](https://www.random.org/clients/http/api/) for truly random number generation and includes comprehensive error handling with local fallback.

## Table of Contents
- [Demo / TL;DR](#demo--tldr)
- [Quick Start](#quick-start)
- [How to Play](#how-to-play)
- [Architecture Overview](#architecture-overview)
- [Extensions & Features](#extensions--features)
- [Testing & CI](#testing--ci)
- [Technical Implementation Details](#technical-implementation-details)
- [Critical Design Decisions](#critical-design-decisions)
- [Planning](https://trello.com/b/6MPl0smI/reach-mastermind)

---

## Demo / TL;DR

```bash
git clone https://github.com/joe-bor/mastermind.git
cd mastermind
chmod +x run.sh
./run.sh
```

Enter guesses like: `0 1 3 5`  
The game prints feedback after each guess and ends when you guess correctly or run out of attempts.

## Quick Start

**Prerequisites**

* Java JDK 23 (tested on OpenJDK 23)
* Unix-like shell (macOS / Linux) or Windows with bash (or use `gradlew.bat`)
* Internet connection (optional, only for Random.org; the game works offline via fallback RNG)

**Clone & Run**

```bash
git clone https://github.com/joe-bor/mastermind.git
cd mastermind
chmod +x run.sh   # one-time on Unix-like systems
./run.sh
```

**Developer build & tests**

```bash
# Build + tests
./gradlew build

# Run tests only
./gradlew test

# Run from compiled classes (developer)
./gradlew compileJava
java -cp build/classes/java/main com.mastermind.Main
```

## How to Play

* The secret is **4 numbers** in the range `0` to `7` (inclusive). Duplicates are allowed.
* You have **10 attempts** to guess the secret.
* Enter guesses as **four numbers separated by spaces** (example: `1 2 3 4`).
* After each guess you receive feedback:

    * `All correct` — all digits match in correct positions (you win).
    * `All incorrect` — no digits match.
    * `X correct numbers, and Y correct location` — `X` digits appear in the secret (regardless of position), and `Y` of them are in the exact positions.

**Example Gameplay:**

```
========================================
           MASTERMIND - GAME MENU
========================================
Remaining attempts: 10

Choose an option:
1. Make a guess
2. Show game history
3. Exit game

Enter your choice (1-3): 1
Enter your guess (10 attempts remaining): 0 1 2 3
Your guess: 0 1 2 3
Result: 3 correct numbers, and 0 correct location

---

Enter your guess (8 attempts remaining): 1 2 3 4
Your guess: 1 2 3 4
Result: All correct


========================================
          * * * CONGRATULATIONS! * * *
                 YOU WON!

The secret combination was: 1 2 3 4
========================================
```

## Architecture Overview

This project follows a layered, testable design with clean separation of concerns:

### Dependency Injection Chain
```
Main.java
└── GameController(UserInterface, GameFactory)
    ├── UserInterface → MenuChoice enum
    └── GameFactory(NumberGenerator)
        └── RandomNumberGenerator(RandomNumberApiClient)
            └── RandomNumberApiClient(HttpClient)
```


**Key Design Patterns:**
* **Strategy Pattern** - `NumberGenerator` interface for swappable generation strategies
* **Factory Pattern** - `GameFactory` for controlled game creation
* **Value Objects** - `NumCombination`, `History` for type-safe, validated data
* **State Machine** - `Game` with `Status` enum for game flow management

## Extensions & Features

### Beyond Basic Requirements

**Enhanced User Experience**
- Comprehensive input validation with helpful error messages
- Menu-driven interface supporting multiple game sessions
- History tracking with guess-feedback pairs

**Improved Reliability**
- HTTP client with timeouts and status code handling
- Retry logic with exponential backoff for API resilience
- Local fallback ensures game always playable offline
- Comprehensive error handling for all failure scenarios

**Professional Development Practices**
- GitHub Actions CI/CD pipeline for automated testing
- 95+ unit tests with comprehensive edge case coverage
- Clean dependency injection enabling easy testing and mocking

### Potential Future Extensions
- Difficulty levels (different ranges, combination lengths)
- Multiplayer support with turn-based gameplay
- Game statistics and scoring system
- Timed gameplay modes

## Testing & CI

* Unit tests and integration tests live under `src/test/java`.
* The test suite covers:
    * All scoring scenarios, including duplicates and edge-cases
    * API client behavior via mock HTTP responses
    * Error scenarios (timeouts, malformed responses)
    * CLI input validation
* Run tests locally: `./gradlew test`
* CI: a GitHub Actions workflow runs the same test suite on every push

### External Dependencies
- **Random.org API** - True random number generation
- **No external runtime dependencies** - Completely self-contained application

---

## Critical Design Decisions

**NumCombination as Central Domain Model**
- Chose strong typing over primitive collections for type safety and validation
- Encapsulates parsing logic for user input with comprehensive error messages
- Immutable design prevents accidental state modification

**Resilient External API Integration**
- 3-attempt retry logic with exponential backoff for Random.org API
- Graceful degradation to local `java.util.Random` when API unavailable
- Custom `RandomNumberApiException` for clear error semantics
- 10-second HTTP timeouts prevent hanging

**Feedback Algorithm with Duplicate Handling**
- Two-pass algorithm: exact position matches first, then remaining digit matches
- Frequency-based scoring using `Math.min(answerCount, guessCount)` for proper duplicate handling
- Handles complex scenarios like answer "1 1 2 3" vs guess "1 1 1 2"

**Strategy Pattern for Number Generation**
- `NumberGenerator` interface allows swapping between API-based and local number generation
- Enables clean dependency injection and comprehensive testing
- Supports future extensions (different difficulty levels, number ranges)

**Factory Pattern for Game Creation**
- `GameFactory` encapsulates game creation complexity
- Single responsibility: handles number generation coordination
- Enables controlled instantiation with proper dependency injection

---

*REACH Backend Challenge August 2025.*