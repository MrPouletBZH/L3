programme premiertest:

const diff=-1; egs=1;
var ent i,j;

debut
	lire(i,j);
	si i <> j alors 
		ecrire(diff);
	sinon
		lire(i,j);
		si i <> j alors 
			ecrire(diff);
		sinon
			ecrire(egs)
		fsi
	fsi
fin