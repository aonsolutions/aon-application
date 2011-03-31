package com.code.aon.accounting.annualReport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.CommonUtil;

public class AnnualReportLauncher {
/*	
	private SummaryProviderParameters params;

	private String resolve(String content) {
		String accounts = StringUtils.substringBetween(content, IAccountingConstants.OPEN_EXPRESSION,IAccountingConstants.CLOSE_EXPRESSION);
		if (accounts!= null) {
			try {
				StringBuilder accountExp = new StringBuilder();
				String[] tokens = StringUtils.split(accounts,IAccountingConstants.ASTERISK);
				for (String token:tokens) {
					token = token.trim();
					if (StringUtils.isNotBlank(token)) {
						accountExp.append(accountExp.length()>0?IAccountingConstants.PIPE:IAccountingConstants.EMPTY);
						accountExp.append(token);	
						accountExp.append(IAccountingConstants.ASTERISK);
					}
				}
				Double amount = getAccountsAmount(accountExp.toString());
				DecimalFormat formatter = new DecimalFormat(IAccountingConstants.DECIMAL_FORMAT_PATTERN);
				String value = null;
				try {
					value = formatter.format(amount);	
				} catch (NumberFormatException e) {
					
				}
				String before = StringUtils.substringBefore(content, IAccountingConstants.OPEN_EXPRESSION);
				String after  = StringUtils.substringAfter(content, IAccountingConstants.CLOSE_EXPRESSION);
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

	public SummaryProviderParameters getParams() {
		if (params == null) {
			params = new SummaryProviderParameters();
		}
		return params;
	}
	
	private void pdf(List<AnnualReportDetail> list) throws ManagerBeanException {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			HttpServletResponse res = (HttpServletResponse) ec.getResponse();
			res.setContentType(MimeType.MIME_PDF.getName());
			res.setHeader("Content-Disposition", "attachment; filename=\"memoria.pdf\";");
			pdf(res.getOutputStream(),list);
			res.flushBuffer();
			ctx.responseComplete();
		} catch (IOException e) {
			AonUtil.addErrorMessage("El fichero no es correcto");
			throw new AbortProcessingException(e);
		}
	}

	public void pdf(OutputStream out) throws ManagerBeanException {
		pdf(out,getList());
	}
	public void pdf(OutputStream out,List<AnnualReportDetail> list) throws ManagerBeanException {
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">");
			buf.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			buf.append("<head>");
			buf.append("	<title></title>");
			buf.append("	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
			buf.append("	<meta http-equiv=\"Expires\" content=\"0\" />");
			buf.append("	<meta http-equiv=\"Pragma\" content=\"no-cache\" />");
			buf.append("	<meta http-equiv=\"Cache-Control\" content=\"no-store\" />");
			buf.append("	<style type=\"text/css\">");
			buf.append("	</style>");
			buf.append("</head>");
			buf.append("<body>");
			for (AnnualReportDetail detail : list) {
				buf.append(detail.getContentResolved());
			}
			buf.append("</body>");
			buf.append("</html>");
			ITextRenderer renderer = new ITextRenderer();
			ByteArrayOutputStream tidyOut = new ByteArrayOutputStream(); //we need this later
			Tidy tidy = new Tidy(); 
			tidy.setXHTML(true); 
			tidy.setInputEncoding("iso-8859-1");
			tidy.setOutputEncoding("iso-8859-1");
			tidy.parse( buf.toString(), tidyOut);
			InputStream tidyIn = new ByteArrayInputStream(tidyOut.toByteArray());
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.FALSE);
			dbf.setFeature("http://xml.org/sax/features/validation", Boolean.FALSE);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(tidyIn);
			renderer.setDocument(doc,null);
			renderer.layout();
			renderer.createPDF(out, false);
			renderer.getWriter().setCloseStream(false);
			renderer.finishPDF();
			tidyOut.close();
			tidyIn.close();
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
*/	
}



