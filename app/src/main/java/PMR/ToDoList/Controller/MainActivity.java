package PMR.ToDoList.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import PMR.ToDoList.Model.User;
import PMR.ToDoList.R;

public class MainActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    //GESTION DE LA TOOLBAR
    private androidx.appcompat.widget.Toolbar toolbar;

    //GESTION DES INFORMATIONS DE CONNEXION
    private EditText edtPseudo;
    private TextView txtPseudo;

    private EditText edtMdp;
    private TextView txtMdp;

    private Button btnConnexion;

    //LISTE DES UTILISATEURS ENREGISTRÉS DANS LA BASE DE DONNÉES
    public static ArrayList<User> myUsersList;
    private TextView tvUrlApi;
    public static String urlApi;

    //UTILISATEUR INITIANT LA CONNEXION
    private User myUser;
    private String pseudo;
    private String password;

    //GESTION DE LA CONNEXION A INTERNET
    private NetworkStateReceiver networkStateReceiver;
    private TextView etatConnexion;
    private Boolean connexionOk;
    private TextView erreurConnexion;

    //GESTION DES INFORMATIONS A ENREGISTRER
    public static final String EXTRA_LOGIN = "LOGIN";
    public static final String EXTRA_CONNEXIONOK = "CONNEXIONOK";


    // METHODE POUR LES TOASTS
    public void alerter(String s) {
        Toast myToast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        myToast.show();
    }

    /*
    ON CREATE
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myUsersList = getUsersFromFile();


        try {
            urlApi= getUrlApiFromJson();
        } catch (IOException e) {
            e.printStackTrace();
            urlApi= "http://tomnab.fr/todo-api/";
            sauvegarderUrlApiToJsonFile(urlApi);
        }

        //AJOUT DES INFORMATIONS DE LA TOOLBAR
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Connexion");


        // GESTION DE LA CONNEXION
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        etatConnexion= findViewById(R.id.etatConnexion);
        erreurConnexion = findViewById(R.id.erreurConnexion);
        erreurConnexion.setText("");

        //BIND DES VIEWS POUR LA CONNEXION
        edtPseudo = findViewById(R.id.edtPseudo);
        btnConnexion = findViewById(R.id.btnPseudo);
        txtPseudo = findViewById(R.id.txtPseudo);
        edtMdp = findViewById(R.id.edtMdp);
        txtMdp = findViewById(R.id.txtMdp);

        //LISTENER SUR LE BOUTON DE CONNEXION
        btnConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pour pouvoir appuyer sur OK, on doit avoir les deux champs mot de passe et
                //login remplis
                if ((edtPseudo.getText().toString().matches("")) |
                        (edtMdp.getText().toString().matches(""))) {
                    alerter("Entrez un pseudo et un mot de passe");

                }
                else{

                    // ON ENREGISTRE LES PSEUDOS ET PASSWORD UTILISES
                    pseudo = edtPseudo.getText().toString();
                    password = edtMdp.getText().toString();

                    if (connexionOk){
                        AsyncTask task = new PostAsyncTask();
                        task.execute();
                    }

                    else {
                        // Si on a pas de connexion, on regarde si l'utilisateur
                        //correspondant à ce pseudo/password est enregistré
                        String hashTemporaire="";
                        myUser=new User(pseudo,password,hashTemporaire);

                        Boolean estDansSettings=false;
                        Boolean pseudoOk=false;
                        Boolean passwordOk=false;

                        for (int i = 0; i < myUsersList.size(); i++) {
                            if (myUsersList.get(i).getPseudo().equals(pseudo)){
                                pseudoOk=true;
                                if (myUsersList.get(i).getPassword().equals(password)){
                                    passwordOk=true;
                                    myUser.setHash(myUsersList.get(i).getHash());
                                }
                            }
                        }

                        // Si le mot de passe est incorrect, on informe l'utilisateur
                        if ((pseudoOk==true) && (passwordOk==false)){
                            erreurConnexion.setText("Mot de passe incorrect");
                        }

                        // Si l'utilisateur n'a jamais été enregistré, on informe l'utilisateur
                        if (pseudoOk==false){
                            erreurConnexion.setText("Sans connexion, impossible d'accéder aux ToDoLists d'un utilisateur jamais renseigné");
                        }


                        // Si l'utilisateur est enregistré, on passe en mode Hors connexion
                        if ((pseudoOk==true) && (passwordOk==true)){
                            Intent toToDoListActivity = new Intent(MainActivity.this, ToDoListActivity.class);
                            toToDoListActivity.putExtra(EXTRA_LOGIN, myUser);
                            toToDoListActivity.putExtra(EXTRA_CONNEXIONOK,connexionOk.toString());
                            startActivity(toToDoListActivity);
                        }

                    }
                }
            }
        });

    }

    // On retire le listener de connexion lorsqu'on quitte l'application
    public void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    //Ajout du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menupseudo, menu);
        return true;
    }

    //Ajout de la gestion du click sur les items du menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // MENU SETTINGS AVEC LA LISTE DES UTILISATEURS
            case R.id.menu_settings:

                if (!myUsersList.isEmpty()){
                    Intent toSettings = new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(toSettings);
                    break;
                }
                else {
                    alerter("Aucun user dans la base de donnée");
                    break;
                }

            // MENU SETTINGS URL AVEC L'URL UTILISEE PAR L'API
            case R.id.menu_settings_url:

                Intent toSettingsURL = new Intent(MainActivity.this,SettingsURL.class);
                startActivity(toSettingsURL);
                    break;
                }
        return super.onOptionsItemSelected(item);
    }

    //Partie GSON
    //Ecrire des données dans la mémoire interne du téléphone

    public void sauvegarderUserToJsonFile(ArrayList myList) {

        final GsonBuilder builder = new GsonBuilder(); //assure la qualité des données Json
        final Gson gson = builder.setPrettyPrinting().create();
        String fileName = "pseudos&Hashs"; //nom du fichier Json
        FileOutputStream outputStream; //permet de sérialiser correctement user

        String fileContents = gson.toJson(myList);

        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Fonction recréant à chaque ouverture de l'appli une liste de users
    public ArrayList<User> getUsersFromFile() {
        Gson gson = new Gson();
        String json = "";
        ArrayList<User> usersList = null;
        try {
            FileInputStream inputStream = openFileInput("pseudos&Hashs");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                                new BufferedInputStream(inputStream), StandardCharsets.UTF_8));
            usersList = gson.fromJson(br, new TypeToken<List<User>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SI ON A PAS REUSSI A RECUPERER DES ELEMENTS DANS LE FICHIER JSON, ON RETOURNE
        // UNE ARRAYLIST VIDE
        if (usersList==null) return new ArrayList<>();
        // SINON ON RETOURNE LA LISTE DES UTILISATEURS RECUPEREE
        else return usersList;
    }


    //Partie GSON
    //Ecrire des données dans la mémoire interne du téléphone

    public void sauvegarderUrlApiToJsonFile(String urlApi) {

        final GsonBuilder builder = new GsonBuilder(); //assure la qualité des données Json
        final Gson gson = builder.setPrettyPrinting().create();
        String fileName = "UrlApi"; //nom du fichier Json
        FileOutputStream outputStream; //permet de sérialiser correctement user

        String fileContents = gson.toJson(urlApi);

        try {
            outputStream = openFileOutput("UrlApi", Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Fonction recréant à chaque ouverture de l'appli une liste de users
    private String getUrlApiFromJson() throws IOException {
        InputStream in = openFileInput("UrlApi");
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // PARTIE VERIFICATION DE LA CONNEXION

    //Si la connexion est ok: bouton connexion disponible et texte informatif
    @Override
    public void networkAvailable() {
        connexionOk=true;
        erreurConnexion.setText("");
        etatConnexion.setText("Connexion OK");
    }

    //Si la connexion pas ok: bouton connexion pas disponible et texte informatif
    @Override
    public void networkUnavailable() {
        connexionOk=false;
        etatConnexion.setText(
                "Attention ! Vous n'êtes pas connectés à internet.");
    }

    //PARTIE ASYNCTASK

    //Asynctask qui permet d'établir la connexion à l'API, et récupérer le hash de l'utilisateur

    public class PostAsyncTask extends AsyncTask<Object, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object... objects) {
            try {
                return (new DataProvider()).getHash(pseudo, password, "POST");
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String hash){
            super.onPostExecute(hash);

            if (hash.equals("")){
                alerter("Veuillez entrer un pseudo et un mot de passe valides");
            }

            else {
                myUser = new User (pseudo, password,hash);

                boolean estDansSettings=false;


                // ON REGARDE SI L'UTILISATEUR EST DEJA ENREGISTRE
                for (int i = 0; i < myUsersList.size(); i++) {
                    if (myUsersList.get(i).equals(myUser)) {
                        estDansSettings=true;
                    }
                }


                if (!estDansSettings){
                    myUsersList.add(myUser);
                    sauvegarderUserToJsonFile(myUsersList);
                }

                Intent toToDoListActivity = new Intent(MainActivity.this, ToDoListActivity.class);
                toToDoListActivity.putExtra(EXTRA_LOGIN, myUser);
                toToDoListActivity.putExtra(EXTRA_CONNEXIONOK,connexionOk.toString());
                startActivity(toToDoListActivity);
            }
        }
    }
}






