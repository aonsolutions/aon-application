package com.esferalia.aon.gwt.fiscal.client.panel;

import java.util.ArrayList;

import com.esferalia.aon.gwt.fiscal.client.panel.EpigraphSelectionPanel.SelectionCallBack;
import com.esferalia.aon.gwt.fiscal.client.panel.FiscalPanel.FiscalNodeWidget;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfo;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.IntegerStringPair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ActivityForm extends ResizeComposite implements FiscalNodeWidget<FiscalActivity>{

	interface ActivityFormBinder extends
			UiBinder<Widget, ActivityForm> {
	}

	private static final ActivityFormBinder panelBinder = GWT
			.create(ActivityFormBinder.class);
	
	TreeItem node;
	FiscalActivity fiscalActivity;
	EpigraphSelectionPanel epigraphSelection; 
	
	
	@UiField
	Button saveButton;
	
	@UiField
	Button epigraphButton;
	
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
	TabPanel tab;
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
		epigraphSelection = new EpigraphSelectionPanel(new SelectionCallBack() {
			
			@Override
			public void onSelect(Epigraph epigraph) {
				select( epigraph );
				
				FiscalPanel.FISCAL_SERVICE.getFiscalActivityFor(epigraph,fiscalActivity.getYear()
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
			
			@Override
			public void onClose() {
			}
		});
		
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
	}
	@Override
	public void select(TreeItem node, FiscalActivity fiscalActivity) {
		this.node = node;
		this.fiscalActivity = fiscalActivity;
		if (fiscalActivity.getId() != null) {
			FiscalPanel.FISCAL_SERVICE.getFiscalActivity(FiscalPanel.getCurrentDomainName(), 
					FiscalPanel.getCurrentDomain(), fiscalActivity.getId()
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
		} else {
			populate(fiscalActivity);
			onMainActivityClick(null);
		}
	}
	
	protected void select(Epigraph epi) {
		if (this.node.getWidget() != null && this.node.getWidget() instanceof Label) {
			Label label = (Label) this.node.getWidget();
			label.setText(epi.getEpigraph() + " - " + AonStringUtils.abbreviate(epi.getDescription(), 40));
		}
		epigraph.setValue(epi.getEpigraph());
		description.setValue(epi.getDescription());
		maxPerson.setValue(AonNumberUtils.toString(epi.getLimPers()));
		maxImport.setValue(AonNumberUtils.toString(epi.getLimExceso()));
		vatPercent.setValue(AonNumberUtils.toString(epi.getPorcMin()));
		
		// TODO fill lines!!!!
		
	}

	private void populate(FiscalActivity fiscalActivity) {
		year.setValue(AonNumberUtils.toString(fiscalActivity.getYear()));
		epigraph.setValue(fiscalActivity.getEpigraph());
		if (AonStringUtils.isNotBlank(fiscalActivity.getEpigraph())) {
			description.setValue(fiscalActivity.getDescription());
			maxPerson.setValue(AonNumberUtils.toString(fiscalActivity.getMaxPerson()));
			maxImport.setValue(AonNumberUtils.toString(fiscalActivity.getMaxImport()));
			vatPercent.setValue(AonNumberUtils.toString(fiscalActivity.getVatPercent()));
		} else {
			description.setValue(null);
			maxPerson.setValue(null);
			maxImport.setValue(null);
			vatPercent.setValue(null);
		}
		tab.setVisible(fiscalActivity.hasInfoOrModules());
		if ( tab.isVisible() ) {
			if ( fiscalActivity.hasInfo() ) {
				infoContainer.setWidget( getInfoTable(fiscalActivity,fiscalActivity.getInfo() ) );
			}
			if ( fiscalActivity.hasModuleIRPF() ) {
				moduleIRPFContainer.setWidget( getModuleTable(fiscalActivity,fiscalActivity.getModuleIRPF() ) );
			}
			if ( fiscalActivity.hasInfoIRPF() ) {
				infoIRPFContainer.setWidget( getInfoTable(fiscalActivity,fiscalActivity.getInfoIRPF() ) );
			}
			if ( fiscalActivity.hasModuleIVA() ) {
				moduleIVAContainer.setWidget( getModuleTable(fiscalActivity,fiscalActivity.getModuleIVA() ) );
			}
			if ( fiscalActivity.hasInfoIVA() ) {
				infoIVAContainer.setWidget( getInfoTable(fiscalActivity,fiscalActivity.getInfoIVA() ) );
			}
			tab.selectTab(0);
		}
		year.setReadOnly(true);
		epigraph.setEnabled(isEnabled(fiscalActivity));
		description.setReadOnly(true);
		maxPerson.setReadOnly(true);
		maxImport.setReadOnly(true);
		vatPercent.setReadOnly(true);
	}
	

	private boolean isEnabled(FiscalActivity fiscalActivity) {
		return ( fiscalActivity.getYear() >= 2015 );
		
	}

	private FlexTable getInfoTable(FiscalActivity fiscalActivity,ArrayList<FiscalActivityInfo> list) {
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
					listBox.setEnabled(isEnabled(fiscalActivity));
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
					text.setEnabled(isEnabled(fiscalActivity));
					table.setWidget(row, 1, text);
				}
				table.getFlexCellFormatter().setStyleName(row,1, FiscalPanel.AON_RESOURCES.css().aonPanelGridEven());
				++row;
			}
			return table;
		}
		return null;
	}

	private FlexTable getModuleTable(FiscalActivity fiscalActivity,ArrayList<FiscalActivityModule> list) {
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
				text.setEnabled(isEnabled(fiscalActivity));
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
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		Window.alert("Save");
	}
	
	@UiHandler("epigraphButton")
	void onMainActivityClick(ClickEvent event) {
		epigraphSelection.center();
		epigraphSelection.show();
	}

}
