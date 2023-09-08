package net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory;

import java.text.ParseException;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import net.aonsolutions.aon.gwt.ccaa.client.Deposit2;

public class PageF1E extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageF1E> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	String codeAux;

	public PageF1E(Deposit2 deposit) {
		super(deposit);
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		
		initializeTable();
	}

	private void init(){}
	
	@Override
	protected void initializeTable() {
		init();
		table();
	}
	
	private void table(){
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(0, "200px");
		table.getColumnFormatter().setWidth(1, "200px");
		table.getColumnFormatter().setWidth(2, "200px");
		table.getColumnFormatter().setWidth(3, "200px");

		int row = 0;
		table.setWidget(row, 0, new Label("Fecha"));
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 1, new Label("Relaci\u00F3n numerada de las acciones / participaciones"));
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label("Causa de la baja"));
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 3, new Label("% sobre capital"));
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
	
		++row;
		for(Integer i = 0; i< D2DepositConstants.A4_ABREVIATE_KEYS.length; i+=4){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A4_ABREVIATE_KEYS[i],
					D2DepositConstants.A4_ABREVIATE_KEYS[i+1],
					D2DepositConstants.A4_ABREVIATE_KEYS[i+2],
					D2DepositConstants.A4_ABREVIATE_KEYS[i+3],


			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 1 || j == 2)
					paintTextKeyField(table, d2[j], row, j);
				else if(j == 0  )
					paintDateKeyField(table, d2[j], row, j);
				else if(j == 3)
					paintDoubleKeyField(table ,d2[j],row,j);
			}
			++row;		
		}
	}
	
	
	
	
	
	protected int paintKey(FlexTable tab, D2DepositKey[] keys,  int row) {
		for (Integer i = 0; i < keys.length ; i++) {
			paintKeyField(tab,keys[i],row,i+1);
		}
		return  ++row;
	}
	
	private void paintTextKeyField(FlexTable tab, D2DepositFooterKey key,  int row, int col){
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final TextBox text = new TextBox();
		text.setTitle(codeId);
		text.setStyleName(AON.AON_CSS.aonInputText());
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
					text.setTitle(code);
					onEdit(code, d);
				} catch (ParseException e) {
					// nothing.
				}
			}
		});
		if(getMap().containsKey(key.getCode())){
			String d = getMap().get(key.getCode());
			text.setValue(d);
		}
		else text.setValue("");
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
	
		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}

	private void paintDateKeyField(FlexTable tab, D2DepositFooterKey key,  int row, int col){
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final DateBox text = new DateBox();
		text.setFormat(new DateBox.DefaultFormat(AON.DATE_FORMAT));
		text.setTitle(codeId);
		text.setStyleName(AON.AON_CSS.aonInputText());
		codeAux = codeId;
		text.addValueChangeHandler(new ValueChangeHandler<Date>() {
			String code = codeAux;

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				text.setTitle(code);
			
				onEdit(code, DATE_FORMAT.format(text.getValue()));			
			}
		});
		
		if(getMap().containsKey(key.getCode())){
			String datestr = getMap().get(key.getCode());

			getDeposit().getInma().getDate(datestr, new AsyncCallback<Date>() {
				@Override
				public void onSuccess(Date result) {
					text.setValue(result); 	
				}
				
				@Override
				public void onFailure(Throwable caught) {}
			} );
		}

		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());

		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
	
	private void paintDoubleKeyField(FlexTable tab, D2DepositFooterKey key,  int row, int col){
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final DoubleBox text = new DoubleBox();
		text.setTitle(codeId);
		codeAux = codeId;
		text.addChangeHandler(new ChangeHandler() {
			String code = codeAux;
			@Override
			public void onChange(ChangeEvent event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue(0.0,false);
					}
					Double d = text.getValueOrThrow();
					text.setTitle(code);
					onEdit(code, Double.toString(AonMathUtils.round(d)));
					
				} catch (ParseException e) {
					// nothing.
				}
				
			}
		});
		if(getMap().containsKey(key.getCode())){
			Double d =Double.parseDouble(getMap().get(key.getCode()));
			text.setValue(d);
		}
		else text.setValue(0.0);
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());

		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
 	
	protected void onEdit(String key, String value) {
		onEdit(key, value, false);
	}
}
