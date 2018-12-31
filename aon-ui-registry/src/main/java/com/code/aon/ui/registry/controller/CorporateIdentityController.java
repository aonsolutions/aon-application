package com.code.aon.ui.registry.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.code.aon.ui.registry.controller.IRegistryConstants.BATCH_DOCUMENT_CONTROLLER_NAME;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Domain;
import com.code.aon.config.Scope;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Category;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.CategoryType;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.event.DomainLoookupListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

public class CorporateIdentityController extends RegistryAttachController implements ICorporateIdentityController {
	
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
		if ( !isMassiveUpload() ) {
			return getCurrentUserScopes();
		}
		List<SelectItem> scopes = new LinkedList<SelectItem>();
		if ( this.domain.getId() != null ) {
			IManagerBean bean = BeanManager.getManagerBean(Scope.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			List<Integer> list = new LinkedList<Integer>();
			list.add(this.domain.getId());
			if ( (this.domain.getParent() != null) && (this.domain.getParent().getId() != null) ) {
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
	
	public String getDownloadURL() throws ManagerBeanException, IOException, GeneralSecurityException {
		String url = null;
		RegistryAttachment ra = (RegistryAttachment) getTo();
		if ( (ra.getDriveId() != null) && (ra.getMD5() == null) ) {
			String domainName = AonUtil.getDomainName();
			com.esferalia.aon.occam.api.model.Domain domain = new com.esferalia.aon.occam.api.model.Domain()
					.setName(domainName).setId(ra.getDomain());
			User user = new User().setLogin(AonUtil.getRemoteUser() != null ? AonUtil.getRemoteUser() : "");
			DomainGserviceaccount d = DBConsults.getServiceAccount(domain, user);
			Drive drive = AonDrive.getInstace().serviceInitialize(d);
			File f = AonDrive.getInstace().getFile(drive, ra.getDriveId());
			ra.setMD5(f.getMd5Checksum());
		}
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		url = ds.getDomainURL() + ra.getDownloadURL();
		return url;
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<SelectItem> getCategories() throws ManagerBeanException {
		IManagerBean categoryBean = BeanManager.getManagerBean(Category.class);
		Criteria criteria = new Criteria();
		UserUtils.getInstance().addForceHeredityDomainCondition(criteria, categoryBean.getFieldName(IEntityAlias.CATEGORY_DOMAIN) );
		criteria.addEqualExpression(categoryBean.getFieldName(IEntityAlias.CATEGORY_TYPE), CategoryType.REGISTRY_ATTACHMENT);
		criteria.addOrder(categoryBean.getFieldName(IEntityAlias.CATEGORY_NAME));
		return RegistryCollectionsController.getCategoryList( (List) categoryBean.getList(criteria));
	}	

	public List<SelectItem> getCurrentUserScopes() {
		return ConfigCollectionsController.getScopeList(UserUtils.getInstance().getCurrentUserScopes(true));
	}	
	
}