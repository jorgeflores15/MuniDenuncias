package app.flores.com.munidenuncias;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText usuario, password;
    private Button btn_login, btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        usuario = (EditText) findViewById(R.id.input_username);
        password = (EditText) findViewById(R.id.input_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        final String email = sharedPreferences.getString("email", null);
        if(email != null){
            usuario.setText(email);
            password.requestFocus();
        }

        if(sharedPreferences.getBoolean("islogged", false)){
            goDashboard();
        }
    }
    private void login() {

        final String username = usuario.getText().toString();
        final String passwordd = password.getText().toString();

        if (username.isEmpty() || passwordd.isEmpty()) {
            Toast.makeText(this,"Debe completar todo los campos", Toast.LENGTH_SHORT).show();
        }

        ApiService service = ApiServiceGenerator.createService(ApiService.class);
        Call<ResponseMessage> call = null;
        call = service.loginUser(username, passwordd);

        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                try {
                    int statusCode = response.code();
                    Log.d(TAG, "HTTP status code: " + statusCode);
                    if (response.isSuccessful()) {
                        ResponseMessage responseMessage = response.body();
                        Log.d(TAG, "responseMessage: " + responseMessage);
                       // Toast.makeText(LoginActivity.this, responseMessage.getMessage(), Toast.LENGTH_LONG).show();
                        // Save to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        boolean success = editor
                                .putString("email", username)
                                .putBoolean("islogged", true)
                                .commit();
                        // Go to Dashboard
                        goDashboard();
                    } else {
                        //progressDialog.dismiss();
                        Log.e(TAG, "onError: " + response.errorBody().string());
                        Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos!", Toast.LENGTH_SHORT).show();
                        //throw new Exception();
                    }

                } catch (Throwable t) {
                    try {
                        Log.e(TAG, "onThrowable: " + t.toString(), t);
                       // Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
               // Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

    }

    private void goDashboard() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("correo",usuario.getText().toString());
        startActivity(intent);
        finish();
    }

}
