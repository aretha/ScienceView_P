/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis.util;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import topicevolutionvis.utils.filefilter.MyFileFilter;

/**
 *
 * @author Aretha
 */
public class SaveDialog {

    private static javax.swing.JFileChooser dialog;
    private static String _filename;

    public static int showSaveDialog(MyFileFilter filter, Component parent, String filename) {
        if (SaveDialog.dialog == null) {
            SaveDialog.createDialog();
        }

        _filename = null;

        dialog.resetChoosableFileFilters();
        dialog.setAcceptAllFileFilterUsed(false);
        if (filter != null) {
            dialog.setFileFilter(filter);
        }
        dialog.setMultiSelectionEnabled(false);
        dialog.setDialogTitle("Save file");
        if (filename != null && filename.length() > 0) {
            filename = filename.substring(0, filename.lastIndexOf('.')) + "." + filter.getFileExtension().toLowerCase();
            dialog.setSelectedFile(new File(filename));
        } else {
            dialog.setSelectedFile(new File(""));
        }

        SystemPropertiesManager m = SystemPropertiesManager.getInstance();
        dialog.setCurrentDirectory(new File(m.getProperty(filter.getProperty())));

        int result = dialog.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            _filename = dialog.getSelectedFile().getAbsolutePath();
            m.setProperty(filter.getProperty(), dialog.getSelectedFile().getParent());

            //checking if the name finishes with the correct extension
            if (!_filename.toLowerCase().endsWith("." + filter.getFileExtension())) {
                _filename = _filename.concat("." + filter.getFileExtension());
            }
        }

        return result;
    }

    public static String getFilename() {
        return _filename;
    }

    private static void createDialog() {
        dialog = new JFileChooser() {

            @Override
            public void approveSelection() {
                File file = getSelectedFile();
                if (file != null && file.exists()) {
                    String message = "The file \"" + file.getName() + "\" already exists. \n"
                            + "Do you want to replace the existing file?";
                    int answer = JOptionPane.showOptionDialog(this, message, "Save Warning",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);

                    if (answer == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                super.approveSelection();
            }
        };
    }
}
