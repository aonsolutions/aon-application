package com.code.aon.ui.infoweb.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.GridController;
import com.code.aon.infoweb.WebInfoPage;
import com.code.aon.infoweb.WebInfoPageDetail;
import com.code.aon.infoweb.WebInfoPageResource;
import com.code.aon.infoweb.dao.IWebInfoAlias;
import com.code.aon.infoweb.enumeration.WebInfoPageType;

public class CompanyWebInfoPageController extends GridController {

	public boolean showGenericModalPanel = false;

	public boolean showLocationModalPanel = false;

	public boolean showResourceModalPanel = false;
	
	public boolean richTextEnabled = false;

	public WebInfoPage current;
	
	public WebInfoPageDetail detail;

	public boolean newResource = false;
	
	public String rattachId;

	public ListDataModel resources;
	
	public WebInfoPageResource resource;

	public CompanyWebInfoPageController() {
		super();
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event){
		cancel(event);
		super.onSelect(event);
	}

	/* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onAccept(javax.faces.event.ActionEvent)
     */
    public void onAccept(ActionEvent event) {
    	accept(event);
    	onSelectDetail(event);
    	cancel(event);
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

    public boolean isDetailed() {
		WebInfoPage wip = (WebInfoPage)this.model.getRowData();
		if (wip.getType().equals(WebInfoPageType.GALLERY)) return true;
		if (wip.getType().equals(WebInfoPageType.GENERIC)) return true;
		if (wip.getType().equals(WebInfoPageType.LOCATION)) return true;
		return false;
    }

	public void onSelectDetail(ActionEvent event) {
		WebInfoPage wip = (WebInfoPage)this.model.getRowData();
		current = wip;
		setSelectedData(wip);
		if (wip.getType().equals(WebInfoPageType.GENERIC)) {
			setShowGenericModalPanel(true);
		}
		else {
			if (wip.getType().equals(WebInfoPageType.LOCATION)) {
				setShowLocationModalPanel(true);
			}
			else {
				if (wip.getType().equals(WebInfoPageType.GALLERY)) {
					setShowResourceModalPanel(true);
				}
			}
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
		}
		catch (ManagerBeanException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
    }

    public void onResetResource(ActionEvent event) {
    	resource = new WebInfoPageResource();
    	resource.setRattach(new RegistryAttachment());
    	setNewResource(true);
    }

    public void onCancelResource(ActionEvent event) {
		System.out.println(">>>>>>>>>>>> RESOURCE: " + resource.getContent() + " CANCELED.");
		resource = new WebInfoPageResource();
    	setNewResource(false);
    }

	@SuppressWarnings("unused")
	public void onSelectResource(ActionEvent event) {
		WebInfoPageResource wipr = (WebInfoPageResource)this.resources.getRowData();
		resource = wipr;
		rattachId = ""+resource.getRattach().getId();
		setNewResource(false);
		System.out.println(">>>>>>>>>>>> RESOURCE: " + resource.getContent() + " WITH IMAGE " + resource.getRattach().getId() + " SELECTED.");
	}

	/* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onAccept(javax.faces.event.ActionEvent)
     */
    public void onAcceptResource(ActionEvent event) {
		try {
			IManagerBean wiprBean = BeanManager.getManagerBean(WebInfoPageResource.class);
			System.out.println(">>>>>> INSERTANDO EN BASE DE DATOS " + resource.getContent() + " CON LA IMAGEN "+ rattachId + ".");
			resource.setWebInfoPage(current);
			RegistryAttachment ra = new RegistryAttachment();
			ra.setId(Integer.parseInt(rattachId));
			resource.setRattach(ra);
			if (resource.getId() != null) wiprBean.update(resource);
			else wiprBean.insert(resource);
			setSelectedData(current);
			setNewResource(false);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
    }

	/* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onAccept(javax.faces.event.ActionEvent)
     */
    public void onRemoveResource(ActionEvent event) {
		try {
			IManagerBean wiprBean = BeanManager.getManagerBean(WebInfoPageResource.class);
			wiprBean.remove(resource);
			resource = new WebInfoPageResource();
			setSelectedData(current);
			setNewResource(false);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
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
			Integer id = rattach.getId();
			String name = rattach.getDescription();
			System.out.println(">>>>>>>>>>>>>> " + id + " --- " + name + " <<<<<<<<<<<<<<<<");
			SelectItem item = new SelectItem(id, name);
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

	public String getRattachId() {
		System.out.println(">>>>>>>>>>>>>> GETTING RATTACH ID: " + rattachId);
		return rattachId;
	}

	public void setRattachId(String rattachId) {
		System.out.println(">>>>>>>>>>>>>> SETTING RATTACH ID: " + rattachId);
		this.rattachId = rattachId;
	}

	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}

}