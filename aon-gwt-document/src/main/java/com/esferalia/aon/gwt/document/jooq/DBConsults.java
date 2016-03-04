package com.esferalia.aon.gwt.document.jooq;

import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Record18;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record7;
import org.jooq.Result;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.document.shared.Category;
import com.esferalia.aon.gwt.document.shared.CategoryList;
import com.esferalia.aon.gwt.document.shared.Contact;
import com.esferalia.aon.gwt.document.shared.ContactList;
import com.esferalia.aon.gwt.document.shared.Document;
import com.esferalia.aon.gwt.document.shared.FileInfo;
import com.esferalia.aon.gwt.document.shared.Scope;
import com.esferalia.aon.gwt.document.shared.ScopeList;
import com.esferalia.aon.gwt.document.shared.Tag;
import com.esferalia.aon.gwt.document.shared.TagList;
import com.esferalia.aon.gwt.document.shared.Tags;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DBConsults {
	
	private static boolean esta(Domain domain, FileInfo fi,Integer userId,String userLogin){
		Integer[] userScopeArray = AON.getUserScopes(domain.getName(), domain.getId(), userLogin, userId);
		if(userScopeArray == null) return true;
		for (Integer scope: userScopeArray) {
			if(fi.getScope()!=null && fi.getScope().getId().equals(scope)) 
				return true;
		}
		return false;
	}
	
	public static Document getAllRattachNew(Domain domain, User user, String serverName, Boolean confidential){
		Vector<FileInfo>
		filesGwt = new Vector<FileInfo>();
		Vector<FileInfo> 
		vaux = new Vector<FileInfo>();
		if(user.getDomain().equals(domain.getParentId())){
			// Todos los archivos del documental del dominio actual
			LinkedList<FileInfo> s = AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(),
					f -> f.getDomainProperty().eq(domain.getId())
					.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value()))
					, AttachType.REGISTRY).map(new AttachToFileInfo(user, domain.getId()))
					.filter(fi -> !fi.getConfidential() || (confidential && fi.getConfidential())) 
					.collect(Collectors.toCollection(LinkedList::new));
		
			filesGwt.addAll(s);
			vaux.addAll(s);
			
		} else if(user.getDomain().equals(domain.getId())){
			Integer[] userScopeArray = AON.getUserScopes(domain.getName(), domain.getId(), user.getLogin(), user.getId());
			//Todos los archivos del dominio actual con los ambitos del user (incluidos los arcivos con scope nulo).
			LinkedList<FileInfo> s = AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(),
					f -> f.getDomainProperty().eq(domain.getId())
					.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value()))
					.and(f.getScopeProperty().in(userScopeArray).or(f.getScopeProperty().isNull()))
					, AttachType.REGISTRY).map(new AttachToFileInfo(user, domain.getId()))
					.filter(fi -> !fi.getConfidential() || (confidential && fi.getConfidential()))
					.collect(Collectors.toCollection(LinkedList::new));
			filesGwt.addAll(s);
			vaux.addAll(s);
		}
		System.out.println(serverName);
		System.out.println(domain.getName());
		if(domain.getParentId() != null && domain.isEnableHeredity() && domain.getName().equals(serverName)){
			Integer[] userScopeArray = AON.getUserScopes(domain.getName(), domain.getId(), user.getLogin(), user.getId());
			//Todos los archivos del dominio padre con los ambitos del user (incluidos los archivos con scope nulo).
			LinkedList<FileInfo> s = AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(),
								f -> f.getDomainProperty().eq(domain.getParentId())
								.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value()))
								.and(f.getScopeProperty().in(userScopeArray).or(f.getScopeProperty().isNull()))
								, AttachType.REGISTRY).map(new AttachToFileInfo(user, domain.getId()))
					.filter(fi -> !fi.getConfidential() || (confidential && fi.getConfidential())) 
					.collect(Collectors.toCollection(LinkedList::new));
			filesGwt.addAll(s);
			vaux.addAll(s);
		} else if(domain.getParentId() == null){
			Integer[] sonsDomainArray = AON.getSonsDomains(domain.getName(), domain.getId(), user.getLogin());
			//Todos los archivos de los hijos.
			Stream<FileInfo> s = AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(), 
					f -> f.getDomainProperty().in(sonsDomainArray)
					.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value()))
					, AttachType.REGISTRY).map(new AttachToFileInfo(user, domain.getId()));
			filesGwt.addAll(s.filter(fi -> !fi.getConfidential() || (confidential && fi.getConfidential())) 
					.collect(Collectors.toCollection(LinkedList::new)));
		}
		return new Document().setFiles(filesGwt).setEfiles(vaux).setFilter(vaux);
	}
	
	public static Document getAllRattach(Domain domain,User user, String domain2, Boolean confidential){
		AONContext ctx = null;
		try {				
			Vector<FileInfo> filesGwt = new Vector<FileInfo>();
			Vector<FileInfo> vaux = new Vector<FileInfo>();
			
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());	
				Condition c;

				if(user.getDomain() != domain.getId()){

					c=(RATTACH.SCOPE.isNotNull().or(RATTACH.SCOPE.isNull()));
					
				}
				else
					c = RATTACH.SCOPE.isNull();
				
				Byte sh = RegistryAttachmentType.CORPORATE_IDENTITY.value();//5;
				//rattach domain + parent domain + scope not null
				Result<Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp>> username = 
						ctx.getDslContext()
						.selectDistinct(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.TYPE,
								RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID,RATTACH.CATEGORY,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.DPARENT_ID,DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,DOMAIN.PARENT,
								RATTACH.CREATION_USER, RATTACH.CREATION_DATE, RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
						.from(RATTACH)
						.join(DOMAIN)
						.on(RATTACH.DOMAIN.eq(DOMAIN.ID))
						.join(USER_SCOPE).on(USER_SCOPE.SCOPE.eq(RATTACH.SCOPE))
						.where(USER_SCOPE.USER_ID.eq(user.getId()).and(RATTACH.TYPE.eq(sh).and(
								RATTACH.DOMAIN.eq(domain.getId()).or(
								RATTACH.DOMAIN.eq(ctx.getDslContext().select(DOMAIN.PARENT)
														.from(DOMAIN)
														.where(DOMAIN.ID.eq(domain.getId()))
														.and(DOMAIN.ENABLEHEREDITY.eq((byte)1)))))))
						.fetch();
				
				//rattach scope null parent
				Result<Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp>> result1 = 
						ctx.getDslContext()
						.select(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.TYPE,
								RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID,RATTACH.CATEGORY,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.DPARENT_ID,DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,DOMAIN.PARENT,
								RATTACH.CREATION_USER, RATTACH.CREATION_DATE, RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
						.from(RATTACH)
						.join(DOMAIN)
						.on(RATTACH.DOMAIN.eq(DOMAIN.ID))
						.where(RATTACH.TYPE.eq(sh).and((RATTACH.SCOPE.isNull().and(RATTACH.DOMAIN.eq(ctx.getDslContext().select(DOMAIN.PARENT)
										.from(DOMAIN)
										.where(DOMAIN.ID.eq(domain.getId()))
										.and(DOMAIN.ENABLEHEREDITY.eq((byte)1)))))))
						.fetch();
				
				//rattach scope null 
				Result<Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp>> result = 
						ctx.getDslContext()
						.selectDistinct(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.TYPE,
								RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID,RATTACH.CATEGORY,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.DPARENT_ID,DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,DOMAIN.PARENT,
								RATTACH.CREATION_USER, RATTACH.CREATION_DATE, RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
						.from(RATTACH)
						.join(DOMAIN)
						.on(RATTACH.DOMAIN.eq(DOMAIN.ID))
						.where(RATTACH.TYPE.eq(sh).and((c.and(RATTACH.DOMAIN.eq(domain.getId())))))
						.fetch();

				// sons domain
				Result<Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp>> result2 = 
						ctx.getDslContext()
						.selectDistinct(RATTACH.ID, RATTACH.DESCRIPTION,
								RATTACH.MIMETYPE, RATTACH.TYPE,
								RATTACH.ATTACH_DATE, RATTACH.DRIVE_ID,RATTACH.CATEGORY,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.DPARENT_ID,DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,DOMAIN.PARENT,
								RATTACH.CREATION_USER, RATTACH.CREATION_DATE, RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
						.from(RATTACH)
						.join(DOMAIN)
						.on(RATTACH.DOMAIN.eq(DOMAIN.ID))
						.where(RATTACH.TYPE.eq(sh).and(DOMAIN.PARENT.eq(ctx.getDslContext().select(DOMAIN.ID)
										.from(DOMAIN)
										.where(DOMAIN.ID.eq(domain.getId())))))
						.fetch();
				
				for (Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp> record : username) {
					FileInfo fi = newFileInfo(ctx, domain, user,domain2,record);
					
					if (!fi.getConfidential() || (confidential && fi.getConfidential())){
						filesGwt.add(fi);
						if(fi.getDomain().equalsIgnoreCase(domain2) || fi.getIsParent())
							if(domain.getName().equals(domain2))
								vaux.add(fi);
							else if(!fi.getIsParent()) vaux.add(fi);
					}
				}
				for (Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp> record : result) {
					FileInfo fi = newFileInfo(ctx, domain, user, domain2,record);
					if (!esta(domain, fi,user.getId(),user.getLogin())&&(!fi.getConfidential() || (confidential && fi.getConfidential()))){
						filesGwt.add(fi);
						if(fi.getDomain().equalsIgnoreCase(domain2) || fi.getIsParent())
							if(domain.getName().equals(domain2))
								vaux.add(fi);
							else if(!fi.getIsParent()) vaux.add(fi);
					}
				}
				for (Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp> record : result1) {
					FileInfo fi = newFileInfo(ctx, domain, user, domain2,record);
					if (!fi.getConfidential() || (confidential && fi.getConfidential())){
						filesGwt.add(fi);
						if(fi.getDomain().equalsIgnoreCase(domain2) || fi.getIsParent())
							if(domain.getName().equals(domain2))
								vaux.add(fi);
							else if(!fi.getIsParent()) 
								vaux.add(fi);
					}
				}
				for (Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp> record : result2) {
					FileInfo fi = newFileInfo(ctx, domain, user, domain2, record);
					
					if (!fi.getConfidential() || (confidential && fi.getConfidential())){
						filesGwt.add(fi);
						if(fi.getDomain().equalsIgnoreCase(domain2) || fi.getIsParent())
							if(domain.getName().equals(domain2))
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
			if (ctx != null)
				ctx.close();
		}
	}
	
	private static class AttachToFileInfo implements Function<Attach, FileInfo> {
		User user;Integer actualDomain;
		public AttachToFileInfo(User user,Integer actualDomain) {
			this.user = user;
			this.actualDomain = actualDomain;
		}
		
		@Override
		public FileInfo apply(Attach a){
			FileInfo fi = new FileInfo();
			fi.setAonType("registry");
			if (a.getId() != null) {
				fi.setFileId(a.getId());
			}
			if (a.getDescription() != null) {
				fi.setTitle(a.getDescription());
			}
			if (a.getMimeType() != null) {
				fi.setMimetype(a.getMimeType().value());
			}
			
			fi.setType(a.getType());
			
			if (a.getDate() != null) {
				fi.setDate(a.getDate());
				String dateStr = fi.getDate().toString();
				Integer pos = dateStr.indexOf("-");
				Integer pos2 = dateStr.substring(pos+1).indexOf("-");
				String aux = dateStr.substring(pos2+pos+2)+"-"+dateStr.substring(pos+1, pos2+pos+1)+"-"+dateStr.substring(0,pos);
				fi.setDateStr(aux);
			}
			else fi.setDateStr("-");
			if (a.getDriveId() != null) {
				fi.setDriveId(a.getDriveId());
			}
			if (a.getCategory() != null) {
				fi.setCategory(a.getCategory());
			}
			Tags tags = getTags(a.getDomain(), user, fi.getFileId());
		
			if(a.getCategory() != null) {
				com.esferalia.aon.occam.api.model.registry.Category category = AON.getCategory(a.getDomain().getName(), a.getDomain().getId(), user.getLogin(), a.getCategory());
				fi.setCategoryStr(category.getName());
			}			
			else fi.setCategoryStr("-");
			
			fi.setTags(tags.getTags().getList());
			if(tags.getTagsStr()!=null)fi.setTagsStr(tags.getTagsStr()); else fi.setTagsStr("-");
			if(a.getScope() !=null) {
				Scope s = getScope(a.getDomain(), user, a.getScope());
				fi.setScope(s);
			}
			if( a.getConfidential() != null){
				fi.setConfidential(a.getConfidential());
			}
			
			if (a.getDparentId() != null){
				fi.setSize(Integer.valueOf(a.getDparentId()));
			}
			else fi.setSize(0);
			fi.setSizeStr(FileUtils.byteCountToDisplaySize(fi.getSize()!=null?fi.getSize():0));
			
			
			fi.setIcon(getmType(fi));
			if(a.getDomain() != null && a.getDomain().getId() != null)
				fi.setDomainId(a.getDomain().getId());
			if(a.getDomain() != null && a.getDomain().getName() != null)
				fi.setDomain(a.getDomain().getName());
			if(a.getDomain() !=null && a.getDomain().getDescription() != null)
				fi.setDomainDescription(a.getDomain().getDescription());
			if(a.getDomain() != null){
				if(!a.getDomain().getId().equals(actualDomain))
					fi.setIsParent(a.getDomain().getParentId() == null);	
			}
			if(a.getCreationUser() != null)
				fi.setCreationUser(a.getCreationUser());
			if(a.getCreationDate() != null){
				fi.setCreationDateStr(AonDateUtils.getDay(a.getCreationDate())
						+ "-" + (AonDateUtils.getMonth(a.getCreationDate())+1)
						+ "-" + AonDateUtils.getYear(a.getCreationDate()));
			}
			if(a.getModificationUser() != null)
				fi.setModificationUser(a.getModificationUser());
			if(a.getModificationDate() != null){
				fi.setModificationDateStr(AonDateUtils.getDay(a.getModificationDate())
						+ "-" + (AonDateUtils.getMonth(a.getModificationDate())+1)
						+ "-" + AonDateUtils.getYear(a.getModificationDate()));
			}
			return fi;
		}
	}
	
	private static FileInfo newFileInfo(AONContext ctx, Domain domain, User user, String domain2, Record18<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, String, Integer, String, String, Integer, String, Timestamp, String, Timestamp> record){
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
		Tags tags = getTags(domain, user, fi.getFileId());
	
		if(record.value7() != null) {
			Result<Record1<String>> a = ctx.getDslContext().select(CATEGORY.NAME).from(CATEGORY).where(CATEGORY.ID.eq((record.value7()))).fetch();
			a.stream().forEach(r-> {
				fi.setCategoryStr(r.value1());
			});
		}
		
		else fi.setCategoryStr("-");
		
		fi.setTags(tags.getTags().getList());
		if(tags.getTagsStr()!=null)fi.setTagsStr(tags.getTagsStr()); else fi.setTagsStr("-");
		if(record.value8()!=null) {
			Scope s = getScope(domain, user, record.value8());
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
			fi.setDomainId(record.getValue(DOMAIN.ID));
		if(record.value12()!=null)
			fi.setDomain(record.getValue(DOMAIN.NAME));
		if(record.value13()!=null)
			fi.setDomainDescription(record.getValue(DOMAIN.DESCRIPTION));
		if(record.value14()==null)
			if(!fi.getDomain().equals(domain2))
				fi.setIsParent(true);
		if(record.value15() != null)
			fi.setCreationUser(record.value15());
		if(record.value16() != null){
			long a = record.value16().getTime();
			Calendar cal = Calendar.getInstance();
			cal.setTime(new java.util.Date(a));
			String dateStr = cal.get(Calendar.DATE)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.YEAR);
			fi.setCreationDateStr(dateStr);
		}
		if(record.value17() != null)
			fi.setModificationUser(record.value17());
		if(record.value18() != null){
			long a = record.value18().getTime();
			Calendar cal = Calendar.getInstance();
			cal.setTime(new java.util.Date(a));
			String dateStr = cal.get(Calendar.DATE)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.YEAR);
			fi.setModificationDateStr(dateStr);
		}
		return fi;
	}

	public static Scope getScope(Domain domain, User user, Integer scopeId){
		com.esferalia.aon.occam.api.model.security.Scope scope = AON.getScope(domain.getName(), domain.getId(), user.getLogin(), scopeId);
		return new Scope().setId(scope.getId())
				.setDomain(domain.getName())
				.setName(scope.getDescription());
	}
	
	public static String getmType(FileInfo fi2) {
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

	private static Tags getTags(Domain domain, User user,int fileId) {		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Result<Record3<String, Integer, Integer>> data= ctx.getDslContext()
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
					tag.setDomain(getDomainName(domain.setId(record.getValue(TAG.DOMAIN)), user));
				}
				ts.add(tag);
			}
			TagList tl = new TagList();
			tl.setList(ts);
			tags.setTags(tl);
			tags.setTagsStr(string);
			
			return tags;
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static ScopeList getScopeList(Domain domain, User user){
		AONContext ctx = null;
		try {
			ScopeList sl = new ScopeList();
			
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
					
			Result<Record2<String, Integer>> scope;
			if(user.getDomain().equals(domain.getId())){
				scope = ctx.getDslContext().select(SCOPE.DESCRIPTION, SCOPE.ID)
					.from(SCOPE).join(USER_SCOPE)
					.on(USER_SCOPE.SCOPE.eq(SCOPE.ID))
					.where(SCOPE.DOMAIN.eq(domain.getId()).and(USER_SCOPE.USER_ID.eq(user.getId()))).fetch();
			}
			else{
				scope = ctx.getDslContext().select(SCOPE.DESCRIPTION, SCOPE.ID)
						.from(SCOPE)
						.where(SCOPE.DOMAIN.eq(domain.getId())).fetch();
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
			
			Result<Record2<String, Integer>> scopeParent = ctx.getDslContext().select(SCOPE.DESCRIPTION, SCOPE.ID)
					.from(SCOPE).join(DOMAIN)
					.on(SCOPE.DOMAIN.eq(DOMAIN.PARENT)).join(USER_SCOPE)
					.on(USER_SCOPE.SCOPE.eq(SCOPE.ID))
					.where(DOMAIN.ID.eq(domain.getId()).and(USER_SCOPE.USER_ID.eq(user.getId()))).fetch();
			
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
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static ScopeList getScopeListSon(Domain domain, User user){
		AONContext ctx = null;
		try {
			ScopeList sl = new ScopeList();
			
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record3<String, Integer, String>> scopeSon = ctx.getDslContext().select(SCOPE.DESCRIPTION,SCOPE.ID,DOMAIN.NAME)
				.from(SCOPE).join(DOMAIN)
				.on(SCOPE.DOMAIN.eq(DOMAIN.ID))
				.where(DOMAIN.PARENT.eq(domain.getId())).orderBy(SCOPE.DESCRIPTION).fetch();
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
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static TagList getTagList(Domain domain, User user){
		AONContext ctx = null;
		try {
			TagList tl = new TagList();
			
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record2<String, Integer>> tag = ctx.getDslContext().select(TAG.NAME, TAG.ID)
					.from(TAG).join(DOMAIN)
					.on(TAG.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.NAME.eq(domain.getName()).and(TAG.TYPE.eq((byte)0))).orderBy(TAG.NAME).fetch();
			
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
			
			Result<Record2<String, Integer>> tagParent = ctx.getDslContext().select(TAG.NAME, TAG.ID)
					.from(TAG).join(DOMAIN)
					.on(TAG.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.NAME.eq(domain.getName()).and(TAG.TYPE.eq((byte)0))).orderBy(TAG.NAME).fetch();
			
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
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static TagList getTagListSon(Domain domain, User user) {
		AONContext ctx = null;
		try {
			TagList tl = new TagList();
			
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record3<String, Integer, String>> tagSon = ctx.getDslContext().select(TAG.NAME,TAG.ID,DOMAIN.NAME)
				.from(TAG).join(DOMAIN)
				.on(TAG.DOMAIN.eq(DOMAIN.ID))
				.where(DOMAIN.PARENT.eq(ctx.getDslContext().select(DOMAIN.ID)
						.from(DOMAIN)
						.where(DOMAIN.NAME.eq(domain.getName()))).and(TAG.TYPE.eq((byte)0))).orderBy(TAG.NAME).fetch();
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
		if (ctx != null)
			ctx.close();
	}
	}
	
	public static CategoryList getCategoryList(Domain domain, User user){
		AONContext ctx = null;
		try {
			CategoryList cl = new CategoryList();
			
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record2<String, Integer>> category = ctx.getDslContext().select(CATEGORY.NAME,CATEGORY.ID)
					.from(CATEGORY).join(DOMAIN)
					.on(CATEGORY.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.NAME.eq(domain.getName()).and(CATEGORY.TYPE.eq((byte)0))).orderBy(CATEGORY.NAME).fetch();
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
						
			Result<Record2<String, Integer>> categoryParent = ctx.getDslContext().select(CATEGORY.NAME,CATEGORY.ID)
					.from(CATEGORY).join(DOMAIN)
					.on(CATEGORY.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.NAME.eq(domain.getName()).and(CATEGORY.TYPE.eq((byte)0))).orderBy(CATEGORY.NAME).fetch();
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
			if (ctx != null)
				ctx.close();
		}
	}

	public static CategoryList getCategoryListSon(Domain domain, User user) {
		AONContext ctx = null;
		try {
			CategoryList cl = new CategoryList();
			
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record3<String, Integer, String>> categorySon = ctx.getDslContext().select(CATEGORY.NAME,CATEGORY.ID,DOMAIN.NAME)
				.from(CATEGORY).join(DOMAIN)
				.on(CATEGORY.DOMAIN.eq(DOMAIN.ID))
				.where(DOMAIN.PARENT.eq(ctx.getDslContext().select(DOMAIN.ID)
						.from(DOMAIN)
						.where(DOMAIN.NAME.eq(domain.getName()))).and(CATEGORY.TYPE.eq((byte)0))).orderBy(CATEGORY.NAME).fetch();
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
			if (ctx != null)
			ctx.close();
		}
	}
	
	public static Vector<FileInfo> getServiConvenios(Domain domain, User user){
		AONContext ctx = null;
		try {
			Vector<FileInfo> vector = new Vector<FileInfo>();

			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
				
			Result<Record7<Integer, String, Byte, Byte, Date, String, Integer>> username = 
					ctx.getDslContext()
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
				Tags tags = getTags(domain, user, fi.getFileId());
				fi.setSize(0);
				fi.setSizeStr(FileUtils.byteCountToDisplaySize(fi.getSize()!=null?fi.getSize():0));
				if(record.value7() != null) fi.setCategoryStr(ctx.getDslContext().select(CATEGORY.NAME).from(CATEGORY).where(CATEGORY.ID.eq((record.value7()))).fetch().get(0).value1());
				else fi.setCategoryStr("-");
				
				fi.setIcon(getmType(fi));
				
				fi.setTags(tags.getTags().getList());
				if(tags.getTagsStr()!=null)fi.setTagsStr(tags.getTagsStr()); else fi.setTagsStr("-");
				
				fi.setConfidential(false);
				fi.setDomainId(0);
				vector.add(fi);
			}
		
			return vector;
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<Domain> getSons(Domain domain, User user) {
		return AON.getDomainList(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getParentProperty().eq(domain.getId()));
	}

	public static void removeFile(Domain domain, User user, Integer attachId){
		AON.deleteRegistryAttachTag(domain.getName(), domain.getId(), user.getLogin(),
				attachId);
		AON.delete(domain.getName(), domain.getId(), user.getLogin(),
				filter -> filter.getIdProperty().eq(attachId)
				,AttachType.REGISTRY);
	}
	
	public static Domain getDomain(Domain domain, User user){
		return AON.getDomain(domain.getName(), domain.getId(), user.getLogin());
	}
	
	public static Integer getDomainId(Domain domain, User user, String dom){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record1<Integer>> data = ctx.getDslContext()
					.select(DOMAIN.ID).from(DOMAIN).where(DOMAIN.NAME.eq(dom)).fetch();
			
			return data.get(0).value1();
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
		
	}
	
	public static String getDomainName(Domain domain, User user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin()); 
			Record1<String> data = ctx.getDslContext()
					.select(DOMAIN.NAME).from(DOMAIN).where(DOMAIN.ID.eq(domain.getId())).fetchOne();
			return data.getValue(DOMAIN.NAME);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Integer getDomainParent(Domain domain, User user){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());

			Record1<Integer> data = ctx.getDslContext()
					.select(DOMAIN.PARENT).from(DOMAIN).where(DOMAIN.ID.eq(domain.getId())).fetchOne();
			if(data.value1()!= null)
				return data.value1();
			else return null;
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
		
	}
	
	public static Integer insertFile(Domain domain,User user, com.code.aon.google.apis.FileInfo fi){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record1<Integer>> reg = ctx.getDslContext().select(ENTERPRISE.REGISTRY)
				.from(ENTERPRISE.join(DOMAIN).on(ENTERPRISE.DOMAIN.eq(DOMAIN.ID)))
				.where(DOMAIN.NAME.eq(domain.getName())).fetch();
			
			long currentDate = new java.util.Date().getTime();
			
			return ctx.getDslContext().insertInto(RATTACH,RATTACH.REGISTRY,RATTACH.DOMAIN,RATTACH.CATEGORY,RATTACH.MIMETYPE,RATTACH.DESCRIPTION,RATTACH.TYPE,RATTACH.SCOPE,RATTACH.SECURITY_LEVEL,RATTACH.ATTACH_DATE,RATTACH.DATA,RATTACH.DRIVE_ID,RATTACH.DPARENT_ID, RATTACH.CREATION_USER, RATTACH.CREATION_DATE, RATTACH.MODIFICATION_USER, RATTACH.MODIFICATION_DATE)
						.values(reg.get(0).value1(),fi.getDomainId(),fi.getCategory(),fi.getMimetype(),fi.getTitle(),(byte)fi.getType(),fi.getScopeId(),fi.getSecurityLevel(),fi.getDateSql(),null,null,fi.getSize().toString()
								,user.getLogin(),new Timestamp(currentDate),user.getLogin(),new Timestamp(currentDate)).returning(RATTACH.ID).fetchOne().getId();
	
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static FileInfo getFile(Domain domain, User user,Integer id){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			final AONContext sctx = ctx;
			Result<Record15<Integer, String, Byte, Byte, Date, String, Integer, Integer, Byte, Integer, String, String, Timestamp, String, Timestamp>> record = 
					ctx.getDslContext()
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
					tags = getTags(domain,user, fi.getFileId());
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
				if(r.value7() != null) fi.setCategoryStr(sctx.getDslContext().select(CATEGORY.NAME).from(CATEGORY).where(CATEGORY.ID.eq((r.value7()))).fetch().get(0).value1());
				else fi.setCategoryStr("-");
				fi.setTags(tags.getTags().getList());
				if(tags.getTagsStr()!=null)fi.setTagsStr(tags.getTagsStr()); else fi.setTagsStr("-");
				if(r.value8()!=null) {
					Scope s = null;
					try {
						s = getScope(domain, user, record.get(0).value8());
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
					String dateStr = cal.get(Calendar.DATE)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.YEAR);
					fi.setCreationDateStr(dateStr);
				}
				if(r.value14() != null) fi.setModificationUser(r.value14());
				if(r.value15() != null){
					long a = r.value15().getTime();
					Calendar cal = Calendar.getInstance();
					cal.setTime(new java.util.Date(a));
					String dateStr = cal.get(Calendar.DATE)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.YEAR);
					fi.setModificationDateStr(dateStr);
				}
			});
			return fi;
		}finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void insertFileData(Domain domain, User user, Integer id, byte[] a){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
		
			long currentDate = new java.util.Date().getTime();
			
			ctx.getDslContext().update(RATTACH).set(RATTACH.DATA,a)
						.set(RATTACH.MODIFICATION_USER, user.getLogin())
						.set(RATTACH.MODIFICATION_DATE, new Timestamp(currentDate))
						.where(RATTACH.ID.eq(id))
						.execute();
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static void insertTagsFile(Domain domain, User user, Integer attachId, Vector<Tag> tags) {
		for(Tag tag : tags){
			AON.insertRegistryAttachTag(domain.getName(), domain.getId(), user.getLogin(), attachId, tag.getId());	
		}
	}
	
	public static void updateFile(Domain domain,User user, com.code.aon.google.apis.FileInfo fi,Vector<Tag> tags) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			AON.deleteRegistryAttachTag(domain.getName(), domain.getId(), user.getLogin(), fi.getFileId());
			for (Tag tag : tags) {
				AON.insertRegistryAttachTag(domain.getName(), domain.getId(), user.getLogin(), fi.getFileId(), tag.getId());
			}
			
			long currentDate = new java.util.Date().getTime();
			
			
			ctx.getDslContext().update(RATTACH).set(RATTACH.CATEGORY,fi.getCategory())
									.set(RATTACH.MIMETYPE,fi.getMimetype())
									.set(RATTACH.DESCRIPTION,fi.getTitle())
									.set(RATTACH.SCOPE,fi.getScopeId())
									.set(RATTACH.SECURITY_LEVEL,fi.getSecurityLevel())
									.set(RATTACH.ATTACH_DATE,fi.getDateSql())
									.set(RATTACH.MODIFICATION_USER, user.getLogin())
									.set(RATTACH.MODIFICATION_DATE, new Timestamp(currentDate))
							.where(RATTACH.ID.eq(fi.getFileId())).execute();
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static com.code.aon.google.apis.FileInfo getDataAndName(Domain domain, User user, Integer rattachId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			Result<Record2<byte[], String>> data = ctx.getDslContext().select(RATTACH.DATA,RATTACH.DESCRIPTION)
					.from(RATTACH).where(RATTACH.ID.eq(rattachId)).fetch();
			com.code.aon.google.apis.FileInfo fi = new com.code.aon.google.apis.FileInfo();
			for (Record2<byte[], String> record : data) {
				//InputStream is =  new ByteArrayInputStream(record.value1());
				if(record.value1()!= null) fi.setData(record.value1());
				if(record.value2()!= null) fi.setTitle(record.value2());
			}
			return fi;
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
			
	}
	
	public static Integer newTag(Domain domain, User user,String name) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
		
			return ctx.getDslContext().insertInto(TAG,TAG.DOMAIN,TAG.NAME,TAG.TYPE)
				.values(domain.getId(),name,(byte)0).returning(TAG.ID).fetchOne().getId();
			
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}

	public static void editTag(Domain domain, User user, String name,Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			ctx.getDslContext().update(TAG).set(TAG.NAME,name)
							.where(TAG.ID.eq(id)).execute();
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}

	public static void deleteTag(Domain domain, User user, Integer tagId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			ctx.getDslContext().delete(RATTACH_TAG).where(RATTACH_TAG.TAG.eq(tagId)).execute();
			ctx.getDslContext().delete(TAG).where(TAG.ID.eq(tagId)).execute();
			
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}

	public static Integer newCategory(Domain domain, User user, String name) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			return ctx.getDslContext().insertInto(CATEGORY,CATEGORY.DOMAIN,CATEGORY.NAME,CATEGORY.TYPE)
				.values(domain.getId(),name,(byte)0).returning(CATEGORY.ID).fetchOne().getId();
			
		} finally {
			if (ctx != null)
				ctx.close();
		}		
	}

	public static void editCategory(Domain domain, User user, String name,Integer id){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			ctx.getDslContext().update(CATEGORY).set(CATEGORY.NAME,name)
							.where(CATEGORY.ID.eq(id)).execute();
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}

	public static void deleteCategory(Domain domain, User user, Integer categoryId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			long currentDate = new java.util.Date().getTime();
			Integer nullInteger = null;
			ctx.getDslContext().update(RATTACH)
					.set(RATTACH.CATEGORY, nullInteger)
					.set(RATTACH.MODIFICATION_USER, user.getLogin())
					.set(RATTACH.MODIFICATION_DATE, new Timestamp(currentDate))
					.where(RATTACH.CATEGORY.eq(categoryId))
					.execute();
			
			ctx.getDslContext().delete(CATEGORY).where(CATEGORY.ID.eq(categoryId)).execute();
			
		} finally {
			if (ctx != null)
				ctx.close();
		}	
	}
	
	
	public static ContactList getContacts(Domain domain, User user){
		LinkedList<com.esferalia.aon.occam.api.model.Contact> list = AON.getContactList(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getUserIdProperty().eq(user.getId())
				.and(f.getDomainProperty().eq(user.getDomain()))
				.or(f.getDomainProperty().eq(domain.getId())));
		Vector<Contact> vector = new Vector<Contact>();
		for (com.esferalia.aon.occam.api.model.Contact contact : list) {
			Contact c = new Contact();
			c.setDisplayName(contact.getDisplayName());
			c.setEmail(AON.getContactEmail(domain.getName(), domain.getId(), user.getLogin(), contact.getContactData()));
			c.setUser_id(contact.getUserId());
			c.setId(contact.getId());
			vector.add(c);
		}
		ContactList cl = new ContactList();
		cl.setList(vector);
		return cl;
	}
}
