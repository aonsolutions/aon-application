package com.esferalia.aon.gwt.fiscal.client.mod130;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod130.Model130.Model130Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model130AEAT extends Model130Base {

	private AonTextBox receiptBox;
	
	public Model130AEAT(Mod130 mod130,Model130Callback callback) {
		super(mod130, callback);
	}
	
	@Override
	protected void paintParticularyRow(FlexTable table, final Model130Callback callback, IModelScript<Mod130Key> script) {
		if (script.getKeys() == null) return;
		if (script.getKeys()[0] == Mod130Key.P0) {
			paintRowP00(table,script);
		} else if (script.getKeys()[0] == Mod130Key.P1) {
			paintRowP01(table,script);
		} else if (script.getKeys()[0] == Mod130Key.P2) {
			paintRowP02(table,script);
		} 
	}
	
	private void paintRowP00(FlexTable table, IModelScript<Mod130Key> script) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonTextRight() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingRight() );
		table.setWidget(row, 1, new Label());
		final FiscalModelDetail p1 = getModel().ensureDetail(Mod130Key.P0);
		String labelText = IRPFRegime.NORMAL.getDescription();
		if (p1 != null && p1.getAmount() == 1) {
			labelText = IRPFRegime.SIMPLIFIED.getDescription();	
		}
		table.setWidget(row, 2, new Label( labelText ));
	}

	private void paintRowP01(FlexTable table, IModelScript<Mod130Key> script) {
		int row = table.getRowCount();
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonTextRight() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingRight() );
		table.setWidget(row, 1, new Label());
		final FiscalModelDetail p1 = getModel().ensureDetail(Mod130Key.P1);
		table.setWidget(row, 2, new Label( AON.FMT.format(p1.getAmount()) + "%"));
	}

	private void paintRowP02(FlexTable table, IModelScript<Mod130Key> script) {
		int row = table.getRowCount();
		final FiscalModelDetail p2 = getModel().ensureDetail(Mod130Key.P2);
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonTextRight() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingRight() );
		table.setWidget(row, 1, new Label());
		final Label wP2 = new Label(p2.getAmount()==1?AON.MSG.yes():AON.MSG.no());
		table.setWidget(row, 2, wP2 );
	}
	
	@Override
	void paintDeclarationTab(TabLayoutPanel tabPanel) {
		ScrollPanel declarationScrollPanel = new ScrollPanel();
		FlowPanel container = new FlowPanel();
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonBlockCenter());
		
		receiptBox = new AonTextBox();
		receiptBox.setVisibleLength(15);
		receiptBox.setMaxLength(13);
		receiptBox.setValue( getModel().getNumber() );
		receiptBox.addValueChangeHandler( event -> {
			getModel().setNumber(receiptBox.getValue());
			markAsDirty();
		});

		table.addRow()
			.addCell( new Label(AON.MSG.receipt()), AON.CSS.aonTableLabel())
			.addCell(receiptBox);
		
		// Complementaria: Numero justificante de la declaración anterior
		if (getModel().isComplementary()) {
			final AonTextBox previousReceiptBox = new AonTextBox();
			previousReceiptBox.setVisibleLength(15);
			previousReceiptBox.setMaxLength(13);
			previousReceiptBox.setValue( getModel().getReplacedNumber() );
			previousReceiptBox.addValueChangeHandler( event -> {
				getModel().setReplacedNumber(previousReceiptBox.getValue());
				markAsDirty();
			});
			table.addRow()
				.addCell( new Label(AON.MSG.previousReceipt()), AON.CSS.aonTableLabel())
				.addCell(previousReceiptBox);
		}	
		container.add(table);
		declarationScrollPanel.setWidget(container);
		tabPanel.add(declarationScrollPanel, AON.MSG.declaration());
	}

	@Override
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod130, Model130ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod130, Model130ModuleOptions>() {

				@Override
				public Model130ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod130 getModel() {
					return Model130AEAT.this.getModel();
				}

				@Override
				public void showError(String msg) {
					getCallback().showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Mod130ValidatePrintAEAT";
					
				}

				@Override
				public String getDownloadFileAction() {
					return Model130Base.MODEL130_FILE;
				}

				@Override
				public String getSendAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod130SendAEAT";
				}

				@Override
				public void sendSuccessfully() {
					Model130.SERVICE.getMod130(getCallback().getOptions().getOccam(), 
							getModel().getId(), new AsyncCallback<Mod130>() {
						@Override
						public void onSuccess(Mod130 selected) {
							selectAndPopulate(selected);
						}
						@Override
						public void onFailure(Throwable caught) {
							getCallback().showError(AON.MSG.unableToReadDeclaration(caught.getMessage()));
						}
					});
				}

				@Override
				public String getCheckAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod130CheckAEAT";
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod130CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/G601.shtml";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
	@Override
	void decorateDeclarationTab() {
		if (receiptBox != null) {
			receiptBox.setValue( getModel().getNumber() );
		}
	}
	
}
