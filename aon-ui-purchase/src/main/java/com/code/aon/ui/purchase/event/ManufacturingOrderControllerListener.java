package com.code.aon.ui.purchase.event;

import java.util.Date;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.ql.Criteria;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.enumeration.SupplierStatus;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ManufacturingOrderControllerListener extends PurchaseControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;


	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanCreated(event);
		Purchase purchase = (Purchase) event.getController().getTo();
		purchase.setDocumentType(PurchaseDocumentType.MANUFACTURE);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController manufacturingOrderDetailController = FormUtil.getController(MANUFACTURING_ORDER_DETAIL_CONTROLLER_NAME);
		manufacturingOrderDetailController.onReset(null);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanUpdated(event);
		Purchase purchase = (Purchase)event.getController().getTo();
		if (purchase.getProject() != null && purchase.getProject().getId() != null) {
			IController manufacturingOrderDetailController = FormUtil.getController(MANUFACTURING_ORDER_DETAIL_CONTROLLER_NAME);
			manufacturingOrderDetailController.onSearch(null);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Purchase purchase = (Purchase) event.getController().getTo();
		try {
			if(purchase.getSupplier()==null || purchase.getSupplier().getId()==null){
				Supplier supplier = obtainSupplier();
				if(supplier==null || supplier.getId()==null){
					purchase.setSupplier(createCompanySupplier());
				} else {
					purchase.setSupplier(supplier);
				}
				purchase.setDocumentType(PurchaseDocumentType.MANUFACTURE);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	private Supplier obtainSupplier() throws ManagerBeanException {
		Supplier supplier = null;
		Integer id = obtainEnterprise().getRegistry().getId();
		
		IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_REGISTRY_ID), id);
		List<ITransferObject> list = bean.getList(criteria);
		if(list!=null && list.size()>0){
			supplier = (Supplier) list.get(0);
		}
		
		return supplier;
	}

	private Enterprise obtainEnterprise() throws ManagerBeanException {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = (Company) controller.getTo();
		IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
		return (Enterprise)bean.get(company.getRegistry().getId());
	}
	
	private Supplier createCompanySupplier() throws ControllerListenerException {
		try {
			Enterprise ent = obtainEnterprise();
			
			Supplier supplier = new Supplier();
			supplier.setAccount(null);
			supplier.setRegistry(ent.getRegistry());
			supplier.setScope(ent.getScope());
			supplier.setDomain(ent.getRegistry().getDomain());
			supplier.setWithholding(false);
			supplier.setWithholdingFarmer(false);
			supplier.setVatAccrualPayment(false);
			supplier.setTransaction(null);
			supplier.setStatus(SupplierStatus.ACTIVE);
			supplier.setPurchaseValuated(true);
			supplier.setCreationUser(null);
			supplier.setCreationDate(new Date());
			supplier.setModificationUser(null);
			supplier.setModificationDate(new Date());
			
			IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
			supplier = (Supplier) bean.insert(supplier);
			return supplier;
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("ERROR CREATING COMPANY AS SUPPLIER. "+e.getMessage());
		}
	}
	
}