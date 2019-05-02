package net.Ildar.DatabaseSync;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Works with XML documents
 */
public class XML implements AutoCloseable {
    /**
     * Input or output stream
     */
    private Object fileStream = null;

    /**
     * The set of all possible states of XML object
     */
    private enum Mode {READ, WRITE, NONE}

    ;
    /**
     * Current state of object.
     *
     * @see Mode
     */
    private Mode mode = Mode.NONE;


    private Document document;
    private Element rootElement;
    private NodeList jobList;
    private int nodeIndex;

    @Override
    public void close() throws XMLException {
        if (fileStream != null) {
            try {
                ((Closeable) fileStream).close();
            } catch (IOException e) {
                throw new XMLException("I/O error", e);
            }
        }
    }

    /**
     * opens XML file, you can read data with next() function
     *
     * @param filePath path to the XML file
     * @throws XMLException when any error occured
     * @see XML#next()
     */
    public void open(String filePath) throws XMLException {
        try {
            fileStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new XMLException("File not found", e);
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XMLException("XML configuration error", e);
        }
        try {
            document = documentBuilder.parse(filePath);
        } catch (SAXException e) {
            throw new XMLException("SAX error", e);
        } catch (IOException e) {
            throw new XMLException("I/O error", e);
        }
        Element rootElement = document.getDocumentElement();
        if (rootElement == null || !rootElement.getNodeName().equals("jobs"))
            throw new XMLException("Invalid structure in XML file");
        jobList = rootElement.getChildNodes();
        nodeIndex = 0;
        mode = Mode.READ;
    }

    /**
     * get next element from XML document, which was previously opened.
     *
     * @return Job object or null if there are no elements
     * @throws XMLException when any error occured
     * @see XML#open(String)
     * @see XML#next()
     */
    public Job next() throws XMLException {
        if (mode != Mode.READ)
            throw new XMLException("XML file is not opened");
        if (nodeIndex > jobList.getLength() - 1)
            return null;
        Element jobElement = (Element) jobList.item(nodeIndex++);
        String depCode = jobElement.getAttribute("DepCode");
        String depJob = jobElement.getAttribute("DepJob");
        String description = jobElement.getAttribute("Description");
        if (depCode == null || depJob == null || description == null)
            throw new XMLException("Wrong XML structure. Some properties of the job are missing");
        return new Job(depCode, depJob, description);
    }

    /**
     * creates new XML file,   add your data with append(Job) function
     * and write data to the disk with write() function
     *
     * @param filePath path for new XML file
     * @throws XMLException when any error occured
     * @see XML#append(Job)
     * @see XML#write()
     */
    public void create(String filePath) throws XMLException {
        try {
            fileStream = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new XMLException("Unable to create file", e);
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XMLException("XML configuration error", e);
        }
        document = documentBuilder.newDocument();
        rootElement = document.createElement("jobs");
        document.appendChild(rootElement);
        mode = Mode.WRITE;
    }


    /**
     * adds new element to XML document, which was previously created.
     *
     * @param job Job object
     * @throws XMLException when any error occured
     * @see XML#create(String)
     */
    public void append(Job job) throws XMLException {
        if (mode != Mode.WRITE)
            throw new XMLException("XML file is not created");
        if (job == null)
            throw new XMLException("Can't add null object to the XML document");
        Element jobElement = document.createElement("job");
        jobElement.setAttribute("DepCode", job.getDepCode());
        jobElement.setAttribute("DepJob", job.getDepJob());
        jobElement.setAttribute("Description", job.getDescription());
        rootElement.appendChild(jobElement);
    }

    /**
     * writes XML document to the disk.
     *
     * @throws XMLException when any error occured
     * @see XML#create(String)
     */
    public void write() throws XMLException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult((FileOutputStream) fileStream);
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new XMLException("XML transformation error", e);
        }
    }
}
