package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DbRoleContextExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        if (isSkipped(context)) return;
        DbRoleContext.set(DbSessionRole.ADMINISTRATOR);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (isSkipped(context)) return;
        DbRoleContext.clear();
    }

    private boolean isSkipped(ExtensionContext context) {
        return context.getTestClass()
                .map(c -> c.isAnnotationPresent(SkipDbRoleContext.class))
                .orElse(false);
    }
}
