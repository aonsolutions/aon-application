package com.esferalia.aon.payroll;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ActividadCCCPK implements Serializable {

	private static final long serialVersionUID = -6922356169005816164L;

	private Integer cdg;
	private String tipccc;

	@Column(name = "cdg", nullable = false, length = 4)
	public Integer getCdg() {
		return this.cdg;
	}

	public void setCdg(Integer cdg) {
		this.cdg = cdg;
	}

	@Column(name = "tipccc", nullable = false, length = 1)
	public String getTipccc() {
		return this.tipccc;
	}

	public void setTipccc(String tipccc) {
		this.tipccc = tipccc;
	}

}
