/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slider;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import topicevolutionvis.database.ConnectionManager;

/**
 *
 * @author Aretha
 */
public class TesteH2 {
    private static void teste(){
          PreparedStatement stmt;
        try {
            stmt = ConnectionManager.getInstance().getConnection().prepareStatement("CREATE SEQUENCE PUBLIC.SYSTEM_SEQUENCE_B016D488_623B_4C16_99A6_DDA26A9A84C3 START WITH 1889 BELONGS_TO_TABLE; ");
            stmt.execute();
        } catch (IOException | SQLException ex) {
            Logger.getLogger(TesteH2.class.getName()).log(Level.SEVERE, null, ex);
        }
                    
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
             teste();
            }
        });
    }
}
