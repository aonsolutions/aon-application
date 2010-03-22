package com.code.aon.marketplace.plu;

import java.util.ArrayList;
import java.util.List;

public class PluItemSet {

	int code;

	private List<PluItem> itemlist = new ArrayList<PluItem>();
	
	public PluItemSet(int code){
		super();
		this.code = code;
	}
	
	
	public int getCode() {
		return code;
	}


	public List<PluItem> getPluItemlist() {
		return itemlist;
	}

	
	
}
