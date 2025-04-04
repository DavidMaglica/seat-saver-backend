package fipu.diplomski.dmaglica;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {
    public static void main(String[] args) {
        run(Application.class, args);
    }
}
