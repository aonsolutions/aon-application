package net.aonsolutions.occam.api.metadata;

import java.io.Serializable;

public enum AccountingConfigurationMetadata implements Serializable {
	 ID
		{ @Override public <R,T> R visit(AccountingConfigurationMetadataVisitor<R,T> v, T t) { return v.visitId(t);} }
	,ACCOUNTS 
		{ @Override public <R,T> R visit(AccountingConfigurationMetadataVisitor<R,T> v, T t) { return v.visitAccounts(t);} }
	;
	public abstract <R,T> R visit(AccountingConfigurationMetadataVisitor<R,T> visitor, T t);

	public static interface AccountingConfigurationMetadataVisitor<R,T> {
		 R visitId( T t );
		 R visitAccounts( T t );
	}
}
