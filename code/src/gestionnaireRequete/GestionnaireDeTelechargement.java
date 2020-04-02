package gestionnaireRequete;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import commun.ListeDeBlocs;
import commun.Messages;
import requete.Requete;
import requete.RequeteTelecharger;
import terminalClient.GestionnaireFichier;

/**
 * @brief cette classe va gérer les téléchargements d'un fichier depuis un ou plusieurs autres clients.
 */
public class GestionnaireDeTelechargement extends Requete {

	private String nomFichier;													// nom du fichier à télécharger.
	private GestionnaireFichier gestionnaireFichier;							// le gestionnaire de fichier.
	private HashMap<String, ListeDeBlocs> listeDesUtilisateursAyantLeFichier;	// la liste des utilisateur possédant le fichier.
	private long tailleDuFichier;												// la taille du fichier.
	private int nbrDeBlocs;														// le nombre de blocs composant le fichier.
	private RandomAccessFile file;												// le fichier sur le disque.
	private ArrayList<Thread> listeDesThreads;									// la liste des thread qui vont télécharger des données.
	private String adresseServeurCentral;										// adresse du serveur central.
	private int portServeur;													// port du serveur de l'utilisateur.
	
	/**
	 * @brief constructeur de la classe.
	 * @param adresseServeurCentral adresse du serveur central et son port en format <IP>:<PORT>.
	 * @param nomFichier nom du fichier à télécharger.
	 * @param gestionnaireFichier le gestionnaire de fichier.
	 */
	public GestionnaireDeTelechargement(String adresseServeurCentral, String nomFichier, 
			GestionnaireFichier gestionnaireFichier, int portServeur) {
		super(adresseServeurCentral);
		this.adresseServeurCentral = adresseServeurCentral;
		this.nomFichier = nomFichier;
		this.gestionnaireFichier = gestionnaireFichier;
		this.portServeur = portServeur;
		this.listeDesThreads = new ArrayList<Thread>();
	}
	
	/**
	 * @brief methode pour lancer le Thread.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void run() {
		// connecter au serveur central et récuperer les fluxs.
		super.run();
		// envoyer la requête au serveur pour obtenir la listes des utilisateurs et les blocs qu'ils possèdent
		// du fichier à télécharger.
		envoyerRequete("DOWNLOAD "+this.portServeur+" "+this.nomFichier);
		// lire la réponse du serveur central.
		Object obj;
		boolean continuer = true;
		try {
			obj = this.objIn.readObject();
			String reponse = (String)obj;
			if (reponse.equals("KO!")) {
				continuer = false;
				Messages.getInstance().ecrireMessage("Votre ration n'est pas suffisant pour télécharger, vilain!");
			} else {
				this.listeDesUtilisateursAyantLeFichier = (HashMap<String, ListeDeBlocs>)obj;
			}
		} catch (IOException | ClassNotFoundException e) {
			Messages.getInstance().ecrireErreur("echec à la reception de la liste de la liste des utilisateurs ayant le fichier.");
		}
		if (continuer) {
			// construire le fichier "vide" dans le dossier des fichiers incomplets et récuperer le flux.
			try {
				this.tailleDuFichier = determinerTailleDuFichier(this.listeDesUtilisateursAyantLeFichier);
				this.file = this.gestionnaireFichier.creerFichier(this.nomFichier, this.tailleDuFichier);
			} catch (FileNotFoundException e) {
				Messages.getInstance().ecrireErreur("echec à la création du fichier vide "+this.nomFichier);
			}
			// calculer le nbr de blocs.
			float nombreDeBlocsFlottant = (this.tailleDuFichier/((float)GestionnaireFichier.TAILLEDEBLOC));
			this.nbrDeBlocs = (int) Math.ceil(nombreDeBlocsFlottant);
			// tenter de telecharger tous les blocs.
			int numeroBloc = 1;
			while (numeroBloc <= this.nbrDeBlocs) {
				boolean tourPourRien = true;
				Iterator iterateur = this.listeDesUtilisateursAyantLeFichier.entrySet().iterator();
				while (iterateur.hasNext()) {
					Map.Entry utilisateur = (Entry)iterateur.next();
					ListeDeBlocs blocs = (ListeDeBlocs) utilisateur.getValue();
					if (blocs.detientLeBloc(numeroBloc)) {
						// telecharger chez la personne le bloc
						String adresse = (String) utilisateur.getKey();
						RequeteTelecharger requete = new RequeteTelecharger(this.adresseServeurCentral, adresse, this.file, this.nomFichier, this.gestionnaireFichier, numeroBloc, this.portServeur);
						Thread thread = new Thread(requete);
						this.listeDesThreads.add(thread);
						thread.start();
						// incrémenter le bloc à télécharger
						numeroBloc++;
						tourPourRien = false;
					}
				}
				// si le bloc n'est disponible chez personne, le sauter.
				if (tourPourRien == true) {
					numeroBloc++;
				}
			}
			// attendre que tous les threads de téléchargement ce terminent.
			for (Thread thread : this.listeDesThreads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					Messages.getInstance().ecrireErreur("problème à l'attente de la terminaison des threads fils");
				}
			}
			// tester le fichier pour voir s'il est complet et dans ce cas le déplacer dans les fichier complets.
			this.gestionnaireFichier.verifierIntegritee(this.nomFichier);
		}
		// fermer le Thread.
		terminer();
	}
	
	/**
	 * @brief methode pour obtenir la taille du fichier.
	 * @param liste la liste des utilisateurs ayant le fichier.
	 * @return renvoie la taille du fichier final.
	 */
	@SuppressWarnings("rawtypes")
	private long determinerTailleDuFichier(HashMap<String, ListeDeBlocs> liste) {
		long res = 0;
		Iterator iterateur = liste.entrySet().iterator();
		while (iterateur.hasNext()) {
			Map.Entry utilisateur = (Entry)iterateur.next();
			// obtenir la liste de bloc de l'utilisateur.
			ListeDeBlocs blocs  = (ListeDeBlocs) utilisateur.getValue();
			// récuperer la taille du fichier
			if (blocs.obtenirTailleDuFichier() != -1) {
				res = blocs.obtenirTailleDuFichier();
				break;
			}
		}
		return res;
	}
}
