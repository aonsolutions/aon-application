package com.code.aon.ui.accounting.controller.report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.code.aon.AonVersion;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.entry.AccountEntryController;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class LedgerReportController extends BasicController implements IAccountingBookItem{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Period period;
	private Date fromDate;
	private Date toDate;
	private Date date;
	private String order;
	private String account;
	private Integer previousAccountEntryDetail;
	private Integer previousAccount;
	private boolean currentValue = true;
	private boolean odd = true;
	private boolean coverVisible = false;
	private boolean counterVisible = false;
	private int pageCounter = 0;
	private SecurityLevel securityLevel;

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public boolean isCoverVisible() {
		return coverVisible;
	}
	public void setCoverVisible(boolean coverVisible) {
		this.coverVisible = coverVisible;
	}

	public boolean isCounterVisible() {
		return counterVisible;
	}
	public void setCounterVisible(boolean counterVisible) {
		this.counterVisible = counterVisible;
	}

	public int getPageCounter() {
		return pageCounter;
	}
	public void setPageCounter(int pageCounter) {
		this.pageCounter = pageCounter;
	}

	public void onReset(ActionEvent event) {
		initialize();
		super.onReset(event);
	}

	private void initialize() {
		try {
			setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		} catch (ManagerBeanException e) {
			setPeriod(null);
		}
		setFromDate(null);
		setToDate(null);
		setDate(new Date());
		setSecurityLevel(AonUtil.getRoleManager().isConfidentiality()?null:SecurityLevel.OFFICIAL);
		setAccount(null);
		setPageCounter(0);
		setCounterVisible(false);
		setCoverVisible(false);
		previousAccountEntryDetail = null;
		previousAccount = null;
		odd = true;
	}

	public void onEditSearch(ActionEvent event) {
		initialize();
		super.onEditSearch(event);
	}

	public void onSearch(ActionEvent event) {
		try {
			Criteria criteria = getCriteria();
			if (period != null) {
				criteria
						.addEqualExpression(
								getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ACCOUNT_PERIOD_ID),
								period.getId());
			}
			if (getFromDate() != null) {
				criteria
						.addGreaterThanOrEqualExpression(
								getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE),
								getFromDate());
			}
			if (getToDate() != null) {
				criteria
						.addLessThanOrEqualExpression(
								getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE),
								getToDate());
			}
			if (getSecurityLevel() != null) {
				criteria.addEqualExpression(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_SECURITY_LEVEL),
						getSecurityLevel());
			}
			if (!StringUtils.isBlank(getAccount())) {
				criteria.addExpression(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_CODE), getAccount());
			}
			getCriteria().addOrder(
					getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_CODE));
			if ("1".equals(getOrder()) ) {
				getCriteria().addOrder(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE));
				getCriteria().addOrder(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID));
			} else if ("2".equals(getOrder()) ) {
				getCriteria().addOrder(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID));
			} else {
				getCriteria().addOrder(getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_JOURNAL));
			}
			super.onSearch(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		} catch (ExpressionException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public boolean isJournalOrdered() {
		return ("3".equals(getOrder()) );
	}
	
	public boolean isOdd() {
		return odd;
	}
	
	public boolean isFirstEntry() {
		try {
			AccountEntryDetail acd = (AccountEntryDetail) getModel().getRowData();
			if (previousAccountEntryDetail == null || !previousAccountEntryDetail.equals(acd.getId())) {
				previousAccountEntryDetail = acd.getId();
				Integer current = acd.getAccount().getId();
				if (previousAccount == null) {
					previousAccount = current;
					currentValue = true;
				} else {
					if (!previousAccount.equals(current)) {
						previousAccount = current;
						odd = !odd;
						currentValue = true;
					} else {
						currentValue = false;
					}
				}
			}
			return currentValue;
		} catch (java.lang.IllegalArgumentException e) {
			return currentValue;
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void onAccountEntry(ActionEvent event) {
		try {
			AccountEntryDetail detail = (AccountEntryDetail) getModel().getRowData();
			AccountEntryController entryController = (AccountEntryController) FormUtil
					.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(
					IEntityAlias.ACCOUNT_ENTRY_ID), detail.getAccountEntry().getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
			entryController.setBackAction(IAccountingConstants.LEDGER_LIST_NAVKEY);
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar el apunte.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public String onExcelReport() {
		HttpServletResponse response = null;
		OutputStream out = null;
		try {
			String filename = "MayorDeCuentas";
			response = DownloadUtil.getResponse();
			out = DownloadUtil.initDownload(response, filename, MimeType.MIME_MS_EXCEL);
			ExcelReportExporter exporter = new ExcelReportExporter();
			exporter.startExport(filename);
			
			HSSFFont font = exporter.createFont();
			font.setFontHeightInPoints((short) 8);

			HSSFFont boldFont = exporter.createFont();
			boldFont.setFontHeightInPoints((short) 8);
			boldFont.setBold(true);

			HSSFCellStyle headerCellStyle = exporter.createCellStyle();
		    headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
		    headerCellStyle.setAlignment(HorizontalAlignment.CENTER );
		    headerCellStyle.setFont(boldFont);
		    
			exporter.addHeaderCell("Cuenta", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Descripción.", exporter.getWidth(60), headerCellStyle);
			exporter.addHeaderCell("Asiento", exporter.getWidth(5), headerCellStyle);
			exporter.addHeaderCell("Fecha", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Concepto", exporter.getWidth(50), headerCellStyle);
			exporter.addHeaderCell("Debe", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Haber", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Contrapartida", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Descripción contrapartida.", exporter.getWidth(60), headerCellStyle);
			
			
			HSSFCellStyle defaultStyle = exporter.createCellStyle();
			defaultStyle.setFont(font);

			HSSFCellStyle dateStyle = exporter.createCellStyle();
			dateStyle.setDataFormat( exporter.getDataFormat().getFormat(ExcelReportExporter.DATE_PATTERN));
			dateStyle.setFont(font);

			HSSFCellStyle amountStyle = exporter.createCellStyle();
			amountStyle.setFont(font);
			amountStyle.setDataFormat( exporter.getDataFormat().getFormat(ExcelReportExporter.DECIMAL_PATTERN));
			
			List<ITransferObject> list = search(0, getRowCount());
			for ( ITransferObject to : list ) {
				AccountEntryDetail ae = (AccountEntryDetail) to;
				exporter.startLine();
				exporter.addStringCell( ae.getAccount().getCode() , defaultStyle );
				exporter.addStringCell( ae.getAccount().getDescription() , defaultStyle );
				exporter.addNumberCell( ae.getAccountEntry().getId(), defaultStyle );
				exporter.addDateCell( ae.getAccountEntry().getEntryDate() , dateStyle );
				exporter.addStringCell( ae.getConcept() , defaultStyle );
				exporter.addDecimalCell( ae.getDebit(),amountStyle );
				exporter.addDecimalCell( ae.getCredit(),amountStyle );
				exporter.addStringCell( ae.getBalancingAccount() != null
							?ae.getBalancingAccount().getCode():"" 
						, defaultStyle );
				exporter.addStringCell( ae.getBalancingAccount() != null
							?ae.getBalancingAccount().getDescription():""
						, defaultStyle );
				exporter.endLine();
			}
			exporter.endExport(out);
			
		} catch (ReportException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (IOException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DownloadUtil.finishDownload(response, out);
		}
		return null;
	}
}
