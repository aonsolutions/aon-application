package com.esferalia.aon.gwt.fiscal.client.mod130;

import java.util.EnumMap;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox.ExpressionResolver;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130.IMod130Declaration;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.gwt.fiscal.shared.mod130.Model130ScriptProvider;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class Model130Base extends SimplePanel implements IMod130Declaration {

	protected static final boolean ENABLED = true;
	protected static final boolean DISABLED = false;
	protected static final boolean HAS_INFO = true;
	protected static final boolean HAS_NOT_INFO = false;
	
	private static final int MAX_LABEL_LENGTH = 300;
	private static final int COL_NUMBER = 4;
	
	protected static final String MODEL130_PRINT = "/aon_gwt_fiscal/ms/Model130Print";
	protected static final String MODEL130_FILE = "/aon_gwt_fiscal/ms/Model130File";
	protected static final String MODEL130_PRINT_AEAT = "/aon_gwt_fiscal/ms/Model130PrintAEAT";
	
	private Mod130 model;
	private API API;
	private FlexTable table;
	IFiscalModelCallback<Mod130> callback;
	private EnumMap<Mod130Key,DoubleBox> fieldsMap;
	
	protected FormPanel diskForm = new FormPanel("_blank");
	protected FormPanel aeatForm = new FormPanel("aeatForm");
	
	protected Hidden mod130Hidden = new Hidden("mod130");
	protected Hidden domainIdHidden = new Hidden("domainId");
	protected Hidden domainNameHidden = new Hidden("domainName");
	protected Hidden userHidden = new Hidden("user");
	
	protected Hidden modAeatHidden = new Hidden("mod");
	protected Hidden domainIdAeatHidden = new Hidden("domainId");
	protected Hidden domainNameAeatHidden = new Hidden("domainName");
	protected Hidden userAeatHidden = new Hidden("user");
	protected Hidden certAeatHidden = new Hidden("cert");
	protected Hidden passAeatHidden = new Hidden("pass");
	protected Hidden nameAeatHidden = new Hidden("name");
	protected Hidden documentAeatHidden = new Hidden("document");
	
	private ExpressionResolver resolver = new ExpressionResolver() {
		@Override
		public void resolve(String expression, AsyncCallback<Double> callback) {
			Model130.SERVICE.mathExpression(expression,callback);
		}
	}; 

	public Model130Base(final IFiscalModelCallback<Mod130> callback, AonData aonData) {
		this.callback = callback;
		this.API = new API(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getDomain().getName(), aonData.getDomain().getId(),
				aonData.getUser().getLogin());
		this.model = callback.getFiscalModel();
		this.fieldsMap = new EnumMap<>(Mod130Key.class);
		this.table = new FlexTable();
		paintDeclaration();
		setWidget(table);
	}
	
	protected FlexTable getTable() {
		return table;
	}
	protected EnumMap<Mod130Key, DoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	
	public Mod130 getModel() {
		return model;
	}
	
	public IFiscalModelCallback<Mod130> getCallback() {
		return callback;
	}

	public API getAPI() {
		return API;
	}
	
	
	protected void paintDeclaration() {
		if (getTable().getRowCount() > 0) {
			getTable().removeAllRows();
		}
		defineTable();
		
		for (IModelScript<Mod130Key> ms : Model130ScriptProvider.obtainScript(getCallback().getFiscalModel())) {
			if (ms.paintHeaderBefore()) {
				paintHeader();
			}
			paintRow(getCallback(),ms);	
		}
	}

	
	protected void defineTable() {
		getTable().setWidth("100%");
		getTable().addStyleName(AON.AON_CSS.aonMarginBottom());
		
		getTable().getColumnFormatter().setWidth(0, "auto");
		getTable().getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingLeft() );
		getTable().getColumnFormatter().addStyleName(0, AON.AON_CSS.aonPaddingRight() );
		
		getTable().getColumnFormatter().setWidth(1, "40px");
		getTable().getColumnFormatter().setStyleName(5, AON.AON_CSS.aonTextCenter());
		getTable().getColumnFormatter().setWidth(2, "140px");
		
		getTable().getColumnFormatter().setWidth(3, "50px");
	}
	
	protected void paintHeader() {
		int row = getTable().getRowCount();
		getTable().setWidget(row, 0, new Label(" "));
		getTable().getFlexCellFormatter().setStyleName(row, 0,AON.AON_CSS.aonBold() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextCenter() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderBottom() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonBorderTop() );

		getTable().setWidget(row, 1, new Label(" "));
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

	protected void paintRow(final IFiscalModelCallback<Mod130> callback, IModelScript<Mod130Key> script) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(callback,script);		
		} else {
			int row = table.getRowCount();
			paintLabel(row,callback,script);
			if (script.getKeys() == null) {
				table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);	
			} else {
				int col = 1;
				for (Mod130Key key : script.getKeys()) {
					col = paintBox( row, col, key );
					col = paintField( row, col, callback, script, key );
				}
				paintInfoCol(row,col,callback,script);	
			}
		}
	}
	
	protected void paintParticularyRow(final IFiscalModelCallback<Mod130> callback, IModelScript<Mod130Key> script) {
		
	}
	
	protected void paintLabel( int row,final IFiscalModelCallback<Mod130> callback, IModelScript<Mod130Key> script) {
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
	
	private int paintBox(int row, int col, Mod130Key key) {
		table.setWidget(row, col, new BoxLabel(key.getBox()));
		return ++col;
	}

	private int paintField(int row, int col, final IFiscalModelCallback<Mod130> callback, IModelScript<Mod130Key> script, final Mod130Key key) {
		final FiscalModelDetail det1 = callback.getFiscalModel().ensureDetail(key);
		final DoubleBox input = new DoubleBox();
		input.setResolver(resolver);
		fieldsMap.put(key, input);
		input.setEnabled(callback.getFiscalModel().isNotFinished() && script.isEnabled()); 
		input.setValue(det1.getAmount());
		input.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				if (event.getValue() == null) input.setValue(0.0, false);
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
	
	private void paintInfoCol(int row, int col, final IFiscalModelCallback<Mod130> callback, final IModelScript<Mod130Key> script) {
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
				if 	(infoKey == FiscalModelKeyInfo.ACT_ACCOUNT) button.addStyleName(AON.AON_CSS.aonIconCompanyData());
				
				button.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						Model130.SERVICE.getInfo(callback.getDomainName(),callback.getUser(), callback.getDomain(),
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
	public void calculateAndRefresh(final IFiscalModelCallback<Mod130> callback) {
		Model130.SERVICE.calculate(callback.getDomainName(),callback.getUser(),callback.getFiscalModel(),
				new AsyncCallback<Mod130>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.showErrorMsg(AON.MSG.errorMessage());
					}

					@Override
					public void onSuccess(Mod130 result) {
						for (Mod130Key key : fieldsMap.keySet()) {
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
	
	protected FlowPanel getAnchorPanel(Mod130 mod130, String label, String href) {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonPadding2());
		Anchor a = new Anchor(label,href,"_blank");
		a.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		a.addStyleName(FiscalModelUtils.getAdministrationIcon(mod130.getAdministration()));
		p.add(a);
		return p;
	}
	

	public void printButtonClick() {
		if (getCallback().isDirty()) {
			new ConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
				, new ConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					submitForm(MODEL130_PRINT);
				}

				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL130_PRINT);
		}
	}
	
	protected void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod130Hidden.setValue(String.valueOf(getModel().getId()));
		domainIdHidden.setValue(String.valueOf(getCallback().getDomain()));
		domainNameHidden.setValue(getCallback().getDomainName());
		userHidden.setValue(getCallback().getUser());
		diskForm.submit();
	}

	protected void submitAEAT(String action) {
		submitAEAT(action, "null", "null", "null", "null");
	}
	
	protected void submitAEAT(String action, String cert, String pass, String document, String name) {
		aeatForm.setAction(GWT.getHostPageBaseURL() + action);
		modAeatHidden.setValue(String.valueOf(getModel().getId()));
		domainIdAeatHidden.setValue(String.valueOf(getCallback().getDomain()));
		domainNameAeatHidden.setValue(getCallback().getDomainName());
		userAeatHidden.setValue(getCallback().getUser());
		certAeatHidden.setValue(cert);
		passAeatHidden.setValue(pass);
		nameAeatHidden.setValue(name);
		documentAeatHidden.setValue(document);
		aeatForm.submit();
	}
	
	public FlowPanel getInformationPanel() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		panel.addStyleName(AON.AON_CSS.aonMarginTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingLeft());
		 
		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "30px");
		tab.getColumnFormatter().setWidth(1
				, "auto");
		tab.setStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		Label title = new Label("Informaci\u00F3n \u00FAtil para la confecci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getModel().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;
		for (Pair<String, String> pair : getInformationLinks()) {
			Label icon = new Label();
			icon.addStyleName(FiscalModelUtils.getAdministrationIcon(getModel().getAdministration()));
			tab.setWidget(row, 0, icon );
			tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());

			FlowPanel p = new FlowPanel();
			p.setStyleName(AON.AON_CSS.aonPadding2());
			Anchor a = new Anchor(pair.getLeft(),pair.getRight(), "_blank");
			a.setStyleName(AON.AON_CSS.aonPaddingLeft());
			p.add(a);
			tab.setWidget(row, 1, p );
			tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			row++;
		}
		panel.add(tab);
		return panel;
	}
	
}
