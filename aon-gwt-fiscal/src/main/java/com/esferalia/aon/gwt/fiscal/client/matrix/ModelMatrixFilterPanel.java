package com.esferalia.aon.gwt.fiscal.client.matrix;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;

class ModelMatrixFilterPanel extends AonDisplayTable implements HasValueChangeHandlers<FiscalMatrixParams>, Focusable {
	
	private static final Logger LOGGER = Logger.getLogger(ModelMatrixFilterPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static CommonServiceAsync COMMON_SERVICE;

	private ListBox year;
	private ListBox model;
	private ListBox admon;
	private ListBox scopeBox;
	private CheckBox showConfigurated;
	private CheckBox showMadeModels;
	private AonTextBox declared;
	private AonSearchPanelButton refreshButton;
	private AonSearchPanelButton configButton;

	
	protected ModelMatrixFilterPanel(MatrixModuleOptions options) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		COMMON_SERVICE.getAonConfiguration(options.getDomainName(), options.getDomain(), options.getUser(),
				new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration result) {
				options.setConfiguration(result);
				load(options);
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("No se puede cargar la página [Interno: " + caught.getMessage()+ "]");
			}
		});
	}

	
	protected void load(MatrixModuleOptions options) {
		if (!options.isCompactMode()) {
			addStyleName(AON.CSS.aonSearchPanel());
			addStyleName(AON.CSS.aonMarginTop());
			addStyleName(AON.CSS.aonWidthAlmostAll());
			addStyleName(AON.CSS.aonMarginLeft());
			addStyleName(AON.CSS.aonMarginRight());
		}
		addStyleName(AON.CSS.aonBlockCenter());

		InlineLabel yearLabel = new InlineLabel(AON.MSG.fiscalYear());
		yearLabel.setStyleName(AON.CSS.aonMarginRight());
		year = new ListBox();
		year.setStyleName(AON.CSS.aonMarginRight());
		for (int i = 2012; i < 2025; i++) {
			String y = AonNumberUtils.toString(i);
			year.addItem(y, y);
			if (i == AonDateUtils.getCurrentYear()) {
				year.setSelectedIndex(year.getItemCount() - 1);	
			}
		}
		year.addChangeHandler(event -> fireValueChangeEvent());
		
		InlineLabel modelLabel = new InlineLabel(AON.MSG.fiscalModels());
		modelLabel.setStyleName(AON.CSS.aonMarginRight());
		model = new ListBox();
		model.setStyleName(AON.CSS.aonMarginRight());
		model.addItem(" TODOS ", "");
		for (FiscalModelType m : FiscalModelType.values()) {
			if (m != FiscalModelType.M303_RG && m != FiscalModelType.M303_RS && m != FiscalModelType.M310 && m != FiscalModelType.M311 && m != FiscalModelType.M340 ) {
				model.addItem(AON.MSG.fiscalModelType(m), m.toString());
			}
		}
		model.addChangeHandler(event -> fireValueChangeEvent());

		InlineLabel declaredLabel = new InlineLabel(AON.MSG.declared());
		declaredLabel.setStyleName(AON.CSS.aonMarginRight());
		declared = new AonTextBox();
		declared.setStyleName(AON.CSS.aonMarginRight());
		declared.addValueChangeHandler(event -> fireValueChangeEvent());

		InlineLabel admonLabel = new InlineLabel(AON.MSG.administration());
		admonLabel.setStyleName(AON.CSS.aonMarginRight());

		admon = new ListBox();
		admon.setStyleName(AON.CSS.aonMarginRight());
		admon.addItem(" TODAS ", "");
		for (Administration a : Administration.values()) {
			admon.addItem(a.getDescription());
		}
		admon.addChangeHandler(event -> fireValueChangeEvent());
		
		
		InlineLabel scopeLabel = new InlineLabel(AON.MSG.scope());
		scopeBox = new ListBox();
		boolean showScopes = (options != null && options.getConfiguration() != null && options.getConfiguration().hasAvailableScopes());
		if (showScopes) {
			scopeLabel.setStyleName(AON.CSS.aonMarginRight());
			scopeBox.setStyleName(AON.CSS.aonMarginRight());
			scopeBox.addItem(" TODOS ", "");
			for (Scope scope : options.getConfiguration().getAvailableScopes()) {
				scopeBox.addItem(scope.getDescription(),AonNumberUtils.toString(scope.getId()));
			}
			scopeBox.addChangeHandler(event -> fireValueChangeEvent());
		}
		
		

		showConfigurated = new CheckBox();
		showConfigurated.setValue(false);
		showConfigurated.setStyleName(AON.CSS.aonMarginRight());
		showConfigurated.setText("Mostrar los configurados en par\u00E1metros fiscales");
		showConfigurated.addClickHandler( event -> fireValueChangeEvent());
		
		showMadeModels = new CheckBox();
		showMadeModels.setValue(true);
		showMadeModels.setStyleName(AON.CSS.aonMarginRight());
		showMadeModels.setText("Mostrar los realizados");
		showMadeModels.addClickHandler( event -> fireValueChangeEvent());
		
		refreshButton = new AonSearchPanelButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refreshButton.addStyleName(AON.CSS.aonMarginRight());
		refreshButton.addClickHandler(event -> fireValueChangeEvent());

		configButton = new AonSearchPanelButton(AON.MSG.settings(),AON.CSS.aonIconSettings());
		configButton.addStyleName(AON.CSS.aonMarginRight());
		configButton.addClickHandler(event -> showConfigurationPanel());
		
		if (!options.isCompactMode()) {
			addRow()
				.addCell(yearLabel,AON.CSS.aonTableLabel())
				.addCell(year)
				.addCell(modelLabel,AON.CSS.aonTableLabel())
				.addCell(model)
				.addCell(showConfigurated)
				.addCell(declaredLabel)
				.addCell(declared)
				.addCell(new InlineLabel(),AON.CSS.aonFlexGrow1())
				;
			
			addRow()
				.addCell(admonLabel,AON.CSS.aonTableLabel())
				.addCell(admon)
				.addCell(showScopes?scopeLabel:new InlineLabel(),AON.CSS.aonTableLabel())
				.addCell(showScopes?scopeBox:new InlineLabel())
				.addCell(showMadeModels)
				.addCell(refreshButton)
				.addCell(new InlineLabel())
				.addCell(new InlineLabel(),AON.CSS.aonFlexGrow1())
			;
		} else {
			showConfigurated.setValue(true);
			addRow()
				.addCell(yearLabel,AON.CSS.aonTableLabel())
				.addCell(year)
				.addCell(new InlineLabel())
				.addCell(showConfigurated,AON.CSS.aonFlexGrow1())
				;
		}
		
		fireValueChangeEvent();
	}

	protected void fireValueChangeEvent() {
		int y = AonNumberUtils.toint(year.getSelectedValue());
		FiscalMatrixParams params = new FiscalMatrixParams();
		Administration administration = null;
		if ( admon.getSelectedIndex() > 0) {
			administration = Administration.values()[admon.getSelectedIndex() - 1];
		}
		FiscalModelType modelType = null;
		if ( model.getSelectedIndex() > 0) {
			modelType = FiscalModelType.valueOf(model.getSelectedValue());
		}
		LOGGER.info("Scope ..: " + scopeBox.getSelectedValue()); 
		Integer scope = AonNumberUtils.toInteger(scopeBox.getSelectedValue());
		params.setYear(y)
			.setModel(modelType)
			.setAdministration(administration)
			.setScope(scope)
			.setConfiguredVisible(showConfigurated.getValue())
			.setMadeModelsVisible(showMadeModels.getValue())
			.setDeclared( declared.getValue() )
			;

		ValueChangeEvent.fire(ModelMatrixFilterPanel.this, params);
	}

	@Override
	public int getTabIndex() {
		return year.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		// Nothing
	}

	@Override
	public void setFocus(boolean focused) {
		year.setFocus(focused);
	}

	@Override
	public void setTabIndex(int index) {
		year.setTabIndex(index);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FiscalMatrixParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public int getSelectedYear() {
		return AonNumberUtils.toint(year.getSelectedItemText()); 
	}

	private void showConfigurationPanel() {
		// TODO Auto-generated method stub
		
	}

}
