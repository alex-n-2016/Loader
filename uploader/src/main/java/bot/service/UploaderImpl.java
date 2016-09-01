package bot.service;

import bot.AppProps;
import bot.RabbitConfig;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class UploaderImpl implements Uploader {

    @Autowired
    private AppProps appProps;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void uploadImages(Integer size, boolean isRetry){
        String queueName = isRetry ? RabbitConfig.QUEUE_FAILED : RabbitConfig.QUEUE_UPLOAD;
        int i = size != null ? size : -1;
        for (;i != 0; i--) {
            Object obj = rabbitTemplate.receiveAndConvert(queueName);
            if (obj instanceof String){
                uploadImage((String) obj);
            } else {
                break;
            }
        }
    }

    private void uploadImage(String obj) {
        Path path = null;
        try {
            path = Paths.get(obj);
            uploadFile(path);
            rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_DONE, path.toString());
        } catch (Exception e){
            if (path != null) {
                rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_FAILED, path.toString());
            }
        }
    }


    private void uploadFile(Path path) throws DbxException, IOException {
        DbxRequestConfig config = new DbxRequestConfig(appProps.getIdToken());
        DbxClientV2 client = new DbxClientV2(config, appProps.getAuthToken());

        try (InputStream in = new FileInputStream(path.toFile())) {
            String pathStr = path.toRealPath().toString();
            client.files().uploadBuilder(pathStr).uploadAndFinish(in);
        }
    }
}
