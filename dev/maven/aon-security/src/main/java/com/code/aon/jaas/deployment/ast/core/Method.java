package com.code.aon.jaas.deployment.ast.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.deployment.ast.IMethod;
import com.code.aon.jaas.deployment.ast.INodeVisitor;

/**
 * // TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñakia Ayerbe- 12-feb-2005
 * @since 1.0
 *  
 */
public class Method implements IMethod {

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private String description;

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private String name;

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private String methodIntf;

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private String method;

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private List<String> methodParams = new LinkedList<String>();

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param param
     */
    public void addMethodParam(String param) {
        if (!methodParams.contains(param)) {
            methodParams.add(param);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IMethod#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IMethod#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IMethod#getMethodIntf()
     */
    public String getMethodIntf() {
        return methodIntf;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IMethod#getMethod()
     */
    public String getMethod() {
        return method;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IMethod#params()
     */
    public List<String> params() {
        return Collections.unmodifiableList(methodParams);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INode#accept(com.code.aon.jaas.deployment.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
        visitor.visitMethod(this);
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param string
     */
    public void setDescription(String string) {
        description = string;
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param string
     */
    public void setMethod(String string) {
        method = string;
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param string
     */
    public void setMethodIntf(String string) {
        methodIntf = string;
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

}