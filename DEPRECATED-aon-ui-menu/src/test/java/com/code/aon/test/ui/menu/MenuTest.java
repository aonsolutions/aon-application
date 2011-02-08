package com.code.aon.test.ui.menu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

import com.code.aon.ui.menu.IMenu;
import com.code.aon.ui.menu.IMenuItem;
import com.code.aon.ui.menu.MenuParser;
import com.code.aon.ui.menu.xml.MenuVisitor;
import com.code.aon.ui.menu.xml.TreeMenuVisitor;
import com.code.aon.ui.menu.xml.TreePathVisitor;

/**
 * aon-ui-menu Test Unit class.
 * 
 * @author Consulting & Development. Eugenio Castellano - 08-mar-2005
 * @since 1.0
 *  
 */
public class MenuTest extends TestCase {

    /**
     * Test tree menu.
     */
    public void testTreeMenu() {
        try {
            System.out.println();
            System.out.println("[BEGIN] ******* TEST --- testTreeMenu() ");
            MenuParser parser = new MenuParser();
            InputStream is = MenuTest.class
                    .getResourceAsStream("menu.xml");
            IMenu menu = parser.parse(is);
            ResourceBundle bundle = ResourceBundle.getBundle( "com.code.aon.test.ui.menu.messages"); 
            TreeMenuVisitor visitor = new TreeMenuVisitor(menu,bundle);

            SAXSource source = new SAXSource(visitor, new InputSource());
            StreamResult result = new StreamResult(System.out);
            TransformerFactory f = TransformerFactory.newInstance();
            Transformer t = f.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            t.transform(source, result);
            System.out.println("[BEGIN] ******* TEST --- testTreeMenu() ");
            System.out.println();
        } catch (TransformerConfigurationException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (IOException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (SAXException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (TransformerFactoryConfigurationError e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (TransformerException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        }

    }

    /**
     * Test tree path.
     */
    public void testTreePath() {
        try {
            System.out.println();
            System.out.println("[BEGIN] ******* TEST --- testTreePath() ");
            MenuParser parser = new MenuParser();
            InputStream is = MenuTest.class
                    .getResourceAsStream("menu.xml");
            IMenu menu = parser.parse(is);
            IMenuItem menuItem = menu.find("r0.m0.o1");
            TreePathVisitor visitor = new TreePathVisitor(menuItem);

            SAXSource source = new SAXSource(visitor, new InputSource());
            StreamResult result = new StreamResult(System.out);
            TransformerFactory f = TransformerFactory.newInstance();
            Transformer t = f.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(source, result);

            System.out.println("[END] ******* TEST --- testTreePath() ");
            System.out.println();
        } catch (TransformerConfigurationException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (IOException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (SAXException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (TransformerFactoryConfigurationError e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (TransformerException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Test menu.
     */
    public void testMenu() {
        try {
            System.out.println();
            System.out.println("[BEGIN] ******* TEST --- testMenu() ");
            MenuParser parser = new MenuParser();
            InputStream is = MenuTest.class
                    .getResourceAsStream("menu.xml");
            IMenu menu = parser.parse(is);
            //MenuVisitor visitor = new MenuVisitor(menu);
            IMenu menu2 = (IMenu) menu.find("r0.m0");
            ResourceBundle bundle = ResourceBundle.getBundle( "com.code.aon.test.ui.menu.messages"); 
            MenuVisitor visitor = new MenuVisitor(menu2,bundle);
            SAXSource source = new SAXSource(visitor, new InputSource());
            StreamResult result = new StreamResult(System.out);
            TransformerFactory f = TransformerFactory.newInstance();
            Transformer t = f.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(source, result);

            System.out.println("[END] ******* TEST --- testMenu() ");
            System.out.println();
        } catch (TransformerConfigurationException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (IOException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (SAXException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (TransformerFactoryConfigurationError e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (TransformerException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    /**
     * The main method.
     * 
     * @param args the args
     */
    public static void main(String[] args) {
        MenuTest t = new MenuTest();
        t.testTreeMenu();
    }
    
}