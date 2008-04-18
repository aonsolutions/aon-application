package com.code.aon.ui.desktop.apps.controller;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IApplication;
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
			List list = um.getUserApplications();
            for (int i = 0; i < list.size(); i++) {
				IApplication app = (IApplication)list.get(i);
                if (app != null && noContext.indexOf(app.getContext()) < 0) {
                	String context = app.getContext();
                	context = context.replaceAll(".war", "");
                	String name = app.getDescription();
                	System.out.println(">>>>>>>>>>>>>>>>> ctx = " + context  + " -- desc = " + name );
                	if (name == null){
                		name = context.replaceAll("aon-", "");
                		name = context.replaceAll("/", "");
                	}
                	System.out.println(">>>>>>>>>>>>>>>>> ctx = " + context  + " -- desc = " + name );
                	App a = new App( name, ec.getRequestContextPath() + context + ".auth");
					applicationList.add(a);
				}
			}
		} catch (DeploymentException e) {
			e.printStackTrace();
		}
		return applicationList;
	}
}