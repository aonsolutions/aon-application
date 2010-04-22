package com.code.aon.marketplace.plu;

public class Data {

	public String getOtherFilled(int length, String type) {
		String ret = "";
		if (type.equals("N")) {
			FieldUtils.ZeroFill(0, length);
		}
		else {
			FieldUtils.SpaceFill("", length);
		}
		return ret;
	}

}
