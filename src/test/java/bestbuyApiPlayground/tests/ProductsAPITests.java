package bestbuyApiPlayground.tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import bestbuyApiPlayground.utils.FileUtilities;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

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
		RestAssured.given().when().get("/products").then().statusCode(200).log().body();
	}

	@Test
	public void verifyGetProductsWithLimit() {
		Reporter.log("Executing verifyGetProductsWithLimit..", true);
		RestAssured.given().when().queryParam("$limit", 5).get("/products").then().statusCode(200).log().body();
	}

	@Test
	public void verifyGetProductsWithSpecificId() {
		Reporter.log("Executing verifyGetProductsWithSpecificId..", true);
		RestAssured.given().when().param("id", 185230).get("/products").then().statusCode(200).log().body();
	}

	@Test
	public void verifyPostProducts() {
		Reporter.log("Executing verifyPostProducts..", true);
		StringBuilder jsonString = new StringBuilder();
		try {
			jsonString.append(fileUtilities.readFile("src/test/resources/product.json"));
			RestAssured.given().when().contentType(ContentType.JSON).body(jsonString.toString()).post("/products")
					.then().statusCode(201).log().all();

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
				.statusCode(201).log().all();

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

		int id = RestAssured.given().when().contentType(ContentType.JSON).body(requestPayload).post("/products").then().log().all()
				.extract().path("id");

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

		int id = RestAssured.given().when().contentType(ContentType.JSON).body(requestPayload).post("/products").then().log().all()
				.extract().path("id");


		RestAssured.given().when().delete("/products/" + id).then().statusCode(200).log().all();
		RestAssured.given().when().get("/products/" + id).then().statusCode(404).log().body();

	}
}
