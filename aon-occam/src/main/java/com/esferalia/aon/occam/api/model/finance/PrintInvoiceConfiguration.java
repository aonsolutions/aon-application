package com.esferalia.aon.occam.api.model.finance;

import java.io.InputStream;

public class PrintInvoiceConfiguration {
	
	private InputStream backgroundImage;
	private Integer header;
	private Integer footer;
	private Boolean detailed;
	private Boolean adjustImage;
	
	public PrintInvoiceConfiguration() {
	
	}

	public InputStream getBackgroundImage() {
		return backgroundImage;
	}

	public PrintInvoiceConfiguration setBackgroundImage(InputStream backgroundImage) {
		this.backgroundImage = backgroundImage;
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
