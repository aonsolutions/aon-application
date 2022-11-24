package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Relationship.RELATIONSHIP;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.jooq.tables.records.RelationshipRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RelationshipFilter;
import com.esferalia.aon.occam.api.model.Properties.RelationshipProperties;
import com.esferalia.aon.occam.api.model.Relationship;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;

public class RelationshipDAO {
	
	private RelationshipDAO() {}
	
	private static final RelationshipPropertiesDAO RELATIONSHIP_PROPERTIES = new RelationshipPropertiesDAO();
	
	private static class RelationshipPropertiesDAO implements RelationshipProperties {
		private Condition[] getConditions(RelationshipFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RELATIONSHIP.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RELATIONSHIP.DOMAIN);}
		@Override public Property<String> getDescriptionProperty()  {return new FilterDAO.PropertyDAO<>(RELATIONSHIP.DESCRIPTION);}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, RelationshipFilter filter) {
		return ctx.getDslContext().select()
			.from(RELATIONSHIP)
			.innerJoin(DOMAIN).on(DOMAIN.ID.eq(RELATIONSHIP.DOMAIN))
			.where(RELATIONSHIP_PROPERTIES.getConditions(filter));
	}

	public static Optional<Relationship> get(AONContext ctx, RelationshipFilter filter){
		return getStream(ctx, filter).findFirst();
	}
	
	public static Stream<Relationship> getStream(AONContext ctx, RelationshipFilter filter) {
		ctx.checkRead();
		return select(ctx,filter)
			.orderBy(RELATIONSHIP.ID.desc())
			.fetch()
			.stream()
			.map(new RelationshipFiller());
	}
	
	public static Relationship save(AONContext ctx, Relationship relationship){
		ctx.checkWrite();
		
		return relationship.getId()!=null && relationship.getId()!=-1 ? update(ctx, relationship) : insert(ctx, relationship);
	}
	
	private static Relationship insert(AONContext ctx, Relationship relationship){
		InsertSetMoreStep<RelationshipRecord> r = ctx.getDslContext()
			.insertInto(RELATIONSHIP)
			.set(RELATIONSHIP.DOMAIN, relationship.getDomain().getId())
			.set(RELATIONSHIP.DESCRIPTION, relationship.getDescription());
		
		if(relationship.getId()!=null) {
			r.set(RELATIONSHIP.ID, relationship.getId());
		}
		
		Integer id = r.returning(RELATIONSHIP.ID).fetchOne().getValue(RELATIONSHIP.ID);
		
		relationship.setId(id);
		
		ctx.log().debug("INSERT RELATIONSHIP DATA  id: {0}", relationship.getId());
		
		return relationship;
	}
	
	private static Relationship update(AONContext ctx, Relationship relationship){
		int count = ctx.getDslContext()
		.update(RELATIONSHIP)
		.set(RELATIONSHIP.DESCRIPTION, relationship.getDescription())
		.where(RELATIONSHIP.ID.eq(relationship.getId()))
		.execute();
		ctx.log().debug("INSERT RELATIONSHIP id: {0} ({1} rows)", relationship.getId(), count);
		return relationship;
	}
	
	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f-> f.getIdProperty().eq(id));
		ctx.log().debug("DELETE RELATIONSHIP id: {0}", id);
	}	
	
	private static void delete(AONContext ctx, RelationshipFilter filter){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RELATIONSHIP)
			.where(RELATIONSHIP_PROPERTIES.getConditions(filter))
			.execute();
		ctx.log().debug("DELETE RELATIONSHIP: ({0} rows)", count);
	}	
	
	public static class RelationshipFiller implements Function<Record, Relationship> {
		
		public Relationship apply(Record r) {
			return build(r);
		}
		
		public static Relationship build(Record r) {
			return new Relationship()
				.setId(r.getValue(RELATIONSHIP.ID))
				.setDomain(DomainFiller.build(r))
				.setDescription(r.getValue(RELATIONSHIP.DESCRIPTION))
			;	
		}
	}
}
