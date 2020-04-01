package requete;


/**
 * @brief Cette classe permet d'envoyer un message à un serveur
 *
 */
public class RequeteEnvoieMessage extends Requete {

	private String message;			// message à envoyer au serveur
	
	/**
	 * @brief constructeur de la classe
	 * @param adresseServeur adresse du serveur à qui on envoie le message.
	 * @param message message sous forme d'une chaine de cractère à envoyer.
	 */
	public RequeteEnvoieMessage(String adresseServeur, String message) {
		super(adresseServeur);
		this.message = message;
	}

	/**
	 * @brief methode pour lancer le Thread.
	 */
	@Override
	public void run() {
		// connecter au serveur et récuperer les fluxs.
		super.run();
		// envoyer la requête pour lister les fichiers.
		envoyerRequete(message);
		// fermer le Thread.
		terminer();
	}
	
}
