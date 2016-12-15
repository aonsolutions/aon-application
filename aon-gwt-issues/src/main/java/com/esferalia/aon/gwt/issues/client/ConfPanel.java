package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.IssueFilter;
import com.esferalia.aon.gwt.api.client.incidence.JsFastFilter;
import com.esferalia.aon.gwt.api.client.incidence.JsGithub;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.esferalia.aon.gwt.common.client.polymer.AonToolbar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.IronCollapse;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.iron.widget.IronSelector;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;
import com.vaadin.polymer.paper.widget.PaperTextarea;
import com.vaadin.polymer.paper.widget.PaperToggleButton;
import com.vaadin.polymer.paper.widget.event.ChangeEvent;
import com.vaadin.polymer.paper.widget.event.ChangeEventHandler;

import net.aonsolutions.polymer.aon.widget.AonComboBox;

public class ConfPanel extends Composite {

    interface Binder extends UiBinder<HTMLPanel , ConfPanel> {
    	
    }
    
    @UiField HTMLPanel panel;
    @UiField PaperButton faqButton;
    @UiField PaperIconButton newFaqButton;

    @UiField Button fastFilterHeading;
    @UiField IronCollapse fastFilterCollapse;
    
    @UiField Button workgroupHeading;
    @UiField IronCollapse workgroupCollapse;
    @UiField IronSelector workgroupSelector;
    
    @UiField Button userHeading;
    @UiField IronCollapse userCollapse;
    @UiField IronSelector userSelector;
    
    @UiField Button gitHeading;
    @UiField IronCollapse gitCollapse;
    
    private static Binder binder = GWT.create(Binder.class);
	
	Incidence incidence;
	Issues issues;
	private Boolean admin;
	
    public ConfPanel(Issues issues, Incidence incidence, Boolean admin) {
    	this.incidence = incidence;
    	this.issues = issues;
    	this.admin = admin;
    	
    	initWidget(binder.createAndBindUi(this));
    	
    	initFaqOptions();
    	initFastFilterOptions();
    	initWorkgroupOptions();
    	initUserOptions();
    	initGitOptions();
    }
    
    // -------------------- FAST FILTER (FILTRO RÁPIDO) CONFIGURATION
    
    private void initFastFilterOptions(){
    	incidence.getFastFilter(new AsyncCallback<JSON<JsFastFilter>>() {
			
			@Override
			public void onSuccess(JSON<JsFastFilter> result) {
				VerticalPanel vp = new VerticalPanel();
		    	
		    	PaperToggleButton mias = new PaperToggleButton();
		    	mias.setDisabled(admin);
		    	mias.setChecked(result.getOneData().getMine());
		    	mias.setStyle("padding-left:20px;padding-right:20px;padding-top:20px;");
		    	InlineLabel label1 = new InlineLabel("Mias");
		    	label1.getElement().getStyle().setFontSize(16, Unit.PX);
		    	mias.add(label1);
		    	mias.addChangeHandler(new ChangeEventHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						String requestData = "{\"mine\":\""+ mias.getChecked()+"\"}";
						incidence.setFastFilter(requestData);
					}
				});
		    	vp.add(mias);

		    	PaperToggleButton sinGrupo = new PaperToggleButton();
		    	sinGrupo.setDisabled(admin);
		    	sinGrupo.setStyle("padding-left:20px;padding-right:20px;");
		    	sinGrupo.setChecked(result.getOneData().getWithoutGroup());
		    	InlineLabel label2 = new InlineLabel("Sin Grupo");
		    	label2.getElement().getStyle().setFontSize(16, Unit.PX);
		    	sinGrupo.add(label2);
		    	sinGrupo.addChangeHandler(new ChangeEventHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						String requestData = "{\"without_group\":\""+ sinGrupo.getChecked()+"\"}";
						incidence.setFastFilter(requestData);
					}
				});
		    	vp.add(sinGrupo);

		    	PaperToggleButton sinOperario = new PaperToggleButton();
		    	sinOperario.setDisabled(admin);
		    	sinOperario.setStyle("padding-left:20px;padding-right:20px;");
		    	sinOperario.setChecked(result.getOneData().getWithoutOperator());
		    	InlineLabel label3 = new InlineLabel("Sin Operario");
		    	label3.getElement().getStyle().setFontSize(16, Unit.PX);
		    	sinOperario.add(label3);
		    	sinOperario.addChangeHandler(new ChangeEventHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						String requestData = "{\"without_operator\":\""+ sinOperario.getChecked()+"\"}";
						incidence.setFastFilter(requestData);
					}
				});
		    	vp.add(sinOperario);
		    	
				AonComboBox acb1 = new AonComboBox();
		    	acb1.setDisabled(admin);
				acb1.setItemLabelPath("name");
		    	acb1.setItems(result.getOneData().getTypes());
		    	acb1.setInputElementValue(result.getOneData().getPriority());
				acb1.setStyle("padding-left:20px;padding-right:20px;width:300px");
				acb1.setLabel("Tipo");
				acb1.addSelectedItemChangedHandler(new net.aonsolutions.polymer.aon.widget.event.SelectedItemChangedEventHandler() {
					
					@Override
					public void onSelectedItemChanged(net.aonsolutions.polymer.aon.widget.event.SelectedItemChangedEvent event) {
						JsLabel js = acb1.getSelectedItem().cast();	
						String requestData= "{\"type\":\""+ js.getName() +"\"}";
						incidence.setFastFilter(requestData);	
					}
				});
				vp.add(acb1);
		    	
		    	AonComboBox acb2 = new AonComboBox();
		    	acb2.setDisabled(admin);
		    	acb2.setItemLabelPath("name");
		    	acb2.setItems(result.getOneData().getPriorities());
		    	acb2.setInputElementValue(result.getOneData().getType());
		    	acb2.setStyle("padding-left:20px;padding-right:20px;padding-bottom: 20px; width:300px;");
		    	acb2.setLabel("Prioridad");
		    	acb2.addSelectedItemChangedHandler(new net.aonsolutions.polymer.aon.widget.event.SelectedItemChangedEventHandler() {
					
					@Override
					public void onSelectedItemChanged(net.aonsolutions.polymer.aon.widget.event.SelectedItemChangedEvent event) {
						JsLabel js = acb2.getSelectedItem().cast();	
						String requestData= "{\"priority\":\""+ js.getName() +"\"}";
						incidence.setFastFilter(requestData);	
					}
				});
		    	vp.add(acb2);
		    	
		    	fastFilterCollapse.add(vp);
				if(!fastFilterCollapse.getOpened())
					fastFilterCollapse.toggle();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
    	
    	fastFilterHeading.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fastFilterCollapse.toggle();
			}
		});
    }

    // -------------------- GITHUB CONFIGURATION
    
    private void initGitOptions(){
    	gitHeading.setVisible(false);
    	gitCollapse.setVisible(false);
    	incidence.getGithubConfiguration(new AsyncCallback<JSON<JsGithub>>() {
			
			@Override
			public void onSuccess(JSON<JsGithub> result) {
				VerticalPanel vp = new VerticalPanel();
				
		    	PaperInput gitUsername = new PaperInput();
		    	gitUsername.setDisabled(admin);
		    	gitUsername.setLabel("Nombre de usuario");
		    	gitUsername.setValue(result.getOneData().getUsername());
		    	gitUsername.setStyle("padding-left:20px;padding-right:20px;width:300px;");
		    	gitUsername.addChangeHandler(new ChangeEventHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						String requestData = "{\"username\":\""+ gitUsername.getValue() +"\"}";
						incidence.setGithubConfiguration(requestData);
					}
				});
		    	vp.add(gitUsername);

		    	PaperInput gitRepository = new PaperInput();
		    	gitRepository.setDisabled(admin);
		    	gitRepository.setLabel("Repositorio");
		    	gitRepository.setValue(result.getOneData().getRepository());
		    	gitRepository.setStyle("padding-left:20px;padding-right:20px;width:300px;");
		    	gitRepository.addChangeHandler(new ChangeEventHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						String requestData = "{\"repository\":\""+ gitRepository.getValue() +"\"}";
						incidence.setGithubConfiguration(requestData);
					}
				});
		    	vp.add(gitRepository);
		    	
				PaperInput gitToken = new PaperInput();
		    	gitToken.setDisabled(admin);
				gitToken.setLabel("Token");
		    	gitToken.setValue(result.getOneData().getToken());
		    	gitToken.setStyle("padding-left:20px;padding-right:20px;padding-bottom:20px;width:300px;");
		    	gitToken.addChangeHandler(new ChangeEventHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						String requestData = "{\"token\":\""+ gitToken.getValue() +"\"}";
						incidence.setGithubConfiguration(requestData);
					}
				});
		    	vp.add(gitToken);
		    	
		    	gitCollapse.add(vp);
		    	if(!gitCollapse.getOpened())
					gitCollapse.toggle();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
    		    	
    	gitHeading.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				gitCollapse.toggle();
			}
		});
    }

    // -------------------- FAQs OPTIONS
    
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
						issues.contentDockLayoutPanel.add(new IssuePanel(issues, incidence, result, -1));
						issues.dockLayoutPanel.add(issues.contentDockLayoutPanel);
					}
					@Override
					public void onFailure(Throwable caught) {}
				});
				hide();
			}
		};
	}
    
    // -------------------- WORKGROUP & USER PRINT WIDGETS
    
    private void initWorkgroupOptions(){
    	loadWorkgroup();
		workgroupHeading.addClickHandler(new ClickHandler() {
	   	
	    	   @Override
	    	   public void onClick(ClickEvent event) {
	    		   workgroupCollapse.toggle();
	    	   }
	    });
    }
    
    private void initUserOptions(){
    	loadUser();
    	userHeading.addClickHandler(new ClickHandler() {
			
    		@Override
	    	public void onClick(ClickEvent event) {
				userCollapse.toggle();
			}
    		
	    });
    }
    
    private void printNewButton(Boolean group){
		PaperIconButton pib = new PaperIconButton();
		pib.setIcon("add");
		pib.setDisabled(admin);
		pib.setStyle("min-height: 30px;");
		pib.getElement().getStyle().setLeft(290, Unit.PX);
		pib.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(group) addWorkgroup();
				else addUser();
			}
		});
		if(group) workgroupSelector.add(pib);
		else userSelector.add(pib);
    }
    
	
   
    private void printItem(JsUser user, String icon, Boolean group){
    	HorizontalPanel hp = new HorizontalPanel();
		PaperItem item = new PaperItem();
				
		IronIcon ii = new IronIcon();
		ii.setIcon(icon);
		
		item.add(ii);
		item.add(new Label(user.getLogin()));
		item.setStyle("min-height: 30px;");	
					
		PaperIconButton edit = new PaperIconButton();
		edit.setIcon("create");
		edit.setDisabled(admin);
		edit.setStyle("min-height: 30px;position:absolute;right:40px;padding-top:0px;");
		edit.addClickHandler(new ClickHandler() {
			@Override 
			public void onClick(ClickEvent event) {
				if(group) updateWorkgroup(user);
				else updateUser(user);
			}
		});
		edit.setVisible(false);
					
		PaperIconButton del = new PaperIconButton();
		del.setIcon("delete");
		del.setDisabled(admin);
		del.setStyle("min-height: 30px;position:absolute;right:10px;padding-top:0px;");
		del.getElement().getStyle().setLeft(290, Unit.PX);
		del.addClickHandler(new ClickHandler() {
			@Override 
			public void onClick(ClickEvent event) {
				if(group) removeWorkgroup(user);
				else removeUser(user);
			}
		});
		del.setVisible(false);
					
		hp.add(item);
		hp.add(edit);
		hp.add(del);
		hp.setWidth("100%");
		hp.addDomHandler(new MouseOverHandler() {				
			@Override 
			public void onMouseOver(MouseOverEvent event) {
				edit.setVisible(true);
				del.setVisible(true);
			}
		}, MouseOverEvent.getType());			
		hp.addDomHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				edit.setVisible(false);
				del.setVisible(false);
			}
		}, MouseOutEvent.getType());
		if(group) workgroupSelector.add(hp);
		else userSelector.add(hp);		
    }
    
    // -------------------- WORKGROUP (GRUPO DE TRABAJO) OPTIONS / ACTIONS
    
    private void loadWorkgroup(){
    	incidence.getGroups(new AsyncCallback<JSON<JsUser>>() {

			@Override public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(JSON<JsUser> result) {
				result.getData().stream().forEach(r -> {
					printItem(r, "group-work", true);
				});
				printNewButton(true);
				if(!workgroupCollapse.getOpened())
					workgroupCollapse.toggle();
			}
			
		});
    }
    
    private void addWorkgroup(){		
		PaperInput pi = new PaperInput();
		pi.setLabel("Grupo de Trabajo");
		AonDialog2 dialog =  new AonDialog2("Nuevo Grupo de Trabajo",pi){
			@Override protected void onCancel() {hide();}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				incidence.addGroup("{\"name\":\""+ pi.getValue() +"\"}", new AsyncCallback<JsUser>() {
					
					@Override
					public void onSuccess(JsUser result) {
						workgroupSelector.removeFromParent();
						workgroupSelector = new IronSelector();
						workgroupCollapse.add(workgroupSelector);
						loadWorkgroup();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});		
				hide();
			}
		};
		dialog.center();	
	}
    
    private void updateWorkgroup(JsUser user){
    	PaperInput pi = new PaperInput();
		pi.setLabel("Grupo de Trabajo");
		pi.setValue(user.getLogin());
		AonDialog2 dialog =  new AonDialog2("Editar Grupo de Trabajo",pi){
			@Override protected void onCancel() {hide();}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				incidence.updateGroup(user.getId(), "{\"name\":\""+ pi.getValue() +"\"}", new AsyncCallback<JsUser>() {
					
					@Override
					public void onSuccess(JsUser result) {
						workgroupSelector.removeFromParent();
						workgroupSelector = new IronSelector();
						workgroupCollapse.add(workgroupSelector);
						loadWorkgroup();					
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
				hide();
			}
		};
		dialog.center();
    }
    
    private void removeWorkgroup(JsUser user){
    	incidence.removeGroup(user.getId(), new AsyncCallback<JsUser>() {
			
			@Override public void onSuccess(JsUser result) {
				workgroupSelector.removeFromParent();
				workgroupSelector = new IronSelector();
				workgroupCollapse.add(workgroupSelector);
				loadWorkgroup();	
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
    }
    
    // -------------------- USER (OPERARIO) OPTIONS / ACTIONS
    
    private void loadUser(){
    	incidence.getOperators(new AsyncCallback<JSON<JsUser>>() {
    		
			@Override public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(JSON<JsUser> result) {
				result.getData().stream().forEach(r -> {
					printItem(r, "account-circle", false);
				});
				printNewButton(false);
				if(!userCollapse.getOpened())
					userCollapse.toggle();
			}
			
		});
    }
	
	private void addUser(){		
		VerticalPanel vp = new VerticalPanel();
		PaperInput pi = new PaperInput();
		pi.setLabel("Operario");
		vp.add(pi);
		PaperInput pi2 = new PaperInput();
		pi2.setLabel("Email");
		vp.add(pi2);
		AonDialog2 dialog =  new AonDialog2("Nuevo Operario",vp){
			@Override protected void onCancel() {hide();}
			@Override protected void onAccept() {
				VerticalPanel vp = (VerticalPanel) content.getWidget(0);
				PaperInput name = (PaperInput) vp.getWidget(0);
				PaperInput email = (PaperInput) vp.getWidget(1);
				incidence.addOperator("{\"name\":\""+ name.getValue() +"\",\"email\":\""+ email.getValue() +"\"}", new AsyncCallback<JsUser>() {
					
					@Override
					public void onSuccess(JsUser result) {
						userSelector.removeFromParent();
						userSelector = new IronSelector();
						userCollapse.add(userSelector);
						loadUser();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});		
				hide();
			}
		};
		dialog.center();
	}
	
    private void updateUser(JsUser user){
    	VerticalPanel vp = new VerticalPanel();
		PaperInput pi = new PaperInput();
		pi.setValue(user.getLogin());
		pi.setLabel("Operario");
		vp.add(pi);
		PaperInput pi2 = new PaperInput();
		pi2.setValue(user.getEmail());
		pi2.setLabel("Email");
		vp.add(pi2);
		
		AonDialog2 dialog =  new AonDialog2("Editar Operario",vp){
			@Override protected void onCancel() {hide();}
			@Override protected void onAccept() {
				VerticalPanel vp = (VerticalPanel) content.getWidget(0);
				PaperInput name = (PaperInput) vp.getWidget(0);
				PaperInput email = (PaperInput) vp.getWidget(1);
				incidence.updateOperator(user.getId(), "{\"name\":\""+ name.getValue() +"\",\"email\":\""+ email.getValue() +"\"}", new AsyncCallback<JsUser>() {
					
					@Override
					public void onSuccess(JsUser result) {
						userSelector.removeFromParent();
						userSelector = new IronSelector();
						userCollapse.add(userSelector);
						loadUser();					
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
				hide();
			}
		};
		dialog.center();
    }
    
    private void removeUser(JsUser user){
    	incidence.removeOperator(user.getId(), new AsyncCallback<JsUser>() {
			
			@Override public void onSuccess(JsUser result) {
				userSelector.removeFromParent();
				userSelector = new IronSelector();
				userCollapse.add(userSelector);
				loadUser();	
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
    }
}
