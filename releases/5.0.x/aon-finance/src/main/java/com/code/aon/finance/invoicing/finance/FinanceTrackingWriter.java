package com.code.aon.finance.invoicing.finance;

import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.ql.Criteria;

public class FinanceTrackingWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceTrackingWriter.class.getName());

    public static FinanceTracking addFinanceTracking(Finance finance, Date trackingDate, FinanceTrackingType trackingType, String description) {
        FinanceTracking tracking = new FinanceTracking();
        try {
            IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
            tracking.setFinance(finance);
            tracking.setTrackingDate(trackingDate);
            tracking.setType(trackingType);
            tracking.setDescription(description);
            tracking.setAmount(finance.getTotalAmount());
            tracking = (FinanceTracking)financeTrackingBean.insert(tracking);
        } catch (ManagerBeanException e) {
            LOGGER.error("Error inserting finance tracking of finance with id=" + finance.getId(), e);
        }
        return tracking;
    }
    
    @SuppressWarnings("unchecked")
	public static void removeLastTrackingByType(Finance finance, FinanceTrackingType type){
    	try {
			IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeTrackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
			criteria.addEqualExpression(financeTrackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TYPE), type);
			criteria.addOrder(financeTrackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_ID),false);
			Iterator iter = financeTrackingBean.getList(criteria,0,1).iterator();
			if(iter.hasNext()){
				FinanceTracking tracking = (FinanceTracking)iter.next();
				financeTrackingBean.remove(tracking);
			}
		} catch (ManagerBeanException e) {
            LOGGER.error("Error removing last tracking by type of finance with id=" + finance.getId(), e);
		}
    }

	public static boolean isLastTracking(FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID), tracking.getFinance().getId());
		criteria.addOrder(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_ID), false);
		Iterator<?> iterator = trackingBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			FinanceTracking financeTracking = (FinanceTracking)iterator.next();
			return financeTracking.getId().equals(tracking.getId());
		}
		return false;
	}

	public static boolean wasFinanceReturned(Finance finance) throws ManagerBeanException {
		return (getReturnedTimes(finance) > 0);
	}

	public static int getReturnedTimes(Finance finance) throws ManagerBeanException {
		IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
		criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TYPE), FinanceTrackingType.RETURNED);
		return (trackingBean.getCount(criteria));
	}

}
