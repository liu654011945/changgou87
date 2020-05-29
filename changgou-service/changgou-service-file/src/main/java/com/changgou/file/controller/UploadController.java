package com.changgou.file.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.file.util.FastDFSClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou.file.controller
 * @version 1.0
 * @date 2020/5/6
 */
@RestController

public class UploadController {

    /**
     *
     * 请求:  /uplod
     * 参数： 文件本身  multipartFile
     * 返回值：图片的路径本身
     */

    /**
     * @return
     */
    @PostMapping(value = "/upload")
    public String upload(MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                byte[] bytes = file.getBytes();//strore somewhere  存储到fastdfs即可
                //参数1 指定要元文件的文件名  12345.jpg
                //参数2 指定文件的字节数组
                //参数3 指定文件的扩展名
                FastDFSFile fastDFSFile = new FastDFSFile(file.getOriginalFilename(),
                        bytes,
                        StringUtils.getFilenameExtension(file.getOriginalFilename()));// getFilenameExtension 就是获取扩展名的

                // upload[0]   group1
                // upload[1]   M00/00/00/wKjThF6ycJeAW-ZiAACAThdn_1U074.jpg
                String[] upload = FastDFSClient.upload(fastDFSFile);//上传图片到fastdfs上   返回的就是路径

                // 真正的路径 http://192.168.211.132:8080/group1/M00/00/00/wKjThF6yZjOABCvFAACAThdn_1U976.jpg
                return "http://192.168.211.132:8080/" + upload[0] + "/" + upload[1];
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
}
