package com.code.aon.marketplace.plu;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BokaData extends Data {

	int BONU;
	int STYP;
	int SNR1;
	double BT10;
	double BT20;
	int POS1;
	double GEW1;
	Date ZEIS;

	public BokaData(int bonu, int styp, int snr1, double bt10, double bt20, int pos1, double gew1, Date zeis) {
		super();
		BONU = bonu;
		STYP = styp;
		SNR1 = snr1;
		BT10 = bt10;
		BT20 = bt20;
		POS1 = pos1;
		GEW1 = gew1;
		ZEIS = zeis;
	}
	
	public int getBONU() {
		return BONU;
	}

	public void setBONU(int bonu) {
		BONU = bonu;
	}

	public double getBT10() {
		return BT10;
	}

	public void setBT10(double bt10) {
		BT10 = bt10;
	}

	public double getBT20() {
		return BT20;
	}

	public void setBT20(double bt20) {
		BT20 = bt20;
	}

	public double getGEW1() {
		return GEW1;
	}

	public void setGEW1(double gew1) {
		GEW1 = gew1;
	}

	public int getPOS1() {
		return POS1;
	}

	public void setPOS1(int pos1) {
		POS1 = pos1;
	}

	public int getSNR1() {
		return SNR1;
	}

	public void setSNR1(int snr1) {
		SNR1 = snr1;
	}

	public int getSTYP() {
		return STYP;
	}

	public void setSTYP(int styp) {
		STYP = styp;
	}

	public Date getZEIS() {
		return ZEIS;
	}

	public void setZEIS(Date zeis) {
		ZEIS = zeis;
	}

	public String getBONUFilled(int length) {
		String ret = FieldUtils.ZeroFill(BONU,length);
		return ret;
	}

	public String getSTYPFilled(int length) {
		String ret = FieldUtils.ZeroFill(STYP,length);
		return ret;
	}

	public String getSNR1Filled(int length) {
		String ret = FieldUtils.ZeroFill(SNR1,length);
		return ret;
	}

	public String getBT10Filled(int length) {
		int temp = (new Double(100 * BT10)).intValue();
		String ret = FieldUtils.ZeroFill(temp,length);
		return ret;
	}

	public String getBT20Filled(int length) {
		int temp = (new Double(100 * BT20)).intValue();
		String ret = FieldUtils.ZeroFill(temp,length);
		return ret;
	}

	public String getPOS1Filled(int length) {
		String ret = FieldUtils.ZeroFill(POS1,length);
		return ret;
	}

	public String getGEW1Filled(int length) {
		int temp = (new Double(100 * GEW1)).intValue();
		String ret = FieldUtils.ZeroFill(temp,length);
		return ret;
	}

	public String getZEISFilled(int length, String dateformat) {
		String temp = (new SimpleDateFormat(dateformat)).format(ZEIS);
		String ret = FieldUtils.SpaceFill(temp,length);
		return ret;
	}

	public String getLine(BokaIf boka) {
		String text = boka.getMask();
		text = text.replaceAll("\\$bonu", getBONUFilled(boka.getBONU().getLength()));
		text = text.replaceAll("\\$styp", getSTYPFilled(boka.getSTYP().getLength()));
		text = text.replaceAll("\\$snr1", getSNR1Filled(boka.getSNR1().getLength()));
		text = text.replaceAll("\\$bt10", getBT10Filled(boka.getBT10().getLength()));
		text = text.replaceAll("\\$bt20", getBT20Filled(boka.getBT20().getLength()));
		text = text.replaceAll("\\$pos1", getPOS1Filled(boka.getPOS1().getLength()));
		text = text.replaceAll("\\$gew1", getGEW1Filled(boka.getGEW1().getLength()));
		text = text.replaceAll("\\$zeis", getZEISFilled(boka.getZEIS().getLength(), boka.getDateformat()));
		return text;
	}

}
