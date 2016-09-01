package bot;

import bot.service.Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
@ComponentScan
@Import(RabbitConfig.class)
public class UploaderApp implements CommandLineRunner {

    @Autowired
    Uploader uploader;

    @Autowired
    ApplicationArguments arguments;

    @Override
    public void run(String... args) {
        final Integer amount = getAmount(args);
        final boolean isRetry = arguments.containsOption("r");
        uploader.uploadImages(amount, isRetry);
        System.exit(0);
    }

    //TODO to refactor
    private Integer getAmount(String... args) {
        final Integer amount;
        if (args != null && args.length>1 && "-n".equals(args[0])){
            amount = Integer.valueOf(args[1]);
        } else {
            amount = null;
        }
        return amount;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(UploaderApp.class, args);
    }

}