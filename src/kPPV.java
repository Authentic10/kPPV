import java.io.*;

/**
 * @author hubert.cardot
 */
public class kPPV {
    //General variables for dealing with Iris data (UCI repository)
    // NbEx: number of data per class in dataset
    // NbClasses: Number of classes to recognize
    // NbFeatures: Dimensionality of dataset
    // NbExLearning: Number of exemples per class used for learning (there are the first ones in data storage for each class)

    static int NbEx=50, NbClasses=3, NbFeatures=4, NbExLearning=25;
    static Double data[][][] = new Double[NbClasses][NbEx][NbFeatures];//there are 50*3 exemples at all. All have 4 features

    public static void main(String[] args) {
        System.out.println("Starting kPPV");
        ReadFile();

        //X is an exemple to classify (to take into data -test examples-)
        Double X[] = new Double[NbFeatures];
        // distances: table to store all distances between the given exemple X and all exemples in learning set, using ComputeDistances
        Double distances[] = new Double[NbClasses*NbExLearning];

        //To be done
    }


    private static void ComputeDistances(Double x[], Double distances[]) {
        //---compute the distance between an input data x to test and all examples in training set (in data)

        // To be done
    }

    //——-Reading data from iris.data file
    //1 line -> 1 exemple
    //50 first lines are 50 exemples of class 0, next 50 of class 1 and 50 of class 2
    private static void ReadFile() {

        String line, subPart;
        int classe=0, n=0;
        try {
            BufferedReader fic=new BufferedReader(new FileReader("iris.data"));
            while ((line=fic.readLine())!=null) {
                for(int i=0;i<NbFeatures;i++) {
                    subPart = line.substring(i*NbFeatures, i*NbFeatures+3);
                    data[classe][n][i] = Double.parseDouble(subPart);
                    //System.out.println(data[classe][n][i]+" "+classe+" "+n);
                }
                if (++n==NbEx) { n=0; classe++; }
            }
        }
        catch (Exception e) { System.out.println(e.toString()); }
    }

} //-------------------End of class kPPV-------------------------
