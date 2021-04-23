package com.esferalia.aon.gwt.fiscal.client.matrix;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;

class ModelMatrixFilterPanel extends FlowPanel implements HasValueChangeHandlers<FiscalMatrixParams>, Focusable {
	
	private ListBox year;
	private ListBox model;
	private ListBox admon;
	private CheckBox showConfigurated;
	private CheckBox showMadeModels;
	private AonSearchPanelButton refreshButton;
	
	
	protected ModelMatrixFilterPanel(AonData aonData) {
		setStyleName(AON.CSS.aonSearchPanel());
		addStyleName(AON.CSS.aonMarginLeft());
		addStyleName(AON.CSS.aonMarginRight());
		addStyleName(AON.CSS.aonBlockCenter());
		addStyleName(AON.CSS.aonMarginTop());

		InlineLabel yearLabel = new InlineLabel(AON.MSG.fiscalYear());
		yearLabel.setStyleName(AON.CSS.aonMarginRight());
		add(yearLabel);
		year = new ListBox();
		year.setStyleName(AON.CSS.aonMarginRight());
		for (int i = 2012; i < 2025; i++) {
			String y = AonNumberUtils.toString(i);
			year.addItem(y, y);
			if (i == AonDateUtils.getCurrentYear()) {
				year.setSelectedIndex(year.getItemCount() - 1);	
			}
		}
		add(year);
		year.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				fireValueChangeEvent();
			}
		});
		
		InlineLabel modelLabel = new InlineLabel(AON.MSG.fiscalModels());
		modelLabel.setStyleName(AON.CSS.aonMarginRight());
		add(modelLabel);
		model = new ListBox();
		model.setStyleName(AON.CSS.aonMarginRight());
		model.addItem(" TODOS ", "");
		for (FiscalModelType m : FiscalModelType.values()) {
			if (m != FiscalModelType.M303_RG && m != FiscalModelType.M303_RS) {
				model.addItem(AON.MSG.fiscalModelType(m),m.getValue());
			}
		}
		add(model);
		model.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				fireValueChangeEvent();
			}
		});

		InlineLabel admonLabel = new InlineLabel(AON.MSG.administration());
		admonLabel.setStyleName(AON.CSS.aonMarginRight());
		add(admonLabel);
		admon = new ListBox();
		admon.setStyleName(AON.CSS.aonMarginRight());
		admon.addItem(" TODAS ", "");
		for (Administration a : Administration.values()) {
			admon.addItem(a.getDescription());
		}
		add(admon);
		
		admon.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				fireValueChangeEvent();
			}
		});
		
		showConfigurated = new CheckBox();
		showConfigurated.setValue(false);
		showConfigurated.setStyleName(AON.CSS.aonMarginRight());
		showConfigurated.setText("Mostrar los configurados en par\u00E1metros fiscales");
		showConfigurated.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fireValueChangeEvent();
			}
		});
		add(showConfigurated);
		
		showMadeModels = new CheckBox();
		showMadeModels.setValue(true);
		showMadeModels.setStyleName(AON.CSS.aonMarginRight());
		showMadeModels.setText("Mostrar los realizados");
		showMadeModels.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fireValueChangeEvent();
			}
		});
		add(showMadeModels);
		
		refreshButton = new AonSearchPanelButton(AON.MSG.refresh(),AON.CSS.aonIconRefresh());
		refreshButton.addStyleName(AON.CSS.aonMarginRight());
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				fireValueChangeEvent();
			}
		});
		add(refreshButton);


	}

	protected void fireValueChangeEvent() {
		int y = AonNumberUtils.toint(year.getSelectedValue());
		FiscalMatrixParams params = new FiscalMatrixParams();
		Administration administration = null;
		if ( admon.getSelectedIndex() > 0) {
			administration = Administration.values()[admon.getSelectedIndex() - 1];
		}
		params.setYear(y)
			.setModel(model.getSelectedValue())
			.setAdministration(administration)
			.setConfiguredVisible(showConfigurated.getValue())
			.setMadeModelsVisible(showMadeModels.getValue())
			;
		
		ValueChangeEvent.fire(ModelMatrixFilterPanel.this, params);
	}

	@Override
	public int getTabIndex() {
		return year.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
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
		return AonNumberUtils.toint(year.getSelectedValue()); 
	}

}
