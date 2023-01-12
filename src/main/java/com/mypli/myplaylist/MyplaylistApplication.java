package com.mypli.myplaylist;

import com.mypli.myplaylist.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MyplaylistApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyplaylistApplication.class, args);
	}

}
