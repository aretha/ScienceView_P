/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.data;

import java.util.ArrayList;

/**
 *
 * @author Aretha
 */
public class EndnoteEntry {

    private Integer year, timesCited = 0, ISICitations = 0, id;
    private Double citationsByYear = 0.0;
    private int trueLblWork = 0, type;
    private String abs = null, keywords = null, title = "", journal = null, journal_abbrev = null, publisher, begin_page = null, end_page = null, number, references = "", volume = null, doi = null, research_address = null;
    ArrayList<String> authors = new ArrayList<>();
    StringBuilder content;
    String authorPattern = "[a-zA-Z-\\s]+,\\s[[A-Z].\\s?]+";

    public String getAuthors() {
        StringBuilder aux = new StringBuilder("");
        String author;
        int index;
        for (int i = 0; i < authors.size() - 1; i++) {
            author = authors.get(i);
            if (author.matches(authorPattern)) {
                index = author.indexOf(",");
                aux.append(author.substring(0, index)).append(" ").append(author.substring(index + 1).replaceAll("\\p{Punct}|\\s", "")).append("|");
            } else {
                aux.append(author).append("|");
            }
        }
        if (authors.size() > 0) {
            author = authors.get(authors.size() - 1);
            if (author.matches(authorPattern)) {
                index = author.indexOf(",");
                aux.append(author.substring(0, index)).append(" ").append(author.substring(index + 1).replaceAll("\\p{Punct}|\\s", ""));
            } else {
                aux.append(author);
            }

        }
        return aux.toString();
    }

    public StringBuilder getContent() {
        return this.content;
    }

    public String getDoi() {
        return this.doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public String getVolume() {
        return this.volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setContent(StringBuilder content) {
        this.content = content;
    }

    public Integer getISICitations() {
        return this.ISICitations;
    }

    public String getReferences() {
        return this.references;
    }

    public String getBeginPage() {
        return this.begin_page;
    }

    public String getEndPage() {
        return this.end_page;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public String getAbstract() {
        return this.abs;
    }

    public void setTrueLblWork(int value) {
        this.trueLblWork = value;
    }

    public int getTrueLblWork() {
        return this.trueLblWork;
    }

    public String getTitle() {
        return this.title;
    }

    public String getJournal() {
        return this.journal;
    }

    public Integer getYear() {
        return this.year;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addAuthor(String author) {
        this.authors.add(author);
    }

    public void setNumberOfISICitations(Integer ncitations) {
        this.ISICitations = ncitations;
    }

    public void setNumberOfCitationsByYear(Double ncitations) {
        this.citationsByYear = ncitations;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setAbstract(String abs) {
        this.abs = abs;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTimesCited(Integer timesCited) {
        this.timesCited = timesCited;
    }

    public Integer getTimesCited() {
        return this.timesCited;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public void setJournalAbbrev(String journal_abbrev) {
        this.journal_abbrev = journal_abbrev;
    }

    public String getJournalAbbrev() {
        return this.journal_abbrev;
    }

    public Double getCitationsByYear() {
        return this.citationsByYear;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setBeginPage(String begin_page) {
        this.begin_page = begin_page;
    }

    public void setEndPage(String end_page) {
        this.end_page = end_page;
    }

    public void setResearchAddress(String research_address) {
        this.research_address = research_address;
    }

    public String getResearchAddress() {
        return this.research_address;
    }
}
