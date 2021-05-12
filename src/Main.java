import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
 
public class Main {

    static int startingCity = 0;
    static int numberOfCities;
    static int minutes;
    static int[][] travelCost;
    static Map<String, Integer> solutionsForFiles = new LinkedHashMap<>();
    static Matrix costMatrix;


    public static void main(String[] args) throws FileNotFoundException {
        initInputFilesWithResults();
        int numberOfReruns = 10;
        minutes = 5;
        testForOneFile("ftv33.atsp", solutionsForFiles.get("ftv33.atsp"), numberOfReruns, SelectionType.TOURNAMENT);
        testForAllFiles(numberOfReruns, SelectionType.TOURNAMENT);
//		testForAllFiles(numberOfReruns, SelectionType.ROULETTE);
    }

    public static void testForAllFiles(int numberOfReruns, SelectionType selectionType) throws FileNotFoundException {
        // prepare unique name of file
        Date now = new Date();
        String resultFileName = "reruns=" + numberOfReruns + "_selection=" + selectionType.name() + "_timestamp=" + now.getDay() + "_" + now.getHours() + "_" + now.getMinutes() + "_" + now.getSeconds();
        PrintWriter printWriter = new PrintWriter(resultFileName + ".csv");

        // write column names to csv file
        String columns = "file, number of cities, selection type, runs, average time, best time, average distance, best distance, optimal distance";
        printWriter.println(columns);

        // run algorithm for each file
        for (Map.Entry<String, Integer> problem : solutionsForFiles.entrySet()) {
            String fileName = problem.getKey();
            long sumOfTimes = 0;
            long sumOfDistances = 0;
            long bestTime = Long.MAX_VALUE;
            long bestDistance = Long.MAX_VALUE;

            System.out.println(fileName);

            for (int run = 0; run < numberOfReruns; run++) {
                // initialize matrix
                initializeMatrix(fileName);

                // run algorithm
                long start = System.currentTimeMillis();
                Genetic genetic = new Genetic(numberOfCities, selectionType, travelCost, startingCity, 0);
                Genome result = genetic.optimize(minutes);
                long end = System.currentTimeMillis();

                // get result parameters
                long timeElapsed = end - start;
                sumOfTimes += timeElapsed;
                bestTime = Math.min(bestTime, timeElapsed);

                long distance = result.getFitness();
                sumOfDistances += distance;
                bestDistance = Math.min(bestDistance, distance);
            }

            // save results from one file to result file
            String resultSummary = fileName + ", " + numberOfCities + ", " + selectionType.name() + ", " + numberOfReruns + ", " + sumOfTimes / numberOfReruns + ", " + bestTime + ", " + sumOfDistances / numberOfReruns + ", " + bestDistance + ", " + problem.getValue();
            printWriter.println(resultSummary);

        }
        printWriter.close();
    }


    public static void testForOneFile(String fileName, int optimalDistance, int numberOfReruns, SelectionType selectionType) throws FileNotFoundException {
        // prepare unique name of file
        Date now = new Date();
        String resultFileName = fileName.split("\\.")[0] + "_reruns=" + numberOfReruns + "_selection=" + selectionType.name() + "_timestamp=" + now.getDay() + "_" + now.getHours() + "_" + now.getMinutes() + "_" + now.getSeconds();
        PrintWriter printWriter = new PrintWriter(resultFileName + ".csv");

        // write column names to csv file
        String columns = "time, distance, path";

        printWriter.println("file , " + fileName);
        printWriter.println("number of cities , " + numberOfCities);
        printWriter.println("number of reruns , " + numberOfReruns);
        printWriter.println("selection type , " + selectionType.name());

        printWriter.println(columns);

        long sumOfTimes = 0;
        long sumOfDistances = 0;
        long bestTime = Long.MAX_VALUE;
        long bestDistance = Long.MAX_VALUE;

        System.out.println(fileName);

        for (int run = 0; run < numberOfReruns; run++) {
            // initialize matrix
            initializeMatrix(fileName);

            // run algorithm
            long start = System.currentTimeMillis();
            Genetic genetic = new Genetic(numberOfCities, selectionType, travelCost, startingCity, 0);
            Genome result = genetic.optimize(minutes);
            long end = System.currentTimeMillis();

            // get result parameters
            long timeElapsed = end - start;
            sumOfTimes += timeElapsed;
            bestTime = Math.min(bestTime, timeElapsed);

            long distance = result.getFitness();
            sumOfDistances += distance;
            bestDistance = Math.min(bestDistance, distance);
            // save each result to file
            printWriter.println(timeElapsed + ", " + distance + ", " + result.getGenome().toString());
        }

        // save results from one file to result file
        printWriter.println("average time ," + sumOfTimes / numberOfReruns);
        printWriter.println("best time ," + bestTime);
        printWriter.println("average distance ," + sumOfDistances / numberOfReruns);
        printWriter.println("best distance ," + bestDistance);
        printWriter.println("optimal distance ," + optimalDistance);


        printWriter.close();
    }


    public static void initInputFilesWithResults() {
        solutionsForFiles.put("tsp10.atsp", 212); // T S P
        solutionsForFiles.put("br17.atsp", 39); // B R
        solutionsForFiles.put("ftv33.atsp", 1286);
        solutionsForFiles.put("ftv38.atsp", 1530);
        solutionsForFiles.put("ft53.atsp", 6905); // F T
        solutionsForFiles.put("ftv55.atsp", 1608);
        solutionsForFiles.put("ftv47.atsp", 1776);
        solutionsForFiles.put("ftv64.atsp", 1839);
        solutionsForFiles.put("ft70.atsp", 38673); // F T
        solutionsForFiles.put("ftv70.atsp", 1950);
        solutionsForFiles.put("ftv170.atsp", 2755);
        solutionsForFiles.put("kro124p.atsp", 36230); // K R O


	/*
	http://elib.zib.de/pub/mp-testdata/tsp/tsplib/atsp/index.html
	http://elib.zib.de/pub/mp-testdata/tsp/tsplib/atsp-sol.html

	tsp10: 212 // T S P
    br17: 39 // B R
    ftv33: 1286
    ftv38: 1530
    ftv47: 1776
    ft53: 6905 // F T
    ftv55: 1608
    ftv64: 1839
    ft70: 38673 // F T
    ftv70: 1950
    ftv170: 2755
    kro124: 36230 // K R O

    ftv35: 1473
    ftv44: 1613
    ftv90: 1579
    ftv100: 1788
    ftv110: 1958
    ftv120: 2166
    ftv130: 2307
    ftv140: 2420
    ftv150: 2611
    ftv160: 2683
    p43: 5620
    rbg323: 1326
    rbg358: 1163
    rbg403: 2465
    rbg443: 2720
    ry48p: 14422

	 */
    }

    public static void initializeMatrix(String fileName) {
        costMatrix = new Matrix();
        costMatrix.readFromFile(fileName);
        numberOfCities = costMatrix.getSize();
        travelCost = new int[numberOfCities][numberOfCities];
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                travelCost[i][j] = costMatrix.getValue(i, j);
            }
        }
    }


    public static void printTravelCosts() {
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                System.out.print(travelCost[i][j]);
                if (travelCost[i][j] / 10 == 0)
                    System.out.print("  ");
                else
                    System.out.print(' ');
            }
            System.out.println("");
        }
    }

}
