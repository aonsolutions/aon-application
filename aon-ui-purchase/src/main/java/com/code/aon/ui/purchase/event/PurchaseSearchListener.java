package com.code.aon.ui.purchase.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.product.Item;
import com.code.aon.project.Project;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseSearchListener extends RegistrySearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String REGISTRY_SEARCH_PREFFIX = "Purchase_supplier_registry_";

	private Supplier supplier;
	
	private WorkPlace workPlace;

	private PurchaseStatus[] purchaseStatuses;
	
	private PurchaseDocumentType[] purchaseDocumentTypes;
	
	private Item item;

	private Project project;

	private Boolean[] emailCommunication;
	
	public String getPreffix() throws ManagerBeanException {
		return REGISTRY_SEARCH_PREFFIX;
	}		
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public PurchaseStatus[] getPurchaseStatuses() {
		return purchaseStatuses;
	}

	public void setPurchaseStatuses(PurchaseStatus[] purchaseStatuses) {
		this.purchaseStatuses = purchaseStatuses;
	}
	
	public PurchaseDocumentType[] getPurchaseDocumentTypes() {
		return purchaseDocumentTypes;
	}

	public void setPurchaseDocumentTypes(
			PurchaseDocumentType[] purchaseDocumentTypes) {
		this.purchaseDocumentTypes = purchaseDocumentTypes;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public Boolean[] getEmailCommunication() {
		return emailCommunication;
	}

	public void setEmailCommunication(Boolean[] emailCommunication) {
		this.emailCommunication = emailCommunication;
	}

	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setSupplier((Supplier)BeanManager.getManagerBean(Supplier.class).createNewTo());
		PurchaseStatus[] defaultPurchaseStatus = {PurchaseStatus.PENDING};
		setPurchaseStatuses(defaultPurchaseStatus);
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
		setProject((Project)BeanManager.getManagerBean(Project.class).createNewTo());
		setEmailCommunication(null);
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		CompanyCollectionsController controller = (CompanyCollectionsController) AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		super.completeCriteria( criteria);
		if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PURCHASE_WORK_PLACE_ID), getWorkPlace().getId());			
		} else {
			criteria.addInExpression(getFieldName(IEntityAlias.PURCHASE_WORK_PLACE_ID), controller.getCurrentUserWorkPlacesIds());
		}
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PURCHASE_SUPPLIER_ID), getSupplier().getId());			
		}
		if (!ArrayUtils.isEmpty(getPurchaseStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.PURCHASE_STATUS);
			addEnumToCriteria(criteria, status, getPurchaseStatuses());
		}
		if (!ArrayUtils.isEmpty(getPurchaseDocumentTypes())) {
			String type = getController().resolveAlias(IEntityAlias.PURCHASE_DOCUMENT_TYPE);
			addEnumToCriteria(criteria, type, getPurchaseDocumentTypes());
		}
		if ((getItem() != null) && (getItem().getId() != null)) {
			criteria.addEqualExpression("Purchase.lines.item.id", getItem().getId());
		}
		if ((getProject() != null) && (getProject().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PURCHASE_PROJECT_ID), getProject().getId());			
		}
		if (!ArrayUtils.isEmpty(getEmailCommunication())) {
			String type = getController().resolveAlias(IEntityAlias.PURCHASE_EMAIL_COMMUNICATION);
			addEnumToCriteria(criteria, type, getEmailCommunication());
		}
	}
	
}