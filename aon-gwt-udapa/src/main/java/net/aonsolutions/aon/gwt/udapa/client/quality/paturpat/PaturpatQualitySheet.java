package net.aonsolutions.aon.gwt.udapa.client.quality.paturpat;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.common.JsDataResponse;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import net.aonsolutions.aon.gwt.udapa.client.IUdapa;
import net.aonsolutions.aon.gwt.udapa.client.IUdapaAsync;
import net.aonsolutions.aon.gwt.udapa.client.Utils;
import net.aonsolutions.aon.gwt.udapa.client.quality.AonListBoxChangeHandler;
import net.aonsolutions.aon.gwt.udapa.client.quality.AonValueChangeHandler;
import net.aonsolutions.aon.gwt.udapa.client.quality.FootPanel;
import net.aonsolutions.aon.gwt.udapa.shared.quality.WidgetStack;
import net.aonsolutions.aon.gwt.udapa.shared.quality.WidgetType;
import net.aonsolutions.aon.gwt.udapa.shared.quality.paturpat.Color;
import net.aonsolutions.aon.gwt.udapa.shared.quality.paturpat.Olor;
import net.aonsolutions.aon.gwt.udapa.shared.quality.paturpat.QualitySheetCode;
import net.aonsolutions.aon.gwt.udapa.shared.quality.paturpat.Sabor;
import net.aonsolutions.aon.gwt.udapa.shared.quality.paturpat.Textura;

public class PaturpatQualitySheet extends Composite{
	
	interface Binder extends UiBinder<Widget, PaturpatQualitySheet> {}
	private static final Binder binder = GWT.create(Binder.class);
	private PaturpatQuality parent;
	public JsDataResponse dataResponse;
	final IUdapaAsync impl = GWT.create(IUdapa.class);
	// hashMap || jsonobject
	HashMap<String, String> map = new HashMap<>();
	
	@UiField Label warehouse;
	@UiField Label number;
	@UiField Label date;
	@UiField Label hour;
	
	@UiField(provided = true) FlexTable productData;
	@UiField(provided = true) FlexTable qualityTest;
	@UiField(provided = true) FlexTable observation;
	
	@UiField SplitLayoutPanel contentSplitLayoutPanel;
	
	public API getAPI() {
		return parent.getAPI();
	}
	
	public AonData getAonData() {
		return parent.getAonData();
	}
	
	public JsDataResponse getDataResponse() {
		return dataResponse;
	}
	
	
	public HashMap<String, String> getMap() {
		return map;
	}

	public PaturpatQualitySheet(PaturpatQuality parent, JsDataResponse dataResponse) {
		this.parent = parent;
		this.dataResponse = dataResponse;
		productData = new FlexTable();
		qualityTest = new FlexTable();
		observation = new FlexTable();
		
		
		String id = getDataResponse().getId() + "";
		String domainName = parent.getAonData().getDomain().getName();
		Integer domainId = parent.getAonData().getDomain().getId();
		impl.getPaturpatValues(domainName, domainId, Integer.parseInt(id), new AsyncCallback<HashMap<String,String>>() {
			
			@Override
			public void onSuccess(HashMap<String, String> result) {		
				map = result;
				
				warehouse.setText(map.get("warehouse"));
				number.setText(getDataResponse().getCode());

				Date issueDate = Utils.parseDateTime(getDataResponse().getDate());
				date.setText(Utils.formatDate(issueDate));
				hour.setText(Utils.formatTime(issueDate));

				productData();
				qualityTest();
				observation();
			}
			
			@Override public void onFailure(Throwable caught) {
				//Window.alert(caught.getMessage());
			}
		});
		initWidget(binder.createAndBindUi(this));
	}
	
	private void productData() {
		setWidth(productData, 0, 0);
		setWidth(productData, 0, 1);
		setWidth(productData, 0, 2);
		setWidth(productData, 0, 3);
		setWidth(productData, 0, 4);
		setWidth(productData, 0, 5);
		
		productData.setWidget(0, 0, new Label("Producto"));
		productData.setWidget(0, 1, boldLabel(getDataResponse().getProduct())); // map.get("product_description")));
		productData.setWidget(0, 4, new Label(""));//"Cantidad"));
		productData.setWidget(0, 5, new Label(""));//map.get("product_quantity")));
	/*
		productData.setWidget(1, 0, new Label("Proveedor"));
		productData.setWidget(1, 1, new Label(map.get("product_supplier")));
		productData.getFlexCellFormatter().setColSpan(1, 1, 3);

		productData.setWidget(2, 0, new Label("Origen"));
		productData.setWidget(2, 1, new Label(map.get("full_address")));
		productData.getFlexCellFormatter().setColSpan(2, 1, 3);
		
		productData.setWidget(3, 0, new Label(""));
		productData.setWidget(3, 1, new Label(map.get("end_address")));
		productData.getFlexCellFormatter().setColSpan(3, 1, 3);
*/
	}
	
	private void qualityTest() {
		qualityTest.setWidth("800px");
		qualityTest.setWidget(0, 0, new Label("Olor")); setWidth(qualityTest, 0, 0);
		qualityTest.setWidget(0, 1, listBox(Olor.valueLinkedList(), QualitySheetCode.PFQAC1));
		setWidth(qualityTest, 0, 1);
		qualityTest.setWidget(0, 2, new Label("Sabor")); setWidth(qualityTest, 0, 2);
		qualityTest.setWidget(0, 3, listBox(Sabor.valueLinkedList(), QualitySheetCode.PFQAC2));
		setWidth(qualityTest, 0, 3);
		qualityTest.setWidget(1, 0, new Label("Color"));
		qualityTest.setWidget(1, 1, listBox(Color.valueLinkedList(), QualitySheetCode.PFQAC3));
		qualityTest.setWidget(1, 2, new Label("Textura"));
		qualityTest.setWidget(1, 3, listBox(Textura.valueLinkedList(), QualitySheetCode.PFQAC4));
		
		qualityTest.setWidget(2, 0, new Label(""));
		qualityTest.setWidget(2, 1, new Label("%"));
		qualityTest.setWidget(2, 2, new Label(""));
		qualityTest.setWidget(2, 3, new Label(""));
		
		qualityTest.setWidget(3, 0, new Label("Defectos >3mm"));
		qualityTest.setWidget(3, 1, doubleBox(QualitySheetCode.PFQAC5));
		Double value1 = Double.parseDouble(map.get(QualitySheetCode.PFQAC5.getName()));
		String text1 = value1 <= 3 ? "APTO" : "NO APTO";
		qualityTest.setWidget(3, 2, new Label(text1));
		qualityTest.setWidget(3, 3, new Label(""));
		
		qualityTest.setWidget(4, 0, new Label("Defectos <3mm"));
		qualityTest.setWidget(4, 1, doubleBox(QualitySheetCode.PFQAC6));
		Double value2 = Double.parseDouble(map.get(QualitySheetCode.PFQAC6.getName()));
		String text2 = value2 < 7 ? "APTO" : "NO APTO";
		qualityTest.setWidget(4, 2, new Label(text2));
		qualityTest.setWidget(4, 3, new Label(""));
		
		qualityTest.setWidget(5, 0, new Label("pH"));
		qualityTest.setWidget(5, 1, doubleBox(QualitySheetCode.PFQAC7));
		qualityTest.setWidget(5, 2, new Label(""));
		qualityTest.setWidget(5, 3, new Label(""));
	
		qualityTest.setWidget(6, 0, new Label("Concentraci\u00f3n Sal"));
		qualityTest.setWidget(6, 1, doubleBox(QualitySheetCode.PFQAC8));
		Double value3 = Double.parseDouble(map.get(QualitySheetCode.PFQAC8.getName()));
		String text3 = value3 >= 0.95 ? "APTO" : "NO APTO";
		qualityTest.setWidget(6, 2, new Label(text3));
		qualityTest.setWidget(6, 3, new Label(""));
	
		qualityTest.setWidget(7, 0, new Label("Temperatura"));
		qualityTest.setWidget(7, 1, doubleBox(QualitySheetCode.PFQAC11));
		Double value4 = Double.parseDouble(map.get(QualitySheetCode.PFQAC11.getName()));
		String text4 = value4 >= 2 && value4 <= 8 ? "APTO" : "NO APTO";
		qualityTest.setWidget(7, 2, new Label(text4));
		qualityTest.setWidget(7, 3, new Label(""));
		
		qualityTest.setWidget(8, 0, new Label("Peso Patata"));
		qualityTest.setWidget(8, 1, doubleBox(QualitySheetCode.PFQAC9));
		qualityTest.setWidget(8, 2, new Label(""));
		qualityTest.setWidget(8, 3, new Label(""));
		
		qualityTest.setWidget(9, 0, new Label("Peso Liq. Gobierno"));
		qualityTest.setWidget(9, 1, doubleBox(QualitySheetCode.PFQAC10));
		qualityTest.setWidget(9, 2, new Label(""));
		qualityTest.setWidget(9, 3, new Label(""));
	
		qualityTest.setWidget(10, 0, new Label("Fecha de Caducidad"));
		qualityTest.setWidget(10, 1, dateBox(QualitySheetCode.PFQAC12));
		qualityTest.setWidget(10, 2, new Label(""));
		qualityTest.setWidget(10, 3, new Label(""));

	}
		
	private void setWidth(FlexTable tab, Integer row, Integer col){
		tab.getFlexCellFormatter().setWidth(row, col, "200px");
	}
	
	private void observation() {
		TextArea ta = new TextArea();
		ta.setValue(map.containsKey(QualitySheetCode.PFQO.getName()) 
			? map.get(QualitySheetCode.PFQO.getName()) : "");
		ta.setWidth("100%");
		ta.setHeight("50px");
		ta.addValueChangeHandler(new AonValueChangeHandler<String>(ta) {

			@Override
			public void onValueChange(String value, String prevValue) {
				String id = getDataResponse().getId() + "";
				String domainName = parent.getAonData().getDomain().getName();
				Integer domainId = parent.getAonData().getDomain().getId();
				
				impl.updateValue(domainName, domainId, Integer.parseInt(id), QualitySheetCode.PFQO.getName(),ta.getValue(), map, new AsyncCallback<HashMap<String, String>>() {
					@Override public void onFailure(Throwable caught) {}
					@Override public void onSuccess(HashMap<String, String> result) {
						map = result;
					}
				});
			}
		});
		observation.setWidget(0, 0, ta);
	}
	
	
	
	private Label boldLabel(String  name){
		Label label = new Label(name);
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		return label;
	}
	
	protected ListBox listBox(LinkedList<String> options, QualitySheetCode code){
		ListBox listBox = new ListBox();
		listBox.setStyleName(AON.AON_CSS.aonTextBox());
		listBox.addItem("-");
		options.stream().forEach(o -> listBox.addItem(o));
		Double d = Double.parseDouble(map.get(code.getName()));
		Integer index = map.containsKey(code.getName()) 
			? d.intValue() : 0;
		listBox.setSelectedIndex(index);
		listBox.addChangeHandler(new AonListBoxChangeHandler(listBox) {
			
			@Override
			public void onChange(Integer prevIndex) {
				String id = getDataResponse().getId() + "";
				String domainName = parent.getAonData().getDomain().getName();
				Integer domainId = parent.getAonData().getDomain().getId();
				impl.updateValue(domainName, domainId,Integer.parseInt(id), code.getName(), Integer.toString(listBox.getSelectedIndex()), map, new AsyncCallback<HashMap<String, String>>() {

					@Override public void onFailure(Throwable caught) {}

					@Override
					public void onSuccess(HashMap<String, String> result) {
						map = result;
						
					}
				});
			}
		});
		return listBox;
	}
	
	protected DateBoxEx dateBox(QualitySheetCode code){
		DateBoxEx dateBox = new DateBoxEx();
		dateBox.setStyleName(AON.AON_CSS.aonTextBox());
		Window.alert(map.get(QualitySheetCode.PFQAC12.getName()));
		Window.alert(!"0.0".equals(map.get(QualitySheetCode.PFQAC12.getName())) +"");
		if(!"0.0".equals(map.get(QualitySheetCode.PFQAC12.getName()))) {
			dateBox.setValue(AonDateUtils.parseDate(map.get(QualitySheetCode.PFQAC12.getName())));
		}
		dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				String id = getDataResponse().getId() + "";
				String domainName = parent.getAonData().getDomain().getName();
				Integer domainId = parent.getAonData().getDomain().getId();
				
				impl.updateValue(domainName, domainId,Integer.parseInt(id), code.getName(), AonDateUtils.formatDate(dateBox.getValue()), map, new AsyncCallback<HashMap<String, String>>() {

					@Override public void onFailure(Throwable caught) {}

					@Override
					public void onSuccess(HashMap<String, String> result) {
						map = result;
						
					}
				});
			}
		});
		return dateBox;
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
				
				String id = dataResponse.getId() + "";
				String domainName = parent.getAonData().getDomain().getName();
				Integer domainId = parent.getAonData().getDomain().getId();
				impl.updateValue(domainName, domainId, Integer.parseInt(id), code.getName(), value.toString(), map, new AsyncCallback<HashMap<String, String>>() {

					@Override public void onFailure(Throwable caught) {}

					@Override
					public void onSuccess(HashMap<String, String> result) {
						map = result;
					}
				});
				
				if(QualitySheetCode.PFQAC5.equals(code)) {
					String text = value <= 3 ? "APTO" : "NO APTO";
					qualityTest.setWidget(3, 2, new Label(text));
					impl.updateValue(domainName, domainId, Integer.parseInt(id), QualitySheetCode.PFQAC5B.getName(), text, map, new AsyncCallback<HashMap<String, String>>() {

						@Override public void onFailure(Throwable caught) {}

						@Override
						public void onSuccess(HashMap<String, String> result) {
							map = result;
						}
					});
				} else if(QualitySheetCode.PFQAC6.equals(code)) {
					String text = value < 7 ? "APTO" : "NO APTO";
					qualityTest.setWidget(4, 2, new Label(text));
					impl.updateValue(domainName, domainId, Integer.parseInt(id), QualitySheetCode.PFQAC6B.getName(), text, map, new AsyncCallback<HashMap<String, String>>() {

						@Override public void onFailure(Throwable caught) {}

						@Override
						public void onSuccess(HashMap<String, String> result) {
							map = result;
						}
					});
				} else if(QualitySheetCode.PFQAC8.equals(code)) {
					String text = value >= 0.95 ? "APTO" : "NO APTO";
					qualityTest.setWidget(6, 2, new Label(text));
					impl.updateValue(domainName, domainId, Integer.parseInt(id), QualitySheetCode.PFQAC8B.getName(), text, map, new AsyncCallback<HashMap<String, String>>() {

						@Override public void onFailure(Throwable caught) {}

						@Override
						public void onSuccess(HashMap<String, String> result) {
							map = result;
						}
					});
				} else if(QualitySheetCode.PFQAC11.equals(code)) {
					String text = value >= 2 && value <= 8 ? "APTO" : "NO APTO";
					qualityTest.setWidget(7, 2, new Label(text));
					impl.updateValue(domainName, domainId, Integer.parseInt(id), QualitySheetCode.PFQAC11B.getName(), text, map, new AsyncCallback<HashMap<String, String>>() {

						@Override public void onFailure(Throwable caught) {}

						@Override
						public void onSuccess(HashMap<String, String> result) {
							map = result;
						}
					});
				}  
			}
		});
		return doubleBox;
	}
}
