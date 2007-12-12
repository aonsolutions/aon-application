package com.code.aon.ui.desktop.apps.controller;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.code.aon.bridge.plugin.UserUtils;
import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.core.Application;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ui.form.BasicController;

public class AonApplicationController extends BasicController {

	public class App {
		
		private String name;
		
		private String context;

		public App(String name, String context) {
			this.name = name;
			this.context = context;
		}

		public String getName() {
			return name;
		}

		public String getContext() {
			return context;
		}

	}
	
	private List<App> applicationList;
	

	public List<App> getApplicationList(){
		if(applicationList == null){
			applicationList = obtainAppList();
		}
		return applicationList;
	}

	@SuppressWarnings("unchecked")
	private List<App> obtainAppList() {
		List<App> applicationList = new LinkedList<App>();
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		AuthPrincipal user = null;
		Principal principal = ec.getUserPrincipal();
		String noContext = ec.getRequestContextPath();
		noContext = noContext + "|/aon-webmail.war";
		if ( principal instanceof AuthPrincipal ) {
			user = (AuthPrincipal) principal;
		} else {
			user = new AuthPrincipal( principal.getName() );
		}
		UserManager um = new UserManager();
		um.findUser(user.getShortName());
		try {
			UserUtils.addSharingUserCookies((HttpServletResponse)ec.getResponse(), ec.getUserPrincipal());
			String jsessionid = ((HttpSession)ec.getSession(false)).getId();
			List list = um.getUserApplications();
            for (int i = 0; i < list.size(); i++) {
				Application app = (Application)list.get(i);
                if (app != null && noContext.indexOf(app.getContext()) < 0) {
                	String name = app.getId();
                	name = name.replaceAll("aon-", "");
                	name = name.replaceAll(".war", "");
                	App a = new App(name, app.getContext() + "/webauthentication.auth?JSESSIONID=" + jsessionid);
					applicationList.add(a);
				}
			}
		} catch (DeploymentException e) {
			e.printStackTrace();
		}
		return applicationList;
	}
}