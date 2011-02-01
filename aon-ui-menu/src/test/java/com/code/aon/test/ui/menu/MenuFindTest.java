package com.code.aon.test.ui.menu;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerFactoryConfigurationError;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.code.aon.ui.menu.IMenu;
import com.code.aon.ui.menu.IMenuItem;
import com.code.aon.ui.menu.IOption;
import com.code.aon.ui.menu.MenuParser;

/**
 * aon-ui-menu Test Unit class.
 * 
 * @author Consulting & Development. Eugenio Castellano - 08-mar-2005
 * @since 1.0
 *  
 */
public class MenuFindTest extends TestCase {

    /**
     * Test tree menu.
     */
    public void testFindMenuItemByKey() {
        try {
            System.out.println();
            System.out.println("[BEGIN] ******* TEST --- testFindMenuItemByKey() ");
            MenuParser parser = new MenuParser();
            InputStream is = MenuFindTest.class
                    .getResourceAsStream("aon-finance-menu.xml");
            IMenu menu = parser.parse(is);
            IMenuItem item = menu.findByKey("aon_invoice_management" );
            assertNotNull(item);
            System.out.println(item.getId());
            
            IOption op = menu.findOptionByKey("aon_invoice_management" );
            assertNotNull(op);
            System.out.println(op.getId());
            
            item = menu.find("root.aon_administrative_management.aon_invoice_management");
            assertNotNull(item);
            System.out.println(item.getId());
        } catch (IOException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (SAXException e) {
        	e.printStackTrace();
            fail(e.getMessage());
        } catch (TransformerFactoryConfigurationError e) {
        	e.printStackTrace();
            fail(e.getMessage());
        }

    }

    
}