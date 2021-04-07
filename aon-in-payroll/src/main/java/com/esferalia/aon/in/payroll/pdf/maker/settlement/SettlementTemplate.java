package com.esferalia.aon.in.payroll.pdf.maker.settlement;

import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfColors.GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfFormats.formatDate;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfFormats.toLatinNumber;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfSettings.ALIGNMENT.JUSTIFY;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfSettings.ALIGNMENT.LEFT;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfSettings.ALIGNMENT.RIGHT;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfSettings.PAGE_TYPE.VERTICAL;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.OptionalToolkit.safeDouble;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.OptionalToolkit.safeString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.OptionalToolkit.safeValue;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawBox;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawText;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextRight;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.CraTypes.get_type;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfBox;
import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfFile;
import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfText.PdfTextBuilder;
import com.esferalia.aon.in.payroll.pdf.api.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.settings.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.api.settings.PdfSettings.VERTICAL_ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.maker.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.Accrual;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.Deduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.DeductionTypes;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.UnknownCraException;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement;

public class SettlementTemplate extends PdfFile {

	private Settlement settlement;

	public SettlementTemplate(Settlement settlement, float x, float y, PDDocument doc, ResourceBundle words,
			OutputStream out) {
		super(x, y, doc, words, out);
		this.settlement = settlement;
	}

	/**
	 * Print a settlement
	 * 
	 * @param out
	 * @param settlement
	 * @param locale
	 * @throws CanNotCreatePdfException
	 */
	public static void print(OutputStream out, Settlement settlement, Locale locale) throws CanNotCreatePdfException {

		SettlementTemplate template = null;
		try {
			ResourceBundle words = ResourceBundle.getBundle(
					"com.esferalia.aon.in.payroll.pdf.creators.settlement.bundles.SettlementBundle",
					(Locale) safeValue(locale, new Locale("Es")));

			template = new SettlementTemplate(settlement, 0, 810, new PDDocument(), words, out);
			template.setDefaults(HELVETICA, 10f, BLACK, PdfColors.GRAY);

			template.lang = locale;
			template.limitY = 50;

			template.newPage(VERTICAL);

			template.drawUpperTexts();
			template.checkJump();

			template.drawAccruals();
			template.checkJump();

			template.drawDeductions();
			template.checkJump();

			template.drawLegalText();
			template.print();

		} catch (Exception e) {
			if (template != null)
				try {
					template.close();
				} catch (IOException e1) {
				}
			throw new CanNotCreatePdfException(e);
		}
	}

	/** Draw the text in the upper */
	private void drawUpperTexts() {

		drawTitle();
		drawEnterprise();
		drawEmployee();
		drawDeclare();
		drawDeclaration();

	}

	/** Draw the title */
	private void drawTitle() {
		final String titleTxt = text("TITLE");
		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).width(590).height(0).x(x()).y(y()).color(primary).font(HELVETICA_BOLD).fontSize(16f)
				.content(titleTxt).verticalAlignment(VERTICAL_ALIGNMENT.CENTER).horizontalAlignment(ALIGNMENT.CENTER);

		PdfText title = builder.build();
		title.draw();

		down(40);
		right(55);
	}

	/** Draw the enterprise text */
	private void drawEnterprise() {

		String[] variables = { "enterprise_name", "enterprise_nif", "enterprise_address" };
		String[] values = { safeString(settlement.enterpriseName()), safeString(settlement.enterpriseNif()),
				safeString(settlement.enterpriseAddress()), };
		
		final String enterpiseTxt = replaceVariables(variables, values, text("ENTERPRISE"));

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).width(480).height(0).x(x()).y(y()).lineSpacing(6f).color(primary).font(HELVETICA)
				.fontSize(9f).content(enterpiseTxt).verticalAlignment(VERTICAL_ALIGNMENT.CENTER)
				.horizontalAlignment(JUSTIFY);

		PdfText text = builder.build();
		text.drawMultiple(limitY);
		down(text.height());
	}

	/** Draw the employee text */
	private void drawEmployee() {

		final String dateFormat = text("DATE FORMAT");

		String[] variables = new String[] { "employee_name", "employee_nif", "employee_category",
				"employee_antiquity" };
		String[] values = new String[] { safeString(settlement.employeeName()), safeString(settlement.employeeNif()),
				safeString(settlement.employeeCategory()),
				safeString(formatDate(settlement.employeeAntiquity(), dateFormat)) };

		final String employee_txt = replaceVariables(variables, values, text("EMPLOYEE"));

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).width(480).height(0).x(x()).y(y()).color(primary).font(HELVETICA).fontSize(9f)
				.lineSpacing(6f).content(employee_txt).verticalAlignment(VERTICAL_ALIGNMENT.CENTER)
				.horizontalAlignment(JUSTIFY);

		PdfText text = builder.build();
		text.drawMultiple(limitY);
		down(text.height());
	}

	/** Draw declare text */
	private void drawDeclare() {
		final String declareTxt = text("DECLARE");

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).width(480).height(0).x(x()).y(y()).color(primary).font(HELVETICA).fontSize(9f)
				.lineSpacing(6f).content(declareTxt).horizontalAlignment(LEFT);

		PdfText text = builder.build();
		text.drawMultiple(limitY);
		down(text.height());
	}

	/** Draw the declaration text */
	private void drawDeclaration() {

		final String dateFormat = text("DATE FORMAT");

		String[] variables = new String[] { "end_date", "end_cause", "text_total", "total_amount" };
		String[] values = new String[] { safeString(formatDate(settlement.endDate(), dateFormat)),
				safeString(settlement.endCause()), "UNOS CUANTOS", toLatinNumber(settlement.total().orElse(null)) };

		final String declarationTxt = replaceVariables(variables, values, text("DECLARATION"));

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).width(480).height(0).x(x()).y(y()).color(primary).font(HELVETICA).fontSize(9f)
				.lineSpacing(6f).content(declarationTxt).verticalAlignment(VERTICAL_ALIGNMENT.CENTER)
				.horizontalAlignment(JUSTIFY);

		PdfText text = builder.build();
		text.drawMultiple(limitY);
		down(text.height() + 0);
	}

	/**
	 * Draw accruals
	 * 
	 * @param template
	 * @throws IOException
	 */
	private void drawAccruals() throws IOException {

		String titleTxt = text("ACCRUALS").toUpperCase();
		String totalsTxt = text("TOTALS").toUpperCase();
		String accrualTotalTitle = text("ACCRUAL TOTAL").toUpperCase();
		String accrualTotal = toLatinNumber(settlement.getAccrualTotal().orElse(0.00)) + " " + text("CURRENCY");

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).width(595).height(0).x(x()).y(y()).color(primary).font(HELVETICA_BOLD)
				.fontSize(fontsize + 2).content(titleTxt).verticalAlignment(VERTICAL_ALIGNMENT.CENTER)
				.horizontalAlignment(ALIGNMENT.LEFT);

		PdfText title = builder.build();
		title.draw();
		drawBox(contents, x() + 400, y() - 10, 80, 20, LIGHT_GRAY);

		builder.stream(contents).width(80).height(20).x(x() + 400).y(y() - 10).margin_x(5).font(HELVETICA).fontSize(9f)
				.content(totalsTxt).verticalAlignment(VERTICAL_ALIGNMENT.CENTER)
				.horizontalAlignment(ALIGNMENT.RIGHT);

		PdfText totals = builder.build();
		totals.draw();
		down(20);

		Map<Integer, ArrayList<Accrual>> accruals = settlement.getAccruals();
		accruals.entrySet().stream().sorted(Map.Entry.<Integer, ArrayList<Accrual>>comparingByKey()).forEach(m -> {
			try {
				double local_total = m.getValue().stream().mapToDouble(accrual -> safeDouble(accrual.getAmount()))
						.sum();
				if (local_total != 0) {

					String accrual_txt = m.getKey() + ". " + get_type(m.getKey(), lang);
					String accrual_total_txt = toLatinNumber(local_total) + " " + text("CURRENCY");

					drawText(contents, accrual_txt, x(), y(), BLACK, HELVETICA_BOLD, fontsize);
					drawTextRight(contents, new PDRectangle(x() + 292, y() - 5, 100, 10), accrual_total_txt, BLACK,
							HELVETICA, 9f, 5, 5);
					drawBox(contents, x(), y() - 2, 392, .2f, BLACK);
					down(20);

					m.getValue().stream().forEach(n -> {
						String entry_value = toLatinNumber(n.getAmount().orElse(null)) + " " + text("CURRENCY");
						String entry_txt = " por " + safeString(n.getDescription());

						new PdfText(x(), y(), 60, 15, contents, entry_value, BLACK, HELVETICA, 9f, RIGHT).draw();
						new PdfText(x() + 64, y(), 250, 15, contents, entry_txt, BLACK, HELVETICA, 9f, LEFT).draw();

						down(10.5f);
					});
				}
			} catch (IOException | UnknownCraException e) {
				e.printStackTrace();
			}
			down(5);
		});
		down(5);

		drawTextRight(contents, new PDRectangle(x() + 280, y(), 200, 25), accrualTotal, BLACK, HELVETICA, fontsize, 7,
				5);
		drawTextRight(contents, new PDRectangle(x() + 190, y(), 200, 25), accrualTotalTitle, BLACK, HELVETICA,
				fontsize, 5, 5);
	}

	/**
	 * Draw deductions
	 * 
	 * @param template
	 * @throws IOException
	 */
	private void drawDeductions() throws IOException {

		String title = text("DEDUCTIONS");
		String deduction_total_title = text("DEDUCTION TOTAL");
		String deduction_total = toLatinNumber(settlement.deduction_total().orElse(null)) + " " + text("CURRENCY");
		String payroll_total_title = text("TOTAL");
		String payroll_total = toLatinNumber(settlement.total().orElse(null)) + " " + text("CURRENCY");

		drawText(contents, title, x(), y() - 5, BLACK, HELVETICA_BOLD, fontsize + 2);
		down(20);

		Map<Integer, ArrayList<Deduction>> deductions = settlement.getDeductions();

		deductions.entrySet().stream().sorted(Map.Entry.<Integer, ArrayList<Deduction>>comparingByKey()).forEach(m -> {
			try {
				double local_total = m.getValue().stream().mapToDouble(accrual -> safeDouble(accrual.getAmount()))
						.sum();

				String deduction_txt = m.getKey() + ". " + DeductionTypes.getType(m.getKey());
				String deduction_total_txt = toLatinNumber(local_total) + " " + text("CURRENCY");

				drawText(contents, deduction_txt, x() + 3, y(), BLACK, HELVETICA_BOLD, fontsize);
				drawTextRight(contents, new PDRectangle(x() + 292, y() - 5, 100, 10), deduction_total_txt, BLACK,
						HELVETICA, 9f, 2, 5);
				drawBox(contents, x(), y() - 2, 392, .2f, BLACK);

				down(20);

				if (local_total != 0)
					m.getValue().stream().forEach(n -> {
						String entry_value = toLatinNumber(n.getAmount().orElse(null)) + " " + text("CURRENCY");
						String entry_txt = " por " + n.getDescription().orElse("");
						String entry_percent = (n.getPercent().isEmpty()) ? ""
								: toLatinNumber(n.getPercent().get()) + " % ";

						if (n.getAmount().isPresent() && n.getAmount().get() != 0) {
							PdfText quantity = new PdfText(x(), y(), 60, 15, contents, entry_percent, BLACK, HELVETICA,
									9f, RIGHT);
							quantity.draw();

							new PdfText(x() + 64, y(), 210, 15, contents, entry_txt, BLACK, HELVETICA, 9f, LEFT).draw();
							new PdfText(x() + 64 + 210, y(), 60, 15, contents, entry_value, BLACK, HELVETICA, 9f, RIGHT)
									.draw();

							down(10);
						}
					});
			} catch (IOException e) {
				e.printStackTrace();
			}
			down(5);
		});
		down(15);

		drawTextRight(contents, new PDRectangle(x() + 278, y(), 200, 25), deduction_total, BLACK, HELVETICA, fontsize,
				5, 5);
		drawTextRight(contents, new PDRectangle(x() + 190, y(), 200, 25), deduction_total_title, BLACK, HELVETICA,
				fontsize, 5, 5);

		down(25);

		PdfBox b = new PdfBox(x() + 400, y() - 3, 80, 22, LIGHT_GRAY, contents);
		b.draw();

		drawTextRight(contents, new PDRectangle(x() + 278, y(), 200, 25), payroll_total, BLACK, HELVETICA_BOLD,
				fontsize, 5, 5);
		drawTextRight(contents, new PDRectangle(x() + 190, y(), 200, 25), payroll_total_title, BLACK, HELVETICA_BOLD,
				fontsize, 5, 5);

		down(15);
	}

	/**
	 * Draw compulsory legal stuff
	 * 
	 * @param template
	 * @throws IOException
	 */
	private void drawLegalText() throws IOException {
		down(10);
		String legal_txt = text("LEGAL DATA");
		String legal_advice_txt = text("LEGAL ADVICE");
		String employee_sign_txt = text("EMPLOYEE SIGN");
		String enterprise_sign_txt = text("ENTERPRISE SIGN");
		String representative_sign_txt = text("REPRESENTATIVE SIGN");

		String[] variables = { "location" };
		String[] values = { safeString(settlement.location()) };
		String date_txt = formatDate(new Date(), replaceVariables(variables, values, text("DATE"))).orElse("");

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).x(x()).width(480).y(y()).font(HELVETICA).fontSize(9f).content(legal_txt)
				.lineSpacing(6f).horizontalAlignment(ALIGNMENT.JUSTIFY);

		PdfText legal_text = builder.build();
		legal_text.drawMultiple(limitY);

		down(legal_text.height());

		builder.stream(contents).y(y()).content(legal_advice_txt);

		PdfText legal_advice_text = builder.build();
		legal_advice_text.drawMultiple(limitY);

		down(legal_advice_text.height());

		builder.stream(contents).y(100).content(date_txt);

		PdfText date_text = builder.build();
		date_text.drawMultiple(limitY);
		checkJump();
		down(100);

		float margin = 100;
		float width = 470 / 2 - margin / 2;

		if (settlement.exist_representative().orElse(false)) {
			margin = 12;
			width = 470 / 3 - margin / 2;
		}

		builder.stream(contents).width(width).height(40).y(10).content(employee_sign_txt).color(GRAY)
				.horizontalAlignment(ALIGNMENT.CENTER);

		PdfText employee_sign = builder.build();
		employee_sign.draw();

		right(width + margin);
		builder.stream(contents).width(width).x(x()).content(enterprise_sign_txt);

		PdfText enterprise_sign = builder.build();
		enterprise_sign.draw();

		right(width + margin);
		builder.stream(contents).width(width).x(x()).content(representative_sign_txt);

		PdfText representative_sign = builder.build();
		representative_sign.draw();

	}

	/**
	 * Replace variables in the bundle
	 * 
	 * @param words
	 * @param values
	 * @param text
	 * @return
	 */
	public static String replaceVariables(String[] words, String[] values, String text) {
		for (int i = 0; i < words.length; i++)
			text = text.replace("$" + words[i], values[i]);
		return text;
	}

	public void checkJump() throws IOException {
		if (jump()) {
			newPage(VERTICAL);
			y(800);
		}
	}

}
