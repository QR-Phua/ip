# AlphaOne

A desktop task manager and contact book with a chat-style GUI, built in Java.

For the full user guide, see [`docs/README.md`](docs/README.md).

## Setting up in IntelliJ

**Prerequisites:** JDK 17, IntelliJ IDEA (latest version).

1. Open IntelliJ. If a project is already open, go to `File` > `Close Project` first.
2. Click `Open`, select the project directory, and accept any defaults.
3. Configure the project to use **JDK 17**:
   - Go to `File` > `Project Structure` > `Project`.
   - Set the SDK to JDK 17 and the language level to `SDK default`.
4. Run the app by right-clicking `src/main/java/alphaone/Launcher.java` and choosing `Run Launcher.main()`.

> **Note:** Do not rename or move the `src/main/java` folder — Gradle expects Java source files to be there.

## Building

```
./gradlew run        # run the GUI
./gradlew test       # run all tests
./gradlew shadowJar  # build alphaone.jar in build/libs/
```

## Acknowledgements
This project made use of GitHub Copilot (powered by Claude Sonnet) as an AI-assisted development tool throughout the codebase. Specifically, it was used for:

1) Code refactoring – restructuring and cleaning up existing logic without altering behaviour (original logic is preserved and traceable in the project's Git version history)
2) Code quality and standards enforcement – recommendations to align with Java coding conventions and best practices
3) UI implementation – translating Figma UI prototype designs into JavaFX components
4) Autocomplete and code recommendations – general productivity assistance during development

As per course policy on code reuse and AI tool usage, this acknowledgement is placed here (rather than inline comments) because the use was widespread across the codebase rather than isolated to specific methods or classes. All refactored code retains its original logic, which can be verified through the Git commit history.