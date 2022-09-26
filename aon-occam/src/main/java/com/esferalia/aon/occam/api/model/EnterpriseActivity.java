package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.VATRegime;

public class EnterpriseActivity implements Serializable {

	private static final long serialVersionUID = 1603402672575353203L;
	
	private Integer id;
	private String description;
	private boolean principal;
	private Iae iae;
	private Integer cnae;
	private String cnaeCode;
	private String cnaeDescription;
	private VATRegime vatRegime; 

	public Integer getId() {
		return id;
	}

	public EnterpriseActivity setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public EnterpriseActivity setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public boolean isPrincipal() {
		return principal;
	}
	
	public EnterpriseActivity setPrincipal(boolean principal) {
		this.principal = principal;
		return this;		
	}
	
	public Iae getIae() {
		if(iae == null) {
			iae = new Iae();
		}
		return iae;
	}
	
	public EnterpriseActivity setIae(Iae iae) {
		this.iae = iae;
		return this;
	}
	
	public String getEpigraph() {
		return getIae().getEpigraph();
	}

	public EnterpriseActivity setEpigraph(String epigraph) {
		getIae().setEpigraph(epigraph);
		return this;
	}

	public Integer getCnae() {
		return cnae;
	}

	public EnterpriseActivity setCnae(Integer cnae) {
		this.cnae = cnae;
		return this;		
	}
	public String getCnaeCode() {
		return cnaeCode;
	}

	public EnterpriseActivity setCnaeCode(String cnaeCode) {
		this.cnaeCode = cnaeCode;
		return this;		
	}

	public String getCnaeDescription() {
		return cnaeDescription;
	}

	public EnterpriseActivity setCnaeDescription(String cnaeDescription) {
		this.cnaeDescription = cnaeDescription;
		return this;		
	}

	public VATRegime getVatRegime() {
		if(vatRegime == null) {
			vatRegime = VATRegime.GENERAL;
		}
		return vatRegime;
	}
	public EnterpriseActivity setVatRegime(VATRegime vatRegime) {
		this.vatRegime = vatRegime;
		return this;
	}

}
