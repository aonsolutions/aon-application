package com.code.aon.ui.customer.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.finance.CustomerFee;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoicingGroupControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		InvoicingGroup invoicingGroup = (InvoicingGroup)event.getController().getTo();
		try{
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
        	criteria.addEqualExpression(customerBean.getFieldName(IEntityAlias.CUSTOMER_INVOICING_GROUP_ID), invoicingGroup.getId());
        	for (ITransferObject ito : customerBean.getList(criteria)) {
        		Customer customer = (Customer)ito;
        		customer.setInvoicingGroup(null);
        		customerBean.update(customer);
        	}

			IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
			criteria = new Criteria();
        	criteria.addEqualExpression(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_INVOICING_GROUP_ID), invoicingGroup.getId());
        	for (ITransferObject ito : customerFeeBean.getList(criteria)) {
        		CustomerFee customerFee = (CustomerFee)ito;
        		customerFee.setInvoicingGroup(null);
        		customerFeeBean.update(customerFee);
        	}
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException(ex.getMessage(), ex);
		}
	}

}