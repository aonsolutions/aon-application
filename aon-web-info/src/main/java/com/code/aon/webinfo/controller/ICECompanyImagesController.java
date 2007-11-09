package com.code.aon.webinfo.controller;

import java.util.EventObject;

import javax.faces.event.ActionEvent;

import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.form.BasicController;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.faces.component.inputfile.InputFile;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;

public class ICECompanyImagesController extends BasicController {

	private InputFile inputFile;
	
	private String fileName;
	
	private int percent;
	
	private PersistentFacesState state;
	
	
	public ICECompanyImagesController() {
		super();
		setState(PersistentFacesState.getInstance());
	}

	public InputFile getInputFile() {
		return inputFile;
	}

	public void setInputFile(InputFile inputFile) {
		this.inputFile = inputFile;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}
	
	public PersistentFacesState getState() {
		return state;
	}

	public void setState(PersistentFacesState state) {
		this.state = state;
	}

	public String getImageUrl(){
		RegistryAttachment attach = (RegistryAttachment)this.getTo();
		return "/" + attach.getId() + ".images";
	}
	public void onSelect(RowSelectorEvent event){
		super.onSelect(new ActionEvent(event.getComponent()));
	}
	
	public void fileUploaded(ActionEvent event){
		InputFile inputFile = (InputFile)event.getSource();
		if(inputFile.getStatus() == InputFile.SAVED){
			this.inputFile = inputFile;
			this.fileName = inputFile.getFileInfo().getFileName();
		}
		this.percent = -1;
	}
	
	@SuppressWarnings("unused")
	public void progress(EventObject event){
		try {
			InputFile inputFile = (InputFile)event.getSource();
			this.percent = inputFile.getFileInfo().getPercent();
			if(state != null){
				state.render();
			}
		} catch (RenderingException e) {
			e.printStackTrace();
		}
	}
}