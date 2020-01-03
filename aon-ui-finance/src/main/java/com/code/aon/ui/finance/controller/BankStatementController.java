package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_BANK_STATEMENT_CHECK_AUTO_PROCESS_END;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_BANK_STATEMENT_CHECK_AUTO_PROCESS_INFO;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_BANK_STATEMENT_CHECK_AUTO_PROCESS_START;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_UNRECORD_ERROR;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_CHECK_NO_LINE_SELECTED;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_IMPORT_BANK_ACCOUNT_NOT_FOUND;
import static com.code.aon.ui.common.ICommonMessages.PENDING;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.hibernate.Session;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryBankStatement;
import com.code.aon.account.bridge.AccountEntryFinanceBatch;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.account.bridge.writer.FinanceRecordingTo;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.AonFile;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.BankConcept;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.BankStatementLink;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
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
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.event.BankStatementSearchListener;
import com.code.aon.ui.finance.event.FinanceListSearchListener;
import com.code.aon.ui.finance.event.FinanceTrackingListSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class BankStatementController extends BasicController implements IFinanceConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(BankStatementController.class.getName());
	
	private RegistryBank registryBank;
	private Date operationDate;
	private BankConcept bankConcept;
	private Account account;
	private boolean showImportFileWindow;
	private boolean aeb43;
	private boolean confidential;
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

	public boolean isConfidential() {
		return confidential;
	}
	public void setConfidential(boolean confidential) {
		this.confidential = confidential;
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

	public void onEditSearch(ActionEvent event) {
		try {
			if (getAvailableRegistryBanks() == 0) {
				String msg = "No hay Cuentas Bancarias definidas";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		} catch (ManagerBeanException ex) {
			throw new AbortProcessingException(ex);
		}
		super.onEditSearch(event);
	}

	public void onSeeAll(ActionEvent event) throws ManagerBeanException {
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(BANK_STATEMENT_SEARCH_LISTENER_NAME);
		searchListener.setStatementReliabilities(new StatementReliability[0]);
		searchListener.setStatementStatuses(new StatementStatus[0]);
		searchBankStatements(getRegistryBank());
	}

	public void onSeePending(ActionEvent event) throws ManagerBeanException {
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(BANK_STATEMENT_SEARCH_LISTENER_NAME);
		searchListener.setStatementReliabilities(new StatementReliability[0]);
		StatementStatus[] statementStatus = {StatementStatus.PENDING};
		searchListener.setStatementStatuses(statementStatus);
		searchBankStatements(getRegistryBank());
	}

	public void onSeeChecked(ActionEvent event) throws ManagerBeanException {
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(BANK_STATEMENT_SEARCH_LISTENER_NAME);
		searchListener.setStatementReliabilities(new StatementReliability[0]);
		StatementStatus[] statementStatus = {StatementStatus.CHECKED};
		searchListener.setStatementStatuses(statementStatus);
		searchBankStatements(getRegistryBank());
	}

	public void onSeeExact(ActionEvent event) throws ManagerBeanException {
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(BANK_STATEMENT_SEARCH_LISTENER_NAME);
		StatementReliability[] statementReliability = {StatementReliability.VERY_HIGH};
		searchListener.setStatementReliabilities(statementReliability);
		StatementStatus[] statementStatus = {StatementStatus.CHECKED};
		searchListener.setStatementStatuses(statementStatus);
		searchBankStatements(getRegistryBank());
	}

	public void onSeeApproximate(ActionEvent event) throws ManagerBeanException {
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(BANK_STATEMENT_SEARCH_LISTENER_NAME);
		StatementReliability[] statementReliability = {StatementReliability.HIGH};
		searchListener.setStatementReliabilities(statementReliability);
		StatementStatus[] statementStatus = {StatementStatus.CHECKED};
		searchListener.setStatementStatuses(statementStatus);
		searchBankStatements(getRegistryBank());
	}

	public void onSeeAmbiguous(ActionEvent event) throws ManagerBeanException {
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(BANK_STATEMENT_SEARCH_LISTENER_NAME);
		StatementReliability[] statementReliability = {StatementReliability.MEDIUM, StatementReliability.LOW};
		searchListener.setStatementReliabilities(statementReliability);
		StatementStatus[] statementStatus = {StatementStatus.CHECKED};
		searchListener.setStatementStatuses(statementStatus);
		searchBankStatements(getRegistryBank());
	}

	public void onSeeRecorded(ActionEvent event) throws ManagerBeanException {
		BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(BANK_STATEMENT_SEARCH_LISTENER_NAME);
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
				setBankStatementRowChecked(statement, true);
			}
		}
	}

	public void onSelectChecked(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED) {
				setBankStatementRowChecked(statement, true);
			}
		}
	}

	public void onSelectExact(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED && statement.getReliability() == StatementReliability.VERY_HIGH) {
				setBankStatementRowChecked(statement, true);
			}
		}
	}

	public void onSelectApproximate(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED && statement.getReliability() == StatementReliability.HIGH) {
				setBankStatementRowChecked(statement, true);
			}
		}
	}

	public void onSelectAmbiguous(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED) {
				if (statement.getReliability() == StatementReliability.MEDIUM || statement.getReliability() == StatementReliability.LOW) {
					setBankStatementRowChecked(statement, true);
				}
			}
		}
	}

	public void onSelectRecorded(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.RECORDED) {
				setBankStatementRowChecked(statement, true);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void onSelectCurrentPage(ActionEvent event) throws ManagerBeanException {
		clearCheckedBankStatement();
		Iterator<ITransferObject> iterator = ((List<ITransferObject>)getModel().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			BankStatement statement = (BankStatement)iterator.next();
			setBankStatementRowChecked(statement, true);
		}
	}

	public void onBreakdownSelected(ActionEvent event) throws ManagerBeanException {
		for (BankStatement statement : getCheckedBankStatement()) {
			statement.setShowBankStatementLink(true);
			statement.setShowAccountEntry(AonUtil.getRoleManager().isAccountingOperator());
		}
	}

	@SuppressWarnings("unchecked")
	public void onBreakdownCurrentPage(ActionEvent event) throws ManagerBeanException {
		Iterator<ITransferObject> iterator = ((List<ITransferObject>)getModel().getWrappedData()).iterator();
		while (iterator.hasNext()) {
			BankStatement statement = (BankStatement)iterator.next();
			statement.setShowBankStatementLink(true);
			statement.setShowAccountEntry(AonUtil.getRoleManager().isAccountingOperator());
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

	public void onCancelAll(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED) {
				removeLinks(statement);
			}
		}
		searchBankStatements();
	}

	public void onCancelSelected(ActionEvent event) throws ManagerBeanException {
		for (BankStatement statement : getCheckedBankStatement()) {
			if (statement.getStatus() == StatementStatus.CHECKED) {
				removeLinks(statement);
			}
		}
		searchBankStatements();
	}

	public void onCancelExact(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED && statement.getReliability() == StatementReliability.VERY_HIGH) {
				removeLinks(statement);
			}
		}
		searchBankStatements();
	}

	public void onCancelApproximate(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
			BankStatement statement = (BankStatement)ito;
			if (statement.getStatus() == StatementStatus.CHECKED && statement.getReliability() == StatementReliability.HIGH) {
				removeLinks(statement);
			}
		}
		searchBankStatements();
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
		searchBankStatements();
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
	
	@Override
	protected void remove() throws ManagerBeanException {
		BankStatement statement = (BankStatement) getTo();
		if (statement.getStatus() == StatementStatus.PENDING) {
			super.remove();
		} else {
			getErrors().put(statement.getId(), "No se puede borrar la línea del Extracto ya que no esta pendiente.");
		}
	}

	public void onFullReset(ActionEvent event) {
		try {
			getCriteria().addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_ID), new Integer(0));
			onReset(event);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public int getAvailableRegistryBanks() throws ManagerBeanException {
		String companyControllerName = ICompanyConstants.COLLECTIONS_CONTROLLER_NAME;
		CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(companyControllerName);
		return companyCollections.getAllCompanyBanks().size();
	}

	public void onChangeBank(ValueChangeEvent event) {
		if (!isNevv()) {
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
		criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_REGISTRY_BANK_ID), registryBank.getId());
		setCriteria(criteria);
		searchBankStatements();
	}

	private void searchBankStatements() throws ManagerBeanException {
		String backAction = backAction();
		String backActionListener = getBackActionListener();
		onSearch(null);
		setBackAction(backAction);
		setBackActionListener(backActionListener);
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
		setConfidential(false);
		setAonFile(null);

		try {
			getCriteria().addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_ID), new Integer(0));
			setTo(null);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void fileUploaded(UploadEvent event) {
		setAonFile(AttachmentUtil.fileUploaded(event));
	}

	public void onImportFile(ActionEvent event) {
		try {
			int lotNumber = obtainLotNumber();
			if (isAeb43()) {
				importAeb43(lotNumber);
			} else {
				importCsv(lotNumber);
			}

			BankStatementSearchListener searchListener = (BankStatementSearchListener)AonUtil.getRegisteredBean(BANK_STATEMENT_SEARCH_LISTENER_NAME);
			searchListener.initData();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_REGISTRY_BANK_ID), getRegistryBank().getId());
			criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_LOT_NUMBER), lotNumber);
			setCriteria(criteria);
			onSearch(null);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private int obtainLotNumber() throws ManagerBeanException {
		Projection projection = Projection.max(getManagerBean().getFieldName(IEntityAlias.BANK_STATEMENT_LOT_NUMBER));
		Object value = getManagerBean().getUniqueResult(projection, null);
		if (value != null) {
			return ((Integer) value).intValue() + 1;
		}
		return 1;
	}

	private void importAeb43(int lotNumber) throws ManagerBeanException, IOException {
		BankStatement bankStatement = null;
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(getAonFile().openStream()));
		String line = reader.readLine();
		while (line != null) {
			line = StringUtils.rightPad(line, 80);
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
			} else if (lineType.equals("88")) {
				break;
			}
			line = reader.readLine();
		}
		reader.close();
	}

	private RegistryBank importAeb43Header(String line) throws ManagerBeanException {
		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), getRegistryBank().getRegistry().getId());
		String bankAcc = "ES__" + line.substring(2, 10) + "__" + line.substring(10, 20);
		criteria.addExpression(ExpressionUtilities.getLikeExpression(rBankBean.getFieldName(IEntityAlias.REGISTRY_BANK_BANK_ACCOUNT), bankAcc));
		Iterator<?> iterator = rBankBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (RegistryBank)iterator.next();
		} else {
			bankAcc = line.substring(2, 6) + "." + line.substring(6, 10) + ".**." + line.substring(10, 20);
			AonUtil.addErrorMessageFromBundle(FINANCE_IMPORT_BANK_ACCOUNT_NOT_FOUND, bankAcc);
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
		bankStatement.setDocument(line.substring(42, 52));
		bankStatement.setReference1(line.substring(52, 64));
		bankStatement.setReference2(line.substring(64, 80));
		bankStatement.setDescription(line.substring(52, 80));
		bankStatement.setReliability(StatementReliability.VERY_HIGH);
		bankStatement.setSecurityLevel(isConfidential() ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
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
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(getAonFile().openStream()));
		String line = reader.readLine();
		while (line != null) {
			importCsvData(line, lotNumber);
			line = reader.readLine();
		}
		reader.close();
	}

	private BankStatement importCsvData(String line, int lotNumber) throws ManagerBeanException {
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
				date = obtainDateDDMMAA(token.replace("-", "").replace("/", ""));
			} else if (stk.hasMoreTokens()) {
				description += token;
			} else {
				token = token.replace(",", ".");
				while (token.indexOf('.') != token.lastIndexOf('.')) {
					token = token.replaceFirst("[.]", "");
				}
				amount = Double.parseDouble(token);
				if (amount < 0) {
					payment = true;
					amount = amount * (-1);
				}
			}
			pos++;
		}

		BankStatement bankStatement = new BankStatement();
		bankStatement.setRegistryBank(getRegistryBank());
		bankStatement.setLotNumber(lotNumber);
		bankStatement.setOperationDate(date);
		bankStatement.setCommonConcept(StatementConcept.UNKNOWN);
		bankStatement.setOwnConcept(null);
		bankStatement.setPayment(payment);
		bankStatement.setAmount(amount);
		bankStatement.setDocument(null);
		bankStatement.setReference1(null);
		bankStatement.setReference2(null);
		bankStatement.setDescription((description.length() > 80) ? description.substring(0, 80) : description);
		bankStatement.setReliability(StatementReliability.VERY_HIGH);
		bankStatement.setSecurityLevel(isConfidential() ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		bankStatement.setStatus(StatementStatus.PENDING);
		return (BankStatement)getManagerBean().insert(bankStatement);
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
		if (getAccount() == null || StringUtils.isEmpty(getAccount().getCode())) {
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

	public void onAutoCheckSelected(ActionEvent event) throws ManagerBeanException {
		if (getCheckedBankStatement().size() == 0) {
			AonUtil.addWarningMessageFromBundle(FINANCE_CHECK_NO_LINE_SELECTED);
			return;
		}

		LogPanelController log = LogPanelController.getInstance();
		log.reset();
		log.info(AonUtil.getMessage(FINANCE_BANK_STATEMENT_CHECK_AUTO_PROCESS_START));
		try {
			for (BankStatement to : getCheckedBankStatement()) {
				if (to.isPending()) {
					getErrors().put(to.getId(), "No se ha encontrado ninguna coincidencia para Puntear la línea del Extracto.");
					if (hasFinanceLink(to.getCommonConcept())) {
						findFinance(to, false);
						if (to.isPending()) {
							findFinanceBatch(to);
						}
					} else if (hasFinanceReturnLink(to.getCommonConcept())) {
						findFinance(to, true);
					} else if (hasFinanceBatchLink(to.getCommonConcept())) {
						findFinanceBatch(to);
					}

					if (to.isPending()) {
						findTransfer(to);
					}

					if (to.isPending()) {
						findBankStatement(to);
					}
				}
				log.info(AonUtil.getMessage(FINANCE_BANK_STATEMENT_CHECK_AUTO_PROCESS_INFO, to.getDescription(), (!to.isPending()) ? "OK" : "MISS"));
			}

			onSearch(null);
			clearCheckedBankStatement();
		} catch (ManagerBeanException e) {
			log.error(e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ExpressionException e) {
			log.error(e.getMessage());
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			log.info(AonUtil.getMessage(FINANCE_BANK_STATEMENT_CHECK_AUTO_PROCESS_END));
			log.finish();
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
		double fromAmount = CommonUtil.round(to.getAmount() * 0.9);
		double toAmount = CommonUtil.round(to.getAmount() * 1.1);
		Date fromDate = DateUtils.addDays(to.getOperationDate(), -7);
		Date toDate = DateUtils.addDays(to.getOperationDate(), 7);

		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		for (int key=1; key<=3 && to.isPending(); key++) {
			Criteria criteriaFin = new Criteria();
			criteriaFin.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAYMENT), Boolean.valueOf(payment));
	        Criteria criteriaTrk = new Criteria();
			criteriaTrk.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_PAYMENT), Boolean.valueOf(payment));
			criteriaTrk.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_REGISTRY_BANK_ID), to.getRegistryBank().getId());
			criteriaTrk.addNullExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_BANK_STATEMENT_LINK));
			criteriaTrk.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_RECORDED), Boolean.FALSE);
			if (!AonUtil.getRoleManager().isConfidentiality()) {
				criteriaFin.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_SECURITY_LEVEL), SecurityLevel.OFFICIAL);

				criteriaTrk.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
			}
	        if (!returned) {
				criteriaFin.addNotEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PAID);
				criteriaFin.addNotEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.BATCHED);

				criteriaTrk.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_TYPE), FinanceTrackingType.PAID);
			} else {
				criteriaFin.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PAID);

				criteriaTrk.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_TYPE), FinanceTrackingType.RETURNED);
			}
			switch (key) {
				case 1: {
					criteriaFin.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT), to.getAmount());
					criteriaFin.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_DUE_DATE), to.getOperationDate());

					criteriaTrk.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_AMOUNT), to.getAmount());
					criteriaTrk.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_TRACKING_DATE), to.getOperationDate());
					break;
				}
				case 2: {
					criteriaFin.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT), to.getAmount());
					criteriaFin.addBetweenExpression(financeBean.getFieldName(IEntityAlias.FINANCE_DUE_DATE), fromDate, toDate);
					criteriaFin.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_DUE_DATE));

					criteriaTrk.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_AMOUNT), to.getAmount());
					criteriaTrk.addBetweenExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_TRACKING_DATE), fromDate, toDate);
					criteriaTrk.addOrder(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_TRACKING_DATE));
					break;
				}
				case 3: {
					criteriaFin.addBetweenExpression(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT), fromAmount, toAmount);
					criteriaFin.addBetweenExpression(financeBean.getFieldName(IEntityAlias.FINANCE_DUE_DATE), fromDate, toDate);
					criteriaFin.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT));
					criteriaFin.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_DUE_DATE));

					criteriaTrk.addBetweenExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_AMOUNT), fromAmount, toAmount);
					criteriaTrk.addBetweenExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_TRACKING_DATE), fromDate, toDate);
					criteriaTrk.addOrder(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_AMOUNT));
					criteriaTrk.addOrder(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_TRACKING_DATE));
					break;
				}
			}

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
		double fromAmount = CommonUtil.round(to.getAmount() * 0.9);
		double toAmount = CommonUtil.round(to.getAmount() * 1.1);
		Date fromDate = DateUtils.addDays(to.getOperationDate(), -7);
		Date toDate = DateUtils.addDays(to.getOperationDate(), 7);

		IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
		for (int key=1; key<=2 && to.isPending(); key++) {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_PAYMENT), Boolean.valueOf(to.isPayment()));
	        if (!AonUtil.getRoleManager().isConfidentiality()) {
				criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
			}
			criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_REGISTRY_BANK_ID), to.getRegistryBank().getId());
			criteria.addNullExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_BANK_STATEMENT_LINK));
			criteria.addNotEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), FinanceBatchStatus.RECORDED);
			switch (key) {
				case 1: {
					criteria.addEqualExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_ISSUE_DATE), to.getOperationDate());
					break;
				}
				case 2: {
					criteria.addBetweenExpression(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_ISSUE_DATE), fromDate, toDate);
					criteria.addOrder(fBatchBean.getFieldName(IEntityAlias.FINANCE_BATCH_ISSUE_DATE));
					break;
				}
			}

			ITransferObject linkedTo = null;
			int matchesAmount = 0;
			List<ITransferObject> fBatchList = fBatchBean.getList(criteria);
			for (ITransferObject ito : fBatchList) {
				FinanceBatch fBatch = (FinanceBatch)ito;
				double fBatchAmount = fBatch.getFinanceBatchTotalAmount();
				if (to.getAmount() == fBatchAmount) {
					++matchesAmount;
				}
				if (((key == 2) && (fromAmount <= fBatchAmount) && (toAmount >= fBatchAmount)) || (to.getAmount() == fBatchAmount)) {
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

	private void findTransfer(BankStatement to) throws ManagerBeanException, ExpressionException {
		boolean payment = to.isPayment();
		double fromAmount = CommonUtil.round(to.getAmount() * 0.9);
		double toAmount = CommonUtil.round(to.getAmount() * 1.1);
		Date fromDate = DateUtils.addDays(to.getOperationDate(), -7);
		Date toDate = DateUtils.addDays(to.getOperationDate(), 7);
		Account bankAccount = getWriter().obtainPaymentAccount(to.getRegistryBank(), null);

		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		for (int key=1; key<=3 && to.isPending(); key++) {
	        String source = Integer.toString(StatementLinkSource.ACCOUNT.ordinal());
	        String sourceId = bankAccount.getCode();
	        Expression expr1 = ExpressionUtilities.getExpression(source, statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_SOURCE));
	        Expression expr2 = ExpressionUtilities.getExpression(sourceId, statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_SOURCE_ID));
	        Expression accountExpr = ExpressionUtilities.getAndExpression(expr1, expr2);
	        Expression bankConceptExpr = null;
	        for (BankConcept bankConcept : getRelatedBankConcepts(bankAccount)) {
		        source = Integer.toString(StatementLinkSource.BANK_CONCEPT.ordinal());
		        sourceId = Integer.toString(bankConcept.getId());
		        expr1 = ExpressionUtilities.getExpression(source, statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_SOURCE));
		        expr2 = ExpressionUtilities.getExpression(sourceId, statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_SOURCE_ID));
		        Expression andExpr = ExpressionUtilities.getAndExpression(expr1, expr2);
	        	bankConceptExpr = (bankConcept == null) ? andExpr : ExpressionUtilities.getOrExpression(bankConceptExpr, andExpr);
			}
	        Criteria criteria = new Criteria();
	        criteria.addExpression((bankConceptExpr == null) ? accountExpr : ExpressionUtilities.getOrExpression(accountExpr, bankConceptExpr));
	        criteria.addEqualExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_PAYMENT), Boolean.valueOf(!payment));
	        if (!AonUtil.getRoleManager().isConfidentiality()) {
				criteria.addEqualExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
			}
	        criteria.addNullExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_LINKED_BANK_STATEMENT_LINK));
			criteria.addNotEqualExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_STATUS), StatementStatus.RECORDED);
			switch (key) {
				case 1: {
			        criteria.addEqualExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_AMOUNT), to.getAmount());
			        criteria.addEqualExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_OPERATION_DATE), to.getOperationDate());
					break;
				}
				case 2: {
			        criteria.addEqualExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_AMOUNT), to.getAmount());
			        criteria.addBetweenExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_OPERATION_DATE), fromDate, toDate);
					criteria.addOrder(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_OPERATION_DATE));
					break;
				}
				case 3: {
			        criteria.addBetweenExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_AMOUNT), fromAmount, toAmount);
			        criteria.addBetweenExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_OPERATION_DATE), fromDate, toDate);
					criteria.addOrder(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_OPERATION_DATE));
					break;
				}
			}

			ITransferObject linkedTo = null;
			List<ITransferObject> transferList = statementLinkBean.getList(criteria);
			for (ITransferObject ito : transferList) {
				BankStatementLink link = (BankStatementLink)ito;
				if (linkedTo == null || isMoreAccurateLink(to, linkedTo, link.getAmount(), link.getBankStatement().getOperationDate())) {
					linkedTo = link;
				}
			}

			if (linkedTo != null) {
				BankStatementLink link = (BankStatementLink)linkedTo;
				Account linkedAccount = getWriter().obtainPaymentAccount(link.getBankStatement().getRegistryBank(), null);
				BankConcept linkedBankConcept = null;
				if (link.getSource() == StatementLinkSource.BANK_CONCEPT) {
					IManagerBean bankConceptBean = BeanManager.getManagerBean(BankConcept.class);
					criteria = new Criteria();
					criteria.addEqualExpression(bankConceptBean.getFieldName(IEntityAlias.BANK_CONCEPT_ACCOUNT_ID), linkedAccount.getId());
					for (ITransferObject ito : bankConceptBean.getList(criteria)) {
						linkedBankConcept = (BankConcept)ito;
						break;
					}
				}
				if (linkedBankConcept == null) {
					getBankStatementLinkManager().addLink(to, linkedAccount, link.getAmount(), link);
				} else {
					getBankStatementLinkManager().addLink(to, linkedBankConcept, link.getAmount(), link);
				}

				StatementReliability reliability = StatementReliability.LOW;
				if (transferList.size() == 1) {
					if (key == 1) reliability = StatementReliability.VERY_HIGH;
					else if (key == 2) reliability = StatementReliability.HIGH;
					else reliability = StatementReliability.MEDIUM;
				}
				getBankStatementLinkManager().checkBankStatement(to, reliability);
				getErrors().remove(to.getId());
			}
		}
	}

	private void findBankStatement(BankStatement to) throws ManagerBeanException {
		for (int key=1; key<=5 && to.isPending(); key++) {
			Criteria criteria = new Criteria();
			criteria.addNotEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_STATUS), StatementStatus.PENDING);
			criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_REGISTRY_BANK_ID), to.getRegistryBank().getId());
			criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_PAYMENT), Boolean.valueOf(to.isPayment()));
			criteria.addOrder(getFieldName(IEntityAlias.BANK_STATEMENT_OPERATION_DATE), false);
			switch (key) {
				case 1: {
					criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_AMOUNT), to.getAmount());
					criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_DESCRIPTION), to.getDescription());
					break;
				}
				case 2: {
					criteria.addNotEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_AMOUNT), to.getAmount());
					criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_DESCRIPTION), to.getDescription());
					break;
				}
				case 3: {
					criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_AMOUNT), to.getAmount());
					criteria.addNotEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_DESCRIPTION), to.getDescription());
					criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_COMMON_CONCEPT), to.getCommonConcept());
					break;
				}
				case 4: {
					criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_AMOUNT), to.getAmount());
					criteria.addNotEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_DESCRIPTION), to.getDescription());
					criteria.addNotEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_COMMON_CONCEPT), to.getCommonConcept());
					break;
				}
				case 5: {
					String likeStr = StringUtils.substring(to.getDescription(), 0, 15) + "%";
					criteria.addExpression(ExpressionUtilities.getLikeExpression(getFieldName(IEntityAlias.BANK_STATEMENT_DESCRIPTION), likeStr));
					break;
				}
			}

			List<ITransferObject> statementList = getManagerBean().getList(criteria);
			for (ITransferObject ito : statementList) {
				BankStatement statement = (BankStatement)ito;

				IManagerBean linkBean = BeanManager.getManagerBean(BankStatementLink.class);
				criteria = new Criteria();
				criteria.addEqualExpression(linkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), statement.getId());
				criteria.addNotEqualExpression(linkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_SOURCE), StatementLinkSource.BANK_CONCEPT);
				criteria.addNotEqualExpression(linkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_SOURCE), StatementLinkSource.ACCOUNT);
				if (linkBean.getCount(criteria) == 0) {
					criteria = new Criteria();
					criteria.addEqualExpression(linkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), statement.getId());
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
								newLink.setLinkedBankStatementLink(null);
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
		Date linkedDate = null;
		double linkedAmount = 0;
		if (linkedTo instanceof Finance) {
			Finance linkedFinance = (Finance)linkedTo;
			linkedDate = linkedFinance.getDueDate();
			linkedAmount = linkedFinance.getTotalAmount();
		} else if (linkedTo instanceof FinanceTracking) {
			FinanceTracking linkedTracking = (FinanceTracking)linkedTo;
			linkedDate = linkedTracking.getTrackingDate();
			linkedAmount = linkedTracking.getAmount();
		} else if (linkedTo instanceof FinanceBatch) {
			FinanceBatch linkedBatch = (FinanceBatch)linkedTo;
			linkedDate = linkedBatch.getIssueDate();
			linkedAmount = linkedBatch.getFinanceBatchTotalAmount();
		} else if (linkedTo instanceof BankStatementLink) {
			BankStatementLink linkedTransfer = (BankStatementLink)linkedTo;
			linkedDate = linkedTransfer.getBankStatement().getOperationDate();
			linkedAmount = linkedTransfer.getAmount();
		} 

		if (linkedDate != null) {
			long linkedDaysBetween = CommonUtil.getDaysBetweenDates(statement.getOperationDate(), linkedDate, false);
			long daysBetween = CommonUtil.getDaysBetweenDates(statement.getOperationDate(), date, false);
			return isMoreAccurateLink(statement, linkedAmount, linkedDaysBetween, amount, daysBetween);
		}
		return false;
	}

	private boolean isMoreAccurateLink(BankStatement statement, double linkedAmount, long linkedDaysBetween, double amount, long daysBetween) {
		if (Math.abs(statement.getAmount() - amount) == Math.abs(statement.getAmount() - linkedAmount)) {
			return (Math.abs(daysBetween) < Math.abs(linkedDaysBetween));
		}
		return (Math.abs(statement.getAmount() - amount) < Math.abs(statement.getAmount() - linkedAmount));
	}

	public void onAddLinkShow(ActionEvent event) throws ManagerBeanException, ExpressionException {
		BankStatement to = (BankStatement)getModel().getRowData();
		boolean payment = (to.getCommonConcept() != StatementConcept.RETURNED) ? to.isPayment() : !to.isPayment();
		Double fromAmount = new Double(CommonUtil.round(to.getAmount() * 0.9));
		Double toAmount = new Double(CommonUtil.round(to.getAmount() * 1.1));
		Date fromDate = DateUtils.addDays(to.getOperationDate(), -7);
		Date toDate = DateUtils.addDays(to.getOperationDate(), 7);

		BankStatementLinkController statementLinkList = (BankStatementLinkController)FormUtil.getController(BANK_STATEMENT_LINK_CONTROLLER_NAME);
		statementLinkList.clearCheckedStatementLinks();
		statementLinkList.onEditSearch(null);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(statementLinkList.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), to.getId());
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
			criteria.addEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_PAYMENT), Boolean.valueOf(payment));
	        if (!AonUtil.getRoleManager().isConfidentiality()) {
				criteria.addEqualExpression(financeList.getFieldName(IEntityAlias.FINANCE_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
			}
			criteria.addBetweenExpression(financeList.getFieldName(IEntityAlias.FINANCE_AMOUNT), fromAmount, toAmount);
			criteria.addBetweenExpression(financeList.getFieldName(IEntityAlias.FINANCE_DUE_DATE), fromDate, toDate);
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
			criteria.addEqualExpression(trackingList.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_PAYMENT), Boolean.valueOf(payment));
	        if (!AonUtil.getRoleManager().isConfidentiality()) {
				criteria.addEqualExpression(trackingList.getFieldName(IEntityAlias.FINANCE_TRACKING_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
			}
			criteria.addBetweenExpression(trackingList.getFieldName(IEntityAlias.FINANCE_TRACKING_AMOUNT), fromAmount, toAmount);
			criteria.addBetweenExpression(trackingList.getFieldName(IEntityAlias.FINANCE_TRACKING_TRACKING_DATE), fromDate, toDate);
			criteria.setOrderByList(trackingList.getOrderList());
			trackingList.setCriteria(criteria);
			trackingList.onSearch(null);
		}

		if (to.getCommonConcept() != StatementConcept.RETURNED) {
			FBatchListController batchList = (FBatchListController)FormUtil.getController(FINANCE_BATCH_LIST_CONTROLLER_NAME);
			batchList.onEditSearch(null);
			criteria = new Criteria();
			criteria.addEqualExpression(batchList.getFieldName(IEntityAlias.FINANCE_BATCH_PAYMENT), Boolean.valueOf(payment));
	        if (!AonUtil.getRoleManager().isConfidentiality()) {
				criteria.addEqualExpression(batchList.getFieldName(IEntityAlias.FINANCE_BATCH_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
			}
			criteria.addEqualExpression(batchList.getFieldName(IEntityAlias.FINANCE_BATCH_REGISTRY_BANK_ID), to.getRegistryBank().getId());
			criteria.addNullExpression(batchList.getFieldName(IEntityAlias.FINANCE_BATCH_BANK_STATEMENT_LINK));
			criteria.addNotEqualExpression(batchList.getFieldName(IEntityAlias.FINANCE_BATCH_FINANCE_BATCH_STATUS), FinanceBatchStatus.RECORDED);
			criteria.addBetweenExpression(batchList.getFieldName(IEntityAlias.FINANCE_BATCH_ISSUE_DATE), fromDate, toDate);
			criteria.setOrderByList(batchList.getOrderList());
			batchList.setCriteria(criteria);
			batchList.onSearch(null);
		}

		Account bankAccount = getWriter().obtainPaymentAccount(to.getRegistryBank(), null);
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
        String source = Integer.toString(StatementLinkSource.ACCOUNT.ordinal());
        String sourceId = bankAccount.getCode();
        Expression expr1 = ExpressionUtilities.getExpression(source, statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_SOURCE));
        Expression expr2 = ExpressionUtilities.getExpression(sourceId, statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_SOURCE_ID));
        Expression accountExpr = ExpressionUtilities.getAndExpression(expr1, expr2);
        Expression bankConceptExpr = null;
        for (BankConcept bankConcept : getRelatedBankConcepts(bankAccount)) {
	        source = Integer.toString(StatementLinkSource.BANK_CONCEPT.ordinal());
	        sourceId = Integer.toString(bankConcept.getId());
	        expr1 = ExpressionUtilities.getExpression(source, statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_SOURCE));
	        expr2 = ExpressionUtilities.getExpression(sourceId, statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_SOURCE_ID));
	        Expression andExpr = ExpressionUtilities.getAndExpression(expr1, expr2);
        	bankConceptExpr = (bankConcept == null) ? andExpr : ExpressionUtilities.getOrExpression(bankConceptExpr, andExpr);
		}
        criteria = new Criteria();
        criteria.addExpression((bankConceptExpr == null) ? accountExpr : ExpressionUtilities.getOrExpression(accountExpr, bankConceptExpr));
        criteria.addEqualExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_PAYMENT), Boolean.valueOf(!payment));
        if (!AonUtil.getRoleManager().isConfidentiality()) {
			criteria.addEqualExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		}
        criteria.addNullExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_LINKED_BANK_STATEMENT_LINK));
		criteria.addNotEqualExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_STATUS), StatementStatus.RECORDED);
        criteria.addBetweenExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_AMOUNT), fromAmount, toAmount);
        criteria.addBetweenExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_OPERATION_DATE), fromDate, toDate);
		criteria.addOrder(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_OPERATION_DATE));
		getBankStatementLinkManager().setLinkTransferList(statementLinkBean.getList(criteria));
		getBankStatementLinkManager().setLinkTransferModel(null);

		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		criteria = new Criteria();
		criteria.addEqualExpression(entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), bankAccount.getId());
		criteria.addBetweenExpression(entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), fromDate, toDate);
		if ((to.isPayment() && to.getAmount() < 0) || (!to.isPayment() && to.getAmount() >= 0)) {
			criteria.addEqualExpression(entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_DEBIT), to.getAmount());
		} else {
			criteria.addEqualExpression(entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_CREDIT), to.getAmount());
		}
		if (!AonUtil.getRoleManager().isConfidentiality()) {
			criteria.addEqualExpression(entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		}
		getBankStatementLinkManager().setEntryDetailList(entryDetailBean.getList(criteria));
		getBankStatementLinkManager().setEntryDetailModel(null);

        getBankStatementLinkManager().setCurrentStatement(to);
        getBankStatementLinkManager().setAccount(new Account());
        getBankStatementLinkManager().setComments(to.getComments());

        if (getBankStatementLinkManager().getEntryDetailList() != null && getBankStatementLinkManager().getEntryDetailList().size() > 0) {
        	getBankStatementLinkManager().setEntryDetailLinkTab();
        } else if (getBankStatementLinkManager().getLinkTransferList() != null && getBankStatementLinkManager().getLinkTransferList().size() > 0) {
           	getBankStatementLinkManager().setLinkTransferLinkTab();
        } else if (statementLinkList.getRowCount() > 0) {
        	getBankStatementLinkManager().setStatementLinkTab();
        } else if (to.getCommonConcept() == StatementConcept.COLLECTION_BATCH) {
        	getBankStatementLinkManager().setFBatchLinkTab();
        } else {
        	getBankStatementLinkManager().setFinanceLinkTab();
        }
        getBankStatementLinkManager().setAmountPending();
        getErrors().remove(to.getId());
	}

	private List<BankConcept> getRelatedBankConcepts(Account account) throws ManagerBeanException {
		List<BankConcept> bankConcepts = new LinkedList<BankConcept>();
		IManagerBean bankConceptBean = BeanManager.getManagerBean(BankConcept.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bankConceptBean.getFieldName(IEntityAlias.BANK_CONCEPT_ACCOUNT_ID), account.getId());
		for (ITransferObject ito : bankConceptBean.getList(criteria)) {
			bankConcepts.add((BankConcept)ito);
		}
		return bankConcepts;
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
			return getErrors().get(to.getId());
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
		criteria.addEqualExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), statement.getId());
		criteria.addOrder(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_SOURCE));
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

	public double getPendingAmount() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			BankStatement to = (BankStatement)getModel().getRowData();
			return CommonUtil.round(to.getAmount() - getBankStatementLinkManager().getCheckedAmount(to));
		}
		return 0;
	}

	public void onSetVeryHighReliability(ActionEvent event) {
		try {
			BankStatement to = (BankStatement)getModel().getRowData();
			to.setReliability(StatementReliability.VERY_HIGH);
			getManagerBean().update(to);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onSetHighReliability(ActionEvent event) {
		try {
			BankStatement to = (BankStatement)getModel().getRowData();
			to.setReliability(StatementReliability.HIGH);
			getManagerBean().update(to);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onSetMediumReliability(ActionEvent event) {
		try {
			BankStatement to = (BankStatement)getModel().getRowData();
			to.setReliability(StatementReliability.MEDIUM);
			getManagerBean().update(to);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onSetLowReliability(ActionEvent event) {
		try {
			BankStatement to = (BankStatement)getModel().getRowData();
			to.setReliability(StatementReliability.LOW);
			getManagerBean().update(to);
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onShowBankStatementLink(ActionEvent event) {
		try {
			BankStatement to = (BankStatement)getModel().getRowData();
			to.setShowBankStatementLink(true);
			to.setShowAccountEntry(AonUtil.getRoleManager().isAccountingOperator());
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

	public void onRecordSelected(ActionEvent event) throws ManagerBeanException {
		if (getCheckedBankStatementSize() == 0) {
			AonUtil.addWarningMessageFromBundle(FINANCE_CHECK_NO_LINE_SELECTED);
			return;
		}

		for (BankStatement statement : getCheckedBankStatement()) {
			onRecordBankStatement(statement);
		}

		onSearch(null);
		clearCheckedBankStatement();
	}

	public void onRecordBankStatement(ActionEvent event) throws ManagerBeanException {
		BankStatement to = (BankStatement)getModel().getRowData();
		onRecordBankStatement(to);
	}

	private void onRecordBankStatement(BankStatement statement) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			recordBankStatement(HibernateUtil.getSession(sessionName), statement);

			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			getErrors().put(statement.getId(), e.getMessage());
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	@SuppressWarnings("unchecked")
	private void recordBankStatement(Session session, BankStatement statement) throws ManagerBeanException {
		if (statement.isChecked()) {
			List<FinanceTracking> financeTrackingList = new LinkedList<FinanceTracking>();
			List<FinanceBatch> financeBatchList = new LinkedList<FinanceBatch>();
			List<BankStatementLink> transferList = new LinkedList<BankStatementLink>();
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
						financeTrackingList.add(tracking);
					} else {
						String docNumber = tracking.getFinance().getDocumentNumber();
						getErrors().put(statement.getId(), "El Vencimiento " + docNumber + " ya esta Contabilizado.");
					}
				} else if (statementLink.getSource() == StatementLinkSource.FINANCE_BATCH) {
					FinanceBatch batch = (FinanceBatch)statementLink.getSourceTo();
					if (batch.getFinanceBatchStatus() != FinanceBatchStatus.RECORDED) {
						financeBatchList.add(batch);
					} else {
						getErrors().put(statement.getId(), "La Remesa " + batch.getId() + " ya esta Contabilizada.");
					}
				} else {
					if (statementLink.getSource() == StatementLinkSource.BANK_CONCEPT) {
						BankConcept concept = (BankConcept)statementLink.getSourceTo();
						if (concept.getAccount() != null && concept.getAccount().getId() != null) {
							double amount = statementLink.getAmount();
							if (accountMap.containsKey(concept.getAccount())) {
								amount = CommonUtil.round(amount + accountMap.get(concept.getAccount()));
							}
							accountMap.put(concept.getAccount(), new Double(amount));
						} else {
							getErrors().put(statement.getId(), "El Concepto " + concept.getName() + " no tiene Cuenta Contable asociada.");
						}
					} else if (statementLink.getSource() == StatementLinkSource.ACCOUNT) {
						Account account = (Account)statementLink.getSourceTo();
						double amount = statementLink.getAmount();
						if (accountMap.containsKey(account)) {
							amount = CommonUtil.round(amount + accountMap.get(account));
						}
						accountMap.put(account, new Double(amount));
					}

					if (statementLink.getLinkedBankStatementLink() != null) {
						transferList.add(statementLink.getLinkedBankStatementLink());
					}
				}
			}

			if (getErrors().get(statement.getId()) == null) {
				double statementAmount = (statement.isPayment()) ? (0 - statement.getAmount()) : statement.getAmount();
				if (statementAmount != CommonUtil.round(linksAmount)) {
					String message = "El Importe de la línea del Extracto no cuadra con la suma de los Detalles del mismo.";
					if (statementAmount == CommonUtil.round(0 - linksAmount)) {
						message += " Revise si realmente es un Cargo o Abono.";
					}
					getErrors().put(statement.getId(), message);
				} else {
					AccountEntry entry = null;
					Account bankAccount = getWriter().obtainPaymentAccount(statement.getRegistryBank(), null);
					for (BankStatementLink linkTransfer : transferList) {
						IManagerBean entryStatementBean = BeanManager.getManagerBean(AccountEntryBankStatement.class);
						Criteria criteria = new Criteria();
						String alias = entryStatementBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_BANK_STATEMENT_BANK_STATEMENT_ID);
						criteria.addEqualExpression(alias, linkTransfer.getBankStatement().getId());
						for (ITransferObject ito : entryStatementBean.getList(criteria)) {
							entry = ((AccountEntryBankStatement)ito).getAccountEntry();

							IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
							criteria = new Criteria();
							alias = entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID);
							criteria.addEqualExpression(alias, entry.getId());
							alias = entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID);
							criteria.addEqualExpression(alias, bankAccount.getId());
							if ((statement.isPayment() && statement.getAmount() < 0) || (!statement.isPayment() && statement.getAmount() >= 0)) {
								alias = entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_DEBIT);
							} else {
								alias = entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_CREDIT);
							}
							criteria.addEqualExpression(alias, statement.getAmount());
							if (entryDetailBean.getCount(criteria) == 0) {
								getErrors().put(statement.getId(), "El Apunte " + entry.getId() + " no tiene el importe correcto para esta línea");
								entry = null;
							} else {
								getErrors().remove(statement.getId());
								break;
							}
						}
					}
					
					if (entry == null && getErrors().get(statement.getId()) == null) {
						FinanceRecordingTo recordingTo = new FinanceRecordingTo();
						recordingTo.setType((statement.isPayment()) ? AccountEntryType.PAYMENT : AccountEntryType.COLLECTION);
						recordingTo.setDate(statement.getOperationDate());
						recordingTo.setPaymentAccount(bankAccount);
						recordingTo.setBalancingConcept(StringUtils.abbreviate(statement.getDescription(), 32));
						recordingTo.setSecurityLevel(statement.getSecurityLevel());
						recordingTo.setComments(statement.getComments());
						recordingTo.setAccountMap(accountMap);

						if (financeTrackingList.size() > 0) {
							if (statement.getCommonConcept() == StatementConcept.RETURNED) {
								recordingTo.setType((statement.isPayment()) ? AccountEntryType.RETURNED_COLLECTION : AccountEntryType.RETURNED_PAYMENT);
							}
							recordingTo.setFinanceTrackingList(financeTrackingList);
							entry = getWriter().recordFinanceTrackings(recordingTo, entry);
						} else if (financeBatchList.size() > 0) {
							if (financeBatchList.size() > 1) {
								getErrors().put(statement.getId(), "No puede haber más de una Remesa en la misma línea del Extracto.");
							} else {
								FinanceBatch fBatch = financeBatchList.get(0);
								recordingTo.setBalancingConcept(fBatch.getDescription());
								List<?> details = fBatch.getDetailList();
								recordingTo.setFBatchDetailList( (List<FinanceBatchDetail>) details );
								entry = getWriter().recordFBatch(recordingTo, fBatch, entry);
							}
						} else {
							recordingTo.setType(AccountEntryType.MANUAL);
							entry = getWriter().recordBankStatementLinks(recordingTo, statement.isPayment(), statement.getAmount());
						}
					}

					if (entry != null) {
						recordBankStatement(session, entry, statement);
					}
				}
			}
		} else if (statement.getStatus() == StatementStatus.PENDING) {
			getErrors().put(statement.getId(), "La línea del Extracto esta Pendiente. No se puede Contabilizar.");
		} else if (statement.getStatus() == StatementStatus.RECORDED) {
			getErrors().put(statement.getId(), "La línea del Extracto ya esta Contabilizada.");
		}
	}

	public void recordBankStatement(Session session, AccountEntry entry, BankStatement statement) throws ManagerBeanException {
		getWriter().insertAccountEntryBankStatement(entry, statement);

		statement.setSecurityLevel(entry.getSecurityLevel());
		statement.setStatus(StatementStatus.RECORDED);
		if (session != null) {
			statement = (BankStatement)session.merge(statement);
		}
		getManagerBean().update(statement);
	}

	public void onUnrecordSelected(ActionEvent event) throws ManagerBeanException {
		if (getCheckedBankStatementSize() == 0) {
			AonUtil.addWarningMessageFromBundle(FINANCE_CHECK_NO_LINE_SELECTED);
			return;
		}

		for (BankStatement statement : getCheckedBankStatement()) {
			onUnrecordBankStatement(statement);
		}

		onSearch(null);
		clearCheckedBankStatement();
	}

	public void onUnrecordBankStatement(ActionEvent event) throws ManagerBeanException {
		BankStatement to = (BankStatement)getModel().getRowData();
		onUnrecordBankStatement(to);
	}

	private void onUnrecordBankStatement(BankStatement statement) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			unrecordBankStatement(HibernateUtil.getSession(sessionName), statement);

			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			getErrors().put(statement.getId(), e.getMessage());
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private void unrecordBankStatement(Session session, BankStatement statement) throws ManagerBeanException {
		getErrors().remove(statement.getId());
		if (statement.isRecorded()) {
			IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), statement.getId());
			if (statementLinkBean.getCount(criteria) > 0) {
				IManagerBean accEntryStatementBean = BeanManager.getManagerBean(AccountEntryBankStatement.class);
				criteria = new Criteria();
				String alias = accEntryStatementBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_BANK_STATEMENT_BANK_STATEMENT_ID);
				criteria.addEqualExpression(alias, statement.getId());
				for (ITransferObject ito : accEntryStatementBean.getList(criteria)) {
					AccountEntryBankStatement accEntryStatement = (AccountEntryBankStatement)ito;
					AccountEntry accEntry = accEntryStatement.getAccountEntry();

					IManagerBean accEntryFBatchBean = BeanManager.getManagerBean(AccountEntryFinanceBatch.class);
					criteria = new Criteria();
					alias = accEntryFBatchBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_BATCH_ACCOUNT_ENTRY_ID);
					criteria.addEqualExpression(alias, accEntry.getId());
					for (ITransferObject fto : accEntryFBatchBean.getList(criteria)) {
						AccountEntryFinanceBatch accEntryFBatch = (AccountEntryFinanceBatch)fto;
						FinanceBatch fBatch = accEntryFBatch.getFinanceBatch();
				        if (getWriter().canRemoveAccountEntryFinanceBatch(fBatch)) {
							getWriter().removeAccountEntryFinanceBatch(fBatch, false);
				        } else {
				        	String message = AonUtil.getMessage(FINANCE_BATCH_UNRECORD_ERROR);
				        	getErrors().put(statement.getId(), message);
				        	return;
				        }
					}

			        IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
					IManagerBean accEntryTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
					criteria = new Criteria();
					alias = accEntryTrackingBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_ACCOUNT_ENTRY_ID);
					criteria.addEqualExpression(alias, accEntry.getId());
					for (ITransferObject fto : accEntryTrackingBean.getList(criteria)) {
						AccountEntryFinanceTracking accEntryTracking = (AccountEntryFinanceTracking)fto;
						FinanceTracking tracking = accEntryTracking.getFinanceTracking();
						getWriter().removeAccountEntryFinanceTracking(tracking, false);

						tracking.setDescription(AonUtil.getMessage(PENDING));
						tracking.setRecorded(false);
				        trackingBean.update(tracking);
					}
				}

				unrecordBankStatement(session, statement, true);
			} else {
				unrecordBankStatement(session, statement, false);
			}
		} else {
			getErrors().put(statement.getId(), "La línea del Extracto no esta Contabilizada. No se puede Descontabilizar.");
		}
	}

	public void unrecordBankStatement(Session session, BankStatement statement, boolean hasLinks) throws ManagerBeanException {
		getWriter().removeAccountEntryBankStatement(statement, hasLinks);

		statement.setStatus((hasLinks) ? StatementStatus.CHECKED : StatementStatus.PENDING);
		if (session != null) {
			statement = (BankStatement)session.merge(statement);
		}
		getManagerBean().update(statement);
	}

	public AccountEntry getAccountEntry() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			BankStatement to = (BankStatement)getModel().getRowData();
			IManagerBean accEntryStatementBean = BeanManager.getManagerBean(AccountEntryBankStatement.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accEntryStatementBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_BANK_STATEMENT_BANK_STATEMENT_ID), to.getId());
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
		criteria.addEqualExpression(accEntryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accEntry.getId());
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

	public void onLoad(ActionEvent event, BankStatement statement, String backAction, String backActionListener) throws ManagerBeanException {
		onEditSearch(null);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_ID), statement.getId());
		criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_REGISTRY_BANK_ID), statement.getRegistryBank().getId());
		setCriteria(criteria);

		setRegistryBank(statement.getRegistryBank());
		BankStatementSearchListener statementSearch = (BankStatementSearchListener)AonUtil.getRegisteredBean(BANK_STATEMENT_SEARCH_LISTENER_NAME);
		statementSearch.initData();
		onSearch(null);

		setBackAction(backAction);
		setBackActionListener(backActionListener);
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
	
	public int getCheckedBankStatementSize() {
		return getCheckedBankStatement().size();
	}
	
	public void clearCheckedBankStatement() {
		bankStatementChecks = new ArrayList<BankStatement>();
	}
	
	public String onExcelReport() {
		HttpServletResponse response = null;
		OutputStream out = null;
		try {
			Locale locale = AonUtil.getCurrentLocale();
			String filename = "ExtractoBancario";
			response = DownloadUtil.getResponse();
			out = DownloadUtil.initDownload(response, filename, MimeType.MIME_MS_EXCEL);
			ExcelReportExporter exporter = new ExcelReportExporter();
			exporter.startExport(filename);
			
			HSSFFont font = exporter.createFont();
			font.setFontHeightInPoints((short) 8);

			HSSFFont boldFont = exporter.createFont();
			boldFont.setFontHeightInPoints((short) 8);
			boldFont.setBold(true);

			HSSFCellStyle headerCellStyle = exporter.createCellStyle();
		    headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
		    headerCellStyle.setAlignment(HorizontalAlignment.CENTER );
		    headerCellStyle.setFont(boldFont);
		    
			exporter.addHeaderCell("Banco", exporter.getWidth(60), headerCellStyle);
			exporter.addHeaderCell("Lote", exporter.getWidth(5), headerCellStyle);
			exporter.addHeaderCell("Fecha", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Operación", exporter.getWidth(20), headerCellStyle);
			exporter.addHeaderCell("Concepto", exporter.getWidth(50), headerCellStyle);
			exporter.addHeaderCell("Tipo", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Importe", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Estado", exporter.getWidth(12), headerCellStyle);
			exporter.addHeaderCell("Comentarios", exporter.getWidth(250), headerCellStyle);
			
			
			HSSFCellStyle defaultStyle = exporter.createCellStyle();
			defaultStyle.setFont(font);

			HSSFCellStyle dateStyle = exporter.createCellStyle();
			dateStyle.setDataFormat( exporter.getDataFormat().getFormat(ExcelReportExporter.DATE_PATTERN));
			dateStyle.setFont(font);

			HSSFCellStyle amountStyle = exporter.createCellStyle();
			amountStyle.setFont(font);
			amountStyle.setDataFormat( exporter.getDataFormat().getFormat(ExcelReportExporter.DECIMAL_PATTERN));
			
			List<ITransferObject> list = search(0, getRowCount());
			for ( ITransferObject to : list ) {
				BankStatement bs = (BankStatement) to;
				exporter.startLine();
				String bank = bs.getRegistryBank() != null?bs.getRegistryBank().getFullName():""; 
				exporter.addStringCell( bank , defaultStyle );
				exporter.addNumberCell( bs.getLotNumber() , defaultStyle );
				exporter.addDateCell( bs.getOperationDate() , dateStyle );
				String commonConcept = bs.getCommonConcept() != null?bs.getCommonConcept().getName(locale):"";
				exporter.addStringCell( commonConcept , defaultStyle );
				exporter.addStringCell( bs.getDescription() , defaultStyle );
				String paymentLabel = (bs.isPayment())?"Cargo":"Abono";
				exporter.addStringCell( paymentLabel , defaultStyle );
				exporter.addDecimalCell( bs.getAmount(),amountStyle );
				String status = bs.getStatus() != null?bs.getStatus().getName(locale):"";
				exporter.addStringCell( status , defaultStyle );
				exporter.addStringCell( bs.getComments() , defaultStyle );
				exporter.endLine();
			}
			exporter.endExport(out);
			
		} catch (ReportException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (IOException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DownloadUtil.finishDownload(response, out);
		}
		return null;
	}

}
