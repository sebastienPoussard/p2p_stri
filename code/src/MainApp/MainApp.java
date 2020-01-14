package MainApp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ihm.ControleurAccueil;
import ihm.ControleurErreur;
import ihm.ControleurFX;
import ihm.ControleurReussie;
import ihm.ControleurTelecharger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private Map<String, String> page = new HashMap<String, String>();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			this.primaryStage.setTitle("STRI Torrent");
			
			this.page.put("accueil", "/ihm/accueil.fxml");
			this.page.put("telecharger", "/ihm/telecharger.fxml");
			this.page.put("erreur", "/ihm/erreur.fxml");
			this.page.put("reussie", "/ihm/reussie.fxml");
			
			
			showPage("accueil");	

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// charge page primaire
	public void showPage(String titre_page) {
		try {
			// cree loader qui va permettre de charger les pages
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource(this.page.get(titre_page)));
			AnchorPane personOverview = (AnchorPane) loader.load();
			ControleurFX controleur;
			
			switch(titre_page) {
				case "accueil":
					controleur = new ControleurAccueil();
					break;
				case "telecharger":
					controleur = new ControleurTelecharger();
					break;	
				default:
					System.out.println("** ERREUR ** : Mauvaise Page");
					break;	
			}
			
			controleur = loader.getController();
			
			// on charge la mainApp depuis le controleur de la page demande
			controleur.setMainApp(this);

			Scene scene = new Scene(personOverview);
			// on charge la scene dans le primaryStage
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// charge page pop-up
		public void showPagePopUp(String titre_page) {
			try {
				// cree loader qui va permettre de charger les pages
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(MainApp.class.getResource(this.page.get(titre_page)));
				AnchorPane personOverview = (AnchorPane) loader.load();
				ControleurFX controleur;
				
				Stage fenetrePopUp = new Stage();
				fenetrePopUp.initOwner(primaryStage);
				
				switch(titre_page) {
					case "erreur":
						controleur = new ControleurErreur();
						fenetrePopUp.setTitle("Erreur");
						break;
					case "reussie":
						controleur = new ControleurReussie();
						fenetrePopUp.setTitle("Well done !");
						break;
					default:
						System.out.println("** ERREUR ** : Mauvaise Page");
						break;	
				}
				
				controleur = loader.getController();
				
				// on charge la mainApp depuis le controleur de la page demande
				controleur.setFenetre(fenetrePopUp);
				controleur.setMainApp(this);
				
				Scene scene = new Scene(personOverview);
				// on charge la scene dans le primaryStage
				fenetrePopUp.setScene(scene);
				fenetrePopUp.show();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	public static void main(String[] args) {
		launch(args);
	}
}
