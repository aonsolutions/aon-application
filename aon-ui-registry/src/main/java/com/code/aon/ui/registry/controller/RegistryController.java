package com.code.aon.ui.registry.controller;

import static com.code.aon.ui.registry.controller.IRegistryConstants.BUNDLE_NAME;
import static com.code.aon.ui.registry.controller.IRegistryConstants.REGISTRY_DOCUMENT_ERROR;

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
import com.code.aon.common.enumeration.Country;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
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
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
		messageController.initNewMessage();
		FacesContext context = FacesContext.getCurrentInstance();
		Object email = context.getExternalContext().getRequestParameterMap().get("email");
		messageController.setRecipientsTo(email.toString());
	}

	public void initDocument() {
		Registry registry = ((IRegistry) getTo()).getRegistry();
		registry.setType(RegistryType.LEGAL);
		registry.setNationality(Country.ES);
		registry.setDocumentCountry(Country.ES);
		registry.setDocumentType(DocumentType.CIF);		
	}	
	
	public void onChangeRegistryType(ActionEvent event) {
		IRegistry iRegistry = (IRegistry) getTo();
		iRegistry.getRegistry().setDocumentType(iRegistry.getRegistry().getType() == RegistryType.LEGAL ? DocumentType.CIF : DocumentType.NIF);
	}
	
	public void onChangeDocument(ActionEvent event) {
		IRegistry iRegistry = (IRegistry) getTo();
		iRegistry.getRegistry().setType(iRegistry.getRegistry().getDocumentType() == DocumentType.CIF ? RegistryType.LEGAL : RegistryType.NATURAL);

		try {
			if (isNew()) {
				RegistryController.validateDocument(iRegistry, getManagerBean());
			}
		} catch (ManagerBeanException e) {
			LOGGER.warn("unable to check Document.",e);
		}
	}
	
	public static void validateDocument(IRegistry iRegistry, IManagerBean bean) throws ManagerBeanException {
		validateDocument(iRegistry.getRegistry(), ClassUtils.getShortClassName(bean.getPOJOClass()) + ".registry", bean);
	}
	
	public static void validateDocument(Registry registry, String preffix, IManagerBean bean) throws ManagerBeanException {
		if (StringUtils.isNotEmpty(registry.getDocument())) {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(preffix + ".document", registry.getDocument());
			criteria.addEqualExpression(preffix + ".documentCountry", registry.getDocumentCountry());
			List<ITransferObject> list = bean.getList(criteria);
			if (list.size() > 0 ) {
				String msg = AonUtil.getMessage(BUNDLE_NAME, REGISTRY_DOCUMENT_ERROR); 
				AonUtil.addWarningMessage(msg + " " + registry.getDocument());
			}
		}
	}	
	
}