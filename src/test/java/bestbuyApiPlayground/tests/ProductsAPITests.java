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

import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import bestbuyApiPlayground.utils.FileUtilities;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ProductsAPITests {

	FileUtilities fileUtilities;

	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = "http://localhost/";
		RestAssured.port = 3030;
		fileUtilities = new FileUtilities();
	}

	@Test
	public void verifyGetProducts() {
		Reporter.log("Executing verifyGetProducts..", true);
		RestAssured.given().when().get("/products").then().assertThat().statusCode(200).body("total", greaterThan(0))
				.body("data", notNullValue()).body("data[0].id", notNullValue()).body("data[0].name", notNullValue())
				.body("data[0].type", notNullValue()).body("data[0].price", notNullValue())
				.body("data[0].shipping", notNullValue()).body("data[0].description", notNullValue())
				.body("data[0].manufacturer", notNullValue()).body("data[0].model", notNullValue()).log().body();
	}

	@Test
	public void verifyGetProductsWithLimit() throws JsonMappingException, JsonProcessingException {
		Reporter.log("Executing verifyGetProductsWithLimit..", true);
		RestAssured.given().when().queryParam("$limit", 5).get("/products").then().assertThat()
				.body("total", greaterThan(0)).body("limit", equalTo(5)).body("data", notNullValue())
				.body("data[0].id", notNullValue()).body("data[0].name", notNullValue())
				.body("data[0].type", notNullValue()).body("data[0].price", notNullValue())
				.body("data[0].shipping", notNullValue()).body("data[0].description", notNullValue())
				.body("data[0].manufacturer", notNullValue()).body("data[0].model", notNullValue()).log().body();
	}

	@Test
	public void verifyGetProductsWithSpecificId() {
		Reporter.log("Executing verifyGetProductsWithSpecificId..", true);
		RestAssured.given().when().param("id", 185230).get("/products").then().assertThat().body("total", equalTo(1))
				.body("data", notNullValue()).body("data[0].id", notNullValue()).body("data[0].id", equalTo(185230))
				.body("data[0].name", notNullValue()).body("data[0].type", notNullValue())
				.body("data[0].price", notNullValue()).body("data[0].shipping", notNullValue())
				.body("data[0].description", notNullValue()).body("data[0].manufacturer", notNullValue())
				.body("data[0].model", notNullValue()).log().body();
	}

	@Test
	public void testValidProduct() {
		Reporter.log("Executing testValidProduct..", true);
		RestAssured.given().when().get("/products/185230").then().assertThat().statusCode(200)
				.body("id", notNullValue()).body("id", equalTo(185230)).body("name", notNullValue())
				.body("type", notNullValue()).body("price", notNullValue()).body("upc", notNullValue())
				.body("shipping", notNullValue()).body(matchesJsonSchemaInClasspath("schemas/products-schema.json"))
				.log().body();
	}

	@Test
	public void testInvalidProduct() {
		Reporter.log("Executing testInvalidProduct..", true);
		Response response = RestAssured.given().when().get("/products/999999").then().extract().response();
		assertNotNull(response.getBody());
		Reporter.log(response.asPrettyString());
		assertEquals(response.getStatusCode(), 404);
	}

	@Test
	public void verifyPostProducts() {
		Reporter.log("Executing verifyPostProducts..", true);
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
		Reporter.log("Executing verifyPostProductsWithPayloadAsObject..", true);
		Map<String, Object> requestPayload = new HashMap<String, Object>();
		requestPayload.put("name", "iPhone 17");
		requestPayload.put("type", "mobile");
		requestPayload.put("price", 800);
		requestPayload.put("shipping", 30);
		requestPayload.put("upc", "mobile");
		requestPayload.put("description", "dual camera iphone");
		requestPayload.put("manufacturer", "Apple");
		requestPayload.put("model", "iPhone 17");

		RestAssured.given().when().contentType(ContentType.JSON).body(requestPayload).post("/products").then()
				.assertThat().statusCode(201).log().all();

	}

	@Test
	public void verifyPostProductsWithInvalidPayload() {
		Reporter.log("Executing verifyPostProductsWithInvalidPayload..", true);
		RestAssured.given().when().contentType(ContentType.TEXT).body("invalid payload").post("/products").then()
				.assertThat().statusCode(400).log().all();
	}

	@Test
	public void verifyPutProductsWithPayloadAsObject() {
		Reporter.log("Executing verifyPutProductsWithPayloadAsObject..", true);
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

		RestAssured.given().when().contentType(ContentType.JSON).body(requestPayload1).put("/products/" + id).then()
				.statusCode(200).log().all();

	}

	@Test
	public void verifyDeleteProduct() {
		Reporter.log("Executing verifyDeleteProduct..", true);
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
