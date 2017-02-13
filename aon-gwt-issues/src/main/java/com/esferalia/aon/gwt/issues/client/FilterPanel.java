package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.polymer.paper.widget.PaperButton;

import net.aonsolutions.polymer.aon.widget.AonComboBox;

public class FilterPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, FilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();

    Issues issues;
    

    @UiField HorizontalPanel filterHorizontal;
    @UiField InlineLabel fastFilterLabel;
    @UiField InlineLabel priorityLabel;
    @UiField InlineLabel tagLabel;
    @UiField InlineLabel typeLabel;
    @UiField InlineLabel creatorLabel;
    @UiField InlineLabel assignedLabel;
    @UiField InlineLabel workgroupLabel;
    @UiField InlineLabel enterpriseLabel;
    @UiField InlineLabel orderLabel;
    @UiField InlineLabel dateLabel;
    
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
    @UiField PaperButton workgroupButton;
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
    	workgroupButton.setNoink(true);
    	enterpriseButton.setNoink(true);
    	orderButton.setNoink(true);
    	dateButton.setNoink(true);
    	
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
				AonComboBox acb = new AonComboBox();
				acb.setLabel("Prioridad");
				acb.setItemLabelPath("name");
				acb.setItemValuePath("name");
				acb.setItems(labels);
				
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsLabel jsLabel = acb.getSelectedItem().cast();
						if(jsLabel != null){
							priorityButton.setTitle(jsLabel.getName());
							priorityLabel.setText("Prioridad:"+jsLabel.getName()+"; ");
							getIssues().issueFilter.setPriority(jsLabel.getName());
							getIssues().updateIssueList(issues.issueFilter, false);
							popup.hide();		
						}
					}
				});
				popup.add(acb);
				int left = priorityButton.getAbsoluteLeft();
				int top = priorityButton.getAbsoluteTop()
						+ priorityButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
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
				AonComboBox acb = new AonComboBox();
				acb.setLabel("Tipo");
				acb.setItemLabelPath("name");
				acb.setItemValuePath("name");
				acb.setItems(labels);
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsLabel jsLabel = acb.getSelectedItem().cast();
						if(jsLabel != null){
							typeButton.setTitle(jsLabel.getName());
							typeLabel.setText("Tipo:"+jsLabel.getName()+"; ");
							getIssues().issueFilter.setType(jsLabel.getId());
							getIssues().updateIssueList(issues.issueFilter, false);
							popup.hide();											
						}
					}
				});
				popup.add(acb);
				int left = typeButton.getAbsoluteLeft();
				int top = typeButton.getAbsoluteTop()
						+ typeButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
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
				AonComboBox acb = new AonComboBox();
				acb.setItems(labels);
				acb.setItemLabelPath("name");
				acb.setItemValuePath("name");
				acb.setLabel("Etiqueta");
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsLabel jsLabel = acb.getSelectedItem().cast();
						if(jsLabel != null){
							tagButton.setTitle(jsLabel.getName());
							tagLabel.setText("Etiqueta:"+jsLabel.getName()+"; ");
							getIssues().issueFilter.setLabels(jsLabel.getId());
							getIssues().updateIssueList(issues.issueFilter, false);	
							popup.hide();
						}
					}
				});
				popup.add(acb);
				int left = tagButton.getAbsoluteLeft();
				int top = tagButton.getAbsoluteTop()
						+ tagButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
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
				AonComboBox acb = new AonComboBox();
				acb.setItems(users);
				acb.setItemLabelPath("login");
				acb.setItemValuePath("login");
				acb.setLabel("Asignado");
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsUser jsLabel = acb.getSelectedItem().cast();
						if(jsLabel != null){
							assignedButton.setTitle(jsLabel.getLogin());
							assignedLabel.setText("Asignado:"+jsLabel.getLogin()+"; ");
							getIssues().issueFilter.setAssignee(jsLabel.getId().toString());
							getIssues().updateIssueList(issues.issueFilter, false);
							popup.hide();
						}
					}
				});
				popup.add(acb);
				int left = assignedButton.getAbsoluteLeft();
				int top = assignedButton.getAbsoluteTop()
						+ assignedButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("workgroupButton")
	void workgroupButtonClick(ClickEvent event){
		incidence.getWorkgroups(new AsyncCallback<JSON<JsUser>>() {
			
			@Override public void onSuccess(JSON<JsUser> result) {
				AonJsArray<JsUser> users = result.getData();
				PopupPanel popup = new PopupPanel();
				AonComboBox acb = new AonComboBox();
				acb.setItems(users);
				acb.setItemLabelPath("login");
				acb.setItemValuePath("login");
				acb.setLabel("Grupo de Trabajo");
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsUser jsLabel = acb.getSelectedItem().cast();
						if(jsLabel != null){
							workgroupButton.setTitle(jsLabel.getLogin());
							workgroupLabel.setText("Grupo de Trabajo:"+jsLabel.getLogin()+"; ");
							getIssues().issueFilter.setWorkgroup(jsLabel.getId());
							getIssues().updateIssueList(issues.issueFilter, false);
							popup.hide();
						}
					}
				});
				popup.add(acb);
				int left = workgroupButton.getAbsoluteLeft();
				int top = workgroupButton.getAbsoluteTop()
						+ workgroupButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
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
				AonComboBox acb = new AonComboBox();
				acb.setItems(users);
				acb.setItemLabelPath("login");
				acb.setItemValuePath("login");
				acb.setLabel("Creador");
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsUser jsUser = acb.getSelectedItem().cast();
						if(jsUser != null){
							creatorButton.setTitle(jsUser.getLogin());
							creatorLabel.setText("Creador:"+jsUser.getLogin()+"; ");
							getIssues().issueFilter.setCreator(jsUser.getLogin());
							getIssues().updateIssueList(issues.issueFilter, false);	
							popup.hide();
						}
					}
				});
				popup.add(acb);
				int left = creatorButton.getAbsoluteLeft();
				int top = creatorButton.getAbsoluteTop()
						+ creatorButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
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
				AonComboBox acb = new AonComboBox();
				acb.setItems(users);
				acb.setItemLabelPath("login");
				acb.setItemValuePath("login");
				acb.setLabel("Empresa");
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsUser jsUser = acb.getSelectedItem().cast();
						if(jsUser != null){
							enterpriseButton.setTitle(jsUser.getLogin());
							enterpriseLabel.setText("Empresa:"+jsUser.getLogin()+"; ");
							getIssues().issueFilter.setEnterprise(jsUser.getId());
							getIssues().updateIssueList(issues.issueFilter, false);
							popup.hide();
						}
					}
				});
				popup.add(acb);
				int left = enterpriseButton.getAbsoluteLeft();
				int top = enterpriseButton.getAbsoluteTop()
						+ enterpriseButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}	
	
	@UiHandler("orderButton")
	void orderButtonClick(ClickEvent event){
		incidence.getOrderOptions(new AsyncCallback<JSON<JsObject>>() {
			
			@Override public void onSuccess(JSON<JsObject> result) {
				AonJsArray<JsObject> items = result.getData();
				PopupPanel popup = new PopupPanel();
				AonComboBox acb = new AonComboBox();
				acb.setLabel("Orden");
				acb.setItemLabelPath("name");
				acb.setItemValuePath("name");
				acb.setItems(items);
				
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsObject jsObject = acb.getSelectedItem().cast();			
						if(jsObject != null){
							if(jsObject.getName().contains("Antiguos")) 
								getIssues().issueFilter.setDirection("asc");
							else getIssues().issueFilter.setDirection("desc");
						
							if(jsObject.getName().contains("Modificados")) 
								getIssues().issueFilter.setSort("updated");
							else getIssues().issueFilter.setSort("created");
						
							orderButton.setTitle(jsObject.getName());
							orderLabel.setText("Orden:"+jsObject.getName()+"; ");
							getIssues().updateIssueList(issues.issueFilter, false);	
							popup.hide();					
						}
					}
				});
				popup.add(acb);
				int left = orderButton.getAbsoluteLeft();
				int top = orderButton.getAbsoluteTop()
						+ orderButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	@UiHandler("dateButton")
	void dateButtonClick(ClickEvent event){
		incidence.getDateOptions(new AsyncCallback<JSON<JsObject>>() {
			
			@Override public void onSuccess(JSON<JsObject> result) {
				AonJsArray<JsObject> items = result.getData();
				PopupPanel popup = new PopupPanel();
				AonComboBox acb = new AonComboBox();
				acb.setLabel("Fecha");
				acb.setItemLabelPath("name");
				acb.setItemValuePath("name");
				acb.setItems(items);
				
				acb.addValueChangedHandler(new net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler() {
					
					@Override
					public void onValueChanged(net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent event) {
						JsObject jsObject = acb.getSelectedItem().cast();								
						if(jsObject != null){
							if(jsObject.getName().contains("Hoy")) 
								getIssues().issueFilter.setDateDiff(1);
							else if(jsObject.getName().contains("Ayer")) 
								getIssues().issueFilter.setDateDiff(2);
							else if(jsObject.getName().contains("Hace 1 semana")) 
								getIssues().issueFilter.setDateDiff(7);
							else if(jsObject.getName().contains("Hace 1 mes"))
								getIssues().issueFilter.setDateDiff(30);
							else getIssues().issueFilter.setDateDiff(365);

							dateButton.setTitle(jsObject.getName());
							dateLabel.setText("Fecha:"+jsObject.getName()+"; ");
							getIssues().updateIssueList(issues.issueFilter, false);	
							popup.hide();					
						}
					}
				});
				popup.add(acb);
				int left = dateButton.getAbsoluteLeft();
				int top = dateButton.getAbsoluteTop()
						+ dateButton.getOffsetHeight();
				Integer width = Window.getClientWidth();
				if(left > width - 200){
					left = left - 200;
				}
				popup.setAutoHideEnabled(true);
				popup.addAutoHidePartner(acb.getElementById("overlay"));
				popup.setPopupPosition(left, top);
				popup.show();
				acb.open();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public Issues getIssues() {
		return issues;
	}
	
	public void setButtonsLabels(JsSize s) {
		openLabel.setText(s.getOpen() + " Abierta");
		closeLabel.setText(s.getClosed() + " Cerrada");
		removeLabel.setText(s.getDeleted() + " Borrada");
	}
	
	public void initialize(Boolean faq) {
		for(Integer i = 1; i < filterHorizontal.getWidgetCount(); i++)
			filterHorizontal.remove(i);
			
    	openButton.addStyleName(AON.AON_CSS.aonBold());
    	closeButton.removeStyleName(AON.AON_CSS.aonBold());
    	removeButton.removeStyleName(AON.AON_CSS.aonBold());
    	
    	priorityButton.setTitle("");priorityLabel.setText("");
    	tagButton.setTitle("");tagLabel.setText("");
    	typeButton.setTitle("");typeLabel.setText("");
    	creatorButton.setTitle("");creatorLabel.setText("");
    	assignedButton.setTitle("");assignedLabel.setText("");
    	workgroupButton.setTitle("");workgroupLabel.setText("");
    	enterpriseButton.setTitle("");enterpriseLabel.setText("");
    	orderButton.setTitle("");orderLabel.setText("");
    	dateButton.setTitle("");dateLabel.setText("");
    	fastFilterLabel.setText("");
    	
    	titleFilter.setValue("");
    	
    	if(faq){
    		openButton.setVisible(false);
    		closeButton.setVisible(false);
    		removeButton.setVisible(false);
    		priorityButton.setVisible(false);
    		assignedButton.setVisible(false);
    		enterpriseButton.setVisible(false);
    	} else {
    		openButton.setVisible(true);
			closeButton.setVisible(true);
			removeButton.setVisible(true);
			priorityButton.setVisible(true);
			assignedButton.setVisible(true);
			enterpriseButton.setVisible(true);
    	}
	}

}
