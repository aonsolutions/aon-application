package com.esferalia.aon.occam.api.model.warehouse;

import java.util.HashMap;
import java.util.Map;

public class Barcode {
	
	String value;
	BarcodeType type;
	
	public String getValue() {
		return value;
	}
	
	public Barcode setValue(String value) {
		this.value = value;
		return this;
	}
	
	public BarcodeType getType() {
		return type;
	}
	
	public Barcode setType(BarcodeType type) {
		this.type = type;
		return this;
	}
	
	public Map<GS1128Codes, String> parseGS1128() {
		Map<GS1128Codes, String> map = new HashMap<>();
		String barcode = getValue();
		while(barcode.length() > 0) {
			String key = barcode.substring(0, 2);
			GS1128Codes gs1Code = GS1128Codes.safeValueOf(key);
			if(gs1Code == null) {
				key = barcode.substring(0, 3);
				gs1Code = GS1128Codes.safeValueOf(key);
			}
			if(gs1Code == null) return map;
			
			if(!gs1Code.isSeparator()) {
				map.put(gs1Code, barcode.substring(gs1Code.getKeyLength(), gs1Code.getKeyLength() + gs1Code.getValueLength()));
				barcode = barcode.substring(gs1Code.getKeyLength() + gs1Code.getValueLength());
			} else if(barcode.contains("\u001d") || barcode.contains("\f")) {
				Integer index = barcode.contains("\u001d") ? barcode.indexOf('\u001d') : barcode.indexOf('\f');
				map.put(gs1Code, barcode.substring(gs1Code.getKeyLength(), index));
				barcode = barcode.substring(index + 1);
			} else {
				map.put(gs1Code, barcode.substring(gs1Code.getKeyLength()));
				return map;
			}
		}
		
		return map;
	}
}
