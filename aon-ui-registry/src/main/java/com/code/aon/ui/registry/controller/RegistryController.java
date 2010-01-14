package com.code.aon.ui.registry.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

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
			IRegistry iRegistry = (IRegistry) getTo();
			if (isNew()) {
				String document = iRegistry.getRegistry().getDocument();
				if (StringUtils.isNotEmpty(document)) {
					Criteria criteria = new Criteria();
					String alias = getManagerBean().getFieldName(
							getPojoShortName() + "_registry_document");
					criteria.addEqualExpression(alias, document);
					List<ITransferObject> list = getManagerBean().getList(criteria);
					if (list.size() > 0 ) {
						AonUtil.addWarningMessage("Ya existe un registro para el NIF: " + document);
					}
				}
			}
		} catch (ManagerBeanException e) {
			// DO
		}
	}
}