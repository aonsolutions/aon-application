package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import java.text.ParseException;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.deposit.client.D2DepositTreeObject;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class FreeText extends PageAbs {

	final INormalizedMemoryAsync inma = GWT.create(INormalizedMemory.class);
	
	@UiField
	InlineLabel  title1;

	String part;

	Boolean textMode;
	
	
	interface FreeTextBinder extends UiBinder<Widget, FreeText> {
	}

	private static final FreeTextBinder binder = GWT
			.create(FreeTextBinder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public FreeText(String pageHeader, boolean isFreeText, String part, Enterprise enterprise, NormalizedMemory nm, Boolean textMode) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		this.enterprise = enterprise;
		this.part = part;
		this.normalizedMemory = nm;
		this.textMode = textMode;


		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
		
		title1.setText(pageHeader);
	}
	
	public FreeText(String pageHeader, boolean isFreeText, String part, Enterprise enterprise, NormalizedMemory nm, Boolean textMode, String rattachId) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		enterprise.setDocument(rattachId);
		this.enterprise = enterprise;
		this.part = part;
		this.normalizedMemory = nm;
		this.textMode = textMode;

		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
		
		title1.setText(pageHeader);
	}
	
	

	@Override
	protected void initializeTable() {
		// TODO Auto-generated method stub
		table.setWidth("100%");
		table.setCellSpacing(0);
		D2DepositKey key = null;
		switch (part) {
		case "MAT1":key = D2DepositKey.MAT19019001;break;
		case "MAT2":key = D2DepositKey.MAT29029001;break;
		case "MAT3":key = D2DepositKey.MAT39039001;break;
		case "MAT4":key = D2DepositKey.MAT49049001;break;
		case "MAT5":key = D2DepositKey.MAT59059001;break;
		case "MAT6":key = D2DepositKey.MAT69069001;break;
		case "MAT7":key = D2DepositKey.MAT79079001;break;
		case "MAT8":key = D2DepositKey.MAT89089001;break;
		case "MAT9":key = D2DepositKey.MAT99099001;break;
		case "MAT11":key = D2DepositKey.MAT119119001;break;
		case "MAT12":key = D2DepositKey.MAT129129001;break;
		case "MAT13":key = D2DepositKey.MAT139139001;break;
		case "MAT14":key = D2DepositKey.MAT149149001;break;
		default:
			break;
		}		
		paintKeyField(table, key, 0, 0);
	}
	
	String codeAux;
	@Override
	protected void paintKeyField(FlexTable tab,final D2DepositKey key,int row, int col) {
		boolean disabled = isDisabled(key);
		
		FlowPanel panel = new FlowPanel();
		final TextArea text = new TextArea();
		text.setWidth("100%");
		text.setVisibleLines(20);
		text.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue("",false);
					}
					String s = text.getValueOrThrow();
					text.addStyleName(AON.AON_CSS.aonChanged());
					
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					if(!textMode) 
						onEdit(key.getCode(), s);
					else
						inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),key.getCode(), s, year, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {}
							@Override
							public void onSuccess(Void result) {}
						});
				} catch (ParseException e) {
					// nothing.
				}
				
			}
		});
		if(textMode){
			if(map.containsKey(key.getCode())){
				text.setValue(map.get(key.getCode()));
			}
			else text.setValue("");
		}
		else {
			if(mapDraft.containsKey(key.getCode())){
				text.setValue(mapDraft.get(key.getCode()));
			}
			else text.setValue("");
			if(isChanged(key.getCode())){
				text.addStyleName(AON.AON_CSS.aonChanged());
			}
		}
		
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		
		text.setEnabled(!disabled);
		panel.add(text);
		if (!isTitle(key)) {
			panel.addStyleName(AON.AON_CSS.aonFiscalPaddingRight());
		}
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
	
	@Override
	public void dump(D2DepositTreeObject d2DepositObject) {
		// TODO Auto-generated method stub
		
		if(textMode){

			inma.getSchema(enterprise.getDocument(),enterprise.getDomain(),textMode, d2DepositObject.getYear(), new AsyncCallback<Map<String, String>>() {
				
				@Override
				public void onSuccess(Map<String, String> result) {
					map = result;
					initializeTable();
				}
				@Override
				public void onFailure(Throwable caught) {
					
				}
			});
		}else super.dump(d2DepositObject);
	}
	
	
	
}
