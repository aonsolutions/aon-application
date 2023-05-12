package com.code.aon.aio.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;

import com.code.aon.AonVersion;

public class OpenIDAuthServlet extends HttpServlet {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static class PasswordGenerator {
		 
		public static final String NUMEROS = "0123456789";
	 
		public static final String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	 
		public static final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
	 
		public static final String ESPECIALES = "Ò—";
	 
		//
		public static String getPinNumber() {
			return getPassword(NUMEROS, 4);
		}
	 
		public static String getPassword() {
			return getPassword(8);
		}
	 
		public static String getPassword(int length) {
			return getPassword(NUMEROS + MAYUSCULAS + MINUSCULAS, length);
		}
	 
		public static String getPassword(String key, int length) {
			String pswd = "";
	 
			for (int i = 0; i < length; i++) {
				pswd+= key.charAt((int)(Math.random() * key.length()));
			}
	 
			return pswd;
		}
	}

	// instantiate a ConsumerManager object
	private static final ConsumerManager MANAGER = new ConsumerManager();

	private static final String VERIFY = "verify";
	private static final String ENDPOINT = "endpoint";
	
	private static String email;
	private static String pass;
	
	public static String getUsername(){	
		return "OpenID_Email="+email;
	}
	
	public static String getPassword(){
		return pass;
	}
	
	private static class UIMessageExtension implements MessageExtension {

		/**
		 * The Attribute Exchange Type URI.
		 */
		public static final String OPENID_NS_UI = "http://specs.openid.net/extensions/ui/1.0";

		/**
		 * The Attribute Exchange extension-specific parameters.
		 * <p>
		 * The openid.<extension_alias> prefix is not part of the parameter
		 * names
		 */
		protected ParameterList parameters;

		public UIMessageExtension() {
			parameters = new ParameterList();
		}

		@Override
		public String getTypeUri() {
			return OPENID_NS_UI;
		}

		@Override
		public ParameterList getParameters() {
			return parameters;
		}

		@Override
		public void setParameters(ParameterList params) {
			parameters.addParams(params);
		}

		/**
		 * Attribute exchange doesn't implement authentication services.
		 * 
		 * @return false
		 */
		@Override
		public boolean providesIdentifier() {
			return false;
		}

		/**
		 * Attribute exchange parameters are required to be signed.
		 * 
		 * @return true
		 */
		@Override
		public boolean signRequired() {
			return true;
		}

	    public void addParameter(String key, String value)
	        throws MessageException
	    {
	    	Map<String, String> map = new HashMap<String, String>();
	    	map.put(key, value);
	    	parameters.addParams(new ParameterList(map));
	    }
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Check if verify parameter exist
		if (req.getParameterMap().containsKey(VERIFY)) {
			try {
				Identifier identifier = verifyResponse(req);
				if (identifier != null){
					pass= PasswordGenerator.getPassword(
							PasswordGenerator.MINUSCULAS+
							PasswordGenerator.MAYUSCULAS+
							PasswordGenerator.ESPECIALES,10);
					
					RequestDispatcher dispatcher = getServletContext()
							.getRequestDispatcher("/login/popupclose.jsp");
					req.setAttribute("username", getUsername());
					req.setAttribute("password", getPassword() );
					dispatcher.forward(req, resp);
				}
				else{
				RequestDispatcher dispatcher = getServletContext()
						.getRequestDispatcher("/login/popupclosecancel.jsp");
				dispatcher.forward(req, resp);}
			} catch (OpenIDException exception) {
				throw new ServletException(exception);
			}
		} else {
			String identifier = req.getParameter(ENDPOINT);
			
			try {
				authRequest(identifier, req, resp);
			} catch (OpenIDException exception) {
				throw new ServletException(exception);
			}
		}
	}

	// --- placing the authentication request ---
	public void authRequest(String userSuppliedString,
			HttpServletRequest httpReq, HttpServletResponse httpResp)
			throws IOException, ServletException, OpenIDException {

		// configure the return_to URL where our application will receive
		// the authentication responses from the OpenID provider
		String returnToUrl = httpReq.getRequestURL().append("?verify")
				.toString();

		// --- Forward proxy setup (only if needed) ---
		// ProxyProperties proxyProps = new ProxyProperties();
		// proxyProps.setProxyName("proxy.example.com");
		// proxyProps.setProxyPort(8080);
		// HttpClientFactory.setProxyProperties(proxyProps);

		// perform discovery on the user-supplied identifier
		List discoveries = MANAGER.discover(userSuppliedString);

		// attempt to associate with the OpenID provider
		// and retrieve one service endpoint for authentication
		DiscoveryInformation discovered = MANAGER.associate(discoveries);

		// store the discovery information in the user's session
		httpReq.getSession().setAttribute("openid-disc", discovered);

		// obtain a AuthRequest message to be sent to the OpenID provider
		AuthRequest authReq = MANAGER.authenticate(discovered, returnToUrl);
		/* 		SCOPE
        //--- OAuth parameters for getting access tokens for other third party google services ---.  
        HybridOauthMessage hybridMsg = new HybridOauthMessage();  
        ParameterList paramsList = new ParameterList();  
        //ParameterList paramsList = new ParameterList();  
        Parameter param3 = new Parameter("scope", "https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/drive https://www.googleapis.com/auth/tasks");  
        Parameter param2 = new Parameter("consumer", "http://demo.aonsolutions.net:8080/aon-aio/"); 
       // Parameter param1 = new Parameter("ext2", "http://specs.openid.net/extensions/oauth/1.0"); 
       // paramsList.set(param1);  
        paramsList.set(param2);
        paramsList.set(param3);
        hybridMsg.setParameters(paramsList);
        Message message = authReq.createMessage(paramsList);  
        
       */
     
		// Attribute Exchange example: fetching the 'email' attribute
		FetchRequest fetch = FetchRequest.createFetchRequest();
		
		fetch.addAttribute("email", // attribute alias
				"http://schema.openid.net/contact/email", // type URI
				true); // required
		// attach the extension to the authentication request
		authReq.addExtension(fetch);
		
		UIMessageExtension uiExtension = new UIMessageExtension();
		uiExtension.addParameter("mode", "popup");
		authReq.addExtension(uiExtension);
		
		if (!discovered.isVersion2()) {
			// Option 1: GET HTTP-redirect to the OpenID Provider endpoint
			// The only method supported in OpenID 1.x
			// redirect-URL usually limited ~2048 bytes
			httpResp.sendRedirect(authReq.getDestinationUrl(true));
		} else {
			// Option 2: HTML FORM Redirection (Allows payloads >2048 bytes)
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/login/formredirection.jsp");
			httpReq.setAttribute("parameterMap", authReq.getParameterMap());
			httpReq.setAttribute("destinationUrl",
					authReq.getDestinationUrl(false));
			dispatcher.forward(httpReq, httpResp);
		}
	}

	// --- processing the authentication response ---
	public Identifier verifyResponse(HttpServletRequest httpReq)
			throws OpenIDException {
		// extract the parameters from the authentication response
		// (which comes in as a HTTP request from the OpenID provider)
		ParameterList response = new ParameterList(httpReq.getParameterMap());

		// retrieve the previously stored discovery information
		DiscoveryInformation discovered = (DiscoveryInformation) httpReq
				.getSession().getAttribute("openid-disc");

		// extract the receiving URL from the HTTP request
		StringBuffer receivingURL = httpReq.getRequestURL();
		String queryString = httpReq.getQueryString();
		if (queryString != null && queryString.length() > 0) {
			receivingURL.append('?').append(httpReq.getQueryString());
		}

		// verify the response; ConsumerManager needs to be the same
		// (static) instance used to place the authentication request
		VerificationResult verification = MANAGER.verify(
				receivingURL.toString(), response, discovered);

		// examine the verification result and extract the verified identifier
		Identifier verified = verification.getVerifiedId();
		if (verified != null) {
			AuthSuccess authSuccess = (AuthSuccess) verification
					.getAuthResponse();

			if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
			
				FetchResponse fetchResp = (FetchResponse) authSuccess
						.getExtension(AxMessage.OPENID_NS_AX);
				List emails = fetchResp.getAttributeValues("email");
				String email = (String) emails.get(0);

				this.email = email;
				
				
			}

			return verified; // success
		}

		return null;
	}

}
