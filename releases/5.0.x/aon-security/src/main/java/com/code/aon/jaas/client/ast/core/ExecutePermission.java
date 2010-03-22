package com.code.aon.jaas.client.ast.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.client.ast.IExecute;
import com.code.aon.jaas.client.ast.INodeVisitor;

/**
 * Permiso de ejecución definido para un Rol.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 18-may-2004
 * @since 1.0
 *  
 */
public class ExecutePermission implements IExecute {

    /**
     * Indica el identificador y la descripcion del permiso.
     */
    String id, description;

    /**
     * Lista de metodos de acceso.
     */
    List<String> methods = new LinkedList<String>();

    /**
     * Asigna la descripción
     * 
     * @param string
     */
    public void setDescription(String string) {
        description = string;
    }

    /**
     * Asigna el identificador
     * 
     * @param string
     */
    public void setId(String string) {
        id = string;
    }

    /**
     * Añade un método
     * 
     * @param method String
     */
    public void addMethod(String method) {
        if (!methods.contains(method)) {
            methods.add(method);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.security.domain.ast.IPermission#getId()
     */
    public String getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.security.domain.ast.IPermission#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.security.policy.ast.IPermission#getMethods()
     */
    public List<String> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.security.domain.ast.INode#accept(com.code.aon.security.domain.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
		throw new UnsupportedOperationException(); 
//        visitor.visitExecutePermission(this);
    }

}