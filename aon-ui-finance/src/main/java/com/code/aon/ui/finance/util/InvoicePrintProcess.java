package com.code.aon.ui.finance.util;

import static com.code.aon.common.IProgression.FINISH_VALUE;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ICriteriaProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.common.ILongProcess;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.finance.controller.InvoicePrintController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.sign.controller.SignerController;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoicePrintProcess implements ILongProcess {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeeInvoicingProcess.class.getName());
	
	private static final String LOGO_IMAGE_FILE = "logoImageFile";
	
	private InvoicePrintController controller;
	
	private SignerController signerController;
	
	private byte[] logo;
	
	private ReportManager reportManager;
	
	private Map<String, Object> customParameters;
	
	public InvoicePrintProcess(InvoicePrintController controller, String reportKey) {
		this.controller = controller;
		this.signerController = controller.getSignerController();
		this.logo = obtainLogo();
		reportManager = new ReportManager();
		reportManager.setOutputFormat(OutputFormat.PDF);
		reportManager.setReportKey(reportKey);
		try {
			reportManager.setLocale(reportManager.getLocale());
			reportManager.setBundle(reportManager.getBundle());
			customParameters = reportManager.resolveCustomParams();
		} catch (ReportException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void updateProgress(int current, int total) {
		controller.getProgressionState().setProgressionCurrentValue(Math.round((current * 100.0)/total));
	}
	
	private byte[] obtainLogo() {
		CompanyController companyController = InvoiceController.getCompanyController();
		try {
			RegistryAttachment attachment = companyController.obtainCompanyLogo();
			return attachment.getData();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	private byte[] getReport( Invoice invoice ) throws ReportException, ManagerBeanException {
		customParameters.put(LOGO_IMAGE_FILE, new ByteArrayInputStream(this.logo));
		reportManager.setCustomParams(customParameters);
		ICriteriaProvider criteriaProvider = new InvoicePrintCriteriaProvider(controller.getManagerBean(), invoice);
		reportManager.setCriteriaProvider(criteriaProvider);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		reportManager.execute( out, reportManager.getReportKey());
		return out.toByteArray();
	}	

	private byte[] getReportData( Invoice invoice ) {
		byte[] data = null;
		try {
			IAttachment attach = signerController.getSignedAttachment(invoice.getId());
			if ( attach != null ) {
				data = attach.getData();
			} else {
				data = getReport(invoice);
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
		return data;
	}	
	
	@Override
	public void execute() {
		controller.setZipFile(null);
		ZipOutputStream zipOut = null;
		try {
	    	File file = File.createTempFile( "invoices", "." + MimeType.MIME_ZIP.getExtension());
			zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
    		List<Integer> ids = controller.getInvoiceIds();
    		for( int i = 0; i < ids.size(); i++ ) {
    			Invoice invoice = (Invoice) controller.getManagerBean().get(ids.get(i));
    			byte[] data = getReportData(invoice);
    			if (! ArrayUtils.isEmpty(data) ) {
    				String name = controller.getName(invoice, MimeType.MIME_PDF); 
    	            zipOut.putNextEntry(new ZipEntry(name));
    	            zipOut.write(data);
    	        	zipOut.closeEntry();			    				
    			}
	        	updateProgress(i+1, ids.size());
    		}
    		controller.setZipFile(file);
			controller.getProgressionState().setProgressionCurrentValue(FINISH_VALUE);
		} catch (Throwable ex) {
			controller.getProgressionState().setProgressionErrorMessage(ex.getMessage());
			controller.getProgressionState().setProgressionCurrentValue(IProgression.ERROR_VALUE);
		} finally {
			IOUtils.closeQuietly(zipOut);
		}
	}
	
	private static class InvoicePrintCriteriaProvider implements ICriteriaProvider {

		private Criteria criteria;
		
		public InvoicePrintCriteriaProvider( IManagerBean bean, Invoice invoice ) throws ManagerBeanException {
			this.criteria = new Criteria();
			this.criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
		}
		
		@Override
		public Criteria getCriteria() throws ManagerBeanException {
			return criteria;
		}
		
	}
	
}
