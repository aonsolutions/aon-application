package com.esferalia.aon.occam.impl.jooq.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL3902018;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390.Mod3902018DAO;

public class MODEL3902018Impl implements IMODEL3902018 {

	@Override
	public Mod3902018 get(AONContext ctx, Mod390 mod390) {
		return Mod3902018DAO.getMod3902018(ctx, mod390);
	}

	@Override
	public Mod3902018 get(AONContext ctx, Integer id) {
		return Mod3902018DAO.getById(ctx, id);
	}

	@Override
	public String getXML(AONContext ctx, int id) {
		return Mod3902018DAO.getXMLContentById(ctx, id);
	}
	
	@Override
	public Mod3902018 save(AONContext ctx, Mod3902018 mod390) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902018DAO.save(ctx, mod390));
	}

	@Override
	public void delete(AONContext ctx, Mod3902018 mod390) {
		ctx.getDslContext().transaction(
			configuration -> Mod3902018DAO.delete(ctx, mod390));
	}
	
	@Override
	public Mod3902018 changeStatus(AONContext ctx, Mod3902018 mod390, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902018DAO.changeStatus(ctx, mod390, newStatus));		
	}

}
