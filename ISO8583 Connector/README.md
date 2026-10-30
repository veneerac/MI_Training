# ISO8583 Connector and Inbound Endpoint

Official WSO2 releases, used in **Lab 13: Connecting to Financial Systems with the ISO 8583 Connector** (Day 2). Tested with WSO2 Integrator: MI 4.6.0.

| File | What it is | Source |
| --- | --- | --- |
| `iso8583-connector-1.0.4.zip` | ISO8583 connector (`iso8583.init`, `iso8583.sendMessage`): MI **sends** ISO 8583 | [wso2-extensions/esb-connector-iso8583 v1.0.4](https://github.com/wso2-extensions/esb-connector-iso8583/releases/tag/v1.0.4) |
| `mi-inbound-iso8583-1.1.5.zip` | ISO8583 inbound endpoint (listener): MI **receives** ISO 8583 | [wso2-extensions/esb-inbound-iso8583 v1.1.5](https://github.com/wso2-extensions/esb-inbound-iso8583/releases/tag/v1.1.5) |

The lab needs both: the connector for Part A (send), the inbound endpoint for Part B (receive).

## Using the connector in VS Code

VS Code can add `mi-connector-iso8583` 1.0.4 to the project (it appears in `pom.xml`), but this version has **no VS Code form definition**. Its "Add new connection" form saves a connection **without host and port**:

```xml
<localEntry key="isoCon" xmlns="http://ws.apache.org/ns/synapse">
  <iso8583.init>
    <isoCon xmlns="name"/>
  </iso8583.init>
</localEntry>
```

At runtime this fails with `The port number does not contain a parsable integer ... Cannot parse null string`.

Write the configuration in the **source view** instead. Use either of the two styles below; both were tested on MI 4.6.0.

**Inline init** (simplest):

```xml
<iso8583.init>
    <serverHost>localhost</serverHost>
    <serverPort>5010</serverPort>
</iso8583.init>
<iso8583.sendMessage/>
```

**Connection (local entry) + `configKey`**:

```xml
<localEntry key="isoCon" xmlns="http://ws.apache.org/ns/synapse">
    <iso8583.init>
        <serverHost>localhost</serverHost>
        <serverPort>5010</serverPort>
    </iso8583.init>
</localEntry>
```

```xml
<iso8583.sendMessage configKey="isoCon"/>
```

**jPOS libraries:** copy `jpos-1.9.4.jar`, `jdom-1.1.3.jar` and `commons-cli-1.3.1.jar` (from the `ISO8583 Test Server` folder) to the project's `deployment/libs/` folder, or to `<MI_HOME>/lib`.

## Offline or manual deployment (no VS Code)

1. Copy `jpos-1.9.4.jar`, `jdom-1.1.3.jar` and `commons-cli-1.3.1.jar` (from the `ISO8583 Test Server` folder) to `<MI_HOME>/lib`.
2. Connector: copy `iso8583-connector-1.0.4.zip` to `<MI_HOME>/repository/deployment/server/synapse-libs/` and add this import as `<MI_HOME>/repository/deployment/server/synapse-configs/default/imports/{org.wso2.carbon.connector}iso8583.xml`:

   ```xml
   <import xmlns="http://ws.apache.org/ns/synapse" name="iso8583" package="org.wso2.carbon.connector" status="enabled"/>
   ```

3. Inbound endpoint: extract `mi-inbound-iso8583-1.1.5.zip` and copy `mi-inbound-iso8583-1.1.5.jar` to `<MI_HOME>/lib`.
4. Restart MI.

## Inbound endpoint reply

After the injecting sequence finishes, the inbound endpoint replies to the client itself: the request MTI is changed to a response (0200 → 0210) and field 39 is set to `00`. The sequence does not need a Respond mediator.
