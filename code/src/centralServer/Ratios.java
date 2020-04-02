package centralServer;

import java.util.HashMap;

/** @brief cette classe gère les ratios des utilisateurs
 */
public class Ratios {
	
	private static Ratios instance = null;		// instance du singleton.
	private HashMap<String, Long> scoreEnvoie;	// liste des utilisateurs et la quantité de données qu'ils ont envoyés.
	private HashMap<String, Long> scoreRecue;	// liste des utilisateurs et la quantité de données qu'ils ont reçues.
	private double ratioMin = 1.5;
	/**
	 * @brief constructeur privé de la classe.
	 */
	private Ratios() {
		this.scoreEnvoie = new HashMap<String, Long>();
		this.scoreRecue = new HashMap<String, Long>();
	}
	
	/**
	 * @brief singleton pour accéder à une instance unique de Ratios
	 * @return retourne une instance unique de Ratios
	 */
	public static Ratios getInstance() {
		if (instance == null ) {
			instance = new Ratios();
		}
		return instance;
	}
	
	/**
	 * @brief méthode pour ajouter des quantitées de données à la BDD.
	 * @param receveur adresse IP:PORT de la machine qui à reçue des données (en Ko).
	 * @param emetteur adresse IP:PORT de la machine qui à émise les données (en Ko).
	 * @param quantitee quantitée de données transmise
	 */
	public synchronized void ajouterInfos (String receveur, String emetteur, Long quantitee) {
		// si le receveur n'à encore rien reçue le créer
		if (!this.scoreRecue.containsKey(receveur)) {
			this.scoreRecue.put(receveur, quantitee);
		} else {
			// sinon incrémenter.
			this.scoreRecue.put(receveur, this.scoreRecue.get(receveur)+quantitee);
		}
		// si l'emetteur n'à encore rien émit le créer
		if (!this.scoreEnvoie.containsKey(emetteur)) {
			this.scoreEnvoie.put(emetteur, quantitee);
		} else {
			// sinon incrémenter.
			this.scoreEnvoie.put(emetteur, this.scoreEnvoie.get(emetteur)+quantitee);
		}
	}
	
	/**
	 * @brief methode pour savoir si le ratio d'un utilisateur est bon ou non.
	 * @param adresse l'adresse de l'utilisateur IP:PORT
	 * @return renvoie true sur le ratio de l'utilisateur est supérieur à 1,5 
	 * ou si celui-ci à télécharger moins de 1GO (pour laisser le télécharger à l'utilisateur 
	 * afin de partager par la suite. sinon renvoie false.
	 */
	public synchronized boolean ratioEstBon(String adresse) {
		boolean res;
		// récuperer les valeurs de l'utilisateur
		Long envoie = this.scoreEnvoie.get(adresse);
		if (envoie == null)
			envoie = 1L;
		Long recue = this.scoreRecue.get(adresse);
		if (recue == null)
			recue = 1L;
		// si son ration est supérieur à 1,5 ou s'il à téléchargé moins de 1Go alors son ration est bon.
		if ((envoie*1.0)/(recue*1.0) > this.ratioMin  ||  recue < 1000*1000 )
			return true;
		else {
			return false;
		}
	}
}
