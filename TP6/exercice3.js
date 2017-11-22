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

function chauffer_fort(kw) {
	if(kw <= 8) return 0;
	else if(kw <= 10) return (1.0/2.0) * kw - 4.0;
	else return 1;
}

function implication(a, f, y) {
	return Math.min(a, f(y));
}

var t = basse(12);
var x = [];
var res = [];

for(i = 0; i < 150; i++) {
	x.push(i / 150.0 * 15.0);
	res.push(implication(t, chauffer_fort, i / 150.0 * 15.0));
}

plot = new Plot("Simple Plot", "Puissance chauffe (kw)", "Y");
plot.setFrameSize(500,150);
plot.setLineWidth(2);

plot.setColor(Color.red);
plot.addPoints("", x, res, Plot.LINE);
plot.show();

var sum1 = 0;
var sum2 = 0;

var b = 15 - 8;
var a = 15 - (basse(12) + 4.0) / (1.0/2.0);
var c = b - a;
var cog = (2 * a * c + a * a + c * b + a * b + b * b) / (3 * (a + b)) + 8;

IJ.log(cog);