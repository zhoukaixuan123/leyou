package com.leyou.upload.Service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.confg.UploadProperties;
import com.leyou.upload.controller.UploadController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/*
 *功能描述
 * @author zhoukx
 * @date 2019/4/20$
 * @description $
 */
@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);


    // FastDFS 的客户端
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private  UploadProperties prop;
   // private static final List<String> ALLOW_TYPES = Arrays.asList("image/jpeg", "image/png", "image/dmp");


    public String upload(MultipartFile file) {
        try {
            // 1、图片信息校验
            // 1)校验文件类型   类型
            String type = file.getContentType();
            if (!prop.getAllowTypes().contains(type)) {
                logger.info("上传失败，文件类型不匹配：{}", type);
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            // 2)校验图片内容    如果不是图片  则为空
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                logger.info("上传失败，文件内容不符合要求");
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //上传FastDFS
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);

            return prop.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
            logger.error("[文件上传 ]上传失败！", e);
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);

        }
    }
}
