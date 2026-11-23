package ca.cuniversel.locamobile.reseau;

// Miroir Java de LogementReponse (côté serveur).
// Les noms des attributs doivent être EXACTEMENT les clés du JSON,
// sinon Gson laisse la valeur par défaut (0, null, false) sans rien dire.
public class Logement {
    public int id;
    public String titre;
    public String ville;
    public double prixParNuit;
    public int nbVoyageurs;
    public boolean disponible;
    public String proprietaireNom;
}
