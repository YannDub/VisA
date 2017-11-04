// Une macro-squelette pour la couleur.
// Version: 0.1
// Date: 14/09/2011
// Author: L. Macaire
 
macro "augmentation_luminance" {

// recuperation du ID de l'image
image = getImageID();
open();
imageOrigin = getImageID();
selectImage(image);

valeur = getNumber ("quelle augmentation (absolue) de luminance",valeur);

Dialog.create("Debut");
Dialog.addMessage(" Cliquer sur OK pour commencer le traitement ");
Dialog.show();


setBatchMode(true);



// recuperation de la taille W x H de l'image
W = getWidth();
H = getHeight();

run("Duplicate...", "title=luminance modifiee");
image_luminance_aug = getImageID();

for (j=0; j<H; j++) {
   for (i=0; i<W; i++) 
	{
	selectImage (image);
	couleur_avant = getPixel(i,j);
	R_avant = (couleur_avant & 0xff0000) >> 16;
	G_avant = (couleur_avant & 0x00ff00) >> 8;
	B_avant = (couleur_avant & 0x0000ff) ;
	
	R_apres = R_avant + valeur ;
	if(R_apres > 255) R_apres = 255;
	G_apres = G_avant + valeur ;
	if(G_apres > 255) G_apres = 255;
	B_apres = B_avant + valeur ;
	if(B_apres > 255) B_apres = 255;

	couleur_apres = ((R_apres & 0xff ) << 16) + ((G_apres & 0xff) << 8) + B_apres & 0xff;


	selectImage (image_luminance_aug);
	setPixel(i,j,couleur_apres);
      	}
   }

setBatchMode(false);
/*
selectImage(image_luminance_aug);
run("8-bit");
getRawStatistics(n, mean, min, max, std, histo);

selectImage(imageOrigin);
run("8-bit");
getRawStatistics(nl, meanl, minl, maxl, stdl, histol);

print(mean - meanl);
*/
Dialog.create("Fin");
Dialog.addMessage(" Cliquer sur OK pour terminer le traitement");
Dialog.show();


}