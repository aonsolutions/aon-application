package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.AonUrlApi;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.IronCollapse;
import com.vaadin.polymer.iron.widget.IronSelector;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;

public class TagPanel extends Composite {
	
	protected static String USER_NAME = "admin"; //"aibanez91";
	protected static String ORG_NAME = "aonPrueba"; //"aonsolutions";
	protected static String REPO_NAME = "aonPrueba"; //"aon-application";
	protected static String ACCESS_TOKEN = "d8aa641723e106b5d7c2d79d3cad963e0eb6e92d";
	protected static String DESCRIPTION = "Repositorio de prueba para metodos de TEST";

    interface Binder extends UiBinder<ScrollPanel , TagPanel> {
    	
    }
    @UiField HTMLPanel panel;
    
    @UiField Button heading1;
    @UiField IronCollapse collapse1;
    
    @UiField Button heading2;
    @UiField IronCollapse collapse2;
    
    @UiField Button heading3;
    @UiField IronCollapse collapse3;
   
    @UiField IronSelector prioritySelector;
    @UiField IronSelector typeSelector;
    @UiField IronSelector tagSelector;
    
    @UiField PaperIconButton priorityButton;
    @UiField PaperIconButton typeButton;
    @UiField PaperIconButton tagButton;
    
    private static Binder binder = GWT.create(Binder.class);
    
    public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/; 
	
    public TagPanel() {
       initWidget(binder.createAndBindUi(this));
        	
       heading1.addClickHandler(new ClickHandler() {
		
    	   @Override
    	   public void onClick(ClickEvent event) {
    		   collapse1.toggle();
    	   }
       });
       
       heading2.addClickHandler(new ClickHandler() {
   		
    	   @Override
    	   public void onClick(ClickEvent event) {
    		   collapse2.toggle();
    	   }
       });
       
       heading3.addClickHandler(new ClickHandler() {
      		
    	   @Override
    	   public void onClick(ClickEvent event) {
    		   collapse3.toggle();
    	   }
       });
      // getTagList();
    }
    
    @UiHandler("typeButton")
	void onClicktypeButton(ClickEvent event){		
		PaperInput pi = new PaperInput();
		pi.setLabel("Tipo");
		AonDialog dialog =  new AonDialog("Nuevo Tipo",pi){
			@Override protected void onCancel() {}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				// TODO TRATARLO!
			}
		};
		panel.add(dialog);
		dialog.open();	
	}
    
    @UiHandler("priorityButton")
	void onClickPriorityButton(ClickEvent event){		
		PaperInput pi = new PaperInput();
		pi.setLabel("Prioridad");
		AonDialog dialog = new AonDialog("Nueva Prioridad",pi){
			@Override protected void onCancel() {}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				// TODO TRATARLO!
			}
		};
		panel.add(dialog);
		dialog.open();	
	}
    
    @UiHandler("tagButton")
	void onClickTagButton(ClickEvent event){	
		PaperInput pi = new PaperInput();
		pi.setLabel("Etiqueta");
		AonDialog dialog =  new AonDialog("Nueva Etiqueta",pi){
			@Override protected void onCancel() {}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				// TODO TRATARLO!
			}
		};
		panel.add(dialog);
		dialog.open();
	}
    
    protected void getTagList() {
		//Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME);
		String str = USER_NAME + getCurrentDomainName();
		String md5 = "aaaaa";//Md5Utils.getMd5Digest(str.getBytes()).toString();
		Incidence i = new Incidence(AonUrlApi.AONTEST, md5, USER_NAME, USER_NAME, getCurrentDomainName());
		i.getLabels(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				for(Integer i = 0; i < result.getData().length(); i++){
					TreeItem ti = new TreeItem();
					ti.setText(result.getData().get(i).getName());
					ti.getElement().getStyle().setBackgroundColor("#"+result.getData().get(i).getColor());
					ti.getElement().getStyle().setPadding(3, Unit.PX);
					ti.getElement().getStyle().setColor("white");
					ti.getElement().getStyle().setMarginBottom(5, Unit.PX);
					ti.getElement().getStyle().setMarginTop(5, Unit.PX);
					ti.getElement().getStyle().setMarginRight(100, Unit.PX);
					ti.getElement().getStyle().setFontWeight(FontWeight.BOLD);
					//issueTreeItem.addItem(ti);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
}
