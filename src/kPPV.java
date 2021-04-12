import java.io.*;

/**
 * @author hubert.cardot
 */
public class kPPV {
    //General variables for dealing with Iris data (UCI repository)
    // NbEx: number of data per class in dataset
    // NbClasses: Number of classes to recognize
    // NbFeatures: Dimensionality of dataset
    // NbExLearning: Number of examples per class used for learning (there are the first ones in data storage for each class)

    static int NbEx=50, NbClasses=3, NbFeatures=4, NbExLearning=25;
    static Double data[][][] = new Double[NbClasses][NbEx][NbFeatures];//there are 50*3 examples at all. All have 4 features

    // Define train and test arrays
    static Double train[][][] = new Double[NbClasses][NbExLearning][NbFeatures];
    static Double test[][][] = new Double[NbClasses][NbExLearning][NbFeatures];


    public static void main(String[] args) {
        System.out.println("Starting kPPV");
        ReadFile();
        Train_test_split();

        //X is an example to classify (to take into data -test examples-)
        Double X[] = new Double[NbFeatures];
        // distances: table to store all distances between the given example X and all examples in learning set, using ComputeDistances
        Double distances[] = new Double[NbClasses*NbExLearning];

        //To be done
    }


    private static void ComputeDistances(Double x[], Double distances[]) {
        //---compute the distance between an input data x to test and all examples in training set (in data)

        // To be done

        /*int classe = 0, n=0;
        while (n < 25) {
            for(int i=0; i<NbFeatures; i++){
                distances[i] += data[classe][n][i] - x[i];
            }
            n++;
        }*/

    }

    //——-Reading data from iris.data file
    //1 line -> 1 example
    //50 first lines are 50 examples of class 0, next 50 of class 1 and 50 of class 2
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

    // Function to split train and test data
    // Get the first 25 for each class for the training data
    // And the rest for the test data
    public static void Train_test_split(){
        for(int classe=0; classe < NbClasses; classe++){
            for(int ex=0; ex < NbEx; ex++){
                if(ex < NbExLearning){
                    for(int feature=0; feature < NbFeatures; feature++)
                        train[classe][ex][feature] = data[classe][ex][feature];
                } else {
                    for(int feature=0; feature < NbFeatures; feature++)
                        test[classe][ex-NbExLearning][feature] = data[classe][ex][feature];
                }
            }
        }
    }


} //-------------------End of class kPPV-------------------------
