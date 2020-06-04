## Checklist
1. prevent duplicate assets in crossover
    1. double check
1. fitness functions
    1. treynor metric
    1. continous metrics
1. boundary enforcement / allocation cap
    1. double check
1. wrap params into helper objects
    1. boundary
        1. boundary * popsize >= 1
    1. iterations
1. get data sources
    1. crypto currencies
    1. s&p 500
1. argparser 
    1. null option for optional parameters
    1. add helper
1. how to do checks?
1. make portfolio wrapper around allocations
1. documentation catch up
1. refactoring
1. config file

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
- Multi objective optimization
- Evolutionary algorithms
- Modern portfolio optimization
- Mean-variance model
- Types of constraints
- Hard vs soft constraints
- discuss pareto optimum