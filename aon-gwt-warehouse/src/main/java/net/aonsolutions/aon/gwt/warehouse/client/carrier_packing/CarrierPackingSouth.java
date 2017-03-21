package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.common.JsAppParam;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;

import net.aonsolutions.aon.gwt.warehouse.client.Utils;
import net.aonsolutions.aon.gwt.warehouse.shared.CarrierPackingParams;
import net.aonsolutions.aon.gwt.warehouse.shared.CarrierPackingParams.Param;

public class CarrierPackingSouth extends DockLayoutPanel{
	
	CarrierPacking parent;
	JsCarrierPacking jsCarrierPacking;
	
	
	public CarrierPackingSouth(CarrierPacking parent) {
		super(Unit.PX);
		this.parent = parent;
	}
	
	public CarrierPackingSouth(CarrierPacking parent, JsCarrierPacking jsCarrierPacking) {
		super(Unit.PX);
		this.parent = parent;
		this.jsCarrierPacking = jsCarrierPacking;
	}
	
	public CarrierPackingSouth(CarrierPacking parent,JsOrder order, AonJsArray<JsOrderDetail> details) {
		super(Unit.PX);
		this.parent = parent;
		//build(order, details);
	}
	
	
	private void build(JsOrder order, AonJsArray<JsOrderDetail> details) {
		final FlowPanel p = new FlowPanel("pre");
		final FlowPanel headerPanel = new FlowPanel("pre");
		headerPanel.setStyleName(AON.AON_CSS.aonFixedFont());
		headerPanel.addStyleName(AON.AON_CSS.aonFontMedium());
		Label header = new Label(" FECHA        " //13
				+ "SERIE/NUMERO     " //17
				+ "PROVEEDOR                        " //33
				+ "IMPORTE TOTAL    "  //17
				+ "BULTOS    "  //10
				+ "PESO      ");  //10
		header.setStyleName(AON.AON_CSS.aonBold());
		header.addStyleName(AON.AON_CSS.aonMarginTop());
		header.addStyleName(AON.AON_CSS.aonBorderTop());
		header.addStyleName(AON.AON_CSS.aonBorderBottom());
		headerPanel.add(header);
		p.add(headerPanel);
		
		final FlowPanel panel = new FlowPanel("pre");
		panel.setStyleName(AON.AON_CSS.aonFixedFont());
		panel.addStyleName(AON.AON_CSS.aonFontMedium());
		panel.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		String issueDate = order.getIssueDate() != null
				? Utils.formatDate(Utils.parseDateTime(order.getIssueDate()))
				: "";
		final InlineLabel acc = new InlineLabel(AonStringUtils.SPACE
				+ AonStringUtils.rightPad(AonStringUtils.defaultString(issueDate),13)
				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
						AonStringUtils.defaultString(order.getSeriesNumber()), 16), 17)

				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
						AonStringUtils.defaultString(order.getRegistry().getName()), 32),33)
				+ AonStringUtils.rightPad("",17)		
				);
		acc.setTitle("");
		acc.setStyleName(AON.AON_CSS.aonBold());
		
		DoubleBox  db1 = new DoubleBox();
		db1.setWidth("50px");
		db1.setStyleName(AON.AON_CSS.aonTextBox());
		db1.setValue(order.getTotalPackages());
		db1.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				String requestData = "{\"total_packages\":\""+ db1.getValue() +"\"}";
				parent.API.getWarehouse().updateDelivery(order.getId(), requestData);
			}
		});
		
		DoubleBox  db2 = new DoubleBox();
		db2.setWidth("50px");
		db2.setStyleName(AON.AON_CSS.aonTextBox());
		db2.setValue(order.getTotalWeight());
		db2.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				String requestData = "{\"total_weight\":\""+ db2.getValue() +"\"}";
				parent.API.getWarehouse().updateDelivery(order.getId(), requestData);
			}
		});
	
		panel.add(acc);
		panel.add(db1);
		panel.add(new InlineLabel("  "));
		panel.add(db2);
		p.add(panel);
		
		final FlowPanel headerPanel2 = new FlowPanel("pre");
		headerPanel2.setStyleName(AON.AON_CSS.aonFixedFont());
		headerPanel2.addStyleName(AON.AON_CSS.aonFontMedium());
		Label header2 = new Label(" LINEA     " //10
				+ "PRODUCTO                                " //40
				+ "CANTIDAD       " //15
				+ "PRECIO         " //15
				+ "DESCUENTO      " //15
				+ "IMPORTE        "); //15 
		header2.setStyleName(AON.AON_CSS.aonBold());
		header2.addStyleName(AON.AON_CSS.aonMarginTop());
		header2.addStyleName(AON.AON_CSS.aonBorderTop());
		header2.addStyleName(AON.AON_CSS.aonBorderBottom());
		headerPanel2.add(header2);
		p.add(headerPanel2);

		addNorth(p, 100);
	
		FlowPanel center = new FlowPanel();
		
		details.stream().forEach(detail ->{
			FlowPanel line = new FlowPanel("pre");
			
			line.setStyleName(AON.AON_CSS.aonFixedFont());
			line.addStyleName(AON.AON_CSS.aonFontMedium());
			line.addStyleName(AON.AON_CSS.aonMarginBottom());
			
			Double discount = detail.getDiscountExpr() != null ? Double.parseDouble(detail.getDiscountExpr()) : 1.0;
			Double importe = detail.getPrice() * detail.getQuantity() * (1 - (discount/100));
			InlineLabel d = new InlineLabel(AonStringUtils.SPACE
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getLine() + ""),11)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
							AonStringUtils.defaultString(detail.getProductCode() + "-" + detail.getProductName()), 39), 44)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getQuantity() + ""), 16)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getPrice() + ""), 16)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getDiscountExpr()), 16)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString( importe + ""), 15)	
					);
			d.setTitle("");
			d.setStyleName(AON.AON_CSS.aonBold());
			
			line.add(d);
			center.add(line);
		});
		
		ScrollPanel scrollCenter = new ScrollPanel();
		scrollCenter.addStyleName(AON.AON_CSS.aonMarginBottom());
		scrollCenter.setWidget(center);
		
		add(scrollCenter);
	}
	
	
	public FlowPanel getFlowPanel(JsOrder order, AonJsArray<JsOrderDetail> details) {
		final FlowPanel p = new FlowPanel("pre");
		final FlowPanel headerPanel = new FlowPanel("pre");
		headerPanel.setStyleName(AON.AON_CSS.aonFixedFont());
		headerPanel.addStyleName(AON.AON_CSS.aonFontMedium());
		Label header = new Label(" FECHA        " //13
				+ "SERIE/NUMERO     " //17
				+ "PROVEEDOR                        " //33
				+ "IMPORTE TOTAL    "  //17
				+ "BULTOS    "  //10
				+ "PESO      ");  //10
		header.setStyleName(AON.AON_CSS.aonBold());
		header.addStyleName(AON.AON_CSS.aonMarginTop());
		header.addStyleName(AON.AON_CSS.aonBorderTop());
		header.addStyleName(AON.AON_CSS.aonBorderBottom());
		headerPanel.add(header);
		p.add(headerPanel);
		
		final FlowPanel panel = new FlowPanel("pre");
		panel.setStyleName(AON.AON_CSS.aonFixedFont());
		panel.addStyleName(AON.AON_CSS.aonFontMedium());
		panel.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		String issueDate = order.getIssueDate() != null
				? Utils.formatDate(Utils.parseDateTime(order.getIssueDate()))
				: "";
		final InlineLabel acc = new InlineLabel(AonStringUtils.SPACE
				+ AonStringUtils.rightPad(AonStringUtils.defaultString(issueDate),13)
				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
						AonStringUtils.defaultString(order.getSeriesNumber()), 16), 17)

				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
						AonStringUtils.defaultString(order.getRegistry().getName()), 32),33)
				+ AonStringUtils.rightPad("",17)		
				);
		acc.setTitle("");
		acc.setStyleName(AON.AON_CSS.aonBold());
		
		DoubleBox  db1 = new DoubleBox();
		db1.setWidth("50px");
		db1.setStyleName(AON.AON_CSS.aonTextBox());
		db1.setValue(order.getTotalPackages());
		db1.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				String requestData = "{\"total_packages\":\""+ db1.getValue() +"\"}";
				parent.API.getWarehouse().updateDelivery(order.getId(), requestData);
			}
		});
		
		DoubleBox  db2 = new DoubleBox();
		db2.setWidth("50px");
		db2.setStyleName(AON.AON_CSS.aonTextBox());
		db2.setValue(order.getTotalWeight());
		db2.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				String requestData = "{\"total_weight\":\""+ db2.getValue() +"\"}";
				parent.API.getWarehouse().updateDelivery(order.getId(), requestData);
			}
		});
	
		panel.add(acc);
		panel.add(db1);
		panel.add(new InlineLabel("  "));
		panel.add(db2);
		p.add(panel);
		
		final FlowPanel headerPanel2 = new FlowPanel("pre");
		headerPanel2.setStyleName(AON.AON_CSS.aonFixedFont());
		headerPanel2.addStyleName(AON.AON_CSS.aonFontMedium());
		Label header2 = new Label(" LINEA     " //10
				+ "PRODUCTO                                " //40
				+ "CANTIDAD       " //15
				+ "PRECIO         " //15
				+ "DESCUENTO      " //15
				+ "IMPORTE        "); //15 
		header2.setStyleName(AON.AON_CSS.aonBold());
		header2.addStyleName(AON.AON_CSS.aonMarginTop());
		header2.addStyleName(AON.AON_CSS.aonBorderTop());
		header2.addStyleName(AON.AON_CSS.aonBorderBottom());
		headerPanel2.add(header2);
		p.add(headerPanel2);

	
		FlowPanel center = new FlowPanel();
		
		details.stream().forEach(detail ->{
			FlowPanel line = new FlowPanel("pre");
			
			line.setStyleName(AON.AON_CSS.aonFixedFont());
			line.addStyleName(AON.AON_CSS.aonFontMedium());
			line.addStyleName(AON.AON_CSS.aonMarginBottom());
			
			Double discount = detail.getDiscountExpr() != null ? Double.parseDouble(detail.getDiscountExpr()) : 1.0;
			Double importe = detail.getPrice() * detail.getQuantity() * (1 - (discount/100));
			InlineLabel d = new InlineLabel(AonStringUtils.SPACE
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getLine() + ""),11)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
							AonStringUtils.defaultString(detail.getProductCode() + "-" + detail.getProductName()), 39), 44)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getQuantity() + ""), 16)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getPrice() + ""), 16)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getDiscountExpr()), 16)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString( importe + ""), 15)	
					);
			d.setTitle("");
			d.setStyleName(AON.AON_CSS.aonBold());
			
			line.add(d);
			center.add(line);
		});
		
		p.add(center);
		return p;
	}
	
	public VerticalPanel getParameterPanel(AonJsArray<JsAppParam> params){
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		params.stream().forEach(p -> panel.add(buildParameter(p)));
		return panel;
	}
	
	public HorizontalPanel buildParameter(JsAppParam js){
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName(AON.AON_CSS.aonWidthAll());
		Boolean sc = js.getName().contains("SC");
		String[] arr = js.getName().split("_");
		String parameter = arr[arr.length-1];
		PaperItem pi = new PaperItem();
		IronIcon ironIcon = new IronIcon();
		ironIcon.setIcon("receipt");
		pi.add(ironIcon);
		 
		String str = (sc ? "Solicitud de Carga": "Hoja de Ruta") + " - " + parameter + " - " + js.getValue();
	    pi.add(new Label(str));
	    pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
	    
	    PaperIconButton edit = new PaperIconButton();
	    edit.setStyle("height: 24px;padding: 0px;position: absolute;right: 30px;");
	    edit.setIcon("create");
		edit.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				editParameter(js);
			}
		});
		
		PaperIconButton del = new PaperIconButton();
	    del.setStyle("height: 24px;padding: 0px;position: absolute;right: 0px;");
	    del.setIcon("delete");
		del.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				deleteParameter(js);		
			}
		});
	    
		hp.add(pi);
		hp.add(edit);
		hp.add(del);
	    return hp;
	}
	
	private void editParameter(JsAppParam js) {
		String[] arr = js.getName().split("_");
		String parameter = arr[arr.length-1];
		VerticalPanel panel = new VerticalPanel();
		PaperInput param = new PaperInput();
		param.setDisabled(true);
		param.setLabel("Par\u00e1metro");
		param.setValue(parameter);
		panel.add(param);
		
		PaperInput value = new PaperInput();
		value.setLabel("Valor por defecto");
		value.setValue(js.getValue());
		panel.add(value);
		
		AonDialog dialog = new AonDialog("Editar Par\u00e1metro", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				String requestData = "{\"parameter\":\""+ js.getName() +"\","
					+ "\"value\":\""+ value.getValue() +"\"}";
				parent.API.getCommon().insertAppParam(requestData, new AsyncCallback<JsAppParam>() {
					
					@Override public void onSuccess(JsAppParam result) {
						parent.southContent();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
				hide();
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	private void deleteParameter(JsAppParam js) {
		String[] arr = js.getName().split("_");
		String parameter = arr[arr.length-1];
		Label label = new Label("Est\u00e1 seguro que quiere borrar el par\u00e1metro "+ parameter);
		
		AonDialog dialog = new AonDialog("Borrar Parametro", label) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				String requestData = "{\"action\":\"delete\","
						+"\"id\":\""+ js.getId() +"\"}";
				parent.API.getCommon().deleteAppParam(requestData, new AsyncCallback<JsAppParam>() {
					
					@Override public void onSuccess(JsAppParam result) {
						parent.southContent();
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
				hide();
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	
	public VerticalPanel getParameterPanel(CarrierPackingParams params){
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		if(params.getParam() == null) {
			params.setParam(new LinkedList<>());
		}
		params.getParam().stream().forEach(p -> panel.add(buildParameter(params, p)));
		return panel;
	}
	
	public HorizontalPanel buildParameter(CarrierPackingParams params, Param param){ 
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName(AON.AON_CSS.aonWidthAll());
		PaperItem pi = new PaperItem();
		IronIcon ironIcon = new IronIcon();
		ironIcon.setIcon("receipt");
		pi.add(ironIcon);
		 
		String str = param.getName() + " - " + param.getValue();
	    pi.add(new Label(str));
	    pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
	    
	    
	    PaperIconButton edit = new PaperIconButton();
	    edit.setStyle("height: 24px;padding: 0px;position: absolute;right: 30px;");
	    edit.setIcon("create");
		edit.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				editParameter(params, param);
			}
		});
		
		PaperIconButton del = new PaperIconButton();
	    del.setStyle("height: 24px;padding: 0px;position: absolute;right: 0px;");
	    del.setIcon("delete");
		del.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				deleteParameter(params, param);
			}
		});
   
	    hp.add(pi);
	    hp.add(edit);
	    hp.add(del);
	    return hp;
	}
	
	private void editParameter(CarrierPackingParams params, Param parameter) {
		VerticalPanel panel = new VerticalPanel();
		PaperInput param = new PaperInput();
		param.setDisabled(true);
		param.setLabel("Par\u00e1metro");
		param.setValue(parameter.getName());
		panel.add(param);
		
		PaperInput value = new PaperInput();
		value.setLabel("Valor por defecto");
		value.setValue(parameter.getValue());
		panel.add(value);
		
		AonDialog dialog = new AonDialog("Editar Par\u00e1metro", panel) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				for(Param p : params.getParam()){
					if(p.getName() != null && parameter.getName() != null 
							&& (p.getName().equals(parameter.getName()))){
						p.setValue(value.getValue());
					}
				}
				parent.impl.writeXml(params, new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String result) {
						String requestData = "{\"params\":\""+ result +"\"}";
						parent.setParameterPanel(result);
						parent.API.getWarehouse().updateCarrierPacking(parent.getJsCarrierPacking().getId(), requestData, new AsyncCallback<JsCarrierPacking>() {
							
							@Override
							public void onSuccess(JsCarrierPacking result) {
								parent.setJsCarrierPacking(result);
							}
							
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
				hide();
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
	
	private void deleteParameter(CarrierPackingParams params, Param parameter) {
		Label label = new Label("Est\u00e1 seguro que quiere borrar el par\u00e1metro "+ parameter.getName());
		
		AonDialog dialog = new AonDialog("Borrar Par\u00e1metro", label) {
			
			@Override protected void onCancel() {hide();}
			
			@Override 
			protected void onAccept() {	
				for(Param p : params.getParam()){
					if(p.getName() != null && parameter.getName() != null 
						&& p.getName().equals(parameter.getName())){
						params.getParam().remove(p);
					}
				}
				parent.impl.writeXml(params, new AsyncCallback<String>() {
				
					@Override
					public void onSuccess(String result) {
						String requestData = "{\"params\":\""+ result +"\"}";
						parent.setParameterPanel(result);
						parent.API.getWarehouse().updateCarrierPacking(parent.getJsCarrierPacking().getId(), requestData, new AsyncCallback<JsCarrierPacking>() {
						
							@Override
							public void onSuccess(JsCarrierPacking result) {
								parent.setJsCarrierPacking(result);
							}
						
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
				
					@Override public void onFailure(Throwable caught) {}
				});
				hide();
			}
		};
		dialog.setAutoHideEnabled(true);
		dialog.getElement().getStyle().setWidth(310, Unit.PX);
		dialog.center();
	}
}
