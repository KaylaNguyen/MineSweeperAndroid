package hu.ait.minesweeperandroid.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import hu.ait.minesweeperandroid.MainActivity;
import hu.ait.minesweeperandroid.R;
import hu.ait.minesweeperandroid.model.MineSweeperModel;

public class MineSweeperView extends View {

    private Paint paintBg;
    private Paint paintLine;
    private Paint paintText;

    private Bitmap bitmapBg;

    private short winner;

    private final int rowNum = 5;
    private final int colNum = 5;

    private int fieldSize;

    public MineSweeperView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // TODO: background pic
        // TODO: smily icon change when lose/win
        // TODO: change field color
        // TODO: icon for mines and flag

        paintBg = new Paint();
        paintBg.setColor(Color.BLACK);
        paintBg.setStyle(Paint.Style.FILL);

        paintLine = new Paint();
        paintLine.setColor(Color.WHITE);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(5);

        paintText = new Paint();
        paintText.setColor(Color.RED);

        bitmapBg = BitmapFactory.decodeResource(getResources(), R.drawable.sky_background);

        fieldSize = (getHeight() * getWidth()) / (rowNum * colNum);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmapBg = Bitmap.createScaledBitmap(bitmapBg, getWidth(), getHeight(), false);
        // TODO: dynamic text size
        paintText.setTextSize(getHeight() / rowNum / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), paintBg);

        canvas.drawBitmap(bitmapBg, 0, 0, null);

        drawGameBoard(canvas);

        drawFields(canvas);
    }

    private void drawFields(Canvas canvas) {
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                float centerX = j * getHeight() / colNum + getHeight() / (colNum * 3);
                float centerY = i * getWidth() / rowNum + getWidth() / (rowNum * 2);
                if (MineSweeperModel.getInstance().getFieldContent(i, j).isMine() &&
                        MineSweeperModel.getInstance().getFieldContent(i, j).isRevealed()) {
                    canvas.drawText("X",
                            centerX,
                            centerY,
                            paintText);
                } else if (MineSweeperModel.getInstance().getFieldContent(i, j).isRevealed()) {
                    canvas.drawText(
                            String.valueOf(MineSweeperModel.getInstance().getFieldContent(i, j).getNumAdjMines()),
                            centerX,
                            centerY,
                            paintText);
                }
                else if (MineSweeperModel.getInstance().getFieldContent(i, j).isFlagged()) {
                    canvas.drawText(
                            "F",
                            centerX,
                            centerY,
                            paintText);
                }
            }
        }
    }

    private void drawGameBoard(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), paintLine);

        for (int i = 1; i < rowNum; i++) {
            canvas.drawLine(0, getHeight() / rowNum * i, getWidth(), getHeight() / rowNum * i, paintLine);
        }

        for (int j = 1; j < colNum; j++) {
            canvas.drawLine(getWidth() / colNum * j, 0, getWidth() / colNum * j, getHeight(), paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (winner != 0) {
            if (winner == -1) {
                ((MainActivity) getContext()).setPlayerText("You lost!");
                Snackbar.make(this, "You lost!", Snackbar.LENGTH_INDEFINITE).show();
            } else {
                ((MainActivity) getContext()).setPlayerText("You won!");
                Snackbar.make(this, "You won!", Snackbar.LENGTH_INDEFINITE).show();
            }
            ((MainActivity) getContext()).getTimePlayer().stop();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int row = ((int) event.getY()) / (getHeight() / rowNum);
            int col = ((int) event.getX()) / (getWidth() / colNum);

            if (((MainActivity) getContext()).getFlagMode()) {
                if (!MineSweeperModel.getInstance().getFieldContent(row, col).isFlagged()) {
                    MineSweeperModel.getInstance().flagField(row, col);
                    invalidate();
                }
            } else {
                if (!MineSweeperModel.getInstance().getFieldContent(row, col).isRevealed()) {
                    MineSweeperModel.getInstance().makeMove(row, col);
                    invalidate();
                }
            }
            winner = MineSweeperModel.getInstance().checkResult();
        }
//                Log.d("DEBUG", "winner is " + String.valueOf(winner));

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int d = w == 0 ? h : h == 0 ? w : w < h ? w : h;
        setMeasuredDimension(d, d);
    }

    public void resetGame() {
        MineSweeperModel.getInstance().resetModel();
        ((MainActivity) getContext()).setPlayerText("Touch the game area to play");

        winner = 0;

        ((MainActivity) getContext()).getTimePlayer().stop();
        ((MainActivity) getContext()).getTimePlayer().setBase(SystemClock.elapsedRealtime());
        invalidate();
        ((MainActivity) getContext()).getTimePlayer().stop();
    }
}