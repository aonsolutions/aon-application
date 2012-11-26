package com.code.aon.jaas.deployment.ast.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.deployment.ast.INodeVisitor;
import com.code.aon.jaas.deployment.ast.IPermission;
import com.code.aon.jaas.deployment.ast.ISecurityDescriptor;
import com.code.aon.jaas.deployment.ast.ISecurityRole;

/**
 * Esta clase contiene la información asociada al fichero web.xml. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 19-may-2005
 * @since 1.0
 *  
 */
public class WebDescriptor implements ISecurityDescriptor {

    /**
     * Lista de roles.
     */
    List<ISecurityRole> roles = new LinkedList<ISecurityRole>();

    /**
     * Indica las posibles restricciones de seguridad.
     */
    List<IPermission> securityConstraints;

    /**
     * Añade un nuevo Rol a la lista de Roles.
     * 
     * @param role, definido en el fichero web.xml de la aplicación.
     */
    public void add(ISecurityRole role) {
        roles.add(role);
    }

    /**
     * Añade una nueva restricción a la lista.
     * 
     * @param permission
     */
    public void add(IPermission permission) {
        if (securityConstraints == null) {
            securityConstraints = new LinkedList<IPermission>();
        }
        securityConstraints.add(permission);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.ISecurityDescriptor#roles()
     */
    public List<ISecurityRole> roles() {
        return Collections.unmodifiableList(roles);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.ISecurityDescriptor#permissions()
     */
    public List<IPermission> permissions() {
        if (securityConstraints == null) {
        	securityConstraints = new LinkedList<IPermission>();
        }
        return Collections.unmodifiableList(securityConstraints);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INode#accept(com.code.aon.jaas.deployment.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
        visitor.visitWebDescriptor(this);
    }

}