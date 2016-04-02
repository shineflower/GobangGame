package com.jackie.gobanggame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.jackie.gobanggame.view.GobangPanel;

public class MainActivity extends AppCompatActivity {
    private GobangPanel mGobangPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGobangPanel = (GobangPanel) findViewById(R.id.gobang_panel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart_game:
                mGobangPanel.restartGame();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
