package net.aonsolutions.aon.api.servlet;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.NoteJSON;
import com.esferalia.aon.occam.api.json.TagJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.Note;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.TagType;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.ewok.IConstants;

@SuppressWarnings("serial")
@WebServlet(name = "AonNoteServlet", urlPatterns = {"/ms/api/note/*"})
public class NoteServlet extends AonApiHttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(NoteServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI()
			+ " - " + req.getHeader(IConstants.DOMAIN_NAME));
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getNotes(api));
				break;
			case "/one":
				response(req, resp, getNote(api));
				break;
			case "/note-count":
				response(req, resp, getNoteCount(api));
				break;
			case "/note-tag-count":
				response(req, resp, getNoteTagCount(api));
				break;
			case "/tags":
				response(req, resp, getNoteTags(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI()
			+ " - " + req.getHeader(IConstants.DOMAIN_NAME));
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveNote(api));
				break;
			case "/tag":
				response(req, resp, saveTag(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI() 
			+ " - " + req.getHeader(IConstants.DOMAIN_NAME));
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteNote(api));
				break;
			case "/tag":
				response(req, resp, deleteTag(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	// ---------- NOTES

	private JSONArray getNotes(AonApiData api) {
		return NoteJSON.toJSON( 
				AON_SOLUTIONS.getNoteStream(api.getDomain(), "", 
						f-> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getOwnerProperty().eq(api.getUser().getId()))
				)
		);
	}

	private JSONObject getNote(AonApiData api) {
		return NoteJSON.toJSON( 
				AON_SOLUTIONS.getNote(api.getDomain(), "", f->f.getOwnerProperty().eq(api.getUser().getId())
						.and(f.getIdProperty().eq(api.getData().optInt("id")))
				)
		);
	}

	private JSONObject saveNote(AonApiData api) {
		Note note = NoteJSON.fromJSON(api.getData()).setOwner(api.getUser().getId());
	
		return NoteJSON.toJSON(
				AON_SOLUTIONS.saveNote(api.getDomain(), api.getUser().getLogin(), note)
				);
	}

	private JSONObject deleteNote(AonApiData api) {
		Integer noteId = api.getData().optInt("id");
		AON_SOLUTIONS.deleteNote(api.getDomain(), "", noteId);
		return new JSONObject();
	}
	
	// ---------- NOTES COUNT
	
	private JSONObject getNoteCount(AonApiData api) {
	    Date dateEnd = new Date();
		JSONObject json = new JSONObject();
		HashMap<String, Integer> map = AON_SOLUTIONS.getNoteCountForDate(api.getDomain(), "", f->f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getOwnerProperty().eq(api.getUser().getId())),
				dateEnd,
				api.getUser().getId()
		);

		for (Entry<String, Integer> entry : map.entrySet()) 
			json.put(entry.getKey(), entry.getValue());

		return json;
	}
	
	// ---------- NOTES TAG COUNT
	
	private JSONObject getNoteTagCount(AonApiData api) {
	    JSONObject json = new JSONObject();
		
	    HashMap<String, Integer> map = AON_SOLUTIONS.getNoteTagCount(api.getDomain(), "", f->f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getOwnerProperty().eq(api.getUser().getId())),
				api.getUser().getId()
		);

		for (Entry<String, Integer> entry : map.entrySet()) 
			json.put(entry.getKey(), entry.getValue());

		return json;
	}
	
	// ---------- TAG
	
	private JSONArray getNoteTags(AonApiData api) {
		System.out.println("User: " + api.getUser().getId());
		List<Tag> tags = AON.getNoteTagsList(
				api.getDomain().getName(),
				api.getDomain().getId(), 
				api.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getTypeProperty().eq(TagType.NOTE.value())),
				api.getUser().getId());
		
		return TagJSON.toJSON(tags);
	}
	
	private JSONObject saveTag(AonApiData api) {
		Domain domain = api.getDomain();
		Tag tag = TagJSON.fromJSON(api.getData());
		
		if(tag.getId() != null) {
			AON.updateTag(domain.getName(), domain.getId(), api.getUser().getLogin(), tag); 
		} else {
			tag = AON.insertTag(domain.getName(), domain.getId(), api.getUser().getLogin(), tag);
		}

		return TagJSON.toJSON(tag);
	}
	
	private JSONObject deleteTag(AonApiData api) {
		AON.deleteTag(
				api.getDomain().getName(), 
				api.getDomain().getId(), 
				api.getUser().getLogin(), 
				TagJSON.fromJSON(api.getData()));
		return new JSONObject();
	}
	
}
