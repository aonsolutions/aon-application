package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;


import java.util.Map;

import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;

public class D2MVELContext extends AccMiningMVELContext {
	
	private Map<String, String> map;
	
	public D2MVELContext(Map<String, String> map,IAccMiningKeyAccept resolver) {
		super(resolver);
		this.map = map;
	}
	
}
