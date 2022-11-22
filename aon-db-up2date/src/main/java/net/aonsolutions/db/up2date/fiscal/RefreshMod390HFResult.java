package net.aonsolutions.db.up2date.fiscal;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModelDetail.FS_MODEL_DETAIL;

import java.io.Serializable;
import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RefreshMod390HFResult implements Update {
	
	public static final RefreshMod390HFResult REFRESH_MOD390_RESULT = new RefreshMod390HFResult();

	private RefreshMod390HFResult() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		try {
			dslContext.transaction( (config) -> {
				byte pending = 0;
				byte finished = 1;
				byte sent = 4;
				byte customerCheck = 6;
				byte customerAccepted = 7;
				byte customerRejected = 8;
				dslContext.select(FS_MODEL.ID,FS_MODEL.ADMINISTRATION,FS_MODEL.DOMAIN )
					.from(FS_MODEL)
					.where(FS_MODEL.MODEL.eq("390"))
					.and(FS_MODEL.STATUS.in(pending,finished,sent,customerCheck,customerAccepted,customerRejected))
					.fetch()
					.stream()
					.forEach(reco -> {
						Integer id = reco.getValue(FS_MODEL.ID);
						Administration admon = Administration.safeValueOf(reco.getValue(FS_MODEL.ADMINISTRATION));
						String resultKey = null;
						String typeKey = "390-CM004";
						if (admon == Administration.ALAVA) {
							resultKey = "390-AR13X";
						} else if (admon == Administration.BIZKAIA) {
							resultKey = "390-BZC110";
						} else if (admon == Administration.GIPUZKOA) {
							resultKey = "390-GP040";
						}
						if (resultKey != null) {
							Double result = dslContext.select(FS_MODEL_DETAIL.AMOUNT)
								.from(FS_MODEL_DETAIL)
								.where(FS_MODEL_DETAIL.FS_MODEL.eq(id))
								.and(FS_MODEL_DETAIL.TYPE.eq(resultKey))
								.fetch()
								.map(recRes -> recRes.getValue(FS_MODEL_DETAIL.AMOUNT))
								.stream()
								.findFirst()
								.orElse(Double.valueOf(0));
							Record1<String> decTypeRec =  dslContext.select(FS_MODEL_DETAIL.DESCRIPTION)
								.from(FS_MODEL_DETAIL)
								.where(FS_MODEL_DETAIL.FS_MODEL.eq(id))
								.and(FS_MODEL_DETAIL.TYPE.eq(typeKey))
								.fetch()
								.stream()
								.findFirst()
								.orElse(null);
							FiscalModelDeclarationType declarationType = null;
							if (decTypeRec != null) {
								String decType = decTypeRec.getValue(FS_MODEL_DETAIL.DESCRIPTION);
								declarationType = FiscalModelDeclarationType.safeValueOf(decType);
							}
							if (result != null) {
								dslContext.update(FS_MODEL)
									.set(FS_MODEL.RESULT,result)
									.set(FS_MODEL.DECLARATION_TYPE, (declarationType == null? null : declarationType.value()))
									.where(FS_MODEL.ID.eq(id))
									.execute();
							}
						}
					});
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}
	
	private static interface IAdministrationVisitor<T> {
		T visitAlava();
		T visitBizkaia();
		T visitGipuzkoa();
		T visitNavarra();
		T visitCommonTerritory();
		T visitUnknown();
	}

	enum Administration implements Serializable {
		
		ALAVA("Araba/Alava"){ @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitAlava();} },
		BIZKAIA("Bizkaia") { @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitBizkaia();} },
		GIPUZKOA("Gipuzkoa") { @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitGipuzkoa();} },
		NAVARRA("Navarra") { @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitNavarra();} },
		COMMON_TERRITORY("Territorio Com\u00FAn") { @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitCommonTerritory();} },
		UNKNOWN("Otro"){ @Override public <T> T visit(IAdministrationVisitor<T> visitor){ return visitor.visitUnknown();} },
		;

		private String description;
		
		private Administration(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}

		public byte value() {
			return (byte) ordinal();
		}
		
		public abstract <T> T visit(IAdministrationVisitor<T> visitor);
		

		public static Administration safeValueOf( String i ) {
			for (Administration rs : values()) {
				if(i.equalsIgnoreCase(rs.name()) || i.equalsIgnoreCase(rs.getDescription()))
					return rs;
			}
			return null;
		}
		
		
		public static Administration safeValueOf( Byte i ) {
			if (i == null) return null;
			return safeValueOf( i.intValue() ); 
		}
		
		public static Administration safeValueOf( Integer i ) {
			if (i == null) return null;
			if (i < 0 || i >= Administration.values().length) return null;
			return Administration.values()[i];
		}
	}

	enum FiscalModelDeclarationType {

		//-------------------------------------------------- FINANCE    -ASK BANK		
		 NEGATIVE 	("N","Negativa, cero \u00F3 sin. act."	,false		,false)
		,DEPOSIT  	("I","Ingreso"							,true		,true )
		,BANK     	("U","Domiciliaci\u00F3n"				,true		,true )
		,DEPOSIT_CCT("G","Ingreso a anotar en CCT"			,false		,false)
		,TO_DEDUCE	("B","A deducir"						,false		,false)
		
		,COMPENSATE	("C", "A compensar"						,false		,false)
		,PAYBACK	("D", "A devolver"						,true		,true )
		,PAYBACK_CCT("V", "Devoluci\u00F3n a anotar en CCT"	,false		,false)
		;


		private String value;
		private String description;
		private boolean mustCreateFinance;
		private boolean bankRequired;

		private FiscalModelDeclarationType(String value,String description,boolean mustCreateFinance,boolean bankRequired) {
			this.value = value;
			this.description = description;
			this.mustCreateFinance = mustCreateFinance;
			this.bankRequired = bankRequired;
		}
		
		public String getValue() {
			return value;
		}
		public String getDescription() {
			return description;
		}
		public boolean mustCreateFinance() {
			return mustCreateFinance;
		}
		public boolean isBankRequired() {
			return bankRequired;
		}
		
		public static FiscalModelDeclarationType safeValueOf( String value ) {
			if (value == null) return null;
			for (FiscalModelDeclarationType t : FiscalModelDeclarationType.values()) {
				if (t.getValue().equals(value)) return t;
			}
			return null;
		}

		public byte value() {
			return (byte) ordinal();
		}

		public static FiscalModelDeclarationType safeValueOf( Byte i ) {
			if (i == null) return null;
			return safeValueOf( i.intValue() ); 
		}
		public static FiscalModelDeclarationType safeValueOf( Integer i ) {
			if (i == null) return null;
			if (i < 0 || i >= FiscalModelDeclarationType.values().length) return null;
			return FiscalModelDeclarationType.values()[i];
		}
	}

}
