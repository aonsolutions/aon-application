package com.esferalia.aon.occam.impl.jooq.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL3902015;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390.Mod3902015DAO;

public class MODEL3902015Impl implements IMODEL3902015 {

	@Override
	public Mod3902015 get(AONContext ctx, Mod390 mod390) {
		return Mod3902015DAO.getMod3902015(ctx, mod390);
	}

	@Override
	public Mod3902015 get(AONContext ctx, Integer id) {
		return Mod3902015DAO.getById(ctx, id);
	}

	@Override
	public String getXML(AONContext ctx, int id) {
		return Mod3902015DAO.getXMLContentById(ctx, id);
	}
	
	@Override
	public Mod3902015 save(AONContext ctx, Mod3902015 mod390) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902015DAO.save(ctx, mod390));
	}

	@Override
	public void delete(AONContext ctx, Mod3902015 mod390) {
		ctx.getDslContext().transaction(
			configuration -> Mod3902015DAO.delete(ctx, mod390));
	}
	
	@Override
	public Mod3902015 changeStatus(AONContext ctx, Mod3902015 mod390, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902015DAO.changeStatus(ctx, mod390, newStatus));		
	}


}
