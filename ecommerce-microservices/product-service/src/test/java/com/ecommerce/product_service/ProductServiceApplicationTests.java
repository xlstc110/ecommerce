package com.ecommerce.product_service;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.mongodb.MongoDBContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {
	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");
	@LocalServerPort
	private int port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	// static initializer block - 当 JVM 第一次初始化 ProductServiceApplicationTests 类时，
	// 这个 static 代码块会自动执行一次。因此第 26 行的 start() 会自动被调用，启动 MongoDB Testcontainer
	static{
		mongoDBContainer.start();
	}

	@Test
	void shouldCreateProduct() {
		String requestBody = """
				{
				    "image": "images/products/test.jpg",
				    "name": "Test Product",
				    "rating": {"stars": 4.5, "count": 10},
				    "priceCents": 1200,
				    "keywords": ["test"]
				}
				""";

		RestAssured.given()
				.contentType(ContentType.JSON)
				.body(requestBody)
				.when()
				.post("/api/products")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Test Product"))
				.body("priceCents", Matchers.equalTo(1200));
	}

	@Test
	void shouldSearchSeededProductsByKeyword() {
		RestAssured.given()
				.queryParam("search", "basketball")
				.when()
				.get("/api/products")
				.then()
				.statusCode(200)
				.body("name", Matchers.hasItem("Intermediate Size Basketball"));
	}
}
