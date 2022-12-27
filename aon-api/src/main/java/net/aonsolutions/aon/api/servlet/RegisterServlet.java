package net.aonsolutions.aon.api.servlet;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DomainType;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonRegisterServlet", urlPatterns = {"/ms/api/register/*"})
public class RegisterServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(RegisterServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
//			AonApiData api = initialize(req, resp);
//			Auth auth = createAuth(api);
//			Domain domain = createDomain(auth.getSchema());
//			User user = createUser(domain , auth);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response(req, resp);
	}
	
	private Auth createAuth(AonApiData api) {
		Auth auth = AON_SOLUTIONS.getAuth(api.getData().getString("email"));
		if(auth.getUuid() == null) {
    		String pass = Utils.createPasswordHash(auth.getEmail(), api.getData().getString("password"));
			auth = new Auth()
					.setDocument(api.getData().getString("document"))
					.setEmail(api.getData().getString("email"))
					.setName(api.getData().getString("name"))
					.setPhone(api.getData().getString("phone"))
					.setSurname(api.getData().getString("surname"))
					.setPassword(pass);
			
			auth = AON_SOLUTIONS.insertAuth(auth);
		} 
		return auth;
	}
	
	private Domain createDomain(AonApiData api, String schema) throws Exception{
		Domain domain = new Domain()
				.setDescription(api.getData().getString("company_name"))
				.setName(api.getData().getString("company_document") + ".aonsolutions.net")
				.setOwner(api.getData().getString("email"))
				.setActive(true)
				.setDomainType(DomainType.ENTERPRISE)
				.setEnableHeredity(false)
				.setDomainManagement(false);
		
		Registry registry = new Registry()
				.setDocument(api.getData().getString("company_document"))
				.setName(api.getData().getString("company_name"));

		return AON_SOLUTIONS.insertDomain(schema, domain, registry);
	}
	
	private User createUser(Domain domain, Auth auth) {
		
		User user = AON_SOLUTIONS.getUserUuid(domain, auth.getUuid());
		if(user.getId() == null) {
			String login = ramdonLogin();
			String pass = Utils.createPasswordHash(auth.getEmail(), login);
			
			Company cp = AON.getCompany(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()));
			user = new User()
				.setAuth(auth.getUuid())
				.setActive(true)
				.setDomain(domain)
				.setLogin(login)
				.setName(login)
				.setEnterprise(cp.getId());
			return AON.insertUser(domain.getName(), domain.getId(), "", user);
		}
		return new User();
	}
	
	private String ramdonLogin() {
		Random rnd = new Random();
		Integer i = rnd.nextInt(100000000-10000000+1)+10000000;
		return i.toString();	
	}

}
