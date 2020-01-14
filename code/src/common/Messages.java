package common;

/** @brief cette classe gère les messages d'information et d'erreurs
 */
public class Messages {
	
	private Messages() {}
	
	private static Messages instance = null;
	
	/**
	 * @brief singleton pour accéder à une instance unique de Messages
	 * @return retourne une instance unique de Messages
	 */
	public static Messages getInstance() {
		if (instance == null ) {
			instance = new Messages();
		}
		return instance;
	}
	
	/** @brief écrit un message d'information
	 * @param message message à écrire
	 */
	public void ecrireMessage(String message) {
		System.out.println(message);
	}

	/**
	 * @brief écrit un message d'erreur
	 * @param message message à écrire
	 */
	public void ecrireErreur(String message) {
		System.out.println("ERREUR : "+message);
	}
}
