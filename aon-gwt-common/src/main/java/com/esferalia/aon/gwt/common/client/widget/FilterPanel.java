package com.esferalia.aon.gwt.common.client.widget;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.esferalia.aon.gwt.common.client.polymer.AonFilterDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperButton;
import com.vaadin.polymer.paper.widget.PaperIconButton;

public abstract class FilterPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, FilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();
	
	@UiField HorizontalPanel filterLabelPanel;
    @UiField HorizontalPanel panel;
    @UiField PaperIconButton cleanFilter;
    
	private HashMap<String, LinkedList<String>> filterMap;
	
    public FilterPanel() {
    	setFilterMap(new HashMap<>());
    	initWidget(binder.createAndBindUi(this));
	}
    
    public FilterPanel(HashMap<String, LinkedList<String>> filterMap) {
    	setFilterMap(filterMap);
    	initWidget(binder.createAndBindUi(this));
	}
    
    protected abstract void refresh();
    
    private void onChange(String key, LinkedList<String> value) {
    	getFilterMap().put(key, value);
    	refresh();
	}
    
    private void onClean(){
    	setFilterMap(new HashMap<>());
    	refresh();
    }
    
    @UiHandler("cleanFilter")
	void cleanFilter(ClickEvent event){		
    	onClean();
	}
    
    public void addFilter(Widget filter){
    	panel.add(filter);
    }
    
    // DATE BOX
    
    public void addDateFilter(String text, String alias){
    	HorizontalPanel datePanel = new HorizontalPanel(); 
    	datePanel.addStyleName(AON.AON_CSS.aonMarginTop());  
    	InlineLabel issueLabel = new InlineLabel(text);
    	issueLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
    	issueLabel.setWidth("20px");
		datePanel.add(issueLabel);
		
		final DateBoxEx dateBox = new DateBoxEx();
		dateBox.getElement().getStyle().setBorderColor("#dedede");
		dateBox.getElement().getStyle().setHeight(16, Unit.PX);;
		dateBox.setWidth("70px");
    	if(getFilterMap().containsKey(alias) && !getFilterMap().get(alias).isEmpty()) {
    		Long time = Long.parseLong(getFilterMap().get(alias).getFirst());
    		dateBox.setValue(new Date(time));
    	}
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(dateBox.getValue().getTime()));
				onChange(alias, list);
			}
		});
		datePanel.add(dateBox);
		
    	addFilter(datePanel);
    }
    
    // TEXT BOX
    
    public void addTextFilter(String text, String alias){
    	HorizontalPanel textPanel = new HorizontalPanel(); 
    	textPanel.addStyleName(AON.AON_CSS.aonMarginTop());  
    	InlineLabel issueLabel = new InlineLabel(text);
    	issueLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		textPanel.add(issueLabel);
		
    	TextBox textBox = new TextBox();
    	textBox.setStyleName(AON.AON_CSS.aonInputText());
		textBox.getElement().getStyle().setBorderColor("#dedede");
		textBox.getElement().getStyle().setHeight(17, Unit.PX);
		textBox.getElement().getStyle().setMargin(0, Unit.PX);

    	if(getFilterMap().containsKey(alias) && !getFilterMap().get(alias).isEmpty()) {
    		textBox.setValue(getFilterMap().get(alias).getFirst());
    	}
    	textBox.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(textBox.getValue());
				onChange(alias, list);
			}
		});
		textPanel.add(textBox);
		addFilter(textPanel);
    }
    
    public void addTextFilter(String alias){
    	TextBox textBox = new TextBox();
    	textBox.addStyleName(ICSS.aonSearchBoxIssues());
    	if(getFilterMap().containsKey(alias) && !getFilterMap().get(alias).isEmpty()) {
    		textBox.setValue(getFilterMap().get(alias).getFirst());
    	}
    	textBox.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(textBox.getValue());
				onChange(alias, list);
			}
		});
		
		addFilter(textBox);
    }
    
    // PAPER BUTTON
    
    public void addPaperButton(String text, JsArray<JavaScriptObject> data, String alias) {
    	PaperButton paperButton = filterButton(text);
		paperButton.addClickHandler(new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				ButtonClick(paperButton, data, text, alias);
			}
		});
		addFilter(paperButton);
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
    
    private void ButtonClick(PaperButton pb, JsArray<JavaScriptObject> data, String label, String alias){
    	LinkedList<String> filterList = getFilterMap().containsKey(alias) ? 
    			getFilterMap().get(alias) : new LinkedList<>();
    	AonFilterDialog sw = new AonFilterDialog(pb, label, "",
    			filterList, data){

			@Override
			protected void onSelect(JavaScriptObject o, Boolean apply) {
				JSONObject js = new JSONObject(o);
				
				if(apply){
					if(getFilterMap().containsKey(alias)){
						getFilterMap().get(alias).add(js.get("id") + "");
					} else {
						LinkedList<String> list = new LinkedList<>();
						list.add(js.get("id") + "");
						getFilterMap().put(alias, list);
					}
				} else {
					if(getFilterMap().containsKey(alias)){
						getFilterMap().get(alias).remove(js.get("id") + "");
					}
				}
				refresh();
			}
    	};
    	sw.show();
    }

    // GETTERS & SETTERS
    
    public HashMap<String, LinkedList<String>> getFilterMap() {
		return filterMap;
	}
    
    public void setFilterMap(HashMap<String, LinkedList<String>> filterMap) {
		this.filterMap = filterMap;
	} 
    
}
