package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.PercentBox;
import com.esferalia.aon.gwt.common.client.widget.PeriodListBox;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.FiscalNodeWidget;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131.Mod131Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model131Form extends ResizeComposite implements FiscalNodeWidget<Mod131>{

	interface Model131FormBinder extends
			UiBinder<Widget, Model131Form> {
	}

	private static final Model131FormBinder panelBinder = GWT.create(Model131FormBinder.class);
	
	TreeNode<Mod131> node;
	 
	@UiField
	Button saveButton;
	@UiField
	Button calculateButton;
	
	@UiField
	TextBox document;
	
	@UiField
	TextBox name;
	
	@UiField
	TextBox surname;
	
	@UiField
	PeriodListBox period;
	
	@UiField
	IntegerBox year;
	
	@UiField
	SimplePanel activitiesTablePanel;

	public Model131Form() {
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	@Override
	public void select(TreeNode<Mod131> node) {
		this.node = node;
		if ( node.getTreeObject().getId() != null) {
			FiscalTree.FISCAL_SERVICE.getMod131(FiscalTree.getCurrentDomainName(), 
					FiscalTree.getCurrentDomain(), node.getTreeObject().getId()
					,new AsyncCallback<Mod131>() {

						@Override
						public void onSuccess(Mod131 result) {
							populate(result);
						}
						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(caught.getMessage());
						}
			});
		} else {
			populate(node.getTreeObject());
		}
	}

	private void populate(Mod131 mod131) {
		node.setTreeObject( mod131 );
		saveButton.setEnabled(isEnabled(mod131));
		calculateButton.setEnabled(isEnabled(mod131));
		
		document.setValue(mod131.getDocument());
		name.setValue(mod131.getName());
		surname.setValue(mod131.getSurname());
		period.setValue(mod131.getPeriod());
		year.setValue(mod131.getYear());
		
		paintActivitiesTables(mod131);
		
	}
	

	private void paintActivitiesTables(Mod131 mod131) {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonFiscalModelDataTable());
		table.setCellSpacing(0);
		int row = 0;
		table.setWidget(row, 0, new Label(AON.MSG.mod131Activities() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().setColSpan(row, 0, 5);
		row++;
		table.setWidget(row, 0, new Label(AON.MSG.epigraph() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableHeader());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonWidthAuto());
		table.getFlexCellFormatter().setColSpan(row, 0, 2);
		table.setWidget(row, 1, new Label(AON.MSG.netYield()));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableHeader());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonWidth150());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
		table.setWidget(row, 2, new Label(AON.MSG.appliedPercent()));
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableHeader());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonWidth80());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextRight());
		table.getFlexCellFormatter().setColSpan(row, 2, 2);
		
		table.setWidget(row, 3, new Label(AON.MSG.result()));
		table.getFlexCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonFiscalModelDataTableHeader());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonWidth150());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextRight());
		for (Mod131Activity act : mod131.getActivities() ) {
			row++;
			table.setWidget(row, 0, new Label(act.getEpigraph() ));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().setColSpan(row, 0, 2);
			
			DoubleBox netYield = new DoubleBox();
			netYield.setValue(act.getNetYield());
			table.setWidget(row, 1, netYield );
			table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
			
			PercentBox percent = new PercentBox();
			netYield.setValue(act.getPercent());
			table.setWidget(row, 2, percent );
			table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableData());
			table.getFlexCellFormatter().setColSpan(row, 2, 2);

			DoubleBox result = new DoubleBox();
			netYield.setValue(act.getResult());
			table.setWidget(row, 3, result );
			table.getFlexCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonFiscalModelDataTableData());
		}
		row++;
		table.setWidget(row, 0, new Label( AON.MSG.netYieldSum()));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
		
		table.setWidget(row, 1, new Label( "01" ));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableBox());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonWidth30());
		
		DoubleBox netYieldSum = new DoubleBox();
		netYieldSum.setValue(mod131.getC01());
		table.setWidget(row, 2, netYieldSum );
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableData());
		
		table.setWidget(row, 3, new Label( "" ));
		table.getFlexCellFormatter().setColSpan(row, 3, 3);
		
		row++;
		table.setWidget(row, 0, new Label( AON.MSG.mod131ResultSum()));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
		table.getFlexCellFormatter().setColSpan(row, 0, 4);
		
		table.setWidget(row, 1, new Label( "02" ));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableBox());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonWidth30());

		DoubleBox resultSum = new DoubleBox();
		netYieldSum.setValue(mod131.getC02());
		table.setWidget(row, 2, resultSum );
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonFiscalModelDataTableData());
		
		activitiesTablePanel.setWidget(table);		
	}

	private boolean isEnabled(Mod131 mod131) {
		return ( mod131.getYear() >= 2015 );
	}


	@UiHandler("calculateButton")
	void onCalculateButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.calculateAction())) {
			Window.alert("CALCULATE");
		}
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		if (Window.confirm( AON.MSG.saveAction())) {
			Window.alert("SAVE");
		}
	}

}
