package com.code.aon.jaas.deployment.ast;

import java.io.IOException;
import java.io.InputStream;

/**
 * //TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 06-feb-2004
 * @since 1.0
 *  
 */
public interface IAstLoader {

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @param in
     * @return IDescriptor
     * @throws AstException
     * @throws IOException
     */
    IDescriptor parse(InputStream in) throws AstException, IOException;
}