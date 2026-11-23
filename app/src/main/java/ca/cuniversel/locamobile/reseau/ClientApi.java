package ca.cuniversel.locamobile.reseau;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Singleton : Retrofit est construit une seule fois puis réutilisé partout.
public final class ClientApi {

    // 10.0.2.2 = l'ordinateur qui héberge l'émulateur (et pas l'émulateur lui-même)
    private static final String URL_BASE = "http://10.0.2.2:8080/";

    private static LocaMobileApi instance;

    private ClientApi() {
        // pas d'objet ClientApi : on passe toujours par get()
    }

    public static synchronized LocaMobileApi get() {
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            instance = retrofit.create(LocaMobileApi.class);
        }
        return instance;
    }
}
