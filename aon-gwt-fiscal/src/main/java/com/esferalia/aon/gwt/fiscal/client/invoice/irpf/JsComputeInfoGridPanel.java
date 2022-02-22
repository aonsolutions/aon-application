package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class JsComputeInfoGridPanel extends FlowPanel {
	
	private final Label title;
	private final Label subTitle;
	
	public JsComputeInfoGridPanel() {
		setStyleName(AON.CSS.aonBackgroundLigthGray());
		
		title = new Label();
		title.setStyleName(AON.CSS.aonMarginTop());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonWidthAll());
		title.addStyleName(AON.CSS.aonTextCenter());
		title.addStyleName(AON.CSS.aonTextUppercase());
		add( title );
		subTitle = new Label();	
		subTitle.setStyleName(AON.CSS.aonMarginTop());
		subTitle.addStyleName(AON.CSS.aonBold());
		subTitle.addStyleName(AON.CSS.aonWidthAll());
		subTitle.addStyleName(AON.CSS.aonTextCenter());
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
	
	public void addContent(JsIRPFComputeInfo info) {
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonMarginTop());
		table.addStyleName(AON.CSS.aonFontSmaller());
		table.addStyleName(AON.CSS.aonBlockCenter());
		table
			.addLabelWidgetRow("F\u00F3rmula", new Label(info.getFormula()))
			.addLabelWidgetRow("Resultado", new Label(info.getResult()))
		;
		add( table );
	}

	public void setReportTitle( String title) {
		this.title.setText(title);
	}

}
