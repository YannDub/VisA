open("cas_3_dalton15.bmp")
src = getImageID();

nbCluster = 4;
run("k-means Clustering", "number=" + nbCluster + " cluster=0.00010000 enable randomization=4 show");
selectWindow("Cluster centroid values");

nbClassFound = 0;
class = newArray(3 * nbCluster);

for(i = 0; i < getWidth(); i++) {
	for(j = 0; j < getHeight(); j++) {
		h = getPixel(i,j);
		alreadyFound = false;
		for(k = 0; k < nbClassFound; k += 3) {
			red = (h >> 16) & 0xff;
			green = (h >> 8) & 0xff;
			blue = (h) & 0xff;
			alreadyFound = alreadyFound || (red == class[k] && green == class[k + 1] && blue == class[k + 2]);
		}

		if(!alreadyFound) {
			class[nbClassFound] = (h >> 16) & 0xff;
			class[nbClassFound + 1] = (h >> 8) & 0xff;
			class[nbClassFound + 2] = (h) & 0xff;
			print((nbClassFound / 3) + ", " + class[nbClassFound] + " " + class[nbClassFound+1] + " " + class[nbClassFound+2]);
			nbClassFound += 3;
		}
	}
}

function euclidianDistance(red1, green1, blue1, red2, green2, blue2) {
	return (red1 - red2) * (red1 - red2) + (green1 - green2) * (green1 - green2) + (blue1 - blue2) * (blue1 - blue2);
}

run("Open...");
image = getImageID();

for(i = 0; i < getWidth(); i++) {
	for(j = 0; j < getHeight(); j++) {
		h = getPixel(i,j);
		red = (h >> 16) & 0xff;
		green = (h >> 8) & 0xff;
		blue = (h) & 0xff;
		min = euclidianDistance(class[0], class[1], class[2], red, green, blue);
		for(k = 0; k < nbCluster; k++) {
			d = euclidianDistance(class[3*k], class[3*k + 1], class[3*k + 2], red, green, blue);
			if(min >= d) {
				color = (class[3*k] &0xff) << 16 + (class[3*k + 1] & 0xff) << 8 + class[3*k + 2] & 0xff;
				setPixel(i,j,color);
				min = d;
			}
		}
	}
}
