/*
 * Created on 05-sep-2006
 *
 */
package com.code.aon.common.tree;

import org.apache.myfaces.custom.tree2.TreeNode;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 05-sep-2006
 * @since 1.0
 *
 */

public interface ITreeNode extends TreeNode {

    /**
     * Devuelve un Object el elemento que representa el nodo.
     * 
     * @return Devuelve el elemento que representa el nodo.
     */
    Object getUserObject();

    /**
     * Devuelve un String con la acción a ejecutar.
     * 
     * @return Devuelve la acción a ejecutar.
     */
    String getReference();
    
    /**
     * Devuelve un String con el icono representando que el nodo está expandido.
     * 
     * @return Devuelve el icono de elemento abierto.
     */
    String getIconOpen();
    
    /**
     * Devuelve un String con el icono representando que el nodo está comprimido.
     * 
     * @return Devuelve el icono de elemento cerrado.
     */
    String getIconClose();

}
