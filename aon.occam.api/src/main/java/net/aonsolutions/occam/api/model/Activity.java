package net.aonsolutions.occam.api.model;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.ActivityMetadata;
import net.aonsolutions.occam.api.model.type.IRPFRegime;
import net.aonsolutions.occam.api.model.type.VATExemptionCause;
import net.aonsolutions.occam.api.model.type.VATRegime;

public class Activity extends AonEntity<ActivityMetadata> {

	private static final long serialVersionUID = 1603402672575353203L;
	
	private Integer id;
	private Integer domain;
	private String description;
	private boolean main;
	private Date startDate;
	private Date endDate;
	private Cnae cnae;
	private Iae iae;
	private VATRegime vatRegime;
	private boolean surcharge;
	private VATExemptionCause vatExemptionCause;
	private IRPFRegime irpfRegime;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Activity markAsClean() {
		super.markAsClean();
		return this; 
	}

	public Integer getId() {
		return id;
	}
	public Activity setId(Integer id) {
		checkIfDirty( this.id,id, ActivityMetadata.ID);
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Activity setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, ActivityMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public Activity setDescription(String description) {
		checkIfDirty( this.description,description, ActivityMetadata.DESCRIPTION);
		this.description = description;
		return this;
	}
	
	public boolean isMain() {
		return main;
	}
	public Activity setMain(boolean main) {
		checkIfDirty( this.main,main, ActivityMetadata.MAIN);
		this.main = main;
		return this;		
	}
	
	public Date getStartDate() {
		return startDate;
	}
	public Activity setStartDate(Date startDate) {
		checkIfDirty( this.startDate,startDate, ActivityMetadata.START_DATE);
		this.startDate = startDate;
		return this;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	public Activity setEndDate(Date endDate) {
		checkIfDirty( this.endDate,endDate, ActivityMetadata.END_DATE);
		this.endDate = endDate;
		return this;
	}
	
	public Optional<Cnae> getCnae() {
		return Optional.ofNullable(cnae);
	}
	public Activity setCnae(Cnae cnae) {
		checkIfDirty( this.cnae,cnae, ActivityMetadata.CNAE);
		this.cnae = cnae;
		return this;		
	}

	public Optional<Iae> getIae() {
		return Optional.ofNullable(iae);
	}
	public Activity setIae(Iae iae) {
		checkIfDirty( this.iae,iae, ActivityMetadata.IAE);
		this.iae = iae;
		return this;
	}
	
	public VATRegime getVatRegime() {
		return vatRegime;
	}
	public Activity setVatRegime(VATRegime vatRegime) {
		checkIfDirty( this.vatRegime,vatRegime, ActivityMetadata.VAT_REGIME);
		this.vatRegime = vatRegime;
		return this;
	}
	
	public boolean isSurcharge() {
		return surcharge;
	}
	public Activity setSurcharge(boolean surcharge) {
		checkIfDirty( this.surcharge,surcharge, ActivityMetadata.SURCHARGE);
		this.surcharge = surcharge;
		return this;
	}
	
	public VATExemptionCause getVatExemptionCause() {
		return vatExemptionCause;
	}
	public Activity setVatExemptionCause(VATExemptionCause vatExemptionCause) {
		checkIfDirty( this.vatExemptionCause,vatExemptionCause, ActivityMetadata.VAT_EXEMPTION_CAUSE);
		this.vatExemptionCause = vatExemptionCause;
		return this;
	}
	
	public IRPFRegime getIrpfRegime() {
		return irpfRegime;
	}
	public Activity setIrpfRegime(IRPFRegime irpfRegime) {
		checkIfDirty( this.irpfRegime,irpfRegime, ActivityMetadata.IRPF_REGIME);
		this.irpfRegime = irpfRegime;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Account other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
