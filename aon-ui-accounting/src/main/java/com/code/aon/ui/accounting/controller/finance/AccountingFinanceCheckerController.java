package com.code.aon.ui.accounting.controller.finance;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.accounting.util.AccountingFinanceCheck;
import com.code.aon.accounting.util.AccountingFinanceChecker;
import com.code.aon.accounting.util.AccountingFinanceCheckerParams;
import com.code.aon.accounting.util.StrippedStatement;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.IReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.report.StatementController;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.finance.controller.FinanceController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.finance.event.FinanceSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountingFinanceCheckerController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private AccountingFinanceCheckerParams params;
	private DataModel model;
	private DataModel strippedModel;
	private DataModel financesModel;
	
	private String selectedAccountCode;
	private String selectedAccountDescription;
	
	
	public AccountingFinanceCheckerParams getParams() {
		return params;
	}
	public void setParams(AccountingFinanceCheckerParams params) {
		this.params = params;
	}
	
	public DataModel getModel() {
		return model;
	}

	public DataModel getStrippedModel() {
		return strippedModel;
	}
	public DataModel getFinancesModel() {
		return financesModel;
	}
	

	public String getSelectedAccountCode() {
		return selectedAccountCode;
	}
	public void setSelectedAccountCode(String selectedAccountCode) {
		this.selectedAccountCode = selectedAccountCode;
	}
	public String getSelectedAccountDescription() {
		return selectedAccountDescription;
	}
	public void setSelectedAccountDescription(String selectedAccountDescription) {
		this.selectedAccountDescription = selectedAccountDescription;
	}
	
	public void onBack(ActionEvent event ) {
		onSearch(event);
	}
	public void onStart(ActionEvent event ) {
		setParams(new AccountingFinanceCheckerParams());
		getParams().setDomain(DomainManager.getCurrentDomain());
		getParams().setDeadline(new Date());
		getParams().setCreditorsEnabled(true);
		getParams().setSuppliersEnabled(true);
		getParams().setCustomersEnabled(true);
	}
	
	public void onSearch(ActionEvent event ) {
		try {
			LinkedList<AccountingFinanceCheck> list = new LinkedList<AccountingFinanceCheck>();
			list.addAll(AccountingFinanceChecker.getChecks(AonUtil.getDomainName(),
					DomainManager.getCurrentDomain(), AonUtil.getRemoteUser(),getParams()));
			model = new SerializableListDataModel(list);
		} catch (AonException e) {
			String msg = "No se pudo mostrar el resultado. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		
	}
	
	public void onAccountStatement(ActionEvent event) {
		try {
			AccountingFinanceCheck check = (AccountingFinanceCheck) getModel().getRowData();
			onStatement(check.getAccountCode(), event);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar el acceso al extracto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ExpressionException e) {
			String msg = "No se pudo realizar el acceso al extracto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	
	private void onStatement(String accountCode, ActionEvent event) throws ManagerBeanException, ExpressionException {
		StatementController c = (StatementController) AonUtil.getRegisteredBean(IAccountingConstants.STATEMENT_CONTROLLER_NAME);
		c.onReset(event);
		SummaryProviderParameters spp = new SummaryProviderParameters(AonUtil.getDomainName());
		spp.setAccountExpression(accountCode);
		spp.setPeriod(null);
		spp.setFromDate(null);
		spp.setToDate(getParams().getDeadline());
		spp.setSecurityLevel(AonUtil.getRoleManager().isAccountingOperator()?null:SecurityLevel.OFFICIAL);
		c.setParams(spp);
		c.onEditSearch(event);
		Criteria criteria = c.getCriteria();
		String alias = c.getFieldName(IEntityAlias.ACCOUNT_CODE);
		criteria.addExpression(alias, accountCode + IAccountingConstants.ASTERISK);
		c.onSearch(event);
		if (c.getModel().getRowCount() > 0) {
			c.getModel().setRowIndex(0);
			c.onSelect(event);
			c.setBackAction("accountingFinanceChecker_list");
		} else {
			String msg = "No existen cuentas contables para la cuenta.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onFinance(ActionEvent event) {
		AccountingFinanceCheck check = (AccountingFinanceCheck) getModel().getRowData();
		setSelectedAccountCode( check.getAccountCode() );
		setSelectedAccountDescription( check.getAccountDescription() );
		getParams().setAccountCode(check.getAccountCode());
		getParams().setRegistryId(check.getRegistryId());
		finance();
	}
	public void onFinanceBack(ActionEvent event) {
		finance();
	}
	private void finance() {
		try {
			List<AccountingFinanceCheck> list =  AccountingFinanceChecker.getFinances(
				AonUtil.getDomainName(),DomainManager.getCurrentDomain(), AonUtil.getRemoteUser(),getParams() );
			financesModel = new SerializableListDataModel(list);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar el acceso a vencimientos.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (AonException e) {
			String msg = "No se pudo realizar el acceso a vencimientos.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public String onExcel() {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = "Conciliador de Movimientos";
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");
			ServletOutputStream output = response.getOutputStream();
	
			ExcelReportExporter exporter = new ExcelReportExporter();
			exporter.startExport(IReportExporter.DEFAULT_NAME);
			ReportMetadata metadata = getMetadata();
			exporter.exportHeader(metadata);
			List<AccountingFinanceCheck> list = (List<AccountingFinanceCheck>) getModel().getWrappedData();
			for (AccountingFinanceCheck check : list) {
				exporter.startLine();
				int i = 0;
				exporter.exportColumn(metadata.getColumns().get((i++)), check.getAccountCode() );
				exporter.exportColumn(metadata.getColumns().get((i++)), check.getAccountDescription() );
				exporter.exportColumn(metadata.getColumns().get((i++)), check.getDebit()  );
				exporter.exportColumn(metadata.getColumns().get((i++)), check.getCredit() );
				exporter.exportColumn(metadata.getColumns().get((i++)), check.getDebitBalance() );
				exporter.exportColumn(metadata.getColumns().get((i++)), check.getCreditBalance() );
				exporter.exportColumn(metadata.getColumns().get((i++)), check.getFinBalance() );
				exporter.exportColumn(metadata.getColumns().get((i++)), check.getDifference() );
				exporter.endLine();
			}
			exporter.endExport(output);
			output.flush();
			response.flushBuffer();
			faces.responseComplete();
		} catch (ReportException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return null;
	}
	
	private static final ReportColumnMetadata[] COLUMN_LABELS = new ReportColumnMetadata[]{
		new ReportColumnMetadata("ACCOUNT",Types.VARCHAR,"Cuenta",10)
		,new ReportColumnMetadata("NAME",Types.VARCHAR,"Descripción.",50)
		,new ReportColumnMetadata("DEBIT",Types.DOUBLE,"Debe.",12)
		,new ReportColumnMetadata("CREDIT",Types.DOUBLE,"Haber.",12)
		,new ReportColumnMetadata("DEBIT_BALANCE",Types.DOUBLE,"Saldo Deudor.",12)
		,new ReportColumnMetadata("CREDIT_BALANCE",Types.DOUBLE,"Saldo Acreedor.",12)
		,new ReportColumnMetadata("FIN_BALANCE",Types.DOUBLE,"Saldo Tesorería.",12)
		,new ReportColumnMetadata("DIFERENCIA",Types.DOUBLE,"Diferencia.",12)};
	
	private ReportMetadata getMetadata() throws ReportException {

			ReportMetadata metadata = new ReportMetadata();
		for (ReportColumnMetadata rcm : COLUMN_LABELS) {
			metadata.getColumns().add(rcm);
		}
		return metadata;
	}
	
	public void onStrippedStatement(ActionEvent event) {
		AccountingFinanceCheck check = (AccountingFinanceCheck) getModel().getRowData();
		setSelectedAccountCode( check.getAccountCode() );
		getParams().setAccountCode( check.getAccountCode() );
		setSelectedAccountDescription( check.getAccountDescription() );
		strippedStatement();
	}
	
	private void strippedStatement() {
		try {
			List<StrippedStatement> list =  AccountingFinanceChecker.getStrippedStatement(
					AonUtil.getDomainName(),DomainManager.getCurrentDomain(), AonUtil.getRemoteUser(),getParams() );
//			if (strippedType != StrippedType.ALL) {
//				List<StrippedStatement> newList = new LinkedList<StrippedStatement>();
//				
//				for (StrippedStatement ss : list) {
//					if ( ss.isSettled() && strippedType == StrippedType.SETTLED) {
//						newList.add(ss);
//					} else if ( !ss.isSettled() && strippedType == StrippedType.UNSETTLED) {
//						newList.add(ss);
//					}
//				}
//				list = newList ;
//			}
			strippedModel = new SerializableListDataModel(list);					
		} catch (AonException e) {
			String msg = "No se pudo mostrar el resultado. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	public void onStrippedBack(ActionEvent event) {
		strippedStatement();
	}
	public void onStrippedStatementShowFinances(ActionEvent event) {
		StrippedStatement ss = (StrippedStatement) getStrippedModel().getRowData();
		try {
			FinanceSearchListener searchListener = (FinanceSearchListener)
					AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_SEARCH_LISTENER_NAME);
			FinanceController financeController = (FinanceController) 
					AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_CONTROLLER_NAME);
			financeController.setPayment(!getSelectedAccountCode().startsWith("430"));
			financeController.onEditSearch(event);
			searchListener.setFinanceStatuses(null);
			Criteria criteria = financeController.getCriteria();
			criteria.addEqualExpression(financeController.getFieldName(IEntityAlias.FINANCE_CONCEPT)
					, ss.getDocumentNumber());
			financeController.onSearch(event);
			if (financeController.getModel().getRowCount() > 0) {
				financeController.getModel().setRowIndex(0);
				financeController.onSelect(event);
				financeController.setBackAction("accountingFinanceChecker_stripped");
				financeController.setBackActionListener("accountingFinanceChecker.onStrippedBack");
			} else {
				String msg = "No se encuentran vencimientos de " + getSelectedAccountDescription() +
						" para el número de documento " + ss.getDocumentNumber();
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			
		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar el acceso a vencimientos.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	public void onStrippedStatementShowEntries(ActionEvent event) {
		try {
			StrippedStatement ss = (StrippedStatement) getStrippedModel().getRowData();
			StatementController c = (StatementController) AonUtil.getRegisteredBean(IAccountingConstants.STATEMENT_CONTROLLER_NAME);
			c.onReset(event);
			SummaryProviderParameters spp = new SummaryProviderParameters(AonUtil.getDomainName());
			spp.setAccountExpression(getSelectedAccountCode());
			spp.setPeriod(null);
			spp.setFromDate(null);
			spp.setToDate(getParams().getDeadline());
			spp.setSecurityLevel(AonUtil.getRoleManager().isAccountingOperator()?null:SecurityLevel.OFFICIAL);
			spp.setDocumentNumber( ss.getDocumentNumber() );
			c.setParams(spp);
			c.onEditSearch(event);
			Criteria criteria = c.getCriteria();
			String alias = c.getFieldName(IEntityAlias.ACCOUNT_CODE);
			criteria.addExpression(alias, getSelectedAccountCode());
			c.onSearch(event);
			if (c.getModel().getRowCount() > 0) {
				c.getModel().setRowIndex(0);
				c.onSelect(event);
				c.setBackAction("accountingFinanceChecker_stripped");
				c.setBackActionListener("accountingFinanceChecker.onStrippedBack");
			} else {
				String msg = "No se encontraron datos.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar el acceso al extracto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ExpressionException e) {
			String msg = "No se pudo realizar el acceso al extracto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	public String financeShowInvoices() {
		AccountingFinanceCheck check = (AccountingFinanceCheck) getFinancesModel().getRowData();
		String numDoc = check.getDocumentNumber();
		return showInvoices(numDoc,"accountingFinanceChecker_finances","accountingFinanceChecker.onFinanceBack"); 
	}
	public String strippedStatementShowInvoices() {
		StrippedStatement ss = (StrippedStatement) getStrippedModel().getRowData();
		String numDoc = ss.getDocumentNumber();
		return showInvoices(numDoc,"accountingFinanceChecker_stripped","accountingFinanceChecker.onStrippedBack"); 
	}
	
	private String showInvoices(String numDoc, String backAction, String backActionListener) {
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			String invoiceViewer = null;
			String number = StringUtils.substringAfter(numDoc, "/");
			String series = null;
			Criteria c = new Criteria();
			if (StringUtils.isEmpty(number)) {
				number = StringUtils.substringAfter(numDoc, "-");
				c.addNullExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES));
			} else {
				series = StringUtils.substringBefore(numDoc, "/");
				series = StringUtils.substringAfter(series, "-");
				c.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES), series);
			}
			String type = StringUtils.substringBefore(numDoc, "-");
			String alias = invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE);
			if (StringUtils.equals(type, "E")) {
				c.addEqualExpression(alias, InvoiceType.SALES);
			} else if (StringUtils.equals(type, "R") && StringUtils.startsWith(getParams().getAccountCode(), "400")) {
				c.addEqualExpression(alias, InvoiceType.PURCHASE);
			} else if (StringUtils.equals(type, "R") && StringUtils.startsWith(getParams().getAccountCode(), "410")) {
				c.addEqualExpression(alias, InvoiceType.EXPENSES);
			} else if (StringUtils.equals(type, "G")) {
				c.addEqualExpression(alias, InvoiceType.UNDEDUCTIBLE);
			}
			c.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_NUMBER), Integer.parseInt( number ));	 
			List<ITransferObject> list = invoiceBean.getList(c);
			if (list != null && list.size() > 0 ) {
				Invoice invoice = (Invoice) list.get(0);
				String invoiceControllerName = "";
				if (invoice.getType() == InvoiceType.SALES) {
					invoiceControllerName = IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME;
					invoiceViewer = IFinanceConstants.SALE_INVOICE_FORM_NAME;
				} else if (invoice.getType() == InvoiceType.PURCHASE) {
					invoiceControllerName = IFinanceConstants.PURCHASE_INVOICE_CONTROLLER_NAME;
					invoiceViewer = IFinanceConstants.PURCHASE_INVOICE_FORM_NAME;
				} else if (invoice.getType() == InvoiceType.EXPENSES) {
					invoiceControllerName = IFinanceConstants.EXPENSE_INVOICE_CONTROLLER_NAME;
					invoiceViewer = IFinanceConstants.EXPENSE_INVOICE_FORM_NAME;
				} else if (invoice.getType() == InvoiceType.UNDEDUCTIBLE) {
					invoiceControllerName = IFinanceConstants.UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME;
					invoiceViewer = IFinanceConstants.UNDEDUCTIBLE_INVOICE_FORM_NAME;
				}
				InvoiceController invoiceController = (InvoiceController) AonUtil.getRegisteredBean(invoiceControllerName);
				invoiceController.onLoad(null, invoice.getId(), backAction, backActionListener);
				return invoiceViewer;
			}
			String msg = "No se pudo realizar el acceso a facturas.";
			AonUtil.addErrorMessage(msg);
			return null;
		} catch (NumberFormatException e) {
			String msg = "No se pudo realizar el acceso a facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar el acceso a facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
/*
	private enum StrippedType {
		ALL,
		SETTLED,
		UNSETTLED
	}
	private StrippedType strippedType = StrippedType.UNSETTLED;
	public void onStrippedStatementAll(ActionEvent event) {
		strippedType = StrippedType.ALL;		
		strippedStatement();
	}
	public void onStrippedStatementSettled(ActionEvent event) {
		strippedType = StrippedType.SETTLED;		
		strippedStatement();
	}
	public void onStrippedStatementUnsettled(ActionEvent event) {
		strippedType = StrippedType.UNSETTLED;
		strippedStatement();
	}
*/
}



