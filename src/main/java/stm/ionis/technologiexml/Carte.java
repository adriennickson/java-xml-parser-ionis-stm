package stm.ionis.technologiexml;

public class Carte {

    private String numero;
    private String photo;
    private String nom;
    private String prenom;
    private char sexe;
    private String dateNaissance;
    private String lieuNaissance;
    private String taille;
    private String signature;
    private String adresse;
    private String dateDelivrance;
    private String dateExpiration;
    private String par;
    private String nationality;
    private boolean puce;
    private String nationalityCode;
    private String code;
    private String puceBase64;

    public Carte(String numero) {
        this.numero = numero;
    }

    public String getNumero() {
        return numero;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public char getSexe() {
        return sexe;
    }

    public void setSexe(char sexe) {
        this.sexe = sexe;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public void setLieuNaissance(String lieuNaissance) {
        this.lieuNaissance = lieuNaissance;
    }

    public String getTaille() {
        return taille;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getDateDelivrance() {
        return dateDelivrance;
    }

    public void setDateDelivrance(String dateDelivrance) {
        this.dateDelivrance = dateDelivrance;
    }

    public String getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(String dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getPar() {
        return par;
    }

    public void setPar(String par) {
        this.par = par;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public boolean isPuce() {
        return puce;
    }

    public void setPuce(boolean puce) {
        this.puce = puce;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNationalityCode() {
        return nationalityCode;
    }

    public void setNationalityCode(String nationalityCode) {
        this.nationalityCode = nationalityCode;
    }

    public String getPuceBase64() {
        return puceBase64;
    }

    public void setPuceBase64(String puceBase64) {
        this.puceBase64 = puceBase64;
    }
}
