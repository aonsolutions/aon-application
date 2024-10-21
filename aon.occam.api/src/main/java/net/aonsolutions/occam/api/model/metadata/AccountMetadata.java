package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum AccountMetadata implements Serializable {
	 ID				{ @Override public <T> T visit(AccountMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN 		{ @Override public <T> T visit(AccountMetadataVisitor<T> v) { return v.visitDomain();} }
	,CODE			{ @Override public <T> T visit(AccountMetadataVisitor<T> v) { return v.visitCode();} }
	,DESCRIPTION	{ @Override public <T> T visit(AccountMetadataVisitor<T> v) { return v.visitDescription();} }
	,ALIAS			{ @Override public <T> T visit(AccountMetadataVisitor<T> v) { return v.visitAlias();} }
	,ENTRY_ENABLED	{ @Override public <T> T visit(AccountMetadataVisitor<T> v) { return v.visitEntryEnabled();} }
	,LEVEL			{ @Override public <T> T visit(AccountMetadataVisitor<T> v) { return v.visitLevel();} }
	,ACTIVE			{ @Override public <T> T visit(AccountMetadataVisitor<T> v) { return v.visitActive();} }
	,COST_CENTER	{ @Override public <T> T visit(AccountMetadataVisitor<T> v) { return v.visitCostCenter();} }
	;
	
	public abstract <T> T visit(AccountMetadataVisitor<T> v);
	public static interface AccountMetadataVisitor<T> {
		 T visitId();
		 T visitDomain();
		 T visitCode();
		 T visitDescription();
		 T visitAlias();
		 T visitEntryEnabled();
		 T visitLevel();
		 T visitActive();
		 T visitCostCenter();
	}
}
