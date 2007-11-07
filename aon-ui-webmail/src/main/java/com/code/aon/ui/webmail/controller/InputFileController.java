package com.code.aon.ui.webmail.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.webmail.listener.IFileUploadedListener;
import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.component.inputfile.InputFile;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;

/**
 * <p>The InputFileBean class is the backing bean for the inputfile showcase
 * demonstration. It is used to store the state of the uploaded file.</p>
 *
 * @since 0.3.0
 */
public class InputFileController  implements Renderable {

    private int percent = -1;
    private File file = null;
    private List<IFileUploadedListener> listenerClasses = new ArrayList<IFileUploadedListener>();

    /**
     * Renderable Interface
     */
    private PersistentFacesState state;
    private RenderManager renderManager;

    private String fileName = "";
    private String contentType = "";


    public InputFileController() {
        state = PersistentFacesState.getInstance();
    }

    /**
     * Sets the Render Manager.
     *
     * @param renderManager
     */
    public void setRenderManager(RenderManager renderManager) {

        this.renderManager = renderManager;
    }

    /**
     * Gets RenderManager, just try to satisfy WAS
     *
     * @return RenderManager null
     */
    public RenderManager getRenderManager() {
        return null;
    }

    /**
     * Get the PersistentFacesState.
     *
     * @return state the PersistantFacesState
     */
    public PersistentFacesState getState() {

        return state;
    }

    /**
     * Handles rendering exceptions for the progress bar.
     *
     * @param renderingException the exception that occured
     */
    public void renderingException(RenderingException renderingException) {
        renderingException.printStackTrace();
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void action(ActionEvent event) {
        InputFile inputFile = (InputFile) event.getSource();
        fileName = inputFile.getFileInfo().getFileName();
        contentType = inputFile.getFileInfo().getContentType();
        this.percent = inputFile.getFileInfo().getPercent();
        if (inputFile.getStatus() == InputFile.SAVED) {
            setFile(inputFile.getFile());
            fileAdded();
            //file.delete();
        }

        if (inputFile.getStatus() == InputFile.INVALID) {
            inputFile.getFileInfo().getException().printStackTrace();
        }

        if (inputFile.getStatus() == InputFile.SIZE_LIMIT_EXCEEDED) {
            inputFile.getFileInfo().getException().printStackTrace();
        }

        if (inputFile.getStatus() == InputFile.UNKNOWN_SIZE) {
            inputFile.getFileInfo().getException().printStackTrace();
        }
    }

    public void progress(EventObject event) {
        InputFile file = (InputFile) event.getSource();
        this.percent = file.getFileInfo().getPercent();

        if (renderManager != null) {
           renderManager.requestRender(this);
        }

    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    private void fileAdded(){
    	for (IFileUploadedListener l: listenerClasses) {
    		l.fileUploaded(file);
    	}
    }

    /**
     * Return a list containing the listeners associated to controller.
     * 
     * @return List<IControllerListener>
     */
    public List<IFileUploadedListener> getListenerClasses() {
        return listenerClasses;
    }

    /**
     * Set a list containing the listeners associated to controller.
     * 
     * @param listenerClasses
     */
    public void setListenerClasses(List<IFileUploadedListener> listenerClasses) {
        this.listenerClasses = listenerClasses;
    }

    //*************************************************************
    // NEW FOLDER POPUP
    //*************************************************************
    
    private boolean showAddAttachPanelPopup;
    
	/**
	 * @return the showNewFolderPanelPopup
	 */
	public boolean isShowAddAttachPanelPopup() {
		return showAddAttachPanelPopup;
	}

	/**
	 * @param showAddAttachPanelPopup the showAddAttachPanelPopup to set
	 */
	public void setShowAddAttachPanelPopup(boolean showAddAttachPanelPopup) {
		this.showAddAttachPanelPopup = showAddAttachPanelPopup;
	}
    
	public void closeAddAttachPanelPopup(ActionEvent event){
		this.showAddAttachPanelPopup = false;
	}

	public void openAddAttachPanelPopup(ActionEvent event){
		this.showAddAttachPanelPopup = true;
	}


    
}
