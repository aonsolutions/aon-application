package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;

public abstract class ContractRelocationDialog extends AonCustomDialog {
	
	private HTMLPanel container;
	
	private AonDateBox extinctionNotice;
	
	private CheckBox workerInherentReason1;
	private CheckBox workerInherentReason2;
	private CheckBox workerInherentReason3;
	private CheckBox workerInherentReason4;
	
	private TextBox contractTypeTB;
	private TextBox contractTimeType;
	
	private TextBox locationCity;
	private TextBox locationProvince;
	private TextBox locationAddres;
	private TextBox locationNumber;
	
	private AonDateBox startDate;
	private TextBox startHour;
	private TextBox startMinute;
	
	private CheckBox formationAction1;
	private CheckBox formationAction2;
	private CheckBox formationAction3;
	private CheckBox formationAction3_1;
	private CheckBox formationAction3_2;
	private CheckBox formationAction3_3;
	
	private HTMLPanel buttonsPanel;
	
	private String contractType;
	
	private static DateTimeFormat dayFormat = DateTimeFormat.getFormat("dd");
	private static DateTimeFormat monthFormat = DateTimeFormat.getFormat("MMMM");
	private static DateTimeFormat yearFormat = DateTimeFormat.getFormat("yyyy");
	
	// ------------------------------------------------- Constructor

	public ContractRelocationDialog(String contractType) {	
		setCaption("Propuesta Recolocaci\u00f3n");
		this.contractType = contractType;
		createContainer();
		getButtonsPanel();	
		add(container);
		showDialog();	
	}

	private void createContainer() {
		ContractType contractTypeEnum = new ContractType();
		ContractTypeRecord contractTypeObj = contractTypeEnum.getContractType(Integer.parseInt(contractType));
		
		container = new HTMLPanel("");
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.setWidth("800px");
		container.getElement().getStyle().setProperty("margin", "1rem");
		
		HTMLPanel extinctionNoticePanel = new HTMLPanel("");
		extinctionNoticePanel.setStyleName(AON.CSS.aonItemFlex());
		
		InlineLabel extinctionNoticeL = new InlineLabel("Fecha preaviso de extinci\u00f3n");
		extinctionNoticeL.setStyleName(AON.CSS.aonTableLabel());
		
		extinctionNotice = new AonDateBox();
		extinctionNotice.addStyleName(AON.CSS.aonTextCenter());
		
		extinctionNoticePanel.add(extinctionNoticeL);
		extinctionNoticePanel.add(extinctionNotice);
		
		InlineLabel workerInherentReasonL = new InlineLabel("Causas inherentes a la persona trabajadora");
		workerInherentReasonL.setStyleName(AON.CSS.aonTableLabel());
		
		HTMLPanel workerInherentReasonPanel = new HTMLPanel("");
		workerInherentReasonPanel.setStyleName(AON.CSS.aonFlexColumn());
		
		HTMLPanel workerInherentReason1Panel = new HTMLPanel("");
		workerInherentReason1Panel.setStyleName(AON.CSS.aonItemFlex());
		
		workerInherentReason1 = new CheckBox();
		InlineLabel workerInherentReason1L = new InlineLabel("Inexistencia de obras en la provincia en la que esta Vd. contratado.");
		
		workerInherentReason1Panel.add(workerInherentReason1);
		workerInherentReason1Panel.add(workerInherentReason1L);
		
		workerInherentReasonPanel.add(workerInherentReason1Panel);
		
		HTMLPanel workerInherentReason2Panel = new HTMLPanel("");
		workerInherentReason2Panel.setStyleName(AON.CSS.aonItemFlex());
		
		workerInherentReason2 = new CheckBox();
		InlineLabel workerInherentReason2L = new InlineLabel("Inexistencia de obras en la provincia en la que esta Vd. contratado acordes con su cualificaci\u00f3n profesional, nivel, funci\u00f3n y grupo profesional una vez analizada su cualificaci\u00f3n o posible recualificaci\u00f3n.");
		
		workerInherentReason2Panel.add(workerInherentReason2);
		workerInherentReason2Panel.add(workerInherentReason2L);
		
		workerInherentReasonPanel.add(workerInherentReason2Panel);
		
		HTMLPanel workerInherentReason3Panel = new HTMLPanel("");
		workerInherentReason3Panel.setStyleName(AON.CSS.aonItemFlex());
		
		workerInherentReason3 = new CheckBox();
		InlineLabel workerInherentReason3L = new InlineLabel("Su cualificaci\u00f3n, incluso tras un proceso de formaci\u00f3n o recualificaci\u00f3n, no resulta adecuada a las nuevas obras que tiene la empresa en la misma provincia.");
		
		workerInherentReason3Panel.add(workerInherentReason3);
		workerInherentReason3Panel.add(workerInherentReason3L);
		
		workerInherentReasonPanel.add(workerInherentReason3Panel);
		
		HTMLPanel workerInherentReason4Panel = new HTMLPanel("");
		workerInherentReason4Panel.setStyleName(AON.CSS.aonItemFlex());
		
		workerInherentReason4 = new CheckBox();
		InlineLabel workerInherentReason4L = new InlineLabel("Su cualificaci\u00f3n, incluso tras un proceso de formaci\u00f3n o recualificaci\u00f3n, no permite la integraci\u00f3n en las nuevas obras que tiene la empresa en la misma provincia por existir un exceso de personas con la cualificaci\u00f3n necesaria para desarrollar sus mismas funciones y que tienen prioridad sobre Vd. segun los criterios establecidos en el articulo 24 bis.5.b) del Convenio colectivo general del sector de la construcci\u00f3n.");
		
		workerInherentReason4Panel.add(workerInherentReason4);
		workerInherentReason4Panel.add(workerInherentReason4L);
		
		workerInherentReasonPanel.add(workerInherentReason4Panel);
		
		HTMLPanel contractTypePanel = new HTMLPanel("");
		contractTypePanel.setStyleName(AON.CSS.aonItemFlex());
		
		InlineLabel contractTypeL = new InlineLabel("Tipo Contrato");
		contractTypeL.setStyleName(AON.CSS.aonTableLabel());
		
		contractTypeTB = new TextBox();
		contractTypeTB.setStyleName(AON.CSS.aonInputText());
		contractTypeTB.setValue(contractType);
		
		contractTypePanel.add(contractTypeL);
		contractTypePanel.add(contractTypeTB);
		
		HTMLPanel contractTimeTypePanel = new HTMLPanel("");
		contractTimeTypePanel.setStyleName(AON.CSS.aonItemFlex());
		
		InlineLabel contractTimeTypeL = new InlineLabel("Tipo Contrato");
		contractTimeTypeL.setStyleName(AON.CSS.aonTableLabel());
		
		contractTimeType = new TextBox();
		contractTimeType.setStyleName(AON.CSS.aonInputText());
		contractTimeType.setValue(contractTypeObj == null ? "" : AonStringUtils.equals(contractTypeObj.getJourneyType(), "C") ? "COMPLETO" : "PARCIAL");
		
		contractTimeTypePanel.add(contractTimeTypeL);
		contractTimeTypePanel.add(contractTimeType);
		
		InlineLabel locationL = new InlineLabel("Ubicaci\u00f3n de la obra");
		locationL.setStyleName(AON.CSS.aonTableLabel());
		
		HTMLPanel location1Panel = new HTMLPanel("");
		location1Panel.setStyleName(AON.CSS.aonItemFlex());
		
		InlineLabel locationProvinceL = new InlineLabel("Provincia");
		locationProvinceL.setStyleName(AON.CSS.aonTableLabel());
		
		locationProvince = new TextBox();
		locationProvince.setStyleName(AON.CSS.aonInputText());
		
		InlineLabel locationCityL = new InlineLabel("Localidad");
		locationCityL.setStyleName(AON.CSS.aonTableLabel());
		
		locationCity = new TextBox();
		locationCity.setStyleName(AON.CSS.aonInputText());
		
		location1Panel.add(locationProvinceL);
		location1Panel.add(locationProvince);
		location1Panel.add(locationCityL);
		location1Panel.add(locationCity);
		
		HTMLPanel location2Panel = new HTMLPanel("");
		location2Panel.setStyleName(AON.CSS.aonItemFlex());
		
		InlineLabel locationAddresL = new InlineLabel("Direcci\u00f3n");
		locationAddresL.setStyleName(AON.CSS.aonTableLabel());
		
		locationAddres = new TextBox();
		locationAddres.setStyleName(AON.CSS.aonInputText());
		locationAddres.setWidth("500px");
		
		InlineLabel locationNumberL = new InlineLabel("Numero");
		locationNumberL.setStyleName(AON.CSS.aonTableLabel());
		
		locationNumber = new TextBox();
		locationNumber.setStyleName(AON.CSS.aonInputText());
		locationNumber.addStyleName(AON.CSS.aonTextCenter());
		locationNumber.setWidth("50px");
		
		location2Panel.add(locationAddresL);
		location2Panel.add(locationAddres);
		location2Panel.add(locationNumberL);
		location2Panel.add(locationNumber);
		
		HTMLPanel startDatePanel = new HTMLPanel("");
		startDatePanel.setStyleName(AON.CSS.aonItemFlex());
		
		InlineLabel startDateL = new InlineLabel("Fecha incorporaci\u00f3n");
		startDateL.setStyleName(AON.CSS.aonTableLabel());
		
		startDate = new AonDateBox();
		startDate.addStyleName(AON.CSS.aonTextCenter());
		
		InlineLabel startHourL = new InlineLabel("a las");
		startHourL.setStyleName(AON.CSS.aonTableLabel());
		
		startHour = new TextBox();
		startHour.setStyleName(AON.CSS.aonInputText());
		startHour.addStyleName(AON.CSS.aonTextCenter());
		startHour.setWidth("50px");
		
		InlineLabel timeL = new InlineLabel(":");
		timeL.setStyleName(AON.CSS.aonTableLabel());
		
		startMinute = new TextBox();
		startMinute.setStyleName(AON.CSS.aonInputText());
		startMinute.addStyleName(AON.CSS.aonTextCenter());
		startMinute.setWidth("50px");
		
		InlineLabel hoursL = new InlineLabel("horas");
		hoursL.setStyleName(AON.CSS.aonTableLabel());
		
		startDatePanel.add(startDateL);
		startDatePanel.add(startDate);
		startDatePanel.add(startHourL);
		startDatePanel.add(startHour);
		startDatePanel.add(timeL);
		startDatePanel.add(startMinute);
		startDatePanel.add(hoursL);
		
		InlineLabel formationActionL = new InlineLabel("Acciones formativas necesarias para ocupar el puesto");
		formationActionL.setStyleName(AON.CSS.aonTableLabel());
		
		HTMLPanel formationActionPanel = new HTMLPanel("");
		formationActionPanel.setStyleName(AON.CSS.aonFlexColumn());
		
		HTMLPanel formationAction1Panel = new HTMLPanel("");
		formationAction1Panel.setStyleName(AON.CSS.aonItemFlex());
		
		formationAction1 = new CheckBox();
		InlineLabel formationAction1L = new InlineLabel("No es exigible ninguna acci\u00f3n formativa para ocupar el nuevo puesto por disponer ya la persona trabajadora de la formaci\u00f3n adecuada.");
		
		formationAction1Panel.add(formationAction1);
		formationAction1Panel.add(formationAction1L);
		
		formationActionPanel.add(formationAction1Panel);
		
		HTMLPanel formationAction2Panel = new HTMLPanel("");
		formationAction2Panel.setStyleName(AON.CSS.aonItemFlex());
		
		formationAction2 = new CheckBox();
		InlineLabel formationAction2L = new InlineLabel("Formaci\u00f3n en materia de prevenci\u00f3n de riesgos laborales: formaci\u00f3n inicial de 8 horas lectivas.");
		
		formationAction2Panel.add(formationAction2);
		formationAction2Panel.add(formationAction2L);
		
		formationActionPanel.add(formationAction2Panel);
		
		HTMLPanel formationAction3Panel = new HTMLPanel("");
		formationAction3Panel.setStyleName(AON.CSS.aonItemFlex());
		
		formationAction3 = new CheckBox();
		InlineLabel formationAction3L = new InlineLabel("Formaci\u00f3n en materia de prevenci\u00f3n de riesgos laborales: formaci\u00f3n por puesto de trabajo o por oficio:");
		
		formationAction3Panel.add(formationAction3);
		formationAction3Panel.add(formationAction3L);
		
		formationActionPanel.add(formationAction3Panel);
		
		HTMLPanel formationAction3_0Panel = new HTMLPanel("");
		formationAction3_0Panel.setStyleName(AON.CSS.aonFlexColumn());
		formationAction3_0Panel.getElement().getStyle().setProperty("margin-left", "2rem");
		
		HTMLPanel formationAction3_1Panel = new HTMLPanel("");
		formationAction3_1Panel.setStyleName(AON.CSS.aonItemFlex());
		
		formationAction3_1 = new CheckBox();
		InlineLabel formationAction3_1L = new InlineLabel("Formaci\u00f3n para responsables de obra y tecnicos de ejecuci\u00f3n: 20 horas lectivas.");
		
		formationAction3_1Panel.add(formationAction3_1);
		formationAction3_1Panel.add(formationAction3_1L);
		
		formationAction3_0Panel.add(formationAction3_1Panel);
		
		HTMLPanel formationAction3_2Panel = new HTMLPanel("");
		formationAction3_2Panel.setStyleName(AON.CSS.aonItemFlex());
		
		formationAction3_2 = new CheckBox();
		InlineLabel formationAction3_2L = new InlineLabel("Formaci\u00f3n para mandos intermedios: 20 horas lectivas.");
		
		formationAction3_2Panel.add(formationAction3_2);
		formationAction3_2Panel.add(formationAction3_2L);
		
		formationAction3_0Panel.add(formationAction3_2Panel);
		
		HTMLPanel formationAction3_3Panel = new HTMLPanel("");
		formationAction3_3Panel.setStyleName(AON.CSS.aonItemFlex());
		
		formationAction3_3 = new CheckBox();
		InlineLabel formationAction3_3L = new InlineLabel("Formaci\u00f3n para administrativos: 20 horas lectivas.");
		
		formationAction3_3Panel.add(formationAction3_3);
		formationAction3_3Panel.add(formationAction3_3L);
		
		formationAction3_0Panel.add(formationAction3_3Panel);
		
		formationActionPanel.add(formationAction3_0Panel);
		
		container.add(extinctionNoticePanel);
		container.add(workerInherentReasonL);
		container.add(workerInherentReasonPanel);
		container.add(contractTypePanel);
		container.add(contractTimeTypePanel);
		container.add(locationL);
		container.add(location1Panel);
		container.add(location2Panel);
		container.add(startDatePanel);
		container.add(formationActionL);
		container.add(formationActionPanel);
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
	
	private void getButtonsPanel() {
		buttonsPanel = new HTMLPanel("");
		buttonsPanel.setStyleName(AON.CSS.aonDisplayFlexEnd());
		buttonsPanel.getElement().getStyle().setProperty("margin", "0.5rem 1rem 0.8rem 1rem");
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.addClickHandler(e -> onCloseDialog());
		
		buttonsPanel.add(closeBtnDialog);
		
		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> onAcceptDialog());
		
		buttonsPanel.add(acceptBtnDialog);
		container.add(buttonsPanel);
	}
	
	private void onCloseDialog() {
		hide();
	}
	
	private void onAcceptDialog() {
		Map<String, String> contractRelocationInfo = new HashMap<>();
		
		contractRelocationInfo.put("Text1", extinctionNotice.getValue() == null ? "" : dayFormat.format(extinctionNotice.getValue()));
		contractRelocationInfo.put("Text2", extinctionNotice.getValue() == null ? "" : monthFormat.format(extinctionNotice.getValue()));
		contractRelocationInfo.put("Text3", extinctionNotice.getValue() == null ? "" : yearFormat.format(extinctionNotice.getValue()).substring(2, 4));
		
		contractRelocationInfo.put("Check Box4", workerInherentReason1.getValue() ? "Y" : "N");
		contractRelocationInfo.put("Check Box5", workerInherentReason2.getValue() ? "Y" : "N");
		contractRelocationInfo.put("Check Box6", workerInherentReason3.getValue() ? "Y" : "N");
		contractRelocationInfo.put("Check Box7", workerInherentReason4.getValue() ? "Y" : "N");
		
		contractRelocationInfo.put("Text8", contractTypeTB.getValue());
		contractRelocationInfo.put("Text9", contractTimeType.getValue());
		
		contractRelocationInfo.put("Text10", locationCity.getValue());
		contractRelocationInfo.put("Text12", locationProvince.getValue());
		contractRelocationInfo.put("Text13", locationAddres.getValue());
		contractRelocationInfo.put("Text14", locationNumber.getValue());
		
		contractRelocationInfo.put("Text15", startDate.getValue() == null ? "" : dayFormat.format(startDate.getValue()));
		contractRelocationInfo.put("Text16", startDate.getValue() == null ? "" : monthFormat.format(startDate.getValue()));
		contractRelocationInfo.put("Text17", startDate.getValue() == null ? "" : yearFormat.format(startDate.getValue()).substring(2, 4));
		contractRelocationInfo.put("Text18", startHour.getValue());
		contractRelocationInfo.put("Text19", startMinute.getValue());
		
		contractRelocationInfo.put("Check Box20", formationAction1.getValue() ? "Y" : "N");
		contractRelocationInfo.put("Check Box21", formationAction2.getValue() ? "Y" : "N");
		contractRelocationInfo.put("Check Box22", formationAction3.getValue() ? "Y" : "N");
		contractRelocationInfo.put("Check Box23", formationAction3_1.getValue() ? "Y" : "N");
		contractRelocationInfo.put("Check Box24", formationAction3_2.getValue() ? "Y" : "N");
		contractRelocationInfo.put("Check Box25", formationAction3_3.getValue() ? "Y" : "N");
		
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
