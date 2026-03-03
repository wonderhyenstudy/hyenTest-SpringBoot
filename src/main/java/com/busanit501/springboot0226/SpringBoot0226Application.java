package com.busanit501.springboot0226;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
// 생성 시간, 수정 시간, 작성자 등을 자동으로 관리해주는 JPA Auditing 기능을 활성화하는 어노테이션
@EnableJpaAuditing
public class SpringBoot0226Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot0226Application.class, args);
    }

}
