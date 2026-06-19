package nl.hardwerkendenederlanders.hrcms.controllers;

import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.configuration.RequiresPermission;
import nl.hardwerkendenederlanders.hrcms.seeding.databaseSeeder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SeederController {
    databaseSeeder seeder;

    @PostMapping("seed-database")
    @RequiresPermission("admin:manage_users")
    public void seedDatabase() {
        seeder.seed();
    }

    @DeleteMapping("wipe")
    @RequiresPermission("admin:manage_users")
    public void wipeDatabase() {
        seeder.wipe();
    }
}
