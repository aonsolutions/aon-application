/**
 * 
 */
package es.code.ecm.event;

import java.util.EventObject;

import es.code.ecm.nodes.ECMNode;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 19/12/2007
 *
 */
public class PermissionEvent extends EventObject {

	private static final long serialVersionUID = 193619937652322949L;

	/** ECMNode. */
	private ECMNode node;

	public PermissionEvent(Object source, ECMNode node) {
		super(source);
		this.node = node;
	}

	/**
	 * @return the ECM node
	 */
	public ECMNode getNode() {
		return node;
	}

}
