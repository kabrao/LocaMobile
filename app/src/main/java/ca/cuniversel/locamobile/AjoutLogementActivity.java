package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.ErreurApi;
import ca.cuniversel.locamobile.reseau.Logement;
import ca.cuniversel.locamobile.reseau.LogementRequete;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AjoutLogementActivity extends AppCompatActivity {

    private static final String TAG = "LocaMobile";

    private EditText champTitre;
    private EditText champVille;
    private EditText champPrix;
    private EditText champVoyageurs;
    private TextView resultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_logement);

        champTitre = findViewById(R.id.champTitre);
        champVille = findViewById(R.id.champVilleAjout);
        champPrix = findViewById(R.id.champPrix);
        champVoyageurs = findViewById(R.id.champVoyageurs);
        resultat = findViewById(R.id.resultatAjout);
        Button boutonPublier = findViewById(R.id.boutonPublier);

        boutonPublier.setOnClickListener(vue -> publier());
    }

    private void publier() {
        String titre = champTitre.getText().toString().trim();
        String ville = champVille.getText().toString().trim();
        String textePrix = champPrix.getText().toString().trim();
        String texteVoyageurs = champVoyageurs.getText().toString().trim();

        // 1. on vérifie les champs AVANT toute conversion en nombre
        if (titre.isEmpty()) {
            champTitre.setError("Entrez un titre");
            return;
        }
        if (ville.isEmpty()) {
            champVille.setError("Entrez une ville");
            return;
        }
        if (textePrix.isEmpty()) {
            champPrix.setError("Entrez un prix");
            return;
        }
        if (texteVoyageurs.isEmpty()) {
            champVoyageurs.setError("Entrez un nombre de voyageurs");
            return;
        }

        // 2. sans jeton, inutile d'appeler l'API
        String jeton = Session.jeton(this);
        if (jeton == null) {
            resultat.setText("Connectez-vous d'abord.");
            return;
        }

        // 3. conversion (les champs ne sont plus vides)
        double prix;
        int voyageurs;
        try {
            prix = Double.parseDouble(textePrix.replace(',', '.'));
            voyageurs = Integer.parseInt(texteVoyageurs);
        } catch (NumberFormatException e) {
            resultat.setText("Le prix ou le nombre de voyageurs n'est pas valide.");
            return;
        }

        resultat.setText("Envoi en cours...");

        LogementRequete requete = new LogementRequete(titre, ville, prix, voyageurs);
        ClientApi.get().creerLogement("Bearer " + jeton, requete).enqueue(new Callback<Logement>() {
            @Override
            public void onResponse(Call<Logement> call, Response<Logement> reponse) {
                int code = reponse.code();
                Log.d(TAG, "creation : code " + code);

                if (code == 201 && reponse.body() != null) {
                    resultat.setText("201 : logement n° " + reponse.body().id + " créé.");
                } else if (code == 401) {
                    resultat.setText("Erreur 401 : jeton absent ou expiré, reconnectez-vous.");
                } else if (code == 403) {
                    resultat.setText("Erreur 403 : seul un propriétaire peut publier un logement.");
                } else if (code == 400) {
                    JsonObject json = ErreurApi.lire(reponse.errorBody());
                    String champs = ErreurApi.champs(json);
                    if (champs == null) {
                        champs = ErreurApi.message(json);
                    }
                    resultat.setText("Erreur 400 : " + champs);
                } else {
                    resultat.setText("Erreur HTTP " + code);
                }
            }

            @Override
            public void onFailure(Call<Logement> call, Throwable t) {
                Log.e(TAG, "aucune réponse : " + t.getMessage(), t);
                resultat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }
}
