package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

/**
 * @brief Cette classe gere le dossier contenant les fichiers partagés ainsi que le dossier de téléchargement.
 */
public class GestionnaireFichier {
	
	private File dossierDePartage;			// dossier contenant les fichiers partagés.
	private File dossierDeTelechargements;	// dossier ocntenant les fichiers téléchargés.
	
	/**
	 * @brief constructeur de GestionnaireFichier
	 * @param cheminDossierPartages chemin complet vers le dossiers contenant les fichiers partagés.
	 * accepte uniquement un chemin Linux car Linux > Windows.
	 */
	public GestionnaireFichier(String cheminDossierPartages, String cheminDossierTelechargements) {
		// pour l'instant (client et serveur sont séparés) on fait les construction en fonction de si
		// le serveur ou le client requpete la création de l'objet.
		if (cheminDossierPartages != null ) {
			this.dossierDePartage = new File(cheminDossierPartages);
			Messages.getInstance().ecrireMessage("Le serveur partage le dossier : "+cheminDossierPartages);
		}
		if (cheminDossierTelechargements != null ) {
			this.dossierDeTelechargements = new File(cheminDossierTelechargements);
			Messages.getInstance().ecrireMessage("dossier de téléchargement : "+cheminDossierTelechargements);
		}
	}
	
	
	public String listeFichiers() {
		String resultat = "";
		// recuperer la liste des fichier dans le dossier
		File[] listeDeFichiers = this.dossierDePartage.listFiles();
		for (File fichier : listeDeFichiers) {
			// ajouter le nom du fichier
			resultat+= fichier.getName()+" ";
			// ajouter la taille convertie du fichier
			long poids = fichier.length();
			if (poids >= 1000 && poids < 1000*1000) {
				resultat += new DecimalFormat("###.##").format(poids/1000.0) + " Ko";
			} else if ( poids >= 1000*1000 && poids < 1000*1000*1000) {
				resultat += new DecimalFormat("###.##").format(poids/(1000.0*1000.0)) + " Mo";
			} else if ( poids >= 1000*1000*1000) {
				resultat += new DecimalFormat("###.##").format(poids/(1000.0*1000.0*1000.0)) + " Go";
			} else {
				resultat += poids + " octets";
			}
			resultat += "\n";
		}
		return resultat;
	}

	/**
	 * @brief cette méthode permet d'obtenir le stream du fichier à partir d'un nom de fichier.
	 * @param nomFichier nom du fihier que l'on souhaite 
	 * @return	renvoie null si on ne trouve pas le fichier.
	 * @throws FileNotFoundException renvoie cette exception si le fichier demandé d'exist pas.
	 */
	public FileInputStream rechercherFichier(String nomFichier) throws FileNotFoundException {
		
		File[] listeDesFichiers = this.dossierDePartage.listFiles();
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
		return new FileOutputStream(new File(this.dossierDeTelechargements.getAbsolutePath()+"/"+nomFichier));
	}
	
	/**
	 * @brief créer un nouveau fichier temporaire dans le dossier temporaire du fichier.
	 * @param nomFichier nom du fichier téléchargé en bloc à créer.
	 * @param blocDebut numéro du premier bloc.
	 * @param blocFin numéro du dernier bloc.
	 * @throws FileNotFoundException renvoie une erreur si la création du fichier à échoué.
	 * @return renvoie le flux de donnée pour écrire dans le fichier.
	 */
	public FileOutputStream creerFichierTemporaire(String nomFichier, int blocDebut, int blocFin) throws FileNotFoundException{
		return new FileOutputStream(new File(this.dossierDeTelechargements.getAbsoluteFile()+"/temp_"+blocDebut+"-"+blocFin));
	}
	
	/**
	 * @brief créer le dossier temporaire pour acceuillir les fichiers temporaires du téléchargement en plusieurs blocs.
	 * @param nomFichier nom du fichier qui sera télécharger en plusieurs blocs.
	 */
	public void creerDossierTemporaire(String nomFichier) {
		File dossierTemporaire = new File(this.dossierDeTelechargements.getAbsolutePath()+"/temp_"+nomFichier);
		if (!dossierTemporaire.exists()) {
			dossierTemporaire.mkdir();
		}
	}
	
	
	
	

}
