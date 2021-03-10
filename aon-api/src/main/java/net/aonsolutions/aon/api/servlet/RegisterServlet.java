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

@SuppressWarnings("serial")
@WebServlet(name = "AonRegisterServlet", urlPatterns = {"/ms/api/register/*"})
public class RegisterServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(RegisterServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		super.doPost(req, resp);

		try {
//			Auth auth = createAuth();
//			Domain domain = createDomain(auth.getSchema());
//			User user = createUser(domain , auth);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response(req, resp);
	}
	
	private Auth createAuth() {
		Auth auth = AON_SOLUTIONS.getAuth(getData().getString("email"));
		if(auth.getUuid() == null) {
    		String pass = Utils.createPasswordHash(auth.getEmail(), getData().getString("password"));
			auth = new Auth()
					.setDocument(getData().getString("document"))
					.setEmail(getData().getString("email"))
					.setName(getData().getString("name"))
					.setPhone(getData().getString("phone"))
					.setSurname(getData().getString("surname"))
					.setPassword(pass);
			
			auth = AON_SOLUTIONS.insertAuth(auth);
		} 
		return auth;
	}
	
	private Domain createDomain(String schema) throws Exception{
		Domain domain = new Domain()
				.setDescription(getData().getString("company_name"))
				.setName(getData().getString("company_document") + ".aonsolutions.net")
				.setOwner(getData().getString("email"))
				.setActive(true)
				.setDomainType(DomainType.ENTERPRISE)
				.setEnableHeredity(false)
				.setDomainManagement(false);
		
		Registry registry = new Registry()
				.setDocument(getData().getString("company_document"))
				.setName(getData().getString("company_name"));

		return AON_SOLUTIONS.insertDomain(schema, domain, registry);
	}
	
	private User createUser(Domain domain, Auth auth) {
		
		User user = AON_SOLUTIONS.getUserUuid(domain, auth.getUuid());
		if(user.getId() == null) {
			String login = ramdonLogin();
			String pass = Utils.createPasswordHash(auth.getEmail(), login);
			
			Company cp = AON.getCompany(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()));
			user = new User()
				.setAuth(auth.getAuth())
				.setActive(true)
				.setDomain(domain.getId())
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
