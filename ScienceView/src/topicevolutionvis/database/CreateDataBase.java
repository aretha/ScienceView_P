/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.database;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aretha
 */
public class CreateDataBase {

    public void create() throws IOException {
        this.removeTables();
        this.createTables();
        ConnectionManager.getInstance().dispose();
    }

    private void createTables() throws IOException {
        PreparedStatement stmt = null;
        try {

            stmt = SqlManager.getInstance().getSqlStatement("CREATE.TABLE.COLLECTIONS", -1, -1);
            stmt.executeUpdate();
            stmt.close();

            stmt = SqlManager.getInstance().getSqlStatement("CREATE.TABLE.AUTHORS", -1, -1);
            stmt.executeUpdate();
            stmt.close();

            stmt = SqlManager.getInstance().getSqlStatement("CREATE.TABLE.CONTENT", -1, -1);
            stmt.executeUpdate();
            stmt.close();

            stmt = SqlManager.getInstance().getSqlStatement("CREATE.TABLE.REFERENCES", -1, -1);
            stmt.executeUpdate();
            stmt.close();


            stmt = SqlManager.getInstance().getSqlStatement("CREATE.TABLE.DOCUMENTS.TO.REFERENCES", -1, -1);
            stmt.executeUpdate();
            stmt.close();

            stmt = SqlManager.getInstance().getSqlStatement("CREATE.TABLE.DOCUMENTS.TO.AUTHORS", -1, -1);
            stmt.executeUpdate();
            stmt.close();

            stmt = SqlManager.getInstance().getSqlStatement("CREATE.INDEX.REFERENCES", -1, -1);
            stmt.executeUpdate();
            stmt.close();

            stmt = SqlManager.getInstance().getSqlStatement("CREATE.INDEX.AUTHORS", -1, -1);
            stmt.executeUpdate();
            stmt.close();

            stmt = SqlManager.getInstance().getSqlStatement("CREATE.INDEX.MATCH", -1, -1);
            stmt.executeUpdate();
            stmt.close();


        } catch (SQLException ex) {
            Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOException(ex.getMessage());
                }
            }
        }
    }

    /** Remove the tables of this data base. */
    private void removeTables() throws IOException {
        PreparedStatement stmt = null;

        try {
            stmt = SqlManager.getInstance().getSqlStatement("DROP.TABLE.COLLECTIONS", -1, -1);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                    stmt = null;
                } catch (SQLException ex) {
                    Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOException(ex.getMessage());
                }
            }
        }


        try {
            stmt = SqlManager.getInstance().getSqlStatement("DROP.TABLE.DOCUMENTS", -1, -1);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                    stmt = null;
                } catch (SQLException ex) {
                    Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOException(ex.getMessage());
                }
            }
        }

        try {
            stmt = SqlManager.getInstance().getSqlStatement("DROP.TABLE.REFERENCES", -1, -1);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                    stmt = null;
                } catch (SQLException ex) {
                    Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOException(ex.getMessage());
                }
            }
        }

        try {
            stmt = SqlManager.getInstance().getSqlStatement("DROP.TABLE.DOCUMENTS.TO.REFERENCES", -1, -1);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                    stmt = null;
                } catch (SQLException ex) {
                    Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOException(ex.getMessage());
                }
            }
        }

        try {
            stmt = SqlManager.getInstance().getSqlStatement("DROP.TABLE.AUTHORS", -1, -1);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                    stmt = null;
                } catch (SQLException ex) {
                    Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOException(ex.getMessage());
                }
            }
        }

        try {
            stmt = SqlManager.getInstance().getSqlStatement("DROP.TABLE.DOCUMENTS.TO.AUTHORS", -1, -1);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IOException(ex.getMessage());
                }
            }
        }

    }

    public static void main(String[] args) {
        try {
            CreateDataBase cdb = new CreateDataBase();
            cdb.create();
        } catch (IOException ex) {
            Logger.getLogger(CreateDataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
