package com.code.aon.ui.registry.controller;

import static com.code.aon.ui.registry.controller.IRegistryConstants.BUNDLE_NAME;
import static com.code.aon.ui.registry.controller.IRegistryConstants.REGISTRY_DOCUMENT_ERROR;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;

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
import com.code.aon.registry.Registry;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.MessageController;

public class RegistryController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(RegistryController.class);
	
	private String selectedTab;
	
	private boolean showNewFeeWindow;
	
	private boolean showNewProjectWindow;

	private boolean showNewNoteWindow;
	
	private boolean showNewRelationshipWindow;

	private boolean showNewRecordDataWindow;
	
	private boolean showNewSegmentWindow;
	
	private boolean showNewAddInfoWindow;

	private boolean showNewDirStaffWindow;
	
	private boolean showNewTargetItemWindow;
	
	private boolean showNewTargetProfileWindow;
	
	private boolean showNewTargetSellerWindow;
	
	private boolean showNewTargetSupplierWindow;
	
	private boolean showNewDocumentWindow;

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public boolean isShowNewFeeWindow() {
		return showNewFeeWindow;
	}

	public void setShowNewFeeWindow(boolean showNewFeeWindow) {
		this.showNewFeeWindow = showNewFeeWindow;
	}

	public boolean isShowNewNoteWindow() {
		return showNewNoteWindow;
	}

	public void setShowNewNoteWindow(boolean showNewNoteWindow) {
		this.showNewNoteWindow = showNewNoteWindow;
	}

	public boolean isShowNewRecordDataWindow() {
		return showNewRecordDataWindow;
	}

	public boolean isShowNewRelationshipWindow() {
		return showNewRelationshipWindow;
	}

	public void setShowNewRelationshipWindow(boolean showNewRelationshipWindow) {
		this.showNewRelationshipWindow = showNewRelationshipWindow;
	}

	public void setShowNewRecordDataWindow(boolean showNewRecordDataWindow) {
		this.showNewRecordDataWindow = showNewRecordDataWindow;
	}

	public boolean isShowNewSegmentWindow() {
		return showNewSegmentWindow;
	}

	public void setShowNewSegmentWindow(boolean showNewSegmentWindow) {
		this.showNewSegmentWindow = showNewSegmentWindow;
	}

	public boolean isShowNewAddInfoWindow() {
		return showNewAddInfoWindow;
	}

	public void setShowNewAddInfoWindow(boolean showNewAddInfoWindow) {
		this.showNewAddInfoWindow = showNewAddInfoWindow;
	}

	public boolean isShowNewDirStaffWindow() {
		return showNewDirStaffWindow;
	}

	public void setShowNewDirStaffWindow(boolean showNewDirStaffWindow) {
		this.showNewDirStaffWindow = showNewDirStaffWindow;
	}

	public boolean isShowNewProjectWindow() {
		return showNewProjectWindow;
	}

	public void setShowNewProjectWindow(boolean showNewProjectWindow) {
		this.showNewProjectWindow = showNewProjectWindow;
	}
	
	public boolean isShowNewTargetItemWindow() {
		return showNewTargetItemWindow;
	}

	public void setShowNewTargetItemWindow(boolean showNewTargetItemWindow) {
		this.showNewTargetItemWindow = showNewTargetItemWindow;
	}

	public boolean isShowNewTargetProfileWindow() {
		return showNewTargetProfileWindow;
	}

	public void setShowNewTargetProfileWindow(boolean showNewTargetProfileWindow) {
		this.showNewTargetProfileWindow = showNewTargetProfileWindow;
	}

	public boolean isShowNewTargetSellerWindow() {
		return showNewTargetSellerWindow;
	}

	public void setShowNewTargetSellerWindow(boolean showNewTargetSellerWindow) {
		this.showNewTargetSellerWindow = showNewTargetSellerWindow;
	}

	public boolean isShowNewTargetSupplierWindow() {
		return showNewTargetSupplierWindow;
	}

	public void setShowNewTargetSupplierWindow(boolean showNewTargetSupplierWindow) {
		this.showNewTargetSupplierWindow = showNewTargetSupplierWindow;
	}

	public boolean isShowNewDocumentWindow() {
		return showNewDocumentWindow;
	}

	public void setShowNewDocumentWindow(boolean showNewDocumentWindow) {
		this.showNewDocumentWindow = showNewDocumentWindow;
	}

	public boolean isNaturalType() {
		IRegistry iRegistry = (IRegistry) getTo();
		return iRegistry.getRegistry().getType().equals(RegistryType.NATURAL);
	}

	public void onSendEmail(ActionEvent event) {
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		if (mailConfig.getMailAccountCount() > 0) {
			MessageController messageController = (MessageController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
			messageController.initNewMessage();
			FacesContext context = FacesContext.getCurrentInstance();
			Object email = context.getExternalContext().getRequestParameterMap().get("email");
			messageController.setRecipientsTo(email.toString());
		} else {
			AonUtil.addErrorMessageFromBundle(IWebMailConstants.BUNDLE_NAME, IWebMailConstants.NOT_MAIL_ACCOUNTS);
		}
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