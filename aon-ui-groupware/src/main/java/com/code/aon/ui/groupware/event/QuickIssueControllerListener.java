package com.code.aon.ui.groupware.event;

import com.code.aon.AonVersion;
import com.code.aon.groupware.Notice;
import com.code.aon.groupware.enumeration.NoticeStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.QuickIssueController;

public class QuickIssueControllerListener extends NoticeControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    
    @Override
    public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
    	super.afterBeanCreated(event);
    	QuickIssueController controller = (QuickIssueController)event.getController();
    	Notice notice = (Notice)controller.getTo();
    	notice.setType(NoticeType.ISSUE);
		notice.setStatus(NoticeStatus.OPEN);
		notice.setPriority(Priority.NONE);
		controller.setSendMail(false);
	}
    
    @Override
    public void afterBeanAdded(ControllerEvent event)
    		throws ControllerListenerException {
    	super.afterBeanAdded(event);
    	QuickIssueController controller = (QuickIssueController)event.getController();
    	controller.setShowNewissueWindow(false);
    }

}