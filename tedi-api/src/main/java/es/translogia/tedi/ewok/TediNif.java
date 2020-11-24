package es.translogia.tedi.ewok;

import java.io.Serializable;
import java.util.Objects;

public class TediNif implements Serializable {
	
	private static final long serialVersionUID = 6865210693414641809L;
	
	private String str;
	private TediNifType type;

	public String getStr() {
		return str;
	}
	
	public TediNif setStr(String str) {
		this.str = str;
		return this;
	}
	
	public TediNifType getType() {
		return type;
	}
	
	public TediNif setType(TediNifType type) {
		this.type = type;
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(str,type); 
	}
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TediNif other = (TediNif) obj;
        return Objects.equals(str, other.getStr()) 
    		&& Objects.equals(type, other.getType()); 
    }	
}
