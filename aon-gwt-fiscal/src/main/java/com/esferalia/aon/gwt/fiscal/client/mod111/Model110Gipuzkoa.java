package com.esferalia.aon.gwt.fiscal.client.mod111;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod111.Model111.Model111Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.mod111.Model110Gipuzkoa2023Script;
import com.esferalia.aon.occam.api.model.fiscal.mod111.Model110GipuzkoaScript;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model110Gipuzkoa extends Model111Base {
	
	public Model110Gipuzkoa(Mod111 mod111,Model111Callback callback) {
		super(mod111, callback);
	}

	@Override
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		IFiscalModelAdmonPanelCallback<Mod111, Model111ModuleOptions> cbk = 
			new IFiscalModelAdmonPanelCallback<Mod111, Model111ModuleOptions>() {

				@Override
				public Model111ModuleOptions getOptions() {
					return getCallback().getOptions();
				}

				@Override
				public Mod111 getModel() {
					return Model110Gipuzkoa.this.getModel();
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
					return Model111Base.MODEL111_FILE;
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
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod111CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://www.gipuzkoa.eus/es/web/ogasuna/impuestos/modelo/110";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
	@Override
	protected void paintParticularyRow(FlexTable table, Model111Callback callback, IModelScript<Mod111Key> script) {
		if (script == Model110GipuzkoaScript.X00
			|| script == Model110Gipuzkoa2023Script.X00) {
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
			Mod111Key key = Mod111Key.GP_X00;
			final FiscalModelDetail det1 = getModel().ensureDetail(key);
			final AonTextBox input = new AonTextBox();
			input.setVisibleLength(10);
			input.setMaxLength(9);
			input.setEnabled(script.isEnabled()); 
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
}
