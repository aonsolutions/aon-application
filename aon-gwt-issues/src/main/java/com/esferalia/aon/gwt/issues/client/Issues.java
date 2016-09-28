package com.esferalia.aon.gwt.issues.client;

import java.util.Arrays;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.IssueFilter;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.esferalia.aon.gwt.issues.shared.AonData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.iron.IronListElement;
import com.vaadin.polymer.paper.PaperDialogElement;
import com.vaadin.polymer.paper.PaperIconButtonElement;
import com.vaadin.polymer.paper.PaperInputElement;
import com.vaadin.polymer.paper.PaperTextareaElement;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperTextarea;
import com.vaadin.polymer.vaadin.VaadinComboBoxElement;
import com.vaadin.polymer.vaadin.widget.VaadinComboBox;

public class Issues implements EntryPoint {
	
	interface Binder extends UiBinder<Widget, Issues> {

	}
	private static final Binder binder = GWT.create(Binder.class);

	final IIssuesAsync serv = GWT.create(IIssues.class);
	
	private static final String HTTP = "http://";

	@UiField HTMLPanel searchContent;
	@UiField HTMLPanel content;
	@UiField HTMLPanel issueContent;
	@UiField HTMLPanel toolbar;
	@UiField HTMLPanel configurationPanel;
	@UiField DockLayoutPanel dockLayoutPanel;
	@UiField DockLayoutPanel contentDockLayoutPanel;
	
	IssueFilter issueFilter;
	Issues me;
	AonData aonData;
	Boolean more = true;
	
	private Incidence incidence;
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	@Override
	public void onModuleLoad() {
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				PaperInputElement.SRC,
				PaperTextareaElement.SRC,
				PaperDialogElement.SRC,
				VaadinComboBoxElement.SRC,
				PaperIconButtonElement.SRC,
				IronListElement.SRC
				
		));
		
		Polymer.whenReady(o -> {
			startApplication();
			return null;
		});
	}
	
	private void startApplication() {
		GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css().ensureInjected();
		
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		me = this;
		serv.getAonData(getCurrentDomainName(), getCurrentDomain(), new AsyncCallback<AonData>() {
			
			@Override
			public void onSuccess(AonData result) {
				aonData = result;
				incidence = new Incidence(HTTP+result.getDomain().getName()+"/", result.getMd5(),
						result.getLoggedUser(), result.getLoggedUser(), result.getDomain().getName());
				createAonToolbar();
				createFilterPanel(new FilterPanel(me));
				createIssueList(issueFilter = new IssueFilter());
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}

	protected void createFilterPanel(FilterPanel filterPanel){
		searchContent.add(filterPanel);
	}
	
	protected void createIssueList(IssueFilter filter) {
		incidence.getOrgIssues(filter, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {			
				IssueList issueList = new IssueList(me, incidence, result.getData());
				content.add(issueList);
				//createAddDialog();

			}
			
			@Override
			public void onFailure(Throwable arg0) {	}
		});
	}

	protected void updateIssueList(AonJsArray<JsIssue> array){
		IssueList issueList = (IssueList) content.getWidget(0);
		issueList.updateItems(array);
	}
	
	protected void showMoreupdateIssueList(AonJsArray<JsIssue> array){
		IssueList issueList = (IssueList) content.getWidget(0);
		issueList.updateItems(issueList.getItems().concat(array).cast());
	}

	protected void updateIssueList(IssueFilter filter, Boolean showMore) {
		if(!showMore) filter.setPage(1);
		
		incidence.getOrgIssues(filter, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				more = result.getData().length()==30;
				AonJsArray<JsIssue> array = JavaScriptObject.createArray().cast();
				/*if(filter.getTitle() != null && !filter.getTitle().isEmpty()){
					for(Integer i = 0; i < result.getData().length(); i++){
						if(result.getData().get(i).getTitle().contains(filter.getTitle()))
							array.push(result.getData().get(i));
					}
				} else*/
				 array = result.getData();
				
				if(showMore) showMoreupdateIssueList(array);
				else updateIssueList(array);				
				
			}
			
			@Override
			public void onFailure(Throwable arg0) {	}
		});
	} 
	
	
	private void createAonToolbar(){
		toolbar.add(new AonToolbar() {
			
			@Override
			protected void onRefreshButtonClick() {
				issueFilter = new IssueFilter();
				updateIssueList(issueFilter, false);
			}
			
			@Override protected void onMoreOptionButtonClick() {}
			
			@Override
			protected void onMenuButtonClick() {
				if(dockLayoutPanel.getWidgetSize(configurationPanel) == 0){
					configurationPanel.add(new ConfigurationPanel(incidence));
					dockLayoutPanel.setWidgetSize(configurationPanel, 350);
				}
				else {
					configurationPanel.remove(0);
					dockLayoutPanel.setWidgetSize(configurationPanel, 0);
				}		
			}
			
			@Override protected void onEditButtonClick() {}
			
			@Override protected void onDeleteButtonClick() {}
			
			@Override
			protected void onAddButtonClick() {
				incidence.getRegistries(new AsyncCallback<JSON<JsUser>>() {
					
					@Override
					public void onSuccess(JSON<JsUser> result) {
						AonDialog dialog = createAddDialog(result);
						toolbar.add(dialog);
						dialog.open();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		}.setVisibleEditButton(false).setVisibleDeleteButton(false)
		.setVisibleMoreOptionButton(false));
	}
	
	private AonDialog createAddDialog(JSON<JsUser> registries){	
		
		String arr= "[";
		for(Integer i = 0; i < registries.getData().length(); i++){
			if(i > 0) arr = arr + " , ";
 			arr = arr + "\""+ registries.getData().get(i).getLogin()+"\"";
		}
		arr = arr + "]";
		
		VerticalPanel v = new VerticalPanel();
		PaperInput pi = new PaperInput();
		pi.setLabel("Titulo");
		pi.setList("as");
		v.add(pi);
		
		VaadinComboBox vcb = new VaadinComboBox(); 
		vcb.setLabel("Remitente");
		vcb.setItems(arr);
		/*vcb.addDomHandler(new KeyUpHandler() {	
			@Override
			public void onKeyUp(KeyUpEvent event) {		
				Window.alert(vcb.getWidgetCount() + "");
				
				Window.alert(vcb.getPolymerElement().getTextContent());
				
				Window.alert(vcb.getPolymerElement().toString());
				
				Window.alert(vcb.getValidatorType());
				Window.alert(vcb.getKeyBindings().toString());
				char ch = (char) event.getNativeKeyCode();
				vcb.setValue(vcb.getValue() + ch );	
				Window.alert(vcb.getValue());
				if(vcb.getValue().length()> 2){
					incidence.getRegistries(vcb.getValue(), new AsyncCallback<JSON<JsUser>>() {
						
						@Override
						public void onSuccess(JSON<JsUser> result) {
							String arr= "[";
							for(Integer i = 0; i < result.getData().length(); i++){
								if(i > 0) arr = arr + " , ";
					 			arr = arr + "\""+ result.getData().get(i).getLogin()+"\"";
							}
							arr = arr + "]";
							vcb.setItems(arr);
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
				} else {
					vcb.setItems("[]");
				}
			}
		}, KeyUpEvent.getType());
*/
		v.add(vcb);

		PaperTextarea pi4 = new PaperTextarea();
		pi4.setLabel("Descripcion");
		v.add(pi4);
		
		return new AonDialog("Nueva Incidencia",v){
			@Override protected void onCancel() {}
			@Override protected void onAccept() {
				VerticalPanel vp = (VerticalPanel) content.getWidget(0);
				PaperInput pi = (PaperInput) vp.getWidget(0);
				VaadinComboBox pi2 = (VaadinComboBox) vp.getWidget(1);
				PaperTextarea pi4 = (PaperTextarea) vp.getWidget(2);
				
				String r= "{\"title\":\""+ pi.getValue() +"\",\"body\":\""+ pi4.getValue()+" \",\"assignee\":\" \",\"labels\":[],"
						+ "\"enterprise\":\""+ pi2.getValue() +"\", \"due_date\":\""+ "31/12/2100" +"\"}";
					
				incidence.createOrgIssue(r, new AsyncCallback<JsIssue>() {
					
					@Override
					public void onSuccess(JsIssue result) {
						contentDockLayoutPanel.removeFromParent();
						AonToolbar t = (AonToolbar)toolbar.getWidget(0);
						t.setVisibleRefreshButton(false);
						contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
						contentDockLayoutPanel.add(new IssuePanel(me, incidence, result));
						dockLayoutPanel.add(contentDockLayoutPanel);
					}
					@Override
					public void onFailure(Throwable caught) {}
				});
			}
		};
	}
	
}
