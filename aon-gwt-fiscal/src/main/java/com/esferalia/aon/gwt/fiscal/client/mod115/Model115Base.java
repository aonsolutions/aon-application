package com.esferalia.aon.gwt.fiscal.client.mod115;

import java.util.EnumMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox.ExpressionResolver;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod115.Model115.IMod115Declaration;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.mod115.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.mod115.Model115ScriptProvider;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod115KeyInfo;
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
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;

public abstract class Model115Base extends ResizeComposite implements RequiresResize, IMod115Declaration {

	protected static final boolean ENABLED = true;
	protected static final boolean DISABLED = false;
	protected static final boolean HAS_INFO = true;
	protected static final boolean HAS_NOT_INFO = false;
	
	private static final int MAX_LABEL_LENGTH = 100;
	private static final int COL_NUMBER = 4;
	
	private FlexTable table;
	private EnumMap<Mod115Key,DoubleBox> fieldsMap;
	private IFiscalModelCallback<Mod115> callback;
	private ExpressionResolver resolver = new ExpressionResolver() {
		@Override
		public void resolve(String expression, AsyncCallback<Double> callback) {
			Model115.fiscalService.mathExpression(expression,callback);
		}
	}; 

	public Model115Base(IFiscalModelCallback<Mod115> callback) {
		this.callback = callback;
		fieldsMap = new EnumMap<>(Mod115Key.class);
		FlowPanel container = new FlowPanel();
		table = new FlexTable();
		container.add(table);
		
		paintDeclaration(callback.getFiscalModel());
		
		initWidget(container);
	}
	
	protected IFiscalModelCallback<Mod115> getCallback() {
		return callback;
	}
	
	protected FlexTable getTable() {
		return table;
	}
	protected EnumMap<Mod115Key, DoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	
	protected void paintDeclaration(final Mod115 mod115) {
		if (getTable().getRowCount() > 0) {
			getTable().removeAllRows();
		}
		defineTable();
		
		for (IModelScript ms : Model115ScriptProvider.obtainScript(mod115)) {
			if (ms.paintHeaderBefore()) {
				paintHeader();
			}
			paintRow(mod115,ms);	
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
		
		getTable().getColumnFormatter().setWidth(3, "25px");
	}
	
	protected void paintHeader() {
		int row = getTable().getRowCount();
		getTable().setWidget(row, 0, new Label(AON.MSG.concept()));
		getTable().getFlexCellFormatter().setStyleName(row, 0,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottom() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderTop() );

		getTable().setWidget(row, 1, new Label(AON.MSG.amount()));
		getTable().getFlexCellFormatter().setStyleName(row, 1,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 1,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 1,AON.AON_CSS.aonBorderBottom() );
		getTable().getFlexCellFormatter().addStyleName(row, 1,AON.AON_CSS.aonBorderTop() );
		getTable().getFlexCellFormatter().setColSpan(row, 1, 2);
		
		getTable().setWidget(row, 2, new Label("Inf."));
		getTable().getFlexCellFormatter().setStyleName(row, 2,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 2,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 2,AON.AON_CSS.aonBorderBottom() );
		getTable().getFlexCellFormatter().addStyleName(row, 2,AON.AON_CSS.aonBorderTop() );
	}
		
	protected void paintEmptyRow() {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label());
		table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	protected void paintRow(final Mod115 mod115, IModelScript script) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(mod115,script);		
		} else {
			int row = table.getRowCount();
			paintLabel(row,mod115,script);
			if (script.getKeys() == null) {
				table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);	
			} else {
				int col = 1;
				Mod115Key key = script.getKeys()[0];
				col = paintBox( row, col, key );
				col = paintField( row, col, mod115, script, key );
				paintInfoCol(row,col,mod115,script);
			}
		}
	}
	
	protected void paintParticularyRow(final Mod115 mod115, IModelScript script) {
		
	}
	
	protected void paintLabel( int row,Mod115 mod115, IModelScript script) {
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
		if (!script.isEnabled() ) {
			table.getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBold() );
		}
	}
	
	private int paintBox(int row, int col, Mod115Key key) {
		table.setWidget(row, col, new BoxLabel(key.getBox()));
		return ++col;
	}

	private int paintField(int row, int col, final Mod115 mod115, IModelScript script, final Mod115Key key) {
		final FiscalModelDetail det1 = mod115.ensureDetail(key);
		final DoubleBox input = new DoubleBox();
		input.setResolver(resolver);
		fieldsMap.put(key, input);
		input.setEnabled(mod115.isNotFinished() && script.isEnabled()); 
		input.setValue(det1.getAmount());
		input.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				mod115.ensureDetail(key).setAmount(input.getValue());
				if (input.isEnabled()) {
					calculateAndRefresh( mod115 );
				}
				callback.markAsDirty();
			}
		});
		table.setWidget(row, col, input);
		return ++col;
	}
	
	private void paintInfoCol(int row, int col, final Mod115 mod115, final IModelScript script) {
		if (script.getInfoKey() == Mod115KeyInfo.INVOICE) {
			final Button button = new Button("");
			button.setTitle(script.getInfoKey().getLabel());
			button.setStyleName(AON.AON_CSS.aonIconCommandButton());
			button.addStyleName(AON.AON_CSS.aonIconQuestion());
			button.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					Model115.fiscalService.getInfo(Model115.getCurrentDomainName(),Model115.getCurrentDomain(),
						mod115,script.getKeys()[0], script.getInfoKey(),new AsyncCallback<String>() {

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
			table.setWidget(row, col, button);
		} else {
			table.setWidget(row, col, new Label());	
		}
		
	}

	@Override
	public void calculateAndRefresh(Mod115 mod115) {
		Model115.fiscalService.calculateMod115(Model115.getCurrentDomainName(),mod115,
				new AsyncCallback<Mod115>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.showErrorMsg(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod115 result) {
						for (Mod115Key key : fieldsMap.keySet()) {
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
	
	protected FlowPanel getAnchorPanel(Mod115 mod115, String label, String href) {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonPadding2());
		Anchor a = new Anchor(label,href,"_blank");
		a.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		a.addStyleName(FiscalModelUtils.getAdministrationIcon(mod115.getAdministration()));
		p.add(a);
		return p;
	}
	
}
