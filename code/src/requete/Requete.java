package requete;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import commun.Messages;

/**
 * @brief classe abstraite définissant une requête d'un client au serveur.
 */
public abstract class Requete implements Runnable {
	
	protected String ipServeur;							// ip du serveur.
	protected int portServeur;							// port du serveur.
	protected BufferedInputStream inStream;				// flux de données entrant.
	protected BufferedOutputStream outStream;			// flux de données sortant.
	protected Socket socket;							// socket de connexion au serveur.
	protected ObjectOutputStream objOut;				// stream pour envoyer un object.
	protected ObjectInputStream objIn;					// stream pour recevoir un object.
	
	/**
	 * @brief constructeur de Requete.
	 * @param adresseServeur adresse et port du serveur de la forme "IP:PORT".
	 */
	public Requete(String adresseServeur) {
		String[] adresseEtPort = adresseServeur.split(":");
		this.ipServeur = adresseEtPort[0];
		this.portServeur = Integer.parseInt(adresseEtPort[1]);
	}

	/**
	 * @brief methode pour lancer le Thread.
	 */
	@Override
	public void run() {
		// se connecter au serveur.
		try {
			this.socket = new Socket(this.ipServeur, this.portServeur);
		} catch (UnknownHostException e) {
			Messages.getInstance().ecrireErreur("la connexion à "+this.ipServeur+":"+this.portServeur+" n'a pas pu être résolue.");
			return;
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("erreur à la connexion au serveur.");
			return;
		}
		// ouvrir les flux de données
		try {
			this.inStream = new BufferedInputStream(this.socket.getInputStream());
			this.outStream = new BufferedOutputStream(this.socket.getOutputStream());
			this.objOut = new ObjectOutputStream(this.socket.getOutputStream());
			this.objIn = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("Erreur à l'ouverture des flux de données avec le serveur");
			return;
		}
		// afficher un message de succés de connexion.
		Messages.getInstance().ecrireMessage("Connexion au serveur "+this.ipServeur+":"+this.portServeur+" avec succés.");
	}
	
	/**
	 * @brief envoie une requête au serveur.
	 * @param requete requete envoyé au serveur.
	 */
	protected void envoyerRequete(String requete) {
		try {
			this.outStream.write(requete.getBytes());
			this.outStream.flush();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("echec à l'envoie de la commande "+requete);
		}
	}
	
	/**
	 * @brief envoyer un objet au serveur.
	 * @param objet l'objet à envoyer au serveur.
	 */
	protected void envoyerObjet(Object objet) {
		try {
			this.objOut.writeObject(objet);
			this.objOut.flush();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("Echec à l'envoie de l'objet InfoUtilisateurs");
		}
	}
	
	/**
	 * @brief recevoir un objet de la part du client.
	 * @return renvoie l'objet reçue.
	 */
	protected Object recevoirObjet() {
		try {
			// essayer de recevoir l'objet.
			return this.objIn.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @brief fermer les differents éléments du Thread.
	 */
	protected void terminer() {
		try {
			this.socket.close();
			this.inStream.close();
			this.outStream.close();
			this.objIn.close();
			this.objOut.close();
		} catch (IOException e) {
			Messages.getInstance().ecrireErreur("echec à la fermeture du Thread");
		}
	}
}
