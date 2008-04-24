package com.code.ui.gbp.report;

import java.util.Collection;
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
import com.code.gbp.Incidence;
import com.code.gbp.ProFormaBank;
import com.code.gbp.ProFormaInvoice;
import com.code.gbp.ProFormaSignature;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.IncidenceSource;
import com.code.gbp.report.ProFormaReport;

public class ProFormaToReport implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(ProFormaToReport.class.getName());
	
	private static final String PROFORMA_CONTROLLER_NAME = "proForma";

	public Collection getCollection() {
		List<ProFormaReport> list = new LinkedList<ProFormaReport>();
		try{
			ProFormaReport proFormaReport = new ProFormaReport();
			IController proFormaController = AonUtil.getController(PROFORMA_CONTROLLER_NAME);
			ProFormaInvoice proForma = (ProFormaInvoice)proFormaController.getTo();
			proFormaReport.setProFormaInvoice(proForma);
			list.add(proFormaReport);
			proFormaReport.setSignatures(obtainList(ProFormaSignature.class, IGBPAlias.PRO_FORMA_SIGNATURE_PRO_FORMA_INVOICE_ID, proForma.getId()));
			proFormaReport.setIncidences(obtainIncidenceList(proForma.getId()));
			proFormaReport.setBanks(obtainList(ProFormaBank.class, IGBPAlias.PRO_FORMA_BANK_PRO_FORMA_INVOICE_ID, proForma.getId()));
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

	private List<ITransferObject> obtainIncidenceList(Object id){
		try {
			IManagerBean bean = BeanManager.getManagerBean(Incidence.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IGBPAlias.INCIDENCE_SOURCE), IncidenceSource.PRO_FORMA);
			criteria.addEqualExpression(bean.getFieldName(IGBPAlias.INCIDENCE_SOURCE_ID), id);
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
