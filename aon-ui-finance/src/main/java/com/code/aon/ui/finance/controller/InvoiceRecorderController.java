package com.code.aon.ui.finance.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAttachment;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvoiceRecorderController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceRecorderController.class.getName());

	private static final String INVOICE_RECORDER_VIEW_NAME = "invoiceRecorder_list";
	private String invoiceViewer;
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;
	private String checkOption;
	private String invoiceAttachURL;
	private String showBreakDownOption;
	private String showAccountEntryOption;
	

	public List<ITransferObject> search(int start, int count) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			List<ITransferObject> invoices = super.search(start, count);
			List<ITransferObject> list = new LinkedList<ITransferObject>();
			for (ITransferObject to : invoices) {
				Invoice invoice = (Invoice) to;
				InvoiceRecorder ir = new InvoiceRecorder();
				ir.setInvoice(invoice);
				// Se fuerza a calcular el total.
				ir.getInvoiceTotal();
				ir.setRefresh(true);
				list.add(ir);
			}
			HibernateUtil.commitTransaction(sessionName);
			return list;
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg = "Error recuperando facturas";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if (accountEntryInvoiceWriter == null) {
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}

	public String getInvoiceViewer() {
		return invoiceViewer;
	}

	public void setInvoiceViewer(String invoiceViewer) {
		this.invoiceViewer = invoiceViewer;
	}
	
	public String getCheckOption() {
		return checkOption;
	}
	
	public void setCheckOption(String checkOption) {
		this.checkOption = checkOption;
	}
	
	public String getShowBreakDownOption() {
		return showBreakDownOption;
	}
	public void setShowBreakDownOption(String showBreakDownOption) {
		this.showBreakDownOption = showBreakDownOption;
	}
	
	public String getShowAccountEntryOption() {
		return showAccountEntryOption;
	}
	public void setShowAccountEntryOption(String showAccountEntryOption) {
		this.showAccountEntryOption = showAccountEntryOption;
	}
	
	public boolean getShowInvoiceAttach() {
		return AonStringUtils.isNotBlank(invoiceAttachURL) ;
	}
	
	
	public void onCheckOption(ActionEvent event) throws ManagerBeanException {
		if(getCheckOption().equals("InvoiceRecorder-checkAll")) {
			this.onCheckAll(null);
		}else if(getCheckOption().equals("InvoiceRecorder-checkNothing")) {
			this.onCheckNone(null);
		}else if(getCheckOption().equals("InvoiceRecorder-checkBrokenDown")) {
			this.onCheckBrokendown(null);
		}else if(getCheckOption().equals("InvoiceRecorder-checkUnBrokenDown")) {
			this.onCheckUnbrokendown(null);
		}else if(getCheckOption().equals("InvoiceRecorder-checkRight")) {
			this.onCheckRight(null);
		}else if(getCheckOption().equals("InvoiceRecorder-checkWarned")) {
			this.onCheckWarned(null);
		}else if(getCheckOption().equals("InvoiceRecorder-checkEntryVisible")) {
			this.onCheckEntryVisible(null);
		}else if(getCheckOption().equals("InvoiceRecorder-checkEntryInvisible")) {
			this.onCheckEntryInvisible(null);
		}else {
			this.onCheckNone(null);
		}
	}

	public void onCheckAll(ActionEvent event) throws ManagerBeanException {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked(true);
		}
	}

	public void onCheckNone(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked(false);
		}
	}

	public void onCheckBrokendown(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked(ir.isShowTaxBreakDowns() ? true : ir.isChecked());
		}
	}

	public void onCheckUnbrokendown(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked(!ir.isShowTaxBreakDowns() ? true : ir.isChecked());
		}
	}

	public void onCheckRight(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked((ir.isRecordable() && !ir.isWarned()) ? true : ir.isChecked());
		}
	}

	public void onCheckWarned(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked((ir.isRecordable() && ir.isWarned()) ? true : ir.isChecked());
		}
	}

	public void onCheckEntryVisible(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked(ir.isShowAccountEntry() ? true : ir.isChecked());
		}
	}

	public void onCheckEntryInvisible(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked(!ir.isShowAccountEntry() ? true : ir.isChecked());
		}
	}

	@SuppressWarnings("unchecked")
	public List<InvoiceRecorder> getCurrentList() {
		try {
			return (List<InvoiceRecorder>) getModel().getWrappedData();
		} catch (ManagerBeanException e) {
			String msg = "Error obtaining model! ";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onRecordSelected(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			for (InvoiceRecorder invoiceRecorder : getCurrentList()) {
				if (invoiceRecorder.isRecordable() && invoiceRecorder.isChecked()) {
					Invoice invoice = invoiceRecorder.getInvoice();
					if (invoice.getStatus() == InvoiceStatus.PENDING) {
						try {
							HibernateUtil.beginTransaction(sessionName);
							getAccountEntryInvoiceWriter().recordInvoice(invoice);
							invoice.setStatus(InvoiceStatus.SCORED);
							HibernateUtil.getSession(sessionName).merge(invoice);
							HibernateUtil.getSession(sessionName).flush();
							HibernateUtil.commitTransaction(sessionName);
						} catch (Exception e) {
							try {
								HibernateUtil.rollbackTransaction(sessionName);
							} catch (DAOException daoe) {
								String msg = "Unable to rollback transaction!";
								LOGGER.error(msg, e);
							}
							String msg = "Error al contabilizar:  " + invoice.getReferenceCode() + " [" + e.getMessage() +"]";
							LOGGER.error(msg, e);
							AonUtil.addErrorMessage(msg);
							throw new AbortProcessingException(msg);
						} finally {
							HibernateUtil.closeSession(sessionName);
						}
					}
				}
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
			this.onSearch(null);
		}
	}

	public void onUnrecordSelected(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			for (InvoiceRecorder invoiceRecorder : getCurrentList()) {
				if (invoiceRecorder.isChecked()) {
					Invoice invoice = invoiceRecorder.getInvoice();
					try {
						HibernateUtil.beginTransaction(sessionName);
						getAccountEntryInvoiceWriter().unrecordInvoice(invoice);
						invoice.setStatus(InvoiceStatus.PENDING);
						HibernateUtil.getSession(sessionName).merge(invoice);
						HibernateUtil.getSession(sessionName).flush();
						HibernateUtil.commitTransaction(sessionName);
					} catch (Exception e) {
						try {
							HibernateUtil.rollbackTransaction(sessionName);
						} catch (DAOException daoe) {
							String msg = "Unable to rollback transaction!";
							LOGGER.error(msg, e);
						}
						String msg = "Error al descontabilizar:  " + invoice.getReferenceCode() + " [" + e.getMessage() +"]";
						LOGGER.error(msg, e);
						AonUtil.addErrorMessage(msg);
						throw new AbortProcessingException(msg);
					} finally {
						HibernateUtil.closeSession(sessionName);
					}
				}
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
			this.onSearch(null);
		}
	}

	public String invoiceView() {
		return getInvoiceViewer();
	}

	public void onShowAccountEntry(ActionEvent event) {
		try {
			InvoiceRecorder ir = (InvoiceRecorder) getModel().getRowData();
			ir.setDetails(obtaingAccountEntryDetailList(ir.getInvoice()));
			ir.setShowAccountEntry(true);
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onShowAccountEntryOption(ActionEvent event) throws ManagerBeanException {
		if(getCheckOption().equals("InvoiceRecorder-showAllAccountEntry")) {
			this.onShowAllAccountEntry(null);
		}else if(getCheckOption().equals("InvoiceRecorder-showNothingAccountEntry")) {
			this.onHideAllAccountEntry(null);
		}else if(getCheckOption().equals("InvoiceRecorder-showCheckedAccountEntry")) {
			this.onShowCheckedAccountEntry(null);
		}else if(getCheckOption().equals("InvoiceRecorder-showUnCheckedAccountEntry")) {
			this.onShowUncheckedAccountEntry(null);
		}else if(getCheckOption().equals("InvoiceRecorder-showRightAccountEntry")) {
			this.onShowCorrectAccountEntry(null);
		}else if(getCheckOption().equals("InvoiceRecorder-showIncorrectAccountEntry")) {
			this.onShowIncorrectAccountEntry(null);
		}else if(getCheckOption().equals("InvoiceRecorder-showWarnedAccountEntry")) {
			this.onShowWarnedAccountEntry(null);
		}else {
			this.onCheckNone(null);
		}
	}

	public void onShowAllAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setDetails(obtaingAccountEntryDetailList(ir.getInvoice()));
				ir.setShowAccountEntry(true);
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowCheckedAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry(ir.isChecked() ? true : ir.isShowAccountEntry());
				if (ir.isShowAccountEntry()) {
					ir.setDetails(obtaingAccountEntryDetailList(ir.getInvoice()));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowUncheckedAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry(!ir.isChecked() ? true : ir.isShowAccountEntry());
				if (ir.isShowAccountEntry()) {
					ir.setDetails(obtaingAccountEntryDetailList(ir.getInvoice()));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowCorrectAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry((ir.isRecordable() && !ir.isWarned()) ? true : ir.isShowAccountEntry());
				if (ir.isShowAccountEntry()) {
					ir.setDetails(obtaingAccountEntryDetailList(ir.getInvoice()));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowIncorrectAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry((!ir.isRecordable()) ? true : ir.isShowAccountEntry());
				if (ir.isShowAccountEntry()) {
					ir.setDetails(obtaingAccountEntryDetailList(ir.getInvoice()));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowWarnedAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry((ir.isRecordable() && ir.isWarned()) ? true : ir.isShowAccountEntry());
				if (ir.isShowAccountEntry()) {
					ir.setDetails(obtaingAccountEntryDetailList(ir.getInvoice()));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private List<AccountEntryDetail> obtaingAccountEntryDetailList(Invoice invoice) throws ManagerBeanException {
		if (invoice.isRecorded()) {
			List<AccountEntryDetail> entryDetailList = new LinkedList<AccountEntryDetail>();
			IManagerBean entryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryInvoiceBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID), invoice.getId());
			for (ITransferObject ito : entryInvoiceBean.getList(criteria)) {
				AccountEntryInvoice entryInvoice = (AccountEntryInvoice)ito;
				IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), entryInvoice.getAccountEntry().getId());
				int line = 0;
				for (ITransferObject itr : entryDetailBean.getList(criteria)) {
					AccountEntryDetail entryDetail = (AccountEntryDetail)itr;
					entryDetail.setLine(++line);
					entryDetailList.add(entryDetail);
				}
				break;
			}
			return entryDetailList;
		} else {
			return getAccountEntryInvoiceWriter().preRecordInvoice(invoice);
		}
	}

	public void onHideAccountEntry(ActionEvent event) {
		try {
			InvoiceRecorder ir = (InvoiceRecorder) getModel().getRowData();
			ir.setShowAccountEntry(false);
		} catch (ManagerBeanException e) {
			String msg = "Imposible ocultar la previsualización del apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onHideAllAccountEntry(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowAccountEntry(false);
		}
	}

	public void onShowTaxBreakDowns(ActionEvent event) {
		try {
			InvoiceRecorder ir = (InvoiceRecorder) getModel().getRowData();
			ir.setShowTaxBreakDowns(true);
			ir.getTaxBreakDowns();
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar el deglose de la factura: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowBreakDownOption(ActionEvent event) throws ManagerBeanException {
		if(getShowBreakDownOption().equals("InvoiceRecorder-showBreakDownAll")) {
			this.onShowAllTaxBreakDowns(null);
		}else if(getShowBreakDownOption().equals("InvoiceRecorder-showBreakDownNothing")) {
			this.onHideAllTaxBreakDowns(null);
		}else if(getShowBreakDownOption().equals("InvoiceRecorder-showBreakDownChecked")) {
			this.onShowCheckedTaxBreakDowns(null);
		}else if(getShowBreakDownOption().equals("InvoiceRecorder-showBreakDownUnChecked")) {
			this.onShowUncheckedTaxBreakDowns(null);
		}else if(getShowBreakDownOption().equals("InvoiceRecorder-showBreakDownRight")) {
			this.onShowCorrectTaxBreakDowns(null);
		}else if(getShowBreakDownOption().equals("InvoiceRecorder-showBreakDownIncorrect")) {
			this.onShowIncorrectTaxBreakDowns(null);
		}else if(getShowBreakDownOption().equals("InvoiceRecorder-showBreakDownWarned")) {
			this.onShowWarnedTaxBreakDowns(null);
		}else {
			this.onHideAllTaxBreakDowns(null);
		}
	}

	public void onShowAllTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns(true);
		}
	}

	public void onHideAllTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns(false);
		}
	}

	public void onShowCheckedTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns(ir.isChecked() ? true : ir.isShowTaxBreakDowns());
		}
	}

	public void onShowUncheckedTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns(!ir.isChecked() ? true : ir.isShowTaxBreakDowns());
		}
	}

	public void onShowCorrectTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns((ir.isRecordable() && !ir.isWarned()) ? true : ir.isShowTaxBreakDowns());
		}
	}

	public void onShowIncorrectTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns((!ir.isRecordable()) ? true : ir.isShowTaxBreakDowns());
		}
	}

	public void onShowWarnedTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns((ir.isRecordable() && ir.isWarned()) ? true : ir.isShowTaxBreakDowns());
		}
	}

	public void onHideTaxBreakDowns(ActionEvent event) {
		try {
			InvoiceRecorder ir = (InvoiceRecorder) getModel().getRowData();
			ir.setShowTaxBreakDowns(false);
		} catch (ManagerBeanException e) {
			String msg = "Imposible ocultar el deglose de la factura: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onLoadInvoice(ActionEvent event) {
		try {
			InvoiceRecorder recordController = (InvoiceRecorder)getModel().getRowData();
			InvoiceType type = recordController.getInvoice().getType();
			String invoiceControllerName;
			String currentViewName = INVOICE_RECORDER_VIEW_NAME;
			if (type == InvoiceType.SALES) {
				invoiceControllerName = IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME;
				setInvoiceViewer(IFinanceConstants.SALE_INVOICE_FORM_NAME);
			} else if (type == InvoiceType.PURCHASE) {
				invoiceControllerName = IFinanceConstants.PURCHASE_INVOICE_CONTROLLER_NAME;
				setInvoiceViewer(IFinanceConstants.PURCHASE_INVOICE_FORM_NAME);
			} else if (type == InvoiceType.EXPENSES) {
				invoiceControllerName = IFinanceConstants.EXPENSE_INVOICE_CONTROLLER_NAME;
				setInvoiceViewer(IFinanceConstants.EXPENSE_INVOICE_FORM_NAME);
			} else if (type == InvoiceType.UNDEDUCTIBLE) {
				invoiceControllerName = IFinanceConstants.UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME;
				setInvoiceViewer(IFinanceConstants.UNDEDUCTIBLE_INVOICE_FORM_NAME);
			} else {
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				String msg = "No existe visor para el tipo de factura " + type.getName(locale);
				LOGGER.warn(msg);
				AonUtil.addWarningMessage(msg);
				throw new AbortProcessingException(msg);
			}
			recordController.setRefresh(true);
			InvoiceController invoiceController = (InvoiceController) AonUtil.getRegisteredBean(invoiceControllerName);
			invoiceController.onLoad(event, recordController.getInvoice().getId(), currentViewName, "");
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar la factura: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onShowInvoiceAttach(ActionEvent event) {
		try {
			InvoiceRecorder  recordController = (InvoiceRecorder) getModel().getRowData();
			Invoice invoice = recordController.getInvoice();
			if ( invoice.isSales() ) { 
				invoiceAttachURL = URLEncoder.encode(getDownloadURL(recordController.getInvoice()), "UTF-8");
			} else {
				invoiceAttachURL = URLEncoder.encode(getAttachURL(recordController.getInvoice()), "UTF-8");;
			}
		} catch (ManagerBeanException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			invoiceAttachURL = e.getMessage();
		}
	}
	
	public void onHideInvoiceAttach(ActionEvent event) {
		invoiceAttachURL = null;
	}
	public String getInvoiceAttachURL() {
		return invoiceAttachURL;
	}
	public String getAttachURL(Invoice invoice) throws ManagerBeanException{
		com.esferalia.aon.occam.api.model.Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		
		LinkedList<Attach> invoiceAttachments = AON.getAttachList(domain.getName(), domain.getId(), "", p -> p.getAttachModuleProperty().eq(invoice.getId()), AttachType.INVOICE);
		
		return invoiceAttachments.stream().findFirst().map( invoiceAttach -> {
			JSONObject data = new JSONObject()
					.put("domain_id", domain.getId())
					.put("domain_name", domain.getName())
					.put(IJsonNames.ID, invoiceAttach.getId())
					.put("attach_type", AttachType.INVOICE.getName());
			
			return  "/ms/api/file/" +  Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));	
		}).orElseThrow(ManagerBeanException::new);
		
	}

	public String getDownloadURL(Invoice invoice) {
		com.esferalia.aon.occam.api.model.Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "");
		JSONObject json = new JSONObject()
				.put(IJsonNames.ID, invoice.getId())
				.put(IJsonNames.SOURCE, "invoice")
				.put("domain_id", domain.getId())
				.put("domain_name", domain.getName())
				.put(IJsonNames.LOGIN, UserUtils.getInstance().getLoggedUser().getLogin());		
		return "/ms/api/download_invoice_pdf?json=" + Base64.getEncoder().encodeToString(json.toString().getBytes(StandardCharsets.UTF_8));
	}
}