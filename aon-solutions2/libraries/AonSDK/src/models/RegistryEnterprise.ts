import { IRegistryEnterprise, IModel, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { KeyGenerator } from "../utils/KeyGenerator";

export class RegistryEnterprise implements IRegistryEnterprise, IModel {
  private id: string;
  private idEnterprise: string;
  private description: string;
  private dateCreation: Date;
  private dateRegistration: Date;
  private notary: string;
  private protocol: string;
  private inscription: string;
  private key: string;
  protected apiObject: any;

  constructor(idEnterprise?: string, description?: string, dateCreation?: Date, dateRegistration?: Date, notary?: string, protocol?: string, inscription?: string, key?: string) {
    this.id = KeyGenerator.generate(15);
    this.idEnterprise = idEnterprise || '';
    this.description = description || '';
    this.dateCreation = dateCreation || new Date();
    this.dateRegistration = dateRegistration || new Date();
    this.notary = notary || '';
    this.protocol = protocol || '';
    this.inscription = inscription || '';
    this.key = this.idEnterprise;
  }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

  public get Id(): string {
    return this.id;
  }

  public set Id(value: string) {
    this.id = value;
  }

  public get IdEnterprise(): string {
    return this.idEnterprise;
  }

  public set IdEnterprise(value: string) {
    this.idEnterprise = value;
  }

  public get Description(): string {
    return this.description;
  }

  public set Description(value: string) {
    this.description = value;
  }

  public get DateCreation(): Date {
    return this.dateCreation;
  }

  public set DateCreation(value: Date) {
    this.dateCreation = value;
  }

  public get DateRegistration(): Date {
    return this.dateRegistration;
  }

  public set DateRegistration(value: Date) {
    this.dateRegistration = value;
  }

  public get Notary(): string {
    return this.notary;
  }

  public set Notary(value: string) {
    this.notary = value;
  }

  public get Protocol(): string {
    return this.protocol;
  }

  public set Protocol(value: string) {
    this.protocol = value;
  }

  public get Inscription(): string {
    return this.inscription;
  }

  public set Inscription(value: string) {
    this.inscription = value;
  }

  public get Key(): string {
    return this.key;
  }

  public set Key(value: string) {
    this.key = value;
  }

  getKey(): string {
    return this.idEnterprise;
  }

  getFilterableFields(): Map<string, any> {
    let map = new Map<string, any>();
    map.set('id', this.Id);
    map.set('idEnterprise', this.IdEnterprise);
    map.set('description', this.Description);
    map.set('dateCreation', this.DateCreation);
    map.set('dateRegistration', this.DateRegistration);
    map.set('notary', this.Notary);
    map.set('protocol', this.Protocol);
    map.set('inscription', this.Inscription);
    return map;
  }

  getSortableFields(): Map<string, any> {
    let map = new Map<string, any>();
    map.set('id', this.Id);
    map.set('idEnterprise', this.IdEnterprise);
    map.set('description', this.Description);
    map.set('dateCreation', this.DateCreation);
    map.set('dateRegistration', this.DateRegistration);
    map.set('notary', this.Notary);
    map.set('protocol', this.Protocol);
    map.set('inscription', this.Inscription);
    return map;
  }

}

export class StorableRegistryEnterprise extends RegistryEnterprise implements IStorable<RegistryEnterprise> {
  getCollection(): ICollection<RegistryEnterprise> {
    return registryEnterprises;
  }
  getLocalStorage(): string {
      return 'registryEnterprises';
  }
}

export let registryEnterprises : ICollection<RegistryEnterprise> = new Collection<RegistryEnterprise>();
export function setRegistryEnterprises(value: any) { registryEnterprises = value; };