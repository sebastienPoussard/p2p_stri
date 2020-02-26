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
		Messages.getInstance().ecrireErreur("Dossier des fichiers : "+this.cheminDossierFichiers);
		// créer le dossier des fichiers complets s'il n'existe pas déjà.
		if (!this.dossierDesFichiersComplets.exists())
			this.dossierDesFichiersComplets.mkdir();
		Messages.getInstance().ecrireErreur("Dossier des fichiers complets : "+this.cheminDossierFichiersComplets);
		// créer le dossier des fichiers incomplet s'il n'existe pas déjà.
		if (!this.dossierDesFichiersIncomplets.exists())
			this.dossierDesFichiersIncomplets.mkdir();
		Messages.getInstance().ecrireErreur("dossier des fichiers incomplets : "+this.cheminDossierFichiersIncomplets);
		// remplir le marqueur de bloc vide
		this.marqueurVide = new byte[1000*100];
		for(int i = 0; i < 1000*100 ; i+=10) {
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
	 * 
	 * TODO : construire le fichier avec le buffer vide, necessite la taille en paramètre.
	 * TODO : attention au dernier bloc, comment le gerer ?
	 * 
	 * @brief créer un nouveau fichier dans le dossier des fichiers incomplets.
	 * @param nomFichier le nom du fichier créer.
	 * @return renvoie le stream du fichier créer.
	 * @throws FileNotFoundException renvoie une erreur si la création du fichier à échoué.
	 * @return renvoie le flux de donnée pour écrire dans le fichier.
	 */
	public RandomAccessFile creerFichier(String nomFichier) throws FileNotFoundException {
		
		return new RandomAccessFile(new File(this.dossierDesFichiersIncomplets.getAbsolutePath()+"/"+nomFichier), "rw");
	}
	
	/**
	 * TODO : comment gerer le dernier bloc s'il n'est pas de taille standard ?
	 * 
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
			// chaque bloc fait 100Ko (1000 * 100)
			// convertir la taille en nombre de blocs
			double nombreDeBlocsFlottant = (taille/(1000*100));
			int nbrDeBlocs = (int) Math.ceil(nombreDeBlocsFlottant);
			System.out.println(fichier.getName()+" nb blocs : "+nbrDeBlocs);
			try {
				FileInputStream fIn = new FileInputStream(fichier);
				ListeDeBlocs listeDeBlocs = new ListeDeBlocs();
				for (int i = 0; i < nbrDeBlocs ; i++) {
					// lire le premier byte du bloc.
					byte[] b = new byte[1000*100];
					fIn.read(b, 0, 1000*100);
					// si le début du bloc contient le caractère \0 alors le bloc est vide.
					if (!b.equals(this.marqueurVide) ) {
						// ajouter le bloc a la liste.
						listeDeBlocs.ajouterUnBloc(i);
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
}
