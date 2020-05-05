import { Observable } from 'rxjs';

export interface IImportTypeVisitor {
    visitCSV(item: IImportType, data: any): Observable<any>;
    visitXLSX(item: IImportType, data: any): Observable<any>;
    visitEXCEL(item: IImportType, data: any): Observable<any>;
    visitMS_EXCEL(item: IImportType, data: any): Observable<any>;
    visitODS(item: IImportType, data: any): Observable<any>;
    visitPDF(item: IImportType, data: any): Observable<any>;
    visitHTML(item: IImportType, data: any): Observable<any>;
    visitTEXT(item: IImportType, data: any): Observable<any>;
    visitGIF(item: IImportType, data: any): Observable<any>;
    visitJPEG(item: IImportType, data: any): Observable<any>;
    visitPNG(item: IImportType, data: any): Observable<any>;
    visitOGG(item: IImportType, data: any): Observable<any>;
    visitMP4(item: IImportType, data: any): Observable<any>;
    visitMPEG(item: IImportType, data: any): Observable<any>;
    visitOther(item: IImportType, data: any): Observable<any>;
}

export interface IImportTypeAccepter {
    acceptCSV(item: IImportType);
    acceptXLSX(item: IImportType);
    acceptEXCEL(item: IImportType);
    acceptMS_EXCEL(item: IImportType);
    acceptODS(item: IImportType);
    acceptPDF(item: IImportType);
    acceptHTML(item: IImportType);
    acceptTEXT(item: IImportType);
    acceptGIF(item: IImportType);
    acceptJPEG(item: IImportType);
    acceptPNG(item: IImportType);
    acceptOGG(item: IImportType);
    acceptMP4(item: IImportType);
    acceptMPEG(item: IImportType);
    acceptOther(item: IImportType);
}

export interface IImportType {
    mimeType: string;
    icon: string;
    friendlyType: string;
    accept(accepter: IImportTypeAccepter): boolean;
    visit( visitor: IImportTypeVisitor, data: any): Observable<any>;
    isMyType( mimeType: string): boolean;
}

abstract class BasicType implements IImportType {
    abstract mimeType;
    abstract icon;
    abstract friendlyType;
    public abstract accept( visitor: IImportTypeAccepter): boolean;
    public abstract visit( visitor: IImportTypeVisitor, data: any): Observable<any>;
    public isMyType( mimeType: string): boolean {
        return this.mimeType === mimeType;
    }
}

class OtherType extends BasicType {
    readonly mimeType = 'other';
    readonly icon = 'not_interested';
    readonly friendlyType = 'Desconocido';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptOther( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
       return visitor.visitOther( this , data);
    }
}

class CSVType extends BasicType {
    public readonly mimeType = 'text/csv';
    public readonly icon = 'waves';
    public readonly friendlyType = 'Datos separados por comas';

    public accept( accepter: IImportTypeAccepter) {
        return accepter.acceptCSV( this );
    }
    public visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitCSV( this, data);
    }
}

class XLSXType extends BasicType {
    readonly mimeType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
    readonly icon = 'grid_on';
    readonly friendlyType = 'MS Office Excel';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptXLSX( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitXLSX(this, data);
    }
}

class EXCELType extends BasicType {
    readonly mimeType = 'application/excel';
    readonly icon = 'grid_on';
    readonly friendlyType = 'MS Office Excel';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptEXCEL( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitEXCEL(this, data);
    }
}

class MSEXCELType extends BasicType {
    readonly mimeType = 'application/vnd.ms-excel';
    readonly icon = 'grid_on';
    readonly friendlyType = 'MS Office Excel';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptMS_EXCEL( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitMS_EXCEL(this, data);
    }
}

class ODSType extends BasicType {
    readonly mimeType = 'application/vnd.oasis.opendocument.spreadsheet';
    readonly icon = 'grid_on';
    readonly friendlyType = 'OpenDocument Hoja de cálculo';

    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptODS( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitODS(this, data);
    }
}

class PDFType extends BasicType {
    readonly mimeType = 'application/pdf';
    readonly icon = 'picture_as_pdf';
    readonly friendlyType = 'Adobe PDF';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptPDF( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitPDF(this, data);
    }
}

class HTMLType extends BasicType {
    readonly mimeType = 'text/html';
    readonly icon = 'settings_ethernet';
    readonly friendlyType = 'Documento HTML';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptHTML( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitHTML(this, data);
    }
}

class TEXTType extends BasicType {
    readonly mimeType = 'text/plain';
    readonly icon = 'view_headline';
    readonly friendlyType = 'Documento de texto';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptTEXT( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitTEXT(this, data);
    }
}

class GIFType extends BasicType {
    readonly mimeType = 'image/gif';
    readonly icon = 'image';
    readonly friendlyType = 'Imagen GIF';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptGIF( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitGIF(this, data);
    }
}

class JPEGType extends BasicType {
    readonly mimeType = 'image/jpeg';
    readonly icon = 'image';
    readonly friendlyType = 'Imagen JPG';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptJPEG( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitJPEG(this, data );
    }
}

class PNGType extends BasicType {
    readonly mimeType = 'image/png';
    readonly icon = 'image';
    readonly friendlyType = 'Imagen PNG';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptPNG( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitPNG(this, data);
    }
}

class OGGType extends BasicType {
    readonly mimeType = 'video/ogg';
    readonly icon = 'videocam';
    readonly friendlyType = 'Video OGG';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptOGG( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitOGG(this, data);
    }
}

class MP4Type extends BasicType {
    readonly mimeType = 'video/mp4';
    readonly icon = 'videocam';
    readonly friendlyType = 'Video OGG';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptMP4( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitMP4(this, data);
    }
}

class MPEGType extends BasicType {
    readonly mimeType = 'audio/mpeg';
    readonly icon = 'music_video';
    readonly friendlyType = 'Audio';
    accept( accepter: IImportTypeAccepter) {
        return accepter.acceptMPEG( this );
    }
    visit( visitor: IImportTypeVisitor, data: any) {
        return visitor.visitMPEG(this, data);
    }
}

class ImportTypeAccepter implements IImportTypeAccepter {
    acceptCSV(item: IImportType) {return true; }
    acceptXLSX(item: IImportType) {return true; }
    acceptEXCEL(item: IImportType) {return true; }
    acceptMS_EXCEL(item: IImportType) {return true; }
    acceptODS(item: IImportType) {return true; }
    acceptPDF(item: IImportType) {return true; }
    acceptHTML(item: IImportType) {return false; }
    acceptTEXT(item: IImportType) {return true; }
    acceptGIF(item: IImportType) {return true; }
    acceptJPEG(item: IImportType) {return true; }
    acceptPNG(item: IImportType) {return true; }
    acceptOGG(item: IImportType) {return false; }
    acceptMP4(item: IImportType) {return false; }
    acceptMPEG(item: IImportType) {return false; }
    acceptOther(item: IImportType) {return false; }
}

export module  ImportTypeUtils {

    const types: IImportType[] = [
        new CSVType(),
        new XLSXType(),
        new EXCELType(),
        new MSEXCELType(),
        new ODSType(),
        new PDFType(),
        new HTMLType(),
        new TEXTType(),
        new GIFType(),
        new JPEGType(),
        new PNGType(),
        new OGGType(),
        new MP4Type(),
        new MPEGType()
    ];
    const otherType = new OtherType();

    export function get(mimeType: string): IImportType {
        for (const type of types) {
            if (type.isMyType(mimeType)) {
                return type;
            }
        }
        return otherType;
    }
}
