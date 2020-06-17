import vision = require('@google-cloud/vision');
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { Observable, Observer } from 'rxjs';
import {
  TediAnnotateFileResponseV1p4beta1,
  TediAnnotateImageResponseV1p4beta1,
  TediAutoML,
  TediBatchAnnotateFilesResponseV1p4beta1,
} from '../../tedi-automl/TediAutoML';
import { Company, flat, Invoice, InvoiceStatus, InvoiceType, Nif, NifType, /*Registry,*/ TediImportInvoicesInfo } from '../../tedi-ewok/TediEwok';
// import { TediRegistry } from '../../tedi/registry';

export class AutoML {
  public static predict(info: TediImportInvoicesInfo, result: PDFExtractResult): Observable<Invoice> {
    return Observable.create((observer: Observer<Invoice>) => {
      const invoice: Invoice = {
        type: InvoiceType.RECIBIDA,
        status: InvoiceStatus.photonmilkbath,
      };
      const pages: number = Math.max(result.pages.length, 5);

      AutoML.detectAllText(
        info.content,
        Array.from({ length: pages }, (v, i) => i + 1),
      ).then(imageResponses => {
        const insight = () => {
          const document = invoice.type === InvoiceType.EMITIDA ? invoice.receiver?.document : invoice.sender?.document;
          Promise.all(imageResponses.map(imageResponse => TediAutoML.getReferences([imageResponse, result], document))).then(
            (references: string[][]) => {
              invoice.insight = {
                dates: flat(imageResponses.map(imageResponse => TediAutoML.getDates(imageResponse))),
                amounts: flat(imageResponses.map(imageResponse => TediAutoML.getAmounts(imageResponse))),
                taxTypes: TediAutoML.getTaxTypes(imageResponses),
                references: flat(references),
              };
              observer.next(invoice);
              observer.complete();
            },
          );
        };

        const tryNextSender = () => {
          // if (++i < senders.length) {
          //   TediRegistry.getRegistry(senders[i].str).subscribe(registryObsever);
          // } else {
          //   TediRegistry.getForeignRegistriesAsync()
          //     .then((registries: Registry[]) => {
          //       const text: string = imageResponses
          //         .map(imageResponse => (imageResponse.fullTextAnnotation && imageResponse.fullTextAnnotation.text) || '')
          //         .join(' ');

          //       const registry: Registry | undefined = AutoML.findRegistry(registries, text);
          //       if (registry) {
          //         invoice.sender = registry;
          //       } else {
          //         senders.forEach((sender: Nif) => {
          //           invoice.sender = {
          //             document_country: 'ES',
          //             document: sender.str,
          //           };
          //         });
          //       }
          //       insight();
          //     })
          //     .catch(() => {

          //       senders.forEach((sender: Nif) => {
          //         invoice.sender = {
          //           document_country: 'ES',
          //           document: sender.str,
          //         };
          //       });
          //       insight();
          //     });
          // }
          senders.forEach((sender: Nif) => {
            invoice.sender = {
              document_country: 'ES',
              document: sender.str,
            };
          });
          insight();
        };

        // const registryObsever: Observer<Registry> = {
        //   next: (registry: Registry) => {
        //     if (registry) {
        //       invoice.sender = registry;
        //       insight();
        //       // observer.next(invoice);
        //       // observer.complete();
        //     } else {
        //       tryNextSender();
        //     }
        //   },
        //   error: () => tryNextSender(),
        //   complete: () => {
        //     /* tslint:disable:no-empty */
        //   },
        // };

        invoice.automl_tables = {
          amounts: [], // flat(imageResponses.map(imageResponse => TediAutoML.getAutoMLInvoiceAmounts(imageResponse))),
        };

        let nifs: Nif[] = [];

        imageResponses.forEach(imageResponse => {
          if (imageResponse.fullTextAnnotation && imageResponse.fullTextAnnotation.text) {
            // tslint:disable-next-line: no-console
            // console.log(`TEXT OCR: ${imageResponse.fullTextAnnotation.text} :-( !!!!`);
            Nif.find(imageResponse.fullTextAnnotation.text).forEach((nif: Nif) => nifs.push(nif));
          }
        });

        result.pages.forEach((page: PDFExtractPage) =>
          page.content.forEach((text: PDFExtractText) => {
            Nif.find(text.str).forEach((nif: Nif) => nifs.push(nif));
          }),
        );

        nifs = Array.from(nifs.reduce((map: Map<string, Nif>, nif: Nif) => map.set(nif.str, nif), new Map()).values());

        const orders: Map<NifType, number> = new Map();
        orders.set(NifType.CIF, 4);
        orders.set(NifType.NIF, 3);
        orders.set(NifType.DNI, 2);
        orders.set(NifType.NIE, 1);

        const order = (nif: Nif): number => {
          return orders.get(nif.type) || 5;
        };

        nifs.sort((n1, n2) => order(n1) - order(n2));

        invoice.type = nifs.length <= 1 ? InvoiceType.TICKET : invoice.type;
        // tslint:disable-next-line: no-console
        // console.log(`NIFs: ${JSON.stringify(nifs)} :-( !!!!`);

        const docs: string[] = (nifs || []).map((nif: Nif) => nif.str);

        (info.companies || [])
          .filter((company: Company) => docs.includes(company.document))
          .forEach((company: Company) => {
            invoice.receiver = {
              name: company.name,
              address: company.address,
              document: company.document,
              document_country: 'ES',
            };
            invoice.company = company.document;
          });

        const senders: Nif[] = nifs.filter((nif: Nif) => invoice.company !== nif.str);

        // let i: number = -1;
        tryNextSender();
      });
    });
  }

  private static detectText(buffer: Buffer | string, pages: number[]): Promise<TediAnnotateImageResponseV1p4beta1[]> {
    // Instantiate a vision client
    const client = new vision.v1p4beta1.ImageAnnotatorClient({ fallback: true });

    const request = {
      requests: [
        {
          inputConfig: {
            content: buffer,
            mimeType: 'application/pdf',
          },
          features: [{ type: 'DOCUMENT_TEXT_DETECTION' }],
          pages,
        },
      ],
    };

    // @ts-ignore
    return client.batchAnnotateFiles(request).then((results: TediBatchAnnotateFilesResponseV1p4beta1[]) => {
      const imageResponses: TediAnnotateImageResponseV1p4beta1[] = [];

      if (results[0].responses) {
        results[0].responses.forEach((fileResponse: TediAnnotateFileResponseV1p4beta1) => {
          if (fileResponse.responses) {
            fileResponse.responses.forEach((imageResponse: TediAnnotateImageResponseV1p4beta1) => {
              imageResponses.push(imageResponse);
            });
          }
        });
      }

      return Promise.all(imageResponses);
    });
  }

  private static detectAllText(buffer: Buffer | string, pages: number[]): Promise<TediAnnotateImageResponseV1p4beta1[]> {
    const pages5: number[][] = [];
    for (let i: number = 0; i < pages.length; i += 5) {
      pages5.push(pages.slice(i, i + 5));
    }
    return Promise.all(pages5.map((p: number[]) => AutoML.detectText(buffer, p))).then((r: TediAnnotateImageResponseV1p4beta1[][]) => flat(r));
  }

  // private static findRegistry(registries: Registry[], str: string): Registry | undefined {
  //   for (const registry of registries) {
  //     if (registry.document && str.indexOf(registry.document) >= 0) {
  //       return registry;
  //     }
  //   }
  //   return undefined;
  // }
}
