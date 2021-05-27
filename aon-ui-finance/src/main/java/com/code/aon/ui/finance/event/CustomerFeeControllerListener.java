package com.code.aon.ui.finance.event;

import static com.code.aon.ui.common.ICommonMessages.CONFIG_INVALID_BILLING_DATE;
import static com.code.aon.ui.common.ICommonMessages.CONFIG_INVALID_END_DATE;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.CustomerFeeController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerFeeControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CustomerFeeController controller = (CustomerFeeController)event.getController();
		Customer customer = (Customer)controller.getMasterController().getTo();
		CustomerFee customerFee = (CustomerFee)controller.getTo();
		customerFee.setQuantity(1.0);
		customerFee.setInitialDate(CommonUtil.getDate(CommonUtil.getYear(new Date()), CommonUtil.getMonth(new Date()), 1));
		customerFee.setBillingDate(customerFee.getInitialDate());
		customerFee.setSecurityLevel(SecurityLevel.OFFICIAL);
		if (customer.getInvoicingGroup() != null && customer.getInvoicingGroup().getId() != null) {
			customerFee.setInvoicingGroup(customer.getInvoicingGroup());
		}

		controller.setLongDescription(false);
		try {
			customerFee.setLine(calculateNextLine(customer));

			CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			List<ITransferObject> currentUserWorkPlaces = companyCollections.getCurrentUserWorkPlaceList();
			if (currentUserWorkPlaces.size() == 1) {
				customerFee.setWorkPlace((WorkPlace)currentUserWorkPlaces.get(0));
			} else {
				for (ITransferObject ito : currentUserWorkPlaces) {
					WorkPlace workplace = (WorkPlace)ito;
					if (workplace.getScope().equals(customer.getScope())) {
						customerFee.setWorkPlace(workplace);
						break;
					}
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		CustomerFeeController controller = (CustomerFeeController)event.getController();
		CustomerFee customerFee = (CustomerFee)controller.getTo();

		controller.setLongDescription((customerFee.getDescription().length() > 64) ? true : false);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CustomerFee customerFee = (CustomerFee)event.getController().getTo();
		validateFeeDates(customerFee);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CustomerFee customerFee = (CustomerFee)event.getController().getTo();
		validateFeeDates(customerFee);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		if (!event.getController().isNevv()) {
			event.getController().initializeModel();
		}
	}

	private	Integer calculateNextLine(Customer customer) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID), customer.getId());
		Projection projection = Projection.max(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_LINE));
		Object value = customerFeeBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

	private void validateFeeDates(CustomerFee customerFee) throws ControllerListenerException {
		if (customerFee.getFinalDate() != null) {
			if (customerFee.getFinalDate().before(customerFee.getInitialDate())) {
				throw new ControllerListenerException(AonUtil.getMessage(CONFIG_INVALID_END_DATE));
			}
		}

		Calendar initialCalendar = new GregorianCalendar();
		initialCalendar.setTime(customerFee.getInitialDate());
		initialCalendar.set(Calendar.DAY_OF_MONTH, 1);
		if (customerFee.getBillingDate().before(initialCalendar.getTime())) {
			throw new ControllerListenerException(AonUtil.getMessage(CONFIG_INVALID_BILLING_DATE));
		}
	}

}