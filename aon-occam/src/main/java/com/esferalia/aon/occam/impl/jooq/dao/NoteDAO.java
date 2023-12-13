package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Note.NOTE;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.NoteFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.NoteProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.Note;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NoteDAO {
	private static final NotePropertiesDAO NOTE_PROPERTIES = new NotePropertiesDAO();
	
	public static final String DATE_DEFAULT = "9999-01-01"; 
	 
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
		@Override public Property<Byte> getArchiveProperty(){return new FilterDAO.PropertyDAO<>(NOTE.ARCHIVE);}
		@Override public Property<Byte> getPinUpProperty(){return new FilterDAO.PropertyDAO<>(NOTE.PIN_UP);}
		@Override public Property<String> getNoteTagProperty(){return new FilterDAO.PropertyDAO<>(NOTE.NOTE_TAG);}
		@Override public Property<String> getColorProperty(){return new FilterDAO.PropertyDAO<>(NOTE.COLOR);}
		@Override public Property<Timestamp> getArchiveDateProperty(){return new FilterDAO.PropertyDAO<>(NOTE.ARCHIVE_DATE);}
		@Override public Property<Timestamp> getModificationDateProperty(){return new FilterDAO.PropertyDAO<>(NOTE.MODIFICATION_DATE);}
		
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
	
	
	public static Note save(AONContext ctx, Note note) {
		autoComplete(note);
		return (note.getId() != null && note.getId() > 0)
			? update(ctx, note)
			: insert(ctx, note); 
	}
	
	private static Note update(AONContext ctx, Note note){
		ctx.getDslContext().update(NOTE)
			.set(NOTE.DOMAIN, note.getDomain())
			.set(NOTE.OWNER, note.getOwner())
			.set(NOTE.SUBJECT, note.getSubject()!=null?  note.getSubject() : "")
			.set(NOTE.NOTE_, note.getNote())
			.set(NOTE.DATE, new Timestamp(note.getDate().getTime()))
			.set(NOTE.ARCHIVE, note.isArchive() ? (byte)1 : (byte)0)
			.set(NOTE.PIN_UP, note.isPinpUp() ? (byte)1 : (byte)0)
			.set(NOTE.NOTE_TAG, AonStringUtils.isBlank(note.getNoteTag()) ? null : note.getNoteTag().toUpperCase())
			.set(NOTE.COLOR, note.getColor())
			.set(NOTE.ARCHIVE_DATE, null == note.getArchiveDate() ? null : new Timestamp(note.getArchiveDate().getTime()))
			.set(NOTE.MODIFICATION_DATE, null == note.getModificationDate() ? null : new Timestamp(note.getModificationDate().getTime()))
			.where(NOTE.ID.eq(note.getId()))
			.execute();
		ctx.log().debug("UPDATE NOTE id: " + note.getId());		
		return note;
	}
	
	private static Note insert(AONContext ctx, Note note) {
		Integer id = ctx.getDslContext()
				.insertInto(NOTE)
				.set(NOTE.DOMAIN, note.getDomain())
				.set(NOTE.OWNER, note.getOwner())
				.set(NOTE.SUBJECT, note.getSubject()!=null?  note.getSubject() : "")
				.set(NOTE.NOTE_, note.getNote())
				.set(NOTE.DATE, new Timestamp(note.getDate().getTime()))
				.set(NOTE.ARCHIVE, note.isArchive() ? (byte)1 : (byte)0)
				.set(NOTE.PIN_UP, note.isPinpUp() ? (byte)1 : (byte)0)
				.set(NOTE.NOTE_TAG, AonStringUtils.isBlank(note.getNoteTag()) ? null : note.getNoteTag().toUpperCase())
				.set(NOTE.COLOR, note.getColor())
				.set(NOTE.ARCHIVE_DATE, null == note.getArchiveDate() ? null : new Timestamp(note.getArchiveDate().getTime()))
				.set(NOTE.MODIFICATION_DATE, null == note.getModificationDate() ? null : new Timestamp(note.getModificationDate().getTime()))
				.set(NOTE.CREATION_DATE, new Timestamp(note.getCreationDate().getTime()))
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
	
	public static void deleteNoteTag(AONContext ctx, NoteFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext().update(NOTE)
			.set(NOTE.NOTE_TAG, DSL.castNull(NOTE.NOTE_TAG))
			.where(NOTE_PROPERTIES.getConditions(filter))
			.execute();
		ctx.log().debug("DELETE NOTE TAG");		
		
	}

	public static void updateNoteTag(AONContext ctx, String noteTag, NoteFilter filter) {
		ctx.checkWrite();
		ctx.getDslContext().update(NOTE)
			.set(NOTE.NOTE_TAG, noteTag.toUpperCase())
			.where(NOTE_PROPERTIES.getConditions(filter))
			.execute();
		ctx.log().debug("UPDATE NOTE TAG: " + noteTag);	
	}
	
	public static HashMap<String, Integer> countForDate(AONContext ctx, NoteFilter filter, Date dateEnd, Integer userId) {
		HashMap<String, Integer> map = new HashMap<>();

		String dateStr = AonDateUtils.format(dateEnd, "yyyy-MM-dd");
		
		Condition whenOne = NOTE.DATE.lt(DSL.cast(DSL.inline(DATE_DEFAULT), SQLDataType.TIMESTAMP));
		Condition whenTwo = NOTE.DATE.le(DSL.cast(DSL.inline(dateStr), SQLDataType.TIMESTAMP));
		
		Field<Integer> total = DSL.count().as("total");
		Field<Integer> totalExpired = DSL.count( DSL.when(whenOne.and(whenTwo), DSL.inline(1)) ).as("total_expired");
		
		Record2<Integer, Integer> result = ctx.getDslContext()
			.select(totalExpired, total)
			.from(NOTE)
			.where(NOTE_PROPERTIES.getConditions(filter))
			.fetchOne();
		
		map.put("total_expired", (Integer) result.get(DSL.name("total_expired")));
		map.put("total", (Integer) result.get(DSL.name("total")));
		
		Integer archiveNotes = ctx.getDslContext().selectCount().from(NOTE)
			.where(NOTE.DOMAIN.eq(ctx.getDomainId()))
			.and(NOTE.ARCHIVE.eq((byte)1))
			.and(NOTE.OWNER.eq(userId))
			.fetchOne().value1();
		
		map.put("archive", archiveNotes);
		
		return map;
	}
	
	private static class NoteFiller extends Filler implements Function<Record, Note> {
		@Override
		public Note apply(Record r) {
			return new Note()
					.setId(r.getValue(NOTE.ID))
					.setDomain(r.getValue(NOTE.DOMAIN))
					.setOwner(r.getValue(NOTE.OWNER))
					.setSubject(r.getValue(NOTE.SUBJECT))
					.setNote(r.getValue(NOTE.NOTE_))
					.setDate(r.getValue(NOTE.DATE))
					.setArchive(r.getValue(NOTE.ARCHIVE) != (byte)0)
					.setPinpUp(r.getValue(NOTE.PIN_UP) != (byte)0)
					.setNoteTag(r.getValue(NOTE.NOTE_TAG))
					.setColor(r.getValue(NOTE.COLOR))
					.setArchiveDate(r.getValue(NOTE.ARCHIVE_DATE))
					.setModificationDate(r.getValue(NOTE.MODIFICATION_DATE))
					.setCreationDate(r.getValue(NOTE.CREATION_DATE))
					;
		}
	}
	
	private static void autoComplete(Note note) {
		if(note.getDate()==null) 
			note.setDate(AonDateUtils.parse(DATE_DEFAULT, "yyyy-MM-dd"));
		if(note.getCreationDate() == null)
			note.setCreationDate(new Date());
	}
}
