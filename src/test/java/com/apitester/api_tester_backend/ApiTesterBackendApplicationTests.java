package com.apitester.api_tester_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "JWT_SECRET=MyVeryLongSecretKeyForJwtAuthentication123456"
})
class ApiTesterBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
