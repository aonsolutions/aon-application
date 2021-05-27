package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.watson.util.AonWordUtils.abbreviate;
import static com.esferalia.aon.watson.util.AonWordUtils.capitalizeFully;

import com.esferalia.aon.gwt.common.client.widget.ComboBox;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonWordUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class ActualExtraListBox extends ComboBox<ActualExtra> {
	
	
	
	interface Template extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<span class=\"aon-nowrap\"><span class=\"aon-bold\">{1}</span> {0}, {2}</span>")
		SafeHtml span(SafeHtml payment, SafeHtml date, SafeHtml agreement);
	}
	
	private static final Template template = GWT.create(Template.class);

	private static class ActualExtraFormatSafeHtmlRenderer extends
			AbstractFormatSafeHtmlRenderer<ActualExtra> implements Format<ActualExtra>{
		
		private static RegExp ORDER = RegExp.compile(
				"^(\\[\\d+\\])?(\\s*PAGA\\s*)?(.*)$");
		private static RegExp AGREEMENT = RegExp.compile(
				"^(\\s*CONVENIO\\s*)?(COLECTIVO\\s*)?(DEL?\\s*)?(.*)$","i");

		private static DateTimeFormat DATE_FORMAT = DateTimeFormat
				.getFormat("dd/MM/yyyy");

		@Override
		public String format(ActualExtra c) {
			String payment = capitalizeFully(ORDER.exec(c.getPaymentDescription()).getGroup(3));
			String agreement = capitalizeFully(AGREEMENT.exec(c.getAgreementDescription()).getGroup(4));
			return c.getId() + " " + DATE_FORMAT.format(c.getIssueDate()) + " " +payment + ", " + abbreviate(agreement, 20, 25, "...");
		}

		@Override
		public Format<ActualExtra> getFormat() {
			return this;
		}

		@Override
		public SafeHtml render(ActualExtra actualExtra) {

			String payment = capitalizeFully(ORDER.exec(actualExtra.getPaymentDescription()).getGroup(3));
			String agreement = capitalizeFully(AGREEMENT.exec(actualExtra.getAgreementDescription()).getGroup(4));
			
			return template.span( 
					SafeHtmlUtils.fromString(payment),
					SafeHtmlUtils.fromString(DATE_FORMAT.format(actualExtra.getIssueDate())),
					SafeHtmlUtils.fromString(abbreviate(agreement, 20, 25, "..."))
					);
		}
		

	}
	
	private static final ActualExtraFormatSafeHtmlRenderer RENDERER = new ActualExtraFormatSafeHtmlRenderer();

	public ActualExtraListBox() {
		super ((AbstractFormatSafeHtmlRenderer<ActualExtra>)RENDERER);
	}
	
	
}