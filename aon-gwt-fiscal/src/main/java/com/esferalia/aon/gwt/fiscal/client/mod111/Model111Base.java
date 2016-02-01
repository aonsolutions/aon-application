package com.esferalia.aon.gwt.fiscal.client.mod111;

import java.util.EnumMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox.ExpressionResolver;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.IFiscalModelCallback;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.IMod111Declaration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;
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

public abstract class Model111Base extends ResizeComposite implements RequiresResize, IMod111Declaration {

	public static interface IModelScript {
		String getLabel();
		Mod111Key[] getKeys();
		boolean isEnabled();
		Mod111KeyInfo getInfoKey();
	}
	
	protected static final boolean ENABLED = true;
	protected static final boolean DISABLED = false;
	protected static final boolean HAS_INFO = true;
	protected static final boolean HAS_NOT_INFO = false;
	
	private static final int MAX_LABEL_LENGTH = 100;
	private static final int COL_NUMBER = 8;
	
	private FlexTable table;
	private EnumMap<Mod111Key,DoubleBox> fieldsMap;
	private IFiscalModelCallback<Mod111> callback;
	private ExpressionResolver resolver = new ExpressionResolver() {
		@Override
		public void resolve(String expression, AsyncCallback<Double> callback) {
			Model111.fiscalService.mathExpression(expression,callback);
		}
	}; 

	
	public Model111Base(IFiscalModelCallback<Mod111> callback) {
		this.callback = callback;
		fieldsMap = new EnumMap<>(Mod111Key.class);
		FlowPanel container = new FlowPanel();
		table = new FlexTable();
		container.add(table);
		
		paintDeclaration(callback.getFiscalModel());
		
		initWidget(container);
	}
	
	protected FlexTable getTable() {
		return table;
	}
	protected EnumMap<Mod111Key, DoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	
	protected abstract void paintDeclaration(final Mod111 mod111);
	
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
		
		getTable().getColumnFormatter().setWidth(7, "25px");
	}
	
	protected void paintHeader() {
		int row = getTable().getRowCount();
		getTable().setWidget(row, 0, new Label(AON.MSG.concept()));
		getTable().getFlexCellFormatter().setStyleName(row, 0,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottom() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderTop() );

		getTable().setWidget(row, 1, new Label(AON.MSG.receivers()));
		getTable().getFlexCellFormatter().setStyleName(row, 1,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 1,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 1,AON.AON_CSS.aonBorderBottom() );
		getTable().getFlexCellFormatter().addStyleName(row, 1,AON.AON_CSS.aonBorderTop() );
		getTable().getFlexCellFormatter().setColSpan(row, 1, 2);

		getTable().setWidget(row, 2, new Label(AON.MSG.perceptions()));
		getTable().getFlexCellFormatter().setStyleName(row, 2,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 2,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 2,AON.AON_CSS.aonBorderBottom() );
		getTable().getFlexCellFormatter().addStyleName(row, 2,AON.AON_CSS.aonBorderTop() );
		getTable().getFlexCellFormatter().setColSpan(row, 2, 2);

		getTable().setWidget(row, 3, new Label(AON.MSG.retentionAccountShort()));
		getTable().getFlexCellFormatter().setStyleName(row, 3,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 3,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 3,AON.AON_CSS.aonBorderBottom() );
		getTable().getFlexCellFormatter().addStyleName(row, 3,AON.AON_CSS.aonBorderTop() );
		getTable().getFlexCellFormatter().setColSpan(row, 3, 2);
		
		getTable().setWidget(row, 4, new Label("Inf."));
		getTable().getFlexCellFormatter().setStyleName(row, 4,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 4,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 4,AON.AON_CSS.aonBorderBottom() );
		getTable().getFlexCellFormatter().addStyleName(row, 4,AON.AON_CSS.aonBorderTop() );
	}
		
	protected void paintEmptyRow() {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label());
		table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	protected void paintRow(final Mod111 mod111, IModelScript script) {
		int row = table.getRowCount();
		paintLabel(row,mod111,script);
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
			for (Mod111Key key : script.getKeys()) {
				col = paintBox( row, col, key );
				col = paintField( row, col, mod111, script, key );
			}
			paintInfoCol(row,col,mod111,script);	
		}
	}

	protected void paintLabel( int row,Mod111 mod111, IModelScript script) {
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
	
	private int paintBox(int row, int col, Mod111Key key) {
		table.setWidget(row, col, new BoxLabel(key.getBox()));
		return ++col;
	}

	private int paintField(int row, int col, final Mod111 mod111, IModelScript script, final Mod111Key key) {
		final FiscalModelDetail det1 = mod111.ensureDetail(key);
		final DoubleBox input = new DoubleBox();
		input.setResolver(resolver);
		fieldsMap.put(key, input);
		input.setEnabled(script.isEnabled()); 
		input.setValue(det1.getAmount());
		input.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				mod111.ensureDetail(key).setAmount(input.getValue());
				if (input.isEnabled() && callback.isAuthomaticCalculationEnabled()) {
					calculateAndRefresh( mod111 );
				}
				callback.markAsDirty();
			}
		});
		table.setWidget(row, col, input);
		return ++col;
	}
	
	private void paintInfoCol(int row, int col, final Mod111 mod111, final IModelScript script) {
		if (script.getInfoKey() == null || script.getInfoKey() == Mod111KeyInfo.NONE) {
			table.setWidget(row, col, new Label(""));	
		} else {
			final Button button = new Button("");
			button.setTitle(script.getInfoKey().getLabel());
			button.setStyleName(AON.AON_CSS.aonIconCommandButton());
			button.addStyleName(AON.AON_CSS.aonIconQuestion());
			button.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					Model111.fiscalService.getInfo(Model111.getCurrentDomainName(),Model111.getCurrentDomain(),
						mod111,script.getKeys()[0], script.getInfoKey(),new AsyncCallback<String>() {

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
		}
		
	}

	@Override
	public void calculateAndRefresh(Mod111 mod111) {
		Model111.fiscalService.calculateMod111(Model111.getCurrentDomainName(),mod111,
				new AsyncCallback<Mod111>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.showErrorMsg(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod111 result) {
						for (Mod111Key key : fieldsMap.keySet()) {
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
	
	protected FlowPanel getAnchorPanel(String label, String href) {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonPadding2());
		Anchor a = new Anchor(label,href,"_blank");
		a.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		a.addStyleName(AON.AON_CSS.aonIconPdfPreview());
		p.add(a);
		return p;
	}
	
}
