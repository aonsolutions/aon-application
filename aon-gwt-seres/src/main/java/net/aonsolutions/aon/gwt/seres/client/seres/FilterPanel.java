package net.aonsolutions.aon.gwt.seres.client.seres;

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

import net.aonsolutions.aon.gwt.seres.client.ISeres;
import net.aonsolutions.aon.gwt.seres.client.ISeresAsync;

public class FilterPanel extends Composite {
	
	final ISeresAsync impl = GWT.create(ISeres.class);
	
    interface Binder extends UiBinder<HTMLPanel, FilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();

    @UiField HorizontalPanel panel;
    @UiField InlineLabel label;
    
    private SeresPrincipal parent;
    
    private void onChange(String key, LinkedList<String> value) {
    	parent.getFilterMap().put(key, value);
		parent.gridContent();
    }
    
    public FilterPanel(SeresPrincipal parent) {
    	this.parent = parent;
    	initWidget(binder.createAndBindUi(this));       
    	label.setText("Resumen");
		panel.add(datePanel());
		setCheckVisible(false);

    }
    
    public void setTitle(String title){
    	label.setText(title);
    }
    
    public void setCheckVisible(Boolean bool){
    	HorizontalPanel hp =(HorizontalPanel) panel.getWidget(0);
    	for(Integer i = 2 ; i < hp.getWidgetCount() ; i++){
    		CheckBox cb = (CheckBox) hp.getWidget(i);
    		cb.setVisible(bool);
    	}
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
		
		
	    CheckBox pendingCheck = new CheckBox("Pendiente");
	    CheckBox processedCheck = new CheckBox("Procesado");
		CheckBox errorCheck = new CheckBox("Incorrecto");
		
		fillcheckbox(pendingCheck, "pending");
		fillcheckbox(processedCheck, "processed");
		fillcheckbox(errorCheck, "error");
		
		datePanel.add(pendingCheck);
		datePanel.add(processedCheck);
		datePanel.add(errorCheck);
		
		return datePanel;
	}
    
    private void fillcheckbox(CheckBox cb, String key) {
    	if(parent.getFilterMap().containsKey(key)){
			cb.setValue(parent.getFilterMap().get(key).get(0).equalsIgnoreCase("true"));
		}
		cb.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(cb.getValue().toString());
				onChange(key, list);
			}
		});
    }
    
}
