package com.code.aon.ui.accounting.controller.report;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.jooq.DSLContext;
import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.Quarter;
import com.code.aon.accounting.summary.Summary;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.faces.component.util.DownloadUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TrialBalanceController extends DataScrollerState implements ICollectionProvider,IAccountingBookItem{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TrialBalanceController.class);
	private SummaryProviderParameters parameters;
	private SummaryCollection summaryCollection;
	private String backAction;
	private AccountingUtil accountingUtil;
	
	private AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}

	public String getBackAction() {
		return backAction;
	}
	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}

	public String backAction() {
		return backAction;
	}

	public SummaryProviderParameters getParameters() {
		if (parameters == null) {
			SummaryProviderParameters p = new SummaryProviderParameters(AonUtil.getDomainName());
			p.setFromDate(null);
			p.setToDate(null);
			try {
				Period period = AccountingPeriodUtil.getDefaultPeriod(); 
				p.setPeriod(period);
				if (period != null && period.getId() != null) {
					p.setFromDate(period.getInitiationDate());
					p.setToDate(period.getDeadline());
				}
			} catch (ManagerBeanException e) {
				p.setPeriod(null);
			}
			p.setDate(new Date());
			p.setAccountExpression(null);
			p.setLowerLevelVisible(false);
			p.setNoTouchedAccountVisible(true);
			p.setSecurityLevel(AonUtil.getRoleManager().isConfidentiality()?null:SecurityLevel.OFFICIAL);
			boolean excludeClosing = false;
			if (p.getPeriod() != null) {
				try {
					excludeClosing = getAccountingUtil().existsEntry(p.getPeriod(), AccountEntryType.CLOSING, p.getSecurityLevel());
				} catch (ManagerBeanException e) {
					LOGGER.warn("No se pudo saber si existe asiento de cierre",e);					
				}	
			}
			p.setExcludeClosingEntry(excludeClosing);
			boolean excludeOperating = false;
			if (p.getPeriod() != null) {
				try {
					excludeOperating = getAccountingUtil().existsEntry(p.getPeriod(), AccountEntryType.OPERATING, p.getSecurityLevel());
				} catch (ManagerBeanException e) {
					LOGGER.warn("No se pudo saber si existe asiento de explotacion",e);					
				}	
			}
			p.setExcludeOperatingEntry(excludeOperating);
			p.setExcludeOpeningEntry(true);
			p.setExcludeBalancedAccounts(false);
			p.setRowsPerPage(AonUtil.getConfigurationController().getPageLimit());
			p.setAccountLevel(5);
			p.setPageCounter(0);
			p.setCounterVisible(false);
			p.setCoverVisible(false);
			setParameters(p);
		}
		return parameters;
	}
	public void setParameters(SummaryProviderParameters parameters) {
		this.parameters = parameters;
	}

	public boolean isCoverVisible() {
		return getParameters().isCoverVisible();
	}
	public void setCoverVisible(boolean coverVisible) {
		getParameters().setCoverVisible(coverVisible);
	}

	public boolean isCounterVisible() {
		return getParameters().isCounterVisible();
	}
	public void setCounterVisible(boolean counterVisible) {
		getParameters().setCounterVisible(counterVisible);
	}

	public int getPageCounter() {
		return getParameters().getPageCounter();
	}
	public void setPageCounter(int pageCounter) {
		getParameters().setPageCounter(pageCounter);
	}

	public void onReset(ActionEvent event) {
		setBackAction(null);
		setParameters(null);
		setSummaryCollection(null);
	}
	public void onResetStatement(ActionEvent event) {
		onReset(event);
		getParameters().setAccountLevel(5);
	}
	
	public void onSearch(ActionEvent event) {
		try {
			if (getParameters().getPeriod() == null) {
				getParameters().setPeriod(new Period());
			} 
			if (getParameters().getFromDate() == null && getParameters().getPeriod().getInitiationDate() != null) {
				getParameters().setFromDate(getParameters().getPeriod().getInitiationDate());
			}
			if (getParameters().getToDate() == null && getParameters().getPeriod().getDeadline() != null) {
				getParameters().setToDate(getParameters().getPeriod().getDeadline());
			}
			if (getParameters().getPeriod().getId() != null) {
				if (getParameters().getFromDate().before(getParameters().getPeriod().getInitiationDate())) {
					String msg = "La fecha desde es menor que la fecha de inicio del ejercicio seleccionado.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
				if (getParameters().getToDate().after(getParameters().getPeriod().getDeadline())) {
					String msg = "La fecha hasta es mayor que la fecha de final del ejercicio seleccionado.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}
			setSummaryCollection(null);
			setModel(new SerializableListDataModel(getSummaryCollection().getSummaryList()));
			if (getParameters().getAccountLevel() == 5) {
				getParameters().setRowsPerPage(20);
			}
			if (getSummaryCollection().getSummaryList().size() == 1) {
				getModel().setRowIndex(0);
				onStatement(event);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se pudo mostrar el balance. [" + e.getMessage() +"]");
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public SummaryCollection getSummaryCollection() throws ManagerBeanException {
		if (summaryCollection == null) {
			SummaryProvider sp = new SummaryProvider();
			setSummaryCollection(sp.getSummaryCollection(getParameters(),true));
		}
		return summaryCollection;
	}

	public void setSummaryCollection(SummaryCollection summaryCollection) {
		this.summaryCollection = summaryCollection;
	}

	@Override
	public DataModel getModel() {
		if (getDirectModel() == null) {
			setModel(new SerializableListDataModel());
		}
		return getDirectModel();
	}

	public void onStatement(ActionEvent event) {
		try {
			Summary summary = (Summary) getModel().getRowData();
			StatementController c = (StatementController) FormUtil
					.getController(IAccountingConstants.STATEMENT_CONTROLLER_NAME);
			c.onEditSearch(event);
			Criteria criteria = c.getCriteria();
			String alias = c.getFieldName(IEntityAlias.ACCOUNT_CODE);
			criteria.addExpression(alias, summary.getCode() + IAccountingConstants.ASTERISK);
			alias = c.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED);
			criteria.addExpression(ExpressionUtilities.getEqualExpression(alias, true));
			c.setParams(getParameters());
			c.onSearch(event);
			if (c.getModel().getRowCount() > 0) {
				c.getModel().setRowIndex(0);
				c.onSelect(event);
			} else {
				String msg = "No existen cuentas contables con el identificador " + summary.getCode()
						+ "*.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ExpressionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public Collection<?> getCollection() {
		try {
			return getSummaryCollection().getSummaryList();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public Collection<?> getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}

	public void onPeriodChanged(ActionEvent event) {
		try {
			Period period = getParameters().getPeriod();
			if (period == null) {
					Date first = getAccountingUtil().getFirstPeriodInitialDate();
					getParameters().setFromDate(first);
					getParameters().setToDate(null);
			} else {
				getParameters().setFromDate(period.getInitiationDate());
				getParameters().setToDate(period.getDeadline());
			}
		} catch (ManagerBeanException e) {
			// Nothing.
		}
	}

	public void onQuarterChanged(ValueChangeEvent event) {
		Quarter quarter = (Quarter) event.getNewValue();
		if (quarter != null && getParameters().getPeriod() != null) {
			int year = CommonUtil.getYear(getParameters().getPeriod().getInitiationDate()); 
			getParameters().setFromDate(quarter.getStartDate(year));
			getParameters().setToDate(quarter.getDueDate(year));
		}
	}

	public boolean isDateValid() {
		if (getParameters().getPeriod() != null) {
			return true;
		}
		Date from = getParameters().getFromDate();
		Date to = getParameters().getToDate();
		if ( from == null) {
			return false;
		}
		if (to != null && to.compareTo(from) < 0 ) {
			return false;
		}
		return true;
	}

	@Override
	public Integer getPageLimit() {
		return getParameters().getRowsPerPage();
	}

	@Override
	public void setPageLimit(Integer pageLimit) {
		getParameters().setRowsPerPage(pageLimit);
	}
	
	public String onExcelReport() {
		HttpServletResponse response = null;
		OutputStream out = null;
		Connection connection = null;
		try {
			int domainId = DomainManager.getCurrentDomain();
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			DSLContext ctx = DSL.using(connection, AccountingUtil.getDefaultSettings());
			
			java.sql.Date fromDate = new java.sql.Date(getParameters().getFromDate().getTime());
			java.sql.Date toDate = new java.sql.Date(getParameters().getToDate().getTime());
			
			String filename = "MayorDeCuentas";
			response = DownloadUtil.getResponse();
			out = DownloadUtil.initDownload(response, filename, MimeType.MIME_MS_EXCEL);
			ExcelReportExporter exporter = new ExcelReportExporter();
			exporter.startExport(filename);
			
			HSSFFont font = exporter.createFont();
			font.setFontHeightInPoints((short) 8);

			HSSFFont boldFont = exporter.createFont();
			boldFont.setFontHeightInPoints((short) 8);
			boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

			HSSFCellStyle headerCellStyle = exporter.createCellStyle();
		    headerCellStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		    headerCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER );
		    headerCellStyle.setFont(boldFont);
		    
			exporter.addHeaderCell("Cuenta", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Descripción.", exporter.getWidth(40), headerCellStyle);
			exporter.addHeaderCell("N. Apunte", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Fecha", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Concepto", exporter.getWidth(20), headerCellStyle);
			exporter.addHeaderCell("Debe", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Haber", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("S. Deudor", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("S. Acreedor", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Contrapartida", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Descripción contrapartida.", exporter.getWidth(20), headerCellStyle);
			exporter.addHeaderCell("N. Documento", exporter.getWidth(20), headerCellStyle);
			
			HSSFFont firstAccountFont = exporter.createFont();
			firstAccountFont.setFontHeightInPoints((short) 8);
			firstAccountFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
			firstAccountFont.setColor(new HSSFColor.BLUE().getIndex());

			HSSFCellStyle firstAccountCellStyle = exporter.createCellStyle();
			firstAccountCellStyle.setFont(firstAccountFont);
			
			HSSFCellStyle firstAccountAmountStyle = exporter.createCellStyle();
			firstAccountAmountStyle.setFont(firstAccountFont);
			firstAccountAmountStyle.setDataFormat( exporter.getDataFormat().getFormat(ExcelReportExporter.DECIMAL_PATTERN));

			HSSFCellStyle defaultStyle = exporter.createCellStyle();
			defaultStyle.setFont(font);
			
			HSSFCellStyle dateStyle = exporter.createCellStyle();
			dateStyle.setDataFormat( exporter.getDataFormat().getFormat(ExcelReportExporter.DATE_PATTERN));
			dateStyle.setFont(font);

			HSSFCellStyle amountStyle = exporter.createCellStyle();
			amountStyle.setFont(font);
			amountStyle.setDataFormat( exporter.getDataFormat().getFormat(ExcelReportExporter.DECIMAL_PATTERN));
			
			@SuppressWarnings("unchecked")
			List<Summary> list = (List<Summary>) getModel().getWrappedData();
			for ( Summary summary : list ) {
				Result<Record8<Integer, java.sql.Date, String, Double, Double, String, String, String>> 
					record = ctx.select(ACCOUNT_ENTRY.ID,ACCOUNT_ENTRY.ENTRY_DATE,ACCOUNT_ENTRY_DETAIL.CONCEPT
								,ACCOUNT_ENTRY_DETAIL.DEBIT,ACCOUNT_ENTRY_DETAIL.CREDIT,
								ACCOUNT.CODE,ACCOUNT.DESCRIPTION,ACCOUNT_ENTRY_DETAIL.DOCUMENT_NUMBER)
							.from(ACCOUNT_ENTRY_DETAIL)
							.join(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.equal(ACCOUNT_ENTRY.ID) )
							.leftOuterJoin(ACCOUNT).on(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT.equal(ACCOUNT.ID) )
							.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.equal(domainId))
							.and(ACCOUNT_ENTRY_DETAIL.ACCOUNT.equal(summary.getAccountId()))
							.and(ACCOUNT_ENTRY.ENTRY_DATE.greaterOrEqual(fromDate))
							.and(ACCOUNT_ENTRY.ENTRY_DATE.lessOrEqual(toDate))
							.orderBy(ACCOUNT_ENTRY.ENTRY_DATE,ACCOUNT_ENTRY.ID)
							.fetch();
				
				double balance = CommonUtil.round( summary.getInitialUnpaidBalance() - summary.getInitialCreditBalance());
				
				exporter.startLine();
				exporter.addStringCell( summary.getCode() , firstAccountCellStyle );
				exporter.addStringCell( summary.getDescription() , firstAccountCellStyle );
				exporter.addStringCell( null, firstAccountCellStyle );
				exporter.addStringCell( null, firstAccountCellStyle );
				exporter.addStringCell( "SALDO INICIAL", firstAccountCellStyle );
				exporter.addStringCell( null, firstAccountCellStyle );
				exporter.addStringCell( null, firstAccountCellStyle );
				exporter.addDecimalCell( summary.getInitialUnpaidBalance(), firstAccountAmountStyle );
				exporter.addDecimalCell( summary.getInitialCreditBalance(), firstAccountAmountStyle );
				exporter.endLine();
				
				for (Record8<Integer, java.sql.Date, String, Double, Double, String, String, String> step : record) {
					Integer entryId = step.value1();
					Date entryDate = step.value2();
					String concept = step.value3();
					Double debit = step.value4();
					Double credit = step.value5();
					balance = CommonUtil.round(balance + debit - credit); 
					String balAccountCode = step.value6();
					String balAccountDescription = step.value7();
					String documentNumber = step.value8();
					exporter.startLine();
					
					exporter.addStringCell( summary.getCode() , defaultStyle );
					exporter.addStringCell( summary.getDescription() , defaultStyle );
					exporter.addNumberCell( entryId , defaultStyle );
					exporter.addDateCell( entryDate, dateStyle);
					exporter.addStringCell( concept , defaultStyle );
					exporter.addDecimalCell( debit,amountStyle );
					exporter.addDecimalCell( credit,amountStyle );
					exporter.addDecimalCell( balance>0?balance:0.0,amountStyle );
					exporter.addDecimalCell( balance<0?Math.abs(balance):0.0,amountStyle );
					exporter.addStringCell( balAccountCode , defaultStyle );
					exporter.addStringCell( balAccountDescription , defaultStyle );
					exporter.addStringCell( documentNumber, defaultStyle );
					
					exporter.endLine();
				}
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
		} catch (AonConnectionException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DatabaseUtil.closeQuietly(connection);
			DownloadUtil.finishDownload(response, out);
		}
		return null;
	}
}
