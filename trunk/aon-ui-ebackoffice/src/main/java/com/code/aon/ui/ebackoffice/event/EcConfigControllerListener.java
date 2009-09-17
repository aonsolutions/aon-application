package com.code.aon.ui.ebackoffice.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ebackoffice.Eccatalogue;
import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.product.ItemAttachment;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.ebackoffice.controller.EccatalogueController;
import com.code.aon.ui.ebackoffice.controller.EcconfigController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.product.controller.ItemAttachController;
import com.code.aon.ui.util.AonUtil;
import com.sun.faces.util.MessageFactory;

public class EcConfigControllerListener extends ControllerAdapter implements
		IItemConstants {
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		EcconfigController controller = (EcconfigController) event
				.getController();
		AonFile aonFile1 = controller.getAonFile1();
		
		((Ecconfig) controller.getTo()).setHeaderImg(aonFile1.getData());
	
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		EcconfigController controller = (EcconfigController) event
				.getController();
		AonFile aonFile1 = controller.getAonFile1();
	
			
		if (aonFile1 != null) {
			((Ecconfig) controller.getTo()).setHeaderImg(aonFile1.getData());
			
		}
		
	}

}