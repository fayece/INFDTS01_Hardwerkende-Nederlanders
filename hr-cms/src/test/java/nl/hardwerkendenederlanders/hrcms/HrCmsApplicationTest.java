package nl.hardwerkendenederlanders.hrcms;

import org.springframework.boot.SpringApplication;

class HrCmsApplicationTest {

    static void main(String[] args) {
        SpringApplication.from(HrCmsApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
