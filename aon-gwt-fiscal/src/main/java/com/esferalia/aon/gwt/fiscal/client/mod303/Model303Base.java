package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.EnumMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox.ExpressionResolver;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.IMod303Declaration;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.mod303.Model303ScriptProvider;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class Model303Base extends SimplePanel implements IMod303Declaration {

	protected static final boolean ENABLED = true;
	protected static final boolean DISABLED = false;
	protected static final boolean HAS_INFO = true;
	protected static final boolean HAS_NOT_INFO = false;
	
	private static final int MAX_LABEL_LENGTH = 100;
	private static final int COL_NUMBER = 8;
	
	private FlexTable table;
	private EnumMap<Mod303Key,DoubleBox> fieldsMap;
	
	private ExpressionResolver resolver = new ExpressionResolver() {
		@Override
		public void resolve(String expression, AsyncCallback<Double> callback) {
			Model303.fiscalService.mathExpression(expression,callback);
		}
	}; 

	public Model303Base(IFiscalModelCallback<Mod303> callback) {
		fieldsMap = new EnumMap<>(Mod303Key.class);
		table = new FlexTable();
		paintDeclaration(callback);
		setWidget(table);
	}
	
	protected FlexTable getTable() {
		return table;
	}
	protected EnumMap<Mod303Key, DoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	
	protected void paintDeclaration(final IFiscalModelCallback<Mod303> callback) {
		if (getTable().getRowCount() > 0) {
			getTable().removeAllRows();
		}
		defineTable();
		
		for (IModelScript<Mod303Key> ms : Model303ScriptProvider.obtainScript(callback.getFiscalModel())) {
			if (ms.paintHeaderBefore()) {
				paintHeader();
			}
			paintRow(callback,ms);	
		}
	}

	
	protected void defineTable() {
		getTable().setWidth("100%");
		getTable().addStyleName(AON.AON_CSS.aonMarginBottom());
		
		getTable().getColumnFormatter().setWidth(0, "auto");
		getTable().getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		getTable().getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		
		getTable().getColumnFormatter().setWidth(1, "40px");
		getTable().getColumnFormatter().setStyleName(1, AON.AON_CSS.aonTextCenter());
		getTable().getColumnFormatter().setWidth(2, "140px");
		
		getTable().getColumnFormatter().setWidth(3, "40px");
		getTable().getColumnFormatter().setStyleName(3, AON.AON_CSS.aonTextCenter());
		getTable().getColumnFormatter().setWidth(4, "140px");
		
		getTable().getColumnFormatter().setWidth(5, "40px");
		getTable().getColumnFormatter().setStyleName(5, AON.AON_CSS.aonTextCenter());
		getTable().getColumnFormatter().setWidth(6, "140px");
		
		getTable().getColumnFormatter().setWidth(7, "50px");
	}
	
	protected void paintHeader() {
//		int row = getTable().getRowCount();
//		getTable().setWidget(row, 0, new Label(AON.MSG.concept()));
//		getTable().getFlexCellFormatter().setStyleName(row, 0,AON.AON_CSS.aonBold() );
//		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextCenter() );
//		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottom() );
//		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderTop() );
//
//		getTable().setWidget(row, 1, new Label(AON.MSG.taxableBase()));
//		getTable().getFlexCellFormatter().setStyleName(row, 1,AON.AON_CSS.aonBold() );
//		getTable().getFlexCellFormatter().addStyleName(row, 1,AON.AON_CSS.aonTextCenter() );
//		getTable().getFlexCellFormatter().addStyleName(row, 1,AON.AON_CSS.aonBorderBottom() );
//		getTable().getFlexCellFormatter().addStyleName(row, 1,AON.AON_CSS.aonBorderTop() );
//		getTable().getFlexCellFormatter().setColSpan(row, 1, 2);
//
//		getTable().setWidget(row, 2, new Label(AON.MSG.percent()));
//		getTable().getFlexCellFormatter().setStyleName(row, 2,AON.AON_CSS.aonBold() );
//		getTable().getFlexCellFormatter().addStyleName(row, 2,AON.AON_CSS.aonTextCenter() );
//		getTable().getFlexCellFormatter().addStyleName(row, 2,AON.AON_CSS.aonBorderBottom() );
//		getTable().getFlexCellFormatter().addStyleName(row, 2,AON.AON_CSS.aonBorderTop() );
//		getTable().getFlexCellFormatter().setColSpan(row, 2, 2);
//
//		getTable().setWidget(row, 3, new Label(AON.MSG.quota()));
//		getTable().getFlexCellFormatter().setStyleName(row, 3,AON.AON_CSS.aonBold() );
//		getTable().getFlexCellFormatter().addStyleName(row, 3,AON.AON_CSS.aonTextCenter() );
//		getTable().getFlexCellFormatter().addStyleName(row, 3,AON.AON_CSS.aonBorderBottom() );
//		getTable().getFlexCellFormatter().addStyleName(row, 3,AON.AON_CSS.aonBorderTop() );
//		getTable().getFlexCellFormatter().setColSpan(row, 3, 2);
//		
//		getTable().setWidget(row, 4, new Label("Inf."));
//		getTable().getFlexCellFormatter().setStyleName(row, 4,AON.AON_CSS.aonBold() );
//		getTable().getFlexCellFormatter().addStyleName(row, 4,AON.AON_CSS.aonTextCenter() );
//		getTable().getFlexCellFormatter().addStyleName(row, 4,AON.AON_CSS.aonBorderBottom() );
//		getTable().getFlexCellFormatter().addStyleName(row, 4,AON.AON_CSS.aonBorderTop() );
	}
		
	protected void paintEmptyRow() {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label());
		table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}
	protected int paintEmptyCol(int row, int col, int colspan) {
		table.setWidget(row, col, new Label());
		table.getFlexCellFormatter().addStyleName(row, col,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().setColSpan(row, col, colspan);
		return ++col;
	}

	protected void paintRow(final IFiscalModelCallback<Mod303> callback, IModelScript<Mod303Key> script) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(callback,script);		
		} else {
			int row = table.getRowCount();
			paintLabel(row,callback,script);
			if (script.getKeys() == null) {
				table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);	
			} else {
				table.getFlexCellFormatter().setColSpan(row, 0, 
						(script.getKeys().length==1
							?5:
							(script.getKeys().length==2
								?3
								:1)
						) 
					);
				int col = 1;
				for (Mod303Key key : script.getKeys()) {
					if (key == null) {
						col = paintEmptyCol(row, col, 2);
					} else {
						col = paintBox( row, col, key );
						col = paintField( row, col, callback, script, key );
					}
				}
				paintInfoCol(row,col,callback,script);	
			}
		}
	}
	
	protected void paintParticularyRow(final IFiscalModelCallback<Mod303> callback, IModelScript<Mod303Key> script) {
		
	}
	
	protected void paintLabel( int row,final IFiscalModelCallback<Mod303> callback, IModelScript<Mod303Key> script) {
		String labelText = script.getLabel();
		Label label = new Label();
		if (AonStringUtils.length(labelText) > MAX_LABEL_LENGTH) {
			label.setTitle(labelText);	
			labelText = AonStringUtils.abbreviate(labelText, MAX_LABEL_LENGTH);
		}
		label.setText(labelText);
		table.setWidget(row, 0, label);
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottomImportant() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft() );
		if (script.isTitle() ) {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBold() );
		} else {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingLeft20() );
		}
	}
	
	private int paintBox(int row, int col, Mod303Key key) {
		table.setWidget(row, col, new BoxLabel(key.getBox()));
		return ++col;
	}

	private int paintField(int row, int col, final IFiscalModelCallback<Mod303> callback, IModelScript<Mod303Key> script, final Mod303Key key) {
		final FiscalModelDetail det1 = callback.getFiscalModel().ensureDetail(key);
		final DoubleBox input = new DoubleBox();
		input.setResolver(resolver);
		fieldsMap.put(key, input);
		input.setEnabled(callback.getFiscalModel().isNotFinished() && script.isEnabled()); 
		input.setValue(det1.getAmount());
		input.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				double result = callback.getFiscalModel().getResultAmount(key);
				double adjust = callback.getFiscalModel().getAdjustAmount(key);
				double amount = input.getValue();
				if (AonMathUtils.isNotZero(result - adjust - amount)) {
					callback.getFiscalModel().ensureDetail(key).setAdjustAmount( result - amount);	
				}
				callback.getFiscalModel().ensureDetail(key).setAmount(input.getValue());
				if (input.isEnabled()) {
					calculateAndRefresh( callback );
				}
				callback.markAsDirty();
			}
		});
		table.setWidget(row, col, input);
		return ++col;
	}
	
	private void paintInfoCol(int row, int col, final IFiscalModelCallback<Mod303> callback, final IModelScript<Mod303Key> script) {
		FlowPanel buttonContainer = new FlowPanel();
		for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
			buttonContainer.setStyleName(AON.AON_CSS.aonNowrap());
			if 	(infoKey != FiscalModelKeyInfo.NONE) {
				final Button button = new Button("");
				button.setTitle(infoKey.getLabel());
				button.setStyleName(AON.AON_CSS.aonIconCommandButton());
				
				if 	(infoKey == FiscalModelKeyInfo.INVOICE) button.addStyleName(AON.AON_CSS.aonIconInvoice());
				if 	(infoKey == FiscalModelKeyInfo.DIFF_INVOICE) button.addStyleName(AON.AON_CSS.aonIconDiff());
				if 	(infoKey == FiscalModelKeyInfo.COMPUTE) button.addStyleName(AON.AON_CSS.aonIconCalculator());
				if 	(infoKey == FiscalModelKeyInfo.COMPUTE_KEY) button.addStyleName(AON.AON_CSS.aonIconCompanyData());
				
				button.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						Model303.mod303Service.getInfo(Model303.getCurrentDomainName(),Model303.getCurrentDomain(),
							callback.getFiscalModel(),script, infoKey,new AsyncCallback<String>() {

									@Override
									public void onFailure(Throwable caught) {
										callback.showErrorMsg(AON.MSG.errorMessage());
									}

									@Override
									public void onSuccess(String result) {
										callback.showInfoPanel(result);
									}
							
								}
							);	
					}
				});
				buttonContainer.add(button);
			}
			table.setWidget(row, col, buttonContainer);
		}
	}

	@Override
	public void calculateAndRefresh(final IFiscalModelCallback<Mod303> callback) {
		Model303.mod303Service.calculateMod303(Model303.getCurrentDomainName(),callback.getFiscalModel(),
				new AsyncCallback<Mod303>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.showErrorMsg(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod303 result) {
						for (Mod303Key key : fieldsMap.keySet()) {
							double d1 = result.getAmount(key);
							double d2 = fieldsMap.get(key).getValue();
							if (!AonNumberUtils.equals(d1, d2)) {
								fieldsMap.get(key).setValue(d1,true,true);

							}
						}
					}
			
				}
			);	
	}
	
	protected FlowPanel getAnchorPanel(Mod303 mod303, String label, String href) {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonPadding2());
		Anchor a = new Anchor(label,href,"_blank");
		a.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		a.addStyleName(FiscalModelUtils.getAdministrationIcon(mod303.getAdministration()));
		p.add(a);
		return p;
	}
	
}
