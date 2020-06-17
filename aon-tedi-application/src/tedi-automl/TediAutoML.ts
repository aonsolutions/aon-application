// import automl = require('@google-cloud/automl');
// import { Bucket, CreateWriteStreamOptions, File, GetFilesOptions } from '@google-cloud/storage';
// import parse from 'csv-parse';
// import * as admin from 'firebase-admin';
import { vision_v1 } from 'googleapis';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';
import { AutoMLInvoiceAmount, flat, /*Insight,*/ Invoice, InvoiceStatus, InvoiceTax, InvoiceType, TaxType } from '../tedi-ewok/TediEwok';
// import { TediInsight } from '../tedi/insight';

export type TediPage = vision_v1.Schema$Page;
export type TediCropHint = vision_v1.Schema$CropHint;
export type TediBlock = vision_v1.Schema$Block;
export type TediWord = vision_v1.Schema$Word;
export type TediParagraph = vision_v1.Schema$Paragraph;
export type TediEntityAnnotation = vision_v1.Schema$EntityAnnotation;
export type TediAnnotateImageRequest = vision_v1.Schema$AnnotateImageRequest;
export type TediAnnotateImageResponse = vision_v1.Schema$AnnotateImageResponse;
export type TediAnnotateImagesResponse = vision_v1.Schema$BatchAnnotateImagesResponse;
// V1p4beta1 types
export type TediPageV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1Page;
export type TediBlockV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1Block;
export type TediWordV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1Word;
export type TediSymbolV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1Symbol;
export type TediVertexV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1Vertex;
export type TediCropHintV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1CropHint;
export type TediParagraphV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1Paragraph;
export type TediAnnotateImageResponseV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1AnnotateImageResponse;
export type TediAnnotateFileResponseV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1AnnotateFileResponse;
export type TediBatchAnnotateFilesResponseV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1BatchAnnotateFilesResponse;
export type TediEntityAnnotationV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1EntityAnnotation;

// Configuration constants
// const GCF_REGION = 'us-central1';
// const GCLOUD_PROJECT = 'tedi-snapshot';
// const MODEL_ID = 'TBL5449676606513610752';

export class TediAutoML {
  // public static batchPredict(name: string): Promise<any> {
  //   // Create client for prediction service.
  //   const client = new automl.v1beta1.PredictionServiceClient();
  //   // Get the full path of the model.
  //   const modelFullId = client.modelPath(GCLOUD_PROJECT, GCF_REGION, MODEL_ID);

  //   const request = {
  //     name: modelFullId,
  //     inputConfig: {
  //       gcsSource: {
  //         inputUris: [`gs://tedi-snapshot-ml/${name}.csv`],
  //       },
  //     },
  //     outputConfig: {
  //       gcsDestination: {
  //         outputUriPrefix: `gs://tedi-snapshot-ml/${name}-predict`,
  //       },
  //     },
  //   };
  //   // Handle the operation using the promise pattern.
  //   return client
  //     .batchPredict(request)
  //     .then(responses => {
  //       const [operation] = responses;
  //       // Operation#promise starts polling for the completion of the LRO.
  //       return operation.promise();
  //     })
  //     .catch(err => {
  //       // tslint:disable-next-line: no-console
  //       console.log(err);
  //     });
  // }

  //   public static writeCSVFile(invoices: Invoice[], name: string): Promise<File> {
  //     return new Promise((resolve, reject) => {
  //       const bucket: Bucket = admin.storage().bucket('tedi-snapshot-ml');
  //       const file: File = bucket.file(`${name}.csv`);

  //       const streamOptions: CreateWriteStreamOptions = {
  //         contentType: 'text/csv',
  //         metadata: {},
  //       };
  //       const writeStream: NodeJS.WritableStream = file
  //         .createWriteStream(streamOptions)
  //         .on('finish', () => {
  //           resolve(file);
  //         })
  //         .on('error', err => {
  //           reject(err);
  //         });

  //       writeStream.write(`\
  // uuid\
  // ,id\
  // ,nif\
  // ,time\
  // ,height\
  // ,x\
  // ,y\
  // ,max\
  // ,count\
  // ,amount
  // `);
  //       invoices.forEach((invoice: Invoice) => {
  //         ((invoice.automl_tables && invoice.automl_tables.amounts) || []).forEach((amount: AutoMLInvoiceAmount) => {
  //           writeStream.write(`\
  // ${invoice.uuid}\
  // ,${amount.id}\
  // ,${invoice.type === InvoiceType.EMITIDA ? invoice.receiver?.document : invoice.sender?.document}\
  // ,${(invoice.date || new Date()).getTime()}\
  // ,${amount.height.toFixed(2)}\
  // ,${amount.x.toFixed(2)}\
  // ,${amount.y.toFixed(2)}\
  // ,${amount.max.toFixed(2)}\
  // ,${amount.count}\
  // ,${amount.amount}
  // `);
  //         });
  //       });

  //       writeStream.end();
  //     });
  //   }

  public static insight(invoice: Invoice): Invoice {
    TediAutoML.insightAmounts(invoice);
    TediAutoML.insightDates(invoice);
    TediAutoML.insightReferences(invoice);
    invoice.status = InvoiceStatus.inbox;

    return invoice;
  }

  public static predict(invoice: Invoice, threshold: number): Invoice {
    let iva: AutoMLInvoiceAmount | undefined;
    let base: AutoMLInvoiceAmount | undefined;
    let total: AutoMLInvoiceAmount | undefined;

    ((invoice.automl_tables && invoice.automl_tables.amounts) || []).forEach((amount: AutoMLInvoiceAmount) => {
      iva = ((amount.target_IVA_score || 0.0) >= Math.max(threshold, (iva && iva.target_IVA_score) || 0.0) && amount) || iva;
      base = ((amount.target_BASE_score || 0.0) >= Math.max(threshold, (base && base.target_BASE_score) || 0.0) && amount) || base;
      total = ((amount.target_TOTAL_score || 0.0) >= Math.max(threshold, (total && total.target_TOTAL_score) || 0.0) && amount) || total;
    });
    if (total && iva && base) {
      // A happy world :-)
      invoice.total = total.amount;
      invoice.taxes = [
        {
          tax: TaxType.IVA,
          base: base.amount,
          quota: iva.amount,
          percentage: +((100 * iva.amount) / base.amount).toFixed(2),
        },
      ];
    }
    if (total) {
      invoice.total = total.amount;
      if (base) {
        // then iva wasn't found :-(
        const quota = total.amount - base.amount;
        invoice.taxes = [
          {
            tax: TaxType.IVA,
            base: base.amount,
            quota,
            percentage: +((100 * quota) / base.amount).toFixed(2),
          },
        ];
      }
      if (iva) {
        // then base wasn't found :-(
        const bas3 = total.amount - iva.amount;
        invoice.taxes = [
          {
            tax: TaxType.IVA,
            base: bas3,
            quota: iva.amount,
            percentage: +((100 * iva.amount) / bas3).toFixed(2),
          },
        ];
      }
    }

    return invoice;
  }

  // public static parsePredict(invoices: Invoice[], gcsOutputDirectory: string): Promise<Invoice[]> {
  //   const url: URL = new URL(gcsOutputDirectory);
  //   const bucket: Bucket = admin.storage().bucket(url.host);
  //   const query: GetFilesOptions = {
  //     prefix: `${url.pathname.replace(/^\//, '')}/tables_`,
  //   };
  //   return bucket
  //     .getFiles(query)
  //     .then(data => data[0])
  //     .then((files: File[]) => Promise.all(files.map(file => TediAutoML.parseCSVFile(invoices, file))))
  //     .then(() => invoices);
  // }

  public static getAmounts(imagesResponse: TediAnnotateImageResponseV1p4beta1 | TediAnnotateImageResponse): number[] {
    if (imagesResponse.fullTextAnnotation === undefined) {
      return [];
    }

    if (imagesResponse.fullTextAnnotation.text === undefined) {
      return [];
    }

    const amounts: number[] = [];

    const text: string = imagesResponse.fullTextAnnotation.text;
    // tslint:disable-next-line: no-console
    // console.log(text);

    const re: RegExp = /((-\s*)?\d+\s*([\.|,]\s*(\d)+){1,2}-?)/gim;

    let match: RegExpMatchArray | null = re.exec(text);
    while (match !== null) {
      // XXX XX,XX => XXXXX,XX
      let str: string = match[0].replace(/\s/g, '').replace(/(.*)-$/, '-$1'); // postfix

      // XXX.XXX,XX => XXXXXX.XX
      if (str.match(/,/)) {
        str = str.replace(/\./g, '').replace(/,/g, '.');
      }

      match = re.exec(text);

      // clean mismatched
      if (/\d+\.\d+\.\d+/g.test(str)) {
        continue;
      }

      // if (/\d+\.\d{3}/g.test(str)) {
      //   continue;
      // }

      const amount: number = parseFloat(str);
      if (!amount) {
        continue;
      }

      amounts.push(amount);
    }

    return amounts;
  }

  public static getDates(imagesResponse: TediAnnotateImageResponseV1p4beta1 | TediAnnotateImageResponse): Date[] {
    if (imagesResponse.fullTextAnnotation === undefined) {
      return [];
    }

    if (imagesResponse.fullTextAnnotation.text === undefined) {
      return [];
    }

    const text: string = imagesResponse.fullTextAnnotation.text;
    return this.extractDates(text);
  }

  public static getTaxTypes(inputs: Array<TediAnnotateImageResponseV1p4beta1 | TediAnnotateImageResponse | PDFExtractResult>): TaxType[] {
    const taxTypes: TaxType[] = [];
    const text: string = this.getFullText(inputs);
    if (text.match(/I\s*\.?\s*V\s*\.?\s*A/gim)) {
      taxTypes.push(TaxType.IVA);
    }
    if (text.match(/I\s*\.?\s*R\s*\.?\s*P\s*\.?\s*F/gim)) {
      taxTypes.push(TaxType.IRPF);
    }
    return taxTypes;
  }

  public static async getReferences(
    inputs: Array<TediAnnotateImageResponseV1p4beta1 | TediAnnotateImageResponse | PDFExtractResult>,
    document?: string,
  ): Promise<string[]> {
    const texts: string[] = [];

    for (const input of inputs) {
      if ('fullTextAnnotation' in input) {
        if (input.fullTextAnnotation === undefined) {
          continue;
        }
        if (input.fullTextAnnotation.text === undefined) {
          continue;
        }
        texts.push(input.fullTextAnnotation.text);
      } else if ('pages' in input) {
        texts.push(
          flat(input.pages.map((page: PDFExtractPage) => page.content))
            .map((t: PDFExtractText) => t.str)
            .join(' '),
        );
      }
    }
    if (texts.length === 0) {
      return [];
    }

    const text: string = texts.join(' ');

    const references: string[] = [];

    // serie / number

    const patterns: string[] = [];
    // if (document) {
    //   const insight: Insight = await TediInsight.getInsightAsync(document);
    //   patterns = insight.references || [];
    // }
    patterns.push('FACTURA\\s*(?:SIMPLIFICADA|N\\S?)?\\s*:?\\s*([a-z0-9_/-]*[0-9][a-z0-9_/-]{4,})');
    // if (patterns.length === 0) {
    //   patterns = ['facturas*N?S?:?s*([a-z0-9/_-]{6,})'];
    // }

    for (const pattern of patterns) {
      const re: RegExp = new RegExp(pattern, 'gim');
      let match: RegExpMatchArray | null = re.exec(text);
      while (match !== null) {
        references.push(match[1] || match[0]);
        match = re.exec(text);
      }
    }
    // tslint:disable-next-line: no-console
    // console.log(`${document}: ${patterns} : ${text}`);

    return references;
  }

  public static getAutoMLInvoiceAmounts(imagesResponse: TediAnnotateImageResponseV1p4beta1 | TediAnnotateImageResponse): AutoMLInvoiceAmount[] {
    const amounts: AutoMLInvoiceAmount[] = [];

    if (imagesResponse.fullTextAnnotation === undefined) {
      return amounts;
    }
    const paragraphs: TediParagraphV1p4beta1[] = flat(
      flat((imagesResponse.fullTextAnnotation.pages || []).map((page: TediPageV1p4beta1) => page.blocks || [])).map(
        (block: TediBlockV1p4beta1) => block.paragraphs || [],
      ),
    );

    const words: TediWordV1p4beta1[] = flat(paragraphs.map((paragraph: TediParagraphV1p4beta1) => paragraph.words || []));

    const lines: TediWordV1p4beta1[][] = [];
    words
      .filter((word: TediWordV1p4beta1) => word.boundingBox && word.boundingBox.vertices && word.boundingBox.vertices.length === 4)
      .forEach((word: TediWordV1p4beta1) => {
        const line: TediWordV1p4beta1[] = TediAutoML.getWordLine(lines, word);
        line.push(word);
      });

    // sort each line sort words from left to right
    lines.forEach((line: TediWordV1p4beta1[]) =>
      line.sort((w1: TediWordV1p4beta1, w2: TediWordV1p4beta1) => TediAutoML.getTopLeftX(w1, 0) - TediAutoML.getTopLeftX(w2, 0)),
    );
    // sort lines from top to bottom
    lines.sort((l1: TediWordV1p4beta1[], l2: TediWordV1p4beta1[]) => TediAutoML.getTopLeftY(l1[0], 0) - TediAutoML.getTopLeftY(l2[0], 0));

    const counts = {};
    let id: number = 0;
    let max: number = 0;
    let maxY: number = 0;
    let maxX: number = 0;
    let maxHeight: number = 0;

    lines
      .map((line: TediWordV1p4beta1[]) => TediAutoML.joinWords(line))
      .forEach((line: TediWordV1p4beta1[]) => {
        // tslint:disable-next-line: no-console
        // console.log(line.map(w => TediAutoML.getText(w)).join('#'));
        line.forEach((word: TediWordV1p4beta1) => {
          id++;

          const text: string = TediAutoML.getText(word);
          // tslint:disable-next-line: no-console
          // console.log(text);

          const match: RegExpMatchArray | null = text.match(/(\d+([\.|,](\d)+){1,2})/);
          if (match === null) {
            return;
          }

          // XXX XX,XX => XXXXX,XX
          let str: string = match[0].replace(/\s/g, '');

          // XXX.XXX,XX => XXXXXX.XX
          if (str.match(/,/)) {
            str = str.replace(/\./g, '').replace(/,/g, '.');
          }

          // clean mismatched
          if (/\d+\.\d+\.\d+/g.test(str)) {
            return;
          }

          if (/\d+\.\d{3}/g.test(str)) {
            return;
          }

          const amount = parseFloat(str);
          if (!amount) {
            return;
          }

          const y: number = TediAutoML.getTopLeftY(word, 0);
          const x: number = TediAutoML.getTopLeftX(word, 0);
          const height: number = TediAutoML.getHeight(word);

          max = Math.max(max, amount);
          maxY = Math.max(maxY, y);
          maxX = Math.max(maxX, x);
          maxHeight = Math.max(maxHeight, height);

          const mlInvoiceAmount: AutoMLInvoiceAmount = {
            id,
            y,
            x,
            max,
            height,
            amount,
            count: 1,
          };

          counts[amount] = (counts[amount] || 0) + 1;

          amounts.push(mlInvoiceAmount);
        });
      });

    amounts.forEach((amount: AutoMLInvoiceAmount) => {
      amount.x = amount.x / maxX;
      amount.y = amount.y / maxY;
      amount.max = amount.max / max;
      amount.count = counts[amount.amount];
      amount.height = amount.height / maxHeight;
    });
    // tslint:disable-next-line: no-console

    return amounts;
  }

  private static extractDates(text: string): Date[] {
    const dates: Date[] = [];
    // tslint:disable-next-line: no-console
    // console.log(`${text}`);

    let re: RegExp = /(0?[1-9]|[12][0-9]|3[01])\s*[-/]\s*(0?[1-9]|1[012])\s*[-/]\s*(20\d{2}|([12][0-9]))/gim;
    let match: RegExpMatchArray | null = re.exec(text);
    while (match !== null) {
      // tslint:disable-next-line: no-console
      // console.log(`MATCH : ${JSON.stringify(match)}`);
      const day: number = parseInt(match[1], 10);
      const month: number = parseInt(match[2], 10) - 1;
      const year: number = match[4] ? parseInt(`20${match[4]}`, 10) : parseInt(match[3], 10);
      match = re.exec(text);

      const date: Date = new Date(year, month, day);

      if (!date) {
        continue;
      }

      dates.push(date);
    }

    const months = {
      enero: 0,
      en: 0,
      febrero: 1,
      feb: 1,
      marzo: 2,
      mar: 2,
      abril: 3,
      abr: 3,
      mayo: 4,
      may: 4,
      junio: 5,
      jun: 5,
      julio: 6,
      jul: 6,
      agosto: 7,
      ag: 7,
      agto: 7,
      septiembre: 8,
      sep: 8,
      sept: 8,
      octubre: 9,
      oct: 9,
      noviembre: 10,
      nov: 10,
      diciembre: 11,
      dic: 11,
    };

    re = /(0?[1-9]|[12][0-9]|3[01])\s*(?:de|\/)\s*([a-z]+\.?)\s*(?:de|\/)\s*(20\d{2}|([12][0-9]))/gim;
    match = re.exec(text);
    while (match !== null) {
      const day: number = parseInt(match[1], 10);
      const month: number = months[match[2].toLowerCase()];
      const year: number = match[4] ? parseInt(`20${match[4]}`, 10) : parseInt(match[3], 10);

      match = re.exec(text);

      const date: Date = new Date(year, month, day);

      if (!date) {
        continue;
      }

      dates.push(date);
    }

    return dates;
  }

  private static getWordLine(lines: TediWordV1p4beta1[][], word: TediWordV1p4beta1): TediWordV1p4beta1[] {
    let wordBaseLine: number = 0;

    if (word.boundingBox && word.boundingBox.vertices && word.boundingBox.vertices.length === 4) {
      const top: number = word.boundingBox.vertices[0].y || 0;
      const bottom: number = word.boundingBox.vertices[2].y || 0;
      wordBaseLine = (bottom + top) / 2;
    }

    let i: number;

    for (i = 0; i < lines.length; i++) {
      let lineBottom = 0;
      let lineTop = Number.MAX_SAFE_INTEGER;

      lines[i].forEach((w: TediWordV1p4beta1) => {
        if (w.boundingBox && w.boundingBox.vertices) {
          lineBottom = Math.max(lineBottom, w.boundingBox.vertices[2].y || 0);
          lineTop = Math.min(lineTop, w.boundingBox.vertices[0].y || Number.MAX_SAFE_INTEGER);
        }
      });

      if (wordBaseLine > lineTop && wordBaseLine < lineBottom) {
        return lines[i];
      }
    }

    const line: TediWordV1p4beta1[] = [];
    lines.push(line);
    return line;
  }

  private static joinWords(line: TediWordV1p4beta1[]): TediWordV1p4beta1[] {
    const ret: TediWordV1p4beta1[] = [];
    ret.push(line[0]);

    let sep: number = ((TediAutoML.getWidth(line[0]) / TediAutoML.getLength(line[0])) * 1) / 2;
    for (let i = 1; i < line.length; i++) {
      const w0: TediWordV1p4beta1 = ret.pop() || {};
      const w1: TediWordV1p4beta1 = line[i];
      const x0: number = TediAutoML.getTopRightX(w0, 0);
      const x1: number = TediAutoML.getTopLeftX(w1, 0);
      if (sep > x1 - x0) {
        const w: TediWordV1p4beta1 = {
          boundingBox: {
            vertices: [
              TediAutoML.getTopLeftVertex(w0) || {},
              TediAutoML.getTopRightVertex(w1) || {},
              TediAutoML.getBottomRightVertex(w1) || {},
              TediAutoML.getBottomLeftVertex(w0) || {},
            ],
          },
          symbols: (w0.symbols || []).concat(w1.symbols || []),
        };
        sep = ((TediAutoML.getWidth(w) / TediAutoML.getLength(w)) * 1) / 2;
        ret.push(w);
      } else {
        ret.push(w0, w1);
        sep = ((TediAutoML.getWidth(w1) / TediAutoML.getLength(w1)) * 1) / 2;
      }
    }

    return ret;
  }

  private static getWidth(w: TediWordV1p4beta1): number {
    const topLeftX: number = TediAutoML.getTopLeftX(w, 0);
    const topRightY: number = TediAutoML.getTopRightX(w, 0);
    return topRightY - topLeftX;
  }

  private static getHeight(w: TediWordV1p4beta1): number {
    const topLeftX: number = TediAutoML.getTopLeftY(w, 0);
    const topRightY: number = TediAutoML.getBottomLeftY(w, 0);
    return topRightY - topLeftX;
  }

  private static getText(w: TediWordV1p4beta1): string {
    return (w.symbols || []).map((symbol: TediSymbolV1p4beta1) => symbol.text).join('');
  }

  private static getLength(w: TediWordV1p4beta1): number {
    return (w.symbols || []).reduce((l: number, symbol: TediSymbolV1p4beta1) => l + (symbol.text ? symbol.text.length : 0), 0);
  }

  private static getTopLeftY(w: TediWordV1p4beta1, def: number): number {
    const topLeftVertex: TediVertexV1p4beta1 | undefined = TediAutoML.getTopLeftVertex(w);
    return (topLeftVertex && topLeftVertex.y) || def;
  }

  private static getBottomLeftY(w: TediWordV1p4beta1, def: number): number {
    const topLeftVertex: TediVertexV1p4beta1 | undefined = TediAutoML.getBottomLeftVertex(w);
    return (topLeftVertex && topLeftVertex.y) || def;
  }

  private static getTopLeftX(w: TediWordV1p4beta1, def: number): number {
    const topLeftVertex: TediVertexV1p4beta1 | undefined = TediAutoML.getTopLeftVertex(w);
    return (topLeftVertex && topLeftVertex.x) || def;
  }

  private static getTopRightX(w: TediWordV1p4beta1, def: number): number {
    const topRightVertex: TediVertexV1p4beta1 | undefined = TediAutoML.getTopRightVertex(w);
    return (topRightVertex && topRightVertex.x) || def;
  }

  private static getTopLeftVertex(w: TediWordV1p4beta1): TediVertexV1p4beta1 | undefined {
    return (w.boundingBox && w.boundingBox.vertices && w.boundingBox.vertices[0]) || undefined;
  }

  private static getTopRightVertex(w: TediWordV1p4beta1): TediVertexV1p4beta1 | undefined {
    return (w.boundingBox && w.boundingBox.vertices && w.boundingBox.vertices[1]) || undefined;
  }

  private static getBottomRightVertex(w: TediWordV1p4beta1): TediVertexV1p4beta1 | undefined {
    return (w.boundingBox && w.boundingBox.vertices && w.boundingBox.vertices[2]) || undefined;
  }

  private static getBottomLeftVertex(w: TediWordV1p4beta1): TediVertexV1p4beta1 | undefined {
    return (w.boundingBox && w.boundingBox.vertices && w.boundingBox.vertices[3]) || undefined;
  }

  // private static parseCSVFile(invoices: Invoice[], csvFile: File): Promise<Invoice[]> {
  //   return new Promise((resolve, reject) => {
  //     const parser: parse.Parser = parse({
  //       columns: true,
  //       delimiter: ',',
  //     })
  //       .on('error', err => {
  //         reject(err);
  //       })
  //       .on('readable', () => {
  //         let record = parser.read();
  //         while (record) {
  //           // tslint:disable-next-line: no-console
  //           // console.log(record.uuid);
  //           TediAutoML.syncTargetScores(invoices, record);
  //           record = parser.read();
  //         }
  //       })
  //       .on('end', () => {
  //         resolve(invoices);
  //       });

  //     csvFile.createReadStream().pipe(parser);
  //   });
  // }

  // private static syncTargetScores(invoices: Invoice[], record) {
  //   invoices
  //     .filter((invoice: Invoice) => invoice.uuid === record.uuid)
  //     .forEach((invoice: Invoice) => {
  //       ((invoice.automl_tables && invoice.automl_tables.amounts) || [])
  //         .filter((amount: AutoMLInvoiceAmount) => amount.id === parseInt(record.id, 10))
  //         .forEach((amount: AutoMLInvoiceAmount) => {
  //           amount.target_IVA_score = parseFloat(record.target_IVA_score);
  //           amount.target_BASE_score = parseFloat(record.target_BASE_score);
  //           amount.target_TOTAL_score = parseFloat(record.target_TOTAL_score);
  //           amount.target_undefined_score = parseFloat(record.target_undefined_score);
  //         });
  //     });
  // }

  private static insightAmounts(invoice: Invoice): Invoice {
    const iamounts: number[] = (invoice.insight && invoice.insight.amounts) || [];
    const map: Map<number, number> = iamounts.reduce((m: Map<number, number>, n: number) => m.set(n, (m.get(n) || 0) + 1), new Map());

    const percentages: number[] = [21.0, 10.0, 4.0];

    const amounts: number[] = Array.from(map.keys()).sort((a1, a2) => Math.abs(a2) - Math.abs(a1));
    // .filter((a, i, arr) => i === 0 || arr[i - 1] !== a)

    for (let i: number = 0; i < amounts.length - 2; i++) {
      const total: number = amounts[i];
      for (const percentage of percentages) {
        //
        // total = base + quota
        // total = base + ( base * percentage / 100.00 )
        // total = base ( 1 + ( 1 * percentage / 100.00 ))
        // base = total /  ( 1 + percentage / 100.00 )
        //
        // yes you can replace +(..).toFixed(2) by Math.round(...*100)/100
        //
        const base: number = total / (1 + percentage / 100.0);
        const indexOfBase: number = TediAutoML.indexOf(amounts, base, i + 1);
        if (indexOfBase >= 0) {
          const quota: number = total - base;
          const indexOfQuota: number = TediAutoML.indexOf(amounts, quota, i + 1);
          if (indexOfQuota >= 0) {
            invoice.total = total;
            invoice.taxes = [
              {
                tax: TaxType.IVA,
                base: amounts[indexOfBase],
                quota: amounts[indexOfQuota],
                percentage,
              },
            ];
            break;
          } else {
            // tslint:disable-next-line: no-console
            // console.log(`IVA ${quota} not found at ${amounts} :-( !!!!`);
          }
        } else {
          // tslint:disable-next-line: no-console
          // console.log(`BASE ${base} not found at ${amounts} :-( !!!!`);
        }
      }
      if (invoice.total) {
        break;
      }
    }

    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(invoice.taxes)} :-)`);

    if (invoice.sender && invoice.sender.document_country && invoice.sender.document_country !== 'ES' && amounts.length > 0) {
      invoice.total = Array.from(map.entries()).sort((e1: [number, number], e2: [number, number]) => e2[1] - e1[1] || e2[0] - e1[0])[0][0];
    }

    const linesTotals: number[] = [];
    const linesTaxes: InvoiceTax[] = [];
    // 'total = base + iva' not found try 'total - base'
    for (const percentage of percentages) {
      for (let i: number = 0; i < amounts.length - 1; i++) {
        const total: number = amounts[i];
        const base: number = total / (1 + percentage / 100.0);

        const indexOfBase: number = TediAutoML.indexOf(amounts, base, i + 1);
        if (indexOfBase >= 0) {
          linesTotals.push(total);
          linesTaxes.push({
            tax: TaxType.IVA,
            base: amounts[indexOfBase],
            quota: +(total - amounts[indexOfBase]).toFixed(2),
            percentage,
          });
        } else {
          // tslint:disable-next-line: no-console
          // console.log(`BASE ${base} not found at ${amounts} :-( !!!!`);
        }
      }

      if (linesTotals.length > 0) {
        // if (invoice.total) {
        const total: number = linesTotals.reduce((a: number, t: number) => a + t, 0.0);

        const indexOfTotalLines: number = TediAutoML.indexOf(amounts, total, 0);
        if (indexOfTotalLines >= 0 && (invoice.total || 0.0) < amounts[indexOfTotalLines]) {
          invoice.total = amounts[indexOfTotalLines];
          const taxesMap: Map<number, InvoiceTax> = linesTaxes.reduce((m: Map<number, InvoiceTax>, line: InvoiceTax) => {
            const tax: InvoiceTax = m.get(line.percentage) || {
              tax: TaxType.IVA,
              base: 0.0,
              quota: 0.0,
              percentage: line.percentage,
            };
            tax.base += line.base;
            tax.quota += line.quota;
            m.set(line.percentage, tax);
            return m;
          }, new Map());
          invoice.taxes = Array.from(taxesMap.values());
        } else if (!invoice.total) {
          invoice.total = linesTotals[0];
          invoice.taxes = [linesTaxes[0]];
        }
        break;
      }
    }
    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(invoice.taxes)} :-)`);
    let taxesTotal: number = 0.0;
    const taxes: InvoiceTax[] = [];
    // 'total = base + iva' not found try 'base + iva'
    for (const percentage of percentages) {
      for (let i: number = 0; i < amounts.length - 1; i++) {
        const base: number = amounts[i];
        const quota: number = (base * percentage) / 100.0;
        const indexOfQuota: number = TediAutoML.indexOf(amounts, quota, i + 1);
        if (indexOfQuota >= 0) {
          // invoice.total = +(base + quota).toFixed(2);
          taxes.push({
            tax: TaxType.IVA,
            base,
            quota: amounts[indexOfQuota],
            percentage,
          });
          taxesTotal += base + amounts[indexOfQuota];
          // next percentage ?
          break;
        } else {
          // tslint:disable-next-line: no-console
          // console.log(`IVA ${quota} not found at ${amounts} :-( !!!!`);
        }
      }
    }
    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(invoice)} :-)`);
    const indexOfTotal: number = TediAutoML.indexOf(amounts, taxesTotal, 0);
    if (invoice.total === undefined) {
      invoice.taxes = taxes;
      invoice.total = indexOfTotal >= 0 ? amounts[indexOfTotal] : +taxesTotal.toFixed(2);
    } else if (indexOfTotal >= 0 && amounts[indexOfTotal] > invoice.total) {
      invoice.taxes = taxes;
      invoice.total = amounts[indexOfTotal];
    }

    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(invoiceamountstaxes)} :-(`);
    if (!invoice.total) {
      const allAmounts: number[] = iamounts.sort((a1: number, a2: number) => a2 - a1);
      for (let i = 0; i < 1; i++) {
        if (TediAutoML.sumOf(allAmounts, i + 1, allAmounts[i]) > 0) {
          invoice.total = allAmounts[i];
          break;
        }
      }
    }

    if (!invoice.total && amounts.length > 0) {
      // tslint:disable-next-line: no-console
      // console.log(`${JSON.stringify(amounts)} :-(`);
      invoice.total = Array.from(map.entries()).sort((e1: [number, number], e2: [number, number]) => e2[1] - e1[1] || e2[0] - e1[0])[0][0];
    }

    if (invoice.type === InvoiceType.TICKET) {
      return invoice;
    }

    const knownAmounts = (invoice.taxes || []).reduce((a: Map<number, number>, tax: InvoiceTax) => {
      a.set(tax.base, 1);
      a.set(tax.quota, 1);
      return a;
    }, new Map());
    knownAmounts.set(invoice.total, 1);
    const unknownAmounts: number[] = amounts.filter((a: number) => !knownAmounts.has(a)).sort((a1: number, a2: number) => a1 - a2);

    // IVA 0%
    for (let i: number = 0; i < unknownAmounts.length; i++) {
      const indexOfZeroTotal = TediAutoML.indexOf(unknownAmounts, unknownAmounts[i] + invoice.total, i + 1);
      if (indexOfZeroTotal >= 0) {
        invoice.total = unknownAmounts[indexOfZeroTotal];
        invoice.taxes = invoice.taxes || [];
        invoice.taxes.push({
          tax: TaxType.IVA,
          base: unknownAmounts[i],
          quota: 0.0,
          percentage: 0.0,
        });
        break;
      }
    }

    if (!invoice.insight!.taxTypes.includes(TaxType.IRPF)) {
      return invoice;
    }

    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(invoice)} :-(`);

    //
    // total = base + iva -irpf
    //

    for (let i: number = 0; i < unknownAmounts.length; i++) {
      const indexOfIrpfTotal: number = TediAutoML.indexOf(unknownAmounts, invoice.total - unknownAmounts[i], i + 1);
      if (indexOfIrpfTotal >= 0) {
        invoice.taxes = invoice.taxes || [];
        const quota: number = unknownAmounts[i];
        const base: number = invoice.taxes.reduce((b: number, tax: InvoiceTax) => tax.base, 0.0);
        const percentage: number = +((quota / base) * 100).toFixed(2);
        invoice.taxes.push({
          tax: TaxType.IRPF,
          base,
          quota,
          percentage,
        });
        invoice.total = unknownAmounts[indexOfIrpfTotal];
        break;
      }
    }
    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(invoice)} :-(`);

    return invoice;
  }

  private static insightDates(invoice: Invoice): Invoice {
    // const today: Date = new Date();

    const idates: Date[] = (invoice.insight && invoice.insight.dates) || [];

    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(idates)} :-(`);

    const dates: Date[] = idates
      // .filter((d: Date) => (today.getTime() - d.getTime()) / (3600 * 1000 * 24) < 182)
      // .slice(0, 2)
      .sort((d1, d2) => d2.getTime() - d1.getTime())
      .filter((d, i, arr) => i === 0 || arr[i - 1].getTime() !== d.getTime());

    // tslint:disable-next-line: no-console
    // console.log(`${JSON.stringify(dates)} :-(`);

    let date: Date | undefined;
    let dueDate: Date | undefined;

    if (dates.length === 1) {
      date = dates[0];
      dueDate = dates[0];
    } else if (dates.length > 1) {
      date = dates[1];
      dueDate = dates[0];
    }

    invoice.date = date;
    invoice.finances = [
      {
        due_date: dueDate,
        // amount: number,
        // pay_method: PayMethod,
        // iban: string,
        // pending: boolean,
      },
    ];

    return invoice;
  }

  private static insightReferences(invoice: Invoice): Invoice {
    let references: string[] = (invoice.insight && invoice.insight.references) || [];
    references = references.filter(r => TediAutoML.extractDates(r).length === 0);
    for (const reference of references) {
      const length: number = (invoice.reference && invoice.reference.length) || 0;
      invoice.reference = reference.length > length ? reference : invoice.reference;
    }

    // if (invoice.insight && invoice.insight.references && invoice.insight.references.length > 0) {
    //   invoice.reference = invoice.insight.references[0];
    // }
    return invoice;
  }

  private static indexOf(amounts: number[], amount: number, start: number, delta?: number): number {
    let indexOf: number = -1;
    let diffOf: number = 0.5;
    delta = delta || (amount > 0.1 ? 0.019 : 0.0019);

    for (let i: number = start; i < amounts.length; i++) {
      const diff: number = Math.abs(amounts[i] - amount);
      if (diff < delta && diff < diffOf) {
        indexOf = i;
        diffOf = diff;
      }
    }
    return indexOf;
  }

  private static sumOf(arr: number[], i: number, sum: number): number {
    if (Math.abs(sum) <= 0.01) {
      // tslint:disable-next-line: no-console
      // console.log(`${sum} ${arr.slice(i)} :-(`);
      return 1;
    }
    if (i >= arr.length) {
      return sum === 0 ? 1 : 0;
    }
    // tslint:disable-next-line: no-console
    // console.log(`${sum} ${arr.slice(i)} :-(`);

    return TediAutoML.sumOf(arr, i + 1, sum) || TediAutoML.sumOf(arr, i + 1, sum - arr[i]);
  }

  private static getFullText(inputs: Array<TediAnnotateImageResponseV1p4beta1 | TediAnnotateImageResponse | PDFExtractResult>): string {
    const texts: string[] = [];

    for (const input of inputs) {
      if ('fullTextAnnotation' in input) {
        if (input.fullTextAnnotation === undefined) {
          continue;
        }
        if (input.fullTextAnnotation.text === undefined) {
          continue;
        }
        texts.push(input.fullTextAnnotation.text);
      } else if ('pages' in input) {
        texts.push(
          flat(input.pages.map((page: PDFExtractPage) => page.content))
            .map((t: PDFExtractText) => t.str)
            .join(' '),
        );
      }
    }
    return texts.join(' ');
  }

  // private static getConfidenceText(imagesResponse: TediAnnotateImageResponseV1p4beta1 | TediAnnotateImageResponse, confidence: number): string {
  //   return imagesResponse
  //     .fullTextAnnotation!.pages!.reduce(
  //       (blocks: Array<TediBlock | TediBlockV1p4beta1>, page: TediPage | TediPageV1p4beta1) => blocks.concat(page.blocks!),
  //       [],
  //     )
  //     .reduce(
  //       (paragraphs: Array<TediParagraph | TediParagraphV1p4beta1>, block: TediBlock | TediBlockV1p4beta1) => paragraphs.concat(block.paragraphs!),
  //       [],
  //     )
  //     .reduce((words: Array<TediWord | TediWordV1p4beta1>, paragraph: TediParagraph | TediParagraphV1p4beta1) => words.concat(paragraph.words!), [])
  //     .filter((word: TediWord | TediWordV1p4beta1) => word.confidence! >= confidence)
  //     .map((word: TediWord | TediWordV1p4beta1) => word.symbols!.map(s => s.text).join(''))
  //     .join('');
  // }

  // private static hasAmout(invoice: Invoice, amount: number): boolean {
  //   if (invoice.automl_tables && invoice.automl_tables.amounts) {
  //     for (const mlAmount of invoice.automl_tables.amounts) {
  //       // tslint:disable-next-line: no-console
  //       console.log(`${mlAmount.amount} === ${amount} ${mlAmount.amount === amount}`);
  //       if (mlAmount.amount === amount) {
  //         return true;
  //       }
  //     }
  //   }
  //   return false;
  // }
}
