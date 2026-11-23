package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.ConnexionRequete;
import ca.cuniversel.locamobile.reseau.ErreurApi;
import ca.cuniversel.locamobile.reseau.Profil;
import ca.cuniversel.locamobile.reseau.TokenReponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionActivity extends AppCompatActivity {

    private static final String TAG = "LocaMobile";

    private EditText champCourriel;
    private EditText champMotDePasse;
    private TextView resultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        champCourriel = findViewById(R.id.champCourriel);
        champMotDePasse = findViewById(R.id.champMotDePasse);
        resultat = findViewById(R.id.resultat);
        Button boutonSeConnecter = findViewById(R.id.boutonSeConnecter);

        boutonSeConnecter.setOnClickListener(vue -> seConnecter());
    }

    // Temps 1 : on envoie le courriel et le mot de passe une seule fois
    private void seConnecter() {
        String courriel = champCourriel.getText().toString().trim();
        String motDePasse = champMotDePasse.getText().toString();

        if (courriel.isEmpty()) {
            champCourriel.setError("Entrez votre courriel");
            return;
        }
        if (motDePasse.isEmpty()) {
            champMotDePasse.setError("Entrez votre mot de passe");
            return;
        }

        resultat.setText("Connexion en cours...");

        ConnexionRequete requete = new ConnexionRequete(courriel, motDePasse);
        ClientApi.get().connecter(requete).enqueue(new Callback<TokenReponse>() {
            @Override
            public void onResponse(Call<TokenReponse> call, Response<TokenReponse> reponse) {
                Log.d(TAG, "connexion : code " + reponse.code());

                if (reponse.isSuccessful() && reponse.body() != null) {
                    TokenReponse jeton = reponse.body();
                    // on garde le jeton et le nom (jamais le mot de passe)
                    Session.enregistrer(ConnexionActivity.this, jeton.token, jeton.nom);
                    // le jeton existe maintenant : on peut appeler api/moi
                    chargerProfil();
                } else {
                    // 401 : body() est null, le message est dans errorBody()
                    JsonObject json = ErreurApi.lire(reponse.errorBody());
                    String message = ErreurApi.message(json);
                    if (message == null) {
                        resultat.setText("Erreur " + reponse.code());
                    } else {
                        resultat.setText("Erreur " + reponse.code() + " : " + message);
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenReponse> call, Throwable t) {
                Log.e(TAG, "aucune réponse : " + t.getMessage(), t);
                resultat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    // Temps 2 : on présente le jeton dans l'en-tête Authorization
    private void chargerProfil() {
        String entete = "Bearer " + Session.jeton(this);   // avec l'espace !
        // String entete = "Bearer" + Session.jeton(this); // expérience sans espace

        ClientApi.get().monProfil(entete).enqueue(new Callback<Profil>() {
            @Override
            public void onResponse(Call<Profil> call, Response<Profil> reponse) {
                Log.d(TAG, "api/moi : code " + reponse.code());

                if (reponse.isSuccessful() && reponse.body() != null) {
                    Profil p = reponse.body();
                    resultat.setText("Connecté !"
                            + "\nNom : " + p.nom
                            + "\nRôle : " + p.role
                            + "\nCourriel : " + p.courriel
                            + "\nCode HTTP (api/moi) : " + reponse.code());
                } else {
                    resultat.setText("Erreur " + reponse.code() + " sur api/moi : jeton refusé");
                }
            }

            @Override
            public void onFailure(Call<Profil> call, Throwable t) {
                Log.e(TAG, "aucune réponse : " + t.getMessage(), t);
                resultat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }
}
