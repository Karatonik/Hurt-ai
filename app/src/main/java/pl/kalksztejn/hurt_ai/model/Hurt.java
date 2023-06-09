package pl.kalksztejn.hurt_ai.model;

public class Hurt {
    private String id;
    private String owner;
    private String image64Base;

    private String data;

    public Hurt() {
    }

    public Hurt(String id, String owner, String image64Base, String data) {
        this.id = id;
        this.owner = owner;
        this.image64Base = image64Base;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getImage64Base() {
        return image64Base;
    }
    public String getData(){
        return data;
    }

    @Override
    public String toString() {
        return "Hurt{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", image64Base='" + image64Base + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
