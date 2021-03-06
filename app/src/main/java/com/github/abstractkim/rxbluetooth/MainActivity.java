package com.github.abstractkim.rxbluetooth;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.github.abstractkim.rxbluetooth.Communication.BluetoothActivity;
import com.github.abstractkim.rxbluetooth.Communication.ChatClient;
import com.github.abstractkim.rxbluetooth.Communication.ChatServer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
  public static final String TAG = MainActivity.class.getSimpleName();
  public static final int REQUEST_ENABLE_BT = 1;

  public static ChatServer mServer = null;
  public static ChatClient mClient = null;
  public List<String> permissionsNeeded = new ArrayList<String>();
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show();
      }
    });

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    checkPermission(this);
  }

  @Override public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_bluetooth) {
      startActivity(new Intent(this, BluetoothActivity.class));
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody") @Override public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_camera) {
      // Handle the camera action
    } else if (id == R.id.nav_gallery) {

    } else if (id == R.id.nav_slideshow) {

    } else if (id == R.id.nav_manage) {

    } else if (id == R.id.nav_share) {

    } else if (id == R.id.nav_send) {

    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  protected void onDestroy(){
    super.onDestroy();
    Log.d(TAG, "mCreateBTServerDisposible disposed");

  }

  private void checkPermission(final Activity thisActivity){
    final List<String> permissionsList = new ArrayList<String>();
    if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
      permissionsNeeded.add("Get Scaned list");
    if (permissionsList.size() > 0) {
      ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]), 0);
    }
  }

  private boolean addPermission(List<String> permissionsList, String permission) {
    if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED) {
      permissionsList.add(permission);
      // Check for Rationale Option
      if (!ActivityCompat.shouldShowRequestPermissionRationale(this,permission))
        return false;
    }
    return true;
  }
}
