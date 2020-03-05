package cn.people.cms.modules.user.service.impl;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.user.model.Menu;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.modules.user.service.IAuthService;
import cn.people.cms.modules.user.service.IMenuService;
import cn.people.cms.modules.user.service.ISystemService;
import cn.people.cms.modules.user.service.IUserService;
import cn.people.cms.util.http.OKHttpUtil;
import cn.people.cms.util.json.JsonUtil;
import cn.people.cms.util.mapper.BeanMapper;
import cn.people.domain.ISystem;
import cn.people.domain.IUser;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.nutz.dao.Cnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.people.cms.modules.sys.model.System;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class,readOnly = true)
public class AuthServiceImpl implements IAuthService{


    @Value("${theone.login.url}")
    String url;

    @Value("${theone.login.sysCode}")
    String sysCode;

    @Value("${theone.project.code}")
    private String projectCode;

    @Autowired
    ISystemService systemService;

    @Autowired
    private IMenuService menuService;

    @Autowired
    private IUserService userService;

    @Override
    public IUser login(String userName, String password) {
        try {
            JSONObject json = new JSONObject();
            json.put("username",userName);
            json.put("password",password);
            json.put("sysCode",sysCode);
            String result = OKHttpUtil.httpPost(url + "/auth/login",json.toJSONString());

            Result result1 = JsonUtil.fromJson(result,Result.class);

            System system = systemService.fetch(projectCode);
            ISystem entity = BeanMapper.map(system, ISystem.class);
            IUser user = null;
            try {
                user = JsonUtil.fromJson(JsonUtil.toJson(result1.getData()),IUser.class);
                if(null != system){
                    user.setISystem(entity);
                }
                List<String> permsList = new ArrayList<String>();
                User user1 = BeanMapper.map(user, User.class);
                User oriUser = userService.fetch(user1.getUsername());
                user1.setId(oriUser.getId());
                List<Menu> menus;
                if(user.getId() == 1){
                    menus = menuService.query(null, Cnd.where("del_flag", "=", 0));
                }else {
                    menus = userService.getUserMenus(user1);
                }
                if(null != menus && menus.size() > 0){
                    for(Menu menu : menus){
                        if (StringUtils.isBlank(menu.getPermission())){
                            continue;
                        }
                        permsList.add(menu.getPermission());
                    }
                }
                user.setPermissions(permsList);
                result1.setData(user);
            } catch (Exception e) {
                log.warn("用户未登陆");
            }
            return user;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
