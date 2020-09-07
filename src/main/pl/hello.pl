parent(alex, lyan).
parent(gm, alex).
grandparent(X, Y) :- parent(X, Z), parent(Z, Y).
