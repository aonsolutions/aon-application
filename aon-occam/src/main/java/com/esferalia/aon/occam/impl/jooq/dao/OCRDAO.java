package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Raddinfo.RADDINFO;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Raddinfo;

public class OCRDAO {
	
	private static final Logger LOGGER = Logger.getLogger(OCRDAO.class.getName());
	
	private static final int GLOBAL_DOMAIN = 0;
	private static final String GLOBAL_DOMAIN_NAME = "global.aonsolutions.net";
	private static final String OCR_REF_PATTERN = "OCR_REF_PATTERN";
	private static final String REGISTRY_ALIAS_MARK = "OCR AUTOML";
 
	public static String[] getReferencePatterns( String user, String document ) {
		try ( AONContext ctx =  AONContext.getAONContext(GLOBAL_DOMAIN_NAME, GLOBAL_DOMAIN, user) ) {
			return ctx.getDslContext()
				.select( RADDINFO.VALUE )
				.from(RADDINFO)
				.innerJoin(REGISTRY).on(REGISTRY.ID.eq(RADDINFO.REGISTRY))
				.where(REGISTRY.DOMAIN.eq(GLOBAL_DOMAIN))
				.and(REGISTRY.DOCUMENT.eq( document))
				.and(RADDINFO.ATTRIBUTE.eq( OCR_REF_PATTERN ))
				.orderBy(RADDINFO.VALUE_DATE.desc())
				.fetch()
				.stream()
				.map( rec -> rec.getValue(RADDINFO.VALUE))
				.toArray(String[]::new);
		} catch (Throwable t) {
			LOGGER.severe("Con not teach OCR ("+ t.getMessage() +")");
			return null;
		}
	}

	public static void teachReferenceCode(String user, String document, String reference) {
		teachReferenceCode(user, document, reference, new Date());
	}
	public static void teachReferenceCode(String user, String document, String reference, Date date) {
		try ( AONContext ctx =  AONContext.getAONContext(GLOBAL_DOMAIN_NAME, GLOBAL_DOMAIN, user) ) {
			Integer registry = ensureRegistry(ctx, document );
			LinkedList<Raddinfo> patterns = ctx.getDslContext()
				.select(RADDINFO.fields())
				.from(RADDINFO)
				.where(RADDINFO.DOMAIN.eq(GLOBAL_DOMAIN))
				.and(RADDINFO.REGISTRY.eq(registry))
				.and(RADDINFO.ATTRIBUTE.eq(OCR_REF_PATTERN))
				.orderBy(RADDINFO.REGISTRY,RADDINFO.VALUE_DATE.desc())
				.fetch()
				.stream()
				.map( rec -> new Raddinfo()
					.setId( rec.getValue(RADDINFO.ID) )
					.setDomain( rec.getValue(RADDINFO.DOMAIN) )
					.setAttribute( rec.getValue(RADDINFO.ATTRIBUTE) )
					.setValue( rec.getValue(RADDINFO.VALUE) )
					.setValueDate( rec.getValue(RADDINFO.VALUE_DATE) ))
				.collect(Collectors.toCollection(LinkedList::new));
			
			if ( patterns == null || patterns.size() == 0) {
				String pattern = generalize( reference );
				ctx.log().info("[OCR] No Pattern found! creating: ["+pattern+"]");
				ctx.getDslContext().insertInto(RADDINFO)
					.set(RADDINFO.DOMAIN, GLOBAL_DOMAIN)	
					.set(RADDINFO.REGISTRY, registry)
					.set(RADDINFO.ATTRIBUTE, OCR_REF_PATTERN)
					.set(RADDINFO.VALUE, pattern)
					.set(RADDINFO.VALUE_DATE, new java.sql.Date( date.getTime() ) )
					.execute();
			} else {
				boolean matches = false;
				for ( Raddinfo raddinfo : patterns ) {
					Pattern pat = Pattern.compile( raddinfo.getValue() , Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
					Matcher matcher = pat.matcher(reference);
					matches = matcher.find();
					ctx.log().info("[OCR] Pattern : ["+raddinfo.getValue()+"] against ["+reference+"] find? " + matches);
					if (matches) {
						if ( date.after(raddinfo.getValueDate())) {
							ctx.getDslContext().update(RADDINFO)
								.set(RADDINFO.VALUE_DATE, new java.sql.Date( date.getTime() ) )
								.where(RADDINFO.ID.eq(raddinfo.getId()))
								.execute();
						}
						break;
					}
				}
				if (!matches) {
					ctx.getDslContext().insertInto(RADDINFO)
						.set(RADDINFO.DOMAIN, GLOBAL_DOMAIN)	
						.set(RADDINFO.REGISTRY, registry)
						.set(RADDINFO.ATTRIBUTE, OCR_REF_PATTERN)
						.set(RADDINFO.VALUE, generalize( reference ))
						.set(RADDINFO.VALUE_DATE, new java.sql.Date( date.getTime() ) )
						.execute();
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static Integer ensureRegistry(AONContext ctx, String document) {
		Integer registry = ctx.getDslContext().select(REGISTRY.ID)
				.from(REGISTRY)
				.where(REGISTRY.DOMAIN.eq( GLOBAL_DOMAIN ))
				.and(REGISTRY.DOCUMENT.eq( document ))
				.fetch()
				.stream()
				.map( rec -> rec.getValue(REGISTRY.ID))
				.findFirst()
				.orElse(null);
		if (registry == null) {
			registry  = ctx.getDslContext().insertInto(REGISTRY)
					.set(REGISTRY.DOMAIN, GLOBAL_DOMAIN)	
					.set(REGISTRY.DOCUMENT, document)
					.set(REGISTRY.ALIAS, REGISTRY_ALIAS_MARK)
					.returning(REGISTRY.ID)
					.fetchOne()
					.getValue(REGISTRY.ID);
		}
		return registry;
	}
	
	private static final char SLASH = '\\';

	private static final String INPUT_START_RE = "\\b(?<ref>";
	private static final String INPUT_END_RE = ")\\b";
	
	private static final char DIGIT  = 'd';
	private static final String DIGIT_RE = String.valueOf( new char[]{SLASH,DIGIT});

	private static final char WORD = 'w';
	private static final String WORD_RE = String.valueOf( new char[]{SLASH,WORD});

	private static final char SPACE = 's';
	private static final String SPACE_RE = String.valueOf( new char[]{SLASH,SPACE});

	private static final char NON_WORD = 'W';
	private static final String NON_WORD_RE = String.valueOf( new char[]{SLASH,NON_WORD});

	private static final char ANY = '@';
	private static final String ANY_RE = ".";
	private static final String DOT_RE = "\\.";

//	private static String infer(String  pat, String ref) {
//		if (ref != null) {
//			ref = ref.trim().toUpperCase();
//			if (pat == null) {
//				return generalize(ref);
//			}
//			pat = pat.replace("\\", "");
//			int maxLength = Math.max(ref.length(),pat.length());
//			char[] ret = new char[maxLength];
//			Arrays.fill(ret, ' ');
//			System.out.println("\t infering: ["+pat+"]  and ["+ref+"]");
//			for (int i = 0; i < maxLength ; ++i) {
//				if (i >= pat.length()) {
//					ret[i] = generalize(ref.charAt(i));;;
//				} else if (i >= ref.length()) {
//					ret[i] = generalize(pat.charAt(i));;
//				} else {
//					ret[i] = join(pat.charAt(i) , ref.charAt(i));
//				}
//			}
//			pat = new String( ret );	
//		}
//		String toReturn = toRegex( pat );
//		System.out.println("\t infering: ["+pat+"]  and ["+ref+"] ---> [" + toReturn + "]");
//		return toReturn;
//	}
//	private static char join(char p , char r) {
//		r = generalize(r);
//		if (p==r) return p;
//		if ( p==DIGIT  && r==WORD) return WORD;
//		if ( p==WORD && r==DIGIT) return WORD;
//		return ANY;
//	}
	

	
	private static char generalize(char r) {
		if (Character.isDigit(r)) return  DIGIT;	
		else if (Character.isLetter(r)) return WORD;	
		else if (Character.isWhitespace(r)) return SPACE;
		else return r;
	}
	
	private static String generalize(String ref) {
		if (ref == null) return null;
		char[] ret = new char[ref.length()];
		for (int i = 0; i < ref.length() ; ++i) {
			ret[i] = generalize(ref.charAt(i));
		}
		return toRegex( new String( ret ));
	}
	
	private static String toRegex(String  pat) {
		StringBuffer buf = new StringBuffer(); 
		char prev = Character.MIN_VALUE;
		int count = 1;
		for (int i = 0; i < pat.length() ; i++) {
			char c = pat.charAt(i);
			if ( c != prev) {
				prev = c;
				if (count > 1) {
					buf.append('{');
					buf.append(count);
					buf.append('}');
					count = 1;
				}
				buf.append(c);
			} else {
				count++;
			}
		}
		if (count > 1) {
			buf.append('{');
			buf.append(count);
			buf.append('}');
		}
		String p = buf.toString().replace( String.valueOf(DIGIT), DIGIT_RE)
			.replace( String.valueOf(WORD), WORD_RE)
			.replace( String.valueOf(SPACE), SPACE_RE)
			.replace( String.valueOf(NON_WORD), NON_WORD_RE)
			.replace( String.valueOf('.'), DOT_RE)
			.replace( String.valueOf(ANY), ANY_RE)
			;
		return INPUT_START_RE + p + INPUT_END_RE;
	}
	
}

