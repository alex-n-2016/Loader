package bot;

import bot.service.Scheduler;
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
public class SchedulerApp implements CommandLineRunner {

    @Autowired
    Scheduler scheduler;

    @Autowired
    ApplicationArguments arguments;

    @Override
    public void run(String... args) {
        final String dir;
        if (args != null && args.length>0){
            dir = args[0];
            scheduler.addImagesToResize(dir);
        }
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SchedulerApp.class, args);
    }

}