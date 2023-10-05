import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Folder, ApiFolder } from "../models/Folder";
import { Collection } from "../utils/Collection";
import { APIGenericMultipleObjectCrudRepository } from "./GenericRepository";
import { apiFolders } from "../models/Folder";
import { ApiHttpRequest } from "../utils/Http";
import { BASE_URL } from "../utils/Environment";

export class APIFolderMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<Folder> {

    constructor(){
        super(new ApiFolder(), Folder);
    }

    async get(filter?: IFilter | undefined): Promise<ICollection<Folder>> {
        let collection: ICollection<Folder> = new Collection<Folder>();
        for(let folder of apiFolders)
            collection.add(folder)
        let data = await ApiHttpRequest.get(BASE_URL + '/ms/api/contract', {}, {})
        if(data)
            data.forEach((element:any) => {
                collection.add(this.apiModel.parseDataToReceive(element))
            });
        if(collection.size() > 0){
            if(filter && filter.fields || filter?.intervalFields) collection = collection.filter(filter);
            if(filter && filter.orderBy) collection.sort(filter);
            if(filter && filter.pageItems && filter.pageNum) collection = collection.paginate(filter.pageNum,filter.pageItems);
        }
        return collection;
    }

}