package com.esferalia.aon.gwt.fiscal.client.invoice.irpf;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class JsComputeInfoGridPanel extends FlowPanel {
	
	private final Label title;
	private final Label subTitle;
	
	public JsComputeInfoGridPanel() {
		title = new Label();
		title.setStyleName(AON.CSS.aonMarginTop());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonWidthAll());
		title.addStyleName(AON.CSS.aonTextCenter());
		title.addStyleName(AON.CSS.aonTextUppercase());
		title.addStyleName(AON.CSS.aonFontXLarger());
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
	
	public void addContent(JsIRPFComputeInfo info) {
		AonDisplayTable table = new AonDisplayTable();
		table.addStyleName(AON.CSS.aonFontMedium());
		table.addStyleName(AON.CSS.aonBlockCenter());
		table.addStyleName(AON.CSS.aonMarginTop());
		table.addRow(AON.CSS.aonLineHeightDouble())
			.addCell( new Label("F\u00F3rmula"), AON.CSS.aonWidth100() )
			.addCell( getFormula(info) );
		table.addRow(AON.CSS.aonLineHeightDouble())
			.addCell( new Label("Resultado"), AON.CSS.aonWidth100() )
			.addCell( getResult(info) );
		add( table );
	}

	interface SafeTemplate extends SafeHtmlTemplates {
		@Template ("<span style=\""
				+ "border: 0.5px solid black; "
				+ "color: black; "
				+ "font-family: Fixed, monospace !important; "
				+ "font-size: 0.9em; "
				+ "padding: 2px 2px 2px 3px; "
				+ "text-align: center; "
				+ "display: inline;"
				+ "margin-left: 2px;"
				+ "margin-right: 2px;"
				+ "\">{0}</span>")

		SafeHtml text(String key);
	}
	private static final SafeTemplate template = GWT.create(SafeTemplate.class);
	
	private HTMLPanel getFormula(JsIRPFComputeInfo info) {
		int i = 0;
		String pattern = info.getPattern();
		for (String keyString  : info.getKeys()) {
			String searchPattern = "{"+i+"}";
			Mod111Key key = Mod111Key.valueOf(keyString);
			String box = template.text(key.getBoxAsString()).asString();
			pattern = AonStringUtils.replace(pattern, searchPattern, box);
			i++;
		}
		Mod111Key key = Mod111Key.valueOf(info.getKey());
		pattern += " = " + template.text(key.getBoxAsString()).asString();  
		return new HTMLPanel(pattern);
	}
	
	private HTMLPanel getResult(JsIRPFComputeInfo info) {
		int i = 0;
		String pattern = info.getPattern();
		for (String value  : info.getKeyValues()) {
			String searchPattern = "{"+i+"}";
			String box = template.text(value).asString();
			pattern = AonStringUtils.replace(pattern, searchPattern, box);
			i++;
		}
		pattern += " = " + template.text(info.getValue()).asString();  
		return new HTMLPanel(pattern);
	}
}
