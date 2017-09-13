package generators.misc;

import java.util.Locale;
import java.util.Vector;

import generators.framework.Generator;
import generators.framework.GeneratorBundle;
import generators.misc.continousdoubleauction.ContinousDoubleAuction;
import generators.misc.devs.DEVS;
import generators.misc.doubleauction.DoubleAuction;
import generators.misc.drunkenbishop.DrunkenBishop;
import generators.misc.gameoflife.GameOfLifeParallel;
import generators.misc.kNN.KNNAllOrNothing;
import generators.misc.kNN.KNNMajorityVote;
import generators.misc.lee.LeeGenerator;
import generators.misc.nonuniformTM.SpaceComplexity;
import generators.misc.schulzemethod.SchulzeMethod;
import generators.misc.sweepandpruneaabb2d.SweepAndPruneAABB2D;

public class DummyGenerator implements GeneratorBundle {

  @Override
  public Vector<Generator> getGenerators() {
    Vector<Generator> generators = new Vector<Generator>(35, 15);
    generators.add(new CandidateElimination());
    generators.add(new ClockPageReplacement());
    generators.add(new OPTICS());
    generators.add(new TowerOfHanoi());
    generators.add(new VogelApproximation());
    generators.add(new Levenshtein());
    generators.add(new MoodleConnectPresentation());
    generators.add(new VariableDemo());
    
    // TODO "under probation"
    // generators.add(new Choly());
    // generators.add(new GameOfLive());
    // generators.add(new GaussianFilter());
    // generators.add(new Greedy());
    // generators.add(new Histogramm());
    // generators.add(new KdTree());
    // generators.add(new RoundRobin());
    // generators.add(new Simplex());
    // generators.add(new Simplex2());
    
    // Generators from the AlgoAnim course in summer semester 2011.
    generators.add(new JaroWinkler());
    generators.add(new RamerDouglasPeucker());
    generators.add(new KVDiagramm());
    generators.add(new MaekawaAlgorithm());
    generators.add(new CYK());
    generators.add(new DFAMinimierung());
    generators.add(new DTW());
    generators.add(new FloydCycle());
    generators.add(new Josephus());
    generators.add(new LongestCommonSubsequence());
    generators.add(new NeedlemanWunsch());
    
    // Generators from the AlgoAnim course in summer semester 2012.
    generators.add(new HPFgenerator());
    generators.add(new SRTFgenerator());
    generators.add(new LoopParallelizationReduction());
    generators.add(new GameOfLive());
    generators.add(new FisherYatesShuffle());
    generators.add(new Base64());
    generators.add(new Casteljau());
    generators.add(new DropHeuristik());
    generators.add(new BrentsAlgorithm());
    generators.add(new ValueIterationGenerator());
    generators.add(new DTWAnimation());
    generators.add(new PolicyIterationGenerator());
    generators.add(new NearestCentroidClassifier());
//    generators.add(new Simplex2());
    generators.add(new DeBoorAlgorithmus());
    generators.add(new DeCasteljauAlgorithmus());

    // Generators from the AlgoAnim course in summer semester 2013.
    generators.add(new ForwardAlgorithmGenerator());
    generators.add(new DEVS());
    generators.add(new ModiMethod());
    generators.add(new DecentralStandardization());
    generators.add(new PalindromeGenerator());
    generators.add(new Greedy());
//    generators.add(new GameOfLife2()); // Improved upon by original authors in 2014.
    generators.add(new HammingWeight());
    generators.add(new Knapsack()); // Exception.
    generators.add(new ShuntingYard()); // Exzellent.
    generators.add(new FindS());
    generators.add(new FindG());
    generators.add(new DreiBlockHeuristik()); // Gut.
    generators.add(new SchulzeMethod()); // Gut.
    generators.add(new DrunkenBishop()); // Exzellent.
    generators.add(new KMedianPPGenerator2()); // Exzellent.
    generators.add(new Base64Encode());
    generators.add(new LeeGenerator()); // Exzellent.
//    generators.add(new GameOfLifeParallel()); // Improved upon by original authors in 2014.
    generators.add(new Base64Decode());
    generators.add(new LongestCommonSubstringGenerator()); // Exzellent.
    generators.add(new Wochentagsberechnung()); // Exzellent.

    // Generators from the AlgoAnim course in summer semester 2014.
    generators.add(new EvoAlgoGenerator());
    generators.add(new KNNAllOrNothing());  // kreativer Einsatz der JOptionPane bei der Validierung.
    generators.add(new KNNMajorityVote());  // v.s.
    generators.add(new EvoWordsGenerator());
    generators.add(new GameOfLife2());
    generators.add(new GameOfLifeParallel());
    generators.add(new DoubleAuction());  // Graphisch hochwertig.
    generators.add(new ContinousDoubleAuction());  // v.s.
    generators.add(new CORDIC());
    generators.add(new EloZahl("EN"));  // Gut.
    generators.add(new EloZahl("DE"));  // v.s.
    generators.add(new DFA());
    generators.add(new KuhnMunkres());  // Gut.
    generators.add(new HopcroftKarp());

    // Generators from the AlgoAnim course in summer term 2016.
    generators.add(new EDF_Scheduling());
    generators.add(new EDF_preemptive_Scheduling());
    generators.add(new EDF_nonpreemptive_Scheduling());
    generators.add(new FindGSetGenerator());
//    generators.add(new FindSSetGenerator());
//    generators.add(new NaiveBayesGen());
    generators.add(new OAuth2());
    generators.add(new RouletteWheelSelection());
    generators.add(new Sorensen(Locale.GERMANY));
    generators.add(new Sorensen(Locale.US));
    generators.add(new SweepAndPruneAABB2D("resources/sap", Locale.GERMANY));
    generators.add(new SweepAndPruneAABB2D("resources/sap", Locale.US));
    generators.add(new TournamentSelection());
    
    // GR
    generators.add(new SpaceComplexity());
    generators.add(new WagnerWithinGenerator(Locale.GERMANY));
    generators.add(new WagnerWithinGenerator(Locale.US));
    
    generators.add(new MultilevelQueue("resources/MultilevelQueue", Locale.US));
    generators.add(new MultilevelQueue("resources/MultilevelQueue", Locale.GERMANY));
    
    return generators;
  }
}
