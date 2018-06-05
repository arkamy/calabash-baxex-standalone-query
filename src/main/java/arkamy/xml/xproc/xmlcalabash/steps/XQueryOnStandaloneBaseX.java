package arkamy.xml.xproc.xmlcalabash.steps;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.basex.core.Context;
import org.basex.core.cmd.XQuery;
import org.basex.query.QueryProcessor;
import org.basex.query.value.Value;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.xmlcalabash.core.XMLCalabash;

/****************************************************************************/
/*  File:       StandaloneQuery.java                                        */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2011-08-31                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2011 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */

import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.io.ReadablePipe;
import com.xmlcalabash.io.WritablePipe;
import com.xmlcalabash.library.DefaultStep;
import com.xmlcalabash.runtime.XAtomicStep;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;


/**
 * Sample extension step to evaluate a query using BaseX.
 *
 * @author Florent Georges
 * @date   2011-08-31
 */
@XMLCalabash(
        name = "bxs:query",
        type = "{http://arkamy/xml/calabash-baxex-standalone-query}query")

public class XQueryOnStandaloneBaseX
        extends DefaultStep
{
    public XQueryOnStandaloneBaseX(XProcRuntime runtime, XAtomicStep step)
    {
        super(runtime,step);
    }

    @Override
    public void setInput(String port, ReadablePipe pipe)
    {
    	if (port.equals("query")) pipeQuery = pipe;
    	if (port.equals("source")) pipeSource = pipe;
    }

    @Override
    public void setOutput(String port, WritablePipe pipe)
    {
        pipeResult = pipe;
    }

    @Override
    public void reset()
    {
        pipeSource.resetReader();
        pipeQuery.resetReader();
        pipeResult.resetWriter();
    }

    @Override
    public void run()
            throws SaxonApiException
    {
        super.run();
        
        // TODO: There should be something more efficient than serializing
        // everything and parsing it again...  Besides, if the result is not an
        // XML document, wrap it into a c:data element. See Christian's comment on
        // http://fgeorges.blogspot.be/2011/09/writing-extension-step-for-calabash-to.html.

        // Query
        XdmNode query_doc = pipeQuery.read();
        String query_txt = query_doc.getStringValue();
        
        // Source
        org.w3c.dom.Document source = null;
        XdmNode source_doc = null;
    	if (pipeSource != null)	{    
    		source_doc =  pipeSource.read();
    		if (source_doc != null) {
	    		try {
	    			source = getDomDocument(source_doc);
				} catch (Exception e) {
					throw new XProcException("Error getting input source as a DOM Document : ", e);
				}
    		}
    	}

    	// Context
    	Context context = new Context();
    	
    	/* 
    	 * Two ways for xquery execution :
    	 * QueryProcessor for xquery with input source
    	 * XQuery for xquery with no input source
    	 */
    	String result_txt;
    	QueryProcessor qp = null;
    	try {
	    	if (source==null) {
	    		XQuery query = new XQuery(query_txt);
	    		result_txt = query.execute(context);
	    	} else {
	    		qp = new QueryProcessor(query_txt, context);
            	qp.context(source);
    			Value result = qp.value();
    			result_txt = result.toString();
    		} 
    	} catch (Exception e) {
    			throw new XProcException("Error executing a query with BaseX", e);
    	} finally {
    		if (qp!=null) qp.close();
    		context.close();			
    	}        
               
        // Write the result to the output port
        result_txt = "<c:result xmlns:c='http://www.w3.org/ns/xproc-step'>" + result_txt + "</c:result>";
        DocumentBuilder builder = runtime.getProcessor().newDocumentBuilder();
        Source src = new StreamSource(new StringReader(result_txt));
        XdmNode doc = builder.build(src);
        pipeResult.write(doc);        
    }
    
    /**
     * Get a DOM Document from a XdmNode
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public org.w3c.dom.Document getDomDocument(XdmNode node) throws SAXException, IOException, ParserConfigurationException {
        
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder db = factory.newDocumentBuilder();
        String node_txt = node.toString();
		InputSource is = new InputSource(new StringReader(node_txt));
	    return  db.parse(is);        
    }

    private ReadablePipe pipeSource = null;
    private ReadablePipe pipeQuery = null;
    private WritablePipe pipeResult = null;
}


/* ------------------------------------------------------------------------ */
/*  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS COMMENT.               */
/*                                                                          */
/*  The contents of this file are subject to the Mozilla Public License     */
/*  Version 1.0 (the "License"); you may not use this file except in        */
/*  compliance with the License. You may obtain a copy of the License at    */
/*  http://www.mozilla.org/MPL/.                                            */
/*                                                                          */
/*  Software distributed under the License is distributed on an "AS IS"     */
/*  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See    */
/*  the License for the specific language governing rights and limitations  */
/*  under the License.                                                      */
/*                                                                          */
/*  The Original Code is: all this file.                                    */
/*                                                                          */
/*  The Initial Developer of the Original Code is Florent Georges.          */
/*                                                                          */
/*  Contributor(s): none.                                                   */
/* ------------------------------------------------------------------------ */
