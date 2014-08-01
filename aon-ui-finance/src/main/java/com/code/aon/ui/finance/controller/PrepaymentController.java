package com.code.aon.ui.finance.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.util.BankUtil;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.Finance;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.Prepayment;
import com.code.aon.product.Item;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PrepaymentController extends BasicController implements IFinanceConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private RegistryBank registryBank;
	private boolean showBankManualInput;
	private InvoiceDetail invoiceDetail;
	private CustomerFee customerFee;
	private boolean longDescription;

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public boolean isShowBankManualInput() throws ManagerBeanException {
		return showBankManualInput;
	}

	public void setShowBankManualInput(boolean showBankManualInput) {
		this.showBankManualInput = showBankManualInput;
	}

	public InvoiceDetail getInvoiceDetail() {
		return invoiceDetail;
	}

	public void setInvoiceDetail(InvoiceDetail invoiceDetail) {
		this.invoiceDetail = invoiceDetail;
	}

	public CustomerFee getCustomerFee() {
		return customerFee;
	}

	public void setCustomerFee(CustomerFee customerFee) {
		this.customerFee = customerFee;
	}

	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}


	public boolean isPaymentReadOnly() {
		return !((Prepayment)getTo()).getFinance().isPending();
	}

	public boolean isChargeReadOnly() {
		return ((Prepayment)getTo()).isCollectInvoice();
	}

	public void onCreditorChanged(LookupChangeEvent event) throws ManagerBeanException {
		Prepayment prepayment = (Prepayment)getTo();
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Creditor creditor = (Creditor)event.getNewValue();
			prepayment.setCreditor(creditor);

			Finance finance = prepayment.getFinance();
			RegistryPayMethod rPayMethod = creditor.getRegistry().getPayMethod();
			finance.setPayMethod((rPayMethod==null) ? new PayMethod() : rPayMethod.getPayment());
			finance.setBankAccount((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? new BankAccount() : rPayMethod.getBankAccount());
			finance.setBankAlias((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? null : rPayMethod.getBankAlias());
			finance.setBic((rPayMethod==null || rPayMethod.getRegistryBank()==null) ? null : rPayMethod.getBic());

			setRegistryBank((rPayMethod==null) ? null : rPayMethod.getRegistryBank());
			setShowBankManualInput(false);
		} else {
			setRegistryBank(null);
			setShowBankManualInput(false);
		}
	}

	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPayMethod = (PayMethod)event.getOldValue();
		PayMethod newPayMethod = (PayMethod)event.getNewValue();
		if (oldPayMethod == null || newPayMethod == null || oldPayMethod.getType() != newPayMethod.getType()) {
			Finance finance = ((Prepayment)getTo()).getFinance();
			finance.setPayMethod(newPayMethod);
			finance.setBankAccount(new BankAccount());
			finance.setBankAlias(null);
			finance.setBic(null);

			setRegistryBank(null);
			setShowBankManualInput(false);
		}
	}

	public List<SelectItem> getAllBanks() throws ManagerBeanException {
		Prepayment prepayment = (Prepayment)getTo();
		if (prepayment.getCreditor() != null && prepayment.getCreditor().getId() != null) {
			Finance finance = prepayment.getFinance();
			if (finance.getPayMethod() != null && finance.getPayMethod().getType() == PayMethodType.BANK_TRANSFER) {
				RegistryCollectionsController registryColls = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return registryColls.getAllRegistryBanks(prepayment.getCreditor().getRegistry());
			} 
			CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return companyColls.getAllCompanyBanks();
		}
		return new LinkedList<SelectItem>();
	}

	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		Prepayment prepayment = (Prepayment)getTo();
		if (prepayment.getCreditor() != null && prepayment.getCreditor().getId() != null) {
			Finance finance = prepayment.getFinance();
			if (finance.getPayMethod() != null && finance.getPayMethod().getType() == PayMethodType.BANK_TRANSFER) {
				RegistryCollectionsController registryColls = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return registryColls.getActiveRegistryBanks(prepayment.getCreditor().getRegistry());
			} 
			CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return companyColls.getActiveCompanyBanks();
		}
		return new LinkedList<SelectItem>();
	}

	public void onRBankChanged(ValueChangeEvent event) {
		Finance finance = ((Prepayment)getTo()).getFinance();
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			RegistryBank rbank = (RegistryBank) event.getNewValue();
			finance.setBankAccount(rbank.getBankAccount());
			finance.setBankAlias(rbank.getBankAlias());
			finance.setBic(rbank.getBic());

			setRegistryBank(rbank);
		} else {
			finance.setBankAccount(new BankAccount());
			finance.setBankAlias(null);
			finance.setBic(null);

			setRegistryBank(null);
		}
	}

	public void onBankManualInput(ActionEvent event) throws ManagerBeanException {
		Finance finance = ((Prepayment)getTo()).getFinance();
		finance.setBankAccount(new BankAccount());
		finance.setBankAlias(null);
		finance.setBic(null);

		setRegistryBank(null);
	}

	public void onBankAccountData(ActionEvent event) {
		BankUtil.fillBankAccountData(((Prepayment)getTo()).getFinance());
	}

	public void onCustomerChanged(LookupChangeEvent event) throws ManagerBeanException {
		Prepayment prepayment = (Prepayment)getTo();
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			prepayment.setCustomer(customer);

			if (customer.getInvoicingGroup() != null) {
				getCustomerFee().setInvoicingGroup(customer.getInvoicingGroup());
			}
			CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			List<ITransferObject> currentUserWorkPlaces = companyCollections.getCurrentUserWorkPlaceList();
			if (currentUserWorkPlaces.size() > 1) {
				for (ITransferObject ito : currentUserWorkPlaces) {
					WorkPlace workplace = (WorkPlace)ito;
					if (workplace.getScope().equals(customer.getScope())) {
						customerFee.setWorkPlace(workplace);
						break;
					}
				}
			}
		} else {
			getCustomerFee().setLine(0);
			getCustomerFee().setInvoicingGroup((InvoicingGroup)BeanManager.getManagerBean(InvoicingGroup.class).createNewTo());
		}
		removeProject(null);
	}

	public void onItemChanged(ValueChangeEvent event) {
		Item oldItem = (Item)event.getOldValue();
		Item newItem = (Item)event.getNewValue();
		if (oldItem.getFullName().equals(getCustomerFee().getDescription())) {
			getCustomerFee().setDescription(newItem.getFullName());
		}
	}

	public void onDescriptionChanged(ValueChangeEvent event) {
		getCustomerFee().setDescription((String)event.getNewValue());
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);

		if (StringUtils.isEmpty(getCustomerFee().getDescription())) {
			String longDescription = customerFee.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				customerFee.setDescription(customerFee.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public List<SelectItem> getProjects() throws ManagerBeanException {
		Prepayment prepayment = (Prepayment)getTo();
		List<SelectItem> projects = new LinkedList<SelectItem>();
		if (prepayment.getCustomer() != null && prepayment.getCustomer().getId() != null) {
			IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID), prepayment.getCustomer().getId());
			criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_ACTIVE), Boolean.TRUE);
			criteria.addOrder(projectBean.getFieldName(IEntityAlias.PROJECT_NAME));
			for (ITransferObject ito : projectBean.getList(criteria)) {
				Project project = (Project)ito;
				SelectItem item = new SelectItem(project, project.getName());
				projects.add(item);
			}
		}
		return projects;
	}

	public void removeProject(ActionEvent event) throws ManagerBeanException {
		getCustomerFee().setProject(null);
	}

	public boolean isInvoicingGroupInMyScopes() {
		return UserUtils.getInstance().isScopeInUserScopes(getCustomerFee().getInvoicingGroup().getCustomer().getScope());
	}

	public void onLoadFinance(ActionEvent event) throws ManagerBeanException {
		Finance finance = ((Prepayment)getTo()).getFinance();

		BasicController financeController = (BasicController)AonUtil.getRegisteredBean(FINANCE_CONTROLLER_NAME);
		financeController.onLoad(event, finance.getId(), PREPAYMENT_FORM_NAME, PREPAYMENT_CONTROLLER_NAME + ".refresh");
	}

	public void onLoadCustomer(ActionEvent event) throws ManagerBeanException {
		if (getCustomerFee() != null) {
			CustomerController customerController = (CustomerController)AonUtil.getRegisteredBean(CUSTOMER_CONTROLLER_NAME);
			customerController.setSelectedTab(ICustomerConstants.CUSTOMER_FEE_TAB);
			customerController.onLoad(event, getCustomerFee().getCustomer().getId(), PREPAYMENT_FORM_NAME, null);
		}
	}

	public void onLoadInvoice(ActionEvent event) throws ManagerBeanException {
		if (getInvoiceDetail() != null) {
			BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
			invoiceController.onLoad(event, getInvoiceDetail().getInvoice().getId(), PREPAYMENT_FORM_NAME, PREPAYMENT_CONTROLLER_NAME + ".refreshInvoice");
		}
	}

	public void refreshInvoice(ActionEvent event) throws ManagerBeanException {
		Prepayment prepayment = (Prepayment)getTo();
		setInvoiceDetail((InvoiceDetail)BeanManager.getManagerBean(InvoiceDetail.class).get(prepayment.getCollectId()));
	}

}