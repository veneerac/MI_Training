# Troubleshooting Session 01

Bank scenario: the `AccountStatementService` data service fails at peak time. Follow
`Troubleshooting Session 01.pdf`.

| File | Use |
|---|---|
| `setup-bank-db.sql` | Creates `bankdb` (5 accounts, 13 transactions) and the user `bankuser`. Run as MySQL root: `mysql -u root -p < setup-bank-db.sql` |
| `AccountStatementService.dbs` | Data service. Copy to `<MI_HOME>/repository/deployment/server/dataservices/` (create the folder first; a fresh pack does not have it). Needs `mysql-connector-j-8.2.0.jar` in `<MI_HOME>/lib`. |
| `BankStatementService_1.0.0.car` | The bank API (`/bank/statement/{AccountNo}`, `/bank/account/{AccountNo}`) that calls the data service, for the channels. The CAR now also contains the data service (`AccountStatementService.dbs`), so deploying it is enough; do NOT also copy the `.dbs` by hand (two copies would clash). Copy to `<MI_HOME>/repository/deployment/server/carbonapps/` (or deploy with the Management API). Calls the data service on port 8290; the project is in `BankStatementService/` (build: `mvn clean package`). |
| `Pool Configuration Samples.txt` | Trainer reference: the optimized pool as a `[[datasource]]` in `deployment.toml` and inside the `.dbs`, with the sizing formula (tested on MI 4.6.0). |
| `Troubleshooting Session 01 - Connection Pool - Diagrams.pdf` | Trainer diagrams: where a request waits in the pool, life of a connection, pool size and wait, where the settings go. |
| `LoadClient.java` | Load client. JDK 11+, no build: `java LoadClient.java [url] [users] [requestsPerUser]` (default: statement for 1001, 80 users, 1 request each). |

Resources (MI on the default port 8290):

```
GET http://localhost:8290/services/AccountStatementService/account/{AccountNo}
GET http://localhost:8290/services/AccountStatementService/statement/{AccountNo}
```

Through the API (CAR above): `GET http://localhost:8290/bank/statement/{AccountNo}` and `GET http://localhost:8290/bank/account/{AccountNo}`. Use these URLs in `LoadClient.java` to load test through the API.

Accounts: `1001` to `1005`. Send `Accept: application/json` for JSON.
