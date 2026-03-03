package com.busanit501.springboot0226;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
@Log4j2
public class DataSourceTests {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testConnection() throws SQLException {
        // 롬복(Lombok)의 기능
        // 블록이 끝날 때 con.close()를 자동으로 호출하여 DB 연결 자원을 안전하게 해제
        @Cleanup
        Connection con = dataSource.getConnection();
        log.info("연결테스트 확인 : " +con);
        Assertions.assertNotNull(con);
    }
}