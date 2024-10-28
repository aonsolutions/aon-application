/**
 * 
 */
package net.aonsolutions.storage.s3;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractDoc.CONTRACT_DOC;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.Keys;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * @author rtrepiana
 *
 */
public class Rsync {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		Option hostOption =  Option.builder("h")
						.longOpt("host")
						.required()
						.hasArg()
						.argName("name")
						.desc("Connect to database host.")
						.build();
		
		Option userOption =  Option.builder("u")
				.longOpt("user")
				.required()
				.hasArg()
				.argName("name")
				.desc("User for login to database server.")
				.build();

		Option passwordOption =  Option.builder("p")
				.longOpt("password")
				.required()
				.hasArg()
				.argName("name")
				.desc("Password to use when connecting to database server.")
				.build();

		Option databaseOption =  Option.builder("d")
				.longOpt("database")
				.required()
				.hasArg()
				.argName("name")
				.desc("Database to connect.")
				.build();

		Option tableOption = Option.builder("t")
				.longOpt("table")
				.required()
				.hasArg()
				.argName("name")
				.desc("Table to synchronize from.")
				.build();
		
		Option whereOption = Option.builder("w")
				.longOpt("where")
				.hasArg()
				.argName("name")
				.desc("Sync only selected records.")
				.build(); 
		
		Option endpointOption = Option.builder("e")
				.longOpt("endpoint")
				.required()
				.hasArg()
				.argName("url")
				.desc("S3 service endpoint.")
				.build();

		Option regionOption = Option.builder("r")
				.longOpt("region")
				.required()
				.hasArg()
				.argName("name")
				.desc("S3 service region.")
				.build();

		Option bucketOption = Option.builder("b")
				.longOpt("bucket")
				.required()
				.hasArg()
				.argName("url")
				.desc("S3 service bucket to synchronize to.")
				.build();

		Option accessKeyOption = Option.builder("a")
				.longOpt("accesskey")
				.required()
				.hasArg()
				.argName("name")
				.desc("S3 service access key.")
				.build();

		Option secretKeyOption = Option.builder("s")
				.longOpt("secretkey")
				.required()
				.hasArg()
				.argName("name")
				.desc("S3 service secret key.")
				.build();

		Options options = new Options();
		options.addOption(hostOption);
		options.addOption(userOption);
		options.addOption(passwordOption);
		options.addOption(databaseOption);
		options.addOption(tableOption);
		options.addOption(whereOption);
		
		options.addOption(endpointOption);
		options.addOption(regionOption);
		options.addOption(bucketOption);
		options.addOption(accessKeyOption);
		options.addOption(secretKeyOption);
		
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(options, args);
			
			String host = line.getOptionValue(hostOption);
			String user = line.getOptionValue(userOption);
			String password = line.getOptionValue(passwordOption);
			String database = line.getOptionValue(databaseOption);
			String table = line.getOptionValue(tableOption);
			String where = line.getOptionValue(whereOption, "1=1");

			String endpoint = line.getOptionValue(endpointOption);
			String region = line.getOptionValue(regionOption);
			String bucket = line.getOptionValue(bucketOption);
			String accessKey = line.getOptionValue(accessKeyOption);
			String secretKey = line.getOptionValue(secretKeyOption);

			String jdbcUrl = String.format("jdbc:mysql://%s/%s", host,  database);
			Connection connection = DriverManager.getConnection( jdbcUrl , user , password);
			
			Settings settings = new Settings();
			settings.setRenderSchema(false);
			//settings.setParamType(ParamType.INLINED);
			
			DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
			
			S3Client client = S3Client.create();
			
			rSyncContractDoc(dslContext, where, client, bucket);
			
		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed. Reason: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ant", options);			
		}
	}
	
	private static void rSyncContractDoc(DSLContext dslContext, String where, S3Client client, String bucket) {

		dslContext
		.select()
		.from(CONTRACT_ATTACH)
		.innerJoin(CONTRACT).onKey(Keys.FK_CONTRACT_ATTACH_CONTRACT)
		.innerJoin(PERSON).onKey(Keys.FK_CONTRACT_PERSON)
		.innerJoin(REGISTRY).onKey(Keys.FK_PERSON_REGISTRY)
		.innerJoin(ENTERPRISE_CCC).onKey(Keys.FK_CONTRACT_ENTERPRISE_CCC)
		.innerJoin(DOMAIN).onKey(Keys.FK_CONTRACT_DOMAIN)
		.where(DSL.condition(where))
		.and(CONTRACT_ATTACH.DATA.isNotNull())
		.forEach( r -> {
			byte [] data = r.get(CONTRACT_ATTACH.DATA);
			Byte mimeType = r.get(CONTRACT_ATTACH.MIMETYPE);
			String contentType = MimeType.valueOf(mimeType).getName();
			Map<String, String> userData = new HashMap<>();
			userData.put("IPF", r.get(REGISTRY.DOCUMENT));
			userData.put("NAF", r.get(PERSON.SOCIAL_SECURITY_NUM));
			userData.put("CCC", r.get(ENTERPRISE_CCC.CCC));
//			userData.put("EMPLOYEE",  r.get(PERSON.FIRST_SURNAME) +" "+ r.get(PERSON.SECOND_SURNAME) +", " + r.get(PERSON.NAME) );
//			userData.put("ENTERPRISE",  r.get(DOMAIN.DESCRIPTION) );
			try {
				String s3Key = putAttach(client, bucket, data, contentType, userData);
				
				dslContext
				.insertInto(CONTRACT_DOC)
				.set(CONTRACT_DOC.S3_KEY, s3Key)
				
				.set(CONTRACT_DOC.ID, r.get(CONTRACT_ATTACH.ID))
				.set(CONTRACT_DOC.DOMAIN, r.get(CONTRACT_ATTACH.DOMAIN))
				.set(CONTRACT_DOC.CONTRACT, r.get(CONTRACT_ATTACH.CONTRACT))
				
				.set(CONTRACT_DOC.TYPE	, r.get(CONTRACT_ATTACH.TYPE))
				.set(CONTRACT_DOC.MIMETYPE, r.get(CONTRACT_ATTACH.MIMETYPE))
				.set(CONTRACT_DOC.ATTACH_DATE, r.get(CONTRACT_ATTACH.ATTACH_DATE))
				.set(CONTRACT_DOC.DESCRIPTION, r.get(CONTRACT_ATTACH.DESCRIPTION))
				
				.set(CONTRACT_DOC.SCOPE, r.get(CONTRACT_ATTACH.SCOPE))
				.set(CONTRACT_DOC.SECURITY_LEVEL, r.get(CONTRACT_ATTACH.SECURITY_LEVEL))
				.onDuplicateKeyIgnore()
				.execute()
				;
				
				System.out.printf("Success. Synchronized contract's attach [%d]: '%s' :-)\n", r.get(CONTRACT_ATTACH.ID), s3Key );
				
			} catch (NoSuchAlgorithmException | IOException e) {
				System.err.printf("Upps. Something was wrong with contract's attach [%d]: '%s' :-(\n" , r.get(CONTRACT_ATTACH.ID), e.getMessage() );
			}
			
			
		});
		;
	}

	private static String putAttach(S3Client client, String bucket, byte[] data, String contentType, Map<String, String> userMetaData) throws IOException, NoSuchAlgorithmException {
		String sha1Hex = toHex(MessageDigest.getInstance("SHA1").digest(data));
			
		if(doesObjectExist(client, bucket, sha1Hex)) {
			HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucket).key(sha1Hex).build();
			Map<String, String> metaData = client.headObject(request).metadata();
			metaData.forEach( (key,value) -> metaData.merge(key, value, Rsync::join));

			userMetaData.forEach( (key,value) -> metaData.merge(key, value, Rsync::join ));
				
			CopyObjectRequest copyRequest = CopyObjectRequest.builder()
						.sourceBucket(bucket).sourceKey(sha1Hex)
						.destinationBucket(bucket).destinationKey(sha1Hex)
						.metadata(metaData)
						.build();

			client.copyObject(copyRequest);
			System.err.printf("Upps. Contract's attach already at '%s' [%s] (%s):-|\n" , bucket, sha1Hex, metaData);
			
			return sha1Hex;
		}
		String md5Base64 = Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(data));
		
		PutObjectRequest putRequest = PutObjectRequest.builder().bucket(bucket).key(sha1Hex)
				.contentMD5(md5Base64)
				.contentLength((long) data.length)
				.contentType(contentType)
				.metadata(userMetaData).build();
		client.putObject(putRequest, RequestBody.fromBytes(data));
		return sha1Hex; 
	}
	
	private static boolean doesObjectExist(S3Client client, String bucketName, String s3Key) {
		HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucketName).key(s3Key).build();
		try {
			client.headObject(request);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	private static String join(String s1, String s2) {
	    if ( s2 == null || s2.isBlank()) {
		return s1;
	    }
	    if ( s1 == null || s1.isBlank() ) {
		return s2;
	    }
	    
	    if ( s1.equalsIgnoreCase(s2) ) {
		return s1;
	    }
	    
	    if ( s1.toUpperCase().contains(s2.toUpperCase())) {
		return s1;
	    }
	    
	    return String.join(",", s1, s2);	    
	}
	

	private static String toHex(byte [] data) throws NoSuchAlgorithmException {
		StringBuilder md5 = new StringBuilder();
		for (byte b : data) {
			// %[flags][width]conversion
			// Flag '0' - zero-padded
			// Width 2
			// Conversion 'X' -  The result is formatted as a hexadecimal integer, UPPERCASE
			md5.append(String.format("%02X", b));
		}
		return md5.toString();
	}

	private static enum MimeType {

	    JPEG ("image/jpeg"),
	    GIF ("image/gif"),
	    ICS ("text/calendar"),
	    TXT ("text/plain"),
	    HTML ("text/html"),
	    XML ("text/xml"),
	    PNG ("image/png"),
	    BMP ("image/bmp"),
	    TIFF ("image/tiff"),
	    ICO ("image/x-icon"),
	    AVI ("video/x-msvideo"),
	    MPEG ("video/mpeg"),
	    QUICKTIME ("video/quicktime"),
	    MP3 ("audio/mp3"),
	    WAV ("audio/x-wav"),
	    MID ("audio/mid"),
	    RTF ("text/rtf"),
	    MS_WORD ("application/msword"),
	    MS_EXCEL ("application/vnd.ms-excel"),
	    MS_POWER_POINT ("application/vnd.ms-powerpoint"),
	    STAR_OFFICE_TEXT ("application/vnd.oasis.opendocument.text"),
	    STAR_OFFICE_SPREADSHEET ("application/vnd.oasis.opendocument.spreadsheet"),
	    PDF ("application/pdf"),
	    JAVASCRIPT ("text/javascript"),
	    ZIP ("application/zip"),
	    CSS ("text/css"),
	    MS_WORD_2007 ("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
	    MS_EXCEL_2007 ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	    MS_POWER_POINT_2007 ("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
	    SIGNED_PDF ("application/pdf"),
	    CSV ("text/csv"),
	    RSS ("application/rss+xml"),
	    OCTECT_STREAM ("application/octet-stream"),
	    XSIG ("text/xml"),
	    SIGNED_FACTURAE ("text/xml"),
	    JSON("application/json"),
	    PKCS12("application/x-pkcs12"),
	    JKS("application/x-java-keystore"),
	    SVG ("image/svg+xml"),
	    WEBM("video/webm")
	    ;
	    
		private String name;

		private MimeType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		private static MimeType valueOf(Byte type) {
			if ( type == null )
				return MimeType.OCTECT_STREAM;
			if ( type < 0 )
				return MimeType.OCTECT_STREAM;
			if ( type >= MimeType.values().length )
				return MimeType.OCTECT_STREAM;
			
			return MimeType.values()[type];
		}
		
	}
	
	public enum ContractAttachmentType {
		
		CONTRACT_DOC_DRAFT,
		CONTRACT_DOC,
		
		BASIC_COPY_DRAFT,
		BASIC_COPY,
		
		SEPE_CONTRACT_FILE,
		@Deprecated
		SEPE_CONTRACT_COMMUNICATION_ID,
		@Deprecated
		SEPE_CONTRACT_RESPONSE,
		
		TRAINING_CENTER_DIRECT_DEBIT,
		
		TRAINING_ANNEX_I,
		TRAINING_ANNEX_II,

		EXTENSION_DOC_DRAFT,
		EXTENSION_DOC,
		
		CONTRACT_CLAUSES,
		
		SEPE_EXTENSION_FILE,
		@Deprecated
		SEPE_EXTENSION_COMMUNICATION_ID,
		@Deprecated
		SEPE_EXTENSION_RESPONSE,

		SEPE_CERTIFICADOS_FILE,
		@Deprecated
		SEPE_CERTIFICADOS_COMMUNICATION_ID,
		@Deprecated
		SEPE_CERTIFICADOS_RESPONSE,
		
		SEPE_TRANSFORM_FILE,
		@Deprecated
		SEPE_TRANSFORM_COMMUNICATION_ID,
		@Deprecated
		SEPE_TRANSFORM_RESPONSE,
		
		ENTERPRISE_CERTIFICATE_DOC_DRAFT;
		
		ContractAttachmentType typeOf(Byte type) {
			ContractAttachmentType values [] = ContractAttachmentType.values();
			return values[(byte)type];
		}
	}

}
