package bestbuyApiPlayground.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import static org.hamcrest.Matchers.*;
import bestbuyApiPlayground.utils.FileUtilities;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CategoriesAPITest {
	private static final Logger logger = LoggerFactory.getLogger(CategoriesAPITest.class);
	FileUtilities fileUtilities;

	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = "http://localhost/";
		RestAssured.port = 3030;
		fileUtilities = new FileUtilities();
	}

	@Test
	public void verifyGetCategories() {
		logger.info("Executing verifyGetCategories", true);
		Response response = RestAssured.given().when().get("/categories").then().assertThat().statusCode(200)
				.body("total", greaterThan(0)).body("data", notNullValue()).body("data[0].id", notNullValue())
				.body("data[0].name", notNullValue()).body("data[0].createdAt", notNullValue())
				.body("data[0].updatedAt", notNullValue()).body("data[0].subCategories", notNullValue())
				.body("data[0].categoryPath", notNullValue()).extract().response();
		logger.debug(response.asPrettyString());

	}

	@Test
	public void verifyGetCategoriesWithLimit() throws JsonMappingException, JsonProcessingException {
		logger.info("Executing verifyGetCategoriesWithLimit..", true);
		Response response = RestAssured.given().when().queryParam("$limit", 5).get("/categories").then().assertThat()
				.body("total", greaterThan(0)).body("limit", equalTo(5)).body("data", notNullValue())
				.body("data[0].id", notNullValue()).body("data[0].name", notNullValue())
				.body("data[0].createdAt", notNullValue()).body("data[0].updatedAt", notNullValue())
				.body("data[0].subCategories", notNullValue()).body("data[0].categoryPath", notNullValue()).extract()
				.response();
		logger.debug(response.asPrettyString());
	}

}
