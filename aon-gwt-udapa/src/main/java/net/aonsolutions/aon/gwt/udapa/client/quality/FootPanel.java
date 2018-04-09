package net.aonsolutions.aon.gwt.udapa.client.quality;

import java.util.HashMap;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.IronImage;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.vaadin.widget.VaadinUpload;
import com.vaadin.polymer.vaadin.widget.event.UploadSuccessEvent;
import com.vaadin.polymer.vaadin.widget.event.UploadSuccessEventHandler;

import net.aonsolutions.aon.gwt.udapa.shared.quality.Destiny;
import net.aonsolutions.aon.gwt.udapa.shared.quality.QualitySheetCode;

public class FootPanel extends Composite {

	interface Binder extends UiBinder<Widget, FootPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
		
	
	QualitySheet parent;
	
	public API getAPI() {
		return parent.getAPI();
	}
	
	public FootPanel(QualitySheet parent) {
		this.parent = parent;
		initWidget(binder.createAndBindUi(this));
		imgPanel();
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				Integer value  = event.getSelectedItem();
				if(value == 0){
					imgPanel();
					openFootPanel();
				} else  if(value == 1) {
					calculatePanel();
					openFootPanel();
				} else if(value == 2) {
					tagPanel();
					openFootPanel();
				}
			}
		});
	}

	public void imgPanel() {
		getAPI().getAttachment().getQualityImages(parent.getDataResponse().getId(), new AsyncCallback<JSON<JsAttach>>() {
			
			@Override
			public void onSuccess(JSON<JsAttach> result) {
				VerticalPanel vp = new VerticalPanel();
				vp.setWidth("100%");
				VaadinUpload upload = new VaadinUpload();
				String dataRequest = "?domain_name="+parent.getAonData().getDomain().getName() 
						+ "&domain_id="+ parent.getAonData().getDomain().getId()
						+ "&login="+ parent.getAonData().getUser().getLogin()
						+ "&id="+ parent.getDataResponse().getId()
						+ "&attach_type=" + AttachType.DATA.getName();
				upload.setTarget(GWT.getModuleBaseURL() + "uploadImages"+ dataRequest);
				upload.setAccept("image/*");
				upload.addUploadSuccessHandler(new UploadSuccessEventHandler() {
					
					@Override
					public void onUploadSuccess(UploadSuccessEvent event) {
						getAPI().getAttachment().getQualityImages(parent.getDataResponse().getId(), new AsyncCallback<JSON<JsAttach>>() {
							@Override
							public void onSuccess(JSON<JsAttach> result) {
								vp.remove(1);
								vp.add(imagePanel(result.getData().toLinkedList()));
							}
							
							@Override public void onFailure(Throwable caught) {}
						});
					}
				});

				vp.add(upload);
				vp.add(imagePanel(result.getData().toLinkedList()));
				// vp.add(new ImagePanel(result.getData()));
				imgPanel.setWidget(vp);
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	
	public VerticalPanel imagePanel(LinkedList<JsAttach> imgList) {
		Integer x = Window.getClientWidth() / 170;
		VerticalPanel vp = new VerticalPanel();
		Integer indez = imgList.size() / x;
		for(Integer i = 0 ; i <= indez ; i++) {
			HorizontalPanel hp = new HorizontalPanel();
			for(Integer j = 0;j < x; j++) {
				if(imgList.size() > i * x + j) {
					JsAttach js = imgList.get(i * x + j);
					VerticalPanel ivp = new VerticalPanel();
					PaperIconButton pib = new PaperIconButton();
					pib.setIcon("clear");
					pib.setVisible(false);
					pib.setHeight("30px");
					pib.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {							
							String requestData = "{\"id\":\"" + js.getId() +"\","
									+ "\"attach_type\":\""+ js.getAttachType() + "\""
									+ "}";
							getAPI().getAttachment().removeAttach(requestData, new AsyncCallback<JSON<JsAttach>>() {
								
								@Override
								public void onSuccess(JSON<JsAttach> result) {
									getAPI().getAttachment().getQualityImages(parent.getDataResponse().getId(), new AsyncCallback<JSON<JsAttach>>() {
										@Override
										public void onSuccess(JSON<JsAttach> result) {
											VerticalPanel a = (VerticalPanel) imgPanel.getWidget();
											a.remove(1);
											a.add(imagePanel(result.getData().toLinkedList()));
										}
										
										@Override public void onFailure(Throwable caught) {}
									});
								}
								
								@Override
								public void onFailure(Throwable caught) {
									
								}
							});
						}
					});
					ivp.add(pib);

					IronImage ii = new IronImage();
					//ii.setSrc("https://ep01.epimg.net/elcomidista/imagenes/2017/02/22/articulo/1487804099_363696_1487804800_sumario_normal.jpg");
					ii.setSrc(imgList.get(i * x + j).getUrl());
					ii.getElement().getStyle().setWidth(150, Unit.PX);
					ii.getElement().getStyle().setHeight(150, Unit.PX);
					ii.getElement().getStyle().setPadding(10, Unit.PX);
					ii.setSizing("150px");
					ivp.add(ii);
					
					ivp.addDomHandler( new MouseOverHandler() {
						
						@Override
						public void onMouseOver(MouseOverEvent event) {
							ii.getElement().getStyle().setOpacity(50);
							pib.setVisible(true);
						}
					}, MouseOverEvent.getType());
					
					ivp.addDomHandler( new MouseOutHandler() {
						
						@Override
						public void onMouseOut(MouseOutEvent event) {
							ii.getElement().getStyle().setOpacity(100);
							pib.setVisible(false);
						}
					}, MouseOutEvent.getType());
					

					hp.add(ivp);
				}
			}
			vp.add(hp);
		}
		return vp;
	}

	public void calculatePanel() {
		Double dest = Double.parseDouble(parent.getMap().get(QualitySheetCode.UFQDP1.getName())) - 1;
		Destiny destiny = parent.getMap().containsKey(QualitySheetCode.UFQDP1.getName()) && dest >= 0 ? Destiny.values()[dest.intValue()]: Destiny.CALIDAD;
		if(Destiny.BASERRI.equals(destiny) || Destiny.EUSKOLABEL.equals(destiny)) {
			String product_quantity = parent.getMap().containsKey("product_quantity") ? parent.getMap().get("product_quantity") : "0.0";
			Double productQuantity = Double.parseDouble(product_quantity);
			String transport_quantity = parent.getMap().containsKey("neto") ? parent.getMap().get("neto") : "0.0";
			Double transportQuantity = Double.parseDouble(transport_quantity);
			Double quantity = transportQuantity > 0.0 ? transportQuantity : productQuantity;
		
			String tempStr = parent.getMap().containsKey(QualitySheetCode.UFQAC1.getName()) ?  parent.getMap().get(QualitySheetCode.UFQAC1.getName()) : "17.0";
			Double temp = Double.parseDouble(tempStr);
			FlexTable tInfo = new FlexTable();
			
				
			tInfo.setWidget(0, 2, new Label("P Fondo: "));
			String p_fondo = parent.getMap().containsKey(QualitySheetCode.UFQC2.getName()) ?  parent.getMap().get(QualitySheetCode.UFQC2.getName()) : "0.0";
			Double pfondo = Double.parseDouble(p_fondo.replace(",", "."));
			DoubleBox dbPFondo = new DoubleBox();
			dbPFondo.setStyleName(AON.AON_CSS.aonTextBox());
			dbPFondo.setValue(pfondo);
			dbPFondo.setWidth("35px");
			tInfo.setWidget(0, 3, dbPFondo);

			String col = parent.getMap().containsKey(QualitySheetCode.UFQAC8.getName()) ?  parent.getMap().get(QualitySheetCode.UFQAC8.getName()) : "0.0";
			Double color = "1".equals(col) || "1.0".equals(col) ? 0.003 : 0.0; 
		
			String product_price =  parent.getMap().containsKey("product_price") ?  parent.getMap().get("product_price") : "0.0";
			Double contractPrice = Double.parseDouble(product_price);

			Double pFondo = dbPFondo.getValue() != null && !dbPFondo.getValue().equals("") ? dbPFondo.getValue() : 0.0;
			Double z = pFondo > contractPrice ? (pFondo - contractPrice) * 0.55 : 0.0;
			Double price = contractPrice + z;
			if(Destiny.BASERRI.equals(destiny)) {
				price = price * 0.88;
			}
			FlexTable table = new FlexTable();
			table.setWidth("100%");
			table.setWidget(0, 0, new Label(""));

			Label percentage = new Label("%");
			percentage.setTitle("Porcentaje");
			percentage.setStyleName(AON.AON_CSS.aonBold());
			table.setWidget(0, 1, percentage);

			Label kg = new Label("KG");
			kg.setTitle("Kilos");
			kg.setStyleName(AON.AON_CSS.aonBold());
			table.setWidget(0, 2, kg);

			Label prima = new Label("Prima");
			prima.setTitle("Prima");
			prima.setStyleName(AON.AON_CSS.aonBold());
			table.setWidget(0, 3, prima);

			Label euro = new Label("Euros");
			euro.setTitle("Euros");
			euro.setStyleName(AON.AON_CSS.aonBold());
			table.setWidget(0, 4, euro);

			// -------------------- PEQUEÑAS
			
			Label peq = new Label("Peque\u00f1as");
			peq.setTitle("Peque\u00f1as");
			peq.setStyleName(AON.AON_CSS.aonBold());
			table.setWidget(1, 0, peq);

			String perPeq = parent.getMap().containsKey(QualitySheetCode.UFQCC021.getName()) ? parent.getMap().get(QualitySheetCode.UFQCC021.getName()) : "0.0";
			table.setWidget(1, 1, new Label(perPeq));
			Double kgPeq = (quantity * Double.parseDouble(perPeq))/100;
			table.setWidget(1, 2, new Label(Double.toString(AonMathUtils.round(kgPeq))));
			Double primaPeq = Destiny.BASERRI.equals(destiny) ? 0.06 : 0.08;
			table.setWidget(1, 3, new Label(Double.toString(AonMathUtils.round(primaPeq))));
			Double eurosPeq = kgPeq * primaPeq;
			table.setWidget(1, 4, new Label(Double.toString(AonMathUtils.round(eurosPeq))));

			// -------------------- GORDAS
		
			Label gor = new Label("Gordas");
			gor.setTitle("Gordas");
			gor.setStyleName(AON.AON_CSS.aonBold());
			table.setWidget(2, 0, gor);

			String perGor = parent.getMap().containsKey(QualitySheetCode.UFQCC061.getName()) ? parent.getMap().get(QualitySheetCode.UFQCC061.getName()) : "0.0";
			table.setWidget(2, 1, new Label(perGor));
			Double perGord = Double.parseDouble(perGor);
			Double kgGor = perGord > 5 ? (quantity * (perGord-5))/100 : 0.0;
			table.setWidget(2, 2, new Label(Double.toString(AonMathUtils.round(kgGor))));
			Double primaGor = temp < 17.0 ? price * 1.03 : price;
			table.setWidget(2, 3, new Label(Double.toString(AonMathUtils.round(primaGor, 3))));
			Double eurosGor = 0.7 * kgGor * primaGor;
			table.setWidget(2, 4, new Label(Double.toString(AonMathUtils.round(eurosGor))));

			// -------------------- TIERRA
		
			Label ter = new Label("Tierra");
			ter.setTitle("Tierra");
			ter.setStyleName(AON.AON_CSS.aonBold());
			table.setWidget(3, 0, ter);

			String perTer = parent.getMap().containsKey(QualitySheetCode.UFQCC101.getName()) ? parent.getMap().get(QualitySheetCode.UFQCC101.getName()) : "0.0";
			table.setWidget(3, 1, new Label(perTer));
			Double kgTer = (quantity * Double.parseDouble(perTer))/100;
			table.setWidget(3, 2, new Label(Double.toString(AonMathUtils.round(kgTer))));
			table.setWidget(3, 3, new Label("-"));
			table.setWidget(3, 4, new Label("-"));

			// -------------------- DEFECTOS
			
			Label def = new Label("Defectos");
			def.setTitle("Defectos");
			def.setStyleName(AON.AON_CSS.aonBold());
			table.setWidget(4, 0, def);

			String perDef = parent.getMap().containsKey(QualitySheetCode.UFQCD111.getName()) ? parent.getMap().get(QualitySheetCode.UFQCD111.getName()) : "0.0";
			table.setWidget(4, 1, new Label(perDef));
			Double perDefec = Double.parseDouble(perDef);
			Double perDefectos = perDefec < 7 ? (perDefec < 4 ? 0.0 : perDefec -4) : perDefec;
			Double kgDef = ((quantity - kgPeq - kgGor - kgTer) * perDefectos)/100;
			table.setWidget(4, 2, new Label(Double.toString(AonMathUtils.round(kgDef))));
			table.setWidget(4, 3, new Label("-"));
			table.setWidget(4, 4, new Label("-"));
		
			// -------------------- NETO

			Label neto = new Label("Neto");
			neto.setTitle("Neto");
			neto.setStyleName(AON.AON_CSS.aonBold());	
			table.setWidget(5, 0, neto);
		
			table.setWidget(5, 1, new Label("-"));
			Double kgNet = quantity - kgPeq - kgGor - kgTer - kgDef;
			table.setWidget(5, 2, new Label(Double.toString(AonMathUtils.round(kgNet))));
			Double primaNet = temp < 17.0 ?  price * 1.03 : price;
			table.setWidget(5, 3, new Label(Double.toString(AonMathUtils.round(primaNet, 3))));
			Double eurosNet = kgNet * (primaNet + color);
			table.setWidget(5, 4, new Label(Double.toString(AonMathUtils.round(eurosNet))));
		
			// -------------------- RESULTADOS
		
			FlexTable table1 = new FlexTable();
		
			// -------------------- TOTAL EUROS
			Double totalEuros = eurosPeq + eurosGor + eurosNet;

			Label eurosKgBruto = new Label("Euros/Kg Bruto");
			eurosKgBruto.setTitle("Euros/Kg Bruto");
			eurosKgBruto.setStyleName(AON.AON_CSS.aonBold());
			table1.setWidget(0, 0, eurosKgBruto);
			
			Double eurosKgBruto2 = totalEuros / quantity;
			table1.setWidget(0, 1, new Label(Double.toString(AonMathUtils.round(eurosKgBruto2, 4))));

			Label eurosKgNeto = new Label("Euros/Kg Neto");
			eurosKgNeto.setTitle("Euros/Kg Neto");
			eurosKgNeto.setStyleName(AON.AON_CSS.aonBold());
			table1.setWidget(1, 0, eurosKgNeto);
			
			Double eurosKgNeto2 = totalEuros / kgNet;
			table1.setWidget(1, 1, new Label(Double.toString(AonMathUtils.round(eurosKgNeto2, 4))));
			
			Label sinBon80 = new Label(">80 Sin Bonificaci\u00f3n");
			sinBon80.setTitle(">80 Sin Bonificacion");
			sinBon80.setStyleName(AON.AON_CSS.aonBold());
			table1.setWidget(3, 0, sinBon80);
			
			Double sinBon802 = ((quantity - kgPeq - kgGor - kgTer) * Double.parseDouble(perGor)) /100;
			table1.setWidget(3, 1, new Label(Double.toString(AonMathUtils.round(sinBon802))));
			
			Label dtoSinBon = new Label("DTO Sin Bonificaci\u00f3n");
			dtoSinBon.setTitle("DTO Sin Bonificacion");
			dtoSinBon.setStyleName(AON.AON_CSS.aonBold());
			table1.setWidget(4, 0, dtoSinBon);
			
			Double dtoSinBon2 = ((quantity - kgPeq - kgGor - kgTer) * perDefec) /100;
			table1.setWidget(4, 1, new Label(Double.toString(AonMathUtils.round(dtoSinBon2))));

			Label total = new Label("Total Euros");
			total.setTitle("Total Euros");
			total.setStyleName(AON.AON_CSS.aonBold());
			table1.setWidget(2, 0, total);

			Double totalEuros2 =  AonMathUtils.round(kgNet) * AonMathUtils.round(eurosKgNeto2, 4);
			table1.setWidget(2, 1, new Label(Double.toString(AonMathUtils.round(totalEuros2))));
			
			
			VerticalPanel vp = new VerticalPanel();
			vp.setWidth("100%");
			vp.add(tInfo);
			vp.add(table);
			vp.add(table1);
		
			updateIncomeDetail(eurosKgNeto2, kgNet);
			dbPFondo.addValueChangeHandler(new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> arg0) {
					String id = parent.dataResponse.getId() + "";
					String domainName = parent.getAonData().getDomain().getName();
					Integer domainId = parent.getAonData().getDomain().getId();
					parent.impl.updateValue(domainName, domainId, Integer.parseInt(id), QualitySheetCode.UFQC2, dbPFondo.getValue().toString(), parent.getMap(), new AsyncCallback<HashMap<String, String>>() {
						@Override public void onFailure(Throwable caught) {}
						@Override public void onSuccess(HashMap<String, String> result) {}
					});
					
					Double pFondo =dbPFondo.getValue();
					Double z = pFondo > contractPrice ? (pFondo - contractPrice) * 0.55 : 0.0;
					Double price = contractPrice + z;
					if(Destiny.BASERRI.equals(destiny)) {
						price = price * 0.88;
					}
					
					Double primaGor = temp < 17.0 ? price * 1.03 : price;
					table.setWidget(2, 3, new Label(Double.toString(AonMathUtils.round(primaGor, 3))));
					Double eurosGor = 0.7 * kgGor * primaGor;
					table.setWidget(2, 4, new Label(Double.toString(AonMathUtils.round(eurosGor))));		
					
					
					Double primaNet = temp < 17.0 ?  price * 1.03 : price;
					table.setWidget(5, 3, new Label(Double.toString(AonMathUtils.round(primaNet, 3))));
					Double eurosNet = kgNet * (primaNet + color);
					table.setWidget(5, 4, new Label(Double.toString(AonMathUtils.round(eurosNet))));
					
					Double totalEuros = eurosPeq + eurosGor + eurosNet;
					table1.setWidget(0, 1, new Label(Double.toString(AonMathUtils.round(totalEuros))));
					
					Double eurosKgBruto2 = totalEuros / quantity;
					table1.setWidget(1, 1, new Label(Double.toString(AonMathUtils.round(eurosKgBruto2, 4))));
					
					Double eurosKgNeto2 = totalEuros / kgNet;
					table1.setWidget(2, 1, new Label(Double.toString(AonMathUtils.round(eurosKgNeto2, 4))));

					updateIncomeDetail(eurosKgNeto2, kgNet);
				}
			});
			calculatePanel.setWidget(vp);
		}
	}
	
	private void tagPanel() {
		FlexTable ft = new FlexTable();

		Label a = new Label("Posici\u00f3n");
		a.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		ft.setWidget(0, 0, a);
		DoubleBox tb1 = new DoubleBox();
		tb1.setStyleName(AON.AON_CSS.aonInputText());
		tb1.setText("1");
		ft.setWidget(0, 1, tb1);
		
		Label b = new Label("50/60");
		b.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		ft.setWidget(1, 0, b);
		DoubleBox tb2 = new DoubleBox();
		tb2.setStyleName(AON.AON_CSS.aonInputText());
		tb2.setText("8");
		ft.setWidget(1, 1, tb2);
		
		Label c = new Label("60/80");
		c.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		ft.setWidget(2, 0, c);
		DoubleBox tb3 = new DoubleBox();
		tb3.setStyleName(AON.AON_CSS.aonInputText());
		tb3.setText("8");
		ft.setWidget(2, 1, tb3);
		
		Label d = new Label("Variedad");
		d.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		ft.setWidget(3, 0, d);
		TextBox tb4 = new TextBox();
		tb4.setStyleName(AON.AON_CSS.aonInputText());
		tb4.setText(parent.getMap().get("product_name").replace("patata", "").replace("PATATA", ""));
		tb4.setWidth("100px");
		ft.setWidget(3, 1, tb4);
		
		Label e = new Label("Destino");
		e.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		ft.setWidget(4, 0, e);
		TextBox tb5 = new TextBox();
		tb5.setStyleName(AON.AON_CSS.aonInputText());
		tb5.setWidth("100px");
		ft.setWidget(4, 1, tb5);
		
		Label f = new Label("Observaciones");
		f.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		ft.setWidget(5, 0, f);
		TextArea ta = new TextArea();
		ta.setStyleName(AON.AON_CSS.aonInputText());
		ta.setWidth("100px");
		ft.setWidget(5, 1, ta);

		Button but = new Button("Descargar");
		but.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				getAPI().getWarehouse().downloadUdapaTag(parent.getDataResponse().getId(), tb1.getValue(), tb2.getValue(), tb3.getValue(), tb4.getValue(), tb5.getValue(),ta.getValue());
			}
		});
		ft.setWidget(0, 2, but);
		tagPanel.add(ft);
	}
	
	private void updateIncomeDetail(Double price,Double quantity) {
		String[] arr = parent.getMap().get("source").split("@");
		parent.impl.updateIncomeDetail(parent.getAonData().getDomain().getName(), parent.getAonData().getDomain().getId(),
			AonMathUtils.round(price, 4), AonMathUtils.round(quantity), Integer.parseInt(arr[1]), new AsyncCallback<Void>() {
			@Override public void onFailure(Throwable caught) {}
			@Override public void onSuccess(Void result) {}
		});
	}
	
	@UiField MinimizePanel footPanel;
	@UiField TabLayoutPanel tabPanel;
	@UiField ScrollPanel imgPanel;
	@UiField ScrollPanel calculatePanel;
	@UiField ScrollPanel tagPanel;
	
	public TabLayoutPanel getTabPanel() {
		return tabPanel;
	}
	
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	
	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		openFootPanel();
	}
	
	public void openFootPanel() {
		Integer clientHeight = Window.getClientHeight();
		parent.southContentSize(clientHeight.doubleValue() / 3);
		parent.contentSplitLayoutPanel.animate(500);
	}
	
	public void closeFootPanel() {
		parent.southContentSize(30.0);
		parent.contentSplitLayoutPanel.animate(500);
	}
}
