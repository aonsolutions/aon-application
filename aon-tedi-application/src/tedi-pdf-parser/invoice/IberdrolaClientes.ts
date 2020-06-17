import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType, TediString } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

//
//  ___________________________________________________________________________________________________________
// |                                                                           																|
// |   IBERDROLA CLIENTES, S.A.U.                                              																|
// |   CIF A-95758389                                                          																|
// |                                                                           																|
// |   DATOS DE FACTURA                                                        																|
// |   Periodo de facturación dd/MM/yyyy - dd/MM/yyyy                          																|
// |   Número de factura 99999999999999999                                     																|
// |   Fecha de emisión de factura dd de MMM de yyyy                           																|
// |   Fecha prevista de cargo dd/MM/yyyy                                      																|
// |   Factura con lectura real                                                                        				|
// |   Titular XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                           																|
// |   CIF titular XXXXXXXXX                                                   																|
// |   Referencia contrato suministro 99999999                                 																|
// |                                                                           																|
// |   TOTAL IMPORTE FACTURA:      999,99 €                                                                   |
// |                                          				  Dirección de suministro: XXXXXXXXXXXXXXXXXXXXXXXXXX   |
// |                                                    XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX   |
// |   RESUMEN DE FACTURACIÓN                                                                        			    |
// |                                                                           																|
// |   ENERGÍA                              999,99 €                                                          |
// |   SERVICIOS Y OTROS CONCEPTOS          999,99 €                                                          |
// |   IVA 99% s/999,99 €                   999,99 €                                                          |
// |  _______________________________________________                                                         |                																|
// |   TOTAL A PAGAR                        999,99 €                                                          |
// |                                                                           																|
// | _________________________________________________________________________________________________________|
//  ___________________________________________________________________________________________________________
// |                                                                           																|
// |  DATOS RELACIONADOS CON SU SUMINISTRO                                     																|
// |  Referencia contrato suministro: XXXXXXXXXXXXXX                           																|
// |  ...                                                                         														|
// |  ...                                                                         														|
// |  Entidad: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX																													|
// |  IBAN: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX																													|
// |                                                                           																|
// |                                                                           																|
// |                                                                           																|
// | _________________________________________________________________________________________________________|

export class IberdrolaClientes {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;

    // TODO: sort pages by page num ?
    let line0: TediPDFParserLine | undefined;
    let content0: PDFExtractText[] | undefined;
    let match0;
    let i = 0;

    for (i = 0; i < pages.length; i++) {
      const page0: PDFExtractPage = pages[i];

      try {
        // TODO: sort content by x and y coordinates ?
        content0 = page0.content;
        if (content0) {
          TediPDFParserUtils.sort(content0);
          line0 = TediPDFParserUtils.getLine(content0);
          if (line0) {
            do {
              // IBERDROLA CLIENTES, S.A.U.
              match0 = line0.str.match(/\s*Bizkaia,\s*tomo\s*5448,\s*folio\s*19,\s*hoja\s*BI-63981,\s*inscripci.n\s*1.\s*-\s*CIF\s*A-95758389/i);
              if (match0) {
                break;
              }
              line0 = TediPDFParserUtils.getLine(content0, line0.index);
            } while (line0);
            if (match0) {
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
      throw new TediUnknownInvoiceFormatError('Not Iberdrola clientes, invoice');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'C/ TOMÁS REDONDO 1',
          postal_code: '28033',
          city: 'MADRID',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'A95758389',
        name: 'IBREDROLA CLIENTES, S.A.U',
      },
    };

    let line: TediPDFParserLine | undefined = line0;
    let content: PDFExtractText[] = content0;
    let index = 0;

    // Número factura
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/^N.mero\s*de\s*factura\s*(.*)/i);
        if (match) {
          invoice.reference = match[1];
          break;
        }
      }
    } while (line);

    // Fecha de emisión de factura dd de MMM de yyyy
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/Fecha\s*de\s*emisi.n\s*de\s*factura\s*(\d+)\s*de\s*(\D+)\s*de\s*(\d+)/i);
        if (match) {
          const issueDateStr = match[1] + '/' + TediPDFParserUtils.month(match[2]) + '/' + match[3];
          invoice.date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
          break;
        }
      }
    } while (line);

    // Fecha prevista de cargo dd/MM/yyyy
    invoice.finances = [{}];
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/Fecha\s*prevista\s*de\s*cargo\s*(\d+\/\d+\/\d+)/i);
        if (match) {
          const issueDateStr = match[1] + '/' + match[2] + '/' + match[3];
          invoice.finances[0].due_date = moment(issueDateStr, 'DD/MM/YYYY').toDate();
          break;
        }
      }
    } while (line);

    // Titular XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    invoice.receiver = {};
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/Titular\s*(.+)/i);
        if (match) {
          invoice.receiver.name = match[1];
          break;
        }
      }
    } while (line);

    // CIF titular XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/CIF\s*titular\s*(.+)/i);
        if (match) {
          invoice.receiver.document_country = 'ES';
          invoice.receiver.document = match[1];
          break;
        }
      }
    } while (line);

    // TOTAL IMPORTE FACTURA:      999,99 €
    let mainTotal: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/TOTAL\s*IMPORTE\s*FACTURA\s*:\s*(-?\d+(,\d+)?)/i);
        if (match) {
          mainTotal = TediPDFParserUtils.string2Number(match[1]);
          invoice.total = mainTotal;
          invoice.finances[0].amount = mainTotal;
          break;
        }
      }
    } while (line);

    // Dirección de suministro: XXXXXXXXXXXXXXXXXXXXXXXXXX
    let addr = '';
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/Direcci.n\s*de\s*suministro\s*:\s*(.*)/i);
        if (match) {
          addr = TediString.trim(match[1]);
          break;
        }
      }
    } while (line);

    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/RESUMEN DE FACTURACI.N/i);
        if (match) {
          break;
        }
        addr = addr + ' ' + line.str;
      }
    } while (line);

    invoice.receiver.address = { address: TediString.polish(addr) };

    /* Comment details
    // ENERGÍA      999,99 €
    invoice.details = [];
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/ENERG.A\s*(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.details.push({
            description: 'ENERGÍA',
            quantity: 1,
            price: TediPDFParserUtils.string2Number(match[1]),
            discount: 0,
            base: TediPDFParserUtils.string2Number(match[1]),
          });
          break;
        }
      }
    } while (line);

    // SERVICIOS Y OTROS CONCEPTOS      999,99 €
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/SERVICIOS\s*Y\s*OTROS\s*CONCEPTOS\s*(-?\d+(,\d+)?)/i);
        if (match) {
          invoice.details.push({
            description: 'SERVICIOS Y OTROS CONCEPTOS',
            quantity: 1,
            price: TediPDFParserUtils.string2Number(match[1]),
            discount: 0,
            base: TediPDFParserUtils.string2Number(match[1]),
          });
          break;
        }
      }
    } while (line);
    */

    // IVA 99% s/999,99 €                   999,99 €
    invoice.taxes = [];
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        const match = line.str.match(/IVA\s*(\d+)\s*%\s*\D*(-?\d+\s*(,\s*\d+)?)\s*.\s*(-?\d+\s*(,\s*\d+)?)/i);
        if (match) {
          const taxPercent = TediPDFParserUtils.string2Number(match[1]);
          invoice.taxes.push({
            tax: TaxType.IVA,
            quota: TediPDFParserUtils.string2Number(match[4]),
            base: TediPDFParserUtils.string2Number(match[2]),
            percentage: taxPercent,
          });
          /* Comment details  
         invoice.details.forEach(detail => (detail.vat = taxPercent));
          */

          break;
        }
      }
    } while (line);

    let financeMatch;
    for (i++; i < pages.length; i++) {
      const page = pages[i];
      try {
        // TODO: sort content by x and y coordinates ?
        content = page.content;
        TediPDFParserUtils.sort(content);
        line = TediPDFParserUtils.getLine(content);
        if (line) {
          do {
            index = line.index;
            financeMatch = line.str.match(/^Entidad\s*:\s*(.*)/i);
            if (financeMatch) {
              break;
            }
            line = TediPDFParserUtils.getLine(content, index);
          } while (line);
        }
        if (financeMatch) {
          break;
        }
      } catch (e) {
        // tslint:disable-next-line: no-console
        console.error(' Catched!!' + e);
      }
    }

    if (financeMatch) {
      do {
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          const match = line.str.match(/IBAN\s*:\s*(.*)/i);
          if (match) {
            invoice.finances[0].iban = match[1];
            break;
          }
        }
      } while (line);
    }

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
