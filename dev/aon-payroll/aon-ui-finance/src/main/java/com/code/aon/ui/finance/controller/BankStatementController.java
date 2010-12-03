package com.code.aon.ui.finance.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.IAttachmentController;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.StatementConcept;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.event.BankStatementSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class BankStatementController extends BasicController {

	private RegistryBank registryBank;
	private Date operationDate;
	private boolean showImportFileWindow;
	private boolean aeb43;
	private AonFile aonFile;

	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	public Date getOperationDate() {
		return (operationDate == null) ? new Date() : operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public boolean isShowImportFileWindow() {
		return showImportFileWindow;
	}
	public void setShowImportFileWindow(boolean value) {
		this.showImportFileWindow = value;
	}

	public boolean isAeb43() {
		return aeb43;
	}
	public void setAeb43(boolean aeb43) {
		this.aeb43 = aeb43;
	}

	public AonFile getAonFile() {
		return aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	public int getAvailableRegistryBanks() throws ManagerBeanException {
		String companyControllerName = ICompanyConstants.COLLECTIONS_CONTROLLER_NAME;
		CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(companyControllerName);
		return companyCollections.getCompanyBanks().size();
	}

	public void onFullReset(ActionEvent event) {
		try {
			getCriteria().addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_ID), new Integer(0));
			onReset(event);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onImportFileShow(ActionEvent event) {
		setAeb43(true);
		setAonFile(null);

		try {
			getCriteria().addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_ID), new Integer(0));
			setTo(null);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void fileUploaded(UploadEvent event) {
		AonFile aonFile = new AonFile();
		aonFile.setFile(event.getUploadItem().getFile());
		aonFile.setFileName(event.getUploadItem().getFileName());
		setAonFile(aonFile);
	}

	public void onImportFile(ActionEvent event) {
		try {
			if (isAeb43()) {
				importAeb43();
			} else {
				importCsv();
			}
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void importAeb43() throws ManagerBeanException, IOException {
		BankStatement bankStatement = null;
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(getAonFile().getFile())));
		String line = reader.readLine();
		while (line != null) {
			String lineType = line.substring(0, 2);
			if (lineType.equals("11")) {
				RegistryBank registryBank = importAeb43Header(line);
				if (registryBank != null) {
					setRegistryBank(registryBank);
				} else{
					break;
				}
			} else if (lineType.equals("22")) {
				bankStatement = importAeb43Data(line);
			} else if (lineType.equals("23")) {
				importAeb43Concept(line, bankStatement);
			} else {
				break;
			}
			line = reader.readLine();
		}

		if (bankStatement != null) {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_REGISTRY_BANK_ID), getRegistryBank().getId());
			setCriteria(criteria);
			onSearch(null);
		}
	}

	private RegistryBank importAeb43Header(String line) throws ManagerBeanException {
		String searchControllerName = IFinanceConstants.BANK_STATEMENT_SEARCH_CONTROLLER_NAME;
		BankStatementSearchListener bankStatementSearch = (BankStatementSearchListener)AonUtil.getRegisteredBean(searchControllerName);
		bankStatementSearch.initData();
		bankStatementSearch.setFromDate(obtainDateAAMMDD(line.substring(20, 26)));
		bankStatementSearch.setToDate(obtainDateAAMMDD(line.substring(26, 32)));

		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), getRegistryBank().getRegistry().getId());
		String bankAcc = line.substring(2, 10) + "__" + line.substring(10, 20);
		criteria.addExpression(ExpressionUtilities.getLikeExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_BANK_ACCOUNT), bankAcc));
		Iterator<?> iterator = rBankBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (RegistryBank)iterator.next();
		}
		return null;
	}

	private BankStatement importAeb43Data(String line) throws ManagerBeanException {
		int conceptIdx = Integer.parseInt(line.substring(22, 24));
		conceptIdx = (conceptIdx > 90) ? (conceptIdx - 80) : conceptIdx;

		BankStatement bankStatement = new BankStatement();
		bankStatement.setRegistryBank(getRegistryBank());
		bankStatement.setOperationDate(obtainDateAAMMDD(line.substring(10, 16)));
		bankStatement.setConcept(StatementConcept.values()[conceptIdx]);
		bankStatement.setPayment(line.substring(27, 28).equals("1"));
		bankStatement.setAmount(Double.parseDouble(line.substring(28, 42)) / 100);
		bankStatement.setDocument(Integer.parseInt(line.substring(42, 52)));
		bankStatement.setReference1(line.substring(52, 64));
		bankStatement.setReference2(line.substring(64, 80));
		bankStatement.setDescription(line.substring(52, 80));
		bankStatement.setStatus(StatementStatus.PENDING);
		return (BankStatement)getManagerBean().insert(bankStatement);
	}

	private BankStatement importAeb43Concept(String line, BankStatement bankStatement) throws ManagerBeanException {
		String description = line.substring(4, 42).trim() + " " + line.substring(42, 80).trim();
		if (!bankStatement.getDescription().equals(bankStatement.getReference1() + bankStatement.getReference2())) {
			description = bankStatement.getDescription() + " " + description;
		}
		bankStatement.setDescription((description.length() > 80) ? description.substring(0, 79) : description);
		return (BankStatement)getManagerBean().update(bankStatement);
	}

	private void importCsv() throws ManagerBeanException, IOException {
		BankStatement bankStatement = null;
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(getAonFile().getFile())));
		String line = reader.readLine();
		while (line != null) {
			bankStatement = importCsvData(line, (bankStatement == null));
			line = reader.readLine();
		}

		if (bankStatement != null) {
			setCsvToDate(bankStatement.getOperationDate());

			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_REGISTRY_BANK_ID), getRegistryBank().getId());
			setCriteria(criteria);
			onSearch(null);
		}
	}

	private BankStatement importCsvData(String line, boolean firstLine) throws ManagerBeanException {
		String delim = ";";
		int pos = 1;
		Date date = null;
		String description = "";
		double amount = 0;
		boolean payment = false;

		StringTokenizer stk = new StringTokenizer(line, delim, false);
		while (stk.hasMoreTokens()) {
			String token = stk.nextToken();
			if (pos == 1) {
				date = obtainDateDDMMAA(token.replace("-", ""));
			} else if (stk.hasMoreTokens()) {
				description += token;
			} else {
				amount = Double.parseDouble(token.replace(",", "."));
				if (amount < 0) {
					payment = true;
					amount = amount * (-1);
				}
			}
			pos++;
		}

		if (firstLine) {
			String searchControllerName = IFinanceConstants.BANK_STATEMENT_SEARCH_CONTROLLER_NAME;
			BankStatementSearchListener bankStatementSearch = (BankStatementSearchListener)AonUtil.getRegisteredBean(searchControllerName);
			bankStatementSearch.initData();
			bankStatementSearch.setFromDate(date);
		}

		BankStatement bankStatement = new BankStatement();
		bankStatement.setRegistryBank(getRegistryBank());
		bankStatement.setOperationDate(date);
		bankStatement.setConcept(StatementConcept.UNKNOWN);
		bankStatement.setPayment(payment);
		bankStatement.setAmount(amount);
		bankStatement.setDocument(0);
		bankStatement.setDescription(description);
		bankStatement.setStatus(StatementStatus.PENDING);
		return (BankStatement)getManagerBean().insert(bankStatement);
	}

	private void setCsvToDate(Date toDate) {
		String searchControllerName = IFinanceConstants.BANK_STATEMENT_SEARCH_CONTROLLER_NAME;
		BankStatementSearchListener bankStatementSearch = (BankStatementSearchListener)AonUtil.getRegisteredBean(searchControllerName);
		bankStatementSearch.setToDate(toDate);
	}

	private Date obtainDateAAMMDD(String date) {
		GregorianCalendar calendar = new GregorianCalendar();
		int year = 2000 + Integer.parseInt(date.substring(0, 2));
		int month = Integer.parseInt(date.substring(2, 4)) - 1;
		int day = Integer.parseInt(date.substring(4, 6));
		calendar.set(year, month, day, 0, 0, 0);
		return calendar.getTime();
	}

	private Date obtainDateDDMMAA(String date) {
		GregorianCalendar calendar = new GregorianCalendar();
		int day = Integer.parseInt(date.substring(0, 2));
		int month = Integer.parseInt(date.substring(2, 4)) - 1;
		int year = Integer.parseInt(date.substring(4, 8));
		calendar.set(year, month, day, 0, 0, 0);
		return calendar.getTime();
	}

	public void onChangeBank(ValueChangeEvent event) {
		if (!isNew()) {
			RegistryBank registryBank = (RegistryBank)event.getNewValue();
			try {
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_REGISTRY_BANK_ID), registryBank.getId());
				setCriteria(criteria);
				onSearch(null);
			} catch (ManagerBeanException e) {
				addMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}
	}

/*
	private static final Logger LOGGER = LoggerFactory.getLogger(BankStatementController.class.getName());

	private static final String SALE_VIEW_NAME = "saleInvoiceRecorder_list";
	private static final String PURCHASE_VIEW_NAME = "purchaseInvoiceRecorder_list";
	private static final String EXPENSE_VIEW_NAME = "expenseInvoiceRecorder_list";
	private static final String UNDEDUCTIBLE_VIEW_NAME = "undeductibleInvoiceRecorder_list";
	private String invoiceViewer;
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;

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
							String msg = "Error recording invoice:  " + invoice.getReferenceCode();
							LOGGER.error(msg, e);
							AonUtil.addErrorMessage(msg);
							throw new AbortProcessingException(msg);
						} finally {
							HibernateUtil.closeSession(sessionName);
						}
					}
				}
			}
			this.onSearch(null);
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public String invoiceView() {
		return getInvoiceViewer();
	}

	public void onShowAccountEntry(ActionEvent event) {
		try {
			InvoiceRecorder ir = (InvoiceRecorder) getModel().getRowData();
			ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
			ir.setShowAccountEntry(true);
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowAllAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry(true);
				ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
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

	public void onShowCheckedAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry(ir.isChecked() ? true : ir.isShowAccountEntry());
				if (ir.isShowAccountEntry()) {
					ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
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
					ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
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
					ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
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
					ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
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
					ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
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
			String currentViewName;
			if (type == InvoiceType.SALES) {
				invoiceControllerName = IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME;
				setInvoiceViewer(IFinanceConstants.SALE_INVOICE_FORM_NAME);
				currentViewName = SALE_VIEW_NAME;
			} else if (type == InvoiceType.PURCHASE) {
				invoiceControllerName = IFinanceConstants.PURCHASE_INVOICE_CONTROLLER_NAME;
				setInvoiceViewer(IFinanceConstants.PURCHASE_INVOICE_FORM_NAME);
				currentViewName = PURCHASE_VIEW_NAME;
			} else if (type == InvoiceType.EXPENSES) {
				invoiceControllerName = IFinanceConstants.EXPENSE_INVOICE_CONTROLLER_NAME;
				setInvoiceViewer(IFinanceConstants.EXPENSE_INVOICE_FORM_NAME);
				currentViewName = EXPENSE_VIEW_NAME;
			} else if (type == InvoiceType.UNDEDUCTIBLE) {
				invoiceControllerName = IFinanceConstants.UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME;
				setInvoiceViewer(IFinanceConstants.UNDEDUCTIBLE_INVOICE_FORM_NAME);
				currentViewName = UNDEDUCTIBLE_VIEW_NAME;
			} else {
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				String msg = "No existe visor para el tipo de factura " + type.getName(locale);
				LOGGER.warn(msg);
				AonUtil.addWarningMessage(msg);
				throw new AbortProcessingException(msg);
			}
			recordController.setRefresh(true);

			InvoiceController invoiceController = (InvoiceController) AonUtil.getRegisteredBean(invoiceControllerName);
			invoiceController.onLoadInvoice(event, recordController.getInvoice(), currentViewName);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar la factura: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
*/
}