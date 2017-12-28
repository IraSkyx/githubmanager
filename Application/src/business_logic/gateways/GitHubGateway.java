package business_logic.gateways;

import business_logic.repository.GitHubRepositoryFactory;
import business_logic.repository.Repository;
import controller.FrontController;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import launch.Main;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.RepositoryService;

/**
 * Gateway to GitHub Java API
 * @author Adrien
 */
public class GitHubGateway implements APIManager {
    
    public static final String OAUTH2TOKEN = "5492d1456e37ba89cc8985ccdee4a8dc028e916f";
    
    @Override
    public ObservableList<Repository> getRepositoriesByName(String input){
        ObservableList<Repository> list = FXCollections.observableArrayList();
        
        try {                  
            RepositoryService service = new RepositoryService();
            service.getClient().setOAuth2Token(OAUTH2TOKEN);
            
            List<org.eclipse.egit.github.core.SearchRepository> search =  service.searchRepositories(input);
            
            search.stream().forEach(x -> {
                try {
                    org.eclipse.egit.github.core.Repository repo = service.getRepository(RepositoryId.createFromId(x.generateId()));
                    list.add(GitHubRepositoryFactory.make(repo));
                } 
                catch (IOException ex) {
                    Logger.getLogger(GitHubGateway.class.getName()).log(Level.SEVERE, null, ex);
                }           
            });                       
        } 
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }
    
    @Override
    public ObservableList<Repository> getRepositoriesByUsername(String input){
        ObservableList<Repository> list = FXCollections.observableArrayList();
        
        try {               
            RepositoryService service = new RepositoryService();
            service.getClient().setOAuth2Token(OAUTH2TOKEN);
            
            service.getRepositories(input).stream().forEach(x -> {
                list.add(GitHubRepositoryFactory.make(x));
            });   
        } 
        catch (IOException ex) {
            return null;
        }
        
        return list;
    }
    
    public static boolean hasNewCommit(Repository repository){
        try {               
            RepositoryService service = new RepositoryService();
            service.getClient().setOAuth2Token(OAUTH2TOKEN);
            
            org.eclipse.egit.github.core.Repository gitRepository = service.getRepository(RepositoryId.createFromId(repository.getId()));
            if(gitRepository.getUpdatedAt().compareTo(repository.getUpdatedAt()) > 0)
                return true;
            return false;
        } 
        catch (IOException ex) {
            return false;
        }
    }
    
    public static String getReadMe(org.eclipse.egit.github.core.Repository repo){
        try {
            ContentsService contents = new ContentsService();
            contents.getClient().setOAuth2Token(OAUTH2TOKEN);                   
            RepositoryContents readMe = contents.getReadme(RepositoryId.createFromId(repo.generateId()));
            return decode(readMe.getContent());
        } 
        catch (Exception ex) {
            return "No README.md";
        }       
    }
    
    private static String decode(String value) {
        try{
            byte[] decodedValue = Base64.getMimeDecoder().decode(value);
            return new String(decodedValue, StandardCharsets.UTF_8);
        }
        catch(Exception ex){
          return "Failed to decode README.md";
        }
    }
    
    public static void cloneRepository(Repository repo) throws IOException {       
        final URL uri = new URL("https://api.github.com/repos/".concat(repo.getId()).concat("/zipball"));
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose destination");
        final File selectedDir = dirChooser.showDialog(FrontController.getScene().getWindow());   
        if(selectedDir != null){
            File zipDestination = new File(selectedDir.getAbsolutePath() + "/" + repo.getName() + ".zip");
            FileUtils.copyURLToFile(uri, zipDestination);           

            new Thread(() -> {
                try {
                    ZipFile zipFile = new ZipFile(zipDestination);
                    zipFile.extractAll(selectedDir.getAbsolutePath()+"/"+repo.getName());
                    FileUtils.forceDelete(zipDestination);
                }
                catch (net.lingala.zip4j.exception.ZipException | IOException ex ) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }).start();    
        }          
    }  
}
