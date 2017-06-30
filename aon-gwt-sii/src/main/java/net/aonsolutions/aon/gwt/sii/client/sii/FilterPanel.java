package net.aonsolutions.aon.gwt.sii.client.sii;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;

import net.aonsolutions.aon.gwt.sii.client.ISii;
import net.aonsolutions.aon.gwt.sii.client.ISiiAsync;

public class FilterPanel extends Composite {
	
	final ISiiAsync impl = GWT.create(ISii.class);
	
    interface Binder extends UiBinder<HTMLPanel, FilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();

    @UiField HorizontalPanel panel;
    @UiField InlineLabel label;
    
    private SiiPrincipal parent;
    
    private void onChange(String key, LinkedList<String> value) {
    	parent.getFilterMap().put(key, value);
		parent.gridContent();
    }
    
    public FilterPanel(SiiPrincipal parent) {
    	this.parent = parent;
    	initWidget(binder.createAndBindUi(this));       
    	label.setText("Facturas Emitidas");
		panel.add(datePanel());

    }
    
    public void setTitle(String title){
    	label.setText(title);
    }
    
    private HorizontalPanel datePanel() {
    	HorizontalPanel datePanel = new HorizontalPanel(); 
    	datePanel.addStyleName(AON.AON_CSS.aonMarginTop());  
    	InlineLabel fromLabel = new InlineLabel( AON.MSG.from());
    	fromLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
    	fromLabel.setWidth("20px");
		datePanel.add(fromLabel);
		
		
		final DateBoxEx from = new DateBoxEx();
		if(parent.getFilterMap().containsKey("from")){
			Long lon = Long.parseLong(parent.getFilterMap().get("from").get(0));
			Date date = new Date(lon);
			from.setValue(date);
		}
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
		
		CheckBox cb = new CheckBox("Pendientes");
		if(parent.getFilterMap().containsKey("pending")){
			cb.setValue(parent.getFilterMap().get("pending").get(0).equalsIgnoreCase("true"));
		}
		cb.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(cb.getValue().toString());
				onChange("pending", list);
			}
		});
		datePanel.add(cb);
		
		CheckBox cb2 = new CheckBox("Aceptadas");
		if(parent.getFilterMap().containsKey("sent")){
			cb2.setValue(parent.getFilterMap().get("sent").get(0).equalsIgnoreCase("true"));
		}
		cb2.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(cb2.getValue().toString());
				onChange("sent", list);				
			}
		});
		datePanel.add(cb2);
		
		CheckBox cb3 = new CheckBox("Aceptadas con Errores");
		if(parent.getFilterMap().containsKey("sent")){
			cb3.setValue(parent.getFilterMap().get("sent").get(0).equalsIgnoreCase("true"));
		}
		cb3.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(cb3.getValue().toString());
				onChange("sent_error", list);				
			}
		});
		datePanel.add(cb3);
		
		CheckBox cb4 = new CheckBox("Incorrectas");
		if(parent.getFilterMap().containsKey("sent")){
			cb4.setValue(parent.getFilterMap().get("sent").get(0).equalsIgnoreCase("true"));
		}
		cb4.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(cb4.getValue().toString());
				onChange("error", list);				
			}
		});
		datePanel.add(cb4);
		
		CheckBox cb5 = new CheckBox("Anuladas");
		if(parent.getFilterMap().containsKey("sent")){
			cb5.setValue(parent.getFilterMap().get("sent").get(0).equalsIgnoreCase("true"));
		}
		cb5.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(cb5.getValue().toString());
				onChange("anulada", list);				
			}
		});
		datePanel.add(cb5);
		return datePanel;
	}
}
