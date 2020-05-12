package com.esferalia.aon.gwt.template.client.scope;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsCompany;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScopePrincipal extends Composite{
	
	interface Binder extends UiBinder<Widget, ScopePrincipal> {}
	public static final AonGwtIssuesCSS I_CSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField SplitLayoutPanel splitLayoutPanel;
	@UiField DockLayoutPanel contentDockLayoutPanel;
	@UiField SimpleLayoutPanel northContent;
	@UiField SimpleLayoutPanel content;
	
	@UiField MinimizePanel footPanel;
	@UiField TabLayoutPanel tabLayout; 
	@UiField ScrollPanel usersPanel;
//	@UiField ScrollPanel companiesPanel;

	private ScopeMain parent;
	private ScopePrincipal me;
	
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
	
	Integer page = 1;
	Integer perPage = 40;
	Boolean scroll = true;
	public ScopePrincipal(ScopeMain parent) {
		initWidget(binder.createAndBindUi(this));
		this.parent = parent;
		this.me = this;
		filterContent();
		gridContent();

		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> arg0) {
				Integer value  = arg0.getSelectedItem();
				if(value == 1){
					openFootPanel();
				}
			}
		});
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
		getAPI().getCommon().getCompanies(getFilterMap(), new AsyncCallback<JSON<JsCompany>>() {
			
			@Override
			public void onSuccess(JSON<JsCompany> result) {
				content.setWidget(new ScopeGrid(me, result.getData().toLinkedList()));
			}
			
			@Override
			public void onFailure(Throwable caught) {

			}
		});
	}
	
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	
	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		openFootPanel();
	}
	
	public void openFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 3);
		splitLayoutPanel.animate(500);
	}
	
	public void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}	
	
	public void companySelection(JsCompany o) {
		openFootPanel();
		if(o.getScope().getId() != null) {
			getAPI().getCommon().getScopeUsers(o.getScope().getId(), new AsyncCallback<JSON<JsUser>>() {
			
				@Override
				public void onSuccess(JSON<JsUser> result) {
					usersPanel.setWidget(new UserSouthPanel(me, result.getData(), o));
				}
			
				@Override
				public void onFailure(Throwable caught) {
					
				}
			});
		} else {
			usersPanel.setWidget(new Label("No tiene ambito asignado."));
		}
//		getAPI().getCommon().getScopeCompanies(o.getId(), new AsyncCallback<JSON<JsObject>>() {
//			
//			@Override
//			public void onSuccess(JSON<JsObject> result) {
//				companiesPanel.setWidget(new CompanySouthPanel(me, result.getData(), o));
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//
//			}
//		});

	}
}
