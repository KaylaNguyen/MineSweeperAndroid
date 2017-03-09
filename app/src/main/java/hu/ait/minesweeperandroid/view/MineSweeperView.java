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
    private Paint paintRect;

    private Bitmap bitmapBg;
    private Bitmap flagIcon;
    private Bitmap bombIcon;

    private short winner;

    private int rowNum;
    private int colNum;

    public MineSweeperView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paintBg = new Paint();
        paintBg.setColor(Color.DKGRAY);
        paintBg.setStyle(Paint.Style.FILL);

        paintLine = new Paint();
        paintLine.setColor(Color.WHITE);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(5);

        paintRect = new Paint();
        paintRect.setColor(Color.parseColor("#FF7F50"));
        paintRect.setStyle(Paint.Style.FILL);

        paintText = new Paint();
        paintText.setColor(Color.RED);

        bitmapBg = BitmapFactory.decodeResource(getResources(), R.drawable.theme);
        flagIcon = BitmapFactory.decodeResource(getResources(), R.drawable.flag);
        bombIcon = BitmapFactory.decodeResource(getResources(), R.drawable.bomb);

        rowNum = MineSweeperModel.getInstance().rowNum;
        colNum = MineSweeperModel.getInstance().colNum;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmapBg = Bitmap.createScaledBitmap(bitmapBg, getWidth(), getHeight(), false);
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
        int fieldWidth = getWidth() / colNum;
        int fieldHeight = getHeight() / rowNum;
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                float centerX = j * getHeight() / colNum + getHeight() / (colNum * 3);
                float centerY = i * getWidth() / rowNum + getWidth() / (rowNum * 2);

                if (MineSweeperModel.getInstance().getFieldContent(i, j).isMine() &&
                        MineSweeperModel.getInstance().getFieldContent(i, j).isRevealed()) {
                    canvas.drawRect(fieldWidth * j, fieldHeight * i, fieldWidth * (j + 1), fieldHeight * (i + 1), paintRect);

                    float left = j * getHeight() / colNum + getHeight() / (colNum*6);
                    float top = i * getWidth() / rowNum + getWidth() / (rowNum*6);
                    canvas.drawBitmap(bombIcon, left, top, null);
                }

                else if (MineSweeperModel.getInstance().getFieldContent(i, j).isRevealed()) {
                    canvas.drawRect(fieldWidth * j, fieldHeight * i, fieldWidth * (j + 1), fieldHeight * (i + 1), paintRect);

                    int numAdjMines = MineSweeperModel.getInstance().getFieldContent(i, j).getNumAdjMines();
                    if (numAdjMines != 0) {
                        canvas.drawText(String.valueOf(numAdjMines), centerX, centerY, paintText);
                    }
                }

                else if (MineSweeperModel.getInstance().getFieldContent(i, j).isFlagged()) {
                    canvas.drawRect(fieldWidth * j, fieldHeight * i, fieldWidth * (j + 1), fieldHeight * (i + 1), paintRect);

                    float left = j * getHeight() / colNum + getHeight() / (colNum*6);
                    float top = i * getWidth() / rowNum + getWidth() / (rowNum*6);
                    canvas.drawBitmap(flagIcon, left, top, null);
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
                Snackbar.make(this, "You lost!", Snackbar.LENGTH_INDEFINITE).show();
                ((MainActivity) getContext()).getBtnClear().setBackgroundResource(R.drawable.sad);
            } else {
                Snackbar.make(this, "You won!", Snackbar.LENGTH_INDEFINITE).show();
                ((MainActivity) getContext()).getBtnClear().setBackgroundResource(R.drawable.cool);
            }
            ((MainActivity) getContext()).setPlayerText("Click face to play again!");
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
        ((MainActivity) getContext()).getBtnClear().setBackgroundResource(R.drawable.smile);
        this.winner = 0;

        ((MainActivity) getContext()).getTimePlayer().stop();
        ((MainActivity) getContext()).getTimePlayer().setBase(SystemClock.elapsedRealtime());
        invalidate();
        ((MainActivity) getContext()).getTimePlayer().start();
        Snackbar.make(this, "Good luck!", Snackbar.LENGTH_SHORT).show();
    }
}