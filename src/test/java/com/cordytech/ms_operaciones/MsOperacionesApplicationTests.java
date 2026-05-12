package com.cordytech.ms_operaciones;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MsOperacionesApplicationTests {

	@Test
	void contextLoads() {
		// Este test verifica que el contexto de Spring cargue correctamente
	}
}
