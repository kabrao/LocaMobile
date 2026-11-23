package ca.cuniversel.locamobile.reseau;

// Corps envoyé à POST api/auth/connexion. Gson le transforme en :
// {"courriel":"...","motDePasse":"..."}
public class ConnexionRequete {
    public final String courriel;
    public final String motDePasse;

    public ConnexionRequete(String courriel, String motDePasse) {
        this.courriel = courriel;
        this.motDePasse = motDePasse;
    }
}
