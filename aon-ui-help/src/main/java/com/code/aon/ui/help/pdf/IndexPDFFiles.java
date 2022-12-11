package com.code.aon.ui.help.pdf;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDestinationNameTreeNode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.text.PDFTextStripperByArea;

public class IndexPDFFiles {

    private static PDDocument index(InputStream file, String filename, PDDocument index, int indexPages) {
	try {

	    System.out.println("---------------------------------------------------");
	    System.out.println(" INDEXING " + filename);
	    System.out.println("---------------------------------------------------");

	    PDDocument doc = Loader.loadPDF(file);
	    doc.getDocumentCatalog().setDocumentOutline(new PDDocumentOutline());

	    // Create file outline item, add name to names and set action
	    PDOutlineItem outlineItem = new PDOutlineItem();
	    outlineItem.setTitle(filename);
	    index.getDocumentCatalog().getDocumentOutline().addLast(outlineItem);
	    outlineItem.setTitle(filename);

	    PDActionURI action = new PDActionURI();
	    action.setURI("help/" + filename + ".pdf");
	    outlineItem.setAction(action);

	    HashMap<String, PDPageDestination> namesMap = new HashMap<>();

	    for (int i = 2; i <= indexPages; i++) {
		IndexPDFFiles.extractPage(doc, i, namesMap, index, filename);
	    }

	    // Add map to the names
	    PDDocumentNameDictionary names = doc.getDocumentCatalog().getNames();
	    names = new PDDocumentNameDictionary(doc.getDocumentCatalog());

	    PDDestinationNameTreeNode dests = names.getDests();
	    dests = new PDDestinationNameTreeNode();
	    names.setDests(dests);
	    dests.setNames(namesMap);

	    return doc;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return new PDDocument();

    }

    /**
     * Extract a page
     * 
     * @param doc          The document to work on
     * @param pageNumber   The page number
     * @param destinations The extracted destination list
     * @param namesMap     The extracted name map
     * @param index
     * @param filename
     * @throws IOException if it fails to extract
     */
    private static void extractPage(PDDocument doc, int pageNumber, HashMap<String, PDPageDestination> namesMap,
	    PDDocument index, String filename) throws IOException {

	// Get page
	PDPage page = doc.getPage(pageNumber);
	PDFTextStripperByArea stripper = new PDFTextStripperByArea();
	// Extract annotations
	List<PDPageDestination> destinations = IndexPDFFiles.extractPageDestinations(doc, page, stripper);
	stripper.extractRegions(page);

	// Set names
	IndexPDFFiles.setPageNameReferences(doc, stripper, destinations, namesMap, index, filename);
    }

    /**
     * Extracts the page destinations from the annotations and add their regions to
     * stripper
     * 
     * @param doc      The document to work on
     * @param page     The page to work on
     * @param stripper The stripper by area of the document
     * @return destinations The extracted destination list
     * @throws IOException if it fails to extract
     */
    private static List<PDPageDestination> extractPageDestinations(PDDocument doc, PDPage page,
	    PDFTextStripperByArea stripper) throws IOException {

	List<PDPageDestination> destinations = new ArrayList<>();

	List<?> annotations = page.getAnnotations();
	int region = 0;
	for (Object a : annotations) {
	    PDAnnotation annotation = (PDAnnotation) a;

	    if (annotation instanceof PDAnnotationLink) {
		PDAnnotationLink link = (PDAnnotationLink) annotation;
		PDRectangle rect = link.getRectangle();

		float x = rect.getLowerLeftX();
		float y = rect.getUpperRightY();
		float width = rect.getWidth();
		float height = rect.getHeight();

		// Calculate real Y
		PDRectangle pageSize = page.getMediaBox();
		y = pageSize.getHeight() - y;

		Rectangle2D.Float awtRect = new Rectangle2D.Float(x, y, width, height);
		stripper.addRegion("" + region++, awtRect);
		PDDestination destination = link.getDestination();
		if (destination instanceof PDPageDestination) {
		    destinations.add((PDPageDestination) link.getDestination());
		} else if (destination instanceof PDNamedDestination) {
		    PDNamedDestination named = (PDNamedDestination) link.getDestination();
		    PDPageXYZDestination d = (PDPageXYZDestination) doc.getDocumentCatalog().getDests()
			    .getDestination(named.getNamedDestination());
		    destinations.add(d);
		} else if ( destination == null ){
		}
	    }
	}

	return destinations;
    }

    private static void setPageNameReferences(PDDocument doc, PDFTextStripperByArea stripper,
	    List<PDPageDestination> destinations, HashMap<String, PDPageDestination> namesMap, PDDocument index,
	    String filename) {

	for (int i = 0; i < stripper.getRegions().size(); i++) {

	    String r = stripper.getRegions().get(i);

	    // Dotted outline
	    final String dotPatternStr = "^\\s*[\\d\\.]*\\s*(((?!\\.{3,}).)*)\\s*\\.{3,}\\s*(\\d+)\\s*$";
	    final Pattern dotPattern = Pattern.compile(dotPatternStr, Pattern.CASE_INSENSITIVE);
	    final Matcher dotMatcher = dotPattern.matcher(stripper.getTextForRegion(r));

	    // Standard outline
	    String standardPatternStr = "^\\s*[\\d\\.]*\\s*(((?!\\.{3,}).)*)\\s*$";
	    Pattern standardPattern = Pattern.compile(standardPatternStr, Pattern.CASE_INSENSITIVE);
	    Matcher standardMatcher = standardPattern.matcher(stripper.getTextForRegion(r));

	    boolean dotOutlineMode = dotMatcher.matches();
	    boolean standardOutlineMode = standardMatcher.matches();

	    if (i >= destinations.size())
		return;

	    if (!dotOutlineMode && !standardOutlineMode) {
		return;
	    }

	    String name = null;

	    // If dotted outline
	    if (dotOutlineMode) {
		name = dotMatcher.group(1);
	    }

	    // if standard outline
	    else if (standardOutlineMode) {
		name = standardMatcher.group(1);
	    }

	    // If no outline item return
	    if (name == null) {
		return;
	    }

	    name = name.replaceAll("(\\w+)?[\\.\\s]{3,}\\s*\\d+", "$1");

	    // Create outline item, add name to names and set action
	    PDOutlineItem item = new PDOutlineItem();
	    item.setDestination(destinations.get(i));

	    String id = String.format("aon%d", namesMap.size());
	    item.setTitle(name);

	    PDActionURI action = new PDActionURI();
	    action.setURI("help/" + filename + ".pdf" + "#" + id);
	    item.setAction(action);

	    PDPageXYZDestination pdPageXYZDestination = (PDPageXYZDestination) destinations.get(i);
	    System.out.println("DESTINATION NAME [" + r + "] " + name + "[" + id + "] " + pdPageXYZDestination.getLeft()
		    + " , " + pdPageXYZDestination.getTop());

	    index.getDocumentCatalog().getDocumentOutline().getLastChild().addLast(item);
	    namesMap.put(id, destinations.get(i));

	}

    }

    public static class PdfIndexProperties {

	private String name;
	private int index;

	public PdfIndexProperties(String name, int index) {
	    super();
	    this.name = name;
	    this.index = index;
	}

	public String getName() {
	    return name;
	}

	public int getIndex() {
	    return index;
	}

    }

    public static void main(String[] args) throws Exception {
	try {

	    HashMap<String, PdfIndexProperties> files = new HashMap<>();
	    files.put("config", new PdfIndexProperties("CONFIGURACION Manual de USUARIO", 4));
	    files.put("account", new PdfIndexProperties("CONTABILIDAD Manual de USUARIO", 4));
	    files.put("fiscal", new PdfIndexProperties("FISCAL Manual de USUARIO", 3));
	    files.put("payroll", new PdfIndexProperties("LABORAL Manual de USUARIO", 5));

	    System.out.println("-----------------------------------------------------------");
	    System.out.println(" PDF INDEX GENERATION NEW ");
	    System.out.println("-----------------------------------------------------------");

	    PDDocument index = new PDDocument();
	    PDDocumentOutline outline = new PDDocumentOutline();
	    index.getDocumentCatalog().setDocumentOutline(outline);

	    PDPage blankPage = new PDPage();
	    index.addPage(blankPage);

	    try {

		files.keySet().forEach(filename -> {

		    String indexName = files.get(filename).getName();
		    int indexPages = files.get(filename).getIndex();

		    InputStream stream = IndexPDFFiles.class.getResourceAsStream(filename + ".pdf");

		    PDDocument named = index(stream, indexName, index, indexPages);
		    try {
			named.save("target/classes/com/code/aon/ui/help/pdf/" + filename + "_names.pdf");
			named.save("src/main/resources/com/code/aon/ui/help/pdf/" + filename + "_names.pdf");
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		});

		index.save("target/classes/com/code/aon/ui/help/pdf/index.pdf");
		index.save("src/main/resources/com/code/aon/ui/help/pdf/index.pdf");
		index.close();

		System.out.println("[DONE] Index generated.");

	    } catch (IOException e) {
		e.printStackTrace();
	    } finally {
		try {
		    index.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
