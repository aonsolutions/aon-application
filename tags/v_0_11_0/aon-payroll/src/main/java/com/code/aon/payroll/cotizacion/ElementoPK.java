package com.code.aon.payroll.cotizacion;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Clave primaria de Elementos de cotización.
 */
@Embeddable
public class ElementoPK implements Serializable {

	private String cdg;
	private Date fecini;

	@Column(name="cdg", nullable=false, length=8)
	public String getCdg() {
		return this.cdg;
	}
   
	public void setCdg(String cdg) {
		this.cdg = cdg;
	}

	@Column(name="fecini", nullable=false, length=10)
	public Date getFecini() {
		return this.fecini;
	}

	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}


	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( (other == null ) ) return false;
		if ( !(other instanceof ElementoPK) ) return false;
		ElementoPK castOther = (ElementoPK) other; 

		return ( (this.getCdg()==castOther.getCdg()) || ( this.getCdg()!=null && castOther.getCdg()!=null && this.getCdg().equals(castOther.getCdg()) ) )
		&& ( (this.getFecini()==castOther.getFecini()) || ( this.getFecini()!=null && castOther.getFecini()!=null && this.getFecini().equals(castOther.getFecini()) ) );
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + ( getCdg() == null ? 0 : this.getCdg().hashCode() );
		result = 37 * result + ( getFecini() == null ? 0 : this.getFecini().hashCode() );
		return result;
	}   

}
