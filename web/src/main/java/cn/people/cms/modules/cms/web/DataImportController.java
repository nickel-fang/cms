package cn.people.cms.modules.cms.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.cms.service.ITrsFilesImportService;
import cn.people.cms.modules.templates.service.ITemplateService;
import cn.people.cms.util.regex.ImageChecker;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author  by lml on 2017/12/18.
 */
@Api(description = "trs批量导入")
@RestController
@RequestMapping("/auth/import")
@Slf4j
public class DataImportController {

    @Autowired
    private ITrsFilesImportService filesImportService;
    @Autowired
    private ITemplateService templateService;

    @Value("${upload.uploadBasePath}")
    private String uploadBasePath;

    @PostMapping
    public Result trsImport(@RequestParam("file") MultipartFile file,@RequestParam Integer siteId) throws IOException {
        if(null == siteId){
            return Result.error("导入站点编号有误");
        }
        if(!file.getOriginalFilename().endsWith("zip")){
            return Result.error("只支持zip文件上传");
        }
        ZipInputStream zis;
        zis = new ZipInputStream(file.getInputStream(), Charset.forName("GBK"));
        ZipEntry entry;
        String topicName = null;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            String fileName = entry.getName();
            //文件类型检查
            if(fileName.contains("zip")){
                continue;
            }
            String[] names = fileName.split("\\.");
            if (names.length <= 1) {
                continue;
            }
            String fileType = names[names.length - 1];
            if (ImageChecker.isImage(fileType) || fileName.endsWith("trs")) {
                String[] fileNames = fileName.split("/");
                int n;
                BufferedOutputStream outputstream;
                if (ImageChecker.isImage(fileType)) {
                    outputstream = new BufferedOutputStream(new FileOutputStream(uploadBasePath +"/picture/"+ fileNames[fileNames.length - 1]));
                } else {
                    outputstream = new BufferedOutputStream(new FileOutputStream(uploadBasePath + fileNames[fileNames.length - 1]));
                }
                byte[] buf = new byte[1024];
                while ((n = zis.read(buf, 0, 1024)) > -1) {
                    outputstream.write(buf, 0, n);
                }
                outputstream.flush();
                log.info("写入文件"+fileName+"成功");
                InputStream dirStream = null;
                InputStream dataStream;
                try {
                    if(fileName.endsWith("trs")){
                        dataStream = new FileInputStream(uploadBasePath+fileNames[fileNames.length-1]);
                        filesImportService.filesImport(dataStream, topicName,siteId);
                    }
                }catch (Exception ex){
                    log.error(ex.getMessage());
                    return Result.error(-2,"数据格式不正确");
                }finally {
                    if(dirStream!=null){
                        dirStream.close();
                    }
                }
                zis.closeEntry();
            }
        }
        return Result.success("trs文件导入成功");
    }

    @PostMapping(value = "/template")
    public Result tmpImport(@RequestParam("file") MultipartFile file,@RequestParam Integer siteId) throws IOException {
        if(null == siteId){
            return Result.error("导入站点编号有误");
        }
        if(!file.getOriginalFilename().endsWith("zip")){
            return Result.error("只支持zip文件上传");
        }
        String[] zipNames = file.getOriginalFilename().split("\\.");
        ZipInputStream zis;
        zis = new ZipInputStream(file.getInputStream(),Charset.forName("GBK"));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String name = entry.getName();
            File dir = new File(uploadBasePath+name);
            if (name.endsWith("/")) {
                dir.mkdirs();
                continue;
            }
            FileOutputStream fos = new FileOutputStream(uploadBasePath+name);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = zis.read(bytes)) >= 0) {
                fos.write(bytes, 0, length);
            }
            fos.close();
        }
        zis.closeEntry();
        zis.close();
        try {
            templateService.dymicGenerateTemplates(uploadBasePath+zipNames[0],siteId);
            return Result.success("模板导入成功");
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("模板导入失败");
        }

    }
}
