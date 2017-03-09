package hu.ait.minesweeperandroid;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.shimmer.ShimmerFrameLayout;

import hu.ait.minesweeperandroid.view.MineSweeperView;

public class MainActivity extends AppCompatActivity {

    private TextView tvPlayer;

    private Chronometer timePlayer;

    private boolean flagMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tvPlayer = (TextView) findViewById(R.id.tvPlayer);

        this.timePlayer = (Chronometer) findViewById(R.id.player_time);
        this.timePlayer.setBase(SystemClock.elapsedRealtime());

        final MineSweeperView gameView = (MineSweeperView) findViewById(R.id.gameView);

        Button btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.resetGame();
            }
        });

        ShimmerFrameLayout shimmerFrameLayout = (ShimmerFrameLayout) findViewById(
                R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmerAnimation();

        this.timePlayer.start();
    }

    public void setPlayerText(String text) {
        this.tvPlayer.setText(text);
    }

    public Chronometer getTimePlayer(){
        return this.timePlayer;
    }

    public void onToggleClicked(View view) {
        this.flagMode = ((ToggleButton) view).isChecked();
    }

    public boolean getFlagMode(){
        return this.flagMode;
    }
}

