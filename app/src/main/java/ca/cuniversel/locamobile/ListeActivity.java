package ca.cuniversel.locamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListeActivity extends AppCompatActivity {

    private static final String TAG = "LocaMobile";

    private EditText champVille;
    private TextView messageErreur;
    private TextView etatSession;
    private LinearLayout conteneur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        champVille = findViewById(R.id.champVille);
        messageErreur = findViewById(R.id.messageErreur);
        etatSession = findViewById(R.id.etatSession);
        conteneur = findViewById(R.id.conteneurLogements);

        Button boutonChercher = findViewById(R.id.boutonChercher);
        Button boutonConnexion = findViewById(R.id.boutonConnexion);
        Button boutonAjouter = findViewById(R.id.boutonAjouter);

        boutonChercher.setOnClickListener(vue -> charger());

        boutonConnexion.setOnClickListener(vue ->
                startActivity(new Intent(ListeActivity.this, ConnexionActivity.class)));

        boutonAjouter.setOnClickListener(vue ->
                startActivity(new Intent(ListeActivity.this, AjoutLogementActivity.class)));
    }

    // onResume est appelé au démarrage ET à chaque retour sur l'écran :
    // la liste est donc rechargée (ex. après l'ajout d'un logement).
    @Override
    protected void onResume() {
        super.onResume();

        String nom = Session.nom(this);
        if (nom == null) {
            etatSession.setText("Non connecté");
        } else {
            etatSession.setText("Connecté : " + nom);
        }

        charger();
    }

    // Champ vide -> toute la liste ; sinon -> recherche par ville
    private void charger() {
        String ville = champVille.getText().toString().trim();

        Call<List<Logement>> appel;
        if (ville.isEmpty()) {
            appel = ClientApi.get().listerLogements();
        } else {
            appel = ClientApi.get().chercherParVille(ville);
        }

        // URL complète réellement envoyée (utile pour le tableau des URL)
        Log.d(TAG, "URL : " + appel.request().url());

        Log.d(TAG, "1 - avant enqueue");

        appel.enqueue(new Callback<List<Logement>>() {
            @Override
            public void onResponse(Call<List<Logement>> call, Response<List<Logement>> reponse) {
                Log.d(TAG, "3 - début de onResponse");

                if (reponse.isSuccessful() && reponse.body() != null) {
                    List<Logement> logements = reponse.body();
                    Log.d(TAG, "code " + reponse.code() + ", " + logements.size() + " logement(s)");

                    if (logements.isEmpty() && !ville.isEmpty()) {
                        // 200 avec [] : pas une erreur, juste aucun résultat
                        afficherErreur("Aucun logement à " + ville + ".");
                    } else {
                        afficher(logements);
                    }
                } else {
                    Log.d(TAG, "code " + reponse.code());
                    afficherErreur("Erreur HTTP " + reponse.code());
                }
            }

            @Override
            public void onFailure(Call<List<Logement>> call, Throwable t) {
                // aucune réponse du serveur
                Log.e(TAG, "aucune réponse : " + t.getMessage(), t);
                afficherErreur("Serveur injoignable : " + t.getMessage());
            }
        });

        // enqueue ne bloque pas : cette ligne s'exécute avant la réponse
        Log.d(TAG, "2 - juste après enqueue");
    }

    // Une carte par logement, créée à partir de item_logement.xml
    private void afficher(List<Logement> logements) {
        conteneur.removeAllViews();
        messageErreur.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Logement l : logements) {
            View carte = inflater.inflate(R.layout.item_logement, conteneur, false);

            TextView titre = carte.findViewById(R.id.carteTitre);
            TextView ville = carte.findViewById(R.id.carteVille);
            TextView prix = carte.findViewById(R.id.cartePrix);

            titre.setText(l.titre);
            ville.setText(l.ville);
            prix.setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));

            // un seul extra : l'id. La fiche demandera le reste au serveur.
            int id = l.id;
            carte.setOnClickListener(vue -> {
                Intent intention = new Intent(ListeActivity.this, DetailActivity.class);
                intention.putExtra(DetailActivity.CLE_ID, id);
                startActivity(intention);
            });

            conteneur.addView(carte);
        }
    }

    private void afficherErreur(String texte) {
        conteneur.removeAllViews();
        messageErreur.setText(texte);
        messageErreur.setVisibility(View.VISIBLE);
    }
}
