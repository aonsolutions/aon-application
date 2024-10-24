package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

public enum SSRegimeType  implements Serializable {
	
	GENERAL("0111")
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitGeneralRegime(this);
		}
	},
	AGRICULTURAL("0163")
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitAgriculturalRegime(this);
		}
	},
	DOMESTIC_EMPLOYEES("0138")
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitDomesticEmployeesRegime(this);
		}
	},
	SELF_EMPLOYED("0521")
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitSelfEmployedRegime(this);
		}
	},
	COAL_MINING("0911")
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitCoalMiningRegime(this);
		}
	},
	SEA_WORKERS("0800")
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitSeaWorkersRegime(this);
		}
	},
	STUDENT_INSURANCE("1911")
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitStudentInsuranceRegime(this);
		}
	},
	ARTIST("0112")
	{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitArtistRegime(this);
		}
	},
	ISFAS{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitIsfasRegime(this);
		}
	},
	MUFACE{
		@Override
		public <E> E accept(SSRegimeTypeVisitor<E> visitor) {
			return visitor.visitMufaceRegime(this);
		}
	}
	;

	public abstract <E> E accept(SSRegimeTypeVisitor<E> visitor);
	public interface  SSRegimeTypeVisitor<E> {
		E visitGeneralRegime(SSRegimeType ssRegimeType);
		E visitAgriculturalRegime(SSRegimeType ssRegimeType);
		E visitDomesticEmployeesRegime(SSRegimeType ssRegimeType);
		E visitSelfEmployedRegime(SSRegimeType ssRegimeType);
		E visitCoalMiningRegime(SSRegimeType ssRegimeType);
		E visitSeaWorkersRegime(SSRegimeType ssRegimeType);
		E visitStudentInsuranceRegime(SSRegimeType ssRegimeType);
		E visitArtistRegime(SSRegimeType ssRegimeType);
		E visitIsfasRegime(SSRegimeType ssRegimeType);
		E visitMufaceRegime(SSRegimeType ssRegimeType);
	}
	
	private String code;
	
	private SSRegimeType(){
	}
	private SSRegimeType(String code){
		this.code = code;
	}
	
	public String getCode(){
		return code;
	}

	public byte value() {
		return (byte) ordinal();
	}
	
	public static Optional<SSRegimeType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<SSRegimeType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= SSRegimeType.values().length) return Optional.empty();
		return Optional.of(SSRegimeType.values()[i]);
	}
}