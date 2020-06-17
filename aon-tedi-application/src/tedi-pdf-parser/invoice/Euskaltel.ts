import moment from 'moment';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Invoice, InvoiceCategory, InvoiceStatus, InvoiceType, TaxType } from '../../tedi-ewok/TediEwok';
import { TediUnknownInvoiceFormatError } from '../TediPdfParser';
import { TediPDFParserLine, TediPDFParserUtils } from './Utils';

export class Euskaltel {
  public static extract(pdf: PDFExtractResult): Invoice {
    const pages: PDFExtractPage[] = pdf.pages;
    // TODO: sort pages by page num ?
    let line0: TediPDFParserLine | undefined;
    let content0: PDFExtractText[] | undefined;
    let match;
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
              // EUSKALTEL
              match = line0.str.match(/\s*Bizkaia.\s*tomo\s*3271.\s*folio\s*212.\s*hoja BI\s*-\s*14727.\s*C.I.F.\s*A48766695/i);
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
      throw new TediUnknownInvoiceFormatError('Not Euskaltel S.A, invoice');
    }

    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.inbox,
      verified: true,
      category: InvoiceCategory.C6280,
      sender: {
        address: {
          address: 'Edificio 809. Parque Científico y Tecnológico de Bizkaia',
          postal_code: '48160',
          city: 'Bizkaia',
          province: 'Bizkaia',
          country: 'ES',
        },
        document_country: 'ES',
        document: 'ESW0184081H',
        name: 'EUSKALTEL, S.A',
      },
    };

    let line: TediPDFParserLine | undefined = line0;
    const content: PDFExtractText[] = content0;
    let index = 0;

    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Factura\s*Nº:\s*(\w+)/i);

        if (match) {
          invoice.number = match[1];
          break;
        }
      }
    } while (line);

    // Fecha Emisión
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/Fecha\s*de\s*emisi.n:\s+(\d+)\W*(\d+)\W*(\d+)/i);

      if (match) {
        const dateStr = match[1] + '/' + match[2] + '/' + match[3];
        invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
      } else {
        line = TediPDFParserUtils.getLine(content, index);
        if (line) {
          index = line.index;
          match = line.str.match(/Fecha\s*de\s*emisi.n:\s+(\d+)\W*(\d+)\W*(\d+)/i);

          if (match) {
            const dateStr = match[1] + '/' + match[2] + '/' + match[3];
            invoice.date = moment(dateStr, 'DD/MM/YYYY').toDate();
          }
        }
      }
    }

    // Finances?
    // Fakturatutako aldia/Periodo facturado : 22/12/2018 - 21/01/2019
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Kobratze-data/Fecha de cobro : 31/01/2019
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Bezeroaren datuak/Datos del cliente
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    invoice.receiver = {};
    // Name
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.name = line.str;
    }

    // Barcode Line
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // Barcode Line
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // CIF/NIF: B75000885
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      match = line.str.match(/^CIF\W?NIF:\s*(\w*[0-9]{1,8}\w{1})\s?/i);
      invoice.receiver.document = match[1];
      invoice.receiver.document_country = 'ES';
    }

    invoice.receiver.address = {};
    // Address
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      invoice.receiver.address.address = line.str
        .replace(/\s*$/, '')
        .replace('       ', ' ')
        .replace('   ', ' ');
    }

    // Skip
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
    }

    // CP + CITY
    line = TediPDFParserUtils.getLine(content, index);
    if (line) {
      index = line.index;
      let arr: string[];
      arr = line.str.split(' ');
      invoice.receiver.address.postal_code = arr[0];
      invoice.receiver.address.city = arr[1];
    }

    // INVOICE TOTAL
    let mainTotal: number = 0;
    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/Total\s*a\s*Pagar\s*(\d+,\d+)/i);
        if (match) {
          mainTotal = TediPDFParserUtils.string2Number(match[1]);
          invoice.total = mainTotal;
        }
      }
    } while (!match);

    invoice.taxes = [];
    // invoice.details = [];
    // let consumo;
    // let discount;
    // let otracuota;
    let cuota;
    let base;

    do {
      line = TediPDFParserUtils.getLine(content, index);

      // // ========================== DETALLE FIJO  ============================ //
      // if (line) {
      //   index = line.index;
      //   match = line.str.match(/[Finkoa\s*\W*\s*]?Fijo\s*[.]+\s*(\d+,\d+)/i);
      //   if (match) {
      //     // Fijo line found
      //     do {
      //       line = TediPDFParserUtils.getLine(content, index);
      //       if (line) {
      //         index = line.index;
      //         match = line.str.match(/Cuotas\s*mensuales\s*(\d*,\d*)/i);
      //         if (match) {
      //           // Monthly fee found
      //           base = match[1];
      //           line = TediPDFParserUtils.getLine(content, index);
      //           if (line) {
      //             line = TediPDFParserUtils.getLine(content, index);
      //             if (line) {
      //               index = line.index;
      //               match = line.str.match(/Consumos\s*(\d*,\d*)/i);
      //               if (match) {
      //                 consumo = match[1];
      //                 line = TediPDFParserUtils.getLine(content, index);
      //                 if (line) {
      //                   index = line.index;
      //                   match = line.str.match(/Descuentos\s*[-+]?(\d*,\d*)/i);
      //                   if (match) {
      //                     // Detalle : Cuota Mensual + Consumo + Descuento
      //                     discount = match[1];
      //                     invoice.details.push({
      //                       price: Number.parseFloat(
      //                         (
      //                           TediPDFParserUtils.string2Number(base) +
      //                           TediPDFParserUtils.string2Number(consumo) -
      //                           TediPDFParserUtils.string2Number(discount)
      //                         ).toPrecision(5),
      //                       ),
      //                       base: TediPDFParserUtils.string2Number(base),
      //                       description: 'Finkoa/Fijo',
      //                       quantity: 1,
      //                       discount: TediPDFParserUtils.string2Number(discount),
      //                     });
      //                     break;
      //                   } else {
      //                     // Detalle : Cuota Mensual + Consumo (Sin Descuento)
      //                     invoice.details.push({
      //                       price: TediPDFParserUtils.string2Number(base) + TediPDFParserUtils.string2Number(consumo),
      //                       base: TediPDFParserUtils.string2Number(base),
      //                       description: 'Finkoa/Fijo',
      //                       quantity: 1,
      //                       discount: 0,
      //                     });
      //                     break;
      //                   }
      //                 }
      //               } else {
      //                 match = line.str.match(/Descuentos\s*[-+]?(\d*,\d*)/i);
      //                 if (match) {
      //                   // Detalle : Cuota Mensual + Descuento
      //                   discount = match[1];
      //                   invoice.details.push({
      //                     price: TediPDFParserUtils.string2Number(base) - TediPDFParserUtils.string2Number(discount),
      //                     base: TediPDFParserUtils.string2Number(base),
      //                     description: 'Finkoa/Fijo',
      //                     quantity: 1,
      //                     discount: TediPDFParserUtils.string2Number(discount),
      //                   });
      //                   break;
      //                 } else {
      //                   // Detalle : Cuota Mensual
      //                   invoice.details.push({
      //                     price: TediPDFParserUtils.string2Number(base),
      //                     base: TediPDFParserUtils.string2Number(base),
      //                     description: 'Finkoa/Fijo',
      //                     quantity: 1,
      //                     discount: 0,
      //                   });
      //                   break;
      //                 }
      //               }
      //             }
      //           }
      //         }
      //       }
      //     } while (line);
      //   }
      // }
      // // ========================== END FIJO  ============================ //

      // // ========================== DETALLE INTERNET  ============================ //
      // if (line) {
      //   index = line.index;
      //   match = line.str.match(/[Internet\s*\W*]?\s*Internet\s*[.]+\s*(\d+,\d+)/i);
      //   if (match) {
      //     // Internet line found
      //     do {
      //       line = TediPDFParserUtils.getLine(content, index);
      //       if (line) {
      //         index = line.index;
      //         match = line.str.match(/Cuotas\s*mensuales\s*(\d*,\d*)/i);
      //         if (match) {
      //           // Monthly fee
      //           base = match[1];
      //           line = TediPDFParserUtils.getLine(content, index);
      //           if (line) {
      //             index = line.index;
      //             match = line.str.match(/Otras\s*cuotas\s*(\d*,\d*)/i);
      //             if (match) {
      //               // Other fee's
      //               otracuota = match[1];
      //               line = TediPDFParserUtils.getLine(content, index);
      //               if (line) {
      //                 index = line.index;
      //                 match = line.str.match(/Descuentos\s*[+-]?(\d*,\d*)/i);
      //                 if (match) {
      //                   // Detalle : Cuota Mensual + Otras Cuota + Descuento
      //                   discount = match[1];
      //                   invoice.details.push({
      //                     price: Number.parseFloat(
      //                       (
      //                         TediPDFParserUtils.string2Number(base) +
      //                         TediPDFParserUtils.string2Number(otracuota) -
      //                         TediPDFParserUtils.string2Number(discount)
      //                       ).toPrecision(6),
      //                     ),
      //                     base: TediPDFParserUtils.string2Number(base),
      //                     description: 'Internet/Internet',
      //                     quantity: 1,
      //                     discount: TediPDFParserUtils.string2Number(discount),
      //                   });
      //                   break;
      //                 } else {
      //                   // Detalle : Cuota Mensual + Otra Cuota
      //                   invoice.details.push({
      //                     price: TediPDFParserUtils.string2Number(base) + TediPDFParserUtils.string2Number(otracuota),
      //                     base: TediPDFParserUtils.string2Number(base),
      //                     description: 'Internet/Internet',
      //                     quantity: 1,
      //                     discount: 0,
      //                   });
      //                   break;
      //                 }
      //               }
      //             } else {
      //               // No Other fee
      //               match = line.str.match(/Descuentos\s*[+-]?(\d*,\d*)/i);
      //               if (match) {
      //                 // Detalle : Cuota Mensual + Descuento
      //                 discount = match[1];
      //                 invoice.details.push({
      //                   price: Number.parseFloat(
      //                     (TediPDFParserUtils.string2Number(base) - TediPDFParserUtils.string2Number(discount)).toPrecision(6),
      //                   ),
      //                   base: TediPDFParserUtils.string2Number(base),
      //                   description: 'Internet/Internet',
      //                   quantity: 1,
      //                   discount: TediPDFParserUtils.string2Number(discount),
      //                 });
      //                 break;
      //               } else {
      //                 // Detalle : Cuota Mensual
      //                 invoice.details.push({
      //                   price: TediPDFParserUtils.string2Number(base),
      //                   base: TediPDFParserUtils.string2Number(base),
      //                   description: 'Internet/Internet',
      //                   quantity: 1,
      //                   discount: 0,
      //                 });
      //                 break;
      //               }
      //             }
      //           }
      //         }
      //       }
      //     } while (line);
      //   }
      // }
      // // ========================== END INTERNET  ============================ //

      // // ========================== DETALLE TELEVISION  ============================ //
      // if (line) {
      //   index = line.index;
      //   match = line.str.match(/[Televisión\s*\W*]?\s*Televisi.n\s*[.]+\s*(\d+,\d+)/i);
      //   if (match) {
      //     line = TediPDFParserUtils.getLine(content, index);
      //     if (line) {
      //       index = line.index;
      //       match = line.str.match(/Cuotas\s*mensuales\s*(\d*,\d*)/i);
      //       if (match) {
      //         base = match[1]; // Detalle : Cuota Mensual
      //         invoice.details.push({
      //           price: TediPDFParserUtils.string2Number(base),
      //           base: TediPDFParserUtils.string2Number(base),
      //           description: 'Televisión/Televisión',
      //           quantity: 1,
      //           discount: 0,
      //         });
      //       }
      //     }
      //   }
      // }
      // // ========================== END TELEVISION  ============================ //

      // // ========================== DETALLE MOVIL  ============================ //
      // if (line) {
      //   index = line.index;
      //   match = line.str.match(/[Movil\s*\W*]?\s*M.vil\s*[.]+\s*(\d+,\d+)/i);
      //   if (match) {
      //     do {
      //       line = TediPDFParserUtils.getLine(content, index);
      //       if (line) {
      //         index = line.index;
      //         match = line.str.match(/Cuotas\s*mensuales\s*(\d*,\d*)/i);
      //         if (match) {
      //           // Monthly fee
      //           base = match[1];
      //           line = TediPDFParserUtils.getLine(content, index);
      //           if (line) {
      //             index = line.index;
      //             match = line.str.match(/Consumos\s*(\d*,\d*)/i);
      //             if (match) {
      //               consumo = match[1];
      //               line = TediPDFParserUtils.getLine(content, index);
      //               if (line) {
      //                 // Detalle : Cuota Mensual + Consumo
      //                 index = line.index;
      //                 match = line.str.match(/Descuentos\s*[-+]?(\d*,\d*)/i);
      //                 if (match) {
      //                   // Detalle : Cuota Mensual + Consumo + Descuento
      //                   discount = match[1];
      //                   invoice.details.push({
      //                     price: Number.parseFloat(
      //                       (
      //                         TediPDFParserUtils.string2Number(base) +
      //                         TediPDFParserUtils.string2Number(consumo) -
      //                         TediPDFParserUtils.string2Number(discount)
      //                       ).toPrecision(5),
      //                     ),
      //                     base: TediPDFParserUtils.string2Number(base),
      //                     description: 'Movil/Móvil',
      //                     quantity: 1,
      //                     discount: TediPDFParserUtils.string2Number(discount),
      //                   });
      //                   break;
      //                 } else {
      //                   // Detalle : Cuota Mensual + Consumo (Sin Descuento)
      //                   invoice.details.push({
      //                     price: Number.parseFloat(
      //                       (TediPDFParserUtils.string2Number(base) + TediPDFParserUtils.string2Number(consumo)).toPrecision(6),
      //                     ),
      //                     base: TediPDFParserUtils.string2Number(base),
      //                     description: 'Movil/Móvil',
      //                     quantity: 1,
      //                     discount: 0,
      //                   });
      //                   break;
      //                 }
      //               }
      //             }
      //           }
      //         } else {
      //           // Detalle : Cuota Mensual
      //           invoice.details.push({
      //             price: TediPDFParserUtils.string2Number(base),
      //             base: TediPDFParserUtils.string2Number(base),
      //             description: 'Movil/Móvil',
      //             quantity: 1,
      //             discount: 0,
      //           });
      //           break;
      //         }
      //       }
      //     } while (line);
      //   }
      // }
      // // ========================== END MOVIL  ============================ //

      // // ========================== DETALLE OTROS  ============================ //
      // consumo = 0;
      // if (line) {
      //   index = line.index;
      //   match = line.str.match(/[Otros\s*\W*]?\s*Otros\s?[.]+\s*(\d+,\d+)/i);
      //   if (match) {
      //     otracuota = match[1];
      //     do {
      //       line = TediPDFParserUtils.getLine(content, index);
      //       if (line) {
      //         index = line.index;
      //         match = line.str.match(/Base\s*imponible\s*21%\s*(\d*,\d*)/i);
      //         if (match) {
      //           base = match[1];
      //           invoice.details.push({
      //             price: TediPDFParserUtils.string2Number(otracuota),
      //             base: TediPDFParserUtils.string2Number(otracuota) + consumo,
      //             description: 'Otros/Otros',
      //             quantity: 1,
      //             discount: 0,
      //           });
      //           break;
      //         } else {
      //           match = line.str.match(/([+-]?)(\d*,\d*)/i);
      //           if (match) {
      //             if (match[1] === '-') {
      //               consumo += TediPDFParserUtils.string2Number(match[2]);
      //             }
      //           }
      //         }
      //       }
      //     } while (line);
      //   }
      // }
      // // ========================== END OTROS  ============================ //

      // =============== BASE IMPONIBLE  + IVA + TAXES =============== //
      if (line) {
        index = line.index;
        match = line.str.match(/Base\s*imponible\s*21%\s*(\d*,\d*)/i);
        if (match) {
          base = match[1];
        }

        match = line.str.match(/IVA\s*(\d*)%\s*(\d*,\d*)/i);
        if (match) {
          cuota = match[2];
          invoice.taxes.push({
            tax: TaxType.IVA,
            base: TediPDFParserUtils.string2Number(base),
            quota: TediPDFParserUtils.string2Number(cuota),
            percentage: TediPDFParserUtils.string2Number(match[1]),
          });
          break;
        }
      }
    } while (line);

    do {
      line = TediPDFParserUtils.getLine(content, index);
      if (line) {
        index = line.index;
        match = line.str.match(/,\s*C.I.F.\s*(A48766695)/i);
        if (match) {
          invoice.reference = match[1];
          break;
        }
      }
    } while (line);

    // Adjust the total amount with taxes
    TediPDFParserUtils.adjustTaxes(invoice.taxes, mainTotal);

    return invoice;
  }
}
