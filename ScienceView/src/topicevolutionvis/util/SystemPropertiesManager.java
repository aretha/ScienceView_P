/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Aretha
 */
public class SystemPropertiesManager {

    private String filename = "config/system.properties";
    //   private Hashtable<String, String> properties = new Hashtable<String, String>();
    private static SystemPropertiesManager _instance = null;
    private Properties properties = null;

//    public SystemPropertiesManager() throws Exception {
//        //lê o arquivo de propriedades
//        File f = new File(this.filename);
//
//        if (f.exists()) {
//            BufferedReader bufferedReader;
//            try {
//                bufferedReader = new BufferedReader(new FileReader(filename));
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    line = line.trim();
//                    String first_char = line.substring(0, 1);
//
//                    if (first_char.compareTo("#") != 0) {
//                        int pos_equal = line.indexOf("=");
//                        String property_name = line.substring(0, pos_equal);
//
//                        String property_value = line.substring(pos_equal + 1);
//                        properties.put(property_name, property_value);
//                    }
//                }
//                bufferedReader.close();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//        }
//    }
    /** Creates a new instance of SystemPropertiesManager */
    private SystemPropertiesManager() {
        FileInputStream in = null;
        try {
            //lê o arquivo de propriedades
            File f = new File(this.filename);
            if (f.exists()) {
                this.properties = new Properties();

                in = new FileInputStream(this.filename);
                this.properties.load(in);
            }
        } catch (Exception ex) {
            Logger.getLogger(SystemPropertiesManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                Logger.getLogger(SystemPropertiesManager.class.getName()).log(Level.SEVERE, null, e);
            }
        }


    }

    public static SystemPropertiesManager getInstance() {
        if (_instance == null) {
            _instance = new SystemPropertiesManager();
        }
        return _instance;
    }

    public String getProperty(String id) {
        if (this.properties == null) {
            return "";
        } else {
            if (this.properties.containsKey(id)) {
                return this.properties.getProperty(id);
            } else {
                return "";
            }
        }
    }

    public void setProperty(String id, String value) {
        if (this.properties == null) {
            this.properties = new Properties();
        }
        this.properties.setProperty(id, value);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(this.filename);
            this.properties.store(out, "Recording the system's properties");
            out.flush();
        } catch (Exception ex) {
            Logger.getLogger(SystemPropertiesManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(SystemPropertiesManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
}
