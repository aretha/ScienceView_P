/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import topicevolutionvis.database.CollectionsManager;
import topicevolutionvis.database.ConnectionManager;
import topicevolutionvis.database.DatabaseCorpus;
import topicevolutionvis.wizard.DataSourceChoice;

/**
 *
 * @author Aretha
 */
public class DumpDatabaseImporter extends DatabaseImporter {

    public DumpDatabaseImporter(String filename, DataSourceChoice view, boolean removeStopwordsByTagging) {
        super(filename, null, -1, view, removeStopwordsByTagging);
    }

    @Override
    protected Void doInBackground() throws Exception {
        String line;
        StringBuilder command = new StringBuilder();
        PreparedStatement stmt;
        BufferedReader in = new BufferedReader(new FileReader(this.filename));
        boolean search_collection_name = false;
        String collectionName;
        int begin, end;
        while (((line = in.readLine().trim()) != null)) {
            if (search_collection_name) {
                begin = line.indexOf("'");
                end = line.indexOf("'", begin + 1);
                collectionName = line.substring(begin + 1, end);
                if (!DatabaseCorpus.uniqueName(collectionName)) {
                    int answer = JOptionPane.showOptionDialog(view, "A collection intitled \"" + collectionName + "\" already exists. Do you wish to replace this collection?", "Save Warning",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                    if (answer == JOptionPane.NO_OPTION) {
                        break;
                    } else {
                        CollectionsManager.removeCollection(CollectionsManager.getCollectionId(collection));
                    }
                }
            }
            if (!search_collection_name && line.contains("INSERT INTO PUBLIC.COLLECTIONS(ID_COLLECTION, NAME, NRGRAMS, FORMAT, GRAMS)")) {
                search_collection_name = true;
            }
            command.append(line);
            if (line.compareTo(";") != 0) {
                if (line.endsWith(";")) {
                    stmt = ConnectionManager.getInstance().getConnection().prepareStatement(command.toString());
                    stmt.execute();
                    command = new StringBuilder();
                }
            } else {
                command = new StringBuilder();
            }

        }

        return null;
    }
}
