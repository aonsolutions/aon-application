package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.NoteFilter;
import com.esferalia.aon.occam.api.model.aonsolutions.Note;

public interface INote {
	
	public Note getNote(AONContext ctx, NoteFilter filter);
	
	public Stream<Note> getNoteStream(AONContext ctx, NoteFilter filter);	
	
	public Note saveNote(AONContext ctx, Note note);
	
	public void deleteNote(AONContext ctx, Integer id);

	public void deleteNoteTag(AONContext ctx, NoteFilter filter);
	
	public void updateNoteTag(AONContext ctx, String noteTag, NoteFilter filter);

	public HashMap<String, Integer> getNoteCountForDate(AONContext ctx, NoteFilter filter, Date dateEnd, Integer userId);
	
}
