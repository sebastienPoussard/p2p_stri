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
	
	/**
	 * @brief constructeur de GestionnaireFichier
	 * @param cheminDossierPartages chemin complet vers le dossiers contenant les fichiers partagés.
	 * accepte uniquement un chemin Linux car Linux > Windows.
	 */
	public GestionnaireFichier(String cheminDossierFichiers) {
		
		this.dossierDesFichiers = new File(cheminDossierFichiers);
		// créer le dossier d'application s'il nexiste pas déjà.
		if (!this.dossierDesFichiers.exists())
			this.dossierDesFichiers.mkdir();
		Messages.getInstance().ecrireMessage("Dossier des fichiers téléchargés et partagés : "+this.dossierDesFichiers.getAbsolutePath());
	}
	
	/**
	 * 
	 * @brief cette méthode permet d'obtenir le stream du fichier à partir d'un nom de fichier.
	 * @param nomFichier nom du fihier que l'on souhaite 
	 * @return renvoie null si on ne trouve pas le fichier.
	 * @throws FileNotFoundException renvoie cette exception si le fichier demandé d'existe pas.
	 */
	public RandomAccessFile rechercherFichier(String nomFichier) throws FileNotFoundException {
		
		File[] listeDesFichiers = this.dossierDesFichiers.listFiles();
		for (File fichier : listeDesFichiers) {
			if (fichier.getName().equals(nomFichier)) {
					return new RandomAccessFile(fichier, "rw");
			}
		}
		// lancer une erreur si on ne trouve pas le fichier.
		throw new FileNotFoundException();
	}
	
	/**
	 * @brief créer un nouveau fichier dans le dossier de téléchargement.
	 * @param nomFichier le nom du fichier créer.
	 * @return renvoie le stream du fichier créer.
	 * @throws FileNotFoundException renvoie une erreur si la création du fichier à échoué.
	 * @return renvoie le flux de donnée pour écrire dans le fichier.
	 */
	public RandomAccessFile creerFichier(String nomFichier) throws FileNotFoundException {
		
		return new RandomAccessFile(new File(this.dossierDesFichiers.getAbsolutePath()+"/"+nomFichier), "rw");
	}
	
	/**
	 * @brief créer les informations sur l'utilisateur.
	 * @param ip ip du serveur.
	 * @param port port d'écoute du serveur.
	 * @return renvoie les infos de l'utilisateur.
	 */
	
	public InfoUtilisateur creerInfosUtilisateur(String ip, int port) {
		InfoUtilisateur infos = new InfoUtilisateur(ip, port);
		// pour chaque fichier de l'utilisateur, regarder les blocs qu'il possede.
		File[] listeDesFichiers = this.dossierDesFichiers.listFiles();
		for (File fichier : listeDesFichiers) {
			long taille = fichier.getTotalSpace();
			// chaque bloc fait 100Ko (1000 * 100 * 8 )
			// convertir la taille en nombre de blocs
			double nombreDeBlocsFlottant = (taille/(1000*100*8.0));
			int nbrDeBlocs = (int) Math.ceil(nombreDeBlocsFlottant);
			try {
				FileInputStream fIn = new FileInputStream(fichier);
				ListeDeBlocs listeDeBlocs = new ListeDeBlocs();
				for (int i = 0; i < nbrDeBlocs ; i++) {
					// lire le premier byte du bloc.
					byte[] b = new byte[1];
					fIn.read(b, i*(1000*100*8), 1);
					// si le début du bloc contient le caractère \0 alors le bloc est vide.
					if (b[0] != '\0') {
						// ajouter le bloc a la liste.
						listeDeBlocs.ajouterUnBloc(i);
					}
				}
				infos.ajouterFichier(fichier.getName(), listeDeBlocs);
			} catch (IOException e) {
				Messages.getInstance().ecrireErreur("impossible de lire le fichier pour voir ses blocs...");
			}
		}
		return infos;
	}
}
