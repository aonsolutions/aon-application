package com.code.aon.ui.desktop.applications;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.desktop.controller.AonUserController;
import com.code.aon.desktop.controller.DomainController;
import com.code.aon.jaas.auth.util.Util;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class ApplicationsManager implements IDesktopConstants {

	public static final String AON_CMS = "aon-cms";
	
	private List<App> applicationList;

	@SuppressWarnings("unchecked")
	public ApplicationsManager() throws DeploymentException, IOException {
		Properties services = new Properties();
		InputStream is = ApplicationsManager.class.getResourceAsStream( "services.properties" );
		services.load(is);
		applicationList = new ArrayList<App>();
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		
		String thisIp = InetAddress.getLocalHost().getHostAddress();

		
		AonUserController aonUserController = (AonUserController) FormUtil.getController( "currentUser" );
		List list = aonUserController.getUserManager().getUserApplications();
        for (int i = 0; i < list.size(); i++) {
			IApplication app = (IApplication) list.get(i);
			String context = null;
			if ( AON_CMS.equals(app.getId()) ) {
				AonUserController auc = (AonUserController) AonUtil.getRegisteredBean(CURRENT_USER_CONTROLLER_NAME);
				context = DomainController.getCMSDomainURL(auc.getDomain());
			} else {
				context = app.getContext() + "/?aonDesktop=true";
				String ip = Util.findStoredApplicationIp( thisIp, app.getContext() );
				if ( !thisIp.equals( ip ) ) {
					context = ec.getRequestContextPath() + app.getContext() + ".auth?aonDesktop=true";
				}
			}
			String property = services.getProperty( app.getId() );
			App application;
			if ( property != null ) {
				char[] bar = property.substring( 0, property.indexOf( ';' ) ).toCharArray();
				String role = property.substring( property.indexOf( ';' ) + 1 , property.length() );
				boolean isUserInRole = role.equals("") || ec.isUserInRole( role );
				application = new App( app.getId(), app.getDescription(), context, bar, isUserInRole );
			} else {
				application = new App( app.getId(), app.getDescription(), context, new char[] {'1','0','0'}, true );
			}
			applicationList.add(application);
        }
        Collections.sort( applicationList );
	}

	public List<App> getApplicationList(){
		return applicationList;
	}

	public App getApplication(String cn) {
        for (int i = 0; i < applicationList.size(); i++) {
			App app = (App) applicationList.get(i);
			if ( app.getId().equals( cn ) )
				return app;
		}
        return null;
	}

	public class App implements Comparable<App> {

		private String id;

		private String name;

		private String context;

		private boolean infobar;
		private boolean sidebar;
		private boolean toolbar;
		private boolean executable = true;

		public App(String cn, String name, String context, char[] bar, boolean role) {
			this.id = cn;
			this.name = name;
			this.context = context;
			this.infobar = bar[2] == '1' && role;
			this.sidebar = bar[1] == '1' && role;
			this.toolbar = bar[0] == '1' && role;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getContext() {
			return context;
		}

		public boolean isExecutable() {
			return executable;
		}

		public boolean isInfobarEnabled() {
			return infobar;
		}

		public boolean isSidebarEnabled() {
			return sidebar;
		}

		public boolean isToolbarEnabled() {
			return toolbar;
		}

		@Override
		public int compareTo(App o) {
			return name.compareTo(o.getName());
		}

	}

}