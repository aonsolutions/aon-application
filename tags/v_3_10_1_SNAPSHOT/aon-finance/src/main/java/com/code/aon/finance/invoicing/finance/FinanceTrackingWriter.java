package com.code.aon.finance.invoicing.finance;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.ql.Criteria;

public class FinanceTrackingWriter {

    private static final Logger LOGGER = Logger.getLogger(FinanceTrackingWriter.class.getName());

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
            LOGGER.log(Level.SEVERE, "Error inserting finance tracking of finance with id=" + finance.getId(), e);
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
            LOGGER.log(Level.SEVERE, "Error removing last tracking by type of finance with id=" + finance.getId(), e);
		}
    }
}
