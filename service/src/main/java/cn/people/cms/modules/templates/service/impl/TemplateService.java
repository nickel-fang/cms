package cn.people.cms.modules.templates.service.impl;

import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.block.model.Block;
import cn.people.cms.modules.cms.model.Site;
import cn.people.cms.modules.cms.service.ITrsFilesImportService;
import cn.people.cms.modules.file.service.IUploadService;
import cn.people.cms.modules.sys.service.ICategoryService;
import cn.people.cms.modules.templates.VO.TemplateVO;
import cn.people.cms.modules.templates.model.Template;
import cn.people.cms.modules.templates.model.TemplateView;
import cn.people.cms.modules.templates.service.ITemplateService;
import cn.people.cms.util.mapper.JsonMapper;
import cn.people.cms.util.regex.ImageChecker;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * Created by lml on 2018/1/18.
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class,readOnly = true)
public class TemplateService extends BaseService<Template> implements ITemplateService {

    private static final String HTML_SUFFIX = ".html";
    private static final String FTL_SUFFIX= ".ftl";
    public static final String SITES="sites";
    public static final String INDEX="index";
    public static final String CATEGORY="category";

    @Autowired
    private IUploadService uploadService;
    @Autowired
    private ITrsFilesImportService filesImportService;
    @Autowired
    private ICategoryService categoryService;
    @Resource
    private Configuration config;
    @Value("${theone.freemarker.templates.folder}")
    private String storagePrefix;
    @Value("${theone.project.accessDomain}")
    private String accessDomain;
    @Value("${theone.freemarker.upload.size}")
    private Long uploadSize;
    private JsonMapper jsonMapper = new JsonMapper();

    @Override
    @Transactional
    public Object save(Template template) {
        if(!Lang.isEmpty(template.getBlocks())){
            template.getBlocks().forEach(block -> {
                if(block.getTag() == null){
                    block.setTag(Block.TAG+getRandomStr());
                }
            });
        }
        if(null !=template.getId()){
            template.setUpdateAt(new Date());
            Template org = dao.fetch(Template.class,template.getId());
            if(org == null){
                return null;
            }
            if(!Lang.isEmpty(template.getBlocks())){
                template.getBlocks().forEach(block -> {
                    if(block.getId() == null){
                        block.setTemplateId(template.getId());
                        dao.insert(block);
                    }else {
                        block.setTemplateId(template.getId());
                        dao.updateIgnoreNull(block);
                    }
                });
            }
            super.save(template);
        }else {
            template.setResourcePrefix(appendPath(SITES,template.getSiteId().toString()));
            template.setDelFlag(Template.STATUS_ONLINE);
            dao.insertWith(template,Template.BLOCKS);
        }
        //动态拼接资源路径
        //带文件名称的ftl完整上传路径 /data/sites/slug/1.ftl
        Site site = dao.fetch(Site.class,template.getSiteId());
        String path;
        if(site == null){
            path = appendPath(storagePrefix);
        }else {
            path = appendPath(storagePrefix,site.getSlug());
        }
        if(!StringUtils.isBlank(path)){
            String ftlUploadPath =path.concat(template.getId().toString()).concat(FTL_SUFFIX);
            template.setFtlPath(ftlUploadPath);
            uploadService.saveContent(ftlUploadPath ,template.getContent());
        }
        dao.updateIgnoreNull(template);
        return template;
    }

    @Override
    public Template fetch(Integer id) {
        Template template = super.fetch(id);
        if(template == null){
            return null;
        }
        return dao.fetchLinks(template,Template.BLOCKS);
    }

    @Override
    public String uploadResource(MultipartFile file,String type,Integer siteId){
        String fileName = file.getOriginalFilename();
        String path = appendPath(storagePrefix,siteId.toString(),type);
        try {
            uploadService.saveFile(file.getInputStream(),path,fileName);
        } catch (IOException e) {
            log.error(fileName+"获取输入流失败",e);
        }
        int index = path.indexOf(SITES);
        if(index<0){
            return null;
        }
        //ip:/templates/siteId/css/nameuuid.css
       // String domain = accessDomain.endsWith("/")?accessDomain:accessDomain.concat("/");
       // return domain.concat(path.substring(index,path.length())).concat(fileName);
        return path.substring(index,path.length()).concat(fileName);
    }

    @Override
    public QueryResult findByVO(TemplateVO value) {
        Cnd cnd = Cnd.where(Template.FIELD_STATUS,"=",Template.STATUS_ONLINE);
        if(null  != value.getSiteId()){
            cnd.and(Template.SITE_ID,"=",value.getSiteId());
        }
        if(StringUtils.isNotBlank(value.getType())){
            cnd.and(Template.TYPE,"=",value.getType());
        }
        if(StringUtils.isNotBlank(value.getName())){
            cnd.and(Template.NAME,"like","%"+value.getName()+"%");
        }
       return listPage(Template.class,value.getPageNumber(),value.getPageSize(),cnd.desc(Template.SORT));
    }

    /**
     * 根据模板名称、编号和模块名称生成静态HTML存储在本地磁盘
     */
    @Override
    public String freeMarkerContent(Object map, Template entity,Integer id){
        return freeMarkerContent(map, entity,id.toString(),null);
    }

    /**
     * 根据模板名称、编号、模块名称、模板类型生成静态HTML存储在本地磁盘
     */
    @Override
    public String freeMarkerContent(Object obj, Template entity,Integer id,String descType){
        return freeMarkerContent(obj,entity,id.toString(),descType);
    }

    /**
     * 根据模板名称、编号、模块名称、模板类型生成静态HTML存储在本地磁盘
     */
    @Override
    public String freeMarkerContent(Object obj, Template entity,String id,String descType){
        Writer file = null;
        String htmlPath = null;
        String localDir;
        try {
            if(entity == null){
                return null;
            }
            String ftlPath = entity.getFtlPath();
            if(StringUtils.isBlank(ftlPath)){
                log.info("ftlPath is empty");
                return null;
            }
            freemarker.template.Template temp = getFreemarkerTemplate(ftlPath);
            if(temp == null){
                log.error("获取模板引擎失败,ftlPath:{}",ftlPath);
                return null;
            }
            //静态文件dir
            Site site = dao.fetch(Site.class,entity.getSiteId());
            if(site == null){
                return null;
            }
            localDir = getLocalDir(id,site.getSlug(),descType,entity.getType());
            if(localDir == null){
                log.error("-----localDir为空,type={},id={}------",entity.getType(),id);
                return null;
            }
            log.info("----------localDir-------:{}",localDir);
            File folder = new File(localDir);
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    log.info("创建目录{}成功",localDir);
                }
            }
            htmlPath = getHtmlPath(id,localDir,entity.getType());
            file = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(htmlPath))));
            TemplateView view = new TemplateView(obj,entity.getResourcePrefix());
            temp.process(view, file);
            file.flush();
            log.info("template map======{}",jsonMapper.toJson(view));
            log.info("生成静态html成功,路径:{}",htmlPath);
        } catch (Exception e) {
            log.error("生成静态html失败", e);
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    log.error("文件流关闭失败", e);
                }
            }
            return getUri(htmlPath);
        }
    }

    /**
     * 根据模板的ftlPath 获取模板引擎对象
     * @param ftlPath
     * @return
     */
    private freemarker.template.Template getFreemarkerTemplate(String ftlPath){
        if(null == ftlPath){
            return null;
        }
        int index = ftlPath.lastIndexOf("/");
        if(index<0){
            log.info("Incomplete ftlPath information:{}",ftlPath);
            return null;
        }
        try {
            String dir = ftlPath.substring(0,index);
            String fileName = ftlPath.substring(index+1,ftlPath.length());
            config.setDirectoryForTemplateLoading(new File(dir));
            return config.getTemplate(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getHtmlPrefix(String descType,String type){
        return appendPath(descType,type);
    }

    /**
     * //静态文件路径 详情：/date/sites/slug/descType/detail/id.html
      //频道  /date//sites/slug/descType/category/id/index.html
      //站点  /date/sites/slug/descType/index.html
     * @param id
     * @param slug
     * @param descType
     * @param type
     * @return
     */
    private String getLocalDir(String id,String slug,String descType,String type){
       String localDir;
       Boolean isCategory = CATEGORY.equals(type);
       Boolean isSite =categoryService.isSiteRoot(Integer.parseInt(id)) ;
       if(isSite){
           localDir = appendPath(storagePrefix,slug,descType);
       }else if(isCategory){
           localDir = appendPath(storagePrefix,slug,getHtmlPrefix(descType,type),id);
       }else {
           localDir = appendPath(storagePrefix,slug,getHtmlPrefix(descType,type));
       }
        return localDir;
    }

    /**
     * //静态文件路径 详情：/sites/slug/descType/detail/id.html
      //频道 /sites/slug/descType/category/id/index.html
      //频道 /sites/slug/index.html
     * @param id
     * @param localDir
     * @param type
     * @return
     */
    private String getHtmlPath(String id,String localDir,String type){
        String htmlPath;
        Boolean isCategory = CATEGORY.equals(type);
        Boolean isSite =categoryService.isSiteRoot(Integer.parseInt(id)) ;
        if(isCategory || isSite){
            htmlPath =localDir.concat(INDEX).concat(HTML_SUFFIX);
        }else {
            htmlPath =localDir.concat(id).concat(HTML_SUFFIX);
        }
        return htmlPath;
    }

    @Override
    public String getUrl(String localDir,Integer id){

        String htmlName = id+HTML_SUFFIX;
        String htmlPath =localDir.concat(htmlName);
        int locate = htmlPath.indexOf(SITES);
        if(locate<0 ){
            return null;
        }
        return getDomain().concat(htmlPath.substring(locate,htmlPath.length()));
    }

    @Override
    public String getCategoryAddrUri(Integer categoryId){
        return appendPath(String.valueOf(categoryId),INDEX);
    }

    /**
     * 模板资源文件
     * @param files
     * @return 返回服务器中存储的访问路径
     */
    @Override
    public Map uploadFiles(MultipartFile[] files,Integer siteId){
        if(null == files || files.length==0){
            return null;
        }
        Map map = new LinkedHashMap();
        for (MultipartFile file : files) {
            uploadFileWithInfo(file,map,siteId);
        }
        return map;
    }

    /**
     * 模板资源文件
     * @param file
     * @return 返回服务器中存储的访问路径
     */
    @Override
    public Map uploadFile(MultipartFile file,Integer siteId){
        if(file == null){
            return null;
        }
        Map map = new LinkedHashMap();
        uploadFileWithInfo(file,map,siteId);
        return map;
    }

    private void uploadFileWithInfo(MultipartFile file,Map map,Integer siteId){
        if(map == null){
            return;
        }
        //文件类型检查
        String fileName = file.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            return;
        }
        //文件大小检查
        if(file.getSize() > uploadSize){
            map.put(fileName,"上传文件失败，大小超过"+uploadSize);
            return;
        }
        String[] names = fileName.split("\\.");
        if (names.length <= 1) {
            return;
        }
        String fileType = names[names.length - 1].toLowerCase();
        String resourceType =fileType ;
        if(ImageChecker.isImage(fileType)){
            resourceType = "image";
        }else if (!("css".equals(fileType) || "js".equals(fileType))){
            map.put(fileName,"上传文件失败，格式不支持");
            return;
        }
        try {
            String accessPath = uploadResource(file,resourceType,siteId);
            if(accessPath != null){
                map.put(fileName,accessPath);
            }
        } catch (Exception e) {
            log.error("资源文件上传失败", e);
        }
    }

    /**
     * 拼接上传文件的路径
     * @param paths
     * @return
     */
    private String appendPath(String... paths){
        if(paths !=null && paths.length > 0){
            StringBuilder stringBuilder = new StringBuilder();
            for (String path : paths) {
                if(path == null || path.length()<1){
                    continue;
                }
                stringBuilder.append(path).append("/");
            }
            return stringBuilder.toString().replaceAll("//","/");
        }
        return null;
    }

    private String getDomain(){
        return accessDomain.endsWith("/")?accessDomain:accessDomain.concat("/");
    }

    @Override
    public String getUrl(String uri){
        if(uri == null){
            return null;
        }
        String url;
        if(uri.startsWith("/")){
            url = getDomain().concat(uri.substring(1,uri.length()));
        }else {
            url = getDomain().concat(uri);
        }
        return url;
    }

    private String getUri(String htmlPath){
        if(htmlPath == null){
            return null;
        }
        int locate = htmlPath.indexOf(SITES);
        if(locate<0 ){
            return null;
        }
        return htmlPath.substring(locate,htmlPath.length());
    }



    private String getRandomStr(){
        StringBuilder str =new StringBuilder();
        for (int i = 0; i < 12; i++) {
            Random rand = new Random();
            str.append((char) (rand.nextInt(26)+65));
        }
        return str.toString();
    }

    @Override
    public Boolean offLine(String type,Integer id){
        String path = appendPath(storagePrefix,type)+id+HTML_SUFFIX;
        File file = new File(path);
        if(file.exists()){
            return file.delete();
        }else {
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dymicGenerateTemplates(String path, Integer siteId) {
        File parentFile = new File(path);
        if(parentFile.isDirectory()){
            File[] fileList = parentFile.listFiles();
            for(File file:fileList ){
                if(!file.isDirectory()){
                    zipFileHandle(file,siteId);
                    continue;
                }
                //非区块模板
                dirToTemplate(file,siteId);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private void dirToTemplate(File parentFile, Integer siteId){
        File[] fileList = parentFile.listFiles();
        Map map;
        Template template = new Template();
        List<MultipartFile> list = new ArrayList();
        for(File file:fileList ){
            if(file.isDirectory()){
                continue;
            }
            template.setSiteId(siteId);
            if(file.getName().endsWith("ini") || file.getName().endsWith("ftl")){
                try (InputStream inputStream = new FileInputStream(file)){
                    if(file.getName().endsWith("ini")){
                        iniToTemplateInfo(template,inputStream);
                    }else {
                        //ftl 内容
                        template.setContent(filesImportService.getIniContent(inputStream,"UTF-8"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                //js css img等处理
                MultipartFile multipartFile = convertFileToMultipartFile(file);
                if(multipartFile!=null){
                    list.add(multipartFile);
                }
            }
        }
        MultipartFile[] multipartFiles = new MultipartFile[list.size()];
        list.toArray(multipartFiles);
        map = uploadFiles(multipartFiles,siteId);

        template.setResourceJson(jsonMapper.toJson(map));
        save(template);
    }

    private void iniToTemplateInfo(Template template,InputStream inputStream){
        String content = filesImportService.getIniContent(inputStream,"UTF-8");
        if(StringUtils.isBlank(content)){
            return;
        }

        template.setType(filesImportService.getValue(content, "模版类型"));
        template.setName(filesImportService.getValue(content, "模版名称"));
        template.setDescription(filesImportService.getValue(content,"描述"));
        List<String> list = filesImportService.getValueList(content,"区块");
        if(!Lang.isEmpty(list)){
            List<Block>blocks = new ArrayList<>();
            list.forEach(str->{
                Block block = new Block();
                block.setName(str);
                block.setSort(0);
                blocks.add(block);
            });
            template.setBlocks(blocks);
        }
    }

    @Override
    public void zipFileHandle(File file,Integer siteId){
        uploadFile(convertFileToMultipartFile(file),siteId);
    }

    private MultipartFile convertFileToMultipartFile(File file){
        FileInputStream input;
        try {
            input = new FileInputStream(file);
            return new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
