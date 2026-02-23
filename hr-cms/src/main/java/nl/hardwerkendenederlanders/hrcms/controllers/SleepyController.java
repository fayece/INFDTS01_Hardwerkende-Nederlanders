package nl.hardwerkendenederlanders.hrcms.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SleepyController {

    @GetMapping("/Sleepyness")
    public String sleepyness(){
        return("very sleepy");
    }
}
