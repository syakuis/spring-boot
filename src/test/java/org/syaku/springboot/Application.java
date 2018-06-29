package org.syaku.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Seok Kyun. Choi. 최석균 (Syaku)
 * @since 2018. 6. 28.
 */
@SpringBootApplication(scanBasePackages = "org.syaku.springboot")
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
