package sirine.souissi.mytrackerapp;

public class Position {
    int idPosition;
    String longitude,latitude, pseudo;

    public Position(int idPosition, String longitude, String latitude, String pseudo) {
        this.idPosition = idPosition;
        this.longitude = longitude;
        this.latitude = latitude;
        this.pseudo = pseudo;
    }

    @Override
    public String toString() {
        return "Position{" +
                "idPosition=" + idPosition +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", pseudo='" + pseudo + '\'' +
                '}';
    }

    public int getIdPosition() {
        return idPosition;
    }

    public void setIdPosition(int idPosition) {
        this.idPosition = idPosition;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public Position() {
    }


}
