package com.esferalia.aon.in.payroll.pdf.maker.payroll;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors.LIGHT_GRAY;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.formatDate;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.toLatinNumber;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT.CENTER;
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
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DeductionTypes.getType;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayrollFuseBox.getDeductionsByType;
import static java.util.ResourceBundle.getBundle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;

import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfBox;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfImage;
import com.esferalia.aon.in.payroll.pdf.api.component.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfColors;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.ASCIIArtGenerator.ASCIIArtFont;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.ContingencyBases;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.DefaultPayroll.IMPRESION;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.IDefaultPayroll;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFDeduction;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PDFPayment;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PayrollTypes;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.UnknownCraException;
import com.esferalia.aon.in.payroll.tgss.cra.CRAException;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * Class to print Payroll PDF file with PDFbox
 * 
 * @author akrck02
 * @version 0.5-AK
 */
public class PayrollTemplate implements IPayrollTemplate {

    static float fontSize = 9f;

    String filename = "./payroll.pdf";
    float limit;
    PDPageContentStream contents;

    PDDocument doc;
    IDefaultPayroll p;
    Optional<InputStream> logo;
    Locale lang;

    float x;
    float y;
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
	} catch (Exception e) {
	    throw new CanNotCreatePdfException(e);
	}
    }

    public PayrollTemplate(IDefaultPayroll payroll, Optional<InputStream> logo, Optional<Locale> language)
	    throws CanNotCreatePdfException {
	this(Arrays.asList(payroll), logo, language);
    }

    public PayrollTemplate(Collection<IDefaultPayroll> payrolls, Optional<InputStream> logo, Optional<Locale> language)
	    throws CanNotCreatePdfException {
	try {
	    this.doc = new PDDocument();
	    this.lang = language.orElse(new Locale("Es"));
	    this.words = getBundle("com.esferalia.aon.in.payroll.pdf.maker.payroll.bundle.PayrollBundle", this.lang);
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
			return AonDateUtils.compare(date1, date2);
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
		    PartTimeTemplate.append(doc, payroll.getPartTimeParams().orElse(null));
		}
	    }
	} catch (IOException | CanNotCreatePdfException e) {
	    throw new CanNotCreatePdfException(e);
	}
    }
    
    public PayrollTemplate(Collection<IDefaultPayroll> payrolls, Optional<InputStream> logo, Optional<Locale> language, String password)
    	    throws CanNotCreatePdfException {
    	try {
    	    this.doc = new PDDocument();
    	    this.lang = language.orElse(new Locale("Es"));
    	    this.words = getBundle("com.esferalia.aon.in.payroll.pdf.maker.payroll.bundle.PayrollBundle", this.lang);
    	    this.limit = 800;

    	    byte[] bLogo = null;
    	    if (logo.isPresent())
    		bLogo = logo.get().readAllBytes();
    	    
    	    if(null != password) {
    	    	// Crear permisos de acceso
                AccessPermission accessPermission = new AccessPermission();

                // Establecer la política de protección
                StandardProtectionPolicy policy = new StandardProtectionPolicy("40ns0lut10ns", password, accessPermission);
                policy.setEncryptionKeyLength(128); // También puede ser 256
                policy.setPermissions(accessPermission);
                
                // Proteger el documento con la política establecida
                this.doc.protect(policy);
    	    }

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
    			return AonDateUtils.compare(date1, date2);
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
    		    PartTimeTemplate.append(doc, payroll.getPartTimeParams().orElse(null));
    		}
    	    }
    	} catch (IOException | CanNotCreatePdfException e) {
    	    throw new CanNotCreatePdfException(e);
    	}
        }

    private void drawHeader() throws IOException {

	x = 20;
	y = 795;
	String dateFormat = text("FORMATO FECHA");
	String dateLFormat = "dd 'de' MMMM 'de' yyyy";
	String replace = PayrollTypes.toString(p.getPayrollType().orElse(PayrollTypes.Type.SALARY), lang);

	// HEADER CONTENT
	String title = text("TITULO").replace("*", replace).toUpperCase();
	String enterprise = safeString(p.getEnterprise());
	String employee = safeString(p.getEmployee());
	String address = safeString(p.getAddress());

	String nif = text("NIF") + ": " + safeString(p.getNif());
	String nss = text("NSS") + ": " + safeString(p.getNss());
	String profesGroup = text("G.PROFESIONAL") + ": " + safeString(p.getProfessionalGroup());
	String ccc = text("CCC") + ": " + safeString(p.getCcc());
	String cif = text("CIF") + ": " + safeString(p.getCif());
	String cotizGroup = text("G.COTIZ") + ": " + safeString(p.getQuotationGroup());
	String antiquDate = text("FECHA ANTIGUEDAD") + ": "
		+ safeString(formatDate(p.getAntiquity().orElse(null), dateFormat));
	String dayTotal = text("TOTAL DIAS") + ": " + safeInteger(p.getTotalDays());

	String liquidPeriod = text("PERIODO LIQUIDACION") + ": del "
		+ formatDate(p.getLiquidPeriodStart(), dateLFormat).get() + " a "
		+ formatDate(p.getLiquidPeriodEnd(), dateLFormat).get();

	// BUILDING THE HEADER
	float headerFontSize = fontSize;

	enterprise = croppedString(enterprise, 255, HELVETICA_BOLD, fontSize);
	employee = croppedString(employee, 255, HELVETICA_BOLD, fontSize);

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
	drawText(contents, liquidPeriod, x - 8, y, BLACK, HELVETICA, fontSize);
	new PdfText(x + 280, y, 265, 12, contents, dayTotal, BLACK, HELVETICA, fontSize, RIGHT).draw();

    }

    // CHECK IF JUMPS
    private boolean calculate() throws IOException {

	Optional<Map<Integer, ArrayList<PDFPayment>>> accruals = p.getAccruals();
	Optional<Map<Integer, ArrayList<PDFDeduction>>> deductions = p.getDeductions();

	double sum = 0;

	sum += 20;
	sum += accruals.get().entrySet().size() * 12;
	sum += accruals.get().entrySet().stream().mapToDouble(entry -> entry.getValue().stream().count()).sum() * 12;

	sum += 20;
	sum += deductions.get().entrySet().size() * 12;
	sum += deductions.get().entrySet().stream().mapToDouble(entry -> entry.getValue().stream().count()).sum() * 12;

	return sum > 370;
    }

    // DRAW ACCRUALS
    private void drawPayments() throws IOException {
	x = 25;
	y = 675;

	String title = text("DEVENGOS").toUpperCase();
	String totals = text("TOTALES").toUpperCase();
	String paymentTotalTitle = "A." + text("TOTAL DEVENGADO").toUpperCase() + ":";
	String paymentTotal = toLatinNumber(p.getPaymentsTotal().orElse(0.00)) + " " + text("MONEDA");

	drawText(contents, title, x, y, BLACK, HELVETICA_BOLD, fontSize + 3);
	drawBox(contents, x + 470, y - 7, 80, 20, LIGHT_GRAY);
	drawTextRight(contents, new PDRectangle(x + 450, y - 7, 100, 20), totals, BLACK, HELVETICA, fontSize, 7, 7);

	y -= 20;

	Optional<Map<Integer, ArrayList<PDFPayment>>> accruals = p.getAccruals();
	accruals.get().entrySet().stream()
		.filter(m -> isNotLog(m.getKey()))
		.sorted(Map.Entry.<Integer, ArrayList<PDFPayment>>comparingByKey()).forEach(m -> {
		    try {
			double localTotal = m.getValue().stream()
				.mapToDouble(accrual -> safeDouble(accrual.getAmount())).sum();
			if (localTotal != 0) {
			    String craNumber = m.getKey() < 100 ? (m.getKey() + ".") : "";
			    String paymentTxt = getType(m.getKey(), lang);
			    String paymentTotalTxt = toLatinNumber(localTotal) + " " + text("MONEDA");

			    drawText(contents, craNumber, x, y, BLACK, HELVETICA_BOLD, fontSize);

			    drawText(contents, paymentTxt, x + 15, y, BLACK, HELVETICA_BOLD, fontSize);
			    drawTextRight(contents, new PDRectangle(x + 355, y - 5, 100, 10), paymentTotalTxt, BLACK,
				    HELVETICA, fontSize, 5, 5);
			    drawBox(contents, x, y - 2, 455, .2f, BLACK);
			    y -= 15;

			    m.getValue().stream().forEach(n -> {
				String entryValue = toLatinNumber(n.getAmount().orElse(null)) + " " + text("MONEDA");
				String entryTxt = " por " + safeString(n.getDescription());

				PdfText text = new PdfText(x, y, 60, 15, contents, entryValue, BLACK, HELVETICA,
					fontSize, RIGHT);
				text.draw();

				PdfText t2 = new PdfText(x + 64, y, 350, 15, contents, entryTxt, PdfColors.BLACK,
					HELVETICA, fontSize, LEFT);
				t2.drawCroppableLine();

				y -= 10.5f;
			    });
			}
		    } catch (IOException | UnknownCraException ignore) {
		    }
		    y -= 5;
		});
	
	
	for (int log : new int[] { IPayrollTemplate.INFO, IPayrollTemplate.NOTE, IPayrollTemplate.WARNING }) {
	    ArrayList<PDFPayment> logs = accruals.get().get(log);
	    if (logs != null && !logs.isEmpty()) {
		List<String> descriptions = logs.stream().map(PDFPayment::getDescription).filter(Optional::isPresent)
			.map(Optional::get).filter(AonStringUtils::isNotBlank).toList();
		if (!descriptions.isEmpty()) {
		    try {
        		    String logTxt = getType(log, lang);
        		    if ( AonStringUtils.isNotBlank(logTxt)) {
                		    drawText(contents, logTxt, x + 15, y, BLACK, HELVETICA_BOLD, fontSize);
                		    drawBox(contents, x, y - 2, 455, .2f, BLACK);
                		    y -= 15;
        		    }
        
        		    descriptions.forEach(entryTxt -> {
        			if ( log == IPayrollTemplate.INFO )  {
                		    	PdfText t2 = new PdfText(x + 64, y, 350, 15, contents, entryTxt, PdfColors.BLACK, HELVETICA,
                				fontSize, LEFT);
                			t2.drawCroppableLine();
        			} else {
                			try {
                			    	if ( AonStringUtils.startsWith(entryTxt, "art")) {
                			    	    	String art =  AonStringUtils.removeStart(entryTxt, "art") ;
                    		    			drawText(contents, art , x + 64, y, BLACK, PdfFonts.COURIER, fontSize);
                			    	}  else if (AonStringUtils.startsWith(entryTxt, "ascii")) {
                			    	    	String ascii = AonStringUtils.removeStart(entryTxt, "ascii");
                			    	    	try {
                        			    	    	String [] lines = ASCIIArtGenerator.generateAsciiArt(ascii, ASCIIArtGenerator.ART_SIZE_SMALL, ASCIIArtFont.ART_FONT_COURIER, "#");
                        			    	    	for ( String line : lines ) {
                        			    	    	    drawText(contents, line , x + 64, y, BLACK, PdfFonts.COURIER, fontSize/2);
                        			    	    	    y -= 4.00f; //10.5f;
                        			    	    	}
                			    	    	} catch ( Exception e ) {
                			    	    	    
                			    	    	}
                			    	}
                			    	else {
                		    			drawText(contents, entryTxt, x + 64, y, BLACK, PdfFonts.HELVETICA, fontSize);
                			    	}
                			} catch ( IOException e ) {
                			    
                			}
        			}
        			y -= 10.5f;
        		    });
		    } catch ( IOException | UnknownCraException e ) {
			
		    }
		}
	    }
	}
	y -= 5;

	drawTextRight(contents, new PDRectangle(x + 350, y, 200, 25), paymentTotal, BLACK, HELVETICA, fontSize, 7, 5);
	drawTextRight(contents, new PDRectangle(x + 265, y, 200, 25), paymentTotalTitle, BLACK, HELVETICA, fontSize, 5,
		5);
    }

    // DRAW DEDUCTIONS
    private void drawDeductions() throws IOException {

	// DEDUCTIONS CONTENT
	String title = text("DEDUCCIONES").toUpperCase();
	String deductionTotalTitle = "B. " + text("TOTAL DEDUCIR").toUpperCase() + ": ";
	String deductionTotal = toLatinNumber(safeDouble(p.getDeductionTotal())) + " " + text("MONEDA");
	String payrollTotalTitle = text("TOTAL PERCIBIR").toUpperCase() + " (A-B): ";
	String payrollTotal = toLatinNumber(safeDouble(p.getPayrollTotal())) + " " + text("MONEDA");
	String enterpriseSign = text("FIRMA EMPRESA").toUpperCase();
	String employeeSign = formatDate(new Date(), text("FIRMA TRABAJADOR")).get();

	x = 23.5f;

	// BUILD DEDUCTIONS
	Optional<Map<Integer, ArrayList<PDFDeduction>>> deductions = p.getDeductions();
	drawText(contents, title, x, y - 20, BLACK, HELVETICA_BOLD, fontSize + 3);
	y -= 40;

	// FOR EACH DEDUCTION
	deductions.get().entrySet().stream().sorted(Map.Entry.<Integer, ArrayList<PDFDeduction>>comparingByKey())
		.forEach(m -> {
		    try {

			double localTotal = m.getValue().stream()
				.mapToDouble(accrual -> safeDouble(accrual.getAmount())).sum();
			String deductionTxt = /* m.getKey() + */"- " + getType(m.getKey());
			String deductionTotalTxt = toLatinNumber(localTotal) + " " + text("MONEDA");

			drawText(contents, deductionTxt, x, y, BLACK, HELVETICA_BOLD, fontSize);
			drawTextRight(contents, new PDRectangle(x + 355, y - 5, 100, 10), deductionTotalTxt, BLACK,
				HELVETICA, fontSize, 2, 5);
			drawBox(contents, x, y - 2, 455, .2f, BLACK);

			y -= 15;

			if (localTotal != 0)
			    m.getValue().stream()
			    .sorted((d1,d2) -> { 
			    	int compareTo = d1.getDeductionType().orElse(DeductionType.OTHER).compareTo(d2.getDeductionType().orElse(DeductionType.OTHER));
			    	if ( compareTo == 0 )
			    		compareTo = d1.getName().orElse("").compareTo(d2.getName().orElse(""));
			    	return compareTo;
			    })
			    .forEach(n -> {
				String entryValue = toLatinNumber(n.getAmount().orElse(null)) + " " + text("MONEDA");
				String entryPercent = (n.getPercent().isEmpty()) ? ""
					: toLatinNumber(n.getPercent().get()) + " % ";
				String description = getDescription(n);
				String entryTxt = n.getPercent().map( p -> " por ").orElse("") + description.replaceAll("<.*>", "");
				
				
				if (n.getAmount().isPresent() && n.getAmount().get() != 0) {
				    PdfText quantity = new PdfText(x, y, 60, 15, contents, entryPercent, BLACK,
					    HELVETICA, fontSize, RIGHT);
				    quantity.draw();


					if ( n.getDeductionType().orElse(null) == DeductionType.SOLIDARITY ) {
					    PdfText t2 = new PdfText(x + 64, y, 210, 15, contents, entryTxt, BLACK, HELVETICA,
							    fontSize, LEFT);
						    t2.drawCroppableLine();
						String entryBase = (n.getBase().isEmpty()) ? ""
								: toLatinNumber(n.getBase().get()) + " " + text("MONEDA");
					    PdfText base = new PdfText(x + 64 + 210 , y, 60, 15, contents, entryBase, BLACK,
							    HELVETICA, fontSize, RIGHT);
						    base.draw();
					} else {
					    PdfText t2 = new PdfText(x + 64, y, 270, 15, contents, entryTxt, BLACK, HELVETICA,
							    fontSize, LEFT);
						    t2.drawCroppableLine();
					}

					PdfText t3 = new PdfText(x + 64 + 270, y, 60, 15, contents, entryValue, BLACK,
					    HELVETICA, fontSize, RIGHT);
				    t3.draw();

				    y -= 10;
				}
			    });
		    } catch (IOException e) {
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

	if (logo.isPresent()) {
	    byte[] bytes = logo.get().readAllBytes();
	    PdfImage img = new PdfImage(x + 20, y - 60, 170, 70, ALIGNMENT.CENTER, contents, doc, bytes);
	    try {
		img.scale(170, 70, LEFT).draw();
	    } catch (Exception e) {
	    }
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

    public void drawFooter() throws IOException {

	Optional<ContingencyBases> contigencies = p.getContingencies();
	if (contigencies.isPresent()) {
	    y = 177;
	    x = 25;

	    final String title = text("TITULO PIE");
	    final String title2 = text("TITULO PIE 2");
	    final String apEnt = text("AP EMPRESA").toUpperCase();
	    final String type = text("TIPO").toUpperCase();
	    final String base = text("BASE").toUpperCase();

	    final String commonContingenciesTitle = text("CONTINGENCIAS COMUNES");
	    final String monthlyAmmountTitle = text("IMPORTE DE REMUNERACION MENSUAL");
	    final String extraHourProrrationTitle = text("IMPORTE PRORRATA DE PAGA EXTRAORDINARIA");

	    final String meiTitle = text("MECANISMO DE EQUIDAD INTERGENERACIONAL");

	    final String profContingenciesTitle = "3. "
		    + text("CONTINGENCIAS PROFESIONALES Y CONCEPTOS DE RECAUDACION CONJUNTA");
	    final String atEpTitle = text("AT Y EP");
	    final String unemploymentTitle = text("DESEMPLEO");
	    final String profesFormTitle = text("FORMACION PROFESIONAL");
	    final String fogasaTitle = text("FONDO DE GARANTIA SALARIAL");
	    
		final String		   solidarityTitle		   = text("SOLIDARIDAD");

	    final String extraHoursTitle = text("COTIZACION ADICIONAL POR HORAS EXTRAS");
	    final String forceMajeureTitle = text("FUERZA MAYOR O");
	    final String noStructTitle = text("NO ESTRUCTURALES");
	    String irpfTitle = text("BASE SUJETA A RETENCION IRPF") + " ";
	    final String totalContingenciesTitle = text("TOTAL APORTACIONES");
	    final ContingencyBases conts = contigencies.get();

	    final String totalCostsTitle = text("TOTAL COSTES");
	    final String totalCostsAmount = ""
		    + toLatinNumber(p.getPaymentsTotal().orElse(0d) + conts.getTotal().orElse(0d)) + " "
		    + text("MONEDA");

	    final String monthlyAmmount = toLatinNumber(safeDouble(conts.getMonthlyAmount())) + " " + text("MONEDA");
	    final String commContBase = toLatinNumber(safeDouble(conts.getCommonContBase())) + " " + text("MONEDA");
	    String commContType = drawCostPercentage(conts.getCommonContType());

	    if (commContType.contains("-1"))
		commContType = "";

	    final String commContApEnt = toLatinNumber(safeDouble(conts.getCommonContApEnterprise())) + " "
		    + text("MONEDA");
	    final String extraProrrationAmount = toLatinNumber(safeDouble(conts.getExtraProrationAmount())) + " "
		    + text("MONEDA");
	    String meiType = drawCostPercentage(conts.getMeiType());

	    if (meiType.contains("-1"))
		meiType = "";

	    final String meiApEnt = toLatinNumber(safeDouble(conts.getMeiApEnterprise())) + " " + text("MONEDA");

	    final String profContingenciesBase = toLatinNumber(safeDouble(conts.getProfessionalContBase())) + " "
		    + text("MONEDA");
	    String atEpType = drawCostPercentage(conts.getAtEpType());

	    if (atEpType.contains("-1"))
		atEpType = "";

	    final String atEpApEnt = toLatinNumber(safeDouble(conts.getAtEpApEnterprise())) + " " + text("MONEDA");
	    String unemploymentType = drawCostPercentage(conts.getUnemploymentType());

	    if (unemploymentType.contains("-1"))
		unemploymentType = "";

	    final String unemploymentApEnt = toLatinNumber(safeDouble(conts.getUnemploymentApEnterprise())) + " "
		    + text("MONEDA");
	    final String profesFormType = toLatinNumber(safeDouble(conts.getProfesFormType())) + " %";
	    final String profesFormApEnt = toLatinNumber(safeDouble(conts.getProfesFormApEnterprise())) + " "
		    + text("MONEDA");

	    String fogasaType = drawCostPercentage(conts.getFogasaType());
	    if (fogasaType.contains("-1"))
		fogasaType = "";

	    String fogasaApEnt = toLatinNumber(safeDouble(conts.getFogasaApEnterprise())) + " " + text("MONEDA");
	    String forceMajeureBase = toLatinNumber(safeDouble(conts.getForceMajeureBase())) + " " + text("MONEDA");

	    String forceMajeureType = drawCostPercentage(conts.getForceMajeureType());
	    if (forceMajeureType.contains("-1"))
		forceMajeureType = "";

	    String forceMajeureApEnt = toLatinNumber(safeDouble(conts.getForceMajeureApEnterprise())) + " "
		    + text("MONEDA");
	    String noStructBase = toLatinNumber(safeDouble(conts.getNoStructBase())) + " " + text("MONEDA");

	    String noStructType = drawCostPercentage(conts.getNoStructType());
	    if (noStructType.contains("-1"))
		noStructType = "";

	    String noStructApEnt = toLatinNumber(safeDouble(conts.getNoStructApEnterprise())) + " " + text("MONEDA");
	    String totalIrpf = toLatinNumber(safeDouble(conts.getIrpfEsp()) + conts.getIrpfRetribDiner().orElse(0.00))
		    + " " + text("MONEDA");
	    String totalContingenciesAmount = toLatinNumber(safeDouble(conts.getTotal())) + " " + text("MONEDA");

	    Double irpfEsp = safeDouble(conts.getIrpfEsp());
	    Double irpfRetDin = safeDouble(conts.getIrpfRetribDiner());

	    if (irpfEsp != 0)
		irpfTitle += toLatinNumber(irpfEsp) + " " + text("MONEDA") + " " + text("EN ESPECIE") + " ";
	    if (irpfRetDin != 0)
		irpfTitle += toLatinNumber(irpfRetDin) + " " + text("MONEDA") + " "
			+ text("EN RETRIBUCIONES DINERARIAS");

	    drawBorderedBox(contents, 10, 10, 575, 182, LIGHT_GRAY);
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

	    int index = 1;
	    
	    drawText(contents, index++ + ". " + commonContingenciesTitle, x, y, BLACK, HELVETICA_BOLD, fontSize - 1);

	    y -= 9;
	    x += 15;
	    drawText(contents, monthlyAmmountTitle, x, y, BLACK, HELVETICA, fontSize - 3);
	    drawTextRight(contents, new PDRectangle(x + 215, y, 70, 70), monthlyAmmount, BLACK, HELVETICA, fontSize - 3,
		    5, 1);

	    drawTextRight(contents, new PDRectangle(x + 322, y - 5, 70, 70), commContBase, BLACK, HELVETICA,
		    fontSize - 3, 5, 1);
	    drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), commContType, BLACK, HELVETICA,
		    fontSize - 3, 5, 1);
	    drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), commContApEnt, BLACK, HELVETICA,
		    fontSize - 3, 5, 1);

	    y -= 8;
	    drawText(contents, extraHourProrrationTitle, x, y, BLACK, HELVETICA, fontSize - 3);
	    drawTextRight(contents, new PDRectangle(x + 215, y, 70, 70), extraProrrationAmount, BLACK, HELVETICA,
		    fontSize - 3, 5, 1);

	    y -= 12;
	    drawText(contents, index++ + ". " + meiTitle, x - 15, y, BLACK, HELVETICA_BOLD, fontSize - 1 );
	    drawTextRight(contents, new PDRectangle(x + 322, y, 70, 70), commContBase, BLACK, HELVETICA, fontSize - 3,
		    5, 1);
	    drawTextRight(contents, new PDRectangle(x + 396, y, 70, 70), meiType, BLACK, HELVETICA, fontSize - 3, 5, 1);
	    drawTextRight(contents, new PDRectangle(x + 465, y, 70, 70), meiApEnt, BLACK, HELVETICA, fontSize - 3, 5,
		    1);

	    y -= 12;
	    drawText(contents, index++ + ". " + profContingenciesTitle, x - 15, y, BLACK, HELVETICA_BOLD, fontSize -1 );

	    y -= 10;
	    drawText(contents, atEpTitle, x, y, BLACK, HELVETICA, fontSize - 3);
	    drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), atEpType, BLACK, HELVETICA, fontSize - 3,
		    5, 1);
	    drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), atEpApEnt, BLACK, HELVETICA, fontSize - 3,
		    5, 1);

	    y -= 8;
	    drawText(contents, unemploymentTitle, x, y, BLACK, HELVETICA, fontSize - 3);
	    drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), unemploymentType, BLACK, HELVETICA,
		    fontSize - 3, 5, 1);
	    drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), unemploymentApEnt, BLACK, HELVETICA,
		    fontSize - 3, 5, 1);

	    y -= 8;
	    drawTextRight(contents, new PDRectangle(x + 322, y, 70, 70), profContingenciesBase, BLACK, HELVETICA,
		    fontSize - 3, 5, 1);
	    drawText(contents, profesFormTitle, x, y, BLACK, HELVETICA, fontSize - 3);
	    drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), profesFormType, BLACK, HELVETICA,
		    fontSize - 3, 5, 1);
	    drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), profesFormApEnt, BLACK, HELVETICA,
		    fontSize - 3, 5, 1);

	    y -= 8;
	    drawText(contents, fogasaTitle, x, y, BLACK, HELVETICA, fontSize - 3);
	    drawTextRight(contents, new PDRectangle(x + 396, y - 5, 70, 70), fogasaType, BLACK, HELVETICA, fontSize - 3,
		    5, 1);
	    drawTextRight(contents, new PDRectangle(x + 465, y - 5, 70, 70), fogasaApEnt, BLACK, HELVETICA,
		    fontSize - 3, 5, 1);

		List<PDFDeduction> solidarityCosts = getDeductionsByType(p.getCosts(), DeductionType.SOLIDARITY);
		double extraHours = conts.getForceMajeureApEnterprise().orElse(0d) + conts.getNoStructApEnterprise().orElse(0d);
		
		if ( extraHours > 0d || solidarityCosts.isEmpty()  ) {
		    y -= 12;
		    drawText(contents, index++ + ". " + extraHoursTitle, x - 15, y, BLACK, HELVETICA_BOLD, fontSize - 1);
	
		    y -= 10;
		    drawText(contents, forceMajeureTitle, x, y, BLACK, HELVETICA, fontSize - 3);
		    drawTextRight(contents, new PDRectangle(x + 322, y - 2, 70, 70), forceMajeureBase, BLACK, HELVETICA,
			    fontSize - 3, 5, 1);
		    drawTextRight(contents, new PDRectangle(x + 396, y - 2, 70, 70), forceMajeureType, BLACK, HELVETICA,
			    fontSize - 3, 5, 1);
		    drawTextRight(contents, new PDRectangle(x + 465, y - 2, 70, 70), forceMajeureApEnt, BLACK, HELVETICA,
			    fontSize - 3, 5, 1);
	
		    y -= 8;
		    drawText(contents, noStructTitle, x, y, BLACK, HELVETICA, fontSize - 3);
		    drawTextRight(contents, new PDRectangle(x + 322, y - 2, 70, 70), noStructBase, BLACK, HELVETICA,
			    fontSize - 3, 5, 1);
		    drawTextRight(contents, new PDRectangle(x + 396, y - 2, 70, 70), noStructType, BLACK, HELVETICA,
			    fontSize - 3, 5, 1);
		    drawTextRight(contents, new PDRectangle(x + 465, y - 2, 70, 70), noStructApEnt, BLACK, HELVETICA,
			    fontSize - 3, 5, 1);
		}
		
		if ( !solidarityCosts.isEmpty()  ) {
			y -= 12;
			drawText(contents, index++ + ". " + solidarityTitle, x - 15, y, BLACK, HELVETICA_BOLD, fontSize - 1);
			solidarityCosts = solidarityCosts.stream()
					.sorted((c1, c2) -> c2.getName().orElse("").compareTo(c1.getName().orElse(""))).toList();
			for ( PDFDeduction solidarityCost : solidarityCosts ) {
				y -= 8;
				drawText(contents, solidarityCost.getDescription().orElse(""), x, y, BLACK, HELVETICA, fontSize - 3);
				drawTextRight(contents, new PDRectangle(x + 322, y - 2, 70, 70), solidarityCost.getBase().map( b -> toLatinNumber(b) + " " + text("MONEDA")).orElse("")  , BLACK, HELVETICA,
						fontSize - 3, 5, 1);
				drawTextRight(contents, new PDRectangle(x + 396, y - 2, 70, 70), solidarityCost.getPercent().map( b -> toLatinNumber(b) + " %").orElse(""), BLACK, HELVETICA,
						fontSize - 3, 5, 1);
				drawTextRight(contents, new PDRectangle(x + 465, y - 2, 70, 70), solidarityCost.getAmount().map( b -> toLatinNumber(b) + " " + text("MONEDA")).orElse("") , BLACK, HELVETICA,
						fontSize - 3, 5, 1);
			}
		}
		
	    y -= 12;
	    x -= 15;
	    drawText(contents, index + ". " +irpfTitle, x, y, BLACK, HELVETICA_BOLD, fontSize - 1);
	    drawBox(contents, x, y - 2, 405, .2f, BLACK);
	    drawTextRight(contents, new PDRectangle(x + 308, y - 5, 100, 10), totalIrpf, BLACK, HELVETICA, fontSize - 3,
		    5, 5);
	    drawTextRight(contents, new PDRectangle(x + 451, y - 5, 30, 10), totalContingenciesTitle, BLACK,
		    HELVETICA_BOLD, 6.5f, 5, 5);
	    drawTextRight(contents, new PDRectangle(x + 518, y - 5, 30, 10), totalContingenciesAmount, BLACK,
		    HELVETICA_BOLD, 6.5f, 3, 5);

	    if (p.getImpressionType() == IMPRESION.DRAFT) {
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
	return !Objects.equals(key, IPayrollTemplate.INFO)
		&& !Objects.equals(key, IPayrollTemplate.NOTE) 
		&& !Objects.equals(key, IPayrollTemplate.WARNING) ;	
    }
    
	private static String getDescription(PDFDeduction deduction) {
		String deductionName = deduction.getName().orElse("");
		if (AonStringUtils.isBlank(deductionName))
			return deduction.getDescription().orElse("");
		switch (deductionName) {
		case "SOLIDARIDAD_I", "SOLIDARIDAD_I_E":
			return "Solidaridad Primer tramo (hasta el 10%)";
		case "SOLIDARIDAD_II", "SOLIDARIDAD_II_E":
			return "Solidaridad Segundo tramo (del 10% al 50%)";
		case "SOLIDARIDAD_III", "SOLIDARIDAD_III_E":
			return "Solidaridad Tercer tramo (superior al 50%)";
		default:
			return deduction.getDescription().orElse("");
		}
	}
    

}
