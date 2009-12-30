/*
 * Created on 07-sep-2006
 *
 */
package com.code.aon.common.tree.event;

import java.util.EventListener;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 07-sep-2006
 * @since 1.0
 *
 */

public interface TreeSelectionListener extends EventListener {

    public void valueChanged(TreeSelectionEvent event);
}
