package PMR.ToDoList.Controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import PMR.ToDoList.Model.ToDoList;
import PMR.ToDoList.Model.User;
import PMR.ToDoList.R;

import static PMR.ToDoList.Controller.MainActivity.EXTRA_LOGIN;
import static PMR.ToDoList.Controller.MainActivity.myUsersList;
import static android.content.Intent.EXTRA_USER;

public class ToDoListActivity extends AppCompatActivity {

    // PARTIE TOOLBAR
    private androidx.appcompat.widget.Toolbar toolbar;

    // PARTIE RECYCLERVIEW
    private RecyclerView toDoListRecyclerView;
    private ToDoListAdapter toDoListAdapter;
    private RecyclerView.LayoutManager toDoListLayoutManager;

    //PARTIE DONNEES
    public static ArrayList<ToDoList> toDoLists;

    //INSERT TODOLIST
    private Button btnInsertToDoList;
    private EditText textInsertToDoList;

    //USER DE LA TACHE
    private User user;

    public static final String EXTRA_IDLIST = "IDLIST";

    private void alerter(String s) {
        Toast myToast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        myToast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        buildToolbar();

        Intent intentMain = getIntent();
        for(User u : myUsersList){
            if(u.getLogin().matches(intentMain.getSerializableExtra(EXTRA_LOGIN).toString())) user = u;
        }

        toDoLists = user.getMesListeToDo();

        toolbar.setSubtitle(user.getLogin());

        buildRecyclerView(user.getMesListeToDo());

        btnInsertToDoList=findViewById(R.id.btnInsertToDoList);
        textInsertToDoList=findViewById(R.id.textInsertToDoList);

        //BOUTON D'INSERTION D'UNE TO DO LIST
        btnInsertToDoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameToDoList= textInsertToDoList.getText().toString();
                textInsertToDoList.setText("");

                if (!nameToDoList.equals("")){
                    user.ajouteListe(new ToDoList(nameToDoList));
                    toDoListAdapter.notifyItemInserted(toDoListAdapter.getItemCount()-1);
                    sauvegarderToJsonFile(myUsersList);
                }
            }
        });


    }

    public void buildRecyclerView(ArrayList<ToDoList> list){
        toDoListRecyclerView = findViewById(R.id.rvTodoList);
        toDoListRecyclerView.setHasFixedSize(true);
        toDoListLayoutManager = new LinearLayoutManager(this);
        toDoListAdapter = new ToDoListAdapter(list);
        toDoListRecyclerView.setLayoutManager(toDoListLayoutManager);
        toDoListRecyclerView.setAdapter(toDoListAdapter);


        toDoListAdapter.setOnItemClickListener(new ToDoListAdapter.OnItemClickListener() {
            @Override
            //BOUTON QUAND ON CLIQUE SUR UNE CARD
            public void onItemClick(int position) {
                Intent intent=new Intent(ToDoListActivity.this,TasksActivity.class);
                intent.putExtra(EXTRA_IDLIST, toDoLists.get(position).getIdList());
                startActivity(intent);            }
            //BOUTON QUAND ON CLIQUE SUR DELETE
            @Override
            public void onDeleteClick(int position) {

                toDoLists.remove(position);
                user.setMesListeToDo(toDoLists);
                sauvegarderToJsonFile(myUsersList);
                toDoListAdapter.notifyItemRemoved(position);
            }
        });
    }

    public void buildToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    public void sauvegarderToJsonFile(ArrayList myList) {

        final GsonBuilder builder = new GsonBuilder(); //assure la qualité des données Json
        final Gson gson = builder.setPrettyPrinting().create();
        String fileName = "pseudos"; //nom du fichier Json
        FileOutputStream outputStream; //permet de sérialiser correctement user

        String fileContents = gson.toJson(myList);

        try {
            outputStream = openFileOutput("pseudos", Context.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
            Log.i("TODO_Romain", "Sauvegarde du fichier Json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
