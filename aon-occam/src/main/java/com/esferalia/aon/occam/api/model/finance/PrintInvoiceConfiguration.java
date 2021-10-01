package com.esferalia.aon.occam.api.model.finance;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.watson.server.io.AonIOUtils;

public class PrintInvoiceConfiguration {
	
	private Integer header;
	private Integer footer;
	private Boolean detailed;
	private Boolean adjustImage;
	private Attach background;
	

	@Deprecated
	public InputStream getBackgroundImage() {
		return background.getData() != null ? new ByteArrayInputStream(background.getData()) : null;
	}
	
	@Deprecated
	public PrintInvoiceConfiguration setBackgroundImage(InputStream is) {
		if(background == null) background = new Attach();
		try {
			byte[] data = AonIOUtils.toByteArray(is);
			background.setData(data); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public Attach getBackground() {
		return background;
	}
	
	public PrintInvoiceConfiguration setBackground(Attach background) {
		this.background = background;
		return this;
	}
	
	public Integer getHeader() {
		if(header == null) header = 100;
		return header;
	}

	public PrintInvoiceConfiguration setHeader(Integer header) {
		this.header = header;
		return this;
	}

	public Integer getFooter() {
		if(footer == null) footer = 100;
		return footer;
	}

	public PrintInvoiceConfiguration setFooter(Integer footer) {
		this.footer = footer;
		return this;	
	}

	public Boolean isDetailed() {
		return detailed;
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
