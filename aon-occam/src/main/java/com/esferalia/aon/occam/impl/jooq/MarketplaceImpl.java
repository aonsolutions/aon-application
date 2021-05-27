package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IMarketplace;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.impl.jooq.dao.MarketplaceDAO;

public class MarketplaceImpl implements IMarketplace {
	
	@Override
	public LinkedList<Tag> getMarketplaceTagList(AONContext ctx) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> MarketplaceDAO.getMarketplaceTagList(ctx));
	}
}
