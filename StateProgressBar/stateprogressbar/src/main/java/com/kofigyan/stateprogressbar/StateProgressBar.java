package com.kofigyan.stateprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

import com.kofigyan.stateprogressbar.components.StateItem;
import com.kofigyan.stateprogressbar.components.StateItemDescription;
import com.kofigyan.stateprogressbar.components.StateItemNumber;
import com.kofigyan.stateprogressbar.listeners.OnStateItemClickListener;
import com.kofigyan.stateprogressbar.utils.FontManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Kofi Gyan on 4/19/2016.
 */

public class StateProgressBar extends View {

    public enum StateNumber {
        ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);
        private int value;

        StateNumber(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private static final int MIN_STATE_NUMBER = 1;
    private static final int MAX_STATE_NUMBER = 5;

    private static final String STATE_SIZE_KEY = "mStateSize";
    private static final String STATE_LINE_THICKNESS_KEY = "mStateLineThickness";
    private static final String STATE_NUMBER_TEXT_SIZE_KEY = "mStateNumberTextSize";
    private static final String STATE_DESCRIPTION_SIZE_KEY = "mStateDescriptionSize";

    private static final String MAX_STATE_NUMBER_KEY = "mMaxSttNum";
    private static final String CURRENT_STATE_NUMBER_KEY = "mCurrentStateNumber";

    private static final String ANIM_START_DELAY_KEY = "mAnimStartDelay";
    private static final String ANIM_DURATION_KEY = "mAnimDuration";

    private static final String DESC_TOP_SPC_DECR_KEY = "mDescTopSpcDecr";
    private static final String DESC_TOP_SPC_INC_KEY = "mDescTopSpcIncr";

    private static final String BACKGROUND_COLOR_KEY = "mBackgroundColor";
    private static final String FOREGROUND_COLOR_KEY = "mForegroundColor";
    private static final String STT_NUM_BACK_COL_KEY = "mSttNumBkGrndClr";
    private static final String STT_NUM_FORE_COLOR_KEY = "mSttNumFrGrndClr";

    private static final String CURRENT_STATE_DESC_COLOR_KEY = "mCrrntSttDescClr";
    private static final String STATE_DESC_COLOR_KEY = "mStateDescriptionColor";

    private static final String CHECK_STATE_COMPLETED_KEY = "mCheckStateCompleted";

    private static final String ENA_ALL_STT_COMP_KEY = "mEnaAllSttComp";

    private static final String JUSTIFY_MULTILINE_DESC_KEY = "mJustMultDesc";

    private static final String DESCRIPTION_LINE_SPACING_KEY = "mDescLinesSpc";

    private static final String END_CENTER_X_KEY = "mEndCenterX";
    private static final String START_CENTER_X_KEY = "mStartCenterX";
    private static final String ANIM_START_X_POS_KEY = "mAnimStartXPos";
    private static final String ANIM_END_X_POS_KEY = "mAnimEndXPos";
    private static final String IS_CURRENT_ANIM_STARTED_KEY = "mIsCurrentAnimStarted";
    private static final String ANM_CUR_PRO_STT_KEY = "mAniToCurrProgStt";
    private static final String IS_STT_NUM_DESC_KEY = "mIsSttNumDesc";
    private static final String INSTANCE_STATE = "saved_instance";

    private ArrayList<String> mStateDescriptionData = new ArrayList<String>();

    private float mStateRadius;
    private float mStateSize;
    private float mStateLineThickness;
    private float mStateNumberTextSize;
    private float mStateDescriptionSize;

    /**
     * width of one cell = stageWidth/noOfStates
     */
    private float mCellWidth;

    private float mCellHeight;

    /**
     * next cell(state) from previous cell
     */
    private float mNextCellWidth;

    /**
     * center of first cell(state)
     */
    private float mStartCenterX;

    /**
     * center of last cell(state)
     */
    private float mEndCenterX;

    private int mMaxSttNum;
    private int mCurrentStateNumber;

    private int mAnimStartDelay;
    private int mAnimDuration;

    private float mSpacing;

    private float mDescTopSpcDecr;
    private float mDescTopSpcIncr;

    private static final float DEFAULT_TEXT_SIZE = 15f;
    private static final float DEFAULT_STATE_SIZE = 25f;

    /**
     * Paints for drawing
     */
    private Paint mSttNumForgrndPnt;
    private Paint mStateCheckedForegroundPaint;
    private Paint mSttNumBackgrndPnt;
    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;
    private Paint mCurSttDescPaint;
    private Paint mSttDescPaint;

    private int mBackgroundColor;
    private int mForegroundColor;
    private int mSttNumBkGrndClr;
    private int mSttNumFrGrndClr;
    private int mCrrntSttDescClr;
    private int mStateDescriptionColor;

    /**
     * animate inner line to current progress state
     */
    private Animator mAnimator;

    /**
     * tracks progress of line animator
     */
    private float mAnimStartXPos;
    private float mAnimEndXPos;

    private boolean mIsCurrentAnimStarted;

    /**
     * 5 4 3 2 1  (RTL Support)
     */
    private boolean mIsSttNumDesc;


    private int mStateItemClickedNumber;

    private static final String EMPTY_SPACE_DESCRIPTOR = "";


    private Typeface mCustomStateNumberTypeface;
    private Typeface mCustomSttDescTyfc;
    private Typeface mDefaultTypefaceBold;


    private boolean mIsDescriptionMultiline;
    private int mMaxDescriptionLine;
    private float mDescLinesSpc;

    private static final String STT_DESC_LINE_SEP = "\n";

    private boolean mJustMultDesc;


    private boolean mAniToCurrProgStt;
    private boolean mEnaAllSttComp;
    private boolean mCheckStateCompleted;

    private Typeface mCheckFont;


    private OnStateItemClickListener mOnStateItemClickListener;

    public StateProgressBar(Context context) {
        this(context, null, 0);
    }

    public StateProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
        initializePainters();
        updateCheckAllStatesValues(mEnaAllSttComp);

    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        /**
         * Setting default values.
         */
        initStateProgressBar(context);

        mStateDescriptionSize = convertSpToPixel(mStateDescriptionSize);
        mStateLineThickness = convertDpToPixel(mStateLineThickness);
        mSpacing = convertDpToPixel(mSpacing);

        mCheckFont = FontManager.getTypeface(context);
        mDefaultTypefaceBold = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);


        if (attrs != null) {

            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateProgressBar, defStyle, 0);

            mBackgroundColor = a.getColor(R.styleable.StateProgressBar_spb_stateBackgroundColor, mBackgroundColor);
            mForegroundColor = a.getColor(R.styleable.StateProgressBar_spb_stateForegroundColor, mForegroundColor);
            mSttNumBkGrndClr = a.getColor(R.styleable.StateProgressBar_spb_stateNumberBackgroundColor, mSttNumBkGrndClr);
            mSttNumFrGrndClr = a.getColor(R.styleable.StateProgressBar_spb_stateNumberForegroundColor, mSttNumFrGrndClr);
            mCrrntSttDescClr = a.getColor(R.styleable.StateProgressBar_spb_currentStateDescriptionColor, mCrrntSttDescClr);
            mStateDescriptionColor = a.getColor(R.styleable.StateProgressBar_spb_stateDescriptionColor, mStateDescriptionColor);

            mCurrentStateNumber = a.getInteger(R.styleable.StateProgressBar_spb_currentStateNumber, mCurrentStateNumber);
            mMaxSttNum = a.getInteger(R.styleable.StateProgressBar_spb_maxStateNumber, mMaxSttNum);

            mStateSize = a.getDimension(R.styleable.StateProgressBar_spb_stateSize, mStateSize);
            mStateNumberTextSize = a.getDimension(R.styleable.StateProgressBar_spb_stateTextSize, mStateNumberTextSize);
            mStateDescriptionSize = a.getDimension(R.styleable.StateProgressBar_spb_stateDescriptionSize, mStateDescriptionSize);
            mStateLineThickness = a.getDimension(R.styleable.StateProgressBar_spb_stateLineThickness, mStateLineThickness);

            mCheckStateCompleted = a.getBoolean(R.styleable.StateProgressBar_spb_checkStateCompleted, mCheckStateCompleted);
            mAniToCurrProgStt = a.getBoolean(R.styleable.StateProgressBar_spb_animateToCurrentProgressState, mAniToCurrProgStt);
            mEnaAllSttComp = a.getBoolean(R.styleable.StateProgressBar_spb_enableAllStatesCompleted, mEnaAllSttComp);

            mDescTopSpcDecr = a.getDimension(R.styleable.StateProgressBar_spb_descriptionTopSpaceDecrementer, mDescTopSpcDecr);
            mDescTopSpcIncr = a.getDimension(R.styleable.StateProgressBar_spb_descriptionTopSpaceIncrementer, mDescTopSpcIncr);

            mAnimDuration = a.getInteger(R.styleable.StateProgressBar_spb_animationDuration, mAnimDuration);
            mAnimStartDelay = a.getInteger(R.styleable.StateProgressBar_spb_animationStartDelay, mAnimStartDelay);

            mIsSttNumDesc = a.getBoolean(R.styleable.StateProgressBar_spb_stateNumberIsDescending, mIsSttNumDesc);

            mMaxDescriptionLine = a.getInteger(R.styleable.StateProgressBar_spb_maxDescriptionLines, mMaxDescriptionLine);

            mDescLinesSpc = a.getDimension(R.styleable.StateProgressBar_spb_descriptionLinesSpacing, mDescLinesSpc);

            mJustMultDesc = a.getBoolean(R.styleable.StateProgressBar_spb_justifyMultilineDescription, mJustMultDesc);


            if (!mAniToCurrProgStt) {
                stopAnimation();
            }

            resolveStateSize();
            validateLineThickness(mStateLineThickness);
            validateStateNumber(mCurrentStateNumber);

            enum const_div = 2;

            mStateRadius = mStateSize / const_div;

            a.recycle();

        }

    }

    private void initializePainters() {

        mBackgroundPaint = setPaintAttributes(mStateLineThickness, mBackgroundColor);
        mForegroundPaint = setPaintAttributes(mStateLineThickness, mForegroundColor);

        mSttNumForgrndPnt = setPaintAttributes(mStateNumberTextSize, mSttNumFrGrndClr, mCustomStateNumberTypeface != null ? mCustomStateNumberTypeface : mDefaultTypefaceBold);
        mStateCheckedForegroundPaint = setPaintAttributes(mStateNumberTextSize, mSttNumFrGrndClr, mCheckFont);

        mSttNumBackgrndPnt = setPaintAttributes(mStateNumberTextSize, mSttNumBkGrndClr, mCustomStateNumberTypeface != null ? mCustomStateNumberTypeface : mDefaultTypefaceBold);
        mCurSttDescPaint = setPaintAttributes(mStateDescriptionSize, mCrrntSttDescClr, mCustomSttDescTyfc != null ? mCustomSttDescTyfc : mDefaultTypefaceBold);

        mSttDescPaint = setPaintAttributes(mStateDescriptionSize, mStateDescriptionColor, mCustomSttDescTyfc != null ? mCustomSttDescTyfc : mDefaultTypefaceBold);

    }


    public void setStateNumberTypeface(String pathToFont) {
        mCustomStateNumberTypeface = FontManager.getTypeface(getContext(), pathToFont);
        mSttNumForgrndPnt.setTypeface(mCustomStateNumberTypeface != null ? mCustomStateNumberTypeface : mDefaultTypefaceBold);
        mSttNumBackgrndPnt.setTypeface(mCustomStateNumberTypeface != null ? mCustomStateNumberTypeface : mDefaultTypefaceBold);

        invalidate();
    }


    public Typeface getStateNumberTypeface() {
        return mCustomStateNumberTypeface;
    }


    public void setStateDescriptionTypeface(String pathToFont) {
        mCustomSttDescTyfc = FontManager.getTypeface(getContext(), pathToFont);
        mSttDescPaint.setTypeface(mCustomSttDescTyfc != null ? mCustomSttDescTyfc : mDefaultTypefaceBold);
        mCurSttDescPaint.setTypeface(mCustomSttDescTyfc != null ? mCustomSttDescTyfc : mDefaultTypefaceBold);

        invalidate();
    }

    public Typeface getStateDescriptionTypeface(String pathToFont) {
        return mCustomSttDescTyfc;
    }


    private void validateLineThickness(float lineThickness) {
        enum const_div = 2
        float halvedStateSize = mStateSize / const_div;

        if (lineThickness > halvedStateSize) {
            mStateLineThickness = halvedStateSize;
        }
    }

    private void validateStateSize() {
        enum const_div = 2;
        if (mStateSize <= mStateNumberTextSize) {
            mStateSize = mStateNumberTextSize + mStateNumberTextSize / 2;
        }
    }

    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        mBackgroundPaint.setColor(mBackgroundColor);
        invalidate();
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setForegroundColor(int foregroundColor) {
        mForegroundColor = foregroundColor;
        mForegroundPaint.setColor(mForegroundColor);
        invalidate();
    }

    public int getForegroundColor() {
        return mForegroundColor;
    }

    public void setStateLineThickness(float stateLineThickness) {
        mStateLineThickness = convertDpToPixel(stateLineThickness);
        resolveStateLineThickness();
    }

    private void resolveStateLineThickness() {
        validateLineThickness(mStateLineThickness);
        mBackgroundPaint.setStrokeWidth(mStateLineThickness);
        mForegroundPaint.setStrokeWidth(mStateLineThickness);
        invalidate();
    }

    public float getStateLineThickness() {
        return mStateLineThickness;
    }

    public void setStateNumberBackgroundColor(int stateNumberBackgroundColor) {
        mSttNumBkGrndClr = stateNumberBackgroundColor;
        mSttNumBackgrndPnt.setColor(mSttNumBkGrndClr);
        invalidate();
    }

    public int getStateNumberBackgroundColor() {
        return mSttNumBkGrndClr;
    }

    public void setStateNumberForegroundColor(int stateNumberForegroundColor) {
        mSttNumFrGrndClr = stateNumberForegroundColor;
        mSttNumForgrndPnt.setColor(mSttNumFrGrndClr);
        mStateCheckedForegroundPaint.setColor(mSttNumFrGrndClr);
        invalidate();
    }

    public int getStateNumberForegroundColor() {
        return mSttNumFrGrndClr;
    }

    public void setStateDescriptionColor(int stateDescriptionColor) {
        mStateDescriptionColor = stateDescriptionColor;
        mSttDescPaint.setColor(mStateDescriptionColor);
        invalidate();
    }

    public int getStateDescriptionColor() {
        return mStateDescriptionColor;
    }

    public void setCurrentStateDescriptionColor(int currentStateDescriptionColor) {
        mCrrntSttDescClr = currentStateDescriptionColor;
        mCurSttDescPaint.setColor(mCrrntSttDescClr);
        invalidate();
    }

    public int getCurrentStateDescriptionColor() {
        return mCrrntSttDescClr;
    }

    public void setCurrentStateNumber(StateNumber currentStateNumber) {
        validateStateNumber(currentStateNumber.getValue());
        mCurrentStateNumber = currentStateNumber.getValue();
        updateCheckAllStatesValues(mEnaAllSttComp);
        invalidate();
    }

    public int getCurrentStateNumber() {
        return mCurrentStateNumber;
    }


    public void setMaxStateNumber(StateNumber maximumState) {
        mMaxSttNum = maximumState.getValue();
        resolveMaxStateNumber();
    }

    private void resolveMaxStateNumber() {
        validateStateNumber(mCurrentStateNumber);
        updateCheckAllStatesValues(mEnaAllSttComp);
        invalidate();
    }

    public int getMaxStateNumber() {
        return mMaxSttNum;
    }


    public void setStateSize(float stateSize) {
        mStateSize = convertDpToPixel(stateSize);
        resetStateSizeValues();
    }

    public float getStateSize() {
        return mStateSize;
    }

    public void setStateNumberTextSize(float textSize) {
        mStateNumberTextSize = convertSpToPixel(textSize);
        resetStateSizeValues();
    }


    public void setOnStateItemClickListener(OnStateItemClickListener onStateItemClickListener) {
        mOnStateItemClickListener = onStateItemClickListener;
    }


    private void resetStateSizeValues() {

        resolveStateSize();

        mSttNumForgrndPnt.setTextSize(mStateNumberTextSize);
        mSttNumBackgrndPnt.setTextSize(mStateNumberTextSize);
        mStateCheckedForegroundPaint.setTextSize(mStateNumberTextSize);

        mStateRadius = mStateSize / 2;

        validateLineThickness(mStateLineThickness);

        mBackgroundPaint.setStrokeWidth(mStateLineThickness);
        mForegroundPaint.setStrokeWidth(mStateLineThickness);
        requestLayout();
    }

    public void setStateDescriptionSize(float stateDescriptionSize) {
        mStateDescriptionSize = convertSpToPixel(stateDescriptionSize);
        resolveStateDescriptionSize();
    }

    private void resolveStateDescriptionSize() {
        mCurSttDescPaint.setTextSize(mStateDescriptionSize);
        mSttDescPaint.setTextSize(mStateDescriptionSize);
        requestLayout();
    }

    public float getStateDescriptionSize() {
        return mStateDescriptionSize;
    }


    public float getStateNumberTextSize() {
        return mStateNumberTextSize;
    }

    public void checkStateCompleted(boolean checkStateCompleted) {
        mCheckStateCompleted = checkStateCompleted;
        invalidate();
    }


    public void setAllStatesCompleted(boolean enableAllStatesCompleted) {
        mEnaAllSttComp = enableAllStatesCompleted;
        updateCheckAllStatesValues(mEnaAllSttComp);
        invalidate();
    }

    private void updateCheckAllStatesValues(boolean enableAllStatesCompleted) {
        if (enableAllStatesCompleted) {
            mCheckStateCompleted = true;
            mCurrentStateNumber = mMaxSttNum;
            mSttDescPaint.setColor(mCurSttDescPaint.getColor());
        } else {
            mSttDescPaint.setColor(mSttDescPaint.getColor());
        }
    }


    public void enableAnimationToCurrentState(boolean animateToCurrentProgressState) {
        this.mAniToCurrProgStt = animateToCurrentProgressState;

        if (mAniToCurrProgStt && mAnimator == null) {
            startAnimator();
        }

        invalidate();

    }


    private void validateStateNumber(int stateNumber) {
        if (stateNumber > mMaxSttNum) {
            throw new IllegSttExce("State number (" + stateNumber + ") cannot be greater than total number of states " + mMaxSttNum);
        }
    }


    public void setDescriptionTopSpaceIncrementer(float spaceIncrementer) {
        mDescTopSpcIncr = spaceIncrementer;
        requestLayout();
    }


    public void setDescriptionTopSpaceDecrementer(float spaceDecrementer) {
        mDescTopSpcDecr = spaceDecrementer;
        requestLayout();
    }

    public float getDescriptionTopSpaceDecrementer() {
        return mDescTopSpcDecr;
    }

    public float getDescriptionTopSpaceIncrementer() {
        return mDescTopSpcIncr;
    }

    public float getDescriptionLinesSpacing() {
        return mDescLinesSpc;
    }

    public void setDescriptionLinesSpacing(float descriptionLinesSpacing) {
        mDescLinesSpc = descriptionLinesSpacing;
        requestLayout();
    }


    public void setAnimationDuration(int animDuration) {
        mAnimDuration = animDuration;
        invalidate();
    }

    public int getAnimationDuration() {
        return mAnimDuration;
    }

    public void setAnimationStartDelay(int animStartDelay) {
        mAnimStartDelay = animStartDelay;
        invalidate();
    }

    public int getAnimationStartDelay() {
        return mAnimStartDelay;
    }


    public void setStateNumberIsDescending(boolean stateNumberIsDescending) {
        mIsSttNumDesc = stateNumberIsDescending;
        invalidate();
    }

    public boolean getStateNumberIsDescending() {
        return mIsSttNumDesc;
    }


    public boolean isDescriptionMultiline() {
        return mIsDescriptionMultiline;
    }


    private void updateDescriptionMultilineStatus(boolean multiline) {
        mIsDescriptionMultiline = multiline;
    }


    public int getMaxDescriptionLine() {
        return mMaxDescriptionLine;
    }


    public void setMaxDescriptionLine(int maxDescriptionLine) {
        mMaxDescriptionLine = maxDescriptionLine;
        requestLayout();
    }


    public boolean isJustifyMultilineDescription() {
        return mJustMultDesc;
    }

    public void setJustifyMultilineDescription(boolean justifyMultilineDescription) {
        mJustMultDesc = justifyMultilineDescription;
        invalidate();
    }


    private Paint setPaintAttributes(float strokeWidth, int color) {
        Paint paint = setPaintAttributes(color);
        paint.setStrokeWidth(strokeWidth);
        return paint;
    }

    private Paint setPaintAttributes(float textSize, int color, Typeface typeface) {
        Paint paint = setPaintAttributes(color);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);
        return paint;
    }

    private Paint setPaintAttributes(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        return paint;
    }

    private void initStateProgressBar(Context context) {

        mBackgroundColor = ContextCompat.getColor(context, R.color.background_color);
        mForegroundColor = ContextCompat.getColor(context, R.color.foreground_color);
        mSttNumBkGrndClr = ContextCompat.getColor(context, R.color.background_text_color);
        mSttNumFrGrndClr = ContextCompat.getColor(context, R.color.foreground_text_color);
        mCrrntSttDescClr = ContextCompat.getColor(context, R.color.foreground_color);
        mStateDescriptionColor = ContextCompat.getColor(context, R.color.background_text_color);

        mStateSize = 0.0f;
        mStateLineThickness = 4.0f;
        mStateNumberTextSize = 0.0f;
        mStateDescriptionSize = 15f;

        mMaxSttNum = StateNumber.FIVE.getValue();
        mCurrentStateNumber = StateNumber.ONE.getValue();

        mSpacing = 4.0f;

        mDescTopSpcDecr = 0.0f;
        mDescTopSpcIncr = 0.0f;

        mDescLinesSpc = 0.0f;

        mCheckStateCompleted = false;
        mAniToCurrProgStt = false;
        mEnaAllSttComp = false;

        mAnimStartDelay = 100;
        mAnimDuration = 4000;

        mIsSttNumDesc = false;

        mJustMultDesc = false;
    }


    private void resolveStateSize() {
        resolveStateSize(mStateSize != 0, mStateNumberTextSize != 0);
    }


    private void resolveStateSize(boolean isStateSizeSet, boolean isStateTextSizeSet) {
        if (!isStateSizeSet && !isStateTextSizeSet) {
            mStateSize = convertDpToPixel(DEFAULT_STATE_SIZE);
            mStateNumberTextSize = convertSpToPixel(DEFAULT_TEXT_SIZE);

        } else if (isStateSizeSet && isStateTextSizeSet) {
            validateStateSize();

        } else if (!isStateSizeSet) {
            mStateSize = mStateNumberTextSize + mStateNumberTextSize / 2;

        } else {
            mStateNumberTextSize = mStateSize - (mStateSize * 0.375f);
        }

    }


    private void drawCircles(Canvas canvas, Paint paint, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; i++) {
            canvas.drawCircle(mCellWidth * (i + 1) - (mCellWidth / 2), mCellHeight / 2, mStateRadius, paint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mOnStateItemClickListener == null) {
            return false;
        }

        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            if (isPointInCircle((int) event.getX(), (int) event.getY())) {
                performClick();
                return true;
            }
            return false;
        }

        return false;
    }


    @Override
    public boolean performClick() {
        super.performClick();

        if (mOnStateItemClickListener != null) {

            mOnStateItemClickListener.onStateItemClick(this, getStateItem(mStateItemClickedNumber), mStateItemClickedNumber, getCurrentStateNumber() == mStateItemClickedNumber);

            return true;
        }

        return false;
    }


    private boolean isPointInCircle(int clickX, int clickY) {
        boolean isTouched = false;
        for (int i = 0; i < mMaxSttNum; i++) {
            isTouched = (!(clickX < mCellWidth * (i + 1) - (mCellWidth / 2) - mStateRadius || clickX > mCellWidth * (i + 1) - (mCellWidth / 2) + mStateRadius || clickY < mCellHeight / 2 - mStateRadius || clickY > mCellHeight / 2 + mStateRadius));
            if (isTouched) {
                mStateItemClickedNumber = mIsSttNumDesc ? mMaxSttNum - i : i + 1;
                return isTouched;
            }

        }
        return isTouched;
    }


    private StateItem getStateItem(int stateItemClickedNumber) {

        final boolean isCurrentState = getCurrentStateNumber() == stateItemClickedNumber;
        final boolean isForegroundColor = getCurrentStateNumber() >= stateItemClickedNumber;
        final boolean isCompletedState = getCurrentStateNumber() > stateItemClickedNumber;
        final float stateSize = getStateSize();
        final int stateColor = isForegroundColor ? mForegroundColor : mBackgroundColor;
        boolean isCheckedState = false;
        StateItemDescription stateItemDescription = null;

        if (isCompletedState && mCheckStateCompleted) {
            isCheckedState = true;
        }


        final int stateNumberColor = isForegroundColor ? mSttNumFrGrndClr : mSttNumBkGrndClr;
        final float stateNumberSize = getStateNumberTextSize();

        final int stateDescriptionColor = isCurrentState ? mCrrntSttDescClr : mStateDescriptionColor;


        StateItemNumber stateItemNumber = StateItemNumber.builder().
                color(stateNumberColor).
                size(stateNumberSize).
                number(stateItemClickedNumber)
                .build();


        if (!getStateDescriptionData().isEmpty() && stateItemClickedNumber <= getStateDescriptionData().size()) {
            final float stateDescriptionSize = getStateDescriptionSize();

            stateItemDescription = StateItemDescription.builder().
                    color(stateDescriptionColor).
                    size(stateDescriptionSize).
                    text(getStateDescriptionData().get(mIsSttNumDesc ? (getStateDescriptionData().size() >= mMaxSttNum ? stateItemClickedNumber - 1 + (getStateDescriptionData().size() - mMaxSttNum) : stateItemClickedNumber - 1) : stateItemClickedNumber - 1)).
                    build();
        }

        return StateItem.builder().
                color(stateColor).
                size(stateSize).
                stateItemNumber(stateItemNumber).
                isCurrentState(isCurrentState).
                isStateChecked(isCheckedState).
                stateItemDescription(stateItemDescription).
                build();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mCellWidth = getWidth() / mMaxSttNum;
        mNextCellWidth = mCellWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawState(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getDesiredHeight();
        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, height);

        mCellHeight = getCellHeight();

    }


    private int getDesiredHeight() {
        if (mStateDescriptionData.isEmpty()) {
            return (int) (2 * mStateRadius) + (int) (mSpacing);
        } else {
            if (checkForDescriptionMultiLine(mStateDescriptionData)) {
                return (int) (2 * mStateRadius) + (int) (selectMaxDescriptionLine(mMaxDescriptionLine) * (1.3 * mStateDescriptionSize)) + (int) (mSpacing) - (int) (mDescTopSpcDecr) + (int) (mDescTopSpcIncr) + (int) mDescLinesSpc;

            } else {
                return (int) (2 * mStateRadius) + (int) (1.3 * mStateDescriptionSize) + (int) (mSpacing) - (int) (mDescTopSpcDecr) + (int) (mDescTopSpcIncr);
            }
        }
    }


    private int getCellHeight() {
        return (int) (2 * mStateRadius) + (int) (mSpacing);
    }


    private boolean checkForDescriptionMultiLine(ArrayList<String> stateDescriptionData) {
        boolean isMultiLine = false;
        for (String stateDescription : stateDescriptionData) {
            isMultiLine = stateDescription.contains(STT_DESC_LINE_SEP);
            if (isMultiLine) {
                updateDescriptionMultilineStatus(isMultiLine);
                return isMultiLine;
            }
        }
        return isMultiLine;
    }


    private int getMaxDescriptionLine(List<String> stateDescriptionData) {
        int maxLine = 1;
        for (String stateDescription : stateDescriptionData) {
            int lineSize = stateDescription.split(STT_DESC_LINE_SEP).length;
            maxLine = lineSize > maxLine ? lineSize : maxLine;
        }
        mMaxDescriptionLine = maxLine;
        return maxLine;
    }


    private int selectMaxDescriptionLine(int maxLine) {
        return maxLine > 1 ? maxLine : getMaxDescriptionLine(mStateDescriptionData);
    }


    private void drawState(Canvas canvas) {

        setAnimatorStartEndCenterX();

        drawCurrentStateJoiningLine(canvas);

        drawBackgroundLines(canvas);

        drawBackgroundCircles(canvas);


        drawForegroundCircles(canvas);

        drawForegroundLines(canvas);

        drawStateNumberText(canvas, mMaxSttNum);

        drawStateDescriptionText(canvas);

    }

    private void drawBackgroundCircles(Canvas canvas) {

        int startIndex = mIsSttNumDesc ? 0 : mCurrentStateNumber;
        int endIndex = mIsSttNumDesc ? mMaxSttNum - mCurrentStateNumber : mMaxSttNum;

        drawCircles(canvas, mBackgroundPaint, startIndex, endIndex);
    }

    private void drawForegroundCircles(Canvas canvas) {

        int startIndex = mIsSttNumDesc ? mMaxSttNum - mCurrentStateNumber : 0;
        int endIndex = mIsSttNumDesc ? mMaxSttNum : mCurrentStateNumber;

        drawCircles(canvas, mForegroundPaint, startIndex, endIndex);
    }


    private void drawBackgroundLines(Canvas canvas) {
        int startIndex = mIsSttNumDesc ? 0 : mCurrentStateNumber - 1;
        int endIndex = mIsSttNumDesc ? mMaxSttNum - mCurrentStateNumber + 1 : mMaxSttNum;

        drawLines(canvas, mBackgroundPaint, startIndex, endIndex);
    }

    private void drawForegroundLines(Canvas canvas) {
        int startIndex = mIsSttNumDesc ? mMaxSttNum - mCurrentStateNumber + 1 : 0;
        int endIndex = mIsSttNumDesc ? mMaxSttNum : mCurrentStateNumber - 1;

        drawLines(canvas, mForegroundPaint, startIndex, endIndex);
    }


    private void drawLines(Canvas canvas, Paint paint, int startIndex, int endIndex) {

        float startCenterX;
        float endCenterX;

        float startX;
        float stopX;


        if (endIndex > startIndex) {

            startCenterX = mCellWidth / 2 + mCellWidth * startIndex;

            endCenterX = mCellWidth * endIndex - (mCellWidth / 2);

            startX = startCenterX + (mStateRadius * 0.75f);
            stopX = endCenterX - (mStateRadius * 0.75f);

            canvas.drawLine(startX, mCellHeight / 2, stopX, mCellHeight / 2, paint);

        }

    }


    private void setAnimatorStartEndCenterX() {
        if (mCurrentStateNumber > MIN_STATE_NUMBER && mCurrentStateNumber < MAX_STATE_NUMBER + 1) {
            final int count = mIsSttNumDesc ? mMaxSttNum - mCurrentStateNumber + 1 : mCurrentStateNumber - 1;
            for (int i = 0; i < count; i++) {

                if (i == 0) {
                    mStartCenterX = mNextCellWidth - (mCellWidth / 2);
                } else {
                    mStartCenterX = mEndCenterX;
                }

                mNextCellWidth += mCellWidth;
                mEndCenterX = mNextCellWidth - (mCellWidth / 2);
            }
        } else {
            resetStateAnimationData();
        }
    }


    private void drawCurrentStateJoiningLine(Canvas canvas) {
        if (mAniToCurrProgStt && mCurrentStateNumber > 1) {
            animateToCurrentState(canvas);
        } else {
            drawLineToCurrentState(canvas);
        }
    }


    private void drawLineToCurrentState(Canvas canvas) {

        canvas.drawLine(mStartCenterX, mCellHeight / 2, mEndCenterX, mCellHeight / 2, mForegroundPaint);

        mNextCellWidth = mCellWidth;

        stopAnimation();
    }


    private void animateToCurrentState(Canvas canvas) {
        if (!mIsCurrentAnimStarted) {
            mAnimStartXPos = mStartCenterX;
            mAnimEndXPos = mAnimStartXPos;
            mIsCurrentAnimStarted = true;
        }

        if (mAnimEndXPos < mStartCenterX || mStartCenterX > mEndCenterX) {
            stopAnimation();
            enableAnimationToCurrentState(false);
            invalidate();
        } else if (mAnimEndXPos <= mEndCenterX) {
            if (!mIsSttNumDesc) {
                canvas.drawLine(mStartCenterX, mCellHeight / 2, mAnimEndXPos, mCellHeight / 2, mForegroundPaint);
                canvas.drawLine(mAnimEndXPos, mCellHeight / 2, mEndCenterX, mCellHeight / 2, mBackgroundPaint);
            } else {
                canvas.drawLine(mEndCenterX, mCellHeight / 2, mEndCenterX - (mAnimEndXPos - mStartCenterX), mCellHeight / 2, mForegroundPaint);
                canvas.drawLine(mEndCenterX - (mAnimEndXPos - mStartCenterX), mCellHeight / 2, mStartCenterX, mCellHeight / 2, mBackgroundPaint);
            }

            mAnimStartXPos = mAnimEndXPos;

        } else {
            if (!mIsSttNumDesc)
                canvas.drawLine(mStartCenterX, mCellHeight / 2, mEndCenterX, mCellHeight / 2, mForegroundPaint);
            else
                canvas.drawLine(mEndCenterX, mCellHeight / 2, mStartCenterX, mCellHeight / 2, mForegroundPaint);


        }

        mNextCellWidth = mCellWidth;
    }


    private void drawStateDescriptionText(Canvas canvas) {

        int xPos;
        int yPos;
        Paint innerPaintType;

        if (!mStateDescriptionData.isEmpty()) {

            for (int i = 0; i < mStateDescriptionData.size(); i++) {
                if (i < mMaxSttNum) {
                    innerPaintType = selectDescriptionPaint(mCurrentStateNumber, i);
                    xPos = (int) (mNextCellWidth - (mCellWidth / 2));


                    if (mIsDescriptionMultiline && mMaxDescriptionLine > 1) {
                        String stateDescription = mIsSttNumDesc ? mStateDescriptionData.get(mStateDescriptionData.size() - 1 - i) : mStateDescriptionData.get(i);
                        int nextLineCounter = 0;
                        int newXPos = 0;
                        String[] stateDescriptionLines = stateDescription.split(STT_DESC_LINE_SEP);

                        for (String line : stateDescriptionLines) {
                            nextLineCounter = nextLineCounter + 1;

                            if (mJustMultDesc && nextLineCounter > 1) {
                                newXPos = getNewXPosForDescriptionMultilineJustification(stateDescriptionLines[0], line, innerPaintType, xPos);
                            }

                            if (nextLineCounter <= mMaxDescriptionLine) {
                                yPos = (int) (mCellHeight + (nextLineCounter * mStateDescriptionSize) - mSpacing - mDescTopSpcDecr + mDescTopSpcIncr + (nextLineCounter > 1 ? (mDescLinesSpc * (nextLineCounter - 1)) : 0));//mSpacing = mSttNumForgrndPnt.getTextSize()
                                canvas.drawText(line, newXPos == 0 ? xPos : newXPos, yPos, innerPaintType);
                            }

                        }

                    } else {
                        yPos = (int) (mCellHeight + mStateDescriptionSize - mSpacing - mDescTopSpcDecr + mDescTopSpcIncr);//mSpacing = mSttNumForgrndPnt.getTextSize()
                        canvas.drawText(mIsSttNumDesc ? mStateDescriptionData.get(mStateDescriptionData.size() - 1 - i) : mStateDescriptionData.get(i), xPos, yPos, innerPaintType);
                    }

                    mNextCellWidth += mCellWidth;
                }
            }

        }

        mNextCellWidth = mCellWidth;
    }


    private int getNewXPosForDescriptionMultilineJustification(String firstLine, String nextLine, Paint paint, int xPos) {

        float firstLineWidth = paint.measureText(firstLine);
        float nextLineWidth = paint.measureText(nextLine);

        float newXPos;
        float widthDiff;

        if (firstLineWidth > nextLineWidth) {

            widthDiff = firstLineWidth - nextLineWidth;
            newXPos = xPos - widthDiff / 2;

        } else if (firstLineWidth < nextLineWidth) {

            widthDiff = nextLineWidth - firstLineWidth;
            newXPos = xPos + widthDiff / 2;

        } else {
            newXPos = xPos;
        }


        return Math.round(newXPos);

    }


    private Paint selectDescriptionPaint(int currentState, int statePosition) {

        currentState = mIsSttNumDesc ? mMaxSttNum + 1 - currentState : currentState;

        if (statePosition + 1 == currentState) {
            return mCurSttDescPaint;
        } else {
            return mSttDescPaint;
        }
    }


    private void resolveStateDescriptionDataSize(ArrayList<String> stateDescriptionData) {

        final int stateDescriptionDataSize = stateDescriptionData.size();
        if (stateDescriptionDataSize < mMaxSttNum) {
            for (int i = 0; i < mMaxSttNum - stateDescriptionDataSize; i++) {
                stateDescriptionData.add(stateDescriptionDataSize + i, EMPTY_SPACE_DESCRIPTOR);
            }
        }
    }

    public void setStateDescriptionData(String[] stateDescriptionData) {
        mStateDescriptionData = new ArrayList<>(Arrays.asList(stateDescriptionData));

        resolveStateDescriptionDataSize(mStateDescriptionData);

        requestLayout();
    }

    public void setStateDescriptionData(ArrayList<String> stateDescriptionData) {
        mStateDescriptionData = stateDescriptionData;

        resolveStateDescriptionDataSize(mStateDescriptionData);

        requestLayout();
    }

    public List<String> getStateDescriptionData() {
        return mStateDescriptionData;
    }

    private void resetStateAnimationData() {
        if (mStartCenterX > 0 || mStartCenterX < 0) mStartCenterX = 0;
        if (mEndCenterX > 0 || mEndCenterX < 0) mEndCenterX = 0;
        if (mAnimEndXPos > 0 || mAnimEndXPos < 0) mAnimEndXPos = 0;
        if (mIsCurrentAnimStarted) mIsCurrentAnimStarted = false;
    }


    private void drawStateNumberText(Canvas canvas, int noOfCircles) {

        int xPos;
        int yPos;
        Paint innerPaintType;
        boolean isChecked;

        for (int i = 0; i < noOfCircles; i++) {

            innerPaintType = selectPaintType(mCurrentStateNumber, i, mCheckStateCompleted);

            xPos = (int) (mCellWidth * (i + 1) - (mCellWidth / 2));

            yPos = (int) ((mCellHeight / 2) - ((innerPaintType.descent() + innerPaintType.ascent()) / 2));

            isChecked = isCheckIconUsed(mCurrentStateNumber, i);

            if (mCheckStateCompleted && isChecked) {
                canvas.drawText(getContext().getString(R.string.check_icon), xPos, yPos, innerPaintType);
            } else {
                if (mIsSttNumDesc)
                    canvas.drawText(String.valueOf(noOfCircles - i), xPos, yPos, innerPaintType);
                else
                    canvas.drawText(String.valueOf(i + 1), xPos, yPos, innerPaintType);
            }
        }

    }


    private Paint selectPaintType(int currentState, int statePosition, boolean checkStateCompleted) {

        currentState = mIsSttNumDesc ? mMaxSttNum - currentState : currentState;
        Paint foregroundPaint = mIsSttNumDesc ? mSttNumBackgrndPnt : mSttNumForgrndPnt;
        Paint backgroundPaint = mIsSttNumDesc ? mSttNumForgrndPnt : mSttNumBackgrndPnt;

        if (checkStateCompleted) {
            return applyCheckStateCompletedPaintType(currentState, statePosition, checkStateCompleted);
        } else {

            if ((statePosition + 1 == currentState) || (statePosition + 1 < currentState && !checkStateCompleted)) {
                return foregroundPaint;
            } else {
                return backgroundPaint;
            }
        }

    }


    private Paint applyCheckStateCompletedPaintType(int currentState, int statePosition, boolean checkStateCompleted) {
        if (checkStateCompleted(currentState, statePosition, checkStateCompleted)) {
            return mStateCheckedForegroundPaint;
        } else if (statePosition + 1 == (mIsSttNumDesc ? currentState + 1 : currentState)) {
            return mSttNumForgrndPnt;
        } else {
            return mSttNumBackgrndPnt;
        }
    }

    private boolean auxCheckStateCompleted(int currentState, int statePosition, boolean checkStateCompleted){
        if (mEnaAllSttComp && checkStateCompleted){
            return true;
        } else return false;
    }


    private boolean checkStateCompleted(int currentState, int statePosition, boolean checkStateCompleted) {

        if (!mIsSttNumDesc) {
            if (auxCheckStateCompleted){
                return true;
            } else {
                if (statePosition + 1 < currentState && checkStateCompleted){
                    return true;
                }
            }
        } else {
            if (auxCheckStateCompleted) {
                return true;
            } else {
                if (statePosition + 1 > currentState + 1 && checkStateCompleted){
                    return true;
                }
            }
        }
        return false;
    }


    private boolean isCheckIconUsed(int currentState, int statePosition) {

        currentState = mIsSttNumDesc ? mMaxSttNum + 1 - currentState : currentState;

        if (!mIsSttNumDesc)
            return mEnaAllSttComp || statePosition + 1 < currentState;
        else
            return mEnaAllSttComp || statePosition + 1 > currentState;
    }


    private void startAnimator() {
        mAnimator = new Animator();
        mAnimator.start();
    }

    private void stopAnimation() {
        if (mAnimator != null) {
            mAnimator.stop();
        }
    }


    private class Animator implements Runnable {
        private Scroller mScroller;
        private boolean mRestartAnimation = false;

        public Animator() {
            mScroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());
        }

        public void run() {
            if (mAnimator != this) return;

            if (mRestartAnimation) {
                mScroller.startScroll(0, (int) mStartCenterX, 0, (int) mEndCenterX, mAnimDuration);

                mRestartAnimation = false;
            }

            boolean scrollRemains = mScroller.computeScrollOffset();

            mAnimStartXPos = mAnimEndXPos;
            mAnimEndXPos = mScroller.getCurrY();

            if (scrollRemains) {
                invalidate();
                post(this);
            } else {
                stop();
                enableAnimationToCurrentState(false);
            }

        }

        public void start() {
            mRestartAnimation = true;
            postDelayed(this, mAnimStartDelay);
        }

        public void stop() {
            removeCallbacks(this);
            mAnimator = null;
        }

    }


    private float convertDpToPixel(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    private float convertSpToPixel(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        startAnimator();
    }


    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();

        super.onDetachedFromWindow();
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        startAnimator();

    }


    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(END_CENTER_X_KEY, this.mEndCenterX);
        bundle.putFloat(START_CENTER_X_KEY, this.mStartCenterX);

        bundle.putFloat(ANIM_START_X_POS_KEY, this.mAnimStartXPos);
        bundle.putFloat(ANIM_END_X_POS_KEY, this.mAnimEndXPos);

        bundle.putBoolean(IS_CURRENT_ANIM_STARTED_KEY, this.mIsCurrentAnimStarted);
        bundle.putBoolean(ANM_CUR_PRO_STT_KEY, this.mAniToCurrProgStt);

        bundle.putBoolean(IS_STT_NUM_DESC_KEY, this.mIsSttNumDesc);

        bundle.putFloat(STATE_SIZE_KEY, this.mStateSize);
        bundle.putFloat(STATE_LINE_THICKNESS_KEY, this.mStateLineThickness);
        bundle.putFloat(STATE_NUMBER_TEXT_SIZE_KEY, this.mStateNumberTextSize);
        bundle.putFloat(STATE_DESCRIPTION_SIZE_KEY, this.mStateDescriptionSize);

        bundle.putInt(MAX_STATE_NUMBER_KEY, this.mMaxSttNum);
        bundle.putInt(CURRENT_STATE_NUMBER_KEY, this.mCurrentStateNumber);
        bundle.putInt(ANIM_START_DELAY_KEY, this.mAnimStartDelay);
        bundle.putInt(ANIM_DURATION_KEY, this.mAnimDuration);

        bundle.putFloat(DESC_TOP_SPC_DECR_KEY, this.mDescTopSpcDecr);
        bundle.putFloat(DESC_TOP_SPC_INC_KEY, this.mDescTopSpcIncr);

        bundle.putFloat(DESCRIPTION_LINE_SPACING_KEY, this.mDescLinesSpc);

        bundle.putInt(BACKGROUND_COLOR_KEY, this.mBackgroundColor);
        bundle.putInt(FOREGROUND_COLOR_KEY, this.mForegroundColor);
        bundle.putInt(STT_NUM_BACK_COL_KEY, this.mSttNumBkGrndClr);
        bundle.putInt(STT_NUM_FORE_COLOR_KEY, this.mSttNumFrGrndClr);

        bundle.putInt(CURRENT_STATE_DESC_COLOR_KEY, this.mCrrntSttDescClr);
        bundle.putInt(STATE_DESC_COLOR_KEY, this.mStateDescriptionColor);

        bundle.putBoolean(CHECK_STATE_COMPLETED_KEY, this.mCheckStateCompleted);

        bundle.putBoolean(ENA_ALL_STT_COMP_KEY, this.mEnaAllSttComp);

        bundle.putBoolean(JUSTIFY_MULTILINE_DESC_KEY, this.mJustMultDesc);


        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            mEndCenterX = bundle.getFloat(END_CENTER_X_KEY);

            mStartCenterX = bundle.getFloat(START_CENTER_X_KEY);

            mAnimStartXPos = bundle.getFloat(ANIM_START_X_POS_KEY);

            mAnimEndXPos = bundle.getFloat(ANIM_END_X_POS_KEY);

            mIsCurrentAnimStarted = bundle.getBoolean(IS_CURRENT_ANIM_STARTED_KEY);

            mAniToCurrProgStt = bundle.getBoolean(ANM_CUR_PRO_STT_KEY);

            mIsSttNumDesc = bundle.getBoolean(IS_STT_NUM_DESC_KEY);


            mStateNumberTextSize = bundle.getFloat(STATE_NUMBER_TEXT_SIZE_KEY);
            mStateSize = bundle.getFloat(STATE_SIZE_KEY);
            resetStateSizeValues();

            mStateLineThickness = bundle.getFloat(STATE_LINE_THICKNESS_KEY);
            resolveStateLineThickness();

            mStateDescriptionSize = bundle.getFloat(STATE_DESCRIPTION_SIZE_KEY);
            resolveStateDescriptionSize();


            mMaxSttNum = bundle.getInt(MAX_STATE_NUMBER_KEY);
            mCurrentStateNumber = bundle.getInt(CURRENT_STATE_NUMBER_KEY);
            resolveMaxStateNumber();

            mAnimStartDelay = bundle.getInt(ANIM_START_DELAY_KEY);

            mAnimDuration = bundle.getInt(ANIM_DURATION_KEY);


            mDescTopSpcDecr = bundle.getFloat(DESC_TOP_SPC_DECR_KEY);

            mDescTopSpcIncr = bundle.getFloat(DESC_TOP_SPC_INC_KEY);

            mDescLinesSpc = bundle.getFloat(DESCRIPTION_LINE_SPACING_KEY);

            setDescriptionTopSpaceIncrementer(mDescTopSpcIncr); // call requestLayout

            mBackgroundColor = bundle.getInt(BACKGROUND_COLOR_KEY);

            mForegroundColor = bundle.getInt(FOREGROUND_COLOR_KEY);

            mSttNumBkGrndClr = bundle.getInt(STT_NUM_BACK_COL_KEY);

            mSttNumFrGrndClr = bundle.getInt(STT_NUM_FORE_COLOR_KEY);

            mCrrntSttDescClr = bundle.getInt(CURRENT_STATE_DESC_COLOR_KEY);

            mStateDescriptionColor = bundle.getInt(STATE_DESC_COLOR_KEY);

            mJustMultDesc = bundle.getBoolean(JUSTIFY_MULTILINE_DESC_KEY);

            initializePainters();

            checkStateCompleted(bundle.getBoolean(CHECK_STATE_COMPLETED_KEY)); // call invalidate

            setAllStatesCompleted(bundle.getBoolean(ENA_ALL_STT_COMP_KEY));

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));

            return;
        }
        super.onRestoreInstanceState(state);
    }


}
