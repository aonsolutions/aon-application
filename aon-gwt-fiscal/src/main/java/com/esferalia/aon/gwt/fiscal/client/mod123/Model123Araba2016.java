package com.esferalia.aon.gwt.fiscal.client.mod123;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.fiscal.client.mod123.Model123.Model123Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelAdmonPanel.IFiscalModelAdmonPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class Model123Araba2016 extends Model123Base {

	public Model123Araba2016(Mod123 mod123,Model123Callback callback) {
		super(mod123,callback);
	}

	@Override
	protected void paintParticularyRow(FlexTable table, final Model123Callback callback, IModelScript<Mod123Key> script) {
		if (script.getKeys() == null) return;
		
		if (script.getKeys()[0] == Mod123Key.AR_907) { // Declarado en concurso de acreedores
			paintRow907(table,script);
		} else if (script.getKeys()[0] == Mod123Key.AR_908) { // Tipo declaración en concurso (EN EL MODELO ES LA 909) 
			paintRow908(table,script);
		} else if (script.getKeys()[0] == Mod123Key.AR_909) { // Fecha declaración en concurso (EN EL MODELO ES LA 908)
			paintRow909(table,script);
		} else if (script.getKeys()[0] == Mod123Key.AR_930) { // Presentación fuera de plazo por requerimiento (Añadido en 2024)
			paintRow930(table,script);
		}
		
	}

	private void paintRow907(FlexTable table, IModelScript<Mod123Key> script) {
		int row = table.getRowCount();
		final FiscalModelDetail ar907 = getModel().ensureDetail(Mod123Key.AR_907);
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonTextRight() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingRight() );
		
		table.setWidget(row, 1, new AonBoxLabel(Mod123Key.AR_907.getBox()));
		final CheckBox w907 = new CheckBox();
		w907.setEnabled(getModel().isEditable());
		w907.setValue(ar907.getAmount() == 1);
		w907.addClickHandler(event -> {
			ar907.setAmount(w907.getValue().booleanValue()?1.0:0.0);
			markAsDirty();
		});
		table.setWidget(row, 2, w907 );
	}
	
	private void paintRow908(FlexTable table, IModelScript<Mod123Key> script) {
		int row = table.getRowCount();
		final  FiscalModelDetail ar908 = getModel().ensureDetail(Mod123Key.AR_908);
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonTextRight() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingRight() );
		
		table.setWidget(row, 1, new AonBoxLabel(Mod123Key.AR_908.getBox()));
		final ListBox w908 = new ListBox();
		w908.setEnabled(getModel().isEditable());
		w908.addItem("---");
		w908.addItem(AON.MSG.preInsolvencyState());
		w908.addItem(AON.MSG.postInsolvencyState());
		w908.setSelectedIndex((int) ar908.getAmount());
		w908.addChangeHandler(event -> {
			ar908.setAmount(w908.getSelectedIndex());
			markAsDirty();
		});
		table.setWidget(row, 2, w908 );
	}
	private void paintRow909(FlexTable table, IModelScript<Mod123Key> script) {
		int row = table.getRowCount();
		final FiscalModelDetail ar909 = getModel().ensureDetail(Mod123Key.AR_909);
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonTextRight() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingRight() );
		
		table.setWidget(row, 1, new AonBoxLabel(Mod123Key.AR_909.getBox()));
		final AonDateBox w909 = new AonDateBox();
		w909.setEnabled(getModel().isEditable());
		if (AonStringUtils.isNotEmpty( ar909.getDescription() ) ) {
			w909.setValue( w909.parse(ar909.getDescription() , false) );
		}
		w909.addValueChangeHandler( event -> {
			ar909.setDescription(w909.format());
			markAsDirty();
		});
		table.setWidget(row, 2, w909 );
		paintEmptyRow(table);
	}
	
	private void paintRow930(FlexTable table, IModelScript<Mod123Key> script) {
		int row = table.getRowCount();
		final FiscalModelDetail ar930 = getModel().ensureDetail(Mod123Key.AR_930);
		table.setWidget(row, 0, new Label(script.getLabel()));
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonTextRight() );
		table.getFlexCellFormatter().addStyleName(row, 0,AON.CSS.aonPaddingRight() );
		
		table.setWidget(row, 1, new AonBoxLabel(Mod123Key.AR_930.getBox()));
		final CheckBox w930 = new CheckBox();
		w930.setEnabled(getModel().isEditable());
		w930.setValue(ar930.getAmount() == 1);
		w930.addClickHandler(event -> {
			ar930.setAmount(w930.getValue().booleanValue()?1.0:0.0);
			markAsDirty();
		});
		table.setWidget(row, 2, w930 );
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
					return Model123Araba2016.this.getModel();
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
					return Model123Base.MODEL123_FILE;
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
					return GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod123CheckDataResponseData";
				}

				@Override
				public String getModelInformationURL() {
					return "https://egoitza.araba.eus/es/-/modelo-123-retenciones-e-ingresos-a-cuenta-sobre-determinados-rendimientos-del-capital-mobiliario-y-sobre-determinadas-rentas";
				}
		};
		admonPanel = new FiscalModelAdmonPanel<>(cbk);
		tabPanel.add( admonPanel, AON.MSG.administrationName(getModel().getAdministration()));		
	}
	
}
