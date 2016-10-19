package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.gwt.api.client.incidence.JsSize;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.vaadin.widget.VaadinComboBox;
import com.vaadin.polymer.vaadin.widget.event.ValueChangedEvent;
import com.vaadin.polymer.vaadin.widget.event.ValueChangedEventHandler;

public class FilterPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, FilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();

    Issues issues;

    @UiField PaperButton openButton;
    @UiField InlineLabel openLabel;
    
    @UiField PaperButton closeButton;
    @UiField InlineLabel closeLabel;
    
    @UiField PaperButton removeButton;
    @UiField InlineLabel removeLabel;
    
    @UiField PaperButton priorityButton;
    @UiField PaperButton tagButton;
    @UiField PaperButton typeButton;
    @UiField PaperButton creatorButton;
    @UiField PaperButton assignedButton;
    @UiField PaperButton enterpriseButton;
    @UiField PaperButton orderButton;
    @UiField PaperButton dateButton;
    @UiField TextBox titleFilter;
    
    Incidence incidence;
    
    public FilterPanel(Issues issues, Incidence incidence) {

    	this.issues = issues;
    	this.incidence = incidence;

    	initWidget(binder.createAndBindUi(this));       
    	openButton.setNoink(true);
    	openButton.addStyleName(AON.AON_CSS.aonBold());
    	closeButton.setNoink(true);
    	removeButton.setNoink(true);
    	priorityButton.setNoink(true);
    	tagButton.setNoink(true);
    	typeButton.setNoink(true);
    	creatorButton.setNoink(true);
    	assignedButton.setNoink(true);
    	enterpriseButton.setNoink(true);
    	orderButton.setNoink(true);
    	
    	
    	titleFilter.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				getIssues().issueFilter.setTitle(titleFilter.getText());
				getIssues().updateIssueList(getIssues().issueFilter, false);
			}
		});
    }
    
	@UiHandler("openButton")
	void openButtonClick(ClickEvent event){
		removeButton.removeStyleName(AON.AON_CSS.aonBold());
		closeButton.removeStyleName(AON.AON_CSS.aonBold());
		openButton.addStyleName(AON.AON_CSS.aonBold());
		getIssues().issueFilter.setState("open");
		getIssues().updateIssueList(issues.issueFilter, false);
	}
	
	@UiHandler("closeButton")
	void closeButtonClick(ClickEvent event){
		removeButton.removeStyleName(AON.AON_CSS.aonBold());
		openButton.removeStyleName(AON.AON_CSS.aonBold());
		closeButton.addStyleName(AON.AON_CSS.aonBold());
		getIssues().issueFilter.setState("closed");
		getIssues().updateIssueList(issues.issueFilter, false);
	}
	
	@UiHandler("removeButton")
	void removeButtonClick(ClickEvent event){
		openButton.removeStyleName(AON.AON_CSS.aonBold());
		closeButton.removeStyleName(AON.AON_CSS.aonBold());
		removeButton.addStyleName(AON.AON_CSS.aonBold());
		getIssues().issueFilter.setState("deleted");
		getIssues().updateIssueList(issues.issueFilter, false);
	}
	
	@UiHandler("priorityButton")
	void priorityButtonClick(ClickEvent event){
		incidence.getPriorities(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override public void onSuccess(JSON<JsLabel> result) {
				AonJsArray<JsLabel> labels = result.getData();
				PopupPanel popup = new PopupPanel();
				VaadinComboBox vcb = new VaadinComboBox();
				vcb.setItems(getLabelArray(labels));
				vcb.setLabel("Prioridad");
				vcb.addValueChangedHandler(new ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(ValueChangedEvent event) {
						if(labels != null)
							for(Integer i = 0; i < labels.length(); i++)
								if(labels.get(i).getName().equals(vcb.getValue())) {
									JsLabel jsLabel = labels.get(i);
									priorityButton.setTitle(jsLabel.getName());
									getIssues().issueFilter.setPriority(jsLabel.getName());
									getIssues().updateIssueList(issues.issueFilter, false);
								}	
						popup.hide();
					}
				});
				popup.add(vcb);
				int left = priorityButton.getAbsoluteLeft();
				int top = priorityButton.getAbsoluteTop()
						+ priorityButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(vcb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				vcb.toggle();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("typeButton")
	void typeButtonClick(ClickEvent event){
		incidence.getTypes(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override public void onSuccess(JSON<JsLabel> result) {
				AonJsArray<JsLabel> labels = result.getData();
				PopupPanel popup = new PopupPanel();
				VaadinComboBox vcb = new VaadinComboBox();
				vcb.setItems(getLabelArray(labels));
				vcb.setLabel("Tipo");
				vcb.addValueChangedHandler(new ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(ValueChangedEvent event) {
						if(labels != null)
							for(Integer i = 0; i < labels.length(); i++)
								if(labels.get(i).getName().equals(vcb.getValue())) {
									JsLabel jsLabel = labels.get(i);
									typeButton.setTitle(jsLabel.getName());
									getIssues().issueFilter.setType(jsLabel.getId());
									getIssues().updateIssueList(issues.issueFilter, false);
								}	
						popup.hide();
					}
				});
				popup.add(vcb);
				int left = typeButton.getAbsoluteLeft();
				int top = typeButton.getAbsoluteTop()
						+ typeButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(vcb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				vcb.toggle();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("tagButton")
	void tagButtonClick(ClickEvent event){
		incidence.getLabels(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override public void onSuccess(JSON<JsLabel> result) {
				AonJsArray<JsLabel> labels = result.getData();
				PopupPanel popup = new PopupPanel();
				VaadinComboBox vcb = new VaadinComboBox();
				vcb.setItems(getLabelArray(labels));
				vcb.setLabel("Etiqueta");
				vcb.addValueChangedHandler(new ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(ValueChangedEvent event) {
						if(labels != null)
							for(Integer i = 0; i < labels.length(); i++)
								if(labels.get(i).getName().equals(vcb.getValue())) {
									JsLabel jsLabel = labels.get(i);
									tagButton.setTitle(jsLabel.getName());
									getIssues().issueFilter.setLabels(jsLabel.getId());
									getIssues().updateIssueList(issues.issueFilter, false);
								}	
						popup.hide();
					}
				});
				popup.add(vcb);
				int left = tagButton.getAbsoluteLeft();
				int top = tagButton.getAbsoluteTop()
						+ tagButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(vcb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				vcb.toggle();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("assignedButton")
	void assignedButtonClick(ClickEvent event){
		incidence.getUsers(new AsyncCallback<JSON<JsUser>>() {
			
			@Override public void onSuccess(JSON<JsUser> result) {
				AonJsArray<JsUser> users = result.getData();
				PopupPanel popup = new PopupPanel();
				VaadinComboBox vcb = new VaadinComboBox();
				vcb.setItems(getUserArray(users));
				vcb.setLabel("Asignado");
				vcb.addValueChangedHandler(new ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(ValueChangedEvent event) {
						if(users != null)
							for(Integer i = 0; i < users.length(); i++)
								if(users.get(i).getLogin().equals(vcb.getValue())) {
									JsUser jsLabel = users.get(i);
									assignedButton.setTitle(jsLabel.getLogin());
									getIssues().issueFilter.setAssignee(jsLabel.getId());
									getIssues().updateIssueList(issues.issueFilter, false);
								}	
						popup.hide();
					}
				});
				popup.add(vcb);
				int left = assignedButton.getAbsoluteLeft();
				int top = assignedButton.getAbsoluteTop()
						+ assignedButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(vcb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				vcb.toggle();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("creatorButton")
	void creatorButtonClick(ClickEvent event){
		incidence.getApplicationUsers(new AsyncCallback<JSON<JsUser>>() {
			
			@Override public void onSuccess(JSON<JsUser> result) {
				AonJsArray<JsUser> users = result.getData();
				PopupPanel popup = new PopupPanel();
				VaadinComboBox vcb = new VaadinComboBox();
				vcb.setItems(getUserArray(users));
				vcb.setLabel("Creador");
				vcb.addValueChangedHandler(new ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(ValueChangedEvent event) {
						if(users != null)
							for(Integer i = 0; i < users.length(); i++)
								if(users.get(i).getLogin().equals(vcb.getValue())) {
									JsUser jsUser = users.get(i);
									creatorButton.setTitle(jsUser.getLogin());
									getIssues().issueFilter.setCreator(jsUser.getLogin());
									getIssues().updateIssueList(issues.issueFilter, false);
								}	
						popup.hide();
					}
				});
				popup.add(vcb);
				int left = creatorButton.getAbsoluteLeft();
				int top = creatorButton.getAbsoluteTop()
						+ creatorButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(vcb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				vcb.toggle();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}	
	
	@UiHandler("enterpriseButton")
	void enterpriseButtonClick(ClickEvent event){
		incidence.getRegistries(new AsyncCallback<JSON<JsUser>>() {
			
			@Override public void onSuccess(JSON<JsUser> result) {
				AonJsArray<JsUser> users = result.getData();
				PopupPanel popup = new PopupPanel();
				VaadinComboBox vcb = new VaadinComboBox();
				vcb.setItems(getUserArray(users));
				vcb.setLabel("Empresa");
				vcb.addValueChangedHandler(new ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(ValueChangedEvent event) {
						if(users != null)
							for(Integer i = 0; i < users.length(); i++)
								if(users.get(i).getLogin().equals(vcb.getValue())) {
									JsUser jsUser = users.get(i);
									enterpriseButton.setTitle(jsUser.getLogin());
									getIssues().issueFilter.setEnterprise(jsUser.getId());
									getIssues().updateIssueList(issues.issueFilter, false);
								}	
						popup.hide();
					}
				});
				popup.add(vcb);
				int left = enterpriseButton.getAbsoluteLeft();
				int top = enterpriseButton.getAbsoluteTop()
						+ enterpriseButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(vcb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				vcb.toggle();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}	
	
	@UiHandler("orderButton")
	void orderButtonClick(ClickEvent event){		
		PopupPanel popup = new PopupPanel();
		VaadinComboBox vcb = new VaadinComboBox();
		vcb.setItems("[\"Creados - Recientes\", \"Creados - Antiguos\", \"Modificados - Recientes\", \"Modificados - Antiguos\"]");
		vcb.setLabel("Orden");
		vcb.addValueChangedHandler(new ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(ValueChangedEvent event) {
				if(vcb.getValue().contains("Antiguos")) 
					getIssues().issueFilter.setDirection("asc");
				else getIssues().issueFilter.setDirection("desc");
				
				if(vcb.getValue().contains("Modificados")) 
					getIssues().issueFilter.setSort("updated");
				else getIssues().issueFilter.setSort("created");
				
				orderButton.setTitle(vcb.getValue());
				getIssues().updateIssueList(issues.issueFilter, false);	
				popup.hide();
			}
		});
		popup.add(vcb);
		int left = orderButton.getAbsoluteLeft();
		int top = orderButton.getAbsoluteTop()
				+ orderButton.getOffsetHeight();
		Integer width = Window.getClientWidth();
		if(left > width - 200){
			left = left - 200;
		}
		popup.setAutoHideEnabled(true);
		popup.addAutoHidePartner(vcb.getElementById("overlay"));
		popup.setPopupPosition(left, top);
		popup.show();
		vcb.toggle();
	}		
	
	@UiHandler("dateButton")
	void dateButtonClick(ClickEvent event){		
		PopupPanel popup = new PopupPanel();
		VaadinComboBox vcb = new VaadinComboBox();
		vcb.setItems("[\"Hoy\", \"Ayer\", \"Hace 1 semana\", \"Hace 1 mes\"]");
		vcb.setLabel("Fecha");
		vcb.addValueChangedHandler(new ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(ValueChangedEvent event) {
				if(vcb.getValue().contains("Hoy")) 
					getIssues().issueFilter.setDateDiff(1);
				else if(vcb.getValue().contains("Ayer")) 
					getIssues().issueFilter.setDateDiff(2);
				else if(vcb.getValue().contains("Hace 1 semana")) 
					getIssues().issueFilter.setDateDiff(7);
				else getIssues().issueFilter.setDateDiff(30);
				
				dateButton.setTitle(vcb.getValue());
				getIssues().updateIssueList(issues.issueFilter, false);	
				popup.hide();
			}
		});
		popup.add(vcb);
		int left = dateButton.getAbsoluteLeft();
		int top = dateButton.getAbsoluteTop()
				+ dateButton.getOffsetHeight();
		Integer width = Window.getClientWidth();
		if(left > width - 200){
			left = left - 200;
		}
		popup.setAutoHideEnabled(true);
		popup.addAutoHidePartner(vcb.getElementById("overlay"));
		popup.setPopupPosition(left, top);
		popup.show();
		vcb.toggle();
	}		

	private String getLabelArray(AonJsArray<JsLabel> labels) {
		String arr= "[";
		if(labels != null)
			for(Integer i = 0; i < labels.length(); i++){
				if(i > 0) arr = arr + " , ";
				arr = arr + "\""+ labels.get(i).getName()+"\"";
		}
		return arr + "]";
	}
	
	private String getUserArray(AonJsArray<JsUser> users) {
		String arr= "[";
		if(users != null)
			for(Integer i = 0; i < users.length(); i++){
				if(i > 0) arr = arr + " , ";
				arr = arr + "\""+ users.get(i).getLogin()+"\"";
		}
		return arr + "]";
	}
	
	public Issues getIssues() {
		return issues;
	}
	
	public void setButtonsLabels(JsSize s) {
		openLabel.setText(s.getOpen() + " Abierta");
		closeLabel.setText(s.getClosed() + " Cerrada");
		removeLabel.setText(s.getDeleted() + " Borrada");
	}
	
	public void initialize() {
    	openButton.addStyleName(AON.AON_CSS.aonBold());
    	closeButton.removeStyleName(AON.AON_CSS.aonBold());
    	removeButton.removeStyleName(AON.AON_CSS.aonBold());
    	
    	priorityButton.setTitle("");
    	tagButton.setTitle("");
    	typeButton.setTitle("");
    	creatorButton.setTitle("");
    	assignedButton.setTitle("");
    	enterpriseButton.setTitle("");
    	orderButton.setTitle("");
	}

}
