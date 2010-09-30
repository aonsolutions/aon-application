package com.code.aon.file.bank.model.CSB19;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.code.aon.file.bank.model.CSB19.check.CheckIndividual;
import com.code.aon.file.bank.model.CSB19.check.CheckOrderer;
import com.code.aon.file.bank.model.CSB19.check.CheckPresenter;
import com.code.aon.file.bank.model.CSB19.data.Individual;
import com.code.aon.file.bank.model.CSB19.data.IndividualLine;
import com.code.aon.file.bank.model.CSB19.data.Lot;
import com.code.aon.file.bank.model.CSB19.data.Orderer;
import com.code.aon.file.bank.model.CSB19.data.Presenter;
import com.code.aon.file.bank.model.CSB19.xml.XMLLoader;
import com.code.aon.file.format.core.Account;
import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.FileFiller;

public class CSB19 extends AbstractFileFiller {

	private Lot lot;
	private int numreg;

	public CSB19(Lot lot, String filePath) throws FileNotFoundException,
			UnsupportedEncodingException {
		super(filePath);
		this.lot = lot;

		InputStream input = XMLLoader.class.getResourceAsStream("Cabecera_Presentador.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Cabecera_Ordenante.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Individual_Obligatorio.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Individual_Opcional_1.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Individual_Opcional_2.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Individual_Opcional_3.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Individual_Opcional_4.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Individual_Opcional_5.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Individual_Opcional_6.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Total_Ordenante.xml");
		DiskRegisterLoader.load(input, manager);

		input = XMLLoader.class.getResourceAsStream("Total_Presentador.xml");
		DiskRegisterLoader.load(input, manager);

	}

	public ArrayList<Exception> create() {
		Presenter presenter = null;
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(CSB19.LOT, lot);

		presenter = lot.getPresenter();

		CheckPresenter.parse(presenter, exceptions);
		properties.put(CSB19.PRESENTER, presenter);

		if (createLine("Cabecera_Presentador", properties) != null)
			++numreg;

		Iterator<Orderer> iterOrderers = lot.getOrderersIterator();
		while (iterOrderers.hasNext()) {
			Orderer orderer = iterOrderers.next();
			CheckOrderer.parse(orderer, exceptions);
			properties.put(CSB19.ORDERER, orderer);
			properties.put(CSB19.ORDERER_ACCOUNT, orderer.getAccount());

			int numRegInd = 0;
			if (createLine("Cabecera_Ordenante", properties) != null)
				++numRegInd;
			Iterator<Individual> iterIndividuals = orderer.getIndividualsIterator();
			while (iterIndividuals.hasNext()) {
				Individual individual = iterIndividuals.next();
				CheckIndividual.parse(individual, exceptions);

				properties.put(CSB19.INDIVIDUAL, individual);
				properties.put(CSB19.INDIVIDUAL_ACCOUNT, individual.getAccount());

				if (createLine("Individual_Obligatorio", properties) != null)
					++numRegInd;

				if (lot.getType() == Lot.EXTENDED) {
					numRegInd += regExtendido(individual, properties);
				}

				if (individual.getAccountUserAddress() != null && !individual.getAccountUserAddress().equals(""))
					if (createLine("Individual_Opcional_6", properties) != null)
						++numRegInd;
			}
			orderer.setNumRegs(numRegInd + 1);
			if (createLine("Total_Ordenante", properties) != null)
				++numRegInd;
			numreg += numRegInd;
		}
		++numreg;
		lot.setNumRegs(numreg);
		createLine("Total_Presentador", properties);

		output.flush();
		writeErrorsFile();
		return exceptions;
	}

	private int regExtendido(Individual individual,
			Map<String, Object> properties) {
		Iterator<String> conceptsIter = individual.getConceptsIterator();
		int i = 0;
		List<IndividualLine> lines = new ArrayList<IndividualLine>();
		IndividualLine il = null;
		while (conceptsIter.hasNext()) {
			if (il == null) {
				i = 0;
				il = new IndividualLine();
			}
			i++;
			if (i == 1)
				il.setConcept1(conceptsIter.next());
			if (i == 2)
				il.setConcept2(conceptsIter.next());
			if (i == 3) {
				il.setConcept3(conceptsIter.next());
				lines.add(il);
				il = null;
			}
		}
		if (il != null)
			lines.add(il);

		int totalRegs = 0;
		int totalLines = lines.size() > 5 ? 5 : lines.size();
		for (int j = 0; j < totalLines; j++) {
			properties.put(CSB19.INDIVIDUAL_LINE, lines.get(j));
			if (createLine("Individual_Opcional_" + (j + 1), properties) != null)
				++totalRegs;
		}
		return totalRegs;
	}

	private static String LOT = "LOT";
	private static String PRESENTER = "PRESENTER";
	private static String ORDERER = "ORDERER";
	private static String ORDERER_ACCOUNT = "ORDERER_ACCOUNT";
	private static String INDIVIDUAL = "INDIVIDUAL";
	private static String INDIVIDUAL_ACCOUNT = "INDIVIDUAL_ACCOUNT";
	private static String INDIVIDUAL_LINE = "INDIVIDUAL_LINE";

	public static void main(String[] args) throws FileNotFoundException,
			UnsupportedEncodingException {
		Lot lot = new Lot();
		lot.setType(Lot.EXTENDED);
		// lot.setType(Lot.RESUMED);

		Presenter presenter = new Presenter();
		presenter.setCode("00000000H");
		presenter.setSufix("032");
		presenter.setMakeDate(new java.util.Date());
		presenter.setName("NAME");
		presenter.setEntity("1092");
		presenter.setOffice("0001");

		lot.setPresenter(presenter);

		Orderer orderer = new Orderer();
		Account ccc1 = new Account();
		ccc1.parse("10000000000000000000");
		orderer.setAccount(ccc1);
		orderer.setCode("CODE");
		orderer.setMakeDate(new Date());
		orderer.setName("NAME");
		orderer.setProcedure(new Integer(0));
		orderer.setStartDate(new Date());
		orderer.setSufix("SU");

		Individual individual = new Individual();
		individual.setAmount(12.02);
		Account ccc2 = new Account();
		ccc2.parse("00000000000000000000");
		individual.setAccount(ccc2);
		individual.setConcept("CONCEPT");
		individual.setInternalCode("CODE");
		individual.setName("NAME");
		individual.setReferenceCode("CODE");
		individual.setReturnCode("CODE");
		individual.setAccountUserName("accountUserName");
		individual.setAccountUserAddress("setAccountUserAddress");
		individual.setAccountUserAddress2("setAccountUserAddress2");
		individual.setAccountUserPCode(01001);

		orderer.addIndividual(individual);

		Individual individual2 = new Individual();
		individual2.setAmount(12.02);
		Account ccc3 = new Account();
		ccc3.parse("10000000000000000000");
		individual2.setAccount(ccc3);
		individual2.setConcept("CONCEPT");
		individual2.setInternalCode("CODE");
		individual2.setName("NAME");
		individual2.setReferenceCode("CODE");
		individual2.setReturnCode("CODE");
		individual2.setAccountUserName("accountUserName");
		individual2.setAccountUserAddress("setAccountUserAddress");
		individual2.setAccountUserAddress2("setAccountUserAddress2");
		individual2.setAccountUserPCode(01001);

		individual2.addConcept("CCCCCCCCC");
		individual2.addConcept("CCCCCCCCC");
		individual2.addConcept("CCCCCCCCC");
		individual2.addConcept("CCCCCCCCC");

		orderer.addIndividual(individual2);

		lot.addOrderer(orderer);

		String filePath = "c:/tmp/CSB19_31444.txt";
		FileFiller csb19 = new CSB19(lot, filePath);
		csb19.create();
	}

}
