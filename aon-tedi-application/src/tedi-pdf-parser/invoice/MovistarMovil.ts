import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

//  ___________________________________________________________________________________________________________
// |                                                                           																|
// |   Madrid,dd MMM yyyy  - Factura  XX-XXXX-XXXXXX                                              Página 9/99 |
// |                                                                                                          |
// |   UDAPA, S.COOP.                                                          							  			          |
// |   CIF / NIF: 99999999999                                XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 			|
// |   CL ARRIURDINA N 6 P.1. JUNDIZ                         UDAPA, S.COOP.                                   |
// |                                                         CL ARRIURDINA N 6 P.1. JUNDIZ                    |
// |   Tipo de contrato: XXXXXXXXXXXXXXXXXXXXXXXX            01015 VITORIA-GASTEZ                       			|
// |   Nº de líneas: XX                                                                                       |
// |                                                         XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 			|
// |   Domiciliación Bancaria                                                                                 |
// |   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                                                             |
// |   Para ser pagado a partir de dd MMM yyyy                                                                |
// |   (El pago de esta factura se acredita por su abono bancario o el recibí de caja)                        |
// |  ___________________________________________________________                                             |
// | |  Total(Base imponible)                     999.9999 €     |                                            |
// | |  IVA(99%)                                  999.9999 €     |                                            |
// | |  Total factura                             999.9999 €     |                                            |
// | |  Fuera de base imponible                   999.9999 €     |                                            |
// | | __________________________________________________________|                                            |
// | |  Total a pagar (euros)                     999.999999 €   |                                            |
// | | __________________________________________________________|                                            |
// |                                                                           																|
// |  RESUMEN DE SERVICIOS                                                                                    |
// | _________________________________________________________________________________________________________|
// |  Llamadas( 18 Dic. 18 a 17 Ene. 19)    99.9999 €                                                         |
// |                                                                                                          |
// |  Conceptos fuera de base imponible     99.9999 €                                                         |
// |                                                                                                          |
// |  Datos( 18 Dic. 18 a 17 Ene. 19)       99.9999 €                                                         |
// |                                                                                                          |
// |  Descuentos por cliente                99.9999 €                                                         |
// |                                                                                                          |
// |  Descuentos por plan                   99.9999 €                                                         |
// |                                                                                                          |
// |  Cuotas Mensuales                      99.9999 €                                                         |
// |                                                                                                          |
// |  Otros conceptos                       99.9999 €                                                         |
// | _________________________________________________________________________________________________________|

export class MovistarMovil {
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
            // Madrid,1 Enero  2018 - Factura TA60F0248732
            match = line0.str.match(/^Madrid\s*,\s*(\d+)\s*([a-z\.]+)\s*(\d+)\s*-\s*Factura\s*([\w-]+)$/i);
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
      throw new TediUnknownInvoiceFormatError('Not Movistar Móvil invoice');
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
      invoice.reference = match[4];
      const dateStr = match[1] + '/' + TediPDFParserUtils.month(match[2]) + '/' + match[3];
      invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
    }

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;
    let index = line.index;

    // Página 1/9
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    invoice.receiver = {};
    // UDAPA, S.COOP.
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.name = line.str;
    }

    // CIF/NIF: F01131978
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/CIF\s*.\s*NIF\s*:\s*(\w+)/i);
      if (match) {
        invoice.receiver.document = match[1];
        invoice.receiver.document_country = 'ES';
      }
    }

    // MS-000082458305-81-001015- 001-0-0-4-000140-000209
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    invoice.receiver.address = {};
    // CL ARRIURDINA N 6 P.I. JUNDIZ
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.address.address = line.str;
    }

    // Skip UDAPA, S.COOP
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Skip CL ARRIURDINA N 6 P.I. JUNDIZ
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Skip Tipo de contrato
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Skip Nº de líneas
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // City  + PC 01015 VITORIA-GASTEIZ
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      let arr: string[];
      arr = line.str.split(' ');
      invoice.receiver.address.city = arr[1];
      invoice.receiver.address.postal_code = arr[0];
    }

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

    // Skip M00008245830528B9M001227699999999919020100
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Skip (El pago de su factura se acredita por su abono bancario o el recibí de caja)
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // IVA (99%)      999.9999  999.9999
    invoice.taxes = [];
    let base;
    let percen;
    let quota;
    let mainTotal: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/RESUMEN\s*DE\s*SERVICIOS/i);

        if (match) {
          invoice.taxes.push({
            tax: TaxType.IVA,
            base: TediPDFParserUtils.string2Number(base),
            percentage: TediPDFParserUtils.string2Number(percen),
            quota: TediPDFParserUtils.string2Number(quota),
          });
          break;
        }

        // Total (Base imponible) 163,9055
        match = line.str.match(/Total\s*\W?Base\s*imponible\W?\s*([\d,]+)/i);
        if (match) {
          base = match[1];
        }

        // IVA (21%) 34,4202
        match = line.str.match(/IVA\s*\W?(\d*)\W?\W?\s*([\d,]+)/i);
        if (match) {
          percen = match[1];
          quota = match[2];
        }

        // // Fuera de base imponible
        // match = line.str.match(/Fuera\s*de\s*base\s*imponible\s*([+-]?\d+,\d+)/i);
        // if (match) {
        // }

        // TOTAL a pagar (euros) 198,33 €
        match = line.str.match(/TOTAL\s*a\s*pagar\s*\W?euros\W?\s*(-?\d+(,\d+)?)/i);
        if (match) {
          mainTotal = TediPDFParserUtils.string2Number(match[1]);
          invoice.total = mainTotal;
          invoice.finances[0].amount = mainTotal;
        }
      }
    } while (line);

    // // DETAILS
    // invoice.details = [];
    // do {
    //   // Datos (18 Dic. 18 a 17 Ene. 19) 58,3984 € Descuentos por cliente -125,2630 €
    //   line = TediPDFParserUtils.getLine(content, index);
    //   if (line) {
    //     index = line.index;
    //     match = line.str.match(/Datos\s*\W?\d*\s*\w*\W?\s*\d*\s*a\s*\d*\s*\w*\W?\s*\d*\W?\s*(\d+,\d+)\s*€/i);
    //     if (match) {
    //       invoice.details.push({
    //         description: 'Datos (18 Dic. 18 a 17 Ene. 19)',
    //         quantity: 1,
    //         price: TediPDFParserUtils.string2Number(match[1]),
    //         discount: 0,
    //         base: TediPDFParserUtils.string2Number(match[1]),
    //       });
    //     }

    //     // Llamadas (18 Dic. 18 a 17 Ene. 19) 244,3798 €
    //     match = line.str.match(/(Llamadas\s*\W?\d*\s*\w*\W?\s*\d*\s*a\s*\d*\s*\w*\W?\s*\d*\W?)\s*(\d+,\d+)\s*€/i);
    //     if (match) {
    //       invoice.details.push({
    //         description: match[1],
    //         quantity: 1,
    //         price: TediPDFParserUtils.string2Number(match[2]),
    //         discount: 0,
    //         base: TediPDFParserUtils.string2Number(match[2]),
    //       });
    //     }

    //     // Otros Conceptos 22,5550€
    //     match = line.str.match(/Otros\s*conceptos\s*(\d+,\d+)/i);
    //     if (match) {
    //       invoice.details.push({
    //         description: 'Otros conceptos',
    //         quantity: 1,
    //         price: TediPDFParserUtils.string2Number(match[1]),
    //         discount: 0,
    //         base: TediPDFParserUtils.string2Number(match[1]),
    //       });
    //     }

    //     // Cuotas Mensuales 22,5550€
    //     match = line.str.match(/Cuotas\s*Mensuales\s*(\d+,\d+)/i);
    //     if (match) {
    //       invoice.details.push({
    //         description: 'Cuotas Mensuales',
    //         quantity: 1,
    //         price: TediPDFParserUtils.string2Number(match[1]),
    //         discount: 0,
    //         base: TediPDFParserUtils.string2Number(match[1]),
    //       });
    //     }

    //     // Descuentos por cliente -16,0870 €
    //     match = line.str.match(/Descuentos\s*por\s*cliente\s*([+-]?\d+,\d+)/i);
    //     if (match) {
    //       invoice.details.push({
    //         description: 'Descuentos por cliente',
    //         quantity: 1,
    //         price: TediPDFParserUtils.string2Number(match[1]),
    //         discount: 0,
    //         base: TediPDFParserUtils.string2Number(match[1]),
    //       });
    //     }

    //     // Conceptos fuera de base imponible 20,0000€
    //     match = line.str.match(/Conceptos\s*fuera\s*de\s*base\s*imponible\s*([+-]?\d+,\d+)/i);
    //     if (match) {
    //       invoice.details.push({
    //         description: 'Conceptos fuera de base imponible',
    //         quantity: 1,
    //         price: TediPDFParserUtils.string2Number(match[1]),
    //         discount: 0,
    //         base: TediPDFParserUtils.string2Number(match[1]),
    //       });
    //     }

    //     // Descuentos por plan -2,7174€
    //     match = line.str.match(/Descuentos\s*por\s*plan\s*([+-]?\d+,\d+)/i);
    //     if (match) {
    //       invoice.details.push({
    //         description: 'Descuentos por plan',
    //         quantity: 1,
    //         price: TediPDFParserUtils.string2Number(match[1]),
    //         discount: 0,
    //         base: TediPDFParserUtils.string2Number(match[1]),
    //       });
    //     }
    //   }
    // } while (line);

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
