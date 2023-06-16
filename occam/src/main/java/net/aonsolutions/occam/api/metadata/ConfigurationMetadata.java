package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum ConfigurationMetadata implements Serializable {
	 ID
		{ @Override public <R> R visit(ConfigurationMetadataVisitor<R> v) { return v.visitId();} }
	,ACCOUNTING_CONFIGURATION 
		{ @Override public <R> R visit(ConfigurationMetadataVisitor<R> v) { return v.visitAccountingConfiguration();} }
	;
	public abstract <R> R visit(ConfigurationMetadataVisitor<R> visitor);

	public static interface ConfigurationMetadataVisitor<R> {
		 R visitId();
		 R visitAccountingConfiguration();
	}
}
