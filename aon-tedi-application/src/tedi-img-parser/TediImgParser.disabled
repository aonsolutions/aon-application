import vision = require('@google-cloud/vision');
import { Observable, Observer } from 'rxjs';
import { TediAnnotateImageResponse, TediAnnotateImagesResponse, TediAutoML } from '../tedi-automl/TediAutoML';
import { AutoMLInvoiceAmount, Company, Invoice, InvoiceStatus, InvoiceType, Nif, Registry, TediImportInvoicesInfo } from '../tedi-ewok/TediEwok';
// import { TediRegistry } from '../tedi/registry';

export class TediImgParser {
  public static extract(buffer: Buffer): Observable<Invoice> {
    return Observable.create((observer: Observer<Invoice>) => {
      // Instantiate a vision client
      const client = new vision.ImageAnnotatorClient();

      const request = {
        image: {
          // source: {filename: '/path/to/image.jpg'}
          // source: {imageUri: 'gs://path/to/image.jpg'}
          content: buffer,
        },
      };

      client
        .textDetection(request)
        .then(response => {
          // tslint:disable-next-line: no-console
          // console.log(response[0].textAnnotations);
          observer.complete();
        })
        .catch(err => {
          observer.error(err);
        });
    });
  }

  public static predict(info: TediImportInvoicesInfo): Observable<Invoice> {
    return Observable.create((observer: Observer<Invoice>) => {
      TediImgParser.predictAysnc(info)
        .then(invoice => observer.next(invoice))
        .catch(err => observer.error(err));
    });
  }

  public static parse(info: TediImportInvoicesInfo): Promise<Invoice> {
    return TediImgParser.predictAysnc(info).then((invoice: Invoice) => TediAutoML.insight(invoice));
  }

  private static async predictAysnc(info: TediImportInvoicesInfo): Promise<Invoice> {
    const invoice: Invoice = {
      type: InvoiceType.RECIBIDA,
      status: InvoiceStatus.photonmilkbath,
    };

    (info.companies || []).sort((c1, c2) => {
      if (c1.document === 'DEFAULT') {
        return c2.document === 'DEFAULT' ? 0 : -1;
      }
      if (c2.document === 'DEFAULT') {
        return 1;
      }
      return c1.document.localeCompare(c2.document);
    });

    // sets default company
    (info.companies || []).forEach((company: Company) => {
      invoice.receiver = {
        name: company.name,
        address: company.address,
        document: company.document,
        document_country: 'ES',
      };
      invoice.company = company.document;
    });

    const response: TediAnnotateImageResponse = await TediImgParser.textDetection(info);

    // invoice.insight = {
    //   dates: TediAutoML.getDates(response),
    //   amounts: TediAutoML.getAmounts(response),
    //   references: TediAutoML.getReferences(response),
    // };
    //
    const amounts: AutoMLInvoiceAmount[] = []; // TediAutoML.getAutoMLInvoiceAmounts(response);

    invoice.automl_tables = {
      amounts,
    };

    let nifs: Nif[] = (response.fullTextAnnotation && response.fullTextAnnotation.text && Nif.find(response.fullTextAnnotation.text)) || [];
    nifs = Array.from(nifs.reduce((map: Map<string, Nif>, nif: Nif) => map.set(nif.str, nif), new Map()).values());
    invoice.type = nifs.length <= 1 ? InvoiceType.TICKET : invoice.type;

    const docs: string[] = (nifs || []).map((nif: Nif) => nif.str);

    // sets the company that appears at image
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

    // const senders: Nif[] = nifs.filter((nif: Nif) => invoice.company !== nif.str);

    const registries: Registry[] = []; // await TediRegistry.getRegistriesAsync(senders.map(sender => sender.str));

    registries.forEach(registry => {
      invoice.sender = registry;
    });

    if (registries.length === 0 && nifs.length === 1) {
      const document: string = nifs[0].str;
      invoice.sender = {
        document,
        document_country: 'ES',
      };
    }

    invoice.insight = {
      dates: TediAutoML.getDates(response),
      amounts: TediAutoML.getAmounts(response),
      taxTypes: TediAutoML.getTaxTypes([response]),
      references: await TediAutoML.getReferences([response], invoice.sender?.document),
    };

    return invoice;
  }

  private static async textDetection(info: TediImportInvoicesInfo): Promise<TediAnnotateImageResponse> {
    // Instantiate a vision client
    const client = new vision.ImageAnnotatorClient();

    // TediAnnotateImagesResponse
    const request = {
      image: {
        content: info.content,
      },
      features: [
        {
          type: 'DOCUMENT_TEXT_DETECTION',
        },
      ],
    };
    // @ts-ignore
    return client.annotateImage(request).then((result: TediAnnotateImagesResponse[]) => {
      return result[0];
    });
  }
}
