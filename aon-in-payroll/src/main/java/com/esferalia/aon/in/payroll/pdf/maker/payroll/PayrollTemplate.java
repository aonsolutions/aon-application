package com.esferalia.aon.in.payroll.pdf.maker.payroll;

import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfFormats.formatDate;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfFormats.toLatinNumber;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfSettings.ALIGNMENT.CENTER;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfSettings.ALIGNMENT.LEFT;
import static com.esferalia.aon.in.payroll.pdf.api.settings.PdfSettings.ALIGNMENT.RIGHT;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.OptionalToolkit.safeDouble;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.OptionalToolkit.safeInteger;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.OptionalToolkit.safeString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.createVerticalPage;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.croppedString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawBorderedBox;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawBox;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawCostPercentage;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawText;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextCenter;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextRight;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.CraTypes.getType;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.DeductionTypes.getType;
import static java.util.ResourceBundle.getBundle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfBox;
import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfImage;
import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.api.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.settings.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exceptions.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.Accrual;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.ContingencyBases;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.Deduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.DefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.PayrollTypes;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.beans.UnknownCraException;

public class PayrollTemplate {

	static float fontSize = 9f;

	String				filename = "./payroll.pdf";
	float				limit;
	PDPageContentStream	contents;

	PDDocument			  doc;
	DefaultPayroll		  p;
	Optional<InputStream> logo;
	Locale				  lang;

	float		   x;
	float		   y;
	ResourceBundle words;

	// PRINT THE PDF
	public static void print(
			OutputStream os, DefaultPayroll payroll, Optional<InputStream> logo, Optional<Locale> language
	) throws CanNotCreatePdfException {
		try (PDDocument doc = print(payroll, logo, language))
		{
			doc.save(os);
		} catch (Exception e)
		{
			throw new CanNotCreatePdfException(e);
		}
	}

	// PRINT THE PDF FROM COLLECTION
	public static void print(
			OutputStream os, Collection<DefaultPayroll> payrolls, Optional<InputStream> logo, Optional<Locale> language
	) throws CanNotCreatePdfException, IOException {
		try (PDDocument doc = print(payrolls, logo, language))
		{
			doc.save(os);
		} catch (Exception e)
		{
			throw new CanNotCreatePdfException(e);
		}
	}

	// PRINT THE PDF FROM COLLECTION
	private static PDDocument print(
			Collection<DefaultPayroll> payrolls, Optional<InputStream> logo, Optional<Locale> language
	) throws CanNotCreatePdfException, IOException {

		PDDocument		doc		 = new PDDocument();
		PayrollTemplate	template = new PayrollTemplate();

		template.lang  = language.orElse(new Locale("Es"));
		template.words = getBundle("com.esferalia.aon.in.payroll.pdf.maker.payroll.bundles.PayrollBundle",
				template.lang);
		template.limit = 800;

		byte[] bLogo = null;
		if (logo.isPresent())
			bLogo = logo.get().readAllBytes();

		for (DefaultPayroll payroll : payrolls)
		{
			if (bLogo != null)
				logo = Optional.ofNullable(new ByteArrayInputStream(bLogo));

			template.p = payroll;
			if (payroll == null)
				throw new CanNotCreatePdfException("No payroll found.");

			PDPage page = PDFToolkit.createVerticalPage();
			doc.addPage(page);

			template.contents = new PDPageContentStream(doc, page);

			template.drawHeader();
			boolean jump = template.calculate();

			template.doc  = doc;
			template.logo = logo;

			if (jump)
			{
				template.drawAccruals();
				drawBorderedBox(template.contents, 10, 10, 575, 695, LIGHT_GRAY);
				template.contents.close();

				page = createVerticalPage();
				doc.addPage(page);
				template.contents = new PDPageContentStream(doc, page);

				template.drawHeader();
				drawBorderedBox(template.contents, 10, 175, 575, 532, LIGHT_GRAY);
				template.y -= 15;

				template.drawDeductions();
				template.drawFooter();
			} else
			{
				drawBorderedBox(template.contents, 10, 175, 575, 532, LIGHT_GRAY);
				template.drawAccruals();
				template.drawDeductions();
				template.drawFooter();
			}
			template.contents.close();
		}
		return doc;
	}

	private static PDDocument print(DefaultPayroll payroll, Optional<InputStream> logo, Optional<Locale> language)
			throws CanNotCreatePdfException, IOException {

		PDDocument		doc		 = new PDDocument();
		PayrollTemplate	template = new PayrollTemplate();

		template.lang  = language.orElse(new Locale("Es"));
		template.words = getBundle("com.esferalia.aon.in.payroll.pdf.maker.payroll.bundles.PayrollBundle",
				template.lang);

		template.limit = 800;
		template.p	   = payroll;

		if (payroll == null)
			throw new CanNotCreatePdfException("No payroll found.");

		PDPage page = createVerticalPage();
		doc.addPage(page);
		template.contents = new PDPageContentStream(doc, page);

		template.drawHeader();
		boolean jump = template.calculate();

		template.doc  = doc;
		template.logo = logo;

		if (jump)
		{
			template.drawAccruals();
			drawBorderedBox(template.contents, 10, 10, 575, 695, PdfColors.LIGHT_GRAY);
			template.contents.close();

			page = createVerticalPage();
			doc.addPage(page);
			template.contents = new PDPageContentStream(doc, page);

			template.drawHeader();
			drawBorderedBox(template.contents, 10, 175, 575, 532, PdfColors.LIGHT_GRAY);
			template.y -= 15;

			template.drawDeductions();
			template.drawFooter();

		} else
		{
			drawBorderedBox(template.contents, 10, 175, 575, 532, PdfColors.LIGHT_GRAY);
			template.drawAccruals();
			template.drawDeductions();
			template.drawFooter();
		}

		template.contents.close();
		return doc;
	}

	// DRAW THE HEADER
	private void drawHeader() throws IOException {

		x = 20;
		y = 795;
		String dateFormat  = text("FORMATO FECHA");
		String dateLFormat = "dd 'de' MMMM 'de' yyyy";
		String replace	   = PayrollTypes.toString(p.getPayrollType().orElse(PayrollTypes.Type.SALARY), lang);

		// HEADER CONTENT
		String title	  = text("TITULO").replace("*", replace).toUpperCase();
		String enterprise = safeString(p.getEnterprise());
		String employee	  = safeString(p.getEmployee());
		String address	  = safeString(p.getAddress());

		String nif		   = text("NIF") + ": " + safeString(p.getNif());
		String nss		   = text("NSS") + ": " + safeString(p.getNss());
		String profesGroup = text("G.PROFESIONAL") + ": " + safeString(p.getProfessionalGroup());
		String ccc		   = text("CCC") + ": " + safeString(p.getCcc());
		String cif		   = text("CIF") + ": " + safeString(p.getCif());
		String cotizGroup  = text("G.COTIZ") + ": " + safeString(p.getQuotationGroup());
		String antiquDate  = text("FECHA ANTIGUEDAD") + ": "
				+ safeString(formatDate(p.getAntiquity().get(), dateFormat));
		String dayTotal	   = text("TOTAL DIAS") + ": " + safeInteger(p.getTotalDays());

		String liquidPeriod = text("PERIODO LIQUIDACION") + ": del "
				+ formatDate(p.getLiquidPeriodStart(), dateLFormat).get() + " a "
				+ formatDate(p.getLiquidPeriodEnd(), dateLFormat).get();

		// BUILDING THE HEADER
		float headerFontSize = fontSize;

		enterprise = croppedString(enterprise, 255, HELVETICA_BOLD, fontSize);
		employee   = croppedString(employee, 255, HELVETICA_BOLD, fontSize);

		drawBorderedBox(contents, x - 10, y - 85, 575, 120, LIGHT_GRAY);
		drawTextCenter(contents, new PDRectangle(x, y + 4, 530, 100), title, BLACK, HELVETICA_BOLD, 12, 12);

		y -= 60;
		drawBox(contents, x, y, 275, 65, LIGHT_GRAY);
		drawBox(contents, x + 282, y, 275, 65, LIGHT_GRAY);

		y += 50;
		x += 10;
		drawText(contents, enterprise, x, y, BLACK, HELVETICA_BOLD, headerFontSize);
		drawText(contents, employee, x + 280, y, BLACK, HELVETICA_BOLD, headerFontSize);

		y -= 13.5;
		drawText(contents, address, x, y, BLACK, HELVETICA, headerFontSize);
		drawText(contents, nif, x + 280, y, BLACK, HELVETICA, headerFontSize);

		drawText(contents, nss, x + 380, y, BLACK, HELVETICA, headerFontSize);

		y -= 13.5;
		drawText(contents, safeString(p.getAddress_2()), x, y, BLACK, HELVETICA, headerFontSize);
		drawText(contents, profesGroup, x + 280, y, BLACK, HELVETICA, headerFontSize);

		y -= 13.5;
		drawText(contents, ccc, x, y, BLACK, HELVETICA, headerFontSize);
		drawText(contents, cotizGroup, x + 280, y, BLACK, HELVETICA, headerFontSize);

		x += 100;
		drawText(contents, cif, x + 30, y, BLACK, HELVETICA, headerFontSize);
		drawText(contents, antiquDate, x + 280, y, BLACK, HELVETICA, headerFontSize);

		y -= 25;
		x -= 100;
		drawText(contents, liquidPeriod, x - 8, y, BLACK, HELVETICA, fontSize);
		new PdfText(x + 280, y, 265, 12, contents, dayTotal, BLACK, HELVETICA, fontSize, RIGHT).draw();

	}

	// CHECK IF JUMPS
	private boolean calculate() throws IOException {

		Optional<Map<Integer, ArrayList<Accrual>>>	 accruals	= p.getAccruals();
		Optional<Map<Integer, ArrayList<Deduction>>> deductions	= p.getDeductions();

		double sum = 0;

		sum	+= 20;
		sum	+= accruals.get().entrySet().size() * 12;
		sum	+= accruals.get().entrySet().stream().mapToDouble(entry -> entry.getValue().stream().count()).sum() * 12;

		sum	+= 20;
		sum	+= deductions.get().entrySet().size() * 12;
		sum	+= deductions.get().entrySet().stream().mapToDouble(entry -> entry.getValue().stream().count()).sum() * 12;

		return sum > 370;
	}

	// DRAW ACCRUALS
	private void drawAccruals() throws IOException {
		x = 25;
		y = 675;

		String title			 = text("DEVENGOS").toUpperCase();
		String totals			 = text("TOTALES").toUpperCase();
		String accrualTotalTitle = "A." + text("TOTAL DEVENGADO").toUpperCase() + ":";
		String accrualTotal		 = toLatinNumber(p.getAccrualTotal().orElse(0.00)) + " " + text("MONEDA");

		drawText(contents, title, x, y, BLACK, HELVETICA_BOLD, fontSize + 3);
		drawBox(contents, x + 470, y - 7, 80, 20, LIGHT_GRAY);
		drawTextRight(contents, new PDRectangle(x + 450, y - 7, 100, 20), totals, BLACK, HELVETICA, fontSize, 7, 7);

		y -= 20;

		Optional<Map<Integer, ArrayList<Accrual>>> accruals = p.getAccruals();
		accruals.get().entrySet().stream().sorted(Map.Entry.<Integer, ArrayList<Accrual>>comparingByKey()).forEach(m ->
		{
			try
			{
				double localTotal = m.getValue().stream().mapToDouble(accrual -> safeDouble(accrual.getAmount())).sum();
				if (localTotal != 0)
				{
					String accrualTxt	   = m.getKey() + ". " + getType(m.getKey(), lang);
					String accrualTotalTxt = toLatinNumber(localTotal) + " " + text("MONEDA");

					drawText(contents, accrualTxt, x, y, BLACK, HELVETICA_BOLD, fontSize);
					drawTextRight(contents, new PDRectangle(x + 355, y - 5, 100, 10), accrualTotalTxt, BLACK, HELVETICA,
							fontSize, 5, 5);
					drawBox(contents, x, y - 2, 455, .2f, BLACK);
					y -= 15;

					m.getValue().stream().forEach(n ->
					{
						String entryValue = toLatinNumber(n.getAmount().orElse(null)) + " " + text("MONEDA");
						String entryTxt	  = " por " + safeString(n.getDescription());

						PdfText text = new PdfText(x, y, 60, 15, contents, entryValue, BLACK, HELVETICA, fontSize,
								RIGHT);
						text.draw();

						PdfText t2 = new PdfText(x + 64, y, 300, 15, contents, entryTxt, BLACK, HELVETICA, fontSize,
								LEFT);
						t2.draw();

						y -= 10.5f;
					});
				}
			} catch (IOException | UnknownCraException e)
			{
				e.printStackTrace();
			}
			y -= 5;
		});
		y -= 5;

		drawTextRight(contents, new PDRectangle(x + 350, y, 200, 25), accrualTotal, BLACK, HELVETICA, fontSize, 7, 5);
		drawTextRight(contents, new PDRectangle(x + 265, y, 200, 25), accrualTotalTitle, BLACK, HELVETICA, fontSize, 5,
				5);
	}

	// DRAW DEDUCTIONS
	private void drawDeductions() throws IOException {

		// DEDUCTIONS CONTENT
		String title			   = text("DEDUCCIONES").toUpperCase();
		String deductionTotalTitle = "B. " + text("TOTAL DEDUCIR").toUpperCase() + ": ";
		String deductionTotal	   = toLatinNumber(safeDouble(p.getDeductionTotal())) + " " + text("MONEDA");
		String payrollTotalTitle   = text("TOTAL PERCIBIR").toUpperCase() + " (A-B): ";
		String payrollTotal		   = toLatinNumber(safeDouble(p.getPayrollTotal())) + " " + text("MONEDA");
		String enterpriseSign	   = text("FIRMA EMPRESA").toUpperCase();
		String employeeSign		   = formatDate(new Date(), text("FIRMA TRABAJADOR")).get();

		x = 23.5f;

		// BUILD DEDUCTIONS
		Optional<Map<Integer, ArrayList<Deduction>>> deductions = p.getDeductions();
		drawText(contents, title, x, y - 20, BLACK, HELVETICA_BOLD, fontSize + 3);
		y -= 40;

		// FOR EACH DEDUCTION
		deductions.get().entrySet().stream().sorted(Map.Entry.<Integer, ArrayList<Deduction>>comparingByKey())
				.forEach(m ->
				{
					try
					{

						double localTotal = m.getValue().stream().mapToDouble(accrual -> safeDouble(accrual.getAmount()))
								.sum();
						String deductionTxt = m.getKey() + ". " + getType(m.getKey());
						String deductionTotalTxt = toLatinNumber(localTotal) + " " + text("MONEDA");

						drawText(contents, deductionTxt, x, y, BLACK, HELVETICA_BOLD, fontSize);
						drawTextRight(contents, new PDRectangle(x + 355, y - 5, 100, 10), deductionTotalTxt, BLACK,
								HELVETICA, fontSize, 2, 5);
						drawBox(contents, x, y - 2, 455, .2f, BLACK);

						y -= 15;

						if (localTotal != 0)
							m.getValue().stream().forEach(n ->
							{
								String entryValue = toLatinNumber(n.getAmount().orElse(null)) + " " + text("MONEDA");
								String entryTxt = " por " + n.getDescription().orElse("");
								String entryPercent = (n.getPercent().isEmpty()) ? ""
										: toLatinNumber(n.getPercent().get()) + " % ";

								if (n.getAmount().isPresent() && n.getAmount().get() != 0)
								{
									PdfText quantity = new PdfText(x, y, 60, 15, contents, entryPercent, BLACK,
											HELVETICA, fontSize, RIGHT);
									quantity.draw();

									PdfText t2 = new PdfText(x + 64, y, 270, 15, contents, entryTxt, BLACK, HELVETICA,
											fontSize, LEFT);
									t2.draw();

									PdfText t3 = new PdfText(x + 64 + 270, y, 60, 15, contents, entryValue, BLACK,
											HELVETICA, fontSize, RIGHT);
									t3.draw();

									y -= 10;
								}
							});
					} catch (IOException e)
					{
						e.printStackTrace();
					}
					y -= 5;
				});
		y -= 15;

		drawTextRight(contents, new PDRectangle(x + 350, y, 200, 25), deductionTotal, BLACK, HELVETICA, fontSize, 5, 5);
		drawTextRight(contents, new PDRectangle(x + 265, y, 200, 25), deductionTotalTitle, BLACK, HELVETICA, fontSize,
				5, 5);

		PdfText ent = new PdfText(x, y - 2, 150, 25, contents, enterpriseSign, BLACK, HELVETICA, fontSize - 2, CENTER);
		ent.draw();
		y -= 10;

		if (logo.isPresent())
		{
			byte[]	 bytes = logo.get().readAllBytes();
			PdfImage img   = new PdfImage(x + 20, y - 60, 170, 70, ALIGNMENT.CENTER, contents, doc, bytes);
			img.scale(170, 70, LEFT).draw();
		}
		y -= 15;

		PdfBox b = new PdfBox(495, y - 3, 80, 22, LIGHT_GRAY, contents);
		b.draw();

		drawTextRight(contents, new PDRectangle(x + 350, y, 200, 25), payrollTotal, BLACK, HELVETICA_BOLD, fontSize, 5,
				5);
		drawTextRight(contents, new PDRectangle(x + 265, y, 200, 25), payrollTotalTitle, BLACK, HELVETICA_BOLD,
				fontSize, 5, 5);

		y -= 20;
		drawTextRight(contents, new PDRectangle(x + 455, y, 100, 25), employeeSign, BLACK, HELVETICA, fontSize - 2, 5,
				5);
	}

	// DRAW FOOTER
	public void drawFooter() throws IOException {

		Optional<ContingencyBases> contigencies = p.getContingencies();
		if (contigencies.isPresent())
		{
			y = 155;
			x = 25;

			final String title	= text("TITULO PIE");
			final String title2	= text("TITULO PIE 2");
			final String apEnt	= text("AP EMPRESA").toUpperCase();
			final String type	= text("TIPO").toUpperCase();
			final String base	= text("BASE").toUpperCase();

			final String commonContingenciesTitle = "1. " + text("CONTINGENCIAS COMUNES");
			final String monthlyAmmountTitle	  = text("IMPORTE DE REMUNERACION MENSUAL");
			final String extraHourProrrationTitle = text("IMPORTE PRORRATA DE PAGA EXTRAORDINARIA");

			final String profContingenciesTitle	= "2. "
					+ text("CONTINGENCIAS PROFESIONALES Y CONCEPTOS DE RECAUDACION CONJUNTA");
			final String atEpTitle				= text("AT Y EP");
			final String unemploymentTitle		= text("DESEMPLEO");
			final String profesFormTitle		= text("FORMACION PROFESIONAL");
			final String fogasaTitle			= text("FONDO DE GARANTIA SALARIAL");

			final String		   extraHoursTitle		   = "3. " + text("COTIZACION ADICIONAL POR HORAS EXTRAS");
			final String		   forceMajeureTitle	   = text("FUERZA MAYOR O");
			final String		   noStructTitle		   = text("NO ESTRUCTURALES");
			String				   irpfTitle			   = "4. " + text("BASE SUJETA A RETENCION IRPF") + " ";
			final String		   totalContingenciesTitle = text("TOTAL APORTACIONES");
			final ContingencyBases conts				   = contigencies.get();

			final String monthlyAmmount	= toLatinNumber(safeDouble(conts.getMonthlyAmount())) + " " + text("MONEDA");
			final String commContBase	= toLatinNumber(safeDouble(conts.getCommonContBase())) + " " + text("MONEDA");
			String		 commContType	= drawCostPercentage(conts.getCommonContType());

			if (commContType.contains("-1"))
				commContType = "";

			final String commContApEnt		   = toLatinNumber(safeDouble(conts.getCommonContApEnterprise())) + " "	+ text("MONEDA");
			final String extraProrrationAmount = toLatinNumber(safeDouble(conts.getExtraProrationAmount())) + " " + text("MONEDA");
			final String profContingenciesBase = toLatinNumber(safeDouble(conts.getProfessionalContBase())) + " " + text("MONEDA");
			String		 atEpType			   = drawCostPercentage(conts.getAtEpType());

			if (atEpType.contains("-1"))
				atEpType = "";

			final String atEpApEnt		  = toLatinNumber(safeDouble(conts.getAtEpApEnterprise())) + " " + text("MONEDA");
			String		 unemploymentType = drawCostPercentage(conts.getUnemploymentType());

			if (unemploymentType.contains("-1"))
				unemploymentType = "";

			final String unemploymentApEnt = toLatinNumber(safeDouble(conts.getUnemploymentApEnterprise())) + " " + text("MONEDA");
			final String profesFormType	   = toLatinNumber(safeDouble(conts.getProfesFormType())) + " %";
			final String profesFormApEnt   = toLatinNumber(safeDouble(conts.getProfesFormApEnterprise())) + " " + text("MONEDA");

			String fogasaType = drawCostPercentage(conts.getFogasaType());
			if (fogasaType.contains("-1"))
				fogasaType = "";

			String fogasaApEnt		  = toLatinNumber(safeDouble(conts.getFogasaApEnterprise())) + " " + text("MONEDA");
			String forceMajeureBase   = toLatinNumber(safeDouble(conts.getForceMajeureBase())) + " " + text("MONEDA");
			
			String forceMajeureType = drawCostPercentage(conts.getForceMajeureType());
			if (forceMajeureType.contains("-1"))
				forceMajeureType = "";

			String forceMajeureApEnt	= toLatinNumber(safeDouble(conts.getForceMajeureApEnterprise())) + " "	+ text("MONEDA");
			String noStructBase			= toLatinNumber(safeDouble(conts.getNoStructBase())) + " " + text("MONEDA");
			
			String noStructType			= drawCostPercentage(conts.getNoStructType());
			if (noStructType.contains("-1"))
				noStructType = "";

			String noStructApEnt			  = toLatinNumber(safeDouble(conts.getNoStructApEnterprise())) + " " + text("MONEDA");
			String totalIrpf				  = toLatinNumber(safeDouble(conts.getIrpfEsp()) + conts.getIrpfRetribDiner().orElse(0.00)) + " " + text("MONEDA");
			String totalContingenciesAmount   = toLatinNumber(safeDouble(conts.getTotal())) + " " + text("MONEDA");

			Double irpfEsp		= safeDouble(conts.getIrpfEsp());
			Double irpfRetDin	= safeDouble(conts.getIrpfRetribDiner());

			if (irpfEsp != 0)
				irpfTitle += toLatinNumber(irpfEsp) + " " + text("MONEDA") + " " + text("EN ESPECIE") + " ";
			if (irpfRetDin != 0)
				irpfTitle += toLatinNumber(irpfRetDin) + " " + text("MONEDA") + " "	+ text("EN RETRIBUCIONES DINERARIAS");

			drawBorderedBox(contents, 10, 10, 575, 160, LIGHT_GRAY);
			drawText(contents, title, x, y, BLACK, HELVETICA_BOLD, fontSize - 1);

			y -= 10;
			drawText(contents, title2, x, y, BLACK, HELVETICA_BOLD, fontSize - 1);

			y -= 18;
			drawBox(contents, x + 480, y, 70, 20, LIGHT_GRAY);
			drawTextRight(contents, new PDRectangle(x + 480, y, 70, 20), apEnt, BLACK, HELVETICA, fontSize - 1, 5, 7);

			drawBox(contents, x + 408, y, 70, 20, LIGHT_GRAY);
			drawTextRight(contents, new PDRectangle(x + 408, y, 70, 20), type, BLACK, HELVETICA, fontSize - 1, 5, 7);

			drawBox(contents, x + 336, y, 70, 20, LIGHT_GRAY);
			drawTextRight(contents, new PDRectangle(x + 336, y, 70, 20), base, BLACK, HELVETICA, fontSize - 1, 5, 7);

			drawText(contents, commonContingenciesTitle, x, y, BLACK, HELVETICA_BOLD, fontSize - 1);

			y -= 9;
			x += 15;
			drawText(contents, monthlyAmmountTitle, x, y, BLACK, HELVETICA, fontSize - 3);
			drawTextRight(contents, new PDRectangle(x + 215, y, 70, 70), monthlyAmmount, BLACK, HELVETICA, fontSize - 3, 5, 1);

			drawTextRight(contents, new PDRectangle(x + 322, y - 5, 70, 70), commContBase, BLACK, HELVETICA,  fontSize - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), commContType, BLACK, HELVETICA,  fontSize - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), commContApEnt, BLACK, HELVETICA, fontSize - 3, 5, 1);

			y -= 8;
			drawText(contents, extraHourProrrationTitle, x, y, BLACK, HELVETICA, fontSize - 3);
			drawTextRight(contents, new PDRectangle(x + 215, y, 70, 70), extraProrrationAmount, BLACK, HELVETICA,fontSize - 3, 5, 1);

			y -= 12;
			drawText(contents, profContingenciesTitle, x - 15, y, BLACK, HELVETICA_BOLD, 7);

			y -= 10;
			drawText(contents, atEpTitle, x, y, BLACK, HELVETICA, fontSize - 3);
			drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), atEpType, BLACK, HELVETICA, fontSize - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), atEpApEnt, BLACK, HELVETICA, fontSize - 3, 5, 1);

			y -= 8;
			drawText(contents, unemploymentTitle, x, y, BLACK, HELVETICA, fontSize - 3);
			drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), unemploymentType, BLACK, HELVETICA, fontSize - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), unemploymentApEnt, BLACK, HELVETICA, fontSize - 3, 5, 1);

			y -= 8;
			drawTextRight(contents, new PDRectangle(x + 322, y, 70, 70), profContingenciesBase, BLACK, HELVETICA, fontSize - 3, 5, 1);
			drawText(contents, profesFormTitle, x, y, BLACK, HELVETICA, fontSize - 3);
			drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), profesFormType, BLACK, HELVETICA, fontSize - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), profesFormApEnt, BLACK, HELVETICA, fontSize - 3, 5, 1);

			y -= 8;
			drawText(contents, fogasaTitle, x, y, BLACK, HELVETICA, fontSize - 3);
			drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), fogasaType, BLACK, HELVETICA, fontSize - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), fogasaApEnt, BLACK, HELVETICA, fontSize - 3, 5, 1);

			y -= 12;
			drawText(contents, extraHoursTitle, x - 15, y, BLACK, HELVETICA_BOLD, fontSize - 2);

			y -= 10;
			drawText(contents, forceMajeureTitle, x, y, BLACK, HELVETICA, fontSize - 3);
			drawTextRight(contents, new PDRectangle(x + 322, y - 2, 70, 70), forceMajeureBase, BLACK, HELVETICA, fontSize - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 396, y - 2, 70, 70), forceMajeureType, BLACK, HELVETICA, fontSize - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 2, 70, 70), forceMajeureApEnt, BLACK, HELVETICA, fontSize - 3, 5, 1);

			y -= 8;
			drawText(contents, noStructTitle, x, y, BLACK, HELVETICA, fontSize - 3);
			drawTextRight(contents, new PDRectangle(x + 322, y - 2, 70, 70), noStructBase, BLACK, HELVETICA, fontSize - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 396, y - 2, 70, 70), noStructType, BLACK, HELVETICA, fontSize - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 2, 70, 70), noStructApEnt, BLACK, HELVETICA, fontSize - 3, 5, 1);

			y -= 12;
			x -= 15;
			drawText(contents, irpfTitle, x, y, BLACK, HELVETICA_BOLD, fontSize - 3);
			drawBox(contents, x, y - 2, 405, .2f, BLACK);
			drawTextRight(contents, new PDRectangle(x + 308, y - 5, 100, 10), totalIrpf, BLACK, HELVETICA, fontSize - 3, 5, 5);
			drawTextRight(contents, new PDRectangle(x + 451, y - 5, 30, 10), totalContingenciesTitle, BLACK, HELVETICA_BOLD, 6.5f, 5, 5);
			drawTextRight(contents, new PDRectangle(x + 518, y - 5, 30, 10), totalContingenciesAmount, BLACK, HELVETICA_BOLD, 6.5f, 3, 5);

			String vs = "v0.39-AK";

			PdfText version = new PdfText(5f, 1f, 200f, 10f, contents, vs, BLACK, HELVETICA, 5f, LEFT);
			version.draw();

		}

	}

	public String text(String name) {
		return words.getString(name);
	}

}
