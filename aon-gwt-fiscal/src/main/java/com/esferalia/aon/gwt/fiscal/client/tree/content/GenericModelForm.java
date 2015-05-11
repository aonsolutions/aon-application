package com.esferalia.aon.gwt.fiscal.client.tree.content;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.IFiscalTreeContent;
import com.esferalia.aon.gwt.fiscal.client.tree.ITreeNodeCallback;
import com.esferalia.aon.gwt.fiscal.client.tree.node.TreeNode;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.core.client.GWT;
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

public class GenericModelForm extends ResizeComposite implements IFiscalTreeContent<FiscalModel>{

	interface GenericModelFormBinder extends
			UiBinder<Widget, GenericModelForm> {
	}

	private static final GenericModelFormBinder panelBinder = GWT.create(GenericModelFormBinder.class);
	
	FiscalModel fiscalModel;
	ITreeNodeCallback<FiscalModel> callback;
	 
	@UiField
	TextBox document;
	
	@UiField
	TextBox name;
	
	@UiField
	TextBox surname;
	
	@UiField
	Label status;

	@UiField
	ListBox period;
	
	@UiField
	Label year;
	
	@UiField
	SimplePanel headerPanel;

	@UiField
	SimplePanel tablePanel;
	
	public GenericModelForm() {
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
		
		for (Period per : Period.values()) {
			period.addItem(per.getName(),Integer.toString( per.ordinal() ));	
		}
		period.setEnabled(false);
	}
	
	@Override
	public void setCallback(ITreeNodeCallback<FiscalModel> callback) {
		this.callback = callback;
	}

	@Override
	public void select(FiscalModel fm) {
		this.fiscalModel = fm;
		if ( fiscalModel.getId() != null) {
			FiscalTree.FISCAL_SERVICE.getFiscalModel(FiscalTree.getCurrentDomainName(), 
					FiscalTree.getCurrentDomain(), fiscalModel.getId()
					,new AsyncCallback<FiscalModel>() {

						@Override
						public void onSuccess(FiscalModel result) {
							populate(result);
						}
						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(caught.getMessage());
						}
			});
		} else {
			populate(fiscalModel);
		}
	}

	private void populate(FiscalModel fm) {
		this.fiscalModel = fm;
		
		year.setText( Integer.toString( fiscalModel.getYear() ));
		period.setItemSelected(fiscalModel.getPeriod().ordinal(),true);
		
		document.setValue(fiscalModel.getDocument());
		name.setValue(fiscalModel.getName());
		surname.setValue(fiscalModel.getSurname());

		document.setEnabled(false);
		name.setEnabled(false);
		surname.setEnabled(false);

		status.setText(fiscalModel.isFinished()?AON.MSG.finished():AON.MSG.pending() );
		status.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		status.addStyleName(fiscalModel.isFinished()?AON.AON_CSS.aonIconLock():AON.AON_CSS.aonIconUnlock()) ;
		paintHeaderTable(fiscalModel);
		paintTable(fiscalModel);
	}
	
	private void paintHeaderTable(final FiscalModel fm) {
		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		FlexTable headerTable = new FlexTable();
		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(TreeNode.getAdministrationImage(fiscalModel.getAdministration()));
		
		headerTable.setWidget(0, 0, image);
		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		
		headerTable.setWidget(0, 1, new Label( AON.MSG.fiscalModelDescriptionlong(fm.getModel())));
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, TreeNode.getAdministrationBG(fiscalModel.getAdministration()));

		headerTable.setWidget(0, 2, new Label(fm.getModel().getName()));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, TreeNode.getAdministrationBG(fiscalModel.getAdministration()));
		headerPanel.setWidget(headerTable);
	}
	
	private void paintTable(final FiscalModel fm) {
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonFiscalModelDataTable());
		table.setCellSpacing(0);
		int row = 0;
		table.setWidget(row, 0, new Label(AON.MSG.description() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableHeader());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonWidthAuto());
		table.setWidget(row, 1, new Label(AON.MSG.amount()));
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableHeader());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonWidth150());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
		
		row++;
		table.setWidget(row, 0, new Label( AON.MSG.liquidacion() ));
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableTitle());
		table.getFlexCellFormatter().setColSpan(row, 0, 2);

		for (FiscalModelDetail detail : fiscalModel.getMap().values() ) {
			row++;
			table.setWidget(row, 0, new Label( detail.getType() + " " + detail.getDescription()));
			table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonFiscalModelDataTableDesc());
			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());

			DoubleBox doubleBox = new DoubleBox();
			doubleBox.setValue(detail.getAmount());
			doubleBox.setEnabled(false);
			table.setWidget(row, 1, doubleBox );
			table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonFiscalModelDataTableData());
		}
		tablePanel.setWidget(table);
	}
	
}
