package bot;

import java.util.Properties;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;



@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
@ComponentScan
@Import(RabbitConfig.class)
public class StatisticsApp implements CommandLineRunner {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Override
    public void run(String... args) {
        String statistics = "Images\tProcessor Bot\n" +
                "Queue\tCount\n" +
                getQueueStatDesc(RabbitConfig.QUEUE_RESIZE) +
                getQueueStatDesc(RabbitConfig.QUEUE_UPLOAD) +
                getQueueStatDesc(RabbitConfig.QUEUE_DONE) +
                getQueueStatDesc(RabbitConfig.QUEUE_FAILED);
        System.out.print(statistics);
        System.exit(0);
    }

    private String getQueueStatDesc(String queueName) {
        return queueName + "\t" + getQueueCount(queueName) + "\n";
    }

    private String getQueueCount(String queueName) {
        Properties properties = amqpAdmin.getQueueProperties(queueName);
        return properties != null && properties.get("QUEUE_MESSAGE_COUNT") != null ?
                properties.get("QUEUE_MESSAGE_COUNT").toString() : "0";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StatisticsApp.class, args);
    }

}