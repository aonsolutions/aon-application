package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class CarrierPackingSouth extends DockLayoutPanel{
	
	public CarrierPackingSouth(JsOrder purchase, AonJsArray<JsOrderDetail> purchaseDetails) {
		super(Unit.PX);
		build(purchase, purchaseDetails);
	}
	
	
	private void build(JsOrder purchase, AonJsArray<JsOrderDetail> details) {
		final FlowPanel p = new FlowPanel("pre");
		final FlowPanel headerPanel = new FlowPanel("pre");
		headerPanel.setStyleName(AON.AON_CSS.aonFixedFont());
		headerPanel.addStyleName(AON.AON_CSS.aonFontMedium());
		Label header = new Label(" FECHA        " //13
				+ "SERIE/NUMERO     " //17
				+ "PROVEEDOR                        " //33
				+ "IMPORTE TOTAL    ");  //17
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
				+ AonStringUtils.rightPad(AonStringUtils.defaultString(purchase.getIssueDate()),13)
				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
						AonStringUtils.defaultString(purchase.getSeries() +"/" + purchase.getNumber()), 16), 17)

				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
						AonStringUtils.defaultString(purchase.getRegistry().getName()), 32),33)
				+ AonStringUtils.rightPad("",17)		
				);
		acc.setTitle("");
		acc.setStyleName(AON.AON_CSS.aonBold());
		
		panel.add(acc);
		p.add(panel);
		
		final FlowPanel headerPanel2 = new FlowPanel("pre");
		headerPanel2.setStyleName(AON.AON_CSS.aonFixedFont());
		headerPanel2.addStyleName(AON.AON_CSS.aonFontMedium());
		Label header2 = new Label(" LINEA     " //10
				+ "PRODUCTO                                " //40
				+ "CANTIDAD       " //15
				+ "PENDIENTE      " //15
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
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getDelivered() + ""), 17)
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
