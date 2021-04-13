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
    static Double[][][] data = new Double[NbClasses][NbEx][NbFeatures];//there are 50*3 examples at all. All have 4 features

    // Define train and test arrays
    static Double[][][] train = new Double[NbClasses][NbExLearning][NbFeatures];
    static Double[][][] test = new Double[NbClasses][NbExLearning][NbFeatures];


    public static void main(String[] args) {
        System.out.println("Starting kPPV");
        //ReadFile();
        //Train_test_split();
        ConfusionMatrix();

        //X is an example to classify (to take into data -test examples-)
        //Double X[] = new Double[NbFeatures];
        // distances: table to store all distances between the given example X and all examples in learning set, using ComputeDistances
        //Double[] distances = new Double[NbClasses*NbExLearning];

        //Double[] X = {2.8,2.5,1.1,0.1};

        //ComputeDistances(X, distances);

        //int l = PredictedClass(distances);
        //System.out.println("Predicted class : "+ l);
        //Double min = Collections.min(Arrays.asList(distances));
        //System.out.println("Double : "+ min);
        //To be done
    }


    private static void ComputeDistances(Double[] x, Double[] distances) {
        //---compute the distance between an input data x to test and all examples in training set (in data)
        double temp;
        int index = 0;
        // To be done
        for(int classe=0; classe <NbClasses; classe++){
            distances[classe] = 0.0;
            for(int ex = 0; ex < NbExLearning; ex++){
                temp = 0.0;
                for(int feature = 0; feature < NbFeatures; feature++){
                    temp+= Math.pow((train[classe][ex][feature] - x[feature]),2);
                }
                System.out.println("Temp "+index+" : "+Math.sqrt(temp));
                distances[index] = Math.sqrt(temp);
                index++;
            }
        }
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
        catch (Exception e) { System.out.println(e); }
    }

    // Function to split train and test data
    // Get the first 25 for each class for the training data
    // And the rest for the test data
    private static void Train_test_split(){
        for(int classe=0; classe < NbClasses; classe++){
            for(int ex=0; ex < NbEx; ex++){
                if(ex < NbExLearning){
                    if (NbFeatures >= 0) System.arraycopy(data[classe][ex], 0, train[classe][ex], 0, NbFeatures);
                } else {
                    if (NbFeatures >= 0)
                        System.arraycopy(data[classe][ex], 0, test[classe][ex - NbExLearning], 0, NbFeatures);
                }
            }
        }
    }

    // Function to return the predicted class
    // It returns 0 if the distance index is between 0 and 25(exclusive)
    // It returns 1 if the distance index is between 2 and 50(exclusive)
    // It returns 2 if the distance index is between 50 and 75(exclusive)
    private static int PredictedClass(Double[] distances){
        int classe = 0;
        Double min = distances[0];
        for(int i = 1; i < distances.length; i++){
            if(distances[i] < min){
                if(i < 25)
                    classe = 0;
                else if (i < 50)
                    classe = 1;
                else if (i < 75)
                    classe = 2;
            }
        }
        return classe;
    }

    //private static int

    private static void ConfusionMatrix(){
        //int[] Iris_setosa = new int[NbClasses];
        //int[] Iris_versicolor = new int[NbClasses];
        //int[] Iris_virginica = new int[NbClasses];

        int[] Iris_setosa = {1,2,3};
        int[] Iris_versicolor = {4,5,6};
        int[] Iris_virginica = {7,8,9};

        System.out.println("                        Actual               ");
        System.out.println("            --------------------------------|");
        System.out.println(" Predicted | Classe 0 | Classe 1 | Classe 2 |");
        System.out.println("--------------------------------------------|");
        for(int i=0;i <NbClasses; i++){
            System.out.println("| Classe "+i+" |    "+Iris_setosa[i]+"     |     "+Iris_versicolor[i]+"    |     "+Iris_virginica[i]+"    |");
            System.out.println("--------------------------------------------|");
        }

    }



} //-------------------End of class kPPV-------------------------
