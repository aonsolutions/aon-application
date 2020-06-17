import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

//                                                    ________________________________________________________
//                                                   | PAGADO                    	                            |
//                                                   | Vendido por Amazon EU S.á r.l., Sucursal en España     |
//                                                   | IVA ESW0184081H                                        |
//                                                   |________________________________________________________|
//                                                   | XXXXXXXXXXXXXX             XXXXXXXXXXXXXXXXXXX         |
//       XXXXXXXXXXXXXXXXXXXXXXXXXXX                 | XXXXXXXXXXXXXX             XXXXXXXXXXXXXXXXXXX         |
//       XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX              | Total a pagar              XX,XX €                     |
//       XXXXXXXXXXXXXX                              |________________________________________________________|
//       XX
//
//  Si tienes preguntas sobre tus pedidos, visita https:// www.amazon.es/contacto
//  ___________________________________________________________________________________________________________
// |                                                                           																|
// |   Dirección XXXXXXXXXXXXXXX           Dirección de envío             Vendido por                         |
// |   xxxxxx xxxxxx xxxxxxx               xxxxxx xxxxxx xxxxxxx          xxxxxx xxxxxx xxxxxxx               |
// |   xxxxxx xxxxxx xxxxxxxxxxxxx         xxxxxx xxxxxx xxxxxxxxxxxxx    xxxxxx xxxxxx xxxxxxxxxxxxx         |
// |   xxxxxxxx xxxxxx xxxx                xxxxxxxx xxxxxx xxxx           xxxxxxxx xxxxxx xxxx                |
// |   xx                                  xx                             xxxxxxx                             |
// |                                                                      xxxxxxxxxxxxxxxx                    |
// |__________________________________________________________________________________________________________|
// |   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX                                                             |
// |                                                                                                          |
// |   Fecha  del pedido               XXXXXXXXX 			                                                        |
// |   Número del pedido               XXXXXXXXXXXXXXX 			                                                  |
// |   XXXXXXXXXXXXXXXXX               XXXXXXXXXXXXXXX 			                                                  |
// |__________________________________________________________________________________________________________|
// |                                                                                                          |
// |   DETALLES DEL DOCUMENTO                                                                                 |
// |__________________________________________________________________________________________________________|
// |   Descripción                                     Cant.  P.Unitario  IVA %   P.Unitario     Precio total |
// |                                                       (IVA excluido)      (IVA incluido)  (IVA incluido) |
// |                                                                                                          |
// |                                                                                                          |
// |   xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx           1      XX,XX€     21%        XX,XX€           XX,XX€ |
// |   ASIN: B07HJ8B5V1                                                        																|
// |__________________________________________________________________________________________________________|
//                                                     TOTAL                        						       XX,XX€ |
//                                                                             																|
//                                                               IVA %         Precio total            IVA    |
//                                                                            (IVA excluido)                  |
//                                                                            																|
//                                                               21%          	xx,xx €						    x,xx €	|
//                                                      ______________________________________________________|
//                                                      Total                 	xx,xx €						    x,xx €	|

export class Amazon {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;
    // TODO: sort pages by page num ?
    let line0: TediPDFParserLine | undefined;
    let content0: PDFExtractText[] | undefined;
    let match;
    let i = 0;
    let ready: boolean | undefined;
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
              match = line0.str.match(
                /\s*Madrid\s*.\s*Tomo\s*33.166\s*.\s*Libro\s*0\s*.\s*Folio\s*105.\s*Seccion\s*8.\s*Hoja\s*M-596.819\s*.\s*NIF\s*W0184081H/i,
              );
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
      throw new TediUnknownInvoiceFormatError('NOT AMAZON INVOICE');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'Calle de Ramírez de Prado 5',
          postal_code: '28045',
          city: 'MADRID',
          province: 'Madrid',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'W0184081H',
        name: 'Amazon EU S.à r.l., Sucursal en España',
      },
    };

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;
    let index = 0;

    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/^IVA\s?ES(W0184081H)/i);
        if (match) {
          invoice.reference = match[1];
          break;
        }
      }
    } while (line);

    // Fecha de la factura/Fecha de la entrega | Fecha de envío dd/MM/yyyy
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/(\d+)\s?(\w+)\s?(\d+)/i);
      if (match) {
        const dateStr = match[1] + '/' + TediPDFParserUtils.month(match[2]) + '/' + match[3];
        invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
      } else {
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          match = line.str.match(/(\d+)\s?(\w+)\s?(\d+)/i);
          if (match) {
            const dateStr = match[1] + '/' + TediPDFParserUtils.month(match[2]) + '/' + match[3];
            invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
          } else {
            line = TediPDFParserUtils.getLine(content, index);
            if (line) {
              index = line.index;
              match = line.str.match(/^entrega\s?(\d+)\s?(\w+)\s?(\d+)/i);
              if (match) {
                const dateStr = match[1] + '/' + TediPDFParserUtils.month(match[2]) + '/' + match[3];
                invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
              }
            }
          }
        }
      }
    }

    // Skip Lines hasta Dirección de Facturación OR Dirección comercials
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/[A-Za-zñÑáéíóúÁÉÍÓÚ\s]+Direcci.n\s?de\s?env.o/i);
      }
    } while (!match);

    invoice.receiver = {};
    // Name: Alejandra Jimenez González
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      let arr: string[];
      arr = line.str.split('  ');
      invoice.receiver.name = arr[0].replace(/\s*$/, '');
    }

    // Address: C/Santísima Trinidad, 30, planta 7,puerta 8
    invoice.receiver.address = {};
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      match = line.str.match(/(^[0-9]{1,8}\w{1})\s?([A-Za-zñÑáéíóúÁÉÍÓÚ0-9\s,/º]+)/i);
      if (match) {
        ready = true;
        invoice.receiver.document = match[1];
        let arr: string[];
        arr = match[2].split('  ');
        invoice.receiver.address.address = arr[0].replace(',', '').replace(' ', '');
      } else {
        index = line.index;
        let arr: string[];
        arr = line.str.split('  ');
        match = arr[0].match(/([A-Za-zñÑáéíóúÁÉÍÓÚ0-9\s,º]+)\sNIF:/i);
        if (match) {
          invoice.receiver.address.address = match[1];
        } else {
          if (arr.length !== 3) {
            arr = line.str.split(',');
            invoice.receiver.address.address = arr[0];
          } else {
            invoice.receiver.address.address = arr[0];
          }
        }
      }
      invoice.receiver.document_country = 'ES';
    }

    // City: Madrid CP: 28010
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/(B[0-9]{1,8})\s?/i);
      if (match) {
        ready = true;
        invoice.receiver.document = match[1];

        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          let arr: string[];
          arr = line.str.split('  ');
          arr = arr[0].split(',');

          invoice.receiver.address.postal_code = arr[2].replace(' ', '');
          invoice.receiver.address.city = arr[1].replace(' ', '');
        }
      } else {
        match = line.str.match(/(^[0-9]{1,8}\w{1})\s?/i);
        if (match) {
          ready = true;
          invoice.receiver.document = match[1];
          line = TediPDFParserUtils.getLine(content, index);
          if (line) {
            index = line.index;
            let arr: string[];
            arr = line.str.split('  ');
            arr = arr[0].split(',');

            invoice.receiver.address.postal_code = arr[2].replace(' ', '');
            invoice.receiver.address.city = arr[1].replace(' ', '');
          }
        } else {
          let arr: string[];
          arr = line.str.split('  ');
          arr[0] = arr[0].replace(',', '');
          arr = arr[0].split(',');

          invoice.receiver.address.postal_code = arr[1].replace(' ', '');
          arr = arr[0].split(' ');

          invoice.receiver.address.city = arr[1];
        }
      }
    }

    // ES ES España
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Document: B55719207
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/^NIF\s?sujeto\s?de\s?IVA\s?(\w+)\s+IVA\s?\w+/i);
      if (match) {
        invoice.receiver.document = match[1];
      } else {
        // When no "B55719207" on the Invoice
        if (!ready) {
          invoice.receiver.document = '';
        }
      }
    }

    // Skip lines
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/^\s*\(IVA\s?excluido\)\s*\(IVA\s*incluido\)\s*\(IVA\s*incluido\)/i);
      }
    } while (!match);

    // DETAILS + TOTAL
    invoice.details = [];
    let mainTotal: number = 0;
    let n1;
    let n2;
    let text;
    let iva;

    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/(^[+-]?[0-9]{1,9}(?:,[0-9]{1,2})?\s?)€/i);

        if (match) {
          mainTotal = TediPDFParserUtils.string2Number(match[0]);
          invoice.total = mainTotal;

          break;
        }

        match = line.str.match(/^Total\s*([+-]?[0-9]{1,9}(?:,[0-9]{1,2})?\s?)€/i);
        if (match) {
          mainTotal = TediPDFParserUtils.string2Number(match[1]);
          invoice.total = mainTotal;

          break;
        }

        // Total 99.99 €
        match = line.str.match(/[A-Za-zñÑáéíóúÁÉÍÓÚ0-9-,./\s\W]+/i);
        if (match) {
          let arr: string[];
          arr = line.str.split(' ');

          for (i = 0; i < arr.length; i++) {
            // Remove empty items of arr
            match = arr[i].match(/(\d*)%/i);

            if (match) {
              iva = TediPDFParserUtils.string2Number(match[1]);
              iva = iva / 100;
            }

            if (arr[i] === '') {
              arr.splice(i, 1);
            }

            if (arr[i] === '€') {
              n1 = arr[i - 2];
              n2 = arr[i - 1];
            } else {
              text = text + arr[i] + ' ';
              text = text.replace(' 21% ', ' ');
              text = text.replace(' 10% ', ' ');
              text = text.replace(' 4% ', ' ');
              text = text.replace(n1 + ' ', '');
              text = text.replace(n2, '');
              text = text.replace('  ', ' ');
              text = text.replace(' undefined ', ' ');
            }
          }
        }
        /* Comment details
        match = line.str.match(/^ASIN:\s*(\w+)/i);
        if (match) {
          text = text.replace(match[0], '');
          text = text.replace('  ', '');

          const num = TediPDFParserUtils.string2Number(n2) / (1 + iva);

          
          invoice.details.push({
            price: TediPDFParserUtils.string2Number(n2),
            base: Number.parseFloat(num.toPrecision(4)),
            description: text,
            quantity: 1,
            discount: 0,
          });
          text = '';
        }

        */
      }
    } while (line);

    // Taxes
    invoice.taxes = [];
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/IVA\s?excluido/i);
        if (match) {
          line = TediPDFParserUtils.getLine(content, index);
          if (line) {
            index = line.index;
            let arr: string[];
            arr = line.str.split(' ');

            invoice.taxes.push({
              tax: TaxType.IVA,
              base: TediPDFParserUtils.string2Number(arr[1]),
              quota: TediPDFParserUtils.string2Number(arr[3]),
              percentage: TediPDFParserUtils.string2Number(arr[0]),
            });
          }
        }
      }
    } while (!match);

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
