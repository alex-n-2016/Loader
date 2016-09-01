package bot.service;

import bot.AppProps;
import bot.RabbitConfig;
import org.apache.log4j.Logger;
import org.imgscalr.Scalr;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Component
public class ResizerImpl implements Resizer {
    private Logger log = Logger.getLogger(ResizerImpl.class);
    final private static Integer IMAGE_SIZE = 640;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AppProps appProps;

    public void resizeImages(Integer size){
        int i = size != null ? size : -1;

        for (;i != 0; i--) {
            Object obj = rabbitTemplate.receiveAndConvert(RabbitConfig.QUEUE_RESIZE);
            if (obj instanceof String){
                resizeImage((String) obj);
            } else {
                break;
            }
        }
    }

    private void resizeImage(String obj) {
        try {
            Path path = Paths.get(obj);
            BufferedImage bufferedImage = convertImage(path);
            Path destDirPath = Paths.get(appProps.getTempDir(), "images_resized");
            Files.createDirectories(destDirPath);
            Path destPath = Paths.get(destDirPath.toString(),
                    path.getFileName().toString().replaceAll("\\.", "_") + new Date().getTime() + ".jpg");
            Files.deleteIfExists(destPath);
            ImageIO.write(bufferedImage, "jpeg", destPath.toFile());
            rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_UPLOAD, destPath.toRealPath().toString());
            Files.delete(path);
        } catch (IOException e){
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private BufferedImage convertImage(Path srcPath) {
        try {
            BufferedImage initialImage = ImageIO.read(srcPath.toFile());
            BufferedImage scaledImage = Scalr.resize(initialImage, IMAGE_SIZE);
            return getSquaredImage(scaledImage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private BufferedImage getSquaredImage(BufferedImage img) {
        BufferedImage bi = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, img.getType());
        Graphics g = bi.getGraphics();
        g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
        g.setColor(Color.white);
        if (img.getHeight() < IMAGE_SIZE) {
            g.fillRect(0, img.getHeight()+1, IMAGE_SIZE, IMAGE_SIZE - img.getHeight());
        } else if (img.getWidth() < IMAGE_SIZE){
            g.fillRect(img.getWidth() + 1, 0, IMAGE_SIZE - img.getWidth() , IMAGE_SIZE);
        }
        g.dispose();
        return bi;
    }

}
