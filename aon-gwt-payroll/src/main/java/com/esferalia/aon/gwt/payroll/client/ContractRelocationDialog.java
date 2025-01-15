package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;

public abstract class ContractRelocationDialog extends AonCustomDialog {
	
	private HTMLPanel container;
	
	private AonCustomDateBox extinctionNotice = new AonCustomDateBox("Fecha preaviso de extinci\u00f3n");
	private AonCustomListBox workerInherentReason = new AonCustomListBox("Causas inherentes a la persona trabajadora");
	
	private AonCustomTextBox contractType = new AonCustomTextBox("TC2");
	private AonCustomTextBox contractTime = new AonCustomTextBox("Tipo Contrato");
	
	private AonCustomTextBox province = new AonCustomTextBox("Provincia");
	private AonCustomTextBox city = new AonCustomTextBox("Localidad");
	private AonCustomTextBox address = new AonCustomTextBox("Direcci\u00f3n");
	private AonCustomTextBox number = new AonCustomTextBox("Numero");
	
	private AonCustomDateBox date = new AonCustomDateBox("Fecha Incorporaci\u00f3n");
	private AonCustomTextBox hour = new AonCustomTextBox("Hora");
	private AonCustomTextBox minute = new AonCustomTextBox("Minuto");
	
	private AonCustomListBox formationAction = new AonCustomListBox("Acciones formativas necesarias para ocupar el puesto");
	private AonCustomListBox formationPosition = new AonCustomListBox("Formaci\u00f3n por puesto de trabajo o por oficio");
	
	private HTMLPanel buttonsPanel;
	
	private static DateTimeFormat dayFormat = DateTimeFormat.getFormat("dd");
	private static DateTimeFormat monthFormat = DateTimeFormat.getFormat("MMMM");
	private static DateTimeFormat yearFormat = DateTimeFormat.getFormat("yyyy");
	
	// ------------------------------------------------- Constructor

	public ContractRelocationDialog(String contract) {	
		setCaption("Propuesta Recolocaci\u00f3n");
		
		ContractType contractTypeEnum = new ContractType();
		ContractTypeRecord contractTypeObj = contractTypeEnum.getContractType(Integer.parseInt(contract));
		
		container = new HTMLPanel("");
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem");
		container.setWidth("35rem");
		
		container.add(extinctionNotice);
		
		workerInherentReason.clearItems();
		workerInherentReason.addItem("Inexistencia de obras en la provincia en la que esta Vd. contratado.");
		workerInherentReason.addItem("Inexistencia de obras en la provincia en la que esta Vd. contratado acordes con su cualificaci\u00f3n profesional, nivel, funci\u00f3n y grupo profesional una vez analizada su cualificaci\u00f3n o posible recualificaci\u00f3n.");
		workerInherentReason.addItem("Su cualificaci\u00f3n, incluso tras un proceso de formaci\u00f3n o recualificaci\u00f3n, no resulta adecuada a las nuevas obras que tiene la empresa en la misma provincia.");
		workerInherentReason.addItem("Su cualificaci\u00f3n, incluso tras un proceso de formaci\u00f3n o recualificaci\u00f3n, no permite la integraci\u00f3n en las nuevas obras que tiene la empresa en la misma provincia por existir un exceso de personas con la cualificaci\u00f3n necesaria para desarrollar sus mismas funciones y que tienen prioridad sobre Vd. segun los criterios establecidos en el articulo 24 bis.5.b) del Convenio colectivo general del sector de la construcci\u00f3n.");
		container.add(workerInherentReason);
		
		HTMLPanel contractTypePanel = new HTMLPanel("");
		contractTypePanel.setStyleName(AON.CSS.aonItemFlex());
		
		contractType.setValue(contract);
		contractTime.setValue(contractTypeObj == null ? "" : AonStringUtils.equals(contractTypeObj.getJourneyType(), "C") ? "COMPLETO" : "PARCIAL");
		
		contractTypePanel.add(contractType);
		contractTypePanel.add(contractTime);
		container.add(contractTypePanel);
		
		InlineLabel locationL = new InlineLabel("Ubicaci\u00f3n de la obra");
		locationL.setStyleName(AON.CSS.aonTableLabel());
		container.add(locationL);
		
		container.add(address);
		
		HTMLPanel addressPanel = new HTMLPanel("");
		addressPanel.setStyleName(AON.CSS.aonItemFlex());
		
		addressPanel.add(number);
		addressPanel.add(province);
		addressPanel.add(city);
		container.add(addressPanel);
		
		HTMLPanel datePanel = new HTMLPanel("");
		datePanel.setStyleName(AON.CSS.aonItemFlex());
		
		datePanel.add(date);
		datePanel.add(hour);
		datePanel.add(minute);
		container.add(datePanel);
		
		formationAction.clearItems();
		formationAction.addItem("No es exigible ninguna acci\u00f3n formativa para ocupar el nuevo puesto por disponer ya la persona trabajadora de la formaci\u00f3n adecuada.");
		formationAction.addItem("Formaci\u00f3n en materia de prevenci\u00f3n de riesgos laborales: formaci\u00f3n inicial de 8 horas lectivas.");
		formationAction.addItem("Formaci\u00f3n en materia de prevenci\u00f3n de riesgos laborales: formaci\u00f3n por puesto de trabajo o por oficio:");
		formationAction.addChangeHandler(e -> formationPosition.setVisible(formationAction.getListBox().getSelectedIndex() == 2));
		container.add(formationAction);
		
		formationPosition.clearItems();
		formationPosition.addItem("-");
		formationPosition.addItem("Formaci\u00f3n para responsables de obra y tecnicos de ejecuci\u00f3n: 20 horas lectivas.");
		formationPosition.addItem("Formaci\u00f3n para mandos intermedios: 20 horas lectivas.");
		formationPosition.addItem("Formaci\u00f3n para administrativos: 20 horas lectivas.");
		formationPosition.setVisible(false);
		container.add(formationPosition);
		
		buttonsPanel = new HTMLPanel("");
		buttonsPanel.setStyleName(AON.CSS.aonItemFlex());
		buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
		
		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> onAcceptDialog());
		
		buttonsPanel.add(acceptBtnDialog);
		container.add(buttonsPanel);
		
		setWidget(container);
		
		showDialog();	
	}

	// ------------------------------------------------- setWorkplaceDialogObject

	private void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	// ------------------------------------------------- Buttons Panel
	
	private void onAcceptDialog() {
		Map<String, String> contractRelocationInfo = new HashMap<>();
		
		contractRelocationInfo.put("Text1", extinctionNotice.getValue() == null ? "" : dayFormat.format(extinctionNotice.getValue()));
		contractRelocationInfo.put("Text2", extinctionNotice.getValue() == null ? "" : monthFormat.format(extinctionNotice.getValue()));
		contractRelocationInfo.put("Text3", extinctionNotice.getValue() == null ? "" : yearFormat.format(extinctionNotice.getValue()).substring(2, 4));
		
		contractRelocationInfo.put("Check Box4", workerInherentReason.getListBox().getSelectedIndex() == 0 ? "Y" : "N");
		contractRelocationInfo.put("Check Box5", workerInherentReason.getListBox().getSelectedIndex() == 1 ? "Y" : "N");
		contractRelocationInfo.put("Check Box6", workerInherentReason.getListBox().getSelectedIndex() == 2 ? "Y" : "N");
		contractRelocationInfo.put("Check Box7", workerInherentReason.getListBox().getSelectedIndex() == 3 ? "Y" : "N");
		
		contractRelocationInfo.put("Text8", contractType.getValue());
		contractRelocationInfo.put("Text9", contractTime.getValue());
		
		contractRelocationInfo.put("Text10", city.getValue());
		contractRelocationInfo.put("Text12", province.getValue());
		contractRelocationInfo.put("Text13", address.getValue());
		contractRelocationInfo.put("Text14", number.getValue());
		
		contractRelocationInfo.put("Text15", date.getValue() == null ? "" : dayFormat.format(date.getValue()));
		contractRelocationInfo.put("Text16", date.getValue() == null ? "" : monthFormat.format(date.getValue()));
		contractRelocationInfo.put("Text17", date.getValue() == null ? "" : yearFormat.format(date.getValue()).substring(2, 4));
		contractRelocationInfo.put("Text18", hour.getValue());
		contractRelocationInfo.put("Text19", minute.getValue());
		
		contractRelocationInfo.put("Check Box20", formationAction.getListBox().getSelectedIndex() == 0 ? "Y" : "N");
		contractRelocationInfo.put("Check Box21", formationAction.getListBox().getSelectedIndex() == 1 ? "Y" : "N");
		contractRelocationInfo.put("Check Box22", formationAction.getListBox().getSelectedIndex() == 2 ? "Y" : "N");
		contractRelocationInfo.put("Check Box23", formationAction.getListBox().getSelectedIndex() == 2 && formationPosition.getListBox().getSelectedIndex() == 0 ? "Y" : "N");
		contractRelocationInfo.put("Check Box24", formationAction.getListBox().getSelectedIndex() == 2 && formationPosition.getListBox().getSelectedIndex() == 1 ? "Y" : "N");
		contractRelocationInfo.put("Check Box25", formationAction.getListBox().getSelectedIndex() == 2 && formationPosition.getListBox().getSelectedIndex() == 2 ? "Y" : "N");
		
		contractRelocationInfo.put("Text27", dayFormat.format(new Date()));
		contractRelocationInfo.put("Text28", monthFormat.format(new Date()));
		contractRelocationInfo.put("Text29", yearFormat.format(new Date()).substring(2, 4));
		
		contractRelocationInfo.put("Text30", dayFormat.format(new Date()));
		contractRelocationInfo.put("Text31", monthFormat.format(new Date()));
		contractRelocationInfo.put("Text32", yearFormat.format(new Date()).substring(2, 4));
		
		onAccept(contractRelocationInfo);
		hide();
	}

	protected abstract void onAccept(Map<String, String> contractRelocationInfo);
}
