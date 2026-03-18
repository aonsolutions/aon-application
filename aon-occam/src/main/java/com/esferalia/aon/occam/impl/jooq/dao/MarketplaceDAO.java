package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.impl.jooq.dao.TagDAO.TagFiller;

public class MarketplaceDAO {

	public static LinkedList<Tag> getMarketplaceTagList(AONContext ctx){
		return ctx.getDslContext()
				.select().from(TAG).where(TAG.DOMAIN.eq(ctx.getDomainId()))
				.and(TAG.TYPE.eq(TagType.MARKETPLACE.value()))
				.orderBy(TAG.NAME)
				.fetchInto(TAG).stream().map(new TagFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
}
