import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
 
public class Genetic {
    private int generationSize;
    private int genomeSize;
    private int numberOfCities;
    private double reproductionSize;
    private int maxNumberOfIterations;

    private float mutationRate;
    private int tournamentSize;
    private SelectionType selectionType;
    private int[][] travelCost;
    private int startingCity;
    private int targetFitness;

    public Genetic(int _numberOfCities, SelectionType _selectionType, int[][] _travelPrices, int _startingCity, int _targetFitness) {

        this.numberOfCities = _numberOfCities;
        this.genomeSize = _numberOfCities - 1;
        this.selectionType = _selectionType;
        this.travelCost = _travelPrices;
        this.startingCity = _startingCity;
        this.targetFitness = _targetFitness;

        //parameters
        generationSize = 2000;              //initial population size
        reproductionSize = 0.5;             //200 the percentage that determine future population size
        maxNumberOfIterations = 1000;       //how many times this algorithm will be executed till printing solution
        mutationRate = 0.1f;                //percentage of mutation occurance
        tournamentSize = 40;                 //for tournament selection
    }

    public List<Genome> initialPopulation() {
        List<Genome> population = new ArrayList<>();
        for (int i = 0; i < generationSize; i++) {
            population.add(new Genome(numberOfCities, travelCost, startingCity));
        }
        return population;
    }

    public List<Genome> selection(List<Genome> population) {
        List<Genome> selectedList = new ArrayList<>();
        Genome bestGenome;
        for (int i = 0; i < (generationSize * reproductionSize); i++) {
            if (selectionType == SelectionType.ROULETTE)
                selectedList.add(rouletteSelection(population));

            else if (selectionType == SelectionType.TOURNAMENT) {
                selectedList.add(tournamentSelection(population));
            }
        }
        return selectedList;
    }

    //to fix
    public Genome rouletteSelection(List<Genome> _population) {

        Integer sumOfFitnessValue = _population.stream().map(Genome::getFitness).mapToInt(Integer::intValue).sum();
        Random random = new Random();
        int selectedValue = random.nextInt(sumOfFitnessValue);
        float recValue = (float) 1 / selectedValue;
        float currentSum = 0;
        for (Genome genome : _population) {
            currentSum += (float) 1 / genome.getFitness();
            if (currentSum >= recValue) {
                return genome;
            }
        }
        int selectRandom = random.nextInt(generationSize);
        return _population.get(selectRandom);
    }

    public static <E> List<E> pickRandomElements(List<E> _list, int n) {
        Random r = new Random();
        int length = _list.size();
        if (length < n) return null;

        for (int i = length - 1; i >= length - n; --i) {
            Collections.swap(_list, i, r.nextInt(i + 1));
        }
        return _list.subList(length - n, length);
    }

    public Genome tournamentSelection(List<Genome> _population) {
        List<Genome> selected = pickRandomElements(_population, tournamentSize);
        return Collections.min(selected);
    }

    public Genome mutation(Genome _genome) {
        Random random = new Random();
        if (random.nextFloat() < mutationRate) {
            List<Integer> genome = _genome.getGenome();
            Collections.swap(genome, random.nextInt(genomeSize), random.nextInt(genomeSize));
            return new Genome(genome, numberOfCities, travelCost, startingCity);
        }
        return _genome;
    }

    public List<Genome> createGeneration(List<Genome> _population) {
        List<Genome> generation = new ArrayList<>();
        int currentGenerationSize = 0;
        while (currentGenerationSize < generationSize) {
            List<Genome> parents = pickRandomElements(_population, 2);
            List<Genome> children = crossover(parents);
            children.set(0, mutation(children.get(0)));
            children.set(1, mutation(children.get(1)));
            generation.addAll(children);
            currentGenerationSize += 2;
        }
        return generation;
    }

    public List<Genome> crossover(List<Genome> _parents) {

        Random random = new Random();
        int breakpoint = random.nextInt(genomeSize);
        List<Genome> children = new ArrayList<>();

        List<Integer> parent1Genome = new ArrayList<>(_parents.get(0).getGenome());
        List<Integer> parent2Genome = new ArrayList<>(_parents.get(1).getGenome());

        // creating child 1
        for (int i = 0; i < breakpoint; i++) {
            int newVal = parent2Genome.get(i);
            Collections.swap(parent1Genome, parent1Genome.indexOf(newVal), i);
        }
        children.add(new Genome(parent1Genome, numberOfCities, travelCost, startingCity));
        parent1Genome = _parents.get(0).getGenome(); // resetting the edited parent

        // creating child 2
        for (int i = breakpoint; i < genomeSize; i++) {
            int newVal = parent1Genome.get(i);
            Collections.swap(parent2Genome, parent2Genome.indexOf(newVal), i);
        }
        children.add(new Genome(parent2Genome, numberOfCities, travelCost, startingCity));

        return children;
    }

    public Genome optimize(int minutes) {
        List<Genome> population = initialPopulation();
        Genome globalBestGenome = population.get(0);
        long startTime = System.nanoTime();
        long endTime, timeElapsed;

        for (int i = 0; i < maxNumberOfIterations; i++) {
            List<Genome> selected = selection(population);
            population = createGeneration(selected);
            globalBestGenome = Collections.min(population);
            if (globalBestGenome.getFitness() < targetFitness)
                break;
            endTime = System.nanoTime();
            timeElapsed = endTime - startTime;
            if ((timeElapsed / 1000000000) > minutes * 60) {
//                System.out.println("Time break");
                break;
            }
        }
        return globalBestGenome;
    }

    public void printGeneration(List<Genome> generation) {
        for (Genome genome : generation) {
            System.out.println(genome);
        }
    }
}
