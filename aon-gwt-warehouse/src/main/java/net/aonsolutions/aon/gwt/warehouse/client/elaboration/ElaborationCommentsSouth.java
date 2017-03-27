package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ElaborationCommentsSouth extends DockLayoutPanel{
	
	private MainElaboration parent;
	private JsElaboration elaboration;
	
	public ElaborationCommentsSouth(MainElaboration parent, JsElaboration jsElaboration) {
		super(Unit.PX);
		this.parent = parent;
		this.elaboration = elaboration;
		load(jsElaboration.getComments());
	}
	
	
	private void load(String commentsValue) {
		final TextArea comments = new TextArea();
		comments.setText(commentsValue);
		comments.setStyleName(AON.AON_CSS.aonPadding());
		comments.setWidth("100%");
		comments.setHeight("100px");
		
//		comments.setText(jsElaboration != null && 
//				jsElaboration.getComments() != null ? jsElaboration.getComments() : "");
//		comments.setCharacterWidth(20);
//		comments.setVisibleLines(3);
		
		comments.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				parent.updateElaboration(null);
			}
		});
		
		addNorth(comments, 100);
		
		
//		final FlowPanel p = new FlowPanel("pre");
//		final FlowPanel headerPanel = new FlowPanel("pre");
//		headerPanel.setStyleName(AON.AON_CSS.aonFixedFont());
//		headerPanel.addStyleName(AON.AON_CSS.aonFontMedium());
//		Label header = new Label(" FECHA        " //13
//				+ "SERIE/NUMERO     " //17
//				+ "PROVEEDOR                        " //33
//				+ "IMPORTE TOTAL    ");  //10
//		header.setStyleName(AON.AON_CSS.aonBold());
//		header.addStyleName(AON.AON_CSS.aonMarginTop());
//		header.addStyleName(AON.AON_CSS.aonBorderTop());
//		header.addStyleName(AON.AON_CSS.aonBorderBottom());
//		headerPanel.add(header);
//		p.add(headerPanel);
//		
//		final FlowPanel panel = new FlowPanel("pre");
//		panel.setStyleName(AON.AON_CSS.aonFixedFont());
//		panel.addStyleName(AON.AON_CSS.aonFontMedium());
//		panel.addStyleName(AON.AON_CSS.aonMarginBottom());
		
//		String issueDate = purchase.getIssueDate() != null
//				? Utils.formatDate(Utils.parseDateTime(purchase.getIssueDate()))
//				: "";
//		
//		final InlineLabel acc = new InlineLabel(AonStringUtils.SPACE
//				+ AonStringUtils.rightPad(AonStringUtils.defaultString(issueDate),13)
//				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
//						AonStringUtils.defaultString(purchase.getSeriesNumber()), 16), 17)
//
//				+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
//						AonStringUtils.defaultString(purchase.getRegistry().getName()), 32),33)
//				+ AonStringUtils.rightPad("",17)		
//				);
//		acc.setTitle("");
//		acc.setStyleName(AON.AON_CSS.aonBold());
		
//		panel.add(acc);
//		p.add(panel);
//		
//		final FlowPanel headerPanel2 = new FlowPanel("pre");
//		headerPanel2.setStyleName(AON.AON_CSS.aonFixedFont());
//		headerPanel2.addStyleName(AON.AON_CSS.aonFontMedium());
//		Label header2 = new Label("      "//6
//				+ "LINEA     " //10
//				+ "PRODUCTO                                " //40
//				+ "CANTIDAD       " //15
//				+ "PENDIENTE      " //15
//				+ "PRECIO         " //15
//				+ "DESCUENTO      " //15
//				+ "IMPORTE        "); //15 
//		header2.setStyleName(AON.AON_CSS.aonBold());
//		header2.addStyleName(AON.AON_CSS.aonMarginTop());
//		header2.addStyleName(AON.AON_CSS.aonBorderTop());
//		header2.addStyleName(AON.AON_CSS.aonBorderBottom());
//		headerPanel2.add(header2);
//		p.add(headerPanel2);

//		addNorth(p, 100);
	
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


	public void refresh(JsElaboration elaboration) {
		// TODO Auto-generated method stub
		
	}
	
	
}
