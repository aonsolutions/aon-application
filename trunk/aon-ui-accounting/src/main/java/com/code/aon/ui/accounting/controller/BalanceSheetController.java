package com.code.aon.ui.accounting.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;

public class BalanceSheetController implements ICollectionProvider{

	private DataModel balanceModel;
	private List<ITransferObject> balanceDetailList;
	private List<BalanceItem> balanceList = new LinkedList<BalanceItem>();
	private SummaryProviderParameters parameters;
	private SummaryProvider summaryProvider;
	private SummaryCollection summaryCollection;
	private Date fromDate;
	private Date toDate;

	public void onReset(ActionEvent event) {

		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		setFromDate(c.getTime());
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DAY_OF_MONTH, 31);
		setToDate(c.getTime());
	}

	public void onBalanceSheet(ActionEvent event) {
		try {
			getBalanceCollection(1);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onBasicBalanceSheet(ActionEvent event) {

	}

	public void onProfitLossAccount(ActionEvent event) {
		try {
			getBalanceCollection(2);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	public void onBasicProfitLossAccount(ActionEvent event) {

	}

	private void getBalanceCollection(Integer balanceId) throws ManagerBeanException {
		balanceList.clear();
		IManagerBean bean;
		bean = BeanManager.getManagerBean(BalanceDetail.class);
		String balance = bean.getFieldName(IAccountingAlias.BALANCE_DETAIL_BALANCE_ID);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(balance, balanceId);
		balanceDetailList = bean.getList(criteria);
		balanceModel = null;

		for (ITransferObject to : balanceDetailList) {

			BalanceDetail bd = (BalanceDetail) to;
			BalanceItem b = new BalanceItem();
			b.setCode(bd.getCode());
			if (bd.getDescription() != null) {
				b.setDescription(bd.getDescription().trim());
			}

			if (bd.getAccounts() != null && bd.isInternalCalculation() == false) {
				// Calcula el String con las  cuentas  de un balanceDetail
				String accounts = bd.getAccounts();
				String line = new String();
				StringTokenizer tokenizer = new StringTokenizer(accounts, ",");
				while (tokenizer.hasMoreTokens()) {
					line = line + tokenizer.nextToken() + "*|";
				}
				if (line.length() >= 2) {
					line = line.substring(0, line.length() - 1);
					b.setAccounts(line);
					b.setAmount(getAccountsAmount(line, bd.isCreditNature()));
				}

			}

			if (bd.getAccounts() != null && bd.isInternalCalculation()) {// Calcula
				// el String con las cuentas de un balanceDetail que esta compesto por otros(UN TOTAL)
				String line = new String();
				String accounts = bd.getAccounts();// linea de total que
				// proviene de la BD
				String[] data = new String[30];
				StringTokenizer tokenizer = new StringTokenizer(accounts, ",");// Trocea
				// el total para averiguar que cuantas forman cada parte
				int i = 0;
				while (tokenizer.hasMoreTokens()) {
					data[i] = tokenizer.nextToken();
					++i;
				}

				// recorrer la lista "balanceList" para encontrar el code y asi
				// aprovechar sus accounts
				Iterator<BalanceItem> li = balanceList.iterator();
				while (li.hasNext()) {
					BalanceItem balItem = li.next();
					String code = balItem.getCode();
					String account = balItem.getAccounts();

					int j = 0;
					while (data[j] != null) {
						if (code.equals(data[j])) {
							line = line + "|" + account;// si ya lo tenemos,
							// metemos las cuentas
							// en line
						}
						j++;
					}
				}

				line = line.substring(1, line.length());
				b.setAmount(getAccountsAmount(line, bd.isCreditNature()));
				b.setAccounts(line);
			}
			b.setVisible(true);
			if (bd.isZeroFlag() || (bd.isZeroFlag() && b.getAmount() != 0 )) {
				b.setVisible(false);
			}
			if (!bd.isVisible()) {
				b.setVisible(false);
			}
			b.setTitle(bd.isTitle());
			balanceList.add(b);
		}

		balanceModel = null;
	}

	public Double getAccountsAmount(String line, Boolean creditNature) {
		parameters = new SummaryProviderParameters();
		summaryCollection = new SummaryCollection();
		summaryProvider = new SummaryProvider();
		parameters.setAccountExpression(line);
		try {
			summaryCollection = summaryProvider.getSummaryCollection(parameters);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Double amount;
		if (creditNature) {
			amount = CommonUtil.round( summaryCollection.getCredit() -  summaryCollection.getDebit() ); 
		} else {
			amount = CommonUtil.round( summaryCollection.getDebit() -  summaryCollection.getCredit() ); 
		}
		return amount;

	}

	public List<BalanceItem> getBalanceList() {
		return balanceList;
	}

	public void setBalanceList(List<BalanceItem> balanceList) {
		this.balanceList = balanceList;
	}

	public DataModel getBalanceModel() {
		if (balanceModel == null) {
			balanceModel = new ListDataModel(getBalanceList());
		}
		return balanceModel;
	}

	public void setBalanceModel(DataModel balanceModel) {
		this.balanceModel = balanceModel;
	}

	public List<ITransferObject> getBalanceDetailList() {
		return balanceDetailList;
	}

	public void setBalanceDetailList(List<ITransferObject> balanceList) {
		this.balanceDetailList = balanceList;
	}

	public class BalanceItem {

		private String code;
		private String description;
		private String notes;
		private String accounts;
		private Double amount;
		private Double amount2;
		private boolean visible;
		private boolean title;


		public boolean isTitle() {
			return title;
		}

		public void setTitle(boolean title) {
			this.title = title;
		}

		public boolean isVisible() {
			return visible;
		}

		public void setVisible(boolean visible) {
			this.visible = visible;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getAccounts() {
			return accounts;
		}

		public void setAccounts(String accounts) {
			this.accounts = accounts;
		}

		public String getDescription() {
			return description;
		}

		public String getReportDescription() {
			return StringUtils.leftPad(description, (getLevel() * 5)+description.length());
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}

		public Double getAmount() {
			return amount;
		}

		public void setAmount(Double amount) {
			this.amount = amount;
		}

		public Double getAmount2() {
			return amount2;
		}

		public void setAmount2(Double amount2) {
			this.amount2 = amount2;
		}

		public int getLevel() {
			return getCode() == null?0:StringUtils.countMatches(getCode(), ".");
		}
	}

	public SummaryProviderParameters getParameters() {
		if (parameters == null) {
			SummaryProviderParameters p = new SummaryProviderParameters();
			p.setPeriod(null);
			p.setFromDate(getFromDate());
			p.setToDate(getToDate());
			p.setDate(new Date());
			p.setAccountExpression(null);
			p.setLowerLevelVisible(false);
			p.setNoTouchedAccountVisible(false);
			p.setRowsPerPage(20);
			p.setAccountLevel(4);
			p.setBudgeted(false);
			setParameters(p);
		}
		return parameters;
	}

	public void setParameters(SummaryProviderParameters parameters) {
		this.parameters = parameters;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
		return (List<BalanceItem>) getBalanceModel().getWrappedData();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
}
