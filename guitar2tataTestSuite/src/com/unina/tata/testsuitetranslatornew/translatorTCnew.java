package com.unina.tata.testsuitetranslatornew;

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

public class translatorTCnew {
	public static Document tataGuiTreeDocument;	
	public static ArrayList<Document> GuitarTestCaseDocument;
	public static ArrayList<String> GuitarTestCases;
	public static String JunitTestSuite;
	
	
	public static void createJunitTestSuite() throws XPathExpressionException, ParserConfigurationException, IOException {
		for(int i=0; i<GuitarTestCaseDocument.size();i++){
			System.out.println(i+ " of "+ GuitarTestCaseDocument.size()+ " test cases processed" );
			//String testCase="\tpublic void testTrace"+i+1+" () {\n";
			JunitTestSuite="\tpublic void testTrace"+String.format("%05d", i)+" () {" +
					"\n\t\t// Testing base activity" +
					"\n\t\t// Guitar TC: " +GuitarTestCases.get(i)+
					"\n\t\tretrieveWidgets();\n";				
			
			
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
			XPathExpression eventExpr; 
			if(nodes.item(j).getTextContent().contains("e")){
				eventExpr = eventXpath.compile("//EVENT[@id='"+nodes.item(j).getTextContent()+"']");
				Object eventResult = eventExpr.evaluate(tataGuiTreeDocument, XPathConstants.NODESET);
				NodeList eventNodes = (NodeList) eventResult;
				//Scateniamo l'evento
				Node widgetEventNode = ((Element) eventNodes.item(0)).getElementsByTagName("WIDGET").item(0);
				String index;
				if (((Element) widgetEventNode).getAttribute("index").contentEquals("")){
					index="0";
				} else {
					index=((Element) widgetEventNode).getAttribute("index");
				}
				
				String value;
				if (!((Element) eventNodes.item(0)).hasAttribute("value")) {
					value=null;
				} else {
					value ="\""+((Element) eventNodes.item(0)).getAttribute("value")+"\"";
				}
				
				JunitTestSuite="\t\tfireEvent ("+((Element) widgetEventNode).getAttribute("id")+" ,"
						+ index +	", \""
						+((Element) widgetEventNode).getAttribute("type")+"\", \""
						+((Element) widgetEventNode).getAttribute("name")+"\", \""
						+((Element) eventNodes.item(0)).getAttribute("type")+"\", "
						+ value
						+");\n";
				FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
			} else {
				eventExpr = eventXpath.compile("//INPUT[@id='"+nodes.item(j).getTextContent()+"']");
				Object eventResult = eventExpr.evaluate(tataGuiTreeDocument, XPathConstants.NODESET);
				NodeList eventNodes = (NodeList) eventResult;
				//testing della transizione
				JunitTestSuite="\n\t\t// Testing transition \n";
				FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
				//Settiamo gli Input
				JunitTestSuite="\t\tsetInput ("+((Element) eventNodes.item(0)).getAttribute("input_id")+", \""
						+((Element) eventNodes.item(0)).getAttribute("input_type")+"\", \""+
						((Element) eventNodes.item(0)).getAttribute("input_value")+"\");\n";
				FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
			}
			
			if (j<nodes.getLength()-1 && nodes.item(j).getTextContent().contains("e")) {
				//testiamo l'activity risultante
				JunitTestSuite="\n\t\t// Testing final activity for transition\n\t\tretrieveWidgets();\n";
				FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));				
			}
			
			//verifica dell'ultimo evento della ts di guitar, se termina con un input aggiungiamo in coda al caso di test l'evento relativo
			
			
			
//			System.out.println(((Element) eventNodes.item(0)).getElementsByTagName("WIDGET").item(0).getNodeName());
//			System.out.println(((Element) eventNodes.item(0).getParentNode()).getElementsByTagName("INPUT").getLength());
//			System.out.println(((Element) eventNodes.item(0).getNextSibling().getNextSibling()).getElementsByTagName("WIDGET").getLength());
		}

		if (nodes.item(nodes.getLength()-1).getTextContent().contains("i")){
			XPath eventXpath = XPathFactory.newInstance().newXPath();
			XPathExpression eventExpr = eventXpath.compile("//INPUT[@id='"+nodes.item(nodes.getLength()-1).getTextContent()+"']");
			Object eventResult = eventExpr.evaluate(tataGuiTreeDocument, XPathConstants.NODESET);
			NodeList eventNodes = (NodeList) eventResult;
			
			JunitTestSuite="\n\t\t// Testing transition \n";
			FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
			
			Node widgetEventNode = ((Element) eventNodes.item(0).getParentNode().getNextSibling().getNextSibling()).getElementsByTagName("WIDGET").item(0);
			String index;
			if (((Element) widgetEventNode).getAttribute("index").contentEquals("")){
				index="0";
			} else {
				index=((Element) widgetEventNode).getAttribute("index");
			}
			
			String value;
			if (!((Element) eventNodes.item(0)).hasAttribute("value")) {
				value=null;
			} else {
				value ="\""+((Element) eventNodes.item(0)).getAttribute("value")+"\"";
			}
			
			JunitTestSuite="\t\tfireEvent ("+((Element) widgetEventNode).getAttribute("id")+" ,"
					+ index +	", \""
					+((Element) widgetEventNode).getAttribute("type")+"\", \""
					+((Element) widgetEventNode).getAttribute("name")+"\", \""
					+((Element) eventNodes.item(0)).getAttribute("type")+"\", "
					+ value
					+");\n";
			FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
			
		}
	}
	
	private static void createJunitTestCaseOLD(int guitarTC) throws XPathExpressionException, IOException {
		//cerchiamo tutti gli eventi del test case di guitar
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("//EventId");
		Object result = expr.evaluate(GuitarTestCaseDocument.get(guitarTC), XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;	
		for (int j=0; j<nodes.getLength(); j++){
			//System.out.println(nodes.item(j).getTextContent());
			
			XPath eventXpath = XPathFactory.newInstance().newXPath();
			XPathExpression eventExpr; 
			if(nodes.item(j).getTextContent().contains("e")){
				eventExpr = eventXpath.compile("//EVENT[@id='"+nodes.item(j).getTextContent()+"']");
			} else {
				eventExpr = eventXpath.compile("//INPUT[@id='"+nodes.item(j).getTextContent()+"']");
			}
			
			Object eventResult = eventExpr.evaluate(tataGuiTreeDocument, XPathConstants.NODESET);
			NodeList eventNodes = (NodeList) eventResult;
			
			
			//testing della transizione
			JunitTestSuite="\n\t\t// Testing transition \n";
			FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
			//Mettiamo gli Input
			if(nodes.item(j).getTextContent().contains("i")) {
				JunitTestSuite="\t\tsetInput ("+((Element) eventNodes.item(0)).getAttribute("input_id")+", \""
						+((Element) eventNodes.item(0)).getAttribute("input_type")+"\", \""+
						((Element) eventNodes.item(0)).getAttribute("input_value")+"\");\n";
				FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
			} else {
				//Scateniamo l'evento
				Node widgetEventNode = ((Element) eventNodes.item(0)).getElementsByTagName("WIDGET").item(0);
				JunitTestSuite="\t\tfireEvent ("+((Element) widgetEventNode).getAttribute("id")+" ,\""
				+((Element) widgetEventNode).getAttribute("type")+"\", \""
				+((Element) eventNodes.item(0)).getAttribute("type")+"\");\n";
				FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));
			}			
			if (j<nodes.getLength()-1 && nodes.item(j).getTextContent().contains("e")) {
				//testiamo l'activity risultante
				JunitTestSuite="\n\t\t// Testing final activity for transition\n\t\tretrieveWidgets();\n";
				FileManagerTC.foutputstream.write(JunitTestSuite.getBytes(Charset.forName("UTF-8")));				
			}
			
			
			
//			System.out.println(((Element) eventNodes.item(0)).getElementsByTagName("WIDGET").item(0).getNodeName());
//			System.out.println(((Element) eventNodes.item(0).getParentNode()).getElementsByTagName("INPUT").getLength());
//			System.out.println(((Element) eventNodes.item(0).getNextSibling().getNextSibling()).getElementsByTagName("WIDGET").getLength());
		}
		
	}
}
	
	
