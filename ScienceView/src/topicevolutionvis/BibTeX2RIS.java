package topicevolutionvis;

import java.io.File;
import java.io.FileReader;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexEntryType;
import net.sf.jabref.imports.BibtexParser;
import net.sf.jabref.imports.ParserResult;

public class BibTeX2RIS
{
	private BibtexDatabase database;

	private boolean referencesEnabled = true;
	
	public BibTeX2RIS(File bibfile) throws Exception {
		ParserResult parseResult = BibtexParser.parse(new FileReader(bibfile));
		database = parseResult.getDatabase();
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
	
	private String tira_caracter(String data) {
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

	public void printEndOfEntry() {
		System.out.printf("\nER\n");
	}

	public void printBeginOfRecord() {
		System.out.printf("FN ScienceView");
		System.out.printf("\nVR 1.0\n");
	}
	
	public void printEndOfRecord() {
		System.out.printf("\nEF");
	}

	
	public void printField(String name, String... values) {
		if (name == null || values == null || values.length == 0 || values[0] == null) {
			return;
		}
		
		String risFieldName = tira_caracter(name);
		String risFieldValue = tira_caracter(values[0]);
		System.out.printf("\n%s %s", risFieldName, risFieldValue);
		for (int i = 1; i < values.length; i++) {
			risFieldValue = tira_caracter(values[i]);
			System.out.printf("\n   %s", risFieldValue);
		}
	}


	public void convert() {
		String crossrefKey;
		BibtexEntry crossref;
		
		printBeginOfRecord();
		
		for (BibtexEntry entry : database.getEntries()) {
			crossrefKey = entry.getField("crossref");
			crossref = null;
			if (crossrefKey != null && crossrefKey.isEmpty() == false) {
				crossref = database.getEntryByKey(crossrefKey);
			}
	
			if (entry.getType() == BibtexEntryType.INPROCEEDINGS){
				printField("PT", "CPAPER");
				show_inproceedings(entry, database);
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
				show_inproceedings(entry, database);
				printField("JF", crossref.getField("journal"));
				printField("VL", entry.getField("volume"));
				printField("PY", entry.getField("year"));
				printField("CY", crossref.getField("address"));
				printEndOfEntry();
			}
		}
		
		printEndOfRecord();
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

	private void show_inproceedings(BibtexEntry entry, BibtexDatabase database) {
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
	
	public static void main(String[] args) throws Exception {
		File arquivo = new File("/home/magsilva/Dropbox/Projects/RS-Mariane/referencias_mojo.bib");
		BibTeX2RIS bib2ris = new BibTeX2RIS(arquivo);
		bib2ris.convert();
	}

}

