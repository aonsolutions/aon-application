package com.esferalia.aon.in.payroll.tgss.idc;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationHighlight;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationPopup;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class IdcHighlighter {
	
	private PDPage page; 
	private PDDocument doc;
	private List<TextPosition> positions;
	
	private IdcHighlighter(PDPage page, PDDocument doc, List<TextPosition> positions) {
		this.doc = doc;
		this.page = page;
		this.positions = positions;
	}
	
	public void insert(String regex, String str) throws IOException {
		if ( AonStringUtils.isBlank(regex))
			return;
		Pattern pattern = Pattern.compile(regex);
		List<TextPosition> patternPositions = getPositions(pattern, positions);
		insert(doc, page, patternPositions, str);
		addMetaData(patternPositions, str);
	}

	public void highlight(String pattern) throws IOException {
		if ( AonStringUtils.isBlank(pattern))
			return;
		
		List<TextPosition> patternPositions = getPositions(pattern, positions);
		PDRectangle rect = getRectangle(page, patternPositions);
		highlight(doc, page, rect);
		
	}

	public void highlightAll() throws IOException {
		highlightAll("");

	}

	public void highlightAll(String contents) throws IOException {
		PDRectangle rect = getRectangle(page, positions);
		highlight(doc, page, rect );
		
		if ( AonStringUtils.isNotBlank(contents)) {
			annotate(page, rect, contents);
		}

		addMetaData(positions, contents);
	}

	public void annotateAll(String contents) throws IOException {
		
		if ( AonStringUtils.isNotBlank(contents)) {
			PDRectangle rect = getRectangle(page, positions);
			annotate(page, rect, contents);
		}

		addMetaData(positions, contents);
	}

	public void highlight(String pattern, String contents) throws IOException {
		if ( AonStringUtils.isBlank(pattern))
			return;

		List<TextPosition> patternPositions = getPositions(pattern, positions);
		PDRectangle rect = getRectangle(page, patternPositions);
		highlight(doc, page, rect );
		annotate(page, rect, contents);

		addMetaData(patternPositions, contents);
	} 

	public void highlight(String ...strings) throws IOException {
		String regex = Arrays.stream(strings, 0, strings.length-1).collect(Collectors.joining(".*")).replaceAll("\\s", "\\\\s*");
		if ( AonStringUtils.isBlank(regex))
			return;
		Pattern pattern = Pattern.compile(regex);

		List<TextPosition> patternPositions = getPositions(pattern, positions);
		PDRectangle rect = getRectangle(page, patternPositions);
		highlight(doc, page, rect );
		
		String contents = strings[strings.length-1];
		if ( AonStringUtils.isNotBlank(contents)) {
			annotate(page, rect, contents);
		}
		
		addMetaData(patternPositions, contents);
	}

	public void annotate(String ...strings) throws IOException {
		String regex = Arrays.stream(strings, 0, strings.length-1).collect(Collectors.joining(".*")).replaceAll("\\s", "\\\\s*");
		if ( AonStringUtils.isBlank(regex))
			return;
		Pattern pattern = Pattern.compile(regex);

		List<TextPosition> patternPositions = getPositions(pattern, positions);
		PDRectangle rect = getRectangle(page, patternPositions);
		
		String contents = strings[strings.length-1];
		if ( AonStringUtils.isNotBlank(contents)) {
			annotate(page, rect, contents);
		}
		
		addMetaData(patternPositions, contents);
	}

	private void addMetaData(List<TextPosition> positions, String highlight) {
		String text = positions.stream().map(TextPosition::getUnicode).collect(Collectors.joining());
		text = AonStringUtils.remove(text, ' ');
		text = AonStringUtils.substring(text, 0, 15);
		text = AonStringUtils.join("AON", "_", text );
		doc.getDocumentInformation().setCustomMetadataValue(AonStringUtils.upperCase(text), highlight);
	}

//	public void highlight(String pattern1, String pattern2, String contents) throws IOException {
//		String regex = AonStringUtils.join(pattern1 , " ", pattern2).replaceAll("\\s", "\\\\s*");
//		if ( AonStringUtils.isBlank(regex))
//			return;
//		Pattern pattern = Pattern.compile(regex);
//
//		List<TextPosition> patternPositions = getPositions(pattern, positions);
//		PDRectangle rect = getRectangle(page, patternPositions);
//		highlight(doc, page, rect );
//		annotate(page, rect, contents);
//	}

	public static void highlight( File file , File out,  IdcHighlighterListener listener) throws IOException, UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(file)){
			highlight(doc, listener);
			doc.save(out);
		}
	}

 	public static byte [] highlight( byte[] input , IdcHighlighterListener listener) throws IOException , UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(input)){
			highlight(doc, listener);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			doc.save(out);
			return out.toByteArray();
		}
	}

 	public static void highlight( InputStream is , OutputStream os,IdcHighlighterListener listener) throws IOException , UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(is)){
			highlight(doc, listener);
			doc.save(os);
		}
	}
	
	public static void highlight(PDDocument doc, IdcHighlighterListener listener) throws IOException, UnknownPDFException {
       AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent()){
			throw new IOException("You do not have permission to extract text");
		}
		PDFTextStripper stripper = new PDFTextStripper() {
			
			private StringBuilder lineBuilder;
			private List<TextPosition> linePositions;
			
			@Override
			protected void writeString(String string, List<TextPosition> stringPositions) throws IOException {
				
				if ( lineBuilder == null ) {
					lineBuilder = new StringBuilder(string);
					linePositions = new ArrayList<>(stringPositions);
				} else if ( overlap(linePositions, stringPositions)) {
					lineBuilder.append(getWordSeparator());
					lineBuilder.append(string);
					linePositions.addAll(stringPositions);
				} else {
					writeLine(lineBuilder.toString(), linePositions);
					lineBuilder = new StringBuilder(string);
					linePositions = new ArrayList<>(stringPositions);
				}
				
				
			}
			
			protected void writeLine(String line, List<TextPosition> linePositions) throws IOException {

				IdcHighlighter idcHighlighter = new IdcHighlighter(getCurrentPage(), doc, linePositions);
				
				Matcher matcher = IdcParser.EMPLOYEE_NAME.matcher(line);
				if ( matcher.matches() ) {
					listener.onEmployeeName(group(matcher, "name"), idcHighlighter );
					return;
				} 
				
				matcher = IdcParser.EMPLOYEE_NSS_TYPEDOC_DOC_GENDER_BIRTHDATE.matcher(line);
				if ( matcher.matches() ) {
					listener.onEmployeeNaf(group(matcher, "province"), group(matcher, "nss"), idcHighlighter );
					listener.onEmployeeIpf(group(matcher, "docType"), group(matcher, "doc"), idcHighlighter );
					listener.onEmployeeBirthDate(group(matcher, "birthDate"), idcHighlighter );
					return;
				} 

				matcher = IdcParser.ENTERPRISE_NAME_CCC_CIF.matcher(line);
				if ( matcher.matches() ) {
					listener.onEnterpriseName(group(matcher, "name"), idcHighlighter );
					return;
				} 
				
				matcher = IdcParser.ENTERPRISE_ACTIVITY_REGIME.matcher(line);
				if ( matcher.matches() ) {
					listener.onEnterpriseActivity(group(matcher, "code"), group( matcher, "description"), idcHighlighter);
					return;
				} 

				matcher = IdcParser.CONTRACT_TYPE_START_END.matcher(line);
				if ( matcher.matches() ) {
					listener.onContractType(group(matcher, "contractType"), group(matcher, "contractDescription"), idcHighlighter);
					return;
				} 
				
				matcher = IdcParser.CONTRACT_PARTIALCOEF_DATE_AGE.matcher(line);
				if ( matcher.matches() ) {
					listener.onContractStart(group(matcher, "date"), idcHighlighter);
					listener.onCoefficient(group(matcher, "partialCoef"), null, idcHighlighter);
					return;
				} 
				
				matcher = IdcParser.CONTRACT_QUOTEGROUP_INACTIVITY_COMPLETECCC.matcher(line);
				if ( matcher.matches() ) {
					listener.onQuoteGroup(group(matcher, "quoteGroup"), null, idcHighlighter);
					String completeCCC = group(matcher, "completeCCC").replaceAll("\\s+", "");
					listener.onEnterpriseCCC(completeCCC, idcHighlighter);
					
					return;
				} 

				matcher = IdcParser.EMPLOYEE_PERIOD_START.matcher(line);
				if ( matcher.matches() ) {
					listener.onIdcPeriod(group(matcher, "start"), group(matcher, "end"), idcHighlighter);
					return;
				} 
				
				
				matcher = IdcParser.EMPLOYEE_QUOTE_PEC.matcher(line);
				if ( matcher.matches() ) {
					listener.onPEC(group(matcher, "code"), group(matcher, "description"), group(matcher, "tipo"), group(matcher, "quota"), group(matcher, "start"), group(matcher, "end"), idcHighlighter);
					return;
				} 
				
				
				matcher = IdcParser.QUOTATION_TYPES.matcher(line);
				if ( matcher.matches() ) {
					listener.onQuotes(group(matcher, "it"), group(matcher, "ims"), group(matcher, "unemployment"), idcHighlighter);
					return;
				} 
				
			}
			
			private String group(Matcher matcher, String group) {
				return AonStringUtils.defaultString(AonStringUtils.trim(matcher.group(group)), "");
			}
			
			
		};
		
		
		stripper.setSortByPosition(true);
		stripper.setStartPage(0);
		stripper.setEndPage(doc.getNumberOfPages());
		
		Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
		stripper.writeText(doc, dummy);
		
	}
	
    private static boolean within(float first, float second, float variance)
    {
        return second < first + variance && second > first - variance;
    }

    private static boolean overlap(float y1, float height1, float y2, float height2)
    {
        return within(y1, y2, .1f) || y2 <= y1 && y2 >= y1 - height1
                || y1 <= y2 && y1 >= y2 - height2;
    }

    private static boolean overlap(List<TextPosition> textPositions1, List<TextPosition> textPositions2)
    {
    	TextPosition textPosition1 = textPositions1.get(textPositions1.size()-1);
    	TextPosition textPosition2 = textPositions2.get(0);
    	
    	return overlap(textPosition1.getY(), textPosition1.getHeight(), textPosition2.getY(), textPosition2.getHeight());
    }

	private static List<TextPosition> getPositions(String string, List<TextPosition> linePositions) throws IOException {
		String line = linePositions.stream().map(TextPosition::getUnicode).collect(Collectors.joining());
		int fromIndex = AonStringUtils.indexOf(line,string);
		int toIndex = fromIndex + string.length();
		return linePositions.subList(fromIndex, toIndex);
	}
	
	private static List<TextPosition> getPositions(Pattern pattern, List<TextPosition> linePositions) throws IOException {
		String line = linePositions.stream().map(TextPosition::getUnicode).collect(Collectors.joining());
		Matcher matcher = pattern.matcher(line);
		if ( matcher.find() ) {
			return linePositions.subList(matcher.start(), matcher.end());
		} 
		else {
			return Collections.emptyList();
		}
	}

	private static void annotate(PDPage page, PDRectangle position, String contents) throws IOException {
		
		List<PDAnnotation> annotations = page.getAnnotations();
		
        // Now add the markup annotation, a highlight to PDRectangle
        PDAnnotationHighlight txtHighlight = getAnnotationHighlight(position);
        
        txtHighlight.setContents(contents);
        annotations.add(txtHighlight);
	}
	
	private static void annotate(PDPage page, PDRectangle position, PDAnnotationPopup popup) throws IOException {
		
		List<PDAnnotation> annotations = page.getAnnotations();
		
        // Now add the markup annotation, a highlight to PDRectangle
        PDAnnotationHighlight txtHighlight = getAnnotationHighlight(position);
        
        txtHighlight.setPopup(popup);
        annotations.add(txtHighlight);
	}

	private static PDAnnotationHighlight getAnnotationHighlight(PDRectangle position) {
        // Now add the markup annotation, a highlight to PDFBox text
        PDAnnotationHighlight txtHighlight = new PDAnnotationHighlight();
        txtHighlight.setColor(new PDColor(new float[] { 1, 0, 0 }, PDDeviceRGB.INSTANCE));

        txtHighlight.setConstantOpacity(0.0f);

        // Set the rectangle containing the markup
        txtHighlight.setRectangle(position);

        // work out the points forming the four corners of the annotations
        // set out in anti clockwise form (Completely wraps the text)
        // OK, the below doesn't match that description.
        // It's what acrobat 7 does and displays properly!
        float[] quads = new float[8];
        quads[0] = position.getLowerLeftX();  // x1
        quads[1] = position.getUpperRightY(); // y1
        quads[2] = position.getUpperRightX(); // x2
        quads[3] = quads[1]; // y2
        quads[4] = quads[0]; // x3
        quads[5] = position.getLowerLeftY(); // y3
        quads[6] = quads[2]; // x4
        quads[7] = quads[5]; // y5
        
        txtHighlight.setQuadPoints(quads);
        return txtHighlight;
	}
	
	private static PDRectangle getRectangle( PDPage page, List<TextPosition> positions ) {
		List<TextPosition> xs = positions.stream().sorted((t1,t2) -> Float.compare(t1.getX(),t2.getX()) ).collect(Collectors.toList());
		List<TextPosition> ys = positions.stream().sorted((t1,t2) -> Float.compare(t1.getY(),t2.getY()) ).collect(Collectors.toList());
		
		float x = xs.get(0).getX();
		TextPosition lastXPosition = xs.get(xs.size()-1);
		float width = lastXPosition.getX() + lastXPosition.getWidth()  - x ;
		
		float y = ys.get(0).getY();
		float height = positions.stream().map(p -> p.getHeight()).max(Float::compareTo).orElseThrow();
		
		float pageHeight = page.getMediaBox().getHeight();
		
		x = x -1.5f;
		width = width +3.f;
		y = pageHeight - ( y + 1.5f);
		height = height + 5.f;
		
		return new PDRectangle(x, y, width, height);
		
	}
	
	
	private static PDRectangle highlight(PDDocument doc, PDPage page, PDRectangle rect) throws IOException {

		try (PDPageContentStream contents = new PDPageContentStream(doc, page, AppendMode.APPEND, false)){
			contents.setStrokingColor(Color.RED);
			contents.setLineWidth(1.0f);
			
			contents.addRect(rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getWidth(), rect.getHeight());
			contents.stroke();
			
			return rect;
		}
	}

	private static PDRectangle insert(PDDocument doc, PDPage page, List<TextPosition> positions, String text ) throws IOException {

		try (PDPageContentStream contents = new PDPageContentStream(doc, page, AppendMode.APPEND, false)){
			
			PDRectangle rect = getRectangle(page, positions);
			
			contents.beginText();
			
			PDFont font = new PDType1Font(FontName.HELVETICA_BOLD); //get(positions, TextPosition::getFont);
			float fontSize = get(positions, TextPosition::getFontSize);
			contents.setFont(font, fontSize);
			contents.setStrokingColor(Color.RED);
			contents.setNonStrokingColor(Color.RED);
			
			System.out.println(font.getSpaceWidth());
			
			contents.setTextMatrix(Matrix.getTranslateInstance(rect.getUpperRightX() + font.getSpaceWidth() / 1000f , rect.getLowerLeftY() + 1.5f ));
			
			contents.showText(text);
			
			contents.endText();
			
			return rect;
		}
	}
	
	private static <R> R get(List<TextPosition> positions, Function<TextPosition, ? extends R> mapper ) {
		return positions.stream().map(mapper).filter(Objects::nonNull)
		.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
		.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
		.orElse(null)
		;
	}
	
	
	public static void main(String[] args) throws IOException, UnknownPDFException {
		highlight(new File(args[0]), new File(args[1]), new AllIdcHighlighter());
		
	}

}
