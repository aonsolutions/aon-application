package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.INote;
import com.esferalia.aon.occam.api.model.Filter.NoteFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Note;
import com.esferalia.aon.occam.impl.jooq.dao.NoteDAO;

public class NoteImpl implements INote {

	@Override
	public Note getNote(AONContext ctx, NoteFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> NoteDAO.get(ctx, filter));
	}

	@Override
	public Stream<Note> getNoteStream(AONContext ctx, NoteFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> NoteDAO.getStream(ctx, filter));
	}

	@Override
	public Note saveNote(AONContext ctx, Note note) {
		return ctx.getDslContext().transactionResult(configuration -> NoteDAO.save(ctx, note));
	}

	@Override
	public void deleteNote(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> NoteDAO.delete(ctx, id));
	}
	
	@Override
	public void deleteNoteTag(AONContext ctx, NoteFilter filter) {
		ctx.getDslContext().transaction(configuration -> NoteDAO.deleteNoteTag(ctx, filter));
	}
	
	@Override
	public void updateNoteTag(AONContext ctx, String noteTag, NoteFilter filter) {
		ctx.getDslContext().transaction(configuration -> NoteDAO.updateNoteTag(ctx, noteTag, filter));
	}
	
	@Override
	public HashMap<String, Integer> getNoteCountForDate(AONContext ctx, NoteFilter filter, Date dateEnd, Integer userId) {
		return ctx.getDslContext().transactionResult(configuration -> NoteDAO.countForDate(ctx, filter, dateEnd, userId));
	}
}
