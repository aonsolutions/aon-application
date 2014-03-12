package com.code.aon.fiscal.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.fiscal.VatTaxDeclaration;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public class VatTaxDeclarationBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		VatTaxDeclaration dec = (VatTaxDeclaration) evt.getTo();
		checkAdministration( dec );
	}
	
	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		VatTaxDeclaration dec = (VatTaxDeclaration) evt.getTo();
		checkAdministration( dec );
	}

	private void checkAdministration(VatTaxDeclaration dec) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(VatTaxDeclaration.class);
			Criteria c = new Criteria();
			c.addEqualExpression( bean.getFieldName(IEntityAlias.VAT_TAX_DECLARATION_VAT_TAX_ID), dec.getVatTax().getId() );
			c.addEqualExpression( bean.getFieldName(IEntityAlias.VAT_TAX_DECLARATION_ADMINISTRATION), dec.getAdministration() );
			if (dec.getId() != null) {
				Expression exp = ExpressionUtilities.getNotEqualExpression( bean.getFieldName(IEntityAlias.VAT_TAX_DECLARATION_ID), dec.getId());
				c.addExpression(exp);
			}
			int count = bean.getCount(c);
			if (count > 0) {
				throw new ManagerBeanVetoListenerException("Ya existe una declaración para la administracion " + dec.getAdministration());	
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(),e);
		}
		
	}

}
