package net.aonsolutions.db.up2date.fiscal;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep3;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.AlcatrazRecord;

import net.aonsolutions.db.up2date.Update;

public class Mod3032022AlcatrazBind implements Update {
	
	public static final Mod3032022AlcatrazBind MOD303_2022_ALCATRAZ_BIND = new Mod3032022AlcatrazBind();

	private Mod3032022AlcatrazBind() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;
		
		SimpleDateFormat formatter =  new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		java.sql.Date yearStart = new java.sql.Date(  
				Date.from(
					LocalDate.now().with( firstDayOfYear() ).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
				).getTime());
		System.out.println( "yearStart ---> " + yearStart);
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		InsertValuesStep3<AlcatrazRecord,Integer,Integer,Integer> insertSentece = dslContext.insertInto(ALCATRAZ,ALCATRAZ.DOMAIN,ALCATRAZ.FS_MODEL,ALCATRAZ.INVOICE)
				.values((Integer) null,(Integer) null,(Integer) null);
		try {
			dslContext.transaction( config -> {
				dslContext.select(FS_MODEL.ID
						,FS_MODEL.ADMINISTRATION
						,FS_MODEL.YEAR
						,FS_MODEL.PERIOD
						,FS_MODEL.DOMAIN
						,DOMAIN.NAME
						,FS_MODEL.COMMENTS
						,FS_MODEL.CREATION_DATE )
					.from(FS_MODEL)
					.innerJoin(DOMAIN).on(FS_MODEL.DOMAIN.eq(DOMAIN.ID))
					.where(FS_MODEL.MODEL.eq("IVA"))
					.and(FS_MODEL.YEAR.eq(2022))
					.fetch()
					.stream()
					.filter(model -> !dslContext.select(ALCATRAZ.ID)
							.from(ALCATRAZ)
							.where(ALCATRAZ.FS_MODEL.eq(model.getValue(FS_MODEL.ID)))
							.stream()
							.findFirst()
							.isPresent())
					.forEach(model -> {
						Integer modelId = model.getValue(FS_MODEL.ID);
						Administration modelAdmon = Administration.safeValueOf( model.getValue(FS_MODEL.ADMINISTRATION));
						Integer modelYear = model.getValue(FS_MODEL.YEAR);
						Period modelPeriod = Period.safeValueOf( model.getValue(FS_MODEL.PERIOD));
						Integer modelDomain = model.getValue(FS_MODEL.DOMAIN);
						String modelDomainName = model.getValue(DOMAIN.NAME);
						String modelComments = model.getValue(FS_MODEL.COMMENTS);
						Timestamp modelCreationDate = model.getValue(FS_MODEL.CREATION_DATE);
						System.out.println(modelId
							+ " " + modelDomainName 
							+ " (" + modelDomain + " )" 
							+ " " + modelAdmon.getDescription()
							+ " " + modelYear
							+ " " + modelPeriod.getDescription()
							+ " " + formatter.format(modelCreationDate)
						);
						java.sql.Date modelEnd = new java.sql.Date(modelPeriod.getEndDate( modelYear ).getTime());
						BatchBindStep alcatrazBatch = dslContext.select(INVOICE.ID)
							.from(INVOICE)
							.where(INVOICE.DOMAIN.eq(modelDomain))
							.and(INVOICE.TYPE.ne( (byte) 3)) // Gastos no deducibles. 
							.and(INVOICE.TAX_DATE.between( yearStart, modelEnd))
							.and(INVOICE.CREATION_DATE.le( modelCreationDate ))
							.stream()
							.map(recId -> recId.getValue(INVOICE.ID))
							.filter(invId -> !dslContext.select(ALCATRAZ.ID)
									.from(ALCATRAZ)
									.where(ALCATRAZ.INVOICE.eq(invId))
									.stream()
									.findFirst()
									.isPresent())
							.map( invId  -> new Object[] {modelDomain,modelId,invId})
							.reduce(dslContext.batch(insertSentece)
								,BatchBindStep::bind
								,(batch1, batch2) -> batch1);
						if (alcatrazBatch.size() > 0) {
							int[] result = alcatrazBatch.execute();
							modelComments = (modelComments==null?"":modelComments + " ") + "[#ALCATRAZ AUTH UPDATE#]"; 
							dslContext.update(FS_MODEL)
								.set(FS_MODEL.COMMENTS,modelComments)
								.where(FS_MODEL.ID.eq(modelId))
								.execute();
							System.out.println( "Invoices ..; " + result);  	
						} else {
							System.out.println( "NO Invoices ..; ");
						}
					});
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	private enum Administration implements Serializable {
		
		ALAVA("Araba/Alava")
		,BIZKAIA("Bizkaia")
		,GIPUZKOA("Gipuzkoa")
		,NAVARRA("Navarra")
		,COMMON_TERRITORY("Territorio Com\u00FAn")
		,UNKNOWN("Otro")
		;

		private String description;
		
		private Administration(String description) {
			this.description = description;
		}
		private String getDescription() {
			return description;
		}
		private static Administration safeValueOf( Byte i ) {
			if (i == null) return null;
			return safeValueOf( i.intValue() ); 
		}
		
		private static Administration safeValueOf( Integer i ) {
			if (i == null) return null;
			if (i < 0 || i >= Administration.values().length) return null;
			return Administration.values()[i];
		}
		
	}

	private enum Period implements Serializable {

		M01(0,0,"01","Enero"),
		M02(1,1,"02","Febrero"),
		M03(2,2,"03","Marzo"),
		M04(3,3,"04","Abril"),
		M05(4,4,"05","Mayo"),
		M06(5,5,"06","Junio"),
		M07(6,6,"07","Julio"),
		M08(7,7,"08","Agosto"),
		M09(8,8,"09","Septiembre"),
		M10(9,9,"10","Octubre"),
		M11(10,10,"11","Noviembre"),
		M12(11,11,"12","Diciembre"),
		T1(0,2,"1T","1\u00BA Trim."),	//12
		T2(3,5,"2T","2\u00BA Trim."),	//13
		T3(6,8,"3T","3\u00BA Trim."),	//14
		T4(9,11,"4T","4\u00BA Trim."),	//15
		YEAR(0,11,"An","Anual"); //16
		
		private int dueMonth;
		private String description;
		
		private Period(int startMonth,int dueMonth,String name,String description) {
			this.dueMonth= dueMonth;
			this.description= description;
		}

		Date getEndDate(Integer modelYear) {
			return Date.from(
				LocalDate.of(modelYear, (getDueMonth() + 1), 1)
					.with( lastDayOfMonth() )
					.atStartOfDay()
					.atZone(ZoneId.systemDefault())
					.toInstant()
			);
		}

//		Date getStartDate(Integer modelYear) {
//			return Date.from(
//				LocalDate.of(modelYear, (getStartMonth() + 1), 1)
//					.with( firstDayOfMonth() )
//					.atStartOfDay()
//					.atZone(ZoneId.systemDefault())
//					.toInstant()
//			);
//		}
		
	    private String getDescription() {
	    	return description;
		}
	    
//	    private int getStartMonth() {
//			return startMonth;
//		}
		
		private int getDueMonth() {
			return dueMonth;
		}

		private static Period safeValueOf( Byte i ) {
			if (i == null) return null;
			if (i < 0 || i >= Period.values().length) return null;
			return Period.values()[i];
		}
		
	}
}
