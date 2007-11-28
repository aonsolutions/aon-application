/**
 * 
 */
package es.code.cdr.ui.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventObject;

import javax.faces.event.ActionEvent;

import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.component.inputfile.InputFile;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 13/07/2007
 *
 */
public class DocumentUpload implements Renderable {

	/** Document name. */
	String name;
	/** Document keywords. */
	String keywords;
	/** Document nowords. */
	String nowords;
	/** Document category. */
	String category;
	/** Document language. */
	String language;
	/** Document content. */
	File resource;
	/** Uploading percent. */
	int percent = -1;
	/** Renderable Interface. */
	private PersistentFacesState state;
	private RenderManager renderManager;

	/**
	 * Constructs a <code>DocumentUpload</code> object.
	 */
	public DocumentUpload() {
		state = PersistentFacesState.getInstance();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the keywords
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * @return the nowords
	 */
	public String getNowords() {
		return nowords;
	}

	/**
	 * @param nowords the nowords to set
	 */
	public void setNowords(String nowords) {
		this.nowords = nowords;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the resource
	 * @throws MalformedURLException 
	 */
	public URL getResource() throws MalformedURLException {
		return resource.toURL();
	}

	/**
	 * @return the percent
	 */
	public int getPercent() {
		return percent;
	}

	/**
	 * 
	 * @param event
	 */
	public void action(ActionEvent event) {
		InputFile inputFile = (InputFile) event.getSource();
		resource = inputFile.getFile();
		name = inputFile.getFileInfo().getFileName();
		percent = inputFile.getFileInfo().getPercent();
//	file size exceeded the limit
		if (inputFile.getStatus() == InputFile.SIZE_LIMIT_EXCEEDED) {
			inputFile.getFileInfo().getException().printStackTrace();
		}
	}

	/**
	 * 
	 * @param event
	 */
	public void progress(EventObject event) {
		InputFile file = (InputFile) event.getSource();
		percent = file.getFileInfo().getPercent();
		if ( renderManager != null ) {
			renderManager.requestRender(this);
		}
	}

	/**
	 * Clears attrbutes.
	 */
	public void clear() {
		name = keywords = nowords = category = language = null;
		resource.delete();
		resource = null;
		percent = -1;
	}

	/* (non-Javadoc)
	 * @see com.icesoft.faces.async.render.Renderable#getState()
	 */
	public PersistentFacesState getState() {
		return state;
	}

	/* (non-Javadoc)
	 * @see com.icesoft.faces.async.render.Renderable#renderingException(com.icesoft.faces.webapp.xmlhttp.RenderingException)
	 */
	public void renderingException(RenderingException renderingException) {
		// TODO Auto-generated method stub

	}

}
