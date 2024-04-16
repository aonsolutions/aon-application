package com.esferalia.aon.gwt.fiscal.client.mod131;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod131.Model131.Model131Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

abstract class Model131AEAT extends Model131Base {
	
	private AonTextBox receiptBox;
	private FiscalModelAdmonPanel<Mod131, Model131ModuleOptions> admonPanel;
	
	Model131AEAT(Mod131 mod131, Model131Callback callback) {
		super(mod131, callback);
	}
	
	@Override
	protected void paintParticularyRow(FlexTable table, final Model131Callback callback, IModelScript<Mod131Key> script) {
		if (script.getKeys() == null) return;
		if (script.getKeys()[0] == Mod131Key.P2) {
			paintRowP02(table,script);
		} 
	}
	
	private void paintRowP02(FlexTable table, IModelScript<Mod131Key> script) {
		int row = table.getRowCount();
		final FiscalModelDetail p2 = getModel().ensureDetail(Mod131Key.P2);
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonTextRight() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingRight() );
		table.getFlexCellFormatter().setColSpan(row, 0, 6);
		
		final Label wP2 = new Label(p2.getAmount()==1?AON.MSG.yes():AON.MSG.no());
		table.setWidget(row, 1, wP2 );
		table.getFlexCellFormatter().setColSpan(row, 1, 2);
	}
	
	@Override
	protected void paintDeclarationTab(TabLayoutPanel tabPanel) {
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
		IFiscalModelAdmonPanelCallback<Mod131, Model131ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod131, Model131ModuleOptions>() {

				@Override
				public Model131ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod131 getModel() {
					return Model131AEAT.this.getModel();
				}

				@Override
				public void showError(String msg) {
					getCallback().showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Mod131ValidatePrintAEAT";
					
				}

				@Override
				public String getDownloadFileAction() {
					return Model131Base.MODEL131_FILE;
				}

				@Override
				public String getSendAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod131SendAEAT";
				}

				@Override
				public void sendSuccessfully() {
					Model131.SERVICE.get(getCallback().getOptions().getOccam(), 
							getModel().getId(), new AsyncCallback<Mod131>() {
						@Override
						public void onSuccess(Mod131 selected) {
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
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod131CheckAEAT";
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod131CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://sede.agenciatributaria.gob.es/Sede/procedimientoini/G602.shtml";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
	@Override
	protected void decorateDeclarationTab() {
		if (receiptBox != null) {
			receiptBox.setValue( getModel().getNumber() );
		}
	}
	
	@Override
	protected void decorateAdministrationTab() {
		if (admonPanel != null) {
			admonPanel.manageLinks();
		}
	}
}
