package net.aonsolutions.aon.api.test.login;

import static org.junit.Assert.assertEquals;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.github.javafaker.Faker;

import net.aonsolutions.aon.api.servlet.Utils;
import net.aonsolutions.aon.api.test.request.Method;
import net.aonsolutions.aon.api.test.request.Request;

public class LoginTest extends AbstractOccamTest {
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
	
	@Test
	public void test() {
		Auth auth = createAuth();
		
		emptyLogin();
		correctLogin(auth);
		incorrectUsernameLogin();
		incorrectPasswordLogin(auth);

	}
	
	private void emptyLogin() {
		LoginVisit loginVisit = new LoginVisit();
		JSONObject respObject = Request.request(Method.POST, request, response, loginVisit, new JSONObject());
		assertEquals("error", respObject.optString("type"));
		assertEquals("El Usuario No existe.", respObject.optString("message"));
	    System.out.println(respObject);
	}
	
	private void correctLogin(Auth auth) {
		JSONObject respObject = login(auth.getEmail(), "test");
		assertEquals(true, respObject.opt("session_id") != null);
	    System.out.println(respObject);
	}
	
	private void incorrectUsernameLogin() {
		JSONObject respObject = login("aljsadfsa@mdasnfsdaf.casdf", "test");
		assertEquals("error", respObject.optString("type"));
		assertEquals("El Usuario No existe.", respObject.optString("message"));
	    System.out.println(respObject);
	}
	
	private void incorrectPasswordLogin(Auth auth) { 
		JSONObject respObject = login(auth.getEmail(), "t3st");
		assertEquals("error", respObject.optString("type"));
		assertEquals("La Contraseña no coincide.", respObject.optString("message"));
	}
	
	private JSONObject login(String username, String password) {
		JSONObject json = new JSONObject();
		json.put("username", username);
		json.put("password", password);
		
		LoginVisit loginVisit = new LoginVisit();
		JSONObject respObject = Request.request(Method.POST, request, response, loginVisit, json);
		return respObject;
	}
	
	
	private Auth createAuth() {
		String email = faker.internet().emailAddress();
		String password = "test";
		String pass = Utils.createPasswordHash(email, password);

		Auth auth = new Auth()
			.setEmail(email)
			.setPassword(pass)
			.setName("")
			.setSurname("")
			.setDocument("")
			.setPhone("");
		
		auth = AON_SOLUTIONS.insertAuth(DOMAIN_NAME, DOMAIN_ID, auth);
		
		Auth inserted = AON_SOLUTIONS.getAuth(auth.getAuth());		
		
		assertEquals(auth.getEmail(), inserted.getEmail());
		
		return auth;
	}
}
