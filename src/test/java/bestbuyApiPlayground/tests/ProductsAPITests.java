package bestbuyApiPlayground.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import bestbuyApiPlayground.utils.FileUtilities;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ProductsAPITests {
	private static final Logger logger = LoggerFactory.getLogger(ProductsAPITests.class);
	FileUtilities fileUtilities;

	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = "http://localhost/";
		RestAssured.port = 3030;
		fileUtilities = new FileUtilities();
	}

	@Test
	public void verifyGetProducts() {
		logger.info("Executing verifyGetProducts..", true);
		Response response = RestAssured.given().when().get("/products").then().assertThat().statusCode(200).body("total", greaterThan(0))
				.body("data", notNullValue()).body("data[0].id", notNullValue()).body("data[0].name", notNullValue())
				.body("data[0].type", notNullValue()).body("data[0].price", notNullValue())
				.body("data[0].shipping", notNullValue()).body("data[0].description", notNullValue())
				.body("data[0].manufacturer", notNullValue()).body("data[0].model", notNullValue()).extract().response();
		logger.debug(response.asPrettyString());
	}

	@Test
	public void verifyGetProductsWithLimit() throws JsonMappingException, JsonProcessingException {
		logger.info("Executing verifyGetProductsWithLimit..", true);
		Response response = RestAssured.given().when().queryParam("$limit", 5).get("/products").then().assertThat()
				.body("total", greaterThan(0)).body("limit", equalTo(5)).body("data", notNullValue())
				.body("data[0].id", notNullValue()).body("data[0].name", notNullValue())
				.body("data[0].type", notNullValue()).body("data[0].price", notNullValue())
				.body("data[0].shipping", notNullValue()).body("data[0].description", notNullValue())
				.body("data[0].manufacturer", notNullValue()).body("data[0].model", notNullValue()).extract().response();
		logger.debug(response.asPrettyString());
	}

	@Test
	public void verifyGetProductsWithSpecificId() {
		logger.info("Executing verifyGetProductsWithSpecificId..", true);
		Response response = RestAssured.given().when().param("id", 185230).get("/products").then().assertThat().body("total", equalTo(1))
				.body("data", notNullValue()).body("data[0].id", notNullValue()).body("data[0].id", equalTo(185230))
				.body("data[0].name", notNullValue()).body("data[0].type", notNullValue())
				.body("data[0].price", notNullValue()).body("data[0].shipping", notNullValue())
				.body("data[0].description", notNullValue()).body("data[0].manufacturer", notNullValue())
				.body("data[0].model", notNullValue()).extract().response();
		logger.debug(response.asPrettyString());
	}

	@Test
	public void testValidProduct() {
		logger.info("Executing testValidProduct..", true);
		Response response = RestAssured.given().when().get("/products/185230").then().assertThat().statusCode(200)
				.body("id", notNullValue()).body("id", equalTo(185230)).body("name", notNullValue())
				.body("type", notNullValue()).body("price", notNullValue()).body("upc", notNullValue())
				.body("shipping", notNullValue()).body(matchesJsonSchemaInClasspath("schemas/products-schema.json"))
				.extract().response();
		logger.debug(response.asPrettyString());
	}

	@Test
	public void testInvalidProduct() {
		logger.info("Executing testInvalidProduct..", true);
		Response response = RestAssured.given().when().get("/products/999999").then().extract().response();
		assertNotNull(response.getBody());
		logger.debug(response.asPrettyString());
		assertEquals(response.getStatusCode(), 404);
	}

	@Test
	public void verifyPostProducts() {
		logger.info("Executing verifyPostProducts..", true);
		StringBuilder jsonString = new StringBuilder();
		try {
			jsonString.append(fileUtilities.readFile("src/test/resources/product.json"));
			RestAssured.given().when().contentType(ContentType.JSON).body(jsonString.toString()).post("/products")
					.then().assertThat().body(matchesJsonSchemaInClasspath("schemas/products-schema.json"))
					.statusCode(201).body("id", notNullValue()).body("name", notNullValue())
					.body("name", equalTo("Samsung Galaxy")).body("type", equalTo("mobile")).body("price", equalTo(200))
					.body("upc", equalTo("mobile")).body("shipping", equalTo(50))
					.body("description", equalTo("Latest Samsung phone")).body("manufacturer", equalTo("Samsung"))
					.body("model", equalTo("Galaxy S25")).log().all();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	@Test
	public void verifyPostProductsWithPayloadAsObject() {
		logger.info("Executing verifyPostProductsWithPayloadAsObject..", true);
		Map<String, Object> requestPayload = new HashMap<String, Object>();
		requestPayload.put("name", "iPhone 17");
		requestPayload.put("type", "mobile");
		requestPayload.put("price", 800);
		requestPayload.put("shipping", 30);
		requestPayload.put("upc", "mobile");
		requestPayload.put("description", "dual camera iphone");
		requestPayload.put("manufacturer", "Apple");
		requestPayload.put("model", "iPhone 17");

		Response response = RestAssured.given().when().contentType(ContentType.JSON).body(requestPayload).post("/products").then()
				.assertThat().statusCode(201).extract().response();
		logger.debug(response.asPrettyString());

	}

	@Test
	public void verifyPostProductsWithInvalidPayload() {
		logger.info("Executing verifyPostProductsWithInvalidPayload..", true);
		Response response = RestAssured.given().when().contentType(ContentType.TEXT).body("invalid payload").post("/products").then()
				.assertThat().statusCode(400).extract().response();
		logger.debug(response.asPrettyString());
	}

	@Test
	public void verifyPutProductsWithPayloadAsObject() {
		logger.info("Executing verifyPutProductsWithPayloadAsObject..", true);
		Map<String, Object> requestPayload = new HashMap<String, Object>();
		requestPayload.put("name", "iPhone 17");
		requestPayload.put("type", "mobile");
		requestPayload.put("price", 800);
		requestPayload.put("shipping", 30);
		requestPayload.put("upc", "mobile");
		requestPayload.put("description", "dual camera iphone");
		requestPayload.put("manufacturer", "Apple");
		requestPayload.put("model", "iPhone 17");

		int id = RestAssured.given().when().contentType(ContentType.JSON).body(requestPayload).post("/products").then()
				.log().all().extract().path("id");

		Map<String, Object> requestPayload1 = new HashMap<String, Object>();
		requestPayload1.put("name", "iPhone 17 Pro");
		requestPayload1.put("type", "mobile");
		requestPayload1.put("price", 800);
		requestPayload1.put("shipping", 30);
		requestPayload1.put("upc", "mobile");
		requestPayload1.put("description", "dual camera iphone");
		requestPayload1.put("manufacturer", "Apple");
		requestPayload1.put("model", "iPhone 17 Pro");

		Response response = RestAssured.given().when().contentType(ContentType.JSON).body(requestPayload1).put("/products/" + id).then()
				.statusCode(200).extract().response();
		logger.debug(response.asPrettyString());

	}

	@Test
	public void verifyDeleteProduct() {
		logger.info("Executing verifyDeleteProduct..", true);
		Map<String, Object> requestPayload = new HashMap<String, Object>();
		requestPayload.put("name", "iPhone 17");
		requestPayload.put("type", "mobile");
		requestPayload.put("price", 800);
		requestPayload.put("shipping", 30);
		requestPayload.put("upc", "mobile");
		requestPayload.put("description", "dual camera iphone");
		requestPayload.put("manufacturer", "Apple");
		requestPayload.put("model", "iPhone 17");

		int id = RestAssured.given().when().contentType(ContentType.JSON).body(requestPayload).post("/products").then()
				.log().all().extract().path("id");

		RestAssured.given().when().delete("/products/" + id).then().statusCode(200).log().all();
		RestAssured.given().when().get("/products/" + id).then().statusCode(404).log().body();

	}

}
