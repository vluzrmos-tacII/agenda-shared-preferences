package br.com.vluzrmos.agendasharedpreferences;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {
    private static final String CONTACTS_SP = "contacts_list";

    ContactsComparator contactsComparator;

    SharedPreferences savedContacts;
    SharedPreferences.Editor savedContactsEditor;

    EditText contactName;
    EditText contactEmail;
    EditText contactTel;
    Button buttonSaveContact;
    Button buttonRemoveContact;
    Button buttonPrevContact;
    Button buttonNextContact;
    Button buttonNewContact;
    Button buttonCancelContact;
    MenuItem actionCall;

    ArrayList<Contact> contacts;
    int position = 0;
    boolean isNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSharedPreferences();

        contactsComparator = new ContactsComparator();

        contacts = loadContactsList();

        setupViewElements();
    }

    /**
     * Setup the shared preferences of contacts list (filenames)
     */
    private void setupSharedPreferences() {
        savedContacts = getSharedPreferences(CONTACTS_SP, MODE_PRIVATE);
        savedContactsEditor = savedContacts.edit();
    }

    /**
     * Setup the view elements
     */
    private void setupViewElements() {
        contactName = (EditText) findViewById(R.id.contact_name);
        contactEmail = (EditText) findViewById(R.id.contact_email);
        contactTel = (EditText) findViewById(R.id.contact_tel);

        buttonSaveContact = (Button) findViewById(R.id.button_save_contact);
        buttonRemoveContact = (Button) findViewById(R.id.button_remove_contact);
        buttonPrevContact = (Button) findViewById(R.id.button_prev_contact);
        buttonNextContact = (Button) findViewById(R.id.button_next_contact);
        buttonNewContact = (Button) findViewById(R.id.button_new_contact);
        buttonCancelContact = (Button) findViewById(R.id.button_cancel_contact);

        contactName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!Contact.validateName(contactName.getText().toString())){
                        contactName.setError(getString(R.string.validation_contact_name));
                    }
                }
            }
        });

        contactEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!Contact.validateEmail(contactEmail.getText().toString())){
                        contactEmail.setError(getString(R.string.validation_contact_email));
                    }
                }
            }
        });

        contactTel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(!Contact.validatePhoneNumber(contactTel.getText().toString())){
                        contactTel.setError(getString(R.string.validation_contact_tel));
                    }
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        showNextAvailableOrSetNewContact();
    }

    /**
     * Show current contact fields
     */
    private void showCurrentContact() {
        if (positionIsValid()) {
            Contact contact = getCurrentContact();

            contactName.setText(contact.getName());
            contactEmail.setText(contact.getEmail());
            contactTel.setText(contact.getTel());
        }

        clearErrors();
        toggleButtons();
    }

    /**
     * Back to previous position and show contact
     */
    private void showPrevContact() {
        if (positionHasPrev()) {
            prevPosition();
            showCurrentContact();
        }
    }

    /**
     * Forward to next position and show contact
     */
    private void showNextContact() {
        if (positionHasNext()) {
            nextPosition();
            showCurrentContact();
        }
    }

    private void clearContactInfo() {
        contactName.setText("");
        contactEmail.setText("");
        contactTel.setText("");
    }

    private void clearErrors(){
        contactName.requestFocus();

        contactName.setError(null);
        contactEmail.setError(null);
        contactTel.setError(null);
    }

    /**
     * Get current position
     *
     * @return
     */
    private int currentPosition() {
        return position;
    }

    /**
     * Back to previous position
     */
    private void prevPosition() {
        position--;
    }

    /**
     * Forward to next position
     */
    private void nextPosition() {
        position++;
    }

    /**
     * Check whenever the position of iterator on contacts is valid
     *
     * @return
     */
    private boolean positionIsValid() {
        return contacts.size() > 0 && position < contacts.size();
    }

    /**
     * Check whenever position has a prev position
     *
     * @return
     */
    private boolean positionHasPrev() {
        int p = position - 1;

        return p >= 0 && contacts.size() > 0 && p < contacts.size();
    }

    /**
     * Check whenever position has a next position
     *
     * @return
     */
    private boolean positionHasNext() {
        return contacts.size() > 0 && (position + 1) < contacts.size();
    }

    /**
     * Toggle exhibition/activation of buttons on screen
     */
    private void toggleButtons() {
        buttonRemoveContact.setEnabled(!isNew);
        buttonNewContact.setEnabled(!isNew);

        if(actionCall!=null){
            actionCall.setVisible(!isNew);
        }

        if(isNew){
            buttonCancelContact.setVisibility(View.VISIBLE);
            buttonRemoveContact.setVisibility(View.GONE);
            buttonPrevContact.setVisibility(View.GONE);
            buttonNextContact.setVisibility(View.GONE);

            buttonCancelContact.setEnabled(contacts.size()>0);

        }
        else{
            buttonCancelContact.setVisibility(View.GONE);
            buttonRemoveContact.setVisibility(View.VISIBLE);
            buttonPrevContact.setVisibility(View.VISIBLE);
            buttonNextContact.setVisibility(View.VISIBLE);
        }

        buttonPrevContact.setEnabled(positionHasPrev());
        buttonNextContact.setEnabled(positionHasNext());
    }

    /**
     * Load the contact list of filenames from shared preferences
     *
     * @return
     */
    private ArrayList<Contact> loadContactsList() {
        Set<String> setContacts = savedContacts.getStringSet(CONTACTS_SP, new HashSet<String>());
        ArrayList<Contact> contacts = new ArrayList<>();

        for (String filename : setContacts) {
            SharedPreferences shared = getSharedPreferences(filename, MODE_PRIVATE);

            contacts.add(new Contact(shared));
        }

        Collections.sort(contacts, contactsComparator);

        return contacts;
    }

    private Contact getContact(int position) {
        return contacts.get(position);
    }

    private Contact getCurrentContact() {
        if (positionIsValid()) {
            return getContact(position);
        }

        return null;
    }

    /**
     * Saves the contacts list filenames to a shared preferences
     *
     * @param contacts
     */
    private void saveContactsList(ArrayList<Contact> contacts) {

        Set<String> setContacts = new HashSet<>();

        for(Contact contact : contacts){
            setContacts.add(contact.getFilename());
        }

        savedContactsEditor.putStringSet(CONTACTS_SP, setContacts);

        savedContactsEditor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        actionCall = menu.findItem(R.id.action_call);


        actionCall.setVisible(!isNew);


        actionCall.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+getCurrentContact().getTel()));

                startActivity(intent);

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickSaveContact(View view) {
        Contact contact;

        if (!isNew) {
            contact = getCurrentContact();
        } else {
            contact = new Contact(this);
        }

        contact.setName(contactName);
        contact.setEmail(contactEmail);
        contact.setTel(contactTel);

        if(!contact.nameIsValid()){
            contactName.setError(getString(R.string.validation_contact_name));
        }
        else if(!contact.emailIsValid()){
            contactEmail.setError(getString(R.string.validation_contact_email));
        }
        else if(!contact.telIsValid()){
            contactTel.setError(getString(R.string.validation_contact_tel));
        }
        else{
            contact.save();

            contacts.add(contact);


            Collections.sort(contacts, contactsComparator);

            if (isNew) {
                isNew = false;

                saveContactsList(contacts);

                position = contacts.indexOf(contact);
                showCurrentContact();
            }
        }
    }

    public void onClickRemoveContact(View view) {
        Contact contact = getCurrentContact();

        contact.remove(getPackageName());

        contacts.remove(currentPosition());

        saveContactsList(contacts);

        showNextAvailableOrSetNewContact();
    }

    public void showNextAvailableOrSetNewContact() {
        if (positionIsValid()) {
            showCurrentContact();
        }
        else if (positionHasNext()) {
            showNextContact();
        } else if (positionHasPrev()) {
            showPrevContact();
        } else {
            setNewContact();
        }
    }

    public void onClickPrevContact(View view) {
        showPrevContact();
    }

    public void onClickNextContact(View view) {
        showNextContact();
    }

    public void onClickCancelContact(View view) {
        isNew=false;

        showNextAvailableOrSetNewContact();
    }

    public void onClickNewContact(View view) {
        setNewContact();
    }

    private void setNewContact() {
        isNew = true;

        clearContactInfo();
        clearErrors();

        toggleButtons();
    }
}
