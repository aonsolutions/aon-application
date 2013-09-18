package com.code.aon.ui.infoweb.controller;

import static com.code.aon.ui.common.ICommonMessages.INFOWEB_BUNDLE;
import static com.code.aon.ui.common.ICommonMessages.PAGE_DUPLICATED_NAME;
import static com.code.aon.ui.common.ICommonMessages.PAGE_INVALID_CHARACTER;

import java.util.LinkedList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.infoweb.WebInfoPage;
import com.code.aon.infoweb.WebInfoPageDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CompanyWebInfoPageController extends BasicController implements IInfoWebConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyWebInfoPageController.class.getName());
	
	private static final char[] VALID_CHARS = new char[] {' ', '-', '_', '(', ')', '$', '&', '{', '}'};

	public boolean showGenericModalPanel;

	public boolean showLocationModalPanel;

	public boolean showResourceModalPanel;
	
	public boolean richTextEnabled;

	public WebInfoPage current;
	
	public WebInfoPageDetail detail;
	
	public CompanyWebInfoPageController() {
		this.richTextEnabled = true;
	}

    public void onInit(ActionEvent event) throws ManagerBeanException {
    	this.showGenericModalPanel = false;
    	this.showLocationModalPanel = false;
    	this.showResourceModalPanel = false;
    	reorderObjects();
    }	
	
	public int getLastPosition() {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.WEB_INFO_PAGE_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)getManagerBean().getList(criteria);
			if (list.size() > 0) {
				WebInfoPage wip = (WebInfoPage)list.get(0);
				position = wip.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return position;
	}

	public void onActivate(ActionEvent event) {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) {
		activate(false);
	}
	
	private void activate(boolean active) {
		WebInfoPage wip = null;
		try {
			wip = (WebInfoPage) getSelectedTO();
			wip.setActive(active);
			getManagerBean().update(wip);
		} catch (ManagerBeanException e) {
			LOGGER.error("activate", e);
			addMessage( "Error cambiando el estado activo de la pagina " + wip.getName() );
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void move( WebInfoPage wip, int movement ) {
		try {		
			int oldPosition = wip.getPosition();
			int newPosition = oldPosition + movement;
			
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IEntityAlias.WEB_INFO_PAGE_POSITION), newPosition);
			List<ITransferObject> list = getManagerBean().getList(criteria);
			if (!list.isEmpty()) {
				WebInfoPage otherPage = (WebInfoPage) list.get(0);
				otherPage.setPosition(oldPosition);
				getManagerBean().update(otherPage);
			}
			wip.setPosition(newPosition);
			getManagerBean().update(wip);
		
			initializeModel();
		} catch (ManagerBeanException e) {
			LOGGER.error("move", e);
			addMessage( "Error cambiando la posición de la pagina " + wip.getName() );
			throw new AbortProcessingException(e.getMessage(), e);
		}			
	}
	
	public void reorderObjects() throws ManagerBeanException {
		List<ITransferObject> list = getManagerBean().getList( getCriteria() );
		for (int i = 0; i < list.size(); i++) {
			WebInfoPage page = (WebInfoPage) list.get(i);
			int oldPosition = page.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				page.setPosition(newPosition);
				getManagerBean().update( page );
			}
		}
		initializeModel();
	}	
	
    public void onMoveUp(ActionEvent event) {
    	move((WebInfoPage) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) {
    	move((WebInfoPage) this.model.getRowData(), 1);    	
    }

	public void onSelectDetail(ActionEvent event) {
		WebInfoPage wip = (WebInfoPage) getSelectedTO();
		current = wip;
		switch ( wip.getType() ) {
			case LOCATION:
				setShowLocationModalPanel(true);
				selecteDetail(wip);
				break;
			case GALLERY:
				setShowResourceModalPanel(true);
				selectResource(event, wip);
				break;
			case GENERIC:
				setShowGenericModalPanel(true);
				selecteDetail(wip);
				selectResource(event, wip);
				break;
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

	private void selecteDetail(WebInfoPage wip) {
		try {
			IManagerBean wipdBean = BeanManager.getManagerBean(WebInfoPageDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(wipdBean.getFieldName(IEntityAlias.WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE_ID), wip.getId());
			List<ITransferObject> listWipd = wipdBean.getList(criteria);
			if (listWipd.size() > 0) detail = (WebInfoPageDetail)listWipd.get(0);
			else detail = new WebInfoPageDetail();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private void selectResource(ActionEvent event, WebInfoPage wip) {
		WebInfoPageResourceController controller = (WebInfoPageResourceController) AonUtil.getRegisteredBean(WEB_PAGE_RESOURCE_CONTROLLER_NAME);
		controller.onSelect(event, wip);
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
			detail.setWebInfoPage( current );
			wipdBean.insertOrUpdate( detail );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
    }

	public List<SelectItem> getImages() throws ManagerBeanException {
		List<SelectItem> images = new LinkedList<SelectItem>();
		IManagerBean rattachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rattachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ADDITIONAL_IMAGE);
		List<ITransferObject> list = rattachBean.getList(criteria);
		for (int i=0; i <list.size(); i++) {
			RegistryAttachment rattach = (RegistryAttachment)list.get(i);
			SelectItem item = new SelectItem(rattach, rattach.getDescription());
			images.add(item);
		}
		return images;
	}

	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}
	
	private boolean isValidChar( char c ) {
		return Character.isLetter(c) || Character.isDigit(c) || ArrayUtils.contains(VALID_CHARS, c);
	}
	
	public void pageNameCheck(FacesContext context, UIComponent component, Object value) throws ManagerBeanException {
		String pageName = value.toString();
		IManagerBean bean = getManagerBean();
		Criteria criteria = new Criteria();
		WebInfoPage wip = (WebInfoPage) getTo();
		if ( wip.getId() != null ) {
			Expression exp = ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.WEB_INFO_PAGE_ID), wip.getId());
			criteria.addExpression(exp);
		}		
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WEB_INFO_PAGE_NAME), pageName);
		int count = bean.getCount(criteria);
		if ( count > 0 ) {
			FacesMessage message = new FacesMessage(AonUtil.getMessage(INFOWEB_BUNDLE, PAGE_DUPLICATED_NAME));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException( message );
		}
		for( int i = 0; i < pageName.length(); i++ ) {
			char c = pageName.charAt(i);
			if (! isValidChar(c) ) {
				String text = AonUtil.getMessage(INFOWEB_BUNDLE, PAGE_INVALID_CHARACTER);
				String formatted = AonUtil.substituteParams(AonUtil.getCurrentLocale(), text, new Object[]{c});
				FacesMessage message = new FacesMessage(formatted);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);				
				throw new ValidatorException( message );										
			}
		}
	}
	
}