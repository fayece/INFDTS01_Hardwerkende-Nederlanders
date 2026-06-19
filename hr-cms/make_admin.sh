#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "$0")/.."

USERNAME="${1:-admin}"
PASSWORD="${2:-secret}"

source .env

DOCKER_CTX="default"

DEFAULT_SECRET_HASH='$2a$10$ohBMZbB28KBGxvMmpuIVMeH5iOEmf/4TbpXad8smFfw2aghECUiMi'

if [ "$PASSWORD" = "secret" ]; then
  PASSWORD_HASH="$DEFAULT_SECRET_HASH"
else
  SPRING_SECURITY_CRYPTO_JAR="$(find ~/.m2 -name 'spring-security-crypto-*.jar' ! -name '*sources*' | sort -V | tail -1)"
  COMMONS_LOGGING_JAR="$(find ~/.m2 -name 'commons-logging-*.jar' ! -name '*sources*' | head -1)"
  PASSWORD_HASH=$(echo "System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(\"${PASSWORD}\"));" |
    jshell --class-path "${SPRING_SECURITY_CRYPTO_JAR}:${COMMONS_LOGGING_JAR}" -q 2>/dev/null |
    grep -o '\$2[aby]\$[0-9]*\$[A-Za-z0-9./]*')
fi

echo "Creating admin user '${USERNAME}'..."

NEW_ID=$(
  docker --context "$DOCKER_CTX" exec -i hard-work-postgres psql -U "$DB_USERNAME" -d "$DB_NAME" -t -A <<EOF
DO \$\$
DECLARE
  new_id uuid := gen_random_uuid();
  admin_role_id uuid;
BEGIN
  SELECT id INTO admin_role_id FROM roles WHERE internal_name = 'ADMINISTRATOR';

  INSERT INTO users (id) VALUES (new_id);
  INSERT INTO pii.users_pii (user_id, first_name, prefix, last_name, role_id, created_at, active)
    VALUES (new_id, 'Admin', NULL, 'User', admin_role_id, now(), true);
  INSERT INTO pii_strict.users_pii_strict (user_id, password_hash)
    VALUES (new_id, '${PASSWORD_HASH}');

  CREATE TEMP TABLE _new_admin_id (id uuid);
  INSERT INTO _new_admin_id VALUES (new_id);
END \$\$;

SELECT id FROM _new_admin_id;
EOF
)

NEW_ID=$(echo "$NEW_ID" | tail -n1 | tr -d '[:space:]')

echo "Created Postgres user: ${NEW_ID}"

docker --context "$DOCKER_CTX" exec -i mongo_db mongosh -u "$DB_USERNAME" -p "$DB_PASSWORD" \
  --authenticationDatabase admin "$MONGO_DATABASE" --quiet --eval \
  "db.profiles.insertOne({ _id: \"${NEW_ID}\", username: \"${USERNAME}\" })"

echo
echo "Done. Log in with:"
echo "  username: ${USERNAME}"
echo "  password: ${PASSWORD}"
