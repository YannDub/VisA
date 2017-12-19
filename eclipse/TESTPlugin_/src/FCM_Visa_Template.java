import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.frame.*;
import ij.process.ImageProcessor.*;
import ij.plugin.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.lang.Math.*;
import java.lang.Object.*;
import java.lang.String.*;
import java.awt.TextArea;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Window.*;

public class FCM_Visa_Template implements PlugIn {

	class Vec {
		int[] data = new int[3]; // *pointeur sur les composantes*/
	}

	////////////////////////////////////////////////////
	Random r = new Random();

	public int rand(int min, int max) {
		return min + (int) (r.nextDouble() * (max - min));
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	public void run(String arg) {
		// LES PARAMETRES

		ImageProcessor ip;
		ImageProcessor ipseg;
		ImageProcessor ipJ;
		ImagePlus imp;
		ImagePlus impseg;
		ImagePlus impJ;
		IJ.showMessage("Algorithme FCM", "If ready, Press OK");
		ImagePlus cw;

		imp = WindowManager.getCurrentImage();
		ip = imp.getProcessor();

		int width = ip.getWidth();
		int height = ip.getHeight();

		impseg = NewImage.createImage("Image segment�e par FCM", width, height, 1, 24, 0);
		ipseg = impseg.getProcessor();
		impseg.show();

		int nbclasses, nbpixels;
		double valeur_seuil;
		String[] choices = new String[] { "FCM", "HCM", "PCM", "Dave" };

		String algo = (String) JOptionPane.showInputDialog(null, "What is your algo?", "Algo",
				JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

		String demande = JOptionPane.showInputDialog("Nombre de classes : ");
		nbclasses = Integer.parseInt(demande);
		nbpixels = width * height; // taille de l'image en pixels

		demande = JOptionPane.showInputDialog("Valeur de m : ");
		double m = Double.parseDouble(demande);

		demande = JOptionPane.showInputDialog("Nombre it�ration max : ");
		int itermax = Integer.parseInt(demande);

		demande = JOptionPane.showInputDialog("Valeur du seuil de stabilit� : ");
		valeur_seuil = Double.parseDouble(demande);

		demande = JOptionPane.showInputDialog("Randomisation am�lior�e ? ");
		int valeur = Integer.parseInt(demande);

		double c[][] = new double[nbclasses][3];
		// double m;
		double red[] = new double[nbpixels];
		double green[] = new double[nbpixels];
		double blue[] = new double[nbpixels];
		int[] colorarray = new int[3];
		int[] init = new int[3];
		double figJ[] = new double[itermax];
		for (int i = 0; i < itermax; i++) {
			figJ[i] = 0;
		}

		// R�cup�ration des donn�es images
		int cpt = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				ip.getPixel(i, j, colorarray);
				red[cpt] = (double) colorarray[0];
				green[cpt] = (double) colorarray[1];
				blue[cpt] = (double) colorarray[2];
				cpt++;
			}
		}
		////////////////////////////////
		// FCM
		///////////////////////////////
		int xmin = 0;
		int xmax = width;
		int ymin = 0;
		int ymax = height;
		int rx, ry;
		int epsilonx, epsilony;

		// Initialisation des centro�des (al�atoirement )

		for (int i = 0; i < nbclasses; i++) {
			if (valeur == 1) {
				epsilonx = rand((int) (width / (i + 2)), (int) (width / 2));
				epsilony = rand((int) (height / (4)), (int) (height / 2));
			} else {
				epsilonx = 0;
				epsilony = 0;
			}
			rx = rand(xmin + epsilonx, xmax - epsilonx);
			ry = rand(ymin + epsilony, ymax - epsilony);
			ip.getPixel(rx, ry, init);
			c[i][0] = init[0];
			c[i][1] = init[1];
			c[i][2] = init[2];
		}

		switch (algo) {
		case "FCM":
			this.FCM(c, red, green, blue, figJ, nbclasses, nbpixels, m, valeur_seuil, itermax, width, height, ipseg,
					impseg);
			break;
		case "HCM":
			this.HCM(c, red, green, blue, figJ, nbclasses, nbpixels, valeur_seuil, itermax, width, height, ipseg,
					impseg);
			break;
		case "PCM":
			this.PCM(c, red, green, blue, figJ, nbclasses, nbpixels, m, valeur_seuil, itermax, width, height, ipseg, impseg);
			break;
		default:
			this.FCM(c, red, green, blue, figJ, nbclasses, nbpixels, m, valeur_seuil, itermax, width, height, ipseg,
					impseg);
			break;
		}
	} // Fin FCM

	private void distance2(double[] red, double[] green, double[] blue, double[][] c, double[][] d, int nbpixels,
			int kmax) {
		for (int l = 0; l < nbpixels; l++) {
			for (int k = 0; k < kmax; k++) {
				double r2 = Math.pow(red[l] - c[k][0], 2);
				double g2 = Math.pow(green[l] - c[k][1], 2);
				double b2 = Math.pow(blue[l] - c[k][2], 2);
				d[k][l] = r2 + g2 + b2;
			}
		}
	}

	private void uFCM(double[][] u, double[][] d, int nbpixels, int kmax, double m) {
		for (int l = 0; l < nbpixels; l++) {
			for (int k = 0; k < kmax; k++) {
				double sum = 0;
				for (int i = 0; i < kmax; i++) {
					double e = 1;
					if (m >= 2)
						e = (2 / (m - 1));
					sum += Math.pow((d[k][l] / d[i][l]), e);
				}
				if (Double.isNaN(sum))
					u[k][l] = 0;
				else
					u[k][l] = 1.0 / sum;
			}
		}
	}

	private void FCM(double[][] c, double[] red, double[] green, double[] blue, double[] figJ, int nbclasses,
			int nbpixels, double m, double valeur_seuil, int itermax, int width, int height, ImageProcessor ipseg,
			ImagePlus impseg) {
		double Dmat[][] = new double[nbclasses][nbpixels];
		double Dprev[][] = new double[nbclasses][nbpixels];
		double Umat[][] = new double[nbclasses][nbpixels];
		double Uprev[][] = new double[nbclasses][nbpixels];
		int iter;
		double seuil, stab;
		int kmax = nbclasses;
		int cpt = 0;

		// Calcul de distance entre data et centroides
		this.distance2(red, green, blue, c, Dprev, nbpixels, kmax);

		// Initialisation des degr�s d'appartenance
		// A COMPLETER
		this.uFCM(Uprev, Dprev, nbpixels, kmax, m);

		////////////////////////////////////////////////////////////
		// FIN INITIALISATION FCM
		///////////////////////////////////////////////////////////

		/////////////////////////////////////////////////////////////
		// BOUCLE PRINCIPALE
		////////////////////////////////////////////////////////////
		iter = 0;
		stab = 2;
		seuil = valeur_seuil;

		/////////////////// A COMPLETER ///////////////////////////////
		while ((iter < itermax) && (stab > seuil)) {
			// Update the matrix of centroids
			for (int i = 0; i < nbclasses; i++) {
				double sum[] = { 0, 0, 0 };
				double sum2 = 0;
				for (int j = 0; j < nbpixels; j++) {
					sum[0] += Math.pow(Uprev[i][j], m) * red[j];
					sum[1] += Math.pow(Uprev[i][j], m) * green[j];
					sum[2] += Math.pow(Uprev[i][j], m) * blue[j];
				}
				for (int j = 0; j < nbpixels; j++) {
					sum2 += Math.pow(Uprev[i][j], m);
				}
				c[i][0] = sum[0] / sum2;
				c[i][1] = sum[1] / sum2;
				c[i][2] = sum[2] / sum2;

			}
			// Compute Dmat, the matrix of distances (euclidian) with the
			// centro�ds
			this.distance2(red, green, blue, c, Dmat, nbpixels, kmax);

			this.uFCM(Umat, Dmat, nbpixels, kmax, m);

			// Calculate difference between the previous partition and the new
			// partition (performance index)
			for (int l = 0; l < nbpixels; l++) {
				for (int k = 0; k < nbclasses; k++) {
					figJ[iter] += Math.pow(Uprev[k][l], m) * Dprev[k][l];
				}
			}
			stab = iter == 0 ? figJ[iter] : Math.abs(figJ[iter] - figJ[iter - 1]);
			iter++;

			Uprev = Umat.clone();
			Dprev = Dmat.clone();
			////////////////////////////////////////////////////////

			// Affichage de l'image segment�e
			double[] mat_array = new double[nbclasses];
			cpt = 0;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					for (int k = 0; k < nbclasses; k++) {
						mat_array[k] = Umat[k][cpt];
					}
					int indice = IndiceMaxOfArray(mat_array, nbclasses);
					int array[] = new int[3];
					array[0] = (int) c[indice][0];
					array[1] = (int) c[indice][1];
					array[2] = (int) c[indice][2];
					ipseg.putPixel(i, j, array);
					cpt++;
				}
			}
			impseg.updateAndDraw();
			//////////////////////////////////
		} // Fin boucle

		double[] xplot = new double[itermax];
		double[] yplot = new double[itermax];
		for (int w = 0; w < itermax; w++) {
			xplot[w] = (double) w;
			yplot[w] = (double) figJ[w];
		}
		Plot plot = new Plot("Performance Index (FCM)", "iterations", "J(P) value", xplot, yplot);
		plot.setLineWidth(2);
		plot.setColor(Color.blue);
		plot.show();
	}

	private void HCM(double[][] c, double[] red, double[] green, double[] blue, double[] figJ, int nbclasses,
			int nbpixels, double valeur_seuil, int itermax, int width, int height, ImageProcessor ipseg,
			ImagePlus impseg) {
		double Dmat[][] = new double[nbclasses][nbpixels];
		double Dprev[][] = new double[nbclasses][nbpixels];
		double Umat[][] = new double[nbclasses][nbpixels];
		double Uprev[][] = new double[nbclasses][nbpixels];
		int iter;
		double seuil, stab;
		int kmax = nbclasses;
		int cpt = 0;
		
		impseg.setTitle("Image segment�e par HCM");
		
		// Calcul de distance entre data et centroides
		this.distance2(red, green, blue, c, Dprev, nbpixels, kmax);

		// Initialisation des degr�s d'appartenance
		// A COMPLETER
		this.uHCM(Uprev, Dprev, nbpixels, kmax);

		/////////////////////////////////////////////////////////////
		// BOUCLE PRINCIPALE
		////////////////////////////////////////////////////////////
		iter = 0;
		stab = 2;
		seuil = valeur_seuil;

		/////////////////// A COMPLETER ///////////////////////////////
		while ((iter < itermax) && (stab > seuil)) {
			// Update the matrix of centroids
			for (int i = 0; i < nbclasses; i++) {
				double sum[] = { 0, 0, 0 };
				double sum2 = 0;
				for (int j = 0; j < nbpixels; j++) {
					sum[0] += Uprev[i][j] * red[j];
					sum[1] += Uprev[i][j] * green[j];
					sum[2] += Uprev[i][j] * blue[j];
				}
				for (int j = 0; j < nbpixels; j++) {
					sum2 += Uprev[i][j];
				}
				c[i][0] = sum[0] / sum2;
				c[i][1] = sum[1] / sum2;
				c[i][2] = sum[2] / sum2;

			}
			// Compute Dmat, the matrix of distances (euclidian) with the
			// centro�ds
			this.distance2(red, green, blue, c, Dmat, nbpixels, kmax);

			this.uHCM(Umat, Dmat, nbpixels, kmax);

			// Calculate difference between the previous partition and the new
			// partition (performance index)
			for (int l = 0; l < nbpixels; l++) {
				for (int k = 0; k < nbclasses; k++) {
					figJ[iter] += Uprev[k][l] * Dprev[k][l];
				}
			}
			stab = iter == 0 ? figJ[iter] : Math.abs(figJ[iter] - figJ[iter - 1]);
			iter++;

			Uprev = Umat.clone();
			Dprev = Dmat.clone();
			////////////////////////////////////////////////////////

			// Affichage de l'image segment�e
			double[] mat_array = new double[nbclasses];
			cpt = 0;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					for (int k = 0; k < nbclasses; k++) {
						mat_array[k] = Umat[k][cpt];
					}
					int indice = IndiceMaxOfArray(mat_array, nbclasses);
					int array[] = new int[3];
					array[0] = (int) c[indice][0];
					array[1] = (int) c[indice][1];
					array[2] = (int) c[indice][2];
					ipseg.putPixel(i, j, array);
					cpt++;
				}
			}
			impseg.updateAndDraw();
			//////////////////////////////////
		} // Fin boucle

		double[] xplot = new double[itermax];
		double[] yplot = new double[itermax];
		for (int w = 0; w < itermax; w++) {
			xplot[w] = (double) w;
			yplot[w] = (double) figJ[w];
		}
		Plot plot = new Plot("Performance Index (HCM)", "iterations", "J(P) value", xplot, yplot);
		plot.setLineWidth(2);
		plot.setColor(Color.blue);
		plot.show();
	}

	private void uHCM(double[][] u, double[][] d, int nbpixels, int kmax) {
		for (int l = 0; l < nbpixels; l++) {
			for (int i = 0; i < kmax; i++) {
				double res = 1;
				for (int k = 0; k < kmax; k++) {
					if (k != i && !(d[i][l] < d[k][l]))
						res = 0;
				}
				u[i][l] = res;
			}
		}
	}
	
	private void PCM(double[][] c, double[] red, double[] green, double[] blue, double[] figJ, int nbclasses,
			int nbpixels, double m, double valeur_seuil, int itermax, int width, int height, ImageProcessor ipseg,
			ImagePlus impseg) {
		double Dmat[][] = new double[nbclasses][nbpixels];
		double Dprev[][] = new double[nbclasses][nbpixels];
		double Umat[][] = new double[nbclasses][nbpixels];
		double Uprev[][] = new double[nbclasses][nbpixels];
		double[] mu = new double[nbclasses];
		int iter;
		double seuil, stab;
		int kmax = nbclasses;
		int cpt = 0;
		
		impseg.setTitle("Image segment�e par PCM");

		this.distance2(red, green, blue, c, Dprev, nbpixels, kmax);
		this.uFCM(Uprev, Dprev, nbpixels, kmax, m);
		for(int i = 0; i < kmax; i++) {
			this.mu(mu, Uprev, Dprev, nbpixels, kmax, m, i);
		}

		iter = 0;
		stab = 2;
		seuil = valeur_seuil;

		while ((iter < itermax) && (stab > seuil)) {
			// Update the matrix of centroids
			for (int i = 0; i < nbclasses; i++) {
				double sum[] = { 0, 0, 0 };
				double sum2 = 0;
				for (int j = 0; j < nbpixels; j++) {
					sum[0] += Math.pow(Uprev[i][j], m) * red[j];
					sum[1] += Math.pow(Uprev[i][j], m) * green[j];
					sum[2] += Math.pow(Uprev[i][j], m) * blue[j];
				}
				for (int j = 0; j < nbpixels; j++) {
					sum2 += Math.pow(Uprev[i][j], m);
				}
				c[i][0] = sum[0] / sum2;
				c[i][1] = sum[1] / sum2;
				c[i][2] = sum[2] / sum2;
			}
			// Compute Dmat, the matrix of distances (euclidian) with the
			// centro�ds
			this.distance2(red, green, blue, c, Dmat, nbpixels, kmax);
			this.uPCM(Umat, Dmat, mu, nbpixels, kmax, m);
			
			// Calculate difference between the previous partition and the new
			// partition (performance index)
			
			for (int k = 0; k < nbclasses; k++) {
				for (int l = 0; l < nbpixels; l++) {
					figJ[iter] += Math.pow(Uprev[k][l], m) * Dprev[k][l] + mu[k] + Math.pow((1.0 - Uprev[k][l]), m);;
				}
			}
			stab = iter == 0 ? figJ[iter] : Math.abs(figJ[iter] - figJ[iter - 1]);
			iter++;

			for(int i = 0; i < kmax; i++) {
				this.mu(mu, Umat, Dmat, nbpixels, kmax, m, i);
			}
			Uprev = Umat.clone();
			Dprev = Dmat.clone();
			
			////////////////////////////////////////////////////////

			// Affichage de l'image segment�e
			double[] mat_array = new double[nbclasses];
			cpt = 0;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					for (int k = 0; k < nbclasses; k++) {
						mat_array[k] = Umat[k][cpt];
					}
					int indice = IndiceMaxOfArray(mat_array, nbclasses);
					int array[] = new int[3];
					array[0] = (int) c[indice][0];
					array[1] = (int) c[indice][1];
					array[2] = (int) c[indice][2];
					ipseg.putPixel(i, j, array);
					cpt++;
				}
			}
			impseg.updateAndDraw();
			//////////////////////////////////
		} // Fin boucle

		double[] xplot = new double[itermax];
		double[] yplot = new double[itermax];
		for (int w = 0; w < itermax; w++) {
			xplot[w] = (double) w;
			yplot[w] = (double) figJ[w];
		}
		Plot plot = new Plot("Performance Index (FCM)", "iterations", "J(P) value", xplot, yplot);
		plot.setLineWidth(2);
		plot.setColor(Color.blue);
		plot.show();
	}
	
	private void mu(double[] mu, double[][] u, double [][] d, int nbpixels, int kmax, double m, int i) {
		double sum2 = 0;
		for(int j = 0; j < nbpixels; j++) {
			mu[i] += Math.pow(u[i][j], m) * d[i][j];
			sum2 += Math.pow(u[i][j], m);
		}
		mu[i] /= sum2;
	}
	
	private void uPCM(double[][] u, double[][] d, double[] mu, int nbpixels, int kmax, double m) {
		for(int j = 0; j < nbpixels; j++) {
			for(int i = 0; i < kmax; i++) {
				u[i][j] = 1.0 / (1.0 + Math.pow(d[i][j] / mu[i], 1.0 / (m - 1.0)));
			}
		}
	}

	int indice;
	double min, max;

	// Returns the maximum of the array

	public int IndiceMaxOfArray(double[] array, int val) {
		max = 0;
		for (int i = 0; i < val; i++) {
			if (array[i] > max) {
				max = array[i];
				indice = i;
			}
		}
		return indice;
	}

}
