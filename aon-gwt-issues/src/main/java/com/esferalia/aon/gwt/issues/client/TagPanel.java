package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.vaadin.polymer.iron.widget.IronCollapse;
import com.vaadin.polymer.iron.widget.IronSelector;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;

public class TagPanel extends Composite {

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
        
    private static Binder binder = GWT.create(Binder.class);
	
	Incidence incidence;
	
    public TagPanel(Incidence incidence) {
    	this.incidence = incidence;
    	initWidget(binder.createAndBindUi(this));
    	
        createPriority();
        createType();
        createTag();
    }
    
    private void createPriority() {
        incidence.getPriorities(new AsyncCallback<JSON<JsLabel>>() {

			@Override
			public void onSuccess(JSON<JsLabel> result) {
				for(JsLabel label : result.getData().toLinkedList()){
					PaperItem item = new PaperItem();
					item.add(new Label(label.getName()));
					item.setStyle("min-height: 30px;");	
					
					PaperIconButton edit = new PaperIconButton();
					edit.setIcon("create");
					edit.setStyle("min-height: 30px;position:absolute;right:40px;");
					edit.addClickHandler(new ClickHandler() {
						@Override public void onClick(ClickEvent event) {
							onClickEditPriorityButton(event, label);
						}
					});
					item.add(edit);
					edit.setVisible(false);
					
					PaperIconButton del = new PaperIconButton();
					del.setIcon("delete");
					del.setStyle("min-height: 30px;position:absolute;right:0px;");
					del.getElement().getStyle().setLeft(300, Unit.PX);
					del.addClickHandler(new ClickHandler() {
						@Override public void onClick(ClickEvent event) {
							onClickRemovePriorityButton(event, label);
						}
					});
					del.setVisible(false);
					item.add(del);
					
					item.addDomHandler(new MouseOverHandler() {
						
						@Override
						public void onMouseOver(MouseOverEvent event) {
							edit.setVisible(true);
							del.setVisible(true);
						}
					}, MouseOverEvent.getType());
					
					item.addDomHandler(new MouseOutHandler() {
						
						@Override
						public void onMouseOut(MouseOutEvent event) {
							edit.setVisible(false);
							del.setVisible(false);
						}
					}, MouseOutEvent.getType());
					
					prioritySelector.add(item);
				}				
				PaperIconButton pib = new PaperIconButton();
				pib.setIcon("add");
				pib.setStyle("min-height: 30px;");
				pib.getElement().getStyle().setLeft(300, Unit.PX);
				pib.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						onClickPriorityButton(event);
					}
				});
				prioritySelector.add(pib);

				heading1.addClickHandler(new ClickHandler() {
			   	
					@Override
			    	public void onClick(ClickEvent event) {
			    		collapse1.toggle();
			    	}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
    
    private void createType() {
        incidence.getTypes(new AsyncCallback<JSON<JsLabel>>() {

			@Override
			public void onSuccess(JSON<JsLabel> result) {
				for(JsLabel label : result.getData().toLinkedList()){
					PaperItem item = new PaperItem();
					item.add(new Label(label.getName()));
					item.setStyle("min-height: 30px;");	
					
					PaperIconButton edit = new PaperIconButton();
					edit.setIcon("create");
					edit.setStyle("min-height: 30px;position:absolute;right:40px;");
					edit.addClickHandler(new ClickHandler() {
						@Override public void onClick(ClickEvent event) {
							onClickEditTypeButton(event, label);
						}
					});
					edit.setVisible(false);
					item.add(edit);
					
					PaperIconButton del = new PaperIconButton();
					del.setIcon("delete");
					del.setStyle("min-height: 30px;position:absolute;right:0px;");
					del.getElement().getStyle().setLeft(300, Unit.PX);
					del.addClickHandler(new ClickHandler() {
						@Override public void onClick(ClickEvent event) {
							onClickRemoveTypeButton(event, label);
						}
					});
					del.setVisible(false);
					item.add(del);
					
					item.addDomHandler(new MouseOverHandler() {
						
						@Override
						public void onMouseOver(MouseOverEvent event) {
							edit.setVisible(true);
							del.setVisible(true);
						}
					}, MouseOverEvent.getType());
					
					item.addDomHandler(new MouseOutHandler() {
						
						@Override
						public void onMouseOut(MouseOutEvent event) {
							edit.setVisible(false);
							del.setVisible(false);
						}
					}, MouseOutEvent.getType());
					
					typeSelector.add(item);
				}			
				PaperIconButton pib = new PaperIconButton();
				pib.setIcon("add");
				pib.setStyle("min-height: 30px;");
				pib.getElement().getStyle().setLeft(300, Unit.PX);
				pib.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						onClickTypeButton(event);
					}
				});
				typeSelector.add(pib);
				
				heading2.addClickHandler(new ClickHandler() {
				   		
			    	   @Override
			    	   public void onClick(ClickEvent event) {
			    		   collapse2.toggle();
			    	   }
			    });
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
   
    private void createTag() {
    	 incidence.getLabels(new AsyncCallback<JSON<JsLabel>>() {

 			@Override
 			public void onSuccess(JSON<JsLabel> result) {
 				for(JsLabel label : result.getData().toLinkedList()){
 					PaperItem item = new PaperItem();
 					item.add(new Label(label.getName()));
 					item.setStyle("min-height: 30px;");	

					PaperIconButton edit = new PaperIconButton();
					edit.setIcon("create");
					edit.setStyle("min-height: 30px;position:absolute;right:40px;");
					edit.addClickHandler(new ClickHandler() {
						@Override public void onClick(ClickEvent event) {
							onClickEditTagButton(event, label);
						}
					});
					edit.setVisible(false);
					item.add(edit);
 					
					PaperIconButton del = new PaperIconButton();
					del.setIcon("delete");
					del.setStyle("min-height: 30px;position:absolute;right:0px;");
					del.getElement().getStyle().setLeft(300, Unit.PX);
					del.addClickHandler(new ClickHandler() {
						@Override public void onClick(ClickEvent event) {
							onClickRemoveTagButton(event, label);
						}
					});
					del.setVisible(false);
					item.add(del);
					
					item.addDomHandler(new MouseOverHandler() {
						
						@Override
						public void onMouseOver(MouseOverEvent event) {
							edit.setVisible(true);
							del.setVisible(true);
						}
					}, MouseOverEvent.getType());
					
					item.addDomHandler(new MouseOutHandler() {
						
						@Override
						public void onMouseOut(MouseOutEvent event) {
							edit.setVisible(false);
							del.setVisible(false);
						}
					}, MouseOutEvent.getType());
					
 					tagSelector.add(item);
 				}			
 				PaperIconButton pib = new PaperIconButton();
 				pib.setIcon("add");
 				pib.setStyle("min-height: 30px;");
 				pib.getElement().getStyle().setLeft(300, Unit.PX);
 				pib.addClickHandler(new ClickHandler() {
 					
 					@Override
 					public void onClick(ClickEvent event) {
 						onClickTagButton(event);
 					}
 				});
 				tagSelector.add(pib);
 				
 				heading3.addClickHandler(new ClickHandler() {
 				   		
 			    	   @Override
 			    	   public void onClick(ClickEvent event) {
 			    		   collapse3.toggle();
 			    	   }
 			    });
 			}
 			
 			@Override
 			public void onFailure(Throwable caught) {}
 		});
	}
    
	void onClickTypeButton(ClickEvent event){		
		PaperInput pi = new PaperInput();
		pi.setLabel("Tipo");
		AonDialog dialog =  new AonDialog("Nuevo Tipo",pi){
			@Override protected void onCancel() {}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				incidence.createType("{\"name\":\""+ pi.getValue() +"\"}", new AsyncCallback<JsLabel>() {
					
					@Override
					public void onSuccess(JsLabel result) {
						typeSelector.removeFromParent();
						typeSelector = new IronSelector();
						collapse2.add(typeSelector);
						createType();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});		
			}
		};
		panel.add(dialog);
		dialog.open();	
	}
    
	void onClickPriorityButton(ClickEvent event){		
		PaperInput pi = new PaperInput();
		pi.setLabel("Prioridad");
		AonDialog dialog = new AonDialog("Nueva Prioridad",pi){
			@Override protected void onCancel() {}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				incidence.createPriority("{\"name\":\""+ pi.getValue() +"\"}", new AsyncCallback<JsLabel>() {
					
					@Override
					public void onSuccess(JsLabel result) {
						prioritySelector.removeFromParent();
						prioritySelector = new IronSelector();
						collapse1.add(prioritySelector);
						createPriority();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});			
			}
		};
		panel.add(dialog);
		dialog.open();	
	}
    
	void onClickTagButton(ClickEvent event){	
		PaperInput pi = new PaperInput();
		pi.setLabel("Etiqueta");
		AonDialog dialog =  new AonDialog("Nueva Etiqueta",pi){
			@Override protected void onCancel() {}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				incidence.createLabel("{\"name\":\""+ pi.getValue() +"\"}", new AsyncCallback<JsLabel>() {
					
					@Override
					public void onSuccess(JsLabel result) {
						tagSelector.removeFromParent();
						tagSelector = new IronSelector();
						collapse3.add(tagSelector);
						createTag();					
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
		panel.add(dialog);
		dialog.open();
	}
    
	void onClickRemovePriorityButton(ClickEvent event, JsLabel label) {
		incidence.deletePriority(label, new AsyncCallback<JsLabel>() {
			
			@Override public void onSuccess(JsLabel result) {
				prioritySelector.removeFromParent();
				prioritySelector = new IronSelector();
				collapse1.add(prioritySelector);
				createPriority();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	void onClickRemoveTypeButton(ClickEvent event, JsLabel label) {
		incidence.deleteType(label, new AsyncCallback<JsLabel>() {
			
			@Override public void onSuccess(JsLabel result) {
				typeSelector.removeFromParent();
				typeSelector = new IronSelector();
				collapse2.add(typeSelector);
				createType();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	void onClickRemoveTagButton(ClickEvent event, JsLabel label) {
		incidence.deleteLabel(label, new AsyncCallback<JsLabel>() {
			
			@Override public void onSuccess(JsLabel result) {
				tagSelector.removeFromParent();
				tagSelector = new IronSelector();
				collapse3.add(tagSelector);
				createTag();		
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	void onClickEditPriorityButton(ClickEvent event, JsLabel label) {
		PaperInput pi = new PaperInput();
		pi.setLabel("Prioridad");
		AonDialog dialog =  new AonDialog("Editar Prioridad",pi){
			@Override protected void onCancel() {}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				incidence.updatePriority(label, "{\"name\":\""+ pi.getValue() +"\"}", new AsyncCallback<JsLabel>() {
					
					@Override
					public void onSuccess(JsLabel result) {
						prioritySelector.removeFromParent();
						prioritySelector = new IronSelector();
						collapse1.add(prioritySelector);
						createPriority();					
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
		panel.add(dialog);
		dialog.open();	
	}
	
	void onClickEditTypeButton(ClickEvent event, JsLabel label) {
		PaperInput pi = new PaperInput();
		pi.setLabel("Tipo");
		AonDialog dialog =  new AonDialog("Editar Tipo",pi){
			@Override protected void onCancel() {}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				incidence.updateType(label, "{\"name\":\""+ pi.getValue() +"\"}", new AsyncCallback<JsLabel>() {
					
					@Override
					public void onSuccess(JsLabel result) {
						typeSelector.removeFromParent();
						typeSelector = new IronSelector();
						collapse2.add(typeSelector);
						createType();					
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
		panel.add(dialog);
		dialog.open();	
	}
	
	void onClickEditTagButton(ClickEvent event, JsLabel label) {
		PaperInput pi = new PaperInput();
		pi.setLabel("Etiqueta");
		AonDialog dialog =  new AonDialog("Editar Etiqueta",pi){
			@Override protected void onCancel() {}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				incidence.updateLabel(label, "{\"name\":\""+ pi.getValue() +"\"}", new AsyncCallback<JsLabel>() {
					
					@Override
					public void onSuccess(JsLabel result) {
						tagSelector.removeFromParent();
						tagSelector = new IronSelector();
						collapse3.add(tagSelector);
						createTag();					
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		};
		panel.add(dialog);
		dialog.open();	
	}

}
