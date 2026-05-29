package nl.hardwerkendenederlanders.hrcms.controllers;

import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.seeding.databaseSeeder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SeederController {
     databaseSeeder seeder;

     @PostMapping("seed-database")
    public void seedDatabase(){
        seeder.seed2();
    }

    @DeleteMapping("wipe")
    public void wipeDatabase(){
         seeder.wipe();
    }
}
