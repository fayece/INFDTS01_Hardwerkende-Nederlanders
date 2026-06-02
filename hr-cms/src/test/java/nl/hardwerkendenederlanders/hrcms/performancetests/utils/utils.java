package nl.hardwerkendenederlanders.hrcms.performancetests.utils;

import java.util.Random;

public class utils {

    private static Random rnd = new Random();

    public static int randint(int upperBound) {
        return rnd.nextInt(upperBound);
    }
}
