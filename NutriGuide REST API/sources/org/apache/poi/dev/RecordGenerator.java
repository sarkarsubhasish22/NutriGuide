package org.apache.poi.dev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class RecordGenerator {
    public static void main(String[] args) throws Exception {
        Class.forName("org.apache.poi.generator.FieldIterator");
        if (args.length != 4) {
            System.out.println("Usage:");
            System.out.println("  java org.apache.poi.hssf.util.RecordGenerator RECORD_DEFINTIONS RECORD_STYLES DEST_SRC_PATH TEST_SRC_PATH");
            return;
        }
        generateRecords(args[0], args[1], args[2], args[3]);
    }

    private static void generateRecords(String defintionsDir, String recordStyleDir, String destSrcPathDir, String testSrcPathDir) throws Exception {
        File definitionsFile;
        String str = recordStyleDir;
        File definitionsFile2 = new File(defintionsDir);
        int i = 0;
        while (i < definitionsFile2.listFiles().length) {
            File file = definitionsFile2.listFiles()[i];
            if (!file.isFile()) {
                String str2 = destSrcPathDir;
                String str3 = testSrcPathDir;
                definitionsFile = definitionsFile2;
            } else if (file.getName().endsWith("_record.xml") || file.getName().endsWith("_type.xml")) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(file);
                Element record = document.getDocumentElement();
                String extendstg = record.getElementsByTagName("extends").item(0).getFirstChild().getNodeValue();
                String suffix = record.getElementsByTagName("suffix").item(0).getFirstChild().getNodeValue();
                String recordName = record.getAttributes().getNamedItem("name").getNodeValue();
                String packageName = record.getAttributes().getNamedItem("package").getNodeValue().replace('.', '/');
                String destinationPath = destSrcPathDir + "/" + packageName;
                definitionsFile = definitionsFile2;
                File destinationPathFile = new File(destinationPath);
                destinationPathFile.mkdirs();
                File file2 = destinationPathFile;
                String destinationFilepath = destinationPath + "/" + recordName + suffix + ".java";
                DocumentBuilderFactory documentBuilderFactory = factory;
                File file3 = new File(destinationFilepath);
                DocumentBuilder documentBuilder = builder;
                Document document2 = document;
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append("/");
                Element element = record;
                sb.append(extendstg.toLowerCase());
                sb.append(".xsl");
                transform(file, file3, new File(sb.toString()));
                System.out.println("Generated " + suffix + ": " + destinationFilepath);
                String destinationPath2 = testSrcPathDir + "/" + packageName;
                new File(destinationPath2).mkdirs();
                String destinationFilepath2 = destinationPath2 + "/Test" + recordName + suffix + ".java";
                if (!new File(destinationFilepath2).exists()) {
                    transform(file, new File(destinationFilepath2), new File(str + "/" + extendstg.toLowerCase() + "_test.xsl"));
                    PrintStream printStream = System.out;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Generated test: ");
                    sb2.append(destinationFilepath2);
                    printStream.println(sb2.toString());
                } else {
                    System.out.println("Skipped test generation: " + destinationFilepath2);
                }
            } else {
                String str4 = destSrcPathDir;
                String str5 = testSrcPathDir;
                definitionsFile = definitionsFile2;
            }
            i++;
            String str6 = defintionsDir;
            definitionsFile2 = definitionsFile;
        }
    }

    private static void transform(File in, File out, File xslt) throws FileNotFoundException, TransformerException {
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer(new StreamSource(new FileReader(xslt)));
            Properties p = new Properties();
            p.setProperty("method", "text");
            t.setOutputProperties(p);
            t.transform(new StreamSource(in), new StreamResult(out));
        } catch (TransformerException ex) {
            PrintStream printStream = System.err;
            printStream.println("Error compiling XSL style sheet " + xslt);
            throw ex;
        }
    }
}
