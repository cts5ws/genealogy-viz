package model;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * The Person classed is a POJO used to read in a JSON file of node objects
 */
public class Person {

    private @JsonProperty("@id") String id;
    private @JsonProperty("@type") String type;
    private @JsonProperty("BIRT") Birthday birthday;
    private @JsonProperty("FAMC") String famC;
    private @JsonProperty("NAME") String name;
    private @JsonProperty("SEX") String sex;
    private @JsonProperty("_UID") String uid;
    private @JsonProperty("FAMS") String famS;

    public Person() {}

    @Override
    public String toString(){
        return "id: " + this.getId() + " type: " + this.getType() + "birthday: " + this.getBirthday() + " famC: " + this.getFamC() +
                " famS: " + " name: " + this.getName() + " sex: " + this.getSex() + "  model.UID: " + this.getUid();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Birthday getBirthday() {
        return birthday;
    }

    public void setBirthday(Birthday birthday) {
        this.birthday = birthday;
    }

    public String getFamC() {
        return famC;
    }

    public void setFamC(String famC) {
        this.famC = famC;
    }

    public String getFamS() {
        return famS;
    }

    public void setFamS(String famS) {
        this.famS = famS;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
