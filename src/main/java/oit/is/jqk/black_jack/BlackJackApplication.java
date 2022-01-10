package oit.is.jqk.black_jack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BlackJackApplication {

  public static void main(String[] args) {
    SpringApplication.run(BlackJackApplication.class, args);
  }

}
