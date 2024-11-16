package ma.emsi.appcomptes;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ma.emsi.appcomptes.beans.Compte;
import ma.emsi.appcomptes.config.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnCreate;
    private EditText soldeInput, typeInput;
    private RecyclerView recyclerView;
    private CompteAdapter compteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner payloadTypeSpinner = findViewById(R.id.payloadTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payload_types, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payloadTypeSpinner.setAdapter(adapter);

        btnCreate = findViewById(R.id.btnCreate);
        soldeInput = findViewById(R.id.soldeInput);
        typeInput = findViewById(R.id.typeInput);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        compteAdapter = new CompteAdapter(new ArrayList<>());
        recyclerView.setAdapter(compteAdapter);
        getAllComptes();

        btnCreate.setOnClickListener(v -> createCompte());
    }

    private void getAllComptes() {
        Call<List<Compte>> call = RetrofitClient.getApi().getAllComptes();
        call.enqueue(new Callback<List<Compte>>() {
            @Override
            public void onResponse(Call<List<Compte>> call, Response<List<Compte>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    compteAdapter.setComptes(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Aucun compte trouvé.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Compte>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createCompte() {
        String soldeText = soldeInput.getText().toString();
        String typeText = typeInput.getText().toString();

        if (soldeText.isEmpty() || typeText.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            return;
        }

        Compte newCompte = new Compte();
        newCompte.setSolde(Double.parseDouble(soldeText));
        newCompte.setType(typeText);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = sdf.format(new Date());

        try {
            Date date = sdf.parse(formattedDate);
            newCompte.setDateCreation(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<Compte> call = RetrofitClient.getApi().createCompte(newCompte);
        call.enqueue(new Callback<Compte>() {
            @Override
            public void onResponse(Call<Compte> call, Response<Compte> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(MainActivity.this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
                    getAllComptes();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur lors de la création du compte.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Compte> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}