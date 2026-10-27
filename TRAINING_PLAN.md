# WSO2 Integrator: MI 4.6.0 – 4-Day Training Plan

A 4-day, hands-on plan (7 hours a day) that covers the MI Developer Fundamentals course and selected Developer Advanced topics. Each day runs 09:00–17:00 with a 1-hour lunch and two 15-minute breaks.

See [the prerequisites](docs/) and the shared resources in this repository before Day 1.

## Day 1 – Basics and triggering messages

| Time | Session | Lab |
| --- | --- | --- |
| 09:00–10:15 | Introduction to MI: architecture, building blocks, key features, EIPs, VS Code extension, Dashboard and CLI | — |
| 10:30–11:30 | Setup | Lab 1 + Lab 2: Getting started with MI and the VS Code extension |
| 11:30–12:00 | Triggering messages (part 1): APIs and proxy services | — |
| 13:00–14:00 | | Lab 3: Configuring a REST API |
| 14:00–14:30 | | Lab 4: Configuring a proxy service |
| 14:45–15:30 | Triggering messages (part 2): tasks and inbound endpoints | Lab 5: Periodic execution (short) |
| 15:30–16:45 | | Lab 6: Event listeners (JMS with ActiveMQ) |
| 16:45–17:00 | Recap and Q&A | |

## Day 2 – Mediation

| Time | Session | Lab |
| --- | --- | --- |
| 09:00–09:45 | Message routing | Lab 7: Message routing |
| 09:45–10:30 | Message transformation | Lab 8: Message transformation |
| 10:45–12:00 | Service orchestration | Lab 9: Service orchestration |
| 13:00–13:45 | Templates and reuse | Lab 10: Reusing mediation sequences |
| 13:45–14:45 | Asynchronous messaging: message stores and processors | Lab 11: Store and forward (JMS store on ActiveMQ) |
| 15:00–15:20 | Protocol switching (theory) | — |
| 15:20–16:45 | SaaS and B2B integration: connectors | Lab 13: Connecting web APIs and cloud services |
| 16:45–17:00 | Recap and Q&A | |

## Day 3 – Data, quality and operations

| Time | Session | Lab |
| --- | --- | --- |
| 09:00–10:30 | Data integration: data services | Lab 15: Data integration |
| 10:45–12:00 | Debugging mediation and error handling | Lab 16: Debugging mediation |
| 13:00–14:00 | Unit tests: mock services, remote testing | Lab 17: Unit test suite |
| 14:00–14:45 | Management: CLI, ICP, Management API, users | Lab 18: CLI tool and dashboard |
| 15:00–15:45 | Artifact deployment: CApps, microservices, CI/CD | — |
| 15:45–16:30 | Observability (demo) and mediation best practices | — |
| 16:30–17:00 | Fundamentals recap and Q&A | |

## Day 4 – Advanced

| Time | Session | Lab |
| --- | --- | --- |
| 09:00–09:45 | Advanced concepts: configuration, messaging architecture, transports, builders and formatters | — |
| 09:45–10:45 | | DA Lab 01: MI as a JMS producer and consumer |
| 11:00–11:30 | Extending the product: extension points, class mediators, custom tasks and connectors | — |
| 11:30–12:15 | | DA Lab 02: Creating a custom mediator |
| 13:15–13:45 | Troubleshooting | — |
| 13:45–14:15 | Applying security: static and dynamic secrets, Secure Vault, securing APIs | — |
| 14:15–15:00 | | DA Lab 05: Securing secrets for a VM deployment |
| 15:15–16:00 | | DA Lab 07: Basic Auth for a REST API (MySQL user store) |
| 16:00–16:45 | Deployment strategy and performance tuning | — |
| 16:45–17:00 | Wrap-up and feedback | |

## Not covered hands-on

| Item | Reason |
| --- | --- |
| Protocol switching lab | Covered by DA Lab 01 (JMS) |
| Managing users lab (MySQL) | Taught as theory |
| Grafana-based observability lab | Demo only |
| Custom task and custom connector labs | Overview only |
| Secrets for container environments | VM deployment only |
| VFS file streaming lab | Optional, if time allows |
