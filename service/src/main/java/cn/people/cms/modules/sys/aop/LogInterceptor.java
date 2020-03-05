package cn.people.cms.modules.sys.aop;

import cn.people.cms.modules.sys.model.Log;
import cn.people.cms.modules.sys.service.ILogService;
import cn.people.cms.util.base.UserUtil;
import cn.people.cms.util.mapper.JsonMapper;
import cn.people.cms.util.text.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

/**
 * User: 张新征
 * Date: 2017/4/11 10:28
 * Description:
 */
@Aspect
@Component
public class LogInterceptor {
	@Autowired
	private ILogService logService;
	JsonMapper jsonMapper = JsonMapper.defaultMapper();

	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void webLog() {
	}

	@Around("webLog()")
	public Object logInterceptor(ProceedingJoinPoint point) throws Throwable{
		// 接收到请求，记录请求内容
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		// 记录下请求内容
		Log log = new Log();
		log.setCreateDate(new Date());
		if(null != UserUtil.getUser()){
			log.setCreateBy(UserUtil.getUser().getId());
		} else{
			return point.proceed();
		}
		log.setMethod(request.getMethod());
		if("GET".equals(request.getMethod())){
			log.setParams(setParams(request.getParameterMap()));
		}else {
			Object[] args = point.getArgs();
			if(null != args && args.length > 0){
				if(args[0] instanceof MultipartFile){
					MultipartFile file = (MultipartFile)args[0];
					log.setParams(file.getOriginalFilename());
				}else{
					//判断如果不是文件请求，Ueditor的文件请求会引起序列化出错
					if(null != args[0] && !StandardMultipartHttpServletRequest.class.isInstance(args[0])){
						log.setParams(jsonMapper.toJson(args[0]));
					}
				}

			}
		}
		log.setRemoteAddr(request.getHeader("x-real-ip"));
		log.setRequestUri(request.getRequestURI());
		log.setType("1");
		log.setUserAgent(request.getHeader("user-agent"));
		Object result;
		try {
			result = point.proceed();
		} catch (Throwable t) {
			log.setType("2");
			StringWriter stringWriter = new StringWriter();
			t.printStackTrace(new PrintWriter(stringWriter));
			log.setException(stringWriter.toString());
			throw t;
		}finally {
			logService.insert(log);
		}
		return result;
	}

	/**
	 * 设置请求参数
	 */
	private String setParams(Map paramMap){
		if (paramMap == null){
			return "";
		}
		StringBuilder params = new StringBuilder();
		for (Map.Entry<String, String[]> param : ((Map<String, String[]>)paramMap).entrySet()){
			params.append(("".equals(params.toString()) ? "" : "&") + param.getKey() + "=");
			String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
			params.append(StringUtils.abbr(StringUtils.endsWithIgnoreCase(param.getKey(), "password") ? "" : paramValue, 100));
		}
		return params.toString();
	}

}
