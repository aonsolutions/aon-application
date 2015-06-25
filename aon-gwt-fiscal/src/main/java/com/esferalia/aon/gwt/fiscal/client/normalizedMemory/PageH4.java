package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.text.ParseException;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.common.collect.Table;
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

public class PageH4 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageH4> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;
	

	
	@UiField TabPanel tabPanel;

	public PageH4() {
		super();
		table1 = new FlexTable();

		tabPanel = new TabPanel();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
	}
	
	public PageH4(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		table1 = new FlexTable();

		tabPanel = new TabPanel();

		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
		normalizedMemory.importSocietyButton.setVisible(true);
	}

	@Override
	protected void initializeTable() {
		table();
		table1();


	}
	
	private void table1(){
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
		table1.getColumnFormatter().setWidth(2, "200px");
		table1.getColumnFormatter().setWidth(3, "200px");
		
		table1.getColumnFormatter().setWidth(4, "200px");
		table1.getColumnFormatter().setWidth(5, "200px");
		table1.getColumnFormatter().setWidth(6, "200px");
		table1.getColumnFormatter().setWidth(7, "200px");
		table1.getColumnFormatter().setWidth(8, "200px");
		table1.getColumnFormatter().setWidth(9, "200px");
		
		table1.getColumnFormatter().setWidth(10, "200px");
		table1.getColumnFormatter().setWidth(11, "200px");
		table1.getColumnFormatter().setWidth(12, "200px");
		table1.getColumnFormatter().setWidth(13, "200px");
		
		int row = 0;

		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 1, new Label("Capital escriturado"));
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 2, new Label("Capital (no exigido)"));
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 3, new Label("Prima de emisi\u00F3n"));
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 4, new Label("Reservas"));
		table1.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 5, new Label("Acciones y participaciones en patrimonio propias"));
		table1.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 6, new Label("Resultados de ejercicios anteriores"));
		table1.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 7, new Label("Otras aportaciones de socios"));
		table1.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 8, new Label("Resultados del ejercicio"));
		table1.getFlexCellFormatter().addStyleName(row, 8, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 8, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 8, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 9, new Label("Dividendo a cuenta"));
		table1.getFlexCellFormatter().addStyleName(row, 9, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 9, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 9, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 10, new Label("Otros instrumentos de patrmonio neto"));
		table1.getFlexCellFormatter().addStyleName(row, 10, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 10, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 10, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 11, new Label("Ajustes por cambios de valor"));
		table1.getFlexCellFormatter().addStyleName(row, 11, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 11, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 11, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 12, new Label("Subvenciones, donaciones y legados recibidos"));
		table1.getFlexCellFormatter().addStyleName(row, 12, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 12, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 12, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 13, new Label("Total"));
		table1.getFlexCellFormatter().addStyleName(row, 13, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 13, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 13, AON.AON_CSS.aonTextCenter());
		
		++row;
		for(Integer i = 0; i< D2DepositConstants.PNA_ABREVIATE_KEYS_2.length; i+=13){
			D2DepositHeaderKey[] d2 = new D2DepositHeaderKey[]{
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i],
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i+1],
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i+2],
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i+3],
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i+4],
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i+5],
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i+6],
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i+7],
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i+8],
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i+9],
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i+10],
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i+11],
					D2DepositConstants.PNA_ABREVIATE_KEYS_2[i+12],
			};
			row = paintKey(table1, d2 , row);
		}
	}
	
	private void table(){		
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		table.getColumnFormatter().setWidth(2, "200px");
		table.getColumnFormatter().setWidth(3, "200px");
		int row = 0;
		table.setWidget(row, 0, new Label("Patrimonio neto y pasivo"));
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
		for(Integer i = 0; i< (D2DepositConstants.PNA_ABREVIATE_KEYS_1.length/3); i++){
			if(row == 2){
				table.setWidget(row, 0,new Label("INGRESOS Y GASTOS IMPUTADOS DIRECTAMENTE AL PATRIMONIO NETO"));
				++row;
			}
			if(row == 11){
				table.setWidget(row, 0,new Label("TRANSFERENCIAS A LA CUENTA DE P\u00C9RDIDAS Y GANANCIAS"));
				++row;
			}
			D2DepositHeaderKey[] d2 = new D2DepositHeaderKey[]{
					D2DepositConstants.PNA_ABREVIATE_KEYS_1[((D2DepositConstants.PNA_ABREVIATE_KEYS_1.length/3)*2)+i],
					D2DepositConstants.PNA_ABREVIATE_KEYS_1[i],
					D2DepositConstants.PNA_ABREVIATE_KEYS_1[(D2DepositConstants.PNA_ABREVIATE_KEYS_1.length/3)+i],
			};
			row = paintKeyWithText(table, d2 , row);
		}
	}
	
	
	protected int paintKey(FlexTable tab, D2DepositHeaderKey[] keys,  int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		for (Integer i = 0; i < keys.length ; i++) {
			paintKeyField(tab,keys[i],row,i+1);
		}
		return  ++row;
	}
	
	protected int paintKeyWithText(FlexTable tab, D2DepositHeaderKey[] keys,  int row) {
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
