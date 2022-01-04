package com.radiowebufpa.rdioufpa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    // variáveis da página inicial

    private ImageButton btn;
    private boolean playPause;

    // variáveis do MusicService

    Intent serviceIntent;

    // variáveis do NavigationDrawer

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;

    // A string radioNumber é chamada ao usuário escolher a opção "telefone" no NavigationDrawer
    // Então a intent "phoneCall" é chamada como um serviço

    String radioNumber = "91 32018814";
    Intent phoneCall = new Intent(Intent.ACTION_DIAL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // variáveis do Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this,  R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //navigationView = (BottomNavigationView) findViewById(R.id.navigationView);

        // Criando o menu de navegação (Drawer Menu)

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nav = (NavigationView)findViewById(R.id.navmenu);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_closed);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_website :
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://radio.ufpa.br")));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_facebook :
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.facebook.com/radiowebufpa")));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_twitter :
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://twitter.com/radiowebufpa")));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_instagram :
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.instagram.com/radiowebufpa/")));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_youtube :
                        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.youtube.com/user/RadioWebUFP4")));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_phone :
                        phoneCall.setData(Uri.parse("tel:"+radioNumber));
                        startActivity(phoneCall);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        });

        // Criando o botão de play/pause do player

        btn = (ImageButton) findViewById(R.id.audioStreamBtnPlay);

        // Variável que chama a classe MusicService
        serviceIntent = new Intent(this, MusicService.class);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!playPause) {
                    // Troca o botão para pause ao clicar para tocar
                    btn.setImageResource(R.drawable.pause_foreground);

                    // O MusicService é iniciado ao clicar no botão de play
                    ContextCompat.startForegroundService(MainActivity.this, serviceIntent);

                    new LoadingTask(MainActivity.this).execute();

                    playPause = true;
                } else {
                    // Troca o botão para play ao clicar para parar
                    btn.setImageResource(R.drawable.play_foreground);

                    stopService(serviceIntent); // O MusicService é interrompido ao clicar no botão de pause

                    playPause = false;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // AsyncTask para mostrar uma mensagem de "Carregando" até o player começar
    private class LoadingTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        public LoadingTask(MainActivity activity) {
            progressDialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Carregando...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            MusicService.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    MusicService.mediaPlayer.start();
                    progressDialog.dismiss();
                }
            });
        }
    }

}