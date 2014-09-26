package com.code.aon.ui.registry.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.CONFIG_COLLECTIONS;
import static com.code.aon.ui.registry.controller.IRegistryConstants.BATCH_DOCUMENT_CONTROLLER_NAME;

import java.awt.RadialGradientPaint;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Domain;
import com.code.aon.config.Scope;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.event.DomainLoookupListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CorporateIdentityController extends RegistryAttachController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static Integer DOMAIN_0 = 0;
	
	private boolean massiveUpload;
	
	private boolean serviconvenios;
	
	private RegistryAttachment lastAttachment;
	
	private IControllerListener domainLookupListener;
	
	private Domain domain;

	public CorporateIdentityController() {
		this.domainLookupListener = new DomainLoookupListener();
	}

	public IControllerListener getDomainLookupListener() {
		return domainLookupListener;
	}

	public boolean isMassiveUpload() {
		return massiveUpload;
	}

	public void setMassiveUpload(boolean massiveUpload) {
		this.massiveUpload = massiveUpload;
	}

	public boolean isServiconvenios() {
		return serviconvenios;
	}

	public void setServiconvenios(boolean serviconvenios) {
		this.serviconvenios = serviconvenios;
	}

	public RegistryAttachment getLastAttachment() {
		return lastAttachment;
	}

	public void setLastAttachment(RegistryAttachment lastAttachment) {
		this.lastAttachment = lastAttachment;
	}

	public void masiveUpload( ActionEvent event ) {
		accept(event);
		setLastAttachment( (RegistryAttachment) getTo() );
		onReset(event);
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public void onDomainChange( LookupChangeEvent event ) {
		setDomain( (Domain) event.getNewValue() );
	}

	public List<SelectItem> getDomainScopes() throws ManagerBeanException {
		if ( DomainManager.getCurrentDomain().equals(this.domain.getId()) ) {
			ConfigCollectionsController ccc = (ConfigCollectionsController) AonUtil.getRegisteredBean(CONFIG_COLLECTIONS);
			return ccc.getCurrentUserScopes();
		}
		List<SelectItem> scopes = new LinkedList<SelectItem>();
		if ( this.domain.getId() != null ) {
			IManagerBean bean = BeanManager.getManagerBean(Scope.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			List<Integer> list = new LinkedList<Integer>();
			list.add(this.domain.getId());
			if ( this.domain.isEnableHeredity() ) {
				list.add(this.domain.getParent().getId());
			}
			criteria.addInExpression(bean.getFieldName(IEntityAlias.SCOPE_DOMAIN), list );
			criteria.addOrder(bean.getFieldName(IEntityAlias.SCOPE_DESCRIPTION));
			for (ITransferObject ito : bean.getList(criteria)) {
				Scope scope = (Scope)ito;
				SelectItem item = new SelectItem(scope, scope.getDescription());
				scopes.add(item);
			}
		}
		return scopes;			
	}	

	public boolean isCurrentInBatch() throws ManagerBeanException {
		BatchDocument bd = (BatchDocument) AonUtil.getRegisteredBean(BATCH_DOCUMENT_CONTROLLER_NAME);
		return bd.isInBatch( (IAttachment) getSelectedTO() );
	}

	public void onAddCurrentToBatch(ActionEvent event) throws ManagerBeanException {
		BatchDocument bd = (BatchDocument) AonUtil.getRegisteredBean(BATCH_DOCUMENT_CONTROLLER_NAME);
		bd.addToBatch( (IAttachment) getSelectedTO() );
	}
	
	@Override
	protected int getDefaultPageLimit() {
		return AonUtil.getConfigurationController().getPageLimit();
	}	
	
	@Override
	public String initialAction() {
		String initialAction = super.initialAction();
		if ((!AonUtil.getRoleManager().isDocumentManager()) && StringUtils.equals(initialAction, formAction()) ) {
			onCancel(null);
			initialAction = listAction();			
		}
		return initialAction;
	}	
	
	public boolean isEditable() {
		return AonUtil.getRoleManager().isDocumentManager() && (!isServiconvenios());
	}

	public void onInit( ActionEvent event ) {
		setMassiveUpload(false);
		setServiconvenios(false);
		onEditSearch(event);
	}

	public void onInitMassiveUpload( ActionEvent event ) {
		setMassiveUpload(true);
		setServiconvenios(false);
		onReset(event);
	}

	public void onInitServiconvenios( ActionEvent event ) throws ManagerBeanException {
		setMassiveUpload(false);
		setServiconvenios(true);
		onEditSearch(event);
		onSearch(event);
	}
	
	public String getCurrentTagList() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			RegistryAttachment ra = (RegistryAttachment) getModel().getRowData();
			if ( isServiconvenios() ) {
				return ra.getTagList(DOMAIN_0);
			}
			return ra.getTagList();
		}
		return null;
	}
	
	@Override
	public void clearCriteria() throws ManagerBeanException {
		super.clearCriteria();
		if ( isServiconvenios() ) {
			getCriteria().setSkipDomainFilter(true);
			getCriteria().addEqualExpression(getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_DOMAIN), DOMAIN_0);
		}
	}	
	
	@Override
	public Integer getRegistryId() {
		if ( isServiconvenios() ) {
			return AdminUtil.getCompanyId(DOMAIN_0);
		}
		return super.getRegistryId();
	}

	
}