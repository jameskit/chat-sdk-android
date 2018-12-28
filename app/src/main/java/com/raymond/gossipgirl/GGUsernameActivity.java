package com.raymond.gossipgirl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.DisposableList;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class GGUsernameActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String city;
    private EditText nameEditText;
    private boolean usernameInUse;

    private DisposableList disposableList = new DisposableList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        nameEditText = (EditText) findViewById(R.id.username_text);

        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.city_spinner);

        //create a list of items for the spinner.
        String[] items = new String[] {
                "Sydney",
                "Melbourne",
                "Brisbane",
                "Perth",
                "Adelaide",
                "Gold Coast–Tweed Heads",
                "Newcastle–Maitland",
                "Canberra–Queanbeyan",
                "Sunshine Coast",
                "Wollongong",
                "Geelong",
                "Hobart",
                "Townsville",
                "Cairns",
                "Darwin",
                "Toowoomba",
                "Ballarat",
                "Bendigo",
                "Albury–Wodonga",
                "Mackay",
        };

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        city = (String) adapterView.getItemAtPosition(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Another interface callback
    }

    public void didClickOnContinue(View v) {

        String username = nameEditText.getText().toString();

        // What happens if the name is blank?
        if (username.isEmpty()) {
            ToastHelper.show(getApplicationContext(), "Please enter a username.");
            return;
        }

        String stageName = city + "-" + username;
        //The reason for this is that in firebase the stage name is written as city-username, whereas on screen it
        //should be presented as username-city.
        String presentedStageName = GGUserHelper.displayStageName(stageName);

        //Is this name already present?
        disposableList.add(ChatSDK.search().usersForIndex(stageName, 1, GGKeys.StageName)
                .observeOn(AndroidSchedulers.mainThread()).doOnComplete(() -> {
                    //If so, then we have these options:
            if (usernameInUse) {
                AlertDialog.Builder alert = new AlertDialog.Builder(GGUsernameActivity.this);
                alert.setTitle("Stage name already in use");
                alert.setMessage("The stage name " + presentedStageName + " is already in use. You can file a request with the administrator that this be changed, or you can select a new stage name.");
                //The user can either pick a new name
                alert.setPositiveButton("Select a new stage name", (dialog, which) -> {});
                //Or the user can petition to get the username they want.
                alert.setNegativeButton("Request this stage name", (dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), GGNameChangePetitionActivity.class);
                    intent.putExtra("stageNameTransfer", presentedStageName);
                    ChatSDK.ui().startActivity(getApplicationContext(), intent);
                });
                alert.show();
            } else {
                //Now the user must confirm this username and city.
                Intent intent = new Intent (getApplicationContext(), GGConfirmUsernameActivity.class);
                intent.putExtra(GGKeys.Username, username);
                intent.putExtra(GGKeys.StageName, stageName);
                intent.putExtra(GGKeys.PresentedStageName, presentedStageName);
                intent.putExtra(GGKeys.City, city);
                ChatSDK.ui().startActivity(getApplicationContext(), intent);
            }
        }).subscribe(user -> {
            usernameInUse = true;
        }, throwable -> ToastHelper.show(GGUsernameActivity.this, throwable.getLocalizedMessage())));
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposableList.dispose();
    }

}