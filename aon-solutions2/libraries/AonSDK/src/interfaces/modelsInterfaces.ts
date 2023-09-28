import { ICollection, IFilter } from "./utilitiesInterfaces";

export interface IFactory {
    createDocument(): IDocument;
    createFolder(): IFolder;
    createCertificate(): ICertificate;
    createEnterprise(): IEnterprise;
    createRegistryEnterprise(): IRegistryEnterprise;
    createDocumentNote(): IDocumentNote;
    createBank(): IBank;
    createTaxModel(): ITaxModel;
    createMessage(): IMessage;
    createMessageChat(): IMessageChat;
    createEmployee(): IEmployee;
    createUser(): IUser;
}

export interface ICollectionFactory {
    createDocumentCollection(): ICollection<IDocument>;
    createFolderCollection(): ICollection<IFolder>;
    createEnterpriseCollection(): ICollection<IEnterprise>;
    createDocumentNoteCollection(): ICollection<IDocumentNote>;
    createBankCollection(): ICollection<IBank>;
    createTaxModelCollection(): ICollection<ITaxModel>;
    createMessageCollection(): ICollection<IMessage>;
    createMessageChatCollection(): ICollection<IMessageChat>;
    createEmployeeCollection(): ICollection<IEmployee>;
    createMarkCollection(): ICollection<IMark>;
    createUserCollection(): ICollection<IUser>;
    createContractCollection(): ICollection<IContract>;
}

export interface ICollectable {
    /**
     * Get the value of unique key of the object
     * @returns The unique key
     */
    getKey(): string;
    /**
     * Get the filterable fields of the object and their values
     * @returns The filterable fields as Map
     */
    getFilterableFields(): Map<string,any>;
    /**
     * Get the sortable fields of the object and their values
     * @returns The sortable fields as Map
     */
    getSortableFields(): Map<string,any>;
}

export interface IModel extends ICollectable {
    /**
     * Internal key of object not visible outside SDK
     */
    Key: string;
    /**
     * API json object
     */
    ApiObject: any;

}

export interface IApiModel {
    /**
     * Get the url needed for the api http request.
     * @param currentMethod The method is calling the api
     * @return And array with the method of http needed and the url
     */
    getUrl(currentMethod: string, filter?: IFilter): string[];
    /**
     * Get method and the url of http needed for the api http request. POST, GET...
     * @param currentMethod The method is calling the api
     * @return And array with the method of http needed and the url
     */
    getMethod(currentMethod:string, filter?: IFilter): string;
    /**
     * Parse de data to send to format api
     * @param data to send
     * @returns data parsed for api
     */
    parseDataToSend(data: any, currentMethod?:string, filter?: IFilter): any;
    /**
     * Parse de data received to sdk object
     * @param data received
     * @returns data parsed for sdk object
     */
    parseDataToReceive(data: any, currentMethod?:string, filter?: IFilter): any;
    /**
     * @returns true if filters are applied in local, false otherwhise
     */
    localFilter(currentMethod?: string, filter?: IFilter): boolean;
}

export interface IStorable<T extends ICollectable> {
    /**
     * Get the collection on memory of an object
     */
    getCollection(): ICollection<T>;
    /**
     * Get the value of string where the object is stored in local storage
     */
    getLocalStorage(): string;
}

export interface IDocument extends ICollectable {
    File: string;
    FileName: string;
    FileSize: number;
    FileType: string;
    Path: string;
    Date: Date;
    Tag: ICollection<IDocumentTag>;
}

export interface IDocumentTag extends ICollectable {
    Name: string;
}

export enum MainFolders {
    ACONTABILIZAR = '/a_contabilizar',
    CONTABILIZADO = '/contabilizado',
    PAPELERA = '/papelera',
    FISCAL = '/fiscal',
    LABORAL = '/laboral',
}

export interface IFolder extends ICollectable {
    Name: string;
    Path: string;
    Parent: string;
}

export interface ICertificate extends ICollectable {
  Name: string;
  RepresentationType: string;
  ExpeditionDate: Date;
  ExpirationDate: Date;
  Alias: string;
  Type: string;
  Tgss: boolean;
  Sepe: boolean;
  Aeat: boolean;
  DocumentUser: string;
}

export interface IEnterprise extends ICollectable {
    Name: string;
    ProfilePhoto: string;
    Address: string;
    Country: string;
    Province: string;
    SocialReason: string;
    Email: string;
    Phone: string;
    Website: string;
    Document: string;
    DomainName: string;
    DomainId: string;
    Registry: string;
}

export interface IRegistryEnterprise extends ICollectable {
  IdEnterprise: string,
  Description: string,
  DateCreation: Date,
  DateRegistration: Date,
  Notary: string,
  Protocol: string,
  Inscription: string
}

export interface IDocumentNote extends ICollectable {
    Text: string;
    Path: string;
}

export interface IBank extends ICollectable {
    Name: string;
    Total: number;
    Logo: string;
    SwiftBic: string;
    Iban: string;
    LastUpdate: Date;
    SyncStatus: string;
}

export type statusTaxModel = 'en proceso' | 'pendiente' | 'rectificado' | 'confirmado' | 'presentado';

export interface ITaxModel extends ICollectable {
    Name: string;
    TaxType: string;
    Status: statusTaxModel;
    PaymentMethod: string;
    Result: string;
    Trimester: number;
    Year: number;
}

export enum StatusMessage {
    VISTA = 'vista',
    NUEVA = 'nueva',
    ABIERTA = 'abierta',
    CERRADA = 'cerrada',
    PENDIENTE = 'pendiente',
    REALIZADA = 'realizada'
}

export enum TypeMessage {
    CONSULTA = 'consulta',
    TAREA = 'tarea',
    NOTIFICACION = 'notificacion',
    NULL = 'null'
}

export interface IMessage extends ICollectable {
    Id: string;
    Name: string;
    Title: string;
    Description: string;
    Date: Date;
    Type: TypeMessage;
    Status: StatusMessage;
    EndDate: Date;
    LastMessageChatOrigin: boolean;
}

export interface ITaskHolder extends ICollectable {
    Id: string;
    Name: string;
}

export interface IMessageChat extends ICollectable {
    Id: string;
    IdMessage: string;
    Name: string;
    Description: string;
    Date: Date;
    Type: string;
}

export interface IEmployee extends ICollectable {
    Name: string;
    Lastname: string;
    Document: string;
    Email: string;
    Phone: string;
    Naf: string;
    Active: boolean;
}

export interface IContract extends ICollectable {
    Name: string;
    LastName: string;
    Type: string;
    GrossCost: number;
    StartDate: Date;
    EndDate?: Date;
    WorkCenter: string;
    Active: boolean;
}

// export interface IMark extends ICollectable {
//     Id: string;
//     Name: string,
//     Lastname: string,
//     IdEmployee: string, // Ver si este es necesario, o id del usuario
//     Date: Date,
//     EntryDate: Date,
//     ExitDate: Date,
//     Pause: IPause,
//     Location: string,
//     Ccc: string, // código cuenta de cotización
//     Workplace: string,
//     Status: string
// }

export interface IMark extends ICollectable {
    // Id: string, // ????
    // Lastname: string,
    // Pause: IPause,
    // Ccc: string, // código cuenta de cotización
    // Workplace: string,
    Name: string, // nombre del usuario que marca
    IdUser: string, // id del usuario que marca
    Date: Date, // fecha del marcage
    EntryDate: Date, // hora de entrada del marcaje
    ExitDate: Date, // hora de salida del marcaje
    Time: Date; // duracion del marcaje total
    Location: any, // localizacion al marcar
    Status: string // entrada, salida, pausa
}

export interface IMarkDetail extends ICollectable {
    Id: string,
    LastDate: Date,
    LastModification: Date,
    Status: string,
    Location: any
}

export interface IPause {
    StartPause: Date,
    EndPause: Date
}

export interface IUser extends ICollectable {
    Name: string,
    Lastname: string,
    Document: string,
    Email: string,
    Password: string,
    Phone: string,
    Active: boolean,
    Enterprises: string [],
}

export interface IAuth extends ICollectable{
    Email: string;
    Password: string;
}

export interface ICommunity {
  label: string;
  _about: string;
}

export interface ICountry {
  name:         Name;
  tld?:         string[];
  cca2:         string;
  ccn3?:        string;
  cca3:         string;
  cioc?:        string;
  independent?: boolean;
  status:       Status;
  unMember:     boolean;
  currencies?:  Currencies;
  idd:          Idd;
  capital?:     string[];
  altSpellings: string[];
  region:       Region;
  subregion?:   string;
  languages?:   { [key: string]: string };
  translations: { [key: string]: Translation };
  latlng:       number[];
  landlocked:   boolean;
  borders?:     string[];
  area:         number;
  demonyms?:    Demonyms;
  flag:         string;
  maps:         Maps;
  population:   number;
  gini?:        { [key: string]: number };
  fifa?:        string;
  car:          Car;
  timezones:    string[];
  continents:   Continent[];
  flags:        Flags;
  coatOfArms:   CoatOfArms;
  startOfWeek:  StartOfWeek;
  capitalInfo:  CapitalInfo;
  postalCode?:  PostalCode;
}

export interface CapitalInfo {
  latlng?: number[];
}

export interface Car {
  signs?: string[];
  side:   Side;
}

export enum Side {
  Left = "left",
  Right = "right",
}

export interface CoatOfArms {
  png?: string;
  svg?: string;
}

export enum Continent {
  Africa = "Africa",
  Antarctica = "Antarctica",
  Asia = "Asia",
  Europe = "Europe",
  NorthAmerica = "North America",
  Oceania = "Oceania",
  SouthAmerica = "South America",
}

export interface Currencies {
  ZAR?: Aed;
  NOK?: Aed;
  WST?: Aed;
  GMD?: Aed;
  XCD?: Aed;
  EUR?: Aed;
  AWG?: Aed;
  XOF?: Aed;
  KPW?: Aed;
  PYG?: Aed;
  BMD?: Aed;
  XAF?: Aed;
  USD?: Aed;
  GBP?: Aed;
  MZN?: Aed;
  SOS?: Aed;
  SGD?: Aed;
  NIO?: Aed;
  AUD?: Aed;
  PEN?: Aed;
  MXN?: Aed;
  BAM?: BAM;
  BHD?: Aed;
  MOP?: Aed;
  BBD?: Aed;
  UZS?: Aed;
  CNY?: Aed;
  MWK?: Aed;
  ZWL?: Aed;
  KES?: Aed;
  PKR?: Aed;
  FJD?: Aed;
  SZL?: Aed;
  JEP?: Aed;
  TWD?: Aed;
  LKR?: Aed;
  BYN?: Aed;
  AED?: Aed;
  ANG?: Aed;
  CRC?: Aed;
  AOA?: Aed;
  UYU?: Aed;
  CDF?: Aed;
  KWD?: Aed;
  TRY?: Aed;
  MRU?: Aed;
  TVD?: Aed;
  PAB?: Aed;
  EGP?: Aed;
  AZN?: Aed;
  RWF?: Aed;
  INR?: Aed;
  ISK?: Aed;
  SRD?: Aed;
  BGN?: Aed;
  SLL?: Aed;
  TND?: Aed;
  CUC?: Aed;
  CUP?: Aed;
  TTD?: Aed;
  KMF?: Aed;
  SHP?: Aed;
  RON?: Aed;
  NPR?: Aed;
  SAR?: Aed;
  DOP?: Aed;
  DKK?: Aed;
  FOK?: Aed;
  KID?: Aed;
  VUV?: Aed;
  HUF?: Aed;
  YER?: Aed;
  SCR?: Aed;
  LYD?: Aed;
  ILS?: Aed;
  VND?: Aed;
  IRR?: Aed;
  NAD?: Aed;
  LBP?: Aed;
  MYR?: Aed;
  MNT?: Aed;
  GEL?: Aed;
  TJS?: Aed;
  ALL?: Aed;
  TMT?: Aed;
  COP?: Aed;
  VES?: Aed;
  GNF?: Aed;
  SSP?: Aed;
  UAH?: Aed;
  FKP?: Aed;
  HNL?: Aed;
  BRL?: Aed;
  MUR?: Aed;
  THB?: Aed;
  BOB?: Aed;
  SEK?: Aed;
  GGP?: Aed;
  ZMW?: Aed;
  ERN?: Aed;
  KZT?: Aed;
  MAD?: Aed;
  JOD?: Aed;
  MMK?: Aed;
  CZK?: Aed;
  JMD?: Aed;
  KGS?: Aed;
  SDG?: BAM;
  STN?: Aed;
  GIP?: Aed;
  LSL?: Aed;
  PLN?: Aed;
  JPY?: Aed;
  LRD?: Aed;
  CVE?: Aed;
  IMP?: Aed;
  BIF?: Aed;
  PGK?: Aed;
  UGX?: Aed;
  AFN?: Aed;
  XPF?: Aed;
  BWP?: Aed;
  LAK?: Aed;
  GTQ?: Aed;
  CHF?: Aed;
  SBD?: Aed;
  SYP?: Aed;
  BDT?: Aed;
  DJF?: Aed;
  GHS?: Aed;
  OMR?: Aed;
  BSD?: Aed;
  DZD?: Aed;
  HTG?: Aed;
  PHP?: Aed;
  CKD?: Aed;
  NZD?: Aed;
  TOP?: Aed;
  MGA?: Aed;
  CAD?: Aed;
  AMD?: Aed;
  NGN?: Aed;
  BZD?: Aed;
  RUB?: Aed;
  KYD?: Aed;
  MDL?: Aed;
  RSD?: Aed;
  CLP?: Aed;
  IDR?: Aed;
  MVR?: Aed;
  BND?: Aed;
  GYD?: Aed;
  TZS?: Aed;
  KHR?: Aed;
  QAR?: Aed;
  ARS?: Aed;
  IQD?: Aed;
  BTN?: Aed;
  KRW?: Aed;
  HKD?: Aed;
  MKD?: Aed;
  ETB?: Aed;
}

export interface Aed {
  name:   string;
  symbol: string;
}

export interface BAM {
  name: string;
}

export interface Demonyms {
  eng:  Eng;
  fra?: Eng;
}

export interface Eng {
  f: string;
  m: string;
}

export interface Flags {
  png:  string;
  svg:  string;
  alt?: string;
}

export interface Idd {
  root?:     string;
  suffixes?: string[];
}

export interface Maps {
  googleMaps:     string;
  openStreetMaps: string;
}

export interface Name {
  common:      string;
  official:    string;
  nativeName?: { [key: string]: Translation };
}

export interface Translation {
  official: string;
  common:   string;
}

export interface PostalCode {
  format: string;
  regex?: string;
}

export enum Region {
  Africa = "Africa",
  Americas = "Americas",
  Antarctic = "Antarctic",
  Asia = "Asia",
  Europe = "Europe",
  Oceania = "Oceania",
}

export enum StartOfWeek {
  Monday = "monday",
  Saturday = "saturday",
  Sunday = "sunday",
}

export enum Status {
  OfficiallyAssigned = "officially-assigned",
  UserAssigned = "user-assigned",
}

export interface ISimpleValue {
    Value: string;
}

export enum InvoiceType { GASTO = 'gasto', VENTA = 'venta' }

export enum InvoiceStatus { A_CONTABILIZAR = 'a_contabilizar', CONTABILIZADO = 'contabilizado' }

export interface IInvoice extends ICollectable{
    Serie: IInvoiceSerie;
    InvoiceNumber: string;
    Date: Date;
    TotalAmount: number;
    Contact: IContact;
    Category: IInvoiceCategory;
    Lines: ICollection<IInvoiceLine>;
    TransactionType: IInvoiceTransactionType;
    Activity: IInvoiceActivity;
    CriCaja: boolean;
    RE: boolean;
    RegAgri: boolean;
    Tax: ITax;
    TaxBase: number;
    TaxQuota: number;
    IRPF: IIRPF;
    IRPFBase: number;
    IRPFQuota: number;
    InvoiceExpirationLines: ICollection<IInvoiceExpirationLine>;
    Rectified: boolean;
    Type: InvoiceType;
    Status: InvoiceStatus;
}

export interface IInvoiceLine extends ICollectable{
    Product: IProduct;
    Quantity: number;
    Price: number;
    Discount: number;
    TotalPrice: number;
    Tax: ITax;
    IRPF: IIRPF;
}

export interface IInvoiceExpirationLine extends ICollectable{
    Date: Date;
    PaymentMethod: IPaymentMethod;
    BankAccount: string;
    Amount: number;
}

export interface IInvoiceSerie extends ICollectable, ISimpleValue {}

export interface IInvoiceTransactionType extends ICollectable, ISimpleValue{}

export interface IInvoiceActivity extends ICollectable, ISimpleValue{}

export interface IIRPF extends ICollectable, ISimpleValue {
    Type: string;
}

export interface ITax extends ICollectable, ISimpleValue {}

export interface IPaymentMethod extends ICollectable, ISimpleValue {}

export interface IInvoiceCategory extends ICollectable, ISimpleValue {}

export interface IProduct extends ICollectable{
    Code: IProductCode;
    Name: string;
    Category: IProductCategory;
    Class: IProductClass;
    Type: IProductType;
    Status: IProductStatus;
    Tax: ITax;
    IRPF: IIRPF;
    BarCode: string;
    Description: string;
    CostPrice: number;
    Benefit: number;
    Price: number;
    Pvp: number;
}

export interface IProductCode extends ICollectable, ISimpleValue {}

export interface IProductCategory extends ICollectable, ISimpleValue {}

export interface IProductClass extends ICollectable, ISimpleValue {}

export interface IProductType extends ICollectable, ISimpleValue {}

export interface IProductStatus extends ICollectable, ISimpleValue {}

export interface IContact extends ICollectable{
    Name: string;
    ComercialName: string;
    Country: string;
    /**
     * Can be DNI, CIF, etc
     */
    Document: string;
    Address: string;
    Email: string;
    Phone: string;
    Web: string;
    BankAccount: string;
    PaymentMethod: IPaymentMethod;
    TransactionType: IInvoiceTransactionType;
    IRPF: boolean;
    RE: boolean;
}
