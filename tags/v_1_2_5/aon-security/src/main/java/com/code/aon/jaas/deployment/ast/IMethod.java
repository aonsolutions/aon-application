package com.code.aon.jaas.deployment.ast;

import java.util.List;

/**
 * //TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 19-may-2004
 * @since 1.0
 *  
 */
public interface IMethod extends INode {

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return String
     */
    String getDescription();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return String
     */
    String getName();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return String
     */
    String getMethodIntf();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return String
     */
    String getMethod();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return List
     */
    List<String> params();
}