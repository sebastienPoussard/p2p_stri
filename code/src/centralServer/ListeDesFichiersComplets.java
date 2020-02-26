package centralServer;

import java.util.HashMap;

/** @brief cette classe représente la liste des fichiers complets dont le serveur central à connaissance
 */
public class ListeDesFichiersComplets {
	
	private HashMap<String, Long> listeDesFichiersComplets;
	
	private ListeDesFichiersComplets() {
		this.listeDesFichiersComplets = new HashMap<String, Long>();
	}
	
	private static ListeDesFichiersComplets instance = null;
	
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
	
	public synchronized void ajouterFichier(String nom, Long taille) {
		if (!this.listeDesFichiersComplets.containsKey(nom))
			this.listeDesFichiersComplets.put(nom, taille);
	}
	
	public synchronized HashMap<String, Long> obtenirListeDesFichiersComplets() {
		return this.listeDesFichiersComplets;
	}
}
