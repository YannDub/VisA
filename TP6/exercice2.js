importClass(Packages.ij.IJ);
importClass(Packages.ij.gui.Plot);
importClass(Packages.java.awt.Color);

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

function fuzzy_min(f1, f2, x) {
	return Math.min(f1(x), f2(x));
}

function fuzzy_max(f1, f2, x) {
	return Math.max(f1(x), f2(x));
}

x = [0, 5, 10, 15, 20, 25, 30, 35, 40];
var y = []

for(i = 0; i <= 40; i += 5) {
	y.push(fuzzy_max(moyenne, basse, i));
}


plot = new Plot("Simple Plot", "Temperature", "Y");
plot.setFrameSize(500,150);
plot.setLineWidth(2);

plot.setColor(Color.black);
plot.addPoints("", x, y, Plot.LINE);
plot.show();