import java.io.*;
import java.util.*;
import java.util.stream.IntStream;



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
        System.out.println("Starting kPPV ...");
        System.out.println("Reading data ...");
        ReadFile();
        System.out.println("Splitting data into train and test ...");
        TrainTestSplit();

        Double[] distances = new Double[NbClasses*NbExLearning];
        int[] predictions ;

        System.out.println("Testing test data ...");
        predictions = PredictTestData(distances, 3);

        System.out.println("Metrics and Confusion matrix");
        ConfusionMatrix(predictions);

        System.out.println("Cross Validation");
        CrossValidation(3);

    }


    private static void ComputeDistances(Double[] x, Double[] distances) {
        //---compute the distance between an input data x to test and all examples in training set (in data)
        double temp;
        int index = 0;
        for(int classe=0; classe <NbClasses; classe++){
            for(int ex = 0; ex < NbExLearning; ex++){
                temp = 0.0;
                for(int feature = 0; feature < NbFeatures; feature++){
                    temp+= Math.pow((train[classe][ex][feature] - x[feature]),2);
                }
                distances[index] = Math.sqrt(temp);
                index++;
            }
        }
    }

    //??????-Reading data from iris.data file
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
                }
                if (++n==NbEx) { n=0; classe++; }
            }
        }
        catch (Exception e) { System.out.println(e); }
    }

    // Function to split train and test data
    // Get the first 25 for each class for the training data
    // And the rest for the test data
    private static void TrainTestSplit(){
        for(int classe=0; classe < NbClasses; classe++){
            for(int ex=0; ex < NbEx; ex++){
                if(ex < NbExLearning){
                    System.arraycopy(data[classe][ex], 0, train[classe][ex], 0, NbFeatures);
                } else {
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

                min = distances[i];
            }
        }
        return classe;
    }

    // Prediction with parameter k
    private static int PredictedClass(Double[] distances, int k){

        int classe = -1;

        // HashMap to store distances and predicted classes
        Map<Double, Integer> preds = new HashMap<>();

        for(int i = 0; i <distances.length; i++){
            if (i<25)
                preds.put(distances[i],0);
            else if(i <50)
                preds.put(distances[i],1);
            else if(i <75)
                preds.put(distances[i],2);
        }

        // ArrayList to store and sort the distances for knn
        ArrayList<Double> sortedDistances = new ArrayList<>(preds.keySet());
        // Sort the distances
        Collections.sort(sortedDistances);

        int classe_0 = 0, classe_1 = 0, classe_2 = 0;
        int pred, counter = 0;

        // Iterate through the sorted distances and get the corresponding prediction class
        for(double x : sortedDistances){
            if(counter == k )
                break;

            pred = preds.get(x);

            if(pred==0)
                classe_0++;
            else if(pred==1)
                classe_1++;
            else if(pred==2)
                classe_2++;

            counter++;
        }

        // Check the class that have the highest frequency and return it
        if((classe_0 > classe_1) && (classe_0 > classe_2))
            classe = 0;
        else if((classe_1 > classe_0) && (classe_1 > classe_2))
            classe = 1;
        else if((classe_2 > classe_0) && (classe_2 > classe_1))
            classe = 2;

        return classe;
    }

    // Predict all the test data
    private static int[] PredictTestData(Double[] distances, int k){
        // Array to store all the predictions
        int[] predictions = new int[NbClasses*NbExLearning];
        int index = 0; // Index for the prediction array

        for(int classe=0; classe < NbClasses; classe++){
            for(int ex=0; ex < NbExLearning; ex++){
                // Compute Euclidean Distance for each each example in the test
                // with each example in the training data
                ComputeDistances(test[classe][ex], distances);
                // Get the prediction class with PredictedClass function
                if(k==1)
                    predictions[index] = PredictedClass(distances);
                else
                    predictions[index] = PredictedClass(distances, k);
                index++;
            }
        }
        return predictions;
    }


    // Display Precision and Recall metrics and the confusion matrix
    private static void ConfusionMatrix(int[] predictions){
        // Arrays to store the actual and predicted values for each class
        int[] Iris_setosa = {0,0,0}, Iris_versicolor = {0,0,0}, Iris_virginica = {0,0,0};

        for(int i=0; i < predictions.length; i++){
            if(i < 25){ // Check if it's the first class
                if(predictions[i] == 0) // If prediction is correct
                    Iris_setosa[0]+=1;
                else if(predictions[i] == 1)
                    Iris_versicolor[0]+=1;
                else
                    Iris_virginica[0]+=1;
            } else if(i < 50){ // Check if it's the second class
                if(predictions[i] == 0)
                    Iris_setosa[1]+=1;
                else if(predictions[i] == 1) // If prediction is correct
                    Iris_versicolor[1]+=1;
                else
                    Iris_virginica[1]+=1;
            } else if(i < 75){ // Check if it's the last class
                if(predictions[i] == 0)
                    Iris_setosa[2]+=1;
                else if(predictions[i] == 1)
                    Iris_versicolor[2]+=1;
                else
                    Iris_virginica[2]+=1; // If prediction is correct
            }
        }

        // Precision variables for each class
        float Class1Precision, Class2Precision, Class3Precision;
        // Recall variables for each class
        float Class1Recall, Class2Recall, Class3Recall;

        // Overall precision and recall variables
        float Precision, Recall;

        // Precision

        Class1Precision = SafeZero(Iris_setosa, 0);
        Class2Precision = SafeZero(Iris_versicolor, 1);
        Class3Precision = SafeZero(Iris_virginica, 2);

        Precision = (Class1Precision+ Class2Precision + Class3Precision) / 3;

        // Recall

        Class1Recall = SafeZero(Iris_setosa, Iris_versicolor, Iris_virginica, 0);
        Class2Recall = SafeZero(Iris_versicolor, Iris_setosa, Iris_virginica, 1);
        Class3Recall = SafeZero(Iris_virginica, Iris_versicolor, Iris_setosa, 2);

        Recall = (Class1Recall+ Class2Recall + Class3Recall) / 3;

        System.out.println("\nPRECISION\n");

        System.out.println("Classe 1 Precision : "+ Class1Precision);
        System.out.println("Classe 2 Precision : "+ Class2Precision);
        System.out.println("Classe 3 Precision : "+ Class3Precision);
        System.out.println("\n Overall Precision : "+ Precision);

        System.out.println("\nRECALL\n");

        System.out.println("Classe 1 Recall : "+ Class1Recall);
        System.out.println("Classe 2 Recall : "+ Class2Recall);
        System.out.println("Classe 3 Recall : "+ Class3Recall);
        System.out.println("\nOverall Recall : "+ Recall);

        // Confusion matrix
        System.out.println("\n\nCONFUSION MATRIX");

        System.out.println("                        Actual               ");
        System.out.println("            --------------------------------");
        System.out.println(" Predicted | Classe 0 | Classe 1 | Classe 2 ");
        System.out.println("--------------------------------------------");
        System.out.println("| Classe "+0+" |    "+Iris_setosa[0]+"     |     "+Iris_setosa[1]+"    |     "+Iris_setosa[2]+"    ");
        System.out.println("--------------------------------------------");
        System.out.println("| Classe "+1+" |    "+Iris_versicolor[0]+"     |     "+Iris_versicolor[1]+"    |     "+Iris_versicolor[2]+"    ");
        System.out.println("--------------------------------------------");
        System.out.println("| Classe "+2+" |    "+Iris_virginica[0]+"     |     "+Iris_virginica[1]+"    |     "+Iris_virginica[2]+"    ");
        System.out.println("--------------------------------------------");


    }

    //TrainTestSplit for Cross Validation with interval
    private static void TrainTestSplit(int inter){
        int train_counter, test_counter;
        for(int classe=0; classe < NbClasses; classe++){
            train_counter =0; test_counter = 0;
            for(int ex=0; ex < NbEx; ex++){
                if((inter <= ex) && (ex < NbExLearning+inter)){
                    System.arraycopy(data[classe][ex], 0, train[classe][train_counter], 0, NbFeatures);
                    train_counter++;
                } else {
                    System.arraycopy(data[classe][ex], 0, test[classe][test_counter], 0, NbFeatures);
                    test_counter++;
                }
            }
        }
    }

    // Cross Validation function
    private static void CrossValidation(int cv){
        int inter = 0; // Interval for split

        if(cv <=0) //If user gives value less or equal to 0, compute only one validation
            cv = 1;
        else if (cv > 6) //If user gives value greater than 6, compute 6 cross validation (can't do more than 6 cross validation)
            cv = 6;

        // Compute the validation for each iteration
        for(int i=0; i < cv; i++){
            TrainTestSplit(inter);
            Double[] distances = new Double[NbClasses*NbExLearning];
            int[] predictions ;

            System.out.println("\n\nIteration "+(i+1)+" ...");
            predictions = PredictTestData(distances, 3);

            System.out.println("Metrics and Confusion matrix");
            ConfusionMatrix(predictions);
            inter+=5;
        }
    }

    // Function to safely compute the precisions
    private static float SafeZero(int[] array, int idx){
        return IntStream.of(array).sum() == 0 ? 0 : (float)array[idx]/(IntStream.of(array).sum());
    }

    // Function to safely compute the recalls
    private static float SafeZero(int[] array1, int[] array2, int[] array3 , int idx){
        return (float)array1[idx]/(array1[idx]+array2[idx]+array3[idx]);
    }


} //-------------------End of class kPPV-------------------------
