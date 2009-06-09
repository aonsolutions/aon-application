package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.Company;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAttachment;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class InvoiceSignerController extends BasicController{
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceSignerController.class.getName());

	private String password;

	private boolean showPasswordWindow;

	private ArrayList<Invoice> checks = new ArrayList<Invoice>();

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isShowPasswordWindow() {
		return showPasswordWindow;
	}

	public void setShowPasswordWindow(boolean value) {
		this.showPasswordWindow = value;
	}
	
	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			Invoice invoice = (Invoice)iter.next();
			if(!isSigned(invoice)) {
				if (!checks.contains( invoice )) {
					checks.add( invoice );
				}
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedInvoices();
	}

	public boolean getRowChecked() {
		Invoice to = (Invoice) model.getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Invoice to = (Invoice) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Invoice to = (Invoice) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public ArrayList<Invoice> getCheckedInvoices() {
		return checks;
	}

	public void clearCheckedInvoices() {
		checks = new ArrayList<Invoice>();
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		clearCheckedInvoices();
	}
	
	public boolean isModelToSigned() throws ManagerBeanException{
		if (getModel().isRowAvailable()) {
			Invoice invoice = (Invoice)this.getModel().getRowData();
			return isSigned(invoice);
		}
		return true;
	}
	
	private boolean isSigned(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceAttachBean = BeanManager.getManagerBean(InvoiceAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceAttachBean.getFieldName(IFinanceAlias.INVOICE_ATTACHMENT_INVOICE_ID), invoice.getId());
		return (invoiceAttachBean.getCount(criteria) > 0);
	}

	public void onPasswordShow(ActionEvent event) {
		setPassword(null);
	}

	public void onSignSelected(ActionEvent event){
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction( false );
			HibernateUtil.setCloseSession( false );

			RegistryAttachment signature = obtainCompanySignature();
			if (signature != null) {
				IManagerBean invoiceAttachBean = BeanManager.getManagerBean(InvoiceAttachment.class);

				Iterator<Invoice> iter = getCheckedInvoices().iterator();
				while(iter.hasNext()){
					Invoice invoice = iter.next();
					try {
						HibernateUtil.beginTransaction(sessionName);

						InvoiceAttachment invoiceAttach = new InvoiceAttachment();
						invoiceAttach.setInvoice(invoice);
						invoiceAttach.setMimeType(signature.getMimeType());
						invoiceAttach.setData(signature.getData());
						invoiceAttach.setDescription(signature.getDescription());
						invoiceAttach.setSize(signature.getSize());
						invoiceAttachBean.insert(invoiceAttach);

						HibernateUtil.getSession(sessionName).flush();					
						HibernateUtil.commitTransaction(sessionName);
					} catch (Exception e) {
						try {
							HibernateUtil.rollbackTransaction(sessionName);
						} catch (DAOException daoe) {
							String msg =  "Unable to rollback transaction!";
							LOGGER.log(Level.SEVERE, msg, e);
						}
						String msg =  "Error recording invoice:  " + invoice.getReferenceCode();
						LOGGER.log(Level.SEVERE, msg, e);
						AonUtil.addErrorMessage(msg);
						throw new AbortProcessingException(msg);
					} finally {
						HibernateUtil.closeSession(sessionName);
					}
				}
				clearCheckedInvoices();
				this.onSearch(null);
			}
		} catch (ManagerBeanException e) {
			String msg =  "Error obtaining company signature";
			LOGGER.log(Level.SEVERE, msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private RegistryAttachment obtainCompanySignature() throws ManagerBeanException {
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = companyController.obtainCompany();

		IManagerBean rattachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rattachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
		criteria.addEqualExpression(rattachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.DIGITAL_CERTIFICATE);
		Iterator<ITransferObject> iterator = rattachBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (RegistryAttachment)iterator.next();
		}
		return null;
	}

}