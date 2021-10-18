package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Note.NOTE;
import java.sql.Timestamp;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.NoteFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.NoteProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.Note;

public class NoteDAO {
	private static final NotePropertiesDAO NOTE_PROPERTIES = new NotePropertiesDAO();

	protected static class NotePropertiesDAO implements NoteProperties {
		protected Condition[] getConditions(NoteFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(NOTE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(NOTE.DOMAIN);}
		@Override public Property<Integer> getOwnerProperty() {return new FilterDAO.PropertyDAO<>(NOTE.OWNER);}
		@Override public Property<String> getSubjectProperty() {return new FilterDAO.PropertyDAO<>(NOTE.SUBJECT);}
		@Override public Property<String> getNoteProperty(){return new FilterDAO.PropertyDAO<>(NOTE.NOTE_);}
		@Override public Property<Timestamp> getDateProperty(){return new FilterDAO.PropertyDAO<>(NOTE.DATE);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, NoteFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(NOTE)
				.where(NOTE_PROPERTIES.getConditions(filter));
	}
	
	public static Note get(AONContext ctx,NoteFilter filter) {
		ctx.checkRead();
		return select(ctx, filter).limit(1)
			.stream().map(new NoteFiller())
			.findFirst().orElse(new Note());
	}
	
	public static Stream<Note> getStream(AONContext ctx, NoteFilter filter){	
		return select(ctx, filter).fetch().stream().map(new NoteFiller());
	}
	
	public static Stream<Note> getStream(AONContext ctx, NoteFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter)	
				.offset(perPage * (page -1))
				.fetch().stream().map(new NoteFiller());
	}
	
	
	public static Note update(AONContext ctx, Note note){
		ctx.getDslContext().update(NOTE)
			.set(NOTE.DOMAIN, note.getDomain())
			.set(NOTE.OWNER, note.getOwner())
			.set(NOTE.SUBJECT, note.getSubject())
			.set(NOTE.NOTE_, note.getNote())
			.set(NOTE.DATE, new Timestamp(note.getDate().getTime()))
			.where(NOTE.ID.eq(note.getId()))
			.execute();
		ctx.log().debug("UPDATE NOTE id: " + note.getId());		
		return note;
	}
	
	public static Note insert(AONContext ctx, Note note) {
		Integer id  = ctx.getDslContext()
				.insertInto(NOTE)
				.set(NOTE.DOMAIN, note.getDomain())
				.set(NOTE.OWNER, note.getOwner())
				.set(NOTE.SUBJECT, note.getSubject())
				.set(NOTE.NOTE_, note.getNote())
				.set(NOTE.DATE, new Timestamp(note.getDate().getTime()))
				.returning(NOTE.ID).fetchOne().getId();
		note.setId(id);
		ctx.log().debug("INSERT NOTE id: " + id);		
		return note;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		ctx.getDslContext().delete(NOTE).where(NOTE.ID.eq(id)).execute();	
		ctx.log().debug("DELETE NOTE id: " + id);		
	}
	
	private static class NoteFiller implements Function<Record, Note> {
		@Override
		public Note apply(Record r) {
			return new Note()
					.setId(r.getValue(NOTE.ID))
					.setDomain(r.getValue(NOTE.DOMAIN))
					.setOwner(r.getValue(NOTE.OWNER))
					.setSubject(r.getValue(NOTE.SUBJECT))
					.setNote(r.getValue(NOTE.NOTE_))
					.setDate(r.getValue(NOTE.DATE))
					;		
		}
	}
}
