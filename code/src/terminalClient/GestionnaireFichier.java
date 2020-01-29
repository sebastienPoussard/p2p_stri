package terminalClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

import commun.InfoUtilisateur;
import commun.ListeDeBlocs;
import commun.Messages;

/**
 * @brief Cette classe gere le dossier contenant les fichiers partagés ainsi que le dossier de téléchargement.
 */
public class GestionnaireFichier {
	
	private File dossierDesFichiers;		// dossier contenant les fichiers partagés & téléchargés.
	private File dossierTemp;				// dossier contenant les fichier temporaires.
	
	/**
	 * @brief constructeur de GestionnaireFichier
	 * @param cheminDossierPartages chemin complet vers le dossiers contenant les fichiers partagés.
	 * accepte uniquement un chemin Linux car Linux > Windows.
	 */
	public GestionnaireFichier(String cheminDossierApplication) {
		File dossierApplication = new File(cheminDossierApplication);
		// créer le dossier d'application s'il nexiste pas déjà.
		if (!dossierApplication.exists())
			dossierApplication.mkdir();
		// créer le dossier des fichiers téléchargés & partagés s'il n'existe pas déjà.
		this.dossierDesFichiers = new File(cheminDossierApplication+"/fichiers");
		if (!this.dossierDesFichiers.exists())
			this.dossierDesFichiers.mkdir();
		Messages.getInstance().ecrireMessage("Dossier des fichiers téléchargés et partagés : "+this.dossierDesFichiers.getAbsolutePath());
		// créer le dossier de téléchargements temporaire s'il n'existe pas déjà.
		this.dossierTemp = new File(cheminDossierApplication+"/temp");
		if (!this.dossierTemp.exists())
			this.dossierTemp.mkdir();
	}
	
	/**
	 *    A REVOIR
	 * 
	 * @brief cette méthode permet d'obtenir le stream du fichier à partir d'un nom de fichier.
	 * @param nomFichier nom du fihier que l'on souhaite 
	 * @return renvoie null si on ne trouve pas le fichier.
	 * @throws FileNotFoundException renvoie cette exception si le fichier demandé d'existe pas.
	 */
	public FileInputStream rechercherFichier(String nomFichier) throws FileNotFoundException {
		
		File[] listeDesFichiers = this.dossierDesFichiers.listFiles();
		for (File fichier : listeDesFichiers) {
			if (fichier.getName().equals(nomFichier)) {
					return new FileInputStream(fichier);
			}
		}
		throw new FileNotFoundException();
	}
	
	/**
	 * @brief créer un nouveau fichier dans le dossier de téléchargement.
	 * @param nomFichier le nom du fichier créer.
	 * @return renvoie le stream du fichier créer.
	 * @throws FileNotFoundException renvoie une erreur si la création du fichier à échoué.
	 * @return renvoie le flux de donnée pour écrire dans le fichier.
	 */
	public FileOutputStream creerFichier(String nomFichier) throws FileNotFoundException {
		return new FileOutputStream(new File(this.dossierDesFichiers.getAbsolutePath()+"/"+nomFichier));
	}
	
	/**
	 * @brief créer un nouveau fichier temporaire dans le dossier temporaire du fichier.
	 * @param nomFichier nom du fichier téléchargé en bloc à créer.
	 * @param blocDebut numéro du premier bloc.
	 * @param blocFin numéro du dernier bloc.
	 * @throws FileNotFoundException renvoie une erreur si la création du fichier à échoué.
	 * @return renvoie le flux de donnée pour écrire dans le fichier.
	 */
	public FileOutputStream creerFichierTemporaire(String nomFichier, String blocDebut, String blocFin) throws FileNotFoundException{
		creerDossierTemporaire(nomFichier);
		return new FileOutputStream(new File(this.dossierTemp.getAbsoluteFile()+nomFichier+"/"+blocDebut+"-"+blocFin));
	}
	
	/**
	 * @brief créer le dossier temporaire pour acceuillir les fichiers temporaires du téléchargement en plusieurs blocs.
	 * @param nomFichier nom du fichier qui sera télécharger en plusieurs blocs.
	 */
	public void creerDossierTemporaire(String nomFichier) {
		File dossierTemporaire = new File(this.dossierTemp.getAbsolutePath()+"/"+nomFichier);
		if (!dossierTemporaire.exists()) {
			dossierTemporaire.mkdir();
		}
	}
	
	/**
	 * @brief prend les differents bloc de fichier pour reformer le fichier final.
	 * @param nomFichier le nom du fichier à reformer.
	 * @throws IOException 
	 */
	public void reformer(String nomFichier) throws IOException {
		// créer le fichier final
		File fichierFinal = new File(this.dossierDesFichiers.getAbsoluteFile()+"/"+nomFichier);
		FileOutputStream streamFichierFinal = new FileOutputStream(fichierFinal);
		// récuperer la liste des fichiers blocs
		File dossierTemp = new File(this.dossierTemp.getAbsoluteFile()+"/"+nomFichier);
		File[] listeFichierTemp = dossierTemp.listFiles();
		// ouvrir chaque fichier temporaire et écrire son contenu dans le fichier final.
		for (File fichierTemp : listeFichierTemp) {
			// ouvrir le stream du fichier temporaire
			FileInputStream streamFichierTemp = new FileInputStream(fichierTemp);
			int marqueur;						// marqueur de positon dans le buffer de "données".
			byte[] donnee = new byte[1024];		// buffer de données pour stocker la réponse du serveur.
			while ((marqueur = streamFichierTemp.read(donnee)) > 0) {
				streamFichierFinal.write(donnee, 0, marqueur);
				// si c'est le dernier bloc, sortir de la boucle
				if (marqueur < 1024) {
					break;
				}
			}
			// fermer le flux du bloc fichier
			streamFichierTemp.close();
			// supprimer le fichier temporaire
			fichierTemp.delete();
		}
		// fermer le flux du fichier final
		streamFichierFinal.close();
		// supprimer le dossier temporaire contenant les bloc fichiers
		dossierTemp.delete();
	}


	public void remplirListeDesFichiers(InfoUtilisateur infoUtilisateur) {
		//remplir la liste des fichiers complets.
		File[] fichiersComplets = dossierDesFichiers.listFiles();
		Arrays.sort(fichiersComplets);
		for (File fichierComplet : fichiersComplets) {
			ListeDeBlocs listeDeBlocs = new ListeDeBlocs();
			// taille max = 2147 Go
			int nbrBlocs =  (int)fichierComplet.length()/(1000*8);
			listeDeBlocs.ajouterIntervalle(1, nbrBlocs);
			System.out.println(fichierComplet.getAbsolutePath());
		}
		//remplir la liste des fichiers incomplets
		File[] dossierDeFichierIncomplet = dossierTemp.listFiles();
		Arrays.sort(dossierDeFichierIncomplet);
		// parcourir les dossiers
		for (File dossier : dossierDeFichierIncomplet) {
			File[] fichiersTemp = dossier.listFiles();
			Arrays.sort(fichiersTemp);
			// parcourir les fichier de chaque dossier
			for (File fichier : fichiersTemp) {
				System.out.println(fichier.getAbsolutePath());
			}
		}
	}
}
