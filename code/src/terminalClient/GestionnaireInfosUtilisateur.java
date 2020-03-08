package terminalClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

import commun.InfoUtilisateur;
import commun.Messages;
import requete.RequeteUpdateInfosUtilisateur;

/**
 * @brief cette classe va permettre d'envoyer régulierement des infos concenrnant l'utilisateur au serveur central. 
 */
public class GestionnaireInfosUtilisateur implements Runnable{

	GestionnaireFichier gestionnaireFichier;			// le gestionnaire de fichier.
	InfoUtilisateur infos;								// les infos sur l'utilisateur.
	String adresseServeurCentral;						// l'adresse et le port du serveur central.
	int port;											// port d'écoute du serveur.
	
	/**
	 * @brief constructeur de la classe.
	 * @param gestionnaireFichier le gestionnaire de fichier.
	 * @param ipServeurCentral l'ip du serveur central.
	 * @param portServeurCentral le port du serveur central.
	 * @param port d'écoute de la fonctionnalité serveur du client.
	 */
	public GestionnaireInfosUtilisateur(GestionnaireFichier gestionnaireFichier, 
			String ipServeurCentral, int portServeurCentral, int port) {
		this.gestionnaireFichier = gestionnaireFichier;
		this.adresseServeurCentral = ipServeurCentral+":"+String.valueOf(portServeurCentral);
		this.port = port;
	}
	
	/**
	 * @brief méthode pour lancer le Thread.
	 */
	@Override
	public void run() {
		while(true) {
			// créer les information sur l'utilisateur.
			try {
				String ip = InetAddress.getLocalHost().toString().split("/")[1];
				this.infos = this.gestionnaireFichier.creerInfosUtilisateur(ip, this.port);
			} catch (UnknownHostException e) {
				Messages.getInstance().ecrireErreur("impossible de résoudre l'adresse de la machine...");
			}
			// envoyer les infos de l'utilisateur au serveur central.
			RequeteUpdateInfosUtilisateur requete = new RequeteUpdateInfosUtilisateur(this.adresseServeurCentral, this.infos);
			Thread requeteUpdateInfos = new Thread(requete);
			requeteUpdateInfos.start();
			// Attendre un peu pr renvoyer l'update
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

}
