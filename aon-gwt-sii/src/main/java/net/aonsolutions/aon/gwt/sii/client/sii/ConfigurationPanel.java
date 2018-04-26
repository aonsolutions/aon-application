package net.aonsolutions.aon.gwt.sii.client.sii;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.sii.JsSiiConfiguration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.event.IronSelectEvent;
import com.vaadin.polymer.iron.widget.event.IronSelectEventHandler;
import com.vaadin.polymer.paper.widget.PaperItem;
import com.vaadin.polymer.paper.widget.PaperTabs;

import net.aonsolutions.polymer.aon.widget.AonComboBox;

public class ConfigurationPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, ConfigurationPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);
    
    @UiField HTMLPanel panel;
    @UiField HTMLPanel tabContent;
    @UiField PaperTabs tabs;

    SiiMain parent; 
    
    private static final String ZERO = "0";
    private static final String ONE = "1";
    
    public ConfigurationPanel(SiiMain parent) {
    	this.parent = parent;
    	initWidget(binder.createAndBindUi(this));
    	tabs.setSelected("0");
        tabs.addIronSelectHandler(new IronSelectEventHandler() {
			
			@Override
			public void onIronSelect(IronSelectEvent event) {
				for(Integer i = 0; i < tabContent.getWidgetCount(); i++)
					tabContent.remove(i);
				ScrollPanel sp = new ScrollPanel();
				Integer h = Window.getClientHeight() -190;
				sp.getElement().getStyle().setHeight(h, Unit.PX);
		    	Window.addResizeHandler(new ResizeHandler() {
					
					@Override
					public void onResize(ResizeEvent event) {
						Integer h = Window.getClientHeight() -190;
						sp.getElement().getStyle().setHeight(h, Unit.PX);
					}
				});
				if(tabs.getSelected().toString().equals(ZERO)
					|| tabs.getSelected().toString() == ZERO){
					sp.add(menu());
				} else if(tabs.getSelected().toString().equals(ONE)
					|| tabs.getSelected().toString() == ONE){
					parent.getAPI().getSii().getSiiConfiguration(new AsyncCallback<JSON<JsSiiConfiguration>>() {
						
						@Override
						public void onSuccess(JSON<JsSiiConfiguration> result) {
							sp.add(configuration(result.getData().get(0)));
						}
						
						@Override
						public void onFailure(Throwable caught) {}
					});
				}
				tabContent.add(sp);
			}
		});       
    }
    
    private Widget menu() {
    	VerticalPanel vp = new VerticalPanel();
    	vp.setWidth("100%");
    	
        PaperItem pi1 = new PaperItem();
        pi1.add(new Label("Facturas Emitidas"));
        pi1.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi1.getElement().getStyle().setPaddingLeft(24, Unit.PX);
        pi1.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
        PaperItem pi11 = new PaperItem();
        pi11.add(new Label("Generales"));
        pi11.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi11.getElement().getStyle().setPaddingLeft(48, Unit.PX);
        
        PaperItem pi12 = new PaperItem();
        pi12.add(new Label("Simplificadas"));
        pi12.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi12.getElement().getStyle().setPaddingLeft(48, Unit.PX);
        
        PaperItem pi13 = new PaperItem();
        pi13.add(new Label("Rectificativas"));
        pi13.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi13.getElement().getStyle().setPaddingLeft(48, Unit.PX);
        
        PaperItem pi14 = new PaperItem();
        pi14.add(new Label("Intracomunitarias"));
        pi14.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi14.getElement().getStyle().setPaddingLeft(48, Unit.PX);
       
        PaperItem pi2 = new PaperItem();
        pi2.add(new Label("Facturas Recibidas"));
        pi2.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi2.getElement().getStyle().setPaddingLeft(24, Unit.PX);
        
		PaperItem pi21 = new PaperItem();
	    pi21.add(new Label("Compras"));
	    pi21.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
	    pi21.getElement().getStyle().setPaddingLeft(48, Unit.PX);
	      
	    PaperItem pi22 = new PaperItem();
	    pi22.add(new Label("Gastos"));
	    pi22.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
	    pi22.getElement().getStyle().setPaddingLeft(48, Unit.PX);
	    
        PaperItem pi23 = new PaperItem();
        pi23.add(new Label("Rectificativas"));
        pi23.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi23.getElement().getStyle().setPaddingLeft(48, Unit.PX);
        
        PaperItem pi24 = new PaperItem();
        pi24.add(new Label("Intracomunitarias"));
        pi24.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi24.getElement().getStyle().setPaddingLeft(48, Unit.PX);
	    
        PaperItem pi3 = new PaperItem();
        pi3.add(new Label("Bienes de Inversi\u00f3n"));
        pi3.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi3.getElement().getStyle().setPaddingLeft(24, Unit.PX);
        
        PaperItem pi4 = new PaperItem();
        pi4.add(new Label("Operaciones Intracomunitarias"));
        pi4.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi4.getElement().getStyle().setPaddingLeft(24, Unit.PX);
        
        PaperItem pi5 = new PaperItem();
        pi5.add(new Label("Operaciones Cobros/Pagos"));
        pi5.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi5.getElement().getStyle().setPaddingLeft(24, Unit.PX);
        
        PaperItem pi51 = new PaperItem();
        pi51.add(new Label("Cobros"));
        pi51.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi51.getElement().getStyle().setPaddingLeft(48, Unit.PX);
        
        PaperItem pi52 = new PaperItem();
        pi52.add(new Label("Pagos"));
        pi52.setStyle("min-height:24px;font-size:16px;padding:0px;cursor: pointer;");
        pi52.getElement().getStyle().setPaddingLeft(48, Unit.PX);
        
        pi1.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("fe_emitidas", "Facturas Emitidas", true);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi11.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("fe_generales", "Facturas Emitidas - Generales", true);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi12.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("fe_simplificadas", "Facturas Emitidas - Simplificadas", true);

				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi13.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("fe_rectificativas", "Facturas Emitidas - Rectificativas", true);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi14.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("fe_intracomunitarias", "Facturas Emitidas - Intracomunitarias", true);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi2.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("fr_recibidas", "Facturas Recibidas", true);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi21.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("fr_compras", "Facturas Recibidas - Compras", true);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi22.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("fr_gastos", "Facturas Recibidas - Gastos", true);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi23.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("fr_rectificativas", "Facturas Recibidas - Rectificativas", true);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi24.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("fr_intracomunitarias", "Facturas Recibidas - Intracomunitarias", true);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi3.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("bienes", "Bienes de Inversion", true);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi4.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("intracomunitarias", "Operaciones Intracomunitarias", true);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi5.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("cp_cobros_pagos", "Operaciones Cobros/Pagos", false);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi51.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("cp_cobros", "Operaciones Cobros", false);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				pi52.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
			}
		});
        
        pi52.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection("cp_pagos", "Operaciones Pagos", false);
				
				pi1.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi11.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi12.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi13.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi14.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi2.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi21.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi22.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi23.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi24.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi3.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi4.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi5.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi51.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
				pi52.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			}
		});
        
        vp.add(pi1);
        vp.add(pi11);
        vp.add(pi12);
        vp.add(pi13);
        vp.add(pi14);
        vp.add(pi2);
        vp.add(pi21);
        vp.add(pi22);
        vp.add(pi23);
        vp.add(pi24);
        vp.add(pi3);
        vp.add(pi4);
        vp.add(pi5);
        vp.add(pi51);
        vp.add(pi52);
        
        return vp;
	}
    
    private Widget configuration(JsSiiConfiguration configuration) {
     	VerticalPanel vp = new VerticalPanel();
    	
    	AonComboBox acb = new AonComboBox();
       	acb.setItemLabelPath("name");
    	acb.setItemValuePath("name");
    	acb.setItems(configuration.getOperationDateOption());
    	acb.setInputElementValue(configuration.getOperationDate());
    	acb.setStyle("padding-left:20px;padding-right:20px;padding-bottom: 20px; width:250px;");
    	acb.setLabel("Fecha Registro");
    	acb.addChangeHandler(new net.aonsolutions.polymer.aon.widget.event.ChangeEventHandler() {
			
			@Override
			public void onChange(net.aonsolutions.polymer.aon.widget.event.ChangeEvent event) {
				if(!acb.getInputElementValue().equals("")){
					String requestData= "{\"operation_date\":\""+ acb.getInputElementValue() +"\"}";
					// TODO REQUEST DATA
					parent.getAPI().getSii().setSiiConfiguration(requestData);
				}
			}
		});
    	
    	vp.add(acb);
    	return vp;
	}
}
