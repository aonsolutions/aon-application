package net.aonsolutions.aon.gwt.udapa.client.quality;

import java.util.LinkedList;

import javax.swing.text.StyledEditorKit.BoldAction;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.gwt.udapa.shared.quality.Clean;
import net.aonsolutions.aon.gwt.udapa.shared.quality.CleanAptitude;
import net.aonsolutions.aon.gwt.udapa.shared.quality.CulinaryAptitude;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Defects;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Destiny;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Plague;

public class QualitySheet extends Composite{
	
	interface Binder extends UiBinder<Widget, QualitySheet> {}
	private static final Binder binder = GWT.create(Binder.class);
	private UdapaQuality parent;
	
	// ---------- DATOS TRANSPORTE
	@UiField(provided = true) FlexTable transportData;
	@UiField(provided = true) FlexTable productData;
	@UiField(provided = true) FlexTable qualityTest;
	@UiField(provided = true) FlexTable caliberControl;
	@UiField(provided = true) FlexTable defectControl;
	@UiField TextArea observations;
	
	
	public API getAPI() {
		return parent.getAPI();
	}

	public QualitySheet(UdapaQuality parent) {
		this.parent = parent;
		transportData();
		productData();
		qualityTest();
		caliberControl();
		defectControl();
		
		initWidget(binder.createAndBindUi(this));
	}
	
	private void transportData() {
		transportData = new FlexTable();
		transportData.setWidget(0, 0, new Label("Agencia"));
		transportData.setWidget(0, 1, new Label("SEUR"));
		transportData.getFlexCellFormatter().setColSpan(0, 1, 3);
		transportData.setWidget(0, 2, new Label("Bruto"));
		transportData.setWidget(0, 3, new Label("XXXXX"));
		
		transportData.setWidget(1, 0, new Label("Conductor"));
		transportData.setWidget(1, 1, new Label("Juan Benito Martinez"));
		transportData.getFlexCellFormatter().setColSpan(1, 1, 3);
		transportData.setWidget(1, 2, new Label("Tara"));
		transportData.setWidget(1, 3, new Label("YYYYY"));
		
		transportData.setWidget(2, 0, new Label("DNI"));
		transportData.setWidget(2, 1, new Label("12345678-X"));
		transportData.setWidget(2, 2, new Label("Matricula"));
		transportData.setWidget(2, 3, new Label("VI-5678-X"));
		transportData.setWidget(2, 4, new Label("Peso Neto"));
		transportData.setWidget(2, 5, new Label("ZZZZZ"));
	}
	
	private void productData() {
		productData = new FlexTable();
		productData.setWidget(0, 0, new Label("Product"));
		productData.setWidget(0, 1, new Label("Descripcion... #123124"));
		productData.setWidget(0, 2, new Label("Cantidad"));
		productData.setWidget(0, 3, new Label("XXXXX"));
	
		productData.setWidget(1, 0, new Label("Proveedor"));
		productData.setWidget(1, 1, new Label("PACO DELICIAS"));
		productData.setWidget(1, 2, new Label("Destino"));
		productData.setWidget(1, 3, listBox(Destiny.valueLinkedList()));
		
		productData.setWidget(2, 0, new Label("Origen"));
		productData.setWidget(2, 1, new Label("c/ madre vedruna"));
		productData.getFlexCellFormatter().setColSpan(2, 1, 3);

		productData.setWidget(3, 0, new Label(""));
		productData.setWidget(3, 1, new Label("vitoria 01008"));
		productData.getFlexCellFormatter().setColSpan(3, 1, 3);
	}
	
	private void qualityTest() {
		qualityTest = new FlexTable();
		qualityTest.setWidget(0, 0, new Label("Temperatura"));
		qualityTest.setWidget(0, 1, textBox());
		qualityTest.setWidget(0, 2, new Label("Aptitud Culinaria para Fritura"));
		qualityTest.setWidget(0, 3, listBox(CulinaryAptitude.valueLinkedList()));
		
		qualityTest.setWidget(1, 0, new Label("Limpieza"));
		qualityTest.setWidget(1, 1, listBox(Clean.valueLinkedList()));
		qualityTest.setWidget(1, 2, new Label("Aptitud Culinaria para Cocido"));
		qualityTest.setWidget(1, 3, listBox(CulinaryAptitude.valueLinkedList()));
		
		qualityTest.setWidget(2, 0, new Label("Plaga"));
		qualityTest.setWidget(2, 1, listBox(Plague.valueLinkedList()));
		qualityTest.setWidget(2, 2, new Label("Aptitud Limpieza"));
		qualityTest.setWidget(2, 3, listBox(CleanAptitude.valueLinkedList()));
		
		qualityTest.setWidget(3, 0, new Label("Materia Seca"));
		qualityTest.setWidget(3, 1, textBox());
		qualityTest.getFlexCellFormatter().setColSpan(3, 1, 3);
	}
	
	private void caliberControl() {
		caliberControl = new FlexTable();
		caliberControl.setWidget(0, 0, new Label("Peso Muestra"));
		caliberControl.setWidget(0, 1, textBox());
		caliberControl.getFlexCellFormatter().setColSpan(0, 1, 5);
	
		caliberControl.setWidget(1, 0, new Label(""));
		caliberControl.setWidget(1, 1, boldLabel("Peso"));
		caliberControl.setWidget(1, 2, boldLabel("%"));
		caliberControl.setWidget(1, 3, new Label(""));
		caliberControl.setWidget(1, 4, boldLabel("Peso"));
		caliberControl.setWidget(1, 5, boldLabel("%"));
		
		String[] calibresConsumo ={
				"Calibre < 45",
				"Calibre 45-50",
				"Calibre > 80",
				"Sin Calibrar",
				"Tierras Piedra"
		};
		
		String[] calibresSiembra ={
				"Calibre 28-35",
				"Calibre 35-45",
				"Calibre 45-50",
				"Calibre 50-55",
				"Calibre > 55",
				"Sin Calibrar",
				"Tierras Piedra"
		};
		
		for(Integer i = 0 ; i < 5 ; i++){
			caliberControl.setWidget(2+i, 0, new Label(calibresConsumo[i]));
			caliberControl.setWidget(2+i, 1, textBox());
			caliberControl.setWidget(2+i, 2, new Label("XX"));
		}
		
		for(Integer j = 0; j < 7; j++){
			caliberControl.setWidget(2+j, 3,  new Label(calibresSiembra[j]));
			caliberControl.setWidget(2+j, 4, textBox());
			caliberControl.setWidget(2+j, 5, new Label("XX"));
		}
		
		caliberControl.setWidget(7, 0, boldLabel("Total"));
		caliberControl.setWidget(7, 1, new Label("XX"));
		caliberControl.setWidget(7, 2, new Label("XX"));
		
		caliberControl.setWidget(10, 3, boldLabel("Total"));
		caliberControl.setWidget(10, 4, new Label("XX"));
		caliberControl.setWidget(10, 5, new Label("XX"));
	}
	
	private void defectControl() {
		defectControl = new FlexTable();
		defectControl.setWidget(0, 0, new Label("Peso Muestra"));
		TextBox tb = (TextBox) caliberControl.getWidget(0, 1);
		defectControl.setWidget(0, 1, new Label(tb.getValue()));
		defectControl.getFlexCellFormatter().setColSpan(0, 1, 5);
	
		defectControl.setWidget(1, 0, boldLabel("Defecto"));
		defectControl.setWidget(1, 1, boldLabel("Peso"));
		defectControl.setWidget(1, 2, boldLabel("%"));

		for(Integer i = 0 ; i < 5 ; i++){
			defectControl.setWidget(2+i, 0, listBox(Defects.valueLinkedList()));
			defectControl.setWidget(2+i, 1, textBox());
			defectControl.setWidget(2+i, 2, new Label("XX"));
		}
		
		defectControl.setWidget(8, 0, boldLabel("Total"));
		defectControl.setWidget(8, 1, new Label("XX"));
		defectControl.setWidget(8, 2, new Label("XX"));
	}
	
	private Label boldLabel(String  name){
		Label label = new Label(name);
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		return label;
	}
	
	private ListBox listBox(LinkedList<String> options){
		ListBox listBox = new ListBox();
		listBox.setStyleName(AON.AON_CSS.aonTextBox());
		listBox.addItem("-");
		options.stream().forEach(o -> listBox.addItem(o));
		return listBox;
	}
	
	private TextBox textBox(){
		TextBox textBox = new TextBox();
		textBox.setStyleName(AON.AON_CSS.aonTextBox());
		return textBox;
	}
}
