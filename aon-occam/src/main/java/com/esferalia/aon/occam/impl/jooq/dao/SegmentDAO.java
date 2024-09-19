package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Segment.SEGMENT;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Segment;

public class SegmentDAO {
	
	private SegmentDAO() {
		
	}
	
	public static Stream<Segment> stream(AONContext ctx, int domain){
		return ctx.getDslContext().select().from(SEGMENT)
			.where(SEGMENT.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx, domain)))
			.orderBy(SEGMENT.NAME)
			.fetch()
			.stream()
			.map(new SegmentFiller());
	}
	public static class SegmentFiller extends Filler implements Function<Record,Segment> {
		@Override
		public Segment apply(Record r) {
			return new Segment()
				.setId( getValue(r, SEGMENT.ID) )
				.setDomain(getValue(r, SEGMENT.DOMAIN) )
				.setName(getValue(r, SEGMENT.NAME ))
				;
		}
	}


}
