package centralServer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import commun.InfoUtilisateur;

/** @brief cette classe représente la liste des infos utilisateurs connecté.
 */
public class ListeDesInfoUtilisateur {
	
	private HashMap<String, InfoUtilisateur> listeDesInfoUtilisateur;	// la liste des infos utilisateurs connectés
	private static ListeDesInfoUtilisateur instance = null;				// instance statique pour singleton.
	
	/**
	 * @brief constructeur privé pour sigleton
	 */
	private ListeDesInfoUtilisateur() {
		this.listeDesInfoUtilisateur = new HashMap<String, InfoUtilisateur>();
	}
	
	/**
	 * @brief singleton pour accéder à une instance unique de ListeDesInfosUtilisateurs
	 * @return retourne une instance unique de ListeDesInfoUtilisateur
	 */
	public static ListeDesInfoUtilisateur getInstance() {
		if (instance == null ) {
			instance = new ListeDesInfoUtilisateur();
		}
		return instance;
	}

	/**
	 * @brief methode pour ajouter un nouvel utilisateur à la liste des utilisateurs connectés.
	 * @param adresse adresse ip de l'utilisateur à ajouter.
	 * @param info la liste des fichiers que l'utilisateur possède.
	 */
	public synchronized void ajouterUtilisateur(String adresse, InfoUtilisateur info) {
		if (!this.listeDesInfoUtilisateur.containsKey(adresse))
			this.listeDesInfoUtilisateur.put(adresse, info);
	}
	
	/**
	 * @brief methode pour enlever un utilisateur à la liste des utilisateurs connectés.
	 * @param adresse l'adresse ip de l'utilisateur à enlever de la liste.
	 */
	public synchronized void enleverUtilisateur(String adresse) {
		this.listeDesInfoUtilisateur.remove(adresse);
	}
	
	/**
	 * @brief methode pour obtenir la liste des utilisateurs qui possède un fichier en particulier.
	 * @param fichier le nom du fichier en question.
	 * @return renvoie la liste.
	 */
	public synchronized HashMap<String, InfoUtilisateur> obtenirLaListeDesUtilisateursAyantLeFichier(String fichier) {
		HashMap<String, InfoUtilisateur> resultat = new HashMap<String, InfoUtilisateur>();
		// parcourir la liste complete des utilisateurs.
		Iterator iterateur = this.listeDesInfoUtilisateur.entrySet().iterator();
		while (iterateur.hasNext()) {
			Map.Entry utilisateur = (Entry) iterateur.next();
			InfoUtilisateur info = (InfoUtilisateur) utilisateur.getValue();
			// à chaque fois qu'un utilisateur posséde le fichier.
			if (info.detientLeFichier(fichier)) {
				// rajouter l'utilisateur à la liste à renvoyer.
				resultat.put((String)utilisateur.getKey(), (InfoUtilisateur)utilisateur.getValue());
			}
		}
		return resultat;
	}
	
}
