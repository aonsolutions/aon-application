package com.code.aon.ui.finance.event;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Series;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.finance.controller.PosInvoiceController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosInvoiceControllerListener extends SaleInvoiceControllerListener {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanCreated(event);

		PosInvoiceController controller = (PosInvoiceController)event.getController();
		controller.setFinanceGenerationMode(-1);
		Invoice invoice = (Invoice)controller.getTo();
		try {
			Series series = obtainPosSeries();
			if (series != null) {
				invoice.setSeries(series.getCode());
				invoice.setSecurityLevel(series.getSecurityLevel());
			}
			Customer customer = obtainPosCustomer();
			if (customer != null) {
				controller.customerChanged(customer);
			}
			if (controller.getPos() != null && controller.getPos().getId() != null) {
				invoice.setPos(controller.getPos());
			}
			if (controller.getSeller() != null && controller.getSeller().getId() != null) {
				invoice.setSeller(controller.getSeller());
			}
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException(ex.getMessage(), ex);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		setDefaultPosData(event);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		setDefaultPosData(event);
		super.afterBeanUpdated(event);
	}

	public Series obtainPosSeries() throws ManagerBeanException {
		ConfigCollectionsController configCollections = (ConfigCollectionsController)AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		for (SelectItem posItem : configCollections.getPosSeries()) {
			return (Series)posItem.getValue();
		}
		return null;
	}

	private Customer obtainPosCustomer() throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), "POS_CUSTOMER_ID");
		for (ITransferObject ito : appParamBean.getList(criteria)) {
			String value = ((ApplicationParameter)ito).getValue();
			if (StringUtils.isNotEmpty(value)) {
				return (Customer)BeanManager.getManagerBean(Customer.class).get(Integer.parseInt(value));
			}
		}
		return null;
	}

	private void setDefaultPosData(ControllerEvent event) {
		PosInvoiceController controller = (PosInvoiceController)event.getController();
		Invoice invoice = (Invoice)controller.getTo();
		if (invoice.getPos() != null && invoice.getPos().getId() != null) {
			controller.setPos(invoice.getPos());
		}
		if (invoice.getSeller() != null && invoice.getSeller().getId() != null) {
			controller.setSeller(invoice.getSeller());
		}
	}

}