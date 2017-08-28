package net.aonsolutions.aon.gwt.udapa.client.quality;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.esferalia.aon.gwt.common.client.polymer.AonFilterDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperIconButton;

public class FilterPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, FilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();

    @UiField HorizontalPanel panel;
    @UiField InlineLabel categoryLabel;
    @UiField InlineLabel customerLabel;
    @UiField InlineLabel sellerLabel;
    @UiField InlineLabel workplaceLabel;
    @UiField InlineLabel periodLabel;
    @UiField PaperIconButton cleanFilter;
    
    private QualityPrincipal parent;
    
    private void onChange(String key, LinkedList<String> value) {
    	parent.getFilterMap().put(key, value);
		parent.gridContent();
	}
    
    private void onClean(){
    	parent.initializeFilterMap();
    	parent.gridContent();
    }
    
    public FilterPanel(QualityPrincipal parent) {
    	this.parent = parent;
    	initWidget(binder.createAndBindUi(this));       
    	
		panel.add(datePanel());
		
		
		// ------------------ FILTER BUTTONS
		FlowPanel fpanel = new FlowPanel(); 
		
		// ------------------ SUPPLIERS
		PaperButton supplierButton = filterButton("Proveedor");
		supplierButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				parent.getAPI().getRegistry().getSuppliers(new AsyncCallback<JSON<JsObject>>() {
					
					@Override
					public void onSuccess(JSON<JsObject> result) {
						ButtonClick(supplierButton, result, "Proveedor");
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
			}
		});
		fpanel.add(supplierButton);
		
		panel.add(fpanel);
    }
    
    private HorizontalPanel datePanel() {
    	HorizontalPanel datePanel = new HorizontalPanel(); 
    	datePanel.addStyleName(AON.AON_CSS.aonMarginTop());  
    	InlineLabel fromLabel = new InlineLabel( AON.MSG.from());
    	fromLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
    	fromLabel.setWidth("20px");
		datePanel.add(fromLabel);
		
		
		final DateBoxEx from = new DateBoxEx();
		from.getElement().getStyle().setBorderColor("#dedede");
		from.getElement().getStyle().setHeight(16, Unit.PX);;
		from.setWidth("70px");
		from.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(from.getValue().getTime()));
				onChange("from", list);
			}
		});
		datePanel.add(from);
		
		InlineLabel toLabel = new InlineLabel(AON.MSG.to());
		toLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		toLabel.setWidth("20px");
		datePanel.add(toLabel);

		final DateBoxEx to = new DateBoxEx();
		to.setWidth("70px");
		to.getElement().getStyle().setBorderColor("#dedede");
		to.getElement().getStyle().setHeight(16, Unit.PX);;

		to.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(to.getValue().getTime()));
				onChange("to", list);	
			}
		});
		datePanel.add(to);
		
		datePanel.add(new Label("N\u00BA Pedido"));
		TextBox np = new TextBox();
		np.setStyleName(AON.AON_CSS.aonInputText());
		np.getElement().getStyle().setBorderColor("#dedede");
		np.getElement().getStyle().setHeight(17, Unit.PX);
		np.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(np.getValue());
				onChange("code", list);				
			}
		});
		
		datePanel.add(np);
		
		return datePanel;
	}
    
    private PaperButton filterButton(String title) {
		PaperButton button = new PaperButton();
		button.setNoink(true);
		button.setStyleName(ICSS.aonPaperButtonFilterIssues());
				
		InlineLabel label = new InlineLabel(title);
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		button.add(label);
				
		IronIcon icon = new IronIcon();
		icon.setStyleName(ICSS.aonIronIconFilterIssues()); 
		icon.setIcon("arrow-drop-down");
		button.add(icon);
		return button;
	}
    
    @UiHandler("cleanFilter")
	void cleanFilter(ClickEvent event){		
    	categoryLabel.setText("");
    	customerLabel.setText("");
    	sellerLabel.setText("");
    	workplaceLabel.setText("");
    	periodLabel.setText("");
    	onClean();
	}
    

    private String key;
    private void ButtonClick(PaperButton pb, JSON<JsObject> result, String label){
    	if("Proveedor".equals(label)){
    		key = "supplier"; 
    	} 
    	LinkedList<String> filterList = parent.getFilterMap().containsKey(key) ? 
    			parent.getFilterMap().get(key) : new LinkedList<>();
    	AonFilterDialog sw = new AonFilterDialog(pb, label, "",
    			filterList, result.getData().cast()){

			@Override
			protected void onSelect(JavaScriptObject o, Boolean apply) {
				JsObject js = o.cast();
				if(apply){
					if(parent.getFilterMap().containsKey(key)){
						parent.getFilterMap().get(key).add(js.getId()+"");
					} else {
						LinkedList<String> list = new LinkedList<>();
						list.add(js.getId()+"");
						parent.getFilterMap().put(key, list);
					}
				} else {
					if(parent.getFilterMap().containsKey(key)){
						parent.getFilterMap().get(key).remove(js.getId()+"");
					}
				}
				parent.gridContent();
			}
    	};
    	sw.show();
    }
}
