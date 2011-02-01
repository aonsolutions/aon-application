package com.code.aon.jaas.client.ast.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.client.ast.IAccess;
import com.code.aon.jaas.client.ast.INodeVisitor;

/**
 * Permiso de acceso definido para un Rol.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 18-may-2004
 * @since 1.0
 *  
 */
public class AccessPermission implements IAccess {

    /**
     * Indica el identificador y la descripcion del permiso.
     */
    String id, description;

    /**
     * Lista de patrones(URL) y metodos de acceso.
     */
    List<String> patterns = new LinkedList<String>(), methods = new LinkedList<String>();

    /**
     * Constructor
     */
    public AccessPermission() {
    }

    /**
     * Constructor con parámetros
     * 
     * @param id String
     * @param description String
     */
    public AccessPermission(String id, String description) {
        this.id = id;
        this.description = description;
    }

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
     * Añade un nuevo patrón(URL)
     * 
     * @param urlPattern String
     */
    public void addPattern(String urlPattern) {
        if (!patterns.contains(urlPattern)) {
            patterns.add(urlPattern);
        }
    }

    /**
     * Añade un método
     * 
     * @param httpMethod String
     */
    public void addMethod(String httpMethod) {
        if (!methods.contains(httpMethod)) {
            methods.add(httpMethod);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.security.domain.ast.IAccess#getPatterns()
     */
    public List<String> getPatterns() {
        return Collections.unmodifiableList(patterns);
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.security.domain.ast.IAccess#getMethods()
     */
    public List<String> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    /*
     * (non-Javadoc)
     * @see com.code.aon.security.domain.ast.IAccess#boundPermissions()
     */
    public List<String> boundPermissions() {
        // TODO Auto-generated method stub
        return null;
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
     * @see com.code.aon.security.domain.ast.INode#accept(com.code.aon.security.domain.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
		throw new UnsupportedOperationException(); 
//        visitor.visitAccessPermission(this);
    }

}