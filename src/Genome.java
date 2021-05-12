import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
public class Genome implements Comparable {

    List<Integer> genome;
    int[][] travelCostMatrix;    //Cost of transportation between two cities
    int startingCity;
    int numberOfCities = 0;     //size of matrix
    int fitness;

    public Genome(int _numberOfCities, int[][] _travelCostMatrix, int _startingCity) {
        this.startingCity = _startingCity;
        this.numberOfCities = _numberOfCities;
        this.travelCostMatrix = _travelCostMatrix;
        this.genome = randomizeCitiesList();
        fitness = this.calculateFitnessValue();

    }

    public Genome(List<Integer> _listOfCities, int _numberOfCities, int[][] _travelCostMatrix, int _startingCity) {
        this.startingCity = _startingCity;
        this.numberOfCities = _numberOfCities;
        this.travelCostMatrix = _travelCostMatrix;
        this.genome = _listOfCities;
        fitness = this.calculateFitnessValue();
    }


    private int calculateFitnessValue() {
        int fitnessValue = 0;
        int currentCity = this.startingCity;
        //for each loop
        for (int gene : genome) {
            fitnessValue += travelCostMatrix[currentCity][gene];
            currentCity = gene;
        }
        fitnessValue += travelCostMatrix[genome.get(numberOfCities - 2)][startingCity];
        return fitnessValue;
    }

    private List<Integer> randomizeCitiesList() {
        List<Integer> resultList = new ArrayList<Integer>();
        for (int i = 0; i < numberOfCities; i++) {
            if (i != startingCity) resultList.add(i);
        }
        Collections.shuffle(resultList);
        return resultList;
    }

    public List<Integer> getGenome() {
        return genome;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Path: ");
        sb.append(startingCity);
        for (int gene : genome) {
            sb.append(" ");
            sb.append(gene);
        }
        sb.append(" ");
        sb.append(startingCity);
        sb.append("\nDistance: ");
        sb.append(this.fitness);
        return sb.toString();

    }

    @Override
    public int compareTo(Object object) {
        Genome genome = (Genome) object;
        if (this.fitness > genome.getFitness())
            return 1;
        else if (this.fitness < genome.getFitness())
            return -1;
        else return 0;
    }
}

