# lpo-sat-solver
This is the code for my bachelor thesis project about mechanically showing LPO (lexicographic path order) termination of a TRS (term rewriting system), which in turn proves termination. The thesis report pdf is in the top level of the repo.

This is a command-line tool which will check lpo-termination for a given Term Rewriting System.
If the given TRS is lpo-terminating a possible precedence that can be lifted to lpo is given.
The mechanization of this process is achieved by translating the decision problem to SAT and using an existing open-source SAT-solver to determine satisfiability.
