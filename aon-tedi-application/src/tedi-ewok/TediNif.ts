export enum NifType {
  DNI = 'DNI', // 8 números + letra de control.

  NIF = 'NIF', // K,L,M + 7 números + letra de control.
  // NIFK = 'NIFK',
  // NIFL = 'NIFL',
  // NIFM = 'NIFM',

  NIE = 'NIE', // X,Y,Z + 7 números + letra de control.
  // NIEX = 'NIEX',
  // NIEY = 'NIEY',
  // NIEZ = 'NIEZ',

  CIF = 'CIF', // 1 letra + 7 números + letra de control.
  // A Sociedades anónimas											Número
  // B Sociedades de responsabilidad limitada		Número
  // C Sociedades colectivas										Número
  // D Sociedades comandatarias									Número
  // E Comunidades de bienes										Número
  // F Sociedades Cooperativas									Número
  // G Asociaciones y Fundaciones								Número
  // H Comunidades de propietarios							Número
  // J Sociedades civiles												Número
  // N Entidades extranjeras										Letra
  // P Corporaciones Locales										Letra
  // Q Organismos públicos											Letra
  // R Congregaciones														Letra
  // S Órganos																	Letra
  // U Uniones Temporales de Empresas						Número
  // V Otros 																		Número
  // W ---																			Letra
}

const fakes: string[] = ['A00000000'];

export class Nif {
  public static find(str: string): Nif[] {
    const nifs: Nif[] = [];
    const re: RegExp = /((ES[\s_-])?[A-Z0-9][\s_-]?[O0-9]{2}[_-]?[O0-9]{5}[_-]?[A-Z0-9]|[O0-9]{2}\s*[,]\s*[O0-9]{3}\s*[,]\s*[O0-9]{3}\s*[A-Z]|[A-Z0-9][\s_-]?([O0-9]\s?){7}[\s_-]?[A-Z0-9])/im;
    let match: RegExpMatchArray | null = re.exec(str);
    let index = 0;
    while (match !== null) {
      const match0: string = match[0]
        .replace(/O/, '0')
        .replace(/[,\s_-]/g, '')
        .replace(/^ES/, '');

      let nif: Nif | null = Nif.parse(match0);

      if (nif === null && match0.match(/^8/)) {
        nif = Nif.parse(match0.replace(/^8/, 'B'));
      }

      if (nif !== null) {
        const i: number = index + match.index!;
        let fake: boolean = i >= 4 && str.substr(i - 4, 4).match(/[A-Z0-9]{4}/i) !== null;
        if (!fake) {
          fake = str.substr(i + match[0].length, 4).match(/[A-Z0-9]{4}/i) !== null;
        }

        if (!fake) {
          nifs.push(nif);
          index += match.index! + match[0].length;
        } else {
          index += match.index! + 1;
        }
      } else {
        index += match.index! + 1;
      }
      // TODO: We can improve this
      match = re.exec(str.substr(index));
    }
    return nifs.filter(nif => fakes.indexOf(nif.str) < 0);
  }

  public static parse(str: string): Nif | null {
    if (Nif.isCif(str)) {
      return new Nif(str, NifType.CIF);
    }
    if (Nif.isDni(str)) {
      return new Nif(str, NifType.DNI);
    }
    if (Nif.isNie(str)) {
      return new Nif(str, NifType.NIE);
    }
    if (Nif.isNif(str)) {
      return new Nif(str, NifType.NIF);
    }

    return null;
  }

  public static isDni(str: string): boolean {
    return Nif.is(str, /^(\d{8})([A-HJ-NP-TV-Z])$/);
  }

  public static isNif(str: string): boolean {
    return Nif.is(str, /^[KLM](\d{7})([A-HJ-NP-TV-Z])$/);
  }

  public static isNie(str: string): boolean {
    if (str) {
      const match: RegExpMatchArray | null = str.toUpperCase().match(/^([XYZ])(\d{7})([A-HJ-NP-TV-Z])$/);
      if (match) {
        const xyz = { X: 0, Y: 1, Z: 2 };
        return 'TRWAGMYFPDXBNJZSQVHLCKE'[+(xyz[match[1]] + match[2]) % 23] === match[3];
      }
    }
    return false;
  }

  // String.prototype.isNif=function()
  // {
  //   return /^(\d{7,8})([A-HJ-NP-TV-Z])$/.test(this) && ("TRWAGMYFPDXBNJZSQVHLCKE"[(RegExp.$1%23)]==RegExp.$2);
  // };

  public static isCif(str: string): boolean {
    if (str) {
      let match: RegExpMatchArray | null = str.toUpperCase().match(/^[A-JUV](\d{7})([0-9])$/);
      if (match) {
        return Nif.cifCtrlDigit(match[1]) === +match[2];
      }
      match = str.toUpperCase().match(/^[N-SW](\d{7})([A-J])$/);
      if (match) {
        return 'JABCDEFGHI'[Nif.cifCtrlDigit(match[1])] === match[2];
      }
    }
    return false;
  }

  private static is(str: string, regExp: RegExp): boolean {
    if (str) {
      const match: RegExpMatchArray | null = str.toUpperCase().match(regExp);
      if (match) {
        return 'TRWAGMYFPDXBNJZSQVHLCKE'[+match[1] % 23] === match[2];
      }
    }
    return false;
  }

  private static cifCtrlDigit(digits: string): number {
    const a = +digits[1] + +digits[3] + +digits[5];
    const b1 = +digits[0] * 2;
    const b3 = +digits[2] * 2;
    const b5 = +digits[4] * 2;
    const b7 = +digits[6] * 2;

    const u1 = b1 % 10;
    const d1 = (b1 - u1) / 10;
    const u3 = b3 % 10;
    const d3 = (b3 - u3) / 10;
    const u5 = b5 % 10;
    const d5 = (b5 - u5) / 10;
    const u7 = b7 % 10;
    const d7 = (b7 - u7) / 10;
    const b = u1 + d1 + u3 + d3 + u5 + d5 + u7 + d7;

    const c = a + b;
    const e = c % 10;

    const d = e ? 10 - e : 0;

    return d;
  }

  public str: string;
  public type: NifType;

  private constructor(str: string, type: NifType) {
    this.str = str;
    this.type = type;
  }
}
