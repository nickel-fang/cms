package cn.people.cms.modules.file.service.impl;

import cn.people.cms.modules.file.model.MediaInfo;
import cn.people.cms.modules.file.service.IUploadService;
import cn.people.cms.util.regex.AudioChecker;
import cn.people.cms.util.regex.ImageChecker;
import cn.people.cms.util.regex.MediaChecker;
import cn.people.cms.util.time.DateFormatUtil;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Description:文件上传服务类
 */
@Service
@Slf4j
public class UploadService implements IUploadService {

    @Value("${aliyun.oss.bucket}")
    private String bucketName;

    @Value("${aliyun.oss.mediaBucket}")
    private String mediaBucket;

    @Value("${aliyun.oss.part.size}")
    private long partSize;

    @Value("${aliyun.oss.directory}")
    private String directory;

    @Value("${aliyun.oss.videoDir}")
    private String videoDir;

    @Value("${aliyun.oss.audioDir}")
    private String audioDir;

    @Value("${aliyun.oss.url}")
    private String url;

    @Value("${aliyun.oss.mediaEndpoint}")
    private String mediaEndpoint;

    @Value("${theone.project.code}")
    private String code;

    @Value("${upload.domain}")
    private String uploadDomain;

    @Value("${upload.useAliyun}")
    private boolean useAliyun;

    @Value("${upload.uploadBasePath}")
    private String uploadBasePath;

    @Autowired
    private OSSClient ossClient;

    @Autowired
    private MediaInfoService mediaInfoService;

    @Override
    public Map<String, Object> upload(String fileType, MultipartFile file, String keyword) {
        Map<String, Object> map = Maps.newHashMap();
        String key = key(fileType);

        if (useAliyun) {
            //1.初始化Multipart Upload
            InitiateMultipartUploadRequest request;
            if (MediaChecker.isMedia(fileType)) {
                request = new InitiateMultipartUploadRequest(mediaBucket, key);
            } else {
                request = new InitiateMultipartUploadRequest(bucketName, key);
            }
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            request.setObjectMetadata(metadata);
            InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
            String uploadId = result.getUploadId();
            log.info("声明一个上传文件的uploadId:" + uploadId + "\n");
            //2.根据上传文件的大小计算分片的数量（阿里云默认分片大小不能超过10000片，若分片大小超过10000则抛异常）
            long fileLength = file.getSize();
            long partCount = fileLength / partSize;
            if (fileLength % partSize != 0) {
                partCount++;
            }
            if (partCount > 10000) {
                throw new RuntimeException("分片的数量不能超过10000");
            }
            List<PartETag> partETags = new ArrayList<>();
            //3.开始上传分片
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                InputStream inputStream = null;
                try {
                    UploadPartRequest uploadPartRequest = new UploadPartRequest();
                    if (MediaChecker.isMedia(fileType)) {
                        uploadPartRequest.setBucketName(mediaBucket);
                    } else {
                        uploadPartRequest.setBucketName(bucketName);
                    }
                    uploadPartRequest.setKey(key);
                    uploadPartRequest.setUploadId(uploadId);
                    inputStream = file.getInputStream();
                    inputStream.skip(startPos);
                    uploadPartRequest.setInputStream(inputStream);
                    uploadPartRequest.setPartSize(curPartSize);
                    uploadPartRequest.setPartNumber(i + 1);//分片号，从1开始
                    UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                    partETags.add(uploadPartResult.getPartETag());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //4.验证分片上传是否完成
            if (partETags.size() != partCount) {
                throw new IllegalStateException("Upload multipart fail due to some parts are not finished yet");
            } else {
                log.info("Succeed to complete multipart into an object named " + key + "\n");
            }
            //5.完成分片上传
            Collections.sort(partETags, Comparator.comparingInt(PartETag::getPartNumber));
            CompleteMultipartUploadRequest completeMultipartUploadRequest;
            if (MediaChecker.isMedia(fileType)) {
                completeMultipartUploadRequest = new CompleteMultipartUploadRequest(mediaBucket, key, uploadId, partETags);
            } else {
                completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, key, uploadId, partETags);
            }
            ossClient.completeMultipartUpload(completeMultipartUploadRequest);

        } else {
            try {
                saveFile(file.getInputStream(), key);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        BufferedImage image;
        if(ImageChecker.isImage(fileType)){
            try {
                image = ImageIO.read(file.getInputStream());
                map.put("height", image.getHeight());
                map.put("width", image.getWidth());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (MediaChecker.isMedia(fileType)) {
            //媒体类型文件将上传结果存到数据库中
            // map.put("status", file.getOriginalFilename() + "文件上传成功，正在转码中");
            //将上传文件的一些信息保存到数据库中
            save(file, key, fileType, keyword);
        } else {
            if (useAliyun) {
                if (!url.endsWith("/")) {
                    url += "/";
                }
                map.put("fileUrl", url + key);
            }else {
                map.put("fileUrl", uploadDomain + key.replaceAll(uploadBasePath,""));
            }
        }

        return map;
    }

    @Transactional
    public void save(MultipartFile file,String key, String fileType, String keyword){
        MediaInfo media = new MediaInfo();
        media.setName(file.getOriginalFilename());//文件原名称
        media.setAutoName(key.split("/")[key.split("/").length - 1]);//文件保存到库中的名称
        media.setUploadTime(new Date());//文件上传成功的时间
        if (useAliyun) {
            if(!mediaEndpoint.endsWith("/")){
                mediaEndpoint += "/";
            }
            media.setStatus(0);//文件状态 0为上传成功 1转码成功
            media.setFileUrl(mediaEndpoint + key);//文件保存的原路径
        }else {
            media.setStatus(1);//文件状态 0为上传成功 1转码成功
            String url = uploadDomain + key.replaceAll(uploadBasePath,"");
            media.setMp3SmallUrl(url);
            media.setFileUrl(url);
            media.setMp3BigUrl(url);
            media.setMp3SmallSize(file.getSize());
            media.setMp3BigSize(file.getSize());
        }
        if(AudioChecker.isAudio(fileType)){//文件类型 音频 audio 视频 video
            media.setType("audio");
        }else {
            media.setType("video");
        }
        media.setSize(file.getSize());//原文件大小
        media.setKeyword(keyword);
        mediaInfoService.save(media);
    }

    //上传文件目录拼接
    private String key(String type) {
        if (!videoDir.endsWith("/")) {
            videoDir += videoDir + "/";
        }
        if (!directory.endsWith("/")) {
            directory += directory + "/";
        }
        if(!audioDir.endsWith("/")){
            audioDir += "/";
        }
        String key;
        if (MediaChecker.isMedia(type)) {
            if(AudioChecker.isAudio(type)){
                key = audioDir + DateFormatUtil.formatDate("yyyyMMdd", new Date()) + "/" + code +UUID.randomUUID() + "." + type;
            }else {
                key = videoDir + DateFormatUtil.formatDate("yyyyMMdd", new Date()) + "/" + code + UUID.randomUUID() + "." + type;
            }
        } else {
            key = directory + DateFormatUtil.formatDate("yyyyMMdd", new Date()) + "/" + code + UUID.randomUUID() + "." + type;
        }
        return key;
    }


    @Override
    public void saveFile(InputStream inputStream, String path,String fileName) {
        if(path == null || fileName == null){
            log.error("文件格式不正确");
            return;
        }
        OutputStream os = null;
        try {
            byte[] bs = new byte[1024];
            int len;
            File tempFile = new File(path+fileName);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            os = new FileOutputStream(tempFile);
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile(InputStream inputStream, String fileName){
        saveFile(inputStream,uploadBasePath,fileName);
    }

    @Override
    public void saveContent(String filePath,String content){
        if(content == null){
            return;
        }
        OutputStream os = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            os = new FileOutputStream(file);
            os.write(content.getBytes("UTF-8"));
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(os !=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
