package net.aonsolutions.occam.api.model.type;

import java.util.Optional;

public enum AccountEntryOrder {
	  PERIOD_JOURNAL("Ejerc., n\u00BA diario, fecha")
	  	{ @Override public <T> T visit(AccountEntryOrderVisitor<T> visitor) { return visitor.visitPeriodJournal();} }
	 ,CREATION_DATE_DESC("Fecha creaci\u00F3n, descendente")
	  	{ @Override public <T> T visit(AccountEntryOrderVisitor<T> visitor) { return visitor.visitCreationDateDesc();} }
	 ,MODIFICATION_DATE_DESC("Fecha modificaci\u00F3n, descendente")
  		{ @Override public <T> T visit(AccountEntryOrderVisitor<T> visitor) { return visitor.visitModificationDateDesc();} }
	;
	
	private String description;
	
	private AccountEntryOrder(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public byte value() {
		return (byte) ordinal();
	}
	
	public static Optional<AccountEntryOrder> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<AccountEntryOrder> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= AccountEntryOrder.values().length) return Optional.empty();
		return Optional.of(AccountEntryOrder.values()[i]);
	}
	
	public static AccountEntryOrder DEFAULT = AccountEntryOrder.CREATION_DATE_DESC;

	public abstract <T> T visit(AccountEntryOrderVisitor<T> visitor);
	public static interface AccountEntryOrderVisitor<T> {
		T visitPeriodJournal();
		T visitCreationDateDesc();
		T visitModificationDateDesc();
	}
}
