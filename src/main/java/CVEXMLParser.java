
import rmlparser.XMLParser;
import linkingGenerator.CVELinking2;
import rmlparser.XMLParserJena;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.io.SocketOutputBuffer;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.rdf4j.common.io.IOUtil;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class CVEXMLParser {

    public static void main(String[] args) throws Exception {
    	String RMLFile = "rml/nvdcvenew-complete.rml";
    	String CVEXML = "./input/cve-sample2.xml";
    	String CPE ="output/cpe_w3id.ttl";
    	String CWE ="output/1000-cwe_w3id.ttl";
    	parseCVE(CVEXML,RMLFile,CPE,CWE);
    	//parseCVETOCPE(CVEXML, CPE);
    	//parseCVETOCWE(CVEXML, CWE);
    	
    }
    
    public static void parseCVE(String CVEXMLFile, String RMLFile, String CPERDFFile, String CWERDFFile) throws Exception {

        //String xmlFileName = "./input/cvedecade/nvdcve-2.0-2018.xml";
        String fileName = CVEXMLFile.substring(CVEXMLFile.lastIndexOf("/") + 1);
        //System.out.println(fileName);System.exit(0);
        org.apache.jena.rdf.model.Model CVEModel = XMLParserJena.Parse(CVEXMLFile, RMLFile);  
        //CVEModel.write(System.out,"TURTLE");System.exit(0);
        
    	org.apache.jena.rdf.model.Model CVETOCPE = parseCVETOCPE(CVEModel, CPERDFFile, fileName);
    	org.apache.jena.rdf.model.Model CVETOCWE = parseCVETOCWE(CVEModel, CWERDFFile, fileName);
    	
    	org.apache.jena.rdf.model.Model allCVE = CVEModel.union(CVETOCPE).union(CVETOCWE);
    	
    	//remove unnecessary triples (literal cpeId & cweId)
    	Property cpeId = allCVE.createProperty("http://w3id.org/sepses/vocab/ref/cve#cpeId");
    	Property cweId = allCVE.createProperty("http://w3id.org/sepses/vocab/ref/cve#cweId");
     	allCVE.removeAll(null,cpeId, null);
    	allCVE.removeAll(null,cweId, null);
       
    	//get an output file out of the model
    	String allCVEfileName = "output/"+fileName+"-output.ttl";
        //String cveModelfileName = "output/"+fileName+"-output-basic.ttl";
        FileWriter out = new FileWriter(allCVEfileName);
       // FileWriter out = new FileWriter(cveModelfileName);
         
        try {
        	allCVE.write(out,"TURTLE");
        	//CVEModel.write(out,"TURTLE");
        	
        	
        }
        finally {
           CVEModel.close();
		   CVETOCPE.close();
		   CVETOCWE.close();
        }
    }
    
    public static  org.apache.jena.rdf.model.Model parseCVETOCPE(org.apache.jena.rdf.model.Model CVEModel, String CPERDFFile, String fileName) throws Exception {

       
        //has linking to CPE
        org.apache.jena.rdf.model.Model CPEModel = ModelFactory.createDefaultModel() ;
		CPEModel.read(CPERDFFile) ;
		
		
       // org.apache.jena.rdf.model.Model testCVELinking = CVELinking.generateLinking(CVEModel, CPEModel, CWEModel, fileName);
		 org.apache.jena.rdf.model.Model testCVETOCPELinking = CVELinking2.generateLinkingCVETOCPE(CVEModel, CPEModel, fileName);
		         
		// testCVETOCPELinking.write( System.out,"TURTLE");
	        
        //call CVETOCPELingking
       /* String LinkingFileNameCPE = "output/linking/"+fileName+"-linkingCVETOCPE.ttl";
        FileWriter outCVELinkingCPE = new FileWriter(LinkingFileNameCPE);
         
        try {
        	testCVETOCPELinking.write(outCVELinkingCPE,"TURTLE");
        	
        }
        finally {
           CVEModel.close();
		   CPEModel.close();
        }*/
		 
		 return testCVETOCPELinking;

    }
        
    public static  org.apache.jena.rdf.model.Model parseCVETOCWE(org.apache.jena.rdf.model.Model CVEModel, String CWERDFFile, String fileName) throws Exception {

     
		//has linking to CWE
		org.apache.jena.rdf.model.Model CWEModel = ModelFactory.createDefaultModel() ;
		CWEModel.read(CWERDFFile) ;
        
       // org.apache.jena.rdf.model.Model testCVELinking = CVELinking.generateLinking(CVEModel, CPEModel, CWEModel, fileName);
		 org.apache.jena.rdf.model.Model testCVETOCWELinking = CVELinking2.generateLinkingCVETOCWE(CVEModel, CWEModel, fileName);
		         
		 //testCVETOCWELinking.write( System.out,"TURTLE");
	        
        //call CVETOCPELingking
		/* String LinkingFileNameCWE = "output/linking/"+fileName+"-linkingCWE.ttl";
		 FileWriter outCVELinkingCWE = new FileWriter(LinkingFileNameCWE);
         
		 try {
			 testCVETOCWELinking.write(outCVELinkingCWE,"TURTLE");
        	
		 }
        finally {
           CVEModel.close();
		   CWEModel.close();
        }*/
		 
		 return testCVETOCWELinking;

    }
        

    }


    
   


