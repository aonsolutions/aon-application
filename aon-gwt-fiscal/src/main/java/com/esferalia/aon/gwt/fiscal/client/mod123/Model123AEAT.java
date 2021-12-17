package com.esferalia.aon.gwt.fiscal.client.mod123;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod123.Model123.Model123Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model123AEAT extends Model123Base {

	private AonTextBox receiptBox;

	public Model123AEAT(Mod123 mod123, Model123Callback callback) {
		super(mod123,callback);
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
		IFiscalModelAdmonPanelCallback<Mod123, Model123ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod123, Model123ModuleOptions>() {

				@Override
				public Model123ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod123 getModel() {
					return Model123AEAT.this.getModel();
				}

				@Override
				public void showError(String msg) {
					getCallback().showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return GWT.getHostPageBaseURL() + "aon_gwt_fiscal/ms/Mod123ValidatePrintAEAT";
					
				}

				@Override
				public String getDownloadFileAction() {
					return Model123Base.MODEL123_FILE;
				}

				@Override
				public String getSendAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod123SendAEAT";
				}

				@Override
				public void sendSuccessfully() {
					Model123.SERVICE.getMod123(getCallback().getOptions().getOccam(), 
							getModel().getId(), new AsyncCallback<Mod123>() {
						@Override
						public void onSuccess(Mod123 selected) {
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
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod123CheckAEAT";
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod123CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://www.agenciatributaria.gob.es/AEAT.sede/procedimientoini/GH04.shtml";
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
