package com.esferalia.aon.gwt.document.jooq;

import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record7;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.FileInfo;
import com.code.aon.google.apis.jooq.JooqSettings;
import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.CategoryList;
import com.esferalia.aon.gwt.document.shared.Scope;
import com.esferalia.aon.gwt.document.shared.ScopeList;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.esferalia.aon.gwt.document.shared.TagList;
import com.esferalia.aon.gwt.document.shared.Tags;

public class DBConsults {
	private static Vector<com.esferalia.aon.gwt.document.shared.FileInfo> filesGwt;
	private static Vector<String> names;
	private static Vector<FileInfo> files;
	private static String atype=null;
	private static String domain1=null;

	public static void getAllRattach(String domain) throws SQLException {
		Connection connection = null;
		try {
			
			if ((domain1==null || domain1!=domain)||(atype == null || atype != "all")||(files == null && names == null)) {
				
					domain1=domain;
				atype="all";
				files = new Vector<FileInfo>();
				filesGwt = new Vector<com.esferalia.aon.gwt.document.shared.FileInfo>();
				names=new Vector<String>();
				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				Byte sh = 5;
				
				Result<Record9<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte>> username = dslContext
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.TYPE,
								RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID,RATTACH.CATEGORY,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL)
						.from(RATTACH)
						.join(DOMAIN)
						.on(RATTACH.DOMAIN.eq(DOMAIN.ID))
						.where(RATTACH.TYPE.eq(sh).and(
								DOMAIN.NAME.eq(domain).or(
								DOMAIN.PARENT.eq(dslContext.select(DOMAIN.ID)
										.from(DOMAIN)
										.where(DOMAIN.NAME.eq(domain))))))
						.fetch();

				for (Record9<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte> record : username) {
					FileInfo fi = new FileInfo();
					fi.setAonType("registry");
					if (record.value1() != null) {
						fi.setFileId(record.value1());
					}
					if (record.value2() != null) {
						fi.setTitle(record.value2());
					}
					if (record.value3() != null) {
						fi.setMimetype(record.value3());
					}
					if (record.value4() != null) {
						fi.setType(record.value4());
					}
					if (record.value5() != null) {
						fi.setDate(record.value5());
				
					}
					if (record.value6() != null) {
						fi.setDriveId(record.value6());
					}
					if (record.value7() != null) {
						fi.setCategory(record.value7());
					}
					Tags tags = getTags(domain , fi.getFileId());
					files.add(fi);
					com.esferalia.aon.gwt.document.shared.FileInfo fi2 = new com.esferalia.aon.gwt.document.shared.FileInfo();
					fi2.setAonType(fi.getAonType());
					fi2.setFileId(fi.getFileId());
					fi2.setDriveId(fi.getDriveId());
					fi2.setDate(fi.getDate());
					if(fi.getDate()!=null){
						String dateStr = fi.getDate().toString();
						Integer pos = dateStr.indexOf("-");
						Integer pos2 = dateStr.substring(pos+1).indexOf("-");
						String aux = dateStr.substring(pos2+pos+2)+"-"+dateStr.substring(pos+1, pos2+pos+1)+"-"+dateStr.substring(0,pos);
						fi2.setDateStr(aux); 
					}
					else{ fi2.setDateStr("-");}
				
					fi2.setType(fi.getType());
					fi2.setTitle(fi.getTitle());
					fi2.setMimetype(fi.getMimetype());
					fi2.setSize(0);
					fi2.setSizeStr(FileUtils.byteCountToDisplaySize(fi2.getSize()!=null?fi2.getSize():0));
					fi2.setCategory(fi.getCategory());
					if(record.value7() != null) fi2.setCategoryStr(dslContext.select(CATEGORY.NAME).from(CATEGORY).where(CATEGORY.ID.eq((record.value7()))).fetch().get(0).value1());
					
					else fi2.setCategoryStr("-");
					
					fi2.setTags(tags.getTags().getList());
					if(tags.getTagsStr()!=null)fi2.setTagsStr(tags.getTagsStr()); else fi2.setTagsStr("-");
					if(record.value8()!=null) {
						Scope s = getScope(domain,record.value8());
						fi2.setScope(s);
					}
					if(record.value9()!=null){
						Byte val = record.value9();
						if(val ==0 ) fi2.setConfidential(false);
						else fi2.setConfidential(true);
					}
					
					fi2.setIcon(getmType(fi2));
					
					filesGwt.add(fi2);
					names.add(fi.getTitle());
				}
			
			}
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static Scope getScope(String domain , Integer id) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			Result<Record1<String>> data= dslContext
					.select(SCOPE.DESCRIPTION)
					.from(SCOPE)
					.where(SCOPE.ID.eq(id)).fetch();
			
			Scope s = new Scope();
			for (Record1<String> record : data) {
				if(record.value1() != null){
					s.setName(record.value1());
					s.setId(id);
					s.setDomain(domain);
				}
			}
	
			return s;
			
		} finally {
			if (connection != null)
				connection.close();
		}
		
	}
	
	public static String getmType(com.esferalia.aon.gwt.document.shared.FileInfo fi2){
		if(fi2.getMimetype()!=null){
		MimeType t = MimeType.values()[fi2.getMimetype()];
		if(MimeType.MIME_PDF.getName().equals(t.getName())){
			return "aon-icon-google-drive-pdf-sinfondo";
		}
		else if(MimeType.MIME_JPEG.getName().equals(t.getName()) 
				|| MimeType.MIME_BMP.getName().equals(t.getName())
				|| MimeType.MIME_PNG.getName().equals(t.getName())){
			return "aon-icon-google-drive-image";
		}
		else if(MimeType.MIME_MS_WORD.getName().equals(t.getName())
				|| MimeType.MIME_MS_WORD_2007.getName().equals(t.getName())){
			return "aon-icon-google-drive-word";
		}
		else return "aon-icon-google-drive-unknown";

		}
		else return "aon-icon-google-drive-unknown";
	}

	private static Tags getTags(String domain,int fileId) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			Result<Record3<String, Integer, Integer>> data= dslContext
					.select(TAG.NAME,TAG.ID,TAG.DOMAIN)
					.from(RATTACH_TAG).join(TAG).on(TAG.ID.eq(RATTACH_TAG.TAG))
					.where(RATTACH_TAG.RATTACH.eq(fileId)).fetch();
			
			String string = null;
			Tags tags = new Tags();
			Vector<Tag> ts = new Vector<Tag>();
			for (Record3<String, Integer, Integer> record : data) {
				Tag tag = new Tag();
				if(record.value1() != null){
					tag.setName(record.value1());
					if(string!=null) string= string+","+record.value1();
					else string= record.value1();
				}
				if(record.value2()!=null){
					tag.setId(record.value2());
				}
				if(record.value3()!= null){
					tag.setDomain(getDomain(domain,record.value3()));
				}
				ts.add(tag);
			}
			TagList tl = new TagList();
			tl.setList(ts);
			tags.setTags(tl);
			tags.setTagsStr(string);
			
			return tags;
			
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static ScopeList getScopeList(String domain) throws SQLException{
		Connection connection = null;
		try {
			ScopeList sl = new ScopeList();
			
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record2<String, Integer>> scope = dslContext.select(SCOPE.DESCRIPTION, SCOPE.ID)
					.from(SCOPE).join(DOMAIN)
					.on(SCOPE.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.NAME.eq(domain)).fetch();
			
			Vector<Scope> vector = new Vector<Scope>();
			for (Record2<String, Integer> record : scope) {
				Scope s = new Scope();
				if (record.value1() != null)
					s.setName(record.value1());
				if (record.value2() != null)
					s.setId(record.value2());	
				vector.add(s);
			}

			
			sl.setList(vector);

			return sl;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static TagList getTagList(String domain) throws SQLException{
		Connection connection = null;
		try {
			TagList tl = new TagList();
			
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record2<String, Integer>> tag = dslContext.select(TAG.NAME, TAG.ID)
					.from(TAG).join(DOMAIN)
					.on(TAG.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.NAME.eq(domain)).orderBy(TAG.NAME).fetch();
			
			Vector<Tag> vector = new Vector<Tag>();
			for (Record2<String, Integer> record : tag) {
				Tag t = new Tag();
				if (record.value1() != null)
					t.setName(record.value1());
				if (record.value2() != null)
					t.setId(record.value2());
				vector.add(t);
			}

			
			tl.setList(vector);

			return tl;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static CategoryList getCategoryList(String domain) throws SQLException{
		Connection connection = null;
		try {
			CategoryList cl = new CategoryList();
			
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record2<String, Integer>> category = dslContext.select(CATEGORY.NAME,CATEGORY.ID)
					.from(CATEGORY).join(DOMAIN)
					.on(CATEGORY.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.NAME.eq(domain)).orderBy(CATEGORY.NAME).fetch();
			Vector<Category> vector = new Vector<Category>();
			for (Record2<String, Integer> record : category) {
				Category c = new Category();
				if (record.value1() != null)
					c.setName(record.value1());
				if (record.value2() !=null)
					c.setId(record.value2());
				vector.add(c);
			}

			
			cl.setList(vector);

			return cl;
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static Vector<com.esferalia.aon.gwt.document.shared.FileInfo> getServiConvenios(String domain) throws SQLException{
		Connection connection = null;
		try {
			Vector<com.esferalia.aon.gwt.document.shared.FileInfo> vector = new Vector<com.esferalia.aon.gwt.document.shared.FileInfo>();

			if ((domain1==null || domain1!=domain)||(atype == null || atype != "serviConvenios")||files == null && names == null) {
					domain1=domain;
				atype= "serviConvenios";
				files = new Vector<FileInfo>();
				filesGwt = new Vector<com.esferalia.aon.gwt.document.shared.FileInfo>();
				names=new Vector<String>();
				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());

				Result<Record7<Integer, String, Byte, Byte, Date, String, Integer>> username = dslContext
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.TYPE,
								RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID,RATTACH.CATEGORY)
						.from(RATTACH)
						.where(RATTACH.DOMAIN.eq(0))
						.fetch();

				for (Record7<Integer, String, Byte, Byte, Date, String, Integer> record : username) {
					FileInfo fi = new FileInfo();
					fi.setAonType("registry");
					if (record.value1() != null) {
						fi.setFileId(record.value1());
					}
					if (record.value2() != null) {
						fi.setTitle(record.value2());
					}
					if (record.value3() != null) {
						fi.setMimetype(record.value3());
					}
					if (record.value4() != null) {
						fi.setType(record.value4());
					}
					if (record.value5() != null) {
						fi.setDate(record.value5());
					}
					if (record.value6() != null) {
						fi.setDriveId(record.value6());
					}
					if (record.value7() != null) {
						fi.setCategory(record.value7());
					}
					

					files.add(fi);
					com.esferalia.aon.gwt.document.shared.FileInfo fi2 = new com.esferalia.aon.gwt.document.shared.FileInfo();
					fi2.setAonType(fi.getAonType());
					fi2.setFileId(fi.getFileId());
					fi2.setDriveId(fi.getDriveId());
					fi2.setDate(fi.getDate());
					fi2.setType(fi.getType());
					fi2.setTitle(fi.getTitle());
					fi2.setMimetype(fi.getMimetype());
					fi2.setSize(0);
					fi2.setSizeStr(FileUtils.byteCountToDisplaySize(fi2.getSize()!=null?fi2.getSize():0));
					fi2.setCategory(fi.getCategory());
					if(record.value7() != null) fi2.setCategoryStr(dslContext.select(CATEGORY.NAME).from(CATEGORY).where(CATEGORY.ID.eq((record.value7()))).fetch().get(0).value1());
					else fi2.setCategoryStr("-");
					
					fi2.setIcon(getmType(fi2));
					vector.add(fi2);
					filesGwt.add(fi2);
					names.add(fi.getTitle());
				}
			}
			return vector;
			
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	
	public static Vector<String> getSons(String domain) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<String>> sons = dslContext
					.select(DOMAIN.NAME)
					.from(DOMAIN)
					.where(DOMAIN.PARENT.eq(dslContext.select(DOMAIN.ID)
												.from(DOMAIN)
												.where(DOMAIN.NAME.eq(domain))))
					.fetch();
			
			Vector<String> vector = new Vector<String>();
			
			for (Record1<String> record : sons) {
				vector.add(record.value1());
			}
			return vector;
			
		} finally {
			if (connection != null)
				connection.close();
		}
		
	}
	
	public static Vector<FileInfo> getFiles() {
		return files;
	}

	public static void setFiles(Vector<FileInfo> files) {
		DBConsults.files = files;
	}

	public static Vector<String> getNames() {
		return names;
	}

	public static void setNames(Vector<String> names) {
		DBConsults.names = names;
	}

	public static Vector<com.esferalia.aon.gwt.document.shared.FileInfo> getFilesGwt() {
		return filesGwt;
	}

	public static void setFilesGwt(
			Vector<com.esferalia.aon.gwt.document.shared.FileInfo> filesGwt) {
		DBConsults.filesGwt = filesGwt;
	}

	public static void removeFile(String domain,Integer id) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			dslContext.delete(RATTACH_TAG).where(RATTACH_TAG.RATTACH.eq(id)).execute();
			dslContext.delete(RATTACH).where(RATTACH.ID.eq(id)).execute();
			
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Integer getDomainId(String domain,String dom) throws SQLException{
		Connection connection = null;
		try {
			
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<Integer>> data = dslContext
					.select(DOMAIN.ID).from(DOMAIN).where(DOMAIN.NAME.eq(dom)).fetch();
			
			return data.get(0).value1();
			
		} finally {
			if (connection != null)
				connection.close();
		}
		
	}
	
	public static String getDomain(String domain,Integer id) throws SQLException{
		Connection connection = null;
		try {
			
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record1<String>> data = dslContext
					.select(DOMAIN.NAME).from(DOMAIN).where(DOMAIN.ID.eq(id)).fetch();
			
			return data.get(0).value1();
			
		} finally {
			if (connection != null)
				connection.close();
		}
		
	}
	
	public static Integer insertFile(String domain,FileInfo fi) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			return dslContext.insertInto(RATTACH,RATTACH.REGISTRY,RATTACH.DOMAIN,RATTACH.CATEGORY,RATTACH.MIMETYPE,RATTACH.DESCRIPTION,RATTACH.TYPE,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.ATTACH_DATE,RATTACH.DATA,RATTACH.DRIVE_ID,RATTACH.DPARENT_ID)
						.values(1,fi.getDomainId(),fi.getCategory(),fi.getMimetype(),fi.getTitle(),(byte)fi.getType(),fi.getScopeId(),fi.getSecurityLevel(),fi.getDateSql(),null,null,null).returning(RATTACH.ID).fetchOne().getId();
	
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static Integer insertFileData(String domain,Integer id, byte[] a) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			return dslContext.update(RATTACH).set(RATTACH.DATA,a).where(RATTACH.ID.eq(id)).execute();

		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void insertTagsFile(Integer id, Vector<Tag> tags, String domain,Integer dom) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			for (Tag tag : tags) {
				dslContext.insertInto(RATTACH_TAG,RATTACH_TAG.DOMAIN,RATTACH_TAG.RATTACH,RATTACH_TAG.TAG)
				.values(dom,id,tag.getId()).returning(RATTACH.ID).execute();
			}
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void updateFile(String domain,FileInfo fi) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(RATTACH).set(RATTACH.CATEGORY,fi.getCategory())
									.set(RATTACH.MIMETYPE,fi.getMimetype())
									.set(RATTACH.DESCRIPTION,fi.getTitle())
									.set(RATTACH.SCOPE,fi.getScopeId())
									.set(RATTACH.SECURITY_LEVEL,fi.getSecurityLevel())
									.set(RATTACH.ATTACH_DATE,fi.getDateSql())
							.where(RATTACH.ID.eq(fi.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static FileInfo getDataAndName(Integer id, String domain) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			Result<Record2<byte[], String>> data = dslContext.select(RATTACH.DATA,RATTACH.DESCRIPTION)
					.from(RATTACH).where(RATTACH.ID.eq(id)).fetch();
			FileInfo fi = new FileInfo();
			for (Record2<byte[], String> record : data) {
				//InputStream is =  new ByteArrayInputStream(record.value1());
				if(record.value1()!= null) fi.setData(record.value1());
				if(record.value2()!= null) fi.setTitle(record.value2());
			}
			return fi;
			
		} finally {
			if (connection != null)
				connection.close();
		}
			
	}
}
