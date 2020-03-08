package commun;

/** @brief cette classe gère les messages d'information et d'erreurs
 */
@SuppressWarnings("unused")
public class Messages {
	
	// flags pour colorer le terminal.
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_BLACK = "\u001B[30m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_PURPLE = "\u001B[35m";
	private static final String ANSI_CYAN = "\u001B[36m";
	private static final String ANSI_WHITE = "\u001B[37m";
	
	private static Messages instance = null;		// instance du singleton.
	
	/**
	 * @brief singleton pour accéder à une instance unique de ListeDesFichiersComplets
	 * @return retourne une instance unique de ListeDesFichiersComplets
	 */
	public static Messages getInstance() {
		if (instance == null ) {
			instance = new Messages();
		}
		return instance;
	}
	
	/** 
	 * @brief écrit un message d'information
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
		System.out.println(ANSI_RED+"ERREUR : "+message+ANSI_RESET);
	}
}
