package com.code.aon.jaas.deployment.ast.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.xml.sax.SAXException;

import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.deployment.ast.IAstLoader;
import com.code.aon.jaas.deployment.ast.IDescriptor;

/**
 * // TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 07-feb-2005
 * @since 1.0
 *  
 */
public class AstLoader implements IAstLoader {

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private static final String RULES = "deployment_digester.xml";

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private static Digester DIGESTER;

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @return Digester
     */
    private static final Digester getDigester() {
        if (DIGESTER == null) {
            DIGESTER = DigesterLoader.createDigester(AstLoader.class
                    .getResource(RULES));
        }
        return DIGESTER;
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param loader
     * @return Digester
     */
    private static final Digester getDigester(URL loader) {
        if (DIGESTER == null) {
            DIGESTER = DigesterLoader.createDigester(loader);
        }
        return DIGESTER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IAstLoader#parse(java.io.InputStream)
     */
    public IDescriptor parse(InputStream in) throws AstException, IOException {
        try {
            return (IDescriptor) getDigester().parse(in);
        } catch (SAXException e) {
            throw new AstException(e);
        }
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param in
     * @param loader
     * @return Object
     * @throws AstException
     * @throws IOException
     */
    public Object parse(InputStream in, URL loader) throws AstException,
            IOException {
        try {
            return (IDescriptor) getDigester(loader).parse(in);
        } catch (SAXException e) {
            throw new AstException(e);
        }
    }

}