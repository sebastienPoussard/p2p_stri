package commun;

import java.util.HashMap;

public class InfoUtilisateur {

	private String ip;
	private int port;
	private HashMap<String, ListeDeBlocs> fichiersPartages;
	
	public InfoUtilisateur(String ip, int port) {
		this.ip = ip;
		this.port = port;
		this.fichiersPartages = new HashMap<String, ListeDeBlocs>();
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}
	
	public void ajouterFichier(String nomFichier, ListeDeBlocs listeDeBlocs) {
		this.fichiersPartages.put(nomFichier, listeDeBlocs);
	}
	
	public boolean detientLeFichier(String nomFichier) {
		return this.fichiersPartages.containsKey(nomFichier);
	}
	
	public ListeDeBlocs blocDuFichier(String nomFichier) {
		return this.fichiersPartages.get(nomFichier);
	}
}
