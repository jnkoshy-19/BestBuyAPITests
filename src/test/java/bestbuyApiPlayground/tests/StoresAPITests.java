package bestbuyApiPlayground.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

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
		Response response = RestAssured.given().when().get("/stores").then().extract().response();
		assertEquals(response.getStatusCode(), 200);
		assertNotNull(response.getBody());
		Reporter.log(response.asPrettyString());
		assertTrue(response.jsonPath().getInt("total") > 0);
		assertNotNull(response.jsonPath().getJsonObject("data"));
		assertNotNull(response.jsonPath().getInt("data[0].id"));
		assertNotNull(response.jsonPath().getString("data[0].name"));
		assertNotNull(response.jsonPath().getString("data[0].type"));
		assertNotNull(response.jsonPath().getString("data[0].address"));
		assertNotNull(response.jsonPath().getString("data[0].city"));
		assertNotNull(response.jsonPath().getString("data[0].state"));
		assertNotNull(response.jsonPath().getString("data[0].zip"));
	}
	
	@Test
	public void verifyGetStoreForSpecificId() {
		Reporter.log("Executing verifyGetStoreForSpecificId..", true);
		Response response = RestAssured.given().when().get("/stores/4").then().extract().response();
		assertEquals(response.getStatusCode(), 200);
		assertNotNull(response.getBody());
		Reporter.log(response.asPrettyString());
		assertNotNull(response.jsonPath().getInt("id"));
		assertNotNull(response.jsonPath().getString("name"));
		assertEquals("Minnetonka", response.jsonPath().getString("name"));
		assertNotNull(response.jsonPath().getString("type"));
		assertEquals("BigBox", response.jsonPath().getString("type"));
		assertNotNull(response.jsonPath().getString("address"));
		assertEquals("13513 Ridgedale Dr", response.jsonPath().getString("address"));
		assertNotNull(response.jsonPath().getString("city"));
		assertEquals("Hopkins", response.jsonPath().getString("city"));
		assertNotNull(response.jsonPath().getString("state"));
		assertEquals("MN", response.jsonPath().getString("state"));
		assertNotNull(response.jsonPath().getString("zip"));
		assertEquals("55305", response.jsonPath().getString("zip"));
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
			Response response = RestAssured.given().when().contentType(ContentType.JSON).body(jsonString.toString())
					.post("/stores").then().extract().response();
			assertEquals(response.getStatusCode(), 201);
			assertNotNull(response.getBody());
			Reporter.log(response.asPrettyString());
			
			assertNotNull(response.jsonPath().getInt("id"));
			assertNotNull(response.jsonPath().getString("name"));
			assertEquals("CityStore", response.jsonPath().getString("name"));
			assertNotNull(response.jsonPath().getString("type"));
			assertEquals("BigBox", response.jsonPath().getString("type"));
			assertNotNull(response.jsonPath().getString("address"));
			assertEquals("WhiteMarsh Drive", response.jsonPath().getString("address"));
			assertNotNull(response.jsonPath().getString("city"));
			assertEquals("Philly", response.jsonPath().getString("city"));
			assertNotNull(response.jsonPath().getString("state"));
			assertEquals("PA", response.jsonPath().getString("state"));
			assertNotNull(response.jsonPath().getString("zip"));
			assertEquals("19000", response.jsonPath().getString("zip"));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}
}
