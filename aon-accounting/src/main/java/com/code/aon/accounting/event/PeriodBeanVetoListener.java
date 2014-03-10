package com.code.aon.accounting.event;

import java.util.Date;

import com.code.aon.accounting.Period;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * @author Consulting & Development
 *
 */
public class PeriodBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	hasPeriodOverlap(evt);
    }
	
	@Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		hasPeriodOverlap(evt);
    }

	private void hasPeriodOverlap(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException{
		
		Period to = (Period)evt.getTo();
		Date toFrom = to.getInitiationDate();
		Date toTo = to.getDeadline();
		Date pFrom, pTo;
		if(toFrom.after(toTo))
			throw new ManagerBeanVetoListenerException("La fecha de inicio debe ser anterior a la fecha fin.");
        try {
            IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
            Criteria criteria = new Criteria();
            if (to.getId() != null ) {
            	criteria.addNotEqualExpression(periodBean.getFieldName(IEntityAlias.PERIOD_ID), to.getId());	
            }
            for(ITransferObject p: periodBean.getList(criteria)){
            	Period period = (Period)p;
            	pFrom = period.getInitiationDate();
        		pTo = period.getDeadline();
        		if(toFrom.compareTo(pFrom)>=0 && toFrom.compareTo(pTo)<=0){
            		throw new ManagerBeanVetoListenerException("Solape con la fecha de inicio y el periodo "+period.getName());
        		}	
            	if(toTo.compareTo(pFrom)>=0 && toTo.compareTo(pTo)<=0){
            		throw new ManagerBeanVetoListenerException("Solape con la fecha fin y el periodo "+period.getName());
            	}
            }
        } catch (ManagerBeanException e) {
            throw new ManagerBeanVetoListenerException(e);
        }
	}
}
