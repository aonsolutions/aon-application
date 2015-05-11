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
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model111Form extends ResizeComposite implements IFiscalTreeContent<Mod111>{

	interface Mod111FormBinder extends UiBinder<Widget, Model111Form> {
	}

	private static final Mod111FormBinder panelBinder = GWT.create(Mod111FormBinder.class);
	
	Mod111 mod111;
	ITreeNodeCallback<Mod111> callback;
	 
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
	
	@UiField
	Button printButton;

	public Model111Form() {
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
		
		for (Period per : Period.values()) {
			period.addItem(per.getName(),Integer.toString( per.ordinal() ));	
		}
		period.setEnabled(false);
	}
	
	@Override
	public void setCallback(ITreeNodeCallback<Mod111> callback) {
		this.callback = callback;
	}

	@Override
	public void select(Mod111 m111) {
		this.mod111 = m111;
		if ( mod111.getId() != null) {
			FiscalTree.FISCAL_SERVICE.getFiscalModel(FiscalTree.getCurrentDomainName(), 
					FiscalTree.getCurrentDomain(), mod111.getId()
					,new AsyncCallback<FiscalModel>() {

						@Override
						public void onSuccess(FiscalModel result) {
							Mod111 mod111 = new Mod111();
							FiscalModel.map(result, mod111);
							populate(mod111);
						}
						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(caught.getMessage());
						}
			});
		} else {
			populate(m111);
		}
	}

	private void populate(Mod111 fm) {
		this.mod111 = fm;
		
		year.setText( Integer.toString( mod111.getYear() ));
		period.setItemSelected(mod111.getPeriod().ordinal(),true);
		
		document.setValue(mod111.getDocument());
		name.setValue(mod111.getName());
		surname.setValue(mod111.getSurname());

		document.setEnabled(false);
		name.setEnabled(false);
		surname.setEnabled(false);

		status.setText(mod111.isFinished()?AON.MSG.finished():AON.MSG.pending() );
		status.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		status.addStyleName(mod111.isFinished()?AON.AON_CSS.aonIconLock():AON.AON_CSS.aonIconUnlock()) ;
		paintHeaderTable(mod111);
		paintTable(mod111);
	}
	
	private void paintHeaderTable(final Mod111 fm) {
		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		FlexTable headerTable = new FlexTable();
		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(TreeNode.getAdministrationImage(mod111.getAdministration()));
		
		headerTable.setWidget(0, 0, image);
		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		
		headerTable.setWidget(0, 1, new Label( AON.MSG.fiscalModelDescriptionlong(fm.getModel())));
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, TreeNode.getAdministrationBG(mod111.getAdministration()));

		headerTable.setWidget(0, 2, new Label(fm.getModel().getName()));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, TreeNode.getAdministrationBG(mod111.getAdministration()));
		headerPanel.setWidget(headerTable);
	}
	
	protected void paintTable(final FiscalModel fm) {
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

		for (FiscalModelDetail detail : mod111.getMap().values() ) {
			row++;
			Mod111Key key = Mod111Key.getKey(detail.getType()); 
			String description = key == null?detail.getType():key.getDescription();
			table.setWidget(row, 0, new Label( description ));
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
	
	@UiHandler("printButton")
	void onPrintButtonClick(ClickEvent event) {
		requestFullscreen();
	}
	
	
    private native void requestFullscreen()
	/*-{
	    if(document.requestFullscreen) {
	        document.requestFullscreen();
	    } else if(document.mozRequestFullScreen) {
	        document.mozRequestFullScreen();
	    } else if(document.webkitRequestFullscreen) {
	        document.webkitRequestFullscreen();
	    } else if(document.msRequestFullscreen) {
	        document.msRequestFullscreen();
	    }
	}-*/;
}
