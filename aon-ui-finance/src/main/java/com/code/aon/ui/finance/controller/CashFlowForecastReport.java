package com.code.aon.ui.finance.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.CashFlowForecast;
import com.code.aon.finance.Finance;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;


public class CashFlowForecastReport {
	
	private Date fromDate;
	private Date toDate;
	private boolean returnedFinanceIncluded;
	private boolean pendingFinanceIncluded;
	private DataModel bankModel;
	private List<CashFlowBank> bankList;
	private List<CashFlowBank> banks;
	private DataModel model;
	private double total;
	
	public String getBeanName() {
		return "cashFlowForecastReport";
	}
	
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public boolean isReturnedFinanceIncluded() {
		return returnedFinanceIncluded;
	}
	public void setReturnedFinanceIncluded(boolean returnedFinanceIncluded) {
		this.returnedFinanceIncluded = returnedFinanceIncluded;
	}
	
	public boolean isPendingFinanceIncluded() {
		return pendingFinanceIncluded;
	}
	public void setPendingFinanceIncluded(boolean pendingFinanceIncluded) {
		this.pendingFinanceIncluded = pendingFinanceIncluded;
	}

	public DataModel getBankModel() {
		return bankModel;
	}
	public void setBankModel(DataModel bankModel) {
		this.bankModel = bankModel;
	}

	public List<CashFlowBank> getBankList() {
		return bankList;
	}
	public void setBankList(List<CashFlowBank> bankList) {
		this.bankList = bankList;
	}
	
	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public List<CashFlowBank> getBanks() {
		if (banks == null) {
			banks = new LinkedList<CashFlowBank>();
			CashFlowBank sa = new CashFlowBank();
			sa.setId(Integer.MIN_VALUE);
			sa.setDescription("SIN ASIGNAR");
			sa.setEnabled(true);
			sa.setBalance(0);
			banks.add(sa);
			for (CashFlowBank bank: getBankList() ) {
				if (bank.isEnabled()) {
					banks.add(bank);					
				}
			}
		}
		return banks;
	}
	public void setBanks(List<CashFlowBank> banks) {
		this.banks = banks;
	}

	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}

	public void onEditSearch(ActionEvent event) {
		try {
			setFromDate( new Date() ); 
			setToDate(CommonUtil.getMonthLastDay(getFromDate()));
			setReturnedFinanceIncluded(false);
			setPendingFinanceIncluded(true);
			setBankModel(null);
			setBankList(null);
			setBanks(null);
			setModel(null);
			setTotal(0.0);
			initializeBankList();
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar bancos.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	private void initializeBankList() throws ManagerBeanException {
		String companyControllerName = ICompanyConstants.COLLECTIONS_CONTROLLER_NAME;
		CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(companyControllerName);
		List<SelectItem> banks = companyCollections.getCompanyBanks();
		setBankList( new LinkedList<CashFlowBank>());
		for (SelectItem item:banks) {
			RegistryBank rbank = (RegistryBank) item.getValue();
			CashFlowBank bank = new CashFlowBank();
			bank.setId(rbank.getId());
			bank.setDescription(rbank.getFullName());
			bank.setEnabled(true);
			bank.setAmount(0.0);
			// TODO Calcular el saldo inicial del banco.
			bank.setBalance(0.0);
			getBankList().add(bank);
		}
		setBankModel( new ListDataModel(getBankList()));
	}

	public void onSearch(ActionEvent event) {
		checkBanks();
		try {
			List<CashFlowReport> list = new LinkedList<CashFlowReport>();
			list.add( getInitialBalance() );
			list.addAll(loadCashFlows());
			list.addAll(loadPendingFinances());
			if (isReturnedFinanceIncluded()) {
				list.addAll(loadReturnedFinances());
			}
			Collections.sort(list);
			list.add( getFinalBalance() );
			calculateBalance(list);
			setModel(new ListDataModel(list));
		} catch (ManagerBeanException e) {
			String msg = "Error al realizar la búsqueda del listado.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private void calculateBalance(List<CashFlowReport> list) {
		Map<Integer,Double> totals = new HashMap<Integer, Double>();
		for (CashFlowReport cfr: list) {
			for (Integer id : cfr.getMap().keySet() ) {
				double b = 0.0;
				if (!totals.containsKey(id)) {
					b = cfr.getMap().get(id).getBalance();
				} else {
					b = totals.get(id);	
				}
				double amount = cfr.getMap().get(id).getAmount();
				double balance = CommonUtil.round(amount + b);
				cfr.getMap().get(id).setBalance( balance );
				setTotal(CommonUtil.round(getTotal() + amount));
				cfr.setTotal(getTotal());
				totals.put(id, balance);
			}
		}
	}

	private CashFlowReport getInitialBalance() {
		CashFlowReport cfr = new CashFlowReport();
		cfr.setDate(null);
		cfr.setDescription("SALDO INICIAL");
		cfr.setSystemProperty(true);
		cfr.setMap(new HashMap<Integer, CashFlowBank>());
		setTotal(0.0);
		for (CashFlowBank bank : getBanks() ) {
			cfr.getMap().put(bank.getId(), bank);
			double b = CommonUtil.round(cfr.getTotal() + bank.getBalance());
			cfr.setTotal( b );
			setTotal( CommonUtil.round(getTotal() + bank.getBalance()) );
		}
		return cfr;
	}

	private CashFlowReport getFinalBalance() {
		CashFlowReport cfr = new CashFlowReport();
		cfr.setDate(null);
		cfr.setDescription("SALDO FINAL");
		cfr.setSystemProperty(true);
		cfr.setMap(new HashMap<Integer, CashFlowBank>());
		for (CashFlowBank bank : getBanks() ) {
			CashFlowBank cfb = new CashFlowBank();
			cfb.setId(bank.getId());
			cfb.setDescription(bank.getDescription());
			cfr.getMap().put(bank.getId(), cfb);
		}
		return cfr;
	}

	private List<CashFlowReport> loadCashFlows() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(CashFlowForecast.class);
		Criteria c = new Criteria();
		String fromDateAlias = bean.getFieldName(IFinanceAlias.CASH_FLOW_FORECAST_START_DATE);
		String toDateAlias = bean.getFieldName(IFinanceAlias.CASH_FLOW_FORECAST_DUE_DATE);
		c.addLessThanOrEqualExpression(fromDateAlias, getToDate());
		Expression exp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(toDateAlias, getFromDate());
		Expression exp2 = ExpressionUtilities.getNullExpression(toDateAlias);
		c.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2) );
		List<ITransferObject> list = bean.getList(c);
		List<CashFlowReport> flows = new LinkedList<CashFlowReport>();
		for (ITransferObject to: list) {
			CashFlowForecast cff = (CashFlowForecast) to;
			List<Date> dates = getForecastDates(cff);
			for (Date date:dates) {
				CashFlowReport cfr = new CashFlowReport();
				cfr.setDate(date);
				cfr.setType("Pr.");
				cfr.setDescription( cff.getDescription() );
				cfr.setPayment( cff.isPayment() );
				cfr.setMap( new HashMap<Integer, CashFlowBank>());
				CashFlowBank cfb = new CashFlowBank();
				cfb.setId( cff.getRegistryBank()!=null?cff.getRegistryBank().getId():Integer.MIN_VALUE);
				cfb.setDescription(null);
				cfb.setBalance(0.0 );
				double amount = cff.isPayment()?CommonUtil.round(cff.getAmount() * (-1)):cff.getAmount();
				cfb.setAmount( amount );
				cfr.getMap().put(cfb.getId(), cfb);
				cfr.setTotal( 0.0 );
				flows.add(cfr);
			}
		}
		return flows;
	}

	private List<Date> getForecastDates(CashFlowForecast cff) {
		List<Date> dates = new LinkedList<Date>();
		Calendar c = Calendar.getInstance();
		c.setTime(getFromDate());
		int fromDay = c.get(Calendar.DAY_OF_MONTH);
		int fromMonth = c.get(Calendar.MONTH);
		int fromYear = c.get(Calendar.YEAR);
		boolean[] months = cff.getMonths();
		for (int i = fromMonth; i < 12; i++) {
			if (months[i] && ((i > fromMonth) || (i == fromMonth) && cff.getPaymentDay()>= fromDay)) {
				c.set(Calendar.DAY_OF_MONTH,1);
				c.set(Calendar.MONTH,i);
				c.set(Calendar.YEAR,fromYear);
				c.setTime(CommonUtil.getMonthLastDay(c.getTime()));
				int lastDay = c.get(Calendar.DAY_OF_MONTH);
				c.set(Calendar.DAY_OF_MONTH, (lastDay < cff.getPaymentDay())?lastDay:cff.getPaymentDay());
				if (!c.getTime().after(getToDate())) {
					dates.add(c.getTime());	
				}
			}
		}
		return dates;
	}

	private Collection<? extends CashFlowReport> loadPendingFinances() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
		List<CashFlowReport> flows = new LinkedList<CashFlowReport>();		
		if (!isPendingFinanceIncluded()) {
			c.addGreaterThanOrEqualExpression(bean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), getFromDate());	
		}
		c.addLessThanOrEqualExpression(bean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), getToDate());
		List<ITransferObject> list = bean.getList(c);
		for (ITransferObject to:list) {
			Finance finance = (Finance) to;
			CashFlowReport cfr = new CashFlowReport();
			cfr.setDate(finance.getDueDate());
			cfr.setDescription( finance.getDocumentNumber() + " [" + finance.getRegistryName()+ "]" );
			cfr.setType(finance.isPayment()?"Pg.":"Cb.");
			cfr.setPayment( finance.isPayment() );
			cfr.setMap( new HashMap<Integer, CashFlowBank>());
			CashFlowBank cfb = new CashFlowBank();
			cfb.setId( Integer.MIN_VALUE ); // TODO Las transferencias, etc tienes el banco correcto.
			cfb.setDescription(null);
			cfb.setBalance(0.0 );
			double amount = cfr.isPayment()?CommonUtil.round(finance.getAmount() * (-1)):finance.getAmount();
			cfb.setAmount( amount );
			cfr.getMap().put(cfb.getId(), cfb);
			cfr.setTotal( 0.0 );
			flows.add(cfr);
		}
		return flows;
	}

	private Collection<? extends CashFlowReport> loadReturnedFinances() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.RETURNED);
		List<CashFlowReport> flows = new LinkedList<CashFlowReport>();		
		List<ITransferObject> list = bean.getList(c);
		c.addLessThanOrEqualExpression(bean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), getToDate());
		for (ITransferObject to:list) {
			Finance finance = (Finance) to;
			CashFlowReport cfr = new CashFlowReport();
			cfr.setDate(finance.getDueDate());
			cfr.setType("Dv.");
			cfr.setDescription( finance.getDocumentNumber() + " [" + finance.getRegistryName()+ "]" );
			cfr.setPayment( finance.isPayment() );
			cfr.setMap( new HashMap<Integer, CashFlowBank>());
			CashFlowBank cfb = new CashFlowBank();
			cfb.setId( Integer.MIN_VALUE ); // TODO Las transferencias, etc tienes el banco correcto.
			cfb.setDescription(null);
			cfb.setBalance(0.0 );
			double amount = cfr.isPayment()?CommonUtil.round(finance.getAmount() * (-1)):finance.getAmount();
			cfb.setAmount( amount );
			cfr.getMap().put(cfb.getId(), cfb);
			cfr.setTotal( 0.0 );
			flows.add(cfr);
		}
		return flows;
	}

	private void checkBanks() {
		boolean anyChecked = false;
		for (CashFlowBank bank:getBankList()) {
			anyChecked = bank.isEnabled();
			if (anyChecked) {
				break;
			}
		}
		if (!anyChecked) {
			String msg = "Debe marcar algún banco para mostrar el listado.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public class CashFlowBank {
		private Integer id;
		private String  description;
		private double  amount;
		private double  balance;
		private boolean enabled;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}

		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}

		public double getBalance() {
			return balance;
		}
		public void setBalance(double balance) {
			this.balance = balance;
		}
		
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}
	
	public class CashFlowReport implements Comparable<CashFlowReport>{
		private Date date;
		private String type;
		private String description;
		private boolean payment;
		private boolean systemProperty;
		private Map<Integer,CashFlowBank> map;
		private double total;
		
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
		public boolean isPayment() {
			return payment;
		}
		public void setPayment(boolean payment) {
			this.payment = payment;
		}
		
		public boolean isSystemProperty() {
			return systemProperty;
		}
		public void setSystemProperty(boolean systemProperty) {
			this.systemProperty = systemProperty;
		}
		
		public Map<Integer, CashFlowBank> getMap() {
			return map;
		}
		public void setMap(Map<Integer, CashFlowBank> map) {
			this.map = map;
		}

		public double getTotal() {
			return total;
		}
		public void setTotal(double total) {
			this.total = total;
		}

		@Override
		public int compareTo(CashFlowReport cfr) {
			if (cfr == null) {
				return 1;	
			}
			if (getDate() == null) {
				return -1;
			}
			return getDate().compareTo(cfr.getDate());
		}
		
	}
}

