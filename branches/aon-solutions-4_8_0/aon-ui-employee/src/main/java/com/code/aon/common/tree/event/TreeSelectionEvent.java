/*
 * Created on 07-sep-2006
 *
 */
package com.code.aon.common.tree.event;

import java.util.EventObject;

import com.code.aon.common.tree.jsf.AbstractTreeModel;


/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 07-sep-2006
 * @since 1.0
 *
 */

public class TreeSelectionEvent extends EventObject {

    private String oldSelectionNodeId;
    private String newSelectionNodeId;

    /**
     * Construct an event.
     *
     * @param model              event source
     * @param oldSelectionNodeId id of the old selection, null if no node was selected before
     * @param newSelectionNodeId id of the current selection
     */
    public TreeSelectionEvent(AbstractTreeModel model, 
                              String oldSelectionNodeId, String newSelectionNodeId) {
        super(model);
        this.oldSelectionNodeId = oldSelectionNodeId;
        this.newSelectionNodeId = newSelectionNodeId;
    }

    /**
     * Answer the id of the old selection.
     *
     * @return id of previous (old) selection, null if no node was selected before
     */
    public String getoldSelectionNodeId() {
        return oldSelectionNodeId;
    }

    /**
     * Answer the id of the current (new) selection.
     *
     * @return id of the new selected node
     */
    public String getNewSelectionNodeId() {
        return newSelectionNodeId;
    }

}
