package com.code.aon.ui.finance.event;

import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.Finance;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.Prepayment;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.PrepaymentCollect;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.PrepaymentController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.product.controller.ProductCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PrepaymentControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PrepaymentController controller = (PrepaymentController)event.getController();
		Prepayment prepayment = (Prepayment)controller.getTo();
		prepayment.setCollect(PrepaymentCollect.FEE);
		prepayment.getFinance().setPayment(true);
		prepayment.getFinance().setFinanceStatus(FinanceStatus.PENDING);
		controller.setRegistryBank(null);
		controller.setShowBankManualInput(false);

		try {
			controller.setInvoiceDetail(null);
			controller.setCustomerFee((CustomerFee)BeanManager.getManagerBean(CustomerFee.class).createNewTo());
			controller.getCustomerFee().setBillingDate(CommonUtil.getDate(CommonUtil.getYear(new Date()), CommonUtil.getMonth(new Date()), 1));
			ProductCollectionsController productCollections = (ProductCollectionsController)AonUtil.getRegisteredBean(IItemConstants.PRODUCT_COLLECTIONS);
			if (productCollections.getPrepaymentItems().size() > 0) {
				controller.getCustomerFee().setItem((Item)productCollections.getPrepaymentItems().get(0).getValue());
				controller.getCustomerFee().setDescription(controller.getCustomerFee().getItem().getFullName());
			}
			CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			List<ITransferObject> currentUserWorkPlaces = companyCollections.getCurrentUserWorkPlaceList();
			if (currentUserWorkPlaces.size() == 1) {
				controller.getCustomerFee().setWorkPlace((WorkPlace)currentUserWorkPlaces.get(0));
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		PrepaymentController controller = (PrepaymentController)event.getController();
		Prepayment prepayment = (Prepayment)controller.getTo();
		try {
			String bankAccount = (prepayment.getFinance().getBankAccount() != null) ? prepayment.getFinance().getBankAccount().getValue() : null;
			controller.setRegistryBank(null);
			if (StringUtils.isNotEmpty(bankAccount)) {
				for (SelectItem selectItem : controller.getAllBanks()) {
					RegistryBank rBank = (RegistryBank)selectItem.getValue();
					if (bankAccount.equals(rBank.getBankAccount().getValue())) {
						controller.setRegistryBank(rBank);
						break;
					}
				}
			}
			controller.setShowBankManualInput(controller.getRegistryBank() == null && StringUtils.isNotEmpty(bankAccount));

			if (prepayment.isCollectFee()) {
				controller.setInvoiceDetail(null);
				if (prepayment.getCollectId() != null) {
					controller.setCustomerFee((CustomerFee)BeanManager.getManagerBean(CustomerFee.class).get(prepayment.getCollectId()));
					BeanManager.getManagerBean(CustomerFee.class).initializePOJO(controller.getCustomerFee());
				}
			} else {
				controller.setCustomerFee(null);
				if (prepayment.getCollectId() != null) {
					controller.setInvoiceDetail((InvoiceDetail)BeanManager.getManagerBean(InvoiceDetail.class).get(prepayment.getCollectId()));
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		PrepaymentController controller = (PrepaymentController)event.getController();
		Prepayment prepayment = (Prepayment)controller.getTo();
		prepayment.getFinance().setPayment(true);
		prepayment.getFinance().setRegistry(prepayment.getCreditor().getRegistry());
		prepayment.getFinance().setInvoice(null);
		prepayment.getFinance().setFinanceStatus(FinanceStatus.PENDING);
		prepayment.getFinance().setScope(prepayment.getCreditor().getScope());
		prepayment.getFinance().setPrepayment(true);
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			financeBean.restoreNullSubPOJOs(prepayment.getFinance());
			prepayment.setFinance((Finance)financeBean.insert(prepayment.getFinance()));

			controller.getCustomerFee().setCustomer(prepayment.getCustomer());
			controller.getCustomerFee().setLine(calculateNextFeeLine(prepayment.getCustomer()));
			controller.getCustomerFee().setQuantity(1);
			controller.getCustomerFee().setPrice(prepayment.getFinance().getAmount());
			controller.getCustomerFee().setDiscountExpression(new DiscountExpression("0.0"));
			controller.getCustomerFee().setInitialDate(controller.getCustomerFee().getBillingDate());
			controller.getCustomerFee().setPeriod(BillingPeriod.NO_PERIOD);
			IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
			customerFeeBean.restoreNullSubPOJOs(controller.getCustomerFee());
			controller.setCustomerFee((CustomerFee)customerFeeBean.insert(controller.getCustomerFee()));
			BeanManager.getManagerBean(CustomerFee.class).initializePOJO(controller.getCustomerFee());

			prepayment.setCollect(PrepaymentCollect.FEE);
			prepayment.setCollectId(controller.getCustomerFee().getId());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		PrepaymentController controller = (PrepaymentController)event.getController();
		Prepayment prepayment = (Prepayment)controller.getTo();
		if (!controller.isPaymentReadOnly()) {
			try {
				prepayment.getFinance().setRegistry(prepayment.getCreditor().getRegistry());
				prepayment.getFinance().setScope(prepayment.getCreditor().getScope());
				IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
				financeBean.restoreNullSubPOJOs(prepayment.getFinance());
				prepayment.setFinance((Finance)financeBean.update(prepayment.getFinance()));
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
				
		if (!controller.isChargeReadOnly()) {
			try {
				controller.getCustomerFee().setCustomer(prepayment.getCustomer());
				if (controller.getCustomerFee().getLine() == 0) {
					controller.getCustomerFee().setLine(calculateNextFeeLine(prepayment.getCustomer()));
				}
				controller.getCustomerFee().setPrice(prepayment.getFinance().getAmount());
				controller.getCustomerFee().setInitialDate(controller.getCustomerFee().getBillingDate());
				IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
				customerFeeBean.restoreNullSubPOJOs(controller.getCustomerFee());
				controller.setCustomerFee((CustomerFee)customerFeeBean.update(controller.getCustomerFee()));
				BeanManager.getManagerBean(CustomerFee.class).initializePOJO(controller.getCustomerFee());
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		PrepaymentController controller = (PrepaymentController)event.getController();
		if (!controller.isChargeReadOnly() && !controller.isPaymentReadOnly()) {
			Prepayment prepayment = (Prepayment)controller.getTo();
			try {
				BeanManager.getManagerBean(Finance.class).remove(prepayment.getFinance());
				BeanManager.getManagerBean(CustomerFee.class).remove(prepayment.getCollectId());
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

	private	Integer calculateNextFeeLine(Customer customer) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID), customer.getId());
		Projection projection = Projection.max(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_LINE));
		Object value = customerFeeBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}
