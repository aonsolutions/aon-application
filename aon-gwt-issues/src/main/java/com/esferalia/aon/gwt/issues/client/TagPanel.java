package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.polymer.iron.widget.IronCollapse;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.iron.widget.IronSelector;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;

public class TagPanel extends Composite {

    interface Binder extends UiBinder<HTMLPanel , TagPanel> {
    	
    }
    
    @UiField HTMLPanel panel;
    
    @UiField Button heading2;
    @UiField IronCollapse collapse2;
    
    @UiField Button heading3;
    @UiField IronCollapse collapse3;
   
    @UiField IronSelector typeSelector;
    @UiField IronSelector tagSelector;
        
    private static Binder binder = GWT.create(Binder.class);
	
	Incidence incidence;
	private Boolean admin;
	
    public TagPanel(Incidence incidence, Boolean admin) {
    	this.incidence = incidence;
    	this.admin = admin;
    	initWidget(binder.createAndBindUi(this));
   			

        createType();
        createTag();
    }
    
    private void loadType(){
        incidence.getTypes(new AsyncCallback<JSON<JsLabel>>() {

			@Override
			public void onSuccess(JSON<JsLabel> result) {
				for(JsLabel label : result.getData().toLinkedList()){
					HorizontalPanel hp = new HorizontalPanel();
					PaperItem item = new PaperItem();
					
					IronIcon ii = new IronIcon();
					ii.setIcon("label");
					ii.getElement().getStyle().setColor("#"+label.getColor());
					
					item.add(ii);
					item.add(new Label(label.getName()));
					item.setStyle("min-height: 30px;");	
					
					PaperIconButton edit = new PaperIconButton();
					edit.setIcon("create");
					edit.setDisabled(admin);
					edit.setStyle("min-height: 30px;padding-top:0px;");
					edit.addClickHandler(new ClickHandler() {
						@Override public void onClick(ClickEvent event) {
							onClickEditTypeButton(event, label);
						}
					});
					edit.setVisible(false);
					
					PaperIconButton del = new PaperIconButton();
					del.setIcon("delete");
					del.setDisabled(admin);
					del.setStyle("min-height: 30px;padding-top:0px;");
					del.addClickHandler(new ClickHandler() {
						@Override public void onClick(ClickEvent event) {
							onClickRemoveTypeButton(event, label);
						}
					});
					del.setVisible(false);
					
					HorizontalPanel hoption = new HorizontalPanel();
					hoption.getElement().getStyle().setPosition(Position.ABSOLUTE);
					hoption.getElement().getStyle().setRight(10, Unit.PX);
					hoption.add(edit);
					hoption.add(del);

					hp.add(item);
					hp.add(hoption);
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
					typeSelector.add(hp);
				}			
				PaperIconButton pib = new PaperIconButton();
				pib.setIcon("add");
				pib.setDisabled(admin);
				pib.setStyle("min-height: 30px;");
				pib.getElement().getStyle().setLeft(290, Unit.PX);
				pib.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						onClickTypeButton(event);
					}
				});
				typeSelector.add(pib);
				if(!collapse2.getOpened())
					collapse2.toggle();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
    }
    
    private void createType() {
    	loadType();
		heading2.addClickHandler(new ClickHandler() {
	   	
	    	   @Override
	    	   public void onClick(ClickEvent event) {
	    		   collapse2.toggle();
	    	   }
	    });
    }
   
    private void loadTag(){
   	 incidence.getLabels(new AsyncCallback<JSON<JsLabel>>() {

			@Override
			public void onSuccess(JSON<JsLabel> result) {
				for(JsLabel label : result.getData().toLinkedList()){
					HorizontalPanel hp = new HorizontalPanel();
					PaperItem item = new PaperItem();
					
					IronIcon ii = new IronIcon();
					ii.setIcon("label");
					ii.getElement().getStyle().setColor("#"+label.getColor());
					item.add(ii);
					
					item.add(new Label(label.getName()));
					item.setStyle("min-height: 30px;");	



					PaperIconButton edit = new PaperIconButton();
					edit.setIcon("create");
					edit.setDisabled(admin);
					edit.setStyle("min-height: 30px;padding-top:0px;");
					edit.addClickHandler(new ClickHandler() {
						@Override public void onClick(ClickEvent event) {
							onClickEditTagButton(event, label);
						}
					});
					edit.setVisible(false);
					
					PaperIconButton del = new PaperIconButton();
					del.setIcon("delete");
					del.setDisabled(admin);
					del.setStyle("min-height: 30px;padding-top:0px;");
					del.addClickHandler(new ClickHandler() {
						@Override public void onClick(ClickEvent event) {
							onClickRemoveTagButton(event, label);
						}
					});
					del.setVisible(false);
					
					HorizontalPanel hoption = new HorizontalPanel();
					hoption.getElement().getStyle().setPosition(Position.ABSOLUTE);
					hoption.getElement().getStyle().setRight(10, Unit.PX);
					hoption.add(edit);
					hoption.add(del);
					
					hp.add(item);
					hp.add(hoption);
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

					tagSelector.add(hp);
				}			
				PaperIconButton pib = new PaperIconButton();
				pib.setIcon("add");
				pib.setDisabled(admin);
				pib.setStyle("min-height: 30px;");
				pib.getElement().getStyle().setLeft(290, Unit.PX);
				pib.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						onClickTagButton(event);
					}
				});
				tagSelector.add(pib);
				if(!collapse3.getOpened())
					collapse3.toggle();
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
    }
    
    private void createTag() {
    	loadTag();
		heading3.addClickHandler(new ClickHandler() {
			   		
	    	   @Override
	    	   public void onClick(ClickEvent event) {
	    		   collapse3.toggle();
	    	   }
	    });
    }
    
	void onClickTypeButton(ClickEvent event){		
		PaperInput pi = new PaperInput();
		pi.setLabel("Tipo");
		AonDialog2 dialog =  new AonDialog2("Nuevo Tipo",pi){
			@Override protected void onCancel() {hide();}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				if(!"".equals(pi.getValue())){
					incidence.createTag("{\"name\":\""+ pi.getValue() +"\","
						+ "\"type\":\""+ TagType.TASK_TYPE.ordinal() +"\"" +"}", new AsyncCallback<JsLabel>() {
					
						@Override
						public void onSuccess(JsLabel result) {
							typeSelector.removeFromParent();
							typeSelector = new IronSelector();
							collapse2.add(typeSelector);
							loadType();
						}
					
						@Override public void onFailure(Throwable caught) {}
					});		
					hide();
				}
			}
		};
		dialog.center();	
	}
    
	
    
	void onClickTagButton(ClickEvent event){	
		PaperInput pi = new PaperInput();
		pi.setLabel("Etiqueta");
		AonDialog2 dialog =  new AonDialog2("Nueva Etiqueta",pi){
			@Override protected void onCancel() {hide();}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				if(!"".equals(pi.getValue())){
					incidence.createTag("{\"name\":\""+ pi.getValue() +"\","
						+ "\"type\":\"" + TagType.TASK_LABEL.ordinal() + "\"" +"}", new AsyncCallback<JsLabel>() {
					
						@Override
						public void onSuccess(JsLabel result) {
							tagSelector.removeFromParent();
							tagSelector = new IronSelector();
							collapse3.add(tagSelector);
							loadTag();					
						}	
						
						@Override public void onFailure(Throwable caught) {}
					});
					hide();
				}
			}
		};
		dialog.center();
	}
	
	void onClickRemoveTypeButton(ClickEvent event, JsLabel label) {
		incidence.deleteTag(label, new AsyncCallback<JsLabel>() {
			
			@Override public void onSuccess(JsLabel result) {
				typeSelector.removeFromParent();
				typeSelector = new IronSelector();
				collapse2.add(typeSelector);
				loadType();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	void onClickRemoveTagButton(ClickEvent event, JsLabel label) {
		incidence.deleteTag(label, new AsyncCallback<JsLabel>() {
			
			@Override public void onSuccess(JsLabel result) {
				tagSelector.removeFromParent();
				tagSelector = new IronSelector();
				collapse3.add(tagSelector);
				loadTag();		
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	void onClickEditTypeButton(ClickEvent event, JsLabel label) {
		PaperInput pi = new PaperInput();
		pi.setLabel("Tipo");
		pi.setValue(label.getName());
		AonDialog2 dialog =  new AonDialog2("Editar Tipo",pi){
			@Override protected void onCancel() {hide();}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				if(!"".equals(pi.getValue())){
					incidence.updateTag(label, "{\"name\":\""+ pi.getValue() +"\"}", new AsyncCallback<JsLabel>() {
					
						@Override
						public void onSuccess(JsLabel result) {
							typeSelector.removeFromParent();
							typeSelector = new IronSelector();
							collapse2.add(typeSelector);
							loadType();					
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
					hide();
				}
			}
		};
		dialog.center();	
	}
	
	void onClickEditTagButton(ClickEvent event, JsLabel label) {
		PaperInput pi = new PaperInput();
		pi.setLabel("Etiqueta");
		pi.setValue(label.getName());
		AonDialog2 dialog =  new AonDialog2("Editar Etiqueta",pi){
			@Override protected void onCancel() {hide();}
			@Override protected void onAccept() {
				PaperInput pi = (PaperInput) content.getWidget(0);
				if(!"".equals(pi.getValue())){
					incidence.updateTag(label, "{\"name\":\""+ pi.getValue() +"\"}", new AsyncCallback<JsLabel>() {
					
						@Override
						public void onSuccess(JsLabel result) {
							tagSelector.removeFromParent();
							tagSelector = new IronSelector();
							collapse3.add(tagSelector);
							loadTag();					
						}
						
						@Override public void onFailure(Throwable caught) {}
					});
					hide();
				}
			}
		};
		dialog.center();	
	}

}
