package net.aonsolutions.aon.api.test.sales;

import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.github.javafaker.Faker;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.tests.request.Method;
import net.aonsolutions.tests.request.Request;

public class SalesTest extends AbstractOccamTest {
	Faker faker = new Faker();
	
	@Mock
    HttpServletRequest request;
 
    @Mock
    HttpServletResponse response;
    
    @Mock
    private OutputStream myOutputStream;
 
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
	
    Map<String, String> headers = new HashMap<>();

	@Test
	@Disabled
	public void test() {
		headers.put(IConstants.DOMAIN_NAME, "udapa.aibanez.net");
		headers.put(IConstants.DOMAIN_ID, "3049");
		headers.put(IConstants.DOMAIN_LOGIN, "test");
		headers.put("session_id", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJkb21haW5cIjozMDQ5LFwic2NoZW1hX2ZpcnN0X2RvbWFpblwiOlwidWRhcGEuYWliYW5lei5uZXRcIixcImxvZ2luXCI6XCJ0ZXN0XCIsXCJ1c2VyXCI6MTYyNjN9IiwiaXNzIjoiYXV0aDAiLCJleHAiOjE2ODUxMTg2OTMsImlhdCI6MTY1MzU4MjY5M30.-TB7QekrOrfTW4Tf7e28S7x1Ny8N3w6UiSZfHZ2RhH0");

		when(request.getHeaderNames()).thenReturn(Collections.enumeration(headers.keySet()));
		when(request.getHeader(IConstants.DOMAIN_NAME)).thenReturn("udapa.aibanez.net");
		when(request.getHeader(IConstants.DOMAIN_ID)).thenReturn("3049");
		when(request.getHeader(IConstants.DOMAIN_LOGIN)).thenReturn("aon");
		when(request.getHeader("session_id")).thenReturn("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJkb21haW5cIjozMDQ5LFwic2NoZW1hX2ZpcnN0X2RvbWFpblwiOlwidWRhcGEuYWliYW5lei5uZXRcIixcImxvZ2luXCI6XCJ0ZXN0XCIsXCJ1c2VyXCI6MTYyNjN9IiwiaXNzIjoiYXV0aDAiLCJleHAiOjE2ODUxMTg2OTMsImlhdCI6MTY1MzU4MjY5M30.-TB7QekrOrfTW4Tf7e28S7x1Ny8N3w6UiSZfHZ2RhH0");
		getSales();
	}
	
	private void getSales() {
		SalesServletTest iv = new SalesServletTest();
		JSONArray respObject = Request.requestJSONArray(Method.GET, request, response, iv, new JSONObject());
	    System.out.println(respObject);
	}

}
