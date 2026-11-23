package ca.cuniversel.locamobile.reseau;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.ResponseBody;

// Petite classe utilitaire pour lire le JSON d'erreur renvoyé par GestionErreurs :
// {"statut":401,"message":"...","champs":{...}}
public final class ErreurApi {

    private ErreurApi() {
    }

    // Lit errorBody() UNE seule fois et renvoie le JSON, ou null si le corps est vide.
    public static JsonObject lire(ResponseBody corps) {
        if (corps == null) {
            return null;
        }
        try {
            String brut = corps.string();   // lisible une seule fois !
            if (brut.isEmpty()) {
                return null;
            }
            return new Gson().fromJson(brut, JsonObject.class);
        } catch (Exception e) {
            // corps vide ou pas du JSON (ex. 401 sans corps)
            return null;
        }
    }

    // Champ "message", ou null s'il n'existe pas
    public static String message(JsonObject json) {
        if (json == null || !json.has("message") || json.get("message").isJsonNull()) {
            return null;
        }
        return json.get("message").getAsString();
    }

    // Les messages du champ "champs" (erreurs de validation 400), séparés par des virgules
    public static String champs(JsonObject json) {
        if (json == null || !json.has("champs")) {
            return null;
        }
        StringBuilder texte = new StringBuilder();
        for (Map.Entry<String, JsonElement> champ : json.getAsJsonObject("champs").entrySet()) {
            if (texte.length() > 0) {
                texte.append(", ");
            }
            texte.append(champ.getValue().getAsString());
        }
        return texte.toString();
    }
}
