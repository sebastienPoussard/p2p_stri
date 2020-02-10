package requete;

import commun.InfoUtilisateur;

public class RequeteUpdateInfosUtilisateur extends Requete {
	
	InfoUtilisateur infos;				// infos de l'utilisateur à envoyer au serveur central
	
	/**
	 * @brief cette classe envoie les infos utilisateur au serveur central.
	 * @param adresseServeur
	 * @param infos
	 */
	public RequeteUpdateInfosUtilisateur(String adresseServeur, InfoUtilisateur infos) {
		super(adresseServeur);
		this.infos = infos;
	}
	
	@Override
	public void run() {
		super.run();
		this.envoyerObjet(infos);
	}

}
