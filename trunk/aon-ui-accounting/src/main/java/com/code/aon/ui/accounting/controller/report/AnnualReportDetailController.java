package com.code.aon.ui.accounting.controller.report;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ajax4jsf.org.w3c.tidy.Tidy;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.code.aon.accounting.AnnualReportDetail;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.lowagie.text.DocumentException;

public class AnnualReportDetailController  extends LinesController  {
	
	private static final String EMPTY = "";
	private static final String PIPE = "|";
	private static final String ASTERISK = "*";
	private static final String COMMA = ",";
	
	private SummaryProviderParameters params;
	
	public void onExecute(ActionEvent event) {
		try {
			resolveList();
		} catch (ManagerBeanException e) {
			String msg = "Error al resolver la memoria";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onExecutePDF(ActionEvent event) {
		try {
			resolveList();
			pdf();
		} catch (ManagerBeanException e) {
			String msg = "Error al resolver la memoria";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	@SuppressWarnings("unchecked")
	private void resolveList() throws ManagerBeanException {
		List<ITransferObject> list = (List<ITransferObject>) getModel().getWrappedData();
		for (ITransferObject to: list) {
			AnnualReportDetail detail = (AnnualReportDetail) to;
			if (StringUtils.contains(detail.getContent(), "#{")) {
				detail.setContentResolved(resolve( detail.getContent() )); 
			}
		}
	}

	private String resolve(String content) {
		String accounts = StringUtils.substringBetween(content, "#{","}#");
		if (accounts!= null) {
			try {
				StringBuilder accountExp = new StringBuilder();
				String[] tokens = StringUtils.split(accounts,COMMA);
				for (String token:tokens) {
					token = token.trim();
					if (StringUtils.isNotBlank(token)) {
						accountExp.append(accountExp.length()>0?PIPE:EMPTY);
						accountExp.append(token);	
						accountExp.append(ASTERISK);
					}
				}
				Double amount = getAccountsAmount(accountExp.toString());
				DecimalFormat formatter = new DecimalFormat("#,##0.00");
				String value = null;
				try {
					value = formatter.format(amount);	
				} catch (NumberFormatException e) {
					
				}
				String before = StringUtils.substringBefore(content, "#{");
				String after  = StringUtils.substringAfter(content, "}#");
				content = before + value + after;
				content = resolve(content);
			} catch (Throwable e) {
				String msg = "No se pudo realizar el Balance. " + e.getMessage();
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg, e);
			}
			
		}
		return content; 
	}

	private Double getAccountsAmount(String accountExp) throws ManagerBeanException {
		SummaryCollection summaryCollection = new SummaryCollection();
		SummaryProvider summaryProvider = new SummaryProvider();
		getParams().setAccountExpression(accountExp);
		summaryCollection = summaryProvider.getSummaryCollection(getParams(),false);
		return CommonUtil.round(summaryCollection.getCredit() - summaryCollection.getDebit());
	}

	private SummaryProviderParameters getParams() {
		if (params == null) {
			params = new SummaryProviderParameters();
			try {
				params .setPeriod(AccountingPeriodUtil.getDefaultPeriod());
			} catch (ManagerBeanException e) {
				params .setPeriod(null);
			}
			params.setFromDate(null);
			params.setToDate(null);
			params.setDate(new Date());
			params.setAccountExpression(null);
			params.setLowerLevelVisible(false);
			params.setNoTouchedAccountVisible(false);
			params.setRowsPerPage(20);
			params.setAccountLevel(5);
			params.setBudgeted(false);
			params.setPreviousPeriodVisible(true);
			
		}
		return params;
	}

    public void onMoveUp(ActionEvent event) throws ManagerBeanException {
    	moveMenuOption( (AnnualReportDetail) getSelectedTO(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException {
    	moveMenuOption( (AnnualReportDetail) getSelectedTO(), 1);    	
    }	
	
	@SuppressWarnings("unchecked")
	private void moveMenuOption( AnnualReportDetail annualReportDetail, int movement ) throws ManagerBeanException {
		int oldPosition = annualReportDetail.getSortKey();
		int newPosition = oldPosition + movement;
		annualReportDetail.setSortKey( newPosition );
		getManagerBean().update( annualReportDetail );
    	List<AnnualReportDetail> list = (List<AnnualReportDetail>) getModel().getWrappedData();
    	AnnualReportDetail movedMenuOption = list.get( newPosition );
		movedMenuOption.setSortKey( oldPosition );
		getManagerBean().update( movedMenuOption );
		list.set( newPosition, annualReportDetail );
		list.set( oldPosition, movedMenuOption );
	}
	
	
	@SuppressWarnings("unchecked")
	private void pdf() throws ManagerBeanException {
		try {
			StringBuffer before = new StringBuffer();
			before.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">");
			before.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			before.append("<head>");
			before.append("	<title></title>");
			before.append("	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
			before.append("	<meta http-equiv=\"Expires\" content=\"0\" />");
			before.append("	<meta http-equiv=\"Pragma\" content=\"no-cache\" />");
			before.append("	<meta http-equiv=\"Cache-Control\" content=\"no-store\" />");
			before.append("	<link type=\"text/css\" rel=\"stylesheet\" href=\"../aon-desktop/aonResource/5.6.0-SNAPSHOT/aon-richCss.css\" class=\"user\">");			
			before.append("	<style type=\"text/css\">");
			before.append("	</style>");
			before.append("</head>");
			before.append("<body>");
			List<ITransferObject> list = (List<ITransferObject>) getModel().getWrappedData();
			for (ITransferObject to : list) {
				AnnualReportDetail detail = (AnnualReportDetail) to;
				before.append(detail.getContentResolved());
			}
			before.append("</body>");
			before.append("</html>");
			ITextRenderer renderer = new ITextRenderer();
			ByteArrayOutputStream tidyOut = new ByteArrayOutputStream(); //we need this later
			Tidy tidy = new Tidy(); 
			tidy.setXHTML(true); 
			tidy.setInputEncoding("iso-8859-1");
			tidy.setOutputEncoding("iso-8859-1");
			tidy.parse( before.toString(), tidyOut);
			InputStream tidyIn = new ByteArrayInputStream(tidyOut.toByteArray());
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.FALSE);
			dbf.setFeature("http://xml.org/sax/features/validation", Boolean.FALSE);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(tidyIn);
			renderer.setDocument(doc,null);
			renderer.layout();
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			HttpServletResponse res = (HttpServletResponse) ec.getResponse();
			res.setContentType(MimeType.MIME_PDF.getName());
			res.setHeader("Content-Disposition", "attachment; filename=\"memoria.pdf\";");
			renderer.createPDF(res.getOutputStream(), true);
			tidyIn.close();
			tidyOut.close();
			res.flushBuffer();
			ctx.responseComplete();
		} catch (IOException e) {
			AonUtil.addErrorMessage("El fichero no es correcto");
			throw new AbortProcessingException(e);
		} catch (DocumentException e) {
			AonUtil.addErrorMessage("Error al ejecutar el listado");
			throw new AbortProcessingException(e);
		} catch (ParserConfigurationException e) {
			AonUtil.addErrorMessage("Error al analizar el contenido");
			throw new AbortProcessingException(e);
		} catch (SAXException e) {
			AonUtil.addErrorMessage("Error al analizar el contenido");
			throw new AbortProcessingException(e);
		}

	}
	
}



