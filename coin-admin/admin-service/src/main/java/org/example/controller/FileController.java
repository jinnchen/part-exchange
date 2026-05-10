package org.example.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.PutObjectResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.model.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@RestController
@Tag(name = "文件上传")
public class FileController {

    @Autowired
    private OSS ossClient;

    @Value("${oss.bucket.name:coin-exchange-imgs}")
    private String bucketName;

    @Value("${alicoud.oss.endpoint:oss-cn}")
    private String endpoint;

    @Operation(description = "文件上传")
    @PostMapping("/image/AliYunImgUpload")
    public R<String> fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = new Date() + file.getOriginalFilename();
        PutObjectResult putObjectResult = ossClient.putObject(bucketName, fileName, file.getInputStream());
        ResponseMessage response = putObjectResult.getResponse();
        String uri = response.getUri();
        String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
        return R.ok(uri);
    }
}
