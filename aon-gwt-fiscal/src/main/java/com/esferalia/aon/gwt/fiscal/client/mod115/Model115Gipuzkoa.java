package com.esferalia.aon.gwt.fiscal.client.mod115;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod115.Model115.Model115Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.mod115.Model115Gipuzkoa2022Script;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model115Gipuzkoa extends Model115Base {
	
	public Model115Gipuzkoa(Mod115 mod115, Model115Callback callback) {
		super(mod115, callback);
	}

	@Override
	protected void paintParticularyRow(FlexTable table, final Model115Callback callback, IModelScript<Mod115Key> script) {
		if (script == Model115Gipuzkoa2022Script.X00) {
			int row = table.getRowCount();
			table.getFlexCellFormatter().setColSpan(row, 0, 8);
			table.getFlexCellFormatter().setStyleName(row, 0,AON.CSS.aonBold() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonTextCenter() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderBottom() );
			table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonBorderTop() );
			table.setWidget(row, 0, new Label( "Informaci\u00F3n para la presentaci\u00F3n telem\u00E1tica"));
			row++;
			paintLabel(table, row, script);
			table.getFlexCellFormatter().setColSpan(row, 0, 6);
			Mod115Key key = Mod115Key.GP_X00;
			final FiscalModelDetail det1 = getModel().ensureDetail(key);
			final AonTextBox input = new AonTextBox();
			input.setVisibleLength(10);
			input.setMaxLength(9);
			input.setEnabled(getModel().isEditable() && script.isEnabled()); 
			input.setValue(det1.getDescription());
			input.addValueChangeHandler(event -> {
				getModel().ensureDetail(key).setDescription(input.getValue());	
				if (input.isEnabled()) {
					calculateAndRefresh( callback );
				}
				markAsDirty();
			});
			table.setWidget(row, 1, input);
		}
	}
	
	@Override
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod115, Model115ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod115, Model115ModuleOptions>() {

				@Override
				public Model115ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod115 getModel() {
					return Model115Gipuzkoa.this.getModel();
				}

				@Override
				public void showError(String msg) {
					getCallback().showError(msg);
				}

				@Override
				public String getValidatePrintAction() {
					return null;
				}

				@Override
				public String getDownloadFileAction() {
					return Model115Base.MODEL115_FILE;
				}

				@Override
				public String getSendAction() {
					return null;
				}

				@Override
				public void sendSuccessfully() {
					// Nothing
				}

				@Override
				public String getCheckAction() {
					return null;
				}

				@Override
				public String getCheckDataResponseDataAction() {
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod115CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://www.gipuzkoa.eus/es/web/ogasuna/impuestos/modelo/115";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
}
