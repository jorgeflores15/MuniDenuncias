package app.flores.com.munidenuncias;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeLayout;

    private RecyclerView denunciaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent intent = new Intent(HomeActivity.this, NewDenunciaActivity.class);
                //startActivity(intent);
            }
        });
/*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String email = sharedPreferences.getString("email", null);
        Log.d("mail", "email: " + email);
        NavigationView navigationView2 = (NavigationView) findViewById(R.id.nav_view);
        TextView emailText = (TextView) navigationView2.getHeaderView(0).findViewById(R.id.nameUser);
        emailText.setText(sharedPreferences.getString("email", null));


        //Refresh
        // swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);

        //Indicamos que listener recogerá la retrollamada (callback), en este caso, será el metodo OnRefresh de esta clase.

       /* swipeLayout.setOnRefreshListener(this);
        //Podemos espeficar si queremos, un patron de colores diferente al patrón por defecto.
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);*/


        denunciaList = (RecyclerView) findViewById(R.id.recyclerview);
        denunciaList.setLayoutManager(new LinearLayoutManager(this));

        denunciaList.setAdapter(new DenunciasAdapter(this));


        initialize();


    }

    private void initialize() {

        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Denuncia>> call = service.getDenuncias();

        call.enqueue(new Callback<List<Denuncia>>() {
            @Override
            public void onResponse(Call<List<Denuncia>> call, Response<List<Denuncia>> response) {
                try {

                    int statusCode = response.code();
                    Log.d("CODE STATUS", "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Denuncia> denuncias = response.body();
                        Log.d("Denuncias", "denuncias: " + denuncias);

                        DenunciasAdapter adapter = (DenunciasAdapter) denunciaList.getAdapter();
                        adapter.setDenuncia(denuncias);
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.e("Servidor", "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e("t", "onThrowable: " + t.toString(), t);
                        //Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Denuncia>> call, Throwable t) {
                Log.e("OnFallo", "onFailure: " + t.toString());
               // Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private static final int REGISTER_FORM_REQUEST = 100;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REGISTER_FORM_REQUEST) {
            // refresh data
            initialize();
        }

    }

    @Override
    public void onBackPressed() {
       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_exit) {
            Intent intent1 = new Intent(this, LoginActivity.class);
            startActivity(intent1);
            callLogout();

        }
/*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }


    public void callLogout() {
        // remove from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean success = editor.putBoolean("islogged", false).commit();
        //boolean success = editor.clear().commit(); // not recommended
        finish();
    }
}
