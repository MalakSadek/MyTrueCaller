package malaksadek.contacts;

/**
 * Created by malaksadek on 6/19/17.
 */

public class User {

    String Name;
    String Number;
    String Email;
    String City;
    int ID;

    public void setName(String Name){
        this.Name=Name;
    }

    public void setPhone_Number(String Phone_Number){
        this.Number=Phone_Number;
    }

    public void setEmail(String Email){
        this.Email=Email;
    }

    public void setCity(String City){
        this.City=City;
    }

    public void setId(int ID){
        this.ID=ID;
    }

    public String getName(){return this.Name;}

    public String getEmail(){return this.Email;}

    public String getCity(){return this.City;}

    public String getPhone_Number(){return this.Number;}
}
