package com.jackie.gobanggame.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.jackie.gobanggame.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackie on 2016/4/2.
 * 五子棋面板
 */
public class GobangPanel extends View {
    private int mPanelWidth;
    private float mLineHeight;

    private Bitmap mWhiteChessBitmap;
    private Bitmap mBlackChessBitmap;
    private float mRatioChessOfLineHeight = 3.0f / 4;
    private ArrayList<Point> mWhiteChessList;
    private ArrayList<Point> mBlackChessList;

    private boolean mIsWhite = true;  //默认第一颗下白棋
    private boolean mIsGameOver = false;

    private final static int MAX_COUNT_IN_LINE = 5;

    private Paint mPaint;
    private static final int MAX_LINE = 10;

    public GobangPanel(Context context) {
        this(context, null);
    }

    public GobangPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GobangPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

//        setBackgroundColor(0x44FF0000);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhiteChessBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_chess);
        mBlackChessBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black_chess);

        mWhiteChessList = new ArrayList<>();
        mBlackChessList = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int chessWidth = (int) (mRatioChessOfLineHeight * mLineHeight);
        mWhiteChessBitmap = Bitmap.createScaledBitmap(mWhiteChessBitmap, chessWidth, chessWidth, false);
        mBlackChessBitmap = Bitmap.createScaledBitmap(mBlackChessBitmap, chessWidth, chessWidth, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制棋盘
        drawBoard(canvas);
        //绘制棋子
        drawStone(canvas);
        //检查游戏是否结束
        checkGameOver();
    }

    private void drawBoard(Canvas canvas) {
        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (mLineHeight / 2);
            int endX = (int) (mPanelWidth - mLineHeight / 2);

            int y = (int) ((0.5 + i) * mLineHeight);

            canvas.drawLine(startX, y, endX, y, mPaint);  //横线
            canvas.drawLine(y, startX, y, endX, mPaint);  //纵线
        }
    }

    private void drawStone(Canvas canvas) {
        for (Point point : mWhiteChessList) {
            canvas.drawBitmap(mWhiteChessBitmap,
//                    (point.x + (1 - mRatioChessOfLineHeight) / 2) * mLineHeight,
                    (point.x * mLineHeight + mLineHeight / 8),
//                    (point.y + (1 - mRatioChessOfLineHeight) / 2) * mLineHeight, null);
                    (point.y * mLineHeight + mLineHeight / 8), null);
        }

        for (Point point : mBlackChessList) {
            canvas.drawBitmap(mBlackChessBitmap,
//                    (point.x + (1 - mRatioChessOfLineHeight) / 2) * mLineHeight,
                    (point.x * mLineHeight + mLineHeight / 8),
//                    (point.y + (1 - mRatioChessOfLineHeight) / 2) * mLineHeight, null);
                    (point.y * mLineHeight + mLineHeight / 8), null);
        }
    }

    private void checkGameOver() {
        boolean  isWhiteWinner = checkFiveInLine(mWhiteChessList);
        boolean  isBlackWinner = checkFiveInLine(mBlackChessList);

        if (isWhiteWinner || isBlackWinner) {
            mIsGameOver = true;

            Toast.makeText(getContext(), isWhiteWinner ? "白棋胜利" : "黑棋胜利", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point point : points) {
            int x = point.x;
            int y = point.y;

            boolean winner = checkHorizontal(x, y, points);
            if (winner) {
                return true;
            }

            winner = checkVertical(x, y, points);
            if (winner) {
                return true;
            }

            winner = checkLeftDiagonal(x, y, points);
            if (winner) {
                return true;
            }

            winner = checkRightDiagonal(x, y, points);

            if (winner) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断x，y的位置，是否横向有相邻的五个一致
     * @param x       x坐标
     * @param y       y坐标
     * @param points  集合
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = count; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {  //检查左边
                count++;
            } else {
                break;
            }

            if (count == MAX_COUNT_IN_LINE) {
                return true;
            }

            if (points.contains(new Point(x + i, y))) { //检查右边
                count++;
            } else {
                break;
            }

            if (count == MAX_COUNT_IN_LINE) {
                return true;
            }
        }

        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = count; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }

            if (count == MAX_COUNT_IN_LINE) {
                return true;
            }

            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }

            if (count == MAX_COUNT_IN_LINE) {
                return true;
            }
        }

        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = count; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }

            if (count == MAX_COUNT_IN_LINE) {
                return true;
            }

            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }

            if (count == MAX_COUNT_IN_LINE) {
                return true;
            }
        }

        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = count; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }

            if (count == MAX_COUNT_IN_LINE) {
                return true;
            }

            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }

            if (count == MAX_COUNT_IN_LINE) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point point = getValidPoint(x, y);

            if (mWhiteChessList.contains(point) || mBlackChessList.contains(point)) {
                return false;
            }

            if (mIsWhite) {
                mWhiteChessList.add(point);
            } else {
                mBlackChessList.add(point);
            }

            invalidate();
            mIsWhite = !mIsWhite;
        }

        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int)(x / mLineHeight), (int)(y / mLineHeight));
    }

    public void restartGame() {
        mWhiteChessList.clear();
        mBlackChessList.clear();
        mIsGameOver = false;
        invalidate();
    }

    private static final String INSTANCE_STATE = "instance_state";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_CHESS_LIST = "instance_white_chess_list";
    private static final String INSTANCE_BLACK_CHESS_LIST = "instance_black_chess_list";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_CHESS_LIST, mWhiteChessList);
        bundle.putParcelableArrayList(INSTANCE_BLACK_CHESS_LIST, mBlackChessList);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteChessList = bundle.getParcelableArrayList(INSTANCE_WHITE_CHESS_LIST);
            mBlackChessList = bundle.getParcelableArrayList(INSTANCE_BLACK_CHESS_LIST);
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
