package net.aonsolutions.aon.gwt.udapa.client.quality;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class QualityPrincipal extends Composite{
	
	interface Binder extends UiBinder<Widget, QualityPrincipal> {}
	public static final AonGwtIssuesCSS I_CSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField DockLayoutPanel contentDockLayoutPanel;
	@UiField SimpleLayoutPanel northContent;
	@UiField SimpleLayoutPanel content;
	@UiField SimpleLayoutPanel southContent;
	
	private UdapaQuality parent;
	private QualityPrincipal me;
	
	public API getAPI() {
		return parent.getAPI();
	}
	
	public HashMap<String, LinkedList<String>> getFilterMap() {
		return parent.getFilterMap();
	}
	
	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		parent.setFilterMap(filterMap);
	}
	public void sheetContent(JsDataResponse js, HashMap<String, LinkedList<String>> map) {
		parent.sheetContent(js, map);
	}
	
	public QualityPrincipal(UdapaQuality parent) {
		initWidget(binder.createAndBindUi(this));
		this.parent = parent;
		this.me = this;
		filterContent();
		gridContent();
		southContent();
	}
	
	
	public void filterContent(){			
		// TODO
		com.esferalia.aon.gwt.common.client.widget.FilterPanel fp = parent.filterPanel();
		northContent.setWidget(fp);
		// northContent.setWidget(new FilterPanel(this));
	}
	
	public void southContent(){
		southContent.setWidget(new PrincipalFootPanel(this));
	}
	public void gridContent(){
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("40");
		getFilterMap().put("per_page", list);
		parent.getAPI().getCommon().getDataResponseQuality(getFilterMap(), new AsyncCallback<JSON<JsDataResponse>>() {
			
			@Override
			public void onSuccess(JSON<JsDataResponse> result) {
				content.setWidget(new GridPanel(me, result.getData().toLinkedList()));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
	}
	
	
	public void selectDataResponse(JsDataResponse js){
		parent.sheetContent(js, getFilterMap());
	}

	public void initializeFilterMap(){
    	setFilterMap(parent.initializeFilterMap());
	}
	
	public void southContentSize(Double value) {
		contentDockLayoutPanel.setWidgetSize(southContent, value);	
	}
}
