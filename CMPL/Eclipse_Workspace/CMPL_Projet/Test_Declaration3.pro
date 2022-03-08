programme premiertest:

const min=7; max=+77; marq=-1; oui=vrai; nenni=faux;
var ent i,j;
    bool b1,b2,b3;

debut
	i:= (max-min) div 2;
	b1:= min <> max;
	b2:= min < max;
	lire(i, j, b3);
	ecrire(i, j);
	ecrire(b1, b2, b3);
fin
