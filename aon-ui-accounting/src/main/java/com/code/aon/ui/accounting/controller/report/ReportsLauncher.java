package com.code.aon.ui.accounting.controller.report;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import jakarta.servlet.http.HttpServletResponse;

import org.ajax4jsf.org.w3c.tidy.Tidy;
import org.richfaces.event.UploadEvent;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.code.aon.accounting.Period;
import com.code.aon.accounting.annualReport.AnnualReportManager;
import com.code.aon.accounting.annualReport.AnnualReportParameters;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.lowagie.text.DocumentException;

public class ReportsLauncher implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static  String ENCODING = "iso-8859-1";	
	private static  String DEFAULT_REPORT = "memoria.html";
	private AonFile aonFile;
	private SummaryProviderParameters params;
	private String message;
	private Integer reportTemplate;
	
	private boolean useDefaultTemplate;
	private boolean useAttachedTemplate;
	private boolean useLoadedTemplate;
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isPdfEnabled() {
		return (isUseDefaultTemplate() || (!isUseDefaultTemplate() && aonFile != null && aonFile.getMimeType() == MimeType.MIME_HTML));
	}
	public boolean isReportEnabled() {
		return (isUseDefaultTemplate() || (!isUseDefaultTemplate() && aonFile != null && aonFile.getMimeType() != null && aonFile.getMimeType().getName().startsWith("text")));
	}
	
	public boolean isUseDefaultTemplate() {
		return useDefaultTemplate;
	}
	public void setUseDefaultTemplate(boolean useDefaultTemplate) {
		this.useDefaultTemplate = useDefaultTemplate;
	}

	public boolean isUseAttachedTemplate() {
		return useAttachedTemplate;
	}
	public void setUseAttachedTemplate(boolean useAttachedTemplate) {
		this.useAttachedTemplate = useAttachedTemplate;
	}
	
	public boolean isUseLoadedTemplate() {
		return useLoadedTemplate;
	}
	public void setUseLoadedTemplate(boolean useLoadedTemplate) {
		this.useLoadedTemplate = useLoadedTemplate;
	}
	
	public void onChangeUseDefaultTemplate(ActionEvent event) {
		setUseAttachedTemplate(isUseDefaultTemplate()?false:isUseAttachedTemplate());
		setUseLoadedTemplate(isUseDefaultTemplate()?false:isUseLoadedTemplate());
	}
	public void onChangeUseAttachedTemplate(ActionEvent event) {
		setUseDefaultTemplate(isUseAttachedTemplate()?false:isUseDefaultTemplate());
		setUseLoadedTemplate(isUseAttachedTemplate()?false:isUseLoadedTemplate());
		setAonFile(null);
		setMessage(null);
		setReportTemplate(null);
	}
	public void onChangeUseLoadedTemplate(ActionEvent event) {
		setUseDefaultTemplate(isUseLoadedTemplate()?false:isUseDefaultTemplate());
		setUseAttachedTemplate(isUseLoadedTemplate()?false:isUseAttachedTemplate());
		setAonFile(null);
		setMessage(null);
		setReportTemplate(null);
	}
	
	
	public Integer getReportTemplate() {
		return reportTemplate;
	}
	public void setReportTemplate(Integer reportTemplate) {
		this.reportTemplate = reportTemplate;
	}
	public AonFile getAonFile() {
		return aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		if ( this.aonFile != null ) {
			this.aonFile.clean();
		}
		this.aonFile = aonFile;
	}
	public void fileUploaded(UploadEvent event) {
		setMessage(null);
		AonFile f = AttachmentUtil.fileUploaded(event);
		setAonFile(f);
		if (f.getMimeType() == null) {
			setMessage("Formato de archivo desconocido. Si el archivo es de texto, renómbrelo con la extensión TXT.");
		} else {
			if (!f.getMimeType().getName().startsWith("text")) {
				setMessage("La plantilla cargada es de tipo " + f.getMimeType().getName() 
						+ ". La aplicación no soporta este tipo de archivo para su fusión con los datos contables." 
						+ " Únicamente son válidos los archivos de texto.");
			}
		}
	}
	
	public void onReset(ActionEvent event) {
		setAonFile(null);
		try {
			getParams().setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		} catch (ManagerBeanException e) {
			getParams().setPeriod(null);
		}
		getParams().setFromDate(null);
		getParams().setToDate(null);
		getParams().setDate(new Date());
		getParams().setAccountExpression(null);
		getParams().setLowerLevelVisible(false);
		getParams().setNoTouchedAccountVisible(false);
		getParams().setRowsPerPage(20);
		getParams().setAccountLevel(5);
		getParams().setPreviousPeriodVisible(true);
		setUseDefaultTemplate(true);
		setUseAttachedTemplate(false);
		setUseLoadedTemplate(false);
		setMessage(null);
		setAonFile(null);
		setReportTemplate(null);
	}
	
	public void onChangeReportTemplate(ActionEvent event) {
		try {
			setAonFile(null);
			if (getReportTemplate() != null) {
				IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_ID),getReportTemplate());
		        Iterator<?> iter = bean.getList(criteria).iterator();
				if (iter.hasNext()) {
					RegistryAttachment ra = (RegistryAttachment) iter.next();
					aonFile = new AonFile(); 
					aonFile.setAttachment(ra);				
					aonFile.setMimeType(ra.getMimeType());
					aonFile.setFileName(ra.getDescription());
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible obtener Plantilla";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		
	}
	
	public void onDefaultTemplate(ActionEvent event) {
			InputStream in = AnnualReportManager.class.getResourceAsStream(DEFAULT_REPORT);
			DownloadUtil.downloadAttachment(DEFAULT_REPORT, MimeType.MIME_HTML, in, 0);
	}
	
	public void onValidate(ActionEvent event) {
		try {
			Tidy tidy = new Tidy(); 
			tidy.setXHTML(true);
			tidy.setInputStreamName( getAonFile().getFileName());
			tidy.setInputEncoding(ENCODING);
			tidy.setOutputEncoding(ENCODING);
			HttpServletResponse res = DownloadUtil.getResponse();
			res.setContentType(MimeType.MIME_TXT.getName());
			res.setCharacterEncoding(ENCODING);
			tidy.setErrout(res.getWriter());
			InputStream in = getAonFile().openStream();
			tidy.parse( in, null);
			DownloadUtil.finishDownload(res,null);
			in.close();
		} catch (IOException e) {
			AonUtil.addErrorMessage("El fichero no es correcto");
			throw new AbortProcessingException(e);
		}
	}
	
	public void onExecute(ActionEvent event) {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			HttpServletResponse res = (HttpServletResponse) ec.getResponse();
			res.setContentType(MimeType.MIME_HTML.getName());
			res.setCharacterEncoding(ENCODING);
			AnnualReportParameters params = new AnnualReportParameters();
			params.setParams(getParams());
			AnnualReportManager manager = new AnnualReportManager();
			InputStream in = null;
			if (isUseDefaultTemplate()) {
				in = manager.getClass().getResourceAsStream(DEFAULT_REPORT);	
			} else {
				in = getAonFile().openStream();
			}
			InputStreamReader reader = new InputStreamReader(in,ENCODING);
			manager.resolve(reader, params, res.getWriter());
			res.flushBuffer();
			ctx.responseComplete();
			reader.close();
		} catch (IOException e) {
			String msg = "Error al resolver la memoria";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onExecutePDF(ActionEvent event) {
		try {
			pdf();
		} catch (ManagerBeanException e) {
			String msg = "Error al resolver la memoria";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public SummaryProviderParameters getParams() {
		if (params == null) {
			params = new SummaryProviderParameters(AonUtil.getDomainName());
		}
		return params;
	}
	public void setParams(SummaryProviderParameters params) {
		this.params = params;
	}
	
	private void pdf() throws ManagerBeanException {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			HttpServletResponse res = (HttpServletResponse) ec.getResponse();
			res.setContentType(MimeType.MIME_PDF.getName());
			res.setHeader("Content-Disposition", "attachment; filename=\"memoria.pdf\";");
			pdf(res.getOutputStream());
			res.flushBuffer();
			ctx.responseComplete();
		} catch (IOException e) {
			AonUtil.addErrorMessage("El fichero no es correcto");
			throw new AbortProcessingException(e);
		}
	}

	public void pdf(OutputStream out) throws ManagerBeanException {
		try {
			StringWriter writer = new StringWriter();
			AnnualReportParameters params = new AnnualReportParameters();
			params.setParams(getParams());
			AnnualReportManager manager = new AnnualReportManager();
			InputStream in = null;
			if (isUseDefaultTemplate()) {
				in = manager.getClass().getResourceAsStream(DEFAULT_REPORT);	
			} else {
				in = getAonFile().openStream();
			}
			InputStreamReader reader = new InputStreamReader(in,ENCODING);
			manager.resolve(reader, params, writer);
			reader.close();
			ITextRenderer renderer = new ITextRenderer();
			ByteArrayOutputStream tidyOut = new ByteArrayOutputStream(); //we need this later
			Tidy tidy = new Tidy(); 
			tidy.setTidyMark(false);
			tidy.setXHTML(true);
			tidy.setInputEncoding(ENCODING);
			tidy.setOutputEncoding(ENCODING);
			Document doc = tidy.parseDOM(writer.toString(), tidyOut);
			renderer.setDocument(doc,null);
			renderer.layout();
			renderer.createPDF(out, false);
			renderer.getWriter().setCloseStream(false);
			renderer.finishPDF();
			tidyOut.close();
		} catch (IOException e) {
			AonUtil.addErrorMessage("El fichero no es correcto");
			throw new AbortProcessingException(e);
		} catch (DocumentException e) {
			AonUtil.addErrorMessage("Error al ejecutar el listado");
			throw new AbortProcessingException(e);
		}

	}

	public static void main(String[] args) throws ManagerBeanException, FileNotFoundException{
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Period period = (Period) periodBean.get("2010");
		ReportsLauncher rl = new ReportsLauncher();
		SummaryProviderParameters parameters = new SummaryProviderParameters(AonUtil.getDomainName());
		parameters = new SummaryProviderParameters(AonUtil.getDomainName());
		parameters.setPeriod(period);
		parameters.setFromDate(null);
		parameters.setToDate(null);
		parameters.setDate(new Date());
		parameters.setAccountExpression(null);
		parameters.setLowerLevelVisible(false);
		parameters.setNoTouchedAccountVisible(false);
		parameters.setRowsPerPage(20);
		parameters.setAccountLevel(4);
		rl.setParams(parameters);
		rl.setUseDefaultTemplate(true);
		rl.setUseAttachedTemplate(false);
		rl.setUseLoadedTemplate(false);
		FileOutputStream fos = new FileOutputStream("/home/ecastellano/memoria.pdf");
		rl.pdf(fos);
	}
	
	
}



