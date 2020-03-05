package cn.people.cms.modules.user.service;

import cn.people.cms.base.api.Result;
import cn.people.domain.IUser;

public interface IAuthService {

    public IUser login(String userName, String password);
}
