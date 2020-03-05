package cn.people.cms.modules.cms.service.impl;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.model.ArticleData;
import cn.people.cms.modules.cms.model.type.ArticleType;
import cn.people.cms.modules.cms.model.type.SourceType;
import cn.people.cms.modules.cms.model.type.SysCodeType;
import cn.people.cms.modules.cms.service.IArticleService;
import cn.people.cms.modules.cms.service.ITrsFilesImportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author  by lml on 2017/12/18.
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
@Slf4j
public class TrsFilesImportService implements ITrsFilesImportService {

    @Autowired
    private IArticleService articleService;
    @Autowired
    private BaseDao dao;

    @Value("${upload.domain}")
    private String imageDomain;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void  filesImport(InputStream inputStream, String topicName,Integer siteId) {
        String content = getIniContent(inputStream, "GBK");
        if (StringUtils.isNotBlank(content)) {
            String[] articles = content.split("<REC>");
            for (String trs : articles) {
                if (StringUtils.isBlank(trs)) {
                    continue;
                }
                Article article = trsConvertToArticle(trs, null);
                if(StringUtils.isBlank(article.getTitle())){
                    continue;
                }
                List<Article> articleList = dao.query(Article.class, Cnd.where("del_flag", "=", Article.STATUS_SOURCE).and("title", "=", article.getTitle()));
                if (null == articleList || articleList.size() < 1) {
                    articleService.save(article);
                    log.info("文章" + article.getTitle() + "成功");
                }
            }
        }
    }

    @Override
    public String getIniContent(InputStream inputStream,String charsetName){
        StringBuilder result = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,charsetName))){
            char[] arr = new char[1024];
            int read;
            while (true) {
                read = br.read(arr, 0, arr.length);
                if (read < 0) {
                    break;
                }
                result.append(new String(arr, 0, read));
            }
        }catch(IOException e){
            log.error("文件读取错误");
        }
        return result.toString();
    }

    private Article trsConvertToArticle(String trs,Integer categoryId){
        Article article = new Article();
        article.setCategoryId(categoryId);
        article.setIsReference(false);
        article.setSysCode(SysCodeType.ARTICLE.value());
        article.setType(ArticleType.COMMON.value());
        article.setDelFlag(Article.STATUS_SOURCE);
        article.setTitle(getValue(trs, "标题"));
        article.setListTitle(getValue(trs, "显示标题"));
        article.setSource(getValue(trs, "来源"));
        article.setLink(getValue(trs, "链接地址"));
        article.setImportType(SourceType.TRS_IMPORT.value());
        article.setIntroTitle(getValue(trs, "肩标题"));
        article.setSubTitle(getValue(trs, "副标题"));
        article.setKeywords(getValue(trs,"关键词"));
        String recommendation = getValue(trs, "推荐");
        article.setCreateAt(new Date());
        if(StringUtils.isNotBlank(recommendation)){
            try{
                article.setRecommendation(Integer.parseInt(recommendation));
            }catch (Exception ex){
                log.error("推荐数据格式异常");
            }
        }
        article.setImportType(Article.IMPORT_TYPE);
        String imageUlr = getValue(trs, "滚动图片");
        article.setImageUrl(StringUtils.isNotBlank(imageUlr) ? imageDomain+imageUlr : "");
        ArticleData articleData = new ArticleData();
        String content = getValue(trs, "正文");
        content = replaceImg1(content);
        content = replaceImg2(content);
        content = handleContent(content);
        articleData.setContent(content);
        article.setArticleData(articleData);
        return article;
    }

    private static Pattern pattern = Pattern.compile("<[\\u4e00-\\u9fa5]*>=");

    @Override
    public String getValue(String content, String name){
        String regex = "\n<"+name+">=";
        String[] str = content.split(regex);
        if(str.length != 2){
            return "";
        }
        Matcher matcher = pattern.matcher(str[1]);
        if (matcher.find()){
            str[1] = str[1].substring(0, matcher.start());
        }
        return str[1].trim();
    }

    @Override
    public List getValueList(String content, String name){
        List list = new ArrayList();
        String regex = "\n<"+name+">=";
        String[] str = content.split(regex);
        if(str.length < 1){
            return null;
        }
        for(int i=1;i<str.length;i++){
            list.add(str[i].trim());
        }
        return list;
    }

    private static Pattern imgPattern = Pattern.compile("[<](/)?img[^>]*[>]");
    private String replaceImg1(String context){
        Matcher m = imgPattern.matcher(context);
        while(m.find()) {
            String src = m.group();
            String result = src;
            src = src.replaceAll("〖__embimg;\\\\", "\""+imageDomain);
            src = src.replaceAll("__〗", "\"");
            src = "<p style=\"text-align: center;\">" + src + "<br/></p>";
            context = context.replace(result, src);
        }
        return context;
    }

    private  String replaceImg2(String context){
        Matcher m = imgPattern.matcher(context);
        while(m.find()) {
            String src = m.group();
            String result = src;
//            src = src.replaceAll("pic", imageDomain);
            src = "<p style=\"text-align: center;\">" + src + "<br/></p>";
            context = context.replace(result, src);
        }
        return context;
    }

    Pattern contentPattern = Pattern.compile("\n(.*?)\n");
    private String handleContent(String content){
        Matcher matcher = contentPattern.matcher(content);
        while(matcher.find()){
            String src = matcher.group();
            String result = src;
            src = "<p>" + src + "</p>";
            content = content.replace(result, src);
        }

        String result = "";
        if (StringUtils.isNotBlank(content)) {
            String[] contents = content.split("　　");

            for (String temp : contents) {

                if (StringUtils.isBlank(temp)) {
                    continue;
                }
                temp = "<p style=\"text-indent: 2em\">" + temp + "</p>";
                result += temp;
            }
        }


        return result;
    }
}
