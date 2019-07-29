package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory;

import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.Deposit;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

public class PageF1 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageF1> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField CheckBox A18009050;

	String codeAux;
	
	public PageF1(Deposit deposit) {
		super(deposit);
		
		A18009050 = new CheckBox();
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		
		initializeTable();
	}

	private void init(){
		keyExe("8009050", A18009050, "check", true);
	}
	
	
	@Override
	protected void initializeTable() {
		init();
	}
	
	
	String key2Aux;
	private void keyExe(String key2, Widget w, String type, Boolean enable) {
		key2Aux = key2;
		if(type.equals("check")) {
			CheckBox c = (CheckBox) w;
			if(getMap().containsKey(key2)){
				c.setValue(getMap().get(key2).equals("1")); 
				c.setEnabled(enable);
			}
			c.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				String key2 = key2Aux;
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					String value = event.getValue()?"1":"0";
					onEdit(key2, value, false);								
					if(key2.equals("8009050") ){
						onEdit("8080809", value, false);
					}
				}
			});
		}
	}
}
