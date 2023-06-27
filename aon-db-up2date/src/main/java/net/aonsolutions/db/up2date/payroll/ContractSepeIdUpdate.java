package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.ContractDataRecord;

import net.aonsolutions.db.up2date.Update;

public class ContractSepeIdUpdate implements Update {
	
	public static final ContractSepeIdUpdate CONTRACTSEPEIDUPDATE = new ContractSepeIdUpdate();
	
	private static final String SEPE_ID = "SEPE_ID";
	private static final String COMUNICATION_DATE = "COMUNICATION_DATE";
	private static final String SEPE_TRANSFORM_ID = "SEPE_TRANSFORM_ID";
	private static final String COMUNICATION_TRANSFORM_DATE = "COMUNICATION_TRANSFORM_DATE";
	private static final String SEPE_EXTENSION_ID_ = "SEPE_EXTENSION_ID_";
	private static final String COMUNICATION_EXTENSION_DATE_ = "COMUNICATION_EXTENSION_DATE_";
	
	private ContractSepeIdUpdate() {}

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( config -> updateSepeId(dslContext));
	}

	private void updateSepeId(DSLContext dslContext) {
		
		Result<ContractDataRecord> sepeCDRecords = dslContext.selectFrom(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq(SEPE_ID))
				.or(CONTRACT_DATA.NAME.eq(COMUNICATION_DATE))
				.or(CONTRACT_DATA.NAME.eq(SEPE_TRANSFORM_ID))
				.or(CONTRACT_DATA.NAME.eq(COMUNICATION_TRANSFORM_DATE))
				.or(CONTRACT_DATA.NAME.like(SEPE_EXTENSION_ID_ + "%"))
				.or(CONTRACT_DATA.NAME.like(COMUNICATION_EXTENSION_DATE_ + "%"))
				.fetch();
		
		Integer sepeIdUpdates = 0;
		Integer comunicationDateUpdates = 0;
		Integer sepeTransformIdUpdates = 0;
		Integer comunicationTransformDateUpdates = 0;
		Integer sepeExtensionIdUpdates = 0;
		Integer comunicationExtensionDateUpdates = 0;
		
		
		for(ContractDataRecord sepeCDRecord : sepeCDRecords) {
			dslContext.insertInto(CONTRACT_INFO)
				.set(CONTRACT_INFO.DOMAIN, sepeCDRecord.getDomain())
				.set(CONTRACT_INFO.CONTRACT, sepeCDRecord.getContract())
				.set(CONTRACT_INFO.NAME, sepeCDRecord.getName())
				.set(CONTRACT_INFO.EXPRESSION, sepeCDRecord.getExpression())
				.set(CONTRACT_INFO.START_DATE, sepeCDRecord.getStartDate())
				.set(CONTRACT_INFO.END_DATE, sepeCDRecord.getEndDate())
				.execute();
			
			dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.ID.eq(sepeCDRecord.getId()))
				.execute();
			
			if(sepeCDRecord.getName().equals(SEPE_ID)) sepeIdUpdates++;
			else if(sepeCDRecord.getName().equals(COMUNICATION_DATE)) comunicationDateUpdates++;
			else if(sepeCDRecord.getName().equals(SEPE_TRANSFORM_ID)) sepeTransformIdUpdates++;
			else if(sepeCDRecord.getName().equals(COMUNICATION_TRANSFORM_DATE)) comunicationTransformDateUpdates++;
			else if(sepeCDRecord.getName().contains(SEPE_EXTENSION_ID_)) sepeExtensionIdUpdates++;
			else if(sepeCDRecord.getName().contains(COMUNICATION_EXTENSION_DATE_)) comunicationExtensionDateUpdates++;
			
		}
		
		System.out.println("Contract Sepe Id updates : " + sepeIdUpdates);
		System.out.println("Contract Sepe Comunication Date updates : " + comunicationDateUpdates);
		System.out.println("Contract Sepe Transform Id updates : " + sepeTransformIdUpdates);
		System.out.println("Contract Sepe Comunication Transform Date updates : " + comunicationTransformDateUpdates);
		System.out.println("Contract Sepe Extension Id updates : " + sepeExtensionIdUpdates);
		System.out.println("Contract Sepe Comunication Extension Date updates : " + comunicationExtensionDateUpdates);
	}

	
}
