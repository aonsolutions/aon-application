import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Folder, ApiFolder } from "../models/Folder";
import { Collection } from "../utils/Collection";
import { APIGenericMultipleObjectCrudRepository } from "./GenericRepository";
import { apiFolders } from "../models/Folder";

export class APIFolderMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<Folder> {

    constructor(){
        super(new ApiFolder(), Folder);
    }

    async get(filter?: IFilter | undefined): Promise<ICollection<Folder>> {
        let collection: ICollection<Folder> = new Collection<Folder>();
        for(let folder of apiFolders)
            collection.add(folder)
        collection.copyArrayToCollection((await super.get(filter)).toArray())
        if(collection.size() > 0){
            if(filter && filter.fields || filter?.intervalFields) collection = collection.filter(filter);
            if(filter && filter.orderBy) collection.sort(filter);
        }
        return collection;
    }

}