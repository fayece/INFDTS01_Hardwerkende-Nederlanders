package nl.hardwerkendenederlanders.hrcms.models;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Organization extends BaseEntity {

    @Setter
    private String orgName;

    public Organization(UUID id, String orgName) {
        super(id);
        this.orgName = orgName;
    }

    public Organization(String orgName) {
        super();
        this.orgName = orgName;
    }
}
