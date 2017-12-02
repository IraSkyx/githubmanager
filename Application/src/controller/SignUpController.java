package controller;

import business_logic.User;
import business_logic.UsersManager;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.IllegalFormatException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author fsimo
 */

public class SignUpController {
    
    @FXML TextField username;
    
    @FXML TextField email;
    
    @FXML TextField password;
    
    @FXML TextField verifpassword;
    
    @FXML Label error;
    
    @FXML 
    private void GoToHome() throws IOException {   
        FrontController.setScene((BorderPane)FXMLLoader.load(getClass().getResource("/ihm/Home.fxml")));
    }
    
    @FXML 
    private void SignUp() throws IOException {  
        try {      
            User newUser = UsersManager.signUp(username.getText(), email.getText(), password.getText(), verifpassword.getText());
            UsersManager.connect(newUser.getEmail(), newUser.getPassword());
            FrontController.setScene((BorderPane)FXMLLoader.load(getClass().getResource("/ihm/OnlineMode.fxml")));
        }
        catch(InvalidParameterException ex) {
            error.setText(ex.getMessage());
            error.setVisible(true);
        }
    }
}
