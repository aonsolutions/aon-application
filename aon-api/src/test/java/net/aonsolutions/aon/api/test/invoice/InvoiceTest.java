package net.aonsolutions.aon.api.test.invoice;

import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
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

public class InvoiceTest extends AbstractOccamTest {
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
	public void test() {
		headers.put(IConstants.DOMAIN_NAME, DOMAIN_NAME);
		headers.put(IConstants.DOMAIN_ID, DOMAIN_ID.toString());
		headers.put(IConstants.DOMAIN_LOGIN, USER);
		when(request.getHeaderNames()).thenReturn(Collections.enumeration(headers.keySet()));
		when(request.getHeader(IConstants.DOMAIN_NAME)).thenReturn("b93670818-ayudat.aibanez.net");
		when(request.getHeader(IConstants.DOMAIN_ID)).thenReturn("8089");
		when(request.getHeader(IConstants.DOMAIN_LOGIN)).thenReturn("albertocastro");
		getInvoices();
	}
    
	private void getInvoices() {
		InvoiceServletTest iv = new InvoiceServletTest();
		when(request.getParameterMap()).thenReturn(buildParameterMap());
		JSONArray respObject = Request.requestJSONArray(Method.GET, request, response, iv, new JSONObject());
	    System.out.println(respObject);
	}
	
	private Map<String,String[]> buildParameterMap() {
		Map<String,String[]> map = new HashMap<>();
		String[] page = {"1"};
		map.put("page", page);
		String[] perPage = {"30"};
		map.put("per_page", perPage);
		String[] types = {"sales"};
		map.put("types", types);
		String[] from = {"2022-03-01"};
		map.put("from", from);
		String[] to = {"2022-03-04"};
		map.put("to", to);
		return map;
	}
}
