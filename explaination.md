# Project Explanation - Mars Scientific Satellite System
---

## Observer Pattern Usage

In my implementation, I've used the Observer pattern to establish a one-to-many dependency between objects. When the subject (MarSciSat) changes state or completes an action, all registered observers are automatically notified. This pattern has been incredibly useful for monitoring probe activities without creating tight coupling between components.

### Implementation

```
Subject: MarSciSat
Observer Interface: Observer
Concrete Observers: StatusObserver, HistoryObserver
```
### The Use of Observer Pattern

I selected the Observer pattern for several compelling reasons:

**1. Separation of Concerns**: MarSciSat only focuses solely on simulation logic without worrying about diagnostic file writing or history display.

**2. Extensibility**: If I need to add new monitoring features in the future (like a DataAnalyzer or NetworkLogger), I can simply create a new Observer implementation without touching the existing MarSciSat code.

**3. Decoupling**: My observers can be added or removed at runtime without affecting the core simulation. This makes testing much easier—I can test MarSciSat without observers, or test observers independently.

**4. Single Responsibility**: Each observer handles exactly one monitoring task. StatusObserver writes diagnostics, while HistoryObserver handles history requests. Neither needs to know about the other.

### Real-World Application

When a Sol (Martian day) completes in my simulation, multiple things need to happen:
- Write diagnostic data to file (StatusObserver)
- Retrieve probe activities (HistoryObserver)

Without the Observer pattern, I would have to cram all this logic into MarSciSat's `processSol()` method, creating a high maintenance code.

---

## State Pattern Usage

I implemented the State pattern to allow my Probe objects to alter their behavior when their internal state changes. Rather than using complex conditional logic, I delegate behavior to state objects, making the code much cleaner and more maintainable.

### Implementation

```
Context: Probe
State Interface: ProbeState
Concrete States: LowPowerMode, MovingState, MeasureState
```

### The Use of State Pattern

The State pattern was essential for several reasons:

**1. Eliminates Complex Conditionals**: Without the State pattern, the `handleSol()` method would be filled with nested if-else statements checking the current state and executing different logic. This would make the code difficult to test and maintain.

**2. Encapsulation of State-Specific Data**: Each state can maintain its own data. For example, MovingState stores the destination location, while MeasureState stores measurement types and duration. This data doesn't pollute the Probe class.

**3. Open/Closed Principle**: If we need to add a new state (like a MaintenanceState or ChargingState), we can simply create a new class implementing ProbeState without modifying the existing Probe class or other states.

**4. Clear State Transitions**: Using explicit `setState()` calls makes transitions obvious and easy to trace through the code.

### State Transitions in My System:
```
[Initial] → LowPowerMode
    ↓ (move command)
MovingState → MovingState (keep moving)
    ↓ (destination reached)
LowPowerMode
    ↓ (measure command)
MeasureState → MeasureState (keep measuring)
    ↓ (duration expires)
LowPowerMode
```

##  Dependency Injection
Rather than having classes create their own dependencies using `new`, I pass dependencies through constructors. This is called Constructor Injection, and it's the approach I've consistently used. E.g:
```
public MarSciSat(CommsGenerator commsGen, ProbeFactory factory) {
        this.commsGen = commsGen;
        this.factory = factory;
        this.parser = new MessageParser();
    }
```

**1. Testability**: By injecting dependencies, I can easily substitute mock objects during testing. For example, I could inject a MockCommsGenerator that returns predictable messages instead of random ones.

**2. Loose Coupling**: The MarSciSat class doesn't need to know how to create a CommsGenerator or ProbeFactory. It only needs to know that it has these dependencies.

**3. Flexibility**: If I want to use a different CommsGenerator implementation, I can simply inject a different implementation without changing MarSciSat.

**4. Single Responsibility**: MarSciSat focuses on satellite simulation logic, not on constructing its dependencies.

**More Examples of DI**
E.g: Probe Constructor Injection
```
public abstract class Probe {
    private final String name;
    private Location currentLocation;
    
    // Constructor injection of name and initial location
    public Probe(String name, Location initialLocation) {
        this.name = name;
        this.currentLocation = initialLocation;
        this.state = new LowPowerMode(); // Default state
    }
}

// Used in ProbeFactory
Probe probe = new Rover("rover-0", new Location(10.0, 20.0));
```
E.g: State Constructor Injection
```public class MovingState implements ProbeState {
    private final Location desLocation;
    
    // Constructor injection of destination
    public MovingState(Location desLocation) {
        this.desLocation = desLocation;
    }
}

// Used when setting state
probe.setState(new MovingState(destination));
```

## UML Relationships Summary
Because of my messy UML Diagram, this section I will briefly explain the connections between classes.

#### 1. **Inheritance (extends)**

This represents an IS-A relationship where a subclass inherits all behavior from its superclass.
- `Probe <<abstract>> and Rover`
- `Probe <<abstract>> and Drone`


#### 2. **Interface Implementation (implements)**

This relationship indicates that a class must implement all methods defined in an interface.
- `ProbeState <<interface>> and LowPowerMode`
- `ProbeState <<interface>> and MovingState`
- `ProbeState <<interface>> and MeasureState`
- `Observer <<interface>> and StatusObserver`
- `Message <<interface>> and MoveMsg`

#### 3. **Composition (strong ownership)**

This represents a part-of relationship where the parts cannot exist without the owner.
- `MarSciSat and Probe (1 to many)`

**Reason**: In my design, probes are managed exclusively by MarSciSat and don't have meaning outside this satellite system. If MarSciSat is destroyed, the probes should be destroyed too.

#### 4. **Aggregation (weak ownership)**

This is a HAS-A relationship where parts can exist independently of the owner.
- `MarSciSat and CommsGenerator`
- `Probe and Location`
- `Probe and ProbeState`

**Reason**: The CommsGenerator could theoretically exist and function without MarSciSat. Similarly, Location is a value object that could be used in many contexts beyond just probes. The relationship is weaker than composition because these components have independent meaning.

#### 5. **Dependency (uses)**

This indicates that a class uses another class temporarily, typically as a parameter, local variable, or return type.
- `MessageParser and ParsedMessage (creates)`
- `ProbeFactory and Probe (creates)`
- `StatusObserver and MeasureState (casts to)`

**Reason**: MessageParser creates ParsedMessage objects but doesn't store them as fields—they're returned immediately. This is a temporary usage relationship, making it a dependency rather than a stronger association.

#### 6. **Association**

This represents a general relationship when more specific categorisation isn't necessary.

**My Examples:**
- `MarSciSat and Observer (notifies)`
- `Message and MarSciSat (calls methods)`

**Reason**: These represent general interactions without strong ownership implications. MarSciSat notifies observers, but this is more of a collaborative relationship than ownership.

## Generic Implementation Limitations

While working on this project, I considered several opportunities to use Java generics, but I wasn't able to implement it.

#### 1. **Message Handling with Generics**

**Generic Message Command**
```java
public interface Command<T> {
    void execute(T context);
    String getCommandType();
}

// Concrete commands
public class MoveCommand implements Command<MarSciSat> {
    private final String probeName;
    private final double lat, lon;
    
    @Override
    public void execute(MarSciSat sat) {
        sat.handleMoveCommand(probeName, lat, lon);
    }
}

// Then we can implement generic parser message
public class CommandParser<C extends Command<?>> {
    public C parse(String rawMsg) {
        // Parse and return command
    }
}
```

#### 2. **State Pattern with Generics**

**Generic State**
```java
// Generic State
public interface State<T> {
    String getStateName();
    void handle(T context, int timeUnit);
}

// Probe as generic context
public abstract class Probe<S extends State<Probe<S>>> {
    private S state;
    
    public void setState(S newState) {
        this.state = newState;
    }
    
    public void handleSol(int sol) {
        state.handle(this, sol);
    }
}

// Concrete state
public class MovingState implements State<Probe<?>> {
    @Override
    public void handle(Probe<?> probe, int sol) {
        // Move logic...
    }
}
```
---

## PMD Errors Explanation

I encountered several PMD errors.

### Error 1: Scanner Not Closed

**The PMD Error I Received:**
```
Error at oose-pmd-rules.xml:144:13
<property probeName="violationSuppressXPath" ...
Required attribute 'name' is missing
```

This PMD rule checks that resources like `Scanner` are properly closed using try-with-resources or explicit `close()` calls, preventing resource leaks. But I don't use `Scanner` anywhere in my code.

---

### Error 2: Field Naming Convention - Logger

**The PMD Error I Received:**
```
Error at oose-pmd-rules.xml:178:13
<property probeName="violationSuppressXPath" ...
Required attribute 'name' is missing
```

PMD enforces field naming conventions:
- Static final fields should use `UPPERCASE_WITH_UNDERSCORES` (for constants)
- Non-static fields should use `camelCase`

My `Logger` name I used it in the previous assignment, but it didn't result to the PMD error.

The PMD configuration file has `probeName` instead of `name`.

---

### Error 3: SignatureDeclareThrowsException

**The PMD Error I Received:**
```
Error at oose-pmd-rules.xml:98:13
<property probeName="IgnoreJUnitCompletely" value="false" />
Required attribute 'name' is missing
```

This rule warns when methods declare overly generic exceptions like `throws Exception` instead of specific exception types. This is an important rule because specific exceptions provide better documentation and allow callers to handle different error conditions appropriately.

I created `InvalidMessageException` specifically for message parsing errors, which is exactly what this PMD rule encourages.
