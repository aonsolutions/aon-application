package aon.sepe.objects;

import java.util.Date;
import java.util.Optional;

public class ContractExtension {
	
	private String cif;
	private String sepeId;	
	private String regime;
	private String ctaCti;
	private Date startDate;
	private Date endDate;
	private boolean discontinuo;  //¿Ha existido un periodo de inactividad conforme a la normativa vigente, que justifique la discontinuidad?

	public String getCif() {
		return cif;
	}
	
	public Optional<String> getSepeId() {
		return Optional.ofNullable(sepeId);
	}
	
	public String getCtaCti() {
		return ctaCti;
	}
	
	public String getRegime() {
		return regime;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public boolean getDiscontinuo() {
		return discontinuo;
	}
	
	public ContractExtension setRegime(String regime) {
		this.regime = regime;
		return this;
	}
	
	public ContractExtension setCif(String cif) {
		this.cif = cif;
		return this;
	}
	
	public ContractExtension setSepeId(String sepeId) {
		this.sepeId = sepeId;
		return this;
	}
	
	public ContractExtension setCtaCti(String ctaCti) {
		this.ctaCti = ctaCti;
		return this;
	}
	
	public ContractExtension setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public ContractExtension setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public ContractExtension setDiscontinuo(boolean discontinuo) {
		this.discontinuo = discontinuo;
		return this;
	}
	

	@Override
	public String toString() {
		return "ContractExtension ["
				+ "cif=" + cif 
				+ ", sepeId=" + sepeId 
				+ ", regime=" + regime
				+ ", ctaCti=" + ctaCti 
				+ ", startDate=" + startDate 
				+ ", endDate=" + endDate 
				+ ", discontinuo=" + discontinuo 
				+ "]";
	}
	
}
