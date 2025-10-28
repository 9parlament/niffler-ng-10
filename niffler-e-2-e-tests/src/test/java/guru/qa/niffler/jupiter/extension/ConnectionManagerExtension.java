package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.database.ConnectionManager;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ConnectionManagerExtension implements SuiteExtension {

    @Override
    public void afterSuite(ExtensionContext context) {
        ConnectionManager.closeAllConnections();
    }
}
