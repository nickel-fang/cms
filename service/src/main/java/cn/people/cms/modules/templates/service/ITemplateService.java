package cn.people.cms.modules.templates.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.templates.VO.TemplateVO;
import cn.people.cms.modules.templates.model.Template;
import org.nutz.dao.QueryResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

/**
 * Created by lml on 2018/1/18.
 */
public interface ITemplateService extends IBaseService<Template> {
    String uploadResource(MultipartFile file,String type,Integer siteId);
    QueryResult findByVO(TemplateVO value);
    String freeMarkerContent(Object map, Template entity, Integer id);
    String freeMarkerContent(Object map, Template entity, Integer id, String type);
    String freeMarkerContent(Object obj, Template entity,String id,String descType);
    Map uploadFiles(MultipartFile[] files,Integer siteId);
    String getUrl(String localDir,Integer id);
    String getHtmlPrefix(String descType,String type);
    Boolean offLine(String type,Integer id);
    void dymicGenerateTemplates(String path, Integer siteId);
    void zipFileHandle(File file,Integer siteId);
    Map uploadFile(MultipartFile file,Integer siteId);
    String getCategoryAddrUri(Integer categoryId);
    String getUrl(String uri);
}
