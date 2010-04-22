package com.code.aon.ui.registry.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;

/**
 * Controller used in the registry maintenance.
 */
public class RegistryController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(RegistryController.class);
	
	private String selectedTab;

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public boolean isNaturalType() {
		IRegistry iRegistry = (IRegistry) getTo();
		return iRegistry.getRegistry().getType().equals(RegistryType.NATURAL);
	}

	public void onSendEmail(ActionEvent event) {
		MessageController messageController = (MessageController) AonUtil
				.getRegisteredBean(WebMailConstants.BEAN_MESSAGE);
		messageController.initNewMessage();
		FacesContext context = FacesContext.getCurrentInstance();
		Object email = context.getExternalContext().getRequestParameterMap().get("email");
		messageController.setRecipientsTo(email.toString());
	}

	public void onChangeDocument(ActionEvent event) {
		try {
			if (isNew()) {
				IRegistry iRegistry = (IRegistry) getTo();
				RegistryController.validateDocument(iRegistry,getManagerBean());
			}
		} catch (ManagerBeanException e) {
			LOGGER.warn("unable to check Document.",e);
		}
	}
	
	public static void validateDocument(IRegistry iRegistry, IManagerBean bean) throws ManagerBeanException {
		String document = iRegistry.getRegistry().getDocument();
		if (StringUtils.isNotEmpty(document)) {
			Criteria criteria = new Criteria();
			String alias = bean.getFieldName(
					ClassUtils.getShortClassName(bean.getPOJOClass())
					+ "_registry_document");
			criteria.addEqualExpression(alias, document);
			List<ITransferObject> list = bean.getList(criteria);
			if (list.size() > 0 ) {
				String msg = AonUtil.getMessage("registryBundle", "registry_document_error"); 
				AonUtil.addWarningMessage(msg + " " + document);
			}
		}
	}
}