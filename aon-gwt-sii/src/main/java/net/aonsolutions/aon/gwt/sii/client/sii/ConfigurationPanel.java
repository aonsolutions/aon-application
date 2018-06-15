package net.aonsolutions.aon.gwt.sii.client.sii;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.sii.JsSiiConfiguration;
import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
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
import com.vaadin.polymer.iron.widget.IronIcon;
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
    
    public PaperItem buildItem(String text, String icon, Boolean title){
    	PaperItem pi = new PaperItem();
    	pi.setTitle(text);
    	if(icon != null) {
    		IronIcon ironIcon = new IronIcon();
    		ironIcon.setIcon(icon);
    		ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    		pi.add(ironIcon);
    	}
    	pi.add(new Label(text));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;cursor:pointer;" + (title ? "font-weight:bold;" : "")); 
    	return pi;
    }
    
    private ClickHandler menuClickHandler(String option, String title, Boolean checkVisible, PaperItem item, VerticalPanel content) {
    	return new ClickHandler() {
			@Override public void onClick(ClickEvent event) {	
				parent.menuSelection(option, title, checkVisible);
				if(content != null && item != null) {
					content.setVisible(!content.isVisible());
					IronIcon ironIcon = (IronIcon) item.getWidget(0);
					ironIcon.setIcon(content.isVisible() ? "arrow-drop-down" : "arrow-drop-up");
				}
			}	
		};
	}
    
    private Widget menu() {
    	VerticalPanel vp = new VerticalPanel();
    	vp.setWidth("100%");
    	
    	PaperItem pi1 = buildItem("Facturas Emitidas", "arrow-drop-up", true);

    	VerticalPanel vp1 = new VerticalPanel();
    	PaperItem pi11 = buildItem("Generales", null, false);
    	pi11.addClickHandler(menuClickHandler("fe_generales", "Facturas Emitidas - Generales", true, null, null));
    	vp1.add(pi11);
    	PaperItem pi12 = buildItem("Simplificadas", null, false);
    	pi12.addClickHandler(menuClickHandler("fe_simplificadas", "Facturas Emitidas - Simplificadas", true, null, null));
    	vp1.add(pi12);
    	PaperItem pi13 = buildItem("Rectificativas", null, false);
    	pi13.addClickHandler(menuClickHandler("fe_rectificativas", "Facturas Emitidas - Rectificativas", true, null, null));
    	vp1.add(pi13);
    	PaperItem pi14 = buildItem("Intracomunitarias", null, false);
    	pi14.addClickHandler(menuClickHandler("fe_intracomunitarias", "Facturas Emitidas - Intracomunitarias", true, null, null));
    	vp1.add(pi14);
    	
    	vp1.setVisible(false);
    	vp1.setWidth("100%");
    	vp1.getElement().getStyle().setMarginLeft(25, Unit.PX);
    	pi1.addClickHandler(menuClickHandler("fe_emitidas", "Facturas Emitidas", true, pi1, vp1));
    	vp.add(pi1);
    	vp.add(vp1);
   
    	PaperItem pi2 = buildItem("Facturas Recibidas", "arrow-drop-up", true);

    	VerticalPanel vp2 = new VerticalPanel();
    	PaperItem pi21 = buildItem("Compras", null, false);
    	pi21.addClickHandler(menuClickHandler("fr_compras", "Facturas Recibidas - Compras", true, null, null));
    	vp2.add(pi21);
    	PaperItem pi22 = buildItem("Gastos", null, false);
    	pi22.addClickHandler(menuClickHandler("fr_gastos", "Facturas Recibidas - Gastos", true, null, null));
    	vp2.add(pi22);
    	PaperItem pi23 = buildItem("Rectificativas", null, false);
    	pi23.addClickHandler(menuClickHandler("fr_rectificativas", "Facturas Recibidas - Rectificativas", true, null, null));
    	vp2.add(pi23);
    	PaperItem pi24 = buildItem("Intracomunitarias", null, false);
    	pi24.addClickHandler(menuClickHandler("fr_intracomunitarias", "Facturas Recibidas - Intracomunitarias", true, null, null));
    	vp2.add(pi24);
    	
    	vp2.setVisible(false);
    	vp2.setWidth("100%");
    	vp2.getElement().getStyle().setMarginLeft(25, Unit.PX);
    	pi2.addClickHandler(menuClickHandler("fr_recibidas", "Facturas Recibidas", true, pi2, vp2));
    	vp.add(pi2);
    	vp.add(vp2);
    	
    	PaperItem pi3 = buildItem("Bienes de Inversi\u00f3n", null, true);
    	pi3.addClickHandler(menuClickHandler("bienes", "Bienes de Inversion", true, null, null));
    	pi3.getElement().getStyle().setMarginLeft(25, Unit.PX);
    	vp.add(pi3);
    	
    	PaperItem pi4 = buildItem("Operaciones Intracomunitarias", null, true);
    	pi4.addClickHandler(menuClickHandler("intracomunitarias", "Operaciones Intracomunitarias", true, null, null));
    	pi4.getElement().getStyle().setMarginLeft(25, Unit.PX);
    	vp.add(pi4);
    	
    	PaperItem pi5 = buildItem("Operaciones Cobros/Pagos", "arrow-drop-up", true);

    	VerticalPanel vp5 = new VerticalPanel();
    	PaperItem pi51 = buildItem("Cobros", null, false);
    	pi51.addClickHandler(menuClickHandler("cp_cobros", "Operaciones Cobros", false, null, null));
    	vp5.add(pi51);
    	PaperItem pi52 = buildItem("Pagos", null, false);
    	pi52.addClickHandler(menuClickHandler("cp_pagos", "Operaciones Pagos", false, null, null));
    	vp5.add(pi52);
    	
    	vp5.setVisible(false);
    	vp5.setWidth("100%");
    	vp5.getElement().getStyle().setMarginLeft(25, Unit.PX);
    	pi5.addClickHandler(menuClickHandler("cp_cobros_pagos", "Operaciones Cobros/Pagos", false, pi5, vp5));
    	vp.add(pi5);
    	vp.add(vp5);
  
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
