# ISO 8583 Test Server and Test Client

Used by **Lab 13: Connecting to Financial Systems with the ISO 8583 Connector** (Day 2).

| File | Purpose |
| --- | --- |
| `ISO8583TestServer.jar` | Test **host** on port 5010. The ISO8583 connector in MI sends messages here. It approves every request: replies with MTI + 10 (e.g. `0200` → `0210`), field 39 = `00` and an authorization code in field 38. |
| `ISO8583TestClient-1.0.jar` | Test **client** from the WSO2 MI documentation. Sends a raw ISO 8583 message to the MI ISO8583 inbound endpoint on port 5001. |
| `jpos-1.9.4.jar`, `commons-cli-1.3.1.jar`, `jdom-1.1.3.jar`, `org.osgi.core-6.0.0.jar` | Libraries the test server needs. Keep them in the same folder as `ISO8583TestServer.jar`. |
| `source/` | Source of the test server. It uses the same `jposdef.xml` packager as the WSO2 ISO8583 connector. |

## Run

```bash
java -jar ISO8583TestServer.jar          # listens on port 5010
java -jar ISO8583TestServer.jar 6010     # or another port
java -jar ISO8583TestClient-1.0.jar      # sends to localhost:5001
```

Requires Java 11 or later (tested with JDK 21 and WSO2 Integrator: MI 4.6.0).

## Rebuild the test server

```bash
cd source
javac --release 11 -cp ../jpos-1.9.4.jar -d classes src/main/java/org/training/iso/ISO8583TestServer.java
cp src/main/resources/* classes/
printf 'Main-Class: org.training.iso.ISO8583TestServer\nClass-Path: jpos-1.9.4.jar commons-cli-1.3.1.jar jdom-1.1.3.jar org.osgi.core-6.0.0.jar\n' > manifest.txt
jar cfm ../ISO8583TestServer.jar manifest.txt -C classes .
```

## Licenses

- jPOS (`jpos-1.9.4.jar`) is licensed under the GNU AGPL v3. It is included unmodified from Maven Central; its source is available at https://github.com/jpos/jPOS.
- commons-cli, jdom and the OSGi core API are included unmodified from Maven Central under their own licenses.
- The test client comes from the WSO2 MI documentation repository (wso2/docs-mi).

Use test data only. ISO 8583 messages can contain card numbers and PIN blocks.
