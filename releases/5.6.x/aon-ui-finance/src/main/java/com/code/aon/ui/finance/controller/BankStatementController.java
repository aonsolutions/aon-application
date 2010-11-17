package com.code.aon.ui.finance.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.richfaces.event.UploadEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.StatementConcept;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.event.BankStatementSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class BankStatementController extends BasicController {

	private RegistryBank registryBank;
	private Date operationDate;
	private boolean showImportFileWindow;
	private boolean aeb43;
	private AonFile aonFile;
	private boolean showLinkWindow;
	private BankStatementLinkManager linkManager;
	private Map<Integer, List<BankStatementLink>> links;
	private Map<Integer, String> errors;

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

	public boolean isShowLinkWindow() {
		return showLinkWindow;
	}
	public void setShowLinkWindow(boolean value) {
		this.showLinkWindow = value;
	}

	public BankStatementLinkManager getBankStatementLinkManager() {
		if (linkManager == null) {
			linkManager = new BankStatementLinkManager(); 
		}
		return linkManager;
	}

	public void setBankStatementLinkManager(BankStatementLinkManager linkManager) {
		this.linkManager = linkManager;
	}

	public Map<Integer, List<BankStatementLink>> getLinks() {
		return links;
	}
	public void setLinks(Map<Integer, List<BankStatementLink>> links) {
		this.links = links;
	}

	public Map<Integer, String> getErrors() {
		return errors;
	}
	public void setErrors(Map<Integer, String> errors) {
		this.errors = errors;
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

	public void onAddLinkShow(ActionEvent event) throws ManagerBeanException {
		BankStatement to = (BankStatement)getModel().getRowData();
		boolean payment = (to.getConcept() != StatementConcept.RETURNED) ? to.isPayment() : !to.isPayment();
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		if (to.getConcept() != StatementConcept.RETURNED) {
			String statusAlias = financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS);
			Expression pendingExpr = ExpressionUtilities.getEqualExpression(statusAlias, FinanceStatus.PENDING);
			Expression returnedExpr = ExpressionUtilities.getEqualExpression(statusAlias, FinanceStatus.RETURNED);
			criteria.addOrExpression(ExpressionUtilities.getOrExpression(pendingExpr, returnedExpr));
		} else {
			criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PAID);
		}
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_PAYMENT), new Boolean(payment));
		criteria.addLessThanOrEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), to.getOperationDate());
		criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), (to.getConcept() != StatementConcept.RETURNED));
		criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_CONCEPT), (to.getConcept() != StatementConcept.RETURNED));
		getBankStatementLinkManager().setCurrentStatement(to);
		getBankStatementLinkManager().setFinanceListFiltered(financeBean.getList(criteria));
		getBankStatementLinkManager().setFinanceModel(null);
		getBankStatementLinkManager().clearCheckedFinance();

		List<ITransferObject> fBatchList = new LinkedList<ITransferObject>();
		if (to.getConcept() == StatementConcept.COLLECTION_BATCH) {
			IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
			criteria = new Criteria();
			criteria.addEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_REGISTRY_BANK_ID), getRegistryBank().getId());
			criteria.addNotEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), FinanceBatchStatus.RECORDED);
			criteria.addLessThanOrEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE), to.getOperationDate());
			criteria.addOrder(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE));
			fBatchList = fBatchBean.getList(criteria);
		}
		getBankStatementLinkManager().setFbatchListFiltered(fBatchList);
		getBankStatementLinkManager().setFbatchModel(null);
		getBankStatementLinkManager().clearCheckedFbatch();

		getBankStatementLinkManager().setSelectedTab((to.getConcept() == StatementConcept.COLLECTION_BATCH) ? "fBatchLinkTab" : "financeLinkTab");
	}

	public void onAddLink(ActionEvent event) throws ManagerBeanException {
		BankStatement to = getBankStatementLinkManager().getCurrentStatement();
		List<BankStatementLink> linkList = new LinkedList<BankStatementLink>();

		for (Finance finance : getBankStatementLinkManager().getCheckedFinance()) {
			getBankStatementLinkManager().getLinkedFinanceList().add(finance);
			BankStatementLink link = new BankStatementLink();
			link.setTo(finance);
			link.setAmount(finance.getTotalAmount());

			linkList.add(link);
		}

		for (FinanceBatch fBatch : getBankStatementLinkManager().getCheckedFbatch()) {
			getBankStatementLinkManager().getLinkedFbatchList().add(fBatch);
			BankStatementLink link = new BankStatementLink();
			link.setTo(fBatch);
			link.setAmount(fBatch.getFinanceBatchTotalAmount());

			linkList.add(link);
		}

		getLinks().put(to.getId(), linkList);
	}

	public void onCheckLinks(ActionEvent event) throws ManagerBeanException {
		resetLinks();
		resetErrors();
		try {
			List<ITransferObject> bankStatementList = getManagerBean().getList(getCriteria());
			for (ITransferObject ito : bankStatementList) {
				BankStatement to = (BankStatement)ito;
				if (to.getStatus() != StatementStatus.RECORDED) {
					if (hasFinanceLink(to.getConcept())) {
						findFinance(to, FinanceStatus.PENDING);
					} else if (to.getConcept() == StatementConcept.RETURNED) {
						findFinance(to, FinanceStatus.PAID);
					} else if (to.getConcept() == StatementConcept.COLLECTION_BATCH && !to.isPayment()) {
						findFinanceBatch(to);
					}
				}
			}
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ExpressionException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void resetLinks() {
		setLinks(new HashMap<Integer, List<BankStatementLink>>());
	}

	public void resetErrors() {
		setErrors(new HashMap<Integer, String>());
	}

	private boolean hasFinanceLink(StatementConcept concept) {
		return (concept == StatementConcept.UNKNOWN || concept == StatementConcept.WITHDRAWAL || concept == StatementConcept.PAYMENT ||
				 concept == StatementConcept.DEPOSIT || concept == StatementConcept.COLLECTION);
	}

	private boolean hasFinanceBatchLink(StatementConcept concept) {
		return (concept == StatementConcept.COLLECTION_BATCH);
	}

	private void findFinance(BankStatement to, FinanceStatus status) throws ManagerBeanException, ExpressionException {
		boolean payment = (status == FinanceStatus.PENDING) ? to.isPayment() : !to.isPayment();
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		if (status == FinanceStatus.PENDING) {
			String statusAlias = financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS);
			Expression pendingExpr = ExpressionUtilities.getEqualExpression(statusAlias, status);
			Expression returnedExpr = ExpressionUtilities.getEqualExpression(statusAlias, FinanceStatus.RETURNED);
			criteria.addOrExpression(ExpressionUtilities.getOrExpression(pendingExpr, returnedExpr));
		} else {
			criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), status);
		}
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_PAYMENT), new Boolean(payment));
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT), to.getAmount());
		criteria.addLessThanOrEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), to.getOperationDate());
		int count = financeBean.getCount(criteria);
		if (count == 1) {
			for (ITransferObject ito : financeBean.getList(criteria)) {
				Finance finance = (Finance)ito;
				getBankStatementLinkManager().getLinkedFinanceList().add(finance);
				BankStatementLink link = new BankStatementLink();
				link.setTo(finance);
				link.setAmount(to.getAmount());

				List<BankStatementLink> linkList = new LinkedList<BankStatementLink>();
				linkList.add(link);
				getLinks().put(to.getId(), linkList);
			}
		} else if (count > 1) {
			getErrors().put(to.getId(), "Se ha encontrado más de 1 Vencimiento con ese Importe");
		} else {
			getErrors().put(to.getId(), "No se ha encontrado ningún Vencimiento con ese Importe");
		}
	}

	private void findFinanceBatch(BankStatement to) throws ManagerBeanException {
		IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_REGISTRY_BANK_ID), getRegistryBank().getId());
		criteria.addNotEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), FinanceBatchStatus.RECORDED);
		criteria.addLessThanOrEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE), to.getOperationDate());
		criteria.addOrder(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE));
		BankStatementLink link = null;
		int count = 0;
		for (ITransferObject ito : fBatchBean.getList(criteria)) {
			FinanceBatch fBatch = (FinanceBatch)ito;
			if (fBatch.getFinanceBatchTotalAmount().doubleValue() == to.getAmount()) {
				link = new BankStatementLink();
				link.setTo(fBatch);
				link.setAmount(to.getAmount());

				++count;
			}
		}

		if (count == 1) {
			List<BankStatementLink> linkList = new LinkedList<BankStatementLink>();
			linkList.add(link);
			getLinks().put(to.getId(), linkList);
		} else if (count > 1) {
			getErrors().put(to.getId(), "Se ha encontrado más de 1 Remesa con ese Importe");
		} else {
			getErrors().put(to.getId(), "No se ha encontrado ninguna Remesa con ese Importe");
		}
	}

	public String getErrorMessage() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			BankStatement to = (BankStatement)getModel().getRowData();
			return errors.get(to.getId());
		}
		return null;
	}

	public List<BankStatementLink> getBankStatementLinkList() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			BankStatement to = (BankStatement)getModel().getRowData();
			return links.get(to.getId());
		}
		return null;
	}

	public void onShowBankStatementLink(ActionEvent event) {
		try {
			BankStatement to = (BankStatement)getModel().getRowData();
			to.setShowBankStatementLink(true);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onHideBankStatementLink(ActionEvent event) {
		try {
			BankStatement to = (BankStatement)getModel().getRowData();
			to.setShowBankStatementLink(false);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

}
