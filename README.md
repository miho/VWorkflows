VWorkflows
==============

Workflow API with UI bindings for JavaFX.

See [http://mihosoft.eu/?p=523](http://mihosoft.eu/?p=523) and [http://mihosoft.eu/?p=564](http://mihosoft.eu/?p=564) 
for an introduction.

Join the [Developer Group](https://groups.google.com/forum/#!forum/vrl-developers) if you'd like to contribute.

## Maven Coordinates

Repository: https://oss.sonatype.org/content/repositories/snapshots

VWorkflows-FX:

    <dependency>
      <groupId>eu.mihosoft.vrl.workflow</groupId>
      <artifactId>vworkflows-fx</artifactId>
      <version>0.1-r1-SNAPSHOT</version>
    </dependency>

VWorkflows-Core:

    <dependency>
      <groupId>eu.mihosoft.vrl.workflow</groupId>
      <artifactId>vworkflows-core</artifactId>
      <version>0.1-r1-SNAPSHOT</version>
    </dependency>


## How To Build

### Reqirements

- Java >= 1.7.25 (Preview Builds of Java 8 >= b105 are also supported)
- Internet connection (other dependencies are downloaded automatically)


### IDE

Open the `VWorkflows-Core/VWorkflows-FX` Gradle project in your favourite IDE (tested with NetBeans 7.3.1) and build it.
`VWorkflows-FX` comes with a demo. To test it call the `run` task.

### Command Line

Navigate to the gradle project (e.g., `path/to/VWorkflows/VWorkflows-FX`) and enter the following command

#### Bash (Linux/OS X/Cygwin/other Unix-like OS)

    ./gradlew build
    
#### Windows

    gradlew build
