package com.code.aon.ui.fileupload;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.fileupload.HtmlInputFileUpload;
import org.apache.myfaces.custom.fileupload.UploadedFile;

/**
 * File Upload
 * 
 * @author Consulting & Development. Aimar Tellitu - 16-sep-2005
 * @since 1.0
 *  
 */
public class FileUpload {
	
    private static final Logger LOGGER = Logger.getLogger(FileUpload.class.getName());

    /** Identificador del parametro con el nombre del listener que hace se avisa
     *  cuando se añade un fichero */
    private static final String LISTENER_PARAMETER = "listener";    
    
    /** Identificador del parametro con el nombre de la pagina a la que hay que
     *  volver y recargar si se añade un ficheros */
    private static final String RETURN_PAGE_PARAMETER = "returnPage";    

    /** Identificador del parametro con el bundle que se va a utilizar como sufijo
     *  para resolver los mensajes */
    private static final String BUNDLE_PARAMETER = "bundle";    
    
    private static final String FILE_UPLOAD_ELEMENT = "aon_fileupload_element";    
    
    private static final String FILE_UPLOAD_INSERT = "aon_fileupload_insert";
    
    private static final String FILE_UPLOAD_INSERTED = "aon_fileupload_inserted";    

	/** ResourceBundle de la applicación */
	private static ResourceBundle resourceBundle;

    /** Fichero que se ha subido */
	private UploadedFile uploadFile;
	
	/** Componente JSF de subida de ficheros */
	private HtmlInputFileUpload component;
	
	/** Nombre del fichero en el cliente */
	private String fileName;

	/** Nombre de un managed bean que implementa IFileUploadListener */
	private String listener;
	
	/** Pagina a retornar si se añade un fichero */
	private String returnPage;

	/** Sufijo para resolver los mensajes */
	private String bundle;
	
	private String obtainListener() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return (String) facesContext.getExternalContext().getRequestParameterMap().get(LISTENER_PARAMETER);
	}
	
	private String obtainReturnPage() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return (String) facesContext.getExternalContext().getRequestParameterMap().get(RETURN_PAGE_PARAMETER);
	}

	private String obtainBundle() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String bundle = (String) facesContext.getExternalContext().getRequestParameterMap().get(BUNDLE_PARAMETER);
		return ( bundle != null ) ? "_" + bundle : "";
	}

	private void resolveResourceBundle() {
		Application app = FacesContext.getCurrentInstance().getApplication();
		resourceBundle = ResourceBundle.getBundle(app.getMessageBundle(), app.getDefaultLocale() );
	}
	
	private IFileUploadListener getFileUploadListener() {
		FacesContext facesContext = FacesContext.getCurrentInstance();		
	  	ValueBinding vb = facesContext.getApplication().createValueBinding("#{"+ this.listener +"}");
	  	Object controller = vb.getValue(facesContext);
	  	if ( (controller != null) && (controller instanceof IFileUploadListener) ) {
	  		return (IFileUploadListener) controller;
	  	}
	  	return null;
	}
	
	private void reset() {
		this.listener = null;
		this.returnPage = null;
		this.fileName = null;
	}
	
	/**
     * Return component
     * 
	 * @return HtmlInputFileUpload
	 */
	public HtmlInputFileUpload getComponent() {
		return component;
	}

	/**
     * Set component
     * 
	 * @param component
	 */
	public void setComponent(HtmlInputFileUpload component) {
		if ( this.returnPage == null ) {
			setListener( obtainListener() );
			setReturnPage( obtainReturnPage() );
			setBundle( obtainBundle() );
			resolveResourceBundle();
		}
		this.component = component;
	}
	
	/**
     * Return listener
     * 
	 * @return String
	 */
	public String getListener() {
		return this.listener;
	}

	/**
     * Set listener
     * 
	 * @param listener
	 */
	public void setListener(String listener) {
		this.listener = listener;
	}

	/**
     * Return page of return
     * 
	 * @return String
	 */
	public String getReturnPage() {
		return returnPage;
	}

	/**
     * Set page of return
     * 
	 * @param returnPage
	 */
	public void setReturnPage(String returnPage) {
		this.returnPage = returnPage;
	}

	/**
     * Return name of file
     * 
	 * @return String
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
     * Set name of file
     * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
     * Return if it is uploaded
     * 
	 * @return boolean
	 */
	public boolean isUploaded() {
		return this.fileName != null;
	}

	/**
     * Return upload file
     * 
	 * @return UploadedFile
	 */
	public UploadedFile getUploadFile() {
		return this.uploadFile;
	}
	
	/**
     * Set upload file
     * 
	 * @param file
	 */
	public void setUploadFile(UploadedFile file) {
		this.uploadFile = file;
	}

	/**
     * Return bundle
     * 
	 * @return String
	 */
	public String getBundle() {
		return this.bundle;
	}

	/**
     * Set bundle
     * 
	 * @param bundle
	 */
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	/**
     * Execute close action
     * 
	 * @param e
	 */
	public void onClose(ActionEvent e) {
    	LOGGER.fine( "onClose pressed " + e.getSource() );		
		reset();
	}

	/**
     * Execute cancel action
     * 
	 * @param e
	 */
	public void onCancel(ActionEvent e) {
    	LOGGER.fine( "onCancel pressed" + e.getSource() );		
		reset();
	}
	
	/**
     * Upload action
     * 
	 * @param e
	 * @throws IOException
	 */
	public void upload(ActionEvent e) throws IOException {
		IFileUploadListener listener = getFileUploadListener();
		if ( listener != null ) {
			if ( this.uploadFile != null ) {
			listener.upload( uploadFile.getName(), uploadFile.getContentType(), uploadFile.getBytes() );
			setFileName( this.uploadFile.getName() );
			}
		} else {
			LOGGER.severe( "No se ha encontrado un IFileUploadListener para " + this.listener );			
		}
    	LOGGER.fine( "upload " + e.getSource() );		
	}
	
	private String getLabel( String key ) {
		return resourceBundle.getString( key + this.bundle );
	}
	
	/**
     * Return element label
     * 
	 * @return String
	 */
	public String getElementLabel() {
		return getLabel( FILE_UPLOAD_ELEMENT );
	}

	/**
     * Return insert label
     * 
	 * @return String
	 */
	public String getInsertLabel() {
		return getLabel( FILE_UPLOAD_INSERT );
	}

	/**
     * Return inserted label
     * 
	 * @return String
	 */
	public String getInsertedLabel() {
		return getLabel( FILE_UPLOAD_INSERTED );
	}

}
