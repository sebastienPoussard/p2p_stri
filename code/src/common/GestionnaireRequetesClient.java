package common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class GestionnaireRequetesClient implements Runnable {

	private String ipServeur;
	private int PortServeur;
	private String nomFichier;
	private String blocDebut;
	private String blocFin;
	private Socket socket;
	private BufferedInputStream streamIn;
	private BufferedOutputStream streamOut;
	private Object gestionnaireFichier;
	
	
	public GestionnaireRequetesClient(String ipServeur, int portServeur, String nomFichier, String blocDebut,
			String blocFin, GestionnaireFichier gestionnaireDeFichier) {
		this.ipServeur = ipServeur;
		this.PortServeur = portServeur;
		this.nomFichier = nomFichier;
		this.blocDebut = blocDebut;
		this.blocFin = blocFin;
		this.gestionnaireFichier = gestionnaireFichier;
	}
	
	@Override
	public void run() {
		try {
			this.socket = new Socket(this.ipServeur, this.PortServeur);
			this.streamIn = new BufferedInputStream(this.socket.getInputStream());
			this.streamOut = new BufferedOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("echec à l'ouverture de la connexion au serveur "+this.ipServeur+":"+this.PortServeur);
			return;
		}
		telechargerBloc(this.nomFichier, this.blocDebut, this.blocFin);
	}

	private void telechargerBloc(String nomFichier, String blocDebut, String blocFin) {
		Messages.getInstance().ecrireMessage("Téléchargement du fichier : \""+nomFichier+"\""
				+ " bloc "+blocDebut+" à bloc "+blocFin+" sur le serveur "+this.ipServeur+":"+this.PortServeur+" ...");
		// envoyer l'instruction au serveur.
		try {
			this.streamOut.write(("DL "+nomFichier+" "+debut+" "+fin).getBytes());
			this.streamOut.flush();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("echec à l'envoie de la commande DL");
		}
		// créer le fichier en local.
		FileOutputStream streamFichier = null;
		try {
			streamFichier = this.gestionnaireFichier.creerFichier(nomFichier);
		} catch (FileNotFoundException e) {
			Messages.getInstance().ecrireErreur("erreur à la création du fichier dans le dossier téléchargement "+nomFichier);
		}
		// télécharger le fichier.
		int marqueur;						// marqueur de positon dans le buffer de "données".
		byte[] donnee = new byte[1024];		// buffer de données pour stocker la réponse du serveur.
		try {
			while ((marqueur = this.streamIn.read(donnee)) > 0) {
				streamFichier.write(donnee, 0, marqueur);
				// si c'est le dernier bloc, sortir de la boucle
				if (marqueur < 1024) {
					break;
				}
			}
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("erreur pendant le téléchargement du fichier.");
		}
		try {
			streamFichier.close();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("le fichier télécharger n'as pas été correctement fermé.");
		}
		Messages.getInstance().ecrireMessage("Téléchargement de "+nomFichier+" terminé !");
		
	}

}
