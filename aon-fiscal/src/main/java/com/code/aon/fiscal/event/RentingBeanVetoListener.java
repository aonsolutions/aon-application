package com.code.aon.fiscal.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.fiscal.Renting;
import com.code.aon.fiscal.dao.IFiscalAlias;
import com.code.aon.fiscal.enumeration.RentingStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

public class RentingBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Renting renting = (Renting) evt.getTo();
		checkRenting(renting);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Renting renting = (Renting) evt.getTo();
		checkRenting(renting);
	}

	private void checkRenting(Renting renting) throws ManagerBeanVetoListenerException {
		if (renting.isComplementary() && renting.isReplacement()) {
			throw new ManagerBeanVetoListenerException("La declaración no puede ser Complementaria y Sustitutiva.");
		}

		if (!renting.isComplementary() && !renting.isReplacement()) {
			checkNormalRenting(renting);
		} else {
			checkPeriodRenting(renting);
		}
	}

	private void checkNormalRenting(Renting renting) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Renting.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IFiscalAlias.RENTING_YEAR), renting.getYear());
			c.addEqualExpression(bean.getFieldName(IFiscalAlias.RENTING_PERIOD), renting.getPeriod());
			c.addEqualExpression(bean.getFieldName(IFiscalAlias.RENTING_ADMINISTRATION), renting.getAdministration());
			if (renting.getId() != null) {
				c.addExpression(ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IFiscalAlias.RENTING_ID), renting.getId()));
			}
			int size = bean.getCount(c);
			if (size > 0) {
				throw new ManagerBeanVetoListenerException("Ya existe una declaración para este año, periodo y administración.");
			}

		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void checkPeriodRenting(Renting renting) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Renting.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IFiscalAlias.RENTING_YEAR), renting.getYear());
			c.addEqualExpression(bean.getFieldName(IFiscalAlias.RENTING_PERIOD), renting.getPeriod());
			c.addEqualExpression(bean.getFieldName(IFiscalAlias.RENTING_ADMINISTRATION), renting.getAdministration());
			if (renting.getId() != null) {
				c.addExpression(ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IFiscalAlias.RENTING_ID), renting.getId()));
			}
			List<ITransferObject> list = bean.getList(c);
			if (list == null || list.size() == 0) {
				throw new ManagerBeanVetoListenerException("No existe una declaración para este periodo y administración a la que complementar o sustituir.");
			}
			for (ITransferObject to : list) {
				Renting r = (Renting) to;	
				if (r.getStatus() != RentingStatus.FINISHED) {
					throw new ManagerBeanVetoListenerException("La declaración de este periodo a la que complementar o sustituir, no está FINALIZADA.");
				}
				if ((renting.isComplementary() && r.isReplacement()) || (renting.isReplacement() && r.isComplementary())) {
					throw new ManagerBeanVetoListenerException("No se permite la existencia de declaraciones complementarias y sustitutivas en un mismo periodo.");	
				}
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

}
