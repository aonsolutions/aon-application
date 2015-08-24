package com.code.aon.ui.purchase.event;

import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.ql.Criteria;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ManufacturingOrderSearchListener extends PurchaseSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		
		setSupplier(obtainSupplier());
		
		PurchaseDocumentType[] purchaseDocumentTypes = {PurchaseDocumentType.MANUFACTURE};
		setPurchaseDocumentTypes(purchaseDocumentTypes);
	}
	
	private Supplier obtainSupplier() throws ManagerBeanException {
		Supplier supplier;
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Integer id = ((Company)controller.getTo()).getRegistry().getId();
		
		IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_REGISTRY_ID), id);
		List<ITransferObject> list = bean.getList(criteria);
		if(list!=null && list.size()>0){
			supplier = (Supplier) list.get(0);
		} else {
			supplier = (Supplier)BeanManager.getManagerBean(Supplier.class).createNewTo();
			supplier.setId(-1);
		}
		
		return supplier;
	}
	
}