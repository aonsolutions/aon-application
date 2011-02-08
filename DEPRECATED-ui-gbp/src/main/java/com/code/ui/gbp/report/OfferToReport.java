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
import com.code.gbp.Offer;
import com.code.gbp.OfferSignature;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.IncidenceSource;
import com.code.gbp.report.OfferReport;

public class OfferToReport implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(OfferToReport.class.getName());
	
	private static final String OFFER_CONTROLLER_NAME = "offer";

	public Collection getCollection() {
		List<OfferReport> list = new LinkedList<OfferReport>();
		try{
			OfferReport offerReport = new OfferReport();
			IController offerController = AonUtil.getController(OFFER_CONTROLLER_NAME);
			Offer offer = (Offer)offerController.getTo();
			offerReport.setOffer(offer);
			list.add(offerReport);
			offerReport.setSignatures(obtainList(OfferSignature.class, IGBPAlias.OFFER_SIGNATURE_OFFER_ID, offer.getId()));
			offerReport.setIncidences(obtainIncidenceList(offer.getId()));
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
			criteria.addEqualExpression(bean.getFieldName(IGBPAlias.INCIDENCE_SOURCE), IncidenceSource.OFFER);
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
