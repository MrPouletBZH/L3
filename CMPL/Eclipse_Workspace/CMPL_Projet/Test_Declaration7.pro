programme premiertest:
	const i=7;
	var ent d1;
	proc carre fixe (ent v) mod (ent v2)
		var ent d1, d2;
	debut
		v2:=v*v;
		ecrire(v2);
	fin; 

	proc dist fixe (ent x1, y1, x2, y2) mod (ent d)
		var ent d1, d2;
	debut
		ecrire(x1);
	fin; 

debut
	d1 := 2;
	carre(d1)(i);
fin