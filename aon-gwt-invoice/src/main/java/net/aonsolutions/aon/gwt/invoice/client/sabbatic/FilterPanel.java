package net.aonsolutions.aon.gwt.invoice.client.sabbatic;

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

public class FilterPanel extends Composite {
		
    interface Binder extends UiBinder<HTMLPanel, FilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();

    @UiField HorizontalPanel panel;
    @UiField InlineLabel label;
    
    private SabbaticPrincipal parent;
    
    private void onChange(String key, LinkedList<String> value) {
    	parent.getFilterMap().put(key, value);
		parent.gridContent();
    }
    
    public FilterPanel(SabbaticPrincipal parent) {
    	this.parent = parent;
    	initWidget(binder.createAndBindUi(this));       
    	label.setText("Facturas Emitidas");
		panel.add(panel());

    }
    
    public void setTitle(String title){
    	label.setText(title);
    }
    
    public void setCheckVisible(Boolean bool){
    	HorizontalPanel hp =(HorizontalPanel) panel.getWidget(0);
    	for(Integer i = 2 ; i < hp.getWidgetCount() ; i++){
    		CheckBox cb = (CheckBox) hp.getWidget(i);
    		if(i == 2 || i == 5) cb.setVisible(true);
    		else if(i == 7 || i == 8) cb.setVisible(!bool);
    		else cb.setVisible(bool);
    	}
    }
    
    private HorizontalPanel panel() {
    	HorizontalPanel panel = new HorizontalPanel(); 
    	panel.addStyleName(AON.AON_CSS.aonMarginTop());  
    	InlineLabel fromLabel = new InlineLabel( AON.MSG.from());
    	fromLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
    	fromLabel.setWidth("20px");
    	fromLabel.getElement().getStyle().setPaddingTop(4, Unit.PX);
		panel.add(fromLabel);
		
		
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
		panel.add(from);
		
		
		// ---------- PENDIENTES / PENDING
		
		CheckBox cbPending = new CheckBox("Pendientes");
		if(parent.getFilterMap().containsKey("pending")){
			cbPending.setValue(parent.getFilterMap().get("pending").get(0).equalsIgnoreCase("true"));
		}
		cbPending.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(cbPending.getValue().toString());
				onChange("pending", list);
			}
		});
		panel.add(cbPending);
		
		// ---------- ENVIADAS / SENT
		
		CheckBox cb2 = new CheckBox("Enviadas");
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
		panel.add(cb2);

		// ---------- ACEPTADAS / ACCEPTED
		
		CheckBox cb3 = new CheckBox("Aceptadas");
		if(parent.getFilterMap().containsKey("accepted")){
			cb3.setValue(parent.getFilterMap().get("accepted").get(0).equalsIgnoreCase("true"));
		}
		cb3.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(cb3.getValue().toString());
				onChange("accepted", list);				
			}
		});
		panel.add(cb3);

		// ---------- INCORRECTAS / WRONG
		
		CheckBox cb4 = new CheckBox("Incorrectas");
		if(parent.getFilterMap().containsKey("wrong")){
			cb4.setValue(parent.getFilterMap().get("wrong").get(0).equalsIgnoreCase("true"));
		}
		cb4.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(cb4.getValue().toString());
				onChange("wrong", list);				
			}
		});
		panel.add(cb4);
		
		// ---------- BORRADAS / REMOVED - only to admin user.
		
		CheckBox cb5 = new CheckBox("Borradas");
		if(parent.getFilterMap().containsKey("removed")){
			cb5.setValue(parent.getFilterMap().get("removed").get(0).equalsIgnoreCase("true"));
		}
		cb5.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(cb5.getValue().toString());
				onChange("removed", list);				
			}
		});
		panel.add(cb5);
		
		return panel;
	}
}
