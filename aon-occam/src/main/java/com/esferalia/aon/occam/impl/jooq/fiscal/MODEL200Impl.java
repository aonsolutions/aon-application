package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL200;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.impl.jooq.dao.Mod200DAO;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class MODEL200Impl implements IMODEL200 {

	@Override
	public LinkedList<Mod200> getMod200s(AONContext ctx, int domain) {
		LinkedList<Mod200> list = new LinkedList<Mod200>();
		Mod200DAO.getMod200s(ctx, domain)
			.forEach(list::add);
		return list;
	}

	@Override
	public Mod200 getMod200(AONContext ctx, Integer id) {
		return Mod200DAO.getMod200s(ctx, ctx.getDomainId())
				.filter(mod-> AonNumberUtils.equals(mod.getId(), id))
				.findFirst()
				.orElse(null);
	}
	
}
