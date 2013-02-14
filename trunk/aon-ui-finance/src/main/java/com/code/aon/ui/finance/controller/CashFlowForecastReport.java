package com.code.aon.ui.finance.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.CashFlowForecast;
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.report.ReportException;
import com.code.aon.report.dynamic.DynaElements;
import com.code.aon.report.dynamic.DynaReport;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.report.controller.DynaReportManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class CashFlowForecastReport {
	
	private static final String NO_BANK = "SIN BANCO ASIGNADO";
	private Date fromDate;
	private Date toDate;
	private boolean returnedFinanceIncluded;
	private boolean pendingFinanceIncluded;
	private DataModel bankModel;
	private List<CashFlowBank> bankList;
	private List<CashFlowBank> banks;
	private List<CashFlowBank> disabledBanks;
	private DataModel model;
	private double total;
	private Integer bankToEnable;
	
	
	public Integer getBankToEnable() {
		return bankToEnable;
	}
	public void setBankToEnable(Integer bankToEnable) {
		this.bankToEnable = bankToEnable;
	}

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
		if (bankModel == null) {
			setBankModel( new ListDataModel(getBankList()));	
		}
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

	public List<CashFlowBank> getDisabledBanks() {
		if (disabledBanks == null) {
			disabledBanks = new LinkedList<CashFlowBank>();
			for (CashFlowBank bank: getBankList() ) {
				if (!bank.isEnabled()) {
					disabledBanks.add(bank);
				} 
			}
		}
		return disabledBanks;
	}
	public void setDisabledBanks(List<CashFlowBank> disabledBanks) {
		this.disabledBanks = disabledBanks;
	}
	public List<SelectItem> getDisabledBankCollection() {
		List<SelectItem> list = new LinkedList<SelectItem>(); 
		for (CashFlowBank bank: getDisabledBanks() ) {
			list.add(new SelectItem(bank.getId(),bank.getDescription() ) );	
		}
		return list;
	}
	
	public boolean isAnyBankDisabled() {
		return (getDisabledBanks().size() > 0);
	}
	
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}

	public void onReset(ActionEvent event) {
		setBanks(null);
		setModel(null);
		setTotal(0.0);
	}

	public void onEditSearch(ActionEvent event) {
		try {
			setFromDate( new Date() ); 
			setToDate(CommonUtil.getMonthLastDay(getFromDate()));
			setReturnedFinanceIncluded(false);
			setPendingFinanceIncluded(true);
			initializeData();
			setBankList(null);
			initializeBankList();
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar bancos.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	private void initializeData() throws ManagerBeanException {
		setBankModel(null);
		setBanks(null);
		setDisabledBanks(null);
		setModel(null);
		setTotal(0.0);
	}

	private void initializeBankList() throws ManagerBeanException {
		String companyControllerName = ICompanyConstants.COLLECTIONS_CONTROLLER_NAME;
		CompanyCollectionsController companyCollections = (CompanyCollectionsController)AonUtil.getRegisteredBean(companyControllerName);
		List<SelectItem> banks = companyCollections.getAllCompanyBanks();
		setBankList( new LinkedList<CashFlowBank>());
		CashFlowBank sa = new CashFlowBank();
		sa.setId(Integer.MIN_VALUE);
		sa.setDescription(NO_BANK);
		sa.setEnabled(true);
		sa.setBalance(0);
		sa.setInitialBalance(0);
		getBankList().add(sa);
		for (SelectItem item:banks) {
			RegistryBank rbank = (RegistryBank) item.getValue();
			CashFlowBank bank = new CashFlowBank();
			bank.setId(rbank.getId());
			String alias = StringUtils.abbreviate(rbank.getBank().getName(), 15) + " " +rbank.getBankAccount().getAccount();
			bank.setDescription( alias );
			bank.setAccount(rbank.getBankAccount().toString());
			bank.setEnabled(rbank.isActive());
			// TODO Calcular el saldo inicial del banco.
			bank.setBalance(0.0);
			getBankList().add(bank);
		}
	}

	public void onSearch(ActionEvent event) {
		checkDates();
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

	private void checkDates() {
		if (getFromDate().after(getToDate())) {
			String msg = "La fecha de la previsión no puede ser anterior a la fecha desde.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onRefresh(ActionEvent event) {
		onSearch(event);		
	}
	
	@SuppressWarnings("unchecked")
	public void onSimulate(ActionEvent event) {
		CashFlowReport cfr = (CashFlowReport) getModel().getRowData();
		cfr.setDisabled(!cfr.isDisabled());
		List<CashFlowReport> list = (List<CashFlowReport>) getModel().getWrappedData();
		CashFlowReport initial = list.get(0);
		initializeBalances(initial);
		calculateBalance(list);
	}
	
	public void onShow(ActionEvent event) {
		try {
			CashFlowReport cfr = (CashFlowReport) getModel().getRowData();
			if  ("Pr.".equals(cfr.getType())) {
				CashFlowForecastController cffc = (CashFlowForecastController) FormUtil.getController(IFinanceConstants.CASH_FLOW_FORECAST_CONTROLLER_NAME);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(cffc.getManagerBean().getFieldName(IEntityAlias.CASH_FLOW_FORECAST_ID), cfr.getId());
				cffc.setCriteria(criteria);
				cffc.onSearch(null);
				cffc.getModel().setRowIndex(0);
				cffc.onSelect(null);
				cffc.setBackAction("cashFlowForecastReport_list");
			} else {
				FinanceController fc = (FinanceController) FormUtil.getController(IFinanceConstants.FINANCE_CONTROLLER_NAME);
				fc.setPayment(cfr.isPayment());
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(fc.getManagerBean().getFieldName(IEntityAlias.FINANCE_ID), cfr.getId());
				fc.setCriteria(criteria);
				fc.onSearch(null);
				fc.getModel().setRowIndex(0);
				fc.onSelect(null);
				fc.setBackAction("cashFlowForecastReport_list");
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al mostrar el detalle de la línea.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public String showAction() {
		CashFlowReport cfr = (CashFlowReport) getModel().getRowData();
		if  ("Pr.".equals(cfr.getType())) {
			return "cashFlowForecast_form";
		}
		return "finance_form";
	}
	
	@SuppressWarnings("unchecked")
	public void onDisableBank(ActionEvent event) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			String bankId = params.get("bankId");
			if (StringUtils.isNotEmpty(bankId)) {
				int id = Integer.parseInt(bankId);
				for (CashFlowBank bank : getBankList()) {
					if (bank.getId().intValue() == id) {
						bank.setEnabled(false);
						List<CashFlowReport> list = (List<CashFlowReport>) getModel().getWrappedData();
						CashFlowReport initial = list.get(0);
						initializeBalances(initial);
						initializeData();
						break;
					}
				}
				onSearch(event);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al eliminar un banco.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onEnableBank(ActionEvent event) {
		try {
			for (CashFlowBank bank : getBankList()) {
				if (bank.getId().equals(getBankToEnable())) {
					bank.setEnabled(true);
					List<CashFlowReport> list = (List<CashFlowReport>) getModel().getWrappedData();
					CashFlowReport initial = list.get(0);
					initializeBalances(initial);
					initializeData();
					break;
				}
			}
			onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Error al habilitar un banco.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private void calculateBalance(List<CashFlowReport> list) {
		Map<Integer,Double> totals = new HashMap<Integer, Double>();
		for (CashFlowReport cfr: list) {
			if (!cfr.isDisabled()) {
				for (Integer id : cfr.getMap().keySet() ) {
					double b = 0.0;
					if (!totals.containsKey(id)) {
						b = cfr.getMap().get(id).getBalance();
					} else {
						b = totals.get(id);	
					}
					double amount = cfr.getAmount();
					double balance = CommonUtil.round(amount + b);
					cfr.getMap().get(id).setBalance( balance );
					setTotal(CommonUtil.round(getTotal() + amount));
					cfr.setTotal(getTotal());
					totals.put(id, balance);
				}
			}
		}
	}

	private CashFlowReport getInitialBalance() {
		CashFlowReport cfr = new CashFlowReport();
		cfr.setDate(null);
		cfr.setDescription("SALDO INICIAL");
		cfr.setSystemProperty(true);
		cfr.setMap(new HashMap<Integer, CashFlowBank>());
		initializeBalances(cfr);
		return cfr;
	}

	private void initializeBalances(CashFlowReport initialBalance) {
		setTotal(0.0);
		for (CashFlowBank bank : getBankList() ) {
			CashFlowBank cfb = new CashFlowBank();
			cfb.setId(bank.getId());
			cfb.setBalance(bank.getInitialBalance());
			initialBalance.getMap().put(bank.getId(), cfb);
			
			initialBalance.setTotal( cfb.getBalance() );
			setTotal( CommonUtil.round(getTotal() + cfb.getBalance()) );
		}
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
			cfr.getMap().put(bank.getId(), cfb);
		}
		return cfr;
	}

	private List<CashFlowReport> loadCashFlows() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(CashFlowForecast.class);
		Criteria c = new Criteria();
		String fromDateAlias = bean.getFieldName(IEntityAlias.CASH_FLOW_FORECAST_START_DATE);
		String toDateAlias = bean.getFieldName(IEntityAlias.CASH_FLOW_FORECAST_DUE_DATE);
		c.addLessThanOrEqualExpression(fromDateAlias, getToDate());
		Expression exp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(toDateAlias, getFromDate());
		Expression exp2 = ExpressionUtilities.getNullExpression(toDateAlias);
		c.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2) );
		List<ITransferObject> list = bean.getList(c);
		List<CashFlowReport> flows = new LinkedList<CashFlowReport>();
		for (ITransferObject to: list) {
			CashFlowForecast cff = (CashFlowForecast) to;
			if (cff.isUndated()) { // No hay checks marcados, se asume el primer dia como fecha.
				addCashFlowReport(cff,DateUtils.addDays(getToDate(),1),flows, true);
			} else {
				List<Date> dates = getForecastDates(cff);
				for (Date date:dates) {
					addCashFlowReport(cff,date,flows,false);
				}
			}
		}
		return flows;
	}

	private void addCashFlowReport(CashFlowForecast cff, Date date, List<CashFlowReport> flows, boolean undated) {
		CashFlowReport cfr = new CashFlowReport();
		cfr.setId(cff.getId());
		cfr.setDate(date);
		cfr.setType("Pr.");
		cfr.setUndated(undated);
		cfr.setDescription( cff.getDescription() );
		cfr.setPayment( cff.isPayment() );
		cfr.setMap( new HashMap<Integer, CashFlowBank>());
		CashFlowBank cfb = new CashFlowBank();
		int bankId = getRegistryBank(cff);
		cfr.setBankDescription( cff.getRegistryBank()!= null?cff.getRegistryBank().getFullName():NO_BANK);
		cfb.setId( bankId );
		cfb.setBalance(0.0 );
		double amount = cff.isPayment()?CommonUtil.round(cff.getAmount() * (-1)):cff.getAmount();
		cfr.setAmount( amount );
		cfr.getMap().put(cfb.getId(), cfb);
		cfr.setTotal( 0.0 );
		flows.add(cfr);
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
				Date date = c.getTime();
				if (!cff.getStartDate().after(date)) { // La fecha resultante es mayor que el inicio de la prevision.
					if (cff.getDueDate() == null || !cff.getDueDate().before(date)) { // La fecha resultante es mayor que el fin de la prevision.	
						if (!c.getTime().after(getToDate())) { // La fecha resultante es menor que la fecha hasta
							if (!c.getTime().before(getFromDate())){ // La fecha resultante es mayor que la fecha desde
								dates.add(c.getTime());	
							}
						}
					}
				}
			}
		}
		return dates;
	}

	private Collection<? extends CashFlowReport> loadPendingFinances() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
		List<CashFlowReport> flows = new LinkedList<CashFlowReport>();		
		if (!isPendingFinanceIncluded()) {
			c.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_DUE_DATE), getFromDate());	
		}
		c.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_DUE_DATE), getToDate());
		List<ITransferObject> list = bean.getList(c);
		for (ITransferObject to:list) {
			Finance finance = (Finance) to;
			CashFlowReport cfr = new CashFlowReport();
			cfr.setId(finance.getId());
			cfr.setDate(finance.getDueDate());
			cfr.setDescription( finance.getDocumentNumber() + " [" + finance.getRegistryName()+ "]" );
			cfr.setType(finance.isPayment()?"Pg.":"Cb.");
			cfr.setPayment( finance.isPayment() );
			cfr.setMap( new HashMap<Integer, CashFlowBank>());
			cfr.setBankDescription( finance.getBank() != null?finance.getBank().getName()+"["+ finance.getBankAccount()+ "]" :NO_BANK);
			CashFlowBank cfb = new CashFlowBank();
			cfb.setId(getRegistryBank(finance));
			cfb.setBalance(0.0 );
			double amount = cfr.isPayment()?CommonUtil.round(finance.getAmount() * (-1)):finance.getAmount();
			cfr.setAmount( amount );
			cfr.getMap().put(cfb.getId(), cfb);
			cfr.setTotal( 0.0 );
			flows.add(cfr);
		}
		return flows;
	}

	private int getRegistryBank(CashFlowForecast cff) {
		if (cff.getRegistryBank()!=null && cff.getRegistryBank().getId()!=null) {
			for (CashFlowBank bank:getBankList()) {
				if (bank.getId().equals(cff.getRegistryBank().getId())) {
					return cff.getRegistryBank().getId();
				}
			}
		}
		return Integer.MIN_VALUE;
	}

	private int getRegistryBank(Finance finance) {
		if (finance.getBank() == null || finance.getBankAccount() == null) {
			return Integer.MIN_VALUE;	
		}
		for ( CashFlowBank bank: getBankList() ) {
			if (StringUtils.equals(bank.getAccount(), finance.getBankAccount().toString()) ) {
				return bank.getId();
			}
		}
		return Integer.MIN_VALUE;
	}

	private Collection<? extends CashFlowReport> loadReturnedFinances() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.RETURNED);
		List<CashFlowReport> flows = new LinkedList<CashFlowReport>();		
		List<ITransferObject> list = bean.getList(c);
		c.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_DUE_DATE), getToDate());
		for (ITransferObject to:list) {
			Finance finance = (Finance) to;
			CashFlowReport cfr = new CashFlowReport();
			cfr.setId(finance.getId());
			cfr.setDate(finance.getDueDate());
			cfr.setType("Dv.");
			cfr.setDescription( finance.getDocumentNumber() + " [" + finance.getRegistryName()+ "]" );
			cfr.setPayment( finance.isPayment() );
			cfr.setMap( new HashMap<Integer, CashFlowBank>());
			cfr.setBankDescription( finance.getBank() != null?finance.getBank().getName()+"["+ finance.getBankAccount()+ "]" :NO_BANK);
			CashFlowBank cfb = new CashFlowBank();
			cfb.setId(getRegistryBank(finance));
			cfb.setDescription(null);
			cfb.setBalance(0.0 );
			double amount = cfr.isPayment()?CommonUtil.round(finance.getAmount() * (-1)):finance.getAmount();
			cfr.setAmount( amount );
			cfr.getMap().put(cfb.getId(), cfb);
			cfr.setTotal( 0.0 );
			flows.add(cfr);
		}
		return flows;
	}

	public String onExcelReport() {
		try {
			DynaElements dyn = new DynaElements();
			DynaReport report = new DynaReport();
			report.addField("to", CashFlowReport.class);
			report.addField("payment", Boolean.class);
			report.addField("systemProperty", Boolean.class);
			report.addField("disabled", Boolean.class);
			report.addColumn(dyn.getDateColumn("date", "Fecha"))
				.addColumn(dyn.getStringColumn("type", "T", 30))
				.addColumn(dyn.getStringColumn("description", "Descripción", 400))
				.addColumn(dyn.getNumberBlueNormalColumn("amount", "Importe"))
				.addColumn(dyn.getNumberRedBlueBoldColumn("total", "Saldo"));
			for (CashFlowBank bank : getBanks()) {
				if (bank.isEnabled()) {
					report.addColumn(dyn.getNumberRedBlueColumn(new BankCustomExpression(bank.getId()), bank.getDescription()));
				}
			}
			DynaReportManager drm = new DynaReportManager();
			drm.toExcel(report,"PrevisionTesoreria", getStrippedCollection());
		} catch (ReportException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return null;
	}

	private List<CashFlowReport> getStrippedCollection() {
		List<CashFlowReport> list = new LinkedList<CashFlowReport>();
		List<?> model = (List<?>) getModel().getWrappedData();
		for (Object o: model) {
			CashFlowReport r = (CashFlowReport) o;
			if (!r.isDisabled()) {
				list.add(r);
			}
		}
		return list;
	}
}

