package net.aonsolutions.aon.api.test.rawdoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.github.javafaker.Faker;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.tests.request.Method;
import net.aonsolutions.tests.request.Request;

public class RawdocTest extends AbstractOccamTest {
	Faker faker = new Faker();
	
	@Mock
    HttpServletRequest request;
 
    @Mock
    HttpServletResponse response;
    
    @Mock
    private OutputStream myOutputStream;
 
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    Map<String, String> headers = new HashMap<>();
    
	@Test
	public void test() {
		headers.put(IConstants.DOMAIN_NAME, DOMAIN_NAME);
		headers.put(IConstants.DOMAIN_ID, DOMAIN_ID.toString());
		headers.put(IConstants.DOMAIN_LOGIN, USER);
		headers.put(IConstants.SESSION_ID, "AONd95770f269e711eb94390242ac130002");

		when(request.getHeaderNames()).thenReturn(Collections.enumeration(headers.keySet()));
		when(request.getHeader(IConstants.DOMAIN_NAME)).thenReturn(DOMAIN_NAME);
		when(request.getHeader(IConstants.DOMAIN_ID)).thenReturn(DOMAIN_ID.toString());
		when(request.getHeader(IConstants.DOMAIN_LOGIN)).thenReturn(USER);
		when(request.getHeader(IConstants.SESSION_ID)).thenReturn("AONd95770f269e711eb94390242ac130002");
				
		emptyRawdoc();
		rawdocS3File();
	}

	private void emptyRawdoc() {
		RawdocServletTest rawdocServlet = new RawdocServletTest();
		JSONObject respObject = Request.requestJSONObject(Method.POST, request, response, rawdocServlet, new JSONObject());
		assertEquals("error", respObject.optString("type"));
		assertEquals("El estado del documento es un dato obligatorio, no puede estar vacío", respObject.optString("message"));
		System.out.println(respObject);
	}
	
	private void rawdocS3File() {
		RawdocServletTest rawdocServlet = new RawdocServletTest();

		JSONObject processingRawdocJSON = createEmptyFileRawdoc(RawdocStatus.PROCESSING);
		JSONObject processingResponse = Request.requestJSONObject(Method.POST, request, response, rawdocServlet, processingRawdocJSON);
		System.out.println(processingResponse);
		Integer processingId = JsonUtils.getInteger(processingResponse, IJsonNames.ID);
		assertNotNull(processingId);
		
		JSONObject inboxRawdocJSON = createEmptyFileRawdoc(RawdocStatus.INBOX);
		JSONObject inboxResponse = Request.requestJSONObject(Method.POST, request, response, rawdocServlet, inboxRawdocJSON);
		System.out.println(inboxResponse);
		Integer inboxId = JsonUtils.getInteger(processingResponse, IJsonNames.ID);
		assertNotNull(inboxId);
	}

	private JSONObject createEmptyFileRawdoc(RawdocStatus status) {
		JSONObject json = new JSONObject();
    	JSONObject file = new JSONObject();
    	file.put("s3Bucket", "pruebaS3Bucket");
    	file.put("s3Key", faker.book() + ".pdf"); 
    	file.put("url", faker.internet().url());
    	file.put("path", faker.internet().url());
    	file.put("content_type", "application/pdf");
    	json.put(IJsonNames.FILE, file);
    	json.put(IJsonNames.STATUS, status.getTediName());
    	return json;
	}
}
