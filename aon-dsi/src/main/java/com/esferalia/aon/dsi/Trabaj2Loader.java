package com.esferalia.aon.dsi;

//i.puig@analize.es

import static com.esferalia.aon.dsi.CconceLoader.getExpression;
import static com.esferalia.aon.dsi.jooq.tables.Fntconce.FNTCONCE;
import static com.esferalia.aon.dsi.jooq.tables.Fntraba2.FNTRABA2;
import static com.esferalia.aon.dsi.jooq.tables.Fntrabaj.FNTRABAJ;
import static com.esferalia.aon.dsi.jooq.tables.Fnvario2.FNVARIO2;
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
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.sql.Date;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;

import com.code.aon.dbutils.AonSQLException;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.dsi.jooq.tables.records.FnnomincRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FntconceRecord;
import com.esferalia.aon.dsi.jooq.tables.records.Fntraba2Record;
import com.esferalia.aon.dsi.jooq.tables.records.FntrabajRecord;
import com.esferalia.aon.dsi.jooq.tables.records.Fnvario2Record;
import com.esferalia.aon.dsi.jooq.tables.records.FnvariosRecord;
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

	public static interface Callback {

		int getDomain(FntrabajRecord trabaj);

		int getWorplace(FntrabajRecord trabaj);

		Integer getCategory(FntrabajRecord trabaj);

		PaymentConceptRecord getConcept(FntconceRecord conce);
	}

	private Map<String, int[]> ssIdsMap;

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
	}

	public void loadTrabj2(Callback cb, Condition... conditions)
			throws AonSQLException {
		//@formatter:off
		Cursor<Record> trabjCursor = dsiContext.select()
				.from(FNTRABAJ)
				.where(conditions)
				.orderBy(FNTRABAJ.F20SSCOD, FNTRABAJ.F20SSNUM, FNTRABAJ.F20FALTA)
				.fetchLazy();
		//@formatter:on

		//@formatter:off
		Cursor<Record> trab2Cursor = dsiContext.select()
				.from(FNTRABA2)
				.where(conditions)
				.orderBy(FNTRABA2.F20SSCOD, FNTRABA2.F20SSNUM, FNTRABA2.F20FALTA)
				.fetchLazy();
		//@formatter:on

		//@formatter:off
		Cursor<Record> tconceCursor = dsiContext.select()
				.from(FNTCONCE)
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

		while (trab2Cursor.hasNext() && trabjCursor.hasNext()) {
			FntrabajRecord trabaj = trabjCursor.fetchOneInto(FNTRABAJ);
			Fntraba2Record traba2 = trab2Cursor.fetchOneInto(FNTRABA2);
			int domain = cb.getDomain(trabaj);
			int workplace = cb.getWorplace(trabaj);

			String key = trabaj.getF20sscod() + trabaj.getF20ssnum()
					+ trabaj.getF20falta();

			ContractRecord record = getContract(trabaj, workplace);
			if (record != null) {
				// TODO: REPLACE INTO `registry` (...
				// TODO: REPLACE INTO `person` (...
				// TODO: REPLACE INTO `contract` (...
				ssIdsMap.put(key, new int[] { domain, record.getId() });
				continue;
			}

			int person = next(REGISTRY.getIdentity());
			int contract = loadTrabj2(trabaj, traba2, person, workplace,
					domain, cb);

			while (tconce != null 
					//@formatter:off
					&& StringUtils.equals(trabaj.getF20ssnumem(), tconce.getF21ssnumem()) 
					&& StringUtils.equals(trabaj.getF20sscodem(), tconce.getF21sscodem()) 
					&& StringUtils.equals(trabaj.getF20ssnum(), tconce.getF21ssnum()) 
					&& StringUtils.equals(trabaj.getF20sscod(), tconce.getF21sscod()) 
					&& trabaj.getF20falta().equals(tconce.getF21falta())
					//@formatter:on
			) {
				loadConce(tconce, contract, domain, cb);
				tconce = tconceCursor.hasNext() ? tconceCursor
						.fetchOneInto(FNTCONCE) : null;
			}

			ssIdsMap.put(key, new int[] { domain, contract });
		}
	}

	public void loadVarios(Condition... conditions) {
		//@formatter:off
		Cursor<Record> variosCursor = dsiContext.select()
				.from(FNVARIOS)
				.where(conditions)
				.orderBy(FNVARIOS.F30SSCODEM, 
						FNVARIOS.F30SSNUMEM ,
						FNVARIOS.F30SSCOD, 
						FNVARIOS.F30SSNUM, 
						FNVARIOS.F30FALTA, 
						FNVARIOS.F30MESVAR )
				.fetchLazy();
		//@formatter:on

		//@formatter:off
		Cursor<Record> vario2Cursor = dsiContext.select()
				.from(FNVARIO2)
				.where(conditions)
				.orderBy(FNVARIO2.F30SSCODEM, 
						FNVARIO2.F30SSNUMEM ,
						FNVARIO2.F30SSCOD, 
						FNVARIO2.F30SSNUM, 
						FNVARIO2.F30FALTA, 
						FNVARIO2.F30MESVAR )
				.fetchLazy();
		//@formatter:on

		while (variosCursor.hasNext() && vario2Cursor.hasNext()) {
			FnvariosRecord varios = variosCursor.fetchOneInto(FNVARIOS);
			Fnvario2Record vario2 = vario2Cursor.fetchOneInto(FNVARIO2);
		}

	}

	public void execute() {
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

	private int loadTrabj2(FntrabajRecord trabaj, Fntraba2Record traba2,
			int person, int workplace, int domain, Callback cb) {
		InsertSetStep<RegistryRecord> insertSetStepRegistry = getRegistryInsertSetStep();
		//@formatter:off
		insertSetMoreStepRegistry = insertSetStepRegistry
				.set(REGISTRY.ID, person)
				.set(REGISTRY.DOMAIN, domain) //TODO: parentDomain
				.set(REGISTRY.TYPE, enum2Byte(RegistryType.NATURAL))
				.set(REGISTRY.NAME, getFullName(trabaj))
				.set(REGISTRY.DOCUMENT, trabaj.getF20dni())
				.set(REGISTRY.DOCUMENT_TYPE, enum2Byte(getDocumentType(trabaj.getF20dni())))
				;
		//@formatter:on

		StringBuffer address2 = new StringBuffer();
		if (!isBlank(trabaj.getF20piso()))
			address2.append(String.format("%s º", trabaj.getF20piso()));
		if (!isBlank(trabaj.getF20puerta()))
			address2.append(String.format(" %s", trabaj.getF20puerta()));

		//@formatter:off
		InsertSetStep<RaddressRecord> insertSetStepRaddress= getRaddressInsertSetStep();
		insertSetMoreStepRaddress = insertSetStepRaddress
				.set(RADDRESS.REGISTRY, person)
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
			//@formatter:off
			InsertSetStep<RmediaRecord> insertSetStepRmedia= getRmediaInsertSetStep();
			insertSetMoreStepRmedia = insertSetStepRmedia
					.set(RMEDIA.REGISTRY, person)
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

		InsertSetStep<PersonRecord> insertSetStepPerson = getPersonInsertSetStep();
		//@formatter:off
		insertSetMoreStepPerson= insertSetStepPerson
				.set(PERSON.REGISTRY, person)
				.set(PERSON.DOMAIN, domain)
				.set(PERSON.BIRTH_DATE, trabaj.getF20fnac())
				.set(PERSON.GENDER, enum2Byte(gender) )
				.set(PERSON.NAME, trabaj.getF20nombre())
				.set(PERSON.FIRST_SURNAME, trabaj.getF20apell1())
				.set(PERSON.SECOND_SURNAME, trabaj.getF20apell2())
				.set(PERSON.SOCIAL_SECURITY_NUM, getSocialSecurityNum(trabaj))
				;
				//TODO : MARITAL_STATUS, F20NOMBREC? 
		//@formatter:on
		Integer reg = null;
		if (!isBlank(trabaj.getF20matric()))
			try {
				reg = Integer.parseInt(trabaj.getF20matric());
			} catch (NumberFormatException e) {

			}

		SSRegimeType regimeType = SSRegimeType.GENERAL;
		if (StringUtils.equalsIgnoreCase("S", trabaj.getF20autono()))
			regimeType = SSRegimeType.SELF_EMPLOYED;

		int contract = next(CONTRACT.getIdentity());
		InsertSetStep<ContractRecord> insertSetStepCotract = getContractInsertSetStep();
		//@formatter:off
		insertSetMoreStepContract = insertSetStepCotract
		.set(CONTRACT.ID, contract)
		.set(CONTRACT.DOMAIN, domain)
		.set(CONTRACT.PERSON, person)
		.set(CONTRACT.REGISTRATION, reg)
		.set(CONTRACT.WORKPLACE, workplace)
		.set(CONTRACT.START_DATE, trabaj.getF20falta())
		.set(CONTRACT.END_DATE, trabaj.getF20fbaja())
		.set(CONTRACT.DESCRIPTION, trabaj.getF20puesto())
		.set(CONTRACT.SENIORITY_DATE, trabaj.getF20fantig())
		.set(CONTRACT.AGREEMENT_LEVEL_CATEGORY, cb.getCategory(trabaj))
		.set(CONTRACT.SS_REGIME, enum2Byte(regimeType))
		.set(CONTRACT.CATEGORY_DESCRIPTION, trabaj.getF20nomcat())
		.set(CONTRACT.MODEL, enum2Byte(getModel(trabaj.getF20clcto())))
		;
		//@formatter:on

		InsertSetStep<ContractDataRecord> insertSetStepCotractData = getContractDataInsertSetStep();
		//@formatter:off
		insertSetMoreStepContractData = insertSetStepCotractData
			.set(CONTRACT_DATA.DOMAIN, domain)
			.set(CONTRACT_DATA.CONTRACT, contract)
			.set(CONTRACT_DATA.NAME, ContextVariable.QUOTE_GROUP.getName())
			.set(CONTRACT_DATA.EXPRESSION, String.format("\"%s\"",trabaj.getF20grupo()) )
			.set(CONTRACT_DATA.START_DATE, trabaj.getF20falta())
			.set(CONTRACT_DATA.END_DATE, (Date) null)
		;
		if ( trabaj.getF20irpf() != null  ) {
			//@formatter:on
			insertSetStepCotractData = getContractDataInsertSetStep();
			//@formatter:off
			insertSetMoreStepContractData = insertSetStepCotractData
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.CONTRACT, contract)
				.set(CONTRACT_DATA.NAME, ContextVariable.IRPF_PERCENT.getName())
				.set(CONTRACT_DATA.EXPRESSION, String.format("%.2f", trabaj.getF20irpf()) )
				.set(CONTRACT_DATA.START_DATE, trabaj.getF20falta())
				.set(CONTRACT_DATA.END_DATE, (Date) null)
			;
			//@formatter:on
		}

		return contract;
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
