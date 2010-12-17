package com.code.aon.ui.finance.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Calendar;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.richfaces.event.UploadEvent;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryBankStatement;
import com.code.aon.account.bridge.BankConceptAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.account.bridge.writer.FinanceRecordingTo;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.AonFile;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.BankConcept;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.BankStatementLink;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.StatementConcept;
import com.code.aon.finance.enumeration.StatementLinkSource;
import com.code.aon.finance.enumeration.StatementLinkStatus;
import com.code.aon.finance.enumeration.StatementReliability;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.event.BankStatementSearchListener;
import com.code.aon.ui.finance.event.FinanceListSearchListener;
import com.code.aon.ui.finance.event.FinanceTrackingListSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class BankStatementController extends BasicController implements IFinanceConstants {

	private RegistryBank registryBank;
	private Date operationDate;
	private boolean checkByAccount;
	private BankConcept bankConcept;
	private Account account;
	private boolean showImportFileWindow;
	private boolean aeb43;
	private AonFile aonFile;
	private boolean showLinkWindow;
	private BankStatementLinkManager linkManager;
	private Map<Integer, String> errors;
	private AccountEntryFinanceWriter writer;
	private ArrayList<BankStatement> bankStatementChecks= new ArrayList<BankStatement>();
	
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

	public boolean isCheckByAccount() {
		return checkByAccount;
	}
	public void setCheckByAccount(boolean value) {
		this.checkByAccount = value;
	}

	public BankConcept getBankConcept() {
		return bankConcept;
	}
	public void setBankConcept(BankConcept bankConcept) {
		this.bankConcept = bankConcept;
	}

	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
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

	public Map<Integer, String> getErrors() {
		return errors;
	}
	public void setErrors(Map<Integer, String> errors) {
		this.errors = errors;
	}

	public AccountEntryFinanceWriter getWriter() {
		if(writer == null){
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
	}

	public void onSeeAll(ActionEvent event) throws ManagerBeanException {
		String searchController = IFinanceConstants.BANK_STATEMENT_SEARCH_LISTENER_NAME;
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(searchController);
		searchListener.setStatementReliabilities(new StatementReliability[0]);
		searchListener.setStatementStatuses(new StatementStatus[0]);
		searchBankStatements(getRegistryBank());
	}

	public void onSeePending(ActionEvent event) throws ManagerBeanException {
		String searchController = IFinanceConstants.BANK_STATEMENT_SEARCH_LISTENER_NAME;
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(searchController);
		searchListener.setStatementReliabilities(new StatementReliability[0]);
		StatementStatus[] statementStatus = {StatementStatus.PENDING};
		searchListener.setStatementStatuses(statementStatus);
		searchBankStatements(getRegistryBank());
	}

	public void onSeeChecked(ActionEvent event) throws ManagerBeanException {
		String searchController = IFinanceConstants.BANK_STATEMENT_SEARCH_LISTENER_NAME;
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(searchController);
		searchListener.setStatementReliabilities(new StatementReliability[0]);
		StatementStatus[] statementStatus = {StatementStatus.CHECKED};
		searchListener.setStatementStatuses(statementStatus);
		searchBankStatements(getRegistryBank());
	}

	public void onSeeExact(ActionEvent event) throws ManagerBeanException {
		String searchController = IFinanceConstants.BANK_STATEMENT_SEARCH_LISTENER_NAME;
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(searchController);
		StatementReliability[] statementReliability = {StatementReliability.VERY_HIGH};
		searchListener.setStatementReliabilities(statementReliability);
		StatementStatus[] statementStatus = {StatementStatus.CHECKED};
		searchListener.setStatementStatuses(statementStatus);
		searchBankStatements(getRegistryBank());
	}

	public void onSeeApproximate(ActionEvent event) throws ManagerBeanException {
		String searchController = IFinanceConstants.BANK_STATEMENT_SEARCH_LISTENER_NAME;
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(searchController);
		StatementReliability[] statementReliability = {StatementReliability.HIGH};
		searchListener.setStatementReliabilities(statementReliability);
		StatementStatus[] statementStatus = {StatementStatus.CHECKED};
		searchListener.setStatementStatuses(statementStatus);
		searchBankStatements(getRegistryBank());
	}

	public void onSeeAmbiguous(ActionEvent event) throws ManagerBeanException {
		String searchController = IFinanceConstants.BANK_STATEMENT_SEARCH_LISTENER_NAME;
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(searchController);
		StatementReliability[] statementReliability = {StatementReliability.MEDIUM, StatementReliability.LOW};
		searchListener.setStatementReliabilities(statementReliability);
		StatementStatus[] statementStatus = {StatementStatus.CHECKED};
		searchListener.setStatementStatuses(statementStatus);
		searchBankStatements(getRegistryBank());
	}

	public void onSeeRecorded(ActionEvent event) throws ManagerBeanException {
		String searchController = IFinanceConstants.BANK_STATEMENT_SEARCH_LISTENER_NAME;
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(searchController);
		searchListener.setStatementReliabilities(new StatementReliability[0]);
		StatementStatus[] statementStatus = {StatementStatus.RECORDED};
		searchListener.setStatementStatuses(statementStatus);
		searchBankStatements(getRegistryBank());
	}

	public void onSelectPending(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.PENDING) {
				bankStatementChecks.add(statement);
			}
		}
	}

	public void onSelectChecked(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED) {
				bankStatementChecks.add(statement);
			}
		}
	}

	public void onSelectExact(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED && statement.getReliability() == StatementReliability.VERY_HIGH) {
				bankStatementChecks.add(statement);
			}
		}
	}

	public void onSelectApproximate(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED && statement.getReliability() == StatementReliability.HIGH) {
				bankStatementChecks.add(statement);
			}
		}
	}

	public void onSelectAmbiguous(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED) {
				if (statement.getReliability() == StatementReliability.MEDIUM || statement.getReliability() == StatementReliability.LOW) {
					bankStatementChecks.add(statement);
				}
			}
		}
	}

	public void onSelectRecorded(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.RECORDED) {
				bankStatementChecks.add(statement);
			}
		}
	}

	public void onSelectNone(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
	}

	public void onCancelAll(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED) {
				removeLinks(statement);
			}
		}
		onSearch(null);
	}

	public void onCancelSelected(ActionEvent event) throws ManagerBeanException {
		for (BankStatement statement : getCheckedBankStatement()) {
			if (statement.getStatus() == StatementStatus.CHECKED) {
				removeLinks(statement);
			}
		}
		onSearch(null);
	}

	public void onCancelExact(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED && statement.getReliability() == StatementReliability.VERY_HIGH) {
				removeLinks(statement);
			}
		}
		onSearch(null);
	}

	public void onCancelApproximate(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED && statement.getReliability() == StatementReliability.HIGH) {
				removeLinks(statement);
			}
		}
		onSearch(null);
	}

	public void onCancelAmbiguous(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED) {
				if (statement.getReliability() == StatementReliability.MEDIUM || statement.getReliability() == StatementReliability.LOW) {
					removeLinks(statement);
				}
			}
		}
		onSearch(null);
	}

	public void onViewEntrySelected(ActionEvent event) throws ManagerBeanException {
		for (BankStatement statement : getCheckedBankStatement()) {
			statement.setShowAccountEntry(true);
		}
	}

	@SuppressWarnings("unchecked")
	public void onViewEntryCurrentPage(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = ((List<ITransferObject>)getModel().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			BankStatement statement = (BankStatement)iterator.next();
			statement.setShowAccountEntry(true);
		}
	}

	@SuppressWarnings("unchecked")
	public void onViewEntryNone(ActionEvent event) throws ManagerBeanException {
		for (BankStatement statement : getCheckedBankStatement()) {
			statement.setShowAccountEntry(false);
		}

		Iterator<ITransferObject> iterator = ((List<ITransferObject>)getModel().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			BankStatement statement = (BankStatement)iterator.next();
			statement.setShowAccountEntry(false);
		}
	}

	public void onBreakdownSelected(ActionEvent event) throws ManagerBeanException {
		for (BankStatement statement : getCheckedBankStatement()) {
			statement.setShowBankStatementLink(true);
		}
	}

	@SuppressWarnings("unchecked")
	public void onBreakdownCurrentPage(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = ((List<ITransferObject>)getModel().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			BankStatement statement = (BankStatement)iterator.next();
			statement.setShowBankStatementLink(true);
		}
	}

	@SuppressWarnings("unchecked")
	public void onBreakdownNone(ActionEvent event) throws ManagerBeanException {
		for (BankStatement statement : getCheckedBankStatement()) {
			statement.setShowBankStatementLink(false);
		}

		Iterator<ITransferObject> iterator = ((List<ITransferObject>)getModel().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			BankStatement statement = (BankStatement)iterator.next();
			statement.setShowBankStatementLink(false);
		}
	}

	public void onCheckByConcept(ActionEvent event) throws ManagerBeanException {
		setCheckByAccount(false);
	}

	public void onCheckByAccount(ActionEvent event) throws ManagerBeanException {
		setCheckByAccount(true);
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

	public int getAvailableRegistryBanks() throws ManagerBeanException {
		String companyControllerName = ICompanyConstants.COLLECTIONS_CONTROLLER_NAME;
		CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(companyControllerName);
		return companyCollections.getCompanyBanks().size();
	}

	public void onChangeBank(ValueChangeEvent event) {
		if (!isNew()) {
			RegistryBank registryBank = (RegistryBank)event.getNewValue();
			try {
				searchBankStatements(registryBank);
			} catch (ManagerBeanException e) {
				addMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}
	}

	private void searchBankStatements(RegistryBank registryBank) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_REGISTRY_BANK_ID), registryBank.getId());
		setCriteria(criteria);
		onSearch(null);
	}

	public int getDescriptionLength() throws ManagerBeanException {
		int length = 0;
		if (getModel().isRowAvailable()) {
			BankStatement to = (BankStatement)getModel().getRowData();
			length = to.getDescription().length();
		}
		return length;
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
				importAeb43(obtainLotNumber());
			} else {
				importCsv(obtainLotNumber());
			}
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private int obtainLotNumber() throws ManagerBeanException {
		Projection projection = Projection.max(getManagerBean().getFieldName(IFinanceAlias.BANK_STATEMENT_LOT_NUMBER));
		Object value = getManagerBean().getUniqueResult(projection, null);
		if (value != null) {
			return ((Integer) value).intValue() + 1;
		}
		return 1;
	}

	private void importAeb43(int lotNumber) throws ManagerBeanException, IOException {
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
				bankStatement = importAeb43Data(line, lotNumber);
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
		String searchControllerName = IFinanceConstants.BANK_STATEMENT_SEARCH_LISTENER_NAME;
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

	private BankStatement importAeb43Data(String line, int lotNumber) throws ManagerBeanException {
		int conceptIdx = Integer.parseInt(line.substring(22, 24));
		conceptIdx = (conceptIdx > 90) ? (conceptIdx - 80) : conceptIdx;

		BankStatement bankStatement = new BankStatement();
		bankStatement.setRegistryBank(getRegistryBank());
		bankStatement.setLotNumber(lotNumber);
		bankStatement.setOperationDate(obtainDateAAMMDD(line.substring(10, 16)));
		bankStatement.setCommonConcept(StatementConcept.values()[conceptIdx]);
		bankStatement.setOwnConcept(line.substring(24,27));
		bankStatement.setPayment(line.substring(27, 28).equals("1"));
		bankStatement.setAmount(Double.parseDouble(line.substring(28, 42)) / 100);
		bankStatement.setDocument(Integer.parseInt(line.substring(42, 52)));
		bankStatement.setReference1(line.substring(52, 64));
		bankStatement.setReference2(line.substring(64, 80));
		bankStatement.setDescription(line.substring(52, 80));
		bankStatement.setReliability(StatementReliability.VERY_HIGH);
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

	private void importCsv(int lotNumber) throws ManagerBeanException, IOException {
		BankStatement bankStatement = null;
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(getAonFile().getFile())));
		String line = reader.readLine();
		while (line != null) {
			bankStatement = importCsvData(line, lotNumber, (bankStatement == null));
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

	private BankStatement importCsvData(String line, int lotNumber, boolean firstLine) throws ManagerBeanException {
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
			String searchControllerName = IFinanceConstants.BANK_STATEMENT_SEARCH_LISTENER_NAME;
			BankStatementSearchListener bankStatementSearch = (BankStatementSearchListener)AonUtil.getRegisteredBean(searchControllerName);
			bankStatementSearch.initData();
			bankStatementSearch.setFromDate(date);
		}

		BankStatement bankStatement = new BankStatement();
		bankStatement.setRegistryBank(getRegistryBank());
		bankStatement.setLotNumber(lotNumber);
		bankStatement.setOperationDate(date);
		bankStatement.setCommonConcept(StatementConcept.UNKNOWN);
		bankStatement.setOwnConcept(null);
		bankStatement.setPayment(payment);
		bankStatement.setAmount(amount);
		bankStatement.setDocument(0);
		bankStatement.setReference1(null);
		bankStatement.setReference2(null);
		bankStatement.setDescription((description.length() > 80) ? description.substring(0, 80) : description);
		bankStatement.setReliability(StatementReliability.VERY_HIGH);
		bankStatement.setStatus(StatementStatus.PENDING);
		return (BankStatement)getManagerBean().insert(bankStatement);
	}

	private void setCsvToDate(Date toDate) {
		String searchControllerName = IFinanceConstants.BANK_STATEMENT_SEARCH_LISTENER_NAME;
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


	public void onCheckSelected(ActionEvent event) throws ManagerBeanException {
		if (checkByAccount && (getAccount() == null || getAccount().getId() == null)) {
			addMessage("Cuenta Contable: Error de Validación: Valor es necesario.");
			throw new AbortProcessingException();
		} else if (!checkByAccount && (getBankConcept() == null || getBankConcept().getId() == null)) {
			addMessage("Concepto Bancario: Error de Validación: Valor es necesario.");
			throw new AbortProcessingException();
		}

		for (BankStatement statement : getCheckedBankStatement()) {
			if (statement.getStatus() == StatementStatus.PENDING) {
				if (checkByAccount) {
					getBankStatementLinkManager().addLink(statement, getAccount());
				} else {
					getBankStatementLinkManager().addLink(statement, getBankConcept());
				}
				getBankStatementLinkManager().checkBankStatement(statement);

				getErrors().remove(statement.getId());
			}
		}

		onSearch(null);
		clearCheckedBankStatement();
	}

	public void onCheckLinks(ActionEvent event) throws ManagerBeanException {
		//resetErrors();
		try {
			List<ITransferObject> bankStatementList = getManagerBean().getList(getCriteria());
			for (ITransferObject ito : bankStatementList) {
				BankStatement to = (BankStatement)ito;
				if (to.getStatus() != StatementStatus.PENDING) {
					if (hasFinanceLink(to.getCommonConcept())) {
						findFinance(to, false);
					} else if (hasFinanceReturnLink(to.getCommonConcept())) {
						findFinance(to, true);
					} else if (hasFinanceBatchLink(to.getCommonConcept())) {
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

	public void resetErrors() {
		setErrors(new HashMap<Integer, String>());
	}

	private boolean hasFinanceLink(StatementConcept concept) {
		return (concept == StatementConcept.UNKNOWN || concept == StatementConcept.WITHDRAWAL || concept == StatementConcept.PAYMENT ||
				 concept == StatementConcept.DEPOSIT || concept == StatementConcept.COLLECTION);
	}

	private boolean hasFinanceReturnLink(StatementConcept concept) {
		return (concept == StatementConcept.RETURNED);
	}

	private boolean hasFinanceBatchLink(StatementConcept concept) {
		return (concept == StatementConcept.COLLECTION_BATCH);
	}

	private void findFinance(BankStatement to, boolean returned) throws ManagerBeanException, ExpressionException {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(to.getOperationDate());
		calendar.add(Calendar.DATE, -7);
		Date fromDate = calendar.getTime();
		calendar.setTime(to.getOperationDate());
		calendar.add(Calendar.DATE, 7);
		Date toDate = calendar.getTime();

		double fromAmount = CommonUtil.round(to.getAmount() * 0.90);
		double toAmount = CommonUtil.round(to.getAmount() * 1.10);

		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		for (int key=1; key<=4; key++) {
			Criteria criteria = new Criteria();
			if (!returned) {
				criteria.addNotEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.BATCHED);
			} else {
				criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PAID);
			}
			switch (key) {
				case 1: {
					criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT), to.getAmount());
					criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), to.getOperationDate());
					break;
				}
				case 2: {
					criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT), to.getAmount());
					criteria.addBetweenExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), fromDate, to.getOperationDate());
					criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), false);
					break;
				}
				case 3: {
					criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT), to.getAmount());
					criteria.addBetweenExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), to.getOperationDate(), toDate);
					criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE));
					break;
				}
				case 4: {
					criteria.addBetweenExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT), fromAmount, toAmount);
					criteria.addBetweenExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), fromDate, toDate);
					criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT), false);
					criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE));
					break;
				}
			}

			List<ITransferObject> financeList = financeBean.getList(criteria);
			for (ITransferObject ito : financeList) {
				Finance finance = (Finance)ito;
				criteria = new Criteria();
				criteria.addEqualExpression(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_SOURCE), StatementLinkSource.FINANCE_TRACKING);
				//FINANCE TRACKING!!!!
				criteria.addEqualExpression(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_SOURCE_ID), finance.getId());
				if (statementLinkBean.getCount(criteria) == 0) {
					//Si el status es pending se cobra y se asocia
					//Si el status es returned se cobra y se asocia
					//Si el status es paid se mira a ver si esta contabilizado y en caso afirmativo NO se asocia, se saca un mensaje de error y 
					//listo. Si no esta contabilizado se asocia.
					//Si el status es settled, se borra la linea del tracking de saldado y se cobra.
					//Ver diferencias entre amount y totalAmount
					StatementReliability reliability = StatementReliability.LOW;
					if (key == 1 && financeList.size() == 1) {
						reliability = StatementReliability.VERY_HIGH;
					} else if (key < 4 && financeList.size() == 1) {
						reliability = StatementReliability.HIGH;
					}

					StatementLinkStatus status = StatementLinkStatus.PENDING;
					if (finance.getFinanceStatus() == FinanceStatus.RETURNED) {
						status = StatementLinkStatus.RETURNED;
					} else if (finance.getFinanceStatus() == FinanceStatus.PAID) {
						status = StatementLinkStatus.PAID;
					} else if (finance.getFinanceStatus() == FinanceStatus.SETTLED) {
						status = StatementLinkStatus.SETTLED;
					}

					BankStatementLink statementLink = new BankStatementLink();
					statementLink.setBankStatement(to);
					statementLink.setSource(StatementLinkSource.FINANCE_TRACKING);
					statementLink.setSourceId(finance.getId());
					statementLink.setAmount(finance.getTotalAmount());
					statementLink.setStatus(status);
					statementLinkBean.insert(statementLink);

					to.setReliability(reliability);
					to.setStatus(StatementStatus.CHECKED);
					getManagerBean().update(to);
					return;
				}
			}
		}
		/*
		if (count > 1) {
			getErrors().put(to.getId(), "Se ha encontrado más de 1 Vencimiento con ese Importe");
		} else {
			getErrors().put(to.getId(), "No se ha encontrado ningún Vencimiento con ese Importe");
		}*/
	}

	private void findFinanceBatch(BankStatement to) throws ManagerBeanException {
		IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_REGISTRY_BANK_ID), getRegistryBank().getId());
		criteria.addNotEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), FinanceBatchStatus.RECORDED);
		criteria.addLessThanOrEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE), to.getOperationDate());
		criteria.addOrder(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE));
		BankStatementLinx link = null;
		int count = 0;
		for (ITransferObject ito : fBatchBean.getList(criteria)) {
			FinanceBatch fBatch = (FinanceBatch)ito;
			if (fBatch.getFinanceBatchTotalAmount().doubleValue() == to.getAmount()) {
				link = new BankStatementLinx();
				link.setTo(fBatch);
				link.setAmount(to.getAmount());

				++count;
			}
		}

		if (count == 1) {
			List<BankStatementLinx> linkList = new LinkedList<BankStatementLinx>();
			linkList.add(link);
			//getLinks().put(to.getId(), linkList);
		} else if (count > 1) {
			getErrors().put(to.getId(), "Se ha encontrado más de 1 Remesa con ese Importe");
		} else {
			getErrors().put(to.getId(), "No se ha encontrado ninguna Remesa con ese Importe");
		}
	}

	public void onAddLinkShow(ActionEvent event) throws ManagerBeanException {
		BankStatement to = (BankStatement)getModel().getRowData();
		boolean payment = (to.getCommonConcept() != StatementConcept.RETURNED) ? to.isPayment() : !to.isPayment();
		Date fromDate = DateUtils.addDays(to.getOperationDate(), -7);
		Date toDate = DateUtils.addDays(to.getOperationDate(), 7);

		BankStatementLinkController statementLinkList = (BankStatementLinkController)FormUtil.getController(BANK_STATEMENT_LINK_CONTROLLER_NAME);
		statementLinkList.clearCheckedStatementLinks();
		statementLinkList.onEditSearch(null);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(statementLinkList.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), to.getId());
		criteria.addEqualExpression(statementLinkList.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_SOURCE), StatementLinkSource.FINANCE_TRACKING);
		statementLinkList.setCriteria(criteria);
		statementLinkList.onSearch(null);

		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
        financeList.onEditSearch(null);
		FinanceListSearchListener financeSearch = (FinanceListSearchListener)AonUtil.getRegisteredBean(FINANCE_LIST_SEARCH_LISTENER_NAME);
		if (to.getCommonConcept() != StatementConcept.RETURNED) {
			FinanceStatus[] financeStatuses = {FinanceStatus.PENDING, FinanceStatus.RETURNED, FinanceStatus.SETTLED};
			financeSearch.setFinanceStatuses(financeStatuses);
		} else {
			FinanceStatus[] financeStatuses = {FinanceStatus.PAID};
			financeSearch.setFinanceStatuses(financeStatuses);
		}
        criteria = new Criteria();
		criteria.addEqualExpression(financeList.getFieldName(IFinanceAlias.FINANCE_PAYMENT), new Boolean(payment));
		criteria.addGreaterThanOrEqualExpression(financeList.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), fromDate);
		criteria.addLessThanOrEqualExpression(financeList.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), toDate);
		criteria.setOrderByList(financeList.getOrderList());
		financeList.setCriteria(criteria);
		financeList.onSearch(null);

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		trackingList.onEditSearch(null);
		FinanceTrackingListSearchListener trackingSearch = (FinanceTrackingListSearchListener)AonUtil.getRegisteredBean(FINANCE_TRACKING_LIST_SEARCH_LISTENER_NAME);
		trackingSearch.setRegistryBank(getRegistryBank());
		if (to.getCommonConcept() != StatementConcept.RETURNED) {
			FinanceTrackingType[] financeTrackingTypes = {FinanceTrackingType.PAID};
			trackingSearch.setFinanceTrackingTypes(financeTrackingTypes);
		} else {
			FinanceTrackingType[] financeTrackingTypes = {FinanceTrackingType.RETURNED};
			trackingSearch.setFinanceTrackingTypes(financeTrackingTypes);
		}
		criteria = new Criteria();
		criteria.addEqualExpression(trackingList.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_PAYMENT), new Boolean(payment));
		criteria.addGreaterThanOrEqualExpression(trackingList.getFieldName(IFinanceAlias.FINANCE_TRACKING_TRACKING_DATE), fromDate);
		criteria.addLessThanOrEqualExpression(trackingList.getFieldName(IFinanceAlias.FINANCE_TRACKING_TRACKING_DATE), toDate);
		criteria.setOrderByList(trackingList.getOrderList());
		trackingList.setCriteria(criteria);
		trackingList.onSearch(null);

        getBankStatementLinkManager().setCurrentStatement(to);

        getErrors().remove(to.getId());

        /*
		boolean payment = (to.getCommonConcept() != StatementConcept.RETURNED) ? to.isPayment() : !to.isPayment();
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		if (to.getCommonConcept() != StatementConcept.RETURNED) {
			String statusAlias = financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS);
			Expression pendingExpr = ExpressionUtilities.getEqualExpression(statusAlias, FinanceStatus.PENDING);
			Expression returnedExpr = ExpressionUtilities.getEqualExpression(statusAlias, FinanceStatus.RETURNED);
			criteria.addOrExpression(ExpressionUtilities.getOrExpression(pendingExpr, returnedExpr));
		} else {
			criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PAID);
		}
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_PAYMENT), new Boolean(payment));
		criteria.addLessThanOrEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), to.getOperationDate());
		criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), (to.getCommonConcept() != StatementConcept.RETURNED));
		criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_CONCEPT), (to.getCommonConcept() != StatementConcept.RETURNED));
		getBankStatementLinkManager().setCurrentStatement(to);
		getBankStatementLinkManager().setFinanceListFiltered(financeBean.getList(criteria));
		getBankStatementLinkManager().setFinanceModel(null);
		getBankStatementLinkManager().clearCheckedFinance();

		List<ITransferObject> fBatchList = new LinkedList<ITransferObject>();
		if (to.getCommonConcept() == StatementConcept.COLLECTION_BATCH) {
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

		getBankStatementLinkManager().setSelectedTab((to.getCommonConcept() == StatementConcept.COLLECTION_BATCH) ? "fBatchLinkTab" : "financeLinkTab");
		*/
	}

	public void removeLinks(BankStatement statement) throws ManagerBeanException {
		for (ITransferObject ito : getBankStatementLinkList(statement)) {
			getBankStatementLinkManager().removeLink((BankStatementLink)ito);
		}
		getBankStatementLinkManager().unCheckBankStatement(statement);
	}

	public String getErrorMessage() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			BankStatement to = (BankStatement)getModel().getRowData();
			return errors.get(to.getId());
		}
		return null;
	}

	public List<ITransferObject> getBankStatementLinkList() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			BankStatement to = (BankStatement)getModel().getRowData();
			return getBankStatementLinkList(to);
		}
		return null;
	}

	private List<ITransferObject> getBankStatementLinkList(BankStatement statement) throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), statement.getId());
		criteria.addOrder(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_SOURCE));
		return statementLinkBean.getList(criteria);
	}

	public boolean isShowBankStatementLink() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			BankStatement to = (BankStatement)getModel().getRowData();
			if (to.isShowBankStatementLink()) {
				return true;
			} else if (getCheckedBankStatement().contains(to)) {
				for (BankStatement statement : getCheckedBankStatement()) {
					if (to.equals(statement)) {
						return statement.isShowBankStatementLink();
					}
				}
			}
		}
		return false;
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

	public void onRecordLinks(ActionEvent event) throws ManagerBeanException {
		for (BankStatement statement : getCheckedBankStatement()) {
			if (statement.getStatus() == StatementStatus.CHECKED) {
				boolean financeTrackingMode = false;
				boolean financeBatchMode = false;
				List<FinanceTracking> financeTrackingList = new LinkedList<FinanceTracking>();
				Map<Account, Double> accountMap = new HashMap<Account, Double>();
				double amountLinks = 0;

				getErrors().remove(statement.getId());
				for (ITransferObject ito : getBankStatementLinkList(statement)) {
					BankStatementLink statementLink = (BankStatementLink)ito;
					amountLinks += statementLink.getAmount();
					if (statementLink.getSource() == StatementLinkSource.FINANCE_TRACKING) {
						FinanceTracking tracking = (FinanceTracking)statementLink.getSourceTo();
						if (!tracking.isRecorded()) {
							financeTrackingMode = true;
							financeTrackingList.add(tracking);
						} else {
							String docNumber = tracking.getFinance().getDocumentNumber();
							getErrors().put(statement.getId(), "El Vencimiento " + docNumber + " ya esta Contabilizado.");
						}
					} else if (statementLink.getSource() == StatementLinkSource.FINANCE_BATCH) {
						// Lista de Finance Batch
						// Si el finance batch esta contabilizado --> error
						financeBatchMode = true;
					} else if (statementLink.getSource() == StatementLinkSource.BANK_CONCEPT) {
						IManagerBean conceptBean = BeanManager.getManagerBean(BankConceptAccount.class);
						String conceptAlias = conceptBean.getFieldName(IAccountBridgeAlias.BANK_CONCEPT_ACCOUNT_BANK_CONCEPT_ID);
						BankConcept concept = (BankConcept)statementLink.getSourceTo();
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(conceptAlias, concept.getId());
						Iterator<ITransferObject> iterator = conceptBean.getList(criteria).iterator();
						if (iterator.hasNext()) {
							BankConceptAccount conceptAccount = (BankConceptAccount)iterator.next();
							accountMap.put(conceptAccount.getAccount(), new Double(statementLink.getAmount()));
						} else {
							getErrors().put(statement.getId(), "El Concepto " + concept.getName() + " no tiene Cuenta Contable asociada.");
						}
					} else {
						accountMap.put((Account)statementLink.getSourceTo(), new Double(statementLink.getAmount()));
					}
				}

				if (errors.get(statement.getId()) == null) {
					if (statement.getAmount() != amountLinks) {
						getErrors().put(statement.getId(), "El Importe de la línea del Extracto no cuadra con la suma de los Detalles del mismo.");
					} else {
						FinanceRecordingTo recordingTo = new FinanceRecordingTo();
						recordingTo.setDate(statement.getOperationDate());
						recordingTo.setPaymentAccount(getWriter().obtainPaymentAccount(statement.getRegistryBank(), null));
						recordingTo.setBalancingConcept(StringUtils.abbreviate(statement.getDescription(), 32));
						recordingTo.setSecurityLevel(SecurityLevel.OFFICIAL);
						recordingTo.setAccountMap(accountMap);

						AccountEntry entry = null;
						if (financeTrackingMode) {
							if (statement.getCommonConcept() != StatementConcept.RETURNED) {
								recordingTo.setType((statement.isPayment()) ? AccountEntryType.PAYMENT : AccountEntryType.COLLECTION);
							} else {
								recordingTo.setType((statement.isPayment()) ? AccountEntryType.RETURNED_COLLECTION : AccountEntryType.RETURNED_PAYMENT);
							}
							recordingTo.setFinanceTrackingList(financeTrackingList);
							entry = getWriter().recordFinanceTrackings(recordingTo, entry);
						} else if (financeBatchMode) {

						} else {
							recordingTo.setType(AccountEntryType.MANUAL);
							entry = getWriter().recordBankStatementLinks(recordingTo, statement.isPayment(), statement.getAmount());
						}

						if (entry != null) {
							getWriter().insertAccountEntryBankStatement(entry, statement);

							statement.setStatus(StatementStatus.RECORDED);
							statement.setShowBankStatementLink(false);
							getManagerBean().update(statement);
						}
					}
				}
			} else if (statement.getStatus() == StatementStatus.PENDING) {
				getErrors().put(statement.getId(), "La línea del Extracto esta Pendiente. No se puede Contabilizar.");
			} else if (statement.getStatus() == StatementStatus.RECORDED) {
				getErrors().put(statement.getId(), "La línea del Extracto ya esta Contabilizada.");
			}
		}

		onSearch(null);
		clearCheckedBankStatement();
	}

	public AccountEntry getAccountEntry() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			BankStatement to = (BankStatement)getModel().getRowData();
			IManagerBean accEntryStatementBean = BeanManager.getManagerBean(AccountEntryBankStatement.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accEntryStatementBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_BANK_STATEMENT_BANK_STATEMENT_ID), to.getId());
			Iterator<ITransferObject> iterator = accEntryStatementBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				AccountEntryBankStatement accEntryStatement = (AccountEntryBankStatement)iterator.next();
				return accEntryStatement.getAccountEntry();
			}
		}
		return null;
	}

	public List<ITransferObject> getAccountEntryDetails() throws ManagerBeanException {
		AccountEntry accountEntry = getAccountEntry();
		return (accountEntry != null) ? getAccountEntryDetails(accountEntry) : null;
	}

	private List<ITransferObject> getAccountEntryDetails(AccountEntry accEntry) throws ManagerBeanException {
		IManagerBean accEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accEntry.getId());
		return accEntryDetailBean.getList(criteria);
	}

	public boolean isShowAccountEntry() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			BankStatement to = (BankStatement)getModel().getRowData();
			if (to.isShowAccountEntry()) {
				return true;
			} else if (getCheckedBankStatement().contains(to)) {
				for (BankStatement statement : getCheckedBankStatement()) {
					if (to.equals(statement)) {
						return statement.isShowAccountEntry();
					}
				}
			}
		}
		return false;
	}

	public void onShowAccountEntry(ActionEvent event) {
		try {
			BankStatement to = (BankStatement)getModel().getRowData();
			to.setShowAccountEntry(true);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onHideAccountEntry(ActionEvent event) {
		try {
			BankStatement to = (BankStatement)getModel().getRowData();
			to.setShowAccountEntry(false);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	/**
	 * CHECK LIST CONTROL 
	 */

	public void bankStatementRowSelected(ValueChangeEvent event) throws ManagerBeanException  {
		if (event.getNewValue() != null) {
			selectBankStatementRow(((Boolean)event.getNewValue()).booleanValue());
		}
	}

	private void selectBankStatementRow(boolean rowChecked) throws ManagerBeanException  {
		if (getModel().isRowAvailable()) {
			BankStatement bankStatement = (BankStatement)getModel().getRowData();
			setBankStatementRowChecked(bankStatement, rowChecked);
		}
	}

	public boolean getBankStatementRowChecked() throws ManagerBeanException  {
		BankStatement bankStatement = (BankStatement)getModel().getRowData();
		return bankStatementChecks.contains(bankStatement);
	}
	
	public void setBankStatementRowChecked(boolean rowChecked) {
	}

	public void setBankStatementRowChecked(BankStatement bankStatement, boolean rowChecked) {
		if (rowChecked) {
			if (!bankStatementChecks.contains(bankStatement)) {
				bankStatementChecks.add(bankStatement);
			}
		} else {
			if (bankStatementChecks.contains(bankStatement)) {
				bankStatementChecks.remove(bankStatement);
			}
		}
	}
	
	public ArrayList<BankStatement> getCheckedBankStatement() {
		return bankStatementChecks;
	}
	
	public void clearCheckedBankStatement() {
		bankStatementChecks = new ArrayList<BankStatement>();
	}
	
}
