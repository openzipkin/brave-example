## Tracing Example: Jersey 2.32/DataStax Java Driver 3.10/Apache Cassandra 3.11/JRE 8

Instead of servlet, the frontend is a Jersey application. The backend is Apache Cassandra 3.x.
Requests from the frontend to the backend are via [DataStax Java Driver](https://github.com/datastax/java-driver) 3.x.
Both services run as a normal Java application.

* [brave.example.Frontend](src/main/java/brave/example/Frontend.java) - HTTP controller and DataStax Java Driver
  * [tracing.xml](src/main/resources/tracing.xml) configures basic tracing functions. 
* [brave.example.Backend](src/main/java/brave/example/Backend.java) - Sets system properties and runs `CassandraDaemon`

Here's an example screen shot:
![screen shot](https://user-images.githubusercontent.com/64215/100691758-581f8780-33c4-11eb-8451-2ad9c52a0e08.png)
