package com.code.aon.ui.desktop.applications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailUtil;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonServer;

public class WebmailManager implements IServices, IDesktopConstants {
	
	private static final Logger LOGGER = Logger.getLogger( WebmailManager.class.getName() );

	private ApplicationsManager.App app;

    private AonServer webmailServer;

	public WebmailManager() throws DeploymentException, IOException {
		ApplicationsManager apps = (ApplicationsManager) AonUtil.getRegisteredBean( APPLICATIONS_CONTROLLER_NAME );
		app = apps.getApplication( "aon-webmail" );
		if ( app != null && isExecutable() ) {
			try {
				AuthPrincipal user = Utils.getAuthPrincipal();
				MailAccount mailAccount = WebmailUtil.getDefaultAccount(user.getDomain(),user.getShortName());
				this.webmailServer = new AonServer(mailAccount);
				this.webmailServer.connect();
			} catch (Throwable th) {
				LOGGER.log(Level.SEVERE, "Error on Webmail init", th);
			}
		}
	}

    public List<AonFolder> getMailSummaryModel() {
    	List<AonFolder> result = new ArrayList<AonFolder>();
		if ( webmailServer != null ) {
			AonFolder folder = webmailServer.getAonFolder( AonFolder.INBOX_FOLDER_NAME );
			result.add(folder);
			return result;
		}
		return null;
    }

    public boolean isMailActive() {
    	try {
	    	if ( (webmailServer != null) && (webmailServer.isConnected()) ) {
				AonFolder folder = webmailServer.getAonFolder( AonFolder.INBOX_FOLDER_NAME );
				if ( folder != null ) {
					return true;
				}
			}
		} catch (Throwable th) {
    		LOGGER.log(Level.SEVERE, "Error on Webmail init", th);
    	}
		return false;
    }

 // ************************************** IServices methods implementation *************************************
	public boolean isExecutable() {
		return app != null && app.isExecutable();
	}

	public boolean isInfobarEnabled() {
		return app != null && app.isInfobarEnabled();
	}

	public boolean isSidebarEnabled() {
		return app != null && app.isSidebarEnabled();
	}

	public boolean isToolbarEnabled() {
		return app != null && app.isToolbarEnabled();
	}
// ********************************** End of IServices methods implementation **********************************
}
