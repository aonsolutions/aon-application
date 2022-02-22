package com.esferalia.aon.in.payroll.pdf.maker.settlement;

import static com.esferalia.aon.in.payroll.pdf.api.toolkit.DataToolkit.ReadAllBytesSafely;

import java.io.InputStream;
import java.util.Locale;

import com.esferalia.aon.in.payroll.pdf.api.bean.PrintConfiguration;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.DataToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement;

public class SettlePrintConfiguration extends PrintConfiguration {

	private Settlement	 settlement;
	private byte[]		 logo;
	private byte[]		 signature;

	public SettlePrintConfiguration(Settlement settlement, InputStream logo, InputStream signature, Locale language) {

		super(language);
		this.settlement	= settlement;
		this.logo = ReadAllBytesSafely(logo);
		this.signature = ReadAllBytesSafely(signature);
	}

	public Settlement getSettlement() {
		return settlement;
	}

	public void setSettlement(Settlement settlement) {
		this.settlement = settlement;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}
	
	public byte[] getSignature() {
		return signature;
	}
	
	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

}
