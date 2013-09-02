/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AuthorsWordle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import topicevolutionvis.database.SqlManager;

/**
 *
 * @author Aretha
 */
public class AuthorsWorlde {

    public static void main(String[] args) {

            LinkedHashMap<String, Integer> documents = saveDocumentsAuthors();
            LinkedHashMap<String, Integer> citations = saveCitationsAuthors();
            merge(documents, citations);
    }

    public static void merge(LinkedHashMap<String, Integer> documents, LinkedHashMap<String, Integer> citations) {
        BufferedWriter bufferedWriter = null;
        try {
            for (Entry<String, Integer> entry : citations.entrySet()) {
                if (documents.containsKey(entry.getKey())) {
                    int old_value = documents.get(entry.getKey());
                    documents.put(entry.getKey(), old_value + entry.getValue());
                } else {
                    documents.put(entry.getKey(), entry.getValue());
                }
            }
            bufferedWriter = new BufferedWriter(new FileWriter("authorsDocumensAndCitations.txt"), 1024);
            for (Entry<String, Integer> entry : documents.entrySet()) {
                bufferedWriter.write(entry.getKey() + ": " + entry.getValue());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            bufferedWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(AuthorsWorlde.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(AuthorsWorlde.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static LinkedHashMap<String, Integer> saveCitationsAuthors() {
        try {
            PreparedStatement stmt, stmt2;
            ResultSet result, result2;
            LinkedHashMap<String, Integer> authorsTreeMap = new LinkedHashMap<>();
            String collectionName = "DecherLimpo";

            //getting the id_collection
            int id_collection = -1;
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.COLLECTION.BY.NAME",-1,-1);
            stmt.setString(1, collectionName);
            result = stmt.executeQuery();
            if (result.next()) {
                id_collection = result.getInt("id_collection");
            }
            stmt.close();
            result.close();

            int id_author, ocurrences;
            stmt = SqlManager.getInstance().getSqlStatement("COUNT.ID_AUTHORS.OCCURRENCES.CITATIONS",-1,-1);
            stmt.setInt(1, id_collection);
            result = stmt.executeQuery();
            while (result.next()) {
                id_author = result.getInt(1);
                ocurrences = result.getInt(2);

                stmt2 = SqlManager.getInstance().getSqlStatement("SELECT.NAME.AUTHOR",-1,-1);
                stmt2.setInt(1, id_author);
                result2 = stmt2.executeQuery();
                if (result2.next()) {
                    String name = result2.getString(1);
                    authorsTreeMap.put(name, ocurrences);
                }
                stmt2.close();
                result2.close();
            }
            stmt.close();
            result.close();
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("authorsCitations.txt"), 1024)) {
                for (Entry<String, Integer> entry : authorsTreeMap.entrySet()) {
                    bufferedWriter.write(entry.getKey() + ": " + entry.getValue());
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
            return authorsTreeMap;
        } catch (IOException | SQLException ex) {
            Logger.getLogger(AuthorsWorlde.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
//
//    public static void teste() {
//        try {
//            TreeMap<String, Integer> treeMap = new TreeMap<>();
//            String collectionName = "DecherLimpo";
//
//            //getting the id_collection
//            int id_collection = -1;
//            PreparedStatement stmt = SqlManager.getInstance().getSqlStatement("SELECT.COLLECTION.BY.NAME",-1,-1);
//            stmt.setString(1, collectionName);
//            ResultSet result = stmt.executeQuery();
//            if (result.next()) {
//                id_collection = result.getInt("id_collection");
//            }
//            stmt.close();
//            result.close();
//
//            stmt = SqlManager.getInstance().getSqlStatement("TESTE",-1,-1);
//            stmt.setInt(1, id_collection);
//            result = stmt.executeQuery();
//            //   int previousValue;
//            String authors;
//            while (result.next()) {
//                authors = result.getString("authors");
//                System.out.println(authors);
//                //       System.out.println(result.getString("authors")+", "+result.getInt("year")+", "+ result.getInt("volume")+", "+result.getInt("pages"));
//            }

//            stmt = SqlManager.getInstance().getSqlStatement("SEE.AUTHORS.REFERENCES.TRUELBL");
//            stmt.setInt(1, id_collection);
//            result = stmt.executeQuery();
//            int previousValue;
//            while (result.next()) {
//                String author = result.getString("authors");
//                if (!treeMap.containsKey(author)) {
//                    treeMap.put(author, 1);
//                } else {
//                    previousValue = treeMap.get(author);
//                    treeMap.put(author, previousValue + 1);
//                }
//            }
//
//            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("authorsFromTrueLbLReferences.txt"), 1024);
//            for (Entry<String, Integer> entry : treeMap.entrySet()) {
//                bufferedWriter.write(entry.getKey() + ": " + entry.getValue());
//                bufferedWriter.newLine();
//                bufferedWriter.flush();
//            }
//            bufferedWriter.close();


//        } catch (IOException | SQLException ex) {
//            Logger.getLogger(AuthorsWorlde.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public static void count() {
        try {
            String collectionName = "DecherLimpo";
            //getting the id_collection
            int id_collection = -1, count = -1;
            PreparedStatement stmt = SqlManager.getInstance().getSqlStatement("SELECT.COLLECTION.BY.NAME",-1,-1);
            stmt.setString(1, collectionName);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                id_collection = result.getInt("id_collection");
            }
            stmt.close();
            result.close();

            stmt = SqlManager.getInstance().getSqlStatement("COUNT.TRUELBL",-1,-1);
            stmt.setInt(1, id_collection);
            result = stmt.executeQuery();
            if (result.next()) {
                count = result.getInt("COUNT(id_doc)");
            }
            System.out.println("True: " + count);
            stmt.close();
            result.close();

            stmt = SqlManager.getInstance().getSqlStatement("COUNT.NOTLBL",-1,-1);
            stmt.setInt(1, id_collection);
            result = stmt.executeQuery();
            if (result.next()) {
                count = result.getInt("COUNT(id_doc)");
            }
            System.out.println("Not: " + count);
            stmt.close();
            result.close();

            stmt = SqlManager.getInstance().getSqlStatement("COUNT.MAYBE",-1,-1);
            stmt.setInt(1, id_collection);
            result = stmt.executeQuery();
            if (result.next()) {
                count = result.getInt("COUNT(id_doc)");
            }
            System.out.println("Maybe: " + count);
            stmt.close();
            result.close();

            stmt = SqlManager.getInstance().getSqlStatement("COUNT.TOTAL",-1,-1);
            stmt.setInt(1, id_collection);
            result = stmt.executeQuery();
            if (result.next()) {
                count = result.getInt("COUNT(id_doc)");
            }
            System.out.println("Total: " + count);
            stmt.close();
            result.close();

            stmt = SqlManager.getInstance().getSqlStatement("COUNT.ERRADO",-1,-1);
            stmt.setInt(1, id_collection);
            result = stmt.executeQuery();
            while (result.next()) {
                count = result.getInt("class");
                System.out.println("class: " + count);
            }
            stmt.close();
            result.close();

        } catch (IOException | SQLException ex) {
            Logger.getLogger(AuthorsWorlde.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static LinkedHashMap<String, Integer> saveDocumentsAuthors() {
        try {
            PreparedStatement stmt, stmt2;
            ResultSet result, result2;
            LinkedHashMap<String, Integer> authorsTreeMap = new LinkedHashMap<>();
            String collectionName = "DecherLimpo";

            //getting the id_collection
            int id_collection = -1;
            stmt = SqlManager.getInstance().getSqlStatement("SELECT.COLLECTION.BY.NAME",-1,-1);
            stmt.setString(1, collectionName);
            result = stmt.executeQuery();
            if (result.next()) {
                id_collection = result.getInt("id_collection");
            }
            stmt.close();
            result.close();

            int id_author, ocurrences;
            stmt = SqlManager.getInstance().getSqlStatement("COUNT.ID_AUTHORS.OCCURRENCES.DOCUMENTS",-1,-1);
            stmt.setInt(1, id_collection);
            result = stmt.executeQuery();
            while (result.next()) {
                id_author = result.getInt(1);
                ocurrences = result.getInt(2);

                stmt2 = SqlManager.getInstance().getSqlStatement("SELECT.NAME.AUTHOR",-1,-1);
                stmt2.setInt(1, id_author);
                result2 = stmt2.executeQuery();
                if (result2.next()) {
                    String name = result2.getString(1);
                    authorsTreeMap.put(name, ocurrences);
                }
                stmt2.close();
                result2.close();
            }
            stmt.close();
            result.close();
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("authorsDocuments.txt"), 1024)) {
                for (Entry<String, Integer> entry : authorsTreeMap.entrySet()) {
                    bufferedWriter.write(entry.getKey() + ": " + entry.getValue());
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
            return authorsTreeMap;

        } catch (IOException | SQLException ex) {
            Logger.getLogger(AuthorsWorlde.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
