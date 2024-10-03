package net.aonsolutions.db.up2date.finance;

import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.EnterpriseData;

import net.aonsolutions.db.up2date.Update;

public class InvofoxEnterpriseDataFix implements Update {

	public static final InvofoxEnterpriseDataFix INVOFOX_ENTERPRISE_DATA_FIX = new InvofoxEnterpriseDataFix();

	private InvofoxEnterpriseDataFix() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		
		List<EData> dataList = dslContext.select()
			.from(EnterpriseData.ENTERPRISE_DATA)
			.where(EnterpriseData.ENTERPRISE_DATA.NAME.isNull())
			.fetch().stream().map(r -> {
				EData edata = new EData();
				edata.setId(r.getValue(EnterpriseData.ENTERPRISE_DATA.ID));
				edata.setEnterprise(r.getValue(EnterpriseData.ENTERPRISE_DATA.ENTERPRISE));
				edata.setDomain(r.getValue(EnterpriseData.ENTERPRISE_DATA.DOMAIN));
				edata.setName(r.getValue(EnterpriseData.ENTERPRISE_DATA.NAME));
				edata.setExpression(r.getValue(EnterpriseData.ENTERPRISE_DATA.EXPRESSION));
				edata.setStartDate(r.getValue(EnterpriseData.ENTERPRISE_DATA.START_DATE));
				edata.setEndDate(r.getValue(EnterpriseData.ENTERPRISE_DATA.END_DATE));
				return edata;
			}).toList();
				
		dataList.stream().map(r -> r.getDomain()).distinct().forEach(domain -> {
			EData edata = dataList.stream().filter(r -> r.getDomain().equals(domain)).findFirst().orElse(null);
			if(edata != null) {
				Long count = dataList.stream().filter(r -> r.getDomain().equals(domain)).count();
				dslContext.insertInto(ENTERPRISE_DATA)
					.set(ENTERPRISE_DATA.DOMAIN, domain)
					.set(ENTERPRISE_DATA.ENTERPRISE, edata.getEnterprise())
					.set(ENTERPRISE_DATA.NAME, "INVOFOX")
					.set(ENTERPRISE_DATA.EXPRESSION, count.toString())
					.set(ENTERPRISE_DATA.START_DATE, edata.getStartDate())
					.set(ENTERPRISE_DATA.END_DATE, edata.getEndDate())
					.execute();				
			}
		});
				
		dslContext.delete(EnterpriseData.ENTERPRISE_DATA)
		.where(EnterpriseData.ENTERPRISE_DATA.NAME.isNull())
		.execute();
		
		System.out.println("[END]");
	}
	

	class EData {

		private Integer id;
		private Integer enterprise;
		private Integer domain;
		private String name;
		private String expression;
		private Date startDate;
		private Date endDate;

		public EData() {
			
		}
		
		public Integer getId() {
			return id;
		}
		
		public EData setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public Integer getEnterprise() {
			return enterprise;
		}
		
		public EData setEnterprise(Integer enterprise) {
			this.enterprise = enterprise;
			return this;
		}
		
		public Integer getDomain() {
			return domain;
		}
		
		public EData setDomain(Integer domain) {
			this.domain = domain;
			return this;
		}
		
		public String getName() {
			return name;
		}
		
		public EData setName(String name) {
			this.name = name;
			return this;
		}
		
		public String getExpression() {
			return expression;
		}
		
		public EData setExpression(String expression) {
			this.expression = expression;
			return this;
		}
		
		public Date getStartDate() {
			return startDate;
		}
		
		public EData setStartDate(Date startDate) {
			this.startDate = startDate;
			return this;
		}
		
		public Date getEndDate() {
			return endDate;
		}
		
		public EData setEndDate(Date endDate) {
			this.endDate = endDate;
			return this;
		}
	}

}
