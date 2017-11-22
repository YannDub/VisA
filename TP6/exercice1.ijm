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

print("16 degrees");
print(basse(16));
print(moyenne(16));
print(elevee(16));

b = newArray(9);
m = newArray(9);
e = newArray(9);

x = newArray(0,5,10,15,20,25,30,35,40);

for(i = 0; i <= 40; i += 5) {
	b[i / 5] = basse(i);
	m[i / 5] = moyenne(i);
	e[i / 5] = elevee(i);
}

Plot.create("Simple Plot", "Temperature", "Y");
setJustification("center");

Plot.setColor("blue");
Plot.add("line", b);
Plot.setColor("orange");
Plot.add("line", m);
Plot.setColor("red");
Plot.add("line", e);
Plot.show();