package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.EnumMap;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131.IMod131Declaration;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131.Model131Callback;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131Activity.IMod131ActivityCallback;
import com.esferalia.aon.gwt.fiscal.shared.mod131.Model131AEATScript;
import com.esferalia.aon.gwt.fiscal.shared.mod131.Model131ScriptProvider;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

public abstract class Model131Base extends SimplePanel implements IMod131Declaration {

	protected static final boolean ENABLED = true;
	protected static final boolean DISABLED = false;
	protected static final boolean HAS_INFO = true;
	protected static final boolean HAS_NOT_INFO = false;
	
	private static final int MAX_LABEL_LENGTH = 300;
	private static final int COL_NUMBER = 8;
	
	protected static final String MODEL131_PRINT = "/aon_gwt_fiscal/ms/Model131Print";
	protected static final String MODEL131_FILE = "/aon_gwt_fiscal/ms/Model131File";
	protected static final String MODEL131_PRINT_AEAT = "/aon_gwt_fiscal/ms/Model131PrintAEAT";
	
	private static class Mod131ActivityProvidesKey implements ProvidesKey<Mod131Activity> {
		@Override
		public Object getKey(Mod131Activity model) {
			return AonStringUtils.isBlank(model.getEpigraph()) ? null : model.getEpigraph();
		}
	}
	
	private Mod131 model;
	private API API;
	private FlexTable table;
	Model131Callback callback;
	private EnumMap<Mod131Key,DoubleBox> fieldsMap;
	
	protected FormPanel diskForm = new FormPanel("_blank");
	protected FormPanel aeatForm = new FormPanel("aeatForm");
	
	protected Hidden mod131Hidden = new Hidden("mod131");
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
	protected Hidden nrcAeatHidden = new Hidden("nrc");
	protected Hidden testHidden = new Hidden("test");
	
	Model131Base(Mod131 mod131, final Model131Callback callback) {
		this.callback = callback;
		this.API = new API(GWT.getModuleBaseURL(), 
			callback.getOptions().getConfiguration().getMd5(),
			callback.getOptions().getConfiguration().getDomain().getName(), 
			callback.getOptions().getConfiguration().getDomain().getId(),
			callback.getOptions().getConfiguration().getUser().getLogin());
		this.model = mod131;
		this.fieldsMap = new EnumMap<>(Mod131Key.class);
		this.table = new FlexTable();
		paintDeclaration();
		setWidget(table);
	}
	
	protected FlexTable getTable() {
		return table;
	}
	protected EnumMap<Mod131Key, DoubleBox> getFieldsMap() {
		return fieldsMap;
	}
	
	public Mod131 getModel() {
		return model;
	}
	
	public Model131Callback getCallback() {
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
		
		for (IModelScript<Mod131Key> ms : Model131ScriptProvider.obtainScript(getModel())) {
			if (ms.paintHeaderBefore()) {
				paintHeader();
			}
			paintRow(getCallback(), ms);
			if (ms == Model131AEATScript.R00) {
				paintActivityRow(getCallback());
			}
		}
	}
	
	private void paintActivityRow(final Model131Callback callback) {
		int row = getTable().getRowCount();
		final Mod131ActivityProvidesKey providesKey = new Mod131ActivityProvidesKey();
		final Mod131ActivityTable tab = new Mod131ActivityTable(providesKey);
		tab.setStyleName( AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName( AON.AON_CSS.aonBlockCenter());
		tab.addStyleName( AON.AON_CSS.aonMarginTop());
		tab.addStyleName( AON.AON_CSS.aonMarginBottom());
		tab.addRangeChangeHandler(event -> tab.setRowData(getModel().getActivities()));
		final NoSelectionModel<Mod131Activity> mod = new NoSelectionModel<>(providesKey);
		tab.setSelectionModel(mod);
		mod.addSelectionChangeHandler(event -> {
			final CustomDialog dialog = new CustomDialog();
			IMod131ActivityCallback activityCallback = new IMod131ActivityCallback() {
				
				@Override
				public Mod131 getModel() {
					return Model131Base.this.getModel();
				}
				
				@Override
				public void onCancel() {
					dialog.hide();
					calculateAndRefresh(callback);
					tab.redraw();
				}
				
				@Override
				public void onAccept() {
					dialog.hide();
					calculateAndRefresh(callback);
					tab.redraw();
				}
				
				@Override
				public Mod131Activity getActivity() {
					return mod.getLastSelectedObject();
				}

				@Override
				public void onRemove() {
					dialog.hide();
					for (int i = 0; i < getModel().getActivities().size() ; i++ ) {
						if (getModel().getActivities().get(i) == mod.getLastSelectedObject()) {
							getModel().getActivities().get(i).initialize();
						}
					}
					calculateAndRefresh(callback);
					tab.redraw();
				}

				@Override
				public Model131ModuleOptions getOptions() {
					return callback.getOptions();
				}
			};
			Model131Activity actPanel = new Model131Activity(activityCallback);
			dialog.setCaption(mod.getLastSelectedObject().getFullDescription());
			dialog.setGlassEnabled(true);
			dialog.setAnimationEnabled(true);
			dialog.add(actPanel);
			dialog.setWidth("700px");
			dialog.setHeight("600px");
			dialog.show();
			dialog.center();
		});
		tab.setVisibleRangeAndClearData(tab.getVisibleRange(), true);
		FlowPanel tableContainer = new FlowPanel();
		tableContainer.add( tab ) ;
		getTable().setWidget(row, 0, tableContainer );
		getTable().getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	protected void paintParticularyRow(IModelScript<Mod131Key> script) {
		if (script.getKeys() == null) return;
		if (script.getKeys()[0] == Mod131Key.P2) {
			paintRowP02(getCallback(),script);
		}
	}
	
	private void paintRowP02(final Model131Callback callback, IModelScript<Mod131Key> script) {
		int row = getTable().getRowCount();
		final FiscalModelDetail p2 = getModel().ensureDetail(Mod131Key.P2);
		getTable().setWidget(row, 0, new Label(script.getLabel()));
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonTextRight() );
		getTable().getFlexCellFormatter().addStyleName(row, 0,AON.AON_CSS.aonPaddingRight() );
		getTable().getFlexCellFormatter().setColSpan(row, 0, 6);
		
		final Label wP2 = new Label(p2.getAmount()==1?AON.MSG.yes():AON.MSG.no());
		getTable().setWidget(row, 1, wP2 );
		getTable().getFlexCellFormatter().setColSpan(row, 1, 2);
	}

	
	
	protected void defineTable() {
		getTable().setWidth("100%");
		getTable().addStyleName(AON.AON_CSS.aonMarginBottom());
		getTable().addStyleName(AON.AON_CSS.aonBorderCollapse());
		getTable().addStyleName(AON.AON_CSS.aonBlockCenter());
		
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
		getTable().setWidget(row, 1, new Label());
		getTable().setWidget(row, 2, new Label());
		getTable().setWidget(row, 3, new Label());
		getTable().setWidget(row, 4, new Label());
		getTable().setWidget(row, 5, new Label());
		getTable().setWidget(row, 6, new Label());
		getTable().setWidget(row, 6, new Label());
	}
		
	protected void paintEmptyRow() {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label( "." ));
		table.getFlexCellFormatter().setColSpan(row, 0, COL_NUMBER);
	}

	protected void paintRow(final Model131Callback callback, IModelScript<Mod131Key> script) {
		if (script.hasGraphicParticularity()) {
			paintParticularyRow(callback,script);
		} else {
			int row = table.getRowCount();
			paintLabel(row,script);
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
	
	protected void paintLabel( int row, IModelScript<Mod131Key> script) {
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

	private int paintField(int row, int col, final Model131Callback callback, IModelScript<Mod131Key> script, final Mod131Key key) {
		final FiscalModelDetail det1 = getModel().ensureDetail(key);
		final DoubleBox input = new DoubleBox();
		fieldsMap.put(key, input);
		input.setEnabled(getModel().isNotFinished() && script.isEnabled()); 
		input.setValue(det1.getAmount());
		input.addValueChangeHandler(event -> {
			if (event.getValue() == null) input.setValue(0.0, false);
			double result = getModel().getResultAmount(key);
			double adjust = getModel().getAdjustAmount(key);
			double amount = input.getValue();
			if (AonMathUtils.isNotZero(result - adjust - amount)) {
				getModel().ensureDetail(key).setAdjustAmount( result - amount);	
			}
			getModel().ensureDetail(key).setAmount(input.getValue());
			if (input.isEnabled()) {
				calculateAndRefresh( callback );
			}
			callback.markAsDirty();
		});
		table.setWidget(row, col, input);
		return ++col;
	}
	
	private void paintInfoCol(int row, int col, final Model131Callback callback, final IModelScript<Mod131Key> script) {
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
				
				button.addClickHandler(event -> Model131.SERVICE.getInfo(
					callback.getOptions().getOccam(),
					getModel(), script, infoKey,new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable caught) {
								callback.showError(AON.MSG.errorMessage());
							}

							@Override
							public void onSuccess(String result) {
								callback.showInfoPanel(result);
							}
					
						}
					));
				buttonContainer.add(button);
			}
			table.setWidget(row, col, buttonContainer);
		}
	}

	
	
	
	protected void paintParticularyRow(final Model131Callback callback, IModelScript<Mod131Key> script) {
		
	}

	@Override
	public void calculateAndRefresh(final Model131Callback callback) {
		Model131.SERVICE.calculate(
				callback.getOptions().getOccam(),
				getModel(),
				new AsyncCallback<Mod131>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.showError(AON.MSG.errorMessage());
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
		a.addStyleName(FiscalModelUtils.getAdministrationIconStyle(mod131.getAdministration()));
		p.add(a);
		return p;
	}
	
	public void printButtonClick() {
		if (getCallback().isDirty()) {
			new ConfirmDialog().confirm(AON.MSG.draftPrint(),AON.MSG.draftPrintNote() 
				, new ConfirmDialogCallback() {
				
				@Override
				public void onAccept() {
					submitForm(MODEL131_PRINT);
				}

				@Override
				public void onCancel() {
					// Nothing
				}
			});
		} else {
			submitForm(MODEL131_PRINT);
		}
	}
	
	protected void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod131Hidden.setValue(String.valueOf(getModel().getId()));
		domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
		domainNameHidden.setValue(getCallback().getOptions().getDomainName());
		userHidden.setValue(getCallback().getOptions().getUser());
		diskForm.submit();
	}

	protected void submitAEAT(String action) {
		submitAEAT(action, "null", "null", "null", "null", "null");
	}
	
	protected void submitAEAT(String action, String cert, String pass, String document, String name, String nrc) {
		aeatForm.setAction(GWT.getHostPageBaseURL() + action);
		modAeatHidden.setValue(String.valueOf(getModel().getId()));
		domainIdAeatHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
		domainNameAeatHidden.setValue(getCallback().getOptions().getDomainName());
		userAeatHidden.setValue(getCallback().getOptions().getUser());
		certAeatHidden.setValue(cert);
		passAeatHidden.setValue(pass);
		nameAeatHidden.setValue(name);
		documentAeatHidden.setValue(document);
		nrcAeatHidden.setValue(nrc != null ? nrc : "null");
		testHidden.setValue(getCallback().getOptions().getConfiguration().fiscal().isTestEnvironment() ? "1" : "0");
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
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBackgroundStyle(getModel().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;
		for (Pair<String, String> pair : getInformationLinks()) {
			Label icon = new Label();
			icon.addStyleName(FiscalModelUtils.getAdministrationIconStyle(getModel().getAdministration()));
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
