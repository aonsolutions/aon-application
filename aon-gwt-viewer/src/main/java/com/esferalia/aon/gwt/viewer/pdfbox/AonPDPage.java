package com.esferalia.aon.gwt.viewer.pdfbox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class AonPDPage extends PDPage{

    private static final int DEFAULT_USER_SPACE_UNIT_DPI = 72;
    private static final Color TRANSPARENT_WHITE = new Color( 255, 255, 255, 0 );
	
    
    public AonPDPage(PDPage pdpage) throws IOException{
    	if(pdpage.getActions() != null) setActions(pdpage.getActions());
    	if(pdpage.getAnnotations() != null) setAnnotations(pdpage.getAnnotations());
    	if(pdpage.getArtBox() != null) setArtBox(pdpage.getArtBox());
    	if(pdpage.getBleedBox() != null) setBleedBox(pdpage.getBleedBox());
    	if(pdpage.getContents() != null) setContents(pdpage.getContents());
    	if(pdpage.getCropBox() != null) setCropBox(pdpage.getCropBox());
    	if(pdpage.getMediaBox() != null) setMediaBox(pdpage.getMediaBox());
    	if(pdpage.getMetadata() != null) setMetadata(pdpage.getMetadata());
    	if(pdpage.getParent() != null) setParent(pdpage.getParent());
    	if(pdpage.getResources() != null) setResources(pdpage.getResources());
    	if(pdpage.getRotation() != null) setRotation(pdpage.getRotation());
    	if(pdpage.getThreadBeads() != null) setThreadBeads(pdpage.getThreadBeads());
    	if(pdpage.getTrimBox() != null) setTrimBox(pdpage.getTrimBox());
    }
    
	@Override
	public BufferedImage convertToImage(int imageType, int resolution) throws IOException {
		 PDRectangle cropBox = findCropBox();
	        float widthPt = cropBox.getWidth();
	        float heightPt = cropBox.getHeight();
	        float scaling = resolution / (float)DEFAULT_USER_SPACE_UNIT_DPI;
	        int widthPx = Math.round(widthPt * scaling);
	        int heightPx = Math.round(heightPt * scaling);
	        //TODO The following reduces accuracy. It should really be a Dimension2D.Float.
	        Dimension pageDimension = new Dimension( (int)widthPt, (int)heightPt );
	        BufferedImage retval = null;
	        int rotationAngle = findRotation();
	        // normalize the rotation angle
	        if (rotationAngle < 0)
	        {
	            rotationAngle += 360;
	        }
	        else if (rotationAngle >= 360)
	        {
	            rotationAngle -= 360;
	        }
	        // swap width and height
	        if (rotationAngle == 90 || rotationAngle == 270)
	        {
	            retval = new BufferedImage( heightPx, widthPx, imageType );
	        }
	        else
	        {
	            retval = new BufferedImage( widthPx, heightPx, imageType );
	        }
	        Graphics2D graphics = (Graphics2D)retval.getGraphics();
	        graphics.setBackground( TRANSPARENT_WHITE );
	        graphics.clearRect( 0, 0, retval.getWidth(), retval.getHeight() );
	        if (rotationAngle != 0)
	        {
	            int translateX = 0;
	            int translateY = 0;
	            switch(rotationAngle) 
	            {
	                case 90:
	                    translateX = retval.getWidth();
	                    break;
	                case 270:
	                    translateY = retval.getHeight();
	                    break;
	                case 180:
	                    translateX = retval.getWidth();
	                    translateY = retval.getHeight();
	                    break;
	                default:
	                    break;
	            }
	            graphics.translate(translateX,translateY);
	            graphics.rotate((float)Math.toRadians(rotationAngle));
	        }
	        graphics.scale( scaling, scaling );
	        PageDrawer drawer = new PageDrawer();
	        drawer.drawPage( graphics, this, pageDimension );

	        return retval;
	}
}
