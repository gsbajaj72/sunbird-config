package org.sunbird.platform.domain;


/// hi this is test to check smartgit sync.
import static com.jayway.restassured.RestAssured.baseURI;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
//import static com.jayway.restassured.RestAssured.basePath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Random;

import org.sunbird.common.Platform;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;

public class BaseTest 
{
	public ResponseSpecBuilder builderres = new ResponseSpecBuilder();
	public String liveStatus = "Live";
	public String contentType = "application/json";
	public String UrlContentType = "text/plain";
	public String uploadContentType = "multipart/form-data";
	public String userId = "ilimi";
	public String channelId = "Test";
	public String appId = "test.appId";
	
	public String APIToken = Platform.config.getString("ft.access_key");
	public String validuserId = "functional-tests";
	public String invalidUserId = "abc";
	
	public void parser(){
		RestAssured.registerParser("text/csv", null);
	}
	
	public void delay(long time){
		try {
			Thread.sleep(time);
		} catch (Exception e) {}
	}
	/**
	 * sets baseURI and basePath
	 */
	public void setURI()
	{
		baseURI =Platform.config.getString("ft.base_uri"); 
	}
	
		
	/**
	 * adds the given content_type and user_id to the header of RequestSpecBuilder
	 * 
	 * @param content_type - json/xml
	 * @param user_id
	 * @return returns a RequestSpecification object
	 */
	public RequestSpecification getRequestSpec(String content_type,String user_id)
	{
		RequestSpecBuilder builderreq = new RequestSpecBuilder();
		builderreq.addHeader("Content-Type", content_type);
		builderreq.addHeader("user-id", user_id);
		RequestSpecification requestSpec = builderreq.build();
		return requestSpec;
	}
	
	public RequestSpecification getRequestSpecification(String content_type,String user_id, String APIToken)
	{
		RequestSpecBuilder builderreq = new RequestSpecBuilder();
		builderreq.addHeader("Content-Type", content_type);
		builderreq.addHeader("user-id", user_id);
		builderreq.addHeader("Authorization", APIToken);
		RequestSpecification requestSpec = builderreq.build();
		return requestSpec;
	}
	
	public RequestSpecification getRequestSpecification(String content_type,String user_id, String APIToken, String fileUrl)
	{
		RequestSpecBuilder builderreq = new RequestSpecBuilder();
		builderreq.addHeader("user-id", user_id);
		builderreq.addHeader("Content-Type", content_type);
		builderreq.addHeader("Authorization", APIToken);
		builderreq.addHeader("fileUrl", fileUrl);
		RequestSpecification requestSpec = builderreq.build();
		return requestSpec;
	}
	
	public RequestSpecification getRequestSpecification(String content_type,String user_id, String APIToken, String channelId, String appId)
	{
		RequestSpecBuilder builderreq = new RequestSpecBuilder();
		builderreq.addHeader("user-id", user_id);
		builderreq.addHeader("Content-Type", content_type);
		builderreq.addHeader("Authorization", APIToken);
		builderreq.addHeader("X-Channel-Id", channelId);
		RequestSpecification requestSpec = builderreq.build();
		return requestSpec;
	}
	
	/**
	 * checks whether response statuscode is 200,param size is 5, param.status is successful and param.errmsg is null
	 * 
	 * @return ResponseSpecification object
	 */
	public ResponseSpecification get200ResponseSpec()
	{
		builderres.expectStatusCode(200);
		builderres.expectBody("params.size()", is(5));
		builderres.expectBody("params.status", equalTo("successful"));
		builderres.expectBody("params.errmsg", equalTo(null));
		builderres.expectBody("responseCode", equalTo("OK"));
		ResponseSpecification responseSpec = builderres.build();
		return responseSpec;
	}
	
	public ResponseSpecification get200ResponseSpecUpload()
	{
		builderres.expectStatusCode(200);
		builderres.expectBody("params.size()", is(5));
		//builderres.expectBody("params.status", equalTo("successful"));
		builderres.expectBody("params.errmsg", equalTo(null));
		builderres.expectBody("responseCode", equalTo("OK"));
		builderres.expectBody("result.size()", is(3));
		ResponseSpecification responseSpec = builderres.build();
		return responseSpec;
	}
	
	/**
	 * checks whether response statuscode is 500,param size is 5, param.status is failed and responsecode is SERVER_ERROR
	 * 
	 * @return ResponseSpecification object
	 */
	public ResponseSpecification get500ResponseSpec()
	{
		builderres.expectStatusCode(500);
		builderres.expectBody("params.size()", is(5));
		builderres.expectBody("params.status", equalTo("failed"));
		builderres.expectBody("responseCode", equalTo("SERVER_ERROR"));
		ResponseSpecification responseSpec = builderres.build();
		return responseSpec;
	}
	
	
	/**
	 * checks whether HTML response statuscode is 500 and param size is 1
	 * 
	 * @return ResponseSpecification object
	 */
	public ResponseSpecification get500HTMLResponseSpec()
	{
		builderres.expectStatusCode(500);
		builderres.expectBody("params.size()", is(1));
		ResponseSpecification responseSpec = builderres.build();
		return responseSpec;
	}
	
	
	
	/**
	 * checks whether response statuscode is 400 
	 * 
	 * @return ResponseSpecification object
	 */
	public ResponseSpecification get400ResponseSpec()
	{
		builderres.expectStatusCode(400);
		ResponseSpecification responseSpec = builderres.build();
		return responseSpec;
	}
	
	
	/**
	 * checks whether response statuscode is 404,param size is 5, param.status is failed and responsecode is RESOURCE_NOT_FOUND
	 * 
	 * @return ResponseSpecification object
	 */
	public ResponseSpecification get404ResponseSpec()
	{
		builderres.expectStatusCode(404);
		builderres.expectBody("params.size()", is(5));
		builderres.expectBody("params.status", equalTo("failed"));
		builderres.expectBody("responseCode", equalTo("RESOURCE_NOT_FOUND"));
		ResponseSpecification responseSpec = builderres.build();
		return responseSpec;
	}
	
	/**
	 * checks for mandatory fields required to create concepts or dimensions (checks whether response params.errmsg is Validation Errors)
	 * 
	 * @return ResponseSpecification object
	 */
	public ResponseSpecification get400ValidationErrorResponseSpec()
	{
		builderres.expectBody("params.errmsg", equalTo("Validation Errors"));
		ResponseSpecification responseSpec = builderres.build();
		return responseSpec;
	}
	
	/**
	 * checks for duplicates(checks whether response params.errmsg is Node Creation Error)
	 * 
	 * @return ResponseSpecification object
	 */
	public ResponseSpecification verify400DetailedResponseSpec(String errmsg, String responseCode, String resMessages)
	{
		builderres.expectBody("params.errmsg", equalTo(errmsg));
		builderres.expectBody("params.status", equalTo("failed"));
		builderres.expectBody("responseCode", equalTo(responseCode));
		//builderres.expectBody("result.messages", equalTo("Invalid Relation")); //TO-DO: How to get the list and how deep can be the list? 
		ResponseSpecification responseSpec = builderres.build();
		return responseSpec;
	}
	
	/**
	 * generates random integer between min and max
	 * 
	 * @param min
	 * @param max
	 * @return random integer
	 */
	public int generateRandomInt(int min, int max)
	{
		Random random = new Random();
		int randomInt = random.nextInt((max - min) + 1) + min;
		return randomInt;
		
	}
	
	public void contentCleanUp(String jsonContentClean){
		setURI();
		given().
		spec(getRequestSpec(contentType, validuserId)).
		body(jsonContentClean).
	with().
		contentType(JSON).
	when().
		post("v1/exec/content_qe_deleteContentBySearchStringInField").
	then().
		log().all().
		spec(get200ResponseSpec());	
	}
	
	public ResponseSpecification get207ResponseSpec()
	{
		builderres.expectStatusCode(207);
		builderres.expectBody("params.size()", is(5));
		builderres.expectBody("params.status", equalTo("partial successful"));
		builderres.expectBody("responseCode", equalTo("PARTIAL_SUCCESS"));
		ResponseSpecification responseSpec = builderres.build();
		return responseSpec;
	}

}
