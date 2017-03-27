package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrder;
import com.esferalia.aon.gwt.api.client.warehouse.JsOrderDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.occam.api.model.type.ElaborationSource;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class FootPanel extends Composite {

	interface Binder extends UiBinder<Widget, FootPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	public static final int TAB_INDEX_SOURCE = 0;
	public static final int TAB_INDEX_COMMENTS = 1;

	private API API;
	
	private MainElaboration parent;

	@UiField
	MinimizePanel footPanel;
	@UiField
	TabLayoutPanel tabPanel;

	@UiField
	ScrollPanel sourcePanel;
	@UiField
	ScrollPanel observationPanel;
	
		
	public FootPanel(MainElaboration parent) {
		this(parent, null);
	}
	
	public FootPanel(MainElaboration parent, JsElaboration jsElaboration) {
		this.parent = parent;
		API = parent.API;
		initWidget(binder.createAndBindUi(this));
		
		closeFootPanel();
		if(jsElaboration!=null){
			loadSourceTab(jsElaboration);
			loadCommetsTab(jsElaboration);
			
			if(jsElaboration.getComments()!=null && !"".equals(jsElaboration.getComments())){
				openFootPanel();
				tabPanel.selectTab(TAB_INDEX_COMMENTS);
			}
		}
		
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				switch (event.getSelectedItem()) {
				case TAB_INDEX_SOURCE:
					openFootPanel();
					break;
				case TAB_INDEX_COMMENTS:
					openFootPanel();
					break;
				}
			}
		});
	}
	
	protected void loadSourceTab(JsElaboration js){
//		final Widget widget = new Widget();		
//		if(ElaborationSource.SALES.ordinal()==js.getSource()){
//			Integer salesDetailId = js.getSourceId();
//			
//			API.getWarehouse().getSalesDetail(salesDetailId, new AsyncCallback<JSON<JsOrderDetail>>() {
//				
//				@Override
//				public void onSuccess(JSON<JsOrderDetail> result) {
//					FlowPanel panel = createSourcePanel(result.getOneData());
//					widget.setLayoutData(panel);
//				}
//				
//				@Override public void onFailure(Throwable caught) {}
//			});
//			
//		} else if(ElaborationSource.PURCHASE.ordinal()==js.getSource()){
//			// TODO elaboration have purchase source
//		} else {
//			Label label = new Label();
//			label.setText("La elaboración se ha creado manualmente, no tiene origen.");
//			widget.setLayoutData(label);
//		};
//		 
//		sourcePanel.add(widget);
		
		Label label = new Label();
		label.setText("La elaboración se ha creado manualmente, no tiene origen. " + js.getSource() + ". " + js.getSourceId());
		sourcePanel.add(label);
	}
	
	protected void loadCommetsTab(JsElaboration js){
		TextArea comments = new TextArea();
		comments.setWidth("95%");
		comments.setHeight("100px");
		if (js.getComments() != null) {
			comments.setValue(js.getComments());
		}
		comments.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				parent.updateElaboration(js);
			}
		});

		observationPanel.add(comments);
	}
	
	protected FlowPanel createSourcePanel(JsOrderDetail orderDetail){
		JsOrder order2 = null; 
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
		
		// TODO issueDate
		String issueDate = "";
//		String issueDate = order.getIssueDate() != null
//				? Utils.formatDate(Utils.parseDateTime(order.getIssueDate()))
//				: "";
		// TODO acc
		final InlineLabel acc = new InlineLabel(AonStringUtils.SPACE);
//		final InlineLabel acc = new InlineLabel(AonStringUtils.SPACE
//				+ AonStringUtils.rightPad(AonStringUtils.defaultString(issueDate),13)
//				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
//						AonStringUtils.defaultString(order.getSeriesNumber()), 16), 17)
//
//				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
//						AonStringUtils.defaultString(order.getRegistry().getName()), 32),33)
//				+ AonStringUtils.rightPad("",17)		
//				);
		acc.setTitle("");
		acc.setStyleName(AON.AON_CSS.aonBold());
		
		DoubleBox  db1 = new DoubleBox();
		db1.setWidth("50px");
		db1.setStyleName(AON.AON_CSS.aonTextBox());
		// TODO
		db1.setValue(0.0);
//		db1.setValue(order.getTotalPackages());
		db1.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				// TODO
//				String requestData = "{\"total_packages\":\""+ db1.getValue() +"\"}";
//				parent.API.getWarehouse().updateDelivery(order.getId(), requestData);
			}
		});
		
		DoubleBox  db2 = new DoubleBox();
		db2.setWidth("50px");
		db2.setStyleName(AON.AON_CSS.aonTextBox());
		// TODO
		db2.setValue(0.0);
//		db2.setValue(order.getTotalWeight());
		db2.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				// TODO
//				String requestData = "{\"total_weight\":\""+ db2.getValue() +"\"}";
//				parent.API.getWarehouse().updateDelivery(order.getId(), requestData);
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
		
//		details.stream().forEach(detail ->{
//			FlowPanel line = new FlowPanel("pre");
//			
//			line.setStyleName(AON.AON_CSS.aonFixedFont());
//			line.addStyleName(AON.AON_CSS.aonFontMedium());
//			line.addStyleName(AON.AON_CSS.aonMarginBottom());
//			
//			Double discount = detail.getDiscountExpr() != null ? Double.parseDouble(detail.getDiscountExpr()) : 1.0;
//			Double importe = detail.getPrice() * detail.getQuantity() * (1 - (discount/100));
//			InlineLabel d = new InlineLabel(AonStringUtils.SPACE
//					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getLine() + ""),11)
//					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
//							AonStringUtils.defaultString(detail.getProductCode() + "-" + detail.getProductName()), 39), 44)
//					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getQuantity() + ""), 16)
//					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getPrice() + ""), 16)
//					+ AonStringUtils.rightPad(AonStringUtils.defaultString(detail.getDiscountExpr()), 16)
//					+ AonStringUtils.rightPad(AonStringUtils.defaultString( importe + ""), 15)	
//					);
//			d.setTitle("");
//			d.setStyleName(AON.AON_CSS.aonBold());
//			
//			line.add(d);
//			center.add(line);
//		});
		FlowPanel line = new FlowPanel("pre");
		
		line.setStyleName(AON.AON_CSS.aonFixedFont());
		line.addStyleName(AON.AON_CSS.aonFontMedium());
		line.addStyleName(AON.AON_CSS.aonMarginBottom());
		
		Double discount = orderDetail.getDiscountExpr() != null ? Double.parseDouble(orderDetail.getDiscountExpr()) : 1.0;
		Double importe = orderDetail.getPrice() * orderDetail.getQuantity() * (1 - (discount/100));
		InlineLabel d = new InlineLabel(AonStringUtils.SPACE
				+ AonStringUtils.rightPad(AonStringUtils.defaultString(orderDetail.getLine() + ""),11)
				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
						AonStringUtils.defaultString(orderDetail.getProductCode() + "-" + orderDetail.getProductName()), 39), 44)
				+ AonStringUtils.rightPad(AonStringUtils.defaultString(orderDetail.getQuantity() + ""), 16)
				+ AonStringUtils.rightPad(AonStringUtils.defaultString(orderDetail.getPrice() + ""), 16)
				+ AonStringUtils.rightPad(AonStringUtils.defaultString(orderDetail.getDiscountExpr()), 16)
				+ AonStringUtils.rightPad(AonStringUtils.defaultString( importe + ""), 15)	
				);
		d.setTitle("");
		d.setStyleName(AON.AON_CSS.aonBold());
		
		line.add(d);
		center.add(line);
		
		p.add(center);
		
		return p;
	}

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		openFootPanel();
	}

	public void selectTab(int index){
		tabPanel.selectTab(index);
	}
	
	public void openFootPanel() {
		Integer clientHeight = Window.getClientHeight();
		parent.changeSouthContentSize(clientHeight.doubleValue() / 3);
	}

	public void closeFootPanel() {
		parent.changeSouthContentSize(30.0);
	}
}
