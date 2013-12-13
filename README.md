VWorkflows
==============

Workflow API with UI bindings for JavaFX.

See [http://mihosoft.eu/?p=523](http://mihosoft.eu/?p=523) and [http://mihosoft.eu/?p=564](http://mihosoft.eu/?p=564) 
for an introduction.

Join the [Developer Group](https://groups.google.com/forum/#!forum/vrl-developers) if you'd like to contribute.

## Maven Coordinates

Repository: https://oss.sonatype.org/content/repositories/snapshots

### Java 7 ###

VWorkflows-FX:

    <dependency>
      <groupId>eu.mihosoft.vrl.workflow</groupId>
      <artifactId>vworkflows-fx</artifactId>
      <version>0.1-r2-SNAPSHOT</version>
    </dependency>

VWorkflows-Core:

    <dependency>
      <groupId>eu.mihosoft.vrl.workflow</groupId>
      <artifactId>vworkflows-core</artifactId>
      <version>0.1-r2-SNAPSHOT</version>
    </dependency>
    
### Java 8 ###

VWorkflows-FX:

    <dependency>
      <groupId>eu.mihosoft.vrl.workflow-8.0</groupId>
      <artifactId>vworkflows-fx</artifactId>
      <version>0.1-r2-SNAPSHOT</version>
    </dependency>

VWorkflows-Core:

    <dependency>
      <groupId>eu.mihosoft.vrl.workflow-8.0</groupId>
      <artifactId>vworkflows-core</artifactId>
      <version>0.1-r2-SNAPSHOT</version>
    </dependency>


## How To Build

### Reqirements

- Java >= 1.7.25 (Preview Builds of Java 8 >= b105 are also supported)
- Internet connection (other dependencies are downloaded automatically)
- IDE: Gradle Plugin (not necessary for command line usage)

### IDE

Open the `VWorkflows-Core/VWorkflows-FX` Gradle project in your favourite IDE (tested with NetBeans 7.3.1) and build it
by calling the `assemble` task.

### Command Line

Navigate to the gradle project (e.g., `path/to/VWorkflows/VWorkflows-FX`) and enter the following command

#### Bash (Linux/OS X/Cygwin/other Unix-like OS)

    ./gradlew assemble
    
#### Windows (CMD)

    gradlew assemble
    
## Test It

Besides the tests defined in `VWorkflows-Core` (`test` task) it is also possible to run a graphical demo that comes with 
`VWorkflows-FX`. To run it call the `run` task.
