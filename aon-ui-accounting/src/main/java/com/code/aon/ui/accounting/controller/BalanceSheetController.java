package com.code.aon.ui.accounting.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.Period;
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
	private String type;
	private String balanceName;
	private Integer actualYear;
	private Integer previousYear;
	private SummaryProviderParameters previousParameters;
	private  Boolean flagAccounts;
	


	public void onReset(ActionEvent event) {
		parameters = new SummaryProviderParameters();
		parameters.setPeriod(null);
		parameters.setFromDate(null);
		parameters.setToDate(null);
		parameters.setDate(new Date());
		parameters.setAccountExpression(null);
		parameters.setLowerLevelVisible(false);
		parameters.setNoTouchedAccountVisible(false);
		parameters.setRowsPerPage(20);
		parameters.setAccountLevel(4);
		parameters.setBudgeted(false);	
		setFlagAccounts(false);
	}
	
	public void onBalance(ActionEvent event) {	
		
		try {
			getBalanceCollection(Integer.valueOf(type));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	private void getBalanceCollection(Integer type) throws ManagerBeanException {
				
		Calendar firstDay = new GregorianCalendar();
		firstDay.setTime(parameters.getPeriod().getInitiationDate());
		firstDay.set(Calendar.YEAR,firstDay.get(Calendar.YEAR)-1);
	    
		Calendar lastDay = new GregorianCalendar();
		lastDay.setTime(parameters.getPeriod().getDeadline());
		lastDay.set(Calendar.YEAR,lastDay.get(Calendar.YEAR)-1);
		
		Integer year= Integer.valueOf(parameters.getPeriod().getId());
		setActualYear(year);
		year--;		
		setPreviousYear(year);
		String anyo = year.toString();
		Period p= new Period();
		p.setDeadline(lastDay.getTime());
		p.setInitiationDate(firstDay.getTime());
		p.setId(anyo);
		
		previousParameters= new SummaryProviderParameters();
		previousParameters.setPeriod(p);
		previousParameters.setFromDate(parameters.getFromDate());
		previousParameters.setToDate(parameters.getToDate());
		previousParameters.setDate(parameters.getDate());
		previousParameters.setAccountExpression(parameters.getAccountExpression());
		previousParameters.setLowerLevelVisible(parameters.isLowerLevelVisible());
		previousParameters.setNoTouchedAccountVisible(parameters.isNoTouchedAccountVisible());
		previousParameters.setRowsPerPage(20);
		previousParameters.setAccountLevel(4);
		previousParameters.setBudgeted(parameters.isBudgeted());
			
		
		
		balanceList.clear();
		IManagerBean bean;
		bean = BeanManager.getManagerBean(BalanceDetail.class);
		String balanceDetailId = bean.getFieldName(IAccountingAlias.BALANCE_DETAIL_BALANCE_ID);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(balanceDetailId, type);
		balanceDetailList = bean.getList(criteria);
		     
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
					parameters.setAccountExpression(line);
					b.setAmount(getAccountsAmount(parameters, bd.isCreditNature()));
					previousParameters.setAccountExpression(line);					
				 	b.setPreviousAmount(getAccountsAmount(previousParameters, bd.isCreditNature()));
				}

			}

			if (bd.getAccounts() != null && bd.isInternalCalculation()) {// Calcula el String con las cuentas de un balanceDetail que esta compesto por otros(UN TOTAL)
				String line = new String();
				String accounts = bd.getAccounts();// linea de total que proviene de la BD
				String[] data = new String[30];
				StringTokenizer tokenizer = new StringTokenizer(accounts, ",");// Trocea el total para averiguar que cuantas forman cada parte
				int i = 0;
				while (tokenizer.hasMoreTokens()) {
					data[i] = tokenizer.nextToken();
					++i;
				}

				// recorrer la lista "balanceList" para encontrar el code y asi aprovechar sus accounts
				Iterator<BalanceItem> li = balanceList.iterator();
				while (li.hasNext()) {
					BalanceItem balItem = li.next();
					String code = balItem.getCode();
					String account = balItem.getAccounts();

					int j = 0;
					while (data[j] != null) {
						if (code.equals(data[j])) {
							line = line + "|" + account;// si ya lo tenemos,metemos las cuentas en line
						}
						j++;
					}
				}

				line = line.substring(1, line.length());
				parameters.setAccountExpression(line);
				b.setAmount(getAccountsAmount(parameters, bd.isCreditNature()));
				
				previousParameters.setAccountExpression(line);				
				b.setPreviousAmount(getAccountsAmount(previousParameters, bd.isCreditNature()));
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
	
	
	public Double getAccountsAmount(SummaryProviderParameters params, Boolean creditNature) {
		
		summaryCollection = new SummaryCollection();
		summaryProvider = new SummaryProvider();
				
		try {
			summaryCollection = summaryProvider.getSummaryCollection(params);;
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
		private Double previousAmount;
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

		public Double getPreviousAmount() {
			return previousAmount;
		}

		public void setPreviousAmount(Double amount2) {
			this.previousAmount = amount2;
		}

		public int getLevel() {
			return getCode() == null?0:StringUtils.countMatches(getCode(), ".");
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public SummaryProviderParameters getParameters() {
	
		return parameters;
	}

	public void setParameters(SummaryProviderParameters parameters) {
		this.parameters = parameters;
	}

	
	public SummaryProviderParameters getPreviousParameters() {
		return previousParameters;
	}
	public void setPreviousParameters(SummaryProviderParameters previousParameters) {
		this.previousParameters = previousParameters;
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

	public Integer getActualYear() {
		return actualYear;
	}

	public void setActualYear(Integer actualYear) {
		this.actualYear = actualYear;
	}

	public Integer getPreviousYear() {
		return previousYear;
	}

	public void setPreviousYear(Integer previousYear) {
		this.previousYear = previousYear;
	}
	
	public String getBalanceName() {
		return balanceName;
	}
	public void setBalanceName(String balanceName) {
		this.balanceName = balanceName;
	}
	

	public Boolean getFlagAccounts() {
		return flagAccounts;
	}

	public void setFlagAccounts(Boolean flagAccounts) {
		this.flagAccounts = flagAccounts;
	}


	
}

