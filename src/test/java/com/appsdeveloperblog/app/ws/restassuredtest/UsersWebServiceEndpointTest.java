package com.appsdeveloperblog.app.ws.restassuredtest;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UsersWebServiceEndpointTest {
	
	private final String CONTEXT_PATH="/mobile-app-ws";
	private final String EMAIL_ADDRESS = "eng.mohamoh111s@gmail.com";
	private final String JSON = "application/json";
	private static String authorizationHeader;
	private static String userId;
	private static List<Map<String, String>> addresses;

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI="http://localhost";
		RestAssured.port=8081;
	}

	/*
	 * testUserLogin()
	 * 
	 * */
	@Test
	final void a() {
		 Map<String, String> loginDetails = new HashMap<>();
		 loginDetails.put("email", EMAIL_ADDRESS);
		 loginDetails.put("password", "M*1234567");
		 
		 Response resposne = given().
		 contentType(JSON).
		 accept(JSON).
		 body(loginDetails).
		 when().
		 post(CONTEXT_PATH + "/users/login").
		 then().
		 statusCode(200).extract().response();
		 
		 authorizationHeader = resposne.header("Authorization");
		 userId = resposne.header("UserID");
		 
		 assertNotNull(authorizationHeader);
		 assertNotNull(userId);
		 
	}
	
	/*
	 * testGetUserDetails()
	 * 
	 * */
	@Test
	final void b() throws JSONException {
		
		Response response = given()
		 .pathParam("id", userId)
		 .header("Authorization",authorizationHeader)
		 .accept(JSON)
		 .when()
		 .get(CONTEXT_PATH + "/users/{id}")
		 .then()
		 .statusCode(200)
		 .contentType(JSON)
		 .extract()
		 .response();
		
		String userPublicId = response.jsonPath().getString("userId");
		String userEmail = response.jsonPath().getString("email");
        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");


		String bodyString = response.body().asString();
		JSONObject responseBodyJson = new JSONObject(bodyString);
		JSONArray addresses = responseBodyJson.getJSONArray("addresses");

		String addressId = addresses.getJSONObject(0).getString("addressId");
		
		assertNotNull(userPublicId);
		assertNotNull(userEmail);
		assertNotNull(firstName);
		assertNotNull(lastName);
		assertEquals(EMAIL_ADDRESS, userEmail);

		assertNotNull(addresses);
		assertTrue( addresses.length() == 2 );
		assertTrue(addressId.length() == 30);

	}
	
	/*
	 * Test Update User Details
	 * */
	@Test
	final void c() throws JSONException {
		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "Serge");
		userDetails.put("lastName", "Kargopolov");
		
		 Response response = given()
		 .contentType(JSON)
		 .accept(JSON)
		 .header("Authorization",authorizationHeader)
		 .pathParam("id", userId)
		 .body(userDetails)
		 .when()
		 .put(CONTEXT_PATH + "/users/{id}")
		 .then()
		 .statusCode(200)
		 .contentType(JSON)
		 .extract()
		 .response();
		 
         String firstName = response.jsonPath().getString("firstName");
         String lastName = response.jsonPath().getString("lastName");
         

         assertEquals("Serge", firstName);
         assertEquals("Kargopolov", lastName);
	}
	
	/*
	 * Test the Delete User Details
	 * */
	@Test
	@Ignore
	final void d()
	{
		Response response = given()
		.header("Authorization",authorizationHeader)
		.accept(JSON)
		.pathParam("id", userId)
		.when()
		.delete(CONTEXT_PATH + "/users/{id}")
		.then()
		.statusCode(200)
		.contentType(JSON)
		.extract()
		.response();

		String operationResult = response.jsonPath().getString("operationResult");
		assertEquals("SUCCESS", operationResult);

	}
	
}
