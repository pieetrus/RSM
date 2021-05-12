import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
 
public class Matrix {

    int size;
    Vector<Vector<Integer>> matrixVector;
    String[] dim;
    String[] dim2;

    Matrix() {
        matrixVector = new Vector<Vector<Integer>>();
        size = 0;
    }

    public void display() {
        Iterator iterator = matrixVector.iterator();
        for (int i = 0; i < size; i++) {
            while (iterator.hasNext()) {
                System.out.print(iterator.next() + " \n");
            }
        }
    }

    public void clearMatrix() {
        matrixVector.clear();
    }

    boolean readFromFile(String filename) {
        int dimension = 0;

        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.contains("DIMENSION")) {
                    dim = data.split(" ", 2);
                    dimension = Integer.valueOf(dim[1]);
                    this.setSize(dimension);
//                    System.out.println("Dimension is " + dimension);
                }
                if (data.contains("EDGE_WEIGHT_SECTION")) break;
            }

            Vector v1 = new Vector();
            Vector v2 = new Vector();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                int k = 0;
                dim2 = data.split(" ");
                for (int i = 0; i < dim2.length; i++) {
                    if (!dim2[i].isEmpty()) k++;
                }

                dim = new String[k];
                int m = 0;

                for (int i = 0; i < dim2.length; i++) {
                    if (!dim2[i].isEmpty()) {
                        dim[m] = dim2[i];
                        m++;
                    }
                }

                //for file with equal dimension and cost value in line
                if (dim.length == getSize()) {
                    v1 = new Vector();
                    for (int i = 0; i < dim.length; i++) {
                        String tmp = dim[i];
                        if (tmp.contains(" ") || tmp.isEmpty()) continue;
                        v1.add(tmp);
                        if (!v1.isEmpty()) {
                            matrixVector.add(v1);
                        }
                    }

                    //for file with different dimension and cost value in line
                } else if (dim.length < getSize()) {

                    for (int i = 0; i < dim.length; i++) {
                        String tmp = dim[i];
                        if (tmp.contains(" ") || tmp.isEmpty()) continue;
                        v1.add(tmp);
                    }

                    if (v1.size() < getSize()) continue;
                    else {
                        int j = 0;
                        while (v1.size() != getSize()) {
                            v2.add(v1.get(getSize()));
                            v1.remove(getSize());
                            j++;
                        }

                        if (v1.size() == getSize()) {
                            matrixVector.add(v1);
                            v1 = v2;
                            v2 = new Vector();
                            if (v1.size() == getSize()) {
                                matrixVector.add(v1);
                                v1 = new Vector();
                            }
                        }

                    }
                }


            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return false;
    }

    Vector<Vector<Integer>> getMatrixVector() {
        return matrixVector;
    }

    int getValue(int indexFirst, int indexSecond) {

        String tmpString = String.valueOf(matrixVector.get(indexFirst).get(indexSecond));
        int tmpInteger = Integer.valueOf(tmpString);
        //System.out.println(tmpInteger);
        return tmpInteger;
    }

    int getSize() {
        return size;
    }

    void setSize(int tmpSize) {
        this.size = tmpSize;
    }
}

