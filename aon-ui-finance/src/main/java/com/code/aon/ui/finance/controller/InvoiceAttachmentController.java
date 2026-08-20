package com.code.aon.ui.finance.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.faces.controller.AttachmentController;
import com.code.aon.finance.InvoiceAttachment;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;

import solutions.aon.aws.s3.S3;

public class InvoiceAttachmentController extends AttachmentController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(InvoiceAttachmentController.class);
	
	public Occam getOccam() {
		return new Occam()
				.setDomain(DomainManager.getCurrentDomain())
				.setDomainName(AonUtil.getDomainName())
				.setUser("");
	}
    
	
	public void downloadAttachment(ActionEvent event) throws NumberFormatException, ManagerBeanException {
		FacesContext context = FacesContext.getCurrentInstance();
		String id = context.getExternalContext().getRequestParameterMap().get("index");
		InvoiceAttachment invoiceAttachment = (InvoiceAttachment) getManagerBean().get(Integer.valueOf(id));
		switch (invoiceAttachment.getType()) {
		case INVOICE: {
			AON.getInvoiceDoc(getOccam(), invoiceAttachment.getDomain(), invoiceAttachment.getInvoice().getId())
			.ifPresentOrElse(invoiceDoc -> downloadInvoiceDoc(invoiceDoc, invoiceAttachment), () -> DownloadUtil.downloadAttachment(invoiceAttachment));
		}
		default: {
			DownloadUtil.downloadAttachment(invoiceAttachment);
		}
		}
	}
	
	public static void downloadInvoiceDoc(InvoiceDoc invoiceDoc, InvoiceAttachment invoiceAttachment) {
		S3 s3 = S3.getInstance();
		String s3Key = invoiceDoc.getS3Key();
		String aonTable = invoiceDoc.getAonTable();
		String s3Bucket = Optional.ofNullable(invoiceDoc.getS3Bucket()).orElseGet(() -> s3.getAonTableBucket(aonTable, false));
		;
		try {
			byte[] data = s3.download(s3Bucket, s3Key);
			InputStream in = new ByteArrayInputStream(data);
			DownloadUtil.downloadAttachment(invoiceDoc.getDescription(), invoiceAttachment.getMimeType(), in,  data.length);
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
		} 
	}
}
