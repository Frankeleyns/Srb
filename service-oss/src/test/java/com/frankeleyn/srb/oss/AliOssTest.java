package com.frankeleyn.srb.oss;

import com.frankeleyn.srb.oss.utils.OssProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author Frankeleyn
 * @date 2022/2/11 9:35
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AliOssTest {

    @Test
    public void testProperties() {
        System.out.println(OssProperties.ENDPOINT);
        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

    }

    @Test
    public void testFileName() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString();
        String ext = StringUtils.getFilenameExtension("xxx.png");
        String path = "user" + "/" + date + "/" + uuid + "." + ext;

        String urlPre = "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT + "/";
        System.out.println(urlPre + path);

        String upload = urlPre + path;
        String substring = upload.substring(urlPre.length());
        System.out.println(substring);
    }
}
