import { IEnterprise, IModel, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { GET_MULTIPLE, GET_SINGLE, GET_METHOD } from "../utils/Environment";
import { KeyGenerator } from "../utils/KeyGenerator";
import { ErrorResponse } from "../utils/Response";

export class Enterprise implements IEnterprise, IModel {
    private domainName: string;
    private domainId: string;
    private name: string;
    private profilePhoto: string;
    private address: string;
    private country: string;
    private province: string;
    private socialReason: string;
    private email: string;
    private phone: string;
    private website: string;
    private document: string;
    private registry: string;
    private key: string;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Registry(): string {
        return this.registry;
    }

    public set Registry(value: string) {
        this.registry = value;
    }

    public get DomainName(): string{
        return this.domainName;
    }

    public set DomainName(value: string){
        this.domainName = value;
    }

    public get DomainId(): string {
        return this.domainId;
    }

    public set DomainId(value: string) {
        this.domainId = value;
    }

    public get Name(): string {
      return this.name;
    }

    public set Name(value: string) {
      this.name = value;
    }

    public get Address(): string {
      return this.address;
    }

    public set Address(value: string) {
      this.address = value;
    }

    public get Country(): string {
      return this.country;
    }

    public set Country(value: string) {
      this.country = value;
    }

    public get Province(): string {
      return this.province;
    }

    public set Province(value: string) {
      this.province = value;
    }

    public get SocialReason(): string {
      return this.socialReason;
    }

    public set SocialReason(value: string) {
      this.socialReason = value;
    }

    public get Email(): string {
      return this.email;
    }

    public set Email(value: string) {
      this.email = value;
    }

    public get Phone(): string {
      return this.phone;
    }

    public set Phone(value: string) {
      this.phone = value;
    }

    public get Website(): string {
      return this.website;
    }

    public set Website(value: string) {
      this.website = value;
    }

    public get ProfilePhoto(): string {
        return this.profilePhoto;
    }

    public set ProfilePhoto(value: string) {
        this.profilePhoto = value;
    }

    public get Document(): string {
        return this.document;
    }

    public set Document(value: string) {
        this.document = value;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(key: string) {
        this.key = key;
    }

    constructor(name?: string, document?: string, profilePhoto?: string, address?: string, country?: string, province?: string, socialReason?: string, email?: string, phone?: string, website?: string, registry?: string, key?: string) {
        this.name = name || '';
        this.profilePhoto = profilePhoto || '';
        this.address = address || '';
        this.country = country || '';
        this.province = province || '';
        this.socialReason = socialReason || '';
        this.email = email || '';
        this.phone = phone || '';
        this.website = website || '';
        this.document = document || document || '';
        this.key = KeyGenerator.generate(15);
        this.domainId = '';
        this.domainName = '';
        this.registry = '';
    }
    getName(): string {
        return this.name;
    }
    setName(value: string): IEnterprise {
        this.name = value;
        return this
    }
    getProfilePhoto(): string {
        return this.profilePhoto;
    }
    setProfilePhoto(value: string): IEnterprise {
        this.profilePhoto = value;
        return this
    }
    getAdress(): string {
        return this.address;
    }
    setAdress(value: string): IEnterprise {
        this.address = value;
        return this
    }
    getCountry(): string {
        return this.country;
    }
    setCountry(value: string): IEnterprise {
        this.country = value;
        return this
    }
    getProvince(): string {
        return this.province;
    }
    setProvince(value: string): IEnterprise {
        this.province = value;
        return this
    }
    getSocialReason(): string {
        return this.socialReason;
    }
    setSocialReason(value: string): IEnterprise {
        this.socialReason = value;
        return this
    }
    getEmail(): string {
        return this.email;
    }
    setEmail(value: string): IEnterprise {
        this.email = value;
        return this
    }
    getPhone(): string {
        return this.phone;
    }
    setPhone(value: string): IEnterprise {
        this.phone = value;
        return this
    }
    getWebsite(): string {
        return this.website;
    }
    setWebsite(value: string): IEnterprise {
        this.website = value;
        return this
    }
    getDocument(): string {
        return this.document;
    }
    setDocument(value: string): IEnterprise {
        this.document = value;
        return this
    }
    getDomainName(): string {
        return this.domainName;
    }
    setDomainName(value: string): IEnterprise {
        this.domainName = value;
        return this
    }
    getDomainId(): string {
        return this.domainId;
    }
    setDomainId(value: string): IEnterprise {
        this.domainId = value;
        return this
    }
    getRegistry(): string {
        return this.registry;
    }
    setRegistry(value: string): IEnterprise {
        this.registry = value;
        return this
    }

    getKey(): string {
        return this.Key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('document', this.Document);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('document', this.Document);
        return map;
    }

}

export class ApiEnterprise extends Enterprise implements IApiModel {
    getUrl(currentMethod: string, filter: IFilter): string [] {
        if(currentMethod == GET_MULTIPLE)
            return ['/ms/api/company'];
        if(currentMethod == GET_SINGLE)
            return ['/ms/api/company/one?id=' + filter.fields?.get('id')];
        throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE)
            return GET_METHOD;
        if(currentMethod == GET_SINGLE)
            return GET_METHOD;
        throw new ErrorResponse('0199')
    }

    localFilter(): boolean {
        return false;
    }

    parseDataToSend(data: any): any {
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any, currentMethod: string): any {
        if(currentMethod == GET_MULTIPLE){
            if(data.type == 'CONSULTANCY') return new Enterprise();
            let enterprise = new Enterprise();
            enterprise.ApiObject = data;
            enterprise.Document = data.document ? data.document : KeyGenerator.generate(9);
            enterprise.Name = data.name ? data.name : ''
            enterprise.Key = data.id ? data.id : '';
            enterprise.DomainName = data.domain ? data.domain : '';
            enterprise.DomainId = data.id ? data.id : '';
            enterprise.Registry = data.registry ? data.registry : '';
            return enterprise;
        }else if (currentMethod == GET_SINGLE){
            let enterprise = new Enterprise();
            enterprise.ApiObject = data;
            enterprise.Address = data.address.address
            enterprise.Country = data.address.country
            enterprise.Document = data.document
            enterprise.DomainId = data.domain.id
            enterprise.DomainName = data.domain.name
            enterprise.Email = '' // TO DO
            enterprise.Key = data.id
            enterprise.Name = data.name
            enterprise.Phone = '' // TO DO
            enterprise.ProfilePhoto =
            enterprise.Province = data.address.province
            enterprise.Registry = data.id
            enterprise.SocialReason = '' // TO DO
            enterprise.Website = '' // TO DO
            return enterprise;
        }
        throw new ErrorResponse('0199')
    }
}

export class StorableEnterprise extends Enterprise implements IStorable<Enterprise> {
    getCollection(): ICollection<Enterprise> {
        return enterprises;
    }
    getLocalStorage(): string {
        return 'enterprises';
    }
}

export let enterprises: ICollection<Enterprise> = new Collection<Enterprise>();
export function setEnterprises(value: any) { enterprises = value; };