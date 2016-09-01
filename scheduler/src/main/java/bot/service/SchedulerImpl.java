package bot.service;

import bot.RabbitConfig;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;


@Component
public class SchedulerImpl implements Scheduler {
    Logger log = Logger.getLogger(SchedulerImpl.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void addImagesToResize(String dirName) {
        Path dir;
        try {
            dir = Paths.get(dirName).toRealPath();
            DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
            for (Path entry : stream) {
                rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_RESIZE, entry.toString());
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }
}
