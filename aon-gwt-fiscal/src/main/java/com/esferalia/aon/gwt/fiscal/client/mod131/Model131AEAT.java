package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.EnumMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox.ExpressionResolver;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131.IMod131Declaration;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.mod131.Model131ScriptProvider;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;

public class Model131AEAT extends SimplePanel implements IMod131Declaration {
	
	private static class Mod131ActivityProvidesKey implements ProvidesKey<Mod131Activity> {
		@Override
		public Object getKey(Mod131Activity model) {
			return AonStringUtils.isBlank(model.getEpigraph()) ? null : model.getEpigraph();
		}
	}
	
	protected static final boolean ENABLED = true;
	protected static final boolean DISABLED = false;
	protected static final boolean HAS_INFO = true;
	protected static final boolean HAS_NOT_INFO = false;
	
	private static final int MAX_LABEL_LENGTH = 300;
	private static final int COL_NUMBER = 8;
	
	private FlexTable table;
	private EnumMap<Mod131Key,DoubleBox> fieldsMap;
	private ExpressionResolver resolver = new ExpressionResolver() {
		@Override
		public void resolve(String expression, AsyncCallback<Double> callback) {
			Model131.fiscalService.mathExpression(expression,callback);
		}
	}; 

	public Model131AEAT(IFiscalModelCallback<Mod131> callback) {
		fieldsMap = new EnumMap<>(Mod131Key.class);
		table = new FlexTable();
		paintDeclaration(callback);
		setWidget(table);
	}
	
	protected FlexTable getTable() {
		return table;
	}
	protected EnumMap<Mod131Key, DoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	
	protected void paintDeclaration(final IFiscalModelCallback<Mod131> callback) {
		if (getTable().getRowCount() > 0) {
			getTable().removeAllRows();
		}
		defineTable();
		
		for (IModelScript<Mod131Key> ms : Model131ScriptProvider.obtainScript(callback.getFiscalModel())) {
			if (ms.paintHeaderBefore()) {
				paintHeader();
				paintActivityRow(callback);
			}
			paintRow(callback,ms);	
		}
	}

	
	protected void defineTable() {
		getTable().setWidth("100%");
		getTable().addStyleName(AON.AON_CSS.aonMarginBottom());
		getTable().addStyleName(AON.AON_CSS.aonBorderCollapse());
		getTable().addStyleName(AON.AON_CSS.aonFiscalModelDataTable());
		
		getTable().getColumnFormatter().setWidth(0, "20px");
		getTable().getColumnFormatter().setWidth(1, "140px");
		getTable().getColumnFormatter().setWidth(2, "auto");
		getTable().getColumnFormatter().setWidth(3, "140px");
		getTable().getColumnFormatter().setWidth(4, "100px");
		getTable().getColumnFormatter().setWidth(5, "40px");
		getTable().getColumnFormatter().setWidth(6, "140px");
		getTable().getColumnFormatter().setWidth(7, "50px");
	}
	
	protected void paintHeader() {
		int row = getTable().getRowCount();
		getTable().setWidget(row, 0, new Label());
		getTable().getFlexCellFormatter().setStyleName(row, 0,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottom() );

		getTable().setWidget(row, 1, new Label());
		getTable().getFlexCellFormatter().setStyleName(row, 1,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 1,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 1,AON.AON_CSS.aonBorderBottom() );

		getTable().setWidget(row, 2, new Label());
		getTable().getFlexCellFormatter().setStyleName(row, 2,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 2,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 2,AON.AON_CSS.aonBorderBottom() );

		getTable().setWidget(row, 3, new Label());
		getTable().getFlexCellFormatter().setStyleName(row, 3,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 3,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 3,AON.AON_CSS.aonBorderBottom() );

		getTable().setWidget(row, 4, new Label());
		getTable().getFlexCellFormatter().setStyleName(row, 4,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 4,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 4,AON.AON_CSS.aonBorderBottom() );

		getTable().setWidget(row, 5, new Label());
		getTable().getFlexCellFormatter().setStyleName(row, 5,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 5,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 5,AON.AON_CSS.aonBorderBottom() );

		getTable().setWidget(row, 6, new Label());
		getTable().getFlexCellFormatter().setStyleName(row, 6,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 6,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 6,AON.AON_CSS.aonBorderBottom() );

		getTable().setWidget(row, 6, new Label());
		getTable().getFlexCellFormatter().setStyleName(row, 7,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 7,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 7,AON.AON_CSS.aonBorderBottom() );

	}
		
	protected void paintEmptyRow() {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label( "." ));
		table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	protected void paintRow(final IFiscalModelCallback<Mod131> callback, IModelScript<Mod131Key> script) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(callback,script);
		} else {
			int row = table.getRowCount();
			paintLabel(row,callback,script);
			table.getFlexCellFormatter().setColSpan(row, 0, (script.getKeys() == null)?COL_NUMBER:(COL_NUMBER-3));
			if (script.getKeys() != null) {
				int col = 1;
				for (Mod131Key key : script.getKeys()) {
					col = paintBox( row, col, key );
					col = paintField( row, col, callback, script, key );
				}
				paintInfoCol(row,col,callback,script);	
			}
		}
	}
	
	protected void paintLabel( int row,final IFiscalModelCallback<Mod131> callback, IModelScript<Mod131Key> script) {
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
	
	private int paintBox(int row, int col, Mod131Key key) {
		table.setWidget(row, col, new BoxLabel(key.getBox()));
		return ++col;
	}

	private int paintField(int row, int col, final IFiscalModelCallback<Mod131> callback, IModelScript<Mod131Key> script, final Mod131Key key) {
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
	
	private void paintInfoCol(int row, int col, final IFiscalModelCallback<Mod131> callback, final IModelScript<Mod131Key> script) {
		FlowPanel buttonContainer = new FlowPanel();
		for (final FiscalModelKeyInfo infoKey : script.getInfoKeys()) {
			buttonContainer.setStyleName(AON.AON_CSS.aonNowrap());
			if 	(infoKey != FiscalModelKeyInfo.NONE) {
				final Button button = new Button("");
				button.setTitle(infoKey.getLabel());
				button.setStyleName(AON.AON_CSS.aonIconCommandButton());
				
				if 	(infoKey == FiscalModelKeyInfo.INVOICE) button.addStyleName(AON.AON_CSS.aonIconInvoice());
				if 	(infoKey == FiscalModelKeyInfo.DIFF_INVOICE) button.addStyleName(AON.AON_CSS.aonIconDiff());
				if 	(infoKey == FiscalModelKeyInfo.SALARY) button.addStyleName(AON.AON_CSS.aonIconPayroll());
				if 	(infoKey == FiscalModelKeyInfo.SALARY_IN_KIND) button.addStyleName(AON.AON_CSS.aonIconPayroll());
				if 	(infoKey == FiscalModelKeyInfo.DIFF_SALARY) button.addStyleName(AON.AON_CSS.aonIconDiff());
				if 	(infoKey == FiscalModelKeyInfo.COMPUTE) button.addStyleName(AON.AON_CSS.aonIconCalculator());
				if 	(infoKey == FiscalModelKeyInfo.COMPUTE_KEY) button.addStyleName(AON.AON_CSS.aonIconCalculator());
				if 	(infoKey == FiscalModelKeyInfo.IRPF_ACTIVITY) button.addStyleName(AON.AON_CSS.aonIconActivities());
				
				button.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						Model131.fiscalService.getInfo(Model131.getCurrentDomainName(),Model131.getCurrentDomain(),
							callback.getFiscalModel(), script, infoKey,new AsyncCallback<String>() {

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
	public void calculateAndRefresh(final IFiscalModelCallback<Mod131> callback) {
		Model131.fiscalService.calculateMod131(Model131.getCurrentDomainName(),callback.getFiscalModel(),
				new AsyncCallback<Mod131>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.showErrorMsg(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod131 result) {
						for (Mod131Key key : fieldsMap.keySet()) {
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
	
	protected FlowPanel getAnchorPanel(Mod131 mod131, String label, String href) {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonPadding2());
		Anchor a = new Anchor(label,href,"_blank");
		a.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		a.addStyleName(FiscalModelUtils.getAdministrationIcon(mod131.getAdministration()));
		p.add(a);
		return p;
	}
	
	public Widget getInfoPanel(Mod131 mod131) {

		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.add(getAnchorPanel( mod131
				,"Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/G602.shtml"));
		panel.add(getAnchorPanel( mod131
				,"Informaci\u00F3n general." 
				,"https://www.agenciatributaria.gob.es/AEAT.sede/Ayuda/G602.shtml"));
		panel.add(getAnchorPanel(mod131
				,"Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/G602.shtml"));
		return panel;
	}
	
	protected void paintParticularyRow(final IFiscalModelCallback<Mod131> callback, IModelScript<Mod131Key> script) {
		if (script.getKeys() == null) return;
		if (script.getKeys()[0] == Mod131Key.AC1_EPI
		 || script.getKeys()[0] == Mod131Key.AC2_EPI
		 || script.getKeys()[0] == Mod131Key.AC3_EPI
		 || script.getKeys()[0] == Mod131Key.AC4_EPI
		 || script.getKeys()[0] == Mod131Key.AC5_EPI) {
			
		} else if (script.getKeys()[0] == Mod131Key.P1) {
			paintRowP01(callback,script);
		} else if (script.getKeys()[0] == Mod131Key.P2) {
			paintRowP02(callback,script);
		}
	}

	private void paintActivityRow(final IFiscalModelCallback<Mod131> callback) {
		int row = getTable().getRowCount();
		final Mod131ActivityProvidesKey providesKey = new Mod131ActivityProvidesKey();
		final Mod131ActivityTable table = new Mod131ActivityTable(providesKey);
		table.addRangeChangeHandler(new Handler() {
			
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				table.setRowData(callback.getFiscalModel().getActivities());
			}
		});
		final NoSelectionModel<Mod131Activity> model = new NoSelectionModel<Mod131Activity>(providesKey);
		table.setSelectionModel(model);
		model.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				Mod131Activity act = model.getLastSelectedObject();
				Model131Activity actPanel = new Model131Activity(act);
				CustomDialog dialog = new CustomDialog();
				dialog.setCaption(act.getFullDescription());
				dialog.setGlassEnabled(true);
				dialog.setAnimationEnabled(true);
				dialog.add(actPanel);
				dialog.setWidth("700px");
				dialog.setHeight("500px");
				dialog.show();
				dialog.center();
			}
		});
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
		FlowPanel tableContainer = new FlowPanel();
		tableContainer.add( table ) ;
		getTable().setWidget(row, 0, tableContainer );
		getTable().getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	private void paintRowP01(final IFiscalModelCallback<Mod131> callback, IModelScript<Mod131Key> script) {
		int row = getTable().getRowCount();
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		getTable().getFlexCellFormatter().setColSpan(row, 0, 6);
		
		final FiscalModelDetail p1 = callback.getFiscalModel().ensureDetail(Mod131Key.P1);
		getTable().setWidget(row, 1, new Label( AON.FMT.format(p1.getAmount()) + "%"));
		getTable().getFlexCellFormatter().setColSpan(row, 1, 2);
	}

	private void paintRowP02(final IFiscalModelCallback<Mod131> callback, IModelScript<Mod131Key> script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail p2 = callback.getFiscalModel().ensureDetail(Mod131Key.P2);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		getTable().getFlexCellFormatter().setColSpan(row, 0, 6);
		
		final Label wP2 = new Label(p2.getAmount()==1?AON.MSG.yes():AON.MSG.no());
		getTable().setWidget(row, 1, wP2 );
		getTable().getFlexCellFormatter().setColSpan(row, 1, 2);
	}
	

}
