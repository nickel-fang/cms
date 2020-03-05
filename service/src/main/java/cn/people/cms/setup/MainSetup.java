package cn.people.cms.setup;

import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.modules.cms.model.Site;
import cn.people.cms.modules.sys.model.Category;
import cn.people.cms.modules.sys.model.CategoryModel;
import cn.people.cms.modules.sys.model.Log;
import cn.people.cms.modules.user.model.Menu;
import cn.people.cms.modules.user.model.Role;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.util.time.DateFormatUtil;
import cn.people.cms.util.time.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Sqls;
import org.nutz.dao.TableName;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.reflections.Reflections;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

/**
 * 建表
 */
@Component
@Slf4j
public class MainSetup implements InitializingBean {

	@Autowired
	@Qualifier("baseDao")
	private BaseDao dao;

	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	public void afterPropertiesSet() throws Exception {
		Reflections reflections = new Reflections("cn.people.cms.modules");
		Set<Class<?>> singletons = reflections.getTypesAnnotatedWith(Table.class);
		for(Class<?> clazz : singletons){
			if(null == clazz){
				continue;
			}
			if(clazz.equals(Log.class)){
				Date secondYear = DateUtil.nextYear(new Date());
				Date thirdYear = DateUtil.nextYear(secondYear);
				TableName.run(DateFormatUtil.formatDate("yyyy", new Date()), () ->dao.create(clazz, false));
				TableName.run(DateFormatUtil.formatDate("yyyy", secondYear), () ->dao.create(clazz, false));
				TableName.run(DateFormatUtil.formatDate("yyyy", thirdYear), () ->dao.create(clazz, false));
			}else {
				dao.create(clazz, false);
			}
			Daos.migration(dao,clazz,true,false,false);
		}
		if(dao.count(Category.class) == 0){
			execute("classpath:db/sys_category.sql");
		}
		if(dao.count(CategoryModel.class) == 0){
			execute("classpath:db/sys_category_model.sql");
		}
		if(dao.count(Site.class) == 0){
			execute("classpath:db/cms_site.sql");
		}
		if(dao.count(User.class) == 0){
			execute("classpath:db/sys_user.sql");
		}
		if(dao.count(Menu.class) == 0){
			execute("classpath:db/sys_menu.sql");
		}
		if(dao.count(Role.class) == 0){
			execute("classpath:db/sys_role.sql");
		}
		if (dao.count("sys_user_role") == 0) {
			execute("classpath:db/sys_user_role.sql");
		}
		if (dao.count("sys_role_menu") == 0) {
			execute("classpath:db/sys_role_menu.sql");
		}
	}

	private void execute(String path){
		InputStream in = null;
		try {
			in = resourceLoader.getResource(path).getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			String sqlStr = writer.toString();
			Arrays.stream(sqlStr.split(";")).filter(s -> StringUtils.isNotBlank(s)).forEach(s -> {
				Sql sql = Sqls.create(s);
				dao.execute(sql);
			});
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}finally {
			try {
				if(null != in){
					in.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	}
}
