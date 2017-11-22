function basse(temp) {
	if(temp <= 10.0) return 1.0;
	else if(temp <= 20.0) return (-1.0 / 10.0) * temp + 2.0;
	else return 0.0;
}

function moyenne(temp) {
	if(temp <= 10.0 || temp >= 30) return 0.0;
	else if(temp <= 20.0) return (1.0 / 10.0) * temp - 1.0;
	else return (-1.0 / 10.0) * temp + 3.0;
}

function elevee(temp) {
	if(temp <= 20.0) return 0.0;
	else if(temp <= 30.0) return (1.0 / 10.0) * temp -2.0;
	else return 1.0; 
}

function fuzzy_min(f1, f2) {
	return f1(0);
}

fuzzy_min(basse(), moyenne());
