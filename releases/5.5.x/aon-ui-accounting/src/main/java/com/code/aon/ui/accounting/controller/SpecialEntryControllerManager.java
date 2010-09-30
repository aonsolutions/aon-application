package com.code.aon.ui.accounting.controller;

import java.util.HashMap;
import java.util.Map;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.ui.util.AonUtil;

public class SpecialEntryControllerManager {

	private Map<AccountEntryType,String> controllersPointers;
	
	private Map<AccountEntryType,String> getControllersPointers() {
		if (controllersPointers == null) {
			controllersPointers = new HashMap<AccountEntryType, String>();
		}
		return controllersPointers;
	}

	public void register(AccountEntryType type,String controllerName) {
		Map<AccountEntryType,String> map = getControllersPointers();
		map.put(type, controllerName);
	}
	
	public ISpecialAccountEntry getSpecialEntryController(AccountEntryType type) {
		Map<AccountEntryType,String> map = getControllersPointers();
		if (!map.containsKey(type)) {
			throw new IllegalArgumentException("Type: " + type + " not supported.");
		}
		String name = map.get(type);
		Object o = AonUtil.getRegisteredBean(name);
		if (!(o instanceof ISpecialAccountEntry)) {
			throw new IllegalArgumentException("Type: " + type + " not a Special Account Entry Controller.");
		}
		return (ISpecialAccountEntry) o;		
	}
	
}
