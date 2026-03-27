package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Rnote.RNOTE;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RegistryNoteFilter;
import com.esferalia.aon.occam.api.model.Properties.RegistryNoteProperties;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class RegistryNoteDAO {
	
	private RegistryNoteDAO() {
		
	}
	
	private static final RegistryNotePropertiesDAO RNOTE_PROPERTIES = new RegistryNotePropertiesDAO();
	private static class RegistryNotePropertiesDAO implements RegistryNoteProperties {
		
		private Condition[] getConditions(RegistryNoteFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RNOTE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RNOTE.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RNOTE.REGISTRY);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(RNOTE.DESCRIPTION);}
		@Override public Property<Date> getNoteDateProperty() {return new FilterDAO.PropertyDAO<>(RNOTE.NOTE_DATE);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(RNOTE.COMMENTS);}
		@Override public Property<Byte> getNoteTypeProperty() {return new FilterDAO.PropertyDAO<>(RNOTE.NOTE_TYPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(RNOTE.SECURITY_LEVEL);}
	}

	public static class RegistryNoteFiller extends Filler implements Function<Record, RegistryNote> {

		@Override
		public RegistryNote apply(Record r) {
			return build(r);				
		}
		
		public static RegistryNote build(Record r) {
			return new RegistryNote()
				.setId(getValue(r, RNOTE.ID))
				.setDomain(getValue(r, RNOTE.DOMAIN))
				.setRegistry(getValue(r, RNOTE.REGISTRY))
				.setComments(getValue(r, RNOTE.COMMENTS))
				.setDescription(getValue(r, RNOTE.DESCRIPTION))
				.setNoteDate(getValue(r, RNOTE.NOTE_DATE))
				.setNoteType(NoteType.safeValueOf(getValue(r, RNOTE.NOTE_TYPE)))
				.setSecurityLevel(SecurityLevel.safeValueOf(getValue(r, RNOTE.SECURITY_LEVEL)));
		}
	}
	
	private static SelectConditionStep<Record> select(AONContext ctx, RegistryNoteFilter filter) {
		return ctx.getDslContext().select()
			.from(RNOTE)
			.where(RNOTE_PROPERTIES.getConditions(filter));
	}

	
	public static RegistryNote get(AONContext ctx, RegistryNoteFilter filter){
		return select(ctx,filter)
			.limit(1)
			.fetch()
			.stream()
			.map(new RegistryNoteFiller())
			.findFirst()
			.orElse(new RegistryNote());
	}
	
	public static Stream<RegistryNote> getStream(AONContext ctx, RegistryNoteFilter filter) {
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new RegistryNoteFiller());
	}
	
	public static List<RegistryNote> getList(AONContext ctx, RegistryNoteFilter filter) {
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static RegistryNote save(AONContext ctx, RegistryNote rnote){
		return rnote.getId() == null
			? insert(ctx, rnote)
			: update(ctx, rnote);
	}
	
	public static RegistryNote insert(AONContext ctx, RegistryNote rnote){
		ctx.checkWrite();
		Integer id = ctx.getDslContext()
			.insertInto(RNOTE)
			.set(RNOTE.DOMAIN, rnote.getDomain())
			.set(RNOTE.REGISTRY,rnote.getRegistry())
			.set(RNOTE.DESCRIPTION, rnote.getDescription())
			.set(RNOTE.NOTE_DATE, AonDateUtils.toSql(rnote.getNoteDate()))
			.set(RNOTE.COMMENTS, rnote.getComments())
			.set(RNOTE.NOTE_TYPE, rnote.getNoteType().value())
			.set(RNOTE.SECURITY_LEVEL, rnote.getSecurityLevel().value())
			.returning(RNOTE.ID)
			.fetchOne()
			.getValue(RNOTE.ID);
		rnote.setId(id);
		ctx.log().debug("INSERT REGISTRY NOTE ( registry: {0}) id: {1}", rnote.getRegistry(), rnote.getId());
		return rnote; 
	}
	
	public static RegistryNote update(AONContext ctx, RegistryNote rnote){
		ctx.checkWrite();
		int count = ctx.getDslContext().update(RNOTE)
			.set(RNOTE.DOMAIN, rnote.getDomain())
			.set(RNOTE.REGISTRY,rnote.getRegistry())
			.set(RNOTE.DESCRIPTION, rnote.getDescription())
			.set(RNOTE.NOTE_DATE, AonDateUtils.toSql(rnote.getNoteDate()))
			.set(RNOTE.COMMENTS, rnote.getComments())
			.set(RNOTE.NOTE_TYPE, rnote.getNoteType().value())
			.set(RNOTE.SECURITY_LEVEL, rnote.getSecurityLevel().value())
			.where(RNOTE.ID.eq(rnote.getId()))
			.execute();
		ctx.log().debug("UPDATE MEDIA ( registry: {0}) id: {1}. ({2} rows)", rnote.getRegistry(), rnote.getId(),count);
		return rnote; 
	}
	
	public static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RNOTE)
			.where(RNOTE.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE REGISTRY NOTE id: {0} ({1} rows)",id, count);
	}	
	
	public static void delete(AONContext ctx, RegistryNoteFilter filter){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RNOTE)
			.where(RNOTE_PROPERTIES.getConditions(filter))
			.execute();
		ctx.log().debug("DELETE REGISTRY NOTE: ({0} rows)", count);
	}

	public static void saveRegistryObservation(AONContext ctx, Integer domain, Integer registry, String observation) {
		RegistryNote existingObservation = get(ctx, f -> f.getRegistryProperty().eq(registry).and(f.getNoteTypeProperty().eq(NoteType.OBSERVATION.value())));
		
		if(AonStringUtils.isBlank(observation) && null != existingObservation.getId())
			delete(ctx, existingObservation.getId());
		else if(AonStringUtils.isNotBlank(observation)) {
			if(null != existingObservation.getId()) {
				existingObservation.setComments(observation);
				update(ctx, existingObservation);
			} else {
				RegistryNote newObservation = new RegistryNote()
						.setDomain(domain)
						.setRegistry(registry)
						.setDescription("Observaci\u00f3n")
						.setComments(observation)
						.setNoteType(NoteType.OBSERVATION)
						.setNoteDate(new java.util.Date())
						.setSecurityLevel(SecurityLevel.OFFICIAL);
				insert(ctx, newObservation);
			}
		}
		
	}	

}
