package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

public class EnumAwareSqlParameterSource extends BeanPropertySqlParameterSource {

    public EnumAwareSqlParameterSource(Object object) {
        super(object);
    }

    @Override
    public Object getValue(@NonNull String paramName) throws IllegalArgumentException {

        Object value = super.getValue(paramName);
        if (value instanceof Enum<?> e) {
            return e.name();
        }

        return value;
    }
}
