package com.ecommerce.inventory_service;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.mysql.MySQLContainer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	void shouldReadInventory() {
		var response = RestAssured.given()
				.queryParam("skuCode", "iphone_15")
				.queryParam("quantity", 1)
				.when()
				.get("/api/inventory")
				.then()
				.log().all()
				.statusCode(200)
				.extract()
				.as(Boolean.class);

		assertTrue(response);

		var negativeResponse = RestAssured.given()
				.queryParam("skuCode", "iphone_15")
				.queryParam("quantity", 1000)
				.when()
				.get("/api/inventory")
				.then()
				.log().all()
				.statusCode(200)
				.extract()
				.as(Boolean.class);

		assertFalse(negativeResponse);
	}
}
