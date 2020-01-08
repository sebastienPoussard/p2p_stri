package common;

/** @brief cette classe gère les messages d'information et d'erreurs
 */
public class Messages {
	
	/** @brief écrit un message d'information
	 * @param message message à écrire
	 */
	public static void ecrireMessage(String message) {
		System.out.println(message);
	}

	/**
	 * @brief écrit un message d'erreur
	 * @param message message à écrire
	 */
	public static void ecrireErreur(String message) {
		System.out.println("ERREUR : "+message);
	}
}
