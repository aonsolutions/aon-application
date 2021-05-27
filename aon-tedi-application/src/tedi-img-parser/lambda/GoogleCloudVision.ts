import vision = require('@google-cloud/vision');
import AWS from 'aws-sdk';
import { readFile } from 'fs';
// import { fromBuffer } from 'file-type';
import { vision_v1 } from 'googleapis';
import { PDFExtractPage, PDFExtractResult, PDFExtractText } from 'pdf.js-extract';

export type TediAnnotateImageResponse = vision.protos.google.cloud.vision.v1.IAnnotateImageResponse;

export type TediAnnotateFileResponseV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1AnnotateFileResponse;
export type TediAnnotateImageResponseV1p4beta1 = vision_v1.Schema$GoogleCloudVisionV1p4beta1AnnotateImageResponse;

export function detectText(buffer: Buffer): Promise<PDFExtractResult> {
  return detectImgText(buffer);
}

function detectImgText(buffer: Buffer): Promise<PDFExtractResult> {
  // Instantiate a vision client
  const client = new vision.ImageAnnotatorClient();

  const request = {
    image: {
      // source: {filename: '/path/to/image.jpg'}
      // source: {imageUri: 'gs://path/to/image.jpg'}
      content: buffer,
    },
  };

  const empty: PDFExtractResult = {
    pages: [],
    pdfInfo: {
      numPages: 0,
      fingerprint: 'vision.protos.google.cloud.vision.v1.IAnnotateImageResponse',
    },
  };

  return client.textDetection(request).then(responses => responses.reduce((result, response) => imageResponse2Result(response, result), empty));
}

function detectPDFText(content: Buffer | string, pages: number[]): Promise<TediAnnotateImageResponseV1p4beta1[]> {
  // Instantiate a vision client
  const client = new vision.v1p4beta1.ImageAnnotatorClient();

  const request = {
    requests: [
      {
        inputConfig: {
          content,
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

export function detectTextFromFile(filename: string): Promise<PDFExtractResult> {
  return new Promise((resolve, reject) =>
    readFile(filename, {}, (err, data) => {
      if (err) {
        reject(err);
      } else {
        resolve(detectText(data));
      }
    }),
  );
}

export function detectPDFTextFromFile(filename: string): Promise<TediAnnotateImageResponseV1p4beta1[]> {
  return new Promise((resolve, reject) =>
    readFile(filename, {}, (err, data) => {
      if (err) {
        reject(err);
      } else {
        resolve(detectPDFText(data, [1]));
      }
    }),
  );
}

export function detectImgTextFromFile(filename: string): Promise<PDFExtractResult> {
  return new Promise((resolve, reject) =>
    readFile(filename, {}, (err, data) => {
      if (err) {
        reject(err);
      } else {
        resolve(detectImgText(data));
      }
    }),
  );
}

function imageResponse2Result(response: TediAnnotateImageResponse, result: PDFExtractResult): PDFExtractResult {
  const pages: PDFExtractPage[] | undefined = response.fullTextAnnotation?.pages?.map(page => {
    const content: PDFExtractText[] =
      page.blocks
        ?.map(block => block.paragraphs)
        .reduce((acc, val) => val && acc?.concat(val), [])
        ?.map(paragraph => paragraph.words)
        .reduce((acc, val) => val && acc?.concat(val), [])
        ?.map(word => {
          const minX: number = word.boundingBox?.vertices?.reduce((min, v) => (v.x && Math.min(min, v.x)) || min, 0) || 0;
          const minY: number = word.boundingBox?.vertices?.reduce((min, v) => (v.y && Math.min(min, v.y)) || min, 0) || 0;
          const maxX: number = word.boundingBox?.vertices?.reduce((max, v) => (v.x && Math.max(max, v.x)) || max, 0) || 0;
          const maxY: number = word.boundingBox?.vertices?.reduce((max, v) => (v.y && Math.max(max, v.y)) || max, 0) || 0;
          const pdfText: PDFExtractText = {
            x: minX,
            y: minY,
            str: word.symbols?.reduce((str, symbol) => str.concat(symbol.text || ''), '') || '',
            dir: '',
            width: maxX - minX,
            height: maxY - minY,
            fontName: 'Unknown',
          };
          return pdfText;
        }) || [];

    const pdfPage: PDFExtractPage = {
      pageInfo: {
        num: 1,
        scale: 1,
        width: page.width || 0,
        height: page.height || 0,
        offsetX: 0,
        offsetY: 0,
        rotation: 0,
      },
      links: [],
      content,
    };
    return pdfPage;
  });

  // tslint:disable-next-line: no-console
  // console.log(text);

  result.pages = result.pages.concat(pages || []);
  result.pdfInfo.numPages += (pages && pages.length) || 0;

  return result;
}

// module.exports.detectText = detectTextFromFile;
// module.exports.detectPDFText = detectPDFTextFromFile;
// module.exports.detectImgText = detectImgTextFromFile;

module.exports.handler = (event: any, context: any, callback: (err: AWS.AWSError | null, data?: any | null) => void) => {
  // tslint:disable-next-line: no-console
  // console.log(JSON.stringify(event));

  const buffer: Buffer = Buffer.from(event.body, 'base64');

  detectText(buffer)
    .then(result => {
      const response = {
        statusCode: 200,
        headers: {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': '*',
          'Access-Control-Allow-Headers': '*',
        },
        body: JSON.stringify(result),
      };

      callback(null, response);
    })
    .catch(error => {
      callback(null, null);
    });
};
