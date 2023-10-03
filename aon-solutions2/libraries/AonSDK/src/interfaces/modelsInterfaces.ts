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
    getFile(): string;
    setFile(value: string): IDocument;
    FileName: string;
    getFileName(): string;
    setFileName(value: string): IDocument;
    FileSize: number;
    getFileSize(): number;
    setFileSize(value: number): IDocument;
    FileType: string;
    getFileType(): string;
    setFileType(value: string): IDocument;
    Path: string;
    getPath(): string;
    setPath(value: string): IDocument;
    Date: Date;
    getDate(): Date;
    setDate(value: Date): IDocument;
    Tag: ICollection<IDocumentTag>;
    getTag(): ICollection<IDocumentTag>;
    setTag(value: ICollection<IDocumentTag>): IDocument;
}

export interface IDocumentTag extends ICollectable {
    Name: string;
    getName(): string;
    setName(value: string): IDocumentTag;
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
    getName(): string;
    setName(value: string): IFolder;
    Path: string;
    getPath(): string;
    setPath(value: string): IFolder;
    Parent: string;
    getParent(): string;
    setParent(value: string): IFolder;
}

export interface ICertificate extends ICollectable {
  Name: string;
  getName(): string;
  setName(value: string): ICertificate;
  RepresentationType: string;
  getRepresentationType(): string;
  setRepresentationType(value: string): ICertificate;
  ExpeditionDate: Date;
  getExpeditionDate(): Date;
  setExpeditionDate(value: Date): ICertificate;
  ExpirationDate: Date;
  getExpirationDate(): Date;
  setExpirationDate(value: Date): ICertificate;
  Alias: string;
  getAlias(): string;
  setAlias(value: string): ICertificate;
  Type: string;
  getType(): string;
  setType(value: string): ICertificate;
  Tgss: boolean;
  getTgss(): boolean;
  setTgss(value: boolean): ICertificate;
  Sepe: boolean;
  getSepe(): boolean;
  setSepe(value: boolean): ICertificate;
  Aeat: boolean;
  getAeat(): boolean;
  setAeat(value: boolean): ICertificate;
  DocumentUser: string;
  getDocumentUser(): string;
  setDocumentUser(value: string): ICertificate;
}

export interface IEnterprise extends ICollectable {
    Name: string;
    getName(): string;
    setName(value: string): IEnterprise;
    ProfilePhoto: string;
    getProfilePhoto(): string;
    setProfilePhoto(value: string): IEnterprise;
    Address: string;
    getAdress(): string;
    setAdress(value: string): IEnterprise;
    Country: string;
    getCountry(): string;
    setCountry(value: string): IEnterprise;
    Province: string;
    getProvince(): string;
    setProvince(value: string): IEnterprise;
    SocialReason: string;
    getSocialReason(): string;
    setSocialReason(value: string): IEnterprise;
    Email: string;
    getEmail(): string;
    setEmail(value: string): IEnterprise;
    Phone: string;
    getPhone(): string;
    setPhone(value: string): IEnterprise;
    Website: string;
    getWebsite(): string;
    setWebsite(value: string): IEnterprise;
    Document: string;
    getDocument(): string;
    setDocument(value: string): IEnterprise;
    DomainName: string;
    getDomainName(): string;
    setDomainName(value: string): IEnterprise;
    DomainId: string;
    getDomainId(): string;
    setDomainId(value: string): IEnterprise;
    Registry: string;
    getRegistry(): string;
    setRegistry(value: string): IEnterprise;
}

export interface IRegistryEnterprise extends ICollectable {
  IdEnterprise: string;
  Description: string;
  getDescription(): string;
  setDescription(value: string): IRegistryEnterprise;
  DateCreation: Date;
  getDateCreation(): Date;
  setDateCreation(value: Date): IRegistryEnterprise;
  DateRegistration: Date;
  getDateRegistration(): Date;
  setDateRegistration(value: Date): IRegistryEnterprise;
  Notary: string;
  getNotary(): string;
  setNotary(value: string): IRegistryEnterprise;
  Protocol: string;
  getProtocol(): string;
  setProtocol(value: string): IRegistryEnterprise;
  Inscription: string;
  getInscription(): string;
  setInscription(value: string): IRegistryEnterprise;
}

export interface IDocumentNote extends ICollectable {
    Text: string;
    getText(): string;
    setText(value: string): IDocumentNote;
    Path: string;
    getPath(): string;
    setPath(value: string): IDocumentNote;
}

export interface IBank extends ICollectable {
    Name: string;
    getName(): string;
    setName(value: string): IBank;
    Total: number;
    getTotal(): number;
    setTotal(value: number): IBank;
    Logo: string;
    getLogo(): string;
    setLogo(value: string): IBank;
    SwiftBic: string;
    getSwiftBic(): string;
    setSwiftBic(value: string): IBank;
    Iban: string;
    getIban(): string;
    setIban(value: string): IBank;
    LastUpdate: Date;
    getLastUpdate(): Date;
    setLastUpdate(value: Date): IBank;
    SyncStatus: string;
    getSyncStatus(): string;
    setSyncStatus(value: string): IBank;
}

export enum statusTaxModel {
  EN_PROCESO = 'en proceso',
  PENDIENTE = 'pendiente',
  RECTIFICADO = 'rectificado',
  CONFIMRADO = 'confirmado',
  PRESENTADO = 'presentado'
} 

export interface ITaxModel extends ICollectable {
    Name: string;
    getName(): string;
    setName(value: string): ITaxModel;
    TaxType: string;
    getTaxType(): string;
    setTaxType(value: string): ITaxModel;
    Status: statusTaxModel;
    getStatus(): statusTaxModel;
    setStatus(value: statusTaxModel): ITaxModel;
    PaymentMethod: string;
    getPaymentMethod(): string;
    setPaymentMethod(value: string): ITaxModel;
    Result: string;
    getResult(): string;
    setResult(value: string): ITaxModel;
    Trimester: number;
    getTrimester(): number;
    setTrimester(value: number): ITaxModel;
    Year: number;
    getYear(): number;
    setYear(value: number): ITaxModel;
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
    getName(): string;
    setName(value: string): IMessage;
    Title: string;
    getTitle(): string;
    setTitle(value: string): IMessage;
    Description: string;
    getDescription(): string;
    setDescription(value: string): IMessage;
    Date: Date;
    getDate(): Date;
    setDate(value: Date): IMessage;
    Type: TypeMessage;
    getType(): TypeMessage;
    setType(value: TypeMessage): IMessage;
    Status: StatusMessage;
    getStatus(): StatusMessage;
    setStatus(value: StatusMessage): IMessage;
    EndDate: Date;
    getEndDate(): Date;
    setEndDate(value: Date): IMessage;
    LastMessageChatOrigin: boolean;
    getLastMessageChatOrigin(): boolean;
    setLastMessageChatOrigin(value: boolean): IMessage;
}

export interface ITaskHolder extends ICollectable {
    Id: string;
    Name: string;
    getName(): string;
    setName(value: string): ITaskHolder;
}

export interface IMessageChat extends ICollectable {
    Id: string;
    IdMessage: string;
    getIdMessage(): string;
    setIdMessage(value: string): IMessageChat;
    Name: string;
    getName(): string;
    setName(value: string): IMessageChat;
    Description: string;
    getDescription(): string;
    setDescription(value: string): IMessageChat;
    Date: Date;
    getDate(): Date;
    setDate(value: Date): IMessageChat;
    Type: string;
    getType(): string;
    setType(value: string): IMessageChat;
}

export interface IEmployee extends ICollectable {
    Name: string;
    getName(): string;
    setName(value: string): IEmployee;
    Lastname: string;
    getLastname(): string;
    setLastname(value: string): IEmployee;
    Document: string;
    getDocument(): string;
    setDocument(value: string): IEmployee;
    Email: string;
    getEmail(): string;
    setEmail(value: string): IEmployee;
    Phone: string;
    getPhone(): string;
    setPhone(value: string): IEmployee;
    Naf: string;
    getNaf(): string;
    setNaf(value: string): IEmployee;
    Active: boolean;
    getActive(): boolean;
    setActive(value: boolean): IEmployee;
}

export interface IContract extends ICollectable {
    Name: string;
    getName(): string;
    setName(value: string): IContract;
    LastName: string;
    getLastName(): string;
    setLastName(value: string): IContract;
    Type: string;
    getType(): string;
    setType(value: string): IContract;
    GrossCost: number;
    getGrossCost(): number;
    setGrossCost(value: number): IContract;
    StartDate: Date;
    getStartDate(): Date;
    setStartDate(value: Date): IContract;
    EndDate?: Date;
    getEndDate(): Date;
    setEndDate(value: Date): IContract;
    WorkCenter: string;
    getWorkCenter(): string;
    setWorkCenter(value: string): IContract;
    Active: boolean;
    getActive(): boolean;
    setActive(value: boolean): IContract;
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
    getName(): string;
    setName(value: string): IMark;
    IdUser: string, // id del usuario que marca
    getIdUser(): string;
    setIdUser(value: string): IMark;
    Date: Date, // fecha del marcage
    getDate(): Date;
    setDate(value: Date): IMark;
    EntryDate: Date, // hora de entrada del marcaje
    getEntryDate(): Date;
    setEntryDate(value: Date): IMark;
    ExitDate: Date, // hora de salida del marcaje
    getExitDate(): Date;
    setExitDate(value: Date): IMark;
    Time: Date; // duracion del marcaje total
    getTime(): Date;
    setTime(value: Date): IMark;
    Location: any, // localizacion al marcar
    getLocation(): any;
    setLocation(value: any): IMark;
    Status: string // entrada, salida, pausa
    getStatus(): string;
    setStatus(value: string): IMark;
}

export interface IMarkDetail extends ICollectable {
    Id: string
    LastDate: Date
    getLastDate(): Date;
    setLastDate(value: Date): IMarkDetail;
    LastModification: Date
    getLastModification(): Date;
    setLastModification(value: Date): IMarkDetail;
    Status: string
    getStatus(): string;
    setStatus(value: string): IMarkDetail;
    Location: any
    getLocation(): any;
    setLocation(value: any): IMarkDetail;
}

export interface IPause {
    StartPause: Date,
    EndPause: Date
}

export interface IUser extends ICollectable {
    Name: string,
    getName(): string;
    setName(value: string): IUser;
    Lastname: string,
    getLastname(): string;
    setLastname(value: string): IUser;
    Document: string,
    getDocument(): string;
    setDocument(value: string): IUser;
    Email: string,
    getEmail(): string;
    setEmail(value: string): IUser;
    Password: string,
    getPassword(): string;
    setPassword(value: string): IUser;
    Phone: string,
    getPhone(): string;
    setPhone(value: string): IUser;
    Active: boolean,
    getActive(): boolean;
    setActive(value: boolean): IUser;
    Enterprises: string [],
    getEnterprises(): string [];
    setEnterprises(value: string[]): IUser;
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
    getValue(): string;
    setValue(value: string): ISimpleValue;
}

export enum InvoiceType { GASTO = 'gasto', VENTA = 'venta' }

export enum InvoiceStatus { A_CONTABILIZAR = 'a_contabilizar', CONTABILIZADO = 'contabilizado' }

export interface IInvoice extends ICollectable{
    Serie: IInvoiceSerie;
    getSerie(): IInvoiceSerie;
    setSerie(value: IInvoiceSerie): IInvoice;
    InvoiceNumber: string;
    getInvoiceNumber(): string;
    setInvoiceNumber(value: string): IInvoice;
    Date: Date;
    getInvoiceDate(): Date;
    setInvoiceDate(value: Date): IInvoice;
    TotalAmount: number;
    getTotalAmount(): number;
    setTotalAmount(value: number): IInvoice;
    Contact: IContact;
    getContact(): IContact;
    setContact(value: IContact): IInvoice;
    Category: IInvoiceCategory;
    getCategory(): IInvoiceCategory;
    setCategory(value: IInvoiceCategory): IInvoice;
    Lines: ICollection<IInvoiceLine>;
    getLines(): ICollection<IInvoiceLine>;
    setLines(value: ICollection<IInvoiceLine>): IInvoice;
    TransactionType: IInvoiceTransactionType;
    getTransactionType(): IInvoiceTransactionType;
    setTransactionType(value: IInvoiceTransactionType): IInvoice;
    Activity: IInvoiceActivity;
    getActivity(): IInvoiceActivity;
    setActivity(value: IInvoiceActivity): IInvoice;
    CriCaja: boolean;
    getCriCaja(): boolean;
    setCriCaja(value: boolean): IInvoice;
    RE: boolean;
    getRE(): boolean;
    setRE(value: boolean): IInvoice;
    RegAgri: boolean;
    getRegAgri(): boolean;
    setRegAgri(value: boolean): IInvoice;
    Tax: ITax;
    getTax(): ITax;
    setTax(value: ITax): IInvoice;
    TaxBase: number;
    getTaxBase(): number;
    setTaxBase(value: number): IInvoice;
    TaxQuota: number;
    getTaxQuota(): number;
    setTaxQuota(value: number): IInvoice;
    IRPF: IIRPF;
    getIRPF(): IIRPF;
    setIRPF(value: IIRPF): IInvoice;
    IRPFBase: number;
    getIRPFBase(): number;
    setIRPFBase(value: number): IInvoice;
    IRPFQuota: number;
    getIRPFQuota(): number;
    setIRPFQuota(value: number): IInvoice;
    InvoiceExpirationLines: ICollection<IInvoiceExpirationLine>;
    getInvoiceExpirationLines(): ICollection<IInvoiceExpirationLine>;
    setInvoiceExpirationLines(value: ICollection<IInvoiceExpirationLine>): IInvoice;
    Rectified: boolean;
    getRectified(): boolean;
    setRectified(value: boolean): IInvoice;
    Type: InvoiceType;
    getType(): InvoiceType;
    setType(value: InvoiceType): IInvoice;
    Status: InvoiceStatus;
    getStatus(): InvoiceStatus;
    setStatus(value: InvoiceStatus): IInvoice;
}

export interface IInvoiceLine extends ICollectable{
    Product: IProduct;
    getProduct(): IProduct;
    setProduct(value: IProduct): IInvoiceLine;
    Quantity: number;
    getQuantity(): number;
    setQuantity(value: number): IInvoiceLine;
    Price: number;
    getPrice(): number;
    setPrice(value: number): IInvoiceLine;
    Discount: number;
    getDiscount(): number;
    setDiscount(value: number): IInvoiceLine;
    TotalPrice: number;
    getTotalPrice(): number;
    setTotalPrice(value: number): IInvoiceLine;
    Tax: ITax;
    getTax(): ITax;
    setTax(value: ITax): IInvoiceLine;
    IRPF: IIRPF;
    getIRPF(): IIRPF;
    setIRPF(value: IIRPF): IInvoiceLine;
}

export interface IInvoiceExpirationLine extends ICollectable{
    Date: Date;
    getDate(): Date;
    setDate(value: Date): IInvoiceExpirationLine;
    PaymentMethod: IPaymentMethod;
    getPaymentMethod(): IPaymentMethod;
    setPaymentMethod(value: IPaymentMethod): IInvoiceExpirationLine;
    BankAccount: string;
    getBankAccount(): string;
    setBankAccount(value: string): IInvoiceExpirationLine;
    Amount: number;
    getAmount(): number;
    setAmount(value: number): IInvoiceExpirationLine;
}

export interface IInvoiceSerie extends ICollectable, ISimpleValue {}

export interface IInvoiceTransactionType extends ICollectable, ISimpleValue{}

export interface IInvoiceActivity extends ICollectable, ISimpleValue{}

export interface IIRPF extends ICollectable, ISimpleValue {
    Type: string;
    getType(): string;
    setType(value: string): IIRPF;
}

export interface ITax extends ICollectable, ISimpleValue {}

export interface IPaymentMethod extends ICollectable, ISimpleValue {}

export interface IInvoiceCategory extends ICollectable, ISimpleValue {}

export interface IProduct extends ICollectable{
    Code: IProductCode;
    getCode(): IProductCode;
    setCode(value: IProductCode): IProduct;
    Name: string;
    getName(): string;
    setName(value: string): IProduct;
    Category: IProductCategory;
    getCategory(): IProductCategory;
    setCategory(value: IProductCategory): IProduct;
    Class: IProductClass;
    getClass(): IProductClass;
    setClass(value: IProductClass): IProduct;
    Type: IProductType;
    getType(): IProductType;
    setType(value: IProductType): IProduct;
    Status: IProductStatus;
    getStatus(): IProductStatus;
    setStatus(value: IProductStatus): IProduct;
    Tax: ITax;
    getTax(): ITax;
    setTax(value: ITax): IProduct;
    IRPF: IIRPF;
    getIRPF(): IIRPF;
    setIRPF(value: IIRPF): IProduct;
    BarCode: string;
    getBarCode(): string;
    setBarCode(value: string): IProduct;
    Description: string;
    getDescription(): string;
    setDescription(value: string): IProduct;
    CostPrice: number;
    getCostPrice(): number;
    setCostPrice(value: number): IProduct;
    Benefit: number;
    getBenefit(): number;
    setBenefit(value: number): IProduct;
    Price: number;
    getPrice(): number;
    setPrice(value: number): IProduct;
    Pvp: number;
    getPvp(): number;
    setPvp(value: number): IProduct;
}

export interface IProductCode extends ICollectable, ISimpleValue {}

export interface IProductCategory extends ICollectable, ISimpleValue {}

export interface IProductClass extends ICollectable, ISimpleValue {}

export interface IProductType extends ICollectable, ISimpleValue {}

export interface IProductStatus extends ICollectable, ISimpleValue {}

export interface IContact extends ICollectable{
    Name: string;
    getName(): string;
    setName(value: string): IContact;
    ComercialName: string;
    getComercialName(): string;
    setComercialName(value: string): IContact;
    Country: string;
    getCountry(): string;
    setCountry(value: string): IContact;
    Document: string;
    getDocument(): string;
    setDocument(value: string): IContact;
    Address: string;
    getAddress(): string;
    setAddress(value: string): IContact;
    Email: string;
    getEmail(): string;
    setEmail(value: string): IContact;
    Phone: string;
    getPhone(): string;
    setPhone(value: string): IContact;
    Web: string;
    getWeb(): string;
    setWeb(value: string): IContact;
    BankAccount: string;
    getBankAccount(): string;
    setBankAccount(value: string): IContact;
    PaymentMethod: IPaymentMethod;
    getPaymentMethod(): IPaymentMethod;
    setPaymentMethod(value: IPaymentMethod): IContact;
    TransactionType: IInvoiceTransactionType;
    getTransactionType(): IInvoiceTransactionType;
    setTransactionType(value: IInvoiceTransactionType): IContact;
    IRPF: boolean;
    getIRPF(): boolean;
    setIRPF(value: boolean): IContact;
    RE: boolean;
    getRE(): boolean;
    setRE(value: boolean): IContact;
}
