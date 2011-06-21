package com.code.aon.ui.desktop.applications;

import static com.code.aon.webmail.bean.IMailConstants.INBOX_FOLDER_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailUtil;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonServer;

public class WebmailManager implements IServices, IDesktopConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(WebmailManager.class);

	private ApplicationsManager.App app;

    private AonServer server;
    
    private List<AonFolder> mailSummaryModel;

	public WebmailManager() {
		ApplicationsManager apps = (ApplicationsManager) AonUtil.getRegisteredBean( APPLICATIONS_CONTROLLER_NAME );
		app = apps.getApplication( "aon-webmail" );
		if ( app != null ) {
			try {
				AuthPrincipal user = Utils.getAuthPrincipal();
				MailAccount mailAccount = WebmailUtil.getDefaultAccount(user.getDomain(),user.getShortName());
				server = new AonServer(mailAccount);
				server.connect();
				updateMailSummaryModel();
			} catch (Throwable th) {
				LOGGER.error("Error on Webmail init", th);
				if ( server != null ) {
					server.disconnect();
					server = null;
				}
			}
		}
	}

    private void updateMailSummaryModel() {
		AonFolder folder = server.getAonFolder( INBOX_FOLDER_NAME );
		if ( folder != null ) {
			mailSummaryModel = new LinkedList<AonFolder>();
			mailSummaryModel.add(folder);
		}
    }
    
    public List<AonFolder> getMailSummaryModel() {
		if ( mailSummaryModel != null ) {
			try {
				server.ensureConnection();
			} catch (MessagingException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return mailSummaryModel;
    }

    public boolean isMailActive() {
		return (mailSummaryModel != null);
    }

    public void disconect() {
    	if ( server != null ) {
    		server.disconnect();
    	}
    }
    
 // ************************************** IServices methods implementation *************************************
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
