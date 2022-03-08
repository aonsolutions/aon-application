package com.code.aon.aio.controller;

import java.io.Serializable;
import java.util.HashMap;

import com.code.aon.aio.service.drive.DriveService;

public class HelpController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String selected;
	public final HashMap<String,String> VIDEOS = new HashMap<>();
	
	public HelpController() {
		VIDEOS.put("Intro", "Intro.mp4");
		VIDEOS.put("Contabilidad", "Contabilidad.mp4");
		VIDEOS.put("Fiscal", "Fiscal.mp4");
		VIDEOS.put("Laboral", "Laboral.mp4");
		VIDEOS.put("Documental", "Documental.mp4");
		VIDEOS.put("Facturas", "Facturas.mp4");
		
		
		this.selected = "Intro";
	}
	
	public String getSelectedVideo() {		
		return this.selected;
	}
	
	public void setSelectedVideo(String selected) {
		this.selected = selected;
	}
	
	public HashMap<String,String> getVideos() {
		return this.VIDEOS;
	}
}
