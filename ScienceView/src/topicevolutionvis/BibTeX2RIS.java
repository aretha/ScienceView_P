package topicevolutionvis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;

import topicevolutionvis.data.ISICorpusDatabaseImporter;

import com.ironiacorp.io.IoUtil;
import com.ironiacorp.string.StringUtil;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexEntryType;
import net.sf.jabref.imports.BibtexParser;
import net.sf.jabref.imports.ParserResult;

public class BibTeX2RIS
{
	// TODO: move this to JabRef
	private static final String FILENAME_EXTENSION = ".bib";
	
	private boolean referencesEnabled = true;
	
	private BibtexDatabase database;
	
	private Reader reader;
																																																										
	public File outputFile;
	
	public PrintWriter outputWriter;
	
	
	public void setInputFilename(String filename) {
		File file = new File(filename);
		setInputFile(file);
	}
	
	public void setInputFile(File file) {
		String extension;
	
		if (! file.exists() || ! file.isFile()) {
			throw new IllegalArgumentException("Invalid BibTeX file");
		}
		
		try {
			reader = new FileReader(file);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("File not found: " + file, e);
		}
		extension = IoUtil.getExtension(file);
		if (StringUtil.isEmpty(extension)) {
			setOutputFile(file + ISICorpusDatabaseImporter.FILE_EXTENSION);	
		} else {
			setOutputFile(file.getAbsolutePath().replaceAll("\\" + BibTeX2RIS.FILENAME_EXTENSION + "$", ISICorpusDatabaseImporter.FILE_EXTENSION));
		}
	}

	public void setInputStream(InputStream is) {
		setInputStream(is, Charset.defaultCharset());
	}

	public void setInputStream(InputStream is, Charset encoding) {
		reader = new InputStreamReader(is, encoding);
	}
	
	public File getOutputFile() {
		return outputFile;
	}
	
    public void setOutputFile(String filename) {
    	File file = new File(filename);
    	setOutputFile(file);
	}

    public void setOutputFile(File file) {
    	outputFile = file;
    }
    
    
    public void readData() {
		ParserResult parseResult;
		
		try {
			parseResult = BibtexParser.parse(reader);
		} catch (IOException e) {
			throw new RuntimeException("Error reading data", e);
		}
		
		database = parseResult.getDatabase();
    }
    
    /**
     * Convert the BibTeX file into ISI data format. The result is saved
     * to a file (which is defined when instantiating the class or with the
     * method setOutputFile()).
     * 
     * @throws IOException
     */
	public void convert() 
	{
		String crossrefKey;
		BibtexEntry crossref;
	
		FileWriter writer;
		try {
			writer = new FileWriter(outputFile);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write data to output file: " + outputFile);
		}
		/**
		 * especificando o segundo parametro como true, os dados serão enviados para o arquivo a toda chamada do método println(), 
		 * caso contrário, os dados só são enviados quando enviar uma quebra de linha.
		 */
		outputWriter = new PrintWriter(writer, true);

		printBeginOfRecord();
		
		for (BibtexEntry entry : database.getEntries()) {
			crossrefKey = entry.getField("crossref");
			crossref = null;
			if (crossrefKey != null && crossrefKey.isEmpty() == false) {
				crossref = database.getEntryByKey(crossrefKey);
				if (crossref == null) {
					throw new RuntimeException("Missing cross-reference: " + crossrefKey);
				}
			}
	
			if (entry.getType() == BibtexEntryType.INPROCEEDINGS){
				printField("PT", "CPAPER");
				printEntry(entry, database);
				printField("PY", crossref.getField("year"));
				printField("JF", crossref.getField("booktitle"));
				
				if (crossref.getField("location") != null) {
					printField("CY", crossref.getField("address"));
				} else {
					if (crossref.getField("address") != null) {
						printField("CY", crossref.getField("address"));
					}
				}
				printEndOfEntry();
			}
								
			if (entry.getType() == BibtexEntryType.ARTICLE) {
				printField("PT", "JOUR");
				printEntry(entry, database);
				printField("JF", crossref.getField("journal"));
				printField("VL", entry.getField("volume"));
				printField("PY", entry.getField("year"));
				printField("CY", crossref.getField("address"));
				printEndOfEntry();
			}
		}
		
		printEndOfRecord();
	}

	private String convertAuthor(String author, String separator) {
		StringBuilder sb = new StringBuilder();
		String[] words;
		
		words = author.split(",");
		if (words.length > 1) {
			author = words[1] + " " + words[0];
		}
		
		words = author.trim().replace("  ", " ").split("\\s");
		sb.append(words[words.length - 1].trim());
		sb.append(separator);
		sb.append(words[0].trim().charAt(0));
		return sb.toString();
	}
	
	private String replaceChars(String data) {
		 if (data == null) {
			 throw new IllegalArgumentException(new NullPointerException());
		 }

		 // "Expand" macros
		 if(data.contains("#")){
			 data = data.replace("#", "");
		 }
			 
		 // Remove "&"
		 if(data.contains("\\&")){
			 data = data.replace("\\&", "");
		 }

		 // Remove \{ and \}
		 if(data.contains("\\{")){
			 data = data.replace("{", "");
		 }
		 if(data.contains("\\}")){
			 data = data.replace("}", "");
		 }

		 // Remove { and }
		 if(data.contains("{")){
			 data = data.replace("{", "");
		 }
		 if(data.contains("}")){
			 data = data.replace("}", "");
		 }
	 			 
		 return data;
	}
	
	private void printEndOfEntry() {
		outputWriter.println("ER\n");
	}

	private void printBeginOfRecord() {
		outputWriter.println("FN ScienceView");
		outputWriter.println("VR 1.0\n");
	}
	
	private void printEndOfRecord() {
		outputWriter.println("EF");
	}

	
	private void printField(String name, String... values) {
		if (name == null || values == null || values.length == 0 || values[0] == null) {
			return;
		}
		
		String risFieldName = replaceChars(name);
		String risFieldValue = replaceChars(values[0]);
		outputWriter.printf(risFieldName);
		outputWriter.printf(" ");
		outputWriter.println(risFieldValue);
		for (int i = 1; i < values.length; i++) {
			risFieldValue = replaceChars(values[i]);
			outputWriter.printf("   ");
			outputWriter.println(risFieldValue);
		}
	}



	private String[] getReferences(String referencesField, BibtexDatabase database) {
		StringBuilder sb = new StringBuilder();
		String[] entryKeys = referencesField.split(",");
		String[] references = new String[entryKeys.length];
		
		for (int i = 0; i < entryKeys.length; i++) {
			String entryKey = entryKeys[i];
			String crossrefKey;
			BibtexEntry entry;
			BibtexEntry crossref = null;
			String[] authors;
			String year = null;
			String doi;
			int pages = 0;

			entry = database.getEntryByKey(entryKey.trim());

			authors = entry.getField("author").split(" and ");
			crossrefKey = entry.getField("crossref");
			if (crossrefKey != null && ! crossrefKey.isEmpty()) {
				crossref = database.getEntryByKey(crossrefKey);
			}
			
			if (entry.getType() == BibtexEntryType.INPROCEEDINGS){
				year =  crossref.getField("year");
			} else if (entry.getType() == BibtexEntryType.ARTICLE || entry.getType() == BibtexEntryType.BOOK || entry.getType() == BibtexEntryType.MISC) {
			    year =  entry.getField("year");
			}

			if (entry.getType() == BibtexEntryType.BOOK) {
				pages = Integer.parseInt(entry.getField("pages"));
			} else {
				try {
					int[] pageRange;
					pageRange = pages(entry.getField("pages"));
					pages = pageRange[1] - pageRange[0];
				} catch (Exception e) {
					if (entry.getType() == BibtexEntryType.MISC) {
						pages = 0;
					}
				}
			}
				
			doi = entry.getField("doi");
				
			sb.setLength(0);
			sb.append(convertAuthor(authors[0], " "));
			sb.append(", ");
			sb.append(year);
			if (entry.getType() == BibtexEntryType.ARTICLE) {
				sb.append(", ");
				sb.append(crossref.getField("journal"));
				sb.append(", ");
				sb.append("V");
				sb.append(entry.getField("volume"));
			}
			if (entry.getType() == BibtexEntryType.INPROCEEDINGS) {
				sb.append(", ");
				sb.append(crossref.getField("booktitle"));
			}
			if (entry.getType() == BibtexEntryType.BOOK) {
				sb.append(", ");
				sb.append(entry.getField("booktitle"));
			}
			if (entry.getType() == BibtexEntryType.MISC) {
				sb.append(", ");
				sb.append(entry.getField("title"));
			}
			
			sb.append(", ");
			sb.append("P");
			sb.append(pages);
			if (doi != null) {
				sb.append(", ");
				sb.append("DOI ");
				sb.append(doi);
			}
			references[i] = sb.toString();
		}
		return references;
	}

	private int[] pages(String field) {
		/**
		 * tem que declarar o tamanho do vetor!!! Com Integer[] pages; --> não funciona.  
		 */
		String[] subfields;
		int[] pages = new int[2];
		
		field = field.replace("--", "-");
		subfields = field.split("-");
		pages[0] = Integer.valueOf(subfields[0]);
		pages[1] = Integer.valueOf(subfields[1]);

		return pages;
	
	 }

	private void printEntry(BibtexEntry entry, BibtexDatabase database) {
		// autor
		/**
		 * tem que dar split em espaço+and+espaço, porque no meio do nome pode formar
		 * a string and. Com espaço garanto que não vai quebrar no meio de nenhum nome.
		 */
		String[] authors = entry.getField("author").split(" and "); 
		for (int i = 0; i < authors.length; i++) {
			authors[i] = convertAuthor(authors[i], ", ");
		}
		printField("AU", authors);
			
		if (entry.getField("lang") != null && ! entry.getField("lang").equals("en")) {
			printField("TI", entry.getField("title-en"));
			printField("AB", entry.getField("abstract-en"));
			printField("KW", entry.getField("keywords-en"));
		} else {
			printField("TI", entry.getField("title"));
			printField("AB", entry.getField("abstract"));
			printField("KW", entry.getField("keywords"));
		}
				
		int[] pages = pages(entry.getField("pages"));
		printField("BP", Integer.toString(pages[0]));
		printField("EP", Integer.toString(pages[1]));
	
		printField("DI", entry.getField("doi"));	

		if (referencesEnabled) {
		  	String references = entry.getField("references");
		   	if (references != null) {
		   		printField("CR", getReferences(references, database));
		   	}
		}
	}
}

