package com.code.ui.gbp.event;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.gbp.BankPercent;
import com.code.gbp.CampaignSupplier;
import com.code.gbp.dao.IGBPAlias;

public class BankPercentControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.BANK_PERCENT_DISTRIBUTION_PERCENT));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Session session;
        session = HibernateUtil.getSession();
        String stmt = "SELECT SUM (bp.distributionPercent) " +
        		"FROM BankPercent bp ";
        Query query = session.createQuery(stmt);
        List l = query.list();
        double sum = 0;
        if (!l.isEmpty() && (l.get(0)!=null)){
        	sum = ((Double)l.get(0)).doubleValue();
		}
        double added = ((BankPercent)event.getController().getTo()).getDistributionPercent();
        if ((sum+added)>100){
        	throw new ControllerListenerException("Percent sumatory can not be more than 100.");
        }
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		BankPercent to = (BankPercent)event.getController().getTo();
		Session session;
        session = HibernateUtil.getSession();
        String stmt = "SELECT SUM (bp.distributionPercent) " +
        		"FROM BankPercent bp " +
        		"WHERE bp.id <> "+to.getId();
        Query query = session.createQuery(stmt);
        List l = query.list();
        double sum = 0;
        if (!l.isEmpty() && (l.get(0)!=null)){
        	sum = ((Double)l.get(0)).doubleValue();
		}
        double added = to.getDistributionPercent();
        if ((sum+added)>100){
        	throw new ControllerListenerException("Percent sumatory can not be more than 100.");
        }
	}
	
}