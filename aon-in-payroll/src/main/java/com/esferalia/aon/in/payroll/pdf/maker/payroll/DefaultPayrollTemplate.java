package com.esferalia.aon.in.payroll.pdf.maker.payroll;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.formatDate;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.toLatinNumber;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.LEFT;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.RIGHT;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.DataToolkit.safeDouble;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.DataToolkit.safeInteger;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.DataToolkit.safeString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.createVerticalPage;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.croppedString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawBorderedBox;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawBox;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawCostPercentage;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawText;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextCenter;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.drawTextRight;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.CraTypes.getType;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getComplementosSalariales;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getExtraGratificationPayment;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getExtraHoursPayment;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getIndemns;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getInfos;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getNotes;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getOtherDeduction;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getOtherDeductions;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getOtherPayments;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getPPEs;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getPrestSS;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getSalaries;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getSingleDeductionByType;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getWarnings;
import static java.util.ResourceBundle.getBundle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfImage;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.ContingencyBases;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.IMPRESION;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.IDefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PayrollTypes;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.UnknownCraException;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * Class to print Payroll PDF file with PDFbox
 * 
 * @author akrck02
 * @version 0.5-AK
 */
public class DefaultPayrollTemplate implements IPayrollTemplate {

	private final static float FONT_SIZE = 8f;
	private final static float LITTLE_LINE_JUMP = 11.5f;
	private final static float NORMAL_LINE_JUMP = 13f;

	String				filename = "./payroll.pdf";
	float				limit;
	PDPageContentStream	contents;

	PDDocument			  doc;
	IDefaultPayroll		  p;
	Optional<InputStream> logo;
	Locale				  lang;

	float		   x;
	float		   y;
	ResourceBundle words;

	/**
	 * Print the PDF file from a collection
	 * 
	 * @param os
	 * @param payrolld
	 * @param logo
	 * @param language
	 * @throws CanNotCreatePdfException
	 * @throws IOException
	 */
	@Override
	public void print(OutputStream os) throws CanNotCreatePdfException {
		try {
			doc.save(os);
			os.close();
		} catch (Exception e)
		{
			throw new CanNotCreatePdfException(e);
		}
	}
	
	public DefaultPayrollTemplate(IDefaultPayroll payroll, Optional<InputStream> logo, Optional<Locale> language) throws CanNotCreatePdfException {
		this(Arrays.asList(payroll), logo, language);
	}
	
	public DefaultPayrollTemplate(Collection<IDefaultPayroll> payrolls, Optional<InputStream> logo, Optional<Locale> language) throws CanNotCreatePdfException {
		try {
			this.doc		 = new PDDocument();

			this.lang  = language.orElse(new Locale("Es"));
			this.words = getBundle("com.esferalia.aon.in.payroll.pdf.maker.payroll.bundle.ClassicPayrollBundle",
					this.lang);
			this.limit = 800;

			byte[] bLogo = null;
			if (logo.isPresent())
				bLogo = logo.get().readAllBytes();
			
			List<IDefaultPayroll> orderedPayrolls = new LinkedList<>();
			if (payrolls != null) {
				payrolls.stream().sorted((o1, o2) -> {
					String name1 = o1.getEmployee().orElse(null);
					String name2 = o2.getEmployee().orElse(null);
					Date date1 = o1.getLiquidPeriodEnd().orElse(null);
					Date date2 = o2.getLiquidPeriodEnd().orElse(null);
					int strCompare = AonStringUtils.compare(name1, name2);
					if (strCompare != 0) {
						return strCompare;
					} else {
						return com.esferalia.aon.watson.util.AonDateUtils.compare(date1, date2);
					}
				}).forEach(orderedPayrolls::add);
			}

			for (IDefaultPayroll payroll : orderedPayrolls) {
				if (bLogo != null)
					logo = Optional.ofNullable(new ByteArrayInputStream(bLogo));

				this.p = payroll;
				if (payroll == null)
					throw new CanNotCreatePdfException("No payroll found.");

				PDPage page = createVerticalPage();
				doc.addPage(page);

				this.contents = new PDPageContentStream(doc, page);

				this.drawHeader();
				boolean jump = this.calculate();

				this.logo = logo;

				if (jump) {
					this.drawPayments();
					drawBorderedBox(this.contents, 10, 10, 575, 695, LIGHT_GRAY);
					this.contents.close();

					page = createVerticalPage();
					doc.addPage(page);
					this.contents = new PDPageContentStream(doc, page);

					this.drawHeader();
					drawBorderedBox(this.contents, 10, 197, 575, 508, LIGHT_GRAY);
					this.y -= 15;

					this.drawDeductions();
					this.drawFooter();
				} else {
					drawBorderedBox(this.contents, 10, 197, 575, 508, LIGHT_GRAY);
					this.drawPayments();
					this.drawDeductions();
					this.drawFooter();
				}
				this.contents.close();
				if (payroll.getPartTimeParams().isPresent()) {				
					PartTimeTemplate.append(doc, payroll.getPartTimeParams().get());
				}
			}
		} catch (IOException | CanNotCreatePdfException e) {
			throw new CanNotCreatePdfException(e);
		}
	}

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
				+ (p.getAntiquity().isPresent() ? safeString(formatDate(p.getAntiquity().get(), dateFormat)) : "");
		String dayTotal	   = text("TOTAL DIAS") + ": " + safeInteger(p.getTotalDays());

		String liquidPeriod = text("PERIODO LIQUIDACION") + ": del "
				+ formatDate(p.getLiquidPeriodStart(), dateLFormat).get() + " a "
				+ formatDate(p.getLiquidPeriodEnd(), dateLFormat).get();

		// BUILDING THE HEADER
		float headerFontSize = FONT_SIZE + 1;

		enterprise = croppedString(enterprise, 255, HELVETICA_BOLD, headerFontSize);
		employee   = croppedString(employee, 255, HELVETICA_BOLD, headerFontSize);

		drawBorderedBox(contents, x - 10, y - 85, 575, 120, LIGHT_GRAY);
		drawTextCenter(contents, new PDRectangle(x, y + 4, 530, 100), title, BLACK, HELVETICA_BOLD, 12, 12);

		y -= 60;
//		drawBox(contents, x, y, 275, 65, LIGHT_GRAY);
//		drawBox(contents, x + 282, y, 275, 65, LIGHT_GRAY);
		PDFToolkit.drawBorderedBox(contents, x, y, 275, 65, LIGHT_GRAY, 2f);
		PDFToolkit.drawBorderedBox(contents, x + 282, y, 275, 65, LIGHT_GRAY, 2f);

		y += 50;
		x += 10;
		drawText(contents, enterprise, x, y, BLACK, HELVETICA_BOLD, headerFontSize);
		drawText(contents, employee, x + 280, y, BLACK, HELVETICA_BOLD, headerFontSize);

		y -= 13.5;
		drawText(contents, address, x, y, BLACK, HELVETICA, headerFontSize);
		drawText(contents, nif, x + 280, y, BLACK, HELVETICA, headerFontSize);

		drawText(contents, nss, x + 380, y, BLACK, HELVETICA, headerFontSize);

		y -= 13.5;
		drawText(contents, safeString(p.getAddress2()), x, y, BLACK, HELVETICA, headerFontSize);
		drawText(contents, profesGroup, x + 280, y, BLACK, HELVETICA, headerFontSize);

		y -= 13.5;
		drawText(contents, ccc, x, y, BLACK, HELVETICA, headerFontSize);
		drawText(contents, cotizGroup, x + 280, y, BLACK, HELVETICA, headerFontSize);

		x += 100;
		drawText(contents, cif, x + 30, y, BLACK, HELVETICA, headerFontSize);
		drawText(contents, antiquDate, x + 280, y, BLACK, HELVETICA, headerFontSize);

		y -= 25;
		x -= 100;
		drawText(contents, liquidPeriod, x - 8, y, BLACK, HELVETICA, FONT_SIZE);
		new PdfText(x + 280, y, 265, 12, contents, dayTotal, BLACK, HELVETICA, FONT_SIZE, RIGHT).draw();

	}

	// CHECK IF JUMPS
	private boolean calculate() throws IOException {
		float py = calculatePayments();
		py = calculateDeductions(py);
		return py < 180;
	}

	// DRAW ACCRUALS
	private void drawPayments() throws IOException {
		x = 25;
		y = 690;

		String title			 = text("DEVENGOS").toUpperCase();
		String noSalaryTitle	 = "2. Percepciones no salariales";
		String totals			 = text("TOTALES").toUpperCase();
		String paymentTotalTitle = "A." + text("TOTAL DEVENGADO").toUpperCase();
		String paymentTotal		 = toLatinNumber(p.getPaymentsTotal().orElse(0.00));

		drawText(contents, title, x - 5, y, BLACK, HELVETICA, FONT_SIZE);
		drawTextRight(contents, new PDRectangle(x + 450, y, 100, 20), totals, BLACK, HELVETICA, FONT_SIZE, 0, 0);

		y -= NORMAL_LINE_JUMP;
		
		Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments = p.getAccruals();
		
		List<PDFPayment> salaries = getSalaries(allPayments);
		List<PDFPayment> nonSalaries = getComplementosSalariales(allPayments);
		List<PDFPayment> prestSS = getPrestSS(allPayments);
		List<PDFPayment> indemns = getIndemns(allPayments);
		List<PDFPayment> ppes = getPPEs(allPayments);
		
		List<PDFPayment> infos = getInfos(allPayments);
		List<PDFPayment> notes = getNotes(allPayments);
		List<PDFPayment> warnings = getWarnings(allPayments);
		
		List<PDFPayment> others = getOtherPayments(allPayments);

		List<PDFPayment> inKindPaymets = DefaultPayrollFuseBox.getInKindPayments(allPayments);

		String paymentTxt;
		try {
			paymentTxt = 1 + ". " + getType(1, lang);
			drawText(contents, paymentTxt, x, y, BLACK, HELVETICA, FONT_SIZE);
			y -= NORMAL_LINE_JUMP;
			salaries.forEach(ps -> {
				try {
					drawPayment(ps);
					y -= LITTLE_LINE_JUMP;
				} catch (IOException e) {}
			});
			drawText(contents, "Complementos Salariales", x + 10, y, BLACK, HELVETICA, FONT_SIZE);
			y -= LITTLE_LINE_JUMP;
			drawOrLine(nonSalaries);
			double hExtras = getExtraHoursPayment(allPayments);
			PDFPayment hExtrasPayment = new PDFPayment(hExtras != 0 ? hExtras : null, "Horas extraordinarias");
			drawPayment(hExtrasPayment);
			y -= LITTLE_LINE_JUMP;
 
			double gExtras = getExtraGratificationPayment(allPayments);
			PDFPayment gratExtrasPayment = new PDFPayment(gExtras != 0 ? gExtras : null, "Gratificaciones extraordinarias");
			drawPayment(gratExtrasPayment);
			y -= LITTLE_LINE_JUMP;
			
			if ( ppes.isEmpty() ) {
				double kExtras = DefaultPayrollFuseBox.getInKindPayment(allPayments);
				PDFPayment kindExtrasPayment = new PDFPayment(kExtras != 0 ? kExtras : null, "Salario en Especie");
				drawPayment(kindExtrasPayment);
			} else {
				drawText(contents, "Salario en Especie", x + 10, y, BLACK, HELVETICA, FONT_SIZE);
				y -= LITTLE_LINE_JUMP;
				if ( !inKindPaymets.isEmpty() )
					drawOrLine(inKindPaymets);
				drawOrLine(ppes);
			}
			y -= LITTLE_LINE_JUMP;

			drawText(contents, noSalaryTitle, x, y, BLACK, HELVETICA, FONT_SIZE);
			y -= LITTLE_LINE_JUMP;
			drawText(contents, "Indemnizaciones o suplidos", x + 10, y, BLACK, HELVETICA, FONT_SIZE);
			y -= LITTLE_LINE_JUMP;
			drawOrLine(indemns);
			drawText(contents, "Prestaciones e indemnizaciones de la Seguridad Social", x + 10, y, BLACK, HELVETICA, FONT_SIZE);
			y -= LITTLE_LINE_JUMP;
			drawOrLine(prestSS);

			if ( false && !ppes.isEmpty() ) {
				drawText(contents, "Aportación empresarial al plan de pensiones de empleo", x + 10, y, BLACK, HELVETICA, FONT_SIZE);
				y -= LITTLE_LINE_JUMP;
				drawOrLine(ppes);
			}

			drawText(contents, "Otras percepciones no salariales", x + 10, y, BLACK, HELVETICA, FONT_SIZE);
			y -= LITTLE_LINE_JUMP;
			drawOrLine(others);
			
			if ( !infos.isEmpty() ) {
			    infos.forEach(p -> p.setAmount(null));
			    drawOrLine(infos);
			}

			if ( !notes.isEmpty() ) {
			    	notes.forEach(p -> p.setAmount(null));
        			drawText(contents, "Notas", x + 10, y, BLACK, HELVETICA, FONT_SIZE);
        			y -= LITTLE_LINE_JUMP;
        			drawOrLine(notes);
			}
		
			if ( !warnings.isEmpty() ) {
			    	warnings.forEach(p -> p.setAmount(null));
        			drawText(contents, "Avisos", x + 10, y, BLACK, HELVETICA, FONT_SIZE);
        			y -= LITTLE_LINE_JUMP;
        			drawOrLine(warnings);
			}

		} catch (UnknownCraException e) {
		}
		
		y -= 1;

		drawTextRight(contents, new PDRectangle(x + 450, y, 100, 20), paymentTotal, BLACK, HELVETICA, FONT_SIZE, 7, 1);
		drawTextRight(contents, new PDRectangle(x + 150, y, 200, 25), paymentTotalTitle, BLACK, HELVETICA, FONT_SIZE, 5,1);
		drawTotalPaymentLine();
	}
	private float calculatePayments() throws IOException {
		float py = 690;
		py -= NORMAL_LINE_JUMP;
		Optional<Map<Integer, ArrayList<PDFPayment>>> allPayments = p.getAccruals();
		List<PDFPayment> salaries = getSalaries(allPayments);
		List<PDFPayment> nonSalaries = getComplementosSalariales(allPayments);
		List<PDFPayment> prestSS = getPrestSS(allPayments);
		List<PDFPayment> indemns = getIndemns(allPayments);
		List<PDFPayment> others = DefaultPayrollFuseBox.getOtherPayments(allPayments);	
		py -= NORMAL_LINE_JUMP;
		for (PDFPayment ps : salaries) {			
			py -= LITTLE_LINE_JUMP;
		}
		py -= LITTLE_LINE_JUMP;
		py = calculateOrLine(nonSalaries, py);
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py = calculateOrLine(indemns, py);
		py -= LITTLE_LINE_JUMP;
		py = calculateOrLine(prestSS, py);
		py -= LITTLE_LINE_JUMP;
		py = calculateOrLine(others, py);
		py -= 1;
		return py;
	}

	private void drawOrLine(List<PDFPayment> payments) throws IOException {
		if (payments == null || payments.isEmpty()) {
			drawPaymentLine(10);
			y -= LITTLE_LINE_JUMP;
		} else {				
			payments.forEach(ps -> {
				try {
					drawPayment(ps, 10);
					y -= LITTLE_LINE_JUMP;
				} catch (IOException e) {
				}
			});
		}
	}
	
	private float calculateOrLine(List<PDFPayment> payments, float py) throws IOException {
		if (payments == null || payments.isEmpty()) {
			py -= LITTLE_LINE_JUMP;
		} else {
			for (PDFPayment ps : payments) {	
				py -= LITTLE_LINE_JUMP;
			}
		}
		return py;
	}
	
	private void drawPaymentLine(float indent) throws IOException {
		PDFToolkit.drawDashedLine(contents, x + 10 + indent, y, 350 - indent, .2f, BLACK, new float[] {.3f, 1f}, 0);
		drawBox(contents, x + 360, y - .4f, 100, .8f, BLACK);
	}
	
	private void drawTotalPaymentLine() throws IOException {
		PDFToolkit.drawDashedLine(contents, x + 240, y, (460 - 240), .2f, BLACK, new float[] {.3f, 1f}, 0);
		drawBox(contents, x + 460, y - .4f, 85, .8f, BLACK);
	}
	private void drawTotalLine(float indent) throws IOException {
		PDFToolkit.drawDashedLine(contents, x + indent, y, 460 - indent, .2f, BLACK, new float[] {.3f, 1f}, 0);
		drawBox(contents, x + 460, y - .4f, 85, .8f, BLACK);
	}
	private void drawPayment(PDFPayment pdfPayment, float indent) throws IOException {
		String entryTxt = safeString(pdfPayment.getDescription());
		String entryValue = toLatinNumber(pdfPayment.getAmount().orElse(null))/* + " " + text("MONEDA")*/;

		PdfText text = new PdfText(x + 10 + indent, y, 350 - indent, FONT_SIZE, contents, entryTxt, BLACK, HELVETICA,
				FONT_SIZE, LEFT);
		text.drawCroppableLine();
		
		PdfText t2 = new PdfText(x + 360, y, 100, FONT_SIZE, contents, entryValue, PdfColors.BLACK, HELVETICA,
				FONT_SIZE, RIGHT);
		t2.draw();
		drawPaymentLine(indent);
	}
	private void drawPayment(PDFPayment pdfPayment) throws IOException {
		drawPayment(pdfPayment, 0);
	}

	// DRAW DEDUCTIONS
	private void drawDeductions() throws IOException {

		// DEDUCTIONS CONTENT
		String title			   = "II. " + text("DEDUCCIONES").toUpperCase();
		String aportaciones		   = "1. Aportaciones del trabajador a las cotizaciones de la Seguridad Social y conceptos de recaudación";
		String deductionTotalTitle = "B. " + text("TOTAL DEDUCIR").toUpperCase();
		String deductionTotal	   = toLatinNumber(safeDouble(p.getDeductionTotal()));
		String payrollTotalTitle   = text("TOTAL PERCIBIR").toUpperCase() + " (A-B)";
		String payrollTotal		   = toLatinNumber(safeDouble(p.getPayrollTotal()));
		String enterpriseSign	   = text("FIRMA EMPRESA");
		String dateLFormat = "dd 'de' MMMM 'de' yyyy";
		String issueDate = AonDateUtils.format(p.getLiquidPeriodEnd().orElse(null), dateLFormat);
		

		x = 23.5f;

		drawText(contents, title, x - 5, y - 15, BLACK, HELVETICA, FONT_SIZE);
		y -= NORMAL_LINE_JUMP;
		drawText(contents, aportaciones, x, y - 15, BLACK, HELVETICA, FONT_SIZE);
		y -= NORMAL_LINE_JUMP;
		
		Optional<Map<Integer, ArrayList<PDFDeduction>>> allDeductions = p.getDeductions();
		
		PDFDeduction ccDeduction = DefaultPayrollFuseBox.getSingleDeductionByType(allDeductions, DeductionType.COMMON_CONTINGENCY, "Contingencias Comunes", d -> AonStringUtils.notEquals("MEI", d.getName().orElse(null)));
		y -= NORMAL_LINE_JUMP + 2;
		drawDeduction(ccDeduction.getDescription().orElse(""), ccDeduction.getPercent().orElse(0d), ccDeduction.getAmount().orElse(0d));
		
		PDFDeduction meiDeduction = getSingleDeductionByType(allDeductions, DeductionType.MEI, "Mecanismo de Equidad Intergeneracional (MEI)", d -> AonStringUtils.equals("MEI", d.getName().orElse(null)));
		y -= LITTLE_LINE_JUMP;
		drawDeduction(meiDeduction.getDescription().orElse(""), meiDeduction.getPercent().orElse(0d), meiDeduction.getAmount().orElse(0d));

		PDFDeduction unemDeduction = getSingleDeductionByType(allDeductions, DeductionType.UNEMPLOYMENT, "Desempleo");
		y -= LITTLE_LINE_JUMP;
		drawDeduction(unemDeduction.getDescription().orElse(""), unemDeduction.getPercent().orElse(0d), unemDeduction.getAmount().orElse(0d));
		
		PDFDeduction fpDeduction = getSingleDeductionByType(allDeductions, DeductionType.JOB_TRAINING, "Formación Profesional");
		y -= LITTLE_LINE_JUMP;
		drawDeduction(fpDeduction.getDescription().orElse(""), fpDeduction.getPercent().orElse(0d), fpDeduction.getAmount().orElse(0d));
		
		y -= LITTLE_LINE_JUMP;
		drawText(contents, "Horas extraordinarias", x + 10, y, BLACK, HELVETICA, FONT_SIZE);
		
		PDFDeduction strucDeduction = getSingleDeductionByType(allDeductions, DeductionType.STRUCTURAL_OVERTIME, "Fuerza mayor o Estructurales");
		y -= LITTLE_LINE_JUMP;
		drawDeduction(strucDeduction.getDescription().orElse(""), strucDeduction.getPercent().orElse(0d), strucDeduction.getAmount().orElse(0d), 10);
		
		PDFDeduction noStrucDeduction = getSingleDeductionByType(allDeductions, DeductionType.NON_STRUCTURAL_OVERTIME, "No Estructurales");
		y -= LITTLE_LINE_JUMP;
		drawDeduction(noStrucDeduction.getDescription().orElse(""), noStrucDeduction.getPercent().orElse(0d), noStrucDeduction.getAmount().orElse(0d), 10);
		
		y -= LITTLE_LINE_JUMP;
		drawDeductionNoPercent("TOTAL APORTACIONES", p.getTotalSSContributions().orElse(0d));
		
		PDFDeduction irpfDeduction = getSingleDeductionByType(allDeductions, DeductionType.IRPF, "2. Impuesto sobre la renta de las personas Físicas");
		y -= LITTLE_LINE_JUMP;
		drawDeduction(irpfDeduction.getDescription().orElse(""), irpfDeduction.getPercent().orElse(0d), irpfDeduction.getAmount().orElse(0d), -10f);
		
		PDFDeduction advancedDeduction = getSingleDeductionByType(allDeductions, DeductionType.ADVANCE_PAYMENT, "3. Anticipos");
		y -= LITTLE_LINE_JUMP;
		drawDeductionNoPercent(advancedDeduction.getDescription().orElse(""), advancedDeduction.getAmount().orElse(0d), -10f);
		
		PDFDeduction kindDeduction = getSingleDeductionByType(allDeductions, DeductionType.IN_KIND, "4. Valor de los productos recibidos en especie");
		y -= LITTLE_LINE_JUMP;
		drawDeductionNoPercent(kindDeduction.getDescription().orElse(""), kindDeduction.getAmount().orElse(0d), -10f);
		
		PDFDeduction otherDeduction = getOtherDeduction(allDeductions, "5. Otras deducciones");
		y -= LITTLE_LINE_JUMP;
		drawDeductionNoPercent(otherDeduction.getDescription().orElse(""), otherDeduction.getAmount().orElse(0d), -10f);
		
		List<PDFDeduction> otherDeductions = getOtherDeductions(allDeductions);
		listDeductions(otherDeductions, false);
		
		y -= NORMAL_LINE_JUMP;
		drawText(contents, deductionTotalTitle, x + 10 + 200, y, BLACK, HELVETICA, FONT_SIZE);
		drawTextRight(contents, new PDRectangle(x + 450, y, 100, 20), deductionTotal, BLACK, HELVETICA, FONT_SIZE, 7, 0);
		y -= 1;
		drawTotalLine(210);
		y -= NORMAL_LINE_JUMP;
		drawText(contents, payrollTotalTitle, x + 10 + 150, y, BLACK, HELVETICA, FONT_SIZE);
		drawTextRight(contents, new PDRectangle(x + 450, y, 100, 20), payrollTotal, BLACK, HELVETICA, FONT_SIZE, 7, 0);
		y -= 1;
		drawTotalLine(160);
		y -= NORMAL_LINE_JUMP;
		drawText(contents, enterpriseSign, x + 10 + 120, y, BLACK, HELVETICA, FONT_SIZE - 1);
		if (issueDate != null) {
			drawText(contents, issueDate, x + 10 + 380, y, BLACK, HELVETICA, FONT_SIZE - 1);			
		}
		y -= NORMAL_LINE_JUMP;
		drawText(contents, "RECIBÍ", x + 10 + 380, y, BLACK, HELVETICA, FONT_SIZE - 1);
		if (logo.isPresent()) {
			byte[]	 bytes = logo.get().readAllBytes();
			PdfImage img   = new PdfImage(x + 120, y - 50, 170, 70, ALIGNMENT.CENTER, contents, doc, bytes);
			try {				
			img.scale(100, 50, ALIGNMENT.CENTER).draw();
			} catch (Exception e) {}
		}
	}
	private void listDeductions(List<PDFDeduction> deductions, boolean percent) throws IOException {
		for (PDFDeduction deduction : deductions) {
			y -= LITTLE_LINE_JUMP;
			drawOtherDeduction(deduction.getDescription().orElse(""), deduction.getAmount().orElse(0d), 0f);
		}
	}
	private static float calculateOtherDeductions(float py, List<PDFDeduction> deductions) {
		for (int i=0; i < deductions.size(); i++) {
			py -= LITTLE_LINE_JUMP;
		}
		return py;
	}

	private float calculateDeductions(float py) {
		py -= NORMAL_LINE_JUMP;
		py -= NORMAL_LINE_JUMP;
		py -= NORMAL_LINE_JUMP + 2;
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py -= LITTLE_LINE_JUMP;
		py = calculateOtherDeductions(py, getOtherDeductions(p.getDeductions()));
		py -= NORMAL_LINE_JUMP;
		py -= 1;
		py -= NORMAL_LINE_JUMP;
		py -= 1;
		py -= NORMAL_LINE_JUMP;
		py -= NORMAL_LINE_JUMP;
		py -= 50;
		return py;
	}
	
	private void drawDeductionLine(float indent) throws IOException {
		PDFToolkit.drawDashedLine(contents, x + 10 + indent, y, 280 - indent, .2f, BLACK, new float[] {.3f, 1f}, 0);
		drawBox(contents, x + 10 + 280 , y - .4f, 40, .8f, BLACK);
		drawBox(contents, x + 360, y - .4f, 100, .8f, BLACK);
	}
	
	private void drawOtherDeductionLine(float indent) throws IOException {
		PDFToolkit.drawDashedLine(contents, x + 10 + indent, y, 280 - indent, .2f, BLACK, new float[] {.3f, 1f}, 0);
		drawBox(contents, x + 10 + 280 , y - .4f, 70, .8f, BLACK);
	}
	
	private void drawOtherDeduction(String deductionName, double ccAmount, float indent) throws IOException {
		PdfText text = new PdfText(x + 10 + indent, y, 290 - indent, FONT_SIZE, contents, deductionName, BLACK, HELVETICA,
				FONT_SIZE, LEFT);
		text.drawCroppableLine();
		if (ccAmount!= 0) {
			String entryAmount = toLatinNumber(ccAmount);
			PdfText amountText = new PdfText(x + 10 + 280, y, 70, FONT_SIZE, contents, entryAmount, BLACK, HELVETICA,
					FONT_SIZE, RIGHT);
			amountText.draw();
		}
		drawOtherDeductionLine(indent);
	}
	
	private void drawDeduction(String deductionName, double ccPercent, double ccAmount, float indent) throws IOException {
		PdfText text = new PdfText(x + 10 + indent, y, 290 - indent, FONT_SIZE, contents, deductionName, BLACK, HELVETICA,
				FONT_SIZE, LEFT);
		text.drawCroppableLine();
		if (ccPercent != 0) {
			String entryPercent = toLatinNumber(ccPercent) + " % ";
			PdfText percentText = new PdfText(x + 10 + 280, y, 40, FONT_SIZE, contents, entryPercent, BLACK, HELVETICA,
					FONT_SIZE, RIGHT);
			percentText.draw();
		}
		if (ccAmount != 0) {
			String entryAmount = toLatinNumber(ccAmount);
			PdfText amountText = new PdfText(x + 360, y, 100, FONT_SIZE, contents, entryAmount, BLACK, HELVETICA,
					FONT_SIZE, RIGHT);
			amountText.draw();
		}
		drawDeductionLine(indent);
	}
	private void drawDeduction(String deductionName, double ccPercent, double ccAmount) throws IOException {
		drawDeduction(deductionName, ccPercent, ccAmount, 0);
	}
	
	private void drawDeductionNoPercent(String deductionName, double ccAmount, float indent) throws IOException {
		PdfText text = new PdfText(x + 10 + indent, y, 350 - indent, FONT_SIZE, contents, deductionName, BLACK, HELVETICA,
				FONT_SIZE, LEFT);
		text.drawCroppableLine();
		if (ccAmount != 0) {
			String entryAmount = toLatinNumber(ccAmount);
			PdfText amountText = new PdfText(x + 360, y, 100, FONT_SIZE, contents, entryAmount, BLACK, HELVETICA,
					FONT_SIZE, RIGHT);
			amountText.draw();
		}
		drawPaymentLine(indent);
	}
	private void drawDeductionNoPercent(String deductionName, double ccAmount) throws IOException {
		drawDeductionNoPercent(deductionName, ccAmount, 0);
	}

	public void drawFooter() throws IOException {

		Optional<ContingencyBases> contigencies = p.getContingencies();
		if (contigencies.isPresent())
		{
			y = 177;
			x = 25;

			final String title	= text("TITULO PIE");
			final String title2	= text("TITULO PIE 2");
			final String apEnt	= text("AP EMPRESA").toUpperCase();
			final String type	= text("TIPO").toUpperCase();
			final String base	= text("BASE").toUpperCase();

			final String commonContingenciesTitle = "1. " + text("CONTINGENCIAS COMUNES");
			final String monthlyAmmountTitle	  = text("IMPORTE DE REMUNERACION MENSUAL");
			final String extraHourProrrationTitle = text("IMPORTE PRORRATA DE PAGA EXTRAORDINARIA");

			final String meiTitle			= "2. " + text("MECANISMO DE EQUIDAD INTERGENERACIONAL");

			final String profContingenciesTitle	= "3. "
					+ text("CONTINGENCIAS PROFESIONALES Y CONCEPTOS DE RECAUDACION CONJUNTA");
			final String atEpTitle				= text("AT Y EP");
			final String unemploymentTitle		= text("DESEMPLEO");
			final String profesFormTitle		= text("FORMACION PROFESIONAL");
			final String fogasaTitle			= text("FONDO DE GARANTIA SALARIAL");

			final String		   extraHoursTitle		   = "4. " + text("COTIZACION ADICIONAL POR HORAS EXTRAS");
			final String		   forceMajeureTitle	   = text("FUERZA MAYOR O");
			final String		   noStructTitle		   = text("NO ESTRUCTURALES");
			String				   irpfTitle			   = "5. " + text("BASE SUJETA A RETENCION IRPF") + " ";
			final String		   totalContingenciesTitle = text("TOTAL APORTACIONES");
			final ContingencyBases conts				   = contigencies.get();

			final String totalCostsTitle  = text("TOTAL COSTES");
			final String totalCostsAmount = ""
					+ toLatinNumber(p.getPaymentsTotal().orElse(0d) + conts.getTotal().orElse(0d)) + " "
					+ text("MONEDA");

			final String monthlyAmmount	= toLatinNumber(safeDouble(conts.getMonthlyAmount())) + " " + text("MONEDA");
			final String commContBase	= toLatinNumber(safeDouble(conts.getCommonContBase())) + " " + text("MONEDA");
			String		 commContType	= drawCostPercentage(conts.getCommonContType());

			if (commContType.contains("-1"))
				commContType = "";

			final String commContApEnt		   = toLatinNumber(safeDouble(conts.getCommonContApEnterprise())) + " "
					+ text("MONEDA");
			final String extraProrrationAmount = toLatinNumber(safeDouble(conts.getExtraProrationAmount())) + " "
					+ text("MONEDA");
			final String profContingenciesBase = toLatinNumber(safeDouble(conts.getProfessionalContBase())) + " "
					+ text("MONEDA");
			
			String		 meiType	= drawCostPercentage(conts.getMeiType());

			if (meiType.contains("-1"))
				meiType = "";

			final String meiApEnt		   = toLatinNumber(safeDouble(conts.getMeiApEnterprise())) + " "
					+ text("MONEDA");

			String		 atEpType			   = drawCostPercentage(conts.getAtEpType());

			if (atEpType.contains("-1"))
				atEpType = "";

			final String atEpApEnt		  = toLatinNumber(safeDouble(conts.getAtEpApEnterprise())) + " "
					+ text("MONEDA");
			String		 unemploymentType = drawCostPercentage(conts.getUnemploymentType());

			if (unemploymentType.contains("-1"))
				unemploymentType = "";

			final String unemploymentApEnt = toLatinNumber(safeDouble(conts.getUnemploymentApEnterprise())) + " "
					+ text("MONEDA");
			final String profesFormType	   = toLatinNumber(safeDouble(conts.getProfesFormType())) + " %";
			final String profesFormApEnt   = toLatinNumber(safeDouble(conts.getProfesFormApEnterprise())) + " "
					+ text("MONEDA");

			String fogasaType = drawCostPercentage(conts.getFogasaType());
			if (fogasaType.contains("-1"))
				fogasaType = "";

			String fogasaApEnt		= toLatinNumber(safeDouble(conts.getFogasaApEnterprise())) + " " + text("MONEDA");
			String forceMajeureBase	= toLatinNumber(safeDouble(conts.getForceMajeureBase())) + " " + text("MONEDA");

			String forceMajeureType = drawCostPercentage(conts.getForceMajeureType());
			if (forceMajeureType.contains("-1"))
				forceMajeureType = "";

			String forceMajeureApEnt = toLatinNumber(safeDouble(conts.getForceMajeureApEnterprise())) + " "
					+ text("MONEDA");
			String noStructBase		 = toLatinNumber(safeDouble(conts.getNoStructBase())) + " " + text("MONEDA");

			String noStructType = drawCostPercentage(conts.getNoStructType());
			if (noStructType.contains("-1"))
				noStructType = "";

			String noStructApEnt			= toLatinNumber(safeDouble(conts.getNoStructApEnterprise())) + " "
					+ text("MONEDA");
			String totalIrpf				= toLatinNumber(
					safeDouble(conts.getIrpfEsp()) + conts.getIrpfRetribDiner().orElse(0.00)) + " " + text("MONEDA");
			String totalContingenciesAmount	= toLatinNumber(safeDouble(conts.getTotal())) + " " + text("MONEDA");

			Double irpfEsp	  = safeDouble(conts.getIrpfEsp());
			Double irpfRetDin = safeDouble(conts.getIrpfRetribDiner());

			if (irpfEsp != 0)
				irpfTitle += toLatinNumber(irpfEsp) + " " + text("MONEDA") + " " + text("EN ESPECIE") + " ";
			if (irpfRetDin != 0)
				irpfTitle += toLatinNumber(irpfRetDin) + " " + text("MONEDA") + " "
						+ text("EN RETRIBUCIONES DINERARIAS");

			drawBorderedBox(contents, 10, 10, 575, 182, LIGHT_GRAY);
			drawText(contents, title, x, y, BLACK, HELVETICA_BOLD, FONT_SIZE - 1);

			y -= 10;
			drawText(contents, title2, x, y, BLACK, HELVETICA_BOLD, FONT_SIZE - 1);

			y -= 18;
			drawBox(contents, x + 480, y, 70, 20, LIGHT_GRAY);
			drawTextRight(contents, new PDRectangle(x + 480, y, 70, 20), apEnt, BLACK, HELVETICA, FONT_SIZE - 1, 5, 7);

			drawBox(contents, x + 408, y, 70, 20, LIGHT_GRAY);
			drawTextRight(contents, new PDRectangle(x + 408, y, 70, 20), type, BLACK, HELVETICA, FONT_SIZE - 1, 5, 7);

			drawBox(contents, x + 336, y, 70, 20, LIGHT_GRAY);
			drawTextRight(contents, new PDRectangle(x + 336, y, 70, 20), base, BLACK, HELVETICA, FONT_SIZE - 1, 5, 7);

			drawText(contents, commonContingenciesTitle, x, y, BLACK, HELVETICA_BOLD, FONT_SIZE - 1);

			y -= 9;
			x += 15;
			drawText(contents, monthlyAmmountTitle, x, y, BLACK, HELVETICA, FONT_SIZE - 3);
			drawTextRight(contents, new PDRectangle(x + 215, y, 70, 70), monthlyAmmount, BLACK, HELVETICA, FONT_SIZE - 3,
					5, 1);

			drawTextRight(contents, new PDRectangle(x + 322, y - 5, 70, 70), commContBase, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), commContType, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), commContApEnt, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);

			y -= 8;
			drawText(contents, extraHourProrrationTitle, x, y, BLACK, HELVETICA, FONT_SIZE - 3);
			drawTextRight(contents, new PDRectangle(x + 215, y, 70, 70), extraProrrationAmount, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);

			y -= 12;
			drawText(contents, meiTitle, x - 15, y, BLACK, HELVETICA_BOLD, 7);
			drawTextRight(contents, new PDRectangle(x + 322, y, 70, 70), commContBase, BLACK, HELVETICA,
				FONT_SIZE - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 396, y, 70, 70), meiType, BLACK, HELVETICA,
    				FONT_SIZE - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y, 70, 70), meiApEnt, BLACK, HELVETICA,
    				FONT_SIZE - 3, 5, 1);

			y -= 12;
			drawText(contents, profContingenciesTitle, x - 15, y, BLACK, HELVETICA_BOLD, 7);

			y -= 10;
			drawText(contents, atEpTitle, x, y, BLACK, HELVETICA, FONT_SIZE - 3);
			drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), atEpType, BLACK, HELVETICA, FONT_SIZE - 3,
					5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), atEpApEnt, BLACK, HELVETICA, FONT_SIZE - 3,
					5, 1);

			y -= 8;
			drawText(contents, unemploymentTitle, x, y, BLACK, HELVETICA, FONT_SIZE - 3);
			drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), unemploymentType, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), unemploymentApEnt, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);

			y -= 8;
			drawTextRight(contents, new PDRectangle(x + 322, y, 70, 70), profContingenciesBase, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);
			drawText(contents, profesFormTitle, x, y, BLACK, HELVETICA, FONT_SIZE - 3);
			drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), profesFormType, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), profesFormApEnt, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);

			y -= 8;
			drawText(contents, fogasaTitle, x, y, BLACK, HELVETICA, FONT_SIZE - 3);
			drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), fogasaType, BLACK, HELVETICA, FONT_SIZE - 3,
					5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), fogasaApEnt, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);

			y -= 12;
			drawText(contents, extraHoursTitle, x - 15, y, BLACK, HELVETICA_BOLD, FONT_SIZE - 2);

			y -= 10;
			drawText(contents, forceMajeureTitle, x, y, BLACK, HELVETICA, FONT_SIZE - 3);
			drawTextRight(contents, new PDRectangle(x + 322, y - 2, 70, 70), forceMajeureBase, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 396, y - 2, 70, 70), forceMajeureType, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 2, 70, 70), forceMajeureApEnt, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);

			y -= 8;
			drawText(contents, noStructTitle, x, y, BLACK, HELVETICA, FONT_SIZE - 3);
			drawTextRight(contents, new PDRectangle(x + 322, y - 2, 70, 70), noStructBase, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 396, y - 2, 70, 70), noStructType, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);
			drawTextRight(contents, new PDRectangle(x + 465, y - 2, 70, 70), noStructApEnt, BLACK, HELVETICA,
					FONT_SIZE - 3, 5, 1);

			y -= 12;
			x -= 15;
			drawText(contents, irpfTitle, x, y, BLACK, HELVETICA_BOLD, FONT_SIZE - 3);
			drawBox(contents, x, y - 2, 405, .2f, BLACK);
			drawTextRight(contents, new PDRectangle(x + 308, y - 5, 100, 10), totalIrpf, BLACK, HELVETICA, FONT_SIZE - 3,
					5, 5);
			drawTextRight(contents, new PDRectangle(x + 451, y - 5, 30, 10), totalContingenciesTitle, BLACK,
					HELVETICA_BOLD, 6.5f, 5, 5);
			drawTextRight(contents, new PDRectangle(x + 518, y - 5, 30, 10), totalContingenciesAmount, BLACK,
					HELVETICA_BOLD, 6.5f, 3, 5);

			if (p.getImpressionType() == IMPRESION.DRAFT)
			{
				drawTextRight(contents, new PDRectangle(x + 451, y - 14, 30, 10), totalCostsTitle, BLACK,
						HELVETICA_BOLD,

						6.5f, 5, 5);
				drawTextRight(contents, new PDRectangle(x + 518, y - 14, 30, 10), totalCostsAmount, BLACK,
						HELVETICA_BOLD, 6.5f, 3, 5);
			}

		}

	}

	public String text(String name) {
		return words.getString(name);
	}

	private static boolean isNotLog(int key) {
	    return !Objects.equals(key, IPayrollTemplate.INFO) && !Objects.equals(key, IPayrollTemplate.NOTE)
		    && !Objects.equals(key, IPayrollTemplate.WARNING);
	}

}
