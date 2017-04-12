package net.aonsolutions.aon.gwt.udapa.client.quality;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.gwt.udapa.client.IUdapa;
import net.aonsolutions.aon.gwt.udapa.client.IUdapaAsync;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Clean;
import net.aonsolutions.aon.gwt.udapa.shared.quality.CleanAptitude;
import net.aonsolutions.aon.gwt.udapa.shared.quality.CulinaryAptitude;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Defects;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Destiny;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Plague;
import net.aonsolutions.aon.gwt.udapa.shared.quality.QualitySheetCode;
import net.aonsolutions.aon.gwt.udapa.shared.quality.QualitySheetConstants;
import net.aonsolutions.aon.gwt.udapa.shared.quality.WidgetStack;
import net.aonsolutions.aon.gwt.udapa.shared.quality.WidgetType;

public class QualitySheet extends Composite{
	
	interface Binder extends UiBinder<Widget, QualitySheet> {}
	private static final Binder binder = GWT.create(Binder.class);
	private UdapaQuality parent;
	final IUdapaAsync impl = GWT.create(IUdapa.class);
	// hashMap || jsonobject
	HashMap<String, String> map = new HashMap<>();
	
	Stack<WidgetStack> undo = new Stack<>();
	Stack<WidgetStack> redo = new Stack<>();
	
	LinkedList<WidgetStack> calculated = new LinkedList<>();
	// ---------- DATOS TRANSPORTE
	@UiField(provided = true) FlexTable transportData;
	@UiField(provided = true) FlexTable productData;
	@UiField(provided = true) FlexTable qualityTest;
	@UiField(provided = true) FlexTable caliberControl;
	@UiField(provided = true) FlexTable observation;
	
	
	public API getAPI() {
		return parent.getAPI();
	}

	public QualitySheet(UdapaQuality parent) {
		this.parent = parent;
		transportData = new FlexTable();
		productData = new FlexTable();
		qualityTest = new FlexTable();
		caliberControl = new FlexTable();
		observation = new FlexTable();
		
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONKEYDOWN) {
					NativeEvent ne = event.getNativeEvent();
					if (ne.getCtrlKey() && ne.getKeyCode() == 'Z') {
						event.cancel();
						if(!undo.empty()){
							WidgetStack ws = undo.pop();
							if(WidgetType.TEXTBOX.equals(ws.getWidgetType())){
								TextBox tb = (TextBox) ws.getWidget();
								tb.setValue(ws.getPrevValue());
							} else if(WidgetType.LISTBOX.equals(ws.getWidgetType())){
								ListBox lb = (ListBox) ws.getWidget();
								lb.setSelectedIndex(ws.getIntPrevValue());
							} else if(WidgetType.TEXTAREA.equals(ws.getWidgetType())){
								TextArea lb = (TextArea) ws.getWidget();
								lb.setValue(ws.getPrevValue());
							}  else if(WidgetType.DOUBLEBOX.equals(ws.getWidgetType())){
								DoubleBox db = (DoubleBox) ws.getWidget();
								db.setValue(ws.getDoublePrevValue());
							}
							redo.add(ws);
							impl.updateValue(ws.getCode(),ws.getValue(), map, new AsyncCallback<HashMap<String, String>>() {

								@Override public void onFailure(Throwable caught) {}

								@Override
								public void onSuccess(HashMap<String, String> result) {
									map = result;
									calculated();
								}
							});
						}
					} else if (ne.getCtrlKey() && ne.getKeyCode() == 'Y') {
						event.cancel();
					}
				}
			}
		});
		
		impl.getValues(new AsyncCallback<HashMap<String,String>>() {
			
			@Override
			public void onSuccess(HashMap<String, String> result) {		
				map = result;
				
				transportData();
				productData();
				qualityTest();
				caliberControl();
				observation();
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
		
		initWidget(binder.createAndBindUi(this));
	}
	
	private void transportData() {
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
		
		transportData.setWidget(2, 0, new Label("DNI")); setWidth(transportData, 2, 0);
		transportData.setWidget(2, 1, new Label("12345678-X")); setWidth(transportData, 2, 1);
		transportData.setWidget(2, 2, new Label("Matricula"));setWidth(transportData, 2, 2);
		transportData.setWidget(2, 3, new Label("VI-5678-X"));setWidth(transportData, 2, 3); 
		transportData.setWidget(2, 4, boldLabel("Peso Neto"));setWidth(transportData, 2, 4);
		transportData.setWidget(2, 5, boldLabel("ZZZZZ")); setWidth(transportData, 2, 5);
	}
	
	private void productData() {
		setWidth(productData, 0, 0);
		setWidth(productData, 0, 1);
		setWidth(productData, 0, 2);
		setWidth(productData, 0, 3);
		setWidth(productData, 0, 4);
		setWidth(productData, 0, 5);
		
		productData.setWidget(0, 0, new Label("Producto"));
		productData.setWidget(0, 1, boldLabel("Descripcion... #123124"));
		productData.setWidget(0, 4, new Label("Cantidad"));
		productData.setWidget(0, 5, new Label("XXXXX"));
	
		productData.setWidget(1, 0, new Label("Proveedor"));
		productData.setWidget(1, 1, new Label("PACO DELICIAS"));
		productData.setWidget(1, 4, new Label("Destino"));
		productData.setWidget(1, 5, listBox(Destiny.valueLinkedList(), QualitySheetCode.UFQDP1));
		
		productData.setWidget(2, 0, new Label("Origen"));
		productData.setWidget(2, 1, new Label("c/ madre vedruna"));
		productData.setWidget(2, 4, new Label("Rechazado"));
		productData.setWidget(2, 5, new CheckBox());

		
		productData.setWidget(3, 0, new Label(""));
		productData.setWidget(3, 1, new Label("vitoria 01008"));
		productData.getFlexCellFormatter().setColSpan(3, 1, 3);
	}
	
	private void qualityTest() {
		qualityTest.setWidth("800px");
		qualityTest.setWidget(0, 0, new Label("Temperatura")); setWidth(qualityTest, 0, 0);
		qualityTest.setWidget(0, 1, doubleBox(QualitySheetCode.UFQAC1)); setWidth(qualityTest, 0, 1);
		qualityTest.setWidget(0, 2, new Label("Aptitud Culinaria para Fritura")); setWidth(qualityTest, 0, 2);
		qualityTest.setWidget(0, 3, listBox(CulinaryAptitude.valueLinkedList(), QualitySheetCode.UFQAC2)); setWidth(qualityTest, 0, 3);
		
		qualityTest.setWidget(1, 0, new Label("Plaga"));
		qualityTest.setWidget(1, 1, listBox(Plague.valueLinkedList(), QualitySheetCode.UFQAC5));

		qualityTest.setWidget(1, 2, new Label("Aptitud Culinaria para Cocido"));
		qualityTest.setWidget(1, 3, listBox(CulinaryAptitude.valueLinkedList(), QualitySheetCode.UFQAC4));

		qualityTest.setWidget(2, 0, new Label("Limpieza"));
		qualityTest.setWidget(2, 1, listBox(Clean.valueLinkedList(), QualitySheetCode.UFQAC3));
		
		qualityTest.setWidget(2, 2, new Label("Aptitud Lavado"));
		qualityTest.setWidget(2, 3, listBox(CleanAptitude.valueLinkedList(), QualitySheetCode.UFQAC6));
		
		qualityTest.setWidget(3, 0, new Label("Materia Seca"));
		qualityTest.setWidget(3, 1, doubleBox(QualitySheetCode.UFQAC7));
		qualityTest.getFlexCellFormatter().setColSpan(3, 1, 3);
	}
	
	private void caliberControl() {
		caliberControl.setWidth("1000px");
		caliberControl.setWidget(0, 0, new Label("Peso Muestra"));
		 
		RadioButton consumo = new RadioButton("Consumo");
		consumo.setText("Consumo");
		consumo.getElement().getStyle().setPaddingLeft(25, Unit.PX);
		consumo.setValue(true);
		RadioButton siembra = new RadioButton("Siembra");
		siembra.setValue(false);
		siembra.setText("Siembra");
		consumo.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				siembra.setValue(!event.getValue());
				if(event.getValue()){
					calibresConsumo();
				} else calibresSiembra();
			}
		});
		
		siembra.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				consumo.setValue(!event.getValue());
				if(event.getValue()){
					calibresSiembra();
				} else calibresConsumo();
			}
		});
		
		HorizontalPanel hp0 = new HorizontalPanel();
		hp0.add(doubleBox(QualitySheetCode.UFQCC01));
		hp0.add(consumo);
		hp0.add(siembra);
		caliberControl.setWidget(0, 1, hp0);
		
		caliberControl.setWidget(1, 0, new Label(""));	setWidth(caliberControl, 1, 0, "200px");
		HorizontalPanel hp1 =  new HorizontalPanel();
		Label lpeso = boldLabel("Peso");
		lpeso.setWidth("52px");
		Label lpercent = boldLabel("%");
		lpercent.getElement().getStyle().setPaddingLeft(25, Unit.PX);
		hp1.add(lpeso);
		hp1.add(lpercent);
		
		caliberControl.setWidget(1, 1, hp1);setWidth(caliberControl, 1, 1, "300px");
		caliberControl.setWidget(1, 2, boldLabel("Defecto")); setWidth(caliberControl, 1, 2, "200px");
		HorizontalPanel hp2 =  new HorizontalPanel();
		Label lpeso2 = boldLabel("Peso");
		lpeso2.setWidth("52px");
		Label lpercent2 = boldLabel("%");
		lpercent2.getElement().getStyle().setPaddingLeft(25, Unit.PX);
		hp2.add(lpeso2);
		hp2.add(lpercent2);
		caliberControl.setWidget(1, 3, hp2); setWidth(caliberControl, 1, 3, "300px");
		
		if(consumo.getValue()){
			calibresConsumo();
		} else calibresSiembra();
		
		defectControl();	
	}
	
	private void calibresConsumo() {
		String[] calibresConsumo ={
				"Calibre < 45",
				"Calibre 45-50",
				"Calibre > 80",
				"Sin Calibrar",
				"Tierras Piedra"
		};
		
		for(Integer i = 0 ; i < QualitySheetConstants.CALIBER.length ; i = i + 2){
			Integer c = i/2;
			caliberControl.setWidget(2+c, 0, new Label(calibresConsumo[c]));
			HorizontalPanel hp = new HorizontalPanel();
			DoubleBox db = doubleBox(QualitySheetConstants.CALIBER[i]);
			Label label = new Label(map.containsKey(QualitySheetConstants.CALIBER[i + 1])
					? map.get(QualitySheetConstants.CALIBER[i + 1]) : "0.0");
			label.getElement().getStyle().setPaddingLeft(25, Unit.PX);
			WidgetStack ws = new WidgetStack(label, QualitySheetConstants.CALIBER[i + 1]);
			calculated.add(ws);
			hp.add(db);
			hp.add(label);
			caliberControl.setWidget(2+c, 1, hp);
		}
		
		caliberControl.setWidget(7, 0, boldLabel("Total"));
		Label label = boldLabel( map.containsKey(QualitySheetCode.UFQCC14.getName()) 
				? map.get(QualitySheetCode.UFQCC14.getName()) : "0.0");
		label.setWidth("52px");
		WidgetStack ws =new WidgetStack(label, QualitySheetCode.UFQCC14);
		calculated.add(ws);
		
		Label label2 = boldLabel( map.containsKey(QualitySheetCode.UFQCC141.getName()) 
				? map.get(QualitySheetCode.UFQCC141.getName()) : "0.0");
		label2.getElement().getStyle().setPaddingLeft(25, Unit.PX);
		WidgetStack ws2 =new WidgetStack(label2, QualitySheetCode.UFQCC141);
		calculated.add(ws2);
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.add(label);
		hp2.add(label2);
		caliberControl.setWidget(7, 1, hp2);
		
		caliberControl.setWidget(8, 0, new Label());
		caliberControl.setWidget(8, 1, new Label());
		
		caliberControl.setWidget(9, 0, new Label());
		caliberControl.setWidget(9, 1, new Label());		
	}
	
	private void calibresSiembra(){
		String[] calibresSiembra ={
				"Calibre 28-35",
				"Calibre 35-45",
				"Calibre 45-50",
				"Calibre 50-55",
				"Calibre > 55",
				"Sin Calibrar",
				"Tierras Piedra"
		};
	
		for(Integer j = 0; j < QualitySheetConstants.CALIBER_SIEMBRA.length; j = j + 2){
			Integer c = j/2;
			caliberControl.setWidget(2+c, 0,  new Label(calibresSiembra[c]));
			DoubleBox db = doubleBox(QualitySheetConstants.CALIBER_SIEMBRA[j]);
			Label label = new Label(map.containsKey(QualitySheetConstants.CALIBER_SIEMBRA[j+1])
					? map.get(QualitySheetConstants.CALIBER_SIEMBRA[j+1]) : "0.0");
			label.getElement().getStyle().setPaddingLeft(25, Unit.PX);
			WidgetStack ws =new WidgetStack(label, QualitySheetConstants.CALIBER_SIEMBRA[j + 1]);
			calculated.add(ws);
			
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(db);
			hp.add(label);
			caliberControl.setWidget(2+c, 1, hp);
		}
		
		caliberControl.setWidget(9, 0, boldLabel("Total"));
		Label label3 = boldLabel( map.containsKey(QualitySheetCode.UFQCC15.getName()) 
				? map.get(QualitySheetCode.UFQCC15.getName()) : "0.0");
		label3.setWidth("52px");
		WidgetStack ws3 =new WidgetStack(label3, QualitySheetCode.UFQCC15);
		calculated.add(ws3);
		
		
		Label label4 = boldLabel( map.containsKey(QualitySheetCode.UFQCC151.getName()) 
				? map.get(QualitySheetCode.UFQCC151.getName()) : "0.0");
		label4.getElement().getStyle().setPaddingLeft(25, Unit.PX);
		WidgetStack ws4 =new WidgetStack(label4, QualitySheetCode.UFQCC151);
		calculated.add(ws4);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.add(label3);
		hp2.add(label4);
		caliberControl.setWidget(9, 1, hp2);
	}
	
	private void defectControl() {
		for(Integer i = 0; i < QualitySheetConstants.DEFECTS.length; i = i + 2){
			Integer c = i/2;
			caliberControl.setWidget(2+c, 2, new Label(Defects.values()[c].getName()));
			
			Label label = new Label(map.containsKey(QualitySheetConstants.DEFECTS[i+1])
					 ? map.get(QualitySheetConstants.DEFECTS[i+1]) :  "0.0");
			label.getElement().getStyle().setPaddingLeft(25, Unit.PX);
			WidgetStack ws =new WidgetStack(label, QualitySheetConstants.DEFECTS[i + 1]);
			calculated.add(ws);
			
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(doubleBox(QualitySheetConstants.DEFECTS[i]));
			hp.add(label);
			caliberControl.setWidget(2+c, 3, hp);
		}
		Integer tIndex = 2 + Defects.values().length;
		caliberControl.setWidget(tIndex, 2, boldLabel("Total")); 
		
		Label label = boldLabel(map.containsKey(QualitySheetCode.UFQCD11.getName()) 
				? map.get(QualitySheetCode.UFQCD11.getName()) : "0.0");
		label.setWidth("52px");
		WidgetStack ws =new WidgetStack(label, QualitySheetCode.UFQCD11);
		calculated.add(ws);
		
		Label label2 = boldLabel( map.containsKey(QualitySheetCode.UFQCD111.getName()) 
				? map.get(QualitySheetCode.UFQCD111.getName()) : "0.0");
		label2.getElement().getStyle().setPaddingLeft(25, Unit.PX);
		WidgetStack ws2 =new WidgetStack(label2, QualitySheetCode.UFQCD111);
		calculated.add(ws2);
		
		HorizontalPanel hp2 = new HorizontalPanel();
		hp2.add(label);
		hp2.add(label2);
		caliberControl.setWidget(tIndex, 3, hp2);
	}
	
	private void setWidth(FlexTable tab, Integer row, Integer col){
		tab.getFlexCellFormatter().setWidth(row, col, "200px");
	}
	
	private void setWidth(FlexTable tab, Integer row, Integer col, String size){
		tab.getFlexCellFormatter().setWidth(row, col, size);
	}
	
	private void observation() {
		TextArea ta = new TextArea();
		ta.setValue(map.containsKey(QualitySheetCode.UFQO.getName()) 
			? map.get(QualitySheetCode.UFQO.getName()) : "");
		ta.setWidth("100%");
		ta.setHeight("50px");
		ta.addValueChangeHandler(new AonValueChangeHandler<String>(ta) {

			@Override
			public void onValueChange(String value, String prevValue) {
				WidgetStack ws = new WidgetStack(ta, QualitySheetCode.UFQO);
				ws.setWidgetType(WidgetType.TEXTAREA);
				ws.setPrevValue(prevValue);
				ws.setValue(value);
				undo.push(ws);
			}
		});
		observation.setWidget(0, 0, ta);
	}
	
	private void calculated() {
		calculated.stream().forEach(ws -> {
			Label label = (Label) ws.getWidget();
			label.setText(map.get(ws.getCode().getName()));
		});
	}
	
	private Label boldLabel(String  name){
		Label label = new Label(name);
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		return label;
	}
	
	private ListBox listBox(LinkedList<String> options, QualitySheetCode code){
		ListBox listBox = new ListBox();
		listBox.setStyleName(AON.AON_CSS.aonTextBox());
		listBox.addItem("-");
		options.stream().forEach(o -> listBox.addItem(o));
		Double d  = Double.parseDouble(map.get(code.getName()));
		Integer index = map.containsKey(code.getName()) 
			? d.intValue() : 0;
		listBox.setSelectedIndex(index);
		listBox.addChangeHandler(new AonListBoxChangeHandler(listBox) {
			
			@Override
			public void onChange(Integer prevIndex) {
				WidgetStack ws = new WidgetStack(listBox, code);
				ws.setWidgetType(WidgetType.LISTBOX);
				ws.setPrevValue(prevIndex);
				ws.setValue(listBox.getSelectedIndex());
				undo.push(ws);
			}
		});
		
		return listBox;
	}
		
	private TextBox textBox(QualitySheetCode code){
		TextBox textBox = new TextBox();
		textBox.setValue(map.containsKey(code.getName()) 
			? map.get(code.getName()) : "");
		textBox.setStyleName(AON.AON_CSS.aonTextBox());
		textBox.addValueChangeHandler(new AonValueChangeHandler<String>(textBox) {

			@Override
			public void onValueChange(String value, String prevValue) {
				WidgetStack ws = new WidgetStack(textBox, code);
				ws.setWidgetType(WidgetType.TEXTBOX);
				ws.setPrevValue(prevValue);
				ws.setValue(value);
				undo.push(ws);
				impl.updateValue(code, value, map, new AsyncCallback<HashMap<String, String>>() {

					@Override public void onFailure(Throwable caught) {}

					@Override
					public void onSuccess(HashMap<String, String> result) {
						map = result;
						calculated();
					}
				});
			}
		});
		return textBox;
	}
	
	private DoubleBox doubleBox(QualitySheetCode code){
		DoubleBox doubleBox = new DoubleBox();
		doubleBox.setWidth("50px");
		doubleBox.setValue(map.containsKey(code.getName()) 
			? Double.parseDouble(map.get(code.getName())) : 0.0);
		doubleBox.setStyleName(AON.AON_CSS.aonTextBox());
		doubleBox.addValueChangeHandler(new AonValueChangeHandler<Double>(doubleBox) {

			@Override
			public void onValueChange(Double value, Double prevValue) {
				if(value == null) {
					value = 0.0;
					doubleBox.setValue(0.0);
				}
				WidgetStack ws = new WidgetStack(doubleBox, code);
				ws.setWidgetType(WidgetType.DOUBLEBOX);
				ws.setPrevValue(prevValue);
				ws.setValue(value);
				undo.push(ws);
				impl.updateValue(code, value.toString(), map, new AsyncCallback<HashMap<String, String>>() {

					@Override public void onFailure(Throwable caught) {}

					@Override
					public void onSuccess(HashMap<String, String> result) {
						map = result;
						calculated();
					}
				});
			}
		});
		return doubleBox;
	}
}
