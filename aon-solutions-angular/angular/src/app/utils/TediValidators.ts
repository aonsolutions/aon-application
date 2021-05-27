import { FormControl } from '@angular/forms';

export class TediValidators {

  // DOCUMENTS
  private static DNI_REGEX = /^(\d{8})([A-Z])$/;
  private static CIF_REGEX = /^([ABCDEFGHJKLMNPQRSUVW])(\d{7})([0-9A-J])$/;
  private static NIE_REGEX = /^[XYZ]\d{7,8}[A-Z]$/;

  public static documentValidator(control: FormControl) : {[key: string]: boolean} | null {
    if(control.value.length === 0 || !TediValidators.validateDocument(control.value).valid) {
      return {valid: false}
    }
    return null;
  };

  public static nifValidator(control: FormControl) : {[key: string]: boolean} | null {
    if(control.value.length === 0 || !TediValidators.validDNI(control.value)) {
      return {valid: false}
    }
    return null;
  };

  public static nieValidator(control: FormControl) : {[key: string]: boolean} | null {
    if(control.value.length === 0 || !TediValidators.validNIE(control.value)) {
      return {valid: false}
    }
    return null;
  };

  public static cifValidator(control: FormControl) : {[key: string]: boolean} | null {
    if(control.value.length === 0 || !TediValidators.validCIF(control.value)) {
      return {valid: false}
    }
    return null;
  };

  public static promoValidator(control: FormControl) : {[key: string]: boolean} | null {
    if(!TediValidators.validatePromo(control.value)) {
      return {valid: false}
    }
    return null;
  };

  public static validatePromo(promo: string) : boolean {
    return promo.length > 8 && (promo.substring(0,3) === 'WEB' || promo.substring(0,3) === 'DSI'
      && promo.substring(0,3) === 'GPL' || promo.substring(0,3) === 'NLK') && promo.substring(3,7) === 'TEDI';
  }

  public static validateDocument(document: string) {
    document = document.toUpperCase().replace(/\s/, '');

    var valid = false;
    var type = TediValidators.spainIdType( document );

    switch (type) {
      case 'dni':
        valid = TediValidators.validDNI( document );
        break;
      case 'nie':
        valid = TediValidators.validNIE( document );
        break;
      case 'cif':
        valid = TediValidators.validCIF( document );
        break;
    }

    return {
      type: type,
      valid: valid
    };
  }
  private static spainIdType( document: string ) : string {
    if ( document.match( TediValidators.DNI_REGEX ) ) {
      return 'dni';
    }
    if ( document.match( TediValidators.CIF_REGEX ) ) {
      return 'cif';
    }
    if ( document.match( TediValidators.NIE_REGEX ) ) {
      return 'nie';
    }
  };

  private static validDNI( dni:string ): boolean  {
    var dni_letters = "TRWAGMYFPDXBNJZSQVHLCKE";
    var letter = dni_letters.charAt( parseInt( dni, 10 ) % 23 );

    return letter == dni.charAt(8);
  };

  private static validNIE ( nie: string ) : boolean {

    // Change the initial letter for the corresponding number and validate as DNI
    let nie_prefix = nie.charAt( 0 );

    switch (nie_prefix) {
      case 'X': nie_prefix = '0'; break;
      case 'Y': nie_prefix = '1'; break;
      case 'Z': nie_prefix = '2'; break;
    }

    return TediValidators.validDNI( nie_prefix + nie.substr(1) );

  };

  private static validCIF( cif: string ) : boolean{
    if(cif.length !== 9) return false;
    let match = cif.match( TediValidators.CIF_REGEX );

    let letter  = match[1],
        number  = match[2],
        control = match[3];

    let even_sum: number = 0;
    let odd_sum: number = 0;
    let n: number;

    for ( var i = 0; i < number.length; i++) {
      n = parseInt( number[i], 10 );
      if ( i % 2 === 0 ) {
        n *= 2;
        odd_sum += n < 10 ? n : n - 9;
      } else {
        even_sum += n;
      }
    }
    const control_digit = (10 - parseInt((even_sum + odd_sum).toString().substr(-1)));
    const control_letter = 'JABCDEFGHI'.substr( control_digit, 1 );
    if ( letter.match( /[ABEH]/ ) ) {
      return control == control_digit.toString();
    } else if ( letter.match( /[KPQS]/ ) ) {
      return control == control_letter;
    } else {
      return control == control_digit.toString() || control == control_letter;
    }
  }

  // IBAN

  public static ibanValidator(control: FormControl) : {[key: string]: boolean} | null {
    if(control.value.length === 0 || !TediValidators.validateIBAN(control.value)) {
      return {valid: false}
    }
    return null;
  };

  public static validateIBAN(IBAN:string) : boolean {
    //Se pasa a Mayusculas
    IBAN = IBAN.toUpperCase();
    //Se quita los blancos de principio y final.
    IBAN = IBAN.trim();
    IBAN = IBAN.replace(/\s/g, ""); //Y se quita los espacios en blanco dentro de la cadena
    //La longitud debe ser siempre de 24 caracteres
    if (IBAN.length != 24) {
        return false;
    }

    // Se coge las primeras dos letras y se pasan a números
    const letra1 = IBAN.substring(0, 1);
    const letra2 = IBAN.substring(1, 2);
    const num1 = TediValidators.getnumIBAN(letra1);
    const num2 = TediValidators.getnumIBAN(letra2);
    //Se sustituye las letras por números.
    let isbanaux = String(num1) + String(num2) + IBAN.substring(2);
    // Se mueve los 6 primeros caracteres al final de la cadena.
    isbanaux = isbanaux.substring(6) + isbanaux.substring(0,6);

    //Se calcula el resto, llamando a la función modulo97, definida más abajo
    const resto: number = TediValidators.modulo97(isbanaux);
    if (resto == 1){
        return true;
    }else{
        return false;
    }
  }

  private static modulo97(iban: string): number {
    var parts = Math.ceil(iban.length/7);
    var remainer = "";

    for (var i = 1; i <= parts; i++) {
        remainer = String(parseFloat(remainer+iban.substr((i-1)*7, 7))%97);
    }

    return parseFloat(remainer);
  }

  private static getnumIBAN(letra: string) : number {
    const ls_letras = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    return ls_letras.search(letra) + 10;
  }
}
