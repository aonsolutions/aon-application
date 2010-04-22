package com.code.aon.marketplace.plu;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PluItemManager{

	private Map<String,PluItemSet> pluitemsets;
	
	public PluItemManager(){
		super();
		this.pluitemsets= new HashMap<String,PluItemSet>();
	}
	
	public void addItem(PluItem item){
		PluItemSet set = pluitemsets.get(new Integer(item.getCod()));
		if (set == null) {
			set = new PluItemSet(item.getCod());
			pluitemsets.put(""+set.getCode(), set);
		}
		set.getPluItemlist().add(item);
	}
	
	public PluItemSet getPluItemSet(String code){
		return pluitemsets.get(code);
	}
	
	public Collection<PluItemSet> getAll(){
		return pluitemsets.values();
	}
}
