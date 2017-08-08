package dcogburn.hometown;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    Button signOut;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        signOut = (Button) findViewById(R.id.signOut);

        signOut.setOnClickListener(this);

        setSupportActionBar(toolbar);


    }

    public void onClick(View view) {
        if (view == signOut) {
            finish();
            fAuth.signOut();
            startActivity(new Intent(this, SignIn.class));
        }
    }

}
