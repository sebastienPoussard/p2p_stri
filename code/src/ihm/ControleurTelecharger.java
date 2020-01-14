package ihm;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ControleurTelecharger extends ControleurFX{

	@FXML
	private Button deconnexion;
	
	@FXML
	public void clicDeconnexion() {
		//Deconnexion serveur
		
		//redirection
		this.retourPageAccueil();
	}
	
}
