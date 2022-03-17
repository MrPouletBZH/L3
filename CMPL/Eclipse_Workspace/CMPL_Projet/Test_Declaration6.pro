programme premiertest:

const diff=-1; egs=1;
var ent i,j;

debut
	lire(i,j);
	cond
		i = diff: ecrire(diff),
		i = egs: ecrire(egs),
		i = j: ecrire(j)
		aut lire(i,j);
			cond
				i = diff: ecrire(diff),
				i = egs: ecrire(egs),
				i = j: ecrire(j)
				aut ecrire(diff-egs)
			fcond;
	fcond;
fin