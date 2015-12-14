package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.esferalia.aon.jooq.tables.records.TagRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.TagType;

public class MarketplaceDAO {

	public static LinkedList<Tag> getMarketplaceTagList(AONContext ctx){
		return ctx.getDslContext()
				.select().from(TAG).where(TAG.DOMAIN.eq(ctx.getDomainId()))
				.and(TAG.TYPE.eq(TagType.MARKETPLACE.value()))
				.fetchInto(TAG).stream().map(new FullTagFiller())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static class FullTagFiller implements Function<TagRecord, Tag> {
		@Override
		public Tag apply(TagRecord r) {
			return new Tag()
					.setColor(r.getColor())
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setName(r.getName())
					.setType(r.getType());
		}
	}
	
}
