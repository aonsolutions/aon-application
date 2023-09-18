package net.aonsolutions.aon.api.servlet;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.NoteJSON;
import com.esferalia.aon.occam.api.model.aonsolutions.Note;

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
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private JSONArray getNotes(AonApiData api) {
		return NoteJSON.toJSON( 
				AON_SOLUTIONS.getNoteStream(api.getDomain(), "", 
						f-> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getOwnerProperty().eq(api.getUser().getId()))
				)
		);
	}
	
	private JSONObject getNoteCount(AonApiData api) {
	    Date dateEnd = new Date();
		JSONObject json = new JSONObject();
		HashMap<String, Integer> map = AON_SOLUTIONS.getNoteCountForDate(api.getDomain(), "", f->f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getOwnerProperty().eq(api.getUser().getId()) ),
				dateEnd
		);

		for (Entry<String, Integer> entry : map.entrySet()) 
			json.put(entry.getKey(), entry.getValue());

		return json;
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
	
}
