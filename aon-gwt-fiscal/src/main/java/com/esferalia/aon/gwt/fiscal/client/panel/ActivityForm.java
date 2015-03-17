package com.esferalia.aon.gwt.fiscal.client.panel;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfo;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityModule;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.IntegerStringPair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ActivityForm extends ResizeComposite {

	interface ActivityFormBinder extends
			UiBinder<Widget, ActivityForm> {
	}

	private static final ActivityFormBinder panelBinder = GWT
			.create(ActivityFormBinder.class);
	
	String domainName;
	int domainId;

	@UiField
	TextBox year;
	@UiField
	TextBox epigraph;
	@UiField
	TextBox description;
//	@UiField
//	CheckBox farmer;
	@UiField
	TextBox maxPerson;
	@UiField
	TextBox maxImport;
	@UiField
	TextBox vatPercent;
	
	@UiField
	SimplePanel infoContainer;
	@UiField
	SimplePanel moduleIRPFContainer;
	@UiField
	SimplePanel infoIRPFContainer;
	@UiField
	SimplePanel moduleIVAContainer;
	@UiField
	SimplePanel infoIVAContainer;

	public ActivityForm() {
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
	}

	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public void setFiscalActivity(FiscalActivity fiscalActivity) {
		FiscalPanel.FISCAL_SERVICE.getFiscalActivity(domainName, domainId, fiscalActivity.getId()
				,new AsyncCallback<FiscalActivity>() {

					@Override
					public void onSuccess(FiscalActivity result) {
						populate(result);
					}
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}
		});
	}
	
	private void populate(FiscalActivity fiscalActivity) {
		year.setValue(AonNumberUtils.toString(fiscalActivity.getYear()));
		epigraph.setValue(fiscalActivity.getEpigraph());
		description.setValue(fiscalActivity.getDescription());
		maxPerson.setValue(AonNumberUtils.toString(fiscalActivity.getMaxPerson()));
		maxImport.setValue(AonNumberUtils.toString(fiscalActivity.getMaxImport()));
		vatPercent.setValue(AonNumberUtils.toString(fiscalActivity.getVatPercent()));
		infoContainer.setWidget( getInfoTable( fiscalActivity.getInfo() ) );
		moduleIRPFContainer.setWidget( getModuleTable( fiscalActivity.getModuleIRPF() ) );
		infoIRPFContainer.setWidget( getInfoTable( fiscalActivity.getInfoIRPF() ) );
		moduleIVAContainer.setWidget( getModuleTable( fiscalActivity.getModuleIVA() ) );
		infoIVAContainer.setWidget( getInfoTable( fiscalActivity.getInfoIVA() ) );
	}

	private FlexTable getInfoTable(ArrayList<FiscalActivityInfo> list) {
		if (list != null && list.size() > 0) {
			FlexTable table = new FlexTable();
			table.setStyleName(FiscalPanel.AON_RESOURCES.css().aonPanelGrid());
			table.addStyleName(FiscalPanel.AON_RESOURCES.css().aonWidthAll());
			table.addStyleName(FiscalPanel.AON_RESOURCES.css().aonMarginTop());
			table.setCellSpacing(0);
			int row = 0;
			for (FiscalActivityInfo info : list) {
				Label desc = new Label(info.getInfoKey().getDescription() );
				table.setWidget(row, 0, desc);
				table.getFlexCellFormatter().setStyleName(row, 0, FiscalPanel.AON_RESOURCES.css().aonPanelGridOdd());
				table.getFlexCellFormatter().addStyleName(row, 0, FiscalPanel.AON_RESOURCES.css().aonWidthAuto());
				if (info.getInfoKey().isChoice()) {
					ListBox listBox = new ListBox();
					int i = 0;
					for (IntegerStringPair pair : info.getInfoKey().getOptions() ) {
						String value = pair.getKey().toString();
						listBox.addItem(pair.getValue(), value);
						if (AonStringUtils.equals(value, info.getValue())) {
							listBox.setSelectedIndex(i);
						}
						i++;
					}
					table.setWidget(row, 1, listBox);
				} else {
					TextBox text = new TextBox();
					text.setWidth("100px");
					text.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							
						}
					});
					text.setValue(info.getValue());
					text.setStyleName(FiscalPanel.AON_RESOURCES.css().aonInputText());
					table.setWidget(row, 1, text);
				}
				table.getFlexCellFormatter().setStyleName(row,1, FiscalPanel.AON_RESOURCES.css().aonPanelGridEven());
				++row;
			}
			return table;
		}
		return null;
	}

	private FlexTable getModuleTable(ArrayList<FiscalActivityModule> list) {
		if (list != null && list.size() > 0) {
			FlexTable table = new FlexTable();
			table.setStyleName(FiscalPanel.AON_RESOURCES.css().aonPanelGrid());
			table.addStyleName(FiscalPanel.AON_RESOURCES.css().aonWidthAll());
			table.addStyleName(FiscalPanel.AON_RESOURCES.css().aonMarginTop());
			table.setCellSpacing(0);
			int row = 0;
			for (FiscalActivityModule info : list) {
				Label desc = new Label(info.getInfoKey().getDescription() );
				table.setWidget(row, 0, desc);
				table.getFlexCellFormatter().setStyleName(row, 0, FiscalPanel.AON_RESOURCES.css().aonPanelGridOdd());
				table.getFlexCellFormatter().addStyleName(row, 0, FiscalPanel.AON_RESOURCES.css().aonWidthAuto());
				table.getFlexCellFormatter().setStyleName(row, 1, FiscalPanel.AON_RESOURCES.css().aonPanelGridEven());
				table.getFlexCellFormatter().addStyleName(row, 1, FiscalPanel.AON_RESOURCES.css().aonTextRight());
				table.getFlexCellFormatter().setStyleName(row, 2, FiscalPanel.AON_RESOURCES.css().aonPanelGridEven());
				table.getFlexCellFormatter().setStyleName(row, 3, FiscalPanel.AON_RESOURCES.css().aonPanelGridEven());
				table.getFlexCellFormatter().addStyleName(row, 3, FiscalPanel.AON_RESOURCES.css().aonTextRight());
				table.getFlexCellFormatter().setStyleName(row, 4, FiscalPanel.AON_RESOURCES.css().aonPanelGridEven());
				table.getFlexCellFormatter().addStyleName(row, 4, FiscalPanel.AON_RESOURCES.css().aonTextRight());
				table.getFlexCellFormatter().addStyleName(row, 4, FiscalPanel.AON_RESOURCES.css().aonBold());

				TextBox text = new TextBox();
				text.setWidth("100px");
				text.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						
					}
				});
				text.setValue(info.getValue());
				text.setStyleName(FiscalPanel.AON_RESOURCES.css().aonInputText());
				table.setWidget(row, 1, text);
				
				Label unit = new Label(info.getUnit() );
				table.setWidget(row, 2, unit);
				
				Label factor = new Label( FiscalPanel.FMT.format( info.getFactor()) );
				table.setWidget(row, 3, factor);
				
				Label base = new Label(FiscalPanel.FMT.format( info.getBase()));
				table.setWidget(row, 4, base);

				++row;
			}
			return table;
		}
		return null;
	}

}
