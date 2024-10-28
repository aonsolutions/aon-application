package com.esferalia.aon.occam.api.model.doc;

import java.util.Date;

import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.type.MimeType;

public class InvoiceDoc extends S3Doc<InvoiceAttachmentType> {

	private Integer invoice;
	
	public Integer getInvoice() {
		return invoice;
	}
	
	public InvoiceDoc setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}
	
	@Override
	public InvoiceDoc setS3Bucket(String s3Key) {
		super.setS3Key(s3Key);
		return this;
	}
	
	@Override
	public InvoiceDoc setS3Key(String s3Key) {
		super.setS3Key(s3Key);
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
}
