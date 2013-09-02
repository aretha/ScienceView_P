package topicevolutionvis.projection.distance;

import gnu.trove.TIntArrayList;
import gnu.trove.list.array.TDoubleArrayList;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import topicevolutionvis.matrix.SparseMatrix;
import topicevolutionvis.matrix.SparseVector;

/**
 * This class represents distances between elements. It stores the
 * distances from one to all other element.
 *
 * @author Fernando Vieira Paulovich
 */
public class DistanceMatrix implements Cloneable {

    public DistanceMatrix(String filename) throws IOException {
        this.load(filename);
    }

    public DistanceMatrix(int nrElements) {
        this.maxDistance = Double.NEGATIVE_INFINITY;
        this.minDistance = Double.POSITIVE_INFINITY;
        this.nrElements = nrElements;
        this.distmatrix = new double[nrElements - 1][];

        for (int i = 0; i < this.nrElements - 1; i++) {
            this.distmatrix[i] = new double[i + 1];
        }
    }

    /**
     * This constructor create a distance distmatrix with distances for
     * one to all other points passed as argument.
     * @param matrix
     * @param diss
     * @throws java.io.IOException
     */
    public DistanceMatrix(SparseMatrix matrix, Dissimilarity diss) throws IOException {
        this.nrElements = matrix.getRowsCount();
        this.maxDistance = Double.NEGATIVE_INFINITY;
        this.minDistance = Double.POSITIVE_INFINITY;

        //Create and fill the distance distmatrix
        this.distmatrix = new double[this.nrElements - 1][];

        for (int i = 0; i < this.nrElements - 1; i++) {
            this.distmatrix[i] = new double[i + 1];

            for (int j = 0; j < this.distmatrix[i].length; j++) {
                this.setDistance(i + 1, j, diss.calculate(matrix.getRowWithIndex(i + 1), matrix.getRowWithIndex(j)));
            }
        }

        this.ids = matrix.getIds();
        // this.cdata = matrix.getClassData();
    }

    public DistanceMatrix(ArrayList<SparseVector> matrix, Dissimilarity diss) throws IOException {
        this.nrElements = matrix.size();
        this.maxDistance = Double.NEGATIVE_INFINITY;
        this.minDistance = Double.POSITIVE_INFINITY;

        //Create and fill the distance distmatrix
        this.distmatrix = new double[this.nrElements - 1][];

        for (int i = 0; i < this.nrElements - 1; i++) {
            this.distmatrix[i] = new double[i + 1];

            for (int j = 0; j < this.distmatrix[i].length; j++) {
                this.setDistance(i + 1, j, diss.calculate(matrix.get(i + 1), matrix.get(j)));
            }
        }

        this.ids = new int[nrElements];
        for (int i = 0; i < this.nrElements; i++) {
            ids[i] = matrix.get(i).getId();
        }
    }

    protected DistanceMatrix() {
    }

    /**
     * This method modify a distance in the distance matriz.
     * @param indexA The number of the first point.
     * @param indexB The number of the second point.
     * @param value The new value for the distance between the two points.
     */
    public void setDistance(int indexA, int indexB, double value) {
        assert (indexA >= 0 && indexA < nrElements && indexB >= 0 && indexB < nrElements) :
                "ERROR: index out of bounds!";

        if (indexA != indexB) {
            if (indexA < indexB) {
                this.distmatrix[indexB - 1][indexA] = value;
            } else {
                this.distmatrix[indexA - 1][indexB] = value;
            }

            if (minDistance > value && value >= 0.0f) {
                minDistance = value;
            } else {
                if (maxDistance < value && value >= 0.0f) {
                    maxDistance = value;
                }
            }
        }
    }

    /**
     * This method returns the distance between two points.
     * @param indexA The number of the first point.
     * @param indexB The number of the second point.
     * @return The distance between the two points.
     */
    public double getDistance(int indexA, int indexB) {
        assert (indexA >= 0 && indexA < nrElements && indexB >= 0 && indexB < nrElements) :
                "ERROR: index out of bounds!";

        if (indexA == indexB) {
            return 0.0f;
        } else {
            if (indexA < indexB) {
                return this.distmatrix[indexB - 1][indexA];
            } else {
                return this.distmatrix[indexA - 1][indexB];
            }
        }
    }

    /**
     * This method returns the maximum distance stored on the distance distmatrix.
     * @return Returns the maximun distance stored.
     */
    public double getMaxDistance() {
        return maxDistance;
    }

    /**
     * This method returns the minimum distance stored on the distance distmatrix.
     * @return Returns the minimun distance stored.
     */
    public double getMinDistance() {
        return minDistance;
    }

    /**
     * This method returns the number of points where distances are stored
     * on the distance distmatrix.
     * @return The number of points.
     */
    public int getElementCount() {
        return nrElements;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DistanceMatrix dmat = new DistanceMatrix(this.nrElements);
        dmat.maxDistance = this.maxDistance;
        dmat.minDistance = this.minDistance;

        for (int i = 0; i < this.distmatrix.length; i++) {
            System.arraycopy(this.distmatrix[i], 0, dmat.distmatrix[i], 0, this.distmatrix[i].length);
        }

        return dmat;
    }

    public void save(String filename) throws IOException {
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new FileWriter(filename));

            //writting the number of elements
            out.write(Integer.toString(this.nrElements));
            out.write("\r\n");

            //writing the ids
            if (this.ids != null) {
                for (int i = 0; i < ids.length - 1; i++) {
                    out.write(String.valueOf(ids[i]));
                    out.write(";");
                }

                out.write(String.valueOf(ids[ids.length - 1]));
                out.write("\r\n");
            } else {
                for (int i = 0; i < this.nrElements - 1; i++) {
                    out.write(Integer.toString(i) + ";");
                }

                out.write(Integer.toString(this.nrElements - 1) + "\r\n");
            }

            //writing the cdata
            if (cdata != null) {
                for (int i = 0; i < cdata.length - 1; i++) {
                    out.write(Double.toString(cdata[i]));
                    out.write(";");
                }

                out.write(Double.toString(cdata[cdata.length - 1]));
                out.write("\r\n");
            } else {
                for (int i = 0; i < this.nrElements - 1; i++) {
                    out.write("0;");
                }

                out.write("0\r\n");
            }

            for (int i = 0; i < this.distmatrix.length; i++) {
                for (int j = 0; j < this.distmatrix[i].length; j++) {
                    out.write(Double.toString(this.distmatrix[i][j]));

                    if (j < this.distmatrix[i].length - 1) {
                        out.write(";");
                    }
                }

                out.write("\r\n");
            }

        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public final void load(String filename) throws IOException {
        BufferedReader in = null;

        try {
            ///////////////////////////////////////////////////////////////////
            //getting the header information
            in = new BufferedReader(new FileReader(filename));

            //getting the number of elements
            this.nrElements = Integer.parseInt(in.readLine());

            //getting the elements ids
            StringTokenizer tUrls = new StringTokenizer(in.readLine(), ";");
            TIntArrayList aux = new TIntArrayList();
            while (tUrls.hasMoreTokens()) {
                aux.add(Integer.valueOf(tUrls.nextToken().trim()));
            }
            this.ids = new int[aux.size()];
            for (int i = 0; i < aux.size(); i++) {
                ids[i] = aux.get(i);
            }

            //checking
            if (this.ids.length != this.nrElements) {
                throw new IOException("The number of ids does not match "
                        + "with the size of matrix (" + this.ids.length
                        + " - " + this.nrElements + ").");
            }

            //getting the class data
            StringTokenizer tCdata = new StringTokenizer(in.readLine(), ";");
            TDoubleArrayList cdata_aux = new TDoubleArrayList();

            while (tCdata.hasMoreTokens()) {
                String token = tCdata.nextToken();
                cdata_aux.add(Double.parseDouble(token.trim()));
            }

            //checking
            if (this.ids.length != cdata_aux.size()) {
                throw new IOException("The number of class data items does not match "
                        + "with the size of matrix (" + this.ids.length
                        + " - " + this.nrElements + ").");
            }

            this.cdata = new double[cdata_aux.size()];
            for (int i = 0; i < this.cdata.length; i++) {
                this.cdata[i] = cdata_aux.get(i);
            }

            ///////////////////////////////////////////////////////////////////
            //creating the distance matrix
            this.maxDistance = Double.NEGATIVE_INFINITY;
            this.minDistance = Double.POSITIVE_INFINITY;
            this.distmatrix = new double[this.nrElements - 1][];

            for (int i = 0; i < this.distmatrix.length; i++) {
                this.distmatrix[i] = new double[i + 1];
            }

            for (int i = 0; i < this.distmatrix.length; i++) {
                String line = in.readLine();

                if (line != null) {
                    StringTokenizer tDistance = new StringTokenizer(line, ";");

                    for (int j = 0; j < this.distmatrix[i].length; j++) {
                        if (tDistance.hasMoreTokens()) {
                            String token = tDistance.nextToken();
                            double dist = Double.parseDouble(token.trim());
                            this.setDistance(i + 1, j, dist);
                        } else {
                            throw new IOException("Wrong distance matrix file format.");
                        }
                    }
                } else {
                    throw new IOException("Wrong distance matrix file format.");
                }
            }

        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(DistanceMatrix.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public double[] getClassData() {
        return this.cdata;
    }

    public int[] getIds() {
        return this.ids;
    }

    public void setClassData(double[] cdata) {
        this.cdata = cdata;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }
    protected int[] ids;
    protected double[] cdata;
    protected double[][] distmatrix;
    protected int nrElements;	//the number of points
    protected double maxDistance;		//Maximun distance in the distmatrix
    protected double minDistance;		//Minimum distance in the distmatrix
}
