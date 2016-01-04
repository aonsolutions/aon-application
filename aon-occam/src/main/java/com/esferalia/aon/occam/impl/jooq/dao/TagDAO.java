package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Tag.TAG;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Tag;

public class TagDAO {
	
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
}
