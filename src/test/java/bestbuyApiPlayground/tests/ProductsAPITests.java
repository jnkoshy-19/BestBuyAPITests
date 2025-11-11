package bestbuyApiPlayground.tests;

import java.io.IOException;

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
}
