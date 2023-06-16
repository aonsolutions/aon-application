package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum AccountingConfigurationMetadata implements Serializable {
	 ID
		{ @Override public <R> R visit(AccountingConfigurationMetadataVisitor<R> v) { return v.visitId();} }
	,ACCOUNTS 
		{ @Override public <R> R visit(AccountingConfigurationMetadataVisitor<R> v) { return v.visitAccounts();} }
	;
	public abstract <R> R visit(AccountingConfigurationMetadataVisitor<R> visitor);

	public static interface AccountingConfigurationMetadataVisitor<R> {
		 R visitId();
		 R visitAccounts();
	}
}
