package centralServer;

import java.util.HashMap;

import commun.InfoUtilisateur;

/** @brief cette classe représente la liste des infos utilisateurs connecté.
 */
public class ListeDesInfoUtilisateur {
	
	private HashMap<String, InfoUtilisateur> listeDesInfoUtilisateur;
	
	private ListeDesInfoUtilisateur() {
		this.listeDesInfoUtilisateur = new HashMap<String, InfoUtilisateur>();
	}
	
	private static ListeDesInfoUtilisateur instance = null;
	
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
	
	public synchronized void ajouterUtilisateur(String adresse, InfoUtilisateur info) {
		if (!this.listeDesInfoUtilisateur.containsKey(adresse))
			this.listeDesInfoUtilisateur.put(adresse, info);
	}
	
	public synchronized HashMap<String, InfoUtilisateur> obtenirListeDesFichiersComplets() {
		return this.listeDesInfoUtilisateur;
	}
	
	public synchronized void enleverUtilisateur(String adresse) {
		this.listeDesInfoUtilisateur.remove(adresse);
	}
}
