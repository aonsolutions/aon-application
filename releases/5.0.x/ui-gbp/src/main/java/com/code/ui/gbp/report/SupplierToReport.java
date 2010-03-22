package com.code.ui.gbp.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.CampaignSupplier;
import com.code.gbp.Supplier;
import com.code.gbp.SupplierAddInfo;
import com.code.gbp.SupplierContact;
import com.code.gbp.SupplierContactPerson;
import com.code.gbp.SupplierEconomicData;
import com.code.gbp.SupplierObservation;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.report.SupplierReport;

public class SupplierToReport implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(SupplierToReport.class.getName());
	
	private static final String SUPPLIER_CONTROLLER_NAME = "supplier";

	public Collection getCollection() {
		List<SupplierReport> list = new LinkedList<SupplierReport>();
		try{
			SupplierReport supplierReport = new SupplierReport();
			IController supplierController = AonUtil.getController(SUPPLIER_CONTROLLER_NAME);
			Supplier supplier = (Supplier)supplierController.getTo();
			supplierReport.setSupplier(supplier);
			list.add(supplierReport);
			supplierReport.setAddInfos(obtainList(SupplierAddInfo.class, IGBPAlias.SUPPLIER_ADD_INFO_SUPPLIER_ID, supplier.getId()));
			supplierReport.setCampaigns(obtainList(CampaignSupplier.class, IGBPAlias.CAMPAIGN_SUPPLIER_SUPPLIER_ID, supplier.getId()));
			supplierReport.setContactPersons(obtainList(SupplierContactPerson.class, IGBPAlias.SUPPLIER_CONTACT_PERSON_SUPPLIER_ID, supplier.getId()));
			supplierReport.setContacts(obtainList(SupplierContact.class, IGBPAlias.SUPPLIER_CONTACT_SUPPLIER_ID, supplier.getId()));
			supplierReport.setEconomicDatas(obtainList(SupplierEconomicData.class, IGBPAlias.SUPPLIER_ECONOMIC_DATA_SUPPLIER_ID, supplier.getId()));
			supplierReport.setObservations(obtainList(SupplierObservation.class, IGBPAlias.SUPPLIER_OBSERVATION_SUPPLIER_ID, supplier.getId()));
		}catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error obtaining supplier report", e);
		}
		return list;
	}

	private List<ITransferObject> obtainList(Class pojoClass, String alias, Object data){
		try {
			IManagerBean bean = BeanManager.getManagerBean(pojoClass);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(alias), data);
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining list in report.", e);
		}
		return null;
	}
	
	public Collection getCollection(boolean arg0) throws ManagerBeanException {
		// TODO Auto-generated method stub
		return null;
	}

}
