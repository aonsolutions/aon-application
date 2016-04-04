package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.util.function.Function;

import com.esferalia.aon.jooq.tables.records.TagRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Tag;

public class TagDAO {
	
	public static Tag getTag(AONContext ctx, Integer tagId){
		return ctx.getDslContext().select().from(TAG).where(TAG.ID.eq(tagId)).fetchInto(TAG)
				.stream().map(new FullTagFiller()).findFirst().orElse(new Tag());
	}
	
	public static void updateTag(AONContext ctx, Tag tag){
		ctx.getDslContext().update(TAG)
			.set(TAG.NAME, tag.getName())
			.set(TAG.COLOR, tag.getColor())
			.set(TAG.DOMAIN, tag.getDomain())
			.set(TAG.TYPE, tag.getType())
			.where(TAG.ID.eq(tag.getId()))
			.execute();
	}
	
	public static void deleteTag(AONContext ctx, Tag tag){
		ctx.getDslContext().delete(TAG).where(TAG.ID.eq(tag.getId())).execute();
	}
	
	private static class FullTagFiller implements Function<TagRecord, Tag> {
		@Override
		public Tag apply(TagRecord r) {
			return new Tag().setId(r.getId())
					.setColor(r.getColor())
					.setDomain(r.getDomain())
					.setName(r.getName())
					.setType(r.getType());		
		}
	}
}
