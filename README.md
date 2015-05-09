# Doctor-X

An experimental reimplementation of the [Doctor Rest](http://github.com/matobet/doctor-rest) project using
the [Vert.x](http://vertx.io/) framework.

You can run it directly in your IDE by creating a run configuration that uses the main class `com.github.matobet.doctor.Main`

The pom.xml uses the Maven shade plugin to assemble the application and all it's dependencies into a single "fat" jar.

To build a "fat jar"

    mvn package

To run the fat jar:

    java -jar target/doctor-x-1.0.0-SNAPSHOT-fat.jar

(You can take that jar and run it anywhere there is a Java 8+ JDK. It contains all the dependencies it needs so you
don't need to install Vert.x on the target machine).

The Doctor Api will be available at http://localhost:8080
