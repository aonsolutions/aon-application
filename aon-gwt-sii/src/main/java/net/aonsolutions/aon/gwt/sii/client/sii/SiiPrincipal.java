package net.aonsolutions.aon.gwt.sii.client.sii;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
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
import com.google.gwt.user.client.ui.Widget;

public class SiiPrincipal extends Composite{
	
	interface Binder extends UiBinder<Widget, SiiPrincipal> {}
	public static final AonGwtIssuesCSS I_CSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField DockLayoutPanel contentDockLayoutPanel;
	@UiField SimpleLayoutPanel northContent;
	@UiField SimpleLayoutPanel content;
	
	private SiiMain parent;
	private SiiPrincipal me;
	
	public API getAPI() {
		return parent.getAPI();
	}
	
	public HashMap<String, LinkedList<String>> getFilterMap(){
		return parent.getFilterMap();
	}
	
	public void setFilterMap(HashMap<String, LinkedList<String>> filterMap){
		parent.setFilterMap(filterMap);
	}
	
	public SiiPrincipal(SiiMain parent) {
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
		getAPI().getFinance().getInvoices(getFilterMap(), new AsyncCallback<JSON<JsInvoice>>() {
			
			@Override
			public void onSuccess(JSON<JsInvoice> result) {
				content.setWidget(new InvoiceGrid(me, result.getData().toLinkedList()));
			}
			
			@Override
			public void onFailure(Throwable caught) {

			}
		});
	}

	public void initializeFilterMap(){
		parent.initializeFilterMap();
	}
	
	public void content() {
		
	}
	
	public Button getSendAll() {
		return parent.getSendAll();
	}
	
}
