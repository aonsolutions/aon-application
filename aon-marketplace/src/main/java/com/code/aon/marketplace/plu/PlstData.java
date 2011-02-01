package com.code.aon.marketplace.plu;

public class PlstData extends Data {

	int PNUM;
	double GPR1;
	int WGNU;
	String ECO1;
	String PLTE;

	

	public PlstData(int pnum, double gpr1, int wgnu, String eco1, String plte) {
		super();
		PNUM = pnum;
		GPR1 = gpr1;
		WGNU = wgnu;
		ECO1 = eco1;
		PLTE = plte;
	}

	public String getECO1() {
		return ECO1;
	}

	public void setECO1(String eco1) {
		ECO1 = eco1;
	}

	public double getGPR1() {
		return GPR1;
	}

	public void setGPR1(double gpr1) {
		GPR1 = gpr1;
	}

	public String getPLTE() {
		return PLTE;
	}

	public void setPLTE(String plte) {
		PLTE = plte;
	}

	public int getPNUM() {
		return PNUM;
	}

	public void setPNUM(int pnum) {
		PNUM = pnum;
	}

	public int getWGNU() {
		return WGNU;
	}

	public void setWGNU(int wgnu) {
		WGNU = wgnu;
	}

	public String getPNUMFilled(int length) {
		String ret = FieldUtils.ZeroFill(PNUM,length);
		return ret;
	}

	public String getGPR1Filled(int length) {
		int temp = (new Double(100 * GPR1)).intValue();
		String ret = FieldUtils.ZeroFill(temp,length);
		return ret;
	}

	public String getWGNUFilled(int length) {
		String ret = FieldUtils.ZeroFill(WGNU,length);
		return ret;
	}
	
	public String getECO1Filled(int length) {
		String ret = FieldUtils.SpaceFill(ECO1,length);
		return ret;
	}
	
	public String getPLTEFilled(int length) {
		String ret = FieldUtils.SpaceFill(PLTE,length);
		return ret;
	}

	public String getLine(PlstIf plst) {
		String text = plst.getMask();
		text = text.replaceAll("\\$pnum", getPNUMFilled(plst.getPNUM().getLength()));
		text = text.replaceAll("\\$gpr1", getGPR1Filled(plst.getGPR1().getLength()));
		text = text.replaceAll("\\$wgnu", getWGNUFilled(plst.getWGNU().getLength()));
		text = text.replaceAll("\\$eco1", getECO1Filled(plst.getECO1().getLength()));
		text = text.replaceAll("\\$plte", getPLTEFilled(plst.getPLTE().getLength()));
		return text;
	}
	
}
