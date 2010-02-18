package com.code.aon.ui.infoweb.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.infoweb.WebInfoPage;
import com.code.aon.infoweb.WebInfoPageDetail;
import com.code.aon.infoweb.WebInfoPageResource;
import com.code.aon.infoweb.dao.IWebInfoAlias;
import com.code.aon.infoweb.enumeration.WebInfoPageType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class CompanyWebInfoPageController extends BasicController implements IInfoWebConstants {
	
	private static final Logger LOGGER = Logger.getLogger(CompanyWebInfoPageController.class.getName());

	public boolean showGenericModalPanel;

	public boolean showLocationModalPanel;

	public boolean showResourceModalPanel;
	
	public boolean richTextEnabled;

	public WebInfoPage current;
	
	public WebInfoPageDetail detail;

	public boolean newResource;
	
	public RegistryAttachment attachment;

	public ListDataModel resources;
	
	public WebInfoPageResource resource;
	
	public CompanyWebInfoPageController() {
		this.richTextEnabled = true;
	}

	public int getLastPosition() {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addOrder(getManagerBean().getFieldName(IWebInfoAlias.WEB_INFO_PAGE_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)getManagerBean().getList(criteria);
			if (list.size() > 0) {
				WebInfoPage wip = (WebInfoPage)list.get(0);
				position = wip.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return position;
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		WebInfoPage wip = (WebInfoPage)this.model.getRowData();
		wip.setActive(active);
		getManagerBean().update(wip);
	}
	
	@SuppressWarnings("unchecked")
	private void move( WebInfoPage wip, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = wip.getPosition();
		int newPosition = oldPosition + movement;
		wip.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(IWebInfoAlias.WEB_INFO_PAGE_ID), ""+wip.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			WebInfoPage wipage = (WebInfoPage)list.get(0);
			wipage.setPosition(newPosition);
			getManagerBean().update(wipage);
		}
    	List<WebInfoPage> listObjects = (List<WebInfoPage>) this.model.getWrappedData();
    	WebInfoPage wipMoved = listObjects.get( newPosition );
		wipMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(IWebInfoAlias.WEB_INFO_PAGE_ID), ""+wipMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			WebInfoPage option = (WebInfoPage)list.get(0);
			option.setPosition(oldPosition);
			getManagerBean().update(option);
		}
		listObjects.set( newPosition, wip);
		listObjects.set( oldPosition, wipMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	move((WebInfoPage) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	move((WebInfoPage) this.model.getRowData(), 1);    	
    }


	public void onSelectDetail(ActionEvent event) {
		WebInfoPage wip = (WebInfoPage)this.model.getRowData();
		current = wip;
		setSelectedData(wip);
		if (wip.getType().equals(WebInfoPageType.GENERIC)) {
			setShowGenericModalPanel(true);
		} else if (wip.getType().equals(WebInfoPageType.LOCATION)) {
			setShowLocationModalPanel(true);
		} else if (wip.getType().equals(WebInfoPageType.GALLERY)) {
			setShowResourceModalPanel(true);
		}
	}

	public boolean isShowGenericModalPanel() {
		return showGenericModalPanel;
	}

	public void setShowGenericModalPanel(boolean showGenericModalPanel) {
		this.showGenericModalPanel = showGenericModalPanel;
	}

	public boolean isShowLocationModalPanel() {
		return showLocationModalPanel;
	}

	public void setShowLocationModalPanel(boolean showLocationModalPanel) {
		this.showLocationModalPanel = showLocationModalPanel;
	}

	public boolean isShowResourceModalPanel() {
		return showResourceModalPanel;
	}

	public void setShowResourceModalPanel(boolean showResourceModalPanel) {
		this.showResourceModalPanel = showResourceModalPanel;
	}

	public void setSelectedData(WebInfoPage wip) {
		try {
			IManagerBean wipdBean = BeanManager.getManagerBean(WebInfoPageDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(wipdBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE_ID), wip.getId());
			List<ITransferObject> listWipd = wipdBean.getList(criteria);
			if (listWipd.size() > 0) detail = (WebInfoPageDetail)listWipd.get(0);
			else detail = new WebInfoPageDetail();

			IManagerBean wiprBean = BeanManager.getManagerBean(WebInfoPageResource.class);
			criteria = new Criteria();
			criteria.addEqualExpression(wiprBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE_ID), wip.getId());
			List<ITransferObject> listWipr = wiprBean.getList(criteria);
			resources = new ListDataModel(listWipr);
			resetResource();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public WebInfoPageDetail getDetail() {
		return detail;
	}
	
	/* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onAccept(javax.faces.event.ActionEvent)
     */
    public void onAcceptDetail(ActionEvent event) {
		try {
			IManagerBean wipdBean = BeanManager.getManagerBean(WebInfoPageDetail.class);
			detail.setWebInfoPage(current);
			if (detail.getId() != null) wipdBean.update(detail);
			else wipdBean.insert(detail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
    }

    public void onResetResource(ActionEvent event) {
    	resource = new WebInfoPageResource();
    	resource.setRattach(new RegistryAttachment());
    	setNewResource(true);
    }

    public void onCancelResource(ActionEvent event) {
		LOGGER.fine(">>>>>>>>>>>> RESOURCE: " + resource.getContent() + " CANCELED.");
		resource = new WebInfoPageResource();
		resetResource();
    }

	@SuppressWarnings("unused")
	public void onSelectResource(ActionEvent event) {
		WebInfoPageResource wipr = (WebInfoPageResource)this.resources.getRowData();
		resource = wipr; 
		attachment = resource.getRattach();
		setNewResource(false);
	}

	/* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onAccept(javax.faces.event.ActionEvent)
     */
    public void onAcceptResource(ActionEvent event) {
		try {
			IManagerBean wiprBean = BeanManager.getManagerBean(WebInfoPageResource.class);
			resource.setWebInfoPage(current);
			resource.setRattach(attachment);
			wiprBean.insertOrUpdate(resource);
			setSelectedData(current);
			resetResource();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
    }

	/* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onAccept(javax.faces.event.ActionEvent)
     */
    public void onRemoveResource(ActionEvent event) {
		try {
			IManagerBean wiprBean = BeanManager.getManagerBean(WebInfoPageResource.class);
			wiprBean.remove(resource);
			setSelectedData(current);
			resetResource();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
    }

	public List<SelectItem> getImages() throws ManagerBeanException {
		List<SelectItem> images = new LinkedList<SelectItem>();
		IManagerBean rattachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rattachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ADDITIONAL_IMAGE);
		List<ITransferObject> list = rattachBean.getList(criteria);
		for (int i=0; i <list.size(); i++) {
			RegistryAttachment rattach = (RegistryAttachment)list.get(i);
			SelectItem item = new SelectItem(rattach, rattach.getDescription());
			images.add(item);
		}
		return images;
	}

	public ListDataModel getResources() {
		return resources;
	}

	public WebInfoPageResource getResource() {
		return resource;
	}

	public void setResource(WebInfoPageResource resource) {
		this.resource = resource;
	}

	public boolean isNewResource() {
		return newResource;
	}

	public void setNewResource(boolean newResource) {
		this.newResource = newResource;
	}

	public RegistryAttachment getAttachment() {
		return attachment;
	}

	public void setAttachment(RegistryAttachment attachment) {
		this.attachment = attachment;
	}

	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}

	private void resetResource() {
		setResource(null);
		setNewResource(false);
	}
	
	public void pageNameCheck(FacesContext context, UIComponent component, Object value) throws ManagerBeanException {
		String domainName = value.toString();
		IManagerBean bean = getManagerBean();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_NAME), domainName);
		int count = bean.getCount(criteria);
		if ( count > 0 ) {
			FacesMessage message = new FacesMessage(AonUtil.getMessage(BUNDLE_NAME, "infoweb_page_duplicated_name"));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException( message );
		}
		for( int i = 0; i < domainName.length(); i++ ) {
			char c = domainName.charAt(i);
			if (! (Character.isLetter(c) || Character.isDigit(c) || (c == ' ') ) ) {
				String text = AonUtil.getMessage(BUNDLE_NAME, "infoweb_page_invalid_character");
				String formatted = AonUtil.substituteParams(AonUtil.getCurrentLocale(), text, new Object[]{c});
				FacesMessage message = new FacesMessage(formatted);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);				
				throw new ValidatorException( message );										
			}
		}
	}
	
}