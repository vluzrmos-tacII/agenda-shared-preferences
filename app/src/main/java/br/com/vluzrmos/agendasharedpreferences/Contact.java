package br.com.vluzrmos.agendasharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;
import android.widget.EditText;

import java.io.File;
import java.util.Comparator;
import java.util.UUID;

public class Contact {
    private SharedPreferences shared;

    private String name;
    private String email;
    private String tel;

    private String filename;

    public static final String SP_NAME  = "name";
    public static final String SP_EMAIL = "email";
    public static final String SP_TEL   = "tel";
    public static final String SP_FILENAME   = "filename";

    public Contact(Context context){
        String file = generateContactFilename();
        setSharedPreferences(context.getSharedPreferences(file, Context.MODE_PRIVATE));

        load();

        setFilename(file);
    }

    public Contact(SharedPreferences shared){
        setSharedPreferences(shared);

        load();
    }

    public Contact(String name, String email, String tel){
        fill(name, email, tel);
    }

    public Contact(SharedPreferences shared, String name, String email, String tel){
        setSharedPreferences(shared);
        fill(name, email, tel);
    }

    public void setSharedPreferences(SharedPreferences shared){
        this.shared = shared;
    }

    public void fill(String name, String email, String tel){
        this.name = name;
        this.email = email;
        this.tel = tel;
    }

    public void save(){
        if(shared!=null){
            SharedPreferences.Editor editor = shared.edit();

            editor.putString(SP_NAME, getName());
            editor.putString(SP_EMAIL, getEmail());
            editor.putString(SP_TEL, getTel());
            editor.putString(SP_FILENAME, getFilename());

            editor.commit();
        }
    }

    public void load(){
        if(shared != null){
            setName(shared.getString(SP_NAME, ""));
            setEmail(shared.getString(SP_EMAIL, ""));
            setTel(shared.getString(SP_TEL, ""));
            setFilename(shared.getString(SP_FILENAME, ""));
        }
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename){
        this.filename = filename;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(EditText view) {
        setName(view.getText().toString().trim());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmail(EditText view) {
        setEmail(view.getText().toString().trim());
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setTel(EditText view) {
        setTel(view.getText().toString().trim());
    }

    public boolean remove(String app){
        File file = new File("/data/data/"+app+"/shared_prefs/"+getFilename()+".xml");

        return file.delete();
    }

    public static String generateContactFilename(){
        return  "contact_"+UUID.randomUUID();
    }


    public boolean nameIsValid(){
        return validateName(getName());
    }

    public boolean emailIsValid(){
        return  validateEmail(getEmail());
    }

    public boolean telIsValid(){
        return validatePhoneNumber(getTel());
    }

    public boolean phoneIsValid(){
        return telIsValid();
    }

    public static boolean validateName(String name){
        return !name.trim().isEmpty();
    }

    public static boolean validateEmail(String email){
        return ((!email.trim().isEmpty()) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static boolean validatePhoneNumber(String phoneNumber){
        return ((!phoneNumber.trim().isEmpty()) && Patterns.PHONE.matcher(phoneNumber).matches());
    }
}


class ContactsComparator implements Comparator<Contact> {


    @Override
    public int compare(Contact c1, Contact c2) {
        int com = c1.getName().compareToIgnoreCase(c2.getName());

        if(com == 0){
            return c1.getEmail().compareToIgnoreCase(c2.getEmail());
        }
        else{
            return com;
        }
    }
}