package com.code.aon.ui.accounting.controller.balance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.accounting.freemarker.Freemarker;
import com.code.aon.accounting.mvel.BalanceContext;
import com.code.aon.accounting.mvel.BalanceException;
import com.code.aon.accounting.mvel.BalanceItem;
import com.code.aon.accounting.mvel.BalanceKey;
import com.code.aon.accounting.mvel.BalanceMVELContext;
import com.code.aon.accounting.mvel.BalanceSheet;
import com.code.aon.accounting.mvel.BalanceTransformer;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.AonException;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.util.AonUtil;

public class BalanceSheetResolverController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private SummaryProviderParameters parameters;
	private BalanceType balanceType;
	private BalanceSheet balance;
	
	private String result;

	public SummaryProviderParameters getParameters() {
		return parameters;
	}
	public void setParameters(SummaryProviderParameters parameters) {
		this.parameters = parameters;
	}

	public BalanceType getBalanceType() {
		return balanceType;
	}
	public void setBalanceType(BalanceType balanceType) {
		this.balanceType = balanceType;
	}
	public BalanceSheet getBalance() {
		return balance;
	}
	public void setBalance(BalanceSheet balance) {
		this.balance = balance;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public void onClosingBalance(ActionEvent event) {
		setBalanceType(BalanceType.CLOSING);
		onReset(event);
	}

	public void onOperatingBalance(ActionEvent event) {
		setBalanceType(BalanceType.OPERATING);
		onReset(event);
	}

	public void onPatrimonyBalance(ActionEvent event) {
		setBalanceType(BalanceType.PATRIMONY);
		onReset(event);
	}

	public void onCustomBalance(ActionEvent event) {
		setBalanceType(BalanceType.CUSTOM);
		onReset(event);
	}

	public void onReset(ActionEvent event) {
		initializeParameters();
		setResult(null);
	}
	
	private void initializeParameters() {
		parameters = new SummaryProviderParameters(AonUtil.getDomainName());
		try {
			parameters.setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		} catch (ManagerBeanException e) {
			parameters.setPeriod(null);
		}
		parameters.setFromDate(null);
		parameters.setToDate(null);
		parameters.setDate(new Date());
		parameters.setAccountExpression(null);
		parameters.setLowerLevelVisible(false);
		parameters.setNoTouchedAccountVisible(false);
		if (balanceType == BalanceType.CLOSING) {
			parameters.setExcludeClosingEntry(true);	
		}
		if (balanceType == BalanceType.OPERATING) {
			parameters.setExcludeOperatingEntry(true);
			parameters.setExcludeClosingEntry(true);
		}
		parameters.setRowsPerPage(20);
		parameters.setAccountLevel(5);
		parameters.setPreviousPeriodVisible(true);
		parameters.setCounterVisible(false);
		parameters.setCoverVisible(false);
		if (!AonUtil.getRoleManager().isConfidentiality()) {
			parameters.setSecurityLevel(SecurityLevel.OFFICIAL);	
		}
	}

	public List<SelectItem> getBalances(BalanceType balanceType) throws ManagerBeanException {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		List<SelectItem> balanceSheets = new LinkedList<SelectItem>();
		for (BalanceSheet sheet : BalanceSheet.values()) {
			if (sheet.getType() == balanceType) {
				SelectItem item = new SelectItem(sheet, sheet.getName(locale));
				balanceSheets.add(item);
			}
		}
		return balanceSheets;
	}
	
	
	public List<SelectItem> getBalances() throws ManagerBeanException {
		return getBalances(getBalanceType());
	}
	
	public String onPDFBalance() {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			response.setContentType(MimeType.MIME_PDF.getName());
			onBalance(response.getOutputStream(),MimeType.MIME_PDF);
			response.flushBuffer();
			faces.responseComplete();
		} catch (Throwable e) {
			String msg = "No se pudo realizar el Balance. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		return null;
	}
/*
	public String onBalance() {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			response.setContentType(MimeType.MIME_HTML.getName());
			onBalance(response.getOutputStream(),MimeType.MIME_HTML);
			response.flushBuffer();
			faces.responseComplete();
		} catch (Throwable e) {
			String msg = "No se pudo realizar el Balance. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		return null;
	}
*/	
	public void onBalance(ActionEvent event) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			onBalance(out,MimeType.MIME_HTML);
			setResult( new String(out.toByteArray()));
			out.close();
		} catch (Throwable e) {
			String msg = "No se pudo realizar el Balance. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	private String onBalance(OutputStream out, MimeType mimeType) throws Throwable {
		File tempFile = null;
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(AccountEntry.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			BalanceContext balanceContext = getBalanceContext();
			tempFile = File.createTempFile("bal_tmp", ".xml");
			// Realiza el fichero XML fuente de todos los formatos. 
			merge(balanceContext, tempFile );

			if (mimeType == MimeType.MIME_PDF) {
				File htmlTempFile = transform(tempFile);
				ITextRenderer renderer = new ITextRenderer();
				renderer.setDocument( htmlTempFile );
				renderer.layout();
				renderer.createPDF( out );
			} else if (mimeType == MimeType.MIME_HTML) {
				File htmlTempFile = transform(tempFile);
				IOUtils.copy( new FileInputStream(htmlTempFile), out);	
			} else {
				throw new UnsupportedOperationException("Tipo de fichero no soportado (" + mimeType + ")"); 
			}
			HibernateUtil.commitTransaction(sessionName);
		} catch (Throwable e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				
			}
			throw e;
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
			if (tempFile != null){
				try {
					tempFile.delete();
				} catch (Throwable e) {
					// Nothing
				}
			}
		}
		return null;		
	}
	
	private File  transform(File tempFile) throws IOException, BalanceException {
		InputStream fis = null;
		try {
			File htmlTempFile = File.createTempFile("bal_tmp", ".xhtml");
			FileOutputStream output = new FileOutputStream(htmlTempFile);
			BalanceTransformer viewer = new BalanceTransformer();
			fis =  new FileInputStream(tempFile);
			// viewer.transformXML2HTML( fis, output, getBalance() );
			viewer.transformXML2AonComponents( fis, output, getBalance() );
			output.close();
			output = null;
			return htmlTempFile;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// Nothing
				}
			}
		}
	}
	
	private void merge(BalanceContext balanceContext, File tempFile) throws AonException, IOException {
		Freemarker aef = new Freemarker( balanceContext );
		FileOutputStream fos = new FileOutputStream(tempFile);
		OutputStreamWriter writer = new OutputStreamWriter(fos, "ISO-8859-1");
		aef.process(getBalance().getMainTemplate(),writer);
		writer.flush();
		writer.close();
	}
	
	private BalanceContext getBalanceContext() throws BalanceException {
		BalanceMVELContext ctx = new BalanceMVELContext(getParameters());
		BalanceMVELContext previousCtx = null;
		boolean mustShowPreviousYear = false;
		if (getParameters().isPreviousPeriodVisible()) {
			SummaryProviderParameters previousParams = new SummaryProviderParameters(AonUtil.getDomainName());
			previousParams = getParameters().getPreviousPeriodParameters();
			if (previousParams != null) {
				previousCtx = new BalanceMVELContext(previousParams);
				mustShowPreviousYear = true;
			} else {
				getParameters().setPreviousPeriodVisible(false);
			}
		}
		for (BalanceKey aee : getBalance().getKeys()) {
			ctx.put(aee.getCode(), new BalanceItem(aee) );
			if (previousCtx != null) {
				previousCtx.put(aee.getCode(), new BalanceItem(aee) );
			}
		}
		List<BalanceMVELContext> list = new LinkedList<BalanceMVELContext>();
		list.add(ctx);
		if (mustShowPreviousYear) {
			list.add(previousCtx);
		}
		BalanceContext balanceContext = new BalanceContext( list );
		balanceContext.intilizeContextMap( balance );
		return balanceContext;
	}

}
