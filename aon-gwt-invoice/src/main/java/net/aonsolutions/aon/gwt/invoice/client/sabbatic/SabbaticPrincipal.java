package net.aonsolutions.aon.gwt.invoice.client.sabbatic;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.api.client.finance.JsInvoice;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class SabbaticPrincipal extends Composite{
	
	interface Binder extends UiBinder<Widget, SabbaticPrincipal> {}
	public static final AonGwtIssuesCSS I_CSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField SplitLayoutPanel splitLayoutPanel;
	@UiField DockLayoutPanel contentDockLayoutPanel;
	@UiField SimpleLayoutPanel northContent;
	@UiField SimpleLayoutPanel content;
	
	private SabbaticMain parent;
	private SabbaticPrincipal me;
	
	public API getAPI() {
		return parent.getAPI();
	}
	
	public SimpleLayoutPanel getContent(){
		return content;
	}
	
	public SimpleLayoutPanel getNorthContent(){
		return northContent;
	}
	
	
	public HashMap<String, LinkedList<String>> getFilterMap(){
		return parent.getFilterMap();
	}
	
	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap){
		parent.setFilterMap(filterMap);
	}
	
	public SabbaticPrincipal(SabbaticMain parent) {
		initWidget(binder.createAndBindUi(this));
		this.parent = parent;
		this.me = this;
		filterContent();
		gridContent();
	}
	
	public void filterContent(){			
		northContent.setWidget(new FilterPanel(this));
	}
	
	public void gridContent(){
		LinkedList<String> list = new LinkedList<>();
		list.add("1");
		getFilterMap().put("page", list);
		list = new LinkedList<>();
		list.add("40");
		getFilterMap().put("per_page", list);
		getAPI().getCommon().getDataResponse(getFilterMap(), new AsyncCallback<JSON<JsDataResponse>>() {
			
			@Override
			public void onSuccess(JSON<JsDataResponse> result) {
				content.setWidget(new SabbaticGrid(me, result.getData().toLinkedList()));
			}
			
			@Override
			public void onFailure(Throwable caught) {

			}
		});
	}

	public void initializeFilterMap(){
		parent.initializeFilterMap();
	}
	
	public Button getSendButton(){
		return parent.getSendButton();
	}
	
	public Button getRemoveButton(){
		return parent.getRemoveButton();
	}
}
