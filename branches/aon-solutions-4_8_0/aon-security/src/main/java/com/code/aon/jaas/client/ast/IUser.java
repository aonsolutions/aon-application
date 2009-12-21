package com.code.aon.jaas.client.ast;

/**
 * Usuario de la aplicación
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 31-may-2004
 * @since 1.0
 *  
 */
public interface IUser extends INode {

    /**
     * Nombre del usuario
     * 
     * @return String
     */
    String getName();

    /**
     * Descripción del usuario
     * 
     * @return String
     */
    String getDescription();

    /**
     * Contraseña del usuario
     * 
     * @return String
     */
    String getPasswd();

    /**
     * Cambio de contraseña
     * 
     * @param passwd
     */
    void changePasswd(String passwd);
}