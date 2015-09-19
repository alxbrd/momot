package at.ac.tuwien.big.momot.examples.modularization.jsep;

import at.ac.tuwien.big.moea.SearchExperiment;
import at.ac.tuwien.big.moea.experiment.executor.listener.SeedRuntimePrintListener;
import at.ac.tuwien.big.moea.search.algorithm.EvolutionaryAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.LocalSearchAlgorithmFactory;
import at.ac.tuwien.big.moea.search.algorithm.provider.IRegisteredAlgorithm;
import at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension;
import at.ac.tuwien.big.momot.ModuleManager;
import at.ac.tuwien.big.momot.TransformationResultManager;
import at.ac.tuwien.big.momot.TransformationSearchOrchestration;
import at.ac.tuwien.big.momot.examples.modularization.jsep.metric.ModelMetrics;
import at.ac.tuwien.big.momot.examples.modularization.jsep.modularization.ModularizationModel;
import at.ac.tuwien.big.momot.examples.modularization.jsep.modularization.ModularizationPackage;
import at.ac.tuwien.big.momot.examples.modularization.jsep.modularization.Module;
import at.ac.tuwien.big.momot.problem.solution.TransformationSolution;
import at.ac.tuwien.big.momot.problem.unit.parameter.IParameterValue;
import at.ac.tuwien.big.momot.problem.unit.parameter.comparator.IEObjectEqualityHelper;
import at.ac.tuwien.big.momot.problem.unit.parameter.increment.IncrementalStringValue;
import at.ac.tuwien.big.momot.search.algorithm.operator.mutation.TransformationVariableMutation;
import at.ac.tuwien.big.momot.search.fitness.EGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.IEGraphMultiDimensionalFitnessFunction;
import at.ac.tuwien.big.momot.search.fitness.dimension.AbstractEGraphFitnessDimension;
import at.ac.tuwien.big.momot.search.solution.executor.SearchHelper;
import at.ac.tuwien.big.momot.search.solution.repair.ITransformationRepairer;
import at.ac.tuwien.big.momot.search.solution.repair.TransformationPlaceholderRepairer;
import at.ac.tuwien.big.momot.util.MomotUtil;
import com.google.common.base.Objects;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.operator.OnePointCrossover;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.util.progress.ProgressListener;

@SuppressWarnings("all")
public class ModularizationJSEP_RuntimeSearch {
  protected final static String INITIAL_MODEL = "data/input/models/mtunis.xmi";
  
  protected final static int SOLUTION_LENGTH = 30;
  
  protected static String attribute = "calculation";
  
  public class ModularizationJSEP_RuntimeFitnessFunction extends EGraphMultiDimensionalFitnessFunction {
    @Override
    protected void preprocessEvaluation(final TransformationSolution solution) {
      EGraph _execute = solution.execute();
      final ModularizationModel root = MomotUtil.<ModularizationModel>getRoot(_execute, ModularizationModel.class);
      solution.setAttribute("root", root);
      ModularizationCalculator _modularizationCalculator = new ModularizationCalculator(root);
      solution.setAttribute(ModularizationJSEP_RuntimeSearch.attribute, _modularizationCalculator);
    }
  }
  
  protected final String[] modules = new String[] { "data/modularization_jsep.henshin" };
  
  protected final String _parameterValueKey_0 = ModularizationRules.CreateModule.Parameter.MODULE_NAME;
  
  protected final ITransformationRepairer solutionRepairer = new TransformationPlaceholderRepairer();
  
  protected int populationSize = 16384;
  
  protected final int maxEvaluations = 32768;
  
  protected final int nrRuns = 30;
  
  protected IParameterValue _createParameterValue_0() {
    IncrementalStringValue _incrementalStringValue = new IncrementalStringValue("Module", "A");
    return _incrementalStringValue;
  }
  
  protected double _createObjectiveHelper_0(final TransformationSolution solution, final EGraph graph, final EObject root) {
    double _xblockexpression = (double) 0;
    {
      final ModularizationCalculator calculator = solution.<ModularizationCalculator>getAttribute(ModularizationJSEP_RuntimeSearch.attribute, ModularizationCalculator.class);
      ModelMetrics _metrics = calculator.getMetrics();
      _xblockexpression = _metrics.coupling;
    }
    return _xblockexpression;
  }
  
  protected IFitnessDimension<TransformationSolution> _createObjective_0(final TransformationSearchOrchestration orchestration) {
    return new AbstractEGraphFitnessDimension("Coupling", at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Minimum) {
       @Override
       protected double internalEvaluate(TransformationSolution solution) {
    		EGraph graph = solution.execute();
          EObject root = MomotUtil.getRoot(graph);
          return _createObjectiveHelper_0(solution, graph, root);
       }
    };
  }
  
  protected double _createObjectiveHelper_1(final TransformationSolution solution, final EGraph graph, final EObject root) {
    double _xblockexpression = (double) 0;
    {
      final ModularizationCalculator calculator = solution.<ModularizationCalculator>getAttribute(ModularizationJSEP_RuntimeSearch.attribute, ModularizationCalculator.class);
      ModelMetrics _metrics = calculator.getMetrics();
      _xblockexpression = _metrics.cohesion;
    }
    return _xblockexpression;
  }
  
  protected IFitnessDimension<TransformationSolution> _createObjective_1(final TransformationSearchOrchestration orchestration) {
    return new AbstractEGraphFitnessDimension("Cohesion", at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Maximum) {
       @Override
       protected double internalEvaluate(TransformationSolution solution) {
    		EGraph graph = solution.execute();
          EObject root = MomotUtil.getRoot(graph);
          return _createObjectiveHelper_1(solution, graph, root);
       }
    };
  }
  
  protected double _createObjectiveHelper_2(final TransformationSolution solution, final EGraph graph, final EObject root) {
    int _xblockexpression = (int) 0;
    {
      final ModularizationCalculator calculator = solution.<ModularizationCalculator>getAttribute(ModularizationJSEP_RuntimeSearch.attribute, ModularizationCalculator.class);
      ModelMetrics _metrics = calculator.getMetrics();
      _xblockexpression = _metrics.nrModules;
    }
    return _xblockexpression;
  }
  
  protected IFitnessDimension<TransformationSolution> _createObjective_2(final TransformationSearchOrchestration orchestration) {
    return new AbstractEGraphFitnessDimension("NrModules", at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Maximum) {
       @Override
       protected double internalEvaluate(TransformationSolution solution) {
    		EGraph graph = solution.execute();
          EObject root = MomotUtil.getRoot(graph);
          return _createObjectiveHelper_2(solution, graph, root);
       }
    };
  }
  
  protected double _createObjectiveHelper_3(final TransformationSolution solution, final EGraph graph, final EObject root) {
    double _xblockexpression = (double) 0;
    {
      final ModularizationCalculator calculator = solution.<ModularizationCalculator>getAttribute(ModularizationJSEP_RuntimeSearch.attribute, ModularizationCalculator.class);
      ModelMetrics _metrics = calculator.getMetrics();
      _xblockexpression = _metrics.modularizationQuality;
    }
    return _xblockexpression;
  }
  
  protected IFitnessDimension<TransformationSolution> _createObjective_3(final TransformationSearchOrchestration orchestration) {
    return new AbstractEGraphFitnessDimension("MQ", at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Maximum) {
       @Override
       protected double internalEvaluate(TransformationSolution solution) {
    		EGraph graph = solution.execute();
          EObject root = MomotUtil.getRoot(graph);
          return _createObjectiveHelper_3(solution, graph, root);
       }
    };
  }
  
  protected double _createObjectiveHelper_4(final TransformationSolution solution, final EGraph graph, final EObject root) {
    int _xblockexpression = (int) 0;
    {
      final ModularizationCalculator calculator = solution.<ModularizationCalculator>getAttribute(ModularizationJSEP_RuntimeSearch.attribute, ModularizationCalculator.class);
      ModelMetrics _metrics = calculator.getMetrics();
      _xblockexpression = _metrics.minMaxDiff;
    }
    return _xblockexpression;
  }
  
  protected IFitnessDimension<TransformationSolution> _createObjective_4(final TransformationSearchOrchestration orchestration) {
    return new AbstractEGraphFitnessDimension("MinMaxDiff", at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Minimum) {
       @Override
       protected double internalEvaluate(TransformationSolution solution) {
    		EGraph graph = solution.execute();
          EObject root = MomotUtil.getRoot(graph);
          return _createObjectiveHelper_4(solution, graph, root);
       }
    };
  }
  
  protected double _createConstraintHelper_0(final TransformationSolution solution, final EGraph graph, final EObject root) {
    EList<at.ac.tuwien.big.momot.examples.modularization.jsep.modularization.Class> _classes = ((ModularizationModel) root).getClasses();
    final Function1<at.ac.tuwien.big.momot.examples.modularization.jsep.modularization.Class, Boolean> _function = (at.ac.tuwien.big.momot.examples.modularization.jsep.modularization.Class c) -> {
      Module _module = c.getModule();
      return Boolean.valueOf(Objects.equal(_module, null));
    };
    Iterable<at.ac.tuwien.big.momot.examples.modularization.jsep.modularization.Class> _filter = IterableExtensions.<at.ac.tuwien.big.momot.examples.modularization.jsep.modularization.Class>filter(_classes, _function);
    return IterableExtensions.size(_filter);
  }
  
  protected IFitnessDimension<TransformationSolution> _createConstraint_0(final TransformationSearchOrchestration orchestration) {
    return new AbstractEGraphFitnessDimension("UnassignedClasses", at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Minimum) {
       @Override
       protected double internalEvaluate(TransformationSolution solution) {
    		EGraph graph = solution.execute();
          EObject root = MomotUtil.getRoot(graph);
          return _createConstraintHelper_0(solution, graph, root);
       }
    };
  }
  
  protected double _createConstraintHelper_1(final TransformationSolution solution, final EGraph graph, final EObject root) {
    EList<Module> _modules = ((ModularizationModel) root).getModules();
    final Function1<Module, Boolean> _function = (Module m) -> {
      EList<at.ac.tuwien.big.momot.examples.modularization.jsep.modularization.Class> _classes = m.getClasses();
      return Boolean.valueOf(_classes.isEmpty());
    };
    Iterable<Module> _filter = IterableExtensions.<Module>filter(_modules, _function);
    return IterableExtensions.size(_filter);
  }
  
  protected IFitnessDimension<TransformationSolution> _createConstraint_1(final TransformationSearchOrchestration orchestration) {
    return new AbstractEGraphFitnessDimension("EmptyModules", at.ac.tuwien.big.moea.search.fitness.dimension.IFitnessDimension.FunctionType.Minimum) {
       @Override
       protected double internalEvaluate(TransformationSolution solution) {
    		EGraph graph = solution.execute();
          EObject root = MomotUtil.getRoot(graph);
          return _createConstraintHelper_1(solution, graph, root);
       }
    };
  }
  
  protected ModuleManager createModuleManager() {
    ModuleManager manager = new ModuleManager();
    manager.addModules(modules);
    manager.setParameterValue(_parameterValueKey_0, _createParameterValue_0());
    return manager;
  }
  
  protected IEGraphMultiDimensionalFitnessFunction createFitnessFunction(final TransformationSearchOrchestration orchestration) {
    IEGraphMultiDimensionalFitnessFunction function = new ModularizationJSEP_RuntimeSearch.ModularizationJSEP_RuntimeFitnessFunction();
    function.addObjective(_createObjective_0(orchestration));
    function.addObjective(_createObjective_1(orchestration));
    function.addObjective(_createObjective_2(orchestration));
    function.addObjective(_createObjective_3(orchestration));
    function.addObjective(_createObjective_4(orchestration));
    function.addConstraint(_createConstraint_0(orchestration));
    function.addConstraint(_createConstraint_1(orchestration));
    function.setSolutionRepairer(solutionRepairer);
    return function;
  }
  
  protected IRegisteredAlgorithm<NSGAII> _createRegisteredAlgorithm_0(final TransformationSearchOrchestration orchestration, final EvolutionaryAlgorithmFactory<TransformationSolution> moea, final LocalSearchAlgorithmFactory<TransformationSolution> local) {
    TournamentSelection _tournamentSelection = new TournamentSelection(2);
    OnePointCrossover _onePointCrossover = new OnePointCrossover(1.0);
    SearchHelper _searchHelper = orchestration.getSearchHelper();
    TransformationVariableMutation _transformationVariableMutation = new TransformationVariableMutation(_searchHelper, 0.10);
    IRegisteredAlgorithm<NSGAII> _createNSGAIII = moea.createNSGAIII(
      0, 6, _tournamentSelection, _onePointCrossover, _transformationVariableMutation);
    return _createNSGAIII;
  }
  
  protected ProgressListener _createListener_0() {
    SeedRuntimePrintListener _seedRuntimePrintListener = new SeedRuntimePrintListener();
    return _seedRuntimePrintListener;
  }
  
  protected boolean _createEqualityHelperHelper_(final EObject left, final EObject right) {
    boolean _xblockexpression = false;
    {
      boolean _and = false;
      if (!(left instanceof Module)) {
        _and = false;
      } else {
        _and = (right instanceof Module);
      }
      if (_and) {
        EObject _eContainer = left.eContainer();
        EList<Module> _modules = ((ModularizationModel) _eContainer).getModules();
        final int leftIndex = _modules.indexOf(left);
        EObject _eContainer_1 = right.eContainer();
        EList<Module> _modules_1 = ((ModularizationModel) _eContainer_1).getModules();
        final int rightIndex = _modules_1.indexOf(right);
        return Integer.valueOf(leftIndex).equals(Integer.valueOf(rightIndex));
      }
      _xblockexpression = left.equals(right);
    }
    return _xblockexpression;
  }
  
  protected IEObjectEqualityHelper _createEqualityHelper_() {
    return new IEObjectEqualityHelper() {
       @Override
       public boolean equals(EObject left, EObject right) {
          return _createEqualityHelperHelper_(left, right);
       }
    };
  }
  
  protected TransformationSearchOrchestration createOrchestration(final String initialGraph, final int solutionLength) {
    TransformationSearchOrchestration orchestration = new TransformationSearchOrchestration();
    orchestration.setModuleManager(createModuleManager());
    orchestration.setProblemGraph(initialGraph);
    orchestration.setSolutionLength(solutionLength);
    orchestration.setFitnessFunction(createFitnessFunction(orchestration));
    orchestration.setEqualityHelper(_createEqualityHelper_());
    
    EvolutionaryAlgorithmFactory moea = orchestration.createEvolutionaryAlgorithmFactory(populationSize);
    LocalSearchAlgorithmFactory local = orchestration.createLocalSearchAlgorithmFactory();
    orchestration.addAlgorithm("NSGAIII", _createRegisteredAlgorithm_0(orchestration, moea, local));
    
    return orchestration;
  }
  
  protected SearchExperiment createExperiment(final TransformationSearchOrchestration orchestration) {
    SearchExperiment experiment = new SearchExperiment(orchestration, maxEvaluations);
    experiment.setNumberOfRuns(nrRuns);
    experiment.addProgressListener(_createListener_0());
    return experiment;
  }
  
  protected TransformationResultManager handleResults(final SearchExperiment experiment) {
    TransformationResultManager resultManager = new TransformationResultManager(experiment);
    resultManager.saveApproximationSetObjectives("data/output/runtime/approximationSet/momot_" + populationSize + "ps.pf");
    resultManager.setBaseDirectory("data/output/models/runtime/momot_" + populationSize + "ps/");
    resultManager.saveApproximationSetGraphs();
    System.out.println(resultManager.printApproximationSet());
    System.out.println(resultManager.printApproximationSetObjectives());
    return resultManager;
  }
  
  public void performSearch(final String initialGraph, final int solutionLength) {
    TransformationSearchOrchestration orchestration = createOrchestration(initialGraph, solutionLength);
    SearchExperiment experiment = createExperiment(orchestration);
    experiment.run();
//    handleResults(experiment);
  }
  
  public static void init() {
    ModularizationPackage.eINSTANCE.getClass();
  }
  
  public static void main(final String... args) {
    init();
    ModularizationJSEP_RuntimeSearch search = new ModularizationJSEP_RuntimeSearch();
    search.performSearch(INITIAL_MODEL, SOLUTION_LENGTH);
    System.out.println("done.");
  }
}
