package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Relationship.RELATIONSHIP;
import static com.esferalia.aon.jooq.tables.Rrelationship.RRELATIONSHIP;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RRelationshipFilter;
import com.esferalia.aon.occam.api.model.Properties.RRelationshipProperties;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RelationshipDAO.RelationshipFiller;
import com.esferalia.aon.occam.impl.jooq.validation.RegistryRelationshipAutoComplete;

public class RegistryRelationshipDAO {
	
	private RegistryRelationshipDAO() {}
	
	private static final RegistryRelationshipPropertiesDAO RRELATIONSHIP_PROPERTIES = new RegistryRelationshipPropertiesDAO();
	
	private static class RegistryRelationshipPropertiesDAO implements RRelationshipProperties {
		private Condition[] getConditions(RRelationshipFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RRELATIONSHIP.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RRELATIONSHIP.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RRELATIONSHIP.REGISTRY);}
		@Override public Property<Integer> getRelatedRegistryProperty() {return new FilterDAO.PropertyDAO<>(RRELATIONSHIP.RELATED_REGISTRY);}
		@Override public Property<Integer> getRelationshipProperty()  {return new FilterDAO.PropertyDAO<>(RRELATIONSHIP.RELATIONSHIP);}
		@Override public Property<String> getCommentsProperty()  {return new FilterDAO.PropertyDAO<>(RRELATIONSHIP.COMMENTS);}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, RRelationshipFilter filter) {
		return ctx.getDslContext().select()
			.from(RRELATIONSHIP)
			.innerJoin(DOMAIN).on(DOMAIN.ID.eq(RRELATIONSHIP.DOMAIN))
			.innerJoin(RELATIONSHIP).on(RELATIONSHIP.ID.eq(RRELATIONSHIP.RELATIONSHIP))
			.where(RRELATIONSHIP_PROPERTIES.getConditions(filter));
	}

	public static Optional<RegistryRelationship> get(AONContext ctx, RRelationshipFilter filter){
		return getStream(ctx, filter).findFirst();
	}
	
	public static Stream<RegistryRelationship> getStream(AONContext ctx, RRelationshipFilter filter) {
		ctx.checkRead();
		return select(ctx,filter)
			.orderBy(RRELATIONSHIP.ID.desc())
			.fetch()
			.stream()
			.map(new RegistryRelationshipFiller());
	}
	
	public static RegistryRelationship save(AONContext ctx, RegistryRelationship rrelationship){
		ctx.checkWrite();
		
		RegistryRelationshipAutoComplete.autoComplete(ctx, rrelationship);
		
		return rrelationship.getId() !=null ? update(ctx, rrelationship) : insert(ctx, rrelationship);
	}
	
	private static RegistryRelationship insert(AONContext ctx, RegistryRelationship rrelationship){
		return getStream(ctx, 
			// --------------------CHECK REPEAT-------------------
			f-> f.getDomainProperty().eq(rrelationship.getDomain().getId())
			.and(f.getRegistryProperty().eq(rrelationship.getRegistry()))
			.and(f.getRelatedRegistryProperty().eq(rrelationship.getRelatedRegistry()))
			.and(f.getRelationshipProperty().eq(rrelationship.getRelationship().getId()))
		)
		.findFirst()
		.orElseGet(() ->{
			Integer id = ctx.getDslContext()
			.insertInto(RRELATIONSHIP)
			.set(RRELATIONSHIP.DOMAIN, rrelationship.getDomain().getId())
			.set(RRELATIONSHIP.REGISTRY, rrelationship.getRegistry())
			.set(RRELATIONSHIP.RELATED_REGISTRY, rrelationship.getRelatedRegistry())
			.set(RRELATIONSHIP.RELATIONSHIP, rrelationship.getRelationship().getId())
			.set(RRELATIONSHIP.COMMENTS, rrelationship.getComments())
			.returning(RRELATIONSHIP.ID)
			.fetchOne()
			.getValue(RRELATIONSHIP.ID);
			rrelationship.setId(id);
			ctx.log().debug("INSERT REGISTRY RELATIONSHIP DATA  id: {0}", rrelationship.getId());
			return rrelationship;
		});
	}
	
	private static RegistryRelationship update(AONContext ctx, RegistryRelationship rrelationship){
		int count = ctx.getDslContext()
			.update(RRELATIONSHIP)
			.set(RRELATIONSHIP.COMMENTS, rrelationship.getComments())
			.set(RRELATIONSHIP.RELATIONSHIP, rrelationship.getRelationship().getId())
			.where(RRELATIONSHIP.ID.eq(rrelationship.getId()))
			.execute();
		
		ctx.log().debug("INSERT REGISTRY RRELATIONSHIP id: {0} ({1} rows)", rrelationship.getId(), count);
		return rrelationship;
	}
	
	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f-> f.getIdProperty().eq(id));
		ctx.log().debug("DELETE REGISTRY RRELATIONSHIP id: {0}", id);
	}	
	
	private static void delete(AONContext ctx, RRelationshipFilter filter){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RRELATIONSHIP)
			.where(RRELATIONSHIP_PROPERTIES.getConditions(filter))
			.execute();
		ctx.log().debug("DELETE REGISTRY RRELATIONSHIP: ({0} rows)", count);
	}	
	
	public static class RegistryRelationshipFiller implements Function<Record, RegistryRelationship> {
		
		public RegistryRelationship apply(Record r) {
			return build(r);
		}
				
		public RegistryRelationship build(Record r) {
			return new RegistryRelationship()
					.setId(r.getValue(RRELATIONSHIP.ID))
					.setRegistry(r.getValue(RRELATIONSHIP.REGISTRY))
					.setRelatedRegistry(r.getValue(RRELATIONSHIP.RELATED_REGISTRY))
					.setComments(r.getValue(RRELATIONSHIP.COMMENTS))
					.setDomain(DomainFiller.build(r))
					.setRelationship(RelationshipFiller.build(r))
					;
		}
	}
}
