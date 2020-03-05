package cn.people.cms.modules.cms.service;

import java.io.InputStream;
import java.util.List;

/**
 * @author  by lml on 2017/12/18.
 */
public interface ITrsFilesImportService {
    /**
     * trs 文件导入
     * @param inputStream
     */
    void  filesImport(InputStream inputStream, String topicName,Integer siteId);
    String getValue(String content, String name);
    String getIniContent(InputStream inputStream, String charsetName);
    List getValueList(String content, String name);
}
