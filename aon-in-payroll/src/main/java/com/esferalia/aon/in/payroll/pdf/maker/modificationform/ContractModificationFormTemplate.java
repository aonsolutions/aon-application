package com.esferalia.aon.in.payroll.pdf.maker.modificationform;

import static com.esferalia.aon.watson.util.AonStringUtils.trimToEmpty;
import static com.esferalia.aon.watson.util.AonStringUtils.upperCase;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.form.bean.ModificationForm;
import com.esferalia.aon.in.payroll.pdf.maker.form.bean.ModificationForm.ClientData;
import com.esferalia.aon.in.payroll.pdf.maker.form.bean.ModificationForm.CompanyData;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ContractModificationFormTemplate implements AutoCloseable {
	private final PDFont DEFAULT_FONT = PdfFonts.helvetica();
	private final PDFont DEFAULT_BOLD_FONT = PdfFonts.helveticaBold();
	private static final float TITLE_FONT_SIZE = 12f;
	private static final float DATA_FONT_SIZE = 11f;
	private static final float MARGIN_RIGHT = 55;
	private static final float MARGIN_LEFT = 65;
	private static final float MARGIN_LEFT_INDENT = 80;
	private static final float SIGNATURE_Y = 115;
	
	private PDDocument document;
	private PDPage page;
	private PDPageContentStream contents;
	private ModificationForm modificationForm;
	//----- COORDENADAS -----
	private float x;
	private float y;
	//-----------------------
	
	//-------- FORM ---------
	PDAcroForm form;
	//-----------------------
	
	//x -> 595, y -> 841
	public ContractModificationFormTemplate(ModificationForm modificationForm) throws CanNotCreatePdfException {
		if (modificationForm == null) {
			throw new CanNotCreatePdfException("No form found");
		}
		try {
			this.document = new PDDocument();
			this.modificationForm = modificationForm;
			this.page = PDFToolkit.createVerticalPage();
			document.addPage(this.page);
			this.contents = new PDPageContentStream(this.document, this.page);
			this.x = 0;
			this.y = 841;
			
			this.form = new PDAcroForm(this.document);
			this.document.getDocumentCatalog().setAcroForm(form);
			PDResources resources = new PDResources();
			resources.put(COSName.getPDFName("DEFAULT_FONT"), DEFAULT_FONT);
			resources.put(COSName.getPDFName("DEFAULT_BOLD_FONT"), DEFAULT_BOLD_FONT);
			form.setDefaultResources(resources);
			
			//DEFAULTS
			if (AonStringUtils.isBlank(modificationForm.getModificationTitle())) {
				modificationForm.setModificationTitle("NOTIFICACIÓN LABORAL");
			}
			if (modificationForm.getModificationDate() == null) {
				modificationForm.setModificationDate(new Date());
			}
			//--------
			
			drawHeader();
			this.y -= 35;
			drawCompanyInfo();
			this.y -= 25;
			drawClientInfo();
			this.y -= 25;
			drawDeclaration();
			this.y -= 10;
			drawClauses();
			drawSignature();
			
			contents.close();
		} catch (IOException e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	public void print(OutputStream os) throws CanNotCreatePdfException {
		try {
			document.save(os);
			new OutputStreamWriter(os,StandardCharsets.ISO_8859_1);
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}

	private void drawHeader() throws IOException {
		this.x = MARGIN_LEFT;
		this.y -= 65;
		drawLogo();
		
		this.y -= 40;
		drawTitle();
	}
	// ----- HEADER ELEMENTS -----
	private void drawLogo() throws IOException {
		if (this.modificationForm.getCompanyData() != null) {
			CompanyData companyData = this.modificationForm.getCompanyData();
			if (companyData != null) {
				float finalWidth = PDFToolkit.getLogoFinalWidth(companyData.getLogo(), 60, 180);
				float logoStartX = getPageWidth() / 2 - finalWidth/2;
				PDFToolkit.drawResizedLogo(document, page, contents, companyData.getLogo(), logoStartX, y, 60, 180, null);
			}
		}
	}
	private void drawTitle() throws IOException {
		if (this.modificationForm.getModificationTitle() != null) {
			String modificationTitle = upperCase(trimToEmpty(this.modificationForm.getModificationTitle()));
			
			if (AonStringUtils.isNotBlank(modificationTitle)) {
				modificationTitle = modificationTitle
						.replace("\u0080", "\u20AC")
						.replace("\uFFFD", "?")
						;
			}
			
			PDTextField textField = new PDTextField(this.form);
			textField.setPartialName("TitleField");
			String defaultAppearance = "/DEFAULT_BOLD_FONT " + TITLE_FONT_SIZE + " Tf 0 g";
			textField.setDefaultAppearance(defaultAppearance);
			textField.setQ(PDTextField.QUADDING_CENTERED);
			
			this.form.getFields().add(textField);
			
			PDAnnotationWidget widget = textField.getWidgets().get(0);
			PDRectangle rect = new PDRectangle(MARGIN_LEFT, this.y, getPageWidth() - MARGIN_LEFT * 2, TITLE_FONT_SIZE + 4);
			widget.setRectangle(rect);
			widget.setPage(this.page);
			
			widget.setPrinted(true);
			page.getAnnotations().add(widget);
			textField.setValue(modificationTitle);
			
//			PDFToolkit.drawTextCenter(contents,
//					new PDRectangle(MARGIN_LEFT, this.y, getPageWidth() - MARGIN_LEFT * 2, TITLE_FONT_SIZE),
//					modificationTitle,
//					Color.BLACK,
//					DEFAULT_BOLD_FONT,
//					TITLE_FONT_SIZE,
//					0);
		}
	}
	// ---------------------------
	private void drawOrBlank(String text, float blankSpace, PDFont font) throws IOException {
		String finalText = trimToEmpty(text);
		float textWidth = PDFToolkit.fontWidth(finalText, DATA_FONT_SIZE, font);
		if (!AonStringUtils.isBlank(text)) {
			PDFToolkit.drawText(contents, finalText, x, y, Color.BLACK, font, DATA_FONT_SIZE);
			x += textWidth;
		} else {
			x += blankSpace;
		}
	}
	
	private void drawCompanyInfo() throws IOException {
		final float blankSpace = 80;
		final float lineSeparation = 20f;
		//----- COMPANY HEADER -----
		this.x = MARGIN_LEFT;
		PDFToolkit.drawText(contents, "DATOS DE LA EMPRESA:", x, y, Color.BLACK, DEFAULT_FONT, TITLE_FONT_SIZE);		
		//--------------------------
		this.x = MARGIN_LEFT_INDENT;
		this.y -= lineSeparation;
		if (this.modificationForm.getCompanyData() != null) {
			CompanyData companyData = this.modificationForm.getCompanyData();
			//----- NAME -----
			drawOrBlank(upperCase(companyData.getName()), blankSpace, DEFAULT_BOLD_FONT);
			//----------------
			this.y -= lineSeparation - 5;
			this.x = MARGIN_LEFT_INDENT;
			//----- DOCUMENT -----			
			PDFToolkit.drawText(contents, "con CIF nº:", x, y, Color.BLACK, DEFAULT_FONT, TITLE_FONT_SIZE);
			this.x += 65;
			drawOrBlank(upperCase(companyData.getDocument()), blankSpace, DEFAULT_FONT);
//			//--------------------			
//			//----- CCC -----
			this.x += 5;
			PDFToolkit.drawText(contents, "y CCC nº:", x, y, Color.BLACK, DEFAULT_FONT, TITLE_FONT_SIZE);
			this.x += 57;
			drawOrBlank(upperCase(companyData.getCcc()), blankSpace, DEFAULT_FONT);
//			//---------------
			this.x += 1;
			PDFToolkit.drawText(contents, ", domiciliada en:", x, y, Color.BLACK, DEFAULT_FONT, TITLE_FONT_SIZE);
//			drawCompanyField("CIF:", CompanyData::getDocument);
//			this.x += 100;
//	//		this.y -= lineSeparation;
//			drawCompanyField("CCC:", CompanyData::getCcc);
		}
		this.x = MARGIN_LEFT_INDENT;
		this.y -= lineSeparation - 5;
		//----- ADDRESS & LOCATION -----
		drawAddress();
		//------------------------------
	}
	
	//----- COMPANY INFO ELEMENTS -----
	
	@FunctionalInterface
	private static interface CompanyFieldValueCallback<T> {
		public T getValue(ModificationForm.CompanyData companyData);
	}
	
//	private void drawCompanyInfoValue(float titleWidth, CompanyFieldValueCallback<String> callback) throws IOException {
//		if (this.modificationForm.getCompanyData() != null) {
//			String name = trimToEmpty(upperCase(callback.getValue(this.modificationForm.getCompanyData())));
//			PDFToolkit.drawText(contents, " " + name, x + titleWidth, y, Color.BLACK, DEFAULT_FONT, DATA_FONT_SIZE);
//		}
//	}
	
//	private void drawCompanyField(String fieldName, CompanyFieldValueCallback<String> callback) throws IOException {
//		String fieldTitle = trimToEmpty(fieldName);
//		float fieldTitleWidth = PDFToolkit.fontWidth(fieldTitle, DATA_FONT_SIZE, DEFAULT_BOLD_FONT);		
//		PDFToolkit.drawText(contents, fieldTitle, x, y, Color.BLACK, DEFAULT_FONT, DATA_FONT_SIZE);
//		drawCompanyInfoValue(fieldTitleWidth, callback);
//	}
	
	private void drawAddress() throws IOException {
		final float lineSeparation = 15f;
		
//		String addressTitle = "DOMICILIO:";
//		float addressTitleWidth = PDFToolkit.fontWidth(addressTitle, DATA_FONT_SIZE, DEFAULT_BOLD_FONT);		
//		PDFToolkit.drawText(contents, addressTitle, x, y, Color.BLACK, DEFAULT_BOLD_FONT, DATA_FONT_SIZE);
//		this.x += addressTitleWidth + 10;
		if (this.modificationForm.getCompanyData() != null && this.modificationForm.getCompanyData().getAddress() != null) {
			RegistryAddress rAddress = this.modificationForm.getCompanyData().getAddress();
			String address = trimToEmpty(rAddress.getFullAddress());
			float addressMargin = this.x;
			List<String> addressLines = PDFToolkit.getLines(address, getPageWidth() - this.x - MARGIN_LEFT_INDENT, DEFAULT_FONT, DATA_FONT_SIZE);
			for (String addressLine : addressLines) {
				PDFToolkit.drawText(contents, trimToEmpty(addressLine), x, y, Color.BLACK, DEFAULT_FONT, DATA_FONT_SIZE);				
				if (addressLines.indexOf(addressLine) < addressLines.size() - 1)
					this.y -= DATA_FONT_SIZE;
			}
			
			this.y -= lineSeparation;
			drawLocation(addressMargin);
		}
		
	}
	
	private void drawLocation(float margin) throws IOException {
		if (this.modificationForm.getCompanyData() != null && this.modificationForm.getCompanyData().getAddress() != null) {
			RegistryAddress rAddress = this.modificationForm.getCompanyData().getAddress();
			//----- ZIP CODE -----
			String zipCodeStr = trimToEmpty(upperCase(rAddress.getZip()));
			if (!AonStringUtils.isEmpty(zipCodeStr)) {				
				drawLocationElement(zipCodeStr, margin);
				x += 5;
			}
			//--------------------
			//----- CITY -----
			String cityStr = trimToEmpty(upperCase(rAddress.getCity()));
			if (!AonStringUtils.isEmpty(cityStr)) {				
				drawLocationElement(cityStr, margin);
				x += 5;
			}
			//----------------
			//----- PROVINCE -----
			if (!AonStringUtils.equalsIgnoreCase(rAddress.getCity(), rAddress.getProvince())) {
				String provinceStr = "(" + trimToEmpty(upperCase(rAddress.getProvince())) + ")";
				if (!AonStringUtils.isEmpty(provinceStr)) {					
					drawLocationElement(provinceStr, margin);
					x += 5;
				}
			}
			//--------------------
		}
	}
	
	private void drawLocationElement(String text, float margin) throws IOException {
		if (!AonStringUtils.isBlank(text)) {
			float textWidth = PDFToolkit.fontWidth(text, DATA_FONT_SIZE, DEFAULT_FONT);
			if (this.x + textWidth > getPageWidth() - MARGIN_LEFT_INDENT) {
				this.x = margin;
				this.y -= DATA_FONT_SIZE + 2;
			}
			PDFToolkit.drawText(contents, text, this.x, y, Color.BLACK, DEFAULT_FONT, DATA_FONT_SIZE);
			this.x += textWidth;
		}
	}
	
	//---------------------------------
	
	private void drawClientInfo() throws IOException {
		final float blankSpace = 80;		
		final float lineSeparation = 20f;
		//----- COMPANY HEADER -----
		this.x = MARGIN_LEFT;
		PDFToolkit.drawText(contents, "DATOS DEL TRABAJADOR:", x, y, Color.BLACK, DEFAULT_FONT, TITLE_FONT_SIZE);		
		//--------------------------
		this.x = MARGIN_LEFT_INDENT;
		this.y -= lineSeparation;
		if (this.modificationForm.getClientData() != null) {
			ClientData employeeData = this.modificationForm.getClientData();
			if (!AonStringUtils.isBlank(employeeData.getName())) {
				PDFToolkit.drawText(contents, trimToEmpty(upperCase(employeeData.getName())), x, y, Color.BLACK, DEFAULT_BOLD_FONT, DATA_FONT_SIZE);
			}
			
			this.y -= lineSeparation - 5;
			PDFToolkit.drawText(contents, "con NIF nº:", x, y, Color.BLACK, DEFAULT_FONT, TITLE_FONT_SIZE);
			this.x += 62;
			drawOrBlank(upperCase(employeeData.getDocument()), blankSpace, DEFAULT_FONT);
			this.x += 5;
			PDFToolkit.drawText(contents, "y NAF nº:", x, y, Color.BLACK, DEFAULT_FONT, TITLE_FONT_SIZE);
			this.x += 56;
			drawOrBlank(upperCase(employeeData.getSocialSecurityNum()), blankSpace, DEFAULT_FONT);
			
		}
//		drawEmployeeField("NOMBRE:" , ClientData::getName);
//		this.y -= lineSeparation;
//		this.x = MARGIN_LEFT_INDENT;
//		drawEmployeeField("Fecha de antigüedad:", cd -> AonDateUtils.format(cd.getSeniorityDate(), AonDateUtils.SIMPLE_DATE_FORMAT));
		
	}
	
	//----- CLIENT INFO ELEMENTS -----
	
//	@FunctionalInterface
//	private static interface EmployeeFieldValueCallback<T> {
//		public T getValue(ModificationForm.ClientData employeeData);
//	}
	
//	private void drawEmployeeInfoValue(float titleWidth, EmployeeFieldValueCallback<String> callback) throws IOException {
//		if (this.modificationForm.getCompanyData() != null) {
//			String name = trimToEmpty(upperCase(callback.getValue(this.modificationForm.getClientData())));
//			PDFToolkit.drawText(contents, " " + name, x + titleWidth, y, Color.BLACK, DEFAULT_FONT, DATA_FONT_SIZE);
//		}
//	}
	
//	private void drawEmployeeField(String fieldName, EmployeeFieldValueCallback<String> callback) throws IOException {
//		String fieldTitle = trimToEmpty(fieldName);
//		float fieldTitleWidth = PDFToolkit.fontWidth(fieldTitle, DATA_FONT_SIZE, DEFAULT_BOLD_FONT);		
//		PDFToolkit.drawText(contents, fieldTitle, x, y, Color.BLACK, DEFAULT_BOLD_FONT, DATA_FONT_SIZE);
//		drawEmployeeInfoValue(fieldTitleWidth, callback);
//	}
	
//	private void drawEmployeeResponsiveElement(String title, EmployeeFieldValueCallback<String> callback) throws IOException {
//		if (this.modificationForm != null && this.modificationForm.getClientData() != null) {
//			drawResponsiveElement(title, callback.getValue(this.modificationForm.getClientData()), 50, MARGIN_LEFT_INDENT);
//		}
//	}
	//--------------------------------
	
	private void drawDeclaration() throws IOException {
		final float lineSeparation = 15f;
		final float declarationValueFontSize = 10f;

		this.x = MARGIN_LEFT;
//		PDFToolkit.drawTextCenter(contents,
//				new PDRectangle(x, y, getPageWidth() - MARGIN_LEFT *  2, DATA_FONT_SIZE),
//				"INFORMAN",
//				Color.BLACK,
//				DEFAULT_BOLD_FONT,
//				DATA_FONT_SIZE,
//				0);
		this.y -= lineSeparation;
		String dateStr = this.modificationForm.getContractStartDate() != null ? AonDateUtils.format(this.modificationForm.getContractStartDate(), AonDateUtils.SIMPLE_DATE_FORMAT) : "";
		String sepeId = this.modificationForm.getSepeId() != null ? this.modificationForm.getSepeId() : "";
		String dateComunicationStr = !AonStringUtils.isBlank(dateStr) ? String.format(" con fecha %s", dateStr) : "";
		String sepeIdStr = !AonStringUtils.isBlank(sepeId) ? String.format(" con el identificador %s", sepeId) : "";
		
		ModificationForm form = this.modificationForm;
		
		String declatationText = "La empresa procede a comunicar al trabajador mediante el presente escrito de " + form.getModificationTitle()
				+ " que afecta al contrato de trabajo suscrito " + dateComunicationStr
				+(AonStringUtils.isNotBlank(dateComunicationStr) ? " y " : "")
				+ "que fue comunicado en su día al Servicio Público de Empleo"
				+ sepeIdStr + ", todo ello con arreglo a la siguiente";
		
		
		
		String declarationValue = String.format(declatationText, dateStr, sepeId);
		
		List<String> declarationLines = PDFToolkit.getLinesRespectOriginal(declarationValue, getPageWidth() - MARGIN_LEFT - MARGIN_RIGHT, DEFAULT_FONT, declarationValueFontSize);
		
		PDFToolkit.drawTextWellJustified(declarationLines, getPageWidth() - MARGIN_LEFT_INDENT * 2, declarationValueFontSize, DEFAULT_FONT, this.x, this.y, Color.BLACK, contents, declarationValueFontSize + 2);
		
		this.y -= declarationLines.size() * (declarationValueFontSize + 2);
		
	}
	private void drawClauses() throws IOException {
		final float lineSeparation = 15f;
		final float clausesValueFontSize = 10f;
		
		PDFToolkit.drawTextCenter(contents,
				new PDRectangle(x, y, getPageWidth() - MARGIN_LEFT *  2, DATA_FONT_SIZE),
				"INFORMACIÓN",
				Color.BLACK,
				DEFAULT_BOLD_FONT,
				DATA_FONT_SIZE,
				0);
		
		y -= lineSeparation /*+ 10*/;
		
		
		String clauses = this.modificationForm.getClauses() != null ? this.modificationForm.getClauses() : "";
		if (AonStringUtils.isNotBlank(clauses)) {
			clauses = clauses.replace("\u0080", "\u20AC");
		}

		float clausesHeight = y - SIGNATURE_Y - DATA_FONT_SIZE;
		float clausesMaxWidth = getPageWidth() - MARGIN_LEFT - MARGIN_RIGHT;
		
		PDTextField textField = new PDTextField(this.form);
		textField.setPartialName("ClauseField");
		String defaultAppearance = "/DEFAULT_FONT " + clausesValueFontSize + " Tf 0 g";
		textField.setDefaultAppearance(defaultAppearance);
		textField.setQ(PDTextField.QUADDING_LEFT);
		textField.setMultiline(true);
		textField.setRichText(true);
		this.form.getFields().add(textField);
		PDAnnotationWidget widget = textField.getWidgets().get(0);
		PDRectangle rect = new PDRectangle(MARGIN_LEFT, this.y, clausesMaxWidth, -clausesHeight);
		widget.setRectangle(rect);
		widget.setPage(this.page);
		widget.setPrinted(true);
		page.getAnnotations().add(widget);
		
		textField.setValue(clauses);
		
//		List<String> lines = PDFToolkit.getLinesRespectOriginal(clauses, clausesMaxWidth, DEFAULT_FONT, clausesValueFontSize);
//		
//		for (String line : lines) {
//			PDFToolkit.drawText(contents, line, MARGIN_LEFT, y, Color.BLACK, DEFAULT_FONT, clausesValueFontSize);
//			this.y -= clausesValueFontSize + 2;
//		}
		
	}
	private void drawSignature() throws IOException {
		this.y = SIGNATURE_Y;
		StringBuilder dateStrBuilder = new StringBuilder();
		String signatureStr = "Como prueba de conformidad, lo firman en";
		float signatureStrWidth = PDFToolkit.fontWidth(signatureStr, DATA_FONT_SIZE, DEFAULT_FONT);
		String signatureDateStr = "%s a %s de %s de %s";
		
		CompanyData companyData = this.modificationForm.getCompanyData();
		RegistryAddress address = companyData != null ? companyData.getAddress() : null;
		String city = address != null ? address.getCity() : null;
		String location = !AonStringUtils.isBlank(city) ? city : getDots(40);
		
		if (this.modificationForm.getModificationDate() != null) {
			Date modDate = this.modificationForm.getModificationDate();
			dateStrBuilder.append(String.format(signatureDateStr,
					location,
					AonDateUtils.format(modDate, "dd"),
					AonDateUtils.format(modDate, "MMMMMMMMMM"),
					AonDateUtils.format(modDate, "yyyy")));
		} else {
			dateStrBuilder.append(String.format(signatureDateStr, location, getDots(10), getDots(30), getDots(15)));			
		}
		PDFToolkit.drawText(contents, signatureStr, x, y, Color.BLACK, DEFAULT_FONT, DATA_FONT_SIZE);
		this.x += signatureStrWidth + 5;
		
		String dateStr = dateStrBuilder.toString();
		float dateStrWidth = PDFToolkit.fontWidth(dateStr, DATA_FONT_SIZE, DEFAULT_FONT);
		
		if (this.x + dateStrWidth > getPageWidth() - MARGIN_RIGHT) {			
			y-= DATA_FONT_SIZE + 2;
			this.x = MARGIN_LEFT;
		}
		
		PDFToolkit.drawText(contents, dateStrBuilder.toString(), x, y, Color.BLACK, DEFAULT_FONT, DATA_FONT_SIZE);
		this.y -= DATA_FONT_SIZE + 2;
		
		float signatureMargin = MARGIN_LEFT * 1.5f;
		
		this.x = signatureMargin;
		PDFToolkit.drawText(contents, "El trabajador", x, y, Color.BLACK, DEFAULT_FONT, DATA_FONT_SIZE);		
//		this.x += 300;
		String representante = "El representante de la empresa";
		float representanteWidth = PDFToolkit.fontWidth(representante, DATA_FONT_SIZE, DEFAULT_FONT);
		this.x = getPageWidth() - signatureMargin - representanteWidth;		
		PDFToolkit.drawText(contents, representante, x, y, Color.BLACK, DEFAULT_FONT, DATA_FONT_SIZE);
		
		if (companyData != null && companyData.getSignature() != null) {
			float signHeight = PDFToolkit.getLogoFinalHeight(companyData.getSignature(), 60, representanteWidth);
			this.y -= 15 + signHeight;
			PDFToolkit.drawResizedLogo(document, page, contents, companyData.getSignature(), x, y, 60, representanteWidth, null);
		}
	}
	
//	private void drawResponsiveElement(String title, String value, float emptySpace, float margin) throws IOException {
//		final float lineSeparation = 20f;
//		title = trimToEmpty(title);
//		value = trimToEmpty(value);
//		float titleWidth = PDFToolkit.fontWidth(title , DATA_FONT_SIZE, DEFAULT_BOLD_FONT);
//		String finalValue = upperCase(trimToEmpty(value));
//		float valueWidth = PDFToolkit.fontWidth(" " + finalValue, DATA_FONT_SIZE, DEFAULT_BOLD_FONT);
//		float totalWidth = titleWidth + valueWidth;
//		if (this.x + totalWidth > getPageWidth() - MARGIN_LEFT) {
//			this.y -= lineSeparation;
//			this.x = margin;
//		}
//		PDFToolkit.drawText(contents, title , x, y, Color.BLACK, DEFAULT_BOLD_FONT, DATA_FONT_SIZE);
//		if (finalValue != null && !finalValue.isEmpty()) {			
//			PDFToolkit.drawText(contents, " " + finalValue, x + titleWidth, y, Color.BLACK, DEFAULT_FONT, DATA_FONT_SIZE);
//			this.x = x + totalWidth;
//		} else {
//			this.x = x + totalWidth + emptySpace;			
//		}
//	}
	
	private float getPageWidth() {
		if (this.page != null && this.page.getMediaBox() != null) {
			return (float) AonNumberUtils.zeroIfNull( (double) this.page.getMediaBox().getWidth());
		}
		return 0;
	}
	
//	private float getPageHeight() {
//		if (this.page != null && this.page.getMediaBox() != null) {
//			return (float) AonNumberUtils.zeroIfNull( (double) this.page.getMediaBox().getHeight());
//		}
//		return 0;
//	}
	
	private static String getDots (int length) {
		if (length <= 0)
			return "";
		StringBuilder sb = new StringBuilder();
		IntStream.range(0, length).forEach(n -> sb.append('.'));
		return sb.toString();
	}

	@Override
	public void close() throws Exception {
		if (this.document != null) {
			this.document.close();
		}
		
	}
}
