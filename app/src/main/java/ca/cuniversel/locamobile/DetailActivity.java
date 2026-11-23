package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    // clé de l'extra, utilisée des deux côtés (ListeActivity et ici)
    public static final String CLE_ID = "id";

    private static final String TAG = "LocaMobile";

    private TextView etat;
    private LinearLayout carteDetail;
    private TextView detailTitre;
    private TextView detailVille;
    private TextView detailPrix;
    private TextView detailVoyageurs;
    private TextView detailProprietaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        etat = findViewById(R.id.etat);
        carteDetail = findViewById(R.id.carteDetail);
        detailTitre = findViewById(R.id.detailTitre);
        detailVille = findViewById(R.id.detailVille);
        detailPrix = findViewById(R.id.detailPrix);
        detailVoyageurs = findViewById(R.id.detailVoyageurs);
        detailProprietaire = findViewById(R.id.detailProprietaire);

        int id = getIntent().getIntExtra(CLE_ID, -1);
        // id = 999;   // test du 404 (à remettre en commentaire après l'essai)

        if (id == -1) {
            etat.setText("Aucun logement choisi.");
            return;
        }

        chargerFiche(id);
    }

    private void chargerFiche(int id) {
        etat.setText("Chargement...");

        Call<Logement> appel = ClientApi.get().trouverLogement(id);
        Log.d(TAG, "URL : " + appel.request().url());

        appel.enqueue(new Callback<Logement>() {
            @Override
            public void onResponse(Call<Logement> call, Response<Logement> reponse) {
                Log.d(TAG, "fiche : code " + reponse.code() + ", body = " + reponse.body());

                if (reponse.isSuccessful() && reponse.body() != null) {
                    afficher(reponse.body());
                } else if (reponse.code() == 404) {
                    // le serveur a répondu, mais pour dire « introuvable »
                    etat.setText("Ce logement n'existe plus (404).");
                } else {
                    etat.setText("Erreur HTTP " + reponse.code());
                }
            }

            @Override
            public void onFailure(Call<Logement> call, Throwable t) {
                Log.e(TAG, "aucune réponse : " + t.getMessage(), t);
                etat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    private void afficher(Logement l) {
        etat.setText("");
        etat.setVisibility(View.GONE);
        carteDetail.setVisibility(View.VISIBLE);

        detailTitre.setText(l.titre);
        detailVille.setText("Ville : " + l.ville);
        detailPrix.setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));
        detailVoyageurs.setText("Jusqu'à " + l.nbVoyageurs + " voyageur(s)");
        detailProprietaire.setText("Propriétaire : " + l.proprietaireNom);
    }
}
