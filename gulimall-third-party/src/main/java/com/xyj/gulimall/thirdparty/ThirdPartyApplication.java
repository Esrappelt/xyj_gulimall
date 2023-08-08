package com.xyj.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ThirdPartyApplication {
	@Autowired
	OSS oss;
	public static void main(String[] args) {
		SpringApplication.run(ThirdPartyApplication.class, args);
	}

}
