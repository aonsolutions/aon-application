package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.IIbanContainer;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;

public class IbanTextBox extends SimplePanel implements HasSelectionHandlers<Suggestion>, HasEnabled {
	
	SuggestBox iban1;
	TextBox iban2 = new TextBox();
	TextBox iban3 = new TextBox();
	TextBox iban4 = new TextBox();
	TextBox iban5 = new TextBox();
	TextBox iban6 = new TextBox();
	TextBox bic = new TextBox();
	
	private TextBox[] textBoxes = new TextBox[]{
			iban2,iban3,iban4,iban5,iban6};
	
	public IbanTextBox(SuggestOracle suggestOracle) {
		this(suggestOracle, false);
	}
	public IbanTextBox(SuggestOracle suggestOracle, boolean showbic) {
		super( DOM.createSpan());
		FlowPanel panel = new FlowPanel();
		panel.addStyleName(AON.AON_CSS.aonNowrap());
		panel.addStyleName(AON.AON_CSS.aonInline());
		this.add(panel);
		iban1 = new SuggestBox(suggestOracle);
		iban1.setStyleName(AON.AON_CSS.aonInputText());
		iban1.setWidth("100px");
		panel.add(iban1);
		for (TextBox textBox : textBoxes ) {
			textBox.setStyleName(AON.AON_CSS.aonInputText());
			textBox.addStyleName(AON.AON_CSS.aonMarginLeft());
			textBox.setMaxLength(4);
			textBox.setVisibleLength(5);
			panel.add(textBox);
		}
		if (showbic) {
			InlineLabel bicLabel = new InlineLabel("BIC");
			bicLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
			bicLabel.addStyleName(AON.AON_CSS.aonMarginRight());
			panel.add(bicLabel);
			bic.setStyleName(AON.AON_CSS.aonInputText());
			bic.addStyleName(AON.AON_CSS.aonMarginLeft());
			bic.setMaxLength(11);
			bic.setVisibleLength(10);
			panel.add(bic);
		}
		FlowPanel panel2 = new FlowPanel();
		InlineLabel label = new InlineLabel("Comience a escribir para recuperar algun banco de la empresa");
		label.addStyleName(AON.AON_CSS.aonFontSmall());
		label.addStyleName(AON.AON_CSS.aonItalic());
		panel2.add(label);
		panel.add(panel2);
	}
	
	public String getValue() {
		return iban1.getValue() + iban2.getValue() + iban3.getValue()
			 + iban4.getValue() + iban5.getValue() + iban6.getValue();
	}
	
	public String getBic() {
		return bic.getValue();
	}
	
	public void setValue(String value) {
		iban1.setValue( AonStringUtils.substring(value, 0,4));
		iban2.setValue( AonStringUtils.substring(value, 4,8));
		iban3.setValue( AonStringUtils.substring(value, 8,12));
		iban4.setValue( AonStringUtils.substring(value, 12,16));
		iban5.setValue( AonStringUtils.substring(value, 16,20));
		iban6.setValue( AonStringUtils.substring(value, 20,24));
		bic.setValue( AonStringUtils.substring(value, 24));
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Suggestion> handler) {
		return iban1.addSelectionHandler(handler);
	}

	public static class IbanSuggestion implements Suggestion {
		
		private IIbanContainer ibanContainer;

		public IbanSuggestion(IIbanContainer ibanContainer) {
			this.ibanContainer = ibanContainer;
		}

		@Override
		public String getDisplayString() {
			return ibanContainer.getDisplay();
		}

		@Override
		public String getReplacementString() {
			return AonStringUtils.rightPad(ibanContainer.getIBan(), 24) + ibanContainer.getBic();
		}
		
		public IIbanContainer getIbanContainer() {
			return ibanContainer;
		}
	}

	@Override
	public boolean isEnabled() {
		return iban1.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		iban1.setEnabled(enabled);
		iban2.setEnabled(enabled);
		iban3.setEnabled(enabled);
		iban4.setEnabled(enabled);
		iban5.setEnabled(enabled);
		iban6.setEnabled(enabled);
		bic.setEnabled(enabled);
	}
}
