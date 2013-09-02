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
public class ISIEntry {

    private ArrayList<String> authors = null, references = null;
    private String title = null, abs = null, keywords = null, timesCited = null, isiId = null;
    private Integer id = null;
    private Integer publicationYear = null;

    public String getTitle() {
        return this.title;
    }

    public Integer getId() {
        return this.id;
    }

    public String getKeywords(){
        return this.keywords;
    }

    public ArrayList<String> getReferences() {
        return this.references;
    }

    public ArrayList<String> getAuthors() {
        return this.authors;
    }

    public String getAbstract() {
        return this.abs;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsiId() {
        return this.isiId;
    }

    public void setIsiId(String value) {
        this.isiId = value;
    }

    public void setPublicationYear(Integer year) {
        this.publicationYear = year;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }


    public Integer getPublicationYear() {
        return this.publicationYear;
    }

    public void setAbstract(String abs) {
        this.abs = abs;
    }

    public void setTimesCited(String timesCited) {
        this.timesCited = timesCited;
    }

    public String getTimesCited() {
        return this.timesCited;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    public void setReferences(ArrayList<String> references) {
        this.references = references;
    }
}
