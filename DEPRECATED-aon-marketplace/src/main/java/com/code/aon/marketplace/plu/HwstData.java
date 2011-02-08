package com.code.aon.marketplace.plu;

public class HwstData extends Data {

	int HWGN;
	String HWGT;

	
	public HwstData(int hwgn, String hwgt) {
		super();
		HWGN = hwgn;
		HWGT = hwgt;
	}
	public int getHWGN() {
		return HWGN;
	}
	public void setHWGN(int hwgn) {
		HWGN = hwgn;
	}

	public String getHWGT() {
		return HWGT;
	}
	public void setHWGT(String hwgt) {
		HWGT = hwgt;
	}

	public String getHWGNFilled(int length) {
		String ret = FieldUtils.ZeroFill(HWGN,length);
		return ret;
	}

	public String getHWGTFilled(int length) {
		String ret = FieldUtils.SpaceFill(HWGT,length);
		return ret;
	}

	public String getLine(HwstIf hwst) {
		String text = hwst.getMask();
		text = text.replaceAll("\\$hwgn", getHWGNFilled(hwst.getHWGN().getLength()));
		text = text.replaceAll("\\$hwgt", getHWGTFilled(hwst.getHWGT().getLength()));
		return text;
	}
	
}
