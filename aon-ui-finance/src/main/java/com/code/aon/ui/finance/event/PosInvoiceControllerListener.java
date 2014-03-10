package com.code.aon.ui.finance.event;

import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Series;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.Pos;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.PosInvoiceController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosInvoiceControllerListener extends SaleInvoiceControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanCreated(event);

		PosInvoiceController controller = (PosInvoiceController)event.getController();
		controller.setFinanceGenerationMode(-1);
		Invoice invoice = (Invoice)controller.getTo();
		try {
			if (controller.getPosShift() != null && controller.getPosShift().getId() != null) {
				invoice.setPosShift(controller.getPosShift());

				Series series = obtainPosSeries(invoice.getPosShift().getPos());
				if (series != null) {
					invoice.setSeries(series.getCode());
					invoice.setSecurityLevel(series.getSecurityLevel());
				}

				controller.setDefaultCustomer(obtainPosCustomer(invoice.getPosShift().getPos()));
				if (controller.getDefaultCustomer() != null) {
					controller.customerChanged(controller.getDefaultCustomer());
				}

				if (controller.getSeller() != null && controller.getSeller().getId() != null) {
					invoice.setSeller(controller.getSeller());
				}
			} else {
				String msg = "No se puede Facturar. El Usuario no ha abierto la Caja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
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

	public Series obtainPosSeries(Pos pos) throws ManagerBeanException {
		String posSeries = pos.getSeries();
		ConfigCollectionsController configCollections = (ConfigCollectionsController)AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		for (SelectItem posItem : configCollections.getPosSeries()) {
			Series series = (Series)posItem.getValue();
			if (StringUtils.isEmpty(posSeries) || series.getCode().equals(posSeries)) {
				return series;
			}
		}
		return null;
	}

	private Customer obtainPosCustomer(Pos pos) throws ManagerBeanException {
		if (pos.getCustomer() != null && pos.getCustomer().getId() != null) {
			return pos.getCustomer();
		} else if (pos.getWorkPlace().getCustomer() != null && pos.getWorkPlace().getCustomer().getId() != null) {
			return pos.getWorkPlace().getCustomer();
		} else {
			IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(appParamBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), IFinanceConstants.POS_DEFAULT_CUSTOMER_ID);
			for (ITransferObject ito : appParamBean.getList(criteria)) {
				String value = ((ApplicationParameter)ito).getValue();
				if (StringUtils.isNotEmpty(value)) {
					return (Customer)BeanManager.getManagerBean(Customer.class).get(Integer.parseInt(value));
				}
			}
		}
		return null;
	}

	private void setDefaultPosData(ControllerEvent event) {
		PosInvoiceController controller = (PosInvoiceController)event.getController();
		Invoice invoice = (Invoice)controller.getTo();
		if (invoice.getSeller() != null && invoice.getSeller().getId() != null) {
			controller.setSeller(invoice.getSeller());
		}
	}

}