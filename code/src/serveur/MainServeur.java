package serveur;
import java.io.IOException;

import common.Messages;
import common.Serveur;

/**
 * @brief Classe qui permet l'execution du serveur de transfert de fichiers.
 */
public class MainServeur {

	public static void main(String[] args) {
		
		// récuperer le port.
		int port = 8080;
		// récuperer le dossier l'adresse du dossier contenant les fichiers partagés.
		String adresseDossierPartage = "/tmp/share";
		// lancer le serveur.
		Serveur serveur = new Serveur(port, adresseDossierPartage);
		try {
			serveur.lancer();
		} catch (IOException e) {
			e.printStackTrace();
			Messages.getInstance().ecrireErreur("Echec du lancement du serveur, le port est peut être déjà utilisé");
		}
		

		
// CODE SAMPLE 

//						try {
//							// lire la requête envoyée par le client
//							Object o = inStream.readObject();
//							if (o instanceof String ) {
//								buffer = (String)o;
//								// séparer en differentes parties 
//								String[] cmd = buffer.split(" ");
//								String resultat;
//								switch (cmd[0]) {
//								case "CREATION":
//									// creation d'un compte à partir d'un id et d'une somme initiale
//									if (banque.existe(Integer.parseInt(cmd[1]))) {
//										banque.creation(Integer.parseInt(cmd[1]), Float.parseFloat(cmd[2]));
//										resultat = "OK";
//									} else {
//										resultat = "ERREUR id inexistant";
//									}
//									break;
//								case "POSITION":
//									// renvoie la somme du compte identifié par id
//									if (banque.existe(Integer.parseInt(cmd[1]))) {
//										resultat = "OK " + Float.toString(banque.position(Integer.parseInt(cmd[1])));
//									} else {
//										resultat = "ERREUR id inexistant";
//									}
//									break;
//								case "AJOUT":
//									if (banque.existe(Integer.parseInt(cmd[1]))) {
//										banque.ajout(Integer.parseInt(cmd[1]), Float.parseFloat(cmd[2]));
//										resultat = "OK";
//									} else {
//										resultat = "ERREUR id inexistant";
//									}
//									break;
//								case "RETRAIT":
//									if (banque.existe(Integer.parseInt(cmd[1]))) {
//										banque.retrait(Integer.parseInt(cmd[1]), Float.parseFloat(cmd[2]));
//										resultat = "OK";
//									} else {
//										resultat = "ERREUR id inexistant";
//									}
//									break;
//								default:
//									resultat = "ERREUR commande invalide";
//								}
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			socRDV.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
