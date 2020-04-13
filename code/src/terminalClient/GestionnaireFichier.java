package terminalClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Arrays;

import commun.InfoUtilisateur;
import commun.ListeDeBlocs;
import commun.Messages;

/**
 * @brief Cette classe gere le dossier contenant les fichiers partagés et téléchargés.
 */
public class GestionnaireFichier {
	
	private File dossierDesFichiers;					// dossier contenant les dossiers "COMPLETS" "INCOMPLETS".
	private File dossierDesFichiersComplets;			// le dossier contenant les fichiers complets.
	private File dossierDesFichiersIncomplets;			// le dossier contenant les fichiers incomplets.
	private byte[] marqueurVide;						// le marqueur "vide" c'est à dire les parties que l'on à pas encore téléchargé.
	private String cheminDossierFichiers;				// le chemin du dossier qui contient les fichiers "COMPLETS" et "INCOMPLETS".
	private String cheminDossierFichiersComplets;		// le chemin du dossier contenat les fichiers complets.
	private String cheminDossierFichiersIncomplets;		// le chemin du dossier contenant les fichiers incomplets.
//	public static int TAILLEDEBLOC = 1000*100;			// un bloc fait 100Ko (doit être multiple de 10)
	public static int TAILLEDEBLOC = 10;				// un bloc fait 100o pour tests (doit être multiple de 10)
	
	/**
	 * @brief constructeur de GestionnaireFichier
	 * @param cheminDossierPartages chemin complet vers le dossiers contenant les fichiers partagés.
	 * accepte uniquement un chemin Linux car Linux > Windows ¯\_(ツ)_/¯ .
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
	 * @brief cette méthode permet d'obtenir le stream du fichier à partir d'un nom de fichier.
	 * @param nomFichier nom du fihier que l'on souhaite 
	 * @return renvoie le fichier que l'on souhaite.
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
		File[] listeDesFichiersIncomplets = this.dossierDesFichiersIncomplets.listFiles();
		for (File fichier : listeDesFichiersIncomplets) {
			if (fichier.getName().equals(nomFichier)) {
					return new RandomAccessFile(fichier, "rw");
			}
		}
		// lancer une erreur si on ne trouve pas le fichier.
		throw new FileNotFoundException();
	}
	
	/**
	 * @brief permet de créer un fichier "vide" c'est à dire de la taille du fichier complet,
	 * remplie avec le marqueur vide en attendant de reçevoir les bon blocs.
	 * @param nomFichier le nom du fichier à créer.
	 * @param taille la taille du fichier une fois celi-ci complet.
	 * @return renvoie un lien vers le fichier.
	 * @throws FileNotFoundException remonte une exception en cas de problème.
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
	 * @param ip ip de la fonctionnalité serveur du client.
	 * @param port port d'écoute de la fonctionnalité serveur du client.
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
			// convertir la taille en nombre de blocs
			double nombreDeBlocsFlottant = (taille/(TAILLEDEBLOC));
			int nbrDeBlocs = (int) Math.ceil(nombreDeBlocsFlottant);
			try {
				FileInputStream fIn = new FileInputStream(fichier);
				ListeDeBlocs listeDeBlocs = new ListeDeBlocs();
				// parcourir chaque bloc
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
							listeDeBlocs.ajouterUnBloc(i+1);
						}
					} else {
						// sinon on est sur le dernier bloc
						byte[] blocIncomplet = new byte[tailleRestante];
						fIn.read(blocIncomplet, 0, tailleRestante);
						// si le bloc de byte restant n'est pas égale au marqueur vide
						if (!estEgaleAuMarqueurVide(blocIncomplet)) {
							// ajouter le bloc à la liste
							listeDeBlocs.ajouterUnBloc(i+1);
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
	 * @brief méthode pour écrire un bloc (numeroDuBloc) depuis un buffer (buffer) de taille (taille)
	 * @param buffer le buffer contenant les données du bloc à écrire.
	 * @param taille la taille que l'on souhaite écrire du buffer.
	 * @param fichier le fichier vers lequelle on va écrire les données.
	 * @param numeroDuBloc le numéro du bloc que l'on souhaite écrire.
	 */
	public synchronized void ecrire(byte[] buffer, int taille, RandomAccessFile fichier, int numeroDuBloc) {
		try {
			fichier.seek((numeroDuBloc-1)*GestionnaireFichier.TAILLEDEBLOC);
			fichier.write(buffer, 0, taille);
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("erreur à l'écriture du bloc "+numeroDuBloc+" du fichier ");
		}
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

	/**
	 * @brief permet de lire les données du bloc d'un fichier. 
	 * @param nomFichier le nom du fichier dont on souhaite obtenir les données.
	 * @param numeroBloc le numéro du bloc que l'on souhaite lire.
	 * @return renvoie une chaine de byte contenant les informations.
	 */
	public synchronized byte[] obtenirDonnees(String nomFichier, int numeroBloc) {
		byte[] res = new byte[GestionnaireFichier.TAILLEDEBLOC];
		try {
			RandomAccessFile fichier = 	rechercherFichier(nomFichier);
			fichier.seek((numeroBloc-1)*GestionnaireFichier.TAILLEDEBLOC);
			int taille = fichier.read(res, 0, GestionnaireFichier.TAILLEDEBLOC);
			if (taille < GestionnaireFichier.TAILLEDEBLOC) {
				res = Arrays.copyOfRange(res, 0,taille);
			}
			fichier.close();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("impossible de récuperer les données à envoyer.");
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * @brief vérifie si le fichier indiqué est complet, dans ce cas on le déplace du dossier incomplet vers le dossier complet.
	 * @param nomFichier
	 */
	public synchronized void verifierIntegritee(String nomFichier) {
		try {
			// rechercher le fichier.
			boolean estIntegre = true;
			RandomAccessFile fichier = rechercherFichier(nomFichier);
			// convertir la taille en nombre de blocs
			long taille = fichier.length();
			double nombreDeBlocsFlottant = (taille/(TAILLEDEBLOC));
			int nbrDeBlocs = (int) Math.ceil(nombreDeBlocsFlottant);
			int tailleLue;
			byte [] buffer = new byte[TAILLEDEBLOC];
			ByteArrayOutputStream donnees = new ByteArrayOutputStream();
			// pour chaque bloc
			for (int i = 0; i < nbrDeBlocs ; i++) {
				tailleLue = fichier.read(buffer, 0, TAILLEDEBLOC);
				if (tailleLue < TAILLEDEBLOC) {
					donnees.write(buffer, 0, tailleLue);
					buffer = donnees.toByteArray();
				}
				// si le bloc est égale au marqueur vide
				if (estEgaleAuMarqueurVide(buffer)) {
					estIntegre = false;
					break;
				}
			}
			// si le fichier est intègre
			if (estIntegre) {
				// déplacer dans le dossier des fichiers terminés
				File f = new File(this.cheminDossierFichiersIncomplets+"/"+nomFichier);
				f.renameTo(new File(cheminDossierFichiersComplets+"/"+nomFichier));
				Messages.getInstance().ecrireMessage("Le fichier "+nomFichier+" est complet et à été déplacé dans le dossier des fichiers complets");
			}
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("impossible de verifier l'intégritée du fichier.");
		}
	}
}
