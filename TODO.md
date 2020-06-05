## Checklist
1. prevent duplicate assets in crossover
    1. double check
1. fitness functions
    1. treynor metric
    1. continous metrics
1. boundary enforcement / allocation cap
    1. double check
1. fix abstract populator
1. scale negative scores for selection
1. fix stoch selector
1. config file
1. make portfolio a better allocations wrapper
1. documentation catch up
1. refactoring

## Stuff
1. Separate function objects for individual parts of algorithm
    1. crossover
        1. double check dup repair check
    1. elitism
    1. migration
    
1. Selection thresholds 
    1. Fitness functions
        1. Sortino ratio
        1. Omega ratio
        1. MAR ratio
    1. Constraints - hard coded
        1. Max and min % an asset can of portfolio (floor & ceiling)
        1. Max number of assets (cardinality)
        
1. Local search w/ Lamarckism
    1. hill-climbing
    1. simulated annealing
    1. beam search
    
1. Misc
    1. train & test split
    1. asset performance = weighted average of all returns

### Write Up Topics
- project motivation
- project aims
- Modern portfolio optimization
- Mean-variance model
- Multi objective optimization
    - discuss pareto optimum
- Evolutionary algorithms
- Types of constraints
    - Hard vs soft constraints
- general approach taken to problem
