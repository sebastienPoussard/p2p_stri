package centralServer;

import java.util.HashMap;

/** @brief cette classe représente la liste des fichiers complets dont le serveur central à connaissance
 */
public class ListeDesFichiersComplets {
	
	private HashMap<String, Long> listeDesFichiersComplets;		// la liste des fichiers et leur taille
	
	/**
	 * @brief constructeur en private pour le singleton.
	 */
	private ListeDesFichiersComplets() {
		this.listeDesFichiersComplets = new HashMap<String, Long>();
	}
	
	private static ListeDesFichiersComplets instance = null;	// l'unique instance de la classe.
	
	/**
	 * @brief singleton pour accéder à une instance unique de ListeDesFichiersComplets
	 * @return retourne une instance unique de ListeDesFichiersComplets
	 */
	public static ListeDesFichiersComplets getInstance() {
		if (instance == null ) {
			instance = new ListeDesFichiersComplets();
		}
		return instance;
	}
	
	/**
	 * @brief permet d'ajouter un nouveau fichier à la liste des fichiers enregistrés par le serveur.
	 * @param nom le nom du fichier
	 * @param taille la taile du fichier en byte.
	 */
	public synchronized void ajouterFichier(String nom, Long taille) {
		if (!this.listeDesFichiersComplets.containsKey(nom))
			this.listeDesFichiersComplets.put(nom, taille);
	}
	
	/**
	 * @brief permet d'obtenir la liste des fichier complets.
	 * @return renvoie la liste des fichiers complets.
	 */
	public synchronized HashMap<String, Long> obtenirListeDesFichiersComplets() {
		return this.listeDesFichiersComplets;
	}
}
