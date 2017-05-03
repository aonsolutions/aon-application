package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainElaboration extends Composite {

	/*
	 */
	interface Binder extends UiBinder<Widget, MainElaboration> {}
//	public static final AonGwtIssuesCSS I_CSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	DockLayoutPanel contentDockLayoutPanel;
	@UiField
	SplitLayoutPanel contentSplitLayoutPanel;
	@UiField
	SimpleLayoutPanel northContent;
	@UiField
	SimpleLayoutPanel content;
	@UiField
	SimpleLayoutPanel southContent;
	
	private Elaboration parent;
	private HashMap<String, LinkedList<String>> filterMap;
	private MainElaboration me;
	
	public API getAPI() {
		return parent.getAPI();
	}
	

//	public MainElaboration(AonData aonData, Boolean future) {
//		this(aonData);
//		
//		load();
//	}

	public MainElaboration(Elaboration elaboration, HashMap<String, LinkedList<String>> filterMap) {
		initWidget(binder.createAndBindUi(this));
		this.parent = elaboration;
		this.me = this;
		if(filterMap==null){
			filterMap = new HashMap<>();
			LinkedList<String> status = new LinkedList<>();
			status.add(ElaborationStatus.PENDING.ordinal() + "");
		}
		this.filterMap = filterMap;
		
		load();
	}
	
	public MainElaboration(Elaboration elaboration) {
		initWidget(binder.createAndBindUi(this));
		this.parent = elaboration;
		this.me = this;
		filterMap = new HashMap<>();
		LinkedList<String> status = new LinkedList<>();
		status.add(ElaborationStatus.PENDING.ordinal() + "");
		filterMap.put("status", status);
		
		load();
	}

	private void load() {
		loadWestContent();
		loadNorthContent();
		loadContent();
//		loadSouthContent();
	}


	private void loadWestContent() {

	}

	private void loadNorthContent() {
		contentDockLayoutPanel.setWidgetSize(northContent, 85);
		northContent.setWidget(new FilterPanel(parent, parent.getFilterMap()));
	}

	public void loadContent() {
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("40");
		getFilterMap().put("per_page", list);
		getAPI().getWarehouse().getElaborationList(filterMap, new AsyncCallback<JSON<JsElaboration>>() {

			@Override
			public void onSuccess(JSON<JsElaboration> result) {
				content.setWidget(new GridPanel(parent, result.getData().toLinkedList()));
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	public void loadSouthContent() {
		FooterPanel fp = new FooterPanel(parent);
		southContent.setWidget(fp);
		contentSplitLayoutPanel.setWidgetSize(southContent, 0);		
	}
	
	
	public ElaborationPanel getElaborationPanel() {
		ElaborationPanel panel = (ElaborationPanel) northContent.getWidget();
		return panel;
	}
	
	public FooterPanel getFooterPanel() {
		FooterPanel panel = (FooterPanel) southContent.getWidget();
		return panel;
	}

	public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	}

	public void refreshSelect() {
		 ElaborationSelect cps = (ElaborationSelect)
		 content.getWidget();
		 cps.refresh();
	}
	
	public JsElaboration getJsElaboration(){ 
		ElaborationPanel w = (ElaborationPanel) northContent.getWidget();
		return w.getJsElaboration();
	}
	
	public void setJsElaboration(JsElaboration js){ 
		ElaborationPanel w = (ElaborationPanel) northContent.getWidget();
		w.setJsElaboration(js);
	}
	
	protected boolean isElaborationCLosed(){
		ElaborationPanel w = (ElaborationPanel) northContent.getWidget();
		return w.isElaborationCLosed();
	}
	
}
