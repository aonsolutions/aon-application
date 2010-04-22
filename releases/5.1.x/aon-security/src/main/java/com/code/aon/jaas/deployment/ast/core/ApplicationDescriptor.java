package com.code.aon.jaas.deployment.ast.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.deployment.ast.IApplicationDescriptor;
import com.code.aon.jaas.deployment.ast.INodeVisitor;

/**
 * // TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 14-may-2004
 * @since 1.0
 *  
 */
public class ApplicationDescriptor implements IApplicationDescriptor {

    /**
     * // TODO [iayerbe] Documéntame!
     */
    List<String> jars = new LinkedList<String>();

    /**
     * // TODO [iayerbe] Documéntame!
     */
    List<String> wars = new LinkedList<String>();

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param jar
     *            String
     */
    public void addJAR(String jar) {
        if (!jars.contains(jar)) {
            jars.add(jar);
        }
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param war
     */
    public void addWAR(String war) {
        if (!wars.contains(war)) {
            wars.add(war);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IApplicationDescriptor#jars()
     */
    public List<String> jars() {
        return Collections.unmodifiableList(jars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IApplicationDescriptor#wars()
     */
    public List<String> wars() {
        return Collections.unmodifiableList(wars);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INode#accept(com.code.aon.jaas.deployment.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
        visitor.visitApplicationDescriptor(this);
    }
}