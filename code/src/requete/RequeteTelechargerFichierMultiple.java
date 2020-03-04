package requete;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import commun.InfoUtilisateur;
import commun.ListeDeBlocs;
import commun.Messages;
import terminalClient.GestionnaireFichier;

/**
 * 
 * @brief cette classe va télécharger un fichier depuis un ou plusieurs autres clients.
 */
public class RequeteTelechargerFichierMultiple extends Requete {

	private String nomFichier;									// nom du fichier à télécharger.
	private GestionnaireFichier gestionnaireFichier;			// le gestionnaire de fichier.
	private HashMap<String, InfoUtilisateur> listeDesUtilisateursAyantLeFichier;
	private RandomAccessFile fluxFichier;
	
	/**
	 * @brief télécharge un fichier chez un ou plusieurs serveurs.
	 * @param adresseServeurCentral adresse du serveur central et son port en format IP:PORT.
	 * @param nomFichier nom du fichier à télécharger.
	 * @param gestionnaireFichier le gestionnaire de fichier.
	 */
	public RequeteTelechargerFichierMultiple(String adresseServeurCentral, String nomFichier,
			GestionnaireFichier gestionnaireFichier) {
		super(adresseServeurCentral);
		this.nomFichier = nomFichier;
		this.gestionnaireFichier = gestionnaireFichier;
	}
	
	/**
	 * @brief methode pour lancer le Thread.
	 */
	@Override
	public void run() {
		// connecter au serveur central et récuperer les fluxs.
		super.run();
		// envoyer la requête au serveur pour obtenir la listes des utilisateurs et les blocs qu'ils ont
		// pour le fichier que l'on souhaite télécharger.
		envoyerRequete("DOWNLOAD "+this.nomFichier);
		// lire la réponse du serveur central.
		String reponse = "";
		Object obj;
		try {
			obj = this.objIn.readObject();
			this.listeDesUtilisateursAyantLeFichier = (HashMap<String, InfoUtilisateur>)obj;
			Iterator iterateur = listeDesUtilisateursAyantLeFichier.entrySet().iterator();
			while (iterateur.hasNext()) {
				Map.Entry utilisateur = (Entry)iterateur.next();
				reponse += utilisateur.getKey()+" \n"+utilisateur.getValue()+"\n";
			}
			Messages.getInstance().ecrireMessage(reponse);
		} catch (IOException | ClassNotFoundException e) {
			Messages.getInstance().ecrireErreur("echec à la reception de la liste de la liste des utilisateurs ayant le fichier.");
		}
		// construire le fichier "vide" dans le dossier des fichiers incomplets.
		try {
			this.fluxFichier = this.gestionnaireFichier.creerFichier(this.nomFichier, determinerTailleDuFichier(this.listeDesUtilisateursAyantLeFichier));
		} catch (FileNotFoundException e) {
			Messages.getInstance().ecrireErreur("echec à la création du fichier vide "+this.nomFichier);
		}
		// fermer le Thread.
		terminer();
	}
	
	/**
	 * @brief methode pour obtenir la taille du fichier.
	 * @param liste la liste des utilisateurs ayant le fichier.
	 * @return renvoie la taille du fichier final.
	 */
	private long determinerTailleDuFichier(HashMap<String, InfoUtilisateur> liste) {
		long res = 0;
		Iterator iterateur = liste.entrySet().iterator();
		while (iterateur.hasNext()) {
			Map.Entry utilisateur = (Entry)iterateur.next();
			// obtenir les infos de l'utilisateur.
			InfoUtilisateur infos = (InfoUtilisateur) utilisateur.getValue();
			// obtenir la liste de bloc de l'utilisateur.
			ListeDeBlocs blocs = infos.blocDuFichier(this.nomFichier);
			// récuperer la taille du fichier
			if (blocs.obtenirTailleDuFichier() != -1) {
				res = blocs.obtenirTailleDuFichier();
				break;
			}
		}
		return res;
	}
}
