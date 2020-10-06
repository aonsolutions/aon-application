package solutions.aon.in.invoice.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.github.javafaker.Faker;

import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.templates.Document;
import solutions.aon.in.invoice.templates.Document.DocumentType;
import solutions.aon.in.invoice.templates.DocumentParser;

public class DocumentParserTestCase {
	private static Faker FAKER = Faker.instance();
	
	private static final String[] VALID_LEGAL_PERSON_NIFS = {
		  "D08078115" ,"D08-078-115" ,"D08_078_115" ,"D08.078.115"
		 ,"D 08078115","D 08-078-115","D 08_078_115","D 08.078.115"
		 ,"D-08078115","D-08-078-115","D-08_078_115","D-08.078.115"
		 ,"D_08078115","D_08-078-115","D_08_078_115","D_08.078.115"
		 ,"D/08078115","D/08-078-115","D/08_078_115","D/08.078.115"
		
		 ,"R0636875G" ,"R 0636875G","R 0636875 G"
		 			  ,"R-0636875G","R-0636875-G"
		 			  ,"R_0636875G","R_0636875_G"
		 			  ,"R/0636875G","R/0636875/G"
		
		,"V61059770"		,"A52718566"		,"B22296081"		,"E20617486"
		,"W4205433H"		,"W8593385A"		,"J85835049"		,"C55339683"
		,"N2023855F"		,"N6723945I"		,"H06227375"		,"P2328719F"
		,"J92624105"		,"N0925641C"		,"A80934011"		,"C67608943"
		,"V53398624"		,"G89857882"		,"H99335887"		,"S3366554H"
		,"G41523325"		,"U37464856"		,"F78804218"		,"F10446714"
		,"J49881758"		,"P9699955B"		,"U23519374"		,"P3328857B"
		,"J43444728"		,"R2336797B"		,"E59043943"		,"V88619424"
		,"A28099901"		,"U66098419"
	};
	private static final String[] VALID_DNIS = {"73742960S"
		,"10597868C","68362309F","32748386C","59177614V","76329213X"
		,"23049210J","52335130X","39960275Y","38162094B","61149985T"
		,"53431615Q","55478315S","04442166S","36866225P","00636000G"
		,"59700453C","92342848X","88948983W","10131903N","06578360S"
		,"79261218K","01022629A","52038541Y","00503975E","74760064J"
		,"16581506R","40330394D","68046015D","55316635W","39198976F"
		,"12516280W","81522249Z","01053411B","48841174F","37085391F"
		,"75002847P","15308794V","17129441F","83109150F","40114518B"

		, "89385458Y"
		,"89385458-Y"
		,"89.385.458-Y"
		,"89385458/Y"
		,"89.385.458/Y"
		
		,"4442166S"
		,"4442166-S"
		,"4.442.166S"
		,"4.442.166 S"
		,"4.442.166-S"
	};

	private static final String[] VALID_NIES = {"X3068391F"
		,"Y5920403X","X1291539C","Y4769124L","Y6497764W","Y9135113X"
		,"Z4119001M","X1057886R","Z8457881G","Z4976959V","X7921573M"
		,"X8375740Z","Z6708069D","Z3089945S","Y9041672H","Y5930224X"
		,"Z2557267V","X1372262J","Y3824163J","X7385653P","Z4664537G"
		,"Z3112129G","Z1919598T","X2083582N","Y3951793Q","Y9894414N"
		,"Z8755900N","Z1822146E","X8796564F","X0721784K","X9526593Q"
		,"Z7466881M","Y8097589Q","Z2694959P","Y2357610D","Y9818511D"
		,"X1225007G","Y6494703T","Y3618784R","Z0754236W","Z4582602H"
		,"Y0519561M","Y9009781M","X0404139Y","Y4652355K","X6198594W"
	};

	public DocumentParserTestCase() {
		super();
	}
	
	private String getLorem( String data ) {
		String randomText = null;
		if (FAKER.random().nextInt(0, 6) > 3) {
			randomText = 
				((FAKER.random().nextInt(0, 100) < 80)?FAKER.lorem().sentence(FAKER.random().nextInt(0, 5)):"") 
				+ " " + data + " "    
				+ ((FAKER.random().nextInt(0, 100) < 80)?FAKER.lorem().sentence(FAKER.random().nextInt(0, 5)):"")
				;
 	
		} else {
			randomText = 
				((FAKER.random().nextInt(0, 100) < 80)?FAKER.lorem().characters(FAKER.random().nextInt(0, 50),true):"") +
				" " +  data  + " "
				+ ((FAKER.random().nextInt(0, 100) < 80)?FAKER.lorem().characters(FAKER.random().nextInt(0, 50),true):"") 
				;
		}
		System.out.println("[" + randomText + "]");
		return randomText;
	}

	
	@Test
	public void testLegalPersonNIF() throws IOException, UnknownInvoiceException {
		System.out.println();
		System.out.println("------------------------------");
		System.out.println("- testLegalPersonNIF ---------");
		System.out.println("------------------------------");
		for (String text : VALID_LEGAL_PERSON_NIFS) {
			System.out.println( "\t["+ text +"]");
			Collection<Document> nifs = DocumentParser.getNifs( text  );
			assertNotNull(text,nifs);
			assertEquals(text,1,nifs.size());
			Document nif = nifs.stream().findFirst().get();
			assertEquals(text,DocumentType.LEGAL_PERSON_NIF, nif.getType());
		}
	}
	
	@Test
	public void testDNI() throws IOException, UnknownInvoiceException {
		System.out.println();
		System.out.println("-------------------");
		System.out.println("- testDNI ---------");
		System.out.println("-------------------");
		for (String text : VALID_DNIS) {
			text = getLorem( text );
			System.out.println( "\t["+ text +"]");	
			Collection<Document> nifs = DocumentParser.getNifs( text );
			assertNotNull(text,nifs);
			assertEquals(text,1,nifs.size());
			Document nif = nifs.stream().findFirst().get();
			assertEquals(text,DocumentType.DNI, nif.getType());
		}
	}
	
	@Test
	public void testNIE() throws IOException, UnknownInvoiceException {
		System.out.println();
		System.out.println("-------------------");
		System.out.println("- testNIE ---------");
		System.out.println("-------------------");
		for (String text : VALID_NIES) {
			System.out.println( "\t["+ text +"]");
			Collection<Document> nifs = DocumentParser.getNifs( text  );
			assertNotNull(text,nifs);
			assertEquals(text,1,nifs.size());
			Document nif = nifs.stream().findFirst().get();
			assertEquals(text,DocumentType.NIE, nif.getType());
		}
	}

	
	@Test
	public void testXXX() throws IOException, UnknownInvoiceException {
		System.out.println();
		System.out.println("-------------------");
		System.out.println("- testXXX ---------");
		System.out.println("-------------------");
		String text = "* Orange Espagne, S.A.U. Con sede social en Pque. Emp. La Finca, Pº del Club Deportivo, 1, Edif.8, 28223 Pozuelo de Alarcón, Madrid. Inscrita en el Registro Mercantil de Madrid, tomo 13.183, folio 129, hoja M-213468, CIF A-82009812 *";
		Collection<Document> nifs = DocumentParser.getNifs( text  );
		assertNotNull(text,nifs);
		assertEquals(text,1,nifs.size());
		Document nif = nifs.stream().findFirst().get();
		assertEquals(text,DocumentType.LEGAL_PERSON_NIF, nif.getType());
	}
	
}


