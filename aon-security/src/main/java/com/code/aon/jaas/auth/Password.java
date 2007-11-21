/**
 * 
 */
package com.code.aon.jaas.auth;

import com.code.aon.jaas.auth.util.Util;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 22/12/2006
 *
 */
public class Password {

	public static void main(String[] args) {
		String hashAlgorithm = "SHA-256";
		String hashEncoding = "base64";
//		passwd = Util.createPasswordHash( "SHA-256", "base64", "", "nare", "nare123");
//		passwd = Util.createPasswordHash( "SHA-256", "base64", "", "admin", "md115278");
//		passwd = Util.createPasswordHash( "SHA-256", "base64", "", "invitado", "demo");
//		passwd = Util.createPasswordHash( "SHA-256", "base64", "", "ia", "ia88");
//		passwd = Util.createPasswordHash( "SHA-256", "base64", "", "MLOPEZ", "ML1");
//		System.out.println( "Invitado, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "invitado", "demo") );
//		System.out.println( "Invitado, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "x", "xxxxxxxx1") );
//	SECURITY
////	DEMO
//		System.out.println( "Admin, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "admin", "demo2007") );
//		System.out.println( "Admin, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "admin", "security2007") );
//		System.out.println( "emarcos, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "emarcos", "em210469") );
////	DEMO
//		System.out.println( "furrutxi, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "furrutxi", "txi2005") );
//	AON-ACADEMY
//	DEMO
//		System.out.println( "fernando, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "fernando", "Urrutxi") );
//		System.out.println( "eduardo, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "eduardo", "Cortaberria") );
//		System.out.println( "julio, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "julio", "Garcia") );
//		System.out.println( "marta, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "marta", "Arevalillo") );
////	DINI
//		System.out.println( "admin, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "admin", "min2156") );
//		System.out.println( "dini, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "dini", "ni2156") );
//		System.out.println( "lakua, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "lakua", "kua2156") );
//		System.out.println( "beato, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "beato", "to2156") );
//		System.out.println( "zabalgana, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "zabalgana", "na2156") );
//		System.out.println( "clemente, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "clemente", "nte2156") );
//		System.out.println( "silvia, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "silvia", "via2156") );
//		System.out.println( "mjesus, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "mjesus", "sus2156") );
//		System.out.println( "presen, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "presen", "sen2156") );
////	TEACHERS
//		System.out.println( "Admin, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "admin", "teacher2007") );
//		System.out.println( "Sonia, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "sonia", "TeSo0807a") );
//		System.out.println( "Mentxu, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "mentxu", "TeMe0807u") );
//		System.out.println( "Fran, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "fran", "TeFr0807n") );
//		System.out.println( "campus, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "campus", "pus2057") );
//		System.out.println( "contacto, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "contacto", "cto2057") );
//		System.out.println( "fguerand, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "fguerand", "and2057") );
//		System.out.println( "gaelle, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "gaelle", "lle2057") );
//		System.out.println( "heminio, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "heminio", "nio2057") );
//		System.out.println( "inge, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "inge", "nge2057") );
//		System.out.println( "jitka, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "jitka", "tka2057") );
//		System.out.println( "joanne, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "joanne", "nne2057") );
//		System.out.println( "laura, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "laura", "ura2057") );
//		System.out.println( "mathiew, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "mathiew", "iew2057") );
//		System.out.println( "michael, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "michael", "ael2057") );
//		System.out.println( "pia, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "pia", "pia2057") );
//		System.out.println( "rick, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "rick", "ick2057") );
//		System.out.println( "sandra, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "sandra", "dra2057") );
//		System.out.println( "sanprudencio, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "sanprudencio", "cio2057") );
//		System.out.println( "yuliya, Clave:"+ 
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "yuliya", "iya2057") );
//		System.out.println( "marc, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "marc", "arc2057") );
//		System.out.println( "eric, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "eric", "ric2057") );
//	GTA
////	MARIGORTA
//		System.out.println( "Andoni, Clave:"+
//				Util.createPasswordHash( hashAlgorithm, hashEncoding, "", "andoni", "a01006691") );
	}

}
