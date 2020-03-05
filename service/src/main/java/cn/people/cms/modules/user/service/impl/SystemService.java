package cn.people.cms.modules.user.service.impl;

import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.user.service.ISystemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.people.cms.modules.sys.model.System;

@Service
@Transactional(readOnly = true,rollbackFor = Exception.class)
public class SystemService extends BaseService<System> implements ISystemService {

}