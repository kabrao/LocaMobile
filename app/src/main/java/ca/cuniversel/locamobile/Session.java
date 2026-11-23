package ca.cuniversel.locamobile;

import android.content.Context;
import android.content.SharedPreferences;

// Le carnet « session » : on y garde le jeton et le nom, jamais le mot de passe.
public final class Session {

    private static final String FICHIER = "session";
    private static final String CLE_JETON = "jeton";
    private static final String CLE_NOM = "nom";

    private Session() {
    }

    private static SharedPreferences carnet(Context c) {
        return c.getSharedPreferences(FICHIER, Context.MODE_PRIVATE);
    }

    // écrire : ouvrir le carnet, modifier, puis apply()
    public static void enregistrer(Context c, String jeton, String nom) {
        carnet(c).edit()
                .putString(CLE_JETON, jeton)
                .putString(CLE_NOM, nom)
                .apply();
    }

    // null si personne ne s'est encore connecté
    public static String jeton(Context c) {
        return carnet(c).getString(CLE_JETON, null);
    }

    public static String nom(Context c) {
        return carnet(c).getString(CLE_NOM, null);
    }
}
