package com.code.aon.ui.desktop.applications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailUtil;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonServer;

public class WebmailManager implements IServices {

	private ApplicationsManager.App app;

    private AonServer mail_server;

	public WebmailManager() throws DeploymentException, IOException {
		ApplicationsManager apps = (ApplicationsManager) AonUtil.getRegisteredBean( ApplicationsManager.BEAN_NAME );
		app = apps.getApplication( "aon-webmail" );
		if ( app != null && isExecutable() ) {
			try {
				AuthPrincipal user = Utils.getAuthPrincipal();
				MailAccount mailAccount = WebmailUtil.getDefaultAccount(user.getDomain(),user.getShortName());
				this.mail_server = new AonServer(mailAccount);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    public List<AonFolder> getMailSummaryModel() {
    	List<AonFolder> result = new ArrayList<AonFolder>();
		if ( mail_server != null ) {
			AonFolder folder = mail_server.getAonFolder( AonFolder.INBOX_FOLDER_NAME );
			result.add(folder);
			return result;
		}
		return null;
    }

    public boolean isMailActive() {
    	try {
	    	if ( mail_server != null ) {
				AonFolder folder = mail_server.getAonFolder( AonFolder.INBOX_FOLDER_NAME );
				if ( folder != null ) return true;
			}
    	} catch (Exception e) {
    		e.printStackTrace();
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
