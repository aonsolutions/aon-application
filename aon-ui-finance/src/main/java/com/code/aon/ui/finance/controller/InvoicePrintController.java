package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.facturae.FacturaeWriter;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ui.finance.util.FinanceEmailUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.IMailAccount;

public class InvoicePrintController extends InvoiceController implements IFinanceConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoicePrintController.class.getName());
	
	private IPriceStrategy priceStrategy;
	
	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public double getInvoiceTotalPrice() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}
	
	public void onInitSendEmail( ActionEvent event ) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {			
			controller.setAppendSignature(false);
			controller.setSaveSent(false);
			try {		
				controller.onNewMessage(event);
				InvoiceController invoiceController = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
				FinanceEmailUtil emailUtil = invoiceController.getEmailController();
				controller.setSubject( emailUtil.getEmailSubject() );
				String body = emailUtil.getEmailBody();
				controller.updateMessageBody( emailUtil.getEmailContent(body) );
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
				AonUtil.addErrorMessage(th.getMessage());
				throw new AbortProcessingException(th.getMessage(), th);
			}
		}		
	}	
	
	public void onSendInvoicesByEmail( ActionEvent event ) {
		InvoiceController controller = (InvoiceController) AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
		FinanceEmailUtil emailUtil = controller.getEmailController();
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		String subject = messageController.getSubject();
		String content = messageController.getContent();
		IMailAccount account = messageController.getSenderMailAccount();
		try {
			emailUtil.changeMailAccount(account);
			List<ITransferObject> list = getManagerBean().getList(getCriteria());
			for( ITransferObject to : list ) {
				emailUtil.sendInvoice( (Invoice) to, subject, content, messageController.isSaveSent()  );	
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		} finally {
			LogPanelController logger = LogPanelController.getInstance();
			logger.info( AonUtil.getMessage(BUNDLE_KEY, FINANCE_INVOICE_SEND_EMAIL_FNINISH) );
			logger.finish();
			messageController.setShowNewMessageWindow(false);
		}
	}	
	
	private byte[] getData( FacturaeWriter fw, Invoice invoice ) {
		byte[] data = null;
		File invoiceFile = null;
		try {
			invoiceFile = File.createTempFile("facturae ("+invoice.getId() + ")", FacturaeWriter.FACTURAE_EXTENSION );
			String filePath = invoiceFile.getAbsolutePath();
			String fileName = FilenameUtils.getFullPath(filePath) + FilenameUtils.getBaseName(filePath);
			fw.serialize(invoice, fileName);
			data = FileUtils.readFileToByteArray(invoiceFile);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			FileUtils.deleteQuietly(invoiceFile);	
		}
		return data;
	}

    private File getZipFile() throws IOException, ManagerBeanException {
    	File file = File.createTempFile( "invoices", "." + MimeType.MIME_ZIP.getExtension());
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(file) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		FacturaeWriter fw = new FacturaeWriter(AonUtil.getCurrentLocale());
		for( ITransferObject to : getManagerBean().getList(getCriteria()) ) {
			Invoice invoice = (Invoice) to;
			byte[] data = getData(fw, invoice);
			if (! ArrayUtils.isEmpty(data) ) {
				String name = "facturae " + StringUtils.replace(invoice.getReferenceCode(), "/", "-") + "." + MimeType.MIME_XML.getExtension();
	            zipOut.putNextEntry(new ZipEntry(name));
	            zipOut.write(data);
	        	zipOut.closeEntry();				
			}
		}
		zipOut.close();
		return file;
    }
	
    public void downloadInvoiceZip( ActionEvent event ) {
    	File zipFile = null;
    	InputStream in = null;
		try {    	
	    	zipFile = getZipFile();
	    	in = new BufferedInputStream(new FileInputStream(zipFile));
	        DownloadUtil.downloadAttachment("invoices.zip", MimeType.MIME_ZIP, in, zipFile.length() );
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th.getMessage(), th);
		} finally {
			IOUtils.closeQuietly(in);
			FileUtils.deleteQuietly(zipFile);
		}
    }
	
}