package com.esferalia.aon.gwt.document.client;

import java.util.List;

import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Viewer  extends PopupPanel {
	
	
	@UiField ScrollPanel scrollPanel;
	@UiField HTML 	container;
	@UiField Button close;
	@UiField Button print;
	@UiField Button download;
	@UiField Label title;
	@UiField Button prev;
	@UiField Button next;
	@UiField HorizontalPanel menu;
	@UiField SimplePanel simple;
	@UiField HorizontalPanel auxiliar;
	@UiField HorizontalPanel auxiliar2;	
	@UiField HorizontalPanel auxiliar3;
	FileInfo fileInfo;
	List<FileInfo> list;
	Integer num;
	
	public Viewer(List<FileInfo> list, Integer num, FileInfo fileInfo){
		super( true );
		this.fileInfo = fileInfo;
		this.list = list;
		this.num = num;
		setStyleName("{style.prueba}");
		setPopupPosition(0, 0);
		Element elem = getElement();
	    elem.getStyle().setPropertyPx("right", 0);
	    elem.getStyle().setPropertyPx("bottom", 0);
		setWidget(binder.createAndBindUi( this ) );
	}

	private static final int DEFAULT_ZOOM = 100;
	
	interface Binder extends UiBinder<Widget, Viewer> { }
	private static final Binder binder = GWT.create(Binder.class);

	
	private int zoom = DEFAULT_ZOOM;
	
	protected abstract void onPrint();
	
	protected abstract void onDownload();
	
	protected abstract void onShare();
	
	protected abstract void onChange();
	

	
	
	@UiHandler("close")
	void close(ClickEvent event){
		hide();
	}
	
	@UiHandler("download")
	void vdownload(ClickEvent event){
		onDownload();
	}
	
	@UiHandler("print")
	void vprint(ClickEvent event){
		onPrint();
	}
	
	@UiHandler("share")
	void vshare(ClickEvent event){
		onShare();
	}
	
	@UiHandler("prev")
	void vprev(ClickEvent event){
		title.removeStyleName(fileInfo.getIcon());
		num--;
		fileInfo = list.get(num); 
		if(num.equals(list.size()-1)) next.setVisible(false);
		else next.setVisible(true);
		if(num.equals(0)) prev.setVisible(false);
		else prev.setVisible(true);
		onChange();
	}
	
	@UiHandler("next")
	void vnext(ClickEvent event){
		title.removeStyleName(fileInfo.getIcon());
		num++;
		fileInfo = list.get(num);
		if(num.equals(list.size()-1)) next.setVisible(false);
		else next.setVisible(true);
		if(num.equals(0)) prev.setVisible(false);
		else prev.setVisible(true);
		onChange();
	}
	
	/*@UiHandler("close")
	void close(){
		hide();
	}*/
	
}
