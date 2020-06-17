import { PDFExtractText } from 'pdf.js-extract';
import { InvoiceTax, TaxType } from '../../tedi-ewok/TediEwok';

export interface TediPDFParserLine {
  str: string;
  index: number;
}

export class TediPDFParserUtils {
  public static getLine(content: PDFExtractText[], idx: number = 0): TediPDFParserLine | undefined {
    if (!content) {
      return undefined;
    }
    if (idx >= content.length) {
      return undefined;
    }
    const y = content[idx].y;
    let line = content[idx].str;
    while (++idx < content.length && y === content[idx].y) {
      line += ' ' + content[idx].str;
    }
    return {
      str: line,
      index: idx,
    };
  }

  public static sort(content: PDFExtractText[]): void {
    content.sort((a: PDFExtractText, b: PDFExtractText) => {
      const y = a.y - b.y;
      return y === 0 ? a.x - b.x : y;
    });
  }

  public static getLines(content: PDFExtractText[]): string[] {
    this.sort(content);
    const lines: string[] = [];
    let y = -1;
    let line = '';
    for (const token of content) {
      // tslint:disable-next-line: no-console
      console.log(y + ' -- ' + token.y + ' -- [' + token.str + '] ' + '[' + line + ']');
      if (y !== token.y) {
        lines.push(line);
        y = token.y;
        line = '';
      }
      line += ' ' + token.str;
    }
    return lines;
  }

  /**
   * adjustTaxes: Compare the total invoice with the taxes, if they don't match, then adjust them.
   * param; arr:  array of InvoiceTax, mainTotal: total invoice
   */
  public static adjustTaxes(arr: InvoiceTax[], mainTotal: number) {
    if (arr == null || mainTotal == null) {
      return;
    }
    let totalFromTaxes = 0;
    arr.forEach(e => {
      totalFromTaxes += e.base + e.quota;
    });
    totalFromTaxes = Math.round(totalFromTaxes * 100) / 100;

    if (mainTotal !== totalFromTaxes) {
      // if they don't match, adjust them.
      arr.push({
        tax: TaxType.IVA,
        base: Math.round((mainTotal - totalFromTaxes) * 100) / 100,
        percentage: 0,
        quota: 0,
      });
      // tslint:disable-next-line: no-console
      // console.log('FromTaxes != MainTotal  ' + totalFromTaxes + '!=' + mainTotal + ' Adding...' + Math.round((mainTotal - totalFromTaxes) * 100) / 100,);
    }
  }

  public static string2Number(str: string): number {
    str = str.trim();
    str = str.replace(/[\. ]/g, ''); // Se eliminan los puntos y los espacios.
    str = str.replace(/,/g, '.'); // Se sustituyen comas por puntos
    return Number.parseFloat(str);
  }

  public static month(str: string): number {
    str = str.toUpperCase().trim();
    const MONTHS: any = {
      ENERO: 1,
      'ENE.': 1,
      FEBRERO: 2,
      'FEB.': 2,
      MARZO: 3,
      'MAR.': 3,
      ABRIL: 4,
      'ABR.': 4,
      MAYO: 5,
      'MAY.': 5,
      JUNIO: 6,
      'JUN.': 6,
      JULIO: 7,
      'JUL.': 7,
      AGOSTO: 8,
      'AGO.': 8,
      SEPTIEMBRE: 9,
      'SEP.': 9,
      OCTUBRE: 10,
      'OCT.': 10,
      NOVIEMBRE: 11,
      'NOV.': 11,
      DICIEMBRE: 12,
      'DIC.': 12,
    };
    const mth: number = MONTHS[str];
    return mth;
  }
  /*
  public static getYear(str: string): string {
    const arr = str.split('/');
    if (arr && arr.length > 1) {
      return arr[2];
    }
    return '';
  }

  public static getMonth(str: string): string {
    const arr = str.split('/');
    if (arr && arr.length > 0) {
      return arr[1];
    }
    return '';
  }

  public static getDay(str: string): number {
    const arr = str.split('/');
    if (arr && arr.length > 0) {
      return Number(arr[0]);
    }
    return 0;
  }

  public static getBank(iban: string): string | null {
    const match = iban.match(/^\s*(\d{4})/i);
    if (!match) {
      return null;
    }
    const entidad = match[1];
    const BANKS: any = {
      '0019': 'DEUTSCHE BANK SAE',
      '0049': 'BANCO SANTANDER',
      '0061': 'BANCA MARCH',
      '0073': 'OPEN BANK',
      '0075': 'BANCO POPULAR ESPAÑOL',
      '0081': 'BANCO DE SABADELL',
      '0108': 'SOCIÉTÉ GÉNÉRALE',
      '0128': 'BANKINTER',
      '0130': 'BANCO CAIXA GERAL',
      '0131': 'NOVO BANCO',
      '0138': 'BANKOA',
      '0152': 'BARCLAYS BANK PLC',
      '0182': 'BANCO BILBAO VIZCAYA ARGENTARIA',
      '0186': 'BANCO MEDIOLANUM',
      '0188': 'BANCO ALCALÁ',
      '0234': 'BANCO CAMINOS',
      '0238': 'BANCO PASTOR',
      '0239': 'EVO BANCO',
      '0240': 'BANCO DE CRÉDITO SOCIAL COOPERATIVO',
      '0487': 'BANCO MARE NOSTRUM',
      '1465': 'ING BANK NV',
      '1474': 'CITIBANK EUROPE PLC',
      '2000': 'CECABANK',
      '2038': 'BANKIA',
      '2045': 'CAJA DE AHORROS Y MONTE DE PIEDAD DE ONTINYENT',
      '2048': 'LIBERBANK',
      '2080': 'ABANCA CORPORACIÓN BANCARIA',
      '2085': 'IBERCAJA BANCO',
      '2095': 'KUTXABANK',
      '2100': 'CAIXABANK',
      '2103': 'UNICAJA BANCO',
      '2105': 'BANCO CASTILLA-LA MANCHA',
      '3025': 'CAIXA DE CREDIT DEL ENGINYERS',
      '3035': 'CAJA LABORAL POPULAR CC',
      '3081': 'CAJA RURAL CASTILLA-LA MANCHA3058 CAJAMAR CAJA RURAL',
      '3821': 'COMMERZBANK AG',
      '3842': 'BNP PARIBAS PARIS',
      '3873': 'BANCO SANTANDER TOTTA',
      '3877': 'DANSKE BANK A/S',
    };
    const bank = BANKS[entidad];
    return bank;
  }
*/
}
