package com.esferalia.aon.gwt.common.client.widget.solutions;


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

public class AonIbanTextBox extends SimplePanel implements HasSelectionHandlers<Suggestion>, HasEnabled {
	
	private SuggestBox iban1;
	private AonTextBox iban2 = new AonTextBox();
	private AonTextBox iban3 = new AonTextBox();
	private AonTextBox iban4 = new AonTextBox();
	private AonTextBox iban5 = new AonTextBox();
	private AonTextBox iban6 = new AonTextBox();
	private AonTextBox bic = new AonTextBox();
	
	private AonTextBox[] textBoxes = new AonTextBox[]{iban2,iban3,iban4,iban5,iban6};
	
	public AonIbanTextBox(SuggestOracle suggestOracle) {
		this(suggestOracle, false);
	}
	public AonIbanTextBox(SuggestOracle suggestOracle, boolean showbic) {
		super( DOM.createSpan());
		FlowPanel panel = new FlowPanel();
		panel.addStyleName(AON.CSS.aonNowrap());
		panel.addStyleName(AON.CSS.aonInline());
		this.add(panel);
		iban1 = new SuggestBox(suggestOracle);
		iban1.setStyleName(AON.CSS.aonInputText());
		iban1.setWidth("100px");
		panel.add(iban1);
		for (AonTextBox textBox : textBoxes ) {
			textBox.addStyleName(AON.CSS.aonMarginLeft());
			textBox.setMaxLength(4);
			textBox.setVisibleLength(5);
			panel.add(textBox);
		}
		if (showbic) {
			InlineLabel bicLabel = new InlineLabel("BIC");
			bicLabel.setStyleName(AON.CSS.aonMarginLeft());
			bicLabel.addStyleName(AON.CSS.aonMarginRight());
			panel.add(bicLabel);
			bic.addStyleName(AON.CSS.aonMarginLeft());
			bic.setMaxLength(11);
			bic.setVisibleLength(10);
			panel.add(bic);
		}
		FlowPanel panel2 = new FlowPanel();
		InlineLabel label = new InlineLabel("Comience a escribir para recuperar algun banco de la empresa");
		label.addStyleName(AON.CSS.aonFontSmall());
		label.addStyleName(AON.CSS.aonItalic());
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
	
	public void setValue(String iban, String bic2 ) {
		setValue(iban);
		bic.setValue( bic2 );		
	}

	public void setValue(String value) {
		iban1.setValue( AonStringUtils.substring(value, 0,4));
		iban2.setValue( AonStringUtils.substring(value, 4,8));
		iban3.setValue( AonStringUtils.substring(value, 8,12));
		iban4.setValue( AonStringUtils.substring(value, 12,16));
		iban5.setValue( AonStringUtils.substring(value, 16,20));
		iban6.setValue( AonStringUtils.substring(value, 20,24));
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
