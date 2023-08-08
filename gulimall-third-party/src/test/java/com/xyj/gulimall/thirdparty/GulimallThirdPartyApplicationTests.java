package com.xyj.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


@SpringBootTest
class GulimallThirdPartyApplicationTests {
	@Autowired
	OSS ossClient;
	@Test
	public void contextLoads() {
		String path = "C:\\Users\\jie\\Pictures\\Saved Pictures\\3.png";
		try {
			ossClient.putObject("yjxiao", "cf3.png", new FileInputStream(new File(path)));
			System.out.println("上传完成!");
		}catch (FileNotFoundException e){
			System.out.println(e.getMessage());
		}
	}

}
