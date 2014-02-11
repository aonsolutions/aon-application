package com.code.aon.ui.accounting.controller.finance;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.accounting.AccountEntry;
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
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.IReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.entry.AccountEntryController;
import com.code.aon.ui.accounting.controller.report.StatementController;
import com.code.aon.ui.finance.controller.FinanceController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.finance.event.FinanceSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountingFinanceCheckerController {
	
	private AccountingFinanceChecker checker = null;
	private AccountingFinanceCheckerParams params;
	private DataModel model;
	private DataModel strippedModel;
	
	private String selectedAccountCode;
	private String selectedAccountDescription;
	private double strippedSum;
	
	private enum StrippedType {
		ALL,
		SETTLED,
		UNSETTLED
	}
	private StrippedType strippedType = StrippedType.UNSETTLED;
	
	public AccountingFinanceCheckerParams getParams() {
		return params;
	}
	public void setParams(AccountingFinanceCheckerParams params) {
		this.params = params;
	}
	private AccountingFinanceChecker getAccountingFinanceChecker() {
		if (checker == null) {
			checker = new AccountingFinanceChecker();
		}
		return checker;
	}
	
	public DataModel getModel() {
		return model;
	}

	public DataModel getStrippedModel() {
		return strippedModel;
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
	
	public double getStrippedSum() {
		return strippedSum;
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
		Connection c = null;
		try {
			c = DatabaseUtil.getConnection(AonUtil.getDomainName());
			List<AccountingFinanceCheck> list =  getAccountingFinanceChecker().getChecks(c, getParams());
			model = new ListDataModel(list);
		} catch (AonConnectionException e) {
			String msg = "No se pudo mostrar el resultado. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} catch (AonException e) {
			String msg = "No se pudo mostrar el resultado. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} finally {
			DatabaseUtil.closeQuietly(c);
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
		alias = c.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED);
		criteria.addExpression(ExpressionUtilities.getEqualExpression(alias, true));
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
		try {
			AccountingFinanceCheck check = (AccountingFinanceCheck) getModel().getRowData();
			FinanceController financeController = (FinanceController) 
					AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_CONTROLLER_NAME);
			financeController.setPayment(!check.getAccountCode().startsWith("430"));
			financeController.onEditSearch(event);
			Criteria criteria = financeController.getCriteria();
			criteria.addEqualExpression(financeController.getFieldName(IEntityAlias.FINANCE_REGISTRY_ID), check.getRegistryId());
			
			financeController.onSearch(event);
			if (financeController.getModel().getRowCount() > 0) {
				financeController.getModel().setRowIndex(0);
				financeController.onSelect(event);
				financeController.setBackAction("accountingFinanceChecker_list");
				financeController.setBackActionListener("accountingFinanceChecker.onBack");
			} else {
				String msg = "No existen vencimientos pendientes de " + check.getAccountDescription();
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			
		} catch (ManagerBeanException e) {
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
				exporter.exportColumn(metadata.getColumns().get((i++)), check.getAccBalance() );
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
		,new ReportColumnMetadata("NAME",Types.VARCHAR,"Descripción.",40)
		,new ReportColumnMetadata("ACC_BALANCE",Types.DOUBLE,"Saldo Contable.",10)
		,new ReportColumnMetadata("FIN_BALANCE",Types.DOUBLE,"Saldo Tesorería.",10)
		,new ReportColumnMetadata("DIFERENCIA",Types.DOUBLE,"Diferencia.",10)};
	
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
		setSelectedAccountDescription( check.getAccountDescription() );
		strippedType = StrippedType.UNSETTLED;
		strippedStatement();
	}
	
	private void strippedStatement() {
		int domain = DomainManager.getCurrentDomain();
		Connection c = null;
		try {
			c = DatabaseUtil.getConnection(AonUtil.getDomainName());
			List<StrippedStatement> list =  getAccountingFinanceChecker().getStrippedStatement(
					c, domain, getSelectedAccountCode() );
			if (strippedType != StrippedType.ALL) {
				List<StrippedStatement> newList = new LinkedList<StrippedStatement>();
				
				for (StrippedStatement ss : list) {
					if ( ss.isSettled() && strippedType == StrippedType.SETTLED) {
						newList.add(ss);
					} else if ( !ss.isSettled() && strippedType == StrippedType.UNSETTLED) {
						newList.add(ss);
					}
				}
				list = newList ;
			}
			strippedSum = 0;
			for (StrippedStatement ss : list) {
				strippedSum = CommonUtil.round(strippedSum + ss.getDifference());
			}
			strippedModel = new ListDataModel(list);					
			
		} catch (AonConnectionException e) {
			String msg = "No se pudo mostrar el resultado. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} catch (AonException e) {
			String msg = "No se pudo mostrar el resultado. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} finally {
			DatabaseUtil.closeQuietly(c);
		}
	}
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
			AccountEntryController controller = (AccountEntryController) 
					AonUtil.getRegisteredBean(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			controller.onEditSearch(event);
			Criteria criteria = controller.getCriteria();
			criteria.addEqualExpression("AccountEntry.detail.documentNumber"
					, ss.getDocumentNumber());
			controller.onSearch(event);
			if (controller.getModel().getRowCount() > 0) {
				controller.getModel().setRowIndex(0);
				controller.onSelect(event);
				controller.setBackAction("accountingFinanceChecker_stripped");
				controller.setBackActionListener("accountingFinanceChecker.onStrippedBack");
			} else {
				String msg = "No existen vencimientos pendientes de " + getSelectedAccountDescription();
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			
		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar el acceso a vencimientos.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		
	}
	public String strippedStatementShowInvoices() {
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			String invoiceViewer = null;
			StrippedStatement ss = (StrippedStatement) getStrippedModel().getRowData();
			String numDoc = ss.getDocumentNumber();
			String number = StringUtils.substringAfter(numDoc, "/");
			String series = null;
			Criteria c = new Criteria();
			if (StringUtils.isEmpty(number)) {
				number = StringUtils.substringAfter(numDoc, "-");	
			} else {
				series = StringUtils.substringBefore(numDoc, "/");
				series = StringUtils.substringAfter(series, "-");
				c.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES), series);
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
				invoiceController.onLoad(null, invoice.getId()
						, "accountingFinanceChecker_stripped"
						, "accountingFinanceChecker.onStrippedBack");
			}
			return invoiceViewer;
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
}
