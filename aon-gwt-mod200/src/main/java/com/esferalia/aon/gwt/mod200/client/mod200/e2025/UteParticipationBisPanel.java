package com.esferalia.aon.gwt.mod200.client.mod200.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceCountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.mod200.api.model.UteParticipationBis;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class UteParticipationBisPanel extends AonCustomDialog {	

	public static interface UteParticipationBisPanelCallback {
		void onAccept(int index, UteParticipationBis up, boolean modified);
		void onCancel();
		default void onClose() {
			this.onCancel();
		}
	}
	
	private AonDocumentTextBox document;
	private AonTextBox name;
	private ProvinceCountryListBox province;
	private ListBox entityType;
	private ListBox imputationCriteria;	
	private AonDoubleBox c01279;  // Datos relativos a la participación: Valoración de la participación al comienzo del período impositivo                                                                                                       
	private AonDoubleBox c01455;  // Datos relativos a la participación: Valoración de la participación al final del período impositivo                                                                                                          
	private AonDoubleBox c01456;  // Datos relativos a la participación: Ingresos financieros de la participación                                                                                                                                
	private AonDoubleBox c01458;  // Importes imputados: Importe del resultado contable imputado                                                                                                                                                 
	private AonDoubleBox c01459;  // Importes imputados: Gastos financieros netos imputados                                                                                                                                                      
	private AonDoubleBox c01460;  // Importes imputados: Reserva de capitalización que no haya sido aplicada imputada                                                                                                                            
	private AonDoubleBox c01461;  // Importes imputados: Base imponible imputada                                                                                                                                                                 
	private AonDoubleBox c01467;  // Importes imputados: Importe de la deducción generada por bases de deducción para evitar la doble imposición imputadas                                                                                       
	private AonDoubleBox c01468;  // Importes imputados: Importe bonificación generada de las bases de bonificación imputadas                                                                                                                    
	private AonDoubleBox c01523;  // Importes imputados: Importe de la deducción generada por activos fijos por bases de deducción por inversión en Canarias imputadas                                                                           
	private AonDoubleBox c01601;  // Importes imputados: Importe de la deducción generada de investigación y desarrollo e innovación tecnológica por bases de deducción por inversión en Canarias imputadas                                      
	private AonDoubleBox c01638;  // Importes imputados: Importe de la deducción generada de producciones cinematográficas españolas y espectáculos en vivo de artes escénicas y musicales por deducciones por inversión en Canarias imputadas   
	private AonDoubleBox c01639;  // Importes imputados: Importe de la deducción generada del resto de deducciones por inversión en Canarias imputadas                                                                                           
	private AonDoubleBox c01640;  // Importes imputados: Importe de la deducción generada de investigación y desarrollo e innovación tecnológica por bases de deducción imputadas                                                                
	private AonDoubleBox c01743;  // Importes imputados: Importe de la deducción generada de producciones cinematográficas españolas y espectáculos en vivo de artes escénicas y musicales por bases de deducción imputadas                      
	private AonDoubleBox c01909;  // Importes imputados: Importe del resto de deducciones generadas para incentivar determinadas actividades por bases de deducción imputadas                                                                    
	private AonDoubleBox c01910;  // Importes imputados: Importe del resto de deducciones generadas por bases de deducción imputadas no mencionadas anteriormente                                                                                
	private AonDoubleBox c01911;  // Importes imputados: Retenciones e ingresos a cuenta imputados                                                                                                                                               
	private AonDoubleBox c01912;  // Importes imputados: Dividendos y participaciones en beneficios percibidos procedentes de ejercicios anteriores a la adquisición de la participación                                                         
	private AonDoubleBox c01934;  // Importes imputados: Dividendos y participaciones en beneficios percibidos procedentes de ejercicios posteriores a la adquisición de la participación                                                        
	
	private UteParticipationBisPanelCallback callback;
	private int index;
	private boolean modified; 
	
	private FlowPanel rootPanel = new FlowPanel();

	public UteParticipationBisPanel(UteParticipationBisPanelCallback cb) {
		this.callback = cb;		
		setWidth("900px");
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption("Part\u00EDcipes de agrupaciones de inter\u00E9s econ\u00F3mico y UTES (cumplimentaci\u00F3n voluntaria)");
		paint();
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;		
	}

	public void dump(int index, UteParticipationBis up, boolean isEnabled) {
		
		this.index = index;
		setModified(false);
		
		// Asignar valores
		this.document.setValue(up.getDocument());
		this.name.setValue(up.getName());
		this.province.setSelectedIndex(0);
		int idx = up.getProvince();
		if (idx > 0 && idx < Province.values().length) {
			this.province.setSelectedIndex(idx);
		} else {
			Country c = Country.safeValueOf(up.getCountry());
			if (c != null) {
				this.province.setSelectedIndex(c.ordinal() + Province.values().length);
			}
		}
		this.entityType.setSelectedIndex(up.getEntityType());
		this.imputationCriteria.setSelectedIndex(up.getImputationCriteria());
		this.c01279.setValue(up.getC01279());   
		this.c01455.setValue(up.getC01455());   
		this.c01456.setValue(up.getC01456());   
		this.c01458.setValue(up.getC01458());   
		this.c01459.setValue(up.getC01459());   
		this.c01460.setValue(up.getC01460());   
		this.c01461.setValue(up.getC01461());   
		this.c01467.setValue(up.getC01467());   
		this.c01468.setValue(up.getC01468());   
		this.c01523.setValue(up.getC01523());   
		this.c01601.setValue(up.getC01601());   
		this.c01638.setValue(up.getC01638());   
		this.c01639.setValue(up.getC01639());   
		this.c01640.setValue(up.getC01640());   
		this.c01743.setValue(up.getC01743());   
		this.c01909.setValue(up.getC01909());   
		this.c01910.setValue(up.getC01910());   
		this.c01911.setValue(up.getC01911());   
		this.c01912.setValue(up.getC01912());   
		this.c01934.setValue(up.getC01934());   
		
		// Habilitar/Deshabilitar 
		this.document.setEnabled(isEnabled);
		this.name.setEnabled(isEnabled);
		this.province.setEnabled(isEnabled);
		this.entityType.setEnabled(isEnabled);
		this.imputationCriteria.setEnabled(isEnabled);
		this.c01279.setEnabled(isEnabled);   
		this.c01455.setEnabled(isEnabled);   
		this.c01456.setEnabled(isEnabled);   
		this.c01458.setEnabled(isEnabled);   
		this.c01459.setEnabled(isEnabled);   
		this.c01460.setEnabled(isEnabled);   
		this.c01461.setEnabled(isEnabled);   
		this.c01467.setEnabled(isEnabled);   
		this.c01468.setEnabled(isEnabled);   
		this.c01523.setEnabled(isEnabled);   
		this.c01601.setEnabled(isEnabled);   
		this.c01638.setEnabled(isEnabled);   
		this.c01639.setEnabled(isEnabled);   
		this.c01640.setEnabled(isEnabled);   
		this.c01743.setEnabled(isEnabled);   
		this.c01909.setEnabled(isEnabled);   
		this.c01910.setEnabled(isEnabled);   
		this.c01911.setEnabled(isEnabled);   
		this.c01912.setEnabled(isEnabled);   
		this.c01934.setEnabled(isEnabled);
		
	}

	@Override
	public void onClose() {
		onCancel();	
	}
	
	public void onCancel() {
		
		if (isModified()) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.cancelAction(), new AonConfirmDialogCallback() {
				
				@Override
				public void onCancel() { 
					/* DO NOTHING */ 
				}
				
				@Override
				public void onAccept() {
					callback.onCancel();
					hide();
				}
			});
		} else {
			callback.onCancel();
			hide();
		}
		
	}
	
	public void onAccept() {
		UteParticipationBis up = new UteParticipationBis();
		up.setDocument(this.document.getValue());
		up.setName(this.name.getValue());
		if (this.province.getSelectedIndex() < Province.values().length) {
			up.setProvince(this.province.getSelectedIndex());
		} else {
			Country c = Country.values()[this.province.getSelectedIndex() - Province.values().length];
			up.setCountry(c.getIso2());
		}
		up.setEntityType(this.entityType.getSelectedIndex());
		up.setImputationCriteria(this.imputationCriteria.getSelectedIndex());
		up.setC01279(this.c01279.getValue());   
		up.setC01455(this.c01455.getValue());   
		up.setC01456(this.c01456.getValue());   
		up.setC01458(this.c01458.getValue());   
		up.setC01459(this.c01459.getValue());   
		up.setC01460(this.c01460.getValue());   
		up.setC01461(this.c01461.getValue());   
		up.setC01467(this.c01467.getValue());   
		up.setC01468(this.c01468.getValue());   
		up.setC01523(this.c01523.getValue());   
		up.setC01601(this.c01601.getValue());   
		up.setC01638(this.c01638.getValue());   
		up.setC01639(this.c01639.getValue());   
		up.setC01640(this.c01640.getValue());   
		up.setC01743(this.c01743.getValue());   
		up.setC01909(this.c01909.getValue());   
		up.setC01910(this.c01910.getValue());   
		up.setC01911(this.c01911.getValue());   
		up.setC01912(this.c01912.getValue());   
		up.setC01934(this.c01934.getValue());
		
		callback.onAccept(index, up, isModified());
		this.hide();
	}
	
	private void paint() {
		
		rootPanel.clear();
		
		// Datos de la participada
		
		rootPanel.add(getSubtitle(AON.MSG.partMsg2()));
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab1);
		
		document = new AonDocumentTextBox();
		document.setVisibleLength(15);
		document.addValueChangeHandler(event -> setModified(true));
		
		name = new AonTextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.addValueChangeHandler(event -> setModified(true));
		
		province = new ProvinceCountryListBox();
		province.addChangeHandler(event -> setModified(true));
		
		entityType = new ListBox();		
		entityType.addItem("0 - No consta");
		entityType.addItem("1 - Agrupaci\u00F3n de inter\u00E9s econ\u00F3mico espa\u00F1ola");
		entityType.addItem("2 - Agrupaci\u00F3n europea de inter\u00E9s econ\u00F3mico");
		entityType.addItem("3 - Uni\u00F3n temporal de empresas");
		entityType.addItem("4 - Colaboraciones en el extranjero an\u00E1logas a las uniones temporales");
		entityType.addChangeHandler(event -> setModified(true));
		
		imputationCriteria = new ListBox();		
		imputationCriteria.addItem("0 - No consta");
		imputationCriteria.addItem("1 - En la fecha de finalizaci\u00F3n del periodo impositivo de la entidad");
		imputationCriteria.addItem("2 - En el siguiente periodo impositivo");
		imputationCriteria.addChangeHandler(event -> setModified(true));
		
		addRow(tab1, "NIF (o equivalente al NIF del pa\u00EDs de residencia, si no tiene NIF en Espa\u00F1a)", document, AON.CSS.aonWidth440());
		addRow(tab1, "Nombre o raz\u00F3n social", name, AON.CSS.aonWidth440());
		addRow(tab1, AON.MSG.province() + "/" + AON.MSG.country(), province, AON.CSS.aonWidth440());		
		addRow(tab1, "Tipo de entidad", entityType, AON.CSS.aonWidth440());
		addRow(tab1, "Criterio de imputaci\u00F3n art. 46.2 LIS", imputationCriteria, AON.CSS.aonWidth440());
		
		// Datos relativos a la participación
		
		rootPanel.add(getSubtitle("Datos relativos a la participaci\u00F3n"));
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab2);
		
		c01279 = new AonDoubleBox();
		c01279.addValueChangeHandler(event -> doubleValueChanged(c01279));
		
		c01455 = new AonDoubleBox();
		c01455.addValueChangeHandler(event -> doubleValueChanged(c01455));
		
		c01456 = new AonDoubleBox();
		c01456.addValueChangeHandler(event -> doubleValueChanged(c01456));		

		addRow(tab2, Mod2002025Key.UT1279.getDescription(), c01279, AON.CSS.aonWidth440());
		addRow(tab2, Mod2002025Key.UT1455.getDescription(), c01455, AON.CSS.aonWidth440());
		addRow(tab2, Mod2002025Key.UT1456.getDescription(), c01456, AON.CSS.aonWidth440());
		
		// Importes imputados
		
		rootPanel.add(getSubtitle("Importes imputados"));
		
		AonDisplayTable tab3 = new AonDisplayTable();
		tab3.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab3.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab3);
		
		c01458 = new AonDoubleBox();
		c01458.addValueChangeHandler(event -> doubleValueChanged(c01458));
		c01459 = new AonDoubleBox();
		c01459.addValueChangeHandler(event -> doubleValueChanged(c01459));
		c01460 = new AonDoubleBox();
		c01460.addValueChangeHandler(event -> doubleValueChanged(c01460));
		c01461 = new AonDoubleBox();
		c01461.addValueChangeHandler(event -> doubleValueChanged(c01461));
		c01467 = new AonDoubleBox();
		c01467.addValueChangeHandler(event -> doubleValueChanged(c01467));
		c01468 = new AonDoubleBox();
		c01468.addValueChangeHandler(event -> doubleValueChanged(c01468));
		c01523 = new AonDoubleBox();
		c01523.addValueChangeHandler(event -> doubleValueChanged(c01523));
		c01601 = new AonDoubleBox();
		c01601.addValueChangeHandler(event -> doubleValueChanged(c01601));
		c01638 = new AonDoubleBox();
		c01638.addValueChangeHandler(event -> doubleValueChanged(c01638));
		c01639 = new AonDoubleBox();
		c01639.addValueChangeHandler(event -> doubleValueChanged(c01639));
		c01640 = new AonDoubleBox();
		c01640.addValueChangeHandler(event -> doubleValueChanged(c01640));
		c01743 = new AonDoubleBox();
		c01743.addValueChangeHandler(event -> doubleValueChanged(c01743));
		c01909 = new AonDoubleBox();
		c01909.addValueChangeHandler(event -> doubleValueChanged(c01909));
		c01910 = new AonDoubleBox();
		c01910.addValueChangeHandler(event -> doubleValueChanged(c01910));
		c01911 = new AonDoubleBox();
		c01911.addValueChangeHandler(event -> doubleValueChanged(c01911));
		c01912 = new AonDoubleBox();
		c01912.addValueChangeHandler(event -> doubleValueChanged(c01912));
		c01934 = new AonDoubleBox();
		c01934.addValueChangeHandler(event -> doubleValueChanged(c01934));
		
		addRow(tab3, Mod2002025Key.UT1458.getDescription(), c01458);    
		addRow(tab3, Mod2002025Key.UT1459.getDescription(), c01459);   
		addRow(tab3, Mod2002025Key.UT1460.getDescription(), c01460);   
		addRow(tab3, Mod2002025Key.UT1461.getDescription(), c01461);   
		addRow(tab3, Mod2002025Key.UT1467.getDescription(), c01467);   
		addRow(tab3, Mod2002025Key.UT1468.getDescription(), c01468);   
		addRow(tab3, Mod2002025Key.UT1523.getDescription(), c01523);   
		addRow(tab3, Mod2002025Key.UT1601.getDescription(), c01601);   
		addRow(tab3, Mod2002025Key.UT1638.getDescription(), c01638);   
		addRow(tab3, Mod2002025Key.UT1639.getDescription(), c01639);   
		addRow(tab3, Mod2002025Key.UT1640.getDescription(), c01640);   
		addRow(tab3, Mod2002025Key.UT1743.getDescription(), c01743);   
		addRow(tab3, Mod2002025Key.UT1909.getDescription(), c01909);   
		addRow(tab3, Mod2002025Key.UT1910.getDescription(), c01910);   
		addRow(tab3, Mod2002025Key.UT1911.getDescription(), c01911);   
		addRow(tab3, Mod2002025Key.UT1912.getDescription(), c01912);   
		addRow(tab3, Mod2002025Key.UT1934.getDescription(), c01934); 
		
		// Botones Aceptar y Cancelar
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());

		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText(AON.MSG.accept());
		acceptButton.addClickHandler(event -> onAccept());
		
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> onCancel());
		
		buttonsPanel.add(acceptButton);
		buttonsPanel.add(cancelButton);
		rootPanel.add(buttonsPanel);
		
		add(rootPanel);
		
	}
	
	private Label getSubtitle(String text) {
		Label subtitle = new Label(text);
		subtitle.setStyleName(AON.CSS.aonMarginTop());
		subtitle.addStyleName(AON.CSS.aonBold());
		subtitle.addStyleName(AON.CSS.aonWidthAlmostAll());
		subtitle.addStyleName(AON.CSS.aonBlockCenter());
		subtitle.addStyleName(AON.CSS.aonBorderBottom());
		return subtitle;
	}
	
	private void addRow(AonDisplayTable tab, String label, Widget widget) {
		addRow(tab, label, widget, AON.CSS.aonWidthAuto());
	}
	
	private void addRow(AonDisplayTable tab, String label, Widget widget, String labelWidth) {
		tab.addRow()
			.addCell(new Label(label), labelWidth, AON.CSS.aonBorderBottom())
			.addCell(widget);
	}
	
	private void doubleValueChanged(AonDoubleBox text) {
		if (AonStringUtils.isEmpty(text.getText())) {
			text.setValue(0.0,false);
		}
		setModified(true);
	}

}