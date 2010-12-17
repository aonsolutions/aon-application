package com.code.aon.ui.finance.controller;

import java.util.Date;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.BankConcept;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.BankStatementLink;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.StatementConcept;
import com.code.aon.finance.enumeration.StatementLinkSource;
import com.code.aon.finance.enumeration.StatementLinkStatus;
import com.code.aon.finance.enumeration.StatementReliability;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class BankStatementLinkManager implements IFinanceConstants {

	private BankStatement currentStatement;
	private String selectedTab;

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
	}

	public boolean isBatchSource() {
		return (currentStatement != null && currentStatement.getCommonConcept() == StatementConcept.COLLECTION_BATCH);
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

	public void onEditSearch(ActionEvent event) throws ManagerBeanException {
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

	public void onSearch(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.onSearch(event);

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		trackingList.onSearch(event);
	}

	public void onAddLinks(ActionEvent event) throws ManagerBeanException {
		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		for (Finance finance : financeList.getCheckedFinances()) {
			addLink(getCurrentStatement(), finance);
		}
		if (financeList.getCheckedFinances().size() > 0) {
			financeList.clearCheckedFinances();
			financeList.onSearch(null);
			checkBankStatement(getCurrentStatement());
		}

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		for (FinanceTracking tracking : trackingList.getCheckedTrackings()) {
			addLink(getCurrentStatement(), tracking);
		}
		if (trackingList.getCheckedTrackings().size() > 0) {
			trackingList.clearCheckedTrackings();
			trackingList.onSearch(null);
			checkBankStatement(getCurrentStatement());
		}

		BankStatementLinkController statementLinkList = (BankStatementLinkController)FormUtil.getController(BANK_STATEMENT_LINK_CONTROLLER_NAME);
		statementLinkList.onSearch(null);
	}

	private void addLink(BankStatement statement, Finance finance) throws ManagerBeanException {
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
		statementLink = (BankStatementLink)statementLinkBean.insert(statementLink);

		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		finance.setFinanceStatus((statement.getCommonConcept() != StatementConcept.RETURNED) ? FinanceStatus.PAID : FinanceStatus.RETURNED);
		financeBean.update(finance);

		String message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_PENDING);
		FinanceTrackingType type = (finance.getFinanceStatus() == FinanceStatus.PAID) ? FinanceTrackingType.PAID : FinanceTrackingType.RETURNED;
		FinanceTracking tracking = FinanceTrackingWriter.addFinanceTracking(finance, statement.getOperationDate(), type, message, 
									statement.getRegistryBank(), null, finance.getTotalAmount(), false, statementLink);

		statementLink.setSourceId(tracking.getId());
		statementLinkBean.update(statementLink);
	}

	private void addLink(BankStatement statement, FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		BankStatementLink statementLink = new BankStatementLink();
		statementLink.setBankStatement(statement);
		statementLink.setSource(StatementLinkSource.FINANCE_TRACKING);
		statementLink.setSourceId(tracking.getId());
		statementLink.setSourceDate(tracking.getTrackingDate());
		statementLink.setAmount(tracking.getAmount());
		statementLink.setStatus((statement.getCommonConcept() != StatementConcept.RETURNED) ? StatementLinkStatus.PAID : StatementLinkStatus.RETURNED);
		statementLink = (BankStatementLink)statementLinkBean.insert(statementLink);

		IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		tracking.setTrackingDate(statement.getOperationDate());
		tracking.setBankStatementLink(statementLink);
		trackingBean.update(tracking);
	}

	public void addLink(BankStatement statement, BankConcept concept) throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		BankStatementLink statementLink = new BankStatementLink();
		statementLink.setBankStatement(statement);
		statementLink.setSource(StatementLinkSource.BANK_CONCEPT);
		statementLink.setSourceId(concept.getId());
		statementLink.setSourceDate(statement.getOperationDate());
		statementLink.setAmount(statement.getAmount());
		statementLink.setStatus(StatementLinkStatus.PENDING);
		statementLinkBean.insert(statementLink);
	}

	public void addLink(BankStatement statement, Account account) throws ManagerBeanException {
		IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
		BankStatementLink statementLink = new BankStatementLink();
		statementLink.setBankStatement(statement);
		statementLink.setSource(StatementLinkSource.ACCOUNT);
		statementLink.setSourceId(Integer.parseInt(account.getId()));
		statementLink.setSourceDate(statement.getOperationDate());
		statementLink.setAmount(statement.getAmount());
		statementLink.setStatus(StatementLinkStatus.PENDING);
		statementLinkBean.insert(statementLink);
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
			}
		}

		FinanceListController financeList = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
		financeList.onSearch(null);

		FinanceTrackingListController trackingList = (FinanceTrackingListController)FormUtil.getController(FINANCE_TRACKING_LIST_CONTROLLER_NAME);
		trackingList.onSearch(null);
	}

	public void removeLink(BankStatementLink statementLink) throws ManagerBeanException {
		if (statementLink.getSource() == StatementLinkSource.FINANCE_TRACKING) {
			removeLink(statementLink, (FinanceTracking)statementLink.getSourceTo());
		} else if (statementLink.getSource() == StatementLinkSource.FINANCE_BATCH) {
			
		}

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
			if (statementLink.getStatus() == StatementLinkStatus.PENDING) {
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

	public void checkBankStatement(BankStatement statement) throws ManagerBeanException {
		IManagerBean statementBean = BeanManager.getManagerBean(BankStatement.class);
		statement.setReliability(StatementReliability.VERY_HIGH);
		statement.setStatus(StatementStatus.CHECKED);
		statementBean.update(statement);
	}

	public void unCheckBankStatement(BankStatement statement) throws ManagerBeanException {
		IManagerBean statementBean = BeanManager.getManagerBean(BankStatement.class);
		statement.setReliability(StatementReliability.VERY_HIGH);
		statement.setStatus(StatementStatus.PENDING);
		statementBean.update(statement);
	}

}
