package com.code.aon.web.help.pdf;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchPDFFiles {
	
	private static IndexSearcher createSearcher(URI uri) throws IOException {
		Directory dir = FSDirectory.open(Paths.get(uri));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}	


	private static TopDocs searchBy(String f, String query, IndexSearcher searcher) throws Exception {
	    QueryParser qp = new QueryParser(f, new StandardAnalyzer());
	    Query fQuery = qp.parse(query);
	    TopDocs hits = searcher.search(fQuery, 10);
	    return hits;
	}	  

	public static void main(String[] args) throws Exception {
	    
		IndexSearcher searcher = createSearcher(SearchPDFFiles.class.getResource(".").toURI());
		
	    //Search by Contents
	    TopDocs foundDocs = searchBy("contents", "trabajador", searcher);
	     
	    System.out.println("Total Results :: " + foundDocs.totalHits);
	     
	    for (ScoreDoc sd : foundDocs.scoreDocs) 
	    {
	      Document d = searcher.doc(sd.doc);
	      
	      for ( String value : d.getValues("contents") ) 
	    	  System.out.println(value);
	      //System.out.println(String.format(d.get("contents")));
	    }
	     
	}
	
	
}
