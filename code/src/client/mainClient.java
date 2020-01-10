package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import common.Messages;

public class mainClient {
	
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		
		// TEST DE CLIENT POUR VALIDER FONCTIONNEMENT DU SERVEUR
		
		Socket s = new Socket("localhost", 8080);
		BufferedOutputStream b = new BufferedOutputStream(s.getOutputStream());
		BufferedInputStream ib = new BufferedInputStream(s.getInputStream());
		
		b.write("LISTE".getBytes());
		b.flush();
		TimeUnit.SECONDS.sleep(3);
		
		byte[] donnee = new byte[1024];			// buffer de données pour stocker la requête client.
		int marqueur;							// marqueur de position dans le buffer "donnee".
		String resultat = "";
		
		// tant que la socket n'est pas fermée, tenter de lire la requête client.
		while ((marqueur = ib.read(donnee) )!= -1) {
			resultat += new String(donnee, 0, marqueur);
			// si le message est complétement lue, retourner le resultat.
			if (donnee[marqueur] == 0) {
				break;
			}
		}
		// si la connexion est rompue, fermer la socket.
		if (marqueur == -1) {
			s.close();
		}
		Messages.getInstance().ecrireMessage(resultat);
				

		s.close();
		
		// FIN TEST
		
	}
	

}


		
		
// CODE SAMPLE 
//		// variables
//		Socket soc;
//		ObjectInputStream inStream;
//		ObjectOutputStream outStream;
//		Scanner scanner = new Scanner(System.in);
//		String id, somme;
//		
//		
//		// tenter l'ouverture d'une socket
//		try {
//			// connecter la socket au serveur
//			System.out.println("Connexion au serveur ... ");
//			soc = new Socket("localhost", 8080);
//			System.out.println("Connexion réussie !");
//			// récuperer les streams de la socket
//			inStream = new ObjectInputStream(soc.getInputStream());
//			outStream = new ObjectOutputStream(soc.getOutputStream());
//			// traiter les message de l'utilisateur
//			while (true) {
//				System.out.println("Entrez votre commande :"
//						+ "1 : création d'un compte"
//						+ "2 : obtenir le solde d'un compte"
//						+ "3 : ajouter de l'argent sur un compte"
//						+ "4 : retirer de l'argent sur un compte");
//				int choix = scanner.nextInt();
//				switch (choix) {
//				case 1:
//					System.out.flush();
//					System.out.println("CREATION DE COMPTE \n");
//					System.out.print("entrez l'id du compte :");
//					id = scanner.nextLine();
//					System.out.print("entrez la somme du compte :");
//					somme = scanner.nextLine();
//					outStream.writeObject("CREATION "+id+" "+somme);
//					try {
//						Object o = inStream.readObject();
//						if (o instanceof String) {
//							System.out.println((String)o);
//						}
//					} catch (ClassNotFoundException e) {
//						e.printStackTrace();
//					}
//					
//					break;
//				case 2:
//					System.out.flush();
//					System.out.println("SOLDE DU COMPTE \n");
//					System.out.print("entrez l'id du compte :");
//					id = scanner.nextLine();
//					outStream.writeObject("POSITION "+id);
//					try {
//						Object o = inStream.readObject();
//						if (o instanceof String) {
//							System.out.println((String)o);
//						}
//					} catch (ClassNotFoundException e) {
//						e.printStackTrace();
//					}
//					
//					break;
//				case 3:
//					System.out.flush();
//					System.out.println("DEPOSER ARGENT \n");
//					System.out.print("entrez l'id du compte :");
//					id = scanner.nextLine();
//					System.out.print("entrez la somme à deposer :");
//					somme = scanner.nextLine();
//					outStream.writeObject("AJOUT "+id+" "+somme);
//					try {
//						Object o = inStream.readObject();
//						if (o instanceof String) {
//							System.out.println((String)o);
//						}
//					} catch (ClassNotFoundException e) {
//						e.printStackTrace();
//					}
//					
//					break;
//				case 4:
//					System.out.flush();
//					System.out.println("RETIRER DE L'ARGENT \n");
//					System.out.print("entrez l'id du compte :");
//					id = scanner.nextLine();
//					System.out.print("entrez la somme à débiter:");
//					somme = scanner.nextLine();
//					outStream.writeObject("CREATION "+id+" "+somme);
//					try {
//						Object o = inStream.readObject();
//						if (o instanceof String) {
//							System.out.println((String)o);
//						}
//					} catch (ClassNotFoundException e) {
//						e.printStackTrace();
//					}
//					break;
//				default:
//					System.out.flush();
//				}
//			}
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
