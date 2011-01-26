package com.code.aon.ui.finance.controller;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.apache.commons.lang.StringUtils;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.StatusLightCondition;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.CashFlowForecast;
import com.code.aon.finance.Finance;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.report.IReportConstants;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;


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
			sa.setDescription("Sin asignar / otros");
			sa.setEnabled(true);
			sa.setBalance(0);
			sa.setInitialBalance(0);
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
		setModel(null);
		setTotal(0.0);
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
			String alias = StringUtils.abbreviate(rbank.getBank().getName(), 15) + " " +rbank.getBankAccount().getAccount();
			bank.setDescription( alias );
			bank.setAccount(rbank.getBankAccount().toString());
			bank.setEnabled(true);
			// TODO Calcular el saldo inicial del banco.
			bank.setBalance(0.0);
			getBankList().add(bank);
		}
		setBankModel( new ListDataModel(getBankList()));
	}

	public void onSearch(ActionEvent event) {
		checkDates();
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
				criteria.addEqualExpression(cffc.getManagerBean().getFieldName(IFinanceAlias.CASH_FLOW_FORECAST_ID), cfr.getId());
				cffc.setCriteria(criteria);
				cffc.onSearch(null);
				cffc.getModel().setRowIndex(0);
				cffc.onSelect(null);
				cffc.setBackAction("cashFlowForecastReport_list");
			} else {
				FinanceController fc = (FinanceController) FormUtil.getController(IFinanceConstants.FINANCE_CONTROLLER_NAME);
				fc.setPayment(cfr.isPayment());
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(fc.getManagerBean().getFieldName(IFinanceAlias.FINANCE_ID), cfr.getId());
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
		for (CashFlowBank bank : getBanks() ) {
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
				cfr.setId(cff.getId());
				cfr.setDate(date);
				cfr.setType("Pr.");
				cfr.setDescription( cff.getDescription() );
				cfr.setPayment( cff.isPayment() );
				cfr.setMap( new HashMap<Integer, CashFlowBank>());
				CashFlowBank cfb = new CashFlowBank();
				int bankId = cff.getRegistryBank()!=null?cff.getRegistryBank().getId():Integer.MIN_VALUE;
				cfb.setId( bankId );
				cfb.setBalance(0.0 );
				double amount = cff.isPayment()?CommonUtil.round(cff.getAmount() * (-1)):cff.getAmount();
				cfr.setAmount( amount );
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
			cfr.setId(finance.getId());
			cfr.setDate(finance.getDueDate());
			cfr.setDescription( finance.getDocumentNumber() + " [" + finance.getRegistryName()+ "]" );
			cfr.setType(finance.isPayment()?"Pg.":"Cb.");
			cfr.setPayment( finance.isPayment() );
			cfr.setMap( new HashMap<Integer, CashFlowBank>());
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

	private int getRegistryBank(Finance finance) {
		if (finance.getBank() == null || finance.getBankAccount() == null) {
			return Integer.MIN_VALUE;	
		}
		for ( CashFlowBank bank: getBanks() ) {
			if (StringUtils.equals(bank.getAccount(), finance.getBankAccount().toString()) ) {
				return bank.getId();
			}
		}
		return Integer.MIN_VALUE;
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
			cfr.setId(finance.getId());
			cfr.setDate(finance.getDueDate());
			cfr.setType("Dv.");
			cfr.setDescription( finance.getDocumentNumber() + " [" + finance.getRegistryName()+ "]" );
			cfr.setPayment( finance.isPayment() );
			cfr.setMap( new HashMap<Integer, CashFlowBank>());
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
		private String  account;
		private double  balance;
		private double  initialBalance;
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
		public String getAccount() {
			return account;
		}
		public void setAccount(String account) {
			this.account = account;
		}
		public double getBalance() {
			return balance;
		}
		public void setBalance(double balance) {
			this.balance = balance;
		}
		
		public double getInitialBalance() {
			return initialBalance;
		}
		public void setInitialBalance(double initialBalance) {
			this.initialBalance = initialBalance;
		}
		
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}
	
	public class CashFlowReport implements Comparable<CashFlowReport>{
		private Integer id; // ID del vtos. o de la previsión.
		private Date date;
		private String type;
		private String description;
		private boolean payment;
		private boolean systemProperty;
		private boolean disabled;
		private Map<Integer,CashFlowBank> map;
		private double  amount;
		private double total;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		
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
		
		public boolean isDisabled() {
			return disabled;
		}
		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}
		
		public Map<Integer, CashFlowBank> getMap() {
			return map;
		}
		public void setMap(Map<Integer, CashFlowBank> map) {
			this.map = map;
		}

		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}

		public double getTotal() {
			return total;
		}
		public void setTotal(double total) {
			this.total = total;
		}
		public CashFlowReport getTo() {
			return this;
		}
		public double getBalance(Integer id) {
			if (getMap().containsKey(id)) {
				return getMap().get(id).getBalance();	
			}
			return 0.0;
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

	public String onExcelReport() {
		HttpServletResponse response = null;
		OutputStream out = null;
		try {
			Font detailFont = new Font();
			detailFont.setFontName("SansSerif");
			detailFont.setFontSize(7);

			Font detailBoldFont = new Font();
			detailBoldFont.setFontName("SansSerif");
			detailBoldFont.setFontSize(7);
			detailBoldFont.setBold(true);

			Font headerFont = new Font();
			headerFont.setFontName("SansSerif");
			headerFont.setFontSize(8);
			headerFont.setBold(true);

			Style detailStyle = new Style();
			detailStyle.setFont(detailFont);

			Style detailBoldStyle = new Style();
			detailBoldStyle.setFont(detailBoldFont);

			Style headerStyle = new Style();
			headerStyle.setFont(headerFont);
			headerStyle.setBorderBottom(Border.PEN_1_POINT);
			headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
			headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
			headerStyle.setBackgroundColor(Color.WHITE);
			headerStyle.setTextColor(Color.BLACK);
			
			Style dateStyle = (Style) detailStyle.clone();
			dateStyle.setPattern("dd/MM/yyyy");
			
			Style amountStyle = (Style) detailStyle.clone();
			amountStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
			amountStyle.setPattern("#,##0.00");

			Style amountRedStyle = (Style) amountStyle.clone();
			amountRedStyle.setTextColor(Color.RED);
			
			Style amountBlueStyle = (Style) amountStyle.clone();
			amountBlueStyle.setTextColor(Color.BLUE);

			Style amountBoldStyle = (Style) detailBoldStyle.clone();
			amountBoldStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
			amountBoldStyle.setPattern("#,##0.00");

			Style amountBoldRedStyle = (Style) amountBoldStyle.clone();
			amountBoldRedStyle.setTextColor(Color.RED);
			
			Style amountBoldBlueStyle = (Style) amountBoldStyle.clone();
			amountBoldBlueStyle.setTextColor(Color.BLUE);
			
			AbstractColumn dateColumn = ColumnBuilder.getNew()
				.setColumnProperty("date", Date.class.getName())
				.setTitle("Fecha")
				.setWidth(50)
				.setStyle(dateStyle)
				.setHeaderStyle(headerStyle)
				.build();
			AbstractColumn typeColumn = ColumnBuilder.getNew()
				.setColumnProperty("type", String.class.getName())
				.setTitle("T")
				.setWidth(30)
				.setStyle(detailStyle)
				.setHeaderStyle(headerStyle)
				.build();
			AbstractColumn descriptionColumn = ColumnBuilder.getNew()
				.setColumnProperty("description", String.class.getName())
				.setTitle("Descripción")
				.setWidth(400)
				.setStyle(detailStyle)
				.setHeaderStyle(headerStyle)
				.build();
			
			StatusLightCondition positive= new StatusLightCondition(null, new Double(0));
			StatusLightCondition negative  = new StatusLightCondition(new Double(0),null);

			ArrayList<ConditionalStyle> amountConditionalStyles = new ArrayList<ConditionalStyle>();
			amountConditionalStyles.add(new ConditionalStyle(positive,amountBlueStyle));
			amountConditionalStyles.add(new ConditionalStyle(negative,amountStyle));

			AbstractColumn amountColumn = ColumnBuilder.getNew()
				.setColumnProperty("amount", Double.class.getName())
				.setTitle("Importe")
				.setWidth(78)
				.addConditionalStyles(amountConditionalStyles)
				.setHeaderStyle(headerStyle)
				.build();

			ArrayList<ConditionalStyle> totalConditionalStyles = new ArrayList<ConditionalStyle>();
			totalConditionalStyles.add(new ConditionalStyle(negative,amountBoldRedStyle));
			totalConditionalStyles.add(new ConditionalStyle(positive,amountBoldBlueStyle));
			
			AbstractColumn totalColumn = ColumnBuilder.getNew()
				.setColumnProperty("total", Double.class.getName())
				.setTitle("Saldo")
				.setWidth(78)
				.addConditionalStyles(totalConditionalStyles)
				.setHeaderStyle(headerStyle)
				.build();
			
			FastReportBuilder drb = new FastReportBuilder();
			drb.addField("to",CashFlowReport.class.getName());
			drb.addField("payment",Boolean.class.getName());
			drb.addField("systemProperty",Boolean.class.getName());
			drb.addField("disabled",Boolean.class.getName());
			drb.addColumn(dateColumn)
				.addColumn(typeColumn)
				.addColumn(descriptionColumn)
				.addColumn(amountColumn)
				.addColumn(totalColumn);
			ArrayList<ConditionalStyle> bankConditionalStyles = new ArrayList<ConditionalStyle>();
			bankConditionalStyles.add(new ConditionalStyle(negative,amountRedStyle));
			bankConditionalStyles.add(new ConditionalStyle(positive,amountBlueStyle));
			
			for (CashFlowBank bank : getBanks()) {
				if (bank.isEnabled() && bank.getId() != Integer.MIN_VALUE ) {
					AbstractColumn bankColumn = ColumnBuilder.getNew()
						.setCustomExpression(new BankCustomExpression(bank.getId()) )					
						.setTitle(bank.getDescription())
						.setWidth(78)
						.addConditionalStyles(bankConditionalStyles)
						.setHeaderStyle(headerStyle)
						.build();
					drb.addColumn(bankColumn);
				}
			}
			drb.setPrintColumnNames(true)
				.setIgnorePagination(true)
				.setMargins(0, 0, 0, 0);
			
			
			DynamicReport dr = drb.build();
			dr.setWhenNoDataStyle(detailStyle);
			JRDataSource ds = new JRBeanCollectionDataSource((Collection) getModel().getWrappedData());
			JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
			response = DownloadUtil.getResponse();
			out = DownloadUtil.initDownload(response, "CashFlowForecast", MimeType.MIME_MS_EXCEL);
			JRXlsExporter exporter = new JRXlsExporter();
	        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
	        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out); 
	        exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
	        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
	        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
	        exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE,Boolean.TRUE);
			exporter.exportReport();
		} catch (ColumnBuilderException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (IOException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (JRException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DownloadUtil.finishDownload(response, out);
		}
		return null;
	}

	public class BankCustomExpression implements CustomExpression {
		private static final long serialVersionUID = 7368651157413691588L;
		private Integer bankId;

		public BankCustomExpression(Integer bankId) {
			this.bankId = bankId;
		}

		@Override
		public String getClassName() {
			return Double.class.getName();
		}

		@Override
		public Object evaluate(Map fields, Map variables, Map parameters) {
			CashFlowReport to = (CashFlowReport) fields.get("to");
			if (to.getMap().containsKey(bankId)) {
				return to.getMap().get(bankId).getBalance();
			}
			return null;
		}
	}
}

