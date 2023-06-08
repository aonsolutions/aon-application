import { Deserializable } from '../interface/deserializable';

export class Enterprise implements Deserializable {
  //TODO: Investigar si debo hacer una interfaz para la clase enterprise
  //TODO: Comprobar si los datos deben poder ser indefinidos
  //TODO: Cuando deserializo un objeto con object assign, copia los atributos existentes y los no existentes que vengan en el objeto de la api
  //      mirar si este comportamiento es correcto o cambiar a una deserialización en la que solo copie los atributos existentes en nuestro
  //      modelo.
  private withholding?: boolean;
  private surcharge?: boolean;
  private registry?: number;
  private parent?: boolean;
  private shared?: boolean;
  private administration?: string;
  private document?: string;
  private active?: boolean;
  private type?: string;
  private parentId?: number;
  private vatAccrualPayment?: boolean;
  private domain?: string;
  private name?: string;
  private id?: number;

  constructor() {
  }

  deserialize(input: any): this {
    Object.assign(this, input);
    return this;
  }

  get Id(): number | undefined{
    return this.id;
  }

  set Id(id: number | undefined) {
    if(id)
      this.id = +id;
    else
      this.id = undefined;
  }

  get Name(): any{
    return this.name || undefined;
  }

  set Name(name: any) {
    this.name = name;
  }

  get Domain(): any{
    return this.domain || undefined;
  }

  set Domain(domain: any) {
    this.domain = domain;
  }

  get VatAccrualPayment(): any{
    return this.vatAccrualPayment || undefined;
  }

  set VatAccrualPayment(vatAccrualPayment: any) {
    this.vatAccrualPayment = vatAccrualPayment;
  }

  get ParentId(): any{
    return this.parentId || undefined;
  }

  set ParentId(parentId: any) {
    this.parentId = parentId;
  }

  get Type(): any{
    return this.type || undefined;
  }

  set Type(type: any) {
    this.type = type;
  }

  get Active(): any{
    return this.active || undefined;
  }

  set Active(active: any) {
    this.active = active;
  }

  get Document(): any{
    return this.document || undefined;
  }

  set Document(document: any) {
    this.document = document;
  }

  get Administration(): any{
    return this.administration || undefined;
  }

  set Administration(administration: any) {
    this.administration = administration;
  }

  get Shared(): any{
    return this.shared || undefined;
  }

  set Shared(shared: any) {
    this.shared = shared;
  }

  get Parent(): any{
    return this.parent || undefined;
  }

  set Parent(parent: any) {
    this.parent = parent;
  }

  get Registry(): any{
    return this.registry || undefined;
  }

  set Registry(registry: any) {
    this.registry = registry;
  }

  get Surcharge(): any{
    return this.surcharge || undefined;
  }

  set Surcharge(surcharge: any) {
    this.surcharge = surcharge;
  }

  get Withholding(): any{
    return this.withholding || undefined;
  }

  set Withholding(withholding: any) {
    this.withholding = withholding;
  }

}
