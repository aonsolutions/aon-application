package com.code.aon.ui.menu;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.logging.Logger;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.xml.sax.SAXException;

/**
 * Loads the menu int the designed structures, using commons-digester.
 * 
 * @author Consulting & Development. Eugenio Castellano - 07-feb-2004
 * @since 1.0
 */
public class MenuParser {

    /**
	 * Logger initialization.
     */
    private static Logger LOGGER = Logger.getLogger(MenuParser.class.getName());

    /** Instancia del Digester. */
    private static Digester DIGESTER;

    /**
     * Devuelve la instancia Digester válida.
     * 
     * @return Digester La instancia Digester válida.
     */
    private static final Digester getDigester() {
        if (DIGESTER == null) {
            DIGESTER = DigesterLoader.createDigester(MenuParser.class
                    .getResource(IMenuConstants.RULES));
            DIGESTER.setUseContextClassLoader(true);

        }
        return DIGESTER;
    }

    /**
     * Returns the menu structure after being parsed.
     * 
     * @param in
     * 			the input stream to read the menu.
     * @return
     * 			the structure of the menu.
     * @throws IOException
     * 			if an unexpected IO error occurs. 			
     * @throws SAXException
     * 			if an unexpected error occurs during the XML parsing.
     */
    public IRoot parse(InputStream in) throws IOException, SAXException {
        Digester digester = getDigester();
        IRoot menu = (IRoot) digester.parse(in);
        MenuIdSetterMenuVisitor v = new MenuIdSetterMenuVisitor();
        try {
            v.visitRoot(menu);
        } catch (MenuVisitorException e) {
            LOGGER.severe(e.getMessage());
            // Lanzamos un SAXException por estar en un método de XMLReader
            // aunque realmente no sabemos muy bien lo que es.
            throw new SAXException(e.getMessage(), e);
        }
        return menu;
    }

    /**
     * Clase que genera los codigos idetificativos de cada opción.
     * 
     * @author Consulting & Development. Eugenio Castellano - 11-abr-2005
     * @since 1.0
     *  
     */
    /**
     * The Class MenuIdSetterMenuVisitor.
     * 
     * @author atellitu
     */
    private class MenuIdSetterMenuVisitor implements IMenuVisitor {

        /** Id del root del menu. */
        private static final String ROOT_ID = "r0";
    	
        /** Prefijo en el cálculo de los id de menu. */
        private static final String MENU_ID_PREFFIX = "m";

        /** Prefijo en el cálculo de los id de las opciones. */
        private static final String OPTION_ID_PREFFIX = "o";

        /** Separador entre ids del menu. */
        private static final String MENU_ID_SEPARATOR = ".";

        /**
         * Clase que almacena el contador de opciones de cada menú. Realmente es
         * un wrapper de integer, util para ser almacenado en un objteo
         * <code>Stack</code>.
         * 
         * @author Consulting & Development. Eugenio Castellano - 11-abr-2005
         * @since 1.0
         */
        private class Counter {
            
            /** Valor de este contador. */
            private int i = 0;

            /**
             * Suma uno al contador y lo devuelve.
             * 
             * @return El vaor del contador después de sumar.
             */
            public int add() {
                return i++;
            }
        }

        /** Almacena los elementos de acorder con la manera de recorrer un árbol. */
        private Stack<Counter> stack = new Stack<Counter>();
        
        /** Indica si el menu tiene definido algún role de seguridad. */
        private boolean hasRoles;

        /*
         * (non-Javadoc)
         * 
         * @see com.code.aon.ui.menu.IMenuVisitor#visitRoot(com.code.aon.ui.menu.IMenu)
         */
        /**
         * Visit root.
         * 
         * @param root the root
         * 
         * @throws MenuVisitorException the menu visitor exception
         */
        public void visitRoot(IRoot root) throws MenuVisitorException {
        	this.hasRoles = ! root.getRoles().isEmpty();
            String id = (root.getId() != null) ? root.getId() : ROOT_ID; 
            root.setId(id);        	
            scanMenu(root);
            root.setRolesDefined( this.hasRoles );
        }

        /**
         * Visit menu.
         * 
         * @param menu the menu
         * 
         * @throws MenuVisitorException the menu visitor exception
         * 
         * @see com.code.aon.ui.menu.IMenuVisitor#visitMenu(com.code.aon.ui.menu.IMenu)
         */
        public void visitMenu(IMenu menu) throws MenuVisitorException {
            String parentId = menu.getParent().getId();
            Counter counter = stack.peek();
            int i = counter.add();
            String menuId = (menu.getId() != null) ? menu.getId() : MENU_ID_PREFFIX + i; 
            String id = (parentId == null) ? menuId : (parentId
                    + MENU_ID_SEPARATOR + menuId);
            menu.setId(id);
            scanMenu(menu);
        	this.hasRoles |= ! menu.getRoles().isEmpty();            
        }

        /**
         * Visit option.
         * 
         * @param option the option
         * 
         * @throws MenuVisitorException the menu visitor exception
         * @see com.code.aon.ui.menu.IMenuVisitor#visitOption(com.code.aon.ui.menu.IOption)
         */
        public void visitOption(IOption option) throws MenuVisitorException {
            Counter counter = stack.peek();
            int i = counter.add();
            String parentId = option.getParent().getId();
            String id;
            if ( option.getId() != null) {
                id = parentId + MENU_ID_SEPARATOR + option.getId();            	
            } else {
                id = parentId + MENU_ID_SEPARATOR + OPTION_ID_PREFFIX + i;            	
            }
            option.setId(id);
        	this.hasRoles |= ! option.getRoles().isEmpty();
        }

        /**
         * Visit action listener.
         * 
         * @param menu the menu
         * 
         * @throws MenuVisitorException the menu visitor exception
         * 
         * @see com.code.aon.ui.menu.IMenuVisitor#visitActionListener(com.code.aon.ui.menu.IActionListener)
         */
        public void visitActionListener(IActionListener menu) throws MenuVisitorException {
		}

		/**
		 * Método para recorrer el menu especificado como parámetro.
		 * 
		 * @param menu el menu que se desea recorrer.
		 * 
		 * @throws MenuVisitorException Si se produce algún error.
		 */
        private void scanMenu(IMenu menu) throws MenuVisitorException {
            stack.push(new Counter());
        	for( IMenuItem menuItem : menu.getItems() ) {
                menuItem.visit(this);
            }
            stack.pop();
        }

    }
}