package com.code.aon.ui.finance.controller;

import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.seller.Seller;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.customer.controller.CustomerListController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FeeAssignmentController extends CustomerListController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private CustomerFee fee;
	private boolean longDescription;
	private boolean assignToInvoicingGroup;

	public CustomerFee getFee() {
		return fee;
	}

	public void setFee(CustomerFee fee) {
		this.fee = fee;
	}

	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}

	public boolean isAssignToInvoicingGroup() {
		return assignToInvoicingGroup;
	}

	public void setAssignToInvoicingGroup(boolean assignToInvoicingGroup) {
		this.assignToInvoicingGroup = assignToInvoicingGroup;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		initializeFee();
	}

	private void initializeFee() {
		CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		try {
			setFee(new CustomerFee());
			getFee().setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
			getFee().setQuantity(1);
			getFee().setDiscountExpression(new DiscountExpression("0.0"));
			getFee().setInitialDate(CommonUtil.getDate(CommonUtil.getYear(new Date()), CommonUtil.getMonth(new Date()), 1));
			getFee().setBillingDate(getFee().getInitialDate());
			getFee().setPeriod(BillingPeriod.NO_PERIOD);
			getFee().setSecurityLevel(SecurityLevel.OFFICIAL);
			getFee().setSeller((Seller)BeanManager.getManagerBean(Seller.class).createNewTo());
			if (companyCollections.getCurrentUserWorkPlacesCount() == 1) {
				getFee().setWorkPlace((WorkPlace)companyCollections.getCurrentUserWorkPlaceList().get(0));
			}
		} catch (ManagerBeanException ex) {
			throw new AbortProcessingException("Error al Inicializar la Cuota.");
		}

		setLongDescription(false);
		setAssignToInvoicingGroup(true);
	}

	public void onItemChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			getFee().setItem(item);
			getFee().setDescription(item.getFullName());
			getFee().setPrice(item.getPrice());
		}
	}	
	
	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);

		if (StringUtils.equals(getFee().getItem().getFullName().trim(), getFee().getDescription().trim())) {
			String longDescription = getFee().getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				getFee().setDescription(getFee().getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public void onAssignFee(ActionEvent event) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		for (Customer customer : getCheckedCustomers()) {
			getFee().setCustomer(customer);
			getFee().setLine(calculateFeeLine(customer));
			getFee().setInvoicingGroup((isAssignToInvoicingGroup()) ? customer.getInvoicingGroup() : null);
			customerFeeBean.restoreNullSubPOJOs(getFee());
			customerFeeBean.insert(getFee());
		}

		AonUtil.addInfoMessage("Se han creado " + getCheckedCount() + " Cuotas.");
		checkNone(event);
		initializeFee();
	}

	private int calculateFeeLine(Customer customer) throws ManagerBeanException {
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID), customer.getId());
		Projection projection = Projection.max(customerFeeBean.getFieldName(IEntityAlias.CUSTOMER_FEE_LINE));
		Object value = customerFeeBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}