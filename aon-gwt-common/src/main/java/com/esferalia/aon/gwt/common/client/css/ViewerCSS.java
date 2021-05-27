package com.esferalia.aon.gwt.common.client.css;

import com.google.gwt.resources.client.CssResource;

public interface ViewerCSS extends  CssResource {
	
	@ClassName("gwt-PopupPanel-viewer")
	String gwtPopupPanelViewer();
	
	@ClassName("editor-viewer")
	String editorViewer();
	
	@ClassName("label-viewer")
	String labelViewer();
	
	@ClassName("document-viewer")
	String documentViewer();
	
	@ClassName("toolbar-viewer")
	String toolbarViewer();
	
	@ClassName("toolbar-left-viewer")
	String toolbarLeftViewer();
	
	@ClassName("toolbar-center-viewer")
	String toolbarCenterViewer();
	
	@ClassName("toolbar-right-viewer")
	String toolbarRightViewer();

	@ClassName("toolbar-button-viewer")
	String toolbarButtonViewer();
	
	@ClassName("open-button-viewer")
	String openButtonViewer();
	
	
	@ClassName("close-button-viewer")
	String closeButtonViewer();
	
	@ClassName("print-button-viewer")
	String printButtonViewer();
	
	@ClassName("download-button-viewer")
	String downloadButtonViewer();
	
	@ClassName("zoom-in-button-viewer")
	String zoomInButtonViewer();
	
	@ClassName("zoom-out-button-viewer")
	String zoomOutButtonViewer();
	
	@ClassName("share-button-viewer")
	String shareButtonViewer();
	
	@ClassName("sidebar-button-viewer")
	String sidebarButtonViewer();
	
	@ClassName("next-button-viewer")
	String nextButtonViewer();
	
	@ClassName("prev-button-viewer")
	String prevButtonViewer();
	
	@ClassName("share-label-viewer")
	String shareLabelViewer();
	
}

