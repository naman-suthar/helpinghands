package apps.ijp.helpinghandsv2.Fragments;

public class Profile {
    private String name;
    private String age;

    private String aadhaar;
    private String email;
    private String gender;
    private String lati;
    private String longi;
    private String phone;
    private String profession;

    public Profile(String mName, String mAge, String mAadhaar, String mEmail, String mGender, String mLati, String mLongi,String mPhone, String mProffession){
        this.name = mName;
        this.age = mAge;
        this.aadhaar = mAadhaar;
        this.email = mEmail;
        this.gender = mGender;
        this.lati = mLati;
        this.longi = mLongi;
        this.phone = mPhone;
        this.profession = mProffession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }


}
