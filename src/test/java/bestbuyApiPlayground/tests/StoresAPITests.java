package bestbuyApiPlayground.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;

import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import bestbuyApiPlayground.utils.FileUtilities;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class StoresAPITests {
	FileUtilities fileUtilities;

	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = "http://localhost/";
		RestAssured.port = 3030;
		fileUtilities = new FileUtilities();
	}

	@Test
	public void verifyGetStores() {
		Reporter.log("Executing verifyGetStores..", true);
		RestAssured.given().when().get("/stores").then().assertThat().statusCode(200).body("total", greaterThan(0))
				.body("data", notNullValue()).body("data[0].id", notNullValue()).body("data[0].name", notNullValue())
				.body("data[0].type", notNullValue()).body("data[0].address", notNullValue())
				.body("data[0].city", notNullValue()).body("data[0].state", notNullValue())
				.body("data[0].zip", notNullValue()).log().all();
	}

	@Test
	public void verifyGetStoreForSpecificId() {
		Reporter.log("Executing verifyGetStoreForSpecificId..", true);
		RestAssured.given().when().get("/stores/4").then().assertThat().statusCode(200)
				.body(matchesJsonSchemaInClasspath("schemas/store-schema.json")).body("id", notNullValue())
				.body("name", equalTo("Minnetonka")).body("type", equalTo("BigBox"))
				.body("address", equalTo("13513 Ridgedale Dr")).body("city", equalTo("Hopkins"))
				.body("state", equalTo("MN")).body("zip", equalTo("55305")).log().all();
	}

	@Test
	public void testInvalidStore() {
		Reporter.log("Executing testInvalidStore..", true);
		Response response = RestAssured.given().when().get("/stores/999999").then().extract().response();
		assertNotNull(response.getBody());
		Reporter.log(response.asPrettyString());
		assertEquals(response.getStatusCode(), 404);
	}

	@Test
	public void verifyPostStores() {
		Reporter.log("Executing verifyPostStores..", true);
		StringBuilder jsonString = new StringBuilder();
		try {
			jsonString.append(fileUtilities.readFile("src/test/resources/store.json"));
			RestAssured.given().when().contentType(ContentType.JSON).body(jsonString.toString()).post("/stores").then()
					.assertThat().statusCode(201).body("id", notNullValue()).body("name", equalTo("CityStore"))
					.body("type", equalTo("BigBox")).body("address", equalTo("WhiteMarsh Drive"))
					.body("city", equalTo("Philly")).body("state", equalTo("PA")).body("zip", equalTo("19000")).log()
					.all();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}
}
