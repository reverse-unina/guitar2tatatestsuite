package com.unina.tata.filemanagerTC;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.unina.tata.testsuitetranslatornew.translatorTCnew;

public class FileManagerTC {
	private static File dir;
	private static String outputname;
	public static OutputStream foutputstream;
	public static boolean flag;
	/**
	 * @param args
	 * @throws XPathExpressionException 
	 * @throws ParserConfigurationException 
	 * @throws TransformerException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, IOException, TransformerException, SAXException {
		// TODO Auto-generated method stub
		//Input 1 il guitree ottenuto dal crawler
		translatorTCnew.tataGuiTreeDocument=ExportFileIntoDocument(args[0]);
		translatorTCnew.GuitarTestCases= new ArrayList<String>();
		
		translatorTCnew.GuitarTestCaseDocument= new ArrayList<Document>();
		//Input 2 testsuite di guitar
		ExportDocumentIntoList(args[1]);
		//output il nome della testsuite Junit ottenuta
		outputname=args[2];
		//flag=Boolean.parseBoolean(args[3]);
		
		writeHeadTSFile();
		translatorTCnew.createJunitTestSuite();		
		writeEndTSFile();
	}
	
	private static Document ExportFileIntoDocument(String filePath) {
		try {
			File f = new File (System.getProperty ("java.class.path"));
			dir = f.getAbsoluteFile().getParentFile();
			File file = new File(dir.toString()+"\\"+filePath);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			
			return doc;		

		}
		catch (Exception e) {			
		    e.printStackTrace();
		    return null;
		  }		
	}
	
	private static void ExportDocumentIntoList(String dirPath) throws ParserConfigurationException, SAXException, IOException {
		File f = new File (System.getProperty ("java.class.path"));
		dir = f.getAbsoluteFile().getParentFile();
		File directory= new File (dir.toString()+"\\"+dirPath);
		String[] filenames = directory.list();
		for(int i=0; i<filenames.length;i++) {
			File file = new File(dir.toString()+"\\"+dirPath+"\\"+filenames[i]);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			translatorTCnew.GuitarTestCases.add(file.getName());
			translatorTCnew.GuitarTestCaseDocument.add(doc);
		}
		//System.out.println(directory.listFiles());
		
	}
	
	private static void writeHeadTSFile() throws IOException, TransformerException {
		java.io.InputStream head = ClassLoader.getSystemClassLoader().getResourceAsStream("text/head.txt");
//		java.io.InputStream end = ClassLoader.getSystemClassLoader().getResourceAsStream("text/end.txt");
		
		File foutput = new File(dir.toString()+"\\"+outputname);

//		OutputStream foutputstream = new BufferedOutputStream( new FileOutputStream(foutput));
		foutputstream = new BufferedOutputStream( new FileOutputStream(foutput));
		byte[] buffer = new byte[1024 * 500];
		int read_bytes = 0;
		while((read_bytes = head.read(buffer)) > 0)
			foutputstream.write(buffer, 0, read_bytes);
		
		//foutputstream.write(translatorTC.JunitTestSuite.getBytes(Charset.forName("UTF-8")));
				
//		while((read_bytes = end.read(buffer)) > 0)
//			foutputstream.write(buffer, 0, read_bytes);
//		
		
//		foutputstream.close();
		head.close();
//		end.close();
	}
	
	private static void writeEndTSFile() throws IOException, TransformerException {
		java.io.InputStream end = ClassLoader.getSystemClassLoader().getResourceAsStream("text/end.txt");
		byte[] buffer = new byte[1024 * 500];
		int read_bytes = 0;
		
		while((read_bytes = end.read(buffer)) > 0)
			foutputstream.write(buffer, 0, read_bytes);
		
		foutputstream.close();
		end.close();
	}
}
