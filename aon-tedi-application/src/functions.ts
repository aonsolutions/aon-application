/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';
import bodyParser from 'body-parser';
// we use the 'busboy' library to parse multipart/form-data requests.
import Busboy from 'busboy';
import express from 'express';
import * as functions from 'firebase-functions';
import { mergeMap, tap } from 'rxjs/operators';
import { TediAuth } from './tedi-auth/auth';
import { TediEvicertia } from './tedi-evicertia/evicertia';
import { Company, Invoice, InvoiceStatus, Nif, OperationType, Registry, TediError, TediImportInvoicesInfo, User } from './tedi-ewok/TediEwok';
import { GmailGCF } from './tedi-gmail/gcf';
import { TediImgParser } from './tedi-img-parser/TediImgParser';
import { TediPDFBuilder } from './tedi-pdf-builder/TediPdfBuilder';
import { TediPdfParser } from './tedi-pdf-parser/TediPdfParser';
import * as gfu from './tedi-storage/FileUtils';
import { StorageGCF } from './tedi-storage/gcf';
import { TediCompany } from './tedi/company';
import { TediInvoice } from './tedi/invoice';
import { TediRegistry } from './tedi/registry';
import { TediUser } from './tedi/user';

// tslint:disable-next-line: no-var-requires
// const tedi = require('./tedi.js');

// tslint:disable-next-line: no-var-requires
const cors: express.RequestHandler = require('cors')({
  origin: true,
});

// Mail processing logic for Gmail using the Gmail API and Cloud Function

/**
 * Request an OAuth 2.0 authorization code
 * Only new users (or those who want to refresh their auth data) need visit this
 * page
 */
exports.oauth2init = functions.region('europe-west1').https.onRequest((req, res) => {
  return GmailGCF.oauth2init(req, res);
});

/**
 * Get an access token from the authorization code and store token in Datastore
 */
exports.oauth2callback = functions.region('europe-west1').https.onRequest((req, res) => {
  return GmailGCF.oauth2callback(req, res);
});

/**
 * Initialize for watch on the user's inbox
 */
exports.init4watch = functions.region('europe-west1').https.onRequest((req, res) => {
  return GmailGCF.init4watch(req, res);
});

/**
 *
 */
exports.gmail2invoice = functions
  .runWith({
    memory: '512MB',
    timeoutSeconds: 540,
  })
  .region('europe-west1')
  .pubsub.topic('gmail-invoice')
  .onPublish(message => {
    return GmailGCF.gmail2invoice(message);
  });

/**
 *
 */
exports.storage2invoice = functions
  // .runWith({
  //   memory: '128MB',
  //   timeoutSeconds: 60,
  // })
  .region('europe-west1')
  .storage.bucket(`${process.env.GCLOUD_PROJECT}-uploads`)
  .object()
  .onFinalize(object => {
    return StorageGCF.storage2invoice(object);
  });

/**
 *
 */
exports.gmail4signon = functions
  .region('europe-west1')
  .pubsub.topic('gmail-signon')
  .onPublish(message => {
    return GmailGCF.gmail4signon(message);
  });

function manageError(res: express.Response, error: Error | TediError): void {
  const tediError: TediError = error as TediError;
  if (tediError.statusCode) {
    res.status(tediError.statusCode).send({ error: tediError.message });
  } else {
    res.status(403).send({ error: error.message });
  }
}

// AUTH
exports.auth = functions.region('europe-west1').https.onRequest((req, res) => {
  // Forbidding PUT | DELETE requests.
  if (req.method === 'PUT' || req.method === 'DELETE') {
    return res.status(403).send('Forbidden!');
  }

  // Enable CORS using the `cors` express middleware.
  return cors(req, res, () => {
    TediUser.getUser(req.body.email)
      .pipe(mergeMap(usr => TediEvicertia.checkContract(usr)))
      .pipe(mergeMap(usr => TediAuth.login(req.body, usr)))

      .subscribe(
        result => res.status(200).json(result),
        error => manageError(res, error),
      );
  });
});

function ensure(sessions: string | string[] | undefined): string | undefined {
  let session: string | undefined;
  if (sessions) {
    if (typeof sessions === 'string') {
      session = sessions;
    } else if (sessions.length > 0) {
      session = sessions[0];
    }
  }
  return session;
}

// REGISTER
const register = express();
// Automatically allow cross-origin requests
register.use(cors);
register.use(bodyParser.json());
register.options('/', cors);

register.post('/', cors, (req, res) => {
  const cp: Company = req.body.company;
  cp.active = false;
  const usr: User = req.body.user;
  usr.active = false;
  usr.password = TediAuth.encrypt(usr.password);
  TediAuth.checkAuthentication(ensure(req.headers.session_id), true)
    .pipe(mergeMap(() => TediEvicertia.contract(usr, cp)))
    .pipe(
      mergeMap((ctr: any) => {
        cp.contract = {
          uniqueId: ctr.uniqueId,
        };
        return TediCompany.putCompany(cp);
      }),
    )
    .pipe(mergeMap(() => TediUser.putUser(usr)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

exports.register = functions.region('europe-west1').https.onRequest(register);

// COMPANY
const company = express();
// Automatically allow cross-origin requests
company.use(cors);
company.use(bodyParser.json());
company.options('/', cors);
company.get('/', cors, (req, res) => {
  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(mergeMap(usr => TediCompany.getFilter(req.query, usr)))
    .pipe(mergeMap(filter => TediCompany.getCompaniesArray(filter)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

company.post('/', cors, (req, res) => {
  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(mergeMap(usr => TediCompany.checkPermission(usr, req.body, OperationType.CREATE)))
    .pipe(mergeMap(() => TediCompany.putCompany(req.body)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

company.options('/user/:email', cors);
company.get('/user/:email', cors, (req, res) => {
  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(mergeMap(() => TediUser.getUser(req.params.email)))
    .pipe(mergeMap(usr => TediCompany.getFilter(req.query, usr)))
    .pipe(mergeMap(filter => TediCompany.getCompaniesArray(filter)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

company.options('/:document', cors);

company.get('/:document', cors, (req, res) => {
  TediAuth.checkAuthentication(ensure(req.headers.session_id), true)
    .pipe(mergeMap(usr => TediCompany.checkPermission(usr, req.body, OperationType.GET)))
    .pipe(mergeMap(() => TediCompany.getCompany(req.params.document)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

company.post('/:document', cors, (req, res) => {
  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(mergeMap(usr => TediCompany.checkPermission(usr, req.body, OperationType.UPDATE)))
    .pipe(mergeMap(() => TediCompany.updateCompany(req.body)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

company.delete('/:document', cors, (req, res) => {
  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(mergeMap(usr => TediCompany.checkPermission(usr, req.body, OperationType.DELETE)))
    .pipe(mergeMap(() => TediCompany.deleteCompany(req.params.document)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

exports.company = functions.region('europe-west1').https.onRequest(company);

// USER

const user = express();
// Automatically allow cross-origin requests
user.use(cors);
user.use(bodyParser.json());
user.options('/', cors);

user.get('/', cors, (req, res) => {
  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(mergeMap(usr => TediUser.getFilter(req.query, usr)))
    .pipe(mergeMap(filter => TediUser.getUsers(filter)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

user.post('/', cors, (req, res) => {
  const newUser = req.body;
  newUser.password = TediAuth.encrypt(newUser.password);

  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(mergeMap(usr => TediUser.putUser(newUser)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

// Enabling CORS Pre-Flight
user.options('/password', cors);

user.post('/password', cors, (req, res) => {
  const updateUser = req.body;
  const password = TediAuth.generatePassword();

  updateUser.password = TediAuth.encrypt(password);

  TediUser.updateUser(updateUser)
    .pipe(mergeMap(x => TediUser.password(updateUser.email, password)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

// Enabling CORS Pre-Flight
user.options('/:email', cors);
// build user CRUD interfaces:
user.get('/:email', cors, (req, res) => {
  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(mergeMap(usr => TediUser.getUser(req.params.email)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

user.post('/:email', cors, (req, res) => {
  const updateUser = req.body;
  if (updateUser.password) {
    updateUser.password = TediAuth.encrypt(updateUser.password);
  }

  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(mergeMap(usr => TediUser.updateUser(updateUser)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

user.delete('/:email', cors, (req, res) => {
  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(mergeMap(usr => TediUser.deleteUser(req.params.email)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

// Expose tEDI user API as a single Cloud Function:
exports.user = functions.region('europe-west1').https.onRequest(user);

// INVOICE

const invoice = express();
// Automatically allow cross-origin requests
invoice.use(cors);
invoice.use(bodyParser.json());
invoice.options('/', cors);

invoice.get('/', cors, (req, res) => {
  // @ts-ignore
  const comp = req.query.company;
  // @ts-ignore
  const status: InvoiceStatus =
    InvoiceStatus.pending === req.query.status || InvoiceStatus.verified === req.query.status ? InvoiceStatus.inbox : req.query.status;

  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(tap(usr => TediInvoice.checkPermission(usr, OperationType.GET)))
    // @ts-ignore
    .pipe(mergeMap(() => TediInvoice.getInvoices(comp, req.query, status)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

invoice.post('/', cors, (req, res) => {
  let data: TediImportInvoicesInfo;
  let inv: Invoice;
  const isFile: boolean = req.body.content ? true : false;
  if (isFile) {
    data = req.body;
    if (data && data.company) {
      data.company = data.company.toUpperCase();
    }
  } else {
    inv = req.body;
  }
  if (isFile) {
    TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
      .pipe(tap(usr => TediInvoice.checkPermission(usr, OperationType.IMPORT)))
      .pipe(mergeMap(usr => TediInvoice.importFiles(data, usr)))
      .subscribe(
        result => res.status(200).json(result),
        error => manageError(res, error),
      );
  } else {
    TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
      .pipe(tap(usr => TediInvoice.checkPermission(usr, OperationType.IMPORT)))
      .pipe(mergeMap(usr => TediInvoice.importInvoice(inv, usr)))
      .subscribe(
        result => res.status(200).json(result),
        error => manageError(res, error),
      );
  }
});

invoice.delete('/', cors, (req, res) => {
  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(tap(usr => TediInvoice.checkPermission(usr, OperationType.DELETE)))
    .pipe(mergeMap(() => TediInvoice.deleteInvoices(req.body.company, req.body.uuids)))
    .pipe(mergeMap(() => TediInvoice.deleteInvoices(req.body.company, req.body.uuids, req.body.status)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

invoice.options('/d/preview/:company', cors);

invoice.get('/d/preview/:company', cors, (req, res) => {
  TediCompany.getCompany(req.params.company)
    .pipe(mergeMap((cp: Company) => TediCompany.getPrinterConfiguration(cp)))
    .pipe(mergeMap(printerConfiguration => TediPDFBuilder.buildPreview$(req.params.company, printerConfiguration)))
    .subscribe(
      result => res.status(200).send(result),
      error => manageError(res, error),
    );
});

invoice.options('/d/:status/:company/:uuid', cors);

invoice.get('/d/:status/:company/:uuid', cors, (req, res) => {
  const comp = req.params.company;
  const uuid = req.params.uuid;
  const st: string = req.params.status;
  const status: InvoiceStatus =
    InvoiceStatus.pending === req.params.status || InvoiceStatus.verified === req.params.status ? InvoiceStatus.inbox : InvoiceStatus[st];
  TediInvoice.getInvoice(comp, uuid, status)
    .pipe(
      mergeMap(inv =>
        TediCompany.getCompany(req.params.company).pipe(
          mergeMap((cp: Company) => TediPDFBuilder.build$(inv, TediCompany.ensurePrinterConfiguration(cp.printer_configuration))),
        ),
      ),
    )
    .subscribe(
      result => res.status(200).send(result),
      error => manageError(res, error),
    );
});

invoice.options('/d/:uuid', cors);
invoice.get('/d/:uuid', cors, (req, res) => {
  const uuid = req.params.uuid;
  gfu.getDownloadURL$(`invoices/${uuid}`).subscribe(
    url => res.status(200).send({ url }),
    error => manageError(res, error),
  );
});

invoice.options('/s', cors);

invoice.post('/s', cors, (req, res) => {
  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(mergeMap(usr => TediInvoice.sendInvoice(usr, req.body)))
    .subscribe(
      result => res.status(200).send(result),
      error => manageError(res, error),
    );
});

invoice.options('/count/:company/:status', cors);

invoice.get('/count/:company/:status', cors, (req, res) => {
  const comp = req.params.company;
  const st: string = req.params.status;
  const status: InvoiceStatus =
    InvoiceStatus.pending === req.params.status || InvoiceStatus.verified === req.params.status ? InvoiceStatus.inbox : InvoiceStatus[st];

  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(tap(usr => TediInvoice.checkPermission(usr, OperationType.GET)))
    .pipe(mergeMap(() => TediInvoice.getCountInvoices(comp, status)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

invoice.options('/:status/:company/:uuid', cors);

invoice.get('/:status/:company/:uuid', cors, (req, res) => {
  const comp = req.params.company;
  const uuid = req.params.uuid;
  const st: string = req.params.status;
  const status: InvoiceStatus =
    InvoiceStatus.pending === req.params.status || InvoiceStatus.verified === req.params.status ? InvoiceStatus.inbox : InvoiceStatus[st];

  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(tap(usr => TediInvoice.checkPermission(usr, OperationType.GET)))
    .pipe(mergeMap(usr => TediInvoice.getInvoice(comp, uuid, status)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

invoice.post('/:status/:company/:uuid', cors, (req, res) => {
  const comp = req.params.company;
  const st: string = req.params.status;
  const status: InvoiceStatus = InvoiceStatus[st];
  const inv = req.body;
  inv.uuid = inv.uuid || req.params.uuid;
  inv.status = inv.status || status;
  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(tap(usr => TediInvoice.checkPermission(usr, OperationType.UPDATE)))
    .pipe(mergeMap(usr => TediInvoice.updateInvoice(comp, inv, status)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

invoice.delete('/:status/:company/:uuid', cors, (req, res) => {
  const comp = req.params.company;
  const uuid = req.params.uuid;
  const st: string = req.params.status;
  const status: InvoiceStatus = InvoiceStatus[st];
  TediAuth.checkAuthentication(ensure(req.headers.session_id), false)
    .pipe(tap(usr => TediInvoice.checkPermission(usr, OperationType.DELETE)))
    .pipe(mergeMap(() => TediInvoice.deleteInvoice(comp, uuid, status)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

exports.invoice = functions.region('europe-west1').https.onRequest(invoice);

// REGISTRY

const registry = express();
// Automatically allow cross-origin requests
registry.use(cors);
registry.use(bodyParser.json());
// company.options('/', cors);
registry.get('/', cors, (req, res) => {
  TediRegistry.getFilter(req.query)
    .pipe(mergeMap(filter => TediRegistry.getRegistries(filter)))
    .subscribe(
      result => res.status(200).json(result),
      error => manageError(res, error),
    );
});

registry.post('/', cors, (req, res) => {
  TediRegistry.putRegistry(req.body).subscribe(
    result => res.status(200).json(result),
    error => manageError(res, error),
  );
});

registry.options('/:document', cors);

registry.get('/:document', cors, (req, res) => {
  TediRegistry.getRegistry(req.params.document).subscribe(
    result => res.status(200).json(result),
    error => manageError(res, error),
  );
});

registry.post('/:document', cors, (req, res) => {
  TediRegistry.updateRegistry(req.body).subscribe(
    result => res.status(200).json(result),
    error => manageError(res, error),
  );
});

registry.delete('/:document', cors, (req, res) => {
  TediRegistry.deleteRegistry(req.params.document).subscribe(
    result => res.status(200).json(result),
    error => manageError(res, error),
  );
});

exports.registry = functions.region('europe-west1').https.onRequest(registry);

// PARSE

exports.parse = functions.region('europe-west1').https.onRequest((req: functions.https.Request, res: express.Response) => {
  // Set CORS headers for preflight requests
  // Allows POSTs from any origin with the Content-Type header
  // and caches preflight response for 3600s

  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Credentials', 'true');

  if (req.method === 'OPTIONS') {
    // Send response to OPTIONS requests
    res.setHeader('Access-Control-Allow-Methods', 'POST');
    res.setHeader('Access-Control-Allow-Headers', 'Authorization');
    res.setHeader('Access-Control-Max-Age', '3600');
    res.writeHead(204, { Connection: 'close' });
    res.end('');
  } else {
    const nifs: Nif[] = Nif.find(req.url || '');
    const docs: string[] = nifs.map((nif: Nif) => nif.str);

    const busboy: busboy.Busboy = new Busboy({ headers: req.headers });

    const promises: Array<Promise<Invoice>> = [];

    // This code will process each file uploaded.
    busboy.on('file', async (fieldname: string, file: NodeJS.ReadableStream, filename: string, encoding: string, mimetype: string) => {
      const datas: any[] = [];

      const registries: Registry[] = await TediRegistry.getRegistriesAsync(docs);
      const companies: Company[] = registries.map((r: Registry) => {
        return {
          active: true,
          name: r.name || '',
          address: r.address,
          company: r.document!,
          document: r.document!,
        };
      });

      file.on('data', data => datas.push(data));

      file.on('end', () => {
        const info: TediImportInvoicesInfo = {
          content: Buffer.concat(datas),
          contentType: mimetype,
          companies,
        };

        promises.push(mimetype.match(/pdf/) ? TediPdfParser.parse(info) : TediImgParser.parse(info));
      });
    });

    busboy.on('finish', async () => {
      const invoices: Invoice[] = await Promise.all(promises);
      res.setHeader('Content-Type', 'application/json');
      res.writeHead(200, { Connection: 'close' });
      res.end(JSON.stringify(invoices));
      // res.status(200).json(invoices);
    });
    if (req.rawBody) {
      // The raw bytes of the upload will be in req.rawBody.  Send it to busboy, and get
      // a callback when it's finished.
      busboy.end(req.rawBody);
    } else {
      // All the data from readable goes into busboy.
      req.pipe(busboy);
    }
  }
});
