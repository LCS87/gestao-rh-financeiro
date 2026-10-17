package br.com.callcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestaoRhFinanceiroApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestaoRhFinanceiroApplication.class, args);
    }

}
