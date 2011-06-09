package com.code.aon.ui.finance.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.BankConceptAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.BankConcept;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.BankStatementLink;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.StatementConcept;
import com.code.aon.finance.enumeration.StatementLinkSource;
import com.code.aon.finance.enumeration.StatementLinkStatus;
import com.code.aon.finance.enumeration.StatementReliability;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class BankStatementLinkManager implements IFinanceConstants {

	private final String STATEMENT_LINK_TAB = "statementLinkTab";
	private final String FINANCE_LINK_TAB = "financeLinkTab";
	private final String FBATCH_LINK_TAB = "fbatchLinkTab";
	private final String OTHER_CONCEPT_LINK_TAB = "otherConceptsLinkTab";
	private final String ENTRY_DETAIL_LINK_TAB = "entryDetailLinkTab";
	private final String LINK_TRANSFER_LINK_TAB = "linkTransferLinkTab";
	private final String COMMENTS_LINK_TAB = "commentsLinkTab";
	
	private BankStatement currentStatement;
	private String selectedTab;
	private Finance fractionFinance;
	private BankConcept bankConcept;
	private Account account;
	private Double amount;
	private String comments;
	private List<ITransferObject> entryDetailList;
	private DataModel entryDetailModel;
	private List<ITransferObject> linkTransferList;
	private DataModel linkTransferModel;

	public BankStatement getCurrentStatement() {
		return currentStatement;
	}
	public void setCurrentStatement(BankStatement currentStatement) {
		this.currentStatement = currentStatement;
	}

	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
		if (selectedTab.equals(OTHER_CONCEPT_LINK_TAB)) {
			setAmountPending();
		}
	}

	public Finance getFractionFinance() {
		return fractionFinance;
	}
	public void setFractionFinance(Finance finance) {
		this.fractionFinance = finance;
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

	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<ITransferObject> getEntryDetailList() {
		return entryDetailList;
	}
	public void setEntryDetailList(List<ITransferObject> entryDetailList) {
		this.entryDetailList = entryDetailList;
	}

	public DataModel getEntryDetailModel() {
		if (entryDetailModel == null) {
			entryDetailModel = new ListDataModel(entryDetailList);
		}
		return entryDetailModel;
	}
	public void setEntryDetailModel(DataModel model) {
		this.entryDetailModel = model;
	}

	public List<ITransferObject> getLinkTransferList() {
		return linkTransferList;
	}
	public void setLinkTransferList(List<ITransferObject> linkTransferList) {
		this.linkTransferList = linkTransferList;
	}

	public DataModel getLinkTransferModel() {
		if (linkTransferModel == null) {
			linkTransferModel = new ListDataModel(linkTransferList);
		}
		return linkTransferModel;
	}
	public void setLinkTransferModel(DataModel model) {
		this.linkTransferModel = model;
	}

	public boolean isReturnSource() {
		return (currentStatement != null && currentStatement.isReturned());
	}

	public boolean isTrackingLinked() throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), getCurrentStatement().getId());
		criteria.addEqualExpression(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_SOURCE), StatementLinkSource.FINANCE_TRACKING);
		return (statementLinkBean.getCount(criteria) > 0);
	}

	public boolean isBatchSource() {
		return (currentStatement != null && currentStatement.isCollectionBatch());
	}

	public boolean isBatchLinked() throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), getCurrentStatement().getId());
		criteria.addEqualExpression(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_SOURCE), StatementLinkSource.FINANCE_BATCH);
		return (statementLinkBean.getCount(criteria) > 0);
	}

	public boolean isTransferLinked() throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), getCurrentStatement().getId());
		criteria.addNotNullExpression(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_LINKED_BANK_STATEMENT_LINK));
		return (statementLinkBean.getCount(criteria) > 0);
	}

	public void setStatementLinkTab() {
		setSelectedTab(STATEMENT_LINK_TAB);
	}

	public void setFinanceLinkTab() {
		setSelectedTab(FINANCE_LINK_TAB);
	}

	public void setFBatchLinkTab() {
		setSelectedTab(FBATCH_LINK_TAB);
	}

	public void setOtherConceptLinkTab() {
		setSelectedTab(OTHER_CONCEPT_LINK_TAB);
	}

	public void setEntryDetailLinkTab() {
		setSelectedTab(ENTRY_DETAIL_LINK_TAB);
	}

	public void setLinkTransferLinkTab() {
		setSelectedTab(LINK_TRANSFER_LINK_TAB);
	}

	public void setCommentsLinkTab() {
		setSelectedTab(COMMENTS_LINK_TAB);
	}

	public double getCheckedAmount() throws ManagerBeanException {
		return getCheckedAmount(getCurrentStatement());
	}

	public double getCheckedAmount(BankStatement statement) throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		Projection projection = Projection.sum(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_AMOUNT));
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), statement.getId());
		Double amount = (Double)statementLinkBean.getUniqueResult(projection, criteria);
		return (amount!=null) ? CommonUtil.round(amount.doubleValue()) : 0;
	}

	public double getPendingAmount() throws ManagerBeanException {
		return CommonUtil.round(getCurrentStatement().getAmount() - getCheckedAmount());
	}

	public void setAmountPending() {
		try {
			this.amount = getPendingAmount();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException();
		}
	}

	public void checkAllUnChecked(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.checkAll(event);

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		trackingList.checkAll(event);
	}

	public void checkNoneUnChecked(ActionEvent event) {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.checkNone(event);

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		trackingList.checkNone(event);
	}

	public void onEditSearchFinance(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.onEditSearch(event);

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		trackingList.onEditSearch(event);
	}

	public void addExpression(ValueChangeEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.addExpression(event);

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		trackingList.setExpression(event);
	}

	public void addGreaterThanOrEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.addGreaterThanOrEqualExpression(event);

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		trackingList.setExpression(event);
	}

	public void addLessThanOrEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.addLessThanOrEqualExpression(event);

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		trackingList.setExpression(event);
	}

	public void addIdEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.addIdEqualExpression(event);

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		trackingList.setExpression(event);
	}

	public void onSearch(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.onSearch(event);

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		trackingList.onSearch(event);
	}

	public void onAddFinanceLinks(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		for (Finance finance : financeList.getCheckedFinances()) {
			addLink(getCurrentStatement(), finance);
		}
		if (financeList.getCheckedFinances().size() > 0) {
			financeList.onSearch(null);
			checkBankStatement(getCurrentStatement());
		}

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		for (FinanceTracking tracking : trackingList.getCheckedTrackings()) {
			addLink(getCurrentStatement(), tracking);
		}
		if (trackingList.getCheckedTrackings().size() > 0) {
			trackingList.onSearch(null);
			checkBankStatement(getCurrentStatement());
		}

		BankStatementLinkController statementLinkList = (BankStatementLinkController)FormUtil.getController(BANK_STATEMENT_LINK_CONTROLLER_NAME);
		statementLinkList.onSearch(null);
		setStatementLinkTab();
	}

	public void addLink(BankStatement statement, Finance finance) throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		BankStatementLink statementLink = new BankStatementLink();
		statementLink.setBankStatement(statement);
		statementLink.setSource(StatementLinkSource.FINANCE_TRACKING);
		statementLink.setSourceId(0);
		statementLink.setSourceDate(statement.getOperationDate());
		statementLink.setAmount(finance.getTotalAmount());
		if (finance.getFinanceStatus() == FinanceStatus.PAID) {
			statementLink.setStatus(StatementLinkStatus.PAID);
		} else if (finance.getFinanceStatus() == FinanceStatus.PENDING) {
			statementLink.setStatus(StatementLinkStatus.PENDING);
		} else if (finance.getFinanceStatus() == FinanceStatus.RETURNED) {
			statementLink.setStatus(StatementLinkStatus.RETURNED);
		} else if (finance.getFinanceStatus() == FinanceStatus.SETTLED) {
			statementLink.setStatus(StatementLinkStatus.SETTLED);

			FinanceTrackingWriter.removeLastTrackingByType(finance, FinanceTrackingType.SETTLED);
		}
		statementLink.setLinkedBankStatementLink(null);
		statementLink = (BankStatementLink)statementLinkBean.insert(statementLink);

		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		finance.setFinanceStatus((statement.getCommonConcept() != StatementConcept.RETURNED) ? FinanceStatus.PAID : FinanceStatus.RETURNED);
		financeBean.update(finance);

		String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_PENDING);
		FinanceTrackingType type = (finance.getFinanceStatus() == FinanceStatus.PAID) ? FinanceTrackingType.PAID : FinanceTrackingType.RETURNED;
		FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, statement.getOperationDate(), type, message, 
									statement.getRegistryBank(), null, finance.getTotalAmount(), false, statementLink);
		if (type == FinanceTrackingType.RETURNED) {
			returnFinanceBatchDetail(finance);
		}

		statementLink.setSourceId(tracking.getId());
		statementLinkBean.update(statementLink);
	}

	private void returnFinanceBatchDetail(Finance finance) throws ManagerBeanException {
		IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_ID), finance.getId());
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.PAID);
		Iterator<?> iterator = fBatchDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			FinanceBatchDetail detail = (FinanceBatchDetail)iterator.next();
			detail.setStatus(FinanceStatus.RETURNED);
			fBatchDetailBean.update(detail);
		}
	}

	public void addLink(BankStatement statement, FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		BankStatementLink statementLink = new BankStatementLink();
		statementLink.setBankStatement(statement);
		statementLink.setSource(StatementLinkSource.FINANCE_TRACKING);
		statementLink.setSourceId(tracking.getId());
		statementLink.setSourceDate(tracking.getTrackingDate());
		statementLink.setAmount(tracking.getAmount());
		statementLink.setStatus((statement.getCommonConcept() != StatementConcept.RETURNED) ? StatementLinkStatus.PAID : StatementLinkStatus.RETURNED);
		statementLink.setLinkedBankStatementLink(null);
		statementLink = (BankStatementLink)statementLinkBean.insert(statementLink);

		IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		tracking.setTrackingDate(statement.getOperationDate());
		tracking.setBankStatementLink(statementLink);
		trackingBean.update(tracking);
	}

	public void onAddBatchLinks(ActionEvent event) throws ManagerBeanException {
		FBatchListController batchList = (FBatchListController)FormUtil.getController(FINANCE_BATCH_LIST_CONTROLLER_NAME);
		for (FinanceBatch batch : batchList.getCheckedBatches()) {
			addLink(getCurrentStatement(), batch);
		}
		if (batchList.getCheckedBatches().size() > 0) {
			batchList.onSearch(null);
			checkBankStatement(getCurrentStatement());
		}

		BankStatementLinkController statementLinkList = (BankStatementLinkController)FormUtil.getController(BANK_STATEMENT_LINK_CONTROLLER_NAME);
		statementLinkList.onSearch(null);
		setStatementLinkTab();
	}

	public void addLink(BankStatement statement, FinanceBatch batch) throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		BankStatementLink statementLink = new BankStatementLink();
		statementLink.setBankStatement(statement);
		statementLink.setSource(StatementLinkSource.FINANCE_BATCH);
		statementLink.setSourceId(batch.getId());
		statementLink.setSourceDate(batch.getIssueDate());
		statementLink.setAmount(batch.getFinanceBatchTotalAmount());
		statementLink.setStatus(StatementLinkStatus.PAID);
		statementLink.setLinkedBankStatementLink(null);
		statementLink = (BankStatementLink)statementLinkBean.insert(statementLink);

		IManagerBean batchBean = BeanManager.getManagerBean(FinanceBatch.class);
		batch.setBankStatementLink(statementLink);
		batch.setLines(null);
		batchBean.update(batch);
	}

	public void onAddTransferLinks(ActionEvent event) throws ManagerBeanException {
		BankStatementLink to = (BankStatementLink)getLinkTransferModel().getRowData();

		BankStatementController statementController = (BankStatementController)FormUtil.getController(BANK_STATEMENT_CONTROLLER_NAME);
		Account bankAccount = statementController.getWriter().obtainPaymentAccount(to.getBankStatement().getRegistryBank(), null);

		BankConcept bankConcept = null;
		if (to.getSource() == StatementLinkSource.BANK_CONCEPT) {
			IManagerBean bankConceptAccBean = BeanManager.getManagerBean(BankConceptAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bankConceptAccBean.getFieldName(IAccountBridgeAlias.BANK_CONCEPT_ACCOUNT_ACCOUNT_ID), bankAccount.getId());
			for (ITransferObject ito : bankConceptAccBean.getList(criteria)) {
				bankConcept = ((BankConceptAccount)ito).getBankConcept();
				break;
			}
		}

		if (bankConcept == null) {
			addLink(getCurrentStatement(), bankAccount, to.getAmount(), to);
		} else {
			addLink(getCurrentStatement(), bankConcept, to.getAmount(), to);
		}
		checkBankStatement(getCurrentStatement());

		BankStatementLinkController statementLinkList = (BankStatementLinkController)FormUtil.getController(BANK_STATEMENT_LINK_CONTROLLER_NAME);
		statementLinkList.onSearch(null);
		setStatementLinkTab();
	}

	public void onAddOtherConceptLinks(ActionEvent event) throws ManagerBeanException {
		if (getAmount() == null) {
			setAmount(0.0);
		}
		if (getAccount() != null && !StringUtils.isEmpty(getAccount().getId())) {
			if (!getAccount().isEntryEnabled()) {
				AonUtil.addErrorMessage("La Cuenta Contable " + getAccount().getId() + " no permite apuntes.");
				throw new AbortProcessingException();
			}

			addLink(getCurrentStatement(), getAccount(), getAmount().doubleValue());
			setAccount(new Account());
		} else {
			if (getBankConcept() == null || getBankConcept().getId() == null) {
				AonUtil.addErrorMessage("Concepto Bancario: Error de Validación: Valor es necesario.");
				throw new AbortProcessingException();
			}
			
			addLink(getCurrentStatement(), getBankConcept(), getAmount().doubleValue());
		}
		checkBankStatement(getCurrentStatement());

		BankStatementLinkController statementLinkList = (BankStatementLinkController)FormUtil.getController(BANK_STATEMENT_LINK_CONTROLLER_NAME);
		statementLinkList.onSearch(null);
		setStatementLinkTab();
	}

	public void addLink(BankStatement statement, BankConcept concept, double amount) throws ManagerBeanException {
		addLink(statement, concept, amount, null);
	}

	public void addLink(BankStatement statement, BankConcept concept, double amount, BankStatementLink link) throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		BankStatementLink statementLink = new BankStatementLink();
		statementLink.setBankStatement(statement);
		statementLink.setSource(StatementLinkSource.BANK_CONCEPT);
		statementLink.setSourceId(concept.getId());
		statementLink.setSourceDate(statement.getOperationDate());
		statementLink.setAmount(amount);
		statementLink.setStatus(StatementLinkStatus.PENDING);
		statementLink.setLinkedBankStatementLink(link);
		statementLink = (BankStatementLink)statementLinkBean.insert(statementLink);

		if (link != null) {
			link.setLinkedBankStatementLink(statementLink);
			link = (BankStatementLink)statementLinkBean.update(link);
		}
	}

	public void addLink(BankStatement statement, Account account, double amount) throws ManagerBeanException {
		addLink(statement, account, amount, null);
	}

	public void addLink(BankStatement statement, Account account, double amount, BankStatementLink link) throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		BankStatementLink statementLink = new BankStatementLink();
		statementLink.setBankStatement(statement);
		statementLink.setSource(StatementLinkSource.ACCOUNT);
		statementLink.setSourceId(Integer.parseInt(account.getId()));
		statementLink.setSourceDate(statement.getOperationDate());
		statementLink.setAmount(amount);
		statementLink.setStatus(StatementLinkStatus.PENDING);
		statementLink.setLinkedBankStatementLink(link);
		statementLink = (BankStatementLink)statementLinkBean.insert(statementLink);

		if (link != null) {
			link.setLinkedBankStatementLink(statementLink);
			link = (BankStatementLink)statementLinkBean.update(link);
		}
	}

	public void onRemoveLinks(ActionEvent event) throws ManagerBeanException {
		BankStatementLinkController statementLinkList = (BankStatementLinkController)FormUtil.getController(BANK_STATEMENT_LINK_CONTROLLER_NAME);
		for (BankStatementLink statementLink : statementLinkList.getCheckedStatementLinks()) {
			removeLink(statementLink);
		}
		if (statementLinkList.getCheckedStatementLinks().size() > 0) {
			statementLinkList.clearCheckedStatementLinks();
			statementLinkList.onSearch(null);
			if (statementLinkList.getRowCount() == 0) {
				unCheckBankStatement(getCurrentStatement());
			} else {
				checkBankStatement(getCurrentStatement());
			}
		}

		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.onSearch(null);
		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		trackingList.onSearch(null);
		FBatchListController batchList = (FBatchListController)FormUtil.getController(FINANCE_BATCH_LIST_CONTROLLER_NAME);
		batchList.onSearch(null);
	}

	public void removeLink(BankStatementLink statementLink) throws ManagerBeanException {
		if (statementLink.getSource() == StatementLinkSource.FINANCE_TRACKING) {
			removeLink(statementLink, (FinanceTracking)statementLink.getSourceTo());
		} else if (statementLink.getSource() == StatementLinkSource.FINANCE_BATCH) {
			removeLink(statementLink, (FinanceBatch)statementLink.getSourceTo());
		}

		cancelLinkedBankStatementLinks(statementLink.getId());
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		statementLinkBean.remove(statementLink);
	}

	private void removeLink(BankStatementLink statementLink, FinanceTracking tracking) throws ManagerBeanException {
		BankStatement statement = statementLink.getBankStatement();

		IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		if (!FinanceTrackingWriter.isLastTracking(tracking) || 
			(statement.getCommonConcept() != StatementConcept.RETURNED && statementLink.getStatus() == StatementLinkStatus.PAID) ||
			(statement.getCommonConcept() == StatementConcept.RETURNED && statementLink.getStatus() == StatementLinkStatus.RETURNED)) {
			tracking.setTrackingDate(statementLink.getSourceDate());
			tracking.setBankStatementLink(null);
			trackingBean.update(tracking);
		} else {
			trackingBean.remove(tracking);

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			if (statementLink.getStatus() == StatementLinkStatus.PAID) {
				tracking.getFinance().setFinanceStatus(FinanceStatus.PAID);

				updateFinanceBatchDetailStatus(tracking);
			} else if (statementLink.getStatus() == StatementLinkStatus.PENDING) {
				tracking.getFinance().setFinanceStatus(FinanceStatus.PENDING);
			} else if (statementLink.getStatus() == StatementLinkStatus.RETURNED) {
				tracking.getFinance().setFinanceStatus(FinanceStatus.RETURNED);
			} else if (statementLink.getStatus() == StatementLinkStatus.SETTLED) {
				tracking.getFinance().setFinanceStatus(FinanceStatus.SETTLED);

				String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_SETTLED);
				FinanceTrackingWriter.addFinanceTracking(tracking.getFinance(), new Date(), FinanceTrackingType.SETTLED, message);
			}
			financeBean.update(tracking.getFinance());
		}
	}

	private void updateFinanceBatchDetailStatus(FinanceTracking tracking) throws ManagerBeanException {
		Finance finance = tracking.getFinance();
		IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
		criteria.addLessThanExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_ID), tracking.getId());
		criteria.addOrder(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_ID), false);
		Iterator<?> iterator = trackingBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			iterator.next();
			if (iterator.hasNext()) {
				FinanceTracking batchedTracking = (FinanceTracking)iterator.next();
				if (batchedTracking.isBatched()) {
					IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
					criteria = new Criteria();
					criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_ID), finance.getId());
					criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.RETURNED);
					criteria.addOrder(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_ID), false);
					for (ITransferObject ito : fBatchDetailBean.getList(criteria)) {
						FinanceBatchDetail detail = (FinanceBatchDetail)ito;
						detail.setStatus(FinanceStatus.PAID);
						fBatchDetailBean.update(detail);
						return;
					}
				}
			}
		}
	}

	private void removeLink(BankStatementLink statementLink, FinanceBatch batch) throws ManagerBeanException {
		IManagerBean batchBean = BeanManager.getManagerBean(FinanceBatch.class);
		batch.setBankStatementLink(null);
		batch.setLines(null);
		batchBean.update(batch);
	}

	private void cancelLinkedBankStatementLinks(int sourceId) throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(statementLinkBean.getFieldName(IFinanceAlias.BANK_STATEMENT_LINK_LINKED_BANK_STATEMENT_LINK_ID), sourceId);
		for (ITransferObject ito : statementLinkBean.getList(criteria)) {
			BankStatementLink statementLink = (BankStatementLink)ito;
			statementLink.setLinkedBankStatementLink(null);
			statementLinkBean.update(statementLink);
		}
	}

	public void checkBankStatement(BankStatement statement) throws ManagerBeanException {
		StatementReliability reliability = StatementReliability.LOW;
		double checkedAmount = getCheckedAmount();
		if (statement.getAmount() == checkedAmount) {
			reliability = StatementReliability.VERY_HIGH;
		} else {
			double fromAmount = CommonUtil.round(statement.getAmount() * 0.75);
			double toAmount = CommonUtil.round(statement.getAmount() * 1.25);
			if (fromAmount <= checkedAmount && toAmount >= checkedAmount) {
				reliability = StatementReliability.HIGH;
			} else {
				fromAmount = CommonUtil.round(statement.getAmount() * 0.50);
				toAmount = CommonUtil.round(statement.getAmount() * 1.50);
				if (fromAmount <= checkedAmount && toAmount >= checkedAmount) {
					reliability = StatementReliability.MEDIUM;
				}
			}
		}
		checkBankStatement(statement, reliability);
	}

	public void checkBankStatement(BankStatement statement, StatementReliability reliability) throws ManagerBeanException {
		IManagerBean statementBean = BeanManager.getManagerBean(BankStatement.class);
		statement.setReliability(reliability);
		statement.setStatus(StatementStatus.CHECKED);
		statementBean.update(statement);
	}

	public void unCheckBankStatement(BankStatement statement) throws ManagerBeanException {
		IManagerBean statementBean = BeanManager.getManagerBean(BankStatement.class);
		statement.setReliability(StatementReliability.VERY_HIGH);
		statement.setStatus(StatementStatus.PENDING);
		statementBean.update(statement);
	}

	public void onFractionShow(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		setFractionFinance((Finance)financeList.getModel().getRowData());
		setAmountPending();
	}

	public void onFraction(ActionEvent event) throws ManagerBeanException {
		if (getAmount() == 0) {
			AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.PAYMENT_INVALID_AMOUNT_ERROR);
			throw new AbortProcessingException();
		}
		if (getAmount().doubleValue() != getFractionFinance().getTotalAmount()) {
			double amount = getFractionFinance().getTotalAmount();

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			getFractionFinance().setAmount(CommonUtil.round(getAmount().doubleValue() - getFractionFinance().getExpenses()));
			financeBean.update(getFractionFinance());
			String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_FRACTIONED, 1, 2);
			FinanceTrackingWriter.addFinanceTracking(getFractionFinance(), new Date(), FinanceTrackingType.FRACTIONED, message, amount);

			FinanceGenerator financeGenerator = new FinanceGenerator();
			Finance fraction = financeGenerator.duplicateFinance(getFractionFinance(), CommonUtil.round(amount - getAmount().doubleValue()));
			message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_TRACKING_FRACTIONED, 2, 2);
			FinanceTrackingWriter.addFinanceTracking(fraction, new Date(), FinanceTrackingType.FRACTIONED, message, amount);

			AonUtil.addWarningMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.PAYMENT_NOT_MATCH_AMOUNT_ERROR);
		}
	}

	public void onBindEntryDetail(ActionEvent event) throws ManagerBeanException {
		BankStatementLinkController statementLinkList = (BankStatementLinkController)FormUtil.getController(BANK_STATEMENT_LINK_CONTROLLER_NAME);
		for (ITransferObject ito : statementLinkList.getManagerBean().getList(statementLinkList.getCriteria())) {
			BankStatementLink statementLink = (BankStatementLink)ito;
			removeLink(statementLink);
		}

		AccountEntryDetail to = (AccountEntryDetail)getEntryDetailModel().getRowData();
		BankStatementController statementController = (BankStatementController)FormUtil.getController(BANK_STATEMENT_CONTROLLER_NAME);
		statementController.recordBankStatement(to.getAccountEntry(), getCurrentStatement());
	}

	public void onSaveComments(ActionEvent event) throws ManagerBeanException {
		IManagerBean statementBean = BeanManager.getManagerBean(BankStatement.class);
		getCurrentStatement().setComments(getComments());
		statementBean.update(getCurrentStatement());
	}

}