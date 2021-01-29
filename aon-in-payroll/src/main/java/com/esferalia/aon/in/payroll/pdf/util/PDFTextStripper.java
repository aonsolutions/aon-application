package com.esferalia.aon.in.payroll.pdf.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.text.TextPosition;

import com.esferalia.aon.watson.util.AonStringUtils;

public class PDFTextStripper extends org.apache.pdfbox.text.PDFTextStripper
{
	private class TextPositionComparator implements Comparator<TextPosition>
	{
	    @Override
	    public int compare(TextPosition pos1, TextPosition pos2)
	    {
	        // only compare text that is in the same direction
	        int cmp1 = Float.compare(pos1.getDir(), pos2.getDir());
	        if (cmp1 != 0)
	        {
	            //return cmp1;
	        }
	        
	        // get the text direction adjusted coordinates
	        float x1 = pos1.getXDirAdj();
	        float x2 = pos2.getXDirAdj();
	        
	        float pos1YBottom = pos1.getYDirAdj();
	        float pos2YBottom = pos2.getYDirAdj();

	        float yDifference = Math.abs(pos1YBottom - pos2YBottom);

	        // we will do a simple tolerance comparison
	        if (yDifference < minHeight/2.00 )
	        {
	            return Float.compare(x1, x2);
	        }
	        else 
	        {
	        	return Float.compare(pos1YBottom, pos2YBottom);
	        }
	    }
	}
	
	private float minHeight = Float.MAX_VALUE;

    public PDFTextStripper() throws IOException {
		super();
	}


	@Override
    protected void writePage() throws IOException {
    	sort();
    	super.writePage();
    }
	
	@Override
	@Deprecated
	public void setSortByPosition(boolean newSortByPosition) {
		throw new UnsupportedOperationException();
	}
    
    
    private void sort() {
    	TextPositionComparator comparator = new TextPositionComparator();
    	charactersByArticle.forEach( l -> Collections.sort(l, comparator));
    }
    
    @Override
    protected void processTextPosition(TextPosition text) {
    	super.processTextPosition(text);
    	minHeight = Math.min(minHeight, text.getHeightDir());
    	
    }
    /**
     * This will print the usage for this document.
     */
    private static void usage()
    {
        System.err.println( "Usage: java " + PDFTextStripper.class.getName() + " <input-pdf>" );
    }

    /**
     * This will print the documents data.
     *
     * @param args The command line arguments.
     *
     * @throws IOException If there is an error parsing the document.
     */
    public static void main( String[] args ) throws IOException
    {
        if( args.length != 1 )
        {
            usage();
        }
        else
        {
            try (PDDocument document = PDDocument.load(new File(args[0])))
            {
        		for (int p = 1; p <= document.getNumberOfPages(); p++) {
        		PDFTextStripper stripper = new PDFTextStripper();
                //stripper.setSortByPosition( true );
                stripper.setStartPage( p );
                stripper.setEndPage( p );

                Writer dummy = new OutputStreamWriter(System.out);
                stripper.writeText(document, dummy);
                dummy.flush();
        		}
            }
        }
    }
    
}

