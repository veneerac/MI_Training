# WSO2 Integrator: MI 4.6.0 Training – Shared Resources

Files needed for the 4-day MI 4.6.0 training labs.

| Folder | File | Notes |
| --- | --- | --- |
| `ActiveMQ/` | `apache-activemq-5.19.1-bin.tar.gz` | Official Apache ActiveMQ Classic 5.19.1 release (macOS/Linux). Windows: download the `.zip` from https://activemq.apache.org/. Copy the client JARs from `lib/` (activemq-broker, activemq-client, activemq-kahadb-store, hawtbuf, slf4j-api, `lib/optional/activeio-core`) to `MI_HOME/lib`. |
| `MySQL JDBC Driver/` | `mysql-connector-j-8.2.0.jar` | MySQL Connector/J 8.2.0, for MySQL 8.0 or later. Copy it to `MI_HOME/lib`. Driver class: `com.mysql.cj.jdbc.Driver`. |
| `Hospital Backend/` | `Hospital-Service-JDK11-2.0.0.jar` | Sample Hospital backend used by the mediation labs. Run `java -jar Hospital-Service-JDK11-2.0.0.jar` (port 9090). Tested on JDK 21. Source code is in `source/` (build with `mvn package`). |

Requires JDK 21 (MI 4.6.0 is tested with JDK 21 and 25).
