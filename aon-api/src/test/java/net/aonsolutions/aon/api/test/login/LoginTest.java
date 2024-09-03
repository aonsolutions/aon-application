package net.aonsolutions.aon.api.test.login;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.OutputStream;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.github.javafaker.Faker;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.servlet.Utils;
import net.aonsolutions.tests.request.Method;
import net.aonsolutions.tests.request.Request;

public class LoginTest extends AbstractOccamTest {
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
	
	@Test
	public void test() {
		Auth auth = createAuth();
		
		emptyLogin();
		correctLogin(auth);
		incorrectUsernameLogin();
		incorrectPasswordLogin(auth);

	}
	
	private void emptyLogin() {
		LoginServletTest loginVisit = new LoginServletTest();
		JSONObject respObject = Request.requestJSONObject(Method.POST, request, response, loginVisit, new JSONObject());
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
		
		LoginServletTest loginVisit = new LoginServletTest();
		JSONObject respObject = Request.requestJSONObject(Method.POST, request, response, loginVisit, json);
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
