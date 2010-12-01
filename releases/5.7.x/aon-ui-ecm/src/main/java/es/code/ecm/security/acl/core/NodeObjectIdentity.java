package es.code.ecm.security.acl.core;

import es.code.ecm.security.acl.AclObjectIdentity;

public class NodeObjectIdentity implements AclObjectIdentity {

	private String nodeType;
	private String id;

	public NodeObjectIdentity(String nodeType, String id) {
		if( nodeType == null || "".equals(nodeType) )
			throw new IllegalArgumentException( "nodetype required" );
		if( id == null || "".equals( id ) ) {
			throw new IllegalArgumentException( "id required" );
		} else {
			this.nodeType = nodeType;
			this.id = id;
			return;
		}
	}

	protected NodeObjectIdentity() {
		throw new IllegalArgumentException( "Cannot use default constructor" );
	}

	public String getId() {
		return id;
	}

	@Override
	public String getNodeType() {
		return nodeType;
	}

	@Override
	public boolean equals(Object arg0) {
		if( arg0 == null )
			return false;
		if( !(arg0 instanceof NodeObjectIdentity) )
			return false;
		NodeObjectIdentity other = (NodeObjectIdentity)arg0;
		return getId().equals( other.getId() );// && getNodeType().equals( other.getNodeType() );
	}
	
	@Override
	public int hashCode() {
		StringBuffer sb = new StringBuffer();
		sb.append( nodeType ).append( id );
		return sb.toString().hashCode();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append( getClass().getName() ).append( "[" );
		sb.append( "Nodetype: " ).append( nodeType );
		sb.append( "; Identity: " ).append( id ).append( "]" );
		return sb.toString();
	}
}