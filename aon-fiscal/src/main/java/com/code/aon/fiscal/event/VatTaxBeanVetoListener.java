package com.code.aon.fiscal.event;

import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public class VatTaxBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		VatTax vatTax = (VatTax) evt.getTo();
		if (vatTax.getYear()>2017) {
			throw new ManagerBeanVetoListenerException("A partir del ejercicio 2018, utilice el nuevo programa de autoliquidación de IVA");
		}
		checkVatTax(vatTax);
		if (vatTax.isExtraDeclaration()) {
			vatTax.setNumber(getNextNumber(vatTax));
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		VatTax vatTax = (VatTax) evt.getTo();
		checkVatTax(vatTax);
	}

	private void checkVatTax(VatTax vatTax) throws ManagerBeanVetoListenerException {
		if (vatTax.isComplementary() && vatTax.isReplacement()) {
			throw new ManagerBeanVetoListenerException("La declaración no puede ser Complementaria y Sustitutiva.");
		}

		if (!vatTax.isComplementary() && !vatTax.isReplacement()) {
			checkNormalVatTax(vatTax);
		} else {
			checkPeriodVatTax(vatTax);
		}
		
		checkProrata(vatTax);
	}
	
	private void checkProrata(VatTax vatTax) throws ManagerBeanVetoListenerException {
		if (vatTax.getProrata() < 0 ) {
			throw new ManagerBeanVetoListenerException("El porcentaje de prorrata no puede ser menor de cero.");
		}
		if (vatTax.getProrata() > 100 ) {
			throw new ManagerBeanVetoListenerException("El porcentaje de prorrata no puede ser mayor de cien.");
		}
	}

	private void checkNormalVatTax(VatTax vatTax) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(VatTax.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_YEAR), vatTax.getYear());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_PERIOD), vatTax.getPeriod());
			if (vatTax.getId() != null) {
				c.addExpression(ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_ID), vatTax.getId()));
			}
			int size = bean.getCount(c);
			if (size > 0) {
				throw new ManagerBeanVetoListenerException("Ya existe una declaración para este año y periodo.");
			}

		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private void checkPeriodVatTax(VatTax vatTax) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(VatTax.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_YEAR), vatTax.getYear());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_PERIOD), vatTax.getPeriod());
			if (vatTax.getId() != null) {
				c.addExpression(ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_ID), vatTax.getId()));
			}
			List<ITransferObject> list = bean.getList(c);
			if (list == null || list.size() == 0) {
				throw new ManagerBeanVetoListenerException("No existe una declaración para este periodo a la que complementar o sustituir.");
			}
			for (ITransferObject to : list) {
				VatTax tax = (VatTax) to;	
				if (tax.getStatus() != VatTaxStatus.FINISHED) {
					throw new ManagerBeanVetoListenerException("La declaración de este periodo a la que complementar o sustituir, no está FINALIZADA.");
				}
				if ((vatTax.isComplementary() && tax.isReplacement()) || (vatTax.isReplacement() && tax.isComplementary())) {
					throw new ManagerBeanVetoListenerException("No se permite la existencia de declaraciones complementarias y sustitutivas en un mismo periodo.");	
				}
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	private Integer getNextNumber(VatTax vatTax) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(VatTax.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_YEAR), vatTax.getYear());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_PERIOD), vatTax.getPeriod());
			if (vatTax.isComplementary()) {
				c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_COMPLEMENTARY), true);
			}
			if (vatTax.isReplacement()) {
				c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_REPLACEMENT), true);
			}

			c.addOrder(bean.getFieldName(IEntityAlias.VAT_TAX_NUMBER), false);
			List<ITransferObject> list = bean.getList(c);
			if (list == null || list.size() == 0) {
				return 1;
			}
			VatTax lastComplementary = (VatTax) list.get(0);
			return (lastComplementary.getNumber() + 1);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		VatTax vatTax = (VatTax) evt.getTo();
		removeTaxDetails(vatTax);
	}

	private void removeTaxDetails(VatTax vatTax) throws ManagerBeanVetoListenerException {
		try {
			if (vatTax != null && vatTax.getId() != null) {
				IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_ID), vatTax.getId());
				List<ITransferObject> list = bean.getList(c);
				for (ITransferObject to : list) {
					bean.remove(to);
				}
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}

	}

}
