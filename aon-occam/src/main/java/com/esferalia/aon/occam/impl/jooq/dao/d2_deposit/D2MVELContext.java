package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;


import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;

public class D2MVELContext extends AccMiningMVELContext {
	
	private Esquema schema;
	
	public D2MVELContext(Esquema schema,IAccMiningKeyAccept resolver) {
		super(resolver);
		this.schema = schema;
	}
	
}
