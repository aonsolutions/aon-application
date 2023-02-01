package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class JsIRPFComputeKeyInfoGridPanel extends FlowPanel {
	
	private final Label title;
	private final Label subTitle;
	
	public JsIRPFComputeKeyInfoGridPanel() {
		title = new Label();
		title.setStyleName(AON.CSS.aonMarginTop());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonWidthAll());
		title.addStyleName(AON.CSS.aonTextCenter());
		title.addStyleName(AON.CSS.aonTextUppercase());
		title.addStyleName(AON.CSS.aonFontLarger());
		add( title );
		subTitle = new Label();	
		subTitle.addStyleName(AON.CSS.aonBold());
		subTitle.addStyleName(AON.CSS.aonWidthAll());
		subTitle.addStyleName(AON.CSS.aonTextCenter());
		subTitle.addStyleName(AON.CSS.aonTextUppercase());
		subTitle.addStyleName(AON.CSS.aonFontLarger());
		add( subTitle );
	}
	
	@Override
	public void setTitle( String title) {
		super.setTitle(title);
		this.title.setText(title);	
	}
	public void setSubTitle( String subTitle) {
		this.subTitle.setText(subTitle);	
	}
	
	public void addContent(JsIRPFComputeKeyInfo info) {
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonFontMedium());
		table.addStyleName(AON.CSS.aonBlockCenter());
		table.addStyleName(AON.CSS.aonMarginTop());
		if (info.getMessages() != null && info.getMessages().length > 0) {
			for ( String message : info.getMessages()) {
				table.addRow(AON.CSS.aonLineHeightDouble())
					.addCell( new Label(message));
			}
		}
		add( table );
	}

	public void addContent(String html) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.appendHtmlConstant(html);
		add( new HTMLPanel( builder.toSafeHtml() ) );
	}
	
}
