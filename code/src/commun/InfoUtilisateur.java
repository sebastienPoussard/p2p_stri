package commun;

import java.util.HashMap;

public class InfoUtilisateur {

	private String ip;											// ip de l'utilisateur.
	private int port;											// port d'écoute de l'utilisateur.
	private HashMap<String, ListeDeBlocs> fichiersPartages;		// liste des fichiers et des blocs que l'utilisateur à.
	
	/**
	 * @brief constructeur de la classe.
	 * @param ip ip de l'utilisateur
	 * @param port	port d'écoute de l'utilisateur.
	 */
	public InfoUtilisateur(String ip, int port) {
		this.ip = ip;
		this.port = port;
		this.fichiersPartages = new HashMap<String, ListeDeBlocs>();
	}

	/**
	 * @brief permet d'obtenir l'ip de l'utilisateur.
	 * @return retourne l'ip de l'utilisateur.
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @brief permet d'obtenir le port d'écoute de l'utilisateur.
	 * @return retourne le port d'écoute de l'utilisateur.
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * @brief ajoute un fichier et la liste des blocs de ce fichier que l'utilisateur possede.
	 * @param nomFichier le nom du fichier.
	 * @param listeDeBlocs la liste des blocs du fichier.
	 */
	public void ajouterFichier(String nomFichier, ListeDeBlocs listeDeBlocs) {
		this.fichiersPartages.put(nomFichier, listeDeBlocs);
	}
	
	/**
	 * @brief permet de savoir si l'utilisateur detient un fichier spécifique.
	 * @param nomFichier le nom du fichier dont on veut savoir si l'utilisateur à une copie.
	 * @return renvoie true si l'utilisateur posséde le fichier, sinon renvoie false.
	 */
	public boolean detientLeFichier(String nomFichier) {
		return this.fichiersPartages.containsKey(nomFichier);
	}
	
	/**
	 * @brief permet d'obtenir la liste de bloc d'un fichier que l'utilisateur posséde.
	 * @param nomFichier le nom du fichier dont on souhaite obtenir la liste de blocs.
	 * @return renvoie la liste de blocs du fichier de l'utilisateur.
	 */
	public ListeDeBlocs blocDuFichier(String nomFichier) {
		return this.fichiersPartages.get(nomFichier);
	}
}
