package com.code.aon.ui.accounting.controller.report;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.accounting.util.Balance;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;

public class StatementController extends BasicController {

	private static final String STATEMENT_DETAIL_CONTROLLER_NAME = "statementDetail";

	private SummaryProvider sp = new SummaryProvider();

	private Balance openingEntry;
	private Balance fromOpeningEntry;
	private Balance periodBalance;

	private List<Balance> detail;
	private DataModel detailModel;
	
	private SummaryProviderParameters params;


	public Balance getOpeningEntry() {
		return openingEntry;
	}

	public void setOpeningEntry(Balance openingEntry) {
		this.openingEntry = openingEntry;
	}

	public Balance getFromOpeningEntry() {
		return fromOpeningEntry;
	}

	public void setFromOpeningEntry(Balance fromOpeningEntry) {
		this.fromOpeningEntry = fromOpeningEntry;
	}

	public Balance getPeriodBalance() {
		return periodBalance;
	}

	public void setPeriodBalance(Balance periodBalance) {
		this.periodBalance = periodBalance;
	}

	public SummaryProviderParameters getParams() {
		return params;
	}

	public void setParams(SummaryProviderParameters params) {
		this.params = params;
	}

	@Override
	public void select(ActionEvent event) {
		super.select(event);
		refresh();
	}

	public void onRefresh(ActionEvent event) {
		refresh();
	}

	private void refresh() {
		try {
			IController c = FormUtil.getController(STATEMENT_DETAIL_CONTROLLER_NAME);
			c.onSearch(null);
			initialize();
			initializeAmounts();
			transformDetailModel();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("No se pudo actualizar la página."+e.getMessage(), e);
		}
	}


	private void initialize() {
		setOpeningEntry(null);
		setFromOpeningEntry(null);
		setPeriodBalance(null);
		setDetail(null);
	}

	public List<Balance> getDetail() {
		return detail;
	}

	public void setDetail(List<Balance> detail) {
		this.detail = detail;
	}

	public DataModel getDetailModel() {
		return detailModel;
	}

	public void setDetailModel(DataModel detailModel) {
		this.detailModel = detailModel;
	}

	private void transformDetailModel() throws ManagerBeanException {
		IController c = FormUtil.getController(STATEMENT_DETAIL_CONTROLLER_NAME);
		DataModel model = c.getModel();
		Balance previous = null;
		if (isOpeningEntryPresent()) {
			previous = getOpeningEntry();	
		}
		if (isFromOpeningEntryPresent()) {
			previous = getFromOpeningEntry();
		}
		setDetail(new LinkedList<Balance>());
		for (int i = 0; i < model.getRowCount(); i++) {
			model.setRowIndex(i);
			AccountEntryDetail d = (AccountEntryDetail) model.getRowData();
			boolean ignore = false;
			// En el caso de que entre las fechas seleccionadas haya asientos se apertura,
			// se debe excluir el que se haya tomado en cuenta en el saldo incial,
			// es decir, cuando la fecha de getOpeningEntry coincida con la fecha del apunte.
			ignore = d.getAccountEntry().getType() == AccountEntryType.OPENING && getOpeningEntry() != null
					&& DateUtils.isSameDay(getOpeningEntry().getFromDate(), d.getAccountEntry().getEntryDate());
			if (!ignore) {
				Balance balance = new Balance();
				balance.setAccountEntry(d.getAccountEntry().getId());
				balance.setAccount(d.getAccount().getId());
				balance.setDescription(d.getAccount().getDescription());
				balance.setFromDate(d.getAccountEntry().getEntryDate());
				balance.setDebit(d.getDebit());
				balance.setCredit(d.getCredit());
				balance.setConcept(d.getConcept());
				balance.setDocumentNumber(d.getDocumentNumber());
				balance.setBalancingAccount(d.getBalancingAccount() == null ? null : d.getBalancingAccount().getId());
				balance.setBalancingAccountDescription(d.getBalancingAccount() == null ? null : d.getBalancingAccount().getDescription());
				if (previous != null) {
					balance.dragBalance(previous);
				} else {
					double b = CommonUtil.round(d.getDebit() - d.getCredit());
					if (b > 0) {
						balance.setUnpaidBalance(b);
					} else {
						balance.setCreditBalance(CommonUtil.round(b * (-1)));
					}
				}
				detail.add(balance);
				previous = balance;
			}
		}
		setDetailModel(new ListDataModel(getDetail()));
	}

	private void initializeAmounts() throws ManagerBeanException {
/*
	En la parte inicial del listado se indican tres lineas:
	 1.- Asiento de apertura:
	 2.- Acumulados desde el asiento de apertura hasta la fecha de inicio del listado. 
	 3.- Acumulados desde la fecha de inicio hasta la fecha fin del listado.
	 
	Lo que sigue a continuación es un detalle del punto 3 (una lista de AccountEntryDetail).
	
	Uno de los parámetros params.getPeriod ó params.getFromDate, debe tener valor 
	valor para buscar el asiento de apertura inmediatamente inferior en fecha.
	Este valor se almacena en this.openingEntry.

*/	
		Account account = getAccount();
		if (!StringUtils.startsWith(account.getId(), "6") && !StringUtils.startsWith(account.getId(), "7") ) {
			Date from = params.getFromDate()==null?params.isPeriodNull()?new Date(0):params.getPeriod().getInitiationDate():params.getFromDate();
			setOpeningEntry(sp.getOpeningEntryBalance(from, account.getId(),params.getSecurityLevel()));
			setFromOpeningEntry(null);
			Date to = DateUtils.addDays(from, -1);
			from = null;
			if (isOpeningEntryPresent()) {
				if (!DateUtils.isSameDay(getOpeningEntry().getFromDate(), params.getFromDate())) {
					from = getOpeningEntry().getFromDate();
					setFromOpeningEntry(sp.getPeriodBalance(from, to,account.getId(),params.getSecurityLevel(),false,false));
				}
			} else {
				setFromOpeningEntry( sp.getPeriodBalance(from, to, account.getId(),params.getSecurityLevel(),false,false));
			}
		}
		
		setPeriodBalance(sp.getPeriodBalance(params.getFromDate(), params.getToDate(), account.getId(),params.getSecurityLevel(),false,false));
		
		if (isFromOpeningEntryPresent()) {
			if (isOpeningEntryPresent()) {
				// Si exiten acumulados anteriores y asiento de apertura, éste se decuenta de los acumulados anteriores.
				getFromOpeningEntry().substractBalance(getOpeningEntry());
				getFromOpeningEntry().dragBalance(getOpeningEntry());
			}
		}
		
		if (isFromOpeningEntryPresent()) {
			getPeriodBalance().dragBalance(getFromOpeningEntry());
		} else {
			if (isOpeningEntryPresent()) {
				if ( !getPeriodBalance().getFromDate().after(getOpeningEntry().getFromDate()) ) {
					getPeriodBalance().substractBalance(getOpeningEntry());	
				}
				getPeriodBalance().dragBalance(getOpeningEntry());	
			}
		}
	}

	private Account getAccount() {
		return (Account) getTo();
	}

	public boolean isOpeningEntryPresent() {
		return (getOpeningEntry() != null);
	}

	public boolean isFromOpeningEntryPresent() {
		return (getFromOpeningEntry() != null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection getCollection() {
		return getDetail();
	}
	
}
