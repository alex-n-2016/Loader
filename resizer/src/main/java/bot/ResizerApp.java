package bot;

import bot.service.Resizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import java.util.List;

@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
@ComponentScan
@Import(RabbitConfig.class)
public class ResizerApp implements CommandLineRunner {

    @Autowired
    Resizer resizer;

    //TODO to refactor
    @Override
    public void run(String... args) {
        final Integer amount;
        if (args != null && args.length>1 && "-n".equals(args[0])){
            amount = Integer.valueOf(args[1]);
        } else {
            amount = null;
        }
        resizer.resizeImages(amount);
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ResizerApp.class, args);
    }

}