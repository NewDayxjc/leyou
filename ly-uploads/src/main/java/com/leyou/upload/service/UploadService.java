package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private UploadProperties prop;
    private static final List<String> ALLOW_TYPES= Arrays.asList("image/ipeg","image/png","image/bmp");
    public String uploadImage(MultipartFile file){
        //保存文件到本地
        //准备目标路径
               try {
            //校验文件类型
            String contentType = file.getContentType();
            if(!prop.getAllowTypes().contains(contentType)){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //校验文件的内容,若不是图片，则返回空或抛出异常
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image==null){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //准备目标路径
                   // 上传到FastDFS
                   //第一种写法
//                   String extension=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
                   //第二种写法

                   String extension= StringUtils.substringAfterLast(file.getOriginalFilename(),"");
                   StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
//                   File dest=new File("D:/ProgramFiles/IDE/leyou/upload/",file.getOriginalFilename());

//                   file.transferTo(dest);
            //返回路径
            return  prop.getBaseUrl()+storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
            //上传失败
            log.error("文件上传0上传文件失败",e);
            throw new LyException(ExceptionEnum.UPDATE_FILE_ERROR);

        }

    }

}
