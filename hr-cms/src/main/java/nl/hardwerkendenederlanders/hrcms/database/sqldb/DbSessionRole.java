package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DbSessionRole {
    UNAUTHENTICATED("cms_role_unauthenticated"),
    USER("cms_role_user"),
    CONTENT_MANAGER("cms_role_content_manager"),
    ADMINISTRATOR("cms_role_administrator");

    private final String postgresqlRole;

    public static DbSessionRole fromInternalName(String internalName) {
        if (internalName == null) return UNAUTHENTICATED;

        return switch (internalName) {
            case "ADMINISTRATOR" -> ADMINISTRATOR;
            case "CONTENT_MANAGER" -> CONTENT_MANAGER;
            case "USER" -> USER;
            default -> UNAUTHENTICATED;
        };
    }
}
