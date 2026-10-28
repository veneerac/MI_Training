# WSO2 Integrator: MI 4.6.0 Training – Shared Resources

Files needed for the 4-day MI 4.6.0 training labs.

| Folder | File | Notes |
| --- | --- | --- |
| `ActiveMQ/` | `apache-activemq-5.19.1-bin.tar.gz` (macOS/Linux), `apache-activemq-5.19.1-bin.zip` (Windows) | Official Apache ActiveMQ Classic 5.19.1 releases (SHA-512 checked). Copy the client JARs from `lib/` (activemq-broker, activemq-client, activemq-kahadb-store, hawtbuf, slf4j-api, `lib/optional/activeio-core`) to `MI_HOME/lib`. |
| `ActiveMQ/client-jars-for-MI_HOME-lib/` | 6 JARs | The ActiveMQ client JARs taken from the pack above: activemq-broker, activemq-client, activemq-kahadb-store, hawtbuf, slf4j-api, activeio-core. Copy all of them to `MI_HOME/lib` and restart MI. |
| `MySQL JDBC Driver/` | `mysql-connector-j-8.2.0.jar` | MySQL Connector/J 8.2.0, for MySQL 8.0 or later. Copy it to `MI_HOME/lib`. Driver class: `com.mysql.cj.jdbc.Driver`. |
| `Hospital Backend/` | `Hospital-Service-JDK11-2.0.0.jar` | Sample Hospital backend used by the mediation labs. Run `java -jar Hospital-Service-JDK11-2.0.0.jar` (port 9090). Tested on JDK 21. Source code is in `source/` (build with `mvn package`). |

Requires JDK 21 (MI 4.6.0 is tested with JDK 21 and 25) and MySQL Server 8.0 or later.

## What goes into `MI_HOME/lib`

These are the only third-party JARs the labs need. MI already ships with the JMS API and the MySQL user store scripts (`MI_HOME/dbscripts/mysql`).

- The 6 JARs in `ActiveMQ/client-jars-for-MI_HOME-lib/`
- `MySQL JDBC Driver/mysql-connector-j-8.2.0.jar`

Restart MI after copying them.

## Training plan and prerequisites

- [4-day training plan](TRAINING_PLAN.md)
- [Prerequisites (PDF)](docs/MI%204.6.0%20Training%20-%20Prerequisites.pdf)

## Windows notes

Everything in this repository runs on Windows 10/11 with JDK 21. A few commands differ:

| Task | macOS / Linux | Windows |
| --- | --- | --- |
| ActiveMQ package | `apache-activemq-5.19.1-bin.tar.gz` | `apache-activemq-5.19.1-bin.zip` |
| Start ActiveMQ | `bin/activemq console` | `bin\activemq start` |
| Start MI | `bin/micro-integrator.sh` | `bin\micro-integrator.bat` |
| Encrypt secrets (Lab 21) | `bin/ciphertool.sh -Dconfigure` | `bin\ciphertool.bat -Dconfigure` (needs Apache Ant installed) |
| Keystore password file (Lab 21) | `password-tmp` | `password-tmp.txt` |
| Hospital backend, ISO 8583 test server and client | `java -jar <file>.jar` | same |

**cURL commands in the labs:** run them in **Git Bash** (installed with Git for Windows). In Command Prompt and PowerShell, the multi-line `\` and single-quoted JSON in the lab commands do not work as written. Alternatively, use the Try-it feature in VS Code or Postman.
