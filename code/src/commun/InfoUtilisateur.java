package commun;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @brief cette classe permet de stocker les informations sur un utilisateur (ip, port, liste des fichiers
 * qu'il possède et quel parties de ces fichiers il possède)
 */
public class InfoUtilisateur implements Serializable {

	private static final long serialVersionUID = 7843530783617937511L;
	private String ip;											// ip de l'utilisateur.
	private int port;											// port d'écoute de l'utilisateur.
	private HashMap<String, ListeDeBlocs> fichiersPartages;		// liste des fichiers et des blocs que l'utilisateur à.
	private HashMap<String , Long> listeDesFichiersComplets;	// liste des fichiers complets de l'utilisateur.
	
	/**
	 * @brief constructeur de la classe.
	 * @param ip ip de l'utilisateur
	 * @param port	port d'écoute de l'utilisateur.
	 */
	public InfoUtilisateur(String ip, int port) {
		this.ip = ip;
		this.port = port;
		this.fichiersPartages = new HashMap<String, ListeDeBlocs>();
		this.listeDesFichiersComplets = new HashMap<String, Long>();
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
	 * @brief permet de récuperer la liste des fichiers complets de l'utilisateur.
	 * @return renvoie la liste des fichiers complets de l'utilisateur.
	 */
	public HashMap<String, Long> obtenirLaListeDesFichiersComplets() {
		return this.listeDesFichiersComplets;
	}
	
	/**
	 * @brief ajoute un fichier et la liste des blocs de ce fichier que l'utilisateur possede.
	 * @param nomFichier le nom du fichier.
	 * @param listeDeBlocs la liste des blocs du fichier.
	 */
	public void ajouterFichier(String nomFichier, ListeDeBlocs listeDeBlocs) {
		// si la liste de blocs contient la taille, alors le fichier est complet à la liste.
		long tailleDuFichier = listeDeBlocs.obtenirTailleDuFichier();
		if (tailleDuFichier != -1) {
			this.listeDesFichiersComplets.put(nomFichier, tailleDuFichier);
		}
		// dans tous les cas, ajouter la liste de bloc.
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
	
	/**
	 * @brief methode pour obtenir une description de l'objet l'info utilisateur 
	 * principalement utilisé pour debuger et pour afficher des informations.
	 */
	@Override
	public String toString() {
		String res = "***********************************************\n";
		res += "IP   : "+this.ip+"\n";
		res += "PORT : "+this.port+"\n";
		res += "\n";
		Iterator<Map.Entry<String, ListeDeBlocs>> iterateur = fichiersPartages.entrySet().iterator();
		while (iterateur.hasNext()) {
		    Map.Entry<String, ListeDeBlocs> curseur = iterateur.next();
		    res+= "nom du fichier = " + curseur.getKey();
		    if (curseur.getValue().obtenirTailleDuFichier() == -1) {
		    	 res+= "\nblocs = " + curseur.getValue().toString()+"\n";
		    } else {
		    	res+="\ntaille du fichier = " + curseur.getValue().obtenirTailleDuFichier()+"\n";
		    }
		}
		res+= "***********************************************\n";
		return res;
	}
}
