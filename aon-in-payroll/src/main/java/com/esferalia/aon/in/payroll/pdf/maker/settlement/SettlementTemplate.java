package com.esferalia.aon.in.payroll.pdf.maker.settlement;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.formatDate;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.toLatinNumber;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.JUSTIFY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.LEFT;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.RIGHT;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.PAGE_TYPE.VERTICAL;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.DataToolkit.safeDouble;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.DataToolkit.safeString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.DataToolkit.safeValue;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.Number2Text.convertDouble;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawBox;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawText;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextRight;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.CraTypes.getType;
import static com.esferalia.aon.watson.util.AonStringUtils.trimToEmpty;
import static com.esferalia.aon.watson.util.AonStringUtils.upperCase;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfBox;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfFile;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfImage;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfText.PdfTextBuilder;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.VERTICAL_ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DeductionTypes;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.UnknownCraException;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement;
import com.esferalia.aon.in.payroll.pdf.maker.settlement.beans.Settlement.SettlementBuilder;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SettlementTemplate extends PdfFile {

	private Settlement settlement;
	private byte[]	   logo;
	private byte[]	   signature;

	public SettlementTemplate(
			Settlement settlement, float x, float y, PDDocument doc, ResourceBundle words, OutputStream out,
			float limitY, float startPointY
	) {
		super(x, y, doc, words, out, limitY, startPointY);
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
	public static void print(OutputStream out, SettlePrintConfiguration config) throws CanNotCreatePdfException {

		Settlement settlement = config.getSettlement();

		if (out == null)
			throw new CanNotCreatePdfException("No output Stream given.");
		if (settlement == null)
			settlement = new SettlementBuilder().build();

		SettlementTemplate template = null;
		try
		{
			ResourceBundle words = ResourceBundle.getBundle(
					"com.esferalia.aon.in.payroll.pdf.maker.settlement.bundles.SettlementBundle",
					(Locale) safeValue(config.getLanguage(), new Locale("Es")));

			template = new SettlementTemplate(settlement, 0, 810, new PDDocument(), words, out, 0, 800);
			template.setDefaults(HELVETICA, 10f, BLACK, PdfColors.GRAY);

			template.lang	= (Locale) safeValue(config.getLanguage(), new Locale("Es"));
			template.limitY	= 200;
			template.logo = config.getLogo();
			template.signature = config.getSignature();

			template.newPage(VERTICAL);

			template.drawUpperTexts();
			template.checkJump();

			template.drawPayments();
			template.checkJump();

			template.drawDeductions();
			template.checkJump();

			template.drawLegalText();
			template.print();
			template.close();

		} catch (Exception e)
		{
			e.printStackTrace();
			if (template != null)
				try
				{
					template.close();
				} catch (IOException ignored)
				{
				}
			throw new CanNotCreatePdfException(e);
		}
	}

	private void drawUpperTexts() {

		drawTitle();
		drawEnterprise();
		drawEmployee();
		drawDeclare();
		drawDeclaration();

	}

	private void drawTitle() {
		final String   titleTxt	= text("TITLE");
		PdfTextBuilder builder	= new PdfTextBuilder();
		builder.stream(contents).width(590).height(0).x(x()).y(y()).color(primary).font(HELVETICA_BOLD).fontSize(16f)
				.content(titleTxt).verticalAlignment(VERTICAL_ALIGNMENT.CENTER).horizontalAlignment(ALIGNMENT.CENTER);

		PdfText title = builder.build();
		title.draw();

		down(40);
		right(55);
	}

	private void drawEnterprise() {

		String[] variables = { "enterpriseName", "enterpriseNif", "enterpriseAddress" };
		String[] values	   = { safeString(settlement.getEnterpriseName()), safeString(settlement.getEnterpriseNif()),
				safeString(settlement.getEnterpriseAddress()), };

		final String enterpiseTxt = replaceVariables(variables, values, text("ENTERPRISE"));

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).width(480).height(0).x(x()).y(y()).lineSpacing(3f).color(primary).font(HELVETICA)
				.fontSize(9f).content(enterpiseTxt).verticalAlignment(VERTICAL_ALIGNMENT.CENTER)
				.horizontalAlignment(JUSTIFY);

		PdfText text = builder.build();
		drawTextLines(text);
		down(15);
	}

	private void drawEmployee() {

		final String dateFormat = text("DATE FORMAT");

		String[] variables = new String[] { "employeeName", "employeeNif", "employeeCategory", "employeeAntiquity" };
		String[] values	   = new String[] { safeString(settlement.getEmployeeName()),
				safeString(settlement.employeeNIF()),
				settlement != null && settlement.employeeCategory().isPresent() ? "con la categoría de " + settlement.employeeCategory().get() : "",
				safeString(formatDate(settlement.getEmployeeAntiquity(), dateFormat)) };

		final String employeeTxt = replaceVariables(variables, values, text("EMPLOYEE"));

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).width(480).height(0).x(x()).y(y()).color(primary).font(HELVETICA).fontSize(9f)
				.lineSpacing(3f).content(employeeTxt).verticalAlignment(VERTICAL_ALIGNMENT.CENTER)
				.horizontalAlignment(JUSTIFY);

		PdfText text = builder.build();
		drawTextLines(text);
		down(15);
	}

	private void drawDeclare() {
		final String declareTxt = text("DECLARE");

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).width(480).height(0).x(x()).y(y()).color(primary).font(HELVETICA).fontSize(9f)
				.lineSpacing(3f).content(declareTxt).horizontalAlignment(LEFT);

		PdfText text = builder.build();
		drawTextLines(text);
		down(20);
	}

	private void drawDeclaration() {

		final String dateFormat = text("DATE FORMAT");
		
		String[] variables = new String[] { "endDate", "endCause", "textTotal", "totalAmount" };
		String[] values	   = new String[] {
				safeString(formatDate(settlement.getEndDate(), dateFormat)),
				AonStringUtils.isBlank(safeString(settlement.endCause())) ? "" : "por el motivo de " + safeString(settlement.endCause()) + " y",
				convertDouble(settlement.total().orElse(0d)).toUpperCase(),
				toLatinNumber(settlement.total().orElse(null))
		};

		final String declarationTxt = replaceVariables(variables, values, text("DECLARATION"));

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).width(480).height(0).x(x()).y(y()).color(primary).font(HELVETICA).fontSize(9f)
				.lineSpacing(6f).content(declarationTxt).verticalAlignment(VERTICAL_ALIGNMENT.CENTER)
				.horizontalAlignment(JUSTIFY);
		
		PdfText text = builder.build();
		drawTextLines(text);
		down(10);
	}

	private void drawPayments() throws IOException, UnknownCraException {

		String titleTxt			 = text("ACCRUALS").toUpperCase();
		String totalsTxt		 = text("TOTALS").toUpperCase();
		String paymentTotalTitle = text("ACCRUAL TOTAL").toUpperCase();
		String paymentTotal		 = toLatinNumber(settlement.getAccrualTotal().orElse(0.00)) + " " + text("CURRENCY");

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).width(595).height(0).x(x()).y(y()).color(primary).font(HELVETICA_BOLD)
				.fontSize(fontsize + 2).content(titleTxt).verticalAlignment(VERTICAL_ALIGNMENT.CENTER)
				.horizontalAlignment(ALIGNMENT.LEFT);

		PdfText title = builder.build();
		title.draw();
		drawBox(contents, x() + 400, y() - 10, 80, 20, LIGHT_GRAY);

		builder.stream(contents).width(80).height(20).x(x() + 400).y(y() - 10).marginX(5).font(HELVETICA).fontSize(9f)
				.content(totalsTxt).verticalAlignment(VERTICAL_ALIGNMENT.CENTER).horizontalAlignment(ALIGNMENT.RIGHT);

		PdfText totals = builder.build();
		totals.draw();
		down(25);

		Map<Integer, ArrayList<PDFPayment>> payments = settlement.getAccruals();
		if (payments == null)
			payments = new HashMap<Integer, ArrayList<PDFPayment>>();

		payments.entrySet().stream().sorted(Map.Entry.<Integer, ArrayList<PDFPayment>>comparingByKey()).forEach(m ->
		{
			try
			{
				if (m.getValue() != null)
				{
					double localTotal = m.getValue().stream()
							.mapToDouble(payment -> (payment == null) ? 0 : safeDouble(payment.getAmount())).sum();

					if (localTotal != 0)
					{

						String paymentTxt	   = m.getKey() + ". " + getType(m.getKey(), lang);
						String paymentTotalTxt = toLatinNumber(localTotal) + " " + text("CURRENCY");

						drawText(contents, paymentTxt, x(), y(), BLACK, HELVETICA_BOLD, fontsize);
						drawTextRight(contents, new PDRectangle(x() + 292, y() - 5, 100, 10), paymentTotalTxt, BLACK,
								HELVETICA, 9f, 5, 5);
						drawBox(contents, x(), y() - 2, 392, .2f, BLACK);
						down(20);

						m.getValue().stream().forEach(n ->

						{
							if (n != null)
							{
								String entryValue = toLatinNumber(n.getAmount().orElse(null)) + " " + text("CURRENCY");
								String entryTxt	  = " por " + safeString(n.getDescription());
								new PdfText(x(), y(), 60, 15, contents, entryValue, BLACK, HELVETICA, 9f, RIGHT).draw();
								new PdfText(x() + 64, y(), 325, 15, contents, entryTxt, BLACK, HELVETICA, 9f, LEFT)
										.draw();

								down(10.5f);
							}
						});
					}
				}
			} catch (IOException | UnknownCraException ignored)
			{
			}
			down(5);
		});
		down(5);

		drawTextRight(contents, new PDRectangle(x() + 280, y(), 200, 25), paymentTotal, BLACK, HELVETICA, fontsize, 7,
				5);
		drawTextRight(contents, new PDRectangle(x() + 190, y(), 200, 25), paymentTotalTitle, BLACK, HELVETICA, fontsize,
				5, 5);
	}

	private void drawDeductions() throws IOException {

		String title			   = text("DEDUCTIONS");
		String deductionTotalTitle = text("DEDUCTION TOTAL");
		String deductionTotal	   = toLatinNumber(settlement.deductionTotal().orElse(null)) + " " + text("CURRENCY");
		String payrollTotalTitle   = text("TOTAL");
		String payrollTotal		   = toLatinNumber(settlement.total().orElse(null)) + " " + text("CURRENCY");

		drawText(contents, title, x(), y() - 5, BLACK, HELVETICA_BOLD, fontsize + 2);
		down(20);

		Map<Integer, ArrayList<PDFDeduction>> deductions = settlement.getDeductions();
		if (deductions == null)
			deductions = new HashMap<Integer, ArrayList<PDFDeduction>>();

		deductions.entrySet().stream().sorted(Map.Entry.<Integer, ArrayList<PDFDeduction>>comparingByKey()).forEach(m ->
		{
			try
			{
				if (m.getValue() != null)
				{
					double localTotal = m.getValue().stream()
							.mapToDouble(deduction -> (deduction == null) ? 0 : safeDouble(deduction.getAmount()))
							.sum();

					String deductionTxt		 = "- " +/*m.getKey() + ". " + */DeductionTypes.getType(m.getKey());
					String deductionTotalTxt = toLatinNumber(localTotal) + " " + text("CURRENCY");

					drawText(contents, deductionTxt, x() + 3, y(), BLACK, HELVETICA_BOLD, fontsize);
					drawTextRight(contents, new PDRectangle(x() + 292, y() - 5, 100, 10), deductionTotalTxt, BLACK,
							HELVETICA, 9f, 2, 5);
					drawBox(contents, x(), y() - 2, 392, .2f, BLACK);

					down(20);

					if (localTotal != 0)
						m.getValue().stream()
						.sorted((d1,d2) -> d1.getDeductionType().orElse(DeductionType.OTHER).compareTo(d2.getDeductionType().orElse(DeductionType.OTHER) ))
						.forEach(n ->
						{
							if (n != null)
							{
								String entryValue	= toLatinNumber(n.getAmount().orElse(null)) + " "
										+ text("CURRENCY");
								String entryTxt		= " por " + n.getDescription().orElse("");
								String entryPercent	= (n.getPercent().isEmpty()) ? ""
										: toLatinNumber(n.getPercent().get()) + " % ";

								if (n.getAmount().isPresent() && n.getAmount().get() != 0)
								{
									PdfText quantity = new PdfText(x(), y(), 60, 15, contents, n.getPercent().isEmpty() ? entryValue : entryPercent, BLACK,
											HELVETICA, 9f, RIGHT);
									quantity.draw();

									new PdfText(x() + 64, y(), 210, 15, contents, entryTxt, BLACK, HELVETICA, 9f, LEFT)
											.draw();
									new PdfText(x() + 64 + 210, y(), 60, 15, contents, n.getPercent().isEmpty() ? "" : entryValue, BLACK, HELVETICA, 9f,
											RIGHT).draw();

									down(10);
								}
							}
						});
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			down(5);
		});
		down(15);

		drawTextRight(contents, new PDRectangle(x() + 278, y(), 200, 25), deductionTotal, BLACK, HELVETICA, fontsize, 5,
				5);
		drawTextRight(contents, new PDRectangle(x() + 190, y(), 200, 25), deductionTotalTitle, BLACK, HELVETICA,
				fontsize, 5, 5);

		down(25);

		PdfBox b = new PdfBox(x() + 400, y() - 3, 80, 22, LIGHT_GRAY, contents);
		b.draw();

		drawTextRight(contents, new PDRectangle(x() + 278, y(), 200, 25), payrollTotal, BLACK, HELVETICA_BOLD, fontsize,
				5, 5);
		drawTextRight(contents, new PDRectangle(x() + 190, y(), 200, 25), payrollTotalTitle, BLACK, HELVETICA_BOLD,
				fontsize, 5, 5);

		down(15);
	}

	private void drawLegalText() throws IOException {
		down(10);
		String legalTxt				 = text("LEGAL DATA");
		String legalAdviceTxt		 = text("LEGAL ADVICE");
		String employeeSignTxt		 = text("EMPLOYEE SIGN");
		String enterpriseSignTxt	 = text("ENTERPRISE SIGN");
		String representativeSignTxt = text("REPRESENTATIVE SIGN");

		String[] variables = { "location" };
		String[] values	   = {"A "};
		if (settlement.location().isPresent()) {
			values[0]	   = "En " + settlement.location().get() + " a ";
		}
//		String[] values	   = { safeString(settlement.location()) };
		String	 dateTxt   = formatDate(new Date(), replaceVariables(variables, values, text("DATE"))).orElse("");

		PdfTextBuilder builder = new PdfTextBuilder();
		builder.stream(contents).x(x()).width(480).y(y()).font(HELVETICA).fontSize(9f).content(legalTxt).lineSpacing(6f)
				.horizontalAlignment(ALIGNMENT.JUSTIFY);

		PdfText legalText = builder.horizontalAlignment(ALIGNMENT.JUSTIFY).build();
		drawTextLines(legalText);
		down(15);

		builder.stream(contents).y(y()).horizontalAlignment(ALIGNMENT.LEFT).content(legalAdviceTxt);
		PdfText legalAdviceText = builder.build();
		drawTextLines(legalAdviceText);
		down(15);

		builder.stream(contents).y(150).content(dateTxt);
		PdfText dateText = builder.build();
		drawTextLinesFree(dateText);

		float margin = 100;
		float width	 = 470 / 2 - margin / 2;

		if (settlement.existRepresentative().orElse(false))
		{
			margin = 12;
			width  = 470 / 3 - margin / 2;
		}

		builder.stream(contents).width(width).height(40).y(40).content(enterpriseSignTxt).color(GRAY)
				.horizontalAlignment(ALIGNMENT.CENTER);

		PdfImage signImg = new PdfImage(x(), 80, width, 50, ALIGNMENT.CENTER, contents, doc, signature);
		try {
			signImg.draw();
		} catch (Exception e) {
			// No se dibuja
		}
	
		
		PdfText enterpriseSign = builder.build();
		drawTextLinesFree(enterpriseSign);

		right(width + margin);
		builder.stream(contents).width(width).x(x()).content(employeeSignTxt);

		PdfText  employeeSign = builder.build();
		drawTextLinesFree(employeeSign);

		right(width + margin);
		builder.stream(contents).width(width).x(x()).content(representativeSignTxt);

		PdfText representativeSign = builder.build();
		drawTextLinesFree(representativeSign);

	}

	private static String replaceVariables(String[] words, String[] values, String text) {
		for (int i = 0; i < words.length; i++)
			text = text.replace("$" + words[i], values[i]);
		return text;
	}

}
