package com.unina.tata.testsuitetranslator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.unina.tata.filemanagerTC.FileManagerTC;

public class translatorTC {
	public static Document tataGuiTreeDocument;	
	public static ArrayList<Document> GuitarTestCaseDocument;
	public static String JunitTestSuite;
	
	
	public static void createJunitTestSuite() throws XPathExpressionException, ParserConfigurationException, IOException {
		for(int i=0; i<GuitarTestCaseDocument.size();i++){
			//String testCase="\tpublic void testTrace"+i+1+" () {\n";
			JunitTestSuite="\tpublic void testTrace"+i+" () {\n\t\t// Testing base activity\n\t\tretrieveWidgets();\n";				
			
			
			FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
			
			createJunitTestCase(i);
			
			JunitTestSuite="\t}\n\n";
			FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
		}			
	}
	
	private static void createJunitTestCase(int guitarTC) throws XPathExpressionException, IOException {
		//cerchiamo tutti gli eventi del test case di guitar
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//EventId");
		Object result = expr.evaluate(GuitarTestCaseDocument.get(guitarTC), XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;	
		for (int j=0; j<nodes.getLength(); j++){
			//System.out.println(nodes.item(j).getTextContent());
			
			XPath eventXpath = XPathFactory.newInstance().newXPath();
			XPathExpression eventExpr = eventXpath.compile("//EVENT[@id='"+nodes.item(j).getTextContent()+"']");
			Object eventResult = eventExpr.evaluate(tataGuiTreeDocument, XPathConstants.NODESET);
			NodeList eventNodes = (NodeList) eventResult;
			if(FileManagerTC.flag){
				//verifica dell'activity iniziale
				if (j==0) {
					Object initialActivityResult = ((Element) eventNodes.item(0).getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling()).getElementsByTagName("WIDGET");
					//System.out.println(((Element) eventNodes.item(0).getPreviousSibling().getPreviousSibling()).getElementsByTagName("WIDGET").getLength());
					NodeList ActivityWidgets = (NodeList) initialActivityResult;
					for (int k=0; k< ActivityWidgets.getLength();k++) {
						JunitTestSuite="\t\tdoTestWidget("+((Element) ActivityWidgets.item(k)).getAttribute("id")+", \""+
								((Element) ActivityWidgets.item(k)).getAttribute("type")+"\", \""+
								((Element) ActivityWidgets.item(k)).getAttribute("name")+"\");\n";
						FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
					}
				}				
			}
			
			
			//testing della transizione
			JunitTestSuite="\n\t\t// Testing transition \n";
			FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
			//Mettiamo gli Input
			Object InputResult = ((Element) eventNodes.item(0).getParentNode()).getElementsByTagName("INPUT");
			NodeList InputWidgets = (NodeList) InputResult;
			for(int k=0; k<InputWidgets.getLength();k++) {
				JunitTestSuite="\t\tsetInput ("+((Element) InputWidgets.item(k)).getAttribute("input_id")+", \""
						+((Element) InputWidgets.item(k)).getAttribute("input_type")+"\", \""+
						((Element) InputWidgets.item(k)).getAttribute("input_value")+"\");\n";
				FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
			}
			
			//Scateniamo l'evento
			Node widgetEventNode = ((Element) eventNodes.item(0)).getElementsByTagName("WIDGET").item(0);
			JunitTestSuite="\t\tfireEvent ("+((Element) widgetEventNode).getAttribute("id")+" ,\""
			+((Element) widgetEventNode).getAttribute("type")+"\", \""
			+((Element) eventNodes.item(0)).getAttribute("type")+"\");\n";
			FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
			
			//testiamo l'activity risultante
			JunitTestSuite="\n\t\t// Testing final activity for transition\n\t\tretrieveWidgets();\n";
			FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
			
			if(FileManagerTC.flag) {			
				
				Object finalActivityResult = ((Element) eventNodes.item(0).getNextSibling().getNextSibling()).getElementsByTagName("WIDGET");
				NodeList finalActivityWidgets = (NodeList) finalActivityResult;
				for (int k=0; k< finalActivityWidgets.getLength();k++) {
					JunitTestSuite="\t\tdoTestWidget("+((Element) finalActivityWidgets.item(k)).getAttribute("id")+", \""+
							((Element) finalActivityWidgets.item(k)).getAttribute("type")+"\", \""+
							((Element) finalActivityWidgets.item(k)).getAttribute("name")+"\");\n";
					FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
				}			
			}
			
			
//			System.out.println(((Element) eventNodes.item(0)).getElementsByTagName("WIDGET").item(0).getNodeName());
//			System.out.println(((Element) eventNodes.item(0).getParentNode()).getElementsByTagName("INPUT").getLength());
//			System.out.println(((Element) eventNodes.item(0).getNextSibling().getNextSibling()).getElementsByTagName("WIDGET").getLength());
		}
		
	}
}
	
	
