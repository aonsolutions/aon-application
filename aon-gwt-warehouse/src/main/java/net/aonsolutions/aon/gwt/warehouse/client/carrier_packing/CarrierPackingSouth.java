package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class CarrierPackingSouth extends DockLayoutPanel{
	
	CarrierPacking parent;
	public CarrierPackingSouth(CarrierPacking parent,JsOrder order, AonJsArray<JsOrderDetail> details) {
		super(Unit.PX);
		this.parent = parent;
		build(order, details);
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
		
		final InlineLabel acc = new InlineLabel(AonStringUtils.SPACE
				+ AonStringUtils.rightPad(AonStringUtils.defaultString(order.getIssueDate()),13)
				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
						AonStringUtils.defaultString(order.getSeries() +"/" + order.getNumber()), 16), 17)

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
	
}
