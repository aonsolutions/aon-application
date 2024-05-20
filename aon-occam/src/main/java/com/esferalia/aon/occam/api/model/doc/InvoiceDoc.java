package com.esferalia.aon.occam.api.model.doc;

import java.net.URL;
import java.util.Date;

import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.type.MimeType;

public class InvoiceDoc extends Doc<InvoiceAttachmentType> {

	private static final String BUCKET = "aon-invoice-doc";
	private Integer invoice;
	private String s3Key;
	
	public Integer getInvoice() {
		return invoice;
	}
	
	public InvoiceDoc setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public String getS3Bucket() {
		return BUCKET;
	}
	
	public String getS3Key() {
		return s3Key;
	}
	
	public InvoiceDoc setS3Key(String s3Key) {
		this.s3Key = s3Key;
		return this;
	}
	
	@Override
	public InvoiceDoc setId(Integer id) {
		super.setId(id);
		return this;
	}
	
	@Override
	public InvoiceDoc setDomain(Integer domain) {
		super.setDomain(domain);
		return this;
	}
	
	@Override
	public InvoiceDoc setDescription(String description) {
		super.setDescription(description);
		return this;
	}
	
	@Override
	public InvoiceDoc setDate(Date date) {
		super.setDate(date);
		return this;
	}
	
	@Override
	public InvoiceDoc setMimeType(MimeType mimeType) {
		super.setMimeType(mimeType);
		return this;
	}
	
	@Override
	public InvoiceDoc setType(InvoiceAttachmentType type) {
		super.setType(type);
		return this;
	}
	
	@Override
	public URL getDownloadURL() {
		return null;
	}

	@Override
	public URL getDownloadURL(String contentDisposition) {
		return null;
	}
	

}
