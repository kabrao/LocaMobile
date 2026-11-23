package ca.cuniversel.locamobile.reseau;

// Corps envoyé à POST api/logements (même noms que LogementRequete côté serveur)
public class LogementRequete {
    public final String titre;
    public final String ville;
    public final double prixParNuit;
    public final int nbVoyageurs;

    public LogementRequete(String titre, String ville, double prixParNuit, int nbVoyageurs) {
        this.titre = titre;
        this.ville = ville;
        this.prixParNuit = prixParNuit;
        this.nbVoyageurs = nbVoyageurs;
    }
}
