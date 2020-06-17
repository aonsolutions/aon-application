import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

export class Ionos {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;
    // TODO: sort pages by page num ?
    let line0: TediPDFParserLine | undefined;
    let content0: PDFExtractText[] | undefined;
    let match;
    let i = 0;
    for (i = 0; i < pages.length; i++) {
      const page: PDFExtractPage = pages[i];
      try {
        // TODO: sort content by x and y coordinates ?
        content0 = page.content;
        if (content0) {
          TediPDFParserUtils.sort(content0);
          line0 = TediPDFParserUtils.getLine(content0);
          if (line0) {
            do {
              // match = line0.str.match(/NIF\s*(B85049435)/i);
              match = line0.str.match(/\s*1&1\s*IONOS\s*España\s*S\W?L\W?U\W+\s*NIF\s*(B85049435)/i);
              if (match) {
                break;
              }
              line0 = TediPDFParserUtils.getLine(content0, line0.index);
            } while (line0);
            if (match) {
              break;
            }
          }
        }
      } catch (e) {
        // tslint:disable-next-line: no-console
        console.error(' Catched!!' + e);
        throw new TediUnknownInvoiceFormatError(e);
      }
    }

    if (i === pages.length || !line0 || !content0) {
      throw new TediUnknownInvoiceFormatError('Not Ionos invoice');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'Avenida de La Vega, 1 - Edificio Veganova (Edif.3 planta 5o puerta C)',
          postal_code: '28108',
          city: 'MADRID',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'B85049435',
        name: '1&1 IONOS España S.L.U.',
      },
    };

    if (match) {
      invoice.reference = match[1];
    }

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;
    let index = 7;

    // Fecha de facturación: 09/06/19   | INVOICE DATE
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/Fecha\s*de\s*facturaci.n:\s*(\d*)\W?(\d*)\W?(\d*)/i);
      if (match) {
        const dateStr = match[1] + '/' + match[2] + '/' + 20 + match[3];
        invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
      }
    }

    // N.° de factura: 202762359266 | INVOICE NUMBER
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/N.°\s*de\s*factura:\s*(\d*)/i);
      if (match) {
        invoice.number = match[1];
      }
    }

    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    invoice.receiver = {};
    invoice.receiver.address = {};
    // NIF/CIF: B66941873  | DOCUMENT + DOCUMENT COUNTRY
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/NIF\W?CIF:\s*(\w*)/i);

      if (match) {
        invoice.receiver.document = match[1];
        invoice.receiver.document_country = 'ES';
      }
    }

    // Blank line
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Blank line
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // TRANSLOGIA DEVELOPMENT, S.L.  |  NAME
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.name = line.str;
    }

    //
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    //
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    //
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    //
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    //
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    //
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Duque de Wellington 52 | ADDRESS
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.address.address = line.str;
    }

    //
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // 01010 Vitoria-Gasteiz  | POSTAL CODE  + CITY
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      let arr: string[];

      arr = line.str.split(' ');
      invoice.receiver.address.city = arr[1];
      invoice.receiver.address.postal_code = arr[0];
    }

    // Skip lines
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Con.\s*Servicios\s*facturados\s*Tarifa\s*Cantidad\s*Importe\s*\W?EUR\W?\s*IVA\s*\W?%\W?/i);
        if (match) {
          break;
        }
      }
    } while (line);

    invoice.taxes = [];
    // invoice.details= [];
    let iva;
    let base;
    let percent;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;

        match = line.str.replace('.', ',').match(/\d*\s*Cuota\s*mensual\s*\d*,\d*\s*EUR\s*al\s*mes\s*(\d*)\s*m.\s*(\d*,\d*)/i);
        if (match) {
          // invoice.details.push({
          //   price: TediPDFParserUtils.string2Number( match[ 2 ] ),
          //   base:  TediPDFParserUtils.string2Number( match[ 2 ] ),
          //   description: 'Cuota Mensual',
          //   quantity: TediPDFParserUtils.string2Number( match[ 1 ] ),
          //   discount: 0,
          // });
        }

        match = line.str.match(/\d*\s*Descuento\s*Descuento\s*[-]?(\d*,\d*)/i);
        if (match) {
          // invoice.details.push({
          //   price: TediPDFParserUtils.string2Number( match[ 1 ] ),
          //   base:  TediPDFParserUtils.string2Number( match[ 1 ] ),
          //   description: 'Descuento',
          //   quantity: 1,
          //   discount: 0,
          // });
        }

        match = line.str.match(/Total\s*\W?base\s*imponible\W?\s*(\d*,\d*)/i);
        if (match) {
          base = match[1];
        }

        match = line.str.match(/[+]?\s*IVA\s*\W?(\d*,\d*)%\W?\s*(\d*,\d*)\s*EUR/i);
        if (match) {
          percent = match[1];
          iva = match[2];
        }

        match = line.str.match(/Total\s*a\s*pagar\s*(\d*,\d*)\s*EUR/i);
        if (match) {
          invoice.taxes.push({
            tax: TaxType.IVA,
            base: TediPDFParserUtils.string2Number(base),
            quota: TediPDFParserUtils.string2Number(iva),
            percentage: TediPDFParserUtils.string2Number(percent),
          });
          invoice.total = Number.parseFloat(match[1].replace(',', '.'));
        }
      }
    } while (line);
    return invoice;
  }
}
