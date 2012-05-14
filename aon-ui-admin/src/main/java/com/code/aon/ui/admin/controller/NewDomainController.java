package com.code.aon.ui.admin.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.controller.LoggedUser;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DataSourceUtil;

public class NewDomainController {

	private static final String EQUALS = "="; 
	private static final String DOMAIN_TYPE = " --domainType=Child";
	private static final String DOMAIN_USER = " --domainUser";
	private static final String DOMAIN_PASSWORD = " --domainPassword";
	private static final String DOMAIN_NAME = " --domainName";
	private static final String DOMAIN_DESCRIPTION = " --domainDescription";
	private static final String PARENT_DOMAIN_ID = " --domainParentId";
	private static final String DB_HOST = " --host";
	private static final String DB_NAME = " --db";
	private static final String DB_USER = " --user";
	private static final String DB_PASSWORD = " --passwd";
	private static final String USER_MAIL = " --userMail";
	
	private static final String CONNECTION_USER_PROPERTY = "hibernate.connection.username";
	private static final String CONNECTION_PASSWORD_PROPERTY = "hibernate.connection.password";
	private static final String CONNECTION_URL_PROPERTY = "hibernate.connection.url";
		
	
	private String domainName;
	private String domainDescription;
	private String password;

	private String output;
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getDomainDescription() {
		return domainDescription;
	}
	public void setDomainDescription(String domainDescription) {
		this.domainDescription = domainDescription;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public void onInit( ActionEvent event) {
		setDomainName(null);
		setDomainDescription(null);
		setPassword(null);
		setOutput(null);
//		("<div>Mode is set to verbose.</div>"
//		+"<div>DATABASE OPTION</div>"
//		+"<div>---------------</div>"
//		+"<div>Host ......:  127.0.0.1</div>"
//		+"<div>Port ......:  3306</div>"
//		+"<div>User ......:  dbuser</div>"
//		+"<div>Password ..:  serubd2000</div>"
//		+"<div>DB Name ...:  aon_master</div>"
//		+"<div>DOMAIN OPTIONS</div>"
//		+"<div>--------------</div>"
//		+"<div>Domain  Name .........:  test05.aonsolutions.net</div>"
//		+"<div>Domain Description ...:  Test creacion desde python</div>"
//		+"<div>Domain Type ..........:  Child</div>"
//		+"<div>Domain parent ID .....:  1</div>"
//		+"<div>Domain user ..........:  admin</div>"
//		+"<div>Domain user password .:  demo</div>"
//		+"<div>eMail ................:  None</div>"
//		+"<div>connecting to [ dbuser@127.0.0.1:3306/aon_master ] .....connected!</div>"
//		+"<div>validation ..... ok!</div>"
//		+"<div>try to insert domain ( test05.aonsolutions.net , Test creacion desde python , 1 )</div>"
//		+"<div>domain inserted with id= 32</div>"
//		+"<div>application 'aon-aio' found with id  28</div>"
//		+"<div>try to insert domain application ( 32 28 )  ...... inserted with id  49</div>"
//		+"<div>commit .....</div>" 
//		+"<div>Done!</div>");
	}
	
	public void onSave( ActionEvent event) {
		Pattern p = Pattern.compile("[A-Z\\d][A-Z\\d.-]{1,61}[A-Z\\d]$",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(getDomainName());
		if (!m.matches() ) {
			String message = "El formato del nombre del dominio no es válido, debe comenzar y terminar por una letra o número. Solo puede contener letras, números y los caracteres '-' (guión) o '.' (punto).";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
		
		try {
			StringBuffer buf = new StringBuffer();
			buf.append(DOMAIN_TYPE);
			
			buf.append(PARENT_DOMAIN_ID);
			buf.append(EQUALS);
			buf.append(DomainManager.getCurrentDomain());
			
			Properties properties = DataSourceUtil.getDBProperties();
			String user = properties.getProperty(CONNECTION_USER_PROPERTY);
			if (StringUtils.isNotBlank(user)) {
				buf.append(DB_USER);
				buf.append(EQUALS);
				buf.append(user);
			}
			
			String password = properties.getProperty(CONNECTION_PASSWORD_PROPERTY);
			if (StringUtils.isNotBlank(password)) {
				buf.append(DB_PASSWORD);
				buf.append(EQUALS);
				buf.append(password);
			}
			
			String url = properties.getProperty(CONNECTION_URL_PROPERTY);
			String[] tokens = StringUtils.splitByWholeSeparator(url,"://");
			String[] hosts = StringUtils.split(tokens[1],"/");
			String[] hostsItems = StringUtils.split(hosts[0],",:");
			String host = hostsItems[0];
			String dbname =StringUtils.substringBefore(StringUtils.split(tokens[1],"/")[1],"?");
			if (StringUtils.isNotBlank(host)) {
				buf.append(DB_HOST);
				buf.append(EQUALS);
				buf.append(host);
			}
			if (StringUtils.isNotBlank(dbname)) {
				buf.append(DB_NAME);
				buf.append(EQUALS);
				buf.append(dbname);
			}
			
			LoggedUser lu = (LoggedUser) AonUtil.getRegisteredBean(ICommonConstants.LOGGED_USER_CONTROLLER_NAME);
			AuthPrincipal principal = lu.getPrincipal(); 
			Integer userId = principal.getUserId();
			IManagerBean bean = BeanManager.getManagerBean(User.class);  
			User appUser = (User) bean.get(userId);
			
			buf.append(DOMAIN_USER);
			buf.append(EQUALS);
			buf.append(appUser.getLogin());

			buf.append(DOMAIN_PASSWORD);
			buf.append(EQUALS);
			buf.append(getPassword());

			buf.append(DOMAIN_NAME);
			buf.append(EQUALS);
			buf.append(getDomainName());

			buf.append(DOMAIN_DESCRIPTION);
			buf.append(EQUALS);
			buf.append("'");
			buf.append(getDomainDescription());
			buf.append("'");
			
			String command = "newDomain.py -v " + buf.toString();
			System.out.println( command);
			Runtime r = Runtime.getRuntime();
			Process process = r.exec(command);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line = "";
			StringWriter sw = new StringWriter();
			String bef = "<div>";
			String aft = "</div>";
			while ((line = stdInput.readLine()) != null) {
				sw.append(bef);
				sw.append(line);
				sw.append(aft);
			}
			while ((line = stdError.readLine()) != null) {
				sw.append(bef);
				sw.append(line);
				sw.append(aft);
			}
			int exitVal = process.waitFor();
			sw.append(bef);
			sw.append("Process return code ..: " + exitVal);
			sw.append(aft);
			output =  sw.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private String parseDatabaseName(String url) throws URISyntaxException {
		URI u = new URI(url);
		return u.getPath();
	}
	private String parseHost(String url) throws URISyntaxException {
		URI u = new URI(url);
		return u.getHost();
	}
	
}
