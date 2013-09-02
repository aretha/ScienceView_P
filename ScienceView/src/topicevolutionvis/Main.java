/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package topicevolutionvis;

import topicevolutionvis.view.ScienceViewMainFrame;

public class Main {

    protected static ScienceViewMainFrame mainFrame;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        mainFrame = ScienceViewMainFrame.getInstance();
        mainFrame.display();
    }
}
