package net.aonsolutions.aon.gwt.warehouse.client.carrier_packing;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsCarrierPacking;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.paper.widget.PaperIconButton;

import net.aonsolutions.aon.gwt.warehouse.client.Utils;

public class CarrierPackingSouth2 extends DockLayoutPanel{
	CarrierPacking parent;
	JsCarrierPacking carrierPacking;
	public CarrierPackingSouth2(CarrierPacking parent, JsCarrierPacking carrierPacking,
			JsOrder purchase, AonJsArray<JsOrderDetail> purchaseDetails) {
		super(Unit.PX);
		this.parent = parent;
		this.carrierPacking = carrierPacking;
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
				+ "IMPORTE TOTAL    ");  //10
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
		
		String issueDate = purchase.getIssueDate() != null
				? Utils.formatDate(Utils.parseDateTime(purchase.getIssueDate()))
				: "";
		
		final InlineLabel acc = new InlineLabel(AonStringUtils.SPACE
				+ AonStringUtils.rightPad(AonStringUtils.defaultString(issueDate),13)
				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
						AonStringUtils.defaultString(purchase.getSeriesNumber()), 16), 17)

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
		Label header2 = new Label("      "//6
				+ "LINEA     " //10
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
	
		refresh(purchase, details);
	}
	
	public void refresh(JsOrder purchase){
		getCenter().removeFromParent();
		parent.API.getWarehouse().getPurchaseDetails(purchase.getId(), new AsyncCallback<JSON<JsOrderDetail>>() {
			
			@Override
			public void onSuccess(JSON<JsOrderDetail> result) {
				refresh(purchase, result.getData());
			}
			
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void refresh(JsOrder purchase, AonJsArray<JsOrderDetail> details){
		FlowPanel center = new FlowPanel();
		ScrollPanel scrollCenter = new ScrollPanel();
		scrollCenter.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		details.stream().forEach(detail ->{
			FlowPanel line = new FlowPanel("pre");
			
			line.setStyleName(AON.AON_CSS.aonFixedFont());
			line.addStyleName(AON.AON_CSS.aonFontMedium());
			line.addStyleName(AON.AON_CSS.aonMarginBottom());
			
			DoubleBox  db = new DoubleBox();
			db.setWidth("50px");
			db.setStyleName(AON.AON_CSS.aonTextBox());
			db.setValue(detail.getQuantity());
			db.setEnabled(detail.getCarrierPacking() == null );
			PaperIconButton pib = new PaperIconButton();
			pib.setDisabled(detail.getCarrierPacking() != null && detail.getCarrierPacking() != carrierPacking.getId());
			pib.setIcon(detail.getCarrierPacking() != null ? "remove" : "add" );
			pib.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					String requestData = "{\"id\":\""+ detail.getId() +"\","
							+"\"action\":\""+ (detail.getCarrierPacking() != null ? "delete" : "add") + "\","
							+"\"carrier_packing\":\""+ carrierPacking.getId() + "\","
							+"\"quantity\":\""+ db.getValue() + "\""
							+ "}";

					parent.API.getWarehouse().addCarrierPacking("purchase", requestData , new AsyncCallback<JSON<JsOrderDetail>>() {
							
						@Override
						public void onSuccess(JSON<JsOrderDetail> result) {
							parent.API.getWarehouse().getDetails(purchase.getId(), "purchase", new AsyncCallback<JSON<JsOrderDetail>>() {
									
								@Override
								public void onSuccess(JSON<JsOrderDetail> result) {
									scrollCenter.removeFromParent();
									refresh(purchase, result.getData());
									parent.refreshSelect();
								}
								
								@Override public void onFailure(Throwable caught) {}
							});
						}
							
						@Override public void onFailure(Throwable caught) {}
					});
				}
				
			});
			
			Double discount = detail.getDiscountExpr() != null ? Double.parseDouble(detail.getDiscountExpr()) : 1.0;
			Double importe = detail.getPrice() * detail.getQuantity() * (1 - (discount/100));
			InlineLabel d = new InlineLabel(AonStringUtils.SPACE
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getLine() + ""),11)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
							AonStringUtils.defaultString(detail.getProductCode() + "-" + detail.getProductName()), 39), 44));
			d.setTitle("");
			d.setStyleName(AON.AON_CSS.aonBold());
			
			InlineLabel d2 = new InlineLabel("        "
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getCarrierPacking() != null? "0" : detail.getQuantity() + ""), 17)// PENDIENTE
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getPrice() + ""), 16)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getDiscountExpr()), 16)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString( importe + ""), 15)	
					);
			d2.setTitle("");
			d2.setStyleName(AON.AON_CSS.aonBold());
			
			line.add(pib);
			line.add(d);
			line.add(db);
			line.add(d2);
			center.add(line);
		});
		
		scrollCenter.setWidget(center);
		
		add(scrollCenter);
	}
	
	public void autoHeight(Widget widget, Integer value){
		widget.getElement().getStyle().setHeight(Window.getClientHeight() - value, Unit.PX);
		Window.addResizeHandler(new ResizeHandler() {
				
			@Override
			public void onResize(ResizeEvent event) {
				widget.getElement().getStyle().setHeight(Window.getClientHeight() - value, Unit.PX);
			}
		});
	}
	public FlowPanel getFlowPanel(JsOrder purchase, AonJsArray<JsOrderDetail> details) {
		final FlowPanel p = new FlowPanel("pre");
		final FlowPanel headerPanel = new FlowPanel("pre");
		headerPanel.setStyleName(AON.AON_CSS.aonFixedFont());
		headerPanel.addStyleName(AON.AON_CSS.aonFontMedium());
		Label header = new Label(" FECHA        " //13
				+ "SERIE/NUMERO     " //17
				+ "PROVEEDOR                        " //33
				+ "IMPORTE TOTAL    ");  //10
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
		
		String issueDate = purchase.getIssueDate() != null
				? Utils.formatDate(Utils.parseDateTime(purchase.getIssueDate()))
				: "";
				
		final InlineLabel acc = new InlineLabel(AonStringUtils.SPACE
				+ AonStringUtils.rightPad(AonStringUtils.defaultString(issueDate),13)
				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
						AonStringUtils.defaultString(purchase.getSeriesNumber()), 16), 17)

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
		Label header2 = new Label("      "//6
				+ "LINEA     " //10
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

		FlowPanel center = new FlowPanel();
		
		details.stream().forEach(detail ->{
			FlowPanel line = new FlowPanel("pre");
			
			line.setStyleName(AON.AON_CSS.aonFixedFont());
			line.addStyleName(AON.AON_CSS.aonFontMedium());
			line.addStyleName(AON.AON_CSS.aonMarginBottom());
			
			DoubleBox  db = new DoubleBox();
			db.setWidth("50px");
			db.setStyleName(AON.AON_CSS.aonTextBox());
			db.setValue(detail.getQuantity());
			db.setEnabled(detail.getCarrierPacking() == null );
			PaperIconButton pib = new PaperIconButton();
			pib.setDisabled(detail.getCarrierPacking() != null && detail.getCarrierPacking() != carrierPacking.getId());
			pib.setIcon(detail.getCarrierPacking() != null ? "remove" : "add" );
			pib.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					String requestData = "{\"id\":\""+ detail.getId() +"\","
							+"\"action\":\""+ (detail.getCarrierPacking() != null ? "delete" : "add") + "\","
							+"\"carrier_packing\":\""+ carrierPacking.getId() + "\","
							+"\"quantity\":\""+ db.getValue() + "\""
							+ "}";

					parent.API.getWarehouse().addCarrierPacking("purchase", requestData , new AsyncCallback<JSON<JsOrderDetail>>() {
							
						@Override
						public void onSuccess(JSON<JsOrderDetail> result) {
							parent.API.getWarehouse().getDetails(purchase.getId(), "purchase", new AsyncCallback<JSON<JsOrderDetail>>() {
									
								@Override
								public void onSuccess(JSON<JsOrderDetail> result2) {
									parent.southContent(carrierPacking, purchase, result2.getData());
									parent.refreshSelect();
								}
								
								@Override public void onFailure(Throwable caught) {}
							});
						}
							
						@Override public void onFailure(Throwable caught) {}
					});
				}
				
			});
			
			Double discount = detail.getDiscountExpr() != null ? Double.parseDouble(detail.getDiscountExpr()) : 1.0;
			Double importe = detail.getPrice() * detail.getQuantity() * (1 - (discount/100));
			InlineLabel d = new InlineLabel(AonStringUtils.SPACE
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getLine() + ""),11)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
							AonStringUtils.defaultString(detail.getProductCode() + "-" + detail.getProductName()), 39), 44));
			d.setTitle("");
			d.setStyleName(AON.AON_CSS.aonBold());
			
			InlineLabel d2 = new InlineLabel("        "
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getCarrierPacking() != null? "0" : detail.getQuantity() + ""), 17)// PENDIENTE
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getPrice() + ""), 16)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getDiscountExpr()), 16)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString( importe + ""), 15)	
					);
			d2.setTitle("");
			d2.setStyleName(AON.AON_CSS.aonBold());
			
			line.add(pib);
			line.add(d);
			line.add(db);
			line.add(d2);
			center.add(line);
		});
		p.add(center);
		
		return p;
	}
	
	
}
