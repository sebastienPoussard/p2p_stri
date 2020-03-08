package requete;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import commun.Messages;

/**
 * 
 * @brief Cette classe permet de créer un Thread qui va demander à un serveur la liste des fichiers qu'il contient.
 */
public class RequeteListe extends Requete {

	/**
	 * @brief constructeur de la classe RequeteListe
	 * @param adresseServeur adresse et port du serveur de la forme <IP>:<PORT>.
	 */
	public RequeteListe(String adresseServeur) {
		super(adresseServeur);
	}

	/**
	 * @brief methode pour lancer le Thread.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void run() {
		// connecter au serveur et récuperer les fluxs.
		super.run();
		// envoyer la requête pour lister les fichiers.
		envoyerRequete("LISTE");
		// lire la réponse du serveur.
		String reponse = "";
		Object obj;
		try {
			obj = this.objIn.readObject();
			HashMap<String, Long> listeDesFichiersComplets = (HashMap<String, Long>)obj;
			Iterator iterateur = listeDesFichiersComplets.entrySet().iterator();
			while (iterateur.hasNext()) {
				Map.Entry fichier = (Entry)iterateur.next();
				reponse += fichier.getKey()+" ("+fichier.getValue()+")\n";
			}
			Messages.getInstance().ecrireMessage(reponse);
		} catch (IOException | ClassNotFoundException e) {
			Messages.getInstance().ecrireErreur("echec à la reception de la liste des fichiers");
		}
		// fermer le Thread.
		terminer();
	}

}
