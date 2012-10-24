package com.code.aon.finance.invoicing.finance;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.finance.BankStatementLink;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceTrackingWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceTrackingWriter.class.getName());

    public static FinanceTracking addFinanceTracking(Finance finance, Date date, FinanceTrackingType type, String description) {
    	return addFinanceTracking(finance, date, type, description, null, null, finance.getTotalAmount(), false);
    }

    public static FinanceTracking addFinanceTracking(Finance finance, Date date, FinanceTrackingType type, String description, double amount) {
    	return addFinanceTracking(finance, date, type, description, null, null, amount, false);
    }

    public static FinanceTracking addFinanceTracking(Finance finance, Date date, FinanceTrackingType type, String description, 
    		RegistryBank registryBank, PayMethodTypeDetail payMethodTypeDetail, double amount, boolean recorded) {
    	return addFinanceTracking(finance, date, type, description, registryBank, payMethodTypeDetail, amount, recorded, null);
    }
    
    public static FinanceTracking addFinanceTracking(Finance finance, Date date, FinanceTrackingType type, String description, 
    		RegistryBank registryBank, PayMethodTypeDetail payMethodTypeDetail, double amount, boolean recorded, BankStatementLink statementLink) {
        FinanceTracking tracking = new FinanceTracking();
        try {
            IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
            tracking.setFinance(finance);
            tracking.setTrackingDate(date);
            tracking.setType(type);
            tracking.setDescription(description);
            tracking.setRegistryBank(registryBank);
            tracking.setPayMethodTypeDetail(payMethodTypeDetail);
            tracking.setBankStatementLink(statementLink);
            tracking.setAmount(amount);
            tracking.setRecorded(recorded);
            tracking = (FinanceTracking)financeTrackingBean.insert(tracking);
        } catch (ManagerBeanException e) {
            LOGGER.error("Error inserting finance tracking of finance with id=" + finance.getId(), e);
        }
        return tracking;
    }
    
	public static void removeLastTrackingByType(Finance finance, FinanceTrackingType type){
    	try {
			IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
			criteria.addEqualExpression(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_TYPE), type);
			criteria.addOrder(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_ID),false);
			for (ITransferObject ito : financeTrackingBean.getList(criteria, 0, 1)) {
				FinanceTracking tracking = (FinanceTracking)ito;
				financeTrackingBean.remove(tracking);
			}
		} catch (ManagerBeanException e) {
            LOGGER.error("Error removing last tracking by type of finance with id=" + finance.getId(), e);
		}
    }

	public static boolean isLastTracking(FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_ID), tracking.getFinance().getId());
		criteria.addOrder(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_ID), false);
		for (ITransferObject ito : trackingBean.getList(criteria)) {
			FinanceTracking financeTracking = (FinanceTracking)ito;
			return financeTracking.getId().equals(tracking.getId());
		}
		return false;
	}

	public static boolean wasFinanceReturned(Finance finance) throws ManagerBeanException {
		return (getReturnedTimes(finance) > 0);
	}

	private static int getReturnedTimes(Finance finance) throws ManagerBeanException {
		IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
		criteria.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_TYPE), FinanceTrackingType.RETURNED);
		return (trackingBean.getCount(criteria));
	}

}
