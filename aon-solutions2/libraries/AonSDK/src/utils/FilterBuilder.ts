import { IFilter } from "../interfaces/utilitiesInterfaces";

export class FilterBuilder {
    private filter: IFilter = {};

    constructor(){
    }

    private setSelectedFields(fields: string[]): void {
        this.filter.selectedFields = fields;
    }
    /**
     * Set the page number and page items to paginate
     * @param pageNum The page number
     * @param pageItems The number of elements for page
     */
    setPageNumAndItems(pageNum: number, pageItems: number): void {
        this.filter.pageNum = pageNum;
        this.filter.pageItems = pageItems;
    }
    /**
     * Add new field to filter
     * @param field The field to filter as string
     * @param value The value of the field
     */
    addField(field: string, value:any): void {
        if(!this.filter.fields) this.filter.fields = new Map<string,any>();
        if(!this.filter.fields?.has(field)) this.filter.fields?.set(field, []);
        this.filter.fields?.get(field)?.push(value);
    }
    /**
     * Add new interval field to filter
     * @param field The field to filter as string
     * @param startValue Initial value of interval
     * @param endValue Last value of interval
     */
    addInterval(field: string, startValue: any, endValue: any): void {
        if(!this.filter.intervalFields) this.filter.intervalFields = new Map<string,any>();
        this.filter.intervalFields?.set(field, {
            start: startValue,
            end: endValue
        });
    }
    /**
     * Set the order to sort
     * @param field The field to filter as string
     * @param order The order to sort. Can be 'asc' or 'desc'
     */
    addOrder(field:string, order: string): void {
        if(!this.filter.orderBy) this.filter.orderBy = new Map<string,string>();
        this.filter.orderBy?.set(field, order);
    }
    /**
     * Get the IFilter object builded
     * @returns The filter as IFilter
     */
    getFilter(): IFilter {
        return this.filter;
    }
    /**
     * Clear all the filter
     */
    clearAll(): void {
        this.filter = {};
    }
    /**
     * Clear fields of filter
     */
    clearFields(): void {
        delete this.filter.fields;
    }
    /**
     * Clear interval fields of filter
     */
    clearIntervals(): void {
        delete this.filter.intervalFields;
    }

    private clearSelectedFields(): void {
        delete this.filter.selectedFields;
    }
    /**
     * Clear page number and page items
     */
    clearPageNumAndItems(): void {
        delete this.filter.pageNum;
        delete this.filter.pageItems;
    }
}