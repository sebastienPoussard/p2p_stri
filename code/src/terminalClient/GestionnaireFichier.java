package terminalClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.Arrays;

import commun.InfoUtilisateur;
import commun.ListeDeBlocs;
import commun.Messages;

/**
 * @brief Cette classe gere le dossier contenant les fichiers partagés et téléchargés.
 */
public class GestionnaireFichier {
	
	private File dossierDesFichiers;		// dossier contenant les fichiers partagés & téléchargés.
	private File dossierDesFichiersComplets;
	private File dossierDesFichiersIncomplets;
	private byte[] marqueurVide;	
	private String cheminDossierFichiers;
	private String cheminDossierFichiersComplets;
	private String cheminDossierFichiersIncomplets;
	public static int TAILLEDEBLOC = 1000*100;		// un bloc fait 100Ko (doit être multiple de 10)
	
	/**
	 * @brief constructeur de GestionnaireFichier
	 * @param cheminDossierPartages chemin complet vers le dossiers contenant les fichiers partagés.
	 * accepte uniquement un chemin Linux car Linux > Windows.
	 */
	public GestionnaireFichier(String cheminDossierFichiers) {
		// récuperer et créer les chemins des dossiers et les objet File
		this.cheminDossierFichiers = cheminDossierFichiers;
		this.cheminDossierFichiersComplets = cheminDossierFichiers+"/complet";
		this.cheminDossierFichiersIncomplets = cheminDossierFichiers+"/incomplet";
		this.dossierDesFichiers = new File(cheminDossierFichiers);
		this.dossierDesFichiersComplets= new File(this.cheminDossierFichiersComplets);
		this.dossierDesFichiersIncomplets = new File(this.cheminDossierFichiersIncomplets);
		// créer le dossier d'application s'il n'existe pas déjà.
		if (!this.dossierDesFichiers.exists())
			this.dossierDesFichiers.mkdir();
		Messages.getInstance().ecrireMessage("Dossier des fichiers : "+this.cheminDossierFichiers);
		// créer le dossier des fichiers complets s'il n'existe pas déjà.
		if (!this.dossierDesFichiersComplets.exists())
			this.dossierDesFichiersComplets.mkdir();
		Messages.getInstance().ecrireMessage("Dossier des fichiers complets : "+this.cheminDossierFichiersComplets);
		// créer le dossier des fichiers incomplet s'il n'existe pas déjà.
		if (!this.dossierDesFichiersIncomplets.exists())
			this.dossierDesFichiersIncomplets.mkdir();
		Messages.getInstance().ecrireMessage("dossier des fichiers incomplets : "+this.cheminDossierFichiersIncomplets);
		// remplir le marqueur de bloc vide
		this.marqueurVide = new byte[TAILLEDEBLOC];
		for(int i = 0; i < TAILLEDEBLOC ; i+=10) {
			this.marqueurVide[i+0] = 'J';
			this.marqueurVide[i+1] = 'E';
			this.marqueurVide[i+2] = 'A';
			this.marqueurVide[i+3] = 'N';
			this.marqueurVide[i+4] = 'N';
			this.marqueurVide[i+5] = 'E';
			this.marqueurVide[i+6] = 'T';
			this.marqueurVide[i+7] = 'T';
			this.marqueurVide[i+8] = 'E';
			this.marqueurVide[i+9] = '!';
		}
	}
	
	/**
	 * 
	 * @brief cette méthode permet d'obtenir le stream du fichier à partir d'un nom de fichier.
	 * @param nomFichier nom du fihier que l'on souhaite 
	 * @return renvoie null si on ne trouve pas le fichier.
	 * @throws FileNotFoundException renvoie cette exception si le fichier demandé d'existe pas.
	 */
	public RandomAccessFile rechercherFichier(String nomFichier) throws FileNotFoundException {
		// chercher dans les fichier complets.
		File[] listeDesFichiersComplets = this.dossierDesFichiersComplets.listFiles();
		for (File fichier : listeDesFichiersComplets) {
			if (fichier.getName().equals(nomFichier)) {
					return new RandomAccessFile(fichier, "rw");
			}
		}
		// chercher dans les fichiers incomplets.
		File[] listeDesFichiersIncomplets = this.dossierDesFichiersComplets.listFiles();
		for (File fichier : listeDesFichiersIncomplets) {
			if (fichier.getName().equals(nomFichier)) {
					return new RandomAccessFile(fichier, "rw");
			}
		}
		// lancer une erreur si on ne trouve pas le fichier.
		throw new FileNotFoundException();
	}
	
	/**
	 * @brief créer un nouveau fichier dans le dossier des fichiers incomplets.
	 * @param nomFichier le nom du fichier créer.
	 * @return renvoie le stream du fichier créer.
	 * @throws FileNotFoundException renvoie une erreur si la création du fichier à échoué.
	 * @return renvoie le flux de donnée pour écrire dans le fichier.
	 */
	public RandomAccessFile creerFichier(String nomFichier, long taille) throws FileNotFoundException {
		
		RandomAccessFile fichier = new RandomAccessFile(new File(this.dossierDesFichiersIncomplets.getAbsolutePath()+"/"+nomFichier), "rw");
		//remplir le fichier de bloc "vides"
		for (long i = 0; i < taille; i += TAILLEDEBLOC ) {
			try {
				// s'il reste encore de la place écrire un bloc entier
				if (taille - i >= TAILLEDEBLOC) {
					fichier.write(this.marqueurVide);
				// sinon écrire seulement la taille restante
				} else {
					fichier.write(marqueurVide, 0, (int)(taille-i));
				}
			} catch (IOException e) {
				Messages.getInstance().ecrireErreur("probléme à la génération du fichier vide.");
			}
		}
		return fichier;
	}
	
	/**
	 * @brief créer les informations sur l'utilisateur.
	 * @param ip ip du serveur.
	 * @param port port d'écoute du serveur.
	 * @return renvoie les infos de l'utilisateur.
	 */
	
	public InfoUtilisateur creerInfosUtilisateur(String ip, int port) {
		InfoUtilisateur infos = new InfoUtilisateur(ip, port);
		// pour chaque fichier complets que l'utilisateur, ajouter les infos du fichiers.
		// pour un fichier complet on ajoute la taille du fichier.
		File [] listeDesFichiersComplets = this.dossierDesFichiersComplets.listFiles();
		for (File fichier : listeDesFichiersComplets) {
			ListeDeBlocs listeDeBlocs = new ListeDeBlocs();
			listeDeBlocs.definirTailleDuFichier(fichier.length());
			infos.ajouterFichier(fichier.getName(), listeDeBlocs);
		}
		
		// pour chaque fichier incomplet de l'utilisateur, regarder les blocs qu'il possede.
		File[] listeDesFichiersIncomplets = this.dossierDesFichiersIncomplets.listFiles();
		for (File fichier : listeDesFichiersIncomplets) {
			long taille = fichier.length();
			System.out.println(fichier.getName() +" bytes : "+taille);
			// convertir la taille en nombre de blocs
			double nombreDeBlocsFlottant = (taille/(TAILLEDEBLOC));
			int nbrDeBlocs = (int) Math.ceil(nombreDeBlocsFlottant);
			System.out.println(fichier.getName()+" nb blocs : "+nbrDeBlocs);
			try {
				FileInputStream fIn = new FileInputStream(fichier);
				ListeDeBlocs listeDeBlocs = new ListeDeBlocs();
				for (int i = 0; i < nbrDeBlocs ; i++) {
					// lire le bloc.
					byte[] bloc = new byte[TAILLEDEBLOC];
					int tailleRestante = fIn.available();
					// si la taille restante à lire est supérieur à un bloc.
					if (tailleRestante >= TAILLEDEBLOC) {
						// lire la taille d'un bloc.
						fIn.read(bloc, 0, TAILLEDEBLOC);
						// si le bloc lue n'est pas égale au bloc vide.
						if (!estEgaleAuMarqueurVide(bloc) ) {
							// ajouter le bloc à la liste.
							listeDeBlocs.ajouterUnBloc(i);
						}
					} else {
						// sinon on est sur le dernier bloc
						byte[] blocIncomplet = new byte[tailleRestante];
						fIn.read(blocIncomplet, 0, tailleRestante);
						// si le bloc de byte restant n'est pas égale au marqueur vide
						if (!estEgaleAuMarqueurVide(blocIncomplet)) {
							// ajouter le bloc à la liste
							listeDeBlocs.ajouterUnBloc(i);
						}
					}
				}
				infos.ajouterFichier(fichier.getName(), listeDeBlocs);
				fIn.close();
			} catch (IOException e) {
				Messages.getInstance().ecrireErreur("impossible de lire le fichier pour voir ses blocs...");
			}
		} 
		return infos;
	}
	
	/**
	 * @brief methode interne utiliser pour comparer un tableau de byte au marqueur vide.
	 * @param bufferLue le buffer à tester.
	 * @return renvoie true si le tableau de byte est égale au marqueur vide.
	 */
	private boolean estEgaleAuMarqueurVide(byte[] bufferLue) {
		int taille = bufferLue.length;
		boolean res = true;
		for (int i = 0 ; i < taille; i++) {
			if (bufferLue[i] != this.marqueurVide[i] ) {
				res = false;
				break;
			}
		}
		return res;
	}
}
