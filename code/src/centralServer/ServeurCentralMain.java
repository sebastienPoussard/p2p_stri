package centralServer;

public class ServeurCentralMain {

	public static void main(String[] args) {

		// définir le port.
		int port = 9090;
		// lancer le serveur central.
		ServeurCentral serveurCentral = new ServeurCentral(port);
		serveurCentral.lancer();
	}

}
