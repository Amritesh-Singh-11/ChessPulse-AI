package com.chesspulseai.config;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Migrates early development schemas that stored the external UUID as padded binary data. */
@Component
public class PublicIdSchemaMigration implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(PublicIdSchemaMigration.class);
    private final JdbcTemplate jdbc;

    public PublicIdSchemaMigration(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Override
    public void run(ApplicationArguments args) {
        String type = jdbc.query("select data_type from information_schema.columns where table_schema = database() and table_name = 'games' and column_name = 'public_id'", rs -> rs.next() ? rs.getString(1) : null);
        if (type == null || "varchar".equals(type.toLowerCase(Locale.ROOT))) return;
        if (!"binary".equals(type.toLowerCase(Locale.ROOT)) && !"varbinary".equals(type.toLowerCase(Locale.ROOT))) {
            throw new IllegalStateException("Unsupported games.public_id type: " + type);
        }
        log.info("Migrating games.public_id from {} to varchar(36)", type);
        jdbc.execute("alter table games add column public_id_text varchar(36) null");
        jdbc.execute("update games set public_id_text = bin_to_uuid(substring(public_id, 1, 16))");
        jdbc.execute("alter table games drop column public_id");
        jdbc.execute("alter table games change column public_id_text public_id varchar(36) not null");
        jdbc.execute("alter table games add constraint uk_games_public_id unique (public_id)");
        log.info("Migrated games.public_id successfully");
    }
}
