
# ![image](https://github.com/user-attachments/assets/e9362db7-386f-4d32-8ee9-e91f83f38e86)


---

## 🚀 Project Overview

This Java-based simulation models a **Smart Factory** where:

- **Conveyor Belts** (producers) feed items into a shared buffer.
- **Robots** (consumers) retrieve items and perform actions.
- A **modern Swing GUI** visualizes real-time metrics, component animations, and final results.

Key highlights:

- **Multithreaded** design with each `Robot` and `Belt` on its own thread.
- **Producer–Consumer** pattern via a bounded `SharedBuffer<T>` (`ArrayBlockingQueue`).
- **Real-time observability** using an Observer pattern for metrics updates.
- **Five** design patterns: Singleton, Observer, Producer–Consumer, Factory Method, Strategy.
- **SOLID**, **DRY**, and **thread-safe** architecture.

---

## 📊 Live Demo Screenshot



---

## 🛠️ Features

- **Configurable simulation** duration, actions/sec, number of robots/belts.
- **Real-time metrics** table and animated factory layout.
- **Pause/Resume** controls and progress bar.
- **Final summary** dialog with total counts.
- **JUnit 5** tests covering core logic and edge cases.

---

## 📝 Design Patterns Used

1. **Singleton**: `StatisticsContainer` – global metrics store.
2. **Observer**: `Statistic.registerObserver` – GUI listens for metric updates.
3. **Producer–Consumer**: `ConveyorBelt` & `Robot` with `SharedBuffer<T>`.
4. **Factory Method**: `UnitFactory.create(...)` for dynamic unit instantiation.
5. **Strategy**: `ActionStrategy` & `ConsumeStrategy` hooks for behavior variants.

---

## ⚙️ Getting Started

### Prerequisites

- Java 17+
- Gradle 8.x
- (Optional) IntelliJ IDEA or Eclipse

### Build & Run

```bash
# Clone the repository
git clone https://github.com/<your-username>/smart-factory-simulation.git
cd smart-factory-simulation

# Compile
./gradlew clean compileJava

# Run tests
./gradlew test

# Launch simulation (console + GUI)
./gradlew run
```

Alternatively, open `factory.ui.FactoryUI` in your IDE and run the `main` method.

example of the UI i built ( i just spent less than 30 minutes to make this so I know it's not that big deal but here it is:
![image](https://github.com/user-attachments/assets/9dc2b6a7-e0f0-4e51-8dd2-e2bca543ddd7)

---

## 📄 Test Coverage

- **ConveyorBeltTest**: Verifies items moved = `ActionsPerSecond × Time`.
- **MatrixTest**: Validates multi-robot + belt coordination.
- **RobotTest**: Ensures correct action counts per robot.


---

© 2025 Masoud Rafiee\
CS321 – Advanced Programming Techniques

