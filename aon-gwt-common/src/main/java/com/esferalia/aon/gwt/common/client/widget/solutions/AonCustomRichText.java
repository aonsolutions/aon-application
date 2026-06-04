package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.richtexttoolbar.RichTextToolbar;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RichTextArea;

public class AonCustomRichText extends HTMLPanel  {

	private static final String EMPTY_STRING = "";
	private HTMLPanel textBoxPanel = new HTMLPanel(EMPTY_STRING);
	private RichTextArea richTextArea;
	private RichTextToolbar toolbar;
	
    public AonCustomRichText(String title) {
        super(EMPTY_STRING);
        addStyleName(AON.CSS.aonFlexColumn2());
        addStyleName(AON.CSS.aonCustomRichText());

        createTitle(title);
        createInput();
    }


	private void createTitle(String title) {
		HTMLPanel titleLabel = new HTMLPanel(AonStringUtils.isNotBlank(title) ? title : EMPTY_STRING);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
	}

	private void createInput() {
		textBoxPanel.addStyleName(AON.CSS.aonFlexColumn2());
		textBoxPanel.setWidth("100%");
		textBoxPanel.getElement().getStyle().setProperty("border", "1px solid #eee");
		textBoxPanel.getElement().getStyle().setProperty("border-radius", "10px");
		textBoxPanel.getElement().getStyle().setProperty("margin-top", ".5rem");
		
		richTextArea = new RichTextArea();
		richTextArea.setSize("100%", "100%");
		
		toolbar = new RichTextToolbar(richTextArea);
		
		textBoxPanel.add(toolbar);
		textBoxPanel.add(richTextArea);
		
		add(textBoxPanel);
	}
	
	public void setValue(String value) {
		this.richTextArea.setHTML(value);
	}

	public String getValue() {
		return this.richTextArea.getHTML();
	}
	
	public void setMinHeight(String height) {
		richTextArea.getElement().getStyle().setProperty("min-height", height);
	}

	public void addBlurHandler(BlurHandler handler) {
		richTextArea.addBlurHandler(handler);
	}

}
