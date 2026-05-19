package nl.hardwerkendenederlanders.hrcms.models.commonalities;

public interface FullName {
    String getFirstName();

    String getPrefix();

    String getLastName();

    default String getFullName() {
        if (getPrefix() == null || getPrefix().trim().isEmpty()) {
            return getFirstName() + " " + getLastName();
        }
        return getFirstName() + " " + getPrefix() + " " + getLastName();
    }
}
