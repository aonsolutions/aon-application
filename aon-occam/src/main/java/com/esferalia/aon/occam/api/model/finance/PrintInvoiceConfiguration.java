package com.esferalia.aon.occam.api.model.finance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.esferalia.aon.occam.api.model.attachment.Attach;

public class PrintInvoiceConfiguration {
	
	private Integer header;
	private Integer footer;
	private Boolean detailed;
	private Boolean adjustImage;
	private Attach background;
	
	public PrintInvoiceConfiguration() {
	
	}

	public InputStream getBackgroundImage() {
		return background.getData() != null ? new ByteArrayInputStream(background.getData()) : null;
	}

	public Attach getBackground() {
		return background;
	}
	
	public PrintInvoiceConfiguration setBackground(Attach background) {
		this.background = background;
		return this;
	}
	
	public Integer getHeader() {
		return header;
	}

	public PrintInvoiceConfiguration setHeader(Integer header) {
		this.header = header;
		return this;
	}

	public Integer getFooter() {
		return footer;
	}

	public PrintInvoiceConfiguration setFooter(Integer footer) {
		this.footer = footer;
		return this;	
	}

	public Boolean getDetailed() {
		return detailed;
	}

	public PrintInvoiceConfiguration setDetailed(Boolean detailed) {
		this.detailed = detailed;
		return this;
	}

	public Boolean getAdjustImage() {
		return adjustImage;
	}

	public PrintInvoiceConfiguration setAdjustImage(Boolean adjustImage) {
		this.adjustImage = adjustImage;
		return this;
	}
}
