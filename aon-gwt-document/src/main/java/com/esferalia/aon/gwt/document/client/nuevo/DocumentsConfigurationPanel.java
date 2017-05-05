package com.esferalia.aon.gwt.document.client.nuevo;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
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
import com.vaadin.polymer.paper.widget.PaperItem;

public class DocumentsConfigurationPanel extends Composite {

    interface Binder extends UiBinder<HTMLPanel , DocumentsConfigurationPanel> {
    	
    }
    
    @UiField HTMLPanel panel;
    
    @UiField IronSelector menuSelector;
    @UiField PaperItem allFilesPaper;
    @UiField PaperItem systemPaper;
    @UiField PaperItem lotePaper;
    
    @UiField Button categoryButton;
    @UiField IronCollapse categoryCollapse;
    @UiField IronSelector categorySelector;
    
    @UiField Button tagButton;
    @UiField IronCollapse tagCollapse;
    @UiField IronSelector tagSelector;
        
    private static Binder binder = GWT.create(Binder.class);
	
    private Documental parent;
    
    public DocumentsConfigurationPanel(Documental parent) {
    	this.parent = parent;
    	initWidget(binder.createAndBindUi(this));
   		
        createCategory();
        createTag();
    }

    private void createCategory() {
    	parent.getAPI().getAttachment().getCategories(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				load(result.getData(), categorySelector, categoryCollapse, addCategoryClickHandler(),
						editCategoryClickHandler(), deleteCategoryClickHandler());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
    	
    	categoryButton.addClickHandler(new ClickHandler() {
	   	
	    	   @Override
	    	   public void onClick(ClickEvent event) {
	    		   categoryCollapse.toggle();
	    	   }
	    });
    }
    
    private void createTag() {
    	parent.getAPI().getAttachment().getTags(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				load(result.getData(), tagSelector, tagCollapse, addTagClickHandler(),
						editTagClickHandler(), deleteTagClickHandler());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
    	
    	tagButton.addClickHandler(new ClickHandler() {
	   	
	    	   @Override
	    	   public void onClick(ClickEvent event) {
	    		   tagCollapse.toggle();
	    	   }
	    });
    }

    private ClickHandler addCategoryClickHandler(){
    	return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
			}
		};
    }
    
    private ClickHandler editCategoryClickHandler(){
    	return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
			}
		};
    }
    
    private ClickHandler deleteCategoryClickHandler(){
    	return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
			}
		};
    }
    
    private ClickHandler addTagClickHandler(){
    	return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
			}
		};
    }
    
    private ClickHandler editTagClickHandler(){
    	return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
			}
		};
    }
    
    private ClickHandler deleteTagClickHandler(){
    	return new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
			}
		};
    }
    
    private void load(AonJsArray<JsLabel> data, IronSelector selector, IronCollapse collapse,
    		ClickHandler addHandler, ClickHandler editHandler, ClickHandler deleteHandler){
   		for(JsLabel label : data.toLinkedList()){
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
   			//edit.setDisabled(admin);
   			edit.setStyle("min-height: 30px;padding-top:0px;");
   			edit.addClickHandler(editHandler);
   			edit.setVisible(false);
					
   			PaperIconButton del = new PaperIconButton();
   			del.setIcon("delete");
   			//del.setDisabled(admin);
   			del.setStyle("min-height: 30px;padding-top:0px;");
   			del.addClickHandler(deleteHandler);
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
			selector.add(hp);
   		}			
   		PaperIconButton add = new PaperIconButton();
   		add.setIcon("add");
   		//pib.setDisabled(admin);
   		add.setStyle("min-height: 30px;");
   		add.getElement().getStyle().setLeft(290, Unit.PX);
   		add.addClickHandler(addHandler);
   		selector.add(add);
   		if(!collapse.getOpened())
   			collapse.toggle();				
    }
   
}
