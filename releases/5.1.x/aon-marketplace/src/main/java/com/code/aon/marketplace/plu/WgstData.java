package com.code.aon.marketplace.plu;

public class WgstData extends Data {

	int WGNU;
	int HWGN;
	String WGTE;

	
	public WgstData(int wgnu, int hwgn, String wgte) {
		super();
		WGNU = wgnu;
		HWGN = hwgn;
		WGTE = wgte;
	}
	public int getHWGN() {
		return HWGN;
	}
	public void setHWGN(int hwgn) {
		HWGN = hwgn;
	}
	public int getWGNU() {
		return WGNU;
	}
	public void setWGNU(int wgnu) {
		WGNU = wgnu;
	}
	public String getWGTE() {
		return WGTE;
	}
	public void setWGTE(String wgte) {
		WGTE = wgte;
	}

	public String getHWGNFilled(int length) {
		String ret = FieldUtils.ZeroFill(HWGN,length);
		return ret;
	}

	public String getWGNUFilled(int length) {
		String ret = FieldUtils.ZeroFill(WGNU,length);
		return ret;
	}

	public String getWGTEFilled(int length) {
		String ret = FieldUtils.SpaceFill(WGTE,length);
		return ret;
	}
	
	public String getLine(WgstIf wgst) {
		String text = wgst.getMask();
		text = text.replaceAll("\\$wgnu", getWGNUFilled(wgst.getWGNU().getLength()));
		text = text.replaceAll("\\$hwgn", getHWGNFilled(wgst.getHWGN().getLength()));
		text = text.replaceAll("\\$wgte", getWGTEFilled(wgst.getWGTE().getLength()));
		return text;
	}
	
}
