# portolve
Kotlin & Gradle framework for exploring the task of multi-objective portfolio optimization with evolutionary algorithms.

## Motivation

Evolutionary algorithms have long been a topic of interest for me, and Kotlin has been a language have looking to learn
more about through actual project of decent size. This project is the intersection of those to desires, a way for me to
learn about evolutionary algorithmic techniques through an applied project - written in Kotlin - studying the real-world 
task of portfolio optimization. 

## Project

This framework aims to provide solutions to the task of **portfolio optimization**, given some collection of assets and 
an objective function - finding some collection of assets that maximize the objection function. Usually those functions,
such as the **Sharpe ratio** are trying to optimize multiple objectives such as maximizing expecting return and 
minimizing risk.

For a little more background, evolutionary algorithms are type of optimization technique inspired by biological 
evolution. Basically, the algorithm attempts to find good solutions to some optimization function by maintaining 
population of possible solutions and iteratively producing new "generations" through the process of pruning bad 
 solutions that don't fit the objective function, mutating the population, and then repopulating the population through
"breeding" between solutions. The exact objective function, solution representation, and other details are usually 
implementation details usually determined by the context of the problem at hand. 
% more implementation details? learn more at..?
Either way, the hope is that over time, better generations are bred leading to better solutions.

This framework provides a wide range of "drag-and-drop" evolutionary algorithmic techniques, objection 
functions, and a various number of other parameters - all that is needed is some collection of assets and some history
of their returns. The algorithm will then spit back a solution in the form of some number of assets and their 
weight allocations (percentage of some sum of money to put towards each asset) that attempts to maximize the selected 
objective function.

### Features

The framework offers the following evolutionary strategies, objective functions, and other tuneable parameters:

- **Asset Mutator**

- **Weight Mutator**

- **Fitness Strategy**

- **Populator Strategy**

- **Objective Functions**

- **Misc**

## Setup

Portolve was built using Kotlin _ and Gradle ...

### Usage
