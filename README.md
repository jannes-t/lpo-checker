# lpo-sat-solver
Implemenation of a lpo-checker which determines if a given TRS is lpo-terminating

This is a command-line tool which will check lpo-termination for a given Term Rewriting System.
If the given TRS is lpo-terminating a possible precedence that can be lifted to lpo is given.
The mechanization of this process is achieved by translating the decision problem to SAT and using an existing open-source SAT-solver to determine satisfiability.
