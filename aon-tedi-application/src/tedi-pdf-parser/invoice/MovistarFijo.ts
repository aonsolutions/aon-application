import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

//
//  ___________________________________________________________________________________________________________
// |                                                                           																|
// |   Madrid,dd MMM yyyy Factura Fusión XXXXXXX Identificador unico de Acceso:99999999 		       Página 9/9 |
// |                                                                           							  			          |
// |   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                                                             |
// |                                                         XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 			|
// |   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                                                             |
// |   CIF / NIF: 99999999999                                                                        					|
// |                                                         XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 			|
// |   Periodo Consumos: dd MMM yyyy - dd MMM yyyy                                                            |
// |                                                         XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 			|
// |   Fecha de Factura: dd MMM yyyy                                                                          |
// |                                                         XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 			|
// |   Domiciliación Bancaria                                                                                 |
// |   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                                                             |
// |   Para ser pagado a partir de dd MMM yyyy                                                                |
// |   (El pago de esta factura se acredita por su abono bancario o el recibí de caja)                        |
// |                                                                           																|
// |                                                                  Resumen de factura                      |
// |   	                                                              ........................................|
// |                                                                           																|
// |                                                                    Cuotas Mensuales           999.9999 € |
// |                                                                           																|
// |                                                                           																|
// |                                                                    Impuestos                  999.9999 € |
// |                                                                                 Base imponible   Importe |
// |                                                                    IVA (99%)          999.9999  999.9999 |
// |                                                                           																|
// |                                                                           																|
// |                                                                           																|
// |                                                                    TOTAL a pagar            999.999999 € |
// |                                                                           																|
// |                                                                           																|
// |                                                                           																|
// |                                                                           																|
// | _________________________________________________________________________________________________________|

export class MovistarFijo {
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
        content0 = page.content;
        TediPDFParserUtils.sort(content0);
        if (content0) {
          line0 = TediPDFParserUtils.getLine(content0);
          if (line0) {
            // Madrid,1 Octubre 2018 | Factura TA60F0248732 | Teléfono
            match = line0.str.match(/^Madrid\s*,\s*\d+\s*[a-z\.]+\s*\d+[|\s]*Factura\s*(\w+)[|\s]*Tel.fono/i);
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
      throw new TediUnknownInvoiceFormatError('Not Movistar Fijo invoice');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'GRAN VIA, 28',
          postal_code: '28013',
          city: 'MADRID',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'A82018474',
        name: 'TELEFÓNICA DE ESPAÑA, S.A.U',
      },
    };

    if (match) {
      invoice.reference = match[1];
    }

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;
    let index = line.index;

    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    invoice.receiver = {};
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.name = line.str;
    }

    // skip ugly address
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // CIF/NIF:XXXXXXXXXXX
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/CIF\s*.\s*NIF\s*:\s*(\w+)/i);
        if (match) {
          invoice.receiver.document = match[1];
          invoice.receiver.document_country = 'ES';
          break;
        }
      }
    } while (line);

    const dateLineStart = index;

    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.address = { address: line.str };
    }

    // // Periodo Consumos: dd MMM yyyy - dd MMM yyyy
    // line = TediPDFParserUtils.getLine(content, index);
    // if (line) {
    //   index = line.index;
    // }

    // 99999 XXXXXXXXXX
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/(\d{5,5})\s*(.*)/i);
        if (match) {
          if (!invoice.receiver.address) {
            invoice.receiver.address = {};
          }
          invoice.receiver.address.postal_code = match[1].trim();
          invoice.receiver.address.city = match[2].trim();
          break;
        }
      }
    } while (line);

    // Fecha de Factura: dd MMM yyyy
    index = dateLineStart;
    do {
      line = TediPDFParserUtils.getLine(content, index);

      if (line) {
        index = line.index;
        match = line.str.match(/Fecha\s*de\s*Factura\s*:\s*(\d+)\s*(\D+)\s*(\d+)/i);
        if (match) {
          const dateStr = match[1] + '/' + TediPDFParserUtils.month(match[2]) + '/' + match[3];
          invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
          break;
        }
      }
    } while (line);

    // Fecha de cargo: dd MMM yyyy
    invoice.finances = [{}];
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Para\s*ser\s*pagado\s*a\s*partir\s*de\s*(\d+)\s*(\D+)\s*(\d+)/i);
        if (match) {
          const dateStr = match[1] + '/' + TediPDFParserUtils.month(match[2]) + '/' + match[3];
          invoice.finances[0].due_date = moment(dateStr, 'DD/MM/YYYY').toDate();
          break;
        }
      }
    } while (line);

    /*
    // line = getLine(content, line.index);
    // var bank = line.str;
    // console.log("Banco :" + bank);

    // Cuotas Mensuales           999.9999 €
    invoice.details = [];
    let currentIndex = index;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Cuotas\s*Mensuales\s*(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.details.push({
            description: 'Cuotas Mensuales',
            quantity: 1,
            price: TediPDFParserUtils.string2Number(match[1]),
            discount: 0,
            base: TediPDFParserUtils.string2Number(match[1]),
          });
          currentIndex = index;
          break;
        }
      }
    } while (line);
    index = currentIndex;

    currentIndex = index;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Servicios\s*Movistar\s*Fusi.n\s*Empresas\s*(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.details.push({
            description: 'Servicios Movistar Fusión Empresas',
            quantity: 1,
            price: TediPDFParserUtils.string2Number(match[1]),
            discount: 0,
            base: TediPDFParserUtils.string2Number(match[1]),
          });
          currentIndex = index;
          break;
        }
      }
    } while (line);
    index = currentIndex;

    // Consumos           999.9999 €
    currentIndex = index;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Consumos\s*(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.details.push({
            description: 'Consumos',
            quantity: 1,
            price: TediPDFParserUtils.string2Number(match[1]),
            discount: 0,
            base: TediPDFParserUtils.string2Number(match[1]),
          });
          currentIndex = index;
          break;
        }
      }
    } while (line);
    index = currentIndex;
    */

    // IVA (99%)          999.9999  999.9999
    invoice.taxes = [];
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/IVA\s*\(\s*(\d+)\s*%\s*\)\s*(-?\d+\s*(,\s*\d+)?)\s+(-?\d+\s*(,\s*\d+)?)/i);
        if (match) {
          invoice.taxes.push({
            tax: TaxType.IVA,
            base: TediPDFParserUtils.string2Number(match[2]),
            percentage: TediPDFParserUtils.string2Number(match[1]),
            quota: TediPDFParserUtils.string2Number(match[4]),
          });
          break;
        }
      }
    } while (line);

    // TOTAL a pagar           999.9999 €
    let mainTotal: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/TOTAL\s*a\s*pagar\s*(-?\d+(,\d+)?)/i);
        if (match) {
          mainTotal = TediPDFParserUtils.string2Number(match[1]);
          invoice.total = mainTotal;
          invoice.finances[0].amount = mainTotal;
          break;
        }
      }
    } while (line);

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
