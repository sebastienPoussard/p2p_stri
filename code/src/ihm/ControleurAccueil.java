package ihm;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ControleurAccueil extends ControleurFX{
	
	@FXML
	private TextField ip;
	
	@FXML
	private TextField port;
	
	@FXML
	private Button connexion;
	
	@FXML
	private Button reset;
	
	@FXML
	public void clicConnexion() {
		//connexion serveur
		
		//Redirection
		this.mainApp.showPage("telecharger");
	}

}
