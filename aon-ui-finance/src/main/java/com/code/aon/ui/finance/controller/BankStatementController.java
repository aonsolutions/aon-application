package com.code.aon.ui.finance.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
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
import com.code.aon.account.bridge.AccountEntryFinanceBatch;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
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
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.event.BankStatementSearchListener;
import com.code.aon.ui.finance.event.FinanceListSearchListener;
import com.code.aon.ui.finance.event.FinanceTrackingListSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class BankStatementController extends BasicController implements IFinanceConstants {

	private RegistryBank registryBank;
	private Date operationDate;
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

	public void onBreakdownSelected(ActionEvent event) throws ManagerBeanException {
		for (BankStatement statement : getCheckedBankStatement()) {
			statement.setShowBankStatementLink(statement.isChecked());
			statement.setShowAccountEntry(AonUtil.getRoleManager().isAccountingOperator() && statement.isRecorded());
		}
	}

	@SuppressWarnings("unchecked")
	public void onBreakdownCurrentPage(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = ((List<ITransferObject>)getModel().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			BankStatement statement = (BankStatement)iterator.next();
			statement.setShowBankStatementLink(statement.isChecked());
			statement.setShowAccountEntry(AonUtil.getRoleManager().isAccountingOperator() && statement.isRecorded());
		}
	}

	@SuppressWarnings("unchecked")
	public void onBreakdownNone(ActionEvent event) throws ManagerBeanException {
		for (BankStatement statement : getCheckedBankStatement()) {
			statement.setShowBankStatementLink(false);
			statement.setShowAccountEntry(false);
		}

		Iterator<ITransferObject> iterator = ((List<ITransferObject>)getModel().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			BankStatement statement = (BankStatement)iterator.next();
			statement.setShowBankStatementLink(false);
			statement.setShowAccountEntry(false);
		}
	}

	public void onRemoveSelected(ActionEvent event) throws ManagerBeanException {
		for (BankStatement statement : getCheckedBankStatement()) {
			if (statement.getStatus() == StatementStatus.PENDING) {
				getManagerBean().remove(statement);
			} else {
				getErrors().put(statement.getId(), "No se puede borrar la línea del Extracto ya que no esta pendiente.");
			}
		}
		onSearch(null);
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

	public void onCheckSelectedByAccount(ActionEvent event) throws ManagerBeanException {
		if (getAccount() == null || getAccount().getId() == null) {
			addMessage("Cuenta Contable: Error de Validación: Valor es necesario.");
			throw new AbortProcessingException();
		}
		if (!getAccount().isEntryEnabled()) {
			addMessage("La Cuenta Contable " + getAccount().getId() + " no permite apuntes.");
			throw new AbortProcessingException();
		}

		for (BankStatement statement : getCheckedBankStatement()) {
			if (statement.isPending()) {
				getBankStatementLinkManager().addLink(statement, getAccount(), statement.getAmount());
				getBankStatementLinkManager().checkBankStatement(statement, StatementReliability.VERY_HIGH);
				getErrors().remove(statement.getId());
			}
		}

		onSearch(null);
		clearCheckedBankStatement();
	}

	public void onCheckSelectedByConcept(ActionEvent event) throws ManagerBeanException {
		if (getBankConcept() == null || getBankConcept().getId() == null) {
			addMessage("Concepto Bancario: Error de Validación: Valor es necesario.");
			throw new AbortProcessingException();
		}

		for (BankStatement statement : getCheckedBankStatement()) {
			if (statement.isPending()) {
				getBankStatementLinkManager().addLink(statement, getBankConcept(), statement.getAmount());
				getBankStatementLinkManager().checkBankStatement(statement, StatementReliability.VERY_HIGH);
				getErrors().remove(statement.getId());
			}
		}

		onSearch(null);
		clearCheckedBankStatement();
	}

	public void onCheckLinks(ActionEvent event) throws ManagerBeanException {
		try {
			List<ITransferObject> bankStatementList = getManagerBean().getList(getCriteria());
			for (ITransferObject ito : bankStatementList) {
				BankStatement to = (BankStatement)ito;
				if (to.isPending()) {
					getErrors().put(to.getId(), "No se ha encontrado ninguna coincidencia para Puntear la línea del Extracto.");
					if (hasFinanceLink(to.getCommonConcept())) {
						findFinance(to, false);
					} else if (hasFinanceReturnLink(to.getCommonConcept())) {
						findFinance(to, true);
					} else if (hasFinanceBatchLink(to.getCommonConcept())) {
						findFinanceBatch(to);
					}

					if (to.isPending()) {
						findBankStatement(to);
					}
				}
			}

			onSearch(null);
			clearCheckedBankStatement();
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
		boolean payment = (to.getCommonConcept() != StatementConcept.RETURNED) ? to.isPayment() : !to.isPayment();
		Date fromDate = DateUtils.addDays(to.getOperationDate(), -7);
		Date toDate = DateUtils.addDays(to.getOperationDate(), 7);
		double fromAmount = CommonUtil.round(to.getAmount() * 0.9);
		double toAmount = CommonUtil.round(to.getAmount() * 1.1);

		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		for (int key=1; key<=3 && to.isPending(); key++) {
			Criteria criteriaFin = new Criteria();
			criteriaFin.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_PAYMENT), new Boolean(payment));

			Criteria criteriaTrk = new Criteria();
			criteriaTrk.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_PAYMENT), new Boolean(payment));
			criteriaTrk.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_REGISTRY_BANK_ID), to.getRegistryBank().getId());
			criteriaTrk.addNullExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_BANK_STATEMENT_LINK));
			criteriaTrk.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_RECORDED), new Boolean(false));
			if (!returned) {
				criteriaFin.addNotEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PAID);
				criteriaFin.addNotEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.BATCHED);

				criteriaTrk.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TYPE), FinanceTrackingType.PAID);
			} else {
				criteriaFin.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PAID);

				criteriaTrk.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TYPE), FinanceTrackingType.RETURNED);
			}
			switch (key) {
				case 1: {
					criteriaFin.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT), to.getAmount());
					criteriaFin.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), to.getOperationDate());

					criteriaTrk.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_AMOUNT), to.getAmount());
					criteriaTrk.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TRACKING_DATE), to.getOperationDate());
					break;
				}
				case 2: {
					criteriaFin.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT), to.getAmount());
					criteriaFin.addBetweenExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), fromDate, toDate);
					criteriaFin.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE));

					criteriaTrk.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_AMOUNT), to.getAmount());
					criteriaTrk.addBetweenExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TRACKING_DATE), fromDate, toDate);
					criteriaTrk.addOrder(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TRACKING_DATE));
					break;
				}
				case 3: {
					criteriaFin.addBetweenExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT), fromAmount, toAmount);
					criteriaFin.addBetweenExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), fromDate, toDate);
					criteriaFin.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT));
					criteriaFin.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE));

					criteriaTrk.addBetweenExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_AMOUNT), fromAmount, toAmount);
					criteriaTrk.addBetweenExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TRACKING_DATE), fromDate, toDate);
					criteriaTrk.addOrder(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_AMOUNT));
					criteriaTrk.addOrder(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TRACKING_DATE));
					break;
				}
			}
			//SCOPE!!

			ITransferObject linkedTo = null;
			List<ITransferObject> financeList = financeBean.getList(criteriaFin);
			List<ITransferObject> trackingList = trackingBean.getList(criteriaTrk);
			for (ITransferObject ito : financeList) {
				Finance finance = (Finance)ito;
				if (linkedTo == null || isMoreAccurateLink(to, linkedTo, finance.getTotalAmount(), finance.getDueDate())) {
					linkedTo = finance;
				}
			}
			for (ITransferObject ito : trackingList) {
				FinanceTracking tracking = (FinanceTracking)ito;
				if (linkedTo == null || isMoreAccurateLink(to, linkedTo, tracking.getAmount(), tracking.getTrackingDate())) {
					linkedTo = tracking;
				}
			}

			if (linkedTo != null) {
				if (linkedTo instanceof Finance) {
					getBankStatementLinkManager().addLink(to, (Finance)linkedTo);
				} else {
					getBankStatementLinkManager().addLink(to, (FinanceTracking)linkedTo);
				}

				StatementReliability reliability = StatementReliability.LOW;
				if ((financeList.size() + trackingList.size()) == 1) {
					if (key == 1) reliability = StatementReliability.VERY_HIGH;
					else if (key == 2) reliability = StatementReliability.HIGH;
					else reliability = StatementReliability.MEDIUM;
				}
				getBankStatementLinkManager().checkBankStatement(to, reliability);
				getErrors().remove(to.getId());
			}
		}
	}

	private void findFinanceBatch(BankStatement to) throws ManagerBeanException {
		Date fromDate = DateUtils.addDays(to.getOperationDate(), -7);
		Date toDate = DateUtils.addDays(to.getOperationDate(), 7);

		IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
		for (int key=1; key<=2 && to.isPending(); key++) {
			Criteria criteria = new Criteria();
			criteria.addNullExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_BANK_STATEMENT_LINK));
			criteria.addEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_REGISTRY_BANK_ID), to.getRegistryBank().getId());
			criteria.addNotEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), FinanceBatchStatus.RECORDED);
			criteria.addEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_PAYMENT), new Boolean(to.isPayment()));
			criteria.addBetweenExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE), fromDate, toDate);
			criteria.addOrder(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE));
			switch (key) {
				case 1: {
					criteria.addEqualExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE), to.getOperationDate());
					break;
				}
				case 2: {
					criteria.addBetweenExpression(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE), fromDate, toDate);
					criteria.addOrder(fBatchBean.getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE));
					break;
				}
			}
			//SCOPE!!

			ITransferObject linkedTo = null;
			int matchesAmount = 0;
			List<ITransferObject> fBatchList = fBatchBean.getList(criteria);
			for (ITransferObject ito : fBatchList) {
				FinanceBatch fBatch = (FinanceBatch)ito;
				double fBatchAmount = fBatch.getFinanceBatchTotalAmount();
				if (to.getAmount() == fBatchAmount) {
					++matchesAmount;
				}
				if (key == 2 || to.getAmount() == fBatchAmount) {
					if (linkedTo == null || isMoreAccurateLink(to, linkedTo, fBatchAmount, fBatch.getIssueDate())) {
						linkedTo = fBatch;
					}
				}
			}

			if (linkedTo != null) {
				FinanceBatch fBatch = (FinanceBatch)linkedTo;
				getBankStatementLinkManager().addLink(to, fBatch);

				StatementReliability reliability = StatementReliability.LOW;
				if (matchesAmount == 1) {
					if (key == 1) reliability = StatementReliability.VERY_HIGH;
					else if (key == 2) reliability = StatementReliability.HIGH;
				} else if (fBatchList.size() == 1) {
					reliability = StatementReliability.MEDIUM;
				}
				getBankStatementLinkManager().checkBankStatement(to, reliability);
				getErrors().remove(to.getId());
			}
		}
	}

	private void findBankStatement(BankStatement to) throws ManagerBeanException {
		for (int key=1; key<=5 && to.isPending(); key++) {
			Criteria criteria = new Criteria();
			criteria.addNotEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_STATUS), StatementStatus.PENDING);
			criteria.addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_REGISTRY_BANK_ID), to.getRegistryBank().getId());
			criteria.addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_PAYMENT), new Boolean(to.isPayment()));
			criteria.addOrder(getFieldName(IFinanceAlias.BANK_STATEMENT_OPERATION_DATE), false);
			switch (key) {
				case 1: {
					criteria.addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_AMOUNT), to.getAmount());
					criteria.addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_DESCRIPTION), to.getDescription());
					break;
				}
				case 2: {
					criteria.addNotEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_AMOUNT), to.getAmount());
					criteria.addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_DESCRIPTION), to.getDescription());
					break;
				}
				case 3: {
					criteria.addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_AMOUNT), to.getAmount());
					criteria.addNotEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_DESCRIPTION), to.getDescription());
					criteria.addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_COMMON_CONCEPT), to.getCommonConcept());
					break;
				}
				case 4: {
					criteria.addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_AMOUNT), to.getAmount());
					criteria.addNotEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_DESCRIPTION), to.getDescription());
					criteria.addNotEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_COMMON_CONCEPT), to.getCommonConcept());
					break;
				}
				case 5: {
					String likeStr = StringUtils.substring(to.getDescription(), 0, 15) + "%";
					criteria.addExpression(ExpressionUtilities.getLikeExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_DESCRIPTION), likeStr));
					break;
				}
			}

			List<ITransferObject> statementList = getManagerBean().getList(criteria);
			for (ITransferObject ito : statementList) {
				BankStatement statement = (BankStatement)ito;

				IManagerBean linkBean = BeanManager.getManagerBean(BankStatementLink.class);
				criteria = new Criteria();
				criteria.addEqualExpression(linkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), statement.getId());
				criteria.addNotEqualExpression(linkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_SOURCE), StatementLinkSource.BANK_CONCEPT);
				criteria.addNotEqualExpression(linkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_SOURCE), StatementLinkSource.ACCOUNT);
				if (linkBean.getCount(criteria) == 0) {
					criteria = new Criteria();
					criteria.addEqualExpression(linkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), statement.getId());
					if (linkBean.getCount(criteria) > 0) {
						List<ITransferObject> linkList = linkBean.getList(criteria);
						if (key == 1 || key == 3 || key == 4 || linkList.size() == 1) {
							for (ITransferObject itto : linkList) {
								BankStatementLink link = (BankStatementLink)itto;

								BankStatementLink newLink = new BankStatementLink();
								newLink.setBankStatement(to);
								newLink.setSource(link.getSource());
								newLink.setSourceId(link.getSourceId());
								newLink.setSourceDate(to.getOperationDate());
								newLink.setAmount((linkList.size() == 1) ? to.getAmount() : link.getAmount());
								newLink.setStatus(StatementLinkStatus.PENDING);
								linkBean.insert(newLink);
							}

							StatementReliability reliability = StatementReliability.LOW;
							if (key == 1) reliability = StatementReliability.VERY_HIGH;
							else if (key == 2) reliability = StatementReliability.HIGH;
							else if (key == 3) reliability = StatementReliability.MEDIUM;
							getBankStatementLinkManager().checkBankStatement(to, reliability);
							getErrors().remove(to.getId());

							break;
						}
					}
				}
			}

		}
	}

	private boolean isMoreAccurateLink(BankStatement statement, ITransferObject linkedTo, double amount, Date date) {
		if (linkedTo instanceof Finance) {
			Finance linkedFinance = (Finance)linkedTo;
			long linkedDaysBetween = CommonUtil.getDaysBetweenDates(statement.getOperationDate(), linkedFinance.getDueDate(), false);
			long daysBetween = CommonUtil.getDaysBetweenDates(statement.getOperationDate(), date, false);
			return isMoreAccurateLink(statement, linkedFinance.getTotalAmount(), linkedDaysBetween, amount, daysBetween);
		} else if (linkedTo instanceof FinanceTracking) {
			FinanceTracking linkedTracking = (FinanceTracking)linkedTo;
			long linkedDaysBetween = CommonUtil.getDaysBetweenDates(statement.getOperationDate(), linkedTracking.getTrackingDate(), false);
			long daysBetween = CommonUtil.getDaysBetweenDates(statement.getOperationDate(), date, false);
			return isMoreAccurateLink(statement, linkedTracking.getAmount(), linkedDaysBetween, amount, daysBetween);
		} else if (linkedTo instanceof FinanceBatch) {
			FinanceBatch linkedBatch = (FinanceBatch)linkedTo;
			if ((CommonUtil.round(statement.getAmount() * 0.9) > amount) || ((CommonUtil.round(statement.getAmount() * 1.1) < amount))) {
				return false;
			}
			long linkedDaysBetween = CommonUtil.getDaysBetweenDates(statement.getOperationDate(), linkedBatch.getIssueDate(), false);
			long daysBetween = CommonUtil.getDaysBetweenDates(statement.getOperationDate(), date, false);
			return isMoreAccurateLink(statement, linkedBatch.getFinanceBatchTotalAmount(), linkedDaysBetween, amount, daysBetween);
		} 
		return false;
	}

	private boolean isMoreAccurateLink(BankStatement statement, double linkedAmount, long linkedDaysBetween, double amount, long daysBetween) {
		if (Math.abs(statement.getAmount() - amount) == Math.abs(statement.getAmount() - linkedAmount)) {
			return (Math.abs(daysBetween) < Math.abs(linkedDaysBetween));
		}
		return (Math.abs(statement.getAmount() - amount) < Math.abs(statement.getAmount() - linkedAmount));
	}

	public void onAddLinkShow(ActionEvent event) throws ManagerBeanException {
		BankStatement to = (BankStatement)getModel().getRowData();
		boolean payment = (to.getCommonConcept() != StatementConcept.RETURNED) ? to.isPayment() : !to.isPayment();
		Date fromDate = DateUtils.addDays(to.getOperationDate(), -7);
		Date toDate = DateUtils.addDays(to.getOperationDate(), 7);
		Double fromAmount = new Double(CommonUtil.round(to.getAmount() * 0.9));
		Double toAmount = new Double(CommonUtil.round(to.getAmount() * 1.1));

		BankStatementLinkController statementLinkList = (BankStatementLinkController)FormUtil.getController(BANK_STATEMENT_LINK_CONTROLLER_NAME);
		statementLinkList.clearCheckedStatementLinks();
		statementLinkList.onEditSearch(null);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(statementLinkList.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), to.getId());
		statementLinkList.setCriteria(criteria);
		statementLinkList.onSearch(null);

		if (to.getCommonConcept() != StatementConcept.COLLECTION_BATCH) {
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
			criteria.addBetweenExpression(financeList.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), fromDate, toDate);
			criteria.addBetweenExpression(financeList.getFieldName(IFinanceAlias.FINANCE_AMOUNT), fromAmount, toAmount);
			criteria.setOrderByList(financeList.getOrderList());
			financeList.setCriteria(criteria);
			financeList.onSearch(null);

			FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
			trackingList.onEditSearch(null);
			FinanceTrackingListSearchListener trackingSearch = (FinanceTrackingListSearchListener)AonUtil.getRegisteredBean(FINANCE_TRACKING_LIST_SEARCH_LISTENER_NAME);
			trackingSearch.setRegistryBank(to.getRegistryBank());
			if (to.getCommonConcept() != StatementConcept.RETURNED) {
				FinanceTrackingType[] financeTrackingTypes = {FinanceTrackingType.PAID};
				trackingSearch.setFinanceTrackingTypes(financeTrackingTypes);
			} else {
				FinanceTrackingType[] financeTrackingTypes = {FinanceTrackingType.RETURNED};
				trackingSearch.setFinanceTrackingTypes(financeTrackingTypes);
			}
			criteria = new Criteria();
			criteria.addEqualExpression(trackingList.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_PAYMENT), new Boolean(payment));
			criteria.addBetweenExpression(trackingList.getFieldName(IFinanceAlias.FINANCE_TRACKING_TRACKING_DATE), fromDate, toDate);
			criteria.addBetweenExpression(trackingList.getFieldName(IFinanceAlias.FINANCE_TRACKING_AMOUNT), fromAmount, toAmount);
			criteria.setOrderByList(trackingList.getOrderList());
			trackingList.setCriteria(criteria);
			trackingList.onSearch(null);
		} else {
			FBatchListController batchList = (FBatchListController)FormUtil.getController(FINANCE_BATCH_LIST_CONTROLLER_NAME);
			batchList.onEditSearch(null);
			criteria = new Criteria();
			criteria.addNullExpression(batchList.getFieldName(IFinanceAlias.FINANCE_BATCH_BANK_STATEMENT_LINK));
			criteria.addEqualExpression(batchList.getFieldName(IFinanceAlias.FINANCE_BATCH_REGISTRY_BANK_ID), to.getRegistryBank().getId());
			criteria.addNotEqualExpression(batchList.getFieldName(IFinanceAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), FinanceBatchStatus.RECORDED);
			criteria.addEqualExpression(batchList.getFieldName(IFinanceAlias.FINANCE_BATCH_PAYMENT), new Boolean(payment));
			criteria.addBetweenExpression(batchList.getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE), fromDate, toDate);
			criteria.setOrderByList(batchList.getOrderList());
			batchList.setCriteria(criteria);
			batchList.onSearch(null);
		}

		Account bankAccount = getWriter().obtainPaymentAccount(to.getRegistryBank(), null);
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		criteria = new Criteria();
		criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), bankAccount.getId());
		criteria.addBetweenExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), fromDate, toDate);
		if ((to.isPayment() && to.getAmount() < 0) || (!to.isPayment() && to.getAmount() >= 0)) {
			criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_DEBIT), to.getAmount());
		} else {
			criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_CREDIT), to.getAmount());
		}
		if (!AonUtil.getRoleManager().isConfidentiality()) {
			criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		}
		getBankStatementLinkManager().setEntryDetailList(entryDetailBean.getList(criteria));
		getBankStatementLinkManager().setEntryDetailModel(null);

        getBankStatementLinkManager().setCurrentStatement(to);
        getBankStatementLinkManager().setComments(to.getComments());
        if (statementLinkList.getRowCount() > 0) {
        	getBankStatementLinkManager().setStatementLinkTab();
        }
        getBankStatementLinkManager().setAmountPending();
        getErrors().remove(to.getId());
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
			to.setShowBankStatementLink(to.isChecked());
			to.setShowAccountEntry(AonUtil.getRoleManager().isAccountingOperator() && to.isRecorded());
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onHideBankStatementLink(ActionEvent event) {
		try {
			BankStatement to = (BankStatement)getModel().getRowData();
			to.setShowBankStatementLink(false);
			to.setShowAccountEntry(false);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public void onRecordStatement(ActionEvent event) throws ManagerBeanException {
		for (BankStatement statement : getCheckedBankStatement()) {
			if (statement.isChecked()) {
				boolean financeTrackingMode = false;
				boolean financeBatchMode = false;
				List<FinanceTracking> financeTrackingList = new LinkedList<FinanceTracking>();
				List<FinanceBatch> financeBatchList = new LinkedList<FinanceBatch>();
				Map<Account, Double> accountMap = new HashMap<Account, Double>();
				double linksAmount = 0;

				getErrors().remove(statement.getId());
				for (ITransferObject ito : getBankStatementLinkList(statement)) {
					BankStatementLink statementLink = (BankStatementLink)ito;
					if (statement.isReturned() && statementLink.isFinanceTracking()) {
						linksAmount += statementLink.getAmount() * (statementLink.isPayment() ? 1 : (-1));
					} else {
						linksAmount += statementLink.getAmount() * (statementLink.isPayment() ? (-1) : 1);
					}
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
						FinanceBatch batch = (FinanceBatch)statementLink.getSourceTo();
						if (batch.getFinanceBatchStatus() != FinanceBatchStatus.RECORDED) {
							financeBatchMode = true;
							financeBatchList.add(batch);
						} else {
							getErrors().put(statement.getId(), "La Remesa " + batch.getId() + " ya esta Contabilizada.");
						}
					} else if (statementLink.getSource() == StatementLinkSource.BANK_CONCEPT) {
						IManagerBean conceptAccountBean = BeanManager.getManagerBean(BankConceptAccount.class);
						String conceptAlias = conceptAccountBean.getFieldName(IAccountBridgeAlias.BANK_CONCEPT_ACCOUNT_BANK_CONCEPT_ID);
						BankConcept concept = (BankConcept)statementLink.getSourceTo();
						Criteria criteria = new Criteria();
						criteria.addEqualExpression(conceptAlias, concept.getId());
						Iterator<ITransferObject> iterator = conceptAccountBean.getList(criteria).iterator();
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
					double statementAmount = (statement.isPayment()) ? (0 - statement.getAmount()) : statement.getAmount();
					if (statementAmount != CommonUtil.round(linksAmount)) {
						getErrors().put(statement.getId(), "El Importe de la línea del Extracto no cuadra con la suma de los Detalles del mismo.");
					} else {
						FinanceRecordingTo recordingTo = new FinanceRecordingTo();
						recordingTo.setType((statement.isPayment()) ? AccountEntryType.PAYMENT : AccountEntryType.COLLECTION);
						recordingTo.setDate(statement.getOperationDate());
						recordingTo.setPaymentAccount(getWriter().obtainPaymentAccount(statement.getRegistryBank(), null));
						recordingTo.setBalancingConcept(StringUtils.abbreviate(statement.getDescription(), 32));
						recordingTo.setSecurityLevel(SecurityLevel.OFFICIAL);
						recordingTo.setComments(statement.getComments());
						recordingTo.setAccountMap(accountMap);

						AccountEntry entry = null;
						if (financeTrackingMode) {
							if (statement.getCommonConcept() == StatementConcept.RETURNED) {
								recordingTo.setType((statement.isPayment()) ? AccountEntryType.RETURNED_COLLECTION : AccountEntryType.RETURNED_PAYMENT);
							}
							recordingTo.setFinanceTrackingList(financeTrackingList);
							entry = getWriter().recordFinanceTrackings(recordingTo, entry);
						} else if (financeBatchMode) {
							if (financeBatchList.size() > 1) {
								getErrors().put(statement.getId(), "No puede haber más de una Remesa en la misma línea del Extracto.");
							} else {
								FinanceBatch fBatch = financeBatchList.get(0);
								recordingTo.setBalancingConcept(fBatch.getDescription());
								recordingTo.setFBatchDetailList(fBatch.getDetailList());
								entry = getWriter().recordFBatch(recordingTo, fBatch, entry);
							}
						} else {
							recordingTo.setType(AccountEntryType.MANUAL);
							entry = getWriter().recordBankStatementLinks(recordingTo, statement.isPayment(), statement.getAmount());
						}

						if (entry != null) {
							recordBankStatement(entry, statement);
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

	public void recordBankStatement(AccountEntry entry, BankStatement statement) throws ManagerBeanException {
		getWriter().insertAccountEntryBankStatement(entry, statement);

		statement.setStatus(StatementStatus.RECORDED);
		statement.setShowBankStatementLink(false);
		getManagerBean().update(statement);
	}

	public void onUnrecordStatement(ActionEvent event) throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		for (BankStatement statement : getCheckedBankStatement()) {
			if (statement.isRecorded()) {
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), statement.getId());
				if (statementLinkBean.getCount(criteria) > 0) {
					IManagerBean accEntryStatementBean = BeanManager.getManagerBean(AccountEntryBankStatement.class);
					criteria = new Criteria();
					String alias = accEntryStatementBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_BANK_STATEMENT_BANK_STATEMENT_ID);
					criteria.addEqualExpression(alias, statement.getId());
					for (ITransferObject ito : accEntryStatementBean.getList(criteria)) {
						AccountEntryBankStatement accEntryStatement = (AccountEntryBankStatement)ito;
						AccountEntry accEntry = accEntryStatement.getAccountEntry();

						IManagerBean accEntryFBatchBean = BeanManager.getManagerBean(AccountEntryFinanceBatch.class);
						criteria = new Criteria();
						alias = accEntryFBatchBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_BATCH_ACCOUNT_ENTRY_ID);
						criteria.addEqualExpression(alias, accEntry.getId());
						for (ITransferObject fto : accEntryFBatchBean.getList(criteria)) {
							AccountEntryFinanceBatch accEntryFBatch = (AccountEntryFinanceBatch)fto;
							FinanceBatch fBatch = accEntryFBatch.getFinanceBatch();
					        if (getWriter().canRemoveAccountEntryFinanceBatch(fBatch)) {
								getWriter().removeAccountEntryFinanceBatch(fBatch, false);
					        } else {
					            AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_BATCH_UNRECORD_ERROR);
					            throw new AbortProcessingException();
					        }
						}

				        IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
						IManagerBean accEntryTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
						criteria = new Criteria();
						alias = accEntryTrackingBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY_ID);
						criteria.addEqualExpression(alias, accEntry.getId());
						for (ITransferObject fto : accEntryTrackingBean.getList(criteria)) {
							AccountEntryFinanceTracking accEntryTracking = (AccountEntryFinanceTracking)fto;
							FinanceTracking tracking = accEntryTracking.getFinanceTracking();
							getWriter().removeAccountEntryFinanceTracking(tracking, false);

							tracking.setDescription(AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_PENDING));
							tracking.setRecorded(false);
					        trackingBean.update(tracking);
						}
					}

					unrecordBankStatement(statement, true);
				} else {
					unrecordBankStatement(statement, false);
				}
			}
		}

		onSearch(null);
		clearCheckedBankStatement();
	}

	public void unrecordBankStatement(BankStatement statement, boolean hasLinks) throws ManagerBeanException {
		getWriter().removeAccountEntryBankStatement(statement, hasLinks);

		statement.setStatus((hasLinks) ? StatementStatus.CHECKED : StatementStatus.PENDING);
		statement.setShowBankStatementLink(false);
		getManagerBean().update(statement);
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

	public void onLoadBankStatement(ActionEvent event, BankStatement statement, String backAction) throws ManagerBeanException {
		BankStatementSearchListener statementSearch = (BankStatementSearchListener)AonUtil.getRegisteredBean(BANK_STATEMENT_SEARCH_LISTENER_NAME);

		onEditSearch(event);
		getCriteria().addEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_ID), statement.getId());
		statementSearch.setFromDate(statement.getOperationDate());
		statementSearch.setToDate(statement.getOperationDate());
		statementSearch.setPayment(statement.isPayment());
		statementSearch.setAmount(Double.toString(statement.getAmount()));
		statementSearch.setDescription(statement.getDescription());
		statementSearch.setCommonConcept(statement.getCommonConcept());
		statementSearch.setLotNumber(Integer.toString(statement.getLotNumber()));
		statementSearch.setStatementStatuses(null);
		onSearch(event);

		setBackAction(backAction);
		setBackActionListener(BANK_STATEMENT_CONTROLLER_NAME + ".onBack");
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
