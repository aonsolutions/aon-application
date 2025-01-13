package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

import net.aonsolutions.occam.api.model.metadata.MetadataVisitor.ActivityMetadataVisitor;

public enum ActivityMetadata implements Serializable {
	 ID { @Override public <T> T visit(ActivityMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN { @Override public <T> T visit(ActivityMetadataVisitor<T> v) { return v.visitDomain();} }
	,DESCRIPTION { @Override public <T> T visit(ActivityMetadataVisitor<T> v) { return v.visitDescription();} }
	,MAIN { @Override public <T> T visit(ActivityMetadataVisitor<T> v) { return v.visitMain();} }
	,IAE { @Override public <T> T visit(ActivityMetadataVisitor<T> v) { return v.visitIae();} }
	,CNAE { @Override public <T> T visit(ActivityMetadataVisitor<T> v) { return v.visitCnae();} }
	,SURCHARGE { @Override public <T> T visit(ActivityMetadataVisitor<T> v) { return v.visitSurcharge();} }
	,VAT_REGIME { @Override public <T> T visit(ActivityMetadataVisitor<T> v) { return v.visitVatRegime();} }
	,VAT_EXEMPTION_CAUSE { @Override public <T> T visit(ActivityMetadataVisitor<T> v) { return v.visitVatExemptionCause();} }
	,IRPF_REGIME { @Override public <T> T visit(ActivityMetadataVisitor<T> v) { return v.visitIrpfRegime();} }
	,START_DATE { @Override public <T> T visit(ActivityMetadataVisitor<T> v) { return v.visitStartDate();} }
	,END_DATE { @Override public <T> T visit(ActivityMetadataVisitor<T> v) { return v.visitEndDate();} }
	;

	public abstract <T> T visit(ActivityMetadataVisitor<T> v);
}
