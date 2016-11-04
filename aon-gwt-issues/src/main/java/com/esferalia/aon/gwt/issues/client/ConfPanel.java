package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.IssueFilter;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.IronCollapse;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperTextarea;

public class ConfPanel extends Composite {

    interface Binder extends UiBinder<HTMLPanel , ConfPanel> {
    	
    }
    
    @UiField HTMLPanel panel;
    @UiField PaperButton faqButton;
    @UiField PaperIconButton newFaqButton;
    @UiField Button gitHeading;
    @UiField IronCollapse gitCollapse;
    @UiField PaperInput gitUsername;
    @UiField PaperInput gitRepository;
    @UiField PaperInput gitToken;
    
    private static Binder binder = GWT.create(Binder.class);
	
	Incidence incidence;
	Issues issues;
	
    public ConfPanel(Issues issues, Incidence incidence) {
    	this.incidence = incidence;
    	this.issues = issues;
    	initWidget(binder.createAndBindUi(this));
    	
    	initFaqOptions();	
    	initGitOptions();
    }
    
    
    private void initFaqOptions(){
		faqButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				FilterPanel fp = (FilterPanel) issues.searchContent.getWidget(0);
				fp.initialize(true);
				issues.issueFilter = new IssueFilter();
				issues.issueFilter.setState("faq");
				issues.updateIssueList(issues.issueFilter, false);		
			}
		});
		
		newFaqButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AonDialog2 dialog = createAddDialog();
				dialog.center();
			}
		});
	}

    private void initGitOptions(){
		if(!gitCollapse.getOpened())
			gitCollapse.toggle();
		
    	gitUsername.setLabel("Nombre de usuario");
    	gitUsername.setStyle("padding-left:20px;padding-right:20px;");
    	
    	gitRepository.setLabel("Repositorio");
    	gitRepository.setStyle("padding-left:20px;padding-right:20px;");
    	
    	gitToken.setLabel("Token");
    	gitToken.setStyle("padding-left:20px;padding-right:20px;");
    	
    	gitHeading.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				gitCollapse.toggle();
			}
		});
    }
    
    private AonDialog2 createAddDialog(){	
		VerticalPanel v = new VerticalPanel();
		
		PaperInput pi = new PaperInput();
		pi.setLabel("Titulo");
		v.add(pi);
		
		PaperTextarea pi2 = new PaperTextarea();
		pi2.setLabel("Descripcion");
		v.add(pi2);
		
		return new AonDialog2("Nueva Incidencia",v){
			@Override protected void onCancel() {hide();}
			@Override protected void onAccept() {
				VerticalPanel vp = (VerticalPanel) content.getWidget(0);	
				PaperInput pi = (PaperInput) vp.getWidget(0);
				PaperTextarea pi4 = (PaperTextarea) vp.getWidget(1);
				
				String r= "{\"title\":\""+ pi.getValue() +"\",\"body\":\""+ pi4.getValue()+" \",\"assignee\":\" \",\"labels\":[],"
						+ "\"state\":\""+ "faq" +"\", \"due_date\":\""+ "31/12/2100" +"\"}";
					
				incidence.createOrgIssue(r, new AsyncCallback<JsIssue>() {
					
					@Override
					public void onSuccess(JsIssue result) {
						issues.contentDockLayoutPanel.removeFromParent();
						AonToolbar t = (AonToolbar) issues.toolbar.getWidget(0);
						t.setVisibleRefreshButton(false);
						issues.contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
						issues.contentDockLayoutPanel.add(new IssuePanel(issues, incidence, result));
						issues.dockLayoutPanel.add(issues.contentDockLayoutPanel);
					}
					@Override
					public void onFailure(Throwable caught) {}
				});
				hide();
			}
		};
	}
}
