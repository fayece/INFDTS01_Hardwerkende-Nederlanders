package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Aspect
@Component
public class DbRoleAspect {

    private final TransactionTemplate transactionTemplate;
    private final JdbcTemplate jdbcTemplate;

    public DbRoleAspect(PlatformTransactionManager transactionManager, JdbcTemplate jdbcTemplate) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Around("execution(public * nl.hardwerkendenederlanders.hrcms.database.sqldb..*Repository.*(..))")
    public Object applyDbRole(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return transactionTemplate.execute(status -> {
                DbSessionRole role = DbRoleContext.get();
                jdbcTemplate.execute("SET LOCAL ROLE " + role.getPostgresqlRole());
                try {
                    return joinPoint.proceed();
                } catch (Throwable e) {
                    throw new ProceedFailedException(e);
                }
            });
        } catch (ProceedFailedException e) {
            throw e.getCause();
        }
    }

    private static final class ProceedFailedException extends RuntimeException {
        ProceedFailedException(Throwable cause) {
            super(cause);
        }
    }
}
