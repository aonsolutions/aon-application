package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.Target;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class TargetControllerListener extends ControllerAdapter implements ICommercialConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Target target = (Target)event.getController().getTo();
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_ID), target.getRegistry().getId());
			if (customerBean.getList(criteria).size() > 0) {
				throw new ControllerListenerException("Imposible borrar cliente potencial. Esta registrado como cliente.");
			}

			IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
			criteria = new Criteria();
			criteria.addEqualExpression(offerBean.getFieldName(IEntityAlias.OFFER_TARGET_ID), target.getRegistry().getId());
			if (offerBean.getList(criteria).size() > 0) {
				throw new ControllerListenerException("Imposible borrar cliente potencial. Tiene presupuestos asociados.");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
}