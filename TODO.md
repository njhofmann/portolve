## Priorities
1. prevent duplicate assets in crossover
    1. double check
1. fitness functions
    1. treynor metric
    1. continous metrics
1. boundary enforcement / allocation cap
1. get data sources
1. comprehensive argparser
1. documentation catch up
1. refactoring
1. config file

## Stuff
1. Separate function objects for individual parts of algorithm
    1. mutation
        1. alters one or more gene values 
    2. crossover
        1. double check dup repair check
    3. selection method
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
    - how to measure portfolio success with test set?
    - asset performance = weighted sum of all returns
    - covariance metric?

### Write Up
- Modern portfolio optimization
- Mean-variance model
- Types of constraints
- Hard vs soft constraints
- discuss pareto optimum