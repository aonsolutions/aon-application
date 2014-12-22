package com.code.aon.ui.registry.controller.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryTax;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.RegistryTaxController;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryTaxControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RegistryTaxController controller = (RegistryTaxController)event.getController();
		controller.setTaxType(TaxType.VAT);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		RegistryTaxController controller = (RegistryTaxController)event.getController();
		controller.setTaxType(((RegistryTax)controller.getTo()).getTax().getType());
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		RegistryTaxController controller = (RegistryTaxController)event.getController();
		RegistryTax to = (RegistryTax)controller.getTo();
		if (!controller.isVat()) {
			to.setSurcharge(0);
		}
		validateRegistryTax(to);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		RegistryTaxController controller = (RegistryTaxController)event.getController();
		RegistryTax to = (RegistryTax)controller.getTo();
		if (!controller.isVat()) {
			to.setSurcharge(0);
		}
		validateRegistryTax(to);
	}

	private void validateRegistryTax(RegistryTax rTax) throws ControllerListenerException {
		if (rTax.getEndDate() != null && rTax.getEndDate().before(rTax.getStartDate())) {
			throw new ControllerListenerException("Fecha fin de vigencia incorrecta.");
		}
		try {
			IManagerBean rTaxBean = BeanManager.getManagerBean(RegistryTax.class);
			Criteria criteria = new Criteria();
			if (rTax.getId() != null) {
				criteria.addNotEqualExpression(rTaxBean.getFieldName(IEntityAlias.REGISTRY_TAX_ID), rTax.getId());
			}
			criteria.addEqualExpression(rTaxBean.getFieldName(IEntityAlias.REGISTRY_TAX_REGISTRY_ID), rTax.getRegistry().getId());
			criteria.addEqualExpression(rTaxBean.getFieldName(IEntityAlias.REGISTRY_TAX_TAX_ID), rTax.getTax().getId());
			if (rTax.getEndDate() != null) {
				criteria.addLessThanExpression(rTaxBean.getFieldName(IEntityAlias.REGISTRY_TAX_START_DATE), rTax.getEndDate());
			}
			Expression endNull = ExpressionUtilities.getNullExpression(rTaxBean.getFieldName(IEntityAlias.REGISTRY_TAX_END_DATE));
			Expression endExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(rTaxBean.getFieldName(IEntityAlias.REGISTRY_TAX_END_DATE), rTax.getStartDate());
			criteria.addExpression(ExpressionUtilities.getOrExpression(endNull, endExpr));
			if (rTaxBean.getCount(criteria) > 0) {
				throw new ControllerListenerException("Las fechas de vigencia se solapan con otro periodo.");
			}
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException(ex.getMessage());
		}
	}

}
