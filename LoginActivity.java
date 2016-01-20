package vit.argon.ledisplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by vitaliy on 26.10.2015.
 */
public class LoginActivity extends Activity{

    SharedPreferences userData;
    EditText userName, passWord;
    Button OkLogin;
    TextView register;
    String user = "argon";
    String pass = "argon";
    boolean logged = true;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        userData = getSharedPreferences("UserData", 0);
        OkLogin = (Button) findViewById(R.id.entry_to_app);
        register = (TextView) findViewById(R.id.registerLink);
        userName = (EditText) findViewById(R.id.user_name);
        passWord = (EditText) findViewById(R.id.password);
        userLocalStore = new UserLocalStore(this);

        OkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userName.getText().toString().equals(user) && passWord.getText().toString().equals(pass)) {
                    SharedPreferences.Editor userDataEditor = userData.edit();
                    userDataEditor.putBoolean("loggedIn", logged);
                    userDataEditor.commit();
                    startActivity(new Intent(LoginActivity.this, InputConvertActivity.class));
                } else {
                    String username = userName.getText().toString();
                    String password = passWord.getText().toString();

                    User user = new User(username, password);

                    authenticate(user);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, Register.class);
                startActivity(registerIntent);
            }
        });

    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage("Неверные данные");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void authenticate(User user) {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.fetchUserDataAsyncTask(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void logUserIn(User returnedUser) {
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);
        startActivity(new Intent(this, InputConvertActivity.class));
    }
}
