# ISO8583 Connector and Inbound Endpoint

Official WSO2 releases, used in **Lab 13: Connecting to Financial Systems with the ISO 8583 Connector**. Tested with WSO2 Integrator: MI 4.6.0.

| File | What it is | Source |
| --- | --- | --- |
| `iso8583-connector-1.0.4.zip` | ISO8583 connector (`iso8583.sendMessage`) | [wso2-extensions/esb-connector-iso8583 v1.0.4](https://github.com/wso2-extensions/esb-connector-iso8583/releases/tag/v1.0.4) |
| `mi-inbound-iso8583-1.1.5.zip` | ISO8583 inbound endpoint (listener) | [wso2-extensions/esb-inbound-iso8583 v1.1.5](https://github.com/wso2-extensions/esb-inbound-iso8583/releases/tag/v1.1.5) |

## Normal use (VS Code)

You do not need these files when you have internet access: in the WSO2 Integrator: MI for VS Code extension, add the **ISO8583** connector and the **ISO8583** inbound endpoint from the Add Artifact / Connectors view. With MI 4.4.0 and later, the extension also packs the jPOS libraries.

## Offline or manual deployment

1. Copy `jpos-1.9.4.jar`, `jdom-1.1.3.jar` and `commons-cli-1.3.1.jar` (from the `ISO8583 Test Server` folder) to `<MI_HOME>/lib`.
2. Connector: copy `iso8583-connector-1.0.4.zip` to `<MI_HOME>/repository/deployment/server/synapse-libs/` and add this import as `<MI_HOME>/repository/deployment/server/synapse-configs/default/imports/{org.wso2.carbon.connector}iso8583.xml`:

   ```xml
   <import xmlns="http://ws.apache.org/ns/synapse" name="iso8583" package="org.wso2.carbon.connector" status="enabled"/>
   ```

3. Inbound endpoint: extract `mi-inbound-iso8583-1.1.5.zip` and copy `mi-inbound-iso8583-1.1.5.jar` to `<MI_HOME>/lib`.
4. Restart MI.
