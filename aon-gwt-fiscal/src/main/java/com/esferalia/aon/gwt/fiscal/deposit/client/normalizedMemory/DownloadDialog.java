package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryItem;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class DownloadDialog extends CustomDialogB {
	interface Binder extends UiBinder<Widget, DownloadDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField(provided = true) FlexTable flex_table;
	@UiField Button accept_button;
	@UiField Button cancel_button;
	
	public DownloadDialog(Boolean isMemory, Boolean isMa, Integer year) {
		setCaption("Descargar");
		flex_table = new FlexTable();
		setWidget(binder.createAndBindUi(this));
		buildTable(isMemory, isMa, year);
		accept_button.setText("Aceptar");
		accept_button.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				onAccept();
			}
		});
		
		cancel_button.setText("Cancelar");
		cancel_button.setVisible(false);
		cancel_button.addClickHandler(new ClickHandler() {			
			@Override public void onClick(ClickEvent event) {
				onCancel();
			}
		});
	}
	
	protected abstract void onAccept();
	
	protected abstract void onCancel();
	private static final String IDA = "Hoja Identificativa de la sociedad";
	private static final String AR = "Aplicaci\u00f3n de resultados";
	private static final String BS = "Balance de situaci\u00f3n";
	private static final String PYG = "Cuenta de perdidas y ganancias";
	private static final String ECPN = "Estado de cambios en el patrimonio neto";
	private static final String DM = "Declaraci\u00f3n medioambiental";
 	private static final String MA = "Modelo de autocartera";
	private static final String IP = "Instancia de presentaci\u00f3n";
	private static final String CHD = "Certificaci\u00f3n de la huella digital";

	private void buildTable(final Boolean isMemory, final Boolean isMa, Integer year) {
		Integer index = 1;
		
		flex_table.setWidget(index, 0, new Label(IDA));
		CheckBox cbIDA = new CheckBox();cbIDA.setValue(true);
		flex_table.setWidget(index, 1, cbIDA);
		index++;
		
		if(year >= 2016){
			flex_table.setWidget(index, 0, new Label(AR));
			CheckBox cbAR = new CheckBox();cbAR.setValue(true);
			flex_table.setWidget(index, 1, cbAR);
			index++;
		}
		
		flex_table.setWidget(index, 0, new Label(BS));
		CheckBox cbBS = new CheckBox();cbBS.setValue(true);
		flex_table.setWidget(index, 1, cbBS);
		index++;
		
		flex_table.setWidget(index, 0, new Label(PYG));
		CheckBox cbPYG = new CheckBox();cbPYG.setValue(true);
		flex_table.setWidget(index, 1, cbPYG);
		index++;
		
		if(year < 2016){
			flex_table.setWidget(index, 0, new Label(ECPN));
			CheckBox cbECPN = new CheckBox();cbECPN.setValue(true);
			flex_table.setWidget(index, 1, cbECPN);
			index++;
		}
		
		flex_table.setWidget(index, 0, new Label(DM));
		CheckBox cbDM = new CheckBox();cbDM.setValue(true);
		flex_table.setWidget(index, 1, cbDM);
		index++;
		
		if(!isMemory){
			for(Integer pos = 0; pos < MemoryItem.getInstance().getApartadosSize(year); pos++){	
				flex_table.setWidget(index, 0, new Label(MemoryItem.getInstance().getApartadoName(year, pos)));
				CheckBox cb = new CheckBox();cb.setValue(!isMemory);
				cb.setEnabled(!isMemory);
				flex_table.setWidget(index, 1, cb);
				index++;
			}
		}
		
		flex_table.setWidget(index, 0, new Label(MA));
		CheckBox cbMA = new CheckBox();cbMA.setValue(!isMa);cbMA.setEnabled(!isMa);
		flex_table.setWidget(index, 1, cbMA);
		index++;
		
		flex_table.setWidget(index, 0, new Label(IP));
		CheckBox cbIP = new CheckBox();cbIP.setValue(true);
		flex_table.setWidget(index, 1, cbIP);
		index++;
		
		flex_table.setWidget(index, 0, new Label(CHD));
		CheckBox cbCHD = new CheckBox();cbCHD.setValue(true);
		flex_table.setWidget(index, 1, cbCHD);
		index++;
		
		Label label =  new Label("Seleccionar apartados:");
		label.setStyleName(AON.AON_BOLD);
		flex_table.setWidget(0, 0, label);
		CheckBox cbALL = new CheckBox();cbALL.setValue(true);
		cbALL.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Boolean bool = cbALL.getValue();
				for(Integer i = 1; i < flex_table.getRowCount(); i++){
					CheckBox cb = (CheckBox) flex_table.getWidget(i, 1);
					cb.setValue(bool);
				}
			}
		});
		flex_table.setWidget(0, 1, cbALL);
	}
	
}
