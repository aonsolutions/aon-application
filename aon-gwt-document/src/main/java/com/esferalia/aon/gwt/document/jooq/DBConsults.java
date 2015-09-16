package com.esferalia.aon.gwt.document.jooq;

import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Contact.CONTACT;
import static com.esferalia.aon.jooq.tables.ContactData.CONTACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Record16;
import org.jooq.Record18;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record7;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.jooq.JooqSettings;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.registry.Registry;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.CategoryList;
import com.esferalia.aon.gwt.document.shared.Contact;
import com.esferalia.aon.gwt.document.shared.ContactList;
import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.Domain;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.Scope;
import com.esferalia.aon.gwt.document.shared.ScopeList;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.esferalia.aon.gwt.document.shared.TagList;
import com.esferalia.aon.gwt.document.shared.Tags;

public class DBConsults {
	private static Vector<FileInfo> filesGwt;
	private static String atype=null;
	private static String domain1=null;
	private static Vector<FileInfo> vaux;

	private static boolean esta(FileInfo fi,Integer user_id,DSLContext dslContext){
		Result<Record1<Integer>> result = dslContext.select(USER_SCOPE.SCOPE)
				.from(USER_SCOPE)
				.where(USER_SCOPE.USER_ID.eq(user_id))
				
				.fetch();
		
		for (Record1<Integer> r: result) {
			if(fi.getScope()!=null){
				if(fi.getScope().getId().equals(r.value1())){
					return true;
				}
			}
		}
		return false;
	}
	public static Document getAllRattach(String domain,String domain2,Integer user_id, Boolean confidential, Integer domainId,Integer userDomainId) throws SQLException {
		Connection connection = null;
		try {
			

			//if ((domain1==null || domain1!=domain)||(atype == null || atype != "all")) {
				
				domain1=domain;
				atype="all";
				filesGwt = new Vector<FileInfo>();
				vaux = new Vector<FileInfo>();
				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());
				
				Condition c;

				if(userDomainId!= domainId){

					c=(RATTACH.SCOPE.isNotNull().or(RATTACH.SCOPE.isNull()));
					
				}
				else
					c = RATTACH.SCOPE.isNull();
				
				Byte sh = (byte)RegistryAttachmentType.CORPORATE_IDENTITY.ordinal();//5;
				//rattach domain + parent domain + scope not null
				Result<Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp>> username = dslContext
						.selectDistinct(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.TYPE,
								RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID,RATTACH.CATEGORY,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.DPARENT_ID,DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,DOMAIN.PARENT,
								RATTACH.CREATION_USER, RATTACH.CREATION_DATE, RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
						.from(RATTACH)
						.join(DOMAIN)
						.on(RATTACH.DOMAIN.eq(DOMAIN.ID))
						.join(USER_SCOPE).on(USER_SCOPE.SCOPE.eq(RATTACH.SCOPE))
						.where(USER_SCOPE.USER_ID.eq(user_id).and(RATTACH.TYPE.eq(sh).and(
								RATTACH.DOMAIN.eq(domainId).or(
								RATTACH.DOMAIN.eq(dslContext.select(DOMAIN.PARENT)
														.from(DOMAIN)
														.where(DOMAIN.ID.eq(domainId)))))))
						.fetch();
				
				//rattach scope null parent
				Result<Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp>> result1 = dslContext
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.TYPE,
								RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID,RATTACH.CATEGORY,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.DPARENT_ID,DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,DOMAIN.PARENT,
								RATTACH.CREATION_USER, RATTACH.CREATION_DATE, RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
						.from(RATTACH)
						.join(DOMAIN)
						.on(RATTACH.DOMAIN.eq(DOMAIN.ID))
						.where(RATTACH.TYPE.eq(sh).and((RATTACH.SCOPE.isNull().and(RATTACH.DOMAIN.eq(dslContext.select(DOMAIN.PARENT)
										.from(DOMAIN)
										.where(DOMAIN.ID.eq(domainId)))))))
						.fetch();
				
				//rattach scope null 
				Result<Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp>> result = dslContext
						.selectDistinct(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.TYPE,
								RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID,RATTACH.CATEGORY,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.DPARENT_ID,DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,DOMAIN.PARENT,
								RATTACH.CREATION_USER, RATTACH.CREATION_DATE, RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
						.from(RATTACH)
						.join(DOMAIN)
						.on(RATTACH.DOMAIN.eq(DOMAIN.ID))
						.where(RATTACH.TYPE.eq(sh).and((c.and(RATTACH.DOMAIN.eq(domainId)))))
						.fetch();

				// sons domain
				Result<Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp>> result2 = dslContext
						.selectDistinct(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.TYPE,
								RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID,RATTACH.CATEGORY,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.DPARENT_ID,DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,DOMAIN.PARENT,
								RATTACH.CREATION_USER, RATTACH.CREATION_DATE, RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
						.from(RATTACH)
						.join(DOMAIN)
						.on(RATTACH.DOMAIN.eq(DOMAIN.ID))
						.where(RATTACH.TYPE.eq(sh).and(DOMAIN.PARENT.eq(dslContext.select(DOMAIN.ID)
										.from(DOMAIN)
										.where(DOMAIN.ID.eq(domainId)))))
						.fetch();
				
				for (Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp> record : username) {
					FileInfo fi = newFileInfo(dslContext,domain,domain2,record);
					
					if (!fi.getConfidential() || (confidential && fi.getConfidential())){
						filesGwt.add(fi);
						if(fi.getDomain().equalsIgnoreCase(domain2) || fi.getIsParent())
							if(domain.equals(domain2))
								vaux.add(fi);
							else if(!fi.getIsParent()) vaux.add(fi);
					}
				}
				for (Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp> record : result) {
					FileInfo fi = newFileInfo(dslContext,domain,domain2,record);
					if (!esta(fi,user_id,dslContext)&&(!fi.getConfidential() || (confidential && fi.getConfidential()))){
						filesGwt.add(fi);
						if(fi.getDomain().equalsIgnoreCase(domain2) || fi.getIsParent())
							if(domain.equals(domain2))
								vaux.add(fi);
							else if(!fi.getIsParent()) vaux.add(fi);
					}
				}
				for (Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp> record : result1) {
					FileInfo fi = newFileInfo(dslContext,domain,domain2,record);
					if (!fi.getConfidential() || (confidential && fi.getConfidential())){
						filesGwt.add(fi);
						if(fi.getDomain().equalsIgnoreCase(domain2) || fi.getIsParent())
							if(domain.equals(domain2))
								vaux.add(fi);
							else if(!fi.getIsParent()) 
								vaux.add(fi);
					}
				}
				for (Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp> record : result2) {
					FileInfo fi = newFileInfo(dslContext,domain,domain2,record);
					
					if (!fi.getConfidential() || (confidential && fi.getConfidential())){
						filesGwt.add(fi);
						if(fi.getDomain().equalsIgnoreCase(domain2) || fi.getIsParent())
							if(domain.equals(domain2))
								vaux.add(fi);
							else if(!fi.getIsParent()) vaux.add(fi);
					}
				}
			//}
			Document document = new Document();			
			document.setFiles(filesGwt);
			/*Vector<FileInfo> aux = new Vector<FileInfo>();//=  filesGwt.stream().filter(d -> d.getDomain().equalsIgnoreCase(domain));
			filesGwt.stream().forEach(f -> {
				if(f.getDomain().equalsIgnoreCase(domain))
					aux.add(f);
			});*/
			document.setEfiles(vaux);
			document.setFilter(vaux);
			return document;
			
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	private static FileInfo newFileInfo(DSLContext dslContext, String domain, String domain2, Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp> record) throws SQLException {
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
			String dateStr = fi.getDate().toString();
			Integer pos = dateStr.indexOf("-");
			Integer pos2 = dateStr.substring(pos+1).indexOf("-");
			String aux = dateStr.substring(pos2+pos+2)+"-"+dateStr.substring(pos+1, pos2+pos+1)+"-"+dateStr.substring(0,pos);
			fi.setDateStr(aux);
		}
		else fi.setDateStr("-");
		if (record.value6() != null) {
			fi.setDriveId(record.value6());
		}
		if (record.value7() != null) {
			fi.setCategory(record.value7());
		}
		Tags tags = getTags(domain , fi.getFileId());
	
		if(record.value7() != null) {
			Result<Record1<String>> a = dslContext.select(CATEGORY.NAME).from(CATEGORY).where(CATEGORY.ID.eq((record.value7()))).fetch();
			a.stream().forEach(r-> {
				fi.setCategoryStr(r.value1());
			});
		}
		
		else fi.setCategoryStr("-");
		
		fi.setTags(tags.getTags().getList());
		if(tags.getTagsStr()!=null)fi.setTagsStr(tags.getTagsStr()); else fi.setTagsStr("-");
		if(record.value8()!=null) {
			Scope s = getScope(domain,record.value8());
			fi.setScope(s);
		}
		if(record.value9()!=null){
			Byte val = record.value9();
			if(val ==0 ) fi.setConfidential(false);
			else fi.setConfidential(true);
		}
		
		if (record.value10() != null){
			String s = record.value10();
			fi.setSize(Integer.valueOf(s));
		}
		else fi.setSize(0);
		fi.setSizeStr(FileUtils.byteCountToDisplaySize(fi.getSize()!=null?fi.getSize():0));
		
		
		fi.setIcon(getmType(fi));
		if(record.value11()!=null)
			fi.setDomainId(record.value11());
		if(record.value12()!=null)
			fi.setDomain(record.value12());
		if(record.value13()!=null)
			fi.setDomainDescription(record.value13());
		if(record.value14()==null)
			if(!fi.getDomain().equals(domain2))
				fi.setIsParent(true);
		if(record.value15() != null)
			fi.setCreationUser(record.value15());
		if(record.value16() != null){
			long a = record.value16().getTime();
			Calendar cal = Calendar.getInstance();
			cal.setTime(new java.util.Date(a));
			String dateStr = cal.get(Calendar.DATE)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR);
			fi.setCreationDateStr(dateStr);
		}
		if(record.value17() != null)
			fi.setModificationUser(record.value17());
		if(record.value18() != null){
			long a = record.value18().getTime();
			Calendar cal = Calendar.getInstance();
			cal.setTime(new java.util.Date(a));
			String dateStr = cal.get(Calendar.DATE)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR);
			fi.setModificationDateStr(dateStr);
		}
		return fi;
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
	
	public static String getmType(
			FileInfo fi2) {
		return getmType(fi2.getMimetype());
	}
	public static String getmType(
			Byte mimetype) {
		if (mimetype != null) {
			MimeType t = MimeType.values()[mimetype];
			if (MimeType.MIME_PDF.getName().equals(t.getName())) {
				return "aon-icon-google-drive-pdf-sinfondo";
			} else if (MimeType.MIME_JPEG.getName().equals(t.getName())
					|| MimeType.MIME_BMP.getName().equals(t.getName())
					|| MimeType.MIME_PNG.getName().equals(t.getName())) {
				return "aon-icon-google-drive-image";
			} else if (MimeType.MIME_MS_WORD.getName().equals(t.getName())
					|| MimeType.MIME_MS_WORD_2007.getName().equals(t.getName())) {
				return "aon-icon-google-drive-word";
			} else if (MimeType.MIME_MS_EXCEL.getName().equals(t.getName())
					|| MimeType.MIME_MS_EXCEL_2007.getName().equals(t.getName())){
				return "aon-icon-google-drive-excel";
			} else if (MimeType.MIME_MS_POWER_POINT.getName().equals(t.getName())
					|| MimeType.MIME_MS_POWER_POINT_2007.getName().equals(t.getName())){
				return "aon-icon-google-drive-power-point";
			} else if (MimeType.MIME_ZIP.getName().equals(t.getName())){
				return "aon-icon-google-drive-zip";
			} else if (MimeType.MIME_AVI.getName().equals(t.getName())
					|| MimeType.MIME_MPEG.getName().equals(t.getName())){
				return "aon-icon-google-drive-mov";
			} else if (MimeType.MIME_MP3.getName().equals(t.getName())
					|| MimeType.MIME_WAV.getName().equals(t.getName())){
				return "aon-icon-google-drive-audio";
			} else
				return "aon-icon-google-drive-unknown";

		} else
			return "aon-icon-google-drive-unknown";
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
					if(string!=null) string= string+", "+record.value1();
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
	
	public static ScopeList getScopeList(String domain,Integer domainId, Integer user_id, Integer userDomainId) throws SQLException{
		Connection connection = null;
		try {
			ScopeList sl = new ScopeList();
			
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
		
			Result<Record2<String, Integer>> scope;
			if(userDomainId.equals(domainId)){
				scope = dslContext.select(SCOPE.DESCRIPTION, SCOPE.ID)
					.from(SCOPE).join(USER_SCOPE)
					.on(USER_SCOPE.SCOPE.eq(SCOPE.ID))
					.where(SCOPE.DOMAIN.eq(domainId).and(USER_SCOPE.USER_ID.eq(user_id))).fetch();
			}
			else{
				scope = dslContext.select(SCOPE.DESCRIPTION, SCOPE.ID)
						.from(SCOPE)
						.where(SCOPE.DOMAIN.eq(domainId)).fetch();
			}
			Vector<Scope> vector = new Vector<Scope>();
			for (Record2<String, Integer> record : scope) {
				Scope s = new Scope();
				if (record.value1() != null)
					s.setName(record.value1());
				if (record.value2() != null)
					s.setId(record.value2());	
				s.setIsParent(false);
				s.setIsSon(false);
				vector.add(s);
			}
			
			Result<Record2<String, Integer>> scopeParent = dslContext.select(SCOPE.DESCRIPTION, SCOPE.ID)
					.from(SCOPE).join(DOMAIN)
					.on(SCOPE.DOMAIN.eq(DOMAIN.PARENT)).join(USER_SCOPE)
					.on(USER_SCOPE.SCOPE.eq(SCOPE.ID))
					.where(DOMAIN.ID.eq(domainId).and(USER_SCOPE.USER_ID.eq(user_id))).fetch();
			
			for (Record2<String, Integer> record : scopeParent) {
				Scope s = new Scope();
				if (record.value1() != null)
					s.setName(record.value1());
				if (record.value2() != null)
					s.setId(record.value2());	
				s.setIsParent(true);
				s.setIsSon(false);
				long i = vector.stream().filter(scop -> scop.getName().equals(s.getName())).count();
				if(i==0) vector.add(s);
			}

			sl.setList(vector);

			return sl;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static ScopeList getScopeListSon(String domain,Integer domainId, Integer user_id, Integer userDomainId) throws SQLException{
		Connection connection = null;
		try {
			ScopeList sl = new ScopeList();
			
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
		Result<Record3<String, Integer, String>> scopeSon = dslContext.select(SCOPE.DESCRIPTION,SCOPE.ID,DOMAIN.NAME)
				.from(SCOPE).join(DOMAIN)
				.on(SCOPE.DOMAIN.eq(DOMAIN.ID))
				.where(DOMAIN.PARENT.eq(domainId)).orderBy(SCOPE.DESCRIPTION).fetch();
		Vector<Scope> vector = new Vector<Scope>();
		for (Record3<String, Integer, String> record : scopeSon) {
			Scope s = new Scope();
			if (record.value1() != null)
				s.setName(record.value1());
			if (record.value2() !=null)
				s.setId(record.value2());
			s.setIsParent(false);
			s.setIsSon(true);
			if(record.value3() !=null) 
				s.setDomain(record.value3());
			long i = vector.stream().filter(cat -> cat.getName().equals(s.getName())).count();
			if(i==0) vector.add(s);			
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
					.where(DOMAIN.NAME.eq(domain).and(TAG.TYPE.eq((byte)0))).orderBy(TAG.NAME).fetch();
			
			Vector<Tag> vector = new Vector<Tag>();
			for (Record2<String, Integer> record : tag) {
				Tag t = new Tag();
				if (record.value1() != null)
					t.setName(record.value1());
				if (record.value2() != null)
					t.setId(record.value2());
				t.setIsParent(false);
				t.setIsSon(false);
				vector.add(t);
			}
			
			Result<Record2<String, Integer>> tagParent = dslContext.select(TAG.NAME, TAG.ID)
					.from(TAG).join(DOMAIN)
					.on(TAG.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.NAME.eq(domain).and(TAG.TYPE.eq((byte)0))).orderBy(TAG.NAME).fetch();
			
			for (Record2<String, Integer> record : tagParent) {
				Tag t = new Tag();
				if (record.value1() != null)
					t.setName(record.value1());
				if (record.value2() != null)
					t.setId(record.value2());
				t.setIsParent(true);
				t.setIsSon(false);
				long i = vector.stream().filter(tg -> tg.getName().equals(t.getName())).count();
				if(i==0) vector.add(t);
			}			
			
			tl.setList(vector);

			return tl;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static TagList getTagListSon(String domain) throws SQLException{
		Connection connection = null;
		try {
			TagList tl = new TagList();
			
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
		Result<Record3<String, Integer, String>> tagSon = dslContext.select(TAG.NAME,TAG.ID,DOMAIN.NAME)
				.from(TAG).join(DOMAIN)
				.on(TAG.DOMAIN.eq(DOMAIN.ID))
				.where(DOMAIN.PARENT.eq(dslContext.select(DOMAIN.ID)
						.from(DOMAIN)
						.where(DOMAIN.NAME.eq(domain))).and(TAG.TYPE.eq((byte)0))).orderBy(TAG.NAME).fetch();
		Vector<Tag> vector = new Vector<Tag>();
		for (Record3<String, Integer, String> record : tagSon) {
			Tag t = new Tag();
			if (record.value1() != null)
				t.setName(record.value1());
			if (record.value2() !=null)
				t.setId(record.value2());
			t.setIsParent(false);
			t.setIsSon(true);
			if(record.value3() !=null) 
				t.setDomain(record.value3());
			//long i = vector.stream().filter(cat -> cat.getName().equals(t.getName())).count();
			//if(i==0) 
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
					.where(DOMAIN.NAME.eq(domain).and(CATEGORY.TYPE.eq((byte)0))).orderBy(CATEGORY.NAME).fetch();
			Vector<Category> vector = new Vector<Category>();
			for (Record2<String, Integer> record : category) {
				Category c = new Category();
				if (record.value1() != null)
					c.setName(record.value1());
				if (record.value2() !=null)
					c.setId(record.value2());
				c.setIsParent(false);
				c.setIsSon(false);
				vector.add(c);
			}
						
			Result<Record2<String, Integer>> categoryParent = dslContext.select(CATEGORY.NAME,CATEGORY.ID)
					.from(CATEGORY).join(DOMAIN)
					.on(CATEGORY.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.NAME.eq(domain).and(CATEGORY.TYPE.eq((byte)0))).orderBy(CATEGORY.NAME).fetch();
			for (Record2<String, Integer> record : categoryParent) {
				Category c = new Category();
				if (record.value1() != null)
					c.setName(record.value1());
				if (record.value2() !=null)
					c.setId(record.value2());
				c.setIsParent(true);
				c.setIsSon(false);
				long i = vector.stream().filter(cat -> cat.getName().equals(c.getName())).count();
				if(i==0) vector.add(c);
			}
			
			cl.setList(vector);
			return cl;
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static CategoryList getCategoryListSon(String domain) throws SQLException{
		Connection connection = null;
		try {
			CategoryList cl = new CategoryList();
			
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
		Result<Record3<String, Integer, String>> categorySon = dslContext.select(CATEGORY.NAME,CATEGORY.ID,DOMAIN.NAME)
				.from(CATEGORY).join(DOMAIN)
				.on(CATEGORY.DOMAIN.eq(DOMAIN.ID))
				.where(DOMAIN.PARENT.eq(dslContext.select(DOMAIN.ID)
						.from(DOMAIN)
						.where(DOMAIN.NAME.eq(domain))).and(CATEGORY.TYPE.eq((byte)0))).orderBy(CATEGORY.NAME).fetch();
		Vector<Category> vector = new Vector<Category>();
		for (Record3<String, Integer, String> record : categorySon) {
			Category c = new Category();
			if (record.value1() != null)
				c.setName(record.value1());
			if (record.value2() !=null)
				c.setId(record.value2());
			c.setIsParent(false);
			c.setIsSon(true);
			if(record.value3() !=null) 
				c.setDomain(record.value3());
			long i = vector.stream().filter(cat -> cat.getName().equals(c.getName())).count();
			if(i==0) vector.add(c);			
		}
		cl.setList(vector);
		return cl;
	} finally {
		if (connection != null)
			connection.close();
	}
	}
	
	public static Vector<FileInfo> getServiConvenios(String domain) throws SQLException{
		Connection connection = null;
		try {
			Vector<FileInfo> vector = new Vector<FileInfo>();

			if ((domain1==null || domain1!=domain)||(atype == null || atype != "serviConvenios") ) {
				domain1=domain;
				atype= "serviConvenios";
				filesGwt = new Vector<FileInfo>();
				connection = DatabaseSync.getConnection(domain);

				DSLContext dslContext = DSL.using(connection,
						JooqSettings.getDefaultSettings());

				Result<Record7<Integer, String, Byte, Byte, Date, String, Integer>> username = dslContext
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.TYPE,
								RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID,RATTACH.CATEGORY)
						.from(RATTACH)
						.where(RATTACH.DOMAIN.eq(0).and(RATTACH.ID.lessThan(0)))
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
						String dateStr = fi.getDate().toString();
						Integer pos = dateStr.indexOf("-");
						Integer pos2 = dateStr.substring(pos+1).indexOf("-");
						String aux = dateStr.substring(pos2+pos+2)+"-"+dateStr.substring(pos+1, pos2+pos+1)+"-"+dateStr.substring(0,pos);
						fi.setDateStr(aux);
					} else fi.setDateStr("-");
					if (record.value6() != null) {
						fi.setDriveId(record.value6());
					}
					if (record.value7() != null) {
						fi.setCategory(record.value7());
					}
					Tags tags = getTags(domain , fi.getFileId());
					fi.setSize(0);
					fi.setSizeStr(FileUtils.byteCountToDisplaySize(fi.getSize()!=null?fi.getSize():0));
					if(record.value7() != null) fi.setCategoryStr(dslContext.select(CATEGORY.NAME).from(CATEGORY).where(CATEGORY.ID.eq((record.value7()))).fetch().get(0).value1());
					else fi.setCategoryStr("-");
					
					fi.setIcon(getmType(fi));
					
					fi.setTags(tags.getTags().getList());
					if(tags.getTagsStr()!=null)fi.setTagsStr(tags.getTagsStr()); else fi.setTagsStr("-");
					
					fi.setConfidential(false);
					fi.setDomainId(0);
					vector.add(fi);
					filesGwt.add(fi);
				}
			}
			return vector;
			
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	
	public static Vector<Domain> getSons(String domain) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record2<String, String>> sons = dslContext
					.select(DOMAIN.NAME,DOMAIN.DESCRIPTION)
					.from(DOMAIN)
					.where(DOMAIN.PARENT.eq(dslContext.select(DOMAIN.ID)
												.from(DOMAIN)
												.where(DOMAIN.NAME.eq(domain))))
					.fetch();
			
			Vector<Domain> vector = new Vector<Domain>();
			
			for (Record2<String, String> record : sons) {
				Domain d = new Domain();
				d.setName(record.value1());
				d.setDescription(record.value2());
				vector.add(d);
			}
			return vector;
			
		} finally {
			if (connection != null)
				connection.close();
		}
		
	}
	
	

	public static Vector<FileInfo> getFilesGwt() {
		return filesGwt;
	}

	public static void setFilesGwt(
			Vector<FileInfo> filesGwt) {
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

			Record1<String> data = dslContext
					.select(DOMAIN.NAME).from(DOMAIN).where(DOMAIN.ID.eq(id)).fetchOne();
			
			return data.value1();
			
		} finally {
			if (connection != null)
				connection.close();
		}
		
	}
	
	public static Integer getDomainParent(String domain,Integer id) throws SQLException{
		Connection connection = null;
		try {
			
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Record1<Integer> data = dslContext
					.select(DOMAIN.PARENT).from(DOMAIN).where(DOMAIN.ID.eq(id)).fetchOne();
			if(data.value1()!= null)
				return data.value1();
			else return null;
			
		} finally {
			if (connection != null)
				connection.close();
		}
		
	}
	
	public static Integer insertFile(String domain,com.code.aon.google.apis.FileInfo fi) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			
			Result<Record1<Integer>> reg = dslContext.select(ENTERPRISE.REGISTRY)
				.from(ENTERPRISE.join(DOMAIN).on(ENTERPRISE.DOMAIN.eq(DOMAIN.ID)))
				.where(DOMAIN.NAME.eq(domain)).fetch();
			
			Integer userId = AonUtil.getAuthPrincipal().getUserId();
			Record1<String> user = dslContext.select(USER.LOGIN).from(USER).where(USER.ID.eq(userId)).fetchOne();
			long currentDate = new java.util.Date().getTime();
			
			return dslContext.insertInto(RATTACH,RATTACH.REGISTRY,RATTACH.DOMAIN,RATTACH.CATEGORY,RATTACH.MIMETYPE,RATTACH.DESCRIPTION,RATTACH.TYPE,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.ATTACH_DATE,RATTACH.DATA,RATTACH.DRIVE_ID,RATTACH.DPARENT_ID, RATTACH.CREATION_USER, RATTACH.CREATION_DATE, RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
						.values(reg.get(0).value1(),fi.getDomainId(),fi.getCategory(),fi.getMimetype(),fi.getTitle(),(byte)fi.getType(),fi.getScopeId(),fi.getSecurityLevel(),fi.getDateSql(),null,null,fi.getSize().toString()
								,user.value1(),new Timestamp(currentDate),user.value1(),new Timestamp(currentDate)).returning(RATTACH.ID).fetchOne().getId();
	
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static FileInfo getFile(String domain,Integer id) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Result<Record15<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, Integer, String, String, Timestamp, String, Timestamp>> record = dslContext
					.select(RATTACH.ID, RATTACH.DESCRIPTION,
							RATTACH.MIMETYPE, RATTACH.TYPE,
							RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID,RATTACH.CATEGORY,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.DATA.length(),RATTACH.DPARENT_ID,
							RATTACH.CREATION_USER, RATTACH.CREATION_DATE, RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
					.from(RATTACH)
					.where(RATTACH.ID.eq(id))
					.fetch();
			FileInfo fi = new FileInfo();
			
			record.stream().forEach(r->{
				fi.setAonType("registry");
				if (r.value1() != null) 
					fi.setFileId(r.value1());
				if (r.value2() != null) 
					fi.setTitle(r.value2());
				if (r.value3() != null) 
					fi.setMimetype(r.value3());
				if (r.value4() != null) 
					fi.setType(r.value4());
				if (r.value5() != null) 
					fi.setDate(r.value5());
				if (r.value6() != null) 
					fi.setDriveId(r.value6());
				if (r.value7() != null) 
					fi.setCategory(r.value7());
				Tags tags=null;
				try {
					tags = getTags(domain , fi.getFileId());
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(fi.getDate()!=null){
					String dateStr = fi.getDate().toString();
					Integer pos = dateStr.indexOf("-");
					Integer pos2 = dateStr.substring(pos+1).indexOf("-");
					String aux = dateStr.substring(pos2+pos+2)+"-"+dateStr.substring(pos+1, pos2+pos+1)+"-"+dateStr.substring(0,pos);
					fi.setDateStr(aux); 
				}
				else fi.setDateStr("-");
				if(r.value7() != null) fi.setCategoryStr(dslContext.select(CATEGORY.NAME).from(CATEGORY).where(CATEGORY.ID.eq((r.value7()))).fetch().get(0).value1());
				else fi.setCategoryStr("-");
				fi.setTags(tags.getTags().getList());
				if(tags.getTagsStr()!=null)fi.setTagsStr(tags.getTagsStr()); else fi.setTagsStr("-");
				if(r.value8()!=null) {
					Scope s = null;
					try {
						s = getScope(domain,record.get(0).value8());
					} catch (Exception e) {
						e.printStackTrace();
					}
					fi.setScope(s);
				}
				if(r.value9()!=null){
					Byte val = r.value9();
					if(val ==0 ) fi.setConfidential(false);
					else fi.setConfidential(true);
				}
				if (r.value10() != null) fi.setSize(r.value10());
				else if (fi.getDriveId() != null && r.value11() != null){
					String s = r.value11();
					fi.setSize(Integer.valueOf(s));
				}
				else fi.setSize(0);
				fi.setSizeStr(FileUtils.byteCountToDisplaySize(fi.getSize()!=null?fi.getSize():0));
				
				fi.setIcon(getmType(fi));
				
				if(r.value12() != null) fi.setCreationUser(r.value12());
				if(r.value13() != null){
					long a = r.value13().getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTime(new java.util.Date(a));
					String dateStr = cal.get(Calendar.DATE)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR);
					fi.setCreationDateStr(dateStr);
				}
				if(r.value14() != null) fi.setModificationUser(r.value14());
				if(r.value15() != null){
					long a = r.value15().getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTime(new java.util.Date(a));
					String dateStr = cal.get(Calendar.DATE)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR);
					fi.setModificationDateStr(dateStr);
				}
			});
			return fi;
		}finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static void insertFileData(String domain,Integer id, byte[] a) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			Integer userId = AonUtil.getAuthPrincipal().getUserId();
			Record1<String> user = dslContext.select(USER.LOGIN).from(USER).where(USER.ID.eq(userId)).fetchOne();
			long currentDate = new java.util.Date().getTime();
			
			dslContext.update(RATTACH).set(RATTACH.DATA,a)
						.set(RATTACH.MODIFICATION_USER, user.value1())
						.set(RATTACH.MODIFICATION_DATE, new Timestamp(currentDate))
						.where(RATTACH.ID.eq(id))
						.execute();
			
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
	
	public static void updateFile(String domain,com.code.aon.google.apis.FileInfo fi,Vector<Tag> tags, Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			
			dslContext.delete(RATTACH_TAG).where(RATTACH_TAG.RATTACH.eq(fi.getFileId())).execute();

			for (Tag tag : tags) {
				dslContext.insertInto(RATTACH_TAG,RATTACH_TAG.DOMAIN,RATTACH_TAG.RATTACH,RATTACH_TAG.TAG)
				.values(domainId,fi.getFileId(),tag.getId()).execute();
			}
			
			Integer userId = AonUtil.getAuthPrincipal().getUserId();
			Record1<String> user = dslContext.select(USER.LOGIN).from(USER).where(USER.ID.eq(userId)).fetchOne();
			long currentDate = new java.util.Date().getTime();
			
			
			dslContext.update(RATTACH).set(RATTACH.CATEGORY,fi.getCategory())
									.set(RATTACH.MIMETYPE,fi.getMimetype())
									.set(RATTACH.DESCRIPTION,fi.getTitle())
									.set(RATTACH.SCOPE,fi.getScopeId())
									.set(RATTACH.SECURITY_LEVEL,fi.getSecurityLevel())
									.set(RATTACH.ATTACH_DATE,fi.getDateSql())
									.set(RATTACH.MODIFICATION_USER, user.value1())
									.set(RATTACH.MODIFICATION_DATE, new Timestamp(currentDate))
							.where(RATTACH.ID.eq(fi.getFileId())).execute();
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	public static com.code.aon.google.apis.FileInfo getDataAndName(Integer id, String domain) throws SQLException{
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			Result<Record2<byte[], String>> data = dslContext.select(RATTACH.DATA,RATTACH.DESCRIPTION)
					.from(RATTACH).where(RATTACH.ID.eq(id)).fetch();
			com.code.aon.google.apis.FileInfo fi = new com.code.aon.google.apis.FileInfo();
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
	
	public static Integer newTag(String domain, Integer domainId,String name) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
		
			return dslContext.insertInto(TAG,TAG.DOMAIN,TAG.NAME,TAG.TYPE)
				.values(domainId,name,(byte)0).returning(TAG.ID).fetchOne().getId();
			
		} finally {
			if (connection != null)
				connection.close();
		}	
	}

	public static void editTag(String domain, String name,Integer id) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(TAG).set(TAG.NAME,name)
							.where(TAG.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}	
	}

	public static void deleteTag(String domain, Integer tagId) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.delete(RATTACH_TAG).where(RATTACH_TAG.TAG.eq(tagId)).execute();
			dslContext.delete(TAG).where(TAG.ID.eq(tagId)).execute();
			
		} finally {
			if (connection != null)
				connection.close();
		}	
	}

	public static Integer newCategory(String domain, Integer domainId, String name) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
		
			return dslContext.insertInto(CATEGORY,CATEGORY.DOMAIN,CATEGORY.NAME,CATEGORY.TYPE)
				.values(domainId,name,(byte)0).returning(CATEGORY.ID).fetchOne().getId();
			
		} finally {
			if (connection != null)
				connection.close();
		}		
	}

	public static void editCategory(String domain, String name,Integer id) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());
			
			dslContext.update(CATEGORY).set(CATEGORY.NAME,name)
							.where(CATEGORY.ID.eq(id)).execute();
		} finally {
			if (connection != null)
				connection.close();
		}	
	}

	public static void deleteCategory(String domain, Integer categoryId) throws SQLException {
		Connection connection = null;
		try {
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Integer userId = AonUtil.getAuthPrincipal().getUserId();
			Record1<String> user = dslContext.select(USER.LOGIN).from(USER).where(USER.ID.eq(userId)).fetchOne();
			long currentDate = new java.util.Date().getTime();
			
			String sql = "UPDATE rattach SET category = NULL WHERE category = "+categoryId+";";
			dslContext.fetch(sql);
			
			dslContext.update(RATTACH)
					.set(RATTACH.MODIFICATION_USER, user.value1())
					.set(RATTACH.MODIFICATION_DATE, new Timestamp(currentDate))
					.where(RATTACH.CATEGORY.eq(categoryId))
					.execute();
			
			dslContext.delete(CATEGORY).where(CATEGORY.ID.eq(categoryId)).execute();
			
		} finally {
			if (connection != null)
				connection.close();
		}	
	}
	
	public static com.code.aon.registry.RegistryAttachment getRegistryAttachment(int id, String domain)
			throws AonConnectionException, SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Record16<Date, Integer, Timestamp, String, String, Integer, String, String, Integer, Byte, Timestamp, String, Integer, Integer, Byte, Byte> rattach = dslContext.select(RATTACH.ATTACH_DATE, RATTACH.CATEGORY,
															RATTACH.CREATION_DATE, RATTACH.CREATION_USER, RATTACH.DESCRIPTION, 
															RATTACH.DOMAIN, RATTACH.DPARENT_ID, RATTACH.DRIVE_ID, RATTACH.ID, 
															RATTACH.MIMETYPE, RATTACH.MODIFICATION_DATE, RATTACH.MODIFICATION_USER, 
															RATTACH.REGISTRY, RATTACH.SCOPE,RATTACH.SECURITY_LEVEL, RATTACH.TYPE)
					.from(RATTACH).where(RATTACH.ID.eq(id)).fetchOne();
			com.code.aon.registry.RegistryAttachment attach = new com.code.aon.registry.RegistryAttachment();
			
			attach.setAttachDate(rattach.value1());
			com.code.aon.registry.Category c = new com.code.aon.registry.Category();
			c.setId(rattach.value2());
			attach.setCategory(c);
			attach.setCreationDate(rattach.value3());
			attach.setCreationUser(rattach.value4());
			attach.setDescription(rattach.value5());
			attach.setDomain(rattach.value6());
			attach.setDparentId(rattach.value7());
			attach.setDriveId(rattach.value8());
			attach.setId(rattach.value9());
			attach.setMimeType(MimeType.values()[rattach.value10()]);
			attach.setModificationDate(rattach.value11());
			attach.setModificationUser(rattach.value12());
			com.code.aon.registry.Registry r = new Registry();
			r.setId(rattach.value13());
			attach.setRegistry(r);
			com.code.aon.config.Scope s = new com.code.aon.config.Scope();
			s.setId(rattach.value14());
			attach.setScope(s);
			attach.setSecurityLevel(SecurityLevel.values()[rattach.value15()]);
			attach.setRegistryAttachmentType(RegistryAttachmentType.values()[rattach.value16()]);
			
			return attach;
		} finally {
			if (connection != null)
				connection.close();
		}

	}
	public static ContactList getContacts(int user_id, String domain,Integer userDomainId, Integer domainId)
			throws AonConnectionException, SQLException {
		Connection connection = null;
		try {

			connection = DatabaseSync.getConnection(domain);

			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record3<Integer, String, String>> result = dslContext.select(CONTACT.ID, CONTACT.DISPLAYNAME, CONTACT_DATA.EMAIL)
														.from(CONTACT).join(CONTACT_DATA).on(CONTACT.CONTACT_DATA.eq(CONTACT_DATA.ID))
														.where(CONTACT.USER_ID.eq(user_id).and(CONTACT.DOMAIN.eq(userDomainId)
																								.or(CONTACT.DOMAIN.eq(domainId)))).fetch();
			
			Vector<Contact> v = new Vector<Contact>();
			result.stream().forEach(r->{
				Contact c = new Contact();
				c.setId(r.value1());
				c.setDisplayName(r.value2());
				c.setEmail(r.value3());
				c.setUser_id(user_id);
				v.add(c);
			});
			ContactList cl = new ContactList();
			cl.setList(v);
			return cl;
			
		
		} finally {
			if (connection != null)
				connection.close();
		}

	}
	
}
