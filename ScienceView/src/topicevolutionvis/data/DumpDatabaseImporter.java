package topicevolutionvis.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.swing.JOptionPane;

import topicevolutionvis.database.CollectionManager;
import topicevolutionvis.database.ConnectionManager;
import topicevolutionvis.database.SqlManager;
import topicevolutionvis.wizard.DataSourceChoiceWizard;

/**
 *
 * @author Aretha
 */
public class DumpDatabaseImporter extends DatabaseImporter {

    public DumpDatabaseImporter(String filename, DataSourceChoiceWizard view, boolean removeStopwordsByTagging) {
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
        
        ConnectionManager connManager = ConnectionManager.getInstance();
        Connection conn = connManager.getConnection();
        
        while (((line = in.readLine().trim()) != null)) {
            if (search_collection_name) {
                begin = line.indexOf("'");
                end = line.indexOf("'", begin + 1);
                collectionName = line.substring(begin + 1, end);
                if (! collectionManager.isUnique(collection)) {
                    int answer = JOptionPane.showOptionDialog(view, "A collection intitled \"" + collectionName + "\" already exists. Do you wish to replace this collection?", "Save Warning",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
                    if (answer == JOptionPane.YES_OPTION) {
                        CollectionManager collectionManager = new CollectionManager();
                        collectionManager.removeCollection(collection);
                    }
                }
            }
            if (! search_collection_name && line.contains("INSERT INTO PUBLIC.COLLECTIONS(ID_COLLECTION, NAME, NRGRAMS, FORMAT, GRAMS)")) {
                search_collection_name = true;
            }
            command.append(line);
            if (line.compareTo(";") != 0) {
                if (line.endsWith(";")) {
                	stmt = SqlManager.getInstance().createSqlStatement(conn, command.toString());
                    stmt.execute();
                    stmt.close();
                    command.setLength(0);
                }
            } else {
                command.setLength(0);
            }

        }
        in.close();
        conn.close();
        
        return null;
    }
}
