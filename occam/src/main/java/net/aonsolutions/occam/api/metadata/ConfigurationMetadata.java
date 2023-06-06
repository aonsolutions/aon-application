package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum ConfigurationMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(AccountingConfigurationMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,ACCOUNTING_CONFIGURATION 
		{ @Override public <R,T> R visit(AccountingConfigurationMetadataVisitor<R,T> v, T t) { return v.visitAccountingConfiguration(t);} }
	;
	public abstract <R,T> R visit(AccountingConfigurationMetadataVisitor<R,T> visitor, T t);

	public static interface AccountingConfigurationMetadataVisitor<R,T> {
		 R visitId( T t );
		 R visitAccountingConfiguration( T t );
	}
}
