package ca.cuniversel.locamobile.reseau;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

// Description des endpoints de l'API. On ne l'implémente jamais :
// c'est retrofit.create(...) qui fabrique l'objet.
public interface LocaMobileApi {

    // GET http://10.0.2.2:8080/api/logements
    @GET("api/logements")
    Call<List<Logement>> listerLogements();

    // GET api/logements/{id} -> {id} est remplacé par la valeur
    @GET("api/logements/{id}")
    Call<Logement> trouverLogement(@Path("id") int id);

    // GET api/logements?ville=... (Retrofit encode les accents)
    @GET("api/logements")
    Call<List<Logement>> chercherParVille(@Query("ville") String ville);

    // POST api/auth/connexion, le corps JSON vient de l'objet requete
    @POST("api/auth/connexion")
    Call<TokenReponse> connecter(@Body ConnexionRequete requete);

    // GET api/moi, protégé : il faut l'en-tête "Authorization: Bearer <jeton>"
    @GET("api/moi")
    Call<Profil> monProfil(@Header("Authorization") String entete);

    // POST api/logements, seulement pour un PROPRIETAIRE (défi)
    @POST("api/logements")
    Call<Logement> creerLogement(@Header("Authorization") String entete,
                                 @Body LogementRequete requete);
}
