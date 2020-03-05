package cn.people.cms.modules.file.config;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.oss.OSSClient;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User: 张新征
 * Date: 2017/2/13 10:01
 * Description:OSSClient配置
 */
@Configuration
public class OSSConfig {

    @Value("${aliyun.oss.key}")
    private String accessKeyId;

    @Value("${aliyun.oss.secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.mnsEndpoint}")
    private String mnsEndpoint;

    @Value("${aliyun.oss.location}")
    private String location;

    @Bean
    public OSSClient ossClientInfo() {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        return ossClient;
    }

    @Bean
    public MNSClient mnsClientInfo() {
        CloudAccount account = new CloudAccount(accessKeyId, accessKeySecret, mnsEndpoint);
        return account.getMNSClient();
    }

    @Bean
    public IAcsClient acsClientInfo() throws Exception{
        DefaultProfile.addEndpoint("cn-beijing", "cn-beijing", "Mts", "mts.cn-beijing.aliyuncs.com");
        IClientProfile profile = DefaultProfile.getProfile(location, accessKeyId, accessKeySecret);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        return acsClient;
    }

}

