#!/bin/bash

# Script runs once when database is initialized.
# see docs/db-rbac-postgresql.md for the one-time manual equivalent if you have an existing database.

set -euo pipefail

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE ROLE cms_superuser WITH SUPERUSER LOGIN PASSWORD '$CMS_SUPERUSER_PASSWORD';
    CREATE ROLE cms_flyway WITH LOGIN CREATEROLE PASSWORD '$CMS_FLYWAY_PASSWORD';
    GRANT CREATE ON DATABASE $POSTGRES_DB TO cms_flyway;
    ALTER SCHEMA public OWNER TO cms_flyway;
EOSQL
