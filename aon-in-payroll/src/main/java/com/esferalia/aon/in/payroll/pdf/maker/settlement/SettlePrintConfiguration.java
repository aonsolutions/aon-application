package com.esferalia.aon.in.payroll.pdf.maker.settlement;

import java.io.InputStream;
import java.util.Locale;

import com.esferalia.aon.in.payroll.pdf.api.bean.PrintConfiguration;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement;

public class SettlePrintConfiguration extends PrintConfiguration {

	private Settlement	 settlement;
	private byte[]		 logo;

	public SettlePrintConfiguration(Settlement settlement, InputStream logo, Locale language) {

		super(language);
		this.settlement	= settlement;

		try
		{
			this.logo = logo.readAllBytes();
		} catch (Exception e)
		{
			this.logo = new byte[0];
		}
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

}
