package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DbSessionContextExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        if (isSkipped(context)) return;
        DbSessionContext.set(DbSessionRole.ADMINISTRATOR, null);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (isSkipped(context)) return;
        DbSessionContext.clear();
    }

    private boolean isSkipped(ExtensionContext context) {
        return context.getTestClass()
                .map(c -> c.isAnnotationPresent(SkipDbSessionContext.class))
                .orElse(false);
    }
}
