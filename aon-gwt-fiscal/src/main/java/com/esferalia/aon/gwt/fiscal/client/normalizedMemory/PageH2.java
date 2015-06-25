package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.text.ParseException;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PageH2 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageH2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;
	
	@UiField(provided = true)
	FlexTable table2;
	
	@UiField TabPanel tabPanel;

	public PageH2() {
		super();
		table1 = new FlexTable();
		table2 = new FlexTable();
		tabPanel = new TabPanel();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
	}
	
	public PageH2(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		table1 = new FlexTable();
		table2 = new FlexTable();
		tabPanel = new TabPanel();

		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		normalizedMemory.importSocietyButton.setVisible(true);
		tabPanel.selectTab(0);
	}

	@Override
	protected void initializeTable() {
		table();
		table1();
		table2();

	}
	
	private void table(){
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		table.getColumnFormatter().setWidth(2, "200px");
		table.getColumnFormatter().setWidth(3, "200px");
		int row = 0;
		table.setWidget(row, 0, new Label("Activo"));
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 1, new Label("Notas de la memoria"));
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label("Ejercicio 2014"));
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 3, new Label("Ejercicio 2013"));
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());

		++row;
		for(Integer i = 0; i< D2DepositConstants.BA_ABREVIATE_KEYS_1.length; i+=3){
			D2DepositHeaderKey[] d2 = new D2DepositHeaderKey[]{
					D2DepositConstants.BA_ABREVIATE_KEYS_1[i],
					D2DepositConstants.BA_ABREVIATE_KEYS_1[i+1],
					D2DepositConstants.BA_ABREVIATE_KEYS_1[i+2],
			};
			row = paintKey(table, d2 , row);
		}
	}
	
	private void table1(){
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
		table1.getColumnFormatter().setWidth(2, "200px");
		table1.getColumnFormatter().setWidth(3, "200px");
		int row = 0;
		table1.setWidget(row, 0, new Label("Patrimonio neto y pasivo"));
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 1, new Label("Notas de la memoria"));
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 2, new Label("Ejercicio 2014"));
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 3, new Label("Ejercicio 2013"));
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());

		++row;
		for(Integer i = 0; i< (D2DepositConstants.BA_ABREVIATE_KEYS_2.length/3); i++){
			D2DepositHeaderKey[] d2 = new D2DepositHeaderKey[]{
					D2DepositConstants.BA_ABREVIATE_KEYS_2[((D2DepositConstants.BA_ABREVIATE_KEYS_2.length/3)*2)+i],
					D2DepositConstants.BA_ABREVIATE_KEYS_2[i],
					D2DepositConstants.BA_ABREVIATE_KEYS_2[(D2DepositConstants.BA_ABREVIATE_KEYS_2.length/3)+i],
			};
			row = paintKey(table1, d2 , row);
		}
	}
	
	private void table2(){
		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(1, "200px");
		table2.getColumnFormatter().setWidth(2, "200px");
		table2.getColumnFormatter().setWidth(3, "200px");
		int row = 0;
		table2.setWidget(row, 0, new Label("Patrimonio neto y pasivo"));
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 1, new Label("Notas de la memoria"));
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 2, new Label("Ejercicio 2014"));
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 3, new Label("Ejercicio 2013"));
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());

		++row;
		for(Integer i = 0; i< (D2DepositConstants.BA_ABREVIATE_KEYS_3.length/3); i++){
			D2DepositHeaderKey[] d2 = new D2DepositHeaderKey[]{
					D2DepositConstants.BA_ABREVIATE_KEYS_3[((D2DepositConstants.BA_ABREVIATE_KEYS_3.length/3)*2)+i],
					D2DepositConstants.BA_ABREVIATE_KEYS_3[i],
					D2DepositConstants.BA_ABREVIATE_KEYS_3[(D2DepositConstants.BA_ABREVIATE_KEYS_3.length/3)+i],
			};
			row = paintKey(table2, d2 , row);
		}
	}
	
	
	protected int paintKey(FlexTable tab, D2DepositHeaderKey[] keys,  int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		paintKeyFieldTextBox(tab,keys[0], row, 1);
		for (Integer i = 1; i < keys.length ; i++) {
			paintKeyField(tab,keys[i],row,i+1);
		}
		return  ++row;
	}

	
	protected void paintKeyFieldTextBox(FlexTable tab,final D2DepositHeaderKey key,int row, int col) {
		boolean disabled = isDisabled(key);
		
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		boolean show = true;
		try {
			show = Integer.parseInt(codeId) > 0;
		} catch (NumberFormatException e) {
			// Nothing;
		}
		if (show) {
			BoxLabel code = new BoxLabel(codeId);
			//getLabels().put(key, code);
			panel.add(code);
		}

		final TextBox text = new TextBox();
		text.setStyleName("aon-inputText");
		codeAux = codeId;
		text.addChangeHandler(new ChangeHandler() {
			String code = codeAux;
			@Override
			public void onChange(ChangeEvent event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue("",false);
					}
					String d = text.getValueOrThrow();
					text.addStyleName(AON.AON_CSS.aonChanged());
					
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),code, d , new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
					//d2DepositObject.doubleValueChanged(key, d );
				} catch (ParseException e) {
					// nothing.
				}
				
			}
		});
		//text.setValue(d2DepositObject.getDoubleValue(key));
		if(map.containsKey(key.getName())){
			String d = map.get(key.getName());
			text.setValue(d);
		}
		else text.setValue("");
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(!disabled);
		panel.add(text);
		
		//getInputs().put(key, text);
		if (!isTitle(key)) {
			panel.addStyleName(AON.AON_CSS.aonFiscalPaddingRight());
		}
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
}
