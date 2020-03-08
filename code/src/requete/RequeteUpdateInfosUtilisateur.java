package requete;

import commun.InfoUtilisateur;

/**
 *	@brief cette classe permet d'envoyer au serveur central les infos concernant le client.
 */
public class RequeteUpdateInfosUtilisateur extends Requete {
	
	InfoUtilisateur infos;				// infos de l'utilisateur à envoyer au serveur central
	
	/**
	 * @brief constructeur de la classe.
	 * @param adresseServeur l'adresse du serveur central, format <IP>:<PORT> .
	 * @param infos les infos de l'utilisateur à envoyer au serveur central.
	 */
	public RequeteUpdateInfosUtilisateur(String adresseServeurCentral, InfoUtilisateur infos) {
		super(adresseServeurCentral);
		this.infos = infos;
	}
	
	/**
	 * @brief méthode pour lancer le Thread.
	 */
	@Override
	public void run() {
		super.run();
		// envoyer la requete pour signaler que l'on va envoyer les infos.
		this.envoyerRequete("UPDATE");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// envoyer les infos utilisateurs.
		this.envoyerObjet(infos);
		// fermer la connexion
		this.terminer();
	}

}
