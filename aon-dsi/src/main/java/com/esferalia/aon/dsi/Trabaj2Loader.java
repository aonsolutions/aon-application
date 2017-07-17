package com.esferalia.aon.dsi;

//i.puig@analize.es

import static com.esferalia.aon.dsi.CconceLoader.getExpression;
import static com.esferalia.aon.dsi.jooq.tables.Fnempres.FNEMPRES;
import static com.esferalia.aon.dsi.jooq.tables.Fntconce.FNTCONCE;
import static com.esferalia.aon.dsi.jooq.tables.Fntrabaj.FNTRABAJ;
import static com.esferalia.aon.dsi.jooq.tables.Fnvarios.FNVARIOS;
import static com.esferalia.aon.dsi.util.EnumUtils.enum2Byte;
import static com.esferalia.aon.dsi.util.EnumUtils.getDocumentType;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes.Name;

import org.apache.commons.lang.StringUtils;
import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;

import net.aonsolutions.core.dbutils.AonSQLException;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.dsi.jooq.tables.records.FnnomincRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FntconceRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FntrabajRecord;
import com.esferalia.aon.dsi.jooq.tables.records.Fnvario2Record;
import com.esferalia.aon.dsi.jooq.tables.records.FnvariosRecord;
import com.esferalia.aon.jooq.tables.Person;
import com.esferalia.aon.jooq.tables.SalaryBonus;
import com.esferalia.aon.jooq.tables.SalaryCost;
import com.esferalia.aon.jooq.tables.SalaryData;
import com.esferalia.aon.jooq.tables.SalaryDeduction;
import com.esferalia.aon.jooq.tables.SalaryEmbargo;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractPaymentRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.PersonRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.RmediaRecord;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;

public class Trabaj2Loader extends AbstractLoader implements
		NominaLoader.Callback {

	public static interface Listener {
		void onContractIgnored(ContractRecord contract, PersonRecord person);

		void onContractUpdated(ContractRecord contract, PersonRecord person);

		void onContractInserted(ContractRecord contract, PersonRecord person);
	}

	public static class NullListener implements Listener {

		static Listener NULL_LISTENER = new NullListener();

		@Override
		public void onContractIgnored(ContractRecord contract,
				PersonRecord person) {
		}

		@Override
		public void onContractUpdated(ContractRecord contract,
				PersonRecord person) {
		}

		@Override
		public void onContractInserted(ContractRecord contract,
				PersonRecord person) {
		}

	}

	public static interface Callback {

		int getDomain(FntrabajRecord trabaj);

		int getWorplace(FntrabajRecord trabaj);

		Integer getCategory(FntrabajRecord trabaj);

		PaymentConceptRecord getConcept(FntconceRecord conce);
	}

	private static class Ids {
		int registryId;
		int contractId;

		public Ids(int registryId, int contractId) {
			this.registryId = registryId;
			this.contractId = contractId;
		}

	}

	private static class PersonContractRecord {
		private PersonRecord person;
		private ContractRecord contract;

		public PersonContractRecord(PersonRecord personRecord,
				ContractRecord contractRecord) {
			this.person = personRecord;
			this.contract = contractRecord;
		}

	}

	private Date from;

	private boolean replace;

	private Listener listener;

	private List<Ids> toDelete;

	private Map<String, int[]> ssIdsMap;

	private PersonRecord personRecord;
	private RegistryRecord registryRecord;

	private InsertSetMoreStep<PersonRecord> insertSetMoreStepPerson;
	private InsertSetMoreStep<RmediaRecord> insertSetMoreStepRmedia;
	private InsertSetMoreStep<RegistryRecord> insertSetMoreStepRegistry;
	private InsertSetMoreStep<RaddressRecord> insertSetMoreStepRaddress;
	private InsertSetMoreStep<ContractRecord> insertSetMoreStepContract;
	private InsertSetMoreStep<ContractDataRecord> insertSetMoreStepContractData;
	private InsertSetMoreStep<ContractPaymentRecord> insertSetMoreStepContractPayment;

	public Trabaj2Loader(DSLContext dsiContext, DSLContext aonContext) {
		super(dsiContext, aonContext);
		this.ssIdsMap = new Hashtable<String, int[]>();

		this.from = new Date(0); // January 1, 1970;
		this.toDelete = new LinkedList<Ids>();
		this.listener = NullListener.NULL_LISTENER;
	}

	public Trabaj2Loader setFrom(Date from) {
		this.from = from;
		return this;
	}

	public Trabaj2Loader setReplace(boolean replace) {
		this.replace = replace;
		return this;
	}

	public Trabaj2Loader setListener(Listener listener) {
		this.listener = listener;
		return this;
	}

	public void loadTrabj2(Callback cb, Condition... conditions)
			throws AonSQLException {

		//@formatter:off
		Condition trabj2empres = 
				FNTRABAJ.F20SSCODEM.eq(FNEMPRES.F20SSCOD)
				.and(FNTRABAJ.F20SSNUMEM.eq(FNEMPRES.F20SSNUM));
		Cursor<Record> trabjCursor = dsiContext.select()
				.from(FNTRABAJ)
				.join(FNEMPRES)
				.on(trabj2empres)
				.where(conditions)
				.orderBy(FNTRABAJ.F20SSCOD, FNTRABAJ.F20SSNUM, FNTRABAJ.F20FALTA)
				.fetchLazy();
		//@formatter:on

		//@formatter:off
		Condition tconce2empres = 
				FNTCONCE.F21SSCODEM.eq(FNEMPRES.F20SSCOD)
				.and(FNTCONCE.F21SSNUMEM.eq(FNEMPRES.F20SSNUM));

		Cursor<Record> tconceCursor = dsiContext.select()
				.from(FNTCONCE)
				.join(FNEMPRES)
				.on(tconce2empres)
				.where(conditions)
				.orderBy(FNTCONCE.F21SSCODEM, 
						FNTCONCE.F21SSNUMEM ,
						FNTCONCE.F21SSCOD, 
						FNTCONCE.F21SSNUM, 
						FNTCONCE.F21FALTA)
				.fetchLazy();
		//@formatter:on

		FntconceRecord tconce = tconceCursor.hasNext() ? tconceCursor
				.fetchOneInto(FNTCONCE) : null;

		while (trabjCursor.hasNext()) {
			FntrabajRecord trabaj = trabjCursor.fetchOneInto(FNTRABAJ);
			int domain = cb.getDomain(trabaj);
			int workplace = cb.getWorplace(trabaj);

			String key = trabaj.getF20sscod() + trabaj.getF20ssnum()
					+ trabaj.getF20falta();

			List<FntconceRecord> tconces = new ArrayList<FntconceRecord>();
			while (tconce != null 
					//@formatter:off
					&& StringUtils.equals(trabaj.getF20ssnumem(), tconce.getF21ssnumem()) 
					&& StringUtils.equals(trabaj.getF20sscodem(), tconce.getF21sscodem()) 
					&& StringUtils.equals(trabaj.getF20ssnum(), tconce.getF21ssnum()) 
					&& StringUtils.equals(trabaj.getF20sscod(), tconce.getF21sscod()) 
					&& trabaj.getF20falta().equals(tconce.getF21falta())
					//@formatter:on
			) {
				tconces.add(tconce);
				tconce = tconceCursor.hasNext() ? tconceCursor
						.fetchOneInto(FNTCONCE) : null;
			}

			int registryId;
			int contractId;

			PersonContractRecord record = getFullContract(trabaj, workplace);

			if (record != null) {
				contractId = record.contract.getId();
				registryId = record.contract.getPerson();
				if (!replace) {
					ssIdsMap.put(key, new int[] { domain, contractId });
					listener.onContractIgnored(record.contract, record.person);
					continue;
				}
				toDelete.add(new Ids(registryId, contractId));
				loadTrabjRegistry(trabaj, domain, registryId, cb);

				ContractRecord contract = loadTrabjContract(trabaj, domain,
						workplace, registryId, contractId, cb);
				listener.onContractUpdated(contract, lastPerson());

			} else {
				contractId = next(CONTRACT.getIdentity());
				registryId = next(REGISTRY.getIdentity());
				loadTrabjRegistry(trabaj, domain, registryId, cb);
				ContractRecord contract = loadTrabjContract(trabaj, domain,
						workplace, registryId, contractId, cb);
				listener.onContractInserted(contract, lastPerson());
			}

			for (FntconceRecord _tconce : tconces)
				loadConce(_tconce, contractId, domain, cb);

			ssIdsMap.put(key, new int[] { domain, contractId });

		}
	}

	public void loadVarios(Condition... conditions) {
		//@formatter:off
		Condition variosempres = 
				FNVARIOS.F30SSCODEM.eq(FNEMPRES.F20SSCOD)
				.and(FNVARIOS.F30SSNUMEM.eq(FNEMPRES.F20SSNUM));

		Cursor<Record> variosCursor = dsiContext.select()
				.from(FNVARIOS)
				.join(FNEMPRES)
				.on(variosempres)
				.where(conditions)
				.orderBy(FNVARIOS.F30SSCODEM, 
						FNVARIOS.F30SSNUMEM ,
						FNVARIOS.F30SSCOD, 
						FNVARIOS.F30SSNUM, 
						FNVARIOS.F30FALTA, 
						FNVARIOS.F30MESVAR )
				.fetchLazy();
		//@formatter:on

		while (variosCursor.hasNext()) {
			FnvariosRecord varios = variosCursor.fetchOneInto(FNVARIOS);
		}

	}

	public void execute() {
		delete();
		insert();
	}

	// ------------------------------------------------------------------------
	@Override
	public int getDomain(FnnomincRecord nominc) {
		return ssIdsMap.get(nominc.getF30sscod() + nominc.getF30ssnum()
				+ nominc.getF30falta())[0];
	}

	@Override
	public int getContract(FnnomincRecord nominc) {
		return ssIdsMap.get(nominc.getF30sscod() + nominc.getF30ssnum()
				+ nominc.getF30falta())[1];
	}

	// ------------------------------------------------------------------------

	private PersonRecord getPerson(FntrabajRecord trabaj, int domain) {
		//@formatter:off
		return aonContext.
		select()
		.from(PERSON)
		.where(PERSON.DOMAIN.eq(domain))
		.and(PERSON.SOCIAL_SECURITY_NUM.eq(getSocialSecurityNum(trabaj)))
		.fetchOneInto(PERSON)
		;
		//@formatter:on
	}

	private void loadTrabjRegistry(FntrabajRecord trabaj, int domain,
			int registry, Callback cb) {
		
		registryRecord = new RegistryRecord();
		registryRecord.setId(registry);
		registryRecord.setDomain(domain);
		registryRecord.setType(enum2Byte(RegistryType.NATURAL));
		registryRecord.setName(getFullName(trabaj));
		registryRecord.setDocument(trabaj.getF20dni());
		registryRecord.setDocumentType(enum2Byte(getDocumentType(trabaj.getF20dni())));
		
		registryRecord.setAlias(null);
		registryRecord.setNationality("ES");
		registryRecord.setDocumentCountry("ES");
		registryRecord.setSecurityLevel((byte)0);
		
		InsertSetStep<RegistryRecord> insertSetStepRegistry = getRegistryInsertSetStep();
		//@formatter:off
		insertSetMoreStepRegistry = insertSetStepRegistry
				.set(registryRecord);
		//@formatter:on

		StringBuffer address2 = new StringBuffer();
		if (!isBlank(trabaj.getF20piso()))
			address2.append(String.format("%s º", trabaj.getF20piso()));
		if (!isBlank(trabaj.getF20puerta()))
			address2.append(String.format(" %s", trabaj.getF20puerta()));

		//@formatter:off
		int mainAddressId = getRaddressId(trabaj, registry, AddressType.MAIN);
		InsertSetStep<RaddressRecord> insertSetStepRaddress= getRaddressInsertSetStep();
		insertSetMoreStepRaddress = insertSetStepRaddress
				.set(RADDRESS.ID, mainAddressId)
				.set(RADDRESS.REGISTRY, registry)
				.set(RADDRESS.DOMAIN, domain)
				.set(RADDRESS.TYPE, enum2Byte(AddressType.MAIN))
				.set(RADDRESS.NUMBER, trabaj.getF20numero())
				.set(RADDRESS.ADDRESS, trabaj.getF20direcci())
				.set(RADDRESS.ADDRESS2, address2.toString())
				.set(RADDRESS.CITY, trabaj.getF20poblaci())
				.set(RADDRESS.MUNICIPALITY_CODE,
						getMunicipality(trabaj.getF20poblaci()))
				.set(RADDRESS.ZIP, trabaj.getF20cp())
				.set(RADDRESS.GEOZONE, getGeozone(trabaj.getF20provin(), domain));
		//@formatter:on

		// telephone
		String telef = trabaj.getF20telef();
		if (!isBlank(telef)) {
			
//			int telephoneId = getRMediaId(trabaj, registry,
//					MediaType.FIXED_PHONE);
			
//			int telephoneId = next(RMEDIA.getIdentity());
			
			//@formatter:off
			InsertSetStep<RmediaRecord> insertSetStepRmedia= getRmediaInsertSetStep();
			
			insertSetMoreStepRmedia = insertSetStepRmedia
//					.set(RMEDIA.ID, telephoneId)
					.set(RMEDIA.REGISTRY, registry)
					.set(RMEDIA.DOMAIN, domain)
					.set(RMEDIA.MEDIA, enum2Byte(MediaType.FIXED_PHONE))
					.set(RMEDIA.VALUE, telef);
			// TODO : raddress ?
			//@formatter:on
		}

		String sexo = trabaj.getF20sexo();
		Gender gender = Gender.UNKNOWN;
		if ("M".equals(sexo))
			gender = Gender.FEMALE;
		else if ("H".equals(sexo))
			gender = Gender.MALE;

		personRecord = new PersonRecord();
		personRecord.setRegistry(registry);
		personRecord.setDomain(domain);
		personRecord.setBirthDate(trabaj.getF20fnac());
		personRecord.setGender(enum2Byte(gender));
		personRecord.setName(trabaj.getF20nombre());
		personRecord.setFirstSurname(trabaj.getF20apell1());
		personRecord.setSecondSurname(trabaj.getF20apell2());
		personRecord.setSocialSecurityNum(getSocialSecurityNum(trabaj));
		personRecord.setMaritalStatus(enum2Byte(MaritalStatus.UNKNOWN));
		// TODO : MARITAL_STATUS, F20NOMBREC?
		InsertSetStep<PersonRecord> insertSetStepPerson = getPersonInsertSetStep();
		insertSetMoreStepPerson = insertSetStepPerson.set(personRecord);
	}

	private ContractRecord loadTrabjContract(FntrabajRecord trabaj, int domain,
			int workplace, int person, int contract, Callback cb) {

		Integer reg = null;
		if (!isBlank(trabaj.getF20matric()))
			try {
				reg = Integer.parseInt(trabaj.getF20matric());
			} catch (NumberFormatException e) {

			}

		SSRegimeType regimeType = SSRegimeType.GENERAL;
		if (StringUtils.equalsIgnoreCase("S", trabaj.getF20autono()))
			regimeType = SSRegimeType.SELF_EMPLOYED;

		ContractRecord contractRecord = new ContractRecord();
		contractRecord.setValue(CONTRACT.ID, contract);
		contractRecord.setValue(CONTRACT.DOMAIN, domain);
		contractRecord.setValue(CONTRACT.PERSON, person);
		contractRecord.setValue(CONTRACT.REGISTRATION, reg);
		contractRecord.setValue(CONTRACT.WORKPLACE, workplace);
		contractRecord.setValue(CONTRACT.START_DATE, trabaj.getF20falta());
		contractRecord.setValue(CONTRACT.END_DATE, trabaj.getF20fbaja());
		contractRecord.setValue(CONTRACT.DESCRIPTION, trabaj.getF20puesto());
		contractRecord.setValue(CONTRACT.SENIORITY_DATE, trabaj.getF20fantig());
		contractRecord.setValue(CONTRACT.AGREEMENT_LEVEL_CATEGORY,
				cb.getCategory(trabaj));
		contractRecord.setValue(CONTRACT.SS_REGIME, enum2Byte(regimeType));
		contractRecord.setValue(CONTRACT.CATEGORY_DESCRIPTION,
				trabaj.getF20nomcat());
		contractRecord.setValue(CONTRACT.MODEL,
				enum2Byte(getModel(trabaj.getF20clcto())));
		InsertSetStep<ContractRecord> insertSetStepCotract = getContractInsertSetStep();
		insertSetMoreStepContract = insertSetStepCotract.set(contractRecord);

		InsertSetStep<ContractDataRecord> insertSetStepCotractData = getContractDataInsertSetStep();
		String groupName = ContextVariable.QUOTE_GROUP.getName();
		int groupId = getContractDataId(contract, groupName);
		//@formatter:off
		insertSetMoreStepContractData = insertSetStepCotractData
			.set(CONTRACT_DATA.ID, groupId)
			.set(CONTRACT_DATA.DOMAIN, domain)
			.set(CONTRACT_DATA.CONTRACT, contract)
			.set(CONTRACT_DATA.NAME, groupName)
			.set(CONTRACT_DATA.EXPRESSION, String.format("\"%s\"",trabaj.getF20grupo()) )
			.set(CONTRACT_DATA.START_DATE, trabaj.getF20falta())
			.set(CONTRACT_DATA.END_DATE, (Date) null)
		;
		if ( trabaj.getF20irpf() != null  ) {
			String irpfName = ContextVariable.IRPF_PERCENT.getName();
			int irpfId = getContractDataId(contract, irpfName);
			//@formatter:on
			insertSetStepCotractData = getContractDataInsertSetStep();
			//@formatter:off
			insertSetMoreStepContractData = insertSetStepCotractData
				.set(CONTRACT_DATA.ID, irpfId)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.CONTRACT, contract)
				.set(CONTRACT_DATA.NAME, irpfName)
				.set(CONTRACT_DATA.EXPRESSION,  String.format("%.2f", trabaj.getF20irpf()) )
				.set(CONTRACT_DATA.START_DATE, trabaj.getF20falta())
				.set(CONTRACT_DATA.END_DATE, (Date) null)
			;
			//@formatter:on
		}

		return contractRecord;
	}

	private void loadConce(FntconceRecord tconce, int contract, int domain,
			Callback cb) {

		PaymentConceptRecord concept = cb.getConcept(tconce);

		Byte type = isBlank(tconce.getF21clavecra()) ? 1 : Byte
				.parseByte(tconce.getF21clavecra());

		if (concept != null && type.equals(concept.getType()))
			type = null;

		String irpf = null;
		if (equalsIgnoreCase("S", tconce.getF21irpf())) {
			irpf = (concept != null && "_P".equals(concept.getIrpfExpression())) ? null
					: "_P";
		}

		String quote = null;
		if (equalsIgnoreCase("S", tconce.getF21segsoc())) {
			quote = (concept != null && "_P".equals(concept
					.getQuoteExpression())) ? null : "_P";
		}

		String description = tconce.getF21nombre();
		if (concept != null && description != null
				&& description.equals(concept.getDescription()))
			description = null;

		InsertSetStep<ContractPaymentRecord> insertSetStepContractPayment = getContractPaymentInsertSetStep();
		//@formatter:off
		insertSetMoreStepContractPayment = insertSetStepContractPayment
				.set(CONTRACT_PAYMENT.DOMAIN, domain)
				.set(CONTRACT_PAYMENT.CONTRACT, contract)
				.set(CONTRACT_PAYMENT.START_DATE, tconce.getF21falta())
				.set(CONTRACT_PAYMENT.MONTH, (Byte) null )
				.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT,  concept != null ? concept.getId() : null )
				.set(CONTRACT_PAYMENT.EXPRESSION, getExpression(tconce))

				.set(CONTRACT_PAYMENT.TYPE, type)
				.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, irpf )
				.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, quote )
				.set(CONTRACT_PAYMENT.DESCRIPTION, description)
				
				;
		//@formatter:on
	}

	private void loadVarios(FnvariosRecord varios, Fnvario2Record vario2,
			int contract, int domain, int year) {

		Calendar month = Calendar.getInstance();
		month.set(Calendar.YEAR, year);
		month.set(Calendar.MONTH, Integer.valueOf(varios.getF30mesvar()) - 1);

		month.set(Calendar.DAY_OF_MONTH, 1);
		Date startDate = new Date(month.getTimeInMillis());

		month.set(Calendar.DAY_OF_MONTH,
				month.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = new Date(month.getTimeInMillis());

		if (!isBlank(varios.getF30grupo())) {
			InsertSetStep<ContractDataRecord> insertSetStepCotractData = getContractDataInsertSetStep();
			//@formatter:off
			insertSetMoreStepContractData = insertSetStepCotractData
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.CONTRACT, contract)
				.set(CONTRACT_DATA.NAME, ContextVariable.QUOTE_GROUP.getName())
				.set(CONTRACT_DATA.EXPRESSION, varios.getF30grupo() )
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate )
			;
			//@formatter:on
		}

	}

	private ContractRecord getContract(FntrabajRecord trabaj, int workplace) {
		//@formatter:off
		return aonContext.
		select()
		.from(CONTRACT)
		.join(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.where(CONTRACT.WORKPLACE.eq(workplace))
		.and(PERSON.SOCIAL_SECURITY_NUM.eq(getSocialSecurityNum(trabaj)))
		.and(CONTRACT.START_DATE.eq(trabaj.getF20falta()))
		.fetchOneInto(CONTRACT)
		;
		//@formatter:on
	}

	private PersonContractRecord getFullContract(FntrabajRecord trabaj,
			int workplace) {
		//@formatter:off
		Record record = aonContext.
		select()
		.from(CONTRACT)
		.join(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.where(CONTRACT.WORKPLACE.eq(workplace))
		.and(PERSON.SOCIAL_SECURITY_NUM.eq(getSocialSecurityNum(trabaj)))
		.and(CONTRACT.START_DATE.eq(trabaj.getF20falta()))
		.fetchOne()
		;
		if ( record == null )
			return null;
		return new PersonContractRecord(
				record.into(PERSON), 
				record.into(CONTRACT));
		//@formatter:on
	}

	private PersonRecord lastPerson() {
		return personRecord;
	}

	private RegistryRecord lastRegistry() {
		return registryRecord;
	}

	private int getRaddressId(FntrabajRecord trabaj, int registry,
			AddressType type) {
		RaddressRecord record = getRaddress(trabaj, registry, type);
		return record != null ? record.getId() : next(RADDRESS.getIdentity());
	}

	private RaddressRecord getRaddress(FntrabajRecord trabaj, int registry,
			AddressType type) {
		//@formatter:off
		return aonContext.
		select()
		.from(RADDRESS)
		.where(RADDRESS.REGISTRY.eq(registry))
		.and(RADDRESS.TYPE.eq(enum2Byte(type)))
		.limit(1)
		.fetchOneInto(RADDRESS)
		;
		//@formatter:on
	}

	private int getRMediaId(FntrabajRecord trabaj, int registry, MediaType type) {
		RmediaRecord record = getRMedia(trabaj, registry, type);
		return record != null ? record.getId() : next(RMEDIA.getIdentity());
	}

	private RmediaRecord getRMedia(FntrabajRecord trabaj, int registry,
			MediaType type) {
		//@formatter:off
		return aonContext.
		select()
		.from(RMEDIA)
		.where(RMEDIA.REGISTRY.eq(registry))
		.and(RMEDIA.MEDIA.eq(enum2Byte(type)))
		.limit(1)
		.fetchOneInto(RMEDIA)
		;
		//@formatter:on
	}

	private int getContractDataId(int contract, String name) {
		ContractDataRecord record = getContractData(contract, name);
		return record != null ? record.getId() : next(CONTRACT_DATA
				.getIdentity());
	}

	private ContractDataRecord getContractData(int contract, String name) {
		//@formatter:off
		return aonContext.
		select()
		.from(CONTRACT_DATA)
		.where(CONTRACT_DATA.CONTRACT.eq(contract))
		.and(CONTRACT_DATA.NAME.eq(name))
		.limit(1)
		.fetchOneInto(CONTRACT_DATA)
		;
		//@formatter:on
	}

	private String getSocialSecurityNum(FntrabajRecord trabaj) {
		return format("%s%s%s", trabaj.getF20sscod(), trabaj.getF20ssnum(),
				trabaj.getF20ssctrl());
	}

	private InsertSetStep<RegistryRecord> getRegistryInsertSetStep() {
		return get(insertSetMoreStepRegistry, REGISTRY);
	}

	private InsertSetStep<ContractRecord> getContractInsertSetStep() {
		return get(insertSetMoreStepContract, CONTRACT);
	}

	private InsertSetStep<ContractDataRecord> getContractDataInsertSetStep() {
		return get(insertSetMoreStepContractData, CONTRACT_DATA);
	}

	private InsertSetStep<ContractPaymentRecord> getContractPaymentInsertSetStep() {
		return get(insertSetMoreStepContractPayment, CONTRACT_PAYMENT);
	}

	private InsertSetStep<PersonRecord> getPersonInsertSetStep() {
		return get(insertSetMoreStepPerson, PERSON);
	}

	private InsertSetStep<RaddressRecord> getRaddressInsertSetStep() {
		return get(insertSetMoreStepRaddress, RADDRESS);
	}

	private InsertSetStep<RmediaRecord> getRmediaInsertSetStep() {
		return get(insertSetMoreStepRmedia, RMEDIA);
	}

	private void insert() {
		execute(insertSetMoreStepRegistry);
		execute(insertSetMoreStepRaddress);
		execute(insertSetMoreStepRmedia);
		execute(insertSetMoreStepPerson);
		execute(insertSetMoreStepContract);
		execute(insertSetMoreStepContractData);
		execute(insertSetMoreStepContractPayment);

		insertSetMoreStepRegistry = null;
		insertSetMoreStepPerson = null;
		insertSetMoreStepContract = null;
		insertSetMoreStepRaddress = null;
		insertSetMoreStepRmedia = null;
		insertSetMoreStepContractData = null;
		insertSetMoreStepContractPayment = null;
	}

	private void delete() {
		if (toDelete.isEmpty())
			return;

		List<Integer> contractIds = new ArrayList<Integer>(toDelete.size());
		List<Integer> registryIds = new ArrayList<Integer>(toDelete.size());
		for (Ids ids : toDelete) {
			contractIds.add(ids.contractId);
			registryIds.add(ids.registryId);
		}

		//@formatter:off
		
		SelectConditionStep<Record1<Integer>> salarySelectCond= aonContext
				.select(SALARY.ID)
				.from(SALARY)
				.where(SALARY.CONTRACT.in(contractIds));
		
		aonContext
			.delete(SALARY_COST)
			.where(SALARY_COST.SALARY
			.in(salarySelectCond))
			.execute();
		aonContext
			.delete(SALARY_DATA)
			.where(SALARY_DATA.SALARY
			.in(salarySelectCond))
			.execute();
		aonContext
			.delete(SALARY_BONUS)
			.where(SALARY_BONUS.SALARY
			.in(salarySelectCond))
			.execute();
		aonContext
			.delete(SALARY_EMBARGO)
			.where(SALARY_EMBARGO.SALARY
			.in(salarySelectCond))
			.execute();
		aonContext
			.delete(SALARY_PAYMENT)
			.where(SALARY_PAYMENT.SALARY
			.in(salarySelectCond))
			.execute();
		aonContext
			.delete(SALARY_DEDUCTION)
			.where(SALARY_DEDUCTION.SALARY
			.in(salarySelectCond))
			.execute();
		aonContext
			.delete(SALARY)
			.where(SALARY.CONTRACT
			.in(contractIds))
			.execute();
		
		aonContext
			.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT
			.in(contractIds))
			.execute();
		aonContext
			.delete(CONTRACT_PAYMENT)
			.where(CONTRACT_PAYMENT.CONTRACT
			.in(contractIds))
			.execute();
		aonContext
			.delete(CONTRACT)
			.where(CONTRACT.ID
			.in(contractIds))
			.execute();
		
		aonContext
			.delete(PERSON)
			.where(PERSON.REGISTRY
			.in(registryIds))
			.execute();
		aonContext
			.delete(RADDRESS)
			.where(RADDRESS.REGISTRY
			.in(registryIds))
			.execute();
		aonContext
			.delete(RMEDIA)
			.where(RMEDIA.REGISTRY
			.in(registryIds))
			.execute();

		aonContext
			.delete(REGISTRY)
			.where(REGISTRY.ID
			.in(registryIds))
			.execute();

		//@formatter:on

	}

	private static ContractModel getModel(String str) {

		return null;
	}

	private static String getFullName(FntrabajRecord trabaj) {
		StringBuffer buffer = new StringBuffer();
		if (!isBlank(trabaj.getF20apell1())) {
			buffer.append(trabaj.getF20apell1());
		}
		if (!isBlank(trabaj.getF20apell2())) {
			if (buffer.length() > 0) {
				buffer.append(" ");
			}
			buffer.append(trabaj.getF20apell2());
		}
		if (!isBlank(trabaj.getF20nombre())) {
			if (buffer.length() > 0) {
				buffer.append(", ");
			}
			buffer.append(trabaj.getF20nombre());
		}
		return buffer.length() > 0 ? buffer.toString() : null;
	}

}
