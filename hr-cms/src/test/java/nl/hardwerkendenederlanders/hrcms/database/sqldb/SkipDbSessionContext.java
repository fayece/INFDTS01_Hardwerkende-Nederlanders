package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Opt out of the default role setup
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SkipDbSessionContext {}
